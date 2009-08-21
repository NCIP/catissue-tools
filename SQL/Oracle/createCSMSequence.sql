declare
    i number :=1; j number :=1;k number :=1;l number :=1;
    sql_stmt varchar2(150);
begin

    select nvl(max(APPLICATION_ID),0)+1 into i from CSM_APPLICATION;
    sql_stmt :='create sequence CSM_APPLICATI_APPLICATION__SEQ start with ' || i ;
    execute immediate sql_stmt;
    
    select nvl(max(GROUP_ID),0)+1 into i from CSM_GROUP;
    sql_stmt :='create sequence CSM_GROUP_GROUP_ID_SEQ start with ' || i ;
    execute immediate sql_stmt;

    select nvl(max(PG_PE_ID),0)+1 into i from CSM_PG_PE;
    sql_stmt :='create sequence CSM_PG_PE_PG_PE_ID_SEQ start with ' || i ;
    execute immediate sql_stmt;

    select nvl(max(PROTECTION_ELEMENT_ID),0)+1 into i from CSM_PROTECTION_ELEMENT;
    sql_stmt :='create sequence CSM_PROTECTIO_PROTECTION_E_SEQ start with ' || i ;
    execute immediate sql_stmt;        

    select nvl(max(PROTECTION_GROUP_ID),0)+1 into i from CSM_PROTECTION_GROUP;
    sql_stmt :='create sequence CSM_PROTECTIO_PROTECTION_G_SEQ start with ' || i ;
    execute immediate sql_stmt;   
    
    select nvl(max(ROLE_ID),0)+1 into i from CSM_ROLE;
    sql_stmt :='create sequence CSM_ROLE_ROLE_ID_SEQ start with ' || i ;
    execute immediate sql_stmt;   
    
    select nvl(max(ROLE_PRIVILEGE_ID),0)+1 into i from CSM_ROLE_PRIVILEGE;
    sql_stmt :='create sequence CSM_ROLE_PRIV_ROLE_PRIVILE_SEQ start with ' || i ;
    execute immediate sql_stmt;   

        select nvl(max(USER_GROUP_ID),0)+1 into i from CSM_USER_GROUP;
    sql_stmt :='create sequence CSM_USER_GROU_USER_GROUP_I_SEQ start with ' || i ;
    execute immediate sql_stmt;   
    
        select nvl(max(USER_GROUP_ROLE_PG_ID),0)+1 into i from CSM_USER_GROUP_ROLE_PG;
    sql_stmt :='create sequence CSM_USER_GROU_USER_GROUP_R_SEQ start with ' || i ;
    execute immediate sql_stmt;   


   select nvl(max(USER_ID),0)+1 into i from CSM_USER;
    sql_stmt :='create sequence CSM_USER_USER_ID_SEQ start with ' || i ;
    execute immediate sql_stmt;   
    
    select nvl(max(PRIVILEGE_ID),0)+1 into i from CSM_PRIVILEGE;
    sql_stmt :='create sequence CSM_PRIVILEGE_PRIVILEGE_ID_SEQ start with ' || i ;
    execute immediate sql_stmt;   
    

 end;