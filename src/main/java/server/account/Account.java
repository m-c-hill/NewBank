package server.account;

import server.bank.Bank;
import server.database.DbUtils;
import server.database.GetObject;

/**
 * Class to represent a customer's bank account. A user may have multiple accounts, each with an assigned currency.
 */
public class Account {
	private final String accountNumber;
	private final String accountName;
	private final Bank bank;
	private final String statementSchedule = "monthly";
	private double balance;
	private final Currency currency;

	public Account(String accountName, double openingBalance, Currency currency) {
		this.accountNumber = "00000001";  // generateNewAccountNumber(); // TODO: find a way to generate unique account IDs to replace user input
		this.accountName = accountName;
		this.bank = GetObject.getBank(1); // Returns default bank - for now all new accounts are "NewBank" accounts
		this.balance = openingBalance;
		this.currency = currency;
	}

	public double getBalance(){
		return this.balance;
	}

	public Currency getCurrency(){
		return this.currency;
	}

	public String getAccountNumber(){
		return accountNumber;
	}

	public Bank getBank(){
		return this.bank;
	}

	public String getAccountName() {
		return accountName;
	}

	/**
	 * Method to return a basic account summary
	 * @return String containing the account number, balance and currency type
	 */
	public String toString() {
		return (this.accountNumber + ": " + getBalance() + " " + getCurrency().getName());
	}

	/**
	 * Method to withdraw a set amount from the user's account
	 * @param amount Amount to withdraw
	 */
	public void withdrawAmount(double amount){
		updateBalance(this.balance - amount);
	}

	/**
	 * Method to deposit a set amount into the user's account
	 * @param amount Amount to withdraw
	 */
	public void makeDeposit(double amount) {
		updateBalance(this.balance + amount);
	}

	/**
	 * Method to pay a loan back
	 * @param amount Loan amount due
	 */
	public void payBackLoan(double amount){
		updateBalance(this.balance - amount);
	}

	public void executeTransaction(){
		// TODO: create transactions and log them in the database
	}

	public void executeTransfer(){
		// TODO: create method and accompanying class to carry out transfers between accounts and log them in the database
	}

	public void updateBalance(double newBalance){
		this.balance = newBalance; // Update the object balance
		DbUtils.updateBalance(); // Update the balance in the database
	}

	private void generateNewAccountNumber(){
		// TODO: ASAP
	}

	public void closeAccount(){
		// TODO: create method to close account
	}

	public void updateStatementSchedule(){
		// TODO: method for user to choose frequency with which they receive transaction statements
	}

	public void sendStatement(){
		// TODO: method to send a summary of transactions for a given period
	}
}
