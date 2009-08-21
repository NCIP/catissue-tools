



/* Table Objects for table csm_application*/



DROP TABLE IF EXISTS `CSM_APPLICATION`;



CREATE TABLE `CSM_APPLICATION` (

  `APPLICATION_ID` bigint(20) NOT NULL auto_increment,

  `APPLICATION_NAME` varchar(100) NOT NULL default '',

  `APPLICATION_DESCRIPTION` varchar(200) NOT NULL default '',

  `DECLARATIVE_FLAG` tinyint(1) default NULL,

  `ACTIVE_FLAG` tinyint(1) NOT NULL default '0',

  `UPDATE_DATE` date NOT NULL default '0000-00-00',

  PRIMARY KEY  (`APPLICATION_ID`),

  UNIQUE KEY `UQ_APPLICATION_NAME` (`APPLICATION_NAME`)

) ENGINE=InnoDB DEFAULT CHARSET=latin1;





DROP TABLE IF EXISTS `CSM_GROUP`;



CREATE TABLE `CSM_GROUP` (

  `GROUP_ID` bigint(20) NOT NULL auto_increment,

  `GROUP_NAME` varchar(100) NOT NULL default '',

  `GROUP_DESC` varchar(200) default NULL,

  `UPDATE_DATE` date NOT NULL default '0000-00-00',

  `APPLICATION_ID` bigint(20) NOT NULL default '0',

  PRIMARY KEY  (`GROUP_ID`),

  UNIQUE KEY `UQ_GROUP_GROUP_NAME` (`APPLICATION_ID`,`GROUP_NAME`),

  KEY `idx_APPLICATION_ID` (`APPLICATION_ID`)

) ENGINE=InnoDB DEFAULT CHARSET=latin1;





/* Table Objects for table csm_pg_pe*/



DROP TABLE IF EXISTS `CSM_PG_PE`;



CREATE TABLE `CSM_PG_PE` (

  `PG_PE_ID` bigint(20) NOT NULL auto_increment,

  `PROTECTION_GROUP_ID` bigint(20) NOT NULL default '0',

  `PROTECTION_ELEMENT_ID` bigint(20) NOT NULL default '0',

  `UPDATE_DATE` date NOT NULL default '0000-00-00',

  PRIMARY KEY  (`PG_PE_ID`),

  UNIQUE KEY `UQ_PROTECTION_GROUP_PROTECTION_ELEMENT_PROTECTION_GROUP_ID` (`PROTECTION_ELEMENT_ID`,`PROTECTION_GROUP_ID`),

  KEY `idx_PROTECTION_ELEMENT_ID` (`PROTECTION_ELEMENT_ID`),

  KEY `idx_PROTECTION_GROUP_ID` (`PROTECTION_GROUP_ID`)

) ENGINE=InnoDB DEFAULT CHARSET=latin1;





/* Table Objects for table csm_privilege*/



DROP TABLE IF EXISTS `CSM_PRIVILEGE`;



CREATE TABLE `CSM_PRIVILEGE` (

  `PRIVILEGE_ID` bigint(20) NOT NULL auto_increment,

  `PRIVILEGE_NAME` varchar(100) NOT NULL default '',

  `PRIVILEGE_DESCRIPTION` varchar(200) default NULL,

  `UPDATE_DATE` date NOT NULL default '0000-00-00',

  PRIMARY KEY  (`PRIVILEGE_ID`),

  UNIQUE KEY `UQ_PRIVILEGE_NAME` (`PRIVILEGE_NAME`)

) ENGINE=InnoDB DEFAULT CHARSET=latin1;


