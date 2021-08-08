package server.bank;

import server.Email;
import server.Sms;
import server.account.Account;
import server.account.Currency;
import server.database.GetObject;
import server.support.InputProcessor;
import server.support.OutputProcessor;
import server.transaction.Transaction;
import server.user.Admin;
import server.user.Customer;
import server.user.Password;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Objects;

public class NewBank {

	private static final NewBank bank = new NewBank();
	// TODO: move loan limit to customer class
	private static final double LOAN_LIMIT = 2500;
	public static NewBank getBank() {
		return bank;
	}

	/**
	 * Method to process a customer's request
	 *
	 * @param customer Customer
	 * @param request  Request to process
	 * @param in       Input BufferedReader
	 * @param out      Output PrintWriter
	 * @return Response
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
			//TODO: combine all loans options to a new loans menu
			// "REMOVE ACCOUNT" command
			case "5":
				return removeAccount(customer, in, out);
			// "RLOAN" command	
			case "6":
				return requestLoan(customer, in, out);
			// "SHOWMYLOANSTATUS" command
			case "7":
				return showMyLoanStatus(customer, in, out);
			// "SHOWMYTRANSACTIONS" command
			case "8":
				return showMyTransactions(customer, in, out);
			// "PAYBACKLOAN" command
			case "9":
				return payBackLoan(customer, in, out);
			// "CREATE_ETHEREUM_WALLET" command
			case "10":
				return EthereumUtils.createEthereumWallet(customer, in, out);
			// "SHOW_ETHEREUM_WALLET" command
			case "11":
				return EthereumUtils.showEthereumWalletInfo(customer, in, out);
			// "TRANSFER_ETHER" command
			case "12":
				return EthereumUtils.transferEther(customer, in, out);
			// "EMAILMYTRANSACTIONS" command
			case "13":
				return emailRecentTransactions(customer, in, out);
			case "14":
				try {
					return resetPassword(customer, in, out);
				} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
					e.printStackTrace();
				}
			default:
				return "FAIL";
		}
	}

	/**
	 * Method to process an admin's request
	 *
	 * @param admin   Admin
	 * @param request Request to process
	 * @param in      Input
	 * @param out     Output
	 * @return Response
	 */
	public synchronized String processAdminRequest(Admin admin, String request, BufferedReader in, PrintWriter out) {
		switch (request) {
			case "1":
				return admin.showLoansList(out);
			case "2":
				return admin.handleLoanRequest(in, out);
			default:
				break;
		}
		return "Fail";
	}

	/**
	 * Method to allow customers to view their accounts
	 *
	 * @param customer Customer
	 * @return Response
	 */
	private String showMyAccounts(Customer customer) {
		ArrayList<Account> customerAccounts = customer.getAccounts();
		if (customerAccounts.isEmpty()) {
			return "There is no account found under this customer.";
		} else {
			return OutputProcessor.createAccountsTable(customerAccounts);
		}
	}

	/**
	 * Method to withdraw a set amount from the customer's account
	 *
	 * @param customer Customer
	 * @param in       Input
	 * @param out      Output
	 * @return Response
	 */
	public String withdrawAmount(Customer customer, BufferedReader in, PrintWriter out) {
		ArrayList<Account> customerAccounts = customer.getAccounts();

		if (customerAccounts.isEmpty()) {
			return "There are no accounts found for this customer.";
		} else {
			out.println("Please enter the account number of the account you wish to withdraw from"
					+ " (choose from the list below):"
					+ "\nPlease enter 'Exit' to go back to the main menu.");
			out.println(showMyAccounts(customer));  // Display accounts

			String accountNumber = InputProcessor.takeValidInput(customerAccounts, in, out);

			if (accountNumber.equalsIgnoreCase("EXIT")) {
				return "Exit request is taken, going back to the main menu.";
			} else {
				System.out.println(accountNumber);
				Account account = GetObject.getAccount(accountNumber);
				out.println("Enter the amount you want to withdraw: ");
				assert account != null;
				double amount = InputProcessor.takeValidDoubleInput(account.getBalance(), in, out);
				account.withdrawAmount(amount);
				customer.retrieveAccounts(); // Update accounts for customer instance to reflect database changes

				String notification = "Withdrawal successful. You've withdrawn: "
						+ amount + " " + account.getCurrency().getCurrencyId()
						+"\nRemaining balance: "
						+ account.getBalance() + " " + account.getCurrency().getCurrencyId();
				Sms.sendText(notification);
				
				out.println("An SMS notification is sent. Would you like to receive an email copy as well? Please enter YES or NO");
				String answer = InputProcessor.takeValidInput("letters", in, out);
				
				if(answer.equalsIgnoreCase("YES")){
					out.println("Please enter the email address you would like to receive the statement at: ");
					String email = InputProcessor.takeValidInput("valid email addresses", in, out);
					String emailBody = notification;
					String emailSubject = "Withdraw Succesful";
					Email.sendEmail(email, emailSubject, emailBody);
				}
				
				
				return notification;
			}
		}
	}

