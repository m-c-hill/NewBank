package server.support;

import server.account.Account;
import server.bank.BankLoan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// This is a utility class to help with taking and validating user input.
// 
// You can call InputProcessor(String key) to take and validate an input value that matches the provided the key and then assigning the return value to a variable
// For example, InputProcessor("date") will look through the map to get the regex defining the date format  
public class InputProcessor{
    // Mapping each key to its value (regex)
    // Add the rest of the keys with their relevant regexes to this Map
    // The regexes could use some refinement for more precision
    // Test
    private static final Map<String, String> InfoRegexMap = Map.of(
        "email", "^(.+)@(.+)$",
        "date", "^\\d{2}/\\d{2}/\\d{4}$",
        "letters", "^[A-Za-z]+$",
        "numbers", "^[0-9]+$",
        "phonenumber", "^0[\\d]{10}$", //must start with a 0 and be followed by 10 numbers (basic version) OR 1?[\\s-]?\\(?(\\d{3})\\)?[\\s-]?\\d{3}[\\s-]?\\d{4} for international formats (advanced)
        "postcode", "^[a-zA-Z0-9]*$"
        );

    
    // Method to take and validate input
    public static String takeValidInput(String key, BufferedReader in, PrintWriter out) {
        String info = null;
        try {
            while (true) {
                info = in.readLine();
                // Check if the provided input is valid by comparing its format with the relevant regex
                // Upon matching, break and return the given input
                if (isValid(info, InfoRegexMap.get(key))) {
                    break;
                } else {
                    // Display a message if the input is invalid. Loop again to take a new input
                    out.println("Please enter a valid " + key + "(s). \nTry again:");
                    continue;
                }
            }

        } catch (IOException e) {
            out.println("Input error");
        }

        return info;
    }

    // Method overload for capturing the correct account name
    public static String takeValidInput(ArrayList<Account> accountsList, BufferedReader in, PrintWriter out){
        String account = null;
        try {
            while (true) {
                account = in.readLine();
                
                //if Exit is entered take it as a valid input to go back to main menu
                if (account.equals("Exit")) {
                   break;
                }
                
                // Check if the provided account name matches one of the accounts belonging to the Customer
                // Upon matching, break and return the given input
                else if (accountExists(account, accountsList)) {
                    break;
                }
                
                else{
                    // Display a message when no match is found
                    out.println("Please enter a valid account name. \nTry again:");
                }
            }

        } catch (IOException e) {
            out.println("Input error");
        }

        return account;
    }

    // Method to take the account name with sufficient funds to pay back the loan
    public static String takeValidInput(ArrayList<Account> accountsList, double amountToPayBack, BufferedReader in, PrintWriter out){
        String accountName = null;
        try {
            while(true){
                accountName = in.readLine();
                if (accountExists(accountName, accountsList)) {
                    if (hasSufficientFunds(accountName, accountsList, amountToPayBack)) {
                        break;
                    }
                    else{
                        out.println("Insufficient funds. Choose a different account or make a new deposit:");
                        continue;
                    }
                }
                else{
                    out.println("Account name does not exist. Try again:");
                }
            }
        } catch (Exception e) {
            out.println("Input error.");
        }
        return accountName;
    }

    // Method to take and validate the requested amount to withdraw
    public static double takeValidDoubleInput(double balance, BufferedReader in, PrintWriter out){
        double requestedAmount = 0;

        while (true) {
            try {
                String strRequestedAmount = in.readLine();
                requestedAmount = Double.parseDouble(strRequestedAmount);

                if (requestedAmount <= balance) {
                    break;
                }
                else{
                    out.println("The requested amount should be less than or equal to the available balance. Try again:");
                }
            } catch (Exception e) {
                out.println("Can't process non-numerical characters. Try again:");
            }
        }

        return requestedAmount;
    }

    public static double takeValidLoanAmountInput(double loanLimit, BufferedReader in, PrintWriter out){
        double requestedAmount = 0;

        while (true) {
            try {
                String strRequestedAmount = in.readLine();
                requestedAmount = Double.parseDouble(strRequestedAmount);

                if (requestedAmount <= loanLimit) {
                    break;
                }
                else{
                    out.println("The requested amount should be less than or equal to the set loan limit. Try again:");
                }
            } catch (Exception e) {
                out.println("Can't process non-numerical characters. Try again:");
            }
        }

        return requestedAmount;
    }
    
