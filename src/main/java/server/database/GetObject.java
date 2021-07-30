package server.database;

import server.account.Account;
import server.account.Balance;
import server.bank.Address;
import server.user.Admin;
import server.user.Customer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import static server.database.Connection.getDBConnection;

public class GetObject {
	/**
	 * Method to retrieve customer data from the database and create a Customer object
	 * @param userId User ID
	 * @return Customer
	 */
	public static Customer getCustomer(int userId) {

		// Query will return the details of a single customer from their user_id
		String query = "SELECT * FROM customer c LEFT JOIN user u ON u.user_id = c.user_id WHERE u.user_id = ?";
		try {
			PreparedStatement preparedStatement = getDBConnection().prepareStatement(query);
			preparedStatement.setInt(1, userId);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				String prefix = rs.getString("prefix");
				String firstName = rs.getString("first_names");
				String lastName = rs.getString("last_name");
				String nationalInsuranceNumber = rs.getString("national_insurance_number");
				String dateOfBirth = rs.getDate("dateOfBirth").toString();
				String emailAddress = rs.getString("email_address");
				String phoneNumber = rs.getString("phone_number");
				int addressId = rs.getInt("address_id");
				Address custAddress = getAddress(addressId);
				return new Customer(userId, prefix, firstName, lastName, nationalInsuranceNumber, dateOfBirth, emailAddress,
						phoneNumber, custAddress);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Method to retrieve address data from the database and create an Address object
	 * @param addressId Address ID
	 * @return Address
	 */
	public static Address getAddress(int addressId){
		// TODO: redo address table in database
		String query = "SELECT * FROM address WHERE address_id = ?";
		try {
			PreparedStatement preparedStatement = getDBConnection().prepareStatement(query);
			preparedStatement.setInt(1, addressId);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				String addressLine1 = rs.getString("address_line_1");
				String addressLine2 = rs.getString("address_line_2");
				String city = rs.getString("city");
				String region = rs.getString("region");
				String postcode = rs.getString("postcode");
				String country = rs.getString("country");
				return new Address(addressLine1, addressLine2, city, region, postcode, country);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Method to retreive account data from the database and create an account object
	 * @param userId User ID
	 * @return Account
	 */
	public static ArrayList<Account> getAccounts(int userId){
		ArrayList<Account> accounts = new ArrayList<>();
		// TODO: update the database to include multiple accounts per user
		String query = "SELECT * FROM customer c LEFT JOIN user u ON c.user_id = u.user_id LEFT JOIN account a ON a.account_number = c.account_number LEFT JOIN balance b ON a.account_number = b.account_number WHERE u.user_id = ? AND b.primary_balance=true";
		try{
			PreparedStatement preparedStatement = getDBConnection().prepareStatement(query);
			preparedStatement.setInt(1, userId);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				String accountNumber = rs.getString("c.account_number");
				double balance = rs.getDouble("amount");
				accounts.add(new Account(accountNumber, balance));
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return null;
	}

	public static Balance getBalance(int balanceId){
		return null;
	}

	/**
	 * Method to retrieve admin data from the database and create an Admin object
	 * @param userId User ID
	 * @return Admin
	 */
	public static Admin getAdmin(int userId){
		// TODO create method to retrieve an admin account and roles
		String query = "SELECT * FROM admin a LEFT JOIN user u ON u.user_id = a.user_id WHERE u.user_id = ?";

		return null;
	}
}