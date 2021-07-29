package server;

import server.user.Password;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class Main {

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
		Password pw = new Password(200,"matt", "password");
		System.out.println("test");
		boolean auth = pw.authenticate("password");
		System.out.println(auth);
		boolean auth2 = pw.authenticate("notthepassword");
		System.out.println(auth2);
		pw.resetPassword("newpassword1");
		System.out.println("Password successfully reset");
		boolean auth3 = pw.authenticate("newpassword1");
		System.out.println(auth3);
	}
}
