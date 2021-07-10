package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class NewUser {
    
    private BufferedReader in;
	private PrintWriter out;
    
    public void createNewUser(String s){

        in = new BufferedReader(new InputStreamReader(System.in));
        out = new PrintWriter(out, true);

        try {
        // ask for first name
        out.println("Enter First Name");
        String firstName = in.readLine();
        // ask for last name
        out.println("Enter Surname");
        String surname = in.readLine();

        //social security ID
        out.println("Enter SSN");
        String ssn = in.readLine();

        //dob
        out.println("Enter Date of Birth in DDMMYYYY format");
        String dob = in.readLine();

        //validate - later
        
        //place of birth
        out.println("Enter Place of Birth");
        String placeOfBirth = in.readLine();

        //mobile phone number
        out.println("Enter Mobile Phone Number");
        String mobileNummber = in.readLine();

        //email
        out.println("Enter email address");
        String email = in.readLine();

        //address
        out.println("Enter First Line of Address");
        String addressFirstLine = in.readLine();

        out.println("Enter Second Line of Address");
        String addressSecondLine = in.readLine();

        out.println("Enter Town or City");
        String townOrCity = in.readLine();

        out.println("Enter Postcode");
        String postcode = in.readLine();

        //password
        passwordGenerator();
        //password method with instructions
        

     } catch (IOException e) {
        
        }

    }

    //add all data to server
    //use a hashtable? 

    //add account to server 
    Account newAccount = new Account(accountName, openingBalance);

    

    public String passwordGenerator() throws IOException {
    //password instructions
    out.println("Please create a new password");
    out.println("Passwords must be...");
    
    String password = in.readLine();
    return password;
        
    }
}
