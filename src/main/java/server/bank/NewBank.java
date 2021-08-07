package server.bank;

import server.Sms;
import server.account.Account;
import server.account.Currency;
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
import java.util.Objects;

public class NewBank {

	private static final NewBank bank = new NewBank();
	// TODO: Move interest rate to the loans class and make the loan limit unique to each customer
	private static final double INTEREST_RATE = 2.78;
	private static final double LOAN_LIMIT = 2500;
	ArrayList<BankLoan> loansList = new ArrayList<BankLoan>();
	HashMap<String, Customer> customers = new HashMap<String, Customer>();

	public static NewBank getBank() {
		return bank;
	}

	/**
	 * Method to process a customer's request
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
			// "REMOVE ACCOUNT" command
			case "5":
				return removeAccount(customer, in, out);
			// "RLOAN" command
			case "6":
				return requestLoan(customer, in, out);
			// "SHOWMYLOANSTATUS" command
			case "7":
				return showMyLoanStatus(customer, in, out);
			// "PAYBACKLOAN" command
			case "8":
				return payBackLoan(customer, in, out);
			case "9":
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
	 * @param admin Admin
	 * @param request Request to process
	 * @param in Input
	 * @param out Output
	 * @return Response
	 */
	public synchronized String processAdminRequest(Admin admin, String request, BufferedReader in, PrintWriter out) {
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

	/**
	 * Method to allow customers to view their accounts
	 * @param customer Customer
	 * @return Response
	 */
	private String showMyAccounts(Customer customer) {
		ArrayList<Account> customerAccounts = customer.getAccounts();
		if (customerAccounts.isEmpty()) {
			return "There is no account found under this customer.";
		} else {
			return OutputProcessor.createsAccountsTable(customerAccounts);
		}
	}

	/**
	 * Method to withdraw a set amount from the customer's account
	 * @param customer Customer
	 * @param in Input
	 * @param out Output
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
						+ amount + " " + account.getCurrency().getName()
						+"\nRemaining balance: "
						+ account.getBalance() + " " + account.getCurrency().getName();

				Sms.sendText(notification);
				return notification;
			}
		}
	}

	/**
	 * Method to deposit a set amount into the customer's account
	 * @param customer Customer
	 * @param in Input
	 * @param out Output
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

				return notification;
			}
		}
	}

	/**
	 * Method for a customer to open a new account
	 * @param customer Customer
	 * @param in Input
	 * @param out Output
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
					+ Double.toString(openingBalance) + " " + currency.getName();

			Sms.sendText(notification);

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
				for (int i = 0; i < customerAccounts.size(); i++) {
					if (customerAccounts.get(i).getAccountNumber().equals(accountNumber)) {
						Account customerAccount = customerAccounts.get(i);
						Double currentBalance = customerAccount.getBalance();
						if(currentBalance!=0) {
							return "Account removal failed. The outstanding balance is not 0.00.";
							}
						else if (currentBalance == 0)  {
							customer.removeAccount(customerAccounts.get(i));
							String notification = String.format("Process succeeded. The account " 
															+ accountNumber + " is removed.");
							Sms.sendText(notification);
							return notification;
							}
						}			
					}			
				}
				return String.format("The list of accounts is here: "+ "\n"+showMyAccounts(customer));
		}
	
	/**
	 * Method to allow customers to request loans
	 * @param customer Customer
	 * @param in Input
	 * @param out Output
	 * @return Response
	 */
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

							String notification = String.format("Your loan request has been submitted."
									+ "\nYou will receive a confirmation SMS once your request is reviewed by the bank."
									+ "\nYou can also check for the updates on the loan status from the menu");

							Sms.sendText(notification);

							return notification;
						}
					}
					return "Interrupted.";
				}

			}
		} else {
			return "You are not eligible to request a new loan until you complete the payment for the first loan.";
		}
	}

	/**
	 * Method to allow customers to check their loan status
	 * @param customer Customer
	 * @param in Input
	 * @param out Output
	 * @return Response
	 */
	private String showMyLoanStatus(Customer customer, BufferedReader in, PrintWriter out) {
		for (BankLoan bankLoan : loansList) {
			if (bankLoan.getCustomer().getFirstName().equals(customer.getFirstName())) {
				if (!bankLoan.isChecked()) {
					return "Your loan request has not been checked yet.";
				} else if (bankLoan.isChecked() && bankLoan.isAccepted()) {

					String notification = String.format("Your loan request has been accepted." + "\nThe requested amount has been added to your "
							+ bankLoan.getAccount().getAccountNumber() + " account.");

					Sms.sendText(notification);

					return notification;

				} else if (bankLoan.isChecked() && !bankLoan.isAccepted()) {

					String notification = "Your loan request has been rejected. You may request a new loan.";

					Sms.sendText(notification);

					return notification;
				}
			}
		}
		return "You have not submitted any loan requests.";
	}

	/**
	 * Method to allow customers to pay back their loans
	 * @param customer
	 * @param in
	 * @param out
	 * @return
	 */
	private String payBackLoan(Customer customer, BufferedReader in, PrintWriter out) {
		ArrayList<Account> customerAccounts = customer.getAccounts();

		for (BankLoan bankLoan : loansList) {
			if (bankLoan.getCustomer().getFirstName().equals(customer.getFirstName()) && bankLoan.isAccepted()) {
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
	 * Method to reset a customer's password upon request
	 * @param customer Customer
	 * @param in Input
	 * @param out Output
	 */
	private String resetPassword(Customer customer, BufferedReader in, PrintWriter out) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {

		out.println("Please enter your current password: ");
		Password password = GetObject.getPassword(customer.getUserID()); // Retrieve password object
		assert password != null; // TODO: remove this once added to input processor
		boolean auth = password.authenticate(in.readLine()); // Ask user to enter their plain text password
		if (auth) {
			boolean passwordReset = false;
			while (!passwordReset) {
				// TODO: authenticate the plain text password with input processor (10+ characters, 1 symbol, 1 upper)
				out.println("Please enter a new password: ");
				String passwordAttempt1 = in.readLine();
				out.println("Please re-enter your password: ");
				String passwordAttempt2 = in.readLine();

				if (Objects.equals(passwordAttempt1, passwordAttempt2)) {
					password.resetPassword(passwordAttempt1);
					passwordReset = true;
				}
			}
		}
		else{
			return "The password you have entered is incorrect. Taking you back to the main menu.";
		}
		return "Password has been successfully reset.";
	}
}
