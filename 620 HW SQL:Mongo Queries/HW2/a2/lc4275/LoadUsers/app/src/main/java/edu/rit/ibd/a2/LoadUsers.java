package edu.rit.ibd.a2;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import com.mongodb.client.MongoCollection;

/**
 * Author: Liam Cui
 * RIT
 * CSCI 620: A2 Load Users
 *
 * Loads users data from yelp SQL data dump into mongo DB
 * Note for future Liam, It was NOT the tar.gz yelp data dump.
 * It was the SQL data provided by professor Rivero.
 *
 * Changes to field names from the SQL to Mongo
 * VVVVVV SQL name on left Mongo name on right  VVVVVV
 *
 * id to _id (duh mongo needs the underscore dummy)
 * name to uname
 * ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 *
 * Also changed 1 to many SQL relations into array form in mongo db
 */
public class LoadUsers {

	public static void main(String[] args) {
		final String jdbcURL = args[0], jdbcUser = args[1], jdbcPwd = args[2];
		final String mongoDBURL = args[3], mongoDBName = args[4];

		try (Connection con = DriverManager.getConnection(jdbcURL, jdbcUser, jdbcPwd);
			 MongoClient client = getClient(mongoDBURL);) {

			MongoDatabase db = client.getDatabase(mongoDBName);
			MongoCollection<Document> usersCollection = db.getCollection("Users");

			usersCollection.drop();
			Statement setupStmt = con.createStatement();
			setupStmt.execute("SET SESSION group_concat_max_len = 10000");
			setupStmt.close();
			String sql = "SELECT " +
					"  u.*, " +
					"  GROUP_CONCAT(ue.year ORDER BY ue.year ASC) as elite_years_str " +
					"FROM user u " +
					"LEFT JOIN userelite ue ON u.id = ue.uid " +
					"GROUP BY u.id " +
					"ORDER BY u.id";

			PreparedStatement stmt = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			stmt.setFetchSize(1000);

			ResultSet rs = stmt.executeQuery();

			List<Document> batch = new ArrayList<>();
			final int BATCH_SIZE = 1000;

			while (rs.next()) {
				Document doc = new Document();
				doc.append("_id", rs.getInt("id"));
				doc.append("uname", rs.getString("name")); // Map 'name' to 'uname'
				doc.append("yelping_since", rs.getString("yelping_since"));
				appendIfGtZero(doc, rs, "useful");
				appendIfGtZero(doc, rs, "funny");
				appendIfGtZero(doc, rs, "cool");
				appendIfGtZero(doc, rs, "fans");
				appendIfGtZero(doc, rs, "compliment_hot");
				appendIfGtZero(doc, rs, "compliment_more");
				appendIfGtZero(doc, rs, "compliment_profile");
				appendIfGtZero(doc, rs, "compliment_cute");
				appendIfGtZero(doc, rs, "compliment_list");
				appendIfGtZero(doc, rs, "compliment_note");
				appendIfGtZero(doc, rs, "compliment_plain");
				appendIfGtZero(doc, rs, "compliment_cool");
				appendIfGtZero(doc, rs, "compliment_funny");
				appendIfGtZero(doc, rs, "compliment_writer");
				appendIfGtZero(doc, rs, "compliment_photos");
				String eliteStr = rs.getString("elite_years_str");


				if (eliteStr != null && !eliteStr.isEmpty()) {
					String[] yearsArr = eliteStr.split(",");
					List<Integer> yearsList = new ArrayList<>();
					for (String y : yearsArr) {
						try {
							yearsList.add(Integer.parseInt(y.trim()));
						} catch (NumberFormatException e) {
							// Ignore malformed years
						}
					}
					if (!yearsList.isEmpty()) {
						doc.append("elite_years", yearsList);
					}
				}
				batch.add(doc);


				if (batch.size() >= BATCH_SIZE) {
					usersCollection.insertMany(batch);
					batch.clear();
				}
			}


			if (!batch.isEmpty()) {
				usersCollection.insertMany(batch);
			}

		} catch (Exception oops) {
			oops.printStackTrace();
			System.out.println("Dang it!");
		}
	}
	/**
	What it do baby
	 */
	private static void appendIfGtZero(Document doc, ResultSet rs, String col) throws Exception {
		int val = rs.getInt(col);
		if (val > 0) {
			doc.append(col, val);
		}
	}


	private static MongoClient getClient(String mongoDBURL) {
		MongoClient client = null;
		if (mongoDBURL.equals("None"))
			client = MongoClients.create();
		else
			client = MongoClients.create(mongoDBURL);
		return client;
	}

}
