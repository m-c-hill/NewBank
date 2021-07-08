package server;

public class Account {
	
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

	public void withdrawMoney(double amount){
		this.openingBalance = this.openingBalance - amount;
	}
}
