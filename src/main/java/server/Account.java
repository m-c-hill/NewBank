package server;

import java.util.ArrayList;

public class Account {

	private String accountNumber;
	private final Bank bank = new Bank(1, "NewBank", "1 Bank Street", "010001");
	private ArrayList<Balance> balance = new ArrayList<Balance>();

	public Account(String accountNumber, double openingBalance) {
		this.accountNumber = accountNumber;
		this.

	}

	private Balance getPrimaryBalance(){
		// Method to find the primary balance for the account
		// Primary balance is the default balance through which transactions occur
		for (Balance b: this.balance){
			if(b.isPrimaryBalance()){
				return b;
			}
		}
		return null;
	}

	public String toString() {
		Balance primaryBalance = getPrimaryBalance();
		return (this.accountNumber + ": " + primaryBalance.getBalance() + primaryBalance.getCurrency().toString());
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

	public void addNewBalance(Currency currency){
		// TODO: create method to add a new balance to the account (ie. a new currency)
	}
}
