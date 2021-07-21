package server;

public class BankLoan {
    private Customer customer;
    private String reason;

    private double amount;
    private double payBackAmount;

    private boolean accepted;
    private boolean paidBack;

    private final double rate = 2.56;

    public BankLoan(Customer customer, String reason, double amount){
        this.customer = customer;
        this.reason = reason;
        
        this.amount = amount;
        this.payBackAmount = this.amount * rate/100.00;

        this.accepted = false;
        this.paidBack = false;
    }

    public Customer getCustomer() {
        return this.customer;
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

    public boolean isAccepted(){
        return this.accepted;
    }

    public boolean isPaidBack() {
        return this.paidBack;
    }
    
}
