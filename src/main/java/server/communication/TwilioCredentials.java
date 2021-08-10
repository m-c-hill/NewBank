package server.communication;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;

public class TwilioCredentials {
	/**
	 * Simple method to retrieve Twilio credentials from .secrets/twilio_credentials.json
	 * @return an array containing credentials, sender email and phone number
	 */
	public static String[] getTwilioCredentials(){
		String sid = null;
		String authToken = null;
		String phoneNumber = null;
		String senderEmail = null;
		String senderPassword = null;
		String twilioTrialNumnber = null;

		JSONParser parser = new JSONParser();
		try{
			// Read in credentials json file, parse to create a json object
			FileReader credFile = new FileReader(".secrets/twilio_credentials.json");
			Object obj = parser.parse(credFile);
			JSONObject credJson = (JSONObject) obj;

			// Index credJson to retrieve information
			sid = (String) credJson.get("TWILIO_ACCOUNT_SID");
			authToken = (String) credJson.get("TWILIO_AUTH_TOKEN");
			phoneNumber = (String) credJson.get("MY_PHONE_NUMBER");
			senderEmail = (String) credJson.get("SENDER_EMAIL");
			senderPassword = (String) credJson.get("SENDER_PASSWORD");
			twilioTrialNumnber = (String) credJson.get("TWILIO_TRIAL_NUMBER");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return new String[] {sid, authToken, phoneNumber, senderEmail, senderPassword, twilioTrialNumnber};
	}
}
