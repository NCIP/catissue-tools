DROP TABLE  CURATED_PATH CASCADE CONSTRAINTS; 
DROP TABLE  CURATED_PATH_TO_PATH CASCADE CONSTRAINTS;
DROP TABLE PATH CASCADE CONSTRAINTS;
DROP TABLE INTER_MODEL_ASSOCIATION CASCADE CONSTRAINTS;
DROP TABLE INTRA_MODEL_ASSOCIATION CASCADE CONSTRAINTS;
DROP TABLE ASSOCIATION CASCADE CONSTRAINTS;
DROP TABLE ID_TABLE CASCADE CONSTRAINTS;

/*INTERMEDIATE_PATH contains  ASSOCIATION(ASSOCIATION_ID) connected by underscore */
CREATE TABLE PATH(
     PATH_ID           NUMBER(38,0)         NOT NULL,
     FIRST_ENTITY_ID   NUMBER(38,0)         NULL,
     INTERMEDIATE_PATH VARCHAR2(1000)  NULL,
     LAST_ENTITY_ID    NUMBER(38,0)         NULL,
     PRIMARY KEY (PATH_ID)
);
/* Possible values for ASSOCIATION_TYPE are 1 and 2
ASSOCIATION_TYPE = 1 represents INTER_MODEL_ASSOCIATION.
ASSOCIATION_TYPE = 2 represents INTRA_MODEL_ASSOCIATION.
*/     
CREATE TABLE ASSOCIATION(
    ASSOCIATION_ID    NUMBER(38,0)    NOT NULL,
    ASSOCIATION_TYPE  NUMBER(8,0)    NOT NULL ,
    PRIMARY KEY (ASSOCIATION_ID)
);

CREATE TABLE INTER_MODEL_ASSOCIATION(
    ASSOCIATION_ID      NUMBER(38,0)  NOT NULL REFERENCES ASSOCIATION(ASSOCIATION_ID),
    LEFT_ENTITY_ID      NUMBER(38,0)  NOT NULL,
    LEFT_ATTRIBUTE_ID   NUMBER(38,0)  NOT NULL,
    RIGHT_ENTITY_ID     NUMBER(38,0)  NOT NULL,
    RIGHT_ATTRIBUTE_ID  NUMBER(38,0)  NOT NULL,
    PRIMARY KEY (ASSOCIATION_ID) 
);
CREATE TABLE INTRA_MODEL_ASSOCIATION(
    ASSOCIATION_ID    NUMBER(38,0)    NOT NULL REFERENCES ASSOCIATION(ASSOCIATION_ID),
    DE_ASSOCIATION_ID NUMBER(38,0)    NOT NULL,
    PRIMARY KEY (ASSOCIATION_ID) 
);
CREATE TABLE ID_TABLE(
    NEXT_ASSOCIATION_ID    NUMBER(38,0)    NOT NULL,
    PRIMARY KEY (NEXT_ASSOCIATION_ID)
);
CREATE TABLE CURATED_PATH (
	curated_path_Id NUMBER(38,0),
	entity_ids VARCHAR2(1000),
	selected NUMBER(1,0),
	PRIMARY KEY (curated_path_Id)
);

/*This is mapping table for many-to-many relationship between tables PATH and CURATED_PATH */
CREATE TABLE CURATED_PATH_TO_PATH (
	curated_path_Id NUMBER(38,0) REFERENCES CURATED_PATH (curated_path_Id),
	path_id NUMBER(38,0)  REFERENCES PATH (path_id),
	PRIMARY KEY (curated_path_Id,path_id)
);

CREATE INDEX FK9651EF32D8D56A78 ON PATH (FIRST_ENTITY_ID,LAST_ENTITY_ID);
INSERT INTO ID_TABLE(NEXT_ASSOCIATION_ID) VALUES(1);