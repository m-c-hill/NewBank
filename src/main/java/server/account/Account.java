package server.account;

import server.bank.Bank;
import server.database.GetObject;
import server.user.Customer;

import java.util.ArrayList;

public class Account {

	private final int customerId;
	private final String accountNumber;
	private final String accountName;
	private final Bank bank;
	private final String statementSchedule = "monthly";
	private double balance;
	private Currency currency;

	public Account(Customer customer, String accountName, double openingBalance, Currency currency) {
		this.customerId = customer.getCustomerId();
		this.accountNumber = "00000001";  // generateNewAccountNumber(); // TODO: find a way to generate unique account IDs to replace user input
		this.accountName = accountName;
		this.bank = GetObject.getBank(1); // Returns default bank
		this.balance = openingBalance;
		this.currency = currency;
	}

	public String toString() {
		Balance primaryBalance = getPrimaryBalance();
		return (this.accountNumber + ": " + primaryBalance.getBalance() + primaryBalance.getCurrency().toString());
	}

	public String getAccountName() {
		return accountName;
	}

	public double getBalance(){

	}

	public void withdrawAmount(double amount){
		double currentBalance = getPrimaryBalance().getBalance();
		updatePrimaryBalance(currentBalance - amount);
	}

	public void makeDeposit(double amount) {
		double currentBalance = getPrimaryBalance().getBalance();
		updatePrimaryBalance(currentBalance + amount);
	}

	public void payBackLoan(double amount){
		double currentBalance = getPrimaryBalance().getBalance();
		updatePrimaryBalance(currentBalance - amount);
	}

	public void executeTransaction(){
		// TODO: create transactions and log them in the database
	}

	public void executeTransfer(){
		// TODO: create method and accompanying class to carry out transfers between accounts and log them in the database
	}

	private void updatePrimaryBalance(double newBalance){
		// Method to update the primary balance for an account
		Balance primaryBalance = getPrimaryBalance();
		primaryBalance.updateBalance(newBalance);
	}

	public void closeAccount(){
		// TODO: create method to close account
	}

	public void addNewBalance(Currency currency, double openingBalance){
		// TODO: create method to add a new balance to the account (ie. a new currency)
	}

	public void updateStatementSchedule(){
		// TODO: method for user to choose frequency with which they receive transaction statements
	}

	public void sendStatement(){
		// TODO: method to send a summary of transactions for a given period
	}

	public String getAccountNumber(){
		return accountNumber;
	}

	public double getBalance(){
		return this.amount;
	}

	public String getCurrency(){
		return this.currency.toString();
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void updateBalance(double newBalance){
		this.amount = newBalance;
		// TODO: update the corresponding balance in the database
		// Method to update the balance amount in the database after a transaction has occurred
	}

	private void generateNewAccountNumber(){

	}

}
