alter table CATISSUE_PARTICIPANT add (EMAIL_ADDRESS varchar(50));
alter table CATISSUE_PARTICIPANT add (BLOOD_GROUP varchar(20));

create table DYEXTN_MULTISELECT_CHECK_BOX (
	IDENTIFIER bigint not null,
	MULTISELECT tinyint(1),
	primary key (IDENTIFIER)
);
alter table DYEXTN_MULTISELECT_CHECK_BOX add constraint FK4312896DBF67AB26 foreign key (IDENTIFIER) references DYEXTN_SELECT_CONTROL(IDENTIFIER);

ALTER TABLE DYEXTN_COMBOBOX ADD(NO_OF_COLUMNS bigint);