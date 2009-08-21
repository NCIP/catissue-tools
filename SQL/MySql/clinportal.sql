create table CATISSUE_PERMISSIBLE_VALUE (
   IDENTIFIER bigint not null auto_increment,
   CONCEPT_CODE varchar(40),
   DEFINITION text,
   PARENT_IDENTIFIER bigint,
   VALUE varchar(225),
   PUBLIC_ID varchar(30),
   primary key (IDENTIFIER)
);
create table CATISSUE_CDE (
   PUBLIC_ID varchar(30) not null,
   LONG_NAME varchar(200),
   DEFINITION text,
   VERSION varchar(50),
   LAST_UPDATED date,
   primary key (PUBLIC_ID)
);

create table CATISSUE_AUDIT_EVENT (
   IDENTIFIER bigint not null auto_increment,
   IP_ADDRESS varchar(20),
   EVENT_TIMESTAMP datetime,
   USER_ID bigint,
   COMMENTS text,
   primary key (IDENTIFIER)
);

create table CATISSUE_AUDIT_EVENT_LOG (
   IDENTIFIER bigint not null auto_increment,
   OBJECT_IDENTIFIER bigint,
   OBJECT_NAME varchar(50),
   EVENT_TYPE varchar(50),
   AUDIT_EVENT_ID bigint,
   primary key (IDENTIFIER)
);
create table CATISSUE_AUDIT_EVENT_DETAILS (
   IDENTIFIER bigint not null auto_increment,
   ELEMENT_NAME varchar(150),
   PREVIOUS_VALUE varchar(150),
   CURRENT_VALUE varchar(500),
   AUDIT_EVENT_LOG_ID bigint,
   primary key (IDENTIFIER)
);
create table CATISSUE_COLL_COORDINATORS (
   COLLECTION_PROTOCOL_ID bigint not null,
   USER_ID bigint not null,
   primary key (COLLECTION_PROTOCOL_ID, USER_ID)
);
create table CATISSUE_CANCER_RESEARCH_GROUP (
   IDENTIFIER bigint not null auto_increment,
   NAME varchar(255) not null unique,
   primary key (IDENTIFIER)
);

create table CATISSUE_PASSWORD (
   IDENTIFIER bigint not null auto_increment,
   PASSWORD varchar(255),
   UPDATE_DATE date,
   USER_ID bigint,
   primary key (IDENTIFIER)
);

create table CATISSUE_SITE (
   IDENTIFIER bigint not null auto_increment,
   NAME varchar(255) not null unique,
   TYPE varchar(50),
   EMAIL_ADDRESS varchar(255),
   USER_ID bigint,
   ACTIVITY_STATUS varchar(50),
   ADDRESS_ID bigint,
   primary key (IDENTIFIER)
);
create table CATISSUE_INSTITUTION (
   IDENTIFIER bigint not null auto_increment,
   NAME varchar(255) not null unique,
   primary key (IDENTIFIER)
);

create table CATISSUE_SPECIMEN_PROTOCOL (
   IDENTIFIER bigint not null auto_increment,
   PRINCIPAL_INVESTIGATOR_ID bigint,
   TITLE varchar(255) not null unique,
   SHORT_TITLE varchar(255),
   IRB_IDENTIFIER varchar(255),
   START_DATE date,
   END_DATE date,
   ENROLLMENT integer,
   DESCRIPTION_URL varchar(255),
   ACTIVITY_STATUS varchar(50),
   primary key (IDENTIFIER)
);

create table CATISSUE_RACE (
   IDENTIFIER bigint not null auto_increment,
   RACE_NAME varchar(50),
   PARTICIPANT_ID bigint,
   primary key (IDENTIFIER)
);

