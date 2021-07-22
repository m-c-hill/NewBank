package server;

public class Bank {
	private int bankID;
	private String name;
	private Address address;
	private String sortCode;

	public Bank(int bankID, String name, Address address, String sortCode) {
		this.bankID = bankID;
		this.name = name;
		this.address = address;
		this.sortCode = sortCode;
	}

	// TODO: add in method to store new bank details in database

}
