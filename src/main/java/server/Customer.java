package server;

import java.util.ArrayList;

public class Customer extends User{
	
	private ArrayList<Account> accounts;

	// This constructor is overloaded, for now
	// Remove the basic constructor once we're done with the original test data
	public Customer(){
		accounts = new ArrayList<>();
	}
	
	// Constructor overload
	public Customer(String fName, String lName, String ssn, String dob, String pob, String email, String address) {
		super(fName, lName, ssn, dob, pob, email, address);
		accounts = new ArrayList<>();
	}

	public ArrayList<Account> getAccounts() {
		return accounts;
	}
	
	public String accountsToString() {
		String s = "";
		for(Account a : accounts) {
			s += a.toString() + "\n";
		}
		return s;
	}

	public void addAccount(Account account) {
		accounts.add(account);		
	}
}
