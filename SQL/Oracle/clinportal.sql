create table CATISSUE_PERMISSIBLE_VALUE (
   IDENTIFIER number(19,0) not null ,
   CONCEPT_CODE varchar(40),
   DEFINITION varchar2(500),
   PARENT_IDENTIFIER number(19,0),
   VALUE varchar(100),
   PUBLIC_ID varchar(30),
   primary key (IDENTIFIER)
);
create table CATISSUE_CDE (
   PUBLIC_ID varchar(30) not null,
   LONG_NAME varchar(200),
   DEFINITION varchar2(500),
   VERSION varchar(50),
   LAST_UPDATED date,
   primary key (PUBLIC_ID)
);
create table CATISSUE_AUDIT_EVENT (
   IDENTIFIER number(19,0) not null ,
   IP_ADDRESS varchar(20),
   EVENT_TIMESTAMP date,
   USER_ID number(19,0),
   COMMENTS varchar2(500),
   primary key (IDENTIFIER)
);
create table CATISSUE_AUDIT_EVENT_LOG (
   IDENTIFIER number(19,0) not null ,
   OBJECT_IDENTIFIER number(19,0),
   OBJECT_NAME varchar(50),
   EVENT_TYPE varchar(50),
   AUDIT_EVENT_ID number(19,0),
   primary key (IDENTIFIER)
);
create table CATISSUE_AUDIT_EVENT_DETAILS (
   IDENTIFIER number(19,0) not null ,
   ELEMENT_NAME varchar(150),
   PREVIOUS_VALUE varchar(500),
   CURRENT_VALUE varchar(500),
   AUDIT_EVENT_LOG_ID number(19,0),
   primary key (IDENTIFIER)
);
create table CATISSUE_CANCER_RESEARCH_GROUP (
   IDENTIFIER number(19,0) not null ,
   NAME varchar(255) not null unique,
   primary key (IDENTIFIER)
);
create table CATISSUE_PASSWORD (
   IDENTIFIER number(19,0) not null ,
   PASSWORD varchar(255),
   UPDATE_DATE date,
   USER_ID number(19,0),
   primary key (IDENTIFIER)
);
create table CATISSUE_SITE (
   IDENTIFIER number(19,0) not null ,
   NAME varchar(255) not null unique,
   TYPE varchar(50),
   EMAIL_ADDRESS varchar(255),
   USER_ID number(19,0),
   ACTIVITY_STATUS varchar(50),
   ADDRESS_ID number(19,0),
   primary key (IDENTIFIER)
);
create table CATISSUE_INSTITUTION (
   IDENTIFIER number(19,0) not null ,
   NAME varchar(255) not null unique,
   primary key (IDENTIFIER)
);

create table CATISSUE_SPECIMEN_PROTOCOL (
   IDENTIFIER number(19,0) not null ,
   PRINCIPAL_INVESTIGATOR_ID number(19,0),
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
   IDENTIFIER number(19,0) not null,
   PARTICIPANT_ID number(19,0) not null,
   RACE_NAME varchar(50),
   primary key (IDENTIFIER)
);
create table CATISSUE_ADDRESS (
   IDENTIFIER number(19,0) not null ,
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
   IDENTIFIER number(19,0) not null ,
   AFFILIATION varchar(255) not null,
   NAME_OF_REPORTER varchar(255) not null,
   REPORTERS_EMAIL_ID varchar(255) not null,
   MESSAGE_BODY varchar(500) not null,
   SUBJECT varchar(255),
   REPORTED_DATE date,
   ACTIVITY_STATUS varchar(100),
   COMMENTS varchar2(500),
   primary key (IDENTIFIER)
);
create table CATISSUE_PARTICIPANT (
   IDENTIFIER number(19,0) not null ,
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
   IDENTIFIER number(19,0) not null ,
   MEDICAL_RECORD_NUMBER varchar(255),
   SITE_ID number(19,0),
   PARTICIPANT_ID number(19,0),
   primary key (IDENTIFIER)
);
create table CATISSUE_DEPARTMENT (
   IDENTIFIER number(19,0) not null ,
   NAME varchar(255) not null unique,
   primary key (IDENTIFIER)
);
create table CATISSUE_USER (
   IDENTIFIER number(19,0) not null ,
   EMAIL_ADDRESS varchar(255),
   FIRST_NAME varchar(255),
   LAST_NAME varchar(255),
   LOGIN_NAME varchar(255) not null unique,
   START_DATE date,
   ACTIVITY_STATUS varchar(50),
   DEPARTMENT_ID number(19,0),
   CANCER_RESEARCH_GROUP_ID number(19,0),
   INSTITUTION_ID number(19,0),
   ADDRESS_ID number(19,0),
   CSM_USER_ID number(19,0),
   STATUS_COMMENT varchar2(500),
   FIRST_TIME_LOGIN number(1,0) default 1,
   IS_ADMIN number(1,0) default 0,
   primary key (IDENTIFIER)
);
create table CATISSUE_AUDIT_EVENT_QUERY_LOG (
   IDENTIFIER number(19,0) not null,
   QUERY_DETAILS clob,  
   AUDIT_EVENT_ID number(19,0),
   primary key (IDENTIFIER)
);