INSERT INTO `CSM_PRIVILEGE` (`PRIVILEGE_ID`,`PRIVILEGE_NAME`,`PRIVILEGE_DESCRIPTION`,`UPDATE_DATE`) VALUES (1,'CREATE','This privilege grants permission to a user to create an entity. This entity can be an object, a database entry, or a resource such as a network connection','2005-08-22');
INSERT INTO `CSM_PRIVILEGE` (`PRIVILEGE_ID`,`PRIVILEGE_NAME`,`PRIVILEGE_DESCRIPTION`,`UPDATE_DATE`) VALUES (2,'ACCESS','This privilege allows a user to access a particular resource.  Examples of resources include a network or database connection, socket, module of the application, or even the application itself','2005-08-22');
INSERT INTO `CSM_PRIVILEGE` (`PRIVILEGE_ID`,`PRIVILEGE_NAME`,`PRIVILEGE_DESCRIPTION`,`UPDATE_DATE`) VALUES (3,'READ','This privilege permits the user to read data from a file, URL, database, an object, etc. This can be used at an entity level signifying that the user is allowed to read data about a particular entry','2005-08-22');
INSERT INTO `CSM_PRIVILEGE` (`PRIVILEGE_ID`,`PRIVILEGE_NAME`,`PRIVILEGE_DESCRIPTION`,`UPDATE_DATE`) VALUES (4,'WRITE','This privilege allows a user to write data to a file, URL, database, an object, etc. This can be used at an entity level signifying that the user is allowed to write data about a particular entity','2005-08-22');
INSERT INTO `CSM_PRIVILEGE` (`PRIVILEGE_ID`,`PRIVILEGE_NAME`,`PRIVILEGE_DESCRIPTION`,`UPDATE_DATE`) VALUES (5,'UPDATE','This privilege grants permission at an entity level and signifies that the user is allowed to update data for a particular entity. Entities may include an object, object attribute, database row etc','2005-08-22');
INSERT INTO `CSM_PRIVILEGE` (`PRIVILEGE_ID`,`PRIVILEGE_NAME`,`PRIVILEGE_DESCRIPTION`,`UPDATE_DATE`) VALUES (6,'DELETE','This privilege permits a user to delete a logical entity. This entity can be an object, a database entry, a resource such as a network connection, etc','2005-08-22');
INSERT INTO `CSM_PRIVILEGE` (`PRIVILEGE_ID`,`PRIVILEGE_NAME`,`PRIVILEGE_DESCRIPTION`,`UPDATE_DATE`) VALUES (7,'EXECUTE','This privilege allows a user to execute a particular resource. The resource can be a method, function, behavior of the application, URL, button etc','2005-08-22');
INSERT INTO `CSM_PRIVILEGE` (`PRIVILEGE_ID`,`PRIVILEGE_NAME`,`PRIVILEGE_DESCRIPTION`,`UPDATE_DATE`) VALUES (8,'USE','This privilege allows a user to use a particular resource','2005-08-22');
INSERT INTO `CSM_PRIVILEGE` (`PRIVILEGE_ID`,`PRIVILEGE_NAME`,`PRIVILEGE_DESCRIPTION`,`UPDATE_DATE`) VALUES (9,'ASSIGN_READ','This privilege allows a user to assign a read privilege to others','2005-08-22');
INSERT INTO `CSM_PRIVILEGE` (`PRIVILEGE_ID`,`PRIVILEGE_NAME`,`PRIVILEGE_DESCRIPTION`,`UPDATE_DATE`) VALUES (10,'ASSIGN_USE','This privilege allows a user to assign a use privilege to others','2005-08-22');
INSERT INTO `CSM_PRIVILEGE` (`PRIVILEGE_ID`,`PRIVILEGE_NAME`,`PRIVILEGE_DESCRIPTION`,`UPDATE_DATE`) VALUES (11,'PHI_ACCESS','This privilege allows a user to view identified data of an object','2005-08-22');

/*Gautam : Added a READ_DENIED privilege.*/

INSERT INTO `CSM_PRIVILEGE` (`PRIVILEGE_ID`,`PRIVILEGE_NAME`,`PRIVILEGE_DESCRIPTION`,`UPDATE_DATE`) VALUES (12,'READ_DENIED','This privilege doesnt permit the user to read data','2005-08-22');



/* Table Objects for table csm_protection_element*/

DROP TABLE IF EXISTS `CSM_PROTECTION_ELEMENT`;