	/**
	 * Method to deposit a set amount into the customer's account
	 *
	 * @param customer Customer
	 * @param in       Input
	 * @param out      Output
	 * @return Response
	 */
	public String depositAmount(Customer customer, BufferedReader in, PrintWriter out) {
		ArrayList<Account> customerAccounts = customer.getAccounts();

		if (customerAccounts.isEmpty()) {
			return "There is no account found under this customer name.";
		} else {
			out.println("Please enter the name of the account you want to make a deposit to"
					+ "(choose from the list below):" + "\nPlease type EXIT to go back to the main menu.");
			out.println(showMyAccounts(customer)); // Display customer accounts

			String accountNumber = InputProcessor.takeValidInput(customerAccounts, in, out);

			if (accountNumber.equalsIgnoreCase("EXIT")) {
				return "Exit request is taken, going back to the main menu.";
			} else {
				Account account = GetObject.getAccount(accountNumber);
				out.println("Enter the amount you want to deposit: ");
				double amount = InputProcessor.takeValidDepositInput(in, out);
				assert account != null;
				account.makeDeposit(amount);
				customer.retrieveAccounts();

				String notification = "Deposit successful. You've made a deposit of "
						+ amount + " to " + accountNumber
						+ "\nUpdated balance: "
						+ account.getBalance();

				Sms.sendText(notification);
				
				out.println("An SMS notification is sent. Would you like to receive an email copy as well? Please enter YES or NO");
				String answer = InputProcessor.takeValidInput("letters", in, out);
				
				if(answer.equalsIgnoreCase("YES")){
					out.println("Please enter the email address you would like to receive the statement at: ");
					String email = InputProcessor.takeValidInput("valid email addresses", in, out);
					String emailBody = notification;
					String emailSubject = "Deposit Succesful";
					Email.sendEmail(email, emailSubject, emailBody);
				}

				return notification;
			}
		}
	}

	/**
	 * Method for a customer to open a new account
	 *
	 * @param customer Customer
	 * @param in       Input
	 * @param out      Output
	 * @return Response
	 */
	public String createAccount(Customer customer, BufferedReader in, PrintWriter out) {

		ArrayList<Account> customerAccounts = customer.getAccounts();

		out.println("Please enter a name for the account you want to create:"
				+ "\nPlease enter Exit to go back to the main menu.");

		String accountName = InputProcessor.createValidAccountName(customerAccounts, in, out);

		// If the user enters Exit go back to main menu message appears
		if (accountName.equalsIgnoreCase("Exit")) {
			return "Exit request is taken, going back to the main menu.";
		} else {
			double openingBalance = 0;
			Currency currency = GetObject.getCurrency("gbp");
			// TODO: for now, default currency is gbp. Need to allow user to choose
			// TODO: create a method in InputProcessor to check if the currency of choice is valid (ie. in the database)

			// Automatically saves new account to the database through constructor
			customer.addAccount(new Account(customer, accountName, openingBalance, currency));

			assert currency != null;
			String notification = "Process succeeded. You've opened the new account: " + "\n" + accountName + " : "
					+ Double.toString(openingBalance) + " " + currency.getCurrencyId();

			Sms.sendText(notification);
			
			out.println("An SMS notification is sent. Would you like to receive an email copy as well? Please enter YES or NO");
			String answer = InputProcessor.takeValidInput("letters", in, out);
			
			if(answer.equalsIgnoreCase("YES")){
				out.println("Please enter the email address you would like to receive the statement at: ");
				String email = InputProcessor.takeValidInput("valid email addresses", in, out);
				String emailBody = notification;
				String emailSubject = "New Account Created";
				Email.sendEmail(email, emailSubject, emailBody);
			}

			return notification;
		}
	}
	
