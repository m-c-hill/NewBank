package server;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.Random;

public class TwoFactorAuth {

    private static String expectedCode = "null";
    private static boolean authentication;

    // generate random 4 digit code - store as String 'expectedCode'
    public static String generateCode() {

        Random random = new Random();
        String expectedCode = String.format("%04d", random.nextInt(10000));
        return expectedCode;
    }

    public static void codeInput() {

        BufferedReader in;
        PrintWriter out;

        // authorisation false until correct input
        boolean authentication = false;

        // askforcodeinput
        System.out.println("Please enter the four digit code:");
        String inputCode = InputProcessor.takeCodeInput("four digit code", in, out);

        // ifcodematches, success, else fail
        if (inputCode == expectedCode) {
            authentication = true;
        } else {
            System.out.println("Incorrect code.");
        }
    }

    public static boolean twoFactorAuthentication() {

        Sms.sendText(generateCode());

        System.out.println("Your one-time passcode has been sent to your phone");

        codeInput();

        return authentication;
    }

}