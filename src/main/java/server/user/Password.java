package server.user;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

import static server.database.Connection.getDBConnection;

/**
 * TODO: class doc string
 * Reference: https://www.javacodegeeks.com/2012/05/secure-password-storage-donts-dos-and.html
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
		// String salt = generateSalt();
		// String encryptedPassword = encryptPassword(plainTextPassword, salt);
		// storeEncryption();
		// storeSalt();

		// No further fields should be assigned to the class - all should be stored in the database and retrieved using the login id
	}

	public static void main(String[] args) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		byte[] salt = generateSalt();
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
		String statement = "SELECT 1 FROM password WHERE user_id = 1";
		return false;
	}

	private void storeEncryption(){
		// Method to store encrypted password in database
	}

	private void storeSalt(){
		// Method to store salt in database
		// https://stackoverflow.com/questions/17498265/java-store-byte-array-as-string-in-db-and-create-byte-array-using-string-value
	}

	/**
	 * Generates a random salt using the pseudo random number generator algorithm "SHA1PRNG"
	 * @return salt
	 */
	private static byte[] generateSalt() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		final SecureRandom RANDOM = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[8];
		RANDOM.nextBytes(salt);
		//String test = new String(salt, StandardCharsets.ISO_8859_1);
		//System.out.println(test);
		return salt;
	}

	/**
	 * Encrypts a plain text password using the 'PBKDF2 with SHA-1' hashing algorithm
	 * @param plainTextPassword Plain text password entered by the user
	 * @param salt User's unique salt
	 * @return Encrypted password
	 */
	private byte[] encryptPassword(String plainTextPassword, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
		String algorithm = "PBKDF2WithHmacSHA1";
		int derivedKeyLength = 160;
		int iterations = 500;
		KeySpec spec = new PBEKeySpec(plainTextPassword.toCharArray(), salt, iterations, derivedKeyLength);
		SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);
		return f.generateSecret(spec).getEncoded();
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