	/**
	 * Method for a customer to remove an account once the balance is 0.00
	 * @param customer Customer
	 * @param in Input
	 * @param out Output
	 * @return Response
	 */
	public String removeAccount(Customer customer, BufferedReader in, PrintWriter out) {

		ArrayList<Account> customerAccounts = customer.getAccounts();

		out.println("Please enter the name of the account you want to remove" 
				+ " (choose from the list below):" + "\nPlease enter EXIT to go back to the main menu.");
		
		// Display Customer-related accounts as visual aid for providing a choice	
		out.println(showMyAccounts(customer));
					
		String accountNumber = InputProcessor.takeValidInput(customerAccounts, in, out);

		//If the user enters Exit go back to main menu message appears
		 if(accountNumber.equalsIgnoreCase("Exit")){
			return "Exit request is taken, going back to the main menu.";
			}
			
		 else {
			 for (Account account : customerAccounts) {
				 if (account.getAccountNumber().equals(accountNumber)) {
					 double currentBalance = account.getBalance();
					 if (currentBalance != 0) {
						 return "Account removal failed. The outstanding balance is not 0.00.";
					 } else {
						 customer.removeAccount(account);
						 String notification = "Process succeeded. The account "
								 + accountNumber + " is removed.";
						 Sms.sendText(notification);
						 
						 out.println("An SMS notification is sent. Would you like to receive an email copy as well? Please enter YES or NO");
							String answer = InputProcessor.takeValidInput("letters", in, out);
							
							if(answer.equalsIgnoreCase("YES")){
								out.println("Please enter the email address you would like to receive the statement at: ");
								String email = InputProcessor.takeValidInput("valid email addresses", in, out);
								String emailBody = notification;
								String emailSubject = "Account Removed";
								Email.sendEmail(email, emailSubject, emailBody);
							}

						 return notification;
					 }
				 }
			 }
				}
				return "The list of accounts is here: " + "\n" + showMyAccounts(customer);
		}
	
	/**
	 * Method to allow customers to request loans
	 *
	 * @param customer Customer
	 * @param in       Input
	 * @param out      Output
	 * @return Response
	 */
	private String requestLoan(Customer customer, BufferedReader in, PrintWriter out) {
		if (customer.isAllowedToRequestLoan()) {
			ArrayList<Account> customerAccounts = customer.getAccounts();

			if (customerAccounts.isEmpty()) {
				return "There is no account found for this customer.";
			} else {
				out.println("Please, enter the account number of the account you wish to request a loan for"
						+ " (choose from the list below):" + "\nPlease type EXIT to go back to the main menu:" + "\n"
						+ showMyAccounts(customer));
				String accountNumber = InputProcessor.takeValidInput(customerAccounts, in, out);

				if (accountNumber.equalsIgnoreCase("EXIT")) {
					return "Going back to the main menu";
				}

				Account account = GetObject.getAccount(accountNumber);

				out.println("Enter the amount you want to request: ");
				double amount = InputProcessor.takeValidLoanAmountInput(LOAN_LIMIT, in, out);
				out.println("Please provide a justification for requesting a loan: ");
				String loanReason = InputProcessor.takeValidRegularInput(in, out);
				assert account != null;
				BankLoan bankLoan = new BankLoan(customer, account, amount, loanReason);
				customer.setAllowedToRequestLoan(false);
				// TODO: add 'allowedToRequestLoan' variable to database schema
				// TODO: update allowedToRequestLoan in database once added

				String notification = "Your loan request has been submitted." +
						"\nYou will receive a confirmation SMS once your request is reviewed by the bank." +
						"\nYou can also check for the updates on the loan status from the menu";

				Sms.sendText(notification);
				
				out.println("An SMS notification is sent. Would you like to receive an email copy as well? Please enter YES or NO");
				String answer = InputProcessor.takeValidInput("letters", in, out);
				
				if(answer.equalsIgnoreCase("YES")){
					out.println("Please enter the email address you would like to receive the statement at: ");
					String email = InputProcessor.takeValidInput("valid email addresses", in, out);
					String emailBody = notification;
					String emailSubject = "Loan Request Submitted";
					Email.sendEmail(email, emailSubject, emailBody);
				}

				return notification;
			}
		}
		return "You are not eligible to request a new loan until you complete the payment for the first loan.";
	}

