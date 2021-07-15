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

		String accessKey = "AKIAUYHVPM3VAKLGPAXE";
		String secretKey = "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";


		BasicAWSCredentials credentials = new BasicAWSCredentials(
				accessKey,
				secretKey
		);

		AWSCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();

		AmazonRDS awsRDS = AmazonRDSClientBuilder.standard().withRegion(Regions.US_EAST_2).withCredentials(new AWSStaticCredentialsProvider(credentials)).build();

		DescribeDBInstancesResult dbInstResult = awsRDS.describeDBInstances();

		List<DBInstance> dbInstances = dbInstResult.getDBInstances();

		for(DBInstance dbInst:dbInstances){
			System.out.println("DB Instance:: " + dbInst);
		}

		// .withCredentials(credentials)

		System.out.println("It worked!");
	}

}
