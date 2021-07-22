package server;

public abstract class User {

    private String userID;
    private String prefix;
    private String firstName;
    private String lastName;
    private String nationalInsuranceNumber;
    private String dateOfBirth;
    private Address address;
    private String email;
    private String phoneNumber;

    // This empty constructor will remain here to avoid getting a compilation error in Customer class
    // Remove after deleting the original test data
    public User(){

    }

    // Constructor overload
    public User(String fName, String lName, String nationalInsuranceNumber, String dateOfBirth, String pob, String email, String phoneNumber, String address){
        this.firstName = fName;
        this.lastName = lName;
        this.nationalInsuranceNumber = nationalInsuranceNumber;
        this.dateOfBirth = dateOfBirth;
        this.pob = pob;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    // We're exposing the first name because we want to use it as a key in the customers<String, Customer> HashMap
    public String getFirstName() {
        return firstName;
    }
    
    
}
