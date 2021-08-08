-- Example SQL query to return the information and roles of each bank staff member

SELECT a.user_id,
       u.prefix,
       u.first_names,
       u.last_name,
       art.name,
       ad.address_line_1,
       ad.postcode,
       u.email_address,
       ar.expiration_date,
       art.can_view_user_info,
       art.can_view_user_statement,
       art.can_open_account,
       art.can_close_account,
       art.can_grant_loan
FROM admin a
LEFT JOIN user u ON u.user_id = a.user_id
LEFT JOIN address ad ON ad.address_id = u.address_id
LEFT JOIN admin_role ar ON ar.admin_id = a.admin_id
LEFT JOIN admin_role_type art ON art.admin_role_type_id = ar.admin_role_type_id;