create table CATISSUE_ADDRESS (
   IDENTIFIER bigint not null auto_increment,
   STREET varchar(255),
   CITY varchar(50),
   STATE varchar(50),
   COUNTRY varchar(50),
   ZIPCODE varchar(30),
   PHONE_NUMBER varchar(50),
   FAX_NUMBER varchar(50),
   primary key (IDENTIFIER)
);
create table CATISSUE_REPORTED_PROBLEM (
   IDENTIFIER bigint not null auto_increment,
   AFFILIATION varchar(255) not null,
   NAME_OF_REPORTER varchar(255) not null,
   REPORTERS_EMAIL_ID varchar(255) not null,
   MESSAGE_BODY varchar(500) not null,
   SUBJECT varchar(255),
   REPORTED_DATE date,
   ACTIVITY_STATUS varchar(100),
   COMMENTS text,
   primary key (IDENTIFIER)
);
create table CATISSUE_PARTICIPANT (
   IDENTIFIER bigint not null auto_increment,
   LAST_NAME varchar(255),
   FIRST_NAME varchar(255),
   MIDDLE_NAME varchar(255),
   BIRTH_DATE date,
   GENDER varchar(20),
   GENOTYPE varchar(50),
   ETHNICITY varchar(50),
   SOCIAL_SECURITY_NUMBER varchar(50) unique,
   ACTIVITY_STATUS varchar(50),
   DEATH_DATE date,
   VITAL_STATUS varchar(50),
   primary key (IDENTIFIER)
);

create table CATISSUE_PART_MEDICAL_ID (
   IDENTIFIER bigint not null auto_increment,
   MEDICAL_RECORD_NUMBER varchar(255),
   SITE_ID bigint,
   PARTICIPANT_ID bigint,
   primary key (IDENTIFIER)
);
create table CATISSUE_DEPARTMENT (
   IDENTIFIER bigint not null auto_increment,
   NAME varchar(255) not null unique,
   primary key (IDENTIFIER)
);

create table CATISSUE_USER (
   IDENTIFIER bigint not null auto_increment,
   EMAIL_ADDRESS varchar(255),
   FIRST_NAME varchar(255),
   LAST_NAME varchar(255),
   LOGIN_NAME varchar(255) not null unique,
   START_DATE date,
   ACTIVITY_STATUS varchar(50),
   DEPARTMENT_ID bigint,
   CANCER_RESEARCH_GROUP_ID bigint,
   INSTITUTION_ID bigint,
   ADDRESS_ID bigint,
   CSM_USER_ID bigint,
   STATUS_COMMENT text,
   FIRST_TIME_LOGIN bit default 1,
   IS_ADMIN bit default 0,
   primary key (IDENTIFIER)
);
create table CATISSUE_AUDIT_EVENT_QUERY_LOG (
   IDENTIFIER bigint not null auto_increment,
   QUERY_DETAILS longtext,  
   AUDIT_EVENT_ID bigint,
   primary key (IDENTIFIER)
);

