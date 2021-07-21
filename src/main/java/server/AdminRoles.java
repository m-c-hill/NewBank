package server;

public class AdminRoles {
    private boolean allowedToViewLoanList;
    private boolean allowedToGrantLoan;

    public AdminRoles(boolean canGrantLoan, boolean canViewLoans){
        this.allowedToViewLoanList = canViewLoans;
        this.allowedToGrantLoan = canGrantLoan;
    }

    public boolean isAllowedToViewLoanList() {
        return allowedToViewLoanList;
    }

    public boolean isAllowedToGrantLoan() {
        return allowedToGrantLoan;
    }

}
