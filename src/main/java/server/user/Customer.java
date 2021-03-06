package server.user;

import server.database.DbUtils;
import server.account.Account;
import server.bank.Address;
import server.database.GetObject;


import java.util.ArrayList;
import java.util.Date;

/**
 * Class to represent a customer of the bank
 */
public class Customer extends User {

	private ArrayList<Account> accounts;
	private boolean allowedToRequestLoan;

	public Customer(int userID, String prefix, String fName, String lName, String nationalInsuranceNumber,
					Date dateOfBirth, String emailAddress, String phoneNumber, Address address) {

		super(userID, prefix, fName, lName, nationalInsuranceNumber, dateOfBirth, emailAddress, phoneNumber, address);
		this.allowedToRequestLoan = true;
		retrieveAccounts();
		// TODO: add allowedToRequestLoan field to the database
		// TODO: add a customer loan limit field and add to the database
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
		for(Account account: accounts) {
			accountList += account.toString() + "\n";
		}
		return accountList;
	}

	/**
	 * Method to retrieve an array of Account objects (accounts belonging to a user) from the database
	 */
	public void retrieveAccounts() {
		this.accounts = GetObject.getAccounts(this.getUserID());
	}

	public void addAccount(Account account) {
		accounts.add(account);
		// TODO: save new account details to the database
	}
	
	public void removeAccount(Account account) {
		accounts.remove(account);
		int accountNum = Integer.parseInt(account.getAccountNumber());
		DbUtils.removeAccount(accountNum);
	}

}
