package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class NewUser {
    
    private BufferedReader in;
    
    public void createNewUser(String s){

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    
        try {
        // ask for first name
        System.out.println("Enter First Name");
        String firstName = in.readLine();
        // ask for last name
        System.out.println("Enter Surname");
        String surname = in.readLine();

        //social security ID
        System.out.println("Enter SSN");
        String ssn = in.readLine();

        //dob
        System.out.println("Enter Date of Birth in DDMMYYYY format");
        String dob = in.readLine();

        //validate - later
        
        //place of birth
        System.out.println("Enter Place of Birth");
        String placeOfBirth = in.readLine();

        //mobile phone number
        System.out.println("Enter Mobile Phone Number");
        String mobileNummber = in.readLine();

        //email
        System.out.println("Enter email address");
        String email = in.readLine();

        //address
        System.out.println("Enter First Line of Address");
        String addressFirstLine = in.readLine();

        System.out.println("Enter Second Line of Address");
        String addressSecondLine = in.readLine();

        System.out.println("Enter Town or City");
        String townOrCity = in.readLine();

        System.out.println("Enter Postcode");
        String postcode = in.readLine();

        //password
        System.out.println("Enter a valid password");
        
        //password method with instructions
        String password = in.readLine();

     } catch (IOException e) {
        
        }

    }  
}
