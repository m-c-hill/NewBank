package server.database;

import server.bank.Bank;
import server.transaction.StatementSchedule;
import server.bank.Address;
import server.user.Customer;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static server.database.Connection.getDBConnection;

public class DbUtils {

    private final PrintWriter out;
    private final int REGULAR_ACCOUNT_ID = 1;
    private final int NEWBANK_ID = 1;
    public DbUtils(PrintWriter out) throws IOException {
        this.out = out;
    }

    // Create the database connection
    static java.sql.Connection con = getDBConnection();

    /**
     * Method for adding a new customer user to the database.
     * The method will first create a record in the user table with values from the customer object.
     * Then the method will also add a new row to the customer table using the auto-generated primary
     * key from the user table.
     * @param customer the customer object to be added to the database
     */
    public void registerNewCustomer(Customer customer) {

        int addressId = storeUserAddress(customer);
        int userId = createNewUser(customer, addressId);
        int customerId = createNewCustomer(userId);

        out.println("New user created \nUser Id: " + userId + "\nCustomer Id: " + customerId);
    }

    /**
     * Method for creating a new customer (subset of User)
     * @param userId the userId to associate the new customer with
     * @return the customerId
     */
    private int createNewCustomer(int userId) {
        int customerId = 0;

        int accountNumber = createNewAccount(NEWBANK_ID, REGULAR_ACCOUNT_ID);

        try {
            String createCustomerIdScript = "INSERT INTO customer (user_id, account_number) " +
                    "VALUES (?,?);";
            PreparedStatement preparedStatement = con.prepareStatement(createCustomerIdScript, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, userId);
            preparedStatement.setInt(2, accountNumber);
            preparedStatement.executeUpdate();

            // get auto-generated customerId
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if(rs.next()){
                customerId = rs.getInt(1);
            }

        } catch (SQLException exception) {
            out.println(exception);
        }
        return customerId;
    }

    /**
     * Method for creating a new account
     * @param bankId ID of the bank the account is created with
     * @param accountTypeId the type of account
     * @return the account number generated by the database.
     */
    private int createNewAccount(int bankId, int accountTypeId) {
        int accountNum = 0;

        // need to create the user a regular account with NewBank
        // regular account type = 1
        // NewBank
        try {
            String createRegularAccount = "INSERT INTO account (bank_id, account_type_id, statement_schedule) " +
                    "VALUES (?,?, ?);";
            PreparedStatement preparedStatement = con.prepareStatement(createRegularAccount, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, bankId);
            preparedStatement.setInt(2, accountTypeId);
            preparedStatement.setString(3, StatementSchedule.WEEKLY.toString());
            preparedStatement.executeUpdate();

            // get auto-generated customerId
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if(rs.next()){
                accountNum = rs.getInt(1);
            }

        } catch (SQLException exception) {
            out.println(exception);
        }
        out.println("New regular account created: " + accountNum);

        return accountNum;
    }

    /**
     * Method to create a new user in the user table.
     * @param customer the customer object containing the user data
     * @param addressId the ID of the customers address stored in the address table.
     * @return the userId generated by the database.
     */
    private int createNewUser(Customer customer, int addressId) {
        int userId = 0;

        try {

            String createUserScript = "INSERT INTO user (prefix, first_names, last_name, address_id, date_of_birth, email_address, phone_number, national_insurance_number)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement preparedStatement = con.prepareStatement(createUserScript, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, customer.getPrefix());
            preparedStatement.setString(2, customer.getFirstName());
            preparedStatement.setString(3, customer.getLastName());
            preparedStatement.setInt(4, addressId);
            preparedStatement.setString(5, customer.getDateOfBirth());
            preparedStatement.setString(6, customer.getEmailAddress());
            preparedStatement.setString(7, customer.getPhoneNumber());
            preparedStatement.setString(8, customer.getNationalInsuranceNumber());
            preparedStatement.executeUpdate();

            // get auto-generated userId
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if(rs.next()) {
                userId = rs.getInt(1);
            }

        } catch (SQLException exception) {
            out.println(exception.toString());
        }
        return userId;
    }

    /**
     * Method to store the user address contained within the customer object.
     * @param customer the customer object containing the address.
     * @return the addressId to be referenced in the user table row.
     */
    private int storeUserAddress(Customer customer) {
        int addressId = 0;

        try {
            Address userAddress = customer.getAddress();
            String createUserAddress = "INSERT INTO address (address_line_1, address_line_2, city, region, postcode, country)" +
                    " VALUES (?, ?, ?, ?, ?, ?);";

            PreparedStatement preparedStatement = con.prepareStatement(createUserAddress, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, userAddress.getAddressLine1());
            preparedStatement.setString(2, userAddress.getAddressLine2());
            preparedStatement.setString(3, userAddress.getCity());
            preparedStatement.setString(4, userAddress.getRegion());
            preparedStatement.setString(5, userAddress.getPostcode());
            preparedStatement.setString(6, userAddress.getCountry());
            preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if(rs.next()) {
                addressId = rs.getInt(1);
            }
        } catch (SQLException exception) {
            out.println(exception);
        }

        return addressId;
    }

    /**
     * Method to check if user has entered a unique ID (check in database)
     * @return True if user login is
     */
    public static boolean checkLoginExists(String login){
        String query = "SELECT 1 FROM password WHERE login = ?";
        try{
            PreparedStatement preparedStatement = getDBConnection().prepareStatement(query);
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
}
