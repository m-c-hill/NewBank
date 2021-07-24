package server;

import java.sql.Timestamp;

public class Transaction {

	private int transactionID;
	private TransactionType transactionType;
	private Timestamp timestamp;
	private String payee;
	private String accountNumber;
	private double amount;
	private Currency currency;

	// TODO: create the transaction class and store each account transaction in the database
}