CREATE TABLE `CSM_PROTECTION_ELEMENT` (

  `PROTECTION_ELEMENT_ID` bigint(20) NOT NULL auto_increment,

  `PROTECTION_ELEMENT_NAME` varchar(100) NOT NULL default '',

  `PROTECTION_ELEMENT_DESCRIPTION` varchar(200) default NULL,

  `OBJECT_ID` varchar(100) NOT NULL default '',

  `ATTRIBUTE` varchar(100) default NULL,

  `PROTECTION_ELEMENT_TYPE_ID` decimal(10,0) default NULL,

  `APPLICATION_ID` bigint(20) NOT NULL default '0',

  `UPDATE_DATE` date NOT NULL default '0000-00-00',

  PRIMARY KEY  (`PROTECTION_ELEMENT_ID`),

  UNIQUE KEY `UQ_PE_PE_NAME_ATTRIBUTE_APP_ID` (`OBJECT_ID`,`ATTRIBUTE`,`APPLICATION_ID`),

  KEY `idx_APPLICATION_ID` (`APPLICATION_ID`)

) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `CSM_PROTECTION_GROUP`;

CREATE TABLE `CSM_PROTECTION_GROUP` (

  `PROTECTION_GROUP_ID` bigint(20) NOT NULL auto_increment,

  `PROTECTION_GROUP_NAME` varchar(100) NOT NULL default '',

  `PROTECTION_GROUP_DESCRIPTION` varchar(200) default NULL,

  `APPLICATION_ID` bigint(20) NOT NULL default '0',

  `LARGE_ELEMENT_COUNT_FLAG` tinyint(1) NOT NULL default '0',

  `UPDATE_DATE` date NOT NULL default '0000-00-00',

  `PARENT_PROTECTION_GROUP_ID` bigint(20) default NULL,

  PRIMARY KEY  (`PROTECTION_GROUP_ID`),

  UNIQUE KEY `UQ_PROTECTION_GROUP_PROTECTION_GROUP_NAME` (`APPLICATION_ID`,`PROTECTION_GROUP_NAME`),

  KEY `idx_APPLICATION_ID` (`APPLICATION_ID`),

  KEY `idx_PARENT_PROTECTION_GROUP_ID` (`PARENT_PROTECTION_GROUP_ID`)

) ENGINE=InnoDB DEFAULT CHARSET=latin1;





/* Table Objects for table csm_role*/


DROP TABLE IF EXISTS `CSM_ROLE`;



CREATE TABLE `CSM_ROLE` (

  `ROLE_ID` bigint(20) NOT NULL auto_increment,

  `ROLE_NAME` varchar(100) NOT NULL default '',

  `ROLE_DESCRIPTION` varchar(200) default NULL,

  `APPLICATION_ID` bigint(20) NOT NULL default '0',

  `ACTIVE_FLAG` tinyint(1) NOT NULL default '0',

  `UPDATE_DATE` date NOT NULL default '0000-00-00',

  PRIMARY KEY  (`ROLE_ID`),

  UNIQUE KEY `UQ_ROLE_ROLE_NAME` (`APPLICATION_ID`,`ROLE_NAME`),

  KEY `idx_APPLICATION_ID` (`APPLICATION_ID`)

) ENGINE=InnoDB DEFAULT CHARSET=latin1;



/* Table Objects for table csm_role_privilege*/

DROP TABLE IF EXISTS `CSM_ROLE_PRIVILEGE`;

CREATE TABLE `CSM_ROLE_PRIVILEGE` (

  `ROLE_PRIVILEGE_ID` bigint(20) NOT NULL auto_increment,

  `ROLE_ID` bigint(20) NOT NULL default '0',

  `PRIVILEGE_ID` bigint(20) NOT NULL default '0',

  `UPDATE_DATE` date NOT NULL default '0000-00-00',

  PRIMARY KEY  (`ROLE_PRIVILEGE_ID`),

  UNIQUE KEY `UQ_ROLE_PRIVILEGE_ROLE_ID` (`PRIVILEGE_ID`,`ROLE_ID`),

  KEY `idx_PRIVILEGE_ID` (`PRIVILEGE_ID`),

  KEY `idx_ROLE_ID` (`ROLE_ID`)

) ENGINE=InnoDB DEFAULT CHARSET=latin1;



