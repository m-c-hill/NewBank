package server;

public class Balance {
	int balanceID;
	String accountNumber;
	Currency currency;
	double amount;
	boolean primaryBalance;

	public Balance(int balanceID, String accountNumber, Currency currency, double amount, boolean primaryBalance) {
		this.balanceID = balanceID;
		this.accountNumber = accountNumber;
		this.currency = currency;
		this.amount = amount;
		this.primaryBalance = primaryBalance;

		storeBalance();
	}

	private void storeBalance(){
		// Method to store newly created balance in the database
	}

	public void updateBalance(String balanceID, double newBalance){
		// Method to update the balance amount in the database after a transaction has occurred
	}
}
