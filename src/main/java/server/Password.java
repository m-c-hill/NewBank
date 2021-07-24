package server;
/*

NOTE: Class is currently in pseudocode state - will fill this out by Monday 26th July (Matt)
For reference: https://www.javacodegeeks.com/2012/05/secure-password-storage-donts-dos-and.html

 */
public class Password {
	private String loginID;

	public Password(String loginID, String plainTextPassword){
		// Used when a new user is set up. They will enter their password in plain text, it will then

		// Generate new salt
		// Encrypt plain text password

		// Store loginID in database
		// Store salt in database
		// Store encrypted password in database

		this.loginID = loginID;

		// Generate a new salt and encrpyt plain text password
		String salt = generateSalt();
		String encryptedPassword = encryptPassword(plainTextPassword, salt);
		storeEncryption();
		storeSalt();

		// No further fields should be assigned to the class - all should be stored in the database and retrieved using the login id
	}

	public boolean authenticate(String plainTextPassword){
		// Method to check authentication when user enters a password

		// Get unique salt from database for that user ID
		// Encrypt the password entered by the user
		// Get the encrypted password from the database
		// Compare the two - if match, return true
		return false;
	}

	public boolean checkLoginIDUnique(){
		// Method to check if user has entered a unique ID (check in database)
		return false;
	}

	private void storeEncryption(){
		// Method to store encrypted password in database
	}

	private void storeSalt(){
		// Method to store salt in database
	}

	private String generateSalt(){
		return "";
	}

	private String encryptPassword(String plainTextPassword, String salt){
		// Method to encrypt a plain text password
		return "";
	}

	private String getSalt(String loginID){
		// Method to return the salt from the database if it exists
		return "";
	}

	private String getEncryptedPassword(String loginID){
		// Method to return the encrytped password from the database if it exists
		return "";
	}

}