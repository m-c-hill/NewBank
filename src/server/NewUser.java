package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class NewUser {
    
    private BufferedReader in;
	private PrintWriter out;

    private int totalInputsComplete;
    private Boolean nameInput;
    private Boolean ssnInput;
    private Boolean dobInput;
    private Boolean pobInput;
    private Boolean emailInput;
    private Boolean mobNumInput;
    private Boolean addressInput;
    
    public void createNewUser(String s){

        in = new BufferedReader(new InputStreamReader(System.in));
        out = new PrintWriter(out, true);

        totalInputsComplete = 0;
        nameInput = false;
        ssnInput = false;
        dobInput = false;
        pobInput = false;
        emailInput = false;
        mobNumInput = false;
        addressInput = false;
        //when an input is complete/verified in its method, Input = true

        //while all inputs !complete, print required inputs
        while(totalInputsComplete>7){

        //user interface
        out.println("Inputs required: " + (7-totalInputsComplete));

        if (nameInput==false){
            nameMethod();
        }
           
        if (ssnInput == false){
            ssnMethod();
        }

        if (dobInput == false){
            dobMethod();
        }
            
        if (pobInput == false){
            pobMethod();
        }
            
        if (mobNumInput == false){
            mobNumMethod();
        }
            
        if (emailInput == false){
            emailMethod();
        }
            
        if (addressInput == false){
            addressMethod();
        }
    
        }

        //password
        passwordGenerator();
        //password method with instructions

        //opening balance - start at zero until money is added
        double openingBalance = 0;

        //when all information complete - add account to server
        Account newAccount = new Account(accountName, openingBalance);


    }

    public void nameMethod(){
        try{
        // ask for first name
        out.println("Enter First Name");
        String firstName = in.readLine();
        // ask for last name
        out.println("Enter Surname");
        String surname = in.readLine();
        }
    catch (IOException e) {
        
    }
    nameInput = true;
    totalInputsComplete++;
    out.println("Accepted. Section input complete.");
    }

    public void ssnMethod(){
        try{
        out.println("Enter SSN");
        String ssn = in.readLine();
        }
    catch (IOException e) {
        
    }
    ssnInput = true;
    totalInputsComplete++;
    out.println("Accepted. Section input complete.");
    }

    public void dobMethod(){
        try{
            out.println("Enter Date of Birth in DDMMYYYY format");
            String dob = in.readLine();
        }
        catch (IOException e) {

        }
        dobInput = true;
        totalInputsComplete++;
        out.println("Section input complete.");
    }

    public void pobMethod(){
        try{
            out.println("Enter Place of Birth");
            String placeOfBirth = in.readLine();

        }
    catch (IOException e) {
        
    }
    pobInput = true;
    totalInputsComplete++;
    out.println("Accepted. Section input complete.");
    }

    public void mobNumMethod(){
        try{
            out.println("Enter Mobile Phone Number");
            String mobileNummber = in.readLine();    
        String ssn = in.readLine();
        }
        //validateMethod
    catch (IOException e) {
        
    }
    mobNumInput = true;
    totalInputsComplete++;
    out.println("Accepted. Section input complete.");
    }

    public void emailMethod(){
        try{
            out.println("Enter a valid email address");
            String email = in.readLine();    
        String ssn = in.readLine();
        }
        //validateMethod
    catch (IOException e) {
        
    }
    emailInput = true;
    totalInputsComplete++;
    out.println("Accepted. Section input complete.");
    }

    public void addressMethod(){
        try{
        out.println("Enter First Line of Address");
        String addressFirstLine = in.readLine();

        out.println("Enter Second Line of Address");
        String addressSecondLine = in.readLine();

        out.println("Enter Town or City");
        String townOrCity = in.readLine();

        out.println("Enter Postcode");
        String postcode = in.readLine();
   
        String ssn = in.readLine();
        }
        //validateMethod
    catch (IOException e) {
        
    }
    addressInput = true;
    totalInputsComplete++;
    out.println("Accepted. Section input complete.");
    }
    
    //add all data to server? -how? 
    //use a database? 
    
    public String passwordGenerator() throws IOException {
    //password instructions
    out.println("Please create a new password");
    out.println("Passwords must be...");
    
    String password = in.readLine();
    //verify
    return password;
        
    }
}
