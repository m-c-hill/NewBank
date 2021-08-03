package server.user;

import server.database.DbUtils;

/**
 * Class to represent the roles of an admin and the permissions that each role has access to
 */
public class AdminRole {
	private final String name;
	private final String description;
	private final boolean canViewUserInfo;
	private final boolean canViewUserStatement;
	private final boolean canOpenAccount;
	private final boolean canCloseAccount;
	private final boolean canViewLoanRequests;
	private final boolean canHandleLoanRequests;

	public AdminRole(String name, String description, boolean canViewUserInfo,
					 boolean canViewUserStatement, boolean canOpenAccount,
					 boolean canCloseAccount, boolean canViewLoanRequests, boolean canHandleLoanRequests) {
		this.name = name;
		this.description = description;
		this.canViewUserInfo = canViewUserInfo;
		this.canViewUserStatement = canViewUserStatement;
		this.canOpenAccount = canOpenAccount;
		this.canCloseAccount = canCloseAccount;
		this.canViewLoanRequests = canViewLoanRequests;
		this.canHandleLoanRequests = canHandleLoanRequests;

		// If the role does not currently exist, then store it in the database
		if(!DbUtils.checkAdminRoleExists(this.name));
			DbUtils.storeAdminRole(this.name,
					this.description,
					this.canViewUserInfo,
					this.canViewUserStatement,
					this.canOpenAccount,
					this.canCloseAccount,
					this.canViewLoanRequests,
					this.canHandleLoanRequests);
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

	public boolean canViewLoanRequests() {
		return this.canViewLoanRequests;
	}

	public boolean canHandleLoanRequests() {
		return this.canHandleLoanRequests;
	}
}