/* Table Objects for table csm_user*/

DROP TABLE IF EXISTS `CSM_USER`;

CREATE TABLE `CSM_USER` (

  `USER_ID` bigint(20) NOT NULL auto_increment,

  `LOGIN_NAME` varchar(100) NOT NULL default '',

  `FIRST_NAME` varchar(100) NOT NULL default '',

  `LAST_NAME` varchar(100) NOT NULL default '',

  `ORGANIZATION` varchar(100) default NULL,

  `DEPARTMENT` varchar(100) default NULL,

  `TITLE` varchar(100) default NULL,

  `PHONE_NUMBER` varchar(15) default NULL,

  `PASSWORD` varchar(100) default NULL,

  `EMAIL_ID` varchar(100) default NULL,

  `START_DATE` date default NULL,

  `END_DATE` date default NULL,

  `UPDATE_DATE` date NOT NULL default '0000-00-00',

  PRIMARY KEY  (`USER_ID`),

  UNIQUE KEY `UQ_LOGIN_NAME` (`LOGIN_NAME`)

) ENGINE=InnoDB DEFAULT CHARSET=latin1;



/* Table Objects for table csm_user_group*/


DROP TABLE IF EXISTS `CSM_USER_GROUP`;

CREATE TABLE `CSM_USER_GROUP` (

  `USER_GROUP_ID` bigint(20) NOT NULL auto_increment,

  `USER_ID` bigint(20) NOT NULL default '0',

  `GROUP_ID` bigint(20) NOT NULL default '0',

  PRIMARY KEY  (`USER_GROUP_ID`),

  KEY `idx_USER_ID` (`USER_ID`),

  KEY `idx_GROUP_ID` (`GROUP_ID`)

) ENGINE=InnoDB DEFAULT CHARSET=latin1;


/* Table Objects for table csm_user_group_role_pg*/


DROP TABLE IF EXISTS `CSM_USER_GROUP_ROLE_PG`;

CREATE TABLE `CSM_USER_GROUP_ROLE_PG` (

  `USER_GROUP_ROLE_PG_ID` bigint(20) NOT NULL auto_increment,

  `USER_ID` bigint(20) default NULL,

  `GROUP_ID` bigint(20) default NULL,

  `ROLE_ID` bigint(20) NOT NULL default '0',

  `PROTECTION_GROUP_ID` bigint(20) NOT NULL default '0',

  `UPDATE_DATE` date NOT NULL default '0000-00-00',

  PRIMARY KEY  (`USER_GROUP_ROLE_PG_ID`),

  KEY `idx_GROUP_ID` (`GROUP_ID`),

  KEY `idx_ROLE_ID` (`ROLE_ID`),

  KEY `idx_PROTECTION_GROUP_ID` (`PROTECTION_GROUP_ID`),

  KEY `idx_USER_ID` (`USER_ID`)

) ENGINE=InnoDB DEFAULT CHARSET=latin1;



/* Table Objects for table csm_user_pe*/

DROP TABLE IF EXISTS `CSM_USER_PE`;

CREATE TABLE `CSM_USER_PE` (

  `USER_PROTECTION_ELEMENT_ID` bigint(20) NOT NULL auto_increment,

  `PROTECTION_ELEMENT_ID` bigint(20) NOT NULL default '0',

  `USER_ID` bigint(20) NOT NULL default '0',

  `UPDATE_DATE` date NOT NULL default '0000-00-00',

  PRIMARY KEY  (`USER_PROTECTION_ELEMENT_ID`),

  UNIQUE KEY `UQ_USER_PROTECTION_ELEMENT_PROTECTION_ELEMENT_ID` (`USER_ID`,`PROTECTION_ELEMENT_ID`),

  KEY `idx_USER_ID` (`USER_ID`),

  KEY `idx_PROTECTION_ELEMENT_ID` (`PROTECTION_ELEMENT_ID`)

) ENGINE=InnoDB DEFAULT CHARSET=latin1;



