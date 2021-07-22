package server;

public class Currency {
	private String currencyID;
	private double usdExchangeRate;
	private String dtUpdated;
	private boolean crypto;

	public Currency(String currencyID, double usdExchangeRate, String dtUpdated, boolean crypto) {
		this.currencyID = currencyID; // ie. usd, eur, btc
		this.usdExchangeRate = usdExchangeRate;
		this.dtUpdated = dtUpdated;
		this.crypto = crypto;

		storeCurrency();
	}

	public double convertCurrency(int currencyID, double amount){
		// Method to convert into another currency (check if exchange rate is up to date using below methods)
		return 0.00;
	}

	private void getExchangeRate(String currencyID){
		// Method to get the current exchange rate from the database
	}

	private void checkExchangeRate(String currencyID){
		// If dtUpdated < today's date, update the exchange rate using updateExchangeRate()
	}

	private void updateExchangeRate(String currencyID){
		// Method to update the value of usdExchangeRate in database if changed (find API to do this?)
	}

	private void storeCurrency(){
		// Method to add new currency to the database
	}
}
