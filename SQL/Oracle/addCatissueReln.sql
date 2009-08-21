--Add column to table for relating CS to CP
ALTER TABLE CATISSUE_CLINICAL_STUDY ADD (REL_CP_ID number(19,0));

--Add column to table for relating CSE to CPE
ALTER TABLE CATISSUE_CLINICAL_STUDY_EVENT ADD (REL_CPE_ID number(19,0));