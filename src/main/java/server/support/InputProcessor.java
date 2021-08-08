package server.support;

import server.account.Account;
import server.account.Currency;
import server.bank.BankLoan;
import server.database.GetObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// This is a utility class to help with taking and validating user input.
// 
// You can call InputProcessor(String key) to take and validate an input value that matches the provided the key and then assigning the return value to a variable
// For example, InputProcessor("valid dates") will look through the map to get the regex defining the date format  
public class InputProcessor {

    // Mapping each key to its value (regex)
    private static final Map<String, String> InfoRegexMap = Map.of(
        "letters", "^[A-Za-z ]+$",
        "numbers", "^[0-9 ]+$",
        "letters and numbers", "^[A-Za-z0-9 ]+$",
        "valid email addresses", "^(.+)@(.+)[\\.]{1}(\\D+)$",
        "valid dates", "^(0[1-9]|[12][0-9]|3[01])[-/. ]?([0][1-9]|[1][012])[-/. ]?(19|20)\\d\\d$",
        "valid phone numbers", "^0[\\d]{7,12}$",
        "valid postcodes/zipcodes", "^[a-zA-Z0-9 ]{3,10}$",
        "four digit codes", "^[0-9]{4}$"
        );
            "letters", "^[A-Za-z ]+$",
            "numbers", "^[0-9 ]+$",
            "letters and numbers", "^[A-Za-z0-9 ]+$",
            "valid email addresses", "^(.+)@(.+)[\\.]{1}(\\D+)$",
            "valid dates", "^(0[1-9]|[12][0-9]|3[01])[-/. ]?([0][1-9]|[1][012])[-/. ]?(19|20)\\d\\d$",
            "valid phone numbers", "^0[\\d]{7,12}$",
            "valid postcodes/zipcodes", "^[a-zA-Z0-9 ]{3,10}$",
            "password", "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );

    /**
     * Method to take an input from the user and validate it
     *
     * @param key Regex key
     * @param in  Input
     * @param out Output
     * @return User input
     */
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
                    out.println("Invalid entry. This field accepts " + key + " only. \nTry again:");
                }
            }
        } catch (IOException e) {
            out.println("Input error");
        }
        return info;
    }

    public static Date takeValidDate(BufferedReader in, PrintWriter out) {
        try {
            String stringDate = in.readLine();
            // convert to Date object
            return dateFromString(stringDate);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    // Method overload for capturing the correct account name

    /**
     * Method to take an account number input from the user
     *
     * @param accountsList List of accounts belonging to a customer
     * @param in           Input
     * @param out          Output
     * @return Account number
     */
    public static String takeValidInput(ArrayList<Account> accountsList, BufferedReader in, PrintWriter out) {
        String account = null;
        try {
            while (true) {
                account = in.readLine();

                //if Exit is entered take it as a valid input to go back to main menu
                if (account.equalsIgnoreCase("EXIT")) {
                    break;
                }

                // Break if the provided account number matches one of the accounts belonging to the Customer
                else if (accountExists(account, accountsList)) {
                    break;
                } else {
                    // Display a message when no match is found
                    out.println("Please enter a valid account number. \nTry again: ");
                }
            }
        } catch (IOException e) {
            out.println("Input error");
        }
        out.println("Account selected: " + account);
        return account;
    }

    /**
     * Method to take an account number input from the user, account must have same currency as loan to be paid back
     *
     * @param accountsList List of accounts belonging to a customer
     * @param currency     Currecny of loan to pay back
     * @param in           Input
     * @param out          Output
     * @return Account number
     */
    public static Account takeValidInput(ArrayList<Account> accountsList, Currency currency, BufferedReader in, PrintWriter out) {
        String accountNumber = null;
        try {
            while (true) {
                accountNumber = in.readLine();
                if (accountExists(accountNumber, accountsList)) {
                    Account account = GetObject.getAccount(accountNumber);
                    assert account != null;
                    if (!Objects.equals(account.getCurrency().getCurrencyId(), currency.getCurrencyId())) {
                        out.println("Please choose an account with the same currency as your loan payment. \nTry again: ");
                    }
                    break;
                } else {
                    out.println("Please enter a valid account number. \nTry again: ");
                }
            }
        } catch (IOException e) {
            out.println("Input error");
        }
        out.println("Account selected: " + accountNumber);
        return null;
    }

    // Helper method that iterates through the Customer accounts ArrayList and checks if a given account belongs to it
    private static boolean accountExists(String accountNumber, ArrayList<Account> accountsList) {
        for (Account account : accountsList) {
            if (accountNumber.equalsIgnoreCase(account.getAccountNumber())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method to take the account number with sufficient funds to pay back the loan
     *
     * @param accountsList    List of accounts belonging to a customer
     * @param amountToPayBack Amount to payback
     * @param in              Input
     * @param out             Output
     * @return Account number
     */
    public static String takeValidInput(ArrayList<Account> accountsList, double amountToPayBack, BufferedReader in, PrintWriter out) {
        String accountNumber = null;
        try {
            while (true) {
                accountNumber = in.readLine();
                if (accountExists(accountNumber, accountsList)) {
                    if (hasSufficientFunds(accountNumber, accountsList, amountToPayBack)) {
                        break;
                    } else {
                        out.println("Insufficient funds. Choose a different account or make a new deposit: ");
                    }
                } else {
                    out.println("Account name does not exist. Try again:");
                }
            }
        } catch (Exception e) {
            out.println("Input error.");
        }
        return accountNumber;
    }

    /**
     * Method to take and validate the requested amount to withdraw
     *
     * @param balance Balance of the customer's account
     * @param in      Input
     * @param out     Output
     * @return Requested amount to withdraw
     */
    public static double takeValidDoubleInput(double balance, BufferedReader in, PrintWriter out) {
        double requestedAmount = 0;

        while (true) {
            try {
                String strRequestedAmount = in.readLine();
                requestedAmount = Double.parseDouble(strRequestedAmount);

                if (requestedAmount <= balance) {
                    break;
                } else {
                    out.println("You currently have insufficient funds to withdraw " + requestedAmount +
                            "\nYour current balance is " + balance +
                            "\nPlease try again: ");
                }
            } catch (Exception e) {
                out.println("Cannot process non-numerical characters. Try again: ");
            }
        }
        return requestedAmount;
    }

    /**
     * Method to validate a loan amount input from the user
     *
     * @param loanLimit Customer's loan limit
     * @param in        Input
     * @param out       Output
     * @return Loan request amount
     */
    public static double takeValidLoanAmountInput(double loanLimit, BufferedReader in, PrintWriter out) {
        double requestedAmount = 0;

        while (true) {
            try {
                String strRequestedAmount = in.readLine();
                requestedAmount = Double.parseDouble(strRequestedAmount);

                if (requestedAmount <= loanLimit) {
                    break;
                } else {
                    out.println("The requested amount should be less than or equal to the set loan limit. Try again:");
                }
            } catch (Exception e) {
                out.println("Can't process non-numerical characters. Try again:");
            }
        }
        return requestedAmount;
    }

    /**
     * Method to take and validate a new account name when creating a new Account
     *
     * @param accountsList Customer's accounts
     * @param in           Input
     * @param out          Output
     * @return Account name
     */
    public static String createValidAccountName(ArrayList<Account> accountsList, BufferedReader in, PrintWriter out) {
        String account = null;
        try {
            while (true) {
                account = in.readLine();
                //if Exit is entered take it as a valid input to go back to main menu
                if (account.equalsIgnoreCase("EXIT")) {
                    break;
                }

                // Check if the provided account name matches one of the accounts belonging to the Customer
                // If the name is not used before then accept it
                if (!accountExists(account, accountsList)) {
                    break;
                } else {
                    // Display a message that the name is already used
                    out.println("This name exists. Please enter a new valid account name. ");
                }
            }
        } catch (IOException e) {
            out.println("Input error");
        }
        return account;
    }

    /**
     * Method to take a valid name for the Customer
     *
     * @param loansList List of loans
     * @param in        Input
     * @param out       Output
     * @return Valid customer name
     */
    public static String takeValidCustomerNameInput(ArrayList<BankLoan> loansList, BufferedReader in, PrintWriter out) {
        String customerName = null;
        try {
            w:
            while (true) {
                customerName = in.readLine();
                // Go through the loans list and check if the the customer exists
                for (BankLoan bankLoan : loansList) {
                    if (bankLoan.getCustomer().getFirstName().equalsIgnoreCase(customerName)) {
                        break w;
                    } else {
                        out.println("There are no loan request belonging to this customer. Try again: ");
                    }
                }
            }
        } catch (Exception e) {
            out.println("Input error.");
        }
        return customerName;
    }

    /**
     * Method to validate a loan ID from the user and return a Bank Loan from a list of loans
     */
    public static BankLoan takeValidLoanID(ArrayList<BankLoan> loansList, BufferedReader in, PrintWriter out) {
        while (true) {
            int loanId = takeValidIntegerInput(in, out);
            for (BankLoan loan : loansList) {
                if (loanId == loan.getLoanId()) {
                    out.println("Loan ID: " + loanId + " selected.");
                    return loan;
                }
            }
            out.println("Loan ID is not valid - please enter a valid loan ID. ");
        }
    }

    /**
     * Method to take a valid decision for the loan
     *
     * @param in  Input
     * @param out Output
     * @return Validated loan decision
     */
    public static String takeValidLoanDecisionInput(BufferedReader in, PrintWriter out) {
        String decision = null;
        try {
            while (true) {
                decision = in.readLine();
                if (decision.equalsIgnoreCase("ACCEPT") || decision.equalsIgnoreCase("REJECT")) {
                    break;
                } else {
                    out.println("Unknown command. Try again: ");
                }
            }
        } catch (Exception e) {
            out.println("Input error.");
        }
        return decision;
    }

    /**
     * Method to validate a deposit input
     *
     * @param in  Input
     * @param out Output
     * @return Validated amount to deposit
     */
    public static double takeValidDepositInput(BufferedReader in, PrintWriter out) {
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

    // Helper method that iterates through the Customer accounts ArrayList, find the selected account and checks if the funds are sufficient
    private static boolean hasSufficientFunds(String account, ArrayList<Account> accountsList, double payBackAmount) {
        for (Account value : accountsList) {
            if (account.equalsIgnoreCase(value.getAccountNumber())) {
                if (value.getBalance() >= payBackAmount) {
                    return true;
                }
            }
        }
        return false;
    }

    // Input validation method
    private static boolean isValid(String input, String regex) {
        // Extract the regex patter
        Pattern p = Pattern.compile(regex);
        // Compare it to the given input
        Matcher m = p.matcher(input);

        // Return true if there's a match and false if there isn't
        return m.matches();
    }

    //2FA input
    public static String takeCodeInput(String key, BufferedReader in, PrintWriter out) {
        String inputCode = null;

        try {
            while (true) {
                inputCode = in.readLine();
                // Check if the provided input is valid by comparing its format with the
                // relevant regex
                // Upon matching, break and return the given input
                if (isValid(inputCode, InfoRegexMap.get(key))) {
                    break;
                } else {
                    // Display a message if the input is invalid. Loop again to take a new input
                    out.println("Invalid entry. This field accepts " + key + " only. \nTry again:");
                    continue;
                }
            }

        } catch (IOException e) {
            out.println("Input error");
        }
        return inputCode;
    }

    public static double takeValidDoubleInput(BufferedReader in, PrintWriter out) {
        double amount = 0;

        while (true) {
            try {
                String request = in.readLine();
                amount = Double.parseDouble(request);
                break;
            } catch (Exception e) {
                out.println("Cannot process non-numerical characters. Please enter a valid amount: ");
            }
        }
        return amount;
    }

    public static int takeValidIntegerInput(BufferedReader in, PrintWriter out) {
        int value = 0;

        while (true) {
            try {
                String request = in.readLine();
                value = Integer.parseInt(request);
                break;
            } catch (Exception e) {
                out.println("Cannot process non-integer value. Please enter a valid integer: ");
            }
        }
        return value;
    }

    public static boolean doPasswordsMatch(String password1, String password2) {
        return password1.equals(password2);
    }

    public static Date dateFromString(String stringDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
        try {
            return sdf.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String takeValidEthereumAddress(BufferedReader in, PrintWriter out) throws IOException {
        String recipientAddress;
        out.println("Please enter the address you would like to send Ether to");

        Pattern validEthereumAddress = Pattern.compile("(^0x[0-9a-fA-F]{40}$)");
        Matcher m;

        do {
            recipientAddress = in.readLine();
            m = validEthereumAddress.matcher(recipientAddress);

            if (!m.matches()) {
                out.println("Not a valid Ethereum Address, please re-enter");
            }
        } while (!m.matches());
        return recipientAddress;
    }
}
