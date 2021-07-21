package server;

public class AdminRoles {
    private boolean allowedToViewLoanList;
    private boolean allowedToGrantLoan;

    public AdminRoles(boolean allowedToViewLoanList, boolean allowedToGrantLoan){
        this.allowedToViewLoanList = allowedToViewLoanList;
        this.allowedToGrantLoan = allowedToGrantLoan;
    }

    public boolean isAllowedToViewLoanList() {
        return allowedToViewLoanList;
    }

    public boolean isAllowedToGrantLoan() {
        return allowedToGrantLoan;
    }

}
