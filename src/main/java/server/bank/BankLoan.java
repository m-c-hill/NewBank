package server.bank;

import server.account.Account;
import server.account.Currency;
import server.database.DbUtils;
import server.user.Customer;

/**
 * Class to represent a loan provided to a customer through NewBank
 */
public class BankLoan {
    // Customer and account
    private final Customer customer;
    private final Account recipientAccount;

    // Loan balance and payments
    private final double amountLoaned;
    private double outstandingPayments;
    private double amountPaidBack;

    // Further loan information
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
    /*
    public BankLoan(){
        //TODO
    }
    */

    public void makePayment(double amount) {
        this.outstandingPayments -= amount;
        // TODO: update outstandingPayment
    }

    private void updateLoan() {
        // PASS
    }

    /*
    Method to create:
        > Get interest rate
        > Transfer to user account
        > Update approval status
        > Update transfer status
        >
     */

    public Customer getCustomer() {
        return this.customer;
    }

    public Account getAccount() {
        return recipientAccount;
    }

    public String getReason() {
        return this.reason;
    }

    public double getAmount() {
        return amountLoaned;
    }

    public double getOutstandingPayments() {
        return outstandingPayments;
    }

    public boolean isAccepted(){
        if (this.approvalStatus == "accepted"){
            return true;
        } return false;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
