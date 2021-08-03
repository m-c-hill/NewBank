package server;

import server.database.GetObject;
import server.user.Customer;
import server.user.Password;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Main {

	/**
	 * This is for testing purposes and will be deleted before final release
	 * @param args Args
	 */
	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		// Generates logins and passwords for the dummy data (3 customers, 3 admins)
		dummyLogins();
		testSQL();
		System.out.println("test");
	}

	/**
	 * Method to test out various SQL pieces
	 */
	private static void testSQL(){
		Customer customer = GetObject.getCustomer(1);
		System.out.println(customer.getFirstName());
		System.out.println(customer.toString());
	}

	/**
	 * Method to set up dummy data passwords using the
	 */
	private static void dummyLogins() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		int user_id_1 = 1;
		String login_1 = "mark";
		String password_1 = "pw_mark";

		int user_id_2 = 2;
		String login_2 = "jeremy";
		String password_2 = "pw_jeremy";

		int user_id_3 = 3;
		String login_3 = "sophie";
		String password_3 = "pw_sophie";

		int user_id_4 = 4;
		String login_4 = "alan";
		String password_4 = "pw_alan";

		int user_id_5 = 5;
		String login_5 = "april";
		String password_5 = "pw_april";

		int user_id_6 = 6;
		String login_6 = "simon";
		String password_6 = "pw_simon";

		int[] userIds = {user_id_1, user_id_2, user_id_3, user_id_4, user_id_5, user_id_6};
		String[] logins = {login_1, login_2, login_3, login_4, login_5, login_6};
		String[] passwords = {password_1, password_2, password_3, password_4, password_5, password_6};

		for (int i = 0; i < logins.length; i++) {
			try {
				Password password = new Password(userIds[i], logins[i], passwords[i]);
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				e.printStackTrace();
			}
		}
	}
}
