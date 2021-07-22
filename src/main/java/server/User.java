package server;

public abstract class User {

    private int userID;
    private String prefix;
    private String firstName;
    private String lastName;
    private String nationalInsuranceNumber;
    private String dateOfBirth;
    private Address address;
    private String emailAddress;
    private String phoneNumber;
    private Password password;

    // Constructor overload
    public User(int userID, String prefix, String firstName, String lastName, String nationalInsuranceNumber,
                String dateOfBirth, String emailAddress, String phoneNumber, Address address, Password password){
        this.userID = userID; //TODO: find a way to autoincrement the userID and generate a new ID based on latest primary key in database
        this.prefix = prefix;
        this.firstName = firstName;
        this.lastName = lastName;
        this.nationalInsuranceNumber = nationalInsuranceNumber;
        this.dateOfBirth = dateOfBirth;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.password = password;
    }

    public User() {
    }

    // We're exposing the first name because we want to use it as a key in the customers<String, Customer> HashMap
    public String getFirstName() {
        return firstName;
    }

    private void storeUserDetails(){
        // Method to store user details in the database
    }
    
}
