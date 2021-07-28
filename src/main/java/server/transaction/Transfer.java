package server.transaction;

import server.account.Account;
import server.account.Currency;

import java.sql.Timestamp;

public class Transfer {

	private int transferID;
	private Timestamp timestamp;
	private Account senderAccount;
	private Account recipientAccount;
	private double amount;
	private Currency currency;

	// TODO: create the transfer class and store each account transfer in the database
}