package server;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;
	// Admins HashMap
	private HashMap<String, Admin> admins;
	private ArrayList<BankLoan> loansList = new ArrayList<BankLoan>();
	
	private NewBank() {
		customers = new HashMap<>();
		addCustomerTestData();
		addAdminTestData();
	}

	// Exposing the functionality of adding a new customer to the HashMap
	public void addCustomer(Customer c){
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
		
		//new test client with no account yet
		Customer alex = new Customer();
		customers.put("Alex", alex);
	}

	// Admin Test Data
	public void addAdminTestData(){
		Admin bruce = new Admin("Bruce", "Wayne", "22446688", "20/07/1988", "USA", "bruce@gmail.com", "01234567891", "Seattle", 
		new AdminRoles(true, true));
		admins.put(bruce.getFirstName(), bruce);	
	}
	
	public static NewBank getBank() {
		return bank;
	}
	
	public synchronized CustomerID checkCustomerLogInDetails(String userName, String password) {
		if(customers.containsKey(userName)) {
			return new CustomerID(userName);
		}
		return null;
	}

	public synchronized String checkAdminLogInDetails(String userName, String password){
		if (admins.containsKey(userName)) {
			return userName;
		}
		return null;
	}

	// commands from the NewBank customer are processed in this method
	public synchronized String processRequest(CustomerID customer, String request, BufferedReader in, PrintWriter out) {
		if(customers.containsKey(customer.getKey())) {
			switch(request) {
			case "1" : return showMyAccounts(customer);
			// Added "WITHDRAW" command
			case "2" : return withdrawAmount(customer, in, out);
			//"DEPOSIT" command
			case "3" : return depositAmount(customer, in, out);
			//"CREATE ACCOUNT" command
			case "4" : return createAccount(customer, in, out);
			// "RLOAN" command
			case "5" : return requestLoan(customer, in, out);
			default : return "FAIL";
			}
		}
		return "FAIL";
	}

	private String requestLoan(CustomerID customer, BufferedReader in, PrintWriter out){
		return "Success";
	}

	private String showMyAccounts(CustomerID customer) {
		ArrayList<Account> customerAccounts = customers.get(customer.getKey()).getAccounts();
		if (customerAccounts.isEmpty()) {
			
			return String.format("There is no account found under this customer.");
		}
		else {
		
			return (customers.get(customer.getKey())).accountsToString();
		}
	}

	// Loan Request Feature


	// Withdrawal Feature
	public String withdrawAmount(CustomerID customer, BufferedReader in, PrintWriter out){
		
		ArrayList<Account> customerAccounts = customers.get(customer.getKey()).getAccounts();
		if (customerAccounts.isEmpty()) {
			
			return String.format("There is no account found under this customer.");
		}
		else {

			out.println("Please enter the name of the account you want to withdraw from" 
						+ " (choose from the list below):" + "\nPlease enter Exit to go back to the main menu.");
			// Display Customer-related accounts as visual aid for providing a choice	
			out.println(showMyAccounts(customer));
					
			// The provided account must exist within the accounts ArrayList
			String accountName = InputProcessor.takeValidInput(customerAccounts, in, out);

			//If the user enters Exit go back to main menu message appears
			if(accountName.equals("Exit")){
				return "Exit request is taken, going back to the main menu.";	
			}
			
			else {
				
				// These variables are for printing purposes
				double withdrawPrntAmount = 0;
				int accountPrntIndex = 0;
				
				for (int i = 0; i < customerAccounts.size(); i++) {
					if (customerAccounts.get(i).getAccountName().equals(accountName)) {
						// Processing withdrawal amount
						out.println("Enter the amount you want to withdraw:");	
						double amount = InputProcessor.takeValidDoubleInput(customerAccounts.get(i).getOpeningBalance(), in, out);
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
				 + customerAccounts.get(accountPrntIndex).getOpeningBalance());
			}
				
		}
			
		
	}
	// Make Deposit Feature
	public String depositAmount(CustomerID customer, BufferedReader in, PrintWriter out){
			
			ArrayList<Account> customerAccounts = customers.get(customer.getKey()).getAccounts();
			if (customerAccounts.isEmpty()) {
				
				return String.format("There is no account found under this customer.");
			}
			else {
				out.println("Please enter the name of the account you want to make a deposit to" 
						+ "(choose from the list below):" + "\nPlease enter Exit to go back to the main menu.");
				
				// Display Customer-related accounts as visual aid for providing a choice	
				out.println(showMyAccounts(customer));
					
				String accountName = InputProcessor.takeValidInput(customerAccounts, in, out);

				//If the user enters Exit go back to main menu message appears
				if(accountName.equals("Exit")){
					return "Exit request is taken, going back to the main menu.";	
				}
				
				else {
					// These variables are for printing purposes
					double depositPrntAmount = 0;
					int accountPrntIndex = 0;
						
					for (int i = 0; i < customerAccounts.size(); i++) {
						if (customerAccounts.get(i).getAccountName().equals(accountName)) {
							// Processing deposit amount
							out.println("Enter the amount you want to deposit:");	
							double amount = InputProcessor.takeValidDepositInput(customerAccounts.get(i).getOpeningBalance(), in, out);
							// Calling the given account makeDeposit()
							customerAccounts.get(i).makeDeposit(amount);	
							// Values to be printed
							accountPrntIndex = i;
							depositPrntAmount = amount;
								
								break;
							}
						}	
	
						return String.format("Process succeeded. You've made a deposit of "
						 +  depositPrntAmount + " to " + accountName 
						 + "\nUpdated balance: " 
						 + customerAccounts.get(accountPrntIndex).getOpeningBalance());
				
					}
			}
		}

	// Creating New Account Feature
	public String createAccount(CustomerID customer, BufferedReader in, PrintWriter out){
		
		ArrayList<Account> customerAccounts = customers.get(customer.getKey()).getAccounts();
				
		out.println("Please enter a name for the account you want to create:" 
				+ "\nPlease enter Exit to go back to the main menu.");
				
		String accountName = InputProcessor.createValidAccountName(customerAccounts, in, out);
		
		//If the user enters Exit go back to main menu message appears
		if(accountName.equals("Exit")){
			return "Exit request is taken, going back to the main menu.";
		}
		
		else {
			double openingBalance = 0;
					
			customers.get(customer.getKey()).addAccount(new Account(accountName, openingBalance));
					
			return String.format("Process succeeded. You've opened the new account: "
								+ "\n" + accountName + " : " 
								+ Double.toString(openingBalance));
					
			}
		}

	}
