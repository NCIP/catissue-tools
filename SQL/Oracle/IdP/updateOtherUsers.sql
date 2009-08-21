update catissue_user set login_name = concat(login_name , '1'), EMAIL_ADDRESS = concat(EMAIL_ADDRESS , 'm'),FIRST_NAME=concat(FIRST_NAME , '1') ,LAST_NAME=concat(LAST_NAME , '1') where identifier <> 1 ;

