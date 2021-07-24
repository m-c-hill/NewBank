package server;

public class AdminRole {
	private String name;
	private String description;
	private boolean canViewUserInfo = false;
	private boolean canViewUserStatement = false;
	private boolean canOpenAccount = false;
	private boolean canCloseAccount = false;
	private boolean canGrantLoan = false;

	public AdminRole(String name, String description, boolean canViewUserInfo,
					 boolean canViewUserStatement, boolean canOpenAccount,
					 boolean canCloseAccount, boolean canGrantLoan) {
		this.name = name;
		this.description = description;
		this.canViewUserInfo = canViewUserInfo;
		this.canViewUserStatement = canViewUserStatement;
		this.canOpenAccount = canOpenAccount;
		this.canCloseAccount = canCloseAccount;
		this.canGrantLoan = canGrantLoan;

		storeAdminRole();
	}

	// Default admin role to be used until database is up and running, then can be removed.
	public AdminRole(){
		this.name = "Loan manager";
		this.description = "Can grant loans, view user information";
		this.canViewUserInfo = true;
		this.canViewUserStatement = true;
		this.canGrantLoan = true;
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

	public boolean canGrantLoan(){
		return this.canGrantLoan;
	}

}
