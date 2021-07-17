-- Run to reset all database dummy data

DELETE FROM newbank.account;
DELETE FROM newbank.account_type;
DELETE FROM newbank.address;
DELETE FROM newbank.admin;
DELETE FROM newbank.admin_role;
DELETE FROM newbank.admin_role_type;
DELETE FROM newbank.balance;
DELETE FROM newbank.bank;
DELETE FROM newbank.currency;
DELETE FROM newbank.loans;
DELETE FROM newbank.password;
DELETE FROM newbank.transaction;
DELETE FROM newbank.transaction_type;
DELETE FROM newbank.transfer;
DELETE FROM newbank.user;

INSERT INTO newbank.account(account_number, bank_id, account_type_id)
VALUES
       ('08040302', 1, 1),
       ('07776191', 1, 1),
       ('39867532', 1, 1);

INSERT INTO newbank.account_type(name, description)
VALUES
       ('Regular', 'Standard account for new customer');

INSERT INTO newbank.address(address_num, address_line_1, address_line_2, city, region, postcode, country)
VALUES
       ('183', 'Liverpool Road', NULL, 'Slough', 'Surrey', 'SL1 4QZ', 'United Kingdom'),
       ('5', 'Appold Street', NULL, 'London', 'London', 'EC2A 2AG', 'United Kingdom'),
       ('108', 'Crown Street', NULL, 'London', 'London', 'SW5 7NL', 'United Kingdom'),
       ('27', 'Market Place', NULL, 'London', 'London', 'SE6 8SL', 'United Kingdom'),
       ('13', 'Sherburn Avenue', NULL, 'London', 'London', 'W1 8HB', 'United Kingdom'),
       ('1', 'Trafalgar Row', NULL, 'London', 'London', 'W2 3RR', 'United Kingdom');

INSERT INTO newbank.admin(user_id)
VALUES
       (3),
       (6);

INSERT INTO newbank.admin_role(admin_id, admin_role_type_id, expiration_date)
VALUES
       (3, 1, '2022-01-01'),
       (6, 2, '2021-12-01'),
       (7, 3, '2022-05-01');

INSERT INTO newbank.admin_role_type(name, description, can_view_user_info, can_view_user_statement, can_open_account, can_close_account, can_grant_loan)
VALUES
       ('account_admin', 'User can view account info', TRUE, TRUE, FALSE, FALSE, FALSE),
       ('bank_manager', 'User can open/close accounts and view account info',TRUE, TRUE,TRUE,TRUE, FALSE),
       ('loan_manager', 'User can approve/reject loan requests', TRUE, TRUE, TRUE, TRUE, TRUE);

INSERT INTO newbank.balance(account_id, currency_id, amount, primary_balance)
VALUES
       ('08040302', 1, 5100, TRUE),
       ('08040302', 2, 420, FALSE),
       ('07776191', 1, 2500, TRUE),
       ('39867532', 1, 600,TRUE),
       ('39867532', 4, 2, FALSE);

INSERT INTO newbank.bank(name, address_id, sort_code)
VALUES
       ('NewBank', 1, '010101'),
       ('Monzo', 2, '040004');

INSERT INTO newbank.currency(name, usd_exchange_rate, dt_updated, crypto, description)
VALUES
       ('gbp', 1.38, '2021-07-17', FALSE, 'Pound sterling'),
       ('usd', 1, '2021-07-17', FALSE, 'United States dollar'),
       ('euro', 1.18, '2021-07-17', FALSE, 'Euro'),
       ('btc', 31642.80, '2021-07-17', TRUE, 'Bitcoin');

INSERT INTO newbank.customer(user_id, account_id)
VALUES
       (1, 1),
       (2, 2),
       (4, 3);

INSERT INTO newbank.loans(customer_id, account_id, amount, currency_id, approval_status, transfer_status)
VALUES
       (1, '08040302', 10000, 1,'pending', 'pending');

INSERT INTO newbank.transaction_type(name)
VALUES
       ('withdraw'),
       ('deposit'),
       ('transfer'),
       ('payment');

INSERT INTO newbank.transfer(date, sender_account_id, recipient_account_id, amount, currency_id)
VALUES
       ('2021-07-15', '08040302', '07776191', 200, 1);

INSERT INTO newbank.user(prefix, first_names, last_name, address_id, date_of_birth, email_address, phone_number, national_insurance_number)
VALUES
       ('Mr', 'Mark', 'Corrigan', 3, '1970-06-02', 'mark.corrigan@gmail.com', '07654765122', 'LW028542A'),
       ('Mr', 'Jeremy', 'Usbourne', 3, '1971-03-22', 'jeremy.usbourne@gmail.com', '07676765738', 'EZ850996C'),
       ('Miss', 'Sophie', 'Chapman', 4, '1975-01-30', 'sophie.chapman@gmail.com', '07542346574', 'TW840676D'),
       ('Mr', 'Alan', 'Johnson', 5, '1978-12-12', 'alan.johnson@gmail.com', '07644348981', 'SH931726D'),
       ('Mrs', 'April', 'Danecroft', 6, '1980-06-02', 'april.danecroft@gmail.com', '07868676412', 'PY705411A'),
       ('Mr', 'Simon', 'Hans', 3, '1970-01-01', 'simon.hans@gmail.com', '07656574121', 'PA412954B');