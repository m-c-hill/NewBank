package server;

public class BankLoan {
    private Customer customer;
    private String reason;
    private boolean isAccepted;
    private boolean isPaidBack;

    public BankLoan(Customer customer, String reason){
        this.customer = customer;
        this.reason = reason;

        this.isAccepted = false;
        this.isPaidBack = false;
    }

    public Customer getCustomer() {
        return this.customer;
    }

    public String getReason() {
        return this.reason;
    }

    public boolean getIsAccepted(){
        return this.isAccepted;
    }

    public boolean getIsPaidBack(){
        return this.isPaidBack;
    }
    
}