alter table CATISSUE_AUDIT_EVENT_QUERY_LOG add index FK62DC439DBC7298A9 (AUDIT_EVENT_ID), add constraint FK62DC439DBC7298A9 foreign key (AUDIT_EVENT_ID) references CATISSUE_AUDIT_EVENT (IDENTIFIER);
alter table CATISSUE_PASSWORD add index FKDE1F38972206F20F (USER_ID), add constraint FKDE1F38972206F20F foreign key (USER_ID) references CATISSUE_USER (IDENTIFIER);
alter table CATISSUE_SITE add index FKB024C3436CD94566 (ADDRESS_ID), add constraint FKB024C3436CD94566 foreign key (ADDRESS_ID) references CATISSUE_ADDRESS (IDENTIFIER);
alter table CATISSUE_SITE add index FKB024C3432206F20F (USER_ID), add constraint FKB024C3432206F20F foreign key (USER_ID) references CATISSUE_USER (IDENTIFIER);
alter table CATISSUE_SPECIMEN_PROTOCOL add index FKB8481373870EB740 (PRINCIPAL_INVESTIGATOR_ID), add constraint FKB8481373870EB740 foreign key (PRINCIPAL_INVESTIGATOR_ID) references CATISSUE_USER (IDENTIFIER);
alter table CATISSUE_RACE add index FKB0242ECD87E5ADC7 (PARTICIPANT_ID), add constraint FKB0242ECD87E5ADC7 foreign key (PARTICIPANT_ID) references CATISSUE_PARTICIPANT (IDENTIFIER);
alter table CATISSUE_PART_MEDICAL_ID add index FK349E77F9A7F77D13 (SITE_ID), add constraint FK349E77F9A7F77D13 foreign key (SITE_ID) references CATISSUE_SITE (IDENTIFIER);
alter table CATISSUE_PART_MEDICAL_ID add index FK349E77F987E5ADC7 (PARTICIPANT_ID), add constraint FK349E77F987E5ADC7 foreign key (PARTICIPANT_ID) references CATISSUE_PARTICIPANT (IDENTIFIER);
alter table CATISSUE_USER add index FKB025CFC71792AD22 (INSTITUTION_ID), add constraint FKB025CFC71792AD22 foreign key (INSTITUTION_ID) references CATISSUE_INSTITUTION (IDENTIFIER);
alter table CATISSUE_USER add index FKB025CFC7FFA96920 (CANCER_RESEARCH_GROUP_ID), add constraint FKB025CFC7FFA96920 foreign key (CANCER_RESEARCH_GROUP_ID) references CATISSUE_CANCER_RESEARCH_GROUP (IDENTIFIER);
alter table CATISSUE_USER add index FKB025CFC76CD94566 (ADDRESS_ID), add constraint FKB025CFC76CD94566 foreign key (ADDRESS_ID) references CATISSUE_ADDRESS (IDENTIFIER);
alter table CATISSUE_USER add index FKB025CFC7F30C2528 (DEPARTMENT_ID), add constraint FKB025CFC7F30C2528 foreign key (DEPARTMENT_ID) references CATISSUE_DEPARTMENT (IDENTIFIER);
alter table CATISSUE_PERMISSIBLE_VALUE add index FK57DDCE153B5435E (PARENT_IDENTIFIER), add constraint FK57DDCE153B5435E foreign key (PARENT_IDENTIFIER) references CATISSUE_PERMISSIBLE_VALUE (IDENTIFIER);
alter table CATISSUE_PERMISSIBLE_VALUE add index FK57DDCE1FC56C2B1 (PUBLIC_ID), add constraint FK57DDCE1FC56C2B1 foreign key (PUBLIC_ID) references CATISSUE_CDE (PUBLIC_ID);
alter table CATISSUE_AUDIT_EVENT add index FKACAF697A2206F20F (USER_ID), add constraint FKACAF697A2206F20F foreign key (USER_ID) references CATISSUE_USER (IDENTIFIER);
alter table CATISSUE_AUDIT_EVENT_LOG add index FK8BB672DF77F0B904 (AUDIT_EVENT_ID), add constraint FK8BB672DF77F0B904 foreign key (AUDIT_EVENT_ID) references CATISSUE_AUDIT_EVENT (IDENTIFIER);
alter table CATISSUE_AUDIT_EVENT_DETAILS add index FK5C07745D34FFD77F (AUDIT_EVENT_LOG_ID), add constraint FK5C07745D34FFD77F foreign key (AUDIT_EVENT_LOG_ID) references CATISSUE_AUDIT_EVENT_LOG (IDENTIFIER);


/** MSR changes : Start **/

CREATE TABLE CATISSUE_SITE_USERS (
   SITE_ID BIGINT ,
   USER_ID BIGINT ,
   PRIMARY KEY (SITE_ID, USER_ID)
);

ALTER TABLE CATISSUE_SITE_USERS ADD INDEX FK1 (USER_ID), ADD CONSTRAINT FK1 FOREIGN KEY (USER_ID) REFERENCES CATISSUE_USER (IDENTIFIER);
ALTER TABLE CATISSUE_SITE_USERS ADD INDEX FK2 (SITE_ID), ADD CONSTRAINT FK2 FOREIGN KEY (SITE_ID) REFERENCES CATISSUE_SITE (IDENTIFIER);

/** MSR changes : End **/


