package edu.rit.ibd.a1;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.Statement;
import java.sql.PreparedStatement;


import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.function.IOIterator;

public class LoadUsers {

	public static void main(String[] args) {
		final String jdbcURL = args[0], jdbcUser = args[1], jdbcPwd = args[2];
		final String gZipFile = args[3];

		try (Connection con = DriverManager.getConnection(jdbcURL, jdbcUser, jdbcPwd);) {

			con.setAutoCommit(false);

			Create_Tables(con);

			InputStream is = Files.newInputStream(Paths.get(gZipFile));
			GzipCompressorInputStream xzis = new GzipCompressorInputStream(is);
			TarArchiveInputStream taris = new TarArchiveInputStream(xzis);
			IOIterator<TarArchiveEntry> it = taris.iterator();

			while (it.hasNext()) {
				TarArchiveEntry entry = it.next();

				if (entry.getName().equals("yelp_academic_dataset_user.json")) {
					Buffer_Data(con, taris);
				}
			}

		} catch (Exception oops) {
			System.out.println("Dang it!");
		}
	}

	private static void Create_Tables(Connection con) throws SQLException {
		Statement stmt = con.createStatement();

		stmt.executeUpdate("DROP TABLE IF EXISTS UserElite");
		stmt.executeUpdate("DROP TABLE IF EXISTS User");

		String User_Table = "CREATE TABLE IF NOT EXISTS User (" +
				"id VARCHAR(255), " +
				"name VARCHAR(255), " +
				"yelping_since VARCHAR(255), " +
				"useful INTEGER, " +
				"funny INTEGER, " +
				"cool INTEGER, " +
				"fans INTEGER, " +
				"compliment_hot INTEGER, " +
				"compliment_more INTEGER, " +
				"compliment_profile INTEGER, " +
				"compliment_cute INTEGER, " +
				"compliment_list INTEGER, " +
				"compliment_note INTEGER, " +
				"compliment_plain INTEGER, " +
				"compliment_cool INTEGER, " +
				"compliment_funny INTEGER, " +
				"compliment_writer INTEGER, " +
				"compliment_photos INTEGER, " +
				"PRIMARY KEY (id))";

		String UserElite_Table = "CREATE TABLE IF NOT EXISTS UserElite (" +
				"uid VARCHAR(255), " +
				"year INTEGER, " +
				"PRIMARY KEY (uid, year), " +
				"FOREIGN KEY (uid) REFERENCES User(id))";

		stmt.executeUpdate(User_Table);
		stmt.executeUpdate(UserElite_Table);
		con.commit();
	}

	private static void Buffer_Data(Connection con, InputStream stream) throws SQLException {
		String userSQL = "INSERT INTO User (id, name, yelping_since, useful, funny, cool, fans, " +
				"compliment_hot, compliment_more, compliment_profile, compliment_cute, " +
				"compliment_list, compliment_note, compliment_plain, compliment_cool, " +
				"compliment_funny, compliment_writer, compliment_photos) " +
				"VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		String eliteSQL = "INSERT IGNORE INTO UserElite (uid, year) VALUES (?, ?)";

		Scanner sc = new Scanner(stream, "UTF-8");

		try (PreparedStatement Prep_User = con.prepareStatement(userSQL);
			 PreparedStatement Prep_Elite = con.prepareStatement(eliteSQL)) {

			int count = 0;
			while (sc.hasNextLine()) {
				String line = sc.nextLine();
				try {
					String uid = extract(line, "user_id");
					if (uid == null) continue;
					Prep_User.setString(1, uid);
					Prep_User.setString(2, extract(line, "name"));
					Prep_User.setString(3, extract(line, "yelping_since"));

					Prep_User.setInt(4, parseCount(extract(line, "useful")));
					Prep_User.setInt(5, parseCount(extract(line, "funny")));
					Prep_User.setInt(6, parseCount(extract(line, "cool")));
					Prep_User.setInt(7, parseCount(extract(line, "fans")));
					Prep_User.setInt(8, parseCount(extract(line, "compliment_hot")));
					Prep_User.setInt(9, parseCount(extract(line, "compliment_more")));
					Prep_User.setInt(10, parseCount(extract(line, "compliment_profile")));
					Prep_User.setInt(11, parseCount(extract(line, "compliment_cute")));
					Prep_User.setInt(12, parseCount(extract(line, "compliment_list")));
					Prep_User.setInt(13, parseCount(extract(line, "compliment_note")));
					Prep_User.setInt(14, parseCount(extract(line, "compliment_plain")));
					Prep_User.setInt(15, parseCount(extract(line, "compliment_cool")));
					Prep_User.setInt(16, parseCount(extract(line, "compliment_funny")));
					Prep_User.setInt(17, parseCount(extract(line, "compliment_writer")));
					Prep_User.setInt(18, parseCount(extract(line, "compliment_photos")));

					Prep_User.addBatch();

					String eliteYears = extract(line, "elite");
					if (eliteYears != null && !eliteYears.equalsIgnoreCase("None") && !eliteYears.isEmpty() && !eliteYears.equals("null")) {
						for (String year : eliteYears.split(",")) {
							year = year.trim();
							if (!year.isEmpty()) {
								try {
									Prep_Elite.setString(1, uid);
									Prep_Elite.setInt(2, Integer.parseInt(year));
									Prep_Elite.addBatch();
								} catch (NumberFormatException e) {
									// Ignore malformed years
								}
							}
						}
					}

					// Execute Batch
					if (++count % 1000 == 0) {
						Prep_User.executeBatch();
						Prep_Elite.executeBatch();
						con.commit();
					}
				} catch (Exception e) {
					try {
						Prep_User.clearParameters();
						Prep_Elite.clearParameters();
					} catch (SQLException sqle) {

					}
				}
			}
			Prep_User.executeBatch();
			Prep_Elite.executeBatch();
			con.commit();
		}
	}

	private static String extract(String line, String key) {
		String token = "\"" + key + "\"";
		int startSearch = 0;
		int keyPos = -1;
		while (startSearch < line.length()) {
			int found = line.indexOf(token, startSearch);
			if (found == -1) return null; // Key not found anywhere

			int afterKey = found + token.length();

			while (afterKey < line.length() && line.charAt(afterKey) == ' ') {
				afterKey++;
			}

			// Check if the next significant character is a colon
			if (afterKey < line.length() && line.charAt(afterKey) == ':') {
				keyPos = found;
				break;
			}
			startSearch = found + 1;
		}

		if (keyPos == -1) return null;

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

	private static int parseCount(String val) {
		if (val == null || val.equals("null") || val.equalsIgnoreCase("None") || val.isEmpty()) {
			return 0;
		}
		try {
			return Integer.parseInt(val);
		} catch (NumberFormatException e) {
			return 0;
		}
	}
}