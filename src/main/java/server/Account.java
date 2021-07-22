package server;

import org.joda.time.DateTime;

import java.util.ArrayList;

public class Account {

	private String accountNumber;
	private final Bank bank = new Bank(1, "NewBank", "1 Bank Street", "010001");
	private ArrayList<Balance> balance = new ArrayList<Balance>();
	private String statementSchedule = "monthly"; // User may change frequency of statements to weekly, monthly

	public Account(String accountNumber, double openingBalance) {
		this.accountNumber = accountNumber; // TODO: find a way to generate unique account IDs to replace user input

		// Create the primary balance, default balance is gbp, but could be changed in the future so user can choose
		Balance primaryBalance = new Balance(accountNumber = this.accountNumber,
				new Currency(), openingBalance, true);
		this.balance.add(primaryBalance);
	}

	public String toString() {
		Balance primaryBalance = getPrimaryBalance();
		return (this.accountNumber + ": " + primaryBalance.getBalance() + primaryBalance.getCurrency().toString());
	}

	public Balance getPrimaryBalance(){
		// Method to find the primary balance for the account
		// Primary balance is the default balance through which transactions occur
		for (Balance b: this.balance){
			if(b.isPrimaryBalance()){
				return b;
			}
		}
		return null;
	}

	public void withdrawAmount(double amount){
		double currentBalance = getPrimaryBalance().getBalance();
		updateBalance(currentBalance - amount);
	}

	public void makeDeposit(double amount) {
		double currentBalance = getPrimaryBalance().getBalance();
		updateBalance(currentBalance - amount);
	}

	public void executeTransaction(){
		// TODO: create transactions and log them in the database
	}

	public void executeTransfer(){
		// TODO: create method and accompanying class to carry out transfers between accounts and log them in the database
	}

	private void updateBalance(double newBalance){
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

}
