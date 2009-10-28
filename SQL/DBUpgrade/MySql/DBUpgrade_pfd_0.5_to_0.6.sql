#------ Add columns to participant----
alter table CATISSUE_PARTICIPANT add column FAMILY_NAME varchar(255);
alter table CATISSUE_PARTICIPANT add column BUSINESS_FIELD varchar(255);
Alter table CATISSUE_PARTICIPANT add (INSURANCE varchar(20));
Alter table CATISSUE_PARTICIPANT add (REF_BY varchar(50));
Alter table CATISSUE_PARTICIPANT add (CONTACT_NO varchar(50));
delete from CATISSUE_PERMISSIBLE_VALUE where identifier = 2641;
delete from CATISSUE_PERMISSIBLE_VALUE where IDENTIFIER = 60;
delete from CATISSUE_PERMISSIBLE_VALUE where IDENTIFIER = 2636;
alter table catissue_address modify STREET text;
Alter table CATISSUE_PARTICIPANT add (EMAIL_ADDRESS varchar(50));
Alter table CATISSUE_PARTICIPANT add (BLOOD_GROUP varchar(20));