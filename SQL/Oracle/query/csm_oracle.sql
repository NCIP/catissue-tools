update dyextn_role set ASSOCIATION_TYPE = 'ASSOCIATION';
update dyextn_role set association_type='CONTAINTMENT' where identifier in (31);
insert into dyextn_tagged_value select DYEXTN_TAGGED_VALUE_SEQ.NEXTVAL, 'PRIVILEGE_ID','1',61 from dyextn_tagged_value;
insert into dyextn_tagged_value select DYEXTN_TAGGED_VALUE_SEQ.NEXTVAL, 'PRIVILEGE_ID','1',48 from dyextn_tagged_value;
insert into dyextn_tagged_value select DYEXTN_TAGGED_VALUE_SEQ.NEXTVAL, 'PRIVILEGE_ID','1',57 from dyextn_tagged_value;
insert into dyextn_tagged_value select DYEXTN_TAGGED_VALUE_SEQ.NEXTVAL, 'PRIVILEGE_ID','2',109 from dyextn_tagged_value;
insert into dyextn_tagged_value select DYEXTN_TAGGED_VALUE_SEQ.NEXTVAL, 'PRIVILEGE_ID','2',35 from dyextn_tagged_value;
insert into dyextn_tagged_value select DYEXTN_TAGGED_VALUE_SEQ.NEXTVAL, 'PRIVILEGE_ID','2',2 from dyextn_tagged_value;
insert into dyextn_tagged_value select DYEXTN_TAGGED_VALUE_SEQ.NEXTVAL, 'PRIVILEGE_ID','2',33 from dyextn_tagged_value;
insert into dyextn_tagged_value select DYEXTN_TAGGED_VALUE_SEQ.NEXTVAL, 'PRIVILEGE_ID','2',32 from dyextn_tagged_value;
insert into dyextn_tagged_value select DYEXTN_TAGGED_VALUE_SEQ.NEXTVAL, 'PRIVILEGE_ID','2',91 from dyextn_tagged_value;
insert into dyextn_tagged_value select DYEXTN_TAGGED_VALUE_SEQ.NEXTVAL, 'PRIVILEGE_ID','2',106 from dyextn_tagged_value;
insert into dyextn_tagged_value select DYEXTN_TAGGED_VALUE_SEQ.NEXTVAL, 'PRIVILEGE_ID','2',13 from dyextn_tagged_value;
insert into dyextn_tagged_value select DYEXTN_TAGGED_VALUE_SEQ.NEXTVAL, 'PRIVILEGE_ID','2',15 from dyextn_tagged_value;
UPDATE dyextn_primitive_attribute SET IS_IDENTIFIED =1 where IDENTIFIER in (119,95,103,101,97,94,100,117,87);