package server;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Random;

//TWILIO library imports for the SMS Server 
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

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

	public static boolean twoFactorAuthentication(/*user? - how to connect these??*/){

		BufferedReader in;
		PrintWriter out;

		//authorisation false until correct input
		boolean authentication = false;
		
		//getphonenumber
		
		//String numberOut = User.getPhoneNumber(); - getter needs to be static? I daren't change it! 
		
		//generate random 4 digit code - store as String 'expectedCode'
		Random random = new Random();
		String expectedCode = String.format("%04d", random.nextInt(10000));

		//sendtophone

		//twilio method - send 'code' to user with instructions

		System.out.println("Your four digit passcode has been sent to: " /* + phonenumber*/); // add phonenumber

		//askforcodeinput
		System.out.println("Please enter the four digit code:");
		String inputCode = InputProcessor.takeCodeInput("four digit code", in, out);

		//ifcodematches, success, else fail
		if (inputCode == expectedCode){
			authentication = true;
		} else {
			System.out.println("Incorrect code.");
		}

		return authentication;
	}
	
}
