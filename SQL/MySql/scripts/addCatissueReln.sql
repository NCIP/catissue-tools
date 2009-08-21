--Add column to table for relating CS to CP
ALTER TABLE CATISSUE_CLINICAL_STUDY ADD COLUMN REL_CP_ID bigint;

--Add column to table for relating CSE to CPE
ALTER TABLE CATISSUE_CLINICAL_STUDY_EVENT ADD COLUMN REL_CPE_ID bigint;