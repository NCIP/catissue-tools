/*---- SuiteQuery Scripts Begin ---*/
insert into dyextn_abstract_metadata select max(identifier)+1 ,to_date('2005-08-22','yyyy-mm-dd'),'clinicalstudy',to_date('2005-08-22','yyyy-mm-dd'),'edu.wustl.clinportal.domain.ClinicalStudy',null from dyextn_abstract_metadata;
insert into dyextn_abstract_metadata select max(identifier)+1 ,to_date('2005-08-22','yyyy-mm-dd'),'clinicalstudy',to_date('2005-08-22','yyyy-mm-dd'),'edu.wustl.clinportal.domain.ClinicalStudyRegistration',null from dyextn_abstract_metadata;
insert into dyextn_abstract_metadata select max(identifier)+1 ,to_date('2005-08-22','yyyy-mm-dd'),'clinicalstudy',to_date('2005-08-22','yyyy-mm-dd'),'edu.wustl.clinportal.domain.ClinicalStudyEvent', null from dyextn_abstract_metadata;
insert into dyextn_abstract_metadata select max(identifier)+1 ,to_date('2005-08-22','yyyy-mm-dd'),'clinicalstudy',to_date('2005-08-22','yyyy-mm-dd'),'edu.wustl.clinportal.domain.RecordEntry', null from dyextn_abstract_metadata;


insert into dyextn_abstract_entity select identifier from dyextn_abstract_metadata where name='edu.wustl.clinportal.domain.ClinicalStudy';
insert into dyextn_abstract_entity select identifier from dyextn_abstract_metadata where name='edu.wustl.clinportal.domain.ClinicalStudyEvent';
insert into dyextn_abstract_entity select identifier from dyextn_abstract_metadata where name='edu.wustl.clinportal.domain.ClinicalStudyRegistration';
insert into dyextn_abstract_entity select identifier from dyextn_abstract_metadata where name='edu.wustl.clinportal.domain.RecordEntry';

insert into dyextn_entity select identifier,2,1,0,null,3,null,null from dyextn_abstract_metadata where name='edu.wustl.clinportal.domain.ClinicalStudy';
insert into dyextn_entity select identifier,2,1,0,null,3,null,null from dyextn_abstract_metadata where name='edu.wustl.clinportal.domain.ClinicalStudyEvent';
insert into dyextn_entity select identifier,2,1,0,null,3,null,null from dyextn_abstract_metadata where name='edu.wustl.clinportal.domain.ClinicalStudyRegistration';
insert into dyextn_entity select identifier,2,1,0,null,3,null,null from dyextn_abstract_metadata where name='edu.wustl.clinportal.domain.RecordEntry';


/*---- SuiteQuery Scripts End---*/