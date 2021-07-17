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
import java.util.List;

// class AWSCredentialManager

public class Connection {

	/**
	 * Simple method to retrieve aws credentials from .secrets/credentials.json
	 * @return an array containing the access key and secret key
	 */
	private static String[] getCredentials(){
		String key = null;
		String pw = null;

		JSONParser parser = new JSONParser();
		try{
			// Read in credentials json file, parse to create a json object
			FileReader credFile = new FileReader(".secrets/credentials.json");
			Object obj = parser.parse(credFile);
			JSONObject credJson = (JSONObject) obj;

			// Index credJson to retrieve key and secret
			key = (String) credJson.get("access_key");
			pw = (String) credJson.get("secret_key");
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		catch(ParseException e){
			e.printStackTrace();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		// TODO: change above catch messages to logging output; unique way to handle each exception
		return new String[] {key, pw};
	}

	/**
	 * Method to create the connection object for the aws database instance
	 * @return AmazonRDS database connection object
	 */
	public static AmazonRDS getConnection(){

		AmazonRDS awsRDS = null;

		// Get the credentials used to connect to the database
		String[] credArray = getCredentials();
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

	public static void main(String[] args) {

		// Example of retrieving data - retrieves all database instances associated with the connection
		AmazonRDS awsRDS = getConnection();
		DescribeDBInstancesResult dbInstResult = awsRDS.describeDBInstances();

		// Convert this into a list object and then loop through the list to print the instances returned by our query.
		// This should only return one instance since we've only created a single database instance called "newbank"
		List<DBInstance> dbInstances = dbInstResult.getDBInstances();

		// When run, this should print a dictionary with the database instance information!
		for(DBInstance dbInst:dbInstances){
			System.out.println("DB Instance:: " + dbInst);
			System.out.println("-------------------------\n");
		}
	}
}
