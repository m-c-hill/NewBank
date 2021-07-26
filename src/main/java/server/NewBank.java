package server;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class NewBank {

	private static final NewBank bank = new NewBank();
	private HashMap<String, Customer> customers;
	// Admins HashMap
	private HashMap<String, Admin> admins;
	// loansList ArrayList
	private ArrayList<BankLoan> loansList;
	// Interest rate
	private static final double interestRate = 2.78;
	// Loan credit limit
	private static final double loanLimit = 2500;

	private NewBank() {
		customers = new HashMap<>();
		addCustomerTestData();
		
		admins = new HashMap<>();
		addAdminTestData();

		loansList = new ArrayList<>();
	}

	// Exposing the functionality of adding a new customer to the HashMap
	public void addCustomer(Customer c) {
		this.customers.put(c.getFirstName(), c);
	}

	private void addCustomerTestData() {
		Customer bhagy = new Customer();
		bhagy.addAccount(new Account("Main", 1000.0));
		customers.put("Bhagy", bhagy);

		Customer christina = new Customer();
		christina.addAccount(new Account("Savings", 1500.0));
		customers.put("Christina", christina);

		Customer john = new Customer();
		john.addAccount(new Account("Checking", 250.0));
		customers.put("John", john);

		// new test client with no account yet
		Customer alex = new Customer();
		customers.put("Alex", alex);

		// Test Customer 01 using the overloaded constructor
		// This is to test the loan request as the first names is tied to the Customer object in the HashMap
		Customer kylie = new Customer(150, "Ms.", "Kylie", "Johnson", "32561351", "18/04/1982", "kylie@gmail.com", "04444444444", 
				new Address(
					"200", 
					"Some Street", 
					"Some Other Street", 
					"Tokyo", 
					"Fuji", 
					"1000004", "Japan")); 
		kylie.addAccount(new Account("Saving", 4500.0));
		customers.put(kylie.getFirstName(), kylie);

		// Test Customer 02 using the overloaded constructor
		Customer daniel = new Customer(150, "Mr.", "Daniel", "Green", "32561351", "18/04/1982", "daniel@gmail.com", "04444444444", 
				new Address(
					"200", 
					"Some Street", 
					"Some Other Street", 
					"Bavaria", 
					"Munich", 
					"80803", "Germany")); 
		daniel.addAccount(new Account("Checking", 2700.0));
		daniel.addAccount(new Account("Main", 800));
		customers.put(daniel.getFirstName(), daniel);
	}

	// Admin Test Data
	public void addAdminTestData() {
		Admin michael = new Admin(100, "GM", "Michael", "Corielli", "22446688", "20/07/1980", "bruce@gmail.com", "01234567891",
				new Address(
					"107", 
					"Some Street", 
					"Some Other Street", 
					"Seattle", 
					"King County", 
					"98103", 
					"USA"),
				500,
				new AdminRole(
					"Manager", 
					"Has all the administartive privileges.", 
					true, true, true, true, true, true)
					);
		admins.put(michael.getFirstName(), michael);

		Admin grant = new Admin(100, "GM", "Grant", "Stevenson", "22446688", "20/07/1980", "grant@gmail.com", "01234567891",
				new Address(
					"107", 
					"Some Street", 
					"Some Other Street", 
					"Cheshire", 
					"North West Engalnd", 
					"CH11AA", 
					"UK"),
				500,
				new AdminRole(
					"Manager", 
					"Can only view info.", 
					true, true, false, false, false, true)
					);
		admins.put(grant.getFirstName(), grant);
	}

	public static NewBank getBank() {
		return bank;
	}

	public synchronized CustomerID checkCustomerLogInDetails(String userName, String password) {
		if (customers.containsKey(userName)) {
			return new CustomerID(userName);
		}
		return null;
	}

	public synchronized String checkAdminLogInDetails(String userName, String password) {
		if (admins.containsKey(userName)) {
			return userName;
		}
		return null;
	}

	// commands from the NewBank customer are processed in this method
	public synchronized String processCustomerRequest(CustomerID customer, String request, BufferedReader in, PrintWriter out) {
		if (customers.containsKey(customer.getKey())) {
			switch (request) {
				case "1":
					return showMyAccounts(customer);
				// Added "WITHDRAW" command
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
				default:
					return "FAIL";
			}
		}
		return "FAIL";
	}

	public synchronized String processAdminRequest(String admin, String request, BufferedReader in, PrintWriter out){
		
		if (admins.containsKey(admin)) {
			switch (request) {
				case "1":
					return admins.get(admin).showLoansList(this.loansList, out);
				case "2":
					return admins.get(admin).handleLoanRequest(loansList, customers, in, out);
				default:
					break;
			}
		}
		return "Fail";
	}

	private String showMyAccounts(CustomerID customer) {
		ArrayList<Account> customerAccounts = customers.get(customer.getKey()).getAccounts();
		if (customerAccounts.isEmpty()) {

			return String.format("There is no account found under this customer.");
		} else {

			return OutputProcessor.createsAccountsTable(customerAccounts);
		}
	}

	// Withdrawal Feature
	public String withdrawAmount(CustomerID customer, BufferedReader in, PrintWriter out){
		
		ArrayList<Account> customerAccounts = customers.get(customer.getKey()).getAccounts();
		
		if (customerAccounts.isEmpty()) {
			
			return String.format("There is no account found for this customer.");
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
						double amount = InputProcessor.takeValidDoubleInput(customerAccounts.get(i).getPrimaryBalance().getBalance(), in, out);
						// Calling the given account withdrawAmount() to perform deduction once it's been verified that the requested amount is a double and is less than or smaller than the available balance
						customerAccounts.get(i).withdrawAmount(amount);	
						
						// Values to be printed
						accountPrntIndex = i;
						withdrawPrntAmount = amount;
						
						break;
					}
				}	
		
				return String.format("Process succeeded. You've withdrawn "
				 + withdrawPrntAmount 
				 + "\nRemining balance: " 
				 + customerAccounts.get(accountPrntIndex).getPrimaryBalance().getBalance());
			}
				
		}
	}

	// Make Deposit Feature
	public String depositAmount(CustomerID customer, BufferedReader in, PrintWriter out){
			
		ArrayList<Account> customerAccounts = customers.get(customer.getKey()).getAccounts();
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
						double amount = InputProcessor.takeValidDepositInput(customerAccounts.get(i).getPrimaryBalance().getBalance(), in, out);
						// Calling the given account makeDeposit()
						customerAccounts.get(i).makeDeposit(amount);	
						// Values to be printed
						accountPrntIndex = i;
						depositPrntAmount = amount;
							
							break;
						}
					}	

					return String.format("Process succeeded. You've made a deposit of "
					 +  depositPrntAmount + " to " + accountNumber
					 + "\nUpdated balance: " 
					 + customerAccounts.get(accountPrntIndex).getPrimaryBalance().getBalance());
				}
		}
	}

	// Creating New Account Feature
	public String createAccount(CustomerID customer, BufferedReader in, PrintWriter out) {

		ArrayList<Account> customerAccounts = customers.get(customer.getKey()).getAccounts();

		out.println("Please enter a name for the account you want to create:"
				+ "\nPlease enter Exit to go back to the main menu.");

		String accountName = InputProcessor.createValidAccountName(customerAccounts, in, out);

		// If the user enters Exit go back to main menu message appears
		if (accountName.equals("Exit")) {
			return "Exit request is taken, going back to the main menu.";
		}

		else {
			double openingBalance = 0;

			customers.get(customer.getKey()).addAccount(new Account(accountName, openingBalance));

			return String.format("Process succeeded. You've opened the new account: " + "\n" + accountName + " : "
					+ Double.toString(openingBalance));

		}
	}

	// Loan Request Feature
	private String requestLoan(CustomerID customer, BufferedReader in, PrintWriter out) {
		if (customers.get(customer.getKey()).isAllowedToRequestLoan()) {
			ArrayList<Account> customerAccounts = customers.get(customer.getKey()).getAccounts();

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
							double amount = InputProcessor.takeValidLoanAmountInput(loanLimit, in, out);

							out.println("Please provide a justification for requesting a loan:");
							String jStatement = InputProcessor.takeValidRegularInput(in, out);

							BankLoan bankLoan = new BankLoan(customers.get(customer.getKey()), customerAccount, jStatement, amount, interestRate);
							this.loansList.add(bankLoan);

							customers.get(customer.getKey()).setAllowedToRequestLoan(false);

							return String.format("Your loan request has been submitted."
									+ "\nPlease remember to check for updates on the loan status from the menu");
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
	private String showMyLoanStatus(CustomerID customer, BufferedReader in, PrintWriter out) {
		for (BankLoan bankLoan : loansList) {
			if (bankLoan.getCustomer().getFirstName().equals(customer.getKey())) {
				if (!bankLoan.isChecked()) {
					return "Your loan request has not been checked yet.";
				} else if (bankLoan.isChecked() && bankLoan.isAccepted()) {
					return String.format("Your loan request has been accepted." + "\nThe requested amount has been added to your "
								+ bankLoan.getAccount().getAccountNumber() + " account.");
				} else if (bankLoan.isChecked() && !bankLoan.isAccepted()) {
					return "Your loan request has been rejected. You may request a new loan.";
				}
			}
		}

		return "You have not submitted any loan requests.";
	}

	// Method to pay back loan
	private String payBackLoan(CustomerID customer, BufferedReader in, PrintWriter out){
		ArrayList<Account> customerAccounts = customers.get(customer.getKey()).getAccounts();
		
		for (BankLoan bankLoan : loansList) {
			if(bankLoan.getCustomer().getFirstName().equals(customer.getKey()) && bankLoan.isAccepted()){				
				out.println("Which account would you like to use in order to pay back the loan?" + "\n" + showMyAccounts(customer));
				String accountName = InputProcessor.takeValidInput(customerAccounts, bankLoan.getPayBackAmount(), in, out);

				for (int i = 0; i < customerAccounts.size(); i++) {
					if (customerAccounts.get(i).getAccountNumber().equalsIgnoreCase(accountName)) {
						customerAccounts.get(i).payBackLoan(bankLoan.getPayBackAmount());
						customers.get(customer.getKey()).setAllowedToRequestLoan(true);
						bankLoan.setPaidBack(true);
						return "Loan was successfully paid back.";
					}
				}
			}
		}
		return "You have not submitted any loan requests.";
	}

}
