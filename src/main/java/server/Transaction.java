package server;

import java.sql.Timestamp;

public class Transaction {

	int transactionID;
	TransactionType transactionType;
	Timestamp timestamp;
	String payee;
	String accountNumber;
	double amount;
	Currency currency;

	// TODO: create the transaction class and store each account transaction in the database
}
