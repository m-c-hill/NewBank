package server;

public abstract class User {

    private String firstName;
    private String lastName;
    private String ssn;
    private String dob;
    private String pob;
    private String email;
    private String address;

    public User(){

    }

    public User(String fName, String lName, String ssn, String dob, String pob, String email, String address){
        this.firstName = fName;
        this.lastName = lName;
        this.ssn = ssn;
        this.dob = dob;
        this.pob = pob;
        this.email = email;
        this.address = address;
    }

    public String getFirstName() {
        return firstName;
    }
    
    
}
