package server;

public class Balance {
	int balanceID;
	String accountNumber;
	Currency currency;
	double amount;
	boolean primaryBalance;

	public Balance(String accountNumber, Currency currency, double amount, boolean primaryBalance) {
		this.balanceID = 1; // TODO: find a way to autoincrement the balanceID based on the database
		this.accountNumber = accountNumber;
		this.currency = currency;
		this.amount = amount;
		this.primaryBalance = primaryBalance;
		storeBalance();
	}

	public double getBalance(){
		return this.amount;
	}

	public String getCurrency(){
		return this.currency.toString();
	}

	public void updateBalance(double newBalance){
		this.amount = newBalance;
		// TODO: update the corresponding balance in the database
		// Method to update the balance amount in the database after a transaction has occurred
	}

	public boolean isPrimaryBalance(){
		// Returns true if this balance is to be used as the primary balance for transactions / transfers
		return this.primaryBalance;
	}

	private void storeBalance(){
		// TODO: Write method to store newly created balance in the database
	}

	// TODO: find a way to deal with transactions between accounts of different currencies (conversion)
}
