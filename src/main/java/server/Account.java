package server;

public class Account {

	private String accountNumber;

	private Bank bank;

	private String accountName;
	private double openingBalance;

	public Account(String accountName, double openingBalance) {
		this.accountName = accountName;
		this.openingBalance = openingBalance;
	}
	
	public String getAccountName() {
		return accountName;
	}

	public double getOpeningBalance() {
		return openingBalance;
	}

	public String toString() {
		return (accountName + ": " + openingBalance);
	}

	public void withdrawAmount(double amount){
		this.openingBalance = this.openingBalance - amount;
	}

	public void makeDeposit(double amount) {
		double newBalance = this.openingBalance + amount;
		this.openingBalance = newBalance; 
	}
}
