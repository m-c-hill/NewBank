package server;

import java.io.IOException;
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
	public synchronized String processRequest(CustomerID customer, String request) {
		if(customers.containsKey(customer.getKey())) {
			switch(request) {
			case "SHOWMYACCOUNTS" : return showMyAccounts(customer);
			case "WITHDRAW" : return withdrawAmount(customer);
			default : return "FAIL";
			}
		}
		return "FAIL";
	}

	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

	public String withdrawAmount(CustomerID customer){

		System.out.println("Please enter the name of the account you want to withdraw from:");	
		System.out.println(showMyAccounts(customer));
		
		String accountName = InputProcessor.takeValidInput(customers.get(customer.getKey()).getAccounts());

		for (int i = 0; i < customers.get(customer.getKey()).getAccounts().size(); i++) {
			if (customers.get(customer.getKey()).getAccounts().get(i).getAccountName().equals(accountName)) {
				
				System.out.println("Enter the amount you want to withdraw:");	
				double amount = InputProcessor.takeValidDoubleInput(customers.get(customer.getKey()).getAccounts().get(i).getOpeningBalance());
				customers.get(customer.getKey()).getAccounts().get(i).withdrawMoney(amount);
				
				break;
			}
		}	

		return "Process Ended.";
	}

	public static void main(String[] args) throws IOException {
		NewBank nb = new NewBank();

		System.out.println(nb.withdrawAmount(new CustomerID("Bhagy")));
	}

}
