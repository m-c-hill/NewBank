package server;

import java.sql.Timestamp;

public class Transfer {

	int transferID;
	Timestamp timestamp;
	Account senderAccount;
	Account recipientAccount;
	double amount;
	Currency currency;

	// TODO: create the transfer class and store each account transfer in the database
}