 // Method to take and validate a new account name when creating a new Account
    public static String createValidAccountName(ArrayList<Account> accountsList, BufferedReader in, PrintWriter out){
        String account = null;
        try {
            while (true) {
                account = in.readLine();
                //if Exit is entered take it as a valid input to go back to main menu
                if (account.equals("Exit")) {
                    break;
                }
                
                // Check if the provided account name matches one of the accounts belonging to the Customer
                // If the name is not used before then accept it
                if (!accountExists(account, accountsList)) {
                    break;
                }
                else{
                    // Display a message that the name is already used
                    out.println("This name exists. Please enter a new valid account name. ");
                }
            }

        } catch (IOException e) {
            out.println("Input error");
        }

        return account;
     }

     // Method to take a valid name for the Customer
     public static String takeValidCustomerNameInput(ArrayList<BankLoan> loansList, BufferedReader in, PrintWriter out){
         String customerName = null;
         try {
             w:while(true){
                 customerName = in.readLine();
                 // Go through the loans' list and check if the the customer exists
                 for (BankLoan bankLoan : loansList) {
                     if (bankLoan.getCustomer().getFirstName().equalsIgnoreCase(customerName)) {
                         break w;
                     }
                     else{
                         out.println("There are no loan requests belonging to this customer. Try again:");
                     }
                 }
             }
         } catch (Exception e) {
             out.println("Input error.");
         }
         return customerName;
     }

     // Method to take a valid decision for the loan
     public static String takeValidLoanDecisionInput(BufferedReader in, PrintWriter out){
         String decision = null;
         try {
             while (true) {
                 decision = in.readLine();
                 if (decision.equalsIgnoreCase("ACCEPT") || decision.equalsIgnoreCase("REJECT")) {
                     break;
                 }
                 else{
                     out.println("Unknown command. Try again:");
                 }
             }
         } catch (Exception e) {
             out.println("Input error.");
         }
         return decision;
     }
 // Method to take and validate the requested amount to deposit
    public static double takeValidDepositInput(double balance, BufferedReader in, PrintWriter out){
        double requestedAmount = 0;

        while (true) {
            try {
                String strRequestedAmount = in.readLine();
                requestedAmount = Double.parseDouble(strRequestedAmount);
                break;
                   
            } catch (Exception e) {
                out.println("Can't process non-numerical characters. Try again:");
            }
        }

        return requestedAmount;
    }
    
    public static String takeValidRegularInput(BufferedReader in, PrintWriter out) {
        String info = null;
        try {
            info = in.readLine();

        } catch (IOException e) {
            out.println("Input error");
        }

        return info;
    }
  

    // Helper method that iterates through the Customer accounts ArrayList and checks if a given account belongs to it
    private static boolean accountExists(String account, ArrayList<Account> accountsList){
        for (int i = 0; i < accountsList.size(); i++) {
            if (account.equalsIgnoreCase(accountsList.get(i).getAccountNumber())) {
                return true;
            }
        }
        return false;
    }

    // Helper method that iterates through the Customer accounts ArrayList, find the selected account and checks if the funds are sufficient
    private static boolean hasSufficientFunds(String account, ArrayList<Account> accountsList, double payBackAmount){
        for (int i = 0; i < accountsList.size(); i++) {
            if (account.equalsIgnoreCase(accountsList.get(i).getAccountNumber())) {
                if (accountsList.get(i).getPrimaryBalance().getBalance() >= payBackAmount) {
                    return true;
                }
            }
        }
        return false;
    }

    // Method Overload
    // public static String takeValidInput(String key, String oldPass)

    // Input validation method
    private static boolean isValid(String input, String regex){
        // Extract the regex patter
        Pattern p = Pattern.compile(regex);
        // Compare it to the given input
        Matcher m = p.matcher(input);

        // Return true if there's a match and false if there isn't
        return m.matches();
    }

}