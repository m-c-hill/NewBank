package server.database;

import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import server.account.Account;
import server.account.Currency;
import server.bank.Bank;
import server.transaction.StatementSchedule;
import server.bank.Address;
import server.transaction.TransactionType;
import server.user.Customer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

import static server.database.Connection.getDBConnection;

/**
 * Utilities class for database interactions to retrieve and store data
 */
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
     *
     * @param customer the customer object to be added to the database
     */
    public void registerNewCustomer(Customer customer) {

        int addressId = storeUserAddress(customer);
        int userId = createNewUser(customer, addressId);
        int customerId = createNewCustomer(userId);
        createNewAccount("Regular Account", customerId, NEWBANK_ID, REGULAR_ACCOUNT_ID, StatementSchedule.WEEKLY, "GBP");

        out.println("New user created \nUser Id: " + userId + "\nCustomer Id: " + customerId);
    }

    /**
     * Method for creating a new customer (subset of User)
     *
     * @param userId the userId to associate the new customer with
     * @return the customerId
     */
    private int createNewCustomer(int userId) {
        int customerId = 0;

        try {
            String createCustomerIdScript = "INSERT INTO customer (user_id) VALUES (?);";
            PreparedStatement preparedStatement = con.prepareStatement(createCustomerIdScript, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, userId);
            preparedStatement.executeUpdate();

            // get auto-generated customerId
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                customerId = rs.getInt(1);
            }

        } catch (SQLException exception) {
            out.println(exception);
        }
        return customerId;
    }

    /**
     * Method for creating a new account
     *
     * @param bankId        ID of the bank the account is created with
     * @param accountTypeId the type of account
     */
    private void createNewAccount(String accountName, int customerId, int bankId, int accountTypeId, StatementSchedule schedule, String currencyId) {

        int accountNum = Integer.parseInt(Account.generateNewAccountNumber());

        try {
            String createRegularAccount = "INSERT INTO account (account_number, account_name, customer_id, bank_id, account_type_id, statement_schedule, balance, currency_id) " +
                    "VALUES (?,?,?,?,?,?,?,?);";
            PreparedStatement preparedStatement = con.prepareStatement(createRegularAccount, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setInt(1, accountNum);
            preparedStatement.setString(2, accountName);
            preparedStatement.setInt(3, customerId);
            preparedStatement.setInt(4, bankId);
            preparedStatement.setInt(5, accountTypeId);
            preparedStatement.setString(6, schedule.toString());
            preparedStatement.setDouble(7, 0.0);
            preparedStatement.setString(8, currencyId);
            preparedStatement.executeUpdate();

        } catch (SQLException exception) {
            out.println(exception);
        }
        out.println("New regular account created: " + accountNum);
    }

    /**
     * Method for erasing/removing a bank account from a user.
     * @param accountNum Account number
     */
    public static void removeAccount(int accountNum) {
    	String queryMessage = "DELETE FROM account WHERE account_number = ?" ;
    	try {
            PreparedStatement preparedStatement = getDBConnection().prepareStatement(queryMessage);   
            preparedStatement.setInt(1, accountNum);
            preparedStatement.executeUpdate();
    	} catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Method to create a new user in the user table.
     *
     * @param customer  the customer object containing the user data
     * @param addressId the ID of the customers address stored in the address table.
     * @return the userId generated by the database.
     */
    private int createNewUser(Customer customer, int addressId) {
        int userId = 0;

        try {

            String createUserScript = "INSERT INTO user (prefix, first_names, last_name, address_id, date_of_birth, email_address, phone_number, national_insurance_number)" +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement preparedStatement = con.prepareStatement(createUserScript, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, customer.getPrefix());
            preparedStatement.setString(2, customer.getFirstName());
            preparedStatement.setString(3, customer.getLastName());
            preparedStatement.setInt(4, addressId);
            preparedStatement.setDate(5, convertToSqlDate(customer.getDateOfBirth()));
            preparedStatement.setString(6, customer.getEmailAddress());
            preparedStatement.setString(7, customer.getPhoneNumber());
            preparedStatement.setString(8, customer.getNationalInsuranceNumber());
            preparedStatement.executeUpdate();

            // get auto-generated userId
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                userId = rs.getInt(1);
            }

        } catch (SQLException exception) {
            out.println(exception.toString());
        }
        return userId;
    }

    /**
     * Method to store the user address contained within the customer object.
     *
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
            if (rs.next()) {
                addressId = rs.getInt(1);
            }
        } catch (SQLException exception) {
            out.println(exception);
        }

        return addressId;
    }


    /**
     * Method to store a newly created Ethereum wallet.
     * @param userId the user id (primary key)of the user that has just
     *               created the Ethereum wallet.
     * @param wallet the wallet to be stored.
     */
    public void storeEthereumWallet(int userId, Bip39Wallet wallet) {

        try {

            String pathToWallets = "./ethereum_wallets";
            Path walletPath = Paths.get(pathToWallets + "/" + wallet.getFilename());
            String walletContent = Files.readString(walletPath);

            String storeEthereumWallet = "INSERT INTO ethereum_wallet (userId, wallet_contents)" +
                    " VALUES (?, ?);";

            PreparedStatement preparedStatement = con.prepareStatement(storeEthereumWallet);
            preparedStatement.setInt(1, userId);
            preparedStatement.setString(2, walletContent);
            preparedStatement.executeUpdate();

        } catch (SQLException | IOException exception) {
            out.println(exception);
        }
    }

    /**
     * Method to retrieve a users Ethereum wallet from the database
     * @param userId the user id used to query the database
     * @return String wallet contents.
     */
    public Credentials retrieveEthereumCredentials(int userId, BufferedReader in){

        Credentials credentials = null;
        try {
            String retrieveEthereumWallet = "SELECT * FROM ethereum_wallet WHERE userid=?";
            PreparedStatement preparedStatement = con.prepareStatement(retrieveEthereumWallet);
            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery();
            System.out.println("Result Set: " + rs);

            if(rs.next()){
                out.println("Wallet found for user:");
                String walletContents = rs.getString("wallet_contents");

                // create a Credentials object from the wallet contents
                // this requires the user tp enter their separate Ethereum wallet password
                out.println("Please enter your separate Ethereum wallet password");
                try {
                    String walletPassword = in.readLine();
                    credentials = WalletUtils.loadJsonCredentials(walletPassword, walletContents);
                } catch (IOException | CipherException e) {
                    e.printStackTrace();
                }
            } else {
                out.println("You do not yet have an Ethereum Wallet, you can create one from the main menu");
            }

        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        return credentials;
    }

    /**
     * Method to check if user has entered a unique ID (check in database)
     * @return True if user login is
     */
    public static boolean checkLoginExists(String login) {
        String query = "SELECT 1 FROM password WHERE login = ?";
        try {
            PreparedStatement preparedStatement = getDBConnection().prepareStatement(query);
            preparedStatement.setString(1, login);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.isBeforeFirst()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Method to update the account balance in the database
     */
    public static void updateBalance(String accountNumber, double newBalance) {
        String query = "UPDATE account SET balance = ? WHERE account_number = ?";
        try {
            PreparedStatement preparedStatement = getDBConnection().prepareStatement(query);
            preparedStatement.setDouble(1, newBalance);
            preparedStatement.setString(2, accountNumber);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Method to check if an account number exists
     * @return True if a given account number exists in the database
     */
    public static boolean checkAccountNumberExists(String accountNumber) {
        String query = "SELECT 1 FROM account WHERE account_number = ?";
        try {
            PreparedStatement preparedStatement = getDBConnection().prepareStatement(query);
            preparedStatement.setString(1, accountNumber);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Date convertToSqlDate(java.util.Date date) {
        return new Date(date.getTime());
    }

    /**
     * Method to check if an admin role exists
     * @return True if admin roles exists in the database
     */
    public static boolean checkAdminRoleExists(String roleName) {
        String query = "SELECT 1 FROM admin_role_type WHERE name = ?";
        try {
            PreparedStatement preparedStatement = getDBConnection().prepareStatement(query);
            preparedStatement.setString(1, roleName);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Method to store a new admin role in the database
     */
    public static void storeAdminRole(String name, String description, boolean canViewUserInfo,
                                      boolean canViewUserStatement, boolean canOpenAccount, boolean canCloseAccount,
                                      boolean canViewLoanRequests, boolean canHandleLoanRequests){
        String query = "INSERT INTO newbank.admin_role_type(name, description, can_view_user_info, " +
                "can_view_user_statement, can_open_account, can_close_account, can_view_loan_requests, " +
                "can_handle_loan_requests) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = getDBConnection().prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, description);
            preparedStatement.setBoolean(3, canViewUserInfo);
            preparedStatement.setBoolean(4, canViewUserStatement);
            preparedStatement.setBoolean(5, canOpenAccount);
            preparedStatement.setBoolean(6, canCloseAccount);
            preparedStatement.setBoolean(7, canViewLoanRequests);
            preparedStatement.setBoolean(8, canHandleLoanRequests);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    // TODO: this method is temporary and should be merged with createNewAccount ASAP
    /**
     * Method to store a new admin role in the database
     */
    public static void storeAccount(Customer customer, String accountNumber, String accountName, int bankId,
                                    String statementSchedule, double balance, String currency){
        String query = "INSERT INTO account(account_number, account_name, customer_id, " +
                "bank_id, account_type_id, statement_schedule, balance, currency_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            PreparedStatement preparedStatement = getDBConnection().prepareStatement(query);
            preparedStatement.setString(1, accountNumber);
            preparedStatement.setString(2, accountName);
            preparedStatement.setInt(3, DbUtils.getCustomerId(customer.getUserID()));
            preparedStatement.setInt(4, bankId);
            preparedStatement.setInt(5, 1); // All general accounts for now
            preparedStatement.setString(6, statementSchedule);
            preparedStatement.setDouble(7, balance);
            preparedStatement.setString(8, currency);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Method to store a new transaction in the database
     */
    public static void storeTransaction(Account account, int transactionTypeId, Timestamp date, String payee,
                                        double amount, Currency currency){
        String query = "INSERT INTO transaction(account_number, transaction_type_id, date, payee, amount, currency_id) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        try{
            PreparedStatement preparedStatement = getDBConnection().prepareStatement(query);
            preparedStatement.setString(1, account.getAccountNumber());
            preparedStatement.setInt(2, transactionTypeId);
            preparedStatement.setTimestamp(3, date);
            preparedStatement.setString(4, payee);
            preparedStatement.setDouble(5, amount);
            preparedStatement.setString(6, currency.getCurrencyId());
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Method to retrieve the customer ID based on a user ID
     * @param userId User ID
     * @return Customer ID
     */
    public static int getCustomerId(int userId){
        String query = "SELECT * FROM customer WHERE user_id = ?";
        try {
            PreparedStatement preparedStatement = getDBConnection().prepareStatement(query);
            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("customer_id");
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return -1; // Return -1 if no customer found
    }

    /**
     * Query for account validation when user is attempting to recover a forgotten login or reset their password.
     * @param firstName First name
     * @param lastName Last name
     * @param dateOfBirth Date of birth
     * @param postcode Postcode
     * @return User ID if details match a user in the database, else returns -1
     */
    public static int accountRecovery(String firstName, String lastName, String dateOfBirth, String postcode){
        String query = "SELECT user_id " +
                "FROM user u " +
                "LEFT JOIN address a " +
                "ON a.address_id = u.address_id " +
                "WHERE u.first_names = ? " +
                "AND u.last_name = ? " +
                "AND u.date_of_birth = ? " +
                "AND a.postcode = ?";
        try {
            PreparedStatement preparedStatement = getDBConnection().prepareStatement(query);
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, dateOfBirth);
            preparedStatement.setString(4, postcode);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("user_id");
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return -1;
    }

    /**
     * Method to store a new loan request in the database
     */
    public static void storeLoan(Customer customer, Account recipientAccount, double amountLoaned, Currency currency,
                                 String approvalStatus, boolean transferStatus, String reason, Double interestRate,
                                 double outstandingPayment){
        String query = "INSERT INTO loans(customer_id, account_number, amount_loaned, currency_id, " +
                "approval_status, transfer_status, reason, interest_rate, outstanding_payment) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try{
            PreparedStatement preparedStatement = getDBConnection().prepareStatement(query);
            preparedStatement.setInt(1, getCustomerId(customer.getUserID()));
            preparedStatement.setString(2, recipientAccount.getAccountNumber());
            preparedStatement.setDouble(3, amountLoaned);
            preparedStatement.setString(4, currency.getCurrencyId());
            preparedStatement.setString(5, approvalStatus);
            preparedStatement.setBoolean(6, transferStatus);
            preparedStatement.setString(7, reason);
            preparedStatement.setDouble(8, interestRate);
            preparedStatement.setDouble(9, outstandingPayment);
            preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }
}
