package server;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class NewBank {
	
	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;
	
	private NewBank() {
		customers = new HashMap<>();
		addTestData();
	}

	private void addTestData() {
		Customer bhagy = new Customer();
		bhagy.addAccount(new Account("Main", 1000.0));
		customers.put("Bhagy", bhagy);
		
		Customer christina = new Customer();
		christina.addAccount(new Account("Savings", 1500.0));
		customers.put("Christina", christina);
		
		Customer john = new Customer();
		john.addAccount(new Account("Checking", 250.0));
		customers.put("John", john);
	}
	
	public static NewBank getBank() {
		return bank;
	}
	
	public synchronized CustomerID checkLogInDetails(String userName, String password) {
		if(customers.containsKey(userName)) {
			return new CustomerID(userName);
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
			default : return "FAIL";
			}
		}
		return "FAIL";
	}

	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

	// Withdrawal Feature
	public String withdrawAmount(CustomerID customer, BufferedReader in, PrintWriter out){

		out.println("Please enter the name of the account you want to withdraw from (choose from the list below):");
		// Display Customer-related accounts as visual aid for providing a choice	
		out.println(showMyAccounts(customer));
		
		ArrayList<Account> customerAccounts = customers.get(customer.getKey()).getAccounts();

		// The provided account must exist within the accounts ArrayList
		String accountName = InputProcessor.takeValidInput(customerAccounts, in, out);

		// These variables are for printing purposes
		double withdrawPrntAmount = 0;
		int accountPrntIndex = 0;
		
		for (int i = 0; i < customerAccounts.size(); i++) {
			// Ignoring the account name case
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
