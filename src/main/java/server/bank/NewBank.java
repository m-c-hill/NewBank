package server.bank;

import server.Sms;
import server.account.Account;
import server.database.GetObject;
import server.support.InputProcessor;
import server.support.OutputProcessor;
import server.user.Admin;
import server.user.Customer;
import server.user.Password;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;


public class NewBank {

	private static final NewBank bank = new NewBank();
	// TODO: Move interest rate to the loans class and make the loan limit unique to each customer
	// Interest rate
	private static final double INTEREST_RATE = 2.78;
	// Loan credit limitadmin
	private static final double LOAN_LIMIT = 2500;

	public static NewBank getBank() {
		return bank;
	}

	/**
	 * commands from the NewBank customer are processed in this method
	 * @param customer Customer
	 * @param request Request to process
	 * @param in Input BufferedReader
	 * @param out Output PrintWriter
	 * @return Result of request
	 */
	public synchronized String processCustomerRequest(Customer customer, String request, BufferedReader in, PrintWriter out) {
		// TODO: add a reset password option
		switch (request) {
			case "1":
				return showMyAccounts(customer);
			// "WITHDRAW" command
			case "2":
				return withdrawAmount(customer, in, out);
			// "DEPOSIT" command
			case "3":
				return depositAmount(customer, in, out);
			// "CREATE ACCOUNT" command
			case "4":
				return createAccount(customer, in, out);
			// "RLOAN" command
			case "5":
				return requestLoan(customer, in, out);
			// "SHOWMYLOANSTATUS" command
			case "6":
				return showMyLoanStatus(customer, in, out);
			// "PAYBACKLOAN" command
			case "7":
				return payBackLoan(customer, in, out);
			case "8":
				return resetPassword(customer, in, out);
			default:
				return "FAIL";
		}
	}

	public synchronized String processAdminRequest(Admin admin, String request, BufferedReader in, PrintWriter out){
		// TODO: add a reset password option
		switch (request) {
			case "1":
				return admin.showLoansList(loansList, out);
			case "2":
				return admin.handleLoanRequest(loansList, customers, in, out);
			default:
				break;
		}
		return "Fail";
	}

	private String showMyAccounts(Customer customer) {
		ArrayList<Account> customerAccounts = customer.getAccounts();
		if (customerAccounts.isEmpty()) {
			return "There is no account found under this customer.";
		} else {

			return OutputProcessor.createsAccountsTable(customerAccounts);
		}
	}

	// Withdrawal Feature
	public String withdrawAmount(Customer customer, BufferedReader in, PrintWriter out){
		
		ArrayList<Account> customerAccounts = customer.getAccounts();
		
		if (customerAccounts.isEmpty()) {
			return "There is no account found for this customer.";
		} else {
			out.println("Please enter the name of the account you want to withdraw from" 
						+ " (choose from the list below):" + "\nPlease enter Exit to go back to the main menu.");
			// Display Customer-related accounts as visual aid for providing a choice	
			out.println(showMyAccounts(customer));
					
			// The provided account must exist within the accounts ArrayList
			String accountNumber = InputProcessor.takeValidInput(customerAccounts, in, out);

			//If the user enters Exit go back to main menu message appears
			if(accountNumber.equals("Exit")){
				return "Exit request is taken, going back to the main menu.";	
			}
			
			else {
				
				// These variables are for printing purposes
				double withdrawPrntAmount = 0;
				int accountPrntIndex = 0;
				
				for (int i = 0; i < customerAccounts.size(); i++) {
					if (customerAccounts.get(i).getAccountNumber().equals(accountNumber)) {
						// Processing withdrawal amount
						out.println("Enter the amount you want to withdraw:");	
						double amount = InputProcessor.takeValidDoubleInput(customerAccounts.get(i).getBalance(), in, out);
						// Calling the given account withdrawAmount() to perform deduction once it's been verified that the requested amount is a double and is less than or smaller than the available balance
						customerAccounts.get(i).withdrawAmount(amount);	
						
						// Values to be printed
						accountPrntIndex = i;
						withdrawPrntAmount = amount;
						
						break;
					}
				}	
		
				String notification = String.format("Process succeeded. You've withdrawn "
						 + withdrawPrntAmount 
						private NewBank() {
		customers = new HashMap<>();
		addCustomerTestData();

		admins = new HashMap<>();
		addAdminTestData();

		loansList = new ArrayList<>();
	}	 + "\nRemining balance: "
						 + customerAccounts.get(accountPrntIndex).getBalance());
				
				Sms.sendText(notification);
				
				return notification;
				
			}
				
		}
	}

	// Make Deposit Feature
	public String depositAmount(Customer customer, BufferedReader in, PrintWriter out){

		ArrayList<Account> customerAccounts = customer.getAccounts();
		if (customerAccounts.isEmpty()) {
			return String.format("There is no account found under this customer name.");
		}
		else {
			out.println("Please enter the name of the account you want to make a deposit to" 
					+ "(choose from the list below):" + "\nPlease type EXIT to go back to the main menu.");
			// Display Customer-related accounts as visual aid for providing a choice	
			out.println(showMyAccounts(customer));
				
			String accountNumber = InputProcessor.takeValidInput(customerAccounts, in, out);

			//If the user enters Exit go back to main menu message appears
			if(accountNumber.equals("EXIT")){
				return "Exit request is taken, going back to the main menu.";	
			}
			
			else {
				// These variables are for printing purposes
				double depositPrntAmount = 0;
				int accountPrntIndex = 0;
					
				for (int i = 0; i < customerAccounts.size(); i++) {
					if (customerAccounts.get(i).getAccountNumber().equals(accountNumber)) {
						// Processing deposit amount
						out.println("Enter the amount you want to deposit:");	
						double amount = InputProcessor.takeValidDepositInput(customerAccounts.get(i).getBalance(), in, out);
						// Calling the given account makeDeposit()
						customerAccounts.get(i).makeDeposit(amount);	
						// Values to be printed
						accountPrntIndex = i;
						depositPrntAmount = amount;
							break;
						}
					}

				String notification = String.format("Process succeeded. You've made a deposit of "
						 +  depositPrntAmount + " to " + accountNumber
						 + "\nUpdated balance: " 
						 + customerAccounts.get(accountPrntIndex).getBalance());

				Sms.sendText(notification);
				
				return notification;
				
				}
		}
	}

