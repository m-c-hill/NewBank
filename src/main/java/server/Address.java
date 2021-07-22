package server;

public class Address {
	private int addressID;
	private String addressNumber; // String variable since address number could contain letters (ie. flat 2B)
	private String addressLine1;
	private String addressLine2 = null; // Default null as not all UK addresses will have a second line
	private String city;
	private String region;
	private String postcode;
	private String country;

	public Address(int addressID,
				   String addressNumber,
				   String addressLine1,
				   String addressLine2,
				   String city,
				   String region,
				   String postcode,
				   String country) {
		this.addressID = addressID;
		this.addressNumber = addressNumber;
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		this.city = city;
		this.region = region;
		this.postcode = postcode;
		this.country = country;
	}

	public void displayAddress(){
		if(addressLine2 != null){
			System.out.println("Address: " + addressNumber + " " + addressLine1 + "\n" + addressLine2 + "\n" +
					city + "\n" + region + "\n" + postcode + "\n" + country);
		}
		else{
			System.out.println("Address: " + addressNumber + " " + addressLine1 + "\n");
		}
	}
}