alter table CATISSUE_AUDIT_EVENT_QUERY_LOG add constraint FK62DC439DBC7298A9 foreign key (AUDIT_EVENT_ID) references CATISSUE_AUDIT_EVENT ;
alter table CATISSUE_PASSWORD  add constraint FKDE1F38972206F20F foreign key (USER_ID) references CATISSUE_USER  ;
alter table CATISSUE_SITE  add constraint FKB024C3436CD94566 foreign key (ADDRESS_ID) references CATISSUE_ADDRESS  ;
alter table CATISSUE_SITE  add constraint FKB024C3432206F20F foreign key (USER_ID) references CATISSUE_USER  ;
alter table CATISSUE_SPECIMEN_PROTOCOL add constraint FKB8481373870EB740 foreign key (PRINCIPAL_INVESTIGATOR_ID) references CATISSUE_USER  ;
alter table CATISSUE_RACE  add constraint FKB0242ECD87E5ADC7 foreign key (PARTICIPANT_ID) references CATISSUE_PARTICIPANT  ;
alter table CATISSUE_PART_MEDICAL_ID  add constraint FK349E77F9A7F77D13 foreign key (SITE_ID) references CATISSUE_SITE  ;
alter table CATISSUE_PART_MEDICAL_ID  add constraint FK349E77F987E5ADC7 foreign key (PARTICIPANT_ID) references CATISSUE_PARTICIPANT  ;
alter table CATISSUE_USER  add constraint FKB025CFC71792AD22 foreign key (INSTITUTION_ID) references CATISSUE_INSTITUTION  ;
alter table CATISSUE_USER  add constraint FKB025CFC7FFA96920 foreign key (CANCER_RESEARCH_GROUP_ID) references CATISSUE_CANCER_RESEARCH_GROUP  ;
alter table CATISSUE_USER  add constraint FKB025CFC76CD94566 foreign key (ADDRESS_ID) references CATISSUE_ADDRESS  ;
alter table CATISSUE_USER  add constraint FKB025CFC7F30C2528 foreign key (DEPARTMENT_ID) references CATISSUE_DEPARTMENT  ;
alter table CATISSUE_PERMISSIBLE_VALUE  add constraint FK57DDCE153B5435E foreign key (PARENT_IDENTIFIER) references CATISSUE_PERMISSIBLE_VALUE  ;
alter table CATISSUE_PERMISSIBLE_VALUE  add constraint FK57DDCE1FC56C2B1 foreign key (PUBLIC_ID) references CATISSUE_CDE ;
alter table CATISSUE_AUDIT_EVENT  add constraint FKACAF697A2206F20F foreign key (USER_ID) references CATISSUE_USER  ;
alter table CATISSUE_AUDIT_EVENT_LOG  add constraint FK8BB672DF77F0B904 foreign key (AUDIT_EVENT_ID) references CATISSUE_AUDIT_EVENT  ;
alter table CATISSUE_AUDIT_EVENT_DETAILS  add constraint FK5C07745D34FFD77F foreign key (AUDIT_EVENT_LOG_ID) references CATISSUE_AUDIT_EVENT_LOG  ;

create sequence CATISSUE_CANCER_RES_GRP_SEQ;
create sequence CATISSUE_USER_SEQ;
create sequence CATISSUE_SPECIMEN_PROTOCOL_SEQ;
create sequence CATISSUE_SITE_SEQ;
create sequence CATISSUE_ADDRESS_SEQ;
create sequence CATISSUE_AUDIT_EVENT_LOG_SEQ;
create sequence CATISSUE_DEPARTMENT_SEQ;
create sequence CATISSUE_QUERY_TABLE_DATA_SEQ;
create sequence CATISSUE_AUDIT_EVENT_DET_SEQ;