/*  Foreign keys for table csm_group*/


ALTER TABLE `CSM_GROUP`

  ADD FOREIGN KEY (`APPLICATION_ID`) REFERENCES `CSM_APPLICATION` (`APPLICATION_ID`) ON DELETE CASCADE;


/*  Foreign keys for table csm_pg_pe*/


ALTER TABLE `CSM_PG_PE`

  ADD FOREIGN KEY (`PROTECTION_ELEMENT_ID`) REFERENCES `CSM_PROTECTION_ELEMENT` (`PROTECTION_ELEMENT_ID`) ON DELETE CASCADE,

  ADD FOREIGN KEY (`PROTECTION_GROUP_ID`) REFERENCES `CSM_PROTECTION_GROUP` (`PROTECTION_GROUP_ID`) ON DELETE CASCADE;


/*  Foreign keys for table csm_protection_element*/


ALTER TABLE `CSM_PROTECTION_ELEMENT`

  ADD FOREIGN KEY (`APPLICATION_ID`) REFERENCES `CSM_APPLICATION` (`APPLICATION_ID`) ON DELETE CASCADE;


/*  Foreign keys for table csm_protection_group*/


ALTER TABLE `CSM_PROTECTION_GROUP`

  ADD FOREIGN KEY (`APPLICATION_ID`) REFERENCES `CSM_APPLICATION` (`APPLICATION_ID`) ON DELETE CASCADE,

  ADD FOREIGN KEY (`PARENT_PROTECTION_GROUP_ID`) REFERENCES `CSM_PROTECTION_GROUP` (`PROTECTION_GROUP_ID`);


/*  Foreign keys for table csm_role*/


ALTER TABLE `CSM_ROLE`

  ADD FOREIGN KEY (`APPLICATION_ID`) REFERENCES `CSM_APPLICATION` (`APPLICATION_ID`) ON DELETE CASCADE;


/*  Foreign keys for table csm_role_privilege */


ALTER TABLE `CSM_ROLE_PRIVILEGE`

  ADD FOREIGN KEY (`PRIVILEGE_ID`) REFERENCES `CSM_PRIVILEGE` (`PRIVILEGE_ID`) ON DELETE CASCADE,

  ADD FOREIGN KEY (`ROLE_ID`) REFERENCES `CSM_ROLE` (`ROLE_ID`) ON DELETE CASCADE;



/*  Foreign keys for table csm_user_group */


ALTER TABLE `CSM_USER_GROUP`

  ADD FOREIGN KEY (`GROUP_ID`) REFERENCES `CSM_GROUP` (`GROUP_ID`) ON DELETE CASCADE,

  ADD FOREIGN KEY (`USER_ID`) REFERENCES `CSM_USER` (`USER_ID`) ON DELETE CASCADE;


/*  Foreign keys for table csm_user_group_role_pg */


ALTER TABLE `CSM_USER_GROUP_ROLE_PG`

  ADD FOREIGN KEY (`GROUP_ID`) REFERENCES `CSM_GROUP` (`GROUP_ID`) ON DELETE CASCADE,

  ADD FOREIGN KEY (`PROTECTION_GROUP_ID`) REFERENCES `CSM_PROTECTION_GROUP` (`PROTECTION_GROUP_ID`) ON DELETE CASCADE,

  ADD FOREIGN KEY (`ROLE_ID`) REFERENCES `CSM_ROLE` (`ROLE_ID`) ON DELETE CASCADE,

  ADD FOREIGN KEY (`USER_ID`) REFERENCES `CSM_USER` (`USER_ID`) ON DELETE CASCADE;



/*  Foreign keys for table csm_user_pe */

ALTER TABLE `CSM_USER_PE`

  ADD FOREIGN KEY (`USER_ID`) REFERENCES `CSM_USER` (`USER_ID`) ON DELETE CASCADE,

  ADD FOREIGN KEY (`PROTECTION_ELEMENT_ID`) REFERENCES `CSM_PROTECTION_ELEMENT` (`PROTECTION_ELEMENT_ID`) ON DELETE CASCADE;

