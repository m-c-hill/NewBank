package server.bank;

public class Bank {
	private String name;
	private Address address;
	private String sortCode;

	public Bank(String name, Address address, String sortCode) {
		this.name = name;
		this.address = address;
		this.sortCode = sortCode;
	}

	// TODO: add in method to store new bank details in database

}
