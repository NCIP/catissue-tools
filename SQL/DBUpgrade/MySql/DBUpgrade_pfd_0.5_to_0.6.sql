#------ Add columns to participant----
alter table CATISSUE_PARTICIPANT add column FAMILY_NAME varchar(255);
alter table CATISSUE_PARTICIPANT add column BUSINESS_FIELD varchar(255);