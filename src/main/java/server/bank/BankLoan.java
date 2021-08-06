package server.bank;

import server.account.Account;
import server.user.Customer;

/**
 * Class to represent a loan provided to a customer through NewBank
 */
public class BankLoan {
    private Customer customer;
    private Account recipientAccount;

    private String reason;

    private double amount;
    private double payBackAmount;

    private boolean accepted;
    private boolean paidBack;
    private boolean checked;

    public BankLoan(Customer customer, Account account, String reason, double amount, double interestRate){
        this.customer = customer;
        this.recipientAccount = account;
        this.reason = reason;
        
        this.amount = amount;
        this.payBackAmount = this.amount + (this.amount * interestRate/100.00);

        this.checked = false;
        this.accepted = false;
        this.paidBack = false;
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

    public double getAmount() {
        return amount;
    }
    
    public double getPayBackAmount() {
        return payBackAmount;
    }

    public boolean isChecked() {
        return checked;
    }

    public boolean isAccepted(){
        return this.accepted;
    }

    public boolean isPaidBack() {
        return this.paidBack;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public void setPaidBack(boolean paidBack) {
        this.paidBack = paidBack;
    }
    
}
