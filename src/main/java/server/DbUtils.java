package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static server.Connection.getDBConnection;

public class DbUtils {

    private final PrintWriter out;

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
    public void createNewCustomer(Customer customer) {
        int userId = 0;
        int customerId = 0;

        // Adding a new user will require the address details to be stored in a separate table and referenced in
        // the user table with an addressId
        int addressId = storeUserAddress(customer);

        try {
            String createUserScript = "INSERT INTO user (prefix, first_names, last_name, address_id, date_of_birth, email_address, phone_number, national_insurance_number, login_id)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement preparedStatement = con.prepareStatement(createUserScript, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1,customer.getPrefix());
            preparedStatement.setString(2, customer.getFirstName());
            preparedStatement.setString(3, customer.getLastName());
            preparedStatement.setInt(4, addressId);
            preparedStatement.setString(5, customer.getDateOfBirth());
            preparedStatement.setString(6, customer.getEmailAddress());
            preparedStatement.setString(7, customer.getPhoneNumber());
            preparedStatement.setString(8, customer.getNationalInsuranceNumber());
            preparedStatement.setString(9, "testLoginId");
            preparedStatement.executeUpdate();

            // get auto-generated userId
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if(rs.next()) {
                userId = rs.getInt(1);
            }

        } catch (SQLException exception) {
            out.println(exception.toString());
        }

        try {
            String createCustomerIdScript = "INSERT INTO customer (user_id, account_number) " +
                    "VALUES (?,?);";
            PreparedStatement preparedStatement = con.prepareStatement(createCustomerIdScript, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, "12345678");
            preparedStatement.executeUpdate();

            // get auto-generated customerId
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if(rs.next()){
                customerId = rs.getInt(1);
            }

        } catch (SQLException exception) {
            out.println(exception.toString());
        }

        out.println("New user created \nUser Id: " + userId + "\nCustomer Id: " + customerId);
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
            String createUserAddress = "INSERT INTO address (address_num, address_line_1, address_line_2, city, region, postcode, country)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?);";

            PreparedStatement preparedStatement = con.prepareStatement(createUserAddress, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, userAddress.getAddressNumber());
            preparedStatement.setString(2, userAddress.getAddressLine1());
            preparedStatement.setString(3, userAddress.getAddressLine2());
            preparedStatement.setString(4, userAddress.getCity());
            preparedStatement.setString(5, userAddress.getRegion());
            preparedStatement.setString(6, userAddress.getPostcode());
            preparedStatement.setString(7, userAddress.getCountry());
            preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if(rs.next()) {
                addressId = rs.getInt(1);
            }
        } catch (SQLException exception) {
            out.println(exception.toString());
        }

        return addressId;
    }
}
