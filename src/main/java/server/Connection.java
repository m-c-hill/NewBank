package server;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClientBuilder;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.sql.*;
import java.util.List;

public class Connection {

	/**
	 * Simple method to retrieve aws credentials from .secrets/aws_credentials.json
	 * @return an array containing the access key and secret key
	 */
	private static String[] getAWSCredentials(){
		String key = null;
		String pw = null;

		JSONParser parser = new JSONParser();
		try{
			// Read in credentials json file, parse to create a json object
			FileReader credFile = new FileReader(".secrets/aws_credentials.json");
			Object obj = parser.parse(credFile);
			JSONObject credJson = (JSONObject) obj;

			// Index credJson to retrieve key and secret
			key = (String) credJson.get("access_key");
			pw = (String) credJson.get("secret_key");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		// TODO: change above catch block to logging output; unique way to handle each exception
		return new String[] {key, pw};
	}

	/**
	 * Method to create the connection object for the aws instance
	 * @return AmazonRDS connection object
	 */
	private static AmazonRDS getRDSConnection(){

		AmazonRDS awsRDS = null;

		// Get the credentials used to connect to the database
		String[] credArray = getAWSCredentials();
		String accessKey = credArray[0];
		String secretKey = credArray[1];

		try {
			// Create credential object to connect to AWS
			BasicAWSCredentials credentials = new BasicAWSCredentials(
					accessKey,
					secretKey
			);

			// Use credentials to connect to database
			awsRDS = AmazonRDSClientBuilder.standard().withRegion(Regions.US_EAST_2).withCredentials(new AWSStaticCredentialsProvider(credentials)).build();
		}
		catch(Exception e){
			// TODO: handle connection exceptions better - for now this is fine
			e.printStackTrace();
		}

		// Return aws database connection object
		return awsRDS;
	}

	/**
	 * Method to create the conncetion object for the newbank database
	 * @return mysql database connection object
	 */
	public static java.sql.Connection getDBConnection() {

		// Establish aws connection and retrieve newbank instance
		AmazonRDS awsRDS = getRDSConnection();
		DescribeDBInstancesResult dbInstResult = awsRDS.describeDBInstances();
		DBInstance dbInstance = dbInstResult.getDBInstances().get(0);

		// Retrieve database connection information from aws instance
		String name = dbInstance.getDBInstanceIdentifier();
		String engine = dbInstance.getEngine();
		String username = dbInstance.getMasterUsername();
		String endpoint = dbInstance.getEndpoint().getAddress();
		String port = dbInstance.getEndpoint().getPort().toString();
		String password = "password"; // TODO: move this to a more secure location

		java.sql.Connection con = null;

		// Database url
		// Reference: https://docs.aws.amazon.com/elasticbeanstalk/latest/dg/java-rds.html
		String url = "jdbc:" + engine + "://" + endpoint + ":" + port + "/" + name + "?user" + username + "&password=" + password;

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			System.out.println(e);
		}
		return con;
	}

	public static void main(String[] args) throws SQLException {

		// Example of retrieving data - retrieves all database instances associated with the connection
		AmazonRDS awsRDS = getRDSConnection();
		DescribeDBInstancesResult dbInstResult = awsRDS.describeDBInstances();

		// Convert this into a list object and then loop through the list to print the instances returned by our query.
		// This should only return one instance since we've only created a single database instance called "newbank"
		List<DBInstance> dbInstances = dbInstResult.getDBInstances();

		// When run, this should print a dictionary with the database instance information!
		for(DBInstance dbInst:dbInstances){
			System.out.println("DB Instance:: " + dbInst);
			System.out.println("-------------------------\n");
		}


		// Create the database connection
		java.sql.Connection con = getDBConnection();

		// Example database queries

		// Print all tables in the database
		try(Statement stmt = con.createStatement()) {
			ResultSet rs = stmt.executeQuery("Show tables");
			while (rs.next()) {
				System.out.println(rs.getString(1));
				System.out.println();
			}
		}

		// This query will print selected data for a number of customers
		try(Statement stmt = con.createStatement()){

			String query = "SELECT prefix, first_names, last_name, date_of_birth, email_address, phone_number, national_insurance_number, address.postcode FROM customer\nLEFT JOIN user\nON user.user_id = customer.user_id\nLEFT JOIN address\nON user.address_id = address.address_id\n";

			ResultSet transactions = stmt.executeQuery(query);
			ResultSetMetaData rsmd = transactions.getMetaData();
			int columnsNumber = rsmd.getColumnCount();

			// Print results as table - reference: https://coderwall.com/p/609ppa/printing-the-result-of-resultset
			while (transactions.next()) {
				for (int i = 1; i <= columnsNumber; i++) {
					if (i > 1) System.out.print("\n");
					String columnValue = transactions.getString(i);
					System.out.print(rsmd.getColumnName(i) + ": " + columnValue);
				}
				System.out.println("\n\n");
			}

		}
	}
}



