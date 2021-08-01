package server.user;

import server.account.Account;
import server.bank.Address;
import server.database.GetObject;

import java.util.ArrayList;

public class Customer extends User {

	private ArrayList<Account> accounts;
	private boolean allowedToRequestLoan;

	public Customer(){
		super();
		this.accounts = new ArrayList<>();
		this.allowedToRequestLoan = true;
	}

	public Customer(int userID, String prefix, String fName, String lName, String nationalInsuranceNumber,
					String dateOfBirth, String emailAddress, String phoneNumber, Address address) {

		super(userID, prefix, fName, lName, nationalInsuranceNumber, dateOfBirth, emailAddress, phoneNumber, address);
		retrieveAccounts();
		this.allowedToRequestLoan = true;
	}

	public ArrayList<Account> getAccounts() {
		return accounts;
	}

	public boolean isAllowedToRequestLoan() {
		return allowedToRequestLoan;
	}

	public void setAllowedToRequestLoan(boolean allowedToRequestLoan) {
		this.allowedToRequestLoan = allowedToRequestLoan;
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
		// TODO: save new account details to the database
	}

	private void retrieveAccounts() {
		this.accounts = GetObject.getAccounts(this.getUserID());
	}
}
