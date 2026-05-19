package edu.rit.ibd.a1;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;


import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.function.IOIterator;
import java.sql.PreparedStatement;
import org.apache.commons.text.similarity.LevenshteinDistance;


public class LoadBusinesses {
	public static void main(String[] args) {
		final String jdbcURL = args[0], jdbcUser = args[1], jdbcPwd = args[2];
		final String gZipFile = args[3];

		try (Connection con = DriverManager.getConnection(jdbcURL, jdbcUser, jdbcPwd);) {

			con.setAutoCommit(false);

			Create_Tables(con); //Initialize tables in yelp_DB

			InputStream is = Files.newInputStream(Paths.get(gZipFile));
			GzipCompressorInputStream xzis = new GzipCompressorInputStream(is);
			TarArchiveInputStream taris = new TarArchiveInputStream(xzis);
			IOIterator<TarArchiveEntry> it = taris.iterator();

			Map<String, String> State_Map = new HashMap<>(); //Storing 50 states plus territories maybe(guam, puerto rico etc)
			Map<String, String> City_Map = new HashMap<>(); //Storing A lot of cities
			Map<String, String> ZC_Map = new HashMap<>(); //Storing upt to 99999 ZC
			Map<String, String> Category_Map = new HashMap<>(); //Storing the 21 yelp categories found here https://business.yelp.com/resources/articles/yelp-category-list/?domain=local-business
			while (it.hasNext()) {
				TarArchiveEntry entry = it.next();

				if (entry.getName().equals("yelp_academic_dataset_business.json")) {
					Buffer_Data(con, taris, State_Map, City_Map, ZC_Map, Category_Map);
				}
			}

		} catch (Exception oops) {
			System.out.println("Dang it!");
			oops.printStackTrace();
		}
	}

	private static void Create_Tables(Connection con) throws SQLException {
		Statement stmt = con.createStatement();

		String Business_Table = "CREATE TABLE IF NOT EXISTS Business" +
				"(id VARCHAR(255), " +
				"name VARCHAR(255), " +
				"address VARCHAR(255), " +
				"zc_id VARCHAR(255), " +
				"latitude DECIMAL(11, 8), " +
				"longitude DECIMAL(11, 8), " +
				"is_open INTEGER, " +
				"bike_parking INTEGER, " +
				"good_for_dancing INTEGER, " +
				"restaurants_good_for_groups INTEGER, " +
				"restaurants_price_range VARCHAR(255), " +
				"restaurants_take_out INTEGER, " +
				"restaurants_delivery INTEGER, " +
				"restaurants_counter_service INTEGER, " +
				"restaurants_table_service INTEGER, " +
				"restaurants_reservations INTEGER, " +
				"drive_thru INTEGER, " +
				"wheelchair_accessible INTEGER, " +
				"by_appointment_only INTEGER, " +
				"dogs_allowed INTEGER, " +
				"happy_hour INTEGER, " +
				"byob INTEGER, " +
				"corkage INTEGER, " +
				"outdoor_seating INTEGER, " +
				"accepts_insurance INTEGER, " +
				"caters INTEGER, " +
				"coat_check INTEGER, " +
				"good_for_kids INTEGER, " +
				"open_24_hours INTEGER, " +
				"has_tv INTEGER, " +
				"business_accepts_credit_cards INTEGER, " +
				"business_accepts_bitcoin INTEGER, " +
				"PRIMARY KEY (id), " +
				"FOREIGN KEY (zc_id) REFERENCES Zipcode(id))";

		String Zipcode_Table = "CREATE TABLE IF NOT EXISTS Zipcode" +
				"(id VARCHAR(255), " +
				"code VARCHAR(255), " +
				"city_id VARCHAR(255), " +
				"PRIMARY KEY (id), " +
				"FOREIGN KEY (city_id) REFERENCES City(id))";

		String City_Table = "CREATE TABLE IF NOT EXISTS City" +
				"(id VARCHAR(255), " +
				"name VARCHAR(255), " +
				"state_id VARCHAR(255), " +
				"PRIMARY KEY (id), " +
				"FOREIGN KEY (state_id) REFERENCES State(id))";

		String State_Table = "CREATE TABLE IF NOT EXISTS State" +
				"(id VARCHAR(255), " +
				"name VARCHAR(255), " +
				"PRIMARY KEY (id))";

		String Category_Table = "CREATE TABLE IF NOT EXISTS Category" +
				"(id VARCHAR(255), " +
				"name VARCHAR(255), " +
				"PRIMARY KEY (id))";

		String BusinessCategory_Table = "CREATE TABLE IF NOT EXISTS BusinessCategory" +
				"(bid VARCHAR(255), " +
				"cid VARCHAR(255), " +
				"PRIMARY KEY (bid, cid), " +
				"FOREIGN KEY (cid) REFERENCES Category(id))";

		String BusinessHour_Table = "CREATE TABLE IF NOT EXISTS BusinessHour" +
				"(bid VARCHAR(255), " +
				"day VARCHAR(255), " +
				"open_time VARCHAR(255), " +
				"closing_time VARCHAR(255), " +
				"PRIMARY KEY (bid, day), " +
				"FOREIGN KEY (bid) REFERENCES Business(id))";

		stmt.executeUpdate(Category_Table); //No FKs given      HEHE ;)
		stmt.executeUpdate(BusinessCategory_Table); //FK above^


		stmt.executeUpdate(State_Table); //No FKs given
		stmt.executeUpdate(City_Table); //FK above^
		stmt.executeUpdate(Zipcode_Table); //FK above^
		stmt.executeUpdate(Business_Table); //FK above^
		stmt.executeUpdate(BusinessHour_Table); //FK up^
		con.commit();
	}

