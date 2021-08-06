package server.bank;

import server.account.Account;
import server.account.Currency;
import server.database.DbUtils;
import server.user.Customer;

/**
 * Class to represent a loan provided to a customer through NewBank
 */
public class BankLoan {
    private final Customer customer;
    private final Account recipientAccount;
    private final double amountLoaned;
    private final double payBackAmount;
    private final double interestRate;
    private final Currency currency;
    private final String reason;
    private boolean accepted;
    private boolean approvalStatus = false;
    private boolean transferStatus = false;

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
        this.payBackAmount = this.amountLoaned * (1 + interestRate/100.00);
        this.reason = reason;

        //TODO: write storeLoan method in DbUtils
        DbUtils.storeLoan(
                this.customer,
                this.recipientAccount,
                this.amountLoaned,
                this.amountDue,
                this.currency,
                this.reason,
                this.interestRate,
                this.approvalStatus,
                this.transferStatus
        );
    }

    /**
     * Constructor to
     * @return
     */


    public String getReason() {
        return this.reason;
    }

    public double getAmount() {
        return amountLoaned;
    }

    public double getPayBackAmount() {
        return payBackAmount;
    }

    public boolean isAccepted(){
        return this.accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
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
}