/*********Clinportal **********/
create table CATISSUE_CLINICAL_STUDY (
   IDENTIFIER bigint not null auto_increment,
   UNSIGNED_CONSENT_DOC_URL text,
   primary key (IDENTIFIER)
);

create table CATISSUE_STUDY_FORM_CONTEXT (
   IDENTIFIER bigint not null auto_increment,
   CLINICAL_STUDY_EVENT_ID bigint,
   STUDY_FORM_LABEL varchar(255),
   CONTAINER_ID bigint,
   ACTIVITY_STATUS varchar(10),
   CAN_HAVE_MULTIPLE_RECORDS bit,
   primary key (IDENTIFIER)
);

create table CATISSUE_EVENT_ENTRY (
   IDENTIFIER bigint not null auto_increment,
   ENCOUNTER_DATE date,
   ACTIVITY_STATUS varchar(10),
   ENTRY_NUMBER integer,
   CLINICAL_STUDY_REG_ID bigint,
   CLINICAL_STUDY_EVENT_ID bigint,
   primary key (IDENTIFIER)
);


create table CATISSUE_STUDY_COORDINATORS (
   CLINICAL_STUDY_ID bigint not null auto_increment,
   USER_ID bigint not null,
   primary key (CLINICAL_STUDY_ID, USER_ID)
);


create table CATISSUE_CLINICAL_STUDY_EVENT (
   IDENTIFIER bigint not null auto_increment,
   CLINICAL_STUDY_ID bigint,
   COLLECTION_POINT_LABEL varchar(255),
   EVENT_POINT double precision,
   NO_OF_ENTRIES integer,
   ACTIVITY_STATUS varchar(10),
   IS_INFINITE_ENTRY bit,
   primary key (IDENTIFIER)
);

create table CATISSUE_CLINICAL_STUDY_REG (
   IDENTIFIER bigint not null auto_increment,
   CLINICAL_STUDY_PARTICIPANT_ID varchar(255),
   REGISTRATION_DATE date,
   PARTICIPANT_ID bigint,
   ACTIVITY_STATUS varchar(50),
   CLINICAL_STUDY_ID bigint,
   CONSENT_SIGN_DATE datetime,
   CONSENT_DOC_URL text,
   CONSENT_WITNESS bigint,
   primary key (IDENTIFIER)
);

create table CATISUE_CLIN_STUDY_RECORD_NTRY (
   IDENTIFIER bigint not null auto_increment,
   EVENT_ENTRY_ID bigint,
   ACTIVITY_STATUS varchar(50),
   STUDY_FORM_CONTXT_ID bigint,  
   primary key (IDENTIFIER)
);
create table REPORT (
   IDENTIFIER bigint not null auto_increment, 
   REPORT_NAME varchar(255) not null unique, 
   REPORT_CONTENT text,
   primary key (IDENTIFIER)
);
create table CLINPORT_CONSENT_TIER (
    IDENTIFIER bigint not null auto_increment,
    STATEMENT text,
    CLINICAL_STUDY_ID bigint,
    primary key (IDENTIFIER)
);
create table CLINPORT_CONSENT_TIER_RESPONSE (
    IDENTIFIER bigint not null auto_increment,
    RESPONSE varchar(20),
    CONSENT_TIER_ID bigint,
    CLINICAL_STUDY_REG_ID bigint,
    primary key (IDENTIFIER)
);


