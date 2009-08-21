
/*---- SuiteQuery Scripts Begin ---*/
insert into `dyextn_abstract_metadata` select max(identifier)+1 ,'2007-04-16','clinicalstudy','2007-04-16','edu.wustl.catissuecore.domain.ClinicalStudy',null from dyextn_abstract_metadata;
insert into `dyextn_abstract_metadata` select max(identifier)+1 ,'2007-04-16','clinicalstudy','2007-04-16','edu.wustl.catissuecore.domain.ClinicalStudyRegistration',null from dyextn_abstract_metadata;
insert into `dyextn_abstract_metadata` select max(identifier)+1 ,'2007-04-16','clinicalstudy','2007-04-16','edu.wustl.catissuecore.domain.ClinicalStudyEvent', null from dyextn_abstract_metadata;
insert into `dyextn_abstract_metadata` select max(identifier)+1 ,'2007-04-16','recordEntry','2007-04-16','edu.wustl.catissuecore.domain.RecordEntry', null from dyextn_abstract_metadata;

insert into dyextn_entity  (IDENTIFIER,DATA_TABLE_STATE,IS_ABSTRACT,PARENT_ENTITY_ID,INHERITANCE_STRATEGY,DISCRIMINATOR_COLUMN_NAME,DISCRIMINATOR_VALUE )  values ((select identifier from dyextn_abstract_metadata where name='edu.wustl.catissuecore.domain.ClinicalStudy'),2,0,null,3,null,null);
insert into dyextn_entity  (IDENTIFIER,DATA_TABLE_STATE,IS_ABSTRACT,PARENT_ENTITY_ID,INHERITANCE_STRATEGY,DISCRIMINATOR_COLUMN_NAME,DISCRIMINATOR_VALUE )  values ((select identifier from dyextn_abstract_metadata where name='edu.wustl.catissuecore.domain.ClinicalStudyEvent'),2,0,null,3,null,null);
insert into dyextn_entity  (IDENTIFIER,DATA_TABLE_STATE,IS_ABSTRACT,PARENT_ENTITY_ID,INHERITANCE_STRATEGY,DISCRIMINATOR_COLUMN_NAME,DISCRIMINATOR_VALUE )  values ((select identifier from dyextn_abstract_metadata where name='edu.wustl.catissuecore.domain.ClinicalStudyRegistration'),2,0,null,3,null,null);
insert into dyextn_entity  (IDENTIFIER,DATA_TABLE_STATE,IS_ABSTRACT,PARENT_ENTITY_ID,INHERITANCE_STRATEGY,DISCRIMINATOR_COLUMN_NAME,DISCRIMINATOR_VALUE )  values ((select identifier from dyextn_abstract_metadata where name='edu.wustl.catissuecore.domain.RecordEntry'),2,0,null,3,null,null);
/*---- SuiteQuery Scripts End---*/