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

	public Address(String addressNumber, String addressLine1, String addressLine2, String city, String region,
				   String postcode, String country) {
		this.addressID = 1; // TODO: find a way to generate next primary key
		this.addressNumber = addressNumber;
		this.addressLine1 = addressLine1;
		this.addressLine2 = addressLine2;
		this.city = city;
		this.region = region;
		this.postcode = postcode;
		this.country = country;

		storeAddress();
	}

	public int getAddressID() {
		return addressID;
	}

	public void setAddressID(int addressID) {
		this.addressID = addressID;
	}

	public String getAddressNumber() {
		return addressNumber;
	}

	public void setAddressNumber(String addressNumber) {
		this.addressNumber = addressNumber;
	}

	public String getAddressLine1() {
		return addressLine1;
	}

	public void setAddressLine1(String addressLine1) {
		this.addressLine1 = addressLine1;
	}

	public String getAddressLine2() {
		return addressLine2;
	}

	public void setAddressLine2(String addressLine2) {
		this.addressLine2 = addressLine2;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public void displayAddress(){
		if(addressLine2 != null){
			System.out.println("Address: " + addressNumber + " " + addressLine1 + "\n" + addressLine2 + "\n" +
					city + "\n" + region + "\n" + postcode + "\n" + country);
		}
		else{
			System.out.println("Address: " + addressNumber + " " + addressLine1 + "\n" +
					city + "\n" + region + "\n" + postcode + "\n" + country);
		}
	}

	private void storeAddress(){
		// Method to store newly created address in database
	}
}
