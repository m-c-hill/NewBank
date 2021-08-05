package server.transaction;

import server.account.Account;
import server.account.Currency;
import server.database.DbUtils;
import server.database.GetObject;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;

public class Transaction {

	private final String transactionType;
	private final Timestamp timestamp;
	private final String payee;
	private final Account account;
	private final double amount;
	private final Currency currency;

	/**
	 * Constructs a transaction instance and stores it in the database
	 * @param transactionTypeName Transaction type name (ie. withdraw, payment - see transaction_type table)
	 * @param payee Payee
	 * @param account Account
	 * @param amount Transaction amount
	 * @param currency Transaction currency
	 */
	public Transaction(String transactionTypeName, String payee, Account account, double amount, Currency currency){
		this.transactionType = transactionTypeName;
		this.timestamp = Timestamp.from(Instant.now());
		this.payee = payee;
		this.account = account;
		this.amount = amount;
		this.currency = currency;

		int transactionTypeId = TransactionType.getTransactionTypeId(transactionType);
		DbUtils.storeTransaction(this.account, transactionTypeId, this.timestamp, this.payee, this.amount, this.currency);
	}

	public String getTransactionType() {
		return transactionType;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public String getPayee() {
		return payee;
	}

	public Account getAccount() {
		return account;
	}

	public double getAmount() {
		return amount;
	}

	public Currency getCurrency() {
		return currency;
	}
}
