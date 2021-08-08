package server;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

public class Sms {

	public static final String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
	//twilio server account TOKEN
	public static final String AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");
	//number to send the SMS to -target number
	public static final String PHONE_TO_SEND = System.getenv("MY_PHONE_NUMBER");
	//number to send the SMS from, provided by the TWILIO servers
	public static final String TRIAL_NUMBER = System.getenv("TWILIO_TRIAL_NUMBER");
	
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

