package server;

import java.util.ArrayList;

public class Customer extends User{
	
	private ArrayList<Account> accounts;

	public Customer(){
		accounts = new ArrayList<>();
	}
	
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
