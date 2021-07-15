package server;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClientBuilder;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;

import java.util.List;

// class AWSCredentialManager

public class Connection {

	public static void main(String[] args) {

		// AWS credential - will need to securely store these in the future rather than hard code them in as below
		String accessKey = "AKIAUYHVPM3VAKLGPAXE";
		String secretKey = "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";

		// Create credential object to connect to AWS
		BasicAWSCredentials credentials = new BasicAWSCredentials(
				accessKey,
				secretKey
		);

		// Use credentials to connect to database
		AmazonRDS awsRDS = AmazonRDSClientBuilder.standard().withRegion(Regions.US_EAST_2).withCredentials(new AWSStaticCredentialsProvider(credentials)).build();

		// TODO: add in a try catch block to account for failed connection

		// Example of retrieving data - retrieves all database instances associated with the connection
		DescribeDBInstancesResult dbInstResult = awsRDS.describeDBInstances();

		// Convert this into a list object and then loop through the list to print the instances returned by our query.
		// This should only return one instance since we've only created a single database instance called "newbank"
		List<DBInstance> dbInstances = dbInstResult.getDBInstances();
		for(DBInstance dbInst:dbInstances){
			System.out.println("DB Instance:: " + dbInst);
			System.out.println("-------------------------\n");
		}

		// When run, this should return a dictionary with the database instance information!
	}
}
