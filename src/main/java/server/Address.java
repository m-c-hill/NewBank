package server;

public class Address {
	private int addressID;
	private String addressLine1;
	private String addressLine2 = null; // Default null as not all UK addresses will have a second line
	private String city;
	private String region;
	private String postcode;
	private String country;

	public Address(String addressLine1, String addressLine2, String city, String region,
				   String postcode, String country) {
		this.addressID = 1; // TODO: find a way to generate next primary key
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		this.city = city;
		this.region = region;
		this.postcode = postcode;
		this.country = country;

		storeAddress();
	}

	public void displayAddress(){
		if(addressLine2 != null){
			System.out.println("Address: " + addressLine1 + "\n" + addressLine2 + "\n" +
					city + "\n" + region + "\n" + postcode + "\n" + country);
		}
		else{
			System.out.println("Address: " + addressLine1 + "\n" +
					city + "\n" + region + "\n" + postcode + "\n" + country);
		}
	}

	private void storeAddress(){
		// Method to store newly created address in database
	}
}