	/**
	 * Method to allow customers to check their loan status
	 *
	 * @param customer Customer
	 * @param in       Input
	 * @param out      Output
	 * @return Response
	 */
	private String showMyLoanStatus(Customer customer, BufferedReader in, PrintWriter out) {
		// Retrieves all loans currently associated with this customer
		ArrayList<BankLoan> loansList = GetObject.getCustomerLoanList(customer);
		assert loansList != null;
		if (!loansList.isEmpty())
			out.println("Please choose a loan by ID to view the status: ");
			out.println(OutputProcessor.createLoansTable(loansList));
			BankLoan bankLoan = InputProcessor.takeValidLoanID(loansList, in, out); // UPDATE

			String loanStatus = bankLoan.getApprovalStatus();

			switch(loanStatus){
				case "pending":
					return "Your loan request has not been checked yet.";
				case "approved":
					String notification = "Your loan request has been accepted.\n" +
							"The requested amount has been added to account: "
							+ bankLoan.getAccount().getAccountNumber() + ".";
					Sms.sendText(notification);
					
					return notification;
				case "declined":
					notification = "Your loan request has been rejected. You may request a new loan.";
					Sms.sendText(notification);
					
					return notification;
			}
		return "You have not submitted any loan requests.";
	}

	/**
	 * Method to allow customers to pay back their loans
	 * @param customer Customer
	 * @param in Input
	 * @param out Output
	 * @return Response
	 */
	private String payBackLoan(Customer customer, BufferedReader in, PrintWriter out) {
		// Retrieves all loans currently associated with this customer
		ArrayList<BankLoan> loansList = GetObject.getCustomerLoanList(customer);
		ArrayList<Account> accountList = customer.getAccounts();

		assert loansList != null;
		if (!loansList.isEmpty()){
			OutputProcessor.createLoansTable(loansList);
			out.println("Please enter the ID of the loan you would like to pay back: ");
			BankLoan bankLoan = InputProcessor.takeValidLoanID(loansList, in, out);

			if (bankLoan.getOutstandingPayments() == 0){
				return "Loan has already been paid off in full.";
			}

			// If the loan has been transferred to the user's account
			if (bankLoan.getTransferStatus()) {
				out.println("Choose the account you want to use to pay back the loan: ");
				out.println(OutputProcessor.createAccountsTable(accountList));
				Account account = InputProcessor.takeValidInput(accountList, bankLoan.getCurrency(), in, out);

				out.println("How much of the loan do you want to pay off?");
				double amount = InputProcessor.takeValidDoubleInput(in, out);

				// If the requested amount to pay off is greater that the outstanding payments due, then set equal to outstanding payment
				amount = Math.min(amount, bankLoan.getOutstandingPayments());

				bankLoan.payBackLoan(amount);
				assert account != null;
				account.payBackLoan(amount);

				String notification = "";
				if (bankLoan.getOutstandingPayments() == 0) {
					notification = "Loan was successfully paid back in full.";
				} else {
					notification = "You have successfully paid off " + amount + bankLoan.getCurrency().getCurrencyId() +
							"\nOutstanding payments remaining: " + bankLoan.getOutstandingPayments() + bankLoan.getCurrency().getCurrencyId();
				}
				Sms.sendText(notification);
				
				out.println("An SMS notification is sent. Would you like to receive an email copy as well? Please enter YES or NO");
				String answer = InputProcessor.takeValidInput("letters", in, out);
				
				if(answer.equalsIgnoreCase("YES")){
					out.println("Please enter the email address you would like to receive the statement at: ");
					String email = InputProcessor.takeValidInput("valid email addresses", in, out);
					String emailBody = notification;
					String emailSubject = "Loan Paid Back";
					Email.sendEmail(email, emailSubject, emailBody);
				}

				return notification;
			}
		}
		return "Your currently have no loans to pay back.";
	}

