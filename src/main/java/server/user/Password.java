package server.user;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import static server.database.Connection.getDBConnection;

/**
 * TODO: class doc string
 * Reference: https://www.javacodegeeks.com/2012/05/secure-password-storage-donts-dos-and.html
 */
public class Password {
	private String login;
	private final java.sql.Connection con = getDBConnection();

	public Password(int userId, String login, String plainTextPassword) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		this.login = login;
		if (!checkLoginExists(login)){
			byte[] salt = generateSalt();
			byte[] hash = encryptPassword(plainTextPassword, salt);
			storeSaltAndHash(userId, login, salt, hash);
			System.out.println("New password has been stored");
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

	public boolean authenticate(int userId, String plainTextPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
		// Method to check authentication when user enters a password

		byte[][] saltAndHash = retrieveSaltAndHash(userId);
		byte[] salt = saltAndHash[0];
		byte[] hashDB = saltAndHash[1];

		byte[] hashPlainText = encryptPassword(plainTextPassword, salt);

		return Arrays.equals(hashPlainText, hashDB);
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
		int iterations = 1000;
		KeySpec spec = new PBEKeySpec(plainTextPassword.toCharArray(), salt, iterations, derivedKeyLength);
		SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);
		return f.generateSecret(spec).getEncoded();
	}

	/**
	 * Method to store a salt and hash in the database
	 * @param userId User ID
	 * @param login User's login
	 * @param salt Unique salt generated for user
	 * @param hash Encrypted password
	 */
	public void storeSaltAndHash(int userId, String login, byte[] salt, byte[] hash){
		try{
			String query = "INSERT INTO password (user_id, login, pw_salt, pw_hash) VALUES (?, ?, ?, ?)";
			PreparedStatement preparedStatement = con.prepareStatement(query);
			preparedStatement.setInt(1, userId);
			preparedStatement.setString(2, login);
			preparedStatement.setBytes(3, salt);
			preparedStatement.setBytes(4, hash);
			preparedStatement.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to retrieve a string from the database
	 * @return Array containing two byte arrays: the user's salt and user's hash
	 */
	private byte[][] retrieveSaltAndHash(int userId){
		byte[] salt = new byte[]{};
		byte[] hash = new byte[]{};

		try{
			String query = "SELECT * FROM password WHERE user_id=?";
			PreparedStatement preparedStatement = con.prepareStatement(query);
			preparedStatement.setInt(1, userId);
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

	private int retrieveUserId(String login){
		return 1;
	}

	/**
	 * Method to check if user has entered a unique ID (check in database)
	 * @return True if user login is
	 */
	public boolean checkLoginExists(String login){
		String query = "SELECT 1 FROM password WHERE login = ?";
		try{
			PreparedStatement preparedStatement = con.prepareStatement(query);
			preparedStatement.setString(1, login);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void resetPassword(){

	}
}
