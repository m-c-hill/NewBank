package server.account;

import server.bank.Bank;
import server.database.DbUtils;
import server.database.GetObject;
import server.user.Customer;

import java.util.Random;

/**
 * Class to represent a customer's bank account. A user may have multiple accounts, each with an assigned currency.
 */
public class Account {
	private final String accountNumber;
	private final String accountName;
	private final Bank bank;
	private final String statementSchedule;
	private double balance;
	private final Currency currency;

	// Constructor for new bank accounts with an assigned random account number
	public Account(Customer customer, String accountName, double openingBalance, Currency currency) {
		this.accountNumber = generateNewAccountNumber(); // Random and unique 8-digit account number generated
		this.accountName = accountName;
		this.bank = GetObject.getBank(1); // Returns default bank - for now all new accounts are "NewBank" accounts
		this.statementSchedule = "monthly"; // Default to monthly schedule for now
		this.balance = openingBalance;
		this.currency = currency;

		DbUtils.storeAccount(customer,
				this.accountNumber,
				this.accountName,
				1,
				this.statementSchedule,
				this.balance,
				this.currency.getName());
	}

	// Separate constructor method for creating an object from data in the database
	public Account(String accountNumber, String accountName, int bankId, double openingBalance, String currencyId) {
		this.accountNumber = accountNumber;
		this.accountName = accountName;
		this.bank = GetObject.getBank(bankId);
		this.statementSchedule = "monthly"; // Default to monthly schedule for now
		this.balance = openingBalance;
		this.currency = GetObject.getCurrency(currencyId);
	}

	public double getBalance(){
		return this.balance;
	}

	public Currency getCurrency(){
		return this.currency;
	}

	public String getAccountNumber(){
		return this.accountNumber;
	}

	public Bank getBank(){
		return this.bank;
	}

	public String getAccountName() {
		return this.accountName;
	}

	public String getStatementSchedule(){
		return this.statementSchedule;
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

	/**
	 * Method to update the balance of the Account class and the corresponding balance stored in the database
	 * @param newBalance New account balance
	 */
	public void updateBalance(double newBalance){
		this.balance = newBalance; // Update the object balance
		DbUtils.updateBalance(this.accountNumber, newBalance); // Update the balance in the database
	}

	/**
	 * Method to generate a random new 8-digit account number
	 * Generates a random string of 8 digits and checks if the number exists in the database
	 * @return New unique account number
	 */
	public static String generateNewAccountNumber(){
		boolean accountNumberUnique = false;
		String newAccountNumber = "";
		Random r = new Random();

		// Continue to generate random 8-digit account numbers until account number found that does not exist in db
		while(!accountNumberUnique) {
			for (int i = 0; i < 8; i++) {
				int num = r.nextInt(10);
				newAccountNumber += num;
			}
			if(!DbUtils.checkAccountNumberExists(newAccountNumber)){
				// If account number does not exist, break from loop and return newAccountNumber
				accountNumberUnique = true;
			}
		}
		return newAccountNumber;
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
