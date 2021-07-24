package server;

import java.util.ArrayList;

public class Admin extends User{
	int adminID;
	AdminRole role;

	public Admin(int userID, String prefix, String fName, String lName, String nationalInsuranceNumber,
				 String dateOfBirth, String emailAddress, String phoneNumber, Address address,
				 Password password, int adminID, AdminRole role) {
		super(userID, prefix, fName, lName, nationalInsuranceNumber, dateOfBirth, emailAddress, phoneNumber, address, password);
		this.adminID = adminID;
		this.role = role;
	}

	private boolean checkPermission(String taskName){
		// Method to check if an admin has the required permissions perform a specific task
		switch(taskName){
			case "viewUserInfo":
				return role.canViewUserInfo();
			case "viewUserStatement":
				return role.canViewUserStatement();
			case "openAccount":
				return role.canOpenAccount();
			case "closeAccount":
				return role.canCloseAccount();
			case "grantLoan":
				return role.canGrantLoan();
			default:
				System.out.println("Warning: you do not have permission perform the following action: " + taskName);
				return false;
		}
	}

	private void viewUserInfo(int userID){
		//TODO: create method to allow admin to view user information
		if(checkPermission("viewUserInfo")) {
			//code here
		}
	}

	private void viewUserStatement(int userID){
		//TODO: create method to allow admin to view recent statements for a given user / account
		if(checkPermission("viewUserStatement")) {
			// code here
		}
	}

	private void openAccount(){
		// TODO: create method to open an account
		if(checkPermission("openAccount")) {
			// code here
		}
	}

	private void closeAccount(int accountID){
		// TODO: create method to close an account (delete Account object from memory and from the database)
		if(checkPermission("closeAccount")) {
			// code here
		}
	}

	private void grantLoan(){
		// TODO: create method that shows unapproved loans and enables the admin to reject or approve them
		if(checkPermission("grantLoan")) {
			// code here
		}
	}

}