create sequence CATISSUE_REPORTED_PROBLEM_SEQ;

create sequence CATISSUE_AUDIT_EVENT_PARAM_SEQ;
create sequence CATISSUE_PARTICIPANT_SEQ;

create sequence CATISSUE_PART_MEDICAL_ID_SEQ;

create sequence CATISSUE_INSTITUTION_SEQ;
create sequence CATISSUE_PERMISSIBLE_VALUE_SEQ;
create sequence CATISSUE_AUDIT_EVENT_SEQ;
create sequence CATISSUE_PASSWORD_SEQ;
create sequence CATISSUE_AUDIT_EVENT_QUERY_SEQ;
create sequence CATISSUE_RACE_SEQ;


/** MSR changes : Start **/

CREATE TABLE CATISSUE_SITE_USERS (
   SITE_ID NUMBER(19,0) ,
   USER_ID NUMBER(19,0) ,
   PRIMARY KEY (SITE_ID, USER_ID)
);


ALTER TABLE CATISSUE_SITE_USERS ADD CONSTRAINT FK1 FOREIGN KEY (USER_ID) REFERENCES CATISSUE_USER (IDENTIFIER);
ALTER TABLE CATISSUE_SITE_USERS ADD CONSTRAINT FK2 FOREIGN KEY (SITE_ID) REFERENCES CATISSUE_SITE (IDENTIFIER);

/** MSR changes : End **/


/*********Clinportal **********/
create table CATISSUE_CLINICAL_STUDY (
   IDENTIFIER number(19,0) not null,
   UNSIGNED_CONSENT_DOC_URL varchar2(500),
   primary key (IDENTIFIER)
);

create table CATISSUE_STUDY_FORM_CONTEXT (
   IDENTIFIER number(19,0) not null,
   CLINICAL_STUDY_EVENT_ID number(19,0),
   STUDY_FORM_LABEL varchar2(255),
   CONTAINER_ID number(19,0),
   ACTIVITY_STATUS varchar2(10),
   CAN_HAVE_MULTIPLE_RECORDS number(1,0),
   primary key (IDENTIFIER)
);

create table CATISSUE_EVENT_ENTRY (
   IDENTIFIER number(19,0) not null,
   ENCOUNTER_DATE date,
   ACTIVITY_STATUS varchar2(10),
   ENTRY_NUMBER number(10,0),
   CLINICAL_STUDY_REG_ID number(19,0),
   CLINICAL_STUDY_EVENT_ID number(19,0),
   primary key (IDENTIFIER)
);


create table CATISSUE_STUDY_COORDINATORS (
   CLINICAL_STUDY_ID number(19,0) not null,
   USER_ID number(19,0) not null,
   primary key (CLINICAL_STUDY_ID, USER_ID)
);

create table CATISSUE_CLINICAL_STUDY_EVENT (
   IDENTIFIER number(19,0) not null,
   CLINICAL_STUDY_ID number(19,0),
   COLLECTION_POINT_LABEL varchar2(255),
   EVENT_POINT number(19,0),
   NO_OF_ENTRIES number(10,0),
   ACTIVITY_STATUS varchar2(10),
   IS_INFINITE_ENTRY number(1,0),
   primary key (IDENTIFIER)
);

create table CATISSUE_CLINICAL_STUDY_REG (
   IDENTIFIER number(19,0) not null,
   CLINICAL_STUDY_PARTICIPANT_ID varchar2(255),
   REGISTRATION_DATE date,
   PARTICIPANT_ID number(19,0),
   ACTIVITY_STATUS varchar2(50),
   CLINICAL_STUDY_ID number(19,0),
   CONSENT_SIGN_DATE timestamp,
   CONSENT_DOC_URL varchar2(1000),
   CONSENT_WITNESS number(19,0),
   primary key (IDENTIFIER)
);

