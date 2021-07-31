package server;

public class AdminRole {
	private String name;
	private String description;
	private boolean canViewUserInfo = false;
	private boolean canViewUserStatement = false;
	private boolean canOpenAccount = false;
	private boolean canCloseAccount = false;
	private boolean allowedToViewLoanRequests = false;
	private boolean allowedToHandleLoanRequests = false;

	public AdminRole(String name, String description, boolean canViewUserInfo,
					 boolean canViewUserStatement, boolean canOpenAccount,
					 boolean canCloseAccount, boolean allowedToHandleLoanRequest, boolean allowedToViewLoanRequests) {
		this.name = name;
		this.description = description;
		this.canViewUserInfo = canViewUserInfo;
		this.canViewUserStatement = canViewUserStatement;
		this.canOpenAccount = canOpenAccount;
		this.canCloseAccount = canCloseAccount;
		this.allowedToHandleLoanRequests = allowedToHandleLoanRequest;
		this.allowedToViewLoanRequests = allowedToViewLoanRequests;

		storeAdminRole();
	}

	// Default admin role to be used until database is up and running, then can be removed.
	public AdminRole(){
		this.name = "Loan manager";
		this.description = "Can grant loans, view user information";
		this.canViewUserInfo = true;
		this.canViewUserStatement = true;
		this.allowedToHandleLoanRequests = true;
	}

	private void storeAdminRole(){
		// TODO: create method to add admin role to the database
	}

	public boolean canViewUserInfo(){
		return this.canViewUserInfo;
	}

	public boolean canViewUserStatement(){
		return this.canViewUserStatement;
	}

	public boolean canOpenAccount(){
		return this.canOpenAccount;
	}

	public boolean canCloseAccount(){
		return this.canCloseAccount;
	}

	public boolean isAllowedToViewLoanRequests() {
		return allowedToViewLoanRequests;
	}

	public boolean isAllowedToHandleLoanRequests() {
		return allowedToHandleLoanRequests;
	}

}
