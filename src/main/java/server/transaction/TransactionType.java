package server.transaction;

import java.util.Locale;

public class TransactionType {
	private String name;
	private String description;

	public static int getTransactionTypeId(String name){
		switch(name.toLowerCase()){
			case "withdraw":
				return 1;
			case "deposit":
				return 2;
			case "transfer":
				return 3;
			case "payment":
				return 4;
			default:
				return -1;
		}
	}
}