	// Creating New Account Feature
	public String createAccount(Customer customer, BufferedReader in, PrintWriter out) {

		ArrayList<Account> customerAccounts = customer.getAccounts();

		out.println("Please enter a name for the account you want to create:"
				+ "\nPlease enter Exit to go back to the main menu.");

		String accountName = InputProcessor.createValidAccountName(customerAccounts, in, out);

		// If the user enters Exit go back to main menu message appears
		if (accountName.equals("Exit")) {
			return "Exit request is taken, going back to the main menu.";
		}

		else {
			double openingBalance = 0;

			customer.addAccount(new Account(accountName, openingBalance));

			String notification = String.format("Process succeeded. You've opened the new account: " + "\n" + accountName + " : "
					+ Double.toString(openingBalance));
			
			Sms.sendText(notification);
			
			return notification;
		}
	}

	// Loan Request Feature
	private String requestLoan(Customer customer, BufferedReader in, PrintWriter out) {
		if (customer.isAllowedToRequestLoan()) {
			ArrayList<Account> customerAccounts = customer.getAccounts();

			if (customerAccounts.isEmpty()) {
				return "There is no account found for this customer.";
			} else {
				out.println("Please, enter the name of the account you wish to add the loan to"
						+ " (choose from the list below):" + "\nPlease type EXIT to go back to the main menu:" + "\n"
						+ showMyAccounts(customer));
				String accountNumber = InputProcessor.takeValidInput(customerAccounts, in, out);

				if (accountNumber.equalsIgnoreCase("EXIT")) {
					return "Going back to the main menu";
				} else {
					for (int i = 0; i < customerAccounts.size(); i++) {
						if (customerAccounts.get(i).getAccountNumber().equals(accountNumber)) {
							Account customerAccount = customerAccounts.get(i);

							out.println("Enter the amount you want to request:");
							double amount = InputProcessor.takeValidLoanAmountInput(LOAN_LIMIT, in, out);

							out.println("Please provide a justification for requesting a loan:");
							String jStatement = InputProcessor.takeValidRegularInput(in, out);

							BankLoan bankLoan = new BankLoan(customer, customerAccount, jStatement, amount, INTEREST_RATE);
							this.loansList.add(bankLoan);

							customer.setAllowedToRequestLoan(false);

							String notification  = String.format("Your loan request has been submitted." 
									+ "\nYou will receive a confirmation SMS once your request is reviewed by the bank."
									+  "\nYou can also check for the updates on the loan status from the menu");
							
							Sms.sendText (notification);

							return notification;
						}
					}
					return "Interrupted.";
				}

			}
		}
		else{
			return "You are not eligible to request a new loan until you complete the payment for the first loan.";
		}
	}

	// Method to check my loan status
	private String showMyLoanStatus(Customer customer, BufferedReader in, PrintWriter out) {
		for (BankLoan bankLoan : loansList) {
			if (bankLoan.getCustomer().getFirstName().equals(customer.getFirstName())) {
				if (!bankLoan.isChecked()) {
					return "Your loan request has not been checked yet.";
				} else if (bankLoan.isChecked() && bankLoan.isAccepted()) {
					
					String notification = String.format("Your loan request has been accepted." + "\nThe requested amount has been added to your "
							+ bankLoan.getAccount().getAccountNumber() + " account.");
					
					Sms.sendText (notification);
					
					return notification;
					
				} else if (bankLoan.isChecked() && !bankLoan.isAccepted()) {
					
					String notification =  "Your loan request has been rejected. You may request a new loan.";
					
					Sms.sendText (notification);
					
					return notification;	
				}
			}
		}
		return "You have not submitted any loan requests.";
	}

	// Method to pay back loan
	private String payBackLoan(Customer customer, BufferedReader in, PrintWriter out){
		ArrayList<Account> customerAccounts = customer.getAccounts();
		
		for (BankLoan bankLoan : loansList) {
			if(bankLoan.getCustomer().getFirstName().equals(customer.getFirstName()) && bankLoan.isAccepted()){
				out.println("Which account would you like to use in order to pay back the loan?" + "\n" + showMyAccounts(customer));
				String accountName = InputProcessor.takeValidInput(customerAccounts, bankLoan.getPayBackAmount(), in, out);

				for (int i = 0; i < customerAccounts.size(); i++) {
					if (customerAccounts.get(i).getAccountNumber().equalsIgnoreCase(accountName)) {
						customerAccounts.get(i).payBackLoan(bankLoan.getPayBackAmount());
						customer.setAllowedToRequestLoan(true);
						bankLoan.setPaidBack(true);
						
						String notification = "Loan was successfully paid back.";
						
						Sms.sendText(notification);
						
						return notification;
					}
				}
			}
		}
		return "You have not submitted any loan requests.";
	}

	/**
	 * User can reset their password
	 */
	private void ResetPassword(Customer customer, BufferedReader in, PrintWriter out) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

		out.println("Please enter your current password: ");
		Password password = GetObject.getPassword(customer.getUserID()); // Retrieve password object
		boolean auth = password.authenticate(in.readLine()); // Ask user to enter their plain text password
		if(auth){
			Password.
		}
	}
}