	/**
	 * Method to reset a customer's password upon request
	 *
	 * @param customer Customer
	 * @param in       Input
	 * @param out      Output
	 */
	private String resetPassword(Customer customer, BufferedReader in, PrintWriter out) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

		out.println("Please enter your current password: ");
		Password password = GetObject.getPassword(customer.getUserID()); // Retrieve password object
		assert password != null;
		boolean auth = password.authenticate(in.readLine()); // Ask user to enter their plain text password
		if (auth) {
			boolean passwordReset = false;
			while (!passwordReset) {
				out.println("Please enter a new password: ");
				String passwordAttempt1 = InputProcessor.takeValidInput("password", in, out);
				out.println("Please re-enter your password: ");
				String passwordAttempt2 = InputProcessor.takeValidInput("password", in, out);

				if (Objects.equals(passwordAttempt1, passwordAttempt2)) {
					password.resetPassword(passwordAttempt1);
					passwordReset = true;
				}
			}
		} else {
			return "The password you have entered is incorrect. Taking you back to the main menu.";
		}
		return "Password has been successfully reset.";
	}

	/**
	 * Method to display a customer's recent transactions for their chosen dates
	 * @param customer Customer
	 * @param in Input
	 * @param out Output
	 * @return Response (table of transactions if successful)
	 */
	private String showMyTransactions(Customer customer, BufferedReader in, PrintWriter out) {
		ArrayList<Account> customerAccounts = customer.getAccounts();

		if (customerAccounts.isEmpty()) {
			return "There is no account found under this customer name.";
		}
		else{
			out.println("Please enter the account number of the account you wish to see a statement for: ");
			out.println(showMyAccounts(customer));
			String accountNumber = InputProcessor.takeValidInput(customerAccounts, in, out);
			if (accountNumber.equalsIgnoreCase("EXIT")) {
				return "Exit request is taken, going back to the main menu.";
			} else{
				out.println("Loading...");
				Account account = GetObject.getAccount(accountNumber);
				assert account != null;
				ArrayList<Transaction> transactions = account.getRecentTransactions();
				if (transactions.isEmpty()){
					return "No recent transactions found for account " + accountNumber;
				}
				out.println("Displaying up to 10 most recent transactions for account: " + accountNumber);
				return OutputProcessor.createTransactionsTable(transactions);
			}
		}
	}
	
	/**
	 * Method to send an email listing the customer's recent transactions
	 * @param customer Customer
	 * @param in Input
	 * @param out Output
	 * @return Response (table of transactions if successful)
	 */
	private String emailRecentTransactions(Customer customer, BufferedReader in, PrintWriter out) {
		out.println("Please enter the email address you would like to receive the statement at: ");
		String email = InputProcessor.takeValidInput("valid email addresses", in, out);
		String emailBody = showMyTransactions(customer, in, out);
		String emailSubject = "Recent Transactions";
		Email.sendEmail(email, emailSubject, emailBody);
		return String.format("The email is sent to: " + email);
	}
	
}
