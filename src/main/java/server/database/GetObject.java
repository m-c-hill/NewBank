package server.database;

import server.account.Account;
import server.account.Currency;
import server.bank.Address;
import server.bank.Bank;
import server.bank.BankLoan;
import server.user.Admin;
import server.user.AdminRole;
import server.user.Customer;
import server.user.Password;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import static server.database.Connection.getDBConnection;

/**
 * Class to include methods that retrieve data from the database tables to create corresponding objects
 */
public class GetObject {

	/**
	 * Method to retrieve customer data from the database and create a Customer object
	 * @param userId User ID
	 * @return Customer
	 */
	public static Customer getCustomer(int userId) {

		// Query will return the details of a single customer from their user_id
		String query = "SELECT * FROM customer c " +
				"LEFT JOIN user u " +
				"ON u.user_id = c.user_id " +
				"WHERE u.user_id = ?";
		try {
			PreparedStatement preparedStatement = getDBConnection().prepareStatement(query);
			preparedStatement.setInt(1, userId);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				String prefix = rs.getString("prefix");
				String firstName = rs.getString("first_names");
				String lastName = rs.getString("last_name");
				String nationalInsuranceNumber = rs.getString("national_insurance_number");
				String dateOfBirth = rs.getDate("date_of_birth").toString();
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
	 * Method to retrieve account data from the database and create a single Account object
	 * @param accountNumber Account number
	 * @return Account (null if no account found)
	 */
	public static Account getAccount(String accountNumber){
		String query = "SELECT * FROM account WHERE account_number = ?";
		try{
			PreparedStatement preparedStatement = getDBConnection().prepareStatement(query);
			preparedStatement.setString(1, accountNumber);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				String accountName = rs.getString("account_name");
				int bankId = rs.getInt("bank_id");
				double balance = rs.getDouble("balance");
				String currencyId = rs.getString("currency_id");
				return new Account(accountNumber, accountName, bankId, balance, currencyId);
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return null;
	}

	/**
	 * Method to retrieve account data from the database and create an array of Account objects associated with a customer
	 * @param userId User ID
	 * @return List of Accounts for a Customer (empty array if no accounts found)
	 */
	public static ArrayList<Account> getAccounts(int userId){
		ArrayList<Account> accounts = new ArrayList<>();
		String query = "SELECT * FROM customer c "+
				"LEFT JOIN user u ON c.user_id = u.user_id "+
				"LEFT JOIN account a ON a.customer_id = c.customer_id "+
				"WHERE u.user_id = ?";
		try{
			PreparedStatement preparedStatement = getDBConnection().prepareStatement(query);
			preparedStatement.setInt(1, userId);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				String accountNumber = rs.getString("account_number");
				String accountName = rs.getString("account_name");
				int bankId = rs.getInt("bank_id");
				double balance = rs.getDouble("balance");
				String currencyId = rs.getString("currency_id");
				accounts.add(new Account(accountNumber, accountName, bankId, balance, currencyId));
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return accounts;
	}

	/**
	 * Method to retrieve bank data from the database and create a Bank object
	 * @param bankId Bank ID
	 * @return Bank (null if no bank found for the ID provided)
	 */
	public static Bank getBank(int bankId){
		String query = "SELECT * FROM bank WHERE bank_id=?";
		try{
			PreparedStatement preparedStatement = getDBConnection().prepareStatement(query);
			preparedStatement.setInt(1, bankId);
			ResultSet rs = preparedStatement.executeQuery();
			while(rs.next()) {
				String name = rs.getString("name");
				int addressId = rs.getInt("address_id");
				String sortCode = rs.getString("sort_code");
				Address bankAddress = getAddress(addressId);
				return new Bank(name, bankAddress, sortCode);
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return null;
	}

	/**
	 * Method to retrieve currency data from the database and create a Currency object
	 * @param currencyId Currency ID
	 * @return Currency (null if no currency found for the ID provided)
	 */
	public static Currency getCurrency(String currencyId){
		String query = "SELECT * FROM currency WHERE currency_id=?";
		try{
			PreparedStatement preparedStatement = getDBConnection().prepareStatement(query);
			preparedStatement.setString(1, currencyId);
			ResultSet rs = preparedStatement.executeQuery();
			while(rs.next()) {
				double exchangerate = rs.getDouble("usd_exchange_rate");
				Timestamp dtUpdated = rs.getTimestamp("dt_updated");
				boolean crypto = rs.getBoolean("crypto");
				return new Currency(currencyId, exchangerate, dtUpdated, crypto);
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return null;
	}

	/**
	 * Method to retrieve admin data from the database and create an Admin object
	 * @param userId User ID
	 * @return Admin
	 */
	public static Admin getAdmin(int userId) {
		// Query will return the details of a single admin from their user_id
		String query = "SELECT * FROM admin a " +
				"LEFT JOIN user u " +
				"ON u.user_id = c.user_id " +
				"WHERE u.user_id = ?";
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
				Address adminAddress = getAddress(addressId);
				int adminRoleId = rs.getInt("admin_role_type_id");
				AdminRole role = getAdminRole(adminRoleId);
				return new Admin(userId, prefix, firstName, lastName, nationalInsuranceNumber, dateOfBirth, emailAddress,
						phoneNumber, adminAddress, role);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Method to retrieve admin role data from the database and create an Admin Role object
	 * @param adminRoleId Admin Role ID
	 * @return AdminRole
	 */
	public static AdminRole getAdminRole(int adminRoleId){
		String query = "SELECT * FROM admin_role_type WHERE admin_role_type_id = ?";
		try{
			PreparedStatement preparedStatement = getDBConnection().prepareStatement(query);
			preparedStatement.setInt(1, adminRoleId);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				String name = rs.getString("name");
				String description = rs.getString("description");
				boolean canViewUserInfo = rs.getBoolean("can_view_user_info");
				boolean canViewUserStatement = rs.getBoolean("can_view_user_statement");
				boolean canOpenAccount = rs.getBoolean("can_open_account");
				boolean canCloseAccount = rs.getBoolean("can_close_account");
				boolean canViewLoanRequests = rs.getBoolean("can_view_loan_requests");
				boolean canHandleLoanRequest = rs.getBoolean("can_handle_loan_requests");
				return new AdminRole(name, description, canViewUserInfo, canViewUserStatement, canOpenAccount,
						canCloseAccount, canHandleLoanRequest,canViewLoanRequests);
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return null;
	}

	/**
	 * Method to retrieve login data from the database and create a Password object
	 * Hash and salt are not retrieved at this point in the process for security purposes
	 * @param userId User ID
	 * @return Password
	 */
	public static Password getPassword(int userId){
		String query = "SELECT * FROM password WHERE user_id = ?";
		try{
			PreparedStatement preparedStatement = getDBConnection().prepareStatement(query);
			preparedStatement.setInt(1, userId);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				String login = rs.getString("login");
				return new Password(userId, login);
			}
		} catch(SQLException exception){
			exception.printStackTrace();
		}
		return null;
	}

	/**
	 * Method to retrieve all bank loan requests belonging to a specific Customer
	 * @param customerId Customer ID
	 * @return Array of Bank Loans
	 */
	public static ArrayList<BankLoan> getLoanList(int customerId){
		ArrayList<BankLoan> loanList = new ArrayList<BankLoan>();
		String query = "SELECT * FROM loans WHERE customer_id = ?";
		try{
			PreparedStatement preparedStatement = getDBConnection().prepareStatement(query);
			preparedStatement.setInt(1, customerId);
			ResultSet rs = preparedStatement.executeQuery();
			while (rs.next()) {
				//
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return null;
	}

	// TODO: complete this
	/**
	 * Method to retrieve a single loan request from the database by loan ID
	 * @param loanId Loan ID
	 * @return Bank Loan
	 */
	public static BankLoan getLoan(int loanId){
		String query = "SELECT * FROM admin_role_type WHERE loan_id = 1";
		try{
			PreparedStatement preparedStatement = getDBConnection().prepareStatement(query);
			preparedStatement.setInt(1, adminRoleId);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				String name = rs.getString("name");
				String description = rs.getString("description");
			}
		} catch (SQLException exception) {
			exception.printStackTrace();
		}
		return null;
	}
}
