package server;

public class Bank {
	private int bankID;
	private String name;
	private String address; // Change this to address object once database has been correctly connected
	private String sortCode;

	public Bank(int bankID, String name, String address, String sortCode) {
		this.bankID = bankID;
		this.name = name;
		this.address = address;
		this.sortCode = sortCode;
	}

	// TODO: add in method to store new bank details in database

}
