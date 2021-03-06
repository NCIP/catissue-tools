INSERT INTO CSM_ROLE (ROLE_ID,ROLE_NAME,ROLE_DESCRIPTION,APPLICATION_ID,ACTIVE_FLAG,UPDATE_DATE) VALUES (10,'READ_DENIED','Read Denied Role',1,0,to_date('0001-01-01','yyyy-mm-dd'));
INSERT INTO CSM_PRIVILEGE (PRIVILEGE_ID,PRIVILEGE_NAME,PRIVILEGE_DESCRIPTION,UPDATE_DATE) VALUES (12,'READ_DENIED','This privilege doesnt permit the user to read data',to_date('2005-08-22','yyyy-mm-dd'));
INSERT INTO CSM_ROLE_PRIVILEGE (ROLE_PRIVILEGE_ID,ROLE_ID,PRIVILEGE_ID,UPDATE_DATE) VALUES (27,10,12,to_date('0001-01-01','yyyy-mm-dd'));

/* Gautam : Added for allowing supervisor to see identified data.*/
INSERT INTO `CSM_USER_GROUP_ROLE_PG` (`USER_GROUP_ROLE_PG_ID`,`USER_ID`,`GROUP_ID`,`ROLE_ID`,`PROTECTION_GROUP_ID`,`UPDATE_DATE`) VALUES (74,NULL,2,2,20,to_date('2006-01-18','yyyy-mm-dd'));