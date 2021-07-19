package server;

public abstract class User {

    private String firstName;
    private String lastName;
    private String ssn;
    private String dob;
    private String pob;
    private String email;
    private String phoneNum;
    private String address;

    // This empty constructor will remain here to avoid getting a compilation error in Customer class
    // Remove after deleting the original test data
    public User(){

    }

    // Constructor overload
    public User(String fName, String lName, String ssn, String dob, String pob, String email, String phoneNum, String address){
        this.firstName = fName;
        this.lastName = lName;
        this.ssn = ssn;
        this.dob = dob;
        this.pob = pob;
        this.email = email;
        this.phoneNum = phoneNum;
        this.address = address;
    }

    // We're exposing the first name because we want to use it as a key in the customers<String, Customer> HashMap
    public String getFirstName() {
        return firstName;
    }
    
    
}
