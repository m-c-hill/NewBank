package server.user;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import static server.database.Connection.getDBConnection;
import static server.database.DbUtils.checkLoginExists;

/**
 * Class to create, store and authenticate user login and password using a simple salt and hash encryption algorithm
 * Reference: https://www.javacodegeeks.com/2012/05/secure-password-storage-donts-dos-and.html
 */
public class Password {
	private final int userId;
	private final String login;
	private final java.sql.Connection con = getDBConnection();

	public Password(String login, String plainTextPassword) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		this.login = login;

		// If the login does not exist, then store a new login, salt and hash. This will be used during Registration.
		if (!checkLoginExists(this.login)){
			this.userId = getNewUserId();
			byte[] salt = generateSalt();
			byte[] hash = encryptPassword(plainTextPassword, salt);
			storeSaltAndHash(salt, hash);
		}

		// If the login does exist, pull the user id from the database
		else{
			this.userId = getExistingUserId(this.login);
		}
	}


	/**
	 * Generates a random salt using the pseudo random number generator algorithm "SHA1PRNG"
	 * @return salt
	 */
	private static byte[] generateSalt() throws NoSuchAlgorithmException {
		final SecureRandom RANDOM = SecureRandom.getInstance("SHA1PRNG");
		byte[] salt = new byte[8];
		RANDOM.nextBytes(salt);
		return salt;
	}

	/**
	 * Encrypts a plain text password using the 'PBKDF2 with SHA-1' hashing algorithm
	 * The method appends the user's unique pseudo-random salt to the plain text password and then applies the PBKDF2
	 * algorithm multiple times over a cycle of 1000 iterations
	 * @param plainTextPassword Plain text password entered by the user
	 * @param salt User's unique salt
	 * @return Encrypted password in byte array form
	 */
	private byte[] encryptPassword(String plainTextPassword, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
		String algorithm = "PBKDF2WithHmacSHA1";
		int derivedKeyLength = 160;
		int iterations = 1000;
		KeySpec spec = new PBEKeySpec(plainTextPassword.toCharArray(), salt, iterations, derivedKeyLength);
		SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);
		return f.generateSecret(spec).getEncoded();
	}

	/**
	 * Method to check authentication when user enters their password
	 * @param plainTextPassword Password entered by the user
	 * @return True if the user enters the correct password
	 */
	public boolean authenticate(String plainTextPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {

		byte[][] saltAndHash = retrieveSaltAndHash();
		byte[] salt = saltAndHash[0];
		byte[] hashDB = saltAndHash[1];

		byte[] hashPlainText = encryptPassword(plainTextPassword, salt);

		return Arrays.equals(hashPlainText, hashDB);
	}

	/**
	 * Method to store a salt and hash in the database
	 * @param salt Unique salt generated for user
	 * @param hash Encrypted password
	 */
	public void storeSaltAndHash(byte[] salt, byte[] hash){
		try{
			String query = "INSERT INTO password (user_id, login, pw_salt, pw_hash) VALUES (?, ?, ?, ?)";
			PreparedStatement preparedStatement = con.prepareStatement(query);
			preparedStatement.setInt(1, this.userId);
			preparedStatement.setString(2, this.login);
			preparedStatement.setBytes(3, salt);
			preparedStatement.setBytes(4, hash);
			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to retrieve a salt and hash for a specific user from the database
	 * @return Array containing two byte arrays: the user's salt and user's hash
	 */
	private byte[][] retrieveSaltAndHash(){
		byte[] salt = new byte[]{};
		byte[] hash = new byte[]{};

		try{
			String query = "SELECT * FROM password WHERE user_id = ?";
			PreparedStatement preparedStatement = con.prepareStatement(query);
			preparedStatement.setInt(1, this.userId);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()){
				salt = rs.getBytes("pw_salt");
				hash = rs.getBytes("pw_hash");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return new byte[][] {salt, hash};
	}

	/**
	 * Method to reset a user's password
	 * @param newPlainTextPassword New password entered by the user
	 */
	public void resetPassword(String newPlainTextPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {

		// Encrypt the new password using the original salt for that user
		byte[] salt = retrieveSaltAndHash()[0];
		byte[] newHash = encryptPassword(newPlainTextPassword, salt);

		// Store the new hash in the database
		try{
			String query = "UPDATE password SET pw_hash = ? WHERE user_id = ?";
			PreparedStatement preparedStatement = con.prepareStatement(query);
			preparedStatement.setBytes(1, newHash);
			preparedStatement.setInt(2, userId);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to return the next user ID to be stored in the database
	 * @return User ID
	 */
	private int getNewUserId(){
		int userId = 0;
		String query = "SELECT MAX(user_id) FROM user";

		try {
			PreparedStatement preparedStatement = con.prepareStatement(query);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				userId = rs.getInt("user_id") + 1;
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
		return userId;
	}

	/**
	 * Method to return the existing user ID from login
	 * @return User ID
	 */
	public static int getExistingUserId(String login){
		int userId = 0;
		String query = "SELECT * FROM password WHERE login = ?";
		try {
			PreparedStatement preparedStatement = getDBConnection().prepareStatement(query);
			preparedStatement.setString(1, login);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				userId = rs.getInt("user_id");
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
		return userId;
	}
}
