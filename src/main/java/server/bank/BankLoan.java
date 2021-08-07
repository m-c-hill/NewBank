package server.bank;

import server.account.Account;
import server.account.Currency;
import server.database.DbUtils;
import server.user.Customer;
import static server.database.Connection.getDBConnection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * Class to represent a loan provided to a customer through NewBank
 */
public class BankLoan {
    // Customer and account
    private final int loanId;
    private final Customer customer;
    private final Account recipientAccount;

    // Loan balance and payments
    private final double amountLoaned;
    private double outstandingPayments;
    private double amountPaidBack;

    // Other loan information
    private final double interestRate;
    private final Currency currency;
    private final String reason;

    // Status
    private String approvalStatus = "pending"; // Loan must be approved by admin - can be pending, approved or declined
    private boolean transferStatus = false; // True if loan has been transferred to user's account

    /**
     * Constructor to initialise a new bank loan request and store the request in the database
     * @param customer Customer
     * @param account Account to transfer loan to
     * @param reason Reason for loan
     * @param amount Amount to loan
     */
    public BankLoan(Customer customer, Account account, double amount, String reason){
        this.loanId = getLatestLoanId();
        this.customer = customer;
        this.recipientAccount = account;
        this.amountLoaned = amount;
        this.currency = account.getCurrency();
        this.interestRate = 5; // Default interest rate is 5%, loan managers will have the option to update this
        this.outstandingPayments = this.amountLoaned * (1 + interestRate/100.00);
        this.reason = reason;
        this.amountPaidBack = 0;

        // Store new loan request in the database for loan managers to view and approve
        DbUtils.storeLoan(
                this.customer,
                this.recipientAccount,
                this.amountLoaned,
                this.outstandingPayments,
                this.currency,
                this.reason,
                this.interestRate,
                this.approvalStatus,
                this.transferStatus
        );
    }

    /**
     * Constructor to create a loan instance from data retrieved from the database
     */
    public BankLoan(int loanId, Customer customer, Account account, double amount, Currency currency,
                    String approvalStatus, boolean transferStatus, String reason, double interestRate,
                    double outstandingPayments, double amountPaidBack){
        this.loanId = loanId;
        this.customer = customer;
        this.recipientAccount = account;
        this.amountLoaned = amount;
        this.currency = currency;
        this.approvalStatus = approvalStatus;
        this.transferStatus = transferStatus;
        this.reason = reason;
        this.interestRate = interestRate;
        this.outstandingPayments = outstandingPayments;
        this.amountPaidBack = amountPaidBack;
    }

    /**
     * Method to return the latest loan ID from the database, used as the primary key
     * @return Loan ID
     */
    private int getLatestLoanId() {
        String query = "SELECT loan_id FROM loans ORDER BY loan_id DESC";
        try {
            PreparedStatement preparedStatement = getDBConnection().prepareStatement(query);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return rs.getInt("loan_id");
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 1;
    }

    /**
     * Method to pay back a set amount of a user's loan
     * @param amount Amount to pay back
     */
    public void payBackLoan(double amount){
        this.outstandingPayments -= amount;
        this.amountPaidBack += amount;
        updateLoanRecord();
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public Account getAccount() {
        return recipientAccount;
    }

    public String getReason() {
        return this.reason;
    }

    public double getInterestRate() {
        return this.interestRate;
    }

    public double getAmountLoaned() {
        return amountLoaned;
    }

    public double getOutstandingPayments() {
        return outstandingPayments;
    }

    public Currency getCurrency() {
        return this.currency;
    }

    public String getApprovalStatus(){
        return this.approvalStatus;
    }

    public boolean getTransferStatus() {
        return this.transferStatus;
    }

    /**
     * Method to return a status for the loan table
     * @return Status
     */
    public String getStatus() {
        if (Objects.equals(this.approvalStatus, "approved")){
            return (this.transferStatus) ? "approved and transferred" : "approved and awaiting transfer";
        } else {
            return this.approvalStatus;
        }
    }

    public void updateApprovalStatus(String status) {
        this.approvalStatus = status;
        updateLoanRecord();
    }

    public void updateTransferStatus(boolean status) {
        this.transferStatus = status;
        updateLoanRecord();
    }

    private void updateLoanRecord(){
        // Update approval status, transfer status, outstanding payment, amount paid
    }
}
