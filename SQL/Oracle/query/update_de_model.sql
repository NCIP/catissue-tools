--Drop this column as  per new model
ALTER TABLE DYEXTN_PRIMITIVE_ATTRIBUTE DROP COLUMN IS_COLLECTION;
ALTER TABLE DYEXTN_ASSOCIATION ADD (IS_COLLECTION number(1,0) default 0);