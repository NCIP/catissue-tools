create table DE_COLL_ATTR_RECORD_VALUES (
   IDENTIFIER bigint not null,
   RECORD_VALUE text,
   COLLECTION_ATTR_RECORD_ID bigint,
   primary key (IDENTIFIER)
);
create table DE_FILE_ATTR_RECORD_VALUES (
   IDENTIFIER bigint not null,
   CONTENT_TYPE varchar(255),
   FILE_CONTENT text,
   FILE_NAME varchar(255),
   RECORD_ID bigint,
   primary key (IDENTIFIER)
);
create table DE_OBJECT_ATTR_RECORD_VALUES (
   IDENTIFIER bigint not null,
   CLASS_NAME varchar(255),
   OBJECT_CONTENT text,
   RECORD_ID bigint,
   primary key (IDENTIFIER)
);
create table DYEXTN_ABSTRACT_ENTITY (
   id bigint not null,
   primary key (id)
);
create table DYEXTN_ABSTRACT_METADATA (
   IDENTIFIER bigint not null,
   CREATED_DATE date,
   DESCRIPTION text,
   LAST_UPDATED date,
   NAME text,
   PUBLIC_ID varchar(255),
   primary key (IDENTIFIER)
);
create table DYEXTN_ABSTR_CONTAIN_CTR (
   IDENTIFIER bigint not null,
   CONTAINER_ID bigint,
   primary key (IDENTIFIER)
);
create table DYEXTN_ASSOCIATION (
   IDENTIFIER bigint not null,
   DIRECTION varchar(255),
   TARGET_ENTITY_ID bigint,
   SOURCE_ROLE_ID bigint,
   TARGET_ROLE_ID bigint,
   IS_SYSTEM_GENERATED bit,
   primary key (IDENTIFIER)
);
create table DYEXTN_ASSO_DISPLAY_ATTR (
   IDENTIFIER bigint not null,
   SEQUENCE_NUMBER integer,
   DISPLAY_ATTRIBUTE_ID bigint,
   SELECT_CONTROL_ID bigint,
   primary key (IDENTIFIER)
);
create table DYEXTN_ATTRIBUTE (
   IDENTIFIER bigint not null,
   ENTIY_ID bigint,
   primary key (IDENTIFIER)
);
create table DYEXTN_ATTRIBUTE_RECORD (
   IDENTIFIER bigint not null,
   ENTITY_ID bigint,
   ATTRIBUTE_ID bigint,
   RECORD_ID bigint,
   primary key (IDENTIFIER)
);
create table DYEXTN_ATTRIBUTE_TYPE_INFO (
   IDENTIFIER bigint not null,
   PRIMITIVE_ATTRIBUTE_ID bigint,
   primary key (IDENTIFIER)
);
create table DYEXTN_BARR_CONCEPT_VALUE (
   IDENTIFIER bigint not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_BASE_ABSTRACT_ATTRIBUTE (
   IDENTIFIER bigint not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_BOOLEAN_CONCEPT_VALUE (
   IDENTIFIER bigint not null,
   VALUE bit,
   primary key (IDENTIFIER)
);
create table DYEXTN_BOOLEAN_TYPE_INFO (
   IDENTIFIER bigint not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_BYTE_ARRAY_TYPE_INFO (
   IDENTIFIER bigint not null,
   CONTENT_TYPE varchar(255),
   primary key (IDENTIFIER)
);
create table DYEXTN_CADSRDE (
   IDENTIFIER bigint not null,
   PUBLIC_ID varchar(255),
   primary key (IDENTIFIER)
);
create table DYEXTN_CADSR_VALUE_DOMAIN_INFO (
   IDENTIFIER bigint not null,
   DATATYPE varchar(255),
   NAME varchar(255),
   TYPE varchar(255),
   PRIMITIVE_ATTRIBUTE_ID bigint,
   primary key (IDENTIFIER)
);
create table DYEXTN_CATEGORY (
   IDENTIFIER bigint not null,
   ROOT_CATEGORY_ELEMENT bigint,
   CATEGORY_ENTITY_ID bigint,
   primary key (IDENTIFIER)
);
create table DYEXTN_CATEGORY_ASSOCIATION (
   IDENTIFIER bigint not null,
   CATEGORY_ENTIY_ID bigint,
   CATEGORY_ENTITY_ID bigint,
   primary key (IDENTIFIER)
);
create table DYEXTN_CATEGORY_ATTRIBUTE (
   IDENTIFIER bigint not null,
   ABSTRACT_ATTRIBUTE_ID bigint,
   CATEGORY_ENTIY_ID bigint,
   CATEGORY_ENTITY_ID bigint,
   IS_VISIBLE bit,
   IS_RELATTRIBUTE bit,
   IS_CAL_ATTRIBUTE bit, 
   CAL_CATEGORY_ATTR_ID bigint, 
   CAL_DEPENDENT_CATEGORY_ATTR_ID bigint,
   primary key (IDENTIFIER)
);
create table DYEXTN_CATEGORY_ENTITY (
   IDENTIFIER bigint not null,
   NUMBER_OF_ENTRIES integer,
   ENTITY_ID bigint,
   OWN_PARENT_CATEGORY_ENTITY_ID bigint,
   TREE_PARENT_CATEGORY_ENTITY_ID bigint,
   CATEGORY_ASSOCIATION_ID bigint,
   PARENT_CATEGORY_ENTITY_ID bigint,
   REL_ATTR_CAT_ENTITY_ID bigint,
   IS_CREATETABLE bit,
   primary key (IDENTIFIER)
);
create table DYEXTN_CAT_ASSO_CTL (
   IDENTIFIER bigint not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_CHECK_BOX (
   IDENTIFIER bigint not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_COLUMN_PROPERTIES (
   IDENTIFIER bigint not null,
   CONSTRAINT_NAME varchar(255),
   PRIMITIVE_ATTRIBUTE_ID bigint,
   CATEGORY_ATTRIBUTE_ID bigint,
   primary key (IDENTIFIER)
);
create table DYEXTN_COMBOBOX (
   IDENTIFIER bigint not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_CONSTRAINT_PROPERTIES (
   IDENTIFIER bigint not null,
   SOURCE_ENTITY_KEY varchar(255),
   TARGET_ENTITY_KEY varchar(255),
   SRC_CONSTRAINT_NAME varchar(255),
   TARGET_CONSTRAINT_NAME varchar(255),
   ASSOCIATION_ID bigint,
   CATEGORY_ASSOCIATION_ID bigint,
   primary key (IDENTIFIER)
);
create table DYEXTN_CONTAINER (
   IDENTIFIER bigint not null,
   BUTTON_CSS varchar(255),
   CAPTION varchar(800),
   ABSTRACT_ENTITY_ID bigint,
   MAIN_TABLE_CSS varchar(255),
   REQUIRED_FIELD_INDICATOR varchar(255),
   REQUIRED_FIELD_WARNING_MESSAGE varchar(255),
   TITLE_CSS varchar(255),
   BASE_CONTAINER_ID bigint,
   ADD_CAPTION bit,
   ENTITY_GROUP_ID bigint,
   PARENT_CONTAINER_ID bigint,
   VIEW_ID bigint,
   primary key (IDENTIFIER)
);
create table DYEXTN_CONTAINMENT_CONTROL (
   IDENTIFIER bigint not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_CONTROL (
   IDENTIFIER bigint not null,
   SHOW_LABEL bit,
   CAPTION varchar(800),
   IS_CALCULATED bit,
   CSS_CLASS varchar(255),
   HIDDEN bit,
   NAME varchar(255),
   SEQUENCE_NUMBER integer,
   TOOLTIP varchar(255),
   CONTAINER_ID bigint,
   BASE_ABST_ATR_ID bigint,
   READ_ONLY bit,
   HEADING varchar(255), 
   yPosition integer,
   primary key (IDENTIFIER)
);
create table DYEXTN_DATABASE_PROPERTIES (
   IDENTIFIER bigint not null,
   NAME varchar(255),
   primary key (IDENTIFIER)
);
create table DYEXTN_DATA_ELEMENT (
   IDENTIFIER bigint not null,
   CATEGORY_ATTRIBUTE_ID bigint,
   ATTRIBUTE_TYPE_INFO_ID bigint,
   primary key (IDENTIFIER)
);
create table DYEXTN_DATA_GRID (
   IDENTIFIER bigint not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_DATEPICKER (
   IDENTIFIER bigint not null,
   DATE_VALUE_TYPE varchar(255),
   primary key (IDENTIFIER)
);
create table DYEXTN_DATE_CONCEPT_VALUE (
   IDENTIFIER bigint not null,
   VALUE timestamp,
   primary key (IDENTIFIER)
);
create table DYEXTN_DATE_TYPE_INFO (
   IDENTIFIER bigint not null,
   FORMAT varchar(255),
   primary key (IDENTIFIER)
);
create table DYEXTN_DOUBLE_CONCEPT_VALUE (
   IDENTIFIER bigint not null,
   VALUE double precision,
   primary key (IDENTIFIER)
);
create table DYEXTN_DOUBLE_TYPE_INFO (
   IDENTIFIER bigint not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_ENTITY (
   IDENTIFIER bigint not null,
   DATA_TABLE_STATE integer,
   ENTITY_GROUP_ID bigint,
   IS_ABSTRACT bit,
   PARENT_ENTITY_ID bigint,
   INHERITANCE_STRATEGY integer,
   DISCRIMINATOR_COLUMN_NAME varchar(255),
   DISCRIMINATOR_VALUE varchar(255),
   primary key (IDENTIFIER)
);
create table DYEXTN_ENTITY_GROUP (
   IDENTIFIER bigint not null,
   LONG_NAME varchar(255),
   SHORT_NAME varchar(255),
   VERSION varchar(255),
   IS_SYSTEM_GENERATED bit,
   primary key (IDENTIFIER)
);
create table DYEXTN_ENTITY_MAP (
   IDENTIFIER bigint not null,
   CONTAINER_ID bigint,
   STATUS varchar(10),
   STATIC_ENTITY_ID bigint,
   CREATED_DATE date,
   CREATED_BY varchar(255),
   primary key (IDENTIFIER)
);
create table DYEXTN_ENTITY_MAP_CONDNS (
   IDENTIFIER bigint not null,
   STATIC_RECORD_ID bigint,
   TYPE_ID bigint,
   FORM_CONTEXT_ID bigint,
   primary key (IDENTIFIER)
);
create table DYEXTN_ENTITY_MAP_RECORD (
   IDENTIFIER bigint not null,
   FORM_CONTEXT_ID bigint,
   STATIC_ENTITY_RECORD_ID bigint,
   STATUS varchar(10),
   DYNAMIC_ENTITY_RECORD_ID bigint,
   MODIFIED_DATE date,
   CREATED_DATE date,
   CREATED_BY varchar(255),
   primary key (IDENTIFIER)
);
create table DYEXTN_FILE_EXTENSIONS (
   IDENTIFIER bigint not null,
   FILE_EXTENSION varchar(255),
   ATTRIBUTE_ID bigint,
   primary key (IDENTIFIER)
);
create table DYEXTN_FILE_TYPE_INFO (
   IDENTIFIER bigint not null,
   MAX_FILE_SIZE float,
   primary key (IDENTIFIER)
);
create table DYEXTN_FILE_UPLOAD (
   IDENTIFIER bigint not null,
   NO_OF_COLUMNS integer,
   primary key (IDENTIFIER)
);
create table DYEXTN_FLOAT_CONCEPT_VALUE (
   IDENTIFIER bigint not null,
   VALUE float,
   primary key (IDENTIFIER)
);
create table DYEXTN_FLOAT_TYPE_INFO (
   IDENTIFIER bigint not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_FORMULA (
   IDENTIFIER bigint not null,
   EXPRESSION varchar(255),
   CATEGORY_ATTRIBUTE_ID bigint,
   primary key (IDENTIFIER)
);
create table DYEXTN_FORM_CONTEXT (
   IDENTIFIER bigint not null,
   IS_INFINITE_ENTRY bit,
   ENTITY_MAP_ID bigint,
   STUDY_FORM_LABEL varchar(255),
   NO_OF_ENTRIES integer,
   primary key (IDENTIFIER)
);
create table DYEXTN_FORM_CTRL_NOTES (
   IDENTIFIER bigint not null, 
   NOTE varchar(255), 
   FORM_CONTROL_ID bigint, 
   INSERTION_ORDER integer, 
   primary key (IDENTIFIER)
);
create table DYEXTN_INTEGER_CONCEPT_VALUE (
   IDENTIFIER bigint not null,
   VALUE integer,
   primary key (IDENTIFIER)
);
create table DYEXTN_INTEGER_TYPE_INFO (
   IDENTIFIER bigint not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_LABEL (
   IDENTIFIER bigint not null, 
   primary key (IDENTIFIER)
);
create table DYEXTN_LIST_BOX (
   IDENTIFIER bigint not null,
   MULTISELECT bit,
   NO_OF_ROWS integer,
   USE_AUTOCOMPLETE bit,
   primary key (IDENTIFIER)
);
create table DYEXTN_LONG_CONCEPT_VALUE (
   IDENTIFIER bigint not null,
   VALUE bigint,
   primary key (IDENTIFIER)
);
create table DYEXTN_LONG_TYPE_INFO (
   IDENTIFIER bigint not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_NUMERIC_TYPE_INFO (
   IDENTIFIER bigint not null,
   MEASUREMENT_UNITS varchar(255),
   DECIMAL_PLACES integer,
   NO_DIGITS integer,
   primary key (IDENTIFIER)
);
create table DYEXTN_OBJECT_TYPE_INFO (
   IDENTIFIER bigint not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_PATH (
   IDENTIFIER bigint not null,
   CATEGORY_ENTITY_ID bigint,
   primary key (IDENTIFIER)
);
create table DYEXTN_PATH_ASSO_REL (
   IDENTIFIER bigint not null,
   PATH_ID bigint,
   ASSOCIATION_ID bigint,
   PATH_SEQUENCE_NUMBER integer,
   SRC_INSTANCE_ID bigint,
   TGT_INSTANCE_ID bigint,
   primary key (IDENTIFIER)
);
create table DYEXTN_PERMISSIBLE_VALUE (
   IDENTIFIER bigint not null,
   DESCRIPTION varchar(255),
   CATEGORY_ATTRIBUTE_ID bigint,
   ATTRIBUTE_TYPE_INFO_ID bigint,
   primary key (IDENTIFIER)
);
create table DYEXTN_PRIMITIVE_ATTRIBUTE (
   IDENTIFIER bigint not null,
   IS_COLLECTION bit,
   IS_IDENTIFIED bit,
   IS_PRIMARY_KEY bit,
   IS_NULLABLE bit,
   primary key (IDENTIFIER)
);
create table DYEXTN_RADIOBUTTON (
   IDENTIFIER bigint not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_ROLE (
   IDENTIFIER bigint not null,
   ASSOCIATION_TYPE varchar(255),
   MAX_CARDINALITY integer,
   MIN_CARDINALITY integer,
   NAME varchar(255),
   primary key (IDENTIFIER)
);
create table DYEXTN_RULE (
   IDENTIFIER bigint not null,
   NAME varchar(255),
   ATTRIBUTE_ID bigint,
   CATEGORY_ATTR_ID bigint,
   IS_IMPLICIT bit,
   primary key (IDENTIFIER)
);
create table DYEXTN_RULE_PARAMETER (
   IDENTIFIER bigint not null,
   NAME varchar(255),
   VALUE varchar(255),
   RULE_ID bigint,
   primary key (IDENTIFIER)
);
create table DYEXTN_SELECT_CONTROL (
   IDENTIFIER bigint not null,
   SEPARATOR_STRING varchar(255),
   primary key (IDENTIFIER)
);
create table DYEXTN_SEMANTIC_PROPERTY (
   IDENTIFIER bigint not null,
   CONCEPT_CODE varchar(255),
   TERM varchar(255),
   THESAURAS_NAME varchar(255),
   SEQUENCE_NUMBER integer,
   CONCEPT_DEFINITION varchar(255),
   ABSTRACT_METADATA_ID bigint,
   ABSTRACT_VALUE_ID bigint,
   primary key (IDENTIFIER)
);
create table DYEXTN_SHORT_CONCEPT_VALUE (
   IDENTIFIER bigint not null,
   VALUE integer,
   primary key (IDENTIFIER)
);
create table DYEXTN_SHORT_TYPE_INFO (
   IDENTIFIER bigint not null,
   primary key (IDENTIFIER)
);
create table DYEXTN_SQL_AUDIT (
   IDENTIFIER bigint not null,
   AUDIT_DATE date,
   QUERY_EXECUTED text,
   USER_ID bigint,
   primary key (IDENTIFIER)
);
create table DYEXTN_STRING_CONCEPT_VALUE (
   IDENTIFIER bigint not null,
   VALUE varchar(255),
   primary key (IDENTIFIER)
);
create table DYEXTN_STRING_TYPE_INFO (
   IDENTIFIER bigint not null,
   MAX_SIZE integer,
   primary key (IDENTIFIER)
);
create table DYEXTN_TABLE_PROPERTIES (
   IDENTIFIER bigint not null,
   CONSTRAINT_NAME varchar(255),
   ABSTRACT_ENTITY_ID bigint,
   primary key (IDENTIFIER)
);
create table DYEXTN_TAGGED_VALUE (
   IDENTIFIER bigint not null,
   T_KEY varchar(255),
   T_VALUE varchar(255),
   ABSTRACT_METADATA_ID bigint,
   primary key (IDENTIFIER)
);
create table DYEXTN_TEXTAREA (
   IDENTIFIER bigint not null,
   TEXTAREA_COLUMNS integer,
   TEXTAREA_ROWS integer,
   primary key (IDENTIFIER)
);
create table DYEXTN_TEXTFIELD (
   IDENTIFIER bigint not null,
   NO_OF_COLUMNS integer,
   IS_PASSWORD bit,
   IS_URL bit,
   primary key (IDENTIFIER)
);
create table DYEXTN_USERDEFINED_DE (
   IDENTIFIER bigint not null,
   IS_ORDERED bit,
   primary key (IDENTIFIER)
);
create table DYEXTN_USERDEF_DE_VALUE_REL (
   USER_DEF_DE_ID bigint not null,
   PERMISSIBLE_VALUE_ID bigint not null,
   primary key (USER_DEF_DE_ID, PERMISSIBLE_VALUE_ID)
);
create table DYEXTN_VIEW (
   IDENTIFIER bigint not null,
   NAME varchar(255),
   primary key (IDENTIFIER)
);
create table dyextn_id_generator (
   id bigint not null,
   next_available_id bigint,
   primary key (id)
);
alter table DE_COLL_ATTR_RECORD_VALUES add constraint FK847DA57775255CA5 foreign key (COLLECTION_ATTR_RECORD_ID) references DYEXTN_ATTRIBUTE_RECORD (IDENTIFIER);
alter table DE_FILE_ATTR_RECORD_VALUES add constraint FKE68334E74EB991B2 foreign key (RECORD_ID) references DYEXTN_ATTRIBUTE_RECORD (IDENTIFIER);
alter table DE_OBJECT_ATTR_RECORD_VALUES add constraint FK504EADC44EB991B2 foreign key (RECORD_ID) references DYEXTN_ATTRIBUTE_RECORD (IDENTIFIER);
alter table DYEXTN_ABSTRACT_ENTITY add constraint FKA4A164E3D3027A30 foreign key (id) references DYEXTN_ABSTRACT_METADATA (IDENTIFIER);
alter table DYEXTN_ABSTR_CONTAIN_CTR add constraint FK9EB9020A69935DD6 foreign key (CONTAINER_ID) references DYEXTN_CONTAINER (IDENTIFIER);
alter table DYEXTN_ABSTR_CONTAIN_CTR add constraint FK9EB9020A40F198C2 foreign key (IDENTIFIER) references DYEXTN_CONTROL (IDENTIFIER);
alter table DYEXTN_ASSOCIATION add constraint FK1046842440738A50 foreign key (TARGET_ENTITY_ID) references DYEXTN_ENTITY (IDENTIFIER);
alter table DYEXTN_ASSOCIATION add constraint FK1046842439780F7A foreign key (SOURCE_ROLE_ID) references DYEXTN_ROLE (IDENTIFIER);
alter table DYEXTN_ASSOCIATION add constraint FK104684242BD842F0 foreign key (TARGET_ROLE_ID) references DYEXTN_ROLE (IDENTIFIER);
alter table DYEXTN_ASSOCIATION add constraint FK104684246D19A21F foreign key (IDENTIFIER) references DYEXTN_ATTRIBUTE (IDENTIFIER);
alter table DYEXTN_ASSO_DISPLAY_ATTR add constraint FKD12FD3827FD29CDD foreign key (SELECT_CONTROL_ID) references DYEXTN_SELECT_CONTROL (IDENTIFIER);
alter table DYEXTN_ASSO_DISPLAY_ATTR add constraint FKD12FD38235D6E973 foreign key (DISPLAY_ATTRIBUTE_ID) references DYEXTN_PRIMITIVE_ATTRIBUTE (IDENTIFIER);
alter table DYEXTN_ATTRIBUTE add constraint FK37F1E2FFF99EA906 foreign key (ENTIY_ID) references DYEXTN_ENTITY (IDENTIFIER);
alter table DYEXTN_ATTRIBUTE add constraint FK37F1E2FF5CC8694E foreign key (IDENTIFIER) references DYEXTN_BASE_ABSTRACT_ATTRIBUTE (IDENTIFIER);
alter table DYEXTN_ATTRIBUTE_RECORD add constraint FK9B20ED914AC41F7E foreign key (ENTITY_ID) references DYEXTN_ENTITY (IDENTIFIER);
alter table DYEXTN_ATTRIBUTE_RECORD add constraint FK9B20ED914DC2CD16 foreign key (ATTRIBUTE_ID) references DYEXTN_PRIMITIVE_ATTRIBUTE (IDENTIFIER);
alter table DYEXTN_ATTRIBUTE_TYPE_INFO add constraint FK62596D531333996E foreign key (PRIMITIVE_ATTRIBUTE_ID) references DYEXTN_PRIMITIVE_ATTRIBUTE (IDENTIFIER);
alter table DYEXTN_BARR_CONCEPT_VALUE add constraint FK89D27DF74641D513 foreign key (IDENTIFIER) references DYEXTN_PERMISSIBLE_VALUE (IDENTIFIER);
alter table DYEXTN_BASE_ABSTRACT_ATTRIBUTE add constraint FK14BA6610728B19BE foreign key (IDENTIFIER) references DYEXTN_ABSTRACT_METADATA (IDENTIFIER);
alter table DYEXTN_BOOLEAN_CONCEPT_VALUE add constraint FK57B6C4A64641D513 foreign key (IDENTIFIER) references DYEXTN_PERMISSIBLE_VALUE (IDENTIFIER);
alter table DYEXTN_BOOLEAN_TYPE_INFO add constraint FK28F1809FE5294FA3 foreign key (IDENTIFIER) references DYEXTN_ATTRIBUTE_TYPE_INFO (IDENTIFIER);
alter table DYEXTN_BYTE_ARRAY_TYPE_INFO add constraint FK18BDA73E5294FA3 foreign key (IDENTIFIER) references DYEXTN_ATTRIBUTE_TYPE_INFO (IDENTIFIER);
alter table DYEXTN_CADSRDE add constraint FK588A250953CC4A77 foreign key (IDENTIFIER) references DYEXTN_DATA_ELEMENT (IDENTIFIER);
alter table DYEXTN_CADSR_VALUE_DOMAIN_INFO add constraint FK1C9AA3641333996E foreign key (PRIMITIVE_ATTRIBUTE_ID) references DYEXTN_PRIMITIVE_ATTRIBUTE (IDENTIFIER);
alter table DYEXTN_CATEGORY add constraint FKD33DE81B854AC01B foreign key (CATEGORY_ENTITY_ID) references DYEXTN_CATEGORY_ENTITY (IDENTIFIER);
alter table DYEXTN_CATEGORY add constraint FKD33DE81B54A9F59D foreign key (ROOT_CATEGORY_ELEMENT) references DYEXTN_CATEGORY_ENTITY (IDENTIFIER);
alter table DYEXTN_CATEGORY add constraint FKD33DE81B728B19BE foreign key (IDENTIFIER) references DYEXTN_ABSTRACT_METADATA (IDENTIFIER);
alter table DYEXTN_CATEGORY_ASSOCIATION add constraint FK1B7C663D854AC01B foreign key (CATEGORY_ENTITY_ID) references DYEXTN_CATEGORY_ENTITY (IDENTIFIER);
alter table DYEXTN_CATEGORY_ASSOCIATION add constraint FK1B7C663DCAC769C5 foreign key (CATEGORY_ENTIY_ID) references DYEXTN_CATEGORY_ENTITY (IDENTIFIER);
alter table DYEXTN_CATEGORY_ASSOCIATION add constraint FK1B7C663D5CC8694E foreign key (IDENTIFIER) references DYEXTN_BASE_ABSTRACT_ATTRIBUTE (IDENTIFIER);
alter table DYEXTN_CATEGORY_ATTRIBUTE add constraint FKEF3B7758854AC01B foreign key (CATEGORY_ENTITY_ID) references DYEXTN_CATEGORY_ENTITY (IDENTIFIER);
alter table DYEXTN_CATEGORY_ATTRIBUTE add constraint FKEF3B7758A2CA8853 foreign key (CAL_DEPENDENT_CATEGORY_ATTR_ID) references DYEXTN_CATEGORY_ATTRIBUTE (IDENTIFIER);
alter table DYEXTN_CATEGORY_ATTRIBUTE add constraint FKEF3B77585697A453 foreign key (CAL_CATEGORY_ATTR_ID) references DYEXTN_CATEGORY_ATTRIBUTE (IDENTIFIER);
alter table DYEXTN_CATEGORY_ATTRIBUTE add constraint FKEF3B77584DC2CD16 foreign key (ABSTRACT_ATTRIBUTE_ID) references DYEXTN_ATTRIBUTE (IDENTIFIER);
alter table DYEXTN_CATEGORY_ATTRIBUTE add constraint FKEF3B7758CAC769C5 foreign key (CATEGORY_ENTIY_ID) references DYEXTN_CATEGORY_ENTITY (IDENTIFIER);
alter table DYEXTN_CATEGORY_ATTRIBUTE add constraint FKEF3B77585CC8694E foreign key (IDENTIFIER) references DYEXTN_BASE_ABSTRACT_ATTRIBUTE (IDENTIFIER);
alter table DYEXTN_CATEGORY_ENTITY add constraint FK9F2DA887A52559D0 foreign key (PARENT_CATEGORY_ENTITY_ID) references DYEXTN_CATEGORY_ENTITY (IDENTIFIER);
alter table DYEXTN_CATEGORY_ENTITY add constraint FK9F2DA8874AC41F7E foreign key (ENTITY_ID) references DYEXTN_ENTITY (IDENTIFIER);
alter table DYEXTN_CATEGORY_ENTITY add constraint FK9F2DA887F5A32608 foreign key (REL_ATTR_CAT_ENTITY_ID) references DYEXTN_CATEGORY (IDENTIFIER);
alter table DYEXTN_CATEGORY_ENTITY add constraint FK9F2DA887A8103C6F foreign key (TREE_PARENT_CATEGORY_ENTITY_ID) references DYEXTN_CATEGORY_ENTITY (IDENTIFIER);
alter table DYEXTN_CATEGORY_ENTITY add constraint FK9F2DA887FB6EB979 foreign key (CATEGORY_ASSOCIATION_ID) references DYEXTN_CATEGORY_ASSOCIATION (IDENTIFIER);
alter table DYEXTN_CATEGORY_ENTITY add constraint FK9F2DA887D06EE657 foreign key (OWN_PARENT_CATEGORY_ENTITY_ID) references DYEXTN_CATEGORY_ENTITY (IDENTIFIER);
alter table DYEXTN_CATEGORY_ENTITY add constraint FK9F2DA887743AC3F2 foreign key (IDENTIFIER) references DYEXTN_ABSTRACT_ENTITY (ID);
alter table DYEXTN_CAT_ASSO_CTL add constraint FK281FAB50C45A8CFA foreign key (IDENTIFIER) references DYEXTN_ABSTR_CONTAIN_CTR (IDENTIFIER);
alter table DYEXTN_CHECK_BOX add constraint FK4EFF925740F198C2 foreign key (IDENTIFIER) references DYEXTN_CONTROL (IDENTIFIER);
alter table DYEXTN_COLUMN_PROPERTIES add constraint FK8FCE2B3F1333996E foreign key (PRIMITIVE_ATTRIBUTE_ID) references DYEXTN_PRIMITIVE_ATTRIBUTE (IDENTIFIER);
alter table DYEXTN_COLUMN_PROPERTIES add constraint FK8FCE2B3F67F8B59 foreign key (CATEGORY_ATTRIBUTE_ID) references DYEXTN_CATEGORY_ATTRIBUTE (IDENTIFIER);
alter table DYEXTN_COLUMN_PROPERTIES add constraint FK8FCE2B3F3AB6A1D3 foreign key (IDENTIFIER) references DYEXTN_DATABASE_PROPERTIES (IDENTIFIER);
alter table DYEXTN_COMBOBOX add constraint FKABBC649ABF67AB26 foreign key (IDENTIFIER) references DYEXTN_SELECT_CONTROL (IDENTIFIER);
alter table DYEXTN_CONSTRAINT_PROPERTIES add constraint FK82886CD87EE87FF6 foreign key (ASSOCIATION_ID) references DYEXTN_ASSOCIATION (IDENTIFIER);
alter table DYEXTN_CONSTRAINT_PROPERTIES add constraint FK82886CD8FB6EB979 foreign key (CATEGORY_ASSOCIATION_ID) references DYEXTN_CATEGORY_ASSOCIATION (IDENTIFIER);
alter table DYEXTN_CONSTRAINT_PROPERTIES add constraint FK82886CD83AB6A1D3 foreign key (IDENTIFIER) references DYEXTN_DATABASE_PROPERTIES (IDENTIFIER);
alter table DYEXTN_CONTAINER add constraint FK1EAB84E41DCC9E63 foreign key (ABSTRACT_ENTITY_ID) references DYEXTN_ABSTRACT_ENTITY (ID);
alter table DYEXTN_CONTAINER add constraint FK1EAB84E4178865E foreign key (VIEW_ID) references DYEXTN_VIEW (IDENTIFIER);
alter table DYEXTN_CONTAINER add constraint FK1EAB84E440FCA34B foreign key (PARENT_CONTAINER_ID) references DYEXTN_CONTAINER (IDENTIFIER);
alter table DYEXTN_CONTAINER add constraint FK1EAB84E488C075EF foreign key (ENTITY_GROUP_ID) references DYEXTN_ENTITY_GROUP (IDENTIFIER);
alter table DYEXTN_CONTAINER add constraint FK1EAB84E4BF901C84 foreign key (BASE_CONTAINER_ID) references DYEXTN_CONTAINER (IDENTIFIER);
alter table DYEXTN_CONTAINMENT_CONTROL add constraint FK3F9D4AD3C45A8CFA foreign key (IDENTIFIER) references DYEXTN_ABSTR_CONTAIN_CTR (IDENTIFIER);
alter table DYEXTN_CONTROL add constraint FK70FB5E807792A78F foreign key (BASE_ABST_ATR_ID) references DYEXTN_BASE_ABSTRACT_ATTRIBUTE (IDENTIFIER);
alter table DYEXTN_CONTROL add constraint FK70FB5E8069935DD6 foreign key (CONTAINER_ID) references DYEXTN_CONTAINER (IDENTIFIER);
alter table DYEXTN_DATA_ELEMENT add constraint FKB1153E4AA208204 foreign key (ATTRIBUTE_TYPE_INFO_ID) references DYEXTN_ATTRIBUTE_TYPE_INFO (IDENTIFIER);
alter table DYEXTN_DATA_ELEMENT add constraint FKB1153E467F8B59 foreign key (CATEGORY_ATTRIBUTE_ID) references DYEXTN_CATEGORY_ATTRIBUTE (IDENTIFIER);
alter table DYEXTN_DATA_GRID add constraint FK233EB73E40F198C2 foreign key (IDENTIFIER) references DYEXTN_CONTROL (IDENTIFIER);
alter table DYEXTN_DATEPICKER add constraint FKFEADD19940F198C2 foreign key (IDENTIFIER) references DYEXTN_CONTROL (IDENTIFIER);
alter table DYEXTN_DATE_CONCEPT_VALUE add constraint FK45F598A64641D513 foreign key (IDENTIFIER) references DYEXTN_PERMISSIBLE_VALUE (IDENTIFIER);
alter table DYEXTN_DATE_TYPE_INFO add constraint FKFBA549FE5294FA3 foreign key (IDENTIFIER) references DYEXTN_ATTRIBUTE_TYPE_INFO (IDENTIFIER);
alter table DYEXTN_DOUBLE_CONCEPT_VALUE add constraint FKB94E64494641D513 foreign key (IDENTIFIER) references DYEXTN_PERMISSIBLE_VALUE (IDENTIFIER);
alter table DYEXTN_DOUBLE_TYPE_INFO add constraint FKC83869C2BA4AE008 foreign key (IDENTIFIER) references DYEXTN_NUMERIC_TYPE_INFO (IDENTIFIER);
alter table DYEXTN_ENTITY add constraint FK8B24364088C075EF foreign key (ENTITY_GROUP_ID) references DYEXTN_ENTITY_GROUP (IDENTIFIER);
alter table DYEXTN_ENTITY add constraint FK8B2436402264D629 foreign key (PARENT_ENTITY_ID) references DYEXTN_ENTITY (IDENTIFIER);
alter table DYEXTN_ENTITY add constraint FK8B243640743AC3F2 foreign key (IDENTIFIER) references DYEXTN_ABSTRACT_ENTITY (ID);
alter table DYEXTN_ENTITY_GROUP add constraint FK105DE7A0728B19BE foreign key (IDENTIFIER) references DYEXTN_ABSTRACT_METADATA (IDENTIFIER);
alter table DYEXTN_ENTITY_MAP_CONDNS add constraint FK2A9D6029CFA08B13 foreign key (FORM_CONTEXT_ID) references DYEXTN_FORM_CONTEXT (IDENTIFIER);
alter table DYEXTN_ENTITY_MAP_RECORD add constraint FK43A45013CFA08B13 foreign key (FORM_CONTEXT_ID) references DYEXTN_FORM_CONTEXT (IDENTIFIER);
alter table DYEXTN_FILE_EXTENSIONS add constraint FKD49834FA56AF0834 foreign key (ATTRIBUTE_ID) references DYEXTN_FILE_TYPE_INFO (IDENTIFIER);
alter table DYEXTN_FILE_TYPE_INFO add constraint FKA00F0EDE5294FA3 foreign key (IDENTIFIER) references DYEXTN_ATTRIBUTE_TYPE_INFO (IDENTIFIER);
alter table DYEXTN_FILE_UPLOAD add constraint FK2FAD41E740F198C2 foreign key (IDENTIFIER) references DYEXTN_CONTROL (IDENTIFIER);
alter table DYEXTN_FLOAT_CONCEPT_VALUE add constraint FK6785309A4641D513 foreign key (IDENTIFIER) references DYEXTN_PERMISSIBLE_VALUE (IDENTIFIER);
alter table DYEXTN_FLOAT_TYPE_INFO add constraint FK7E1C0693BA4AE008 foreign key (IDENTIFIER) references DYEXTN_NUMERIC_TYPE_INFO (IDENTIFIER);
alter table DYEXTN_FORMULA add constraint FKFE34A8967F8B59 foreign key (CATEGORY_ATTRIBUTE_ID) references DYEXTN_CATEGORY_ATTRIBUTE (IDENTIFIER);
alter table DYEXTN_FORM_CONTEXT add constraint FKE56CCDB12B784475 foreign key (ENTITY_MAP_ID) references DYEXTN_ENTITY_MAP (IDENTIFIER);
alter table DYEXTN_FORM_CTRL_NOTES add constraint FK7A0DA06B41D885B1 foreign key (FORM_CONTROL_ID) references DYEXTN_CONTROL (IDENTIFIER);
alter table DYEXTN_INTEGER_CONCEPT_VALUE add constraint FKFBA33B3C4641D513 foreign key (IDENTIFIER) references DYEXTN_PERMISSIBLE_VALUE (IDENTIFIER);
alter table DYEXTN_INTEGER_TYPE_INFO add constraint FK5F9CB235BA4AE008 foreign key (IDENTIFIER) references DYEXTN_NUMERIC_TYPE_INFO (IDENTIFIER);
alter table DYEXTN_LABEL add constraint FK787668D740F198C2 foreign key (IDENTIFIER) references DYEXTN_CONTROL (IDENTIFIER);
alter table DYEXTN_LIST_BOX add constraint FK208395A7BF67AB26 foreign key (IDENTIFIER) references DYEXTN_SELECT_CONTROL (IDENTIFIER);
alter table DYEXTN_LONG_CONCEPT_VALUE add constraint FK3E1A6EF44641D513 foreign key (IDENTIFIER) references DYEXTN_PERMISSIBLE_VALUE (IDENTIFIER);
alter table DYEXTN_LONG_TYPE_INFO add constraint FK257281EDBA4AE008 foreign key (IDENTIFIER) references DYEXTN_NUMERIC_TYPE_INFO (IDENTIFIER);
alter table DYEXTN_NUMERIC_TYPE_INFO add constraint FK4DEC9544E5294FA3 foreign key (IDENTIFIER) references DYEXTN_ATTRIBUTE_TYPE_INFO (IDENTIFIER);
alter table DYEXTN_OBJECT_TYPE_INFO add constraint FK74819FB0E5294FA3 foreign key (IDENTIFIER) references DYEXTN_ATTRIBUTE_TYPE_INFO (IDENTIFIER);
alter table DYEXTN_PATH add constraint FKC26ADC2854AC01B foreign key (CATEGORY_ENTITY_ID) references DYEXTN_CATEGORY_ENTITY (IDENTIFIER);
alter table DYEXTN_PATH_ASSO_REL add constraint FK1E1260A57EE87FF6 foreign key (ASSOCIATION_ID) references DYEXTN_ASSOCIATION (IDENTIFIER);
alter table DYEXTN_PATH_ASSO_REL add constraint FK1E1260A580C8F93E foreign key (PATH_ID) references DYEXTN_PATH (IDENTIFIER);
alter table DYEXTN_PERMISSIBLE_VALUE add constraint FK136264E0AA208204 foreign key (ATTRIBUTE_TYPE_INFO_ID) references DYEXTN_ATTRIBUTE_TYPE_INFO (IDENTIFIER);
alter table DYEXTN_PERMISSIBLE_VALUE add constraint FK136264E067F8B59 foreign key (CATEGORY_ATTRIBUTE_ID) references DYEXTN_CATEGORY_ATTRIBUTE (IDENTIFIER);
alter table DYEXTN_PRIMITIVE_ATTRIBUTE add constraint FKA9F765C76D19A21F foreign key (IDENTIFIER) references DYEXTN_ATTRIBUTE (IDENTIFIER);
alter table DYEXTN_RADIOBUTTON add constraint FK16F5BA9040F198C2 foreign key (IDENTIFIER) references DYEXTN_CONTROL (IDENTIFIER);
alter table DYEXTN_RULE add constraint FKC27E09990F96714 foreign key (ATTRIBUTE_ID) references DYEXTN_ATTRIBUTE (IDENTIFIER);
alter table DYEXTN_RULE_PARAMETER add constraint FK2256736395D4A5AE foreign key (RULE_ID) references DYEXTN_RULE (IDENTIFIER);
alter table DYEXTN_SELECT_CONTROL add constraint FKDFEBB65740F198C2 foreign key (IDENTIFIER) references DYEXTN_CONTROL (IDENTIFIER);
alter table DYEXTN_SEMANTIC_PROPERTY add constraint FKD2A0B5B15EB60E90 foreign key (ABSTRACT_VALUE_ID) references DYEXTN_PERMISSIBLE_VALUE (IDENTIFIER);
alter table DYEXTN_SEMANTIC_PROPERTY add constraint FKD2A0B5B19AEB0CA3 foreign key (ABSTRACT_METADATA_ID) references DYEXTN_ABSTRACT_METADATA (IDENTIFIER);
alter table DYEXTN_SHORT_CONCEPT_VALUE add constraint FKC1945ABA4641D513 foreign key (IDENTIFIER) references DYEXTN_PERMISSIBLE_VALUE (IDENTIFIER);
alter table DYEXTN_SHORT_TYPE_INFO add constraint FK99540B3BA4AE008 foreign key (IDENTIFIER) references DYEXTN_NUMERIC_TYPE_INFO (IDENTIFIER);
alter table DYEXTN_STRING_CONCEPT_VALUE add constraint FKADE7D8894641D513 foreign key (IDENTIFIER) references DYEXTN_PERMISSIBLE_VALUE (IDENTIFIER);
alter table DYEXTN_STRING_TYPE_INFO add constraint FKDA35FE02E5294FA3 foreign key (IDENTIFIER) references DYEXTN_ATTRIBUTE_TYPE_INFO (IDENTIFIER);
alter table DYEXTN_TABLE_PROPERTIES add constraint FKE608E0811DCC9E63 foreign key (ABSTRACT_ENTITY_ID) references DYEXTN_ABSTRACT_ENTITY (ID);
alter table DYEXTN_TABLE_PROPERTIES add constraint FKE608E0813AB6A1D3 foreign key (IDENTIFIER) references DYEXTN_DATABASE_PROPERTIES (IDENTIFIER);
alter table DYEXTN_TAGGED_VALUE add constraint FKF79D055B9AEB0CA3 foreign key (ABSTRACT_METADATA_ID) references DYEXTN_ABSTRACT_METADATA (IDENTIFIER);
alter table DYEXTN_TEXTAREA add constraint FK946EE25740F198C2 foreign key (IDENTIFIER) references DYEXTN_CONTROL (IDENTIFIER);
alter table DYEXTN_TEXTFIELD add constraint FKF9AFC85040F198C2 foreign key (IDENTIFIER) references DYEXTN_CONTROL (IDENTIFIER);
alter table DYEXTN_USERDEFINED_DE add constraint FK630761FF53CC4A77 foreign key (IDENTIFIER) references DYEXTN_DATA_ELEMENT (IDENTIFIER);
alter table DYEXTN_USERDEF_DE_VALUE_REL add constraint FK3EE58DCF5521B106 foreign key (USER_DEF_DE_ID) references DYEXTN_USERDEFINED_DE (IDENTIFIER);
alter table DYEXTN_USERDEF_DE_VALUE_REL add constraint FK3EE58DCF49BDD67 foreign key (PERMISSIBLE_VALUE_ID) references DYEXTN_PERMISSIBLE_VALUE (IDENTIFIER);
