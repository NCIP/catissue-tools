
drop table CATISSUE_CLINICAL_STUDY_REG cascade constraints;
drop table CATISSUE_STUDY_COORDINATORS cascade constraints;
drop table CATISSUE_CLINICAL_STUDY cascade constraints;
drop table CATISSUE_CLINICAL_STUDY_EVENT cascade constraints;
drop table CATISUE_CLIN_STUDY_RECORD_NTRY cascade constraints;
drop table CATISSUE_STUDY_FORM_CONTEXT cascade constraints;
drop table CATISSUE_EVENT_ENTRY cascade constraints;
drop table CLINPORT_CONSENT_TIER cascade constraints;
drop table CLINPORT_CONSENT_TIER_RESPONSE cascade constraints;

drop sequence CATISSUE_REC_ENTRY_SEQ;
drop sequence CATISSUE_STUDY_REG_SEQ;
drop sequence CATISSUE_STUDY_EVNT_SEQ;
drop sequence CATISSUE_EVNTNTRY_SEQ;
drop sequence CATISSUE_CLINICAL_STUDY_SEQ;
drop sequence CATISSUE_STDY_FRM_CONTXT_SEQ;
drop sequence CLINPORT_CONSENT_TIER_RES_SEQ;
drop sequence CLINPORT_CONSENT_TIER_SEQ;



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
   EVENT_POINT double precision,
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
alter table CATISSUE_CLINICAL_STUDY_REG add constraint FK3D3CBDAE87E5ADC7 foreign key (PARTICIPANT_ID) references CATISSUE_PARTICIPANT;
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
create sequence CATISSUE_REPORT_SEQ;
create sequence CLINPORT_CONSENT_TIER_RES_SEQ;
create sequence CLINPORT_CONSENT_TIER_SEQ;