package server;

import java.util.ArrayList;

public class Customer extends User{
	
	private ArrayList<Account> accounts;

	// This constructor is overloaded, for now
	// Remove the basic constructor once we're done with the original test data
	public Customer(){
		super();
		accounts = new ArrayList<>();
	}

	public Customer(String prefix, String fName, String lName, String nationalInsuranceNumber,
					String dateOfBirth, String emailAddress, String phoneNumber, Address address,
					Password password) {
		super(prefix, fName, lName, nationalInsuranceNumber, dateOfBirth, emailAddress, phoneNumber, address, password);
		this.accounts = new ArrayList<>();
	}

	public ArrayList<Account> getAccounts() {
		return accounts;
	}
	
	public String accountsToString() {
		String accountList = "";
		for(Account a : accounts) {
			accountList += a.toString() + "\n";
		}
		return accountList;
	}

	public void addAccount(Account account) {
		accounts.add(account);		
	}
}
