package server.communication;

//reference web site for the SMS implementations: https://www.twilio.com/docs/sms/quickstart/java

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

public class Sms {

	// Retrieve credential from secrets json
	static String[] credentials = TwilioCredentials.getTwilioCredentials();

	public static final String ACCOUNT_SID = credentials[0];
	//twilio server account TOKEN
	public static final String AUTH_TOKEN = credentials[1];
	//number to send the SMS to -target number
	public static final String PHONE_TO_SEND = credentials[2];
	//number to send the SMS from, provided by the TWILIO servers
	public static final String TRIAL_NUMBER = credentials[5];
	
	/**
	 * This method allows to send an SMS using external Twilio hosting service
	 * The ACCOUNT_SID, AUTH_TOKEN is the server credentials
	 * The PHONE_TO_SEND is the target phone number and the TRIAL_NUMBER
	 * is the phone number provided by TWILIO web service
	 * They are stored secret under environment variables.
	 **/
	public static void sendText(String notification) { 	
	     Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
	     Message message = Message.creator(
	             new com.twilio.type.PhoneNumber(PHONE_TO_SEND),
	             new com.twilio.type.PhoneNumber(TRIAL_NUMBER),
	             notification)
	         .create();
	     System.out.println(message.getSid());
	 }
}