create table CATISUE_CLIN_STUDY_RECORD_NTRY (
   IDENTIFIER number(19,0) not null,
   EVENT_ENTRY_ID number(19,0),
   ACTIVITY_STATUS varchar2(50),
   STUDY_FORM_CONTXT_ID number(19,0),  
   primary key (IDENTIFIER)
);
create table REPORT (
   IDENTIFIER NUMBER(19,0) not null, 
   REPORT_NAME varchar(255) not null unique, 
   REPORT_CONTENT BLOB,
   primary key (IDENTIFIER)
);
create table CLINPORT_CONSENT_TIER (
    IDENTIFIER number(19,0) not null,
    STATEMENT varchar2(500),
    CLINICAL_STUDY_ID number(19,0),
    primary key (IDENTIFIER)
);
create table CLINPORT_CONSENT_TIER_RESPONSE (
    IDENTIFIER number(19,0) not null,
    RESPONSE varchar2(20),
    CONSENT_TIER_ID number(19,0),
    CLINICAL_STUDY_REG_ID number(19,0),
    primary key (IDENTIFIER)
);

alter table CATISSUE_CLINICAL_STUDY add constraint FKC6723679BC7298A9 foreign key (IDENTIFIER) references CATISSUE_SPECIMEN_PROTOCOL;
alter table CATISSUE_CLINICAL_STUDY_EVENT add constraint FKE054953483880B1D foreign key (CLINICAL_STUDY_ID) references CATISSUE_CLINICAL_STUDY;
alter table CATISSUE_CLINICAL_STUDY_REG add constraint FK3D3CBDAE83880B1D foreign key (CLINICAL_STUDY_ID) references CATISSUE_CLINICAL_STUDY;
alter table CATISSUE_CLINICAL_STUDY_REG add constraint FK3D3CBDAE99C40B56 foreign key (CONSENT_WITNESS) references CATISSUE_USER;
alter table CATISSUE_CLINICAL_STUDY_REG add constraint FK3D3CBDAEC67C9F83 foreign key (PARTICIPANT_ID) references CATISSUE_PARTICIPANT;
alter table CATISSUE_STUDY_COORDINATORS add constraint FKDCBC534983880B1D foreign key (CLINICAL_STUDY_ID) references CATISSUE_CLINICAL_STUDY;
alter table CATISSUE_STUDY_COORDINATORS add constraint FKDCBC53492206F20F foreign key (USER_ID) references CATISSUE_USER;

alter table CATISSUE_STUDY_FORM_CONTEXT add constraint FK24923AE633052182 foreign key (CLINICAL_STUDY_EVENT_ID) references CATISSUE_CLINICAL_STUDY_EVENT;
alter table CATISUE_CLIN_STUDY_RECORD_NTRY add constraint FK2BE470A3DDFAC4E7 foreign key (STUDY_FORM_CONTXT_ID) references CATISSUE_STUDY_FORM_CONTEXT;
alter table CATISSUE_EVENT_ENTRY add constraint FK90328AD133052182 foreign key (CLINICAL_STUDY_EVENT_ID) references CATISSUE_CLINICAL_STUDY_EVENT;
alter table CATISSUE_EVENT_ENTRY add constraint FK90328AD12BAE9DC8 foreign key (CLINICAL_STUDY_REG_ID) references CATISSUE_CLINICAL_STUDY_REG;
alter table CATISUE_CLIN_STUDY_RECORD_NTRY add constraint FK2BE470A350B5844D foreign key (EVENT_ENTRY_ID) references CATISSUE_EVENT_ENTRY;
alter table CLINPORT_CONSENT_TIER add constraint FKFC1540779A09F5DC foreign key (CLINICAL_STUDY_ID) references CATISSUE_CLINICAL_STUDY;
alter table CLINPORT_CONSENT_TIER_RESPONSE add constraint FKD5B88B0983F1E630 foreign key (CONSENT_TIER_ID) references CLINPORT_CONSENT_TIER;
alter table CLINPORT_CONSENT_TIER_RESPONSE add constraint FKD5B88B09A670EC0 foreign key (CLINICAL_STUDY_REG_ID) references CATISSUE_CLINICAL_STUDY_REG;

create sequence CATISSUE_REC_ENTRY_SEQ;
create sequence CATISSUE_CLINICAL_STUDY_SEQ;
create sequence CATISSUE_STUDY_REG_SEQ;
create sequence CATISSUE_STUDY_EVNT_SEQ;
create sequence CATISSUE_EVNTNTRY_SEQ;
create sequence CATISSUE_STDY_FRM_CONTXT_SEQ;
create sequence CLINPORT_CONSENT_TIER_RES_SEQ;
create sequence CLINPORT_CONSENT_TIER_SEQ;
/***************************/
commit;

