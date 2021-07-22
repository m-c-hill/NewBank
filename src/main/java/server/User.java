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

    // This empty constructor will remain here to avoid getting a compilation error in Customer class
    // Remove after deleting the original test data
    public User(){

    }

    // Constructor overload
    public User(int userID, String prefix, String fName, String lName, String nationalInsuranceNumber,
                String dateOfBirth, String emailAddress, String phoneNumber, Address address, Password password){
        this.userID = userID;
        this.prefix = prefix;
        this.firstName = fName;
        this.lastName = lName;
        this.nationalInsuranceNumber = nationalInsuranceNumber;
        this.dateOfBirth = dateOfBirth;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.password = password;
    }

    // We're exposing the first name because we want to use it as a key in the customers<String, Customer> HashMap
    public String getFirstName() {
        return firstName;
    }

    private void storeUserDetails(){
        // Method to store user details in the database
    }
    
}
