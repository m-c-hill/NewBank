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

        try {
            String createUserScript = "INSERT INTO user (prefix, first_names, last_name, address_id, date_of_birth, email_address, phone_number, national_insurance_number, login_id)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement preparedStatement = con.prepareStatement(createUserScript, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1,customer.getPrefix());
            preparedStatement.setString(2, customer.getFirstName());
            preparedStatement.setString(3, customer.getLastName());
            preparedStatement.setInt(4, 123);
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
}