alter table CATISSUE_CLINICAL_STUDY add index FKC6723679BC7298A9 (IDENTIFIER),add constraint FKC6723679BC7298A9 foreign key (IDENTIFIER) references CATISSUE_SPECIMEN_PROTOCOL (IDENTIFIER);
alter table CATISSUE_CLINICAL_STUDY_EVENT add index FKE054953483880B1D (CLINICAL_STUDY_ID),add constraint FKE054953483880B1D foreign key (CLINICAL_STUDY_ID) references CATISSUE_CLINICAL_STUDY (IDENTIFIER);
alter table CATISSUE_CLINICAL_STUDY_REG add index FK3D3CBDAE83880B1D (CLINICAL_STUDY_ID),add constraint FK3D3CBDAE83880B1D foreign key (CLINICAL_STUDY_ID) references CATISSUE_CLINICAL_STUDY (IDENTIFIER);
alter table CATISSUE_CLINICAL_STUDY_REG add index FK3D3CBDAE99C40B56 (CONSENT_WITNESS),add constraint FK3D3CBDAE99C40B56 foreign key (CONSENT_WITNESS) references CATISSUE_USER (IDENTIFIER);
alter table CATISSUE_CLINICAL_STUDY_REG add index FK3D3CBDAEC67C9F83 (PARTICIPANT_ID),add constraint FK3D3CBDAEC67C9F83 foreign key (PARTICIPANT_ID) references CATISSUE_PARTICIPANT (IDENTIFIER);
alter table CATISSUE_STUDY_COORDINATORS add index FKDCBC534983880B1D (CLINICAL_STUDY_ID),add constraint FKDCBC534983880B1D foreign key (CLINICAL_STUDY_ID) references CATISSUE_CLINICAL_STUDY (IDENTIFIER);
alter table CATISSUE_STUDY_COORDINATORS add index FKDCBC53492206F20F (USER_ID) ,add constraint FKDCBC53492206F20F foreign key (USER_ID) references CATISSUE_USER (IDENTIFIER);

alter table CATISSUE_STUDY_FORM_CONTEXT add index FK24923AE633052182 (CLINICAL_STUDY_EVENT_ID),add constraint FK24923AE633052182 foreign key (CLINICAL_STUDY_EVENT_ID) references CATISSUE_CLINICAL_STUDY_EVENT (IDENTIFIER);
alter table CATISUE_CLIN_STUDY_RECORD_NTRY add index FK2BE470A3DDFAC4E7 (STUDY_FORM_CONTXT_ID),add constraint FK2BE470A3DDFAC4E7 foreign key (STUDY_FORM_CONTXT_ID) references CATISSUE_STUDY_FORM_CONTEXT (IDENTIFIER);
alter table CATISSUE_EVENT_ENTRY add index FK90328AD133052182 (CLINICAL_STUDY_EVENT_ID),add constraint FK90328AD133052182 foreign key (CLINICAL_STUDY_EVENT_ID) references CATISSUE_CLINICAL_STUDY_EVENT (IDENTIFIER);
alter table CATISSUE_EVENT_ENTRY add index FK90328AD12BAE9DC8 (CLINICAL_STUDY_REG_ID),add constraint FK90328AD12BAE9DC8 foreign key (CLINICAL_STUDY_REG_ID) references CATISSUE_CLINICAL_STUDY_REG (IDENTIFIER);
alter table CATISUE_CLIN_STUDY_RECORD_NTRY add index FK2BE470A350B5844D (EVENT_ENTRY_ID),add constraint FK2BE470A350B5844D foreign key (EVENT_ENTRY_ID) references CATISSUE_EVENT_ENTRY (IDENTIFIER);
alter table CLINPORT_CONSENT_TIER add index FKFC1540779A09F5DC (CLINICAL_STUDY_ID),add constraint FKFC1540779A09F5DC foreign key (CLINICAL_STUDY_ID) references CATISSUE_CLINICAL_STUDY (IDENTIFIER);
alter table CLINPORT_CONSENT_TIER_RESPONSE add index FKD5B88B0983F1E630 (CONSENT_TIER_ID),add constraint FKD5B88B0983F1E630 foreign key (CONSENT_TIER_ID) references CLINPORT_CONSENT_TIER (IDENTIFIER);
alter table CLINPORT_CONSENT_TIER_RESPONSE add index FKD5B88B09A670EC0 (CLINICAL_STUDY_REG_ID),add constraint FKD5B88B09A670EC0 foreign key (CLINICAL_STUDY_REG_ID) references CATISSUE_CLINICAL_STUDY_REG (IDENTIFIER);

/***************************/
commit;