	private static String extract(String line, String key) {
		String token = "\"" + key + "\"";
		int keyPos = line.indexOf(token);
		if (keyPos == -1) {
			return null;
		}
		int valueStart = line.indexOf(":", keyPos) + 1;

		while (valueStart < line.length() && (line.charAt(valueStart) == ' ' || line.charAt(valueStart) == '\"')) {
			valueStart++;
		}
		int valueEnd;

		if (line.charAt(valueStart - 1) == '\"') {
			valueEnd = line.indexOf("\"", valueStart);
		} else {
			int comma = line.indexOf(",", valueStart);
			int brace = line.indexOf("}", valueStart);
			valueEnd = (comma != -1 && (brace == -1 || comma < brace)) ? comma : brace;
		}

		return (valueStart != -1 && valueEnd != -1) ? line.substring(valueStart, valueEnd).trim() : null;
	}

	private static void Buffer_Data(Connection con, InputStream stream, Map<String, String> State_Map, Map<String, String> City_Map, Map<String, String> ZC_Map, Map<String, String> Category_Map) throws SQLException {
		//VI,AB, and XMS ARE not STATES so why did you make me go through so many mental gymnastics to find 25 states when there were only 24 and then these 3. I felt like i was going crazy
		Set<String> Valid_US_States = new HashSet<>(Arrays.asList(
				"AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA",
				"HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD",
				"MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ",
				"NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC",
				"SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY", "DC", "VI", "AB", "XMS"
		));

		Map<String, String> Category_Lower_Map = new HashMap<>();

		PreparedStatement Prep_State = con.prepareStatement("INSERT IGNORE INTO State (id, name) VALUES (?, ?)");
		PreparedStatement Prep_City = con.prepareStatement("INSERT IGNORE INTO City (id, name, state_id) VALUES (?, ?, ?)");
		PreparedStatement Prep_ZC = con.prepareStatement("INSERT IGNORE INTO Zipcode (id, code, city_id) VALUES (?, ?, ?)");

		PreparedStatement Prep_Business = con.prepareStatement("INSERT INTO Business (id, name, address, zc_id, latitude, longitude, is_open, " +
				"bike_parking, good_for_dancing, restaurants_good_for_groups, restaurants_price_range, " +
				"restaurants_take_out, restaurants_delivery, restaurants_counter_service, restaurants_table_service, " +
				"restaurants_reservations, drive_thru, wheelchair_accessible, by_appointment_only, " +
				"dogs_allowed, happy_hour, byob, corkage, outdoor_seating, accepts_insurance, caters, " +
				"coat_check, good_for_kids, open_24_hours, has_tv, business_accepts_credit_cards, " +
				"business_accepts_bitcoin) VALUES (" +
				"?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

		PreparedStatement Prep_Category = con.prepareStatement("INSERT IGNORE INTO Category (id, name) VALUES (?, ?)");
		PreparedStatement Prep_BusinessCategory = con.prepareStatement("INSERT INTO BusinessCategory (bid, cid) VALUES (?, ?)");
		PreparedStatement Prep_BusinessHour = con.prepareStatement("INSERT INTO BusinessHour (bid, day, open_time, closing_time) VALUES (?, ?, ?, ?)");

		Scanner sc = new Scanner(stream, "UTF-8");
		int count = 0, sCount = 1, cCount = 1, zCount = 1, catCount = 1;

		while (sc.hasNextLine()) {
			String line = sc.nextLine();
			String bid = extract(line, "business_id");
			if (bid == null) continue;

			// --- STRICT STATE FILTERING (Corrected) ---
			String rawState = extract(line, "state");
			if (rawState == null) continue;

			String stateKey = rawState.trim().toUpperCase();

			// Only allow valid US states (This filters out "AB", "ON", etc. FAKE alberta is the 51st state thanks samay) fake comment
			// THIS IS LEGACY CODE COMMENT, NOW THAT XMS, the VIRGIN ISLANDS, AND ALBERTA CANADA are now US STATES
			//I guess it makes sense since Trump already got Greenland and Venezuela
			if (!Valid_US_States.contains(stateKey)) {
				continue;
			}

			if (!State_Map.containsKey(stateKey)) {
				String id = "st-" + sCount++;
				State_Map.put(stateKey, id);
				Prep_State.setString(1, id);
				Prep_State.setString(2, stateKey);
				Prep_State.addBatch();
			}
			String stateId = State_Map.get(stateKey);

			// I hate this pleas make the pain stop what the fuck is a levenschtein
			//also i first tried to make the levenschtein distance similatiry based on
			String rawCity = extract(line, "city");
			String cityKey = Regex_CityKey(rawCity);
			String currentCityId = null;

			if (City_Map.containsKey(cityKey + "|" + stateId)) {
				currentCityId = City_Map.get(cityKey + "|" + stateId);
			} else {
				for (Map.Entry<String, String> entry : City_Map.entrySet()) {
					if (entry.getKey().endsWith("|" + stateId)) {
						String existingCityKey = entry.getKey().split("\\|")[0];
						if (getSimilarity(cityKey, existingCityKey) > 0.80) {
							currentCityId = entry.getValue();
							City_Map.put(cityKey + "|" + stateId, currentCityId);
							break;
						}
					}
				}
			}

			if (currentCityId == null) {
				currentCityId = "ci-" + cCount++;
				City_Map.put(cityKey + "|" + stateId, currentCityId);
				Prep_City.setString(1, currentCityId);
				Prep_City.setString(2, rawCity.trim());
				Prep_City.setString(3, stateId);
				Prep_City.addBatch();
			}
			String zip = extract(line, "postal_code");
			if (!ZC_Map.containsKey(zip)) {
				String zId = "zp-" + zCount++;
				ZC_Map.put(zip, zId);
				Prep_ZC.setString(1, zId);
				Prep_ZC.setString(2, zip);
				Prep_ZC.setString(3, currentCityId);
				Prep_ZC.addBatch();
			}

			String categories = extract(line, "categories");
			if (categories != null) {
				Set<String> businessCats = new HashSet<>();

				for (String catName : categories.split(",")) {
					catName = catName.trim();
					// FILTER "null" STRING to fix category count
					if (catName.isEmpty() || catName.equalsIgnoreCase("null") || catName.equalsIgnoreCase("None")) continue;

					String lowerCat = catName.toLowerCase();
					if (businessCats.contains(lowerCat)) continue;
					businessCats.add(lowerCat);

					String catId;
					if (Category_Lower_Map.containsKey(lowerCat)) {
						catId = Category_Lower_Map.get(lowerCat);
					} else {
						catId = "cat-" + catCount++;
						Category_Map.put(catName, catId);
						Category_Lower_Map.put(lowerCat, catId);
						Prep_Category.setString(1, catId);
						Prep_Category.setString(2, catName);
						Prep_Category.addBatch();
					}
					Prep_BusinessCategory.setString(1, bid);
					Prep_BusinessCategory.setString(2, catId);
					Prep_BusinessCategory.addBatch();
				}
			}
			String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
			for (int i = 0; i < days.length; i++) {
				String timeRange = extract(line, days[i]);
				if (timeRange != null && !timeRange.equals("null")) {
					String[] parts = timeRange.split("-");
					if (parts.length == 2) {
						Prep_BusinessHour.setString(1, bid);
						Prep_BusinessHour.setString(2, String.valueOf(i));
						Prep_BusinessHour.setString(3, formatTime(parts[0]));
						Prep_BusinessHour.setString(4, formatTime(parts[1]));
						Prep_BusinessHour.addBatch();
					}
				}
			}

			Prep_Business.setString(1, bid);
			Prep_Business.setString(2, extract(line, "name"));
			Prep_Business.setString(3, extract(line, "address"));
			Prep_Business.setString(4, ZC_Map.get(zip));
			Prep_Business.setString(5, extract(line, "latitude"));
			Prep_Business.setString(6, extract(line, "longitude"));

			String isOpen = extract(line, "is_open");
			Prep_Business.setInt(7, (isOpen != null && (isOpen.equals("1") || isOpen.equalsIgnoreCase("true"))) ? 1 : 0);

			setOptionalBoolean(Prep_Business, 8, extract(line, "BikeParking"));
			setOptionalBoolean(Prep_Business, 9, extract(line, "GoodForDancing"));
			setOptionalBoolean(Prep_Business, 10, extract(line, "RestaurantsGoodForGroups"));

			String price = extract(line, "RestaurantsPriceRange2");
			if (price == null || price.equals("null") || price.equals("None"))
				Prep_Business.setNull(11, java.sql.Types.VARCHAR);
			else
				Prep_Business.setString(11, price);

			setOptionalBoolean(Prep_Business, 12, extract(line, "Restaurants_Take_Out"));
			setOptionalBoolean(Prep_Business, 13, extract(line, "RestaurantsDelivery"));
			setOptionalBoolean(Prep_Business, 14, extract(line, "RestaurantsCounterService"));
			setOptionalBoolean(Prep_Business, 15, extract(line, "RestaurantsTableService"));
			setOptionalBoolean(Prep_Business, 16, extract(line, "RestaurantsReservations"));
			setOptionalBoolean(Prep_Business, 17, extract(line, "DriveThru"));
			setOptionalBoolean(Prep_Business, 18, extract(line, "WheelchairAccessible"));
			setOptionalBoolean(Prep_Business, 19, extract(line, "ByAppointmentOnly"));
			setOptionalBoolean(Prep_Business, 20, extract(line, "DogsAllowed"));
			setOptionalBoolean(Prep_Business, 21, extract(line, "HappyHour"));
			setOptionalBoolean(Prep_Business, 22, extract(line, "BYOB"));
			setOptionalBoolean(Prep_Business, 23, extract(line, "Corkage"));
			setOptionalBoolean(Prep_Business, 24, extract(line, "OutdoorSeating"));
			setOptionalBoolean(Prep_Business, 25, extract(line, "AcceptsInsurance"));
			setOptionalBoolean(Prep_Business, 26, extract(line, "Caters"));
			setOptionalBoolean(Prep_Business, 27, extract(line, "CoatCheck"));
			setOptionalBoolean(Prep_Business, 28, extract(line, "GoodForKids"));
			setOptionalBoolean(Prep_Business, 29, extract(line, "Open24Hours"));
			setOptionalBoolean(Prep_Business, 30, extract(line, "HasTV"));
			setOptionalBoolean(Prep_Business, 31, extract(line, "BusinessAcceptsCreditCards"));
			setOptionalBoolean(Prep_Business, 32, extract(line, "BusinessAcceptsBitcoin"));
			Prep_Business.addBatch();

			if (++count % 1000 == 0) {
				Prep_State.executeBatch();
				Prep_City.executeBatch();
				Prep_ZC.executeBatch();
				Prep_Business.executeBatch();
				Prep_Category.executeBatch();
				Prep_BusinessCategory.executeBatch();
				Prep_BusinessHour.executeBatch();
				con.commit();
			}
		}
		Prep_State.executeBatch();
		Prep_City.executeBatch();
		Prep_ZC.executeBatch();
		Prep_Business.executeBatch();
		Prep_Category.executeBatch();
		Prep_BusinessCategory.executeBatch();
		Prep_BusinessHour.executeBatch();
		con.commit();

		Prep_State.close();
		Prep_City.close();
		Prep_ZC.close();
		Prep_Business.close();
		Prep_Category.close();
		Prep_BusinessCategory.close();
		Prep_BusinessHour.close();
	}


	private static void setOptionalBoolean(PreparedStatement ps, int idx, String val) throws SQLException {
		if (val == null || val.equals("null") || val.equals("None")) {
			ps.setNull(idx, java.sql.Types.INTEGER);
		} else {
			ps.setInt(idx, "True".equalsIgnoreCase(val) ? 1 : 0);
		}
	}


	private static String formatTime(String time) {
		if (time == null || time.equals("null")) return null;
		String[] parts = time.split(":");
		return String.format("%02d:%02d:00", Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
	}


	private static final LevenshteinDistance distance = new LevenshteinDistance();

	public static double getSimilarity(String s1, String s2) {
		if (s1.isEmpty() && s2.isEmpty()) return 1.0;
		if (s1.equals(s2)) return 1.0;
		int dist = distance.apply(s1, s2);
		int maxLen = Math.max(s1.length(), s2.length());
		return 1.0 - ((double) dist / maxLen);
	}


	private static String Regex_CityKey(String city) {
		if (city == null) return "unknown";
		String clean = city.toLowerCase().trim();

		if (clean.matches("^(blvd|ste c|boulevard|virtual|view|downtown|bucks|pinellas|hillsborough county|bucks county)$") ||
				clean.matches("^(indiana|pennsylvania|florida|tennessee|tennesse|arizona)$") ||
				clean.matches("^(pasco|hancock|johnson|boone|saint bernard)$") ||
				clean.contains("mississauga") || clean.contains("san anselmo") ||
				(clean.equals("media") && !clean.contains("pa"))) {
			return "unknown";
		}
		clean = clean.replaceAll("\\b(downtown|dt)\\b", "");
		clean = clean.replaceAll("\\b(ap|airport|afb|air force base|arb|naval base)\\b", "");
		clean = clean.replaceAll("town & country", "town and country");
		clean = clean.replaceAll("mc cordsville", "mccordsville");
		clean = clean.replaceAll("staint albert", "saint albert");
		clean = clean.replaceAll("sturgeon couny", "sturgeon county");
		clean = clean.replaceAll("thonosassa", "thonotosassa");
		clean = clean.replaceAll("thosonassa", "thonotosassa");
		clean = clean.replaceAll("\\b(tren)\\b", "trenton");
		clean = clean.replaceAll("pennsaulen", "pennsauken");
		clean = clean.replaceAll("had twp", "haddon township");
		clean = clean.replaceAll("evshm twp", "evesham");
		clean = clean.replaceAll("afton", "affton");
		clean = clean.replaceAll("maryland height\\b", "maryland heights");
		clean = clean.replaceAll("newtown sq\\b", "newtown square");
		clean = clean.replaceAll("newtown sq\\.", "newtown square");
		clean = clean.replaceAll("new pt richey", "new port richey");
		clean = clean.replaceAll("\\bphila\\b", "philadelphia");
		clean = clean.replaceAll("tuscon", "tucson");
		clean = clean.replaceAll("macdill", "tampa");
		clean = clean.replaceAll("chalemette", "chalmette");
		clean = clean.replaceAll("uppr blck edy", "upper black eddy");
		clean = clean.replaceAll("festerville", "feasterville");
		clean = clean.replaceAll("green valle\\b", "green valley");
		clean = clean.replaceAll("maran\\b", "marana");
		clean = clean.replaceAll("tierre verde", "tierra verde");
		clean = clean.replaceAll("n redngtn bch", "north redington beach");
		clean = clean.replaceAll("lula lula", "lula");
		clean = clean.replaceAll("bosie", "boise");
		clean = clean.replaceAll("meridan", "meridian");
		clean = clean.replaceAll("saintt", "saint");
		clean = clean.replaceAll("st\\.? loius", "saint louis");
		clean = clean.replaceAll("claerwater", "clearwater");
		clean = clean.replaceAll("madiera", "madeira");
		clean = clean.replaceAll("redingtn", "redington");
		clean = clean.replaceAll("prt rchy", "port richey");
		clean = clean.replaceAll("twn n cntry", "town and country");
		clean = clean.replaceAll("inpolis", "indianapolis");
		clean = clean.replaceAll("plainfiled", "plainfield");
		clean = clean.replaceAll("new orlaens", "new orleans");
		clean = clean.replaceAll("metarie", "metairie");
		clean = clean.replaceAll("riveridge", "river ridge");
		clean = clean.replaceAll("creve couer", "creve coeur");
		clean = clean.replaceAll("newtown sqaure", "newtown square");
		clean = clean.replaceAll("drexel hil\\b", "drexel hill");
		clean = clean.replaceAll("haverton", "havertown");
		clean = clean.replaceAll("royford", "royersford");
		clean = clean.replaceAll("new britian", "new britain");
		clean = clean.replaceAll("plymouth mtng", "plymouth meeting");
		clean = clean.replaceAll("monchanin", "montchanin");
		clean = clean.replaceAll("real goleta", "goleta");

		//Explicitly fix St. Louis to ensure it matches the merge key
		if (clean.equals("st louis") || clean.equals("st. louis")) return "saint louis";

		// --- 4. REGIONAL MERGES (The Compressor) ---

		//TN: Nashville
		if (clean.matches(".*\\b(antioch|hermitage|old hickory|madison|bellevue|donelson|green hills|berry hill|inglewood|cane ridge|joelton|whites creek|brentwood|goodlettsville|goodletsville|belle meade|pegram|lakewood|rural hill|nolensville|nolenville)\\b.*")) {
			clean = "nashville";
		}

		//MO: St. Louis
		if (clean.matches(".*\\b(university city|clayton|richmond heights|maplewood|brentwood|kirkwood|webster groves|webster grvs|ladue|olivette|creve coeur|maryland heights|overland|saint ann|st ann|st\\.ann|saint john|ferguson|florissant|hazelwood|bridgeton|sullivan|concord village|bel ridge|dellwood|hampton|charlack|west alton|mehlville|mehville|sappington|affton|lemay|bellefontaine|winchester|peerless park|high ridge)\\b.*")) {
			clean = "saint louis";
		}
		clean = clean.replaceAll("lemay ferry", "lemay");
		clean = clean.replaceAll("town 'n' country", "town and country");

		//IL: Metro East
		if (clean.matches(".*\\b(fairview heights|fairview hts|collinsville|edwardsville|granite city|belleville|bellville|o'fallon|o fallon|shiloh|swansea|glen carbon|maryville|pontoon beach|brooklyn|sauget|cahokia|mascoutah|dupo|millstadt|smithton|freeburg|washington park|fairmont city|cottage hills|rosewood heights|bethalto|hartford|godfrey|madison|scott|south roxana|wood river|foster pond)\\b.*")) {
			clean = "east saint louis";
		}

		//FL: Tampa Bay
		if (clean.matches(".*\\b(town 'n' country|town n country|carrollwood|citrus park|brandon|riverview|valrico|seffner|lutz|odessa|gibsonton|apollo beach|ruskin|university|temple terrace|temple terr|tampa palms|new tampa|central tampa|westchase|mango|thonotosassa|land o lakes|land-o-lakes|pinecrest west park|saint leo|terra ceia|balm|wimauma|sun city center|lithia|trinity|holiday)\\b.*")) {
			clean = "tampa";
		}

		//FL: Pinellas/St Pete
		if (clean.matches(".*\\b(pass-a-grille|stpete|st pete beach|madeira beach|treasure island|seminole|pinellas park|largo|dunedin|safety harbor|oldsmar|gulfport|kenneth city|kenneth|south pasadena|s pasadena|redington|indian shores|belleair|bellair|tierra verde|treasure is|wyndlake|elfers|tarpon springs)\\b.*")) {
			clean = "saint petersburg";
		}

		//DE: Wilmington Area
		if (clean.matches(".*\\b(newark|bear|christiana|hockessin|pike creek|stanton|claymont|elsmere|marshallton|greenville|trolley square|talleyville|bellefonte|montchanin|centreville|aston|winterthur|edgemoor|arden|newport|new castle|newcastle|yorklyn)\\b.*")) {
			clean = "wilmington";
		}

		//PA: Philadelphia & Inner Suburbs
		if (clean.matches(".*\\b(manayunk|roxborough|chestnut hill|mt airy|mount airy|fox street|center city|south philly|northeast philly|overbrook|bala cynwyd|cynwyd|ardmore|havertown|haverford|drexel hill|drexel|plymouth meeting|conshohocken|conshohoeken|levittown|bensalem|croydon|bristol|feasterville|feastville|trevose|southampton|southhampton|willow grove|jenkintown|glenside|upper darby|wynnewood|merion|merion station|folcroft|sharon hill|collingdale|darby|yeadon|lansdowne|clifton heights|secane|morton|rutledge|primos|aldan|east norriton|plymouth|lower providence|rockledge)\\b.*")) {
			clean = "philadelphia";
		}
		clean = clean.replaceAll("\\bkop\\b", "king of prussia");
		clean = clean.replaceAll("chesterbrook", "king of prussia");

		//NJ: South Jersey
		if (clean.matches(".*\\b(haddonfield|collingswood|pennsauken|merchantville|bellmawr|runnemede|glendora|somerdale|haddon heights|audubon|oaklyn|mount ephraim|brooklawn|westmont|barrington|magnolia|lawnside|hi-nella|hi nella|stratford|voorhees|echelon|marlton|evesham|haddon township)\\b.*")) {
			clean = "cherry hill";
		}
		//NJ: Washington Twp/Gloucester Area
		if (clean.matches(".*\\b(turnersville|sewell|hurffville|grenloch|pitman|glassboro|clayton|deptford|woodbury|westville|national park|wenonah|mantua|blackwood)\\b.*")) {
			clean = "washington township";
		}

		//IN: Indianapolis
		if (clean.matches(".*\\b(speedway|beech grove|lawrence|southport|cumberland|clermont|wannamaker|wanamaker|castleton|meridian hills|mccordsville|avon|brownsburg|plainfield|mooresville|greenwood|whiteland|new palestine|fairland)\\b.*")) {
			clean = "indianapolis";
		}

		//ID: Boise
		if (clean.matches(".*\\b(meridian|eagle|garden city|kuna|star)\\b.*")) {
			clean = "boise";
		}

		//AB: Edmonton part of the 51st state now, thanks Trump and Samay
		if (clean.matches(".*\\b(sherwood park|sherwood|st albert|staint albert|spruce grove|stony plain|pilot sound|enoch|beaumont|fort saskatchewan|eaux claires|old strathcona|nw edmonton)\\b.*")) {
			clean = "edmonton";
		}

		//LA: New Orleans
		if (clean.matches(".*\\b(metairie|kenner|chalmette|arabi|gretna|harahan|jefferson|river ridge|elmwood|violet|meraux|westwego|marrero|bridge city|terrytown|estelle|timberlane|bucktown|waggaman|avondale|st\\.rose|luling|boutte|belle chasse)\\b.*")) {
			clean = "new orleans";
		}
		if (clean.matches(".*\\b(french quarter|bywater|gentilly|algiers|ninth ward)\\b.*")) {
			clean = "new orleans";
		}

		//AZ: Tucson (Pronounced tukh - sun)
		if (clean.matches(".*\\b(catalina foothills|casas adobes|casa adobes|tanque verde|flowing wells|drexel heights|corona de tucson|picture rocks|los ranchitos|valencia west|rillito|santa rita|cortaro|catalina|marana|sahuarita|oro valley)\\b.*")) {
			clean = "tucson";
		}

		//NV: Reno 911
		if (clean.matches(".*\\b(sparks|spark|stead|spanish springs|cold springs|sun valley|lemmon valley|washoe valley|vc highlands|virginia city|mccarran|verdi)\\b.*")) {
			clean = "reno";
		}

		clean = clean.replaceAll("\\b(twp|townsh|township|boro|borough)\\b", "");
		clean = clean.replaceAll("\\(.*?\\)", "");
		clean = clean.replaceAll("\\b(ab|bc|mb|nb|nl|ns|nt|nu|on|pe|qc|sk|yt)\\b", "");

		//Remove State Names
		clean = clean.replaceAll("\\b(indiana|tennessee|tennesse|tn|nevada|nv|new jersey|nj|arizona|az|colorado|co|washington|wa|texas|tx|montana|mt|massachusetts|ma|vermont|vt|florida|fl|pennsylvania|pa)\\b", "");


		clean = clean.replaceAll("\\b(hts)\\b", "heights");
		clean = clean.replaceAll("\\b(bch)\\b", "beach");
		clean = clean.replaceAll("\\b(prt)\\b", "port");
		clean = clean.replaceAll("\\b(twp)\\b", "township");
		clean = clean.replaceAll("\\b(vlg)\\b", "village");
		clean = clean.replaceAll("\\b(ctr)\\b", "center");

		//Remove Prefixes & Generic Suffixes
		clean = clean.replaceAll("\\b(st\\.|st|ste|saint)\\b", "");
		clean = clean.replaceAll("\\b(mt|mount|mte)\\b", "");
		clean = clean.replaceAll("\\b(ft|fort)\\b", "");
		clean = clean.replaceAll("\\b(township|metropolitan|government|balance|census|designated|bor|borough|charter|center|centre|city|" +
				"area|bay|mall|shopping|industrial|plaza|park|square|village|heights|valley|hills|manor|terrace|" +
				"shores|junction|estates|isles|beach|county|counties|parish|district|davidson|metro|condominium" +
				")\\b", "");

		//Remove Directions EXCEPT for East St Louis
		if (!clean.contains("east saint louis") && !clean.contains("east st louis")) {
			clean = clean.replaceAll("\\b(new|north|south|east|west|northwest|northeast|southwest|southeast|central|n|s|e|w|greater|upper|lower|middle)\\b", "");
		}

		clean = clean.replaceAll("\\s*([-&,/]|\\\\).*$", "");

		String finalResult = clean.replaceAll("[^a-z]", "").trim();
		return finalResult.isEmpty() ? "unknown" : finalResult;
	}
}