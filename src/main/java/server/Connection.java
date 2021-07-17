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
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.DriverManager;
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

	/*
	private static String[] getDBCredentials(){
		String instance = null;
		String endpoint = null;
		String port = null;
		String user = null;
		String password = null;
		String region = null;

		JSONParser parser = new JSONParser();
		try{
			// Read in credentials json file, parse to create a json object
			FileReader credFile = new FileReader(".secrets/db_credentials.json");
			Object obj = parser.parse(credFile);
			JSONObject credJson = (JSONObject) obj;

			// Index credJson to retrieve key and secret
			instance = (String) credJson.get("instance");
			endpoint = (String) credJson.get("endpoint");
			port = (String) credJson.get("port");
			user = (String) credJson.get("user");
			password = (String) credJson.get("password");
			region = (String) credJson.get("region");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return new String[] {instance, endpoint, port, user, password, region};
	}
	*/

	/**
	 * Method to create the connection object for the aws database instance
	 * @return AmazonRDS database connection object
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

	public static java.sql.Connection getDBConnection() {
		AmazonRDS awsRDS = getRDSConnection();
		DescribeDBInstancesResult dbInstResult = awsRDS.describeDBInstances();
		DBInstance dbInstance = dbInstResult.getDBInstances().get(0);

		String name = dbInstance.getDBInstanceIdentifier();
		String engine = dbInstance.getEngine();
		String username = dbInstance.getMasterUsername();
		String endpoint = dbInstance.getEndpoint().getAddress();
		String port = dbInstance.getEndpoint().getPort().toString();
		String password = "password";

		java.sql.Connection con = null;

		String url = "jdbc:" + engine + "://" + endpoint + ":" + port + "/" + name + "?user" + username + "&password=" + password;
		System.out.println(url);

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection(url, username, "password");
			System.out.println("Success!");
		} catch (Exception e) {
			System.out.println(e);
		}
		return con;
	}

	public static void main(String[] args) {

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

		java.sql.Connection con = getDBConnection();
	}
}
