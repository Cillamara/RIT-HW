package edu.rit.ibd.a2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.bson.Document;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
 * Author: Liam Cui
 * RIT
 * CSCI 620: A2 Load Businesses
 *
 * Loads Businesses data from yelp SQL Database into mongo DB
 *
 *
 * Note for future Liam, It was NOT the tar.gz yelp data dump.
 * It was the SQL data provided by professor Rivero ("mysql" use yelp_A3Q3) #yes my db names are dumb don't judge me or do idc
 *
 * Changes to field names/structure from the SQL to Mongo
 * VVVVVVVVVVVV SQL name on left Mongo name on rightVVVVVVVV
 * id to _id (cuz _id is reserved for PK in Mon GoD B)
 * name to bname
 *
 * locational data is nested into location doc
 * address to location.address
 * city to location.city
 * same for state, zipcode longitude, latitude
 * ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 *
 * Date and time format:
 * yyyy-MM-dd'T'HH:mm:ss'Z'
 *
 * also nested an hours document with dynamic keys for each operating day
 *
 * also remember mongo store bools as 0 or 1
 *
 * aslo
 */

public class LoadBusinesses {

	public static void main(String[] args) {
		final String jdbcURL = args[0], jdbcUser = args[1], jdbcPwd = args[2];
		final String mongoDBURL = args[3], mongoDBName = args[4];

		try (Connection con = DriverManager.getConnection(jdbcURL, jdbcUser, jdbcPwd);
			 MongoClient client = getClient(mongoDBURL);) {

			MongoDatabase db = client.getDatabase(mongoDBName);
			MongoCollection<Document> businessCollection = db.getCollection("Businesses");
			businessCollection.drop(); // Start fresh

			Statement setupStmt = con.createStatement();
			setupStmt.execute("SET SESSION group_concat_max_len = 10000");
			setupStmt.close();

			// SQL Query
			String sql = "SELECT " +
					"  b.*, " +
					"  z.code as zipcode_val, " +
					"  c.name as city_name, " +
					"  s.name as state_name, " +
					"  GROUP_CONCAT(DISTINCT cat.name) as categories_str, " +
					"  GROUP_CONCAT(DISTINCT CONCAT(bh.day, ':', bh.open_time, ':', bh.closing_time)) as hours_str " +
					"FROM Business b " +
					"JOIN Zipcode z ON b.zc_id = z.id " +
					"JOIN City c ON z.city_id = c.id " +
					"JOIN State s ON c.state_id = s.id " +
					"LEFT JOIN BusinessCategory bc ON b.id = bc.bid " +
					"LEFT JOIN Category cat ON bc.cid = cat.id " +
					"LEFT JOIN BusinessHour bh ON b.id = bh.bid " +
					"GROUP BY b.id " +
					"ORDER BY b.id";

			PreparedStatement stmt = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setFetchSize(1000);

			ResultSet rs = stmt.executeQuery();

			List<Document> batch = new ArrayList<>();
			final int BATCH_SIZE = 1000;

			//Date Formatter for parsing the constructed string
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

			while (rs.next()) {
				Document doc = new Document();
				doc.append("_id", rs.getInt("id"));
				doc.append("bname", rs.getString("name"));

				//Integers instead of SQL Booleans
				append(doc, rs, "is_open");
				append(doc, rs, "bike_parking");
				append(doc, rs, "good_for_dancing");
				append(doc, rs, "restaurants_good_for_groups");
				append(doc, rs, "restaurants_price_range");
				append(doc, rs, "restaurants_take_out");
				append(doc, rs, "restaurants_delivery");
				append(doc, rs, "restaurants_counter_service");
				append(doc, rs, "restaurants_table_service");
				append(doc, rs, "restaurants_reservations");
				append(doc, rs, "drive_thru");
				append(doc, rs, "wheelchair_accessible");
				append(doc, rs, "by_appointment_only");
				append(doc, rs, "dogs_allowed");
				append(doc, rs, "happy_hour");
				append(doc, rs, "byob");
				append(doc, rs, "corkage");
				append(doc, rs, "outdoor_seating");
				append(doc, rs, "accepts_insurance");
				append(doc, rs, "caters");
				append(doc, rs, "coat_check");
				append(doc, rs, "good_for_kids");
				append(doc, rs, "open_24_hours");
				append(doc, rs, "has_tv");
				append(doc, rs, "business_accepts_credit_cards");
				append(doc, rs, "business_accepts_bitcoin");

				//Location Doc structure
				Document location = new Document();
				location.append("address", rs.getString("address"));
				location.append("city", rs.getString("city_name"));
				location.append("state", rs.getString("state_name"));
				location.append("zipcode", rs.getString("zipcode_val"));
				location.append("latitude", rs.getBigDecimal("latitude"));
				location.append("longitude", rs.getBigDecimal("longitude"));


				String hoursStr = rs.getString("hours_str");
				Document hoursDoc = new Document();
				if (hoursStr != null && !hoursStr.isEmpty()) {
					String[] days = hoursStr.split(",");
					for (String d : days) {
						String[] parts = d.split(":");
						if (parts.length >= 3) {
							String dayKey = parts[0].trim();

							String openTimeStr = "", closeTimeStr = "";
							if (parts.length == 7) {
								openTimeStr = pad(parts[1]) + ":" + pad(parts[2]) + ":" + pad(parts[3]);
								closeTimeStr = pad(parts[4]) + ":" + pad(parts[5]) + ":" + pad(parts[6]);
							} else if (parts.length == 5) {
								openTimeStr = pad(parts[1]) + ":" + pad(parts[2]) + ":00";
								closeTimeStr = pad(parts[3]) + ":" + pad(parts[4]) + ":00";
							} else {
								openTimeStr = parts[1] + ":00";
								closeTimeStr = parts[2] + ":00";
							}

							hoursDoc.append(dayKey, createDailyHours(openTimeStr, closeTimeStr, sdf));
						}
					}
				}
				doc.append("hours", hoursDoc);
				doc.append("location", location);

				
				String catStr = rs.getString("categories_str");
				if (catStr != null && !catStr.isEmpty()) {
					doc.append("categories", Arrays.asList(catStr.split(",")));
				} else {
					doc.append("categories", new ArrayList<>());
				}

				batch.add(doc);

				if (batch.size() >= BATCH_SIZE) {
					businessCollection.insertMany(batch);
					batch.clear();
				}
			}

			if (!batch.isEmpty()) {
				businessCollection.insertMany(batch);
			}

		} catch (Exception oops) {
			oops.printStackTrace();
			System.out.println("Dang it!");
		}
	}

	private static MongoClient getClient(String mongoDBURL) {
		if (mongoDBURL.equals("None")) return MongoClients.create();
		return MongoClients.create(mongoDBURL);
	}

	/**
	Helper that appends if it exists
	 */
	private static void append(Document doc, ResultSet rs, String col) throws Exception {
		Object val = rs.getObject(col);
		if (val != null) {
			doc.append(col, val);
		}
	}

	/**
	 * Format: 1970-01-01THH:mm:ssZ
	 * @return the document which stores the daily hours
	 */
	private static Document createDailyHours(String open, String close, SimpleDateFormat sdf) {
		if (open == null || close == null) return null;
		Document day = new Document();
		try {
			Date openDate = sdf.parse("1970-01-01T" + open + "Z");
			Date closeDate = sdf.parse("1970-01-01T" + close + "Z");

			day.append("opening", openDate);
			day.append("closing", closeDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return day;
	}

	private static String pad(String t) {
		if (t == null) return "00";
		t = t.trim();
		return (t.length() == 1) ? "0" + t : t;
	}
}