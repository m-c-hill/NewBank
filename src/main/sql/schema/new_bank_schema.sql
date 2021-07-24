-- Warning: only run this for a hard reset of the production database.
DROP SCHEMA IF EXISTS newbank;
CREATE SCHEMA IF NOT EXISTS newbank;

USE newbank;

-- Create users table
-- Description: user information, to be inherited by customer or admin
CREATE TABLE IF NOT EXISTS user (
    user_id int PRIMARY KEY AUTO_INCREMENT,
    prefix varchar(255),
    first_names varchar(255),
    last_name varchar(255),
    address_id int REFERENCES address(address_id),
    date_of_birth datetime,
    email_address varchar(255),
    phone_number varchar(255),
    national_insurance_number varchar(255),
    login_id varchar(255)
);

-- Create customer table
-- Description: bank customer information - each customer can has one account associated with them
CREATE TABLE IF NOT EXISTS customer (
    customer_id int PRIMARY KEY AUTO_INCREMENT,
    user_id int REFERENCES user(user_id),
    account_number varchar(8) REFERENCES account(account_number)
);

-- Create password table
-- Description: user login information - salt and hash are used to validate an encrypted plain text password
CREATE TABLE IF NOT EXISTS password (
    user_id int REFERENCES user(user_id),
    login varchar(255),
    pw_salt varchar(255),
    pw_hash varchar(255)
);

-- Create account table
-- Description: account information, linked to a specific bank and given an account type
CREATE TABLE IF NOT EXISTS account (
    account_number varchar(8) PRIMARY KEY,
    bank_id int REFERENCES bank(bank_id),
    account_type_id int REFERENCES account_type(account_type_id),
    statement_schedule ENUM('weekly', 'biweekly', 'monthly') DEFAULT 'monthly'
);

-- Create account types table
-- Description: account types
CREATE TABLE IF NOT EXISTS account_type (
    account_type_id int PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255),
    description VARCHAR(255)
);

-- Create admin table
-- Description: bank employee information
CREATE TABLE IF NOT EXISTS admin (
    admin_id int PRIMARY KEY AUTO_INCREMENT,
    user_id int REFERENCES user(user_id)
);

-- Create admin roles table
-- Description: admin role instances assigned to each bank employee
CREATE TABLE IF NOT EXISTS admin_role (
    admin_id int REFERENCES admin(admin_id),
    admin_role_type_id int REFERENCES admin_role_type(admin_role_type_id),
    expiration_date datetime
);

-- Create admin role types table
-- Description: admin role definitions with associated permissions - example roles could include bank administrator, loan manager
CREATE TABLE IF NOT EXISTS admin_role_type (
  admin_role_type_id int PRIMARY KEY AUTO_INCREMENT,
  name varchar(255),
  description varchar(255),
  can_view_user_info boolean DEFAULT false,
  can_view_user_statement boolean DEFAULT false,
  can_open_account boolean DEFAULT false,
  can_close_account boolean DEFAULT false,
  can_grant_loan boolean DEFAULT false
);

-- Create bank table
-- Description: bank branch information including address and sort code
CREATE TABLE IF NOT EXISTS bank (
    bank_id int PRIMARY KEY AUTO_INCREMENT,
    name varchar(255),
    address_id int REFERENCES address(address_id),
    sort_code varchar(255)
);

-- Create currency table
-- Description: currency information,
CREATE TABLE IF NOT EXISTS currency (
    currency_id varchar(255) PRIMARY KEY,
    usd_exchange_rate double,
    dt_updated datetime,
    crypto boolean,
    description varchar(255)
);

-- Create address table
-- Description: address information
CREATE TABLE IF NOT EXISTS address (
    address_id int PRIMARY KEY AUTO_INCREMENT,
    address_num varchar(10),
    address_line_1 varchar(255),
    address_line_2 varchar(255),
    city varchar(255),
    region varchar(255),
    postcode varchar(255),
    country varchar(255)
);

-- Create balance table
-- Description: balance object - each account can have several "balances" associated with it. This enables accounts
-- to store multiple currencies in different 'pots'. All transactions will take place on the default balance (determined
-- by the primary_balance boolean), unless otherwise specified.
CREATE TABLE IF NOT EXISTS balance (
    balance_id int PRIMARY KEY AUTO_INCREMENT,
    account_number varchar(8) REFERENCES account(account_number),
    currency_id varchar(255) REFERENCES currency(currency_id),
    amount double,
    primary_balance boolean
);

-- Create transaction types table
-- Description: transaction type information (ie. withdraw/deposit cash to the account, transfer money to/from an
-- account, or make a payment
CREATE TABLE IF NOT EXISTS transaction_type (
    transaction_type_id int PRIMARY KEY AUTO_INCREMENT,
    name ENUM('withdraw', 'deposit', 'transfer', 'payment')
);

-- Create transaction table
-- Description: complete log of all transactions across all accounts
CREATE TABLE IF NOT EXISTS transaction (
    transaction_id int PRIMARY KEY AUTO_INCREMENT,
    account_number varchar(8) REFERENCES account(account_number),
    transaction_type_id int REFERENCES transaction_type(transaction_type_id),
    date timestamp,
    payee varchar(255),
    amount double,
    currency_id varchar(255) REFERENCES currency(currency_id)
);

-- Create transfer table
-- Description: complete log of all transfers between existing accounts
CREATE TABLE IF NOT EXISTS transfer (
    transfer_id int PRIMARY KEY AUTO_INCREMENT,
    date timestamp,
    sender_account_number varchar(8) REFERENCES account(account_number),
    recipient_account_number varchar(8) REFERENCES account(account_number),
    amount double,
    currency_id varchar(255) REFERENCES currency(currency_id)
);

-- Create loans table
-- Description: loan information, tracking loans requests and whether they have been approved/received.
CREATE TABLE IF NOT EXISTS loans (
    loan_id int PRIMARY KEY AUTO_INCREMENT,
    customer_id int REFERENCES customer(customer_id),
    account_number int REFERENCES account(account_number),
    amount double,
    currency_id varchar(255) REFERENCES currency(currency_id),
    approval_status ENUM('pending', 'approved', 'declined') DEFAULT 'pending',
    transfer_status ENUM('pending', 'received') DEFAULT 'pending'
);

SHOW TABLES;
