package edu.wustl.clinportal.privilege;

public class PrivilegeConstants 
{
    public static final String ELEMENT_CLINICAL_STUDY = "clinical-study";
    public static final String ELEMENT_CSUSERS = "csUsers";
    public static final String ELEMENT_CLINICAL_STUDY_TITLE = "title";
    public static final String ELEMENT_USER = "user";
    public static final String ELEMENT_USER_LOGIN_NAME = "login-name";
    public static final String ELEMENT_USER_PRIVILEGE = "permission";
    public static final String PRIVILEGE_ALLOW = "allow";
    public static final String PRIVILEGE_DENY = "deny";
    public static final String ELEMENT_EVENT = "event";
    public static final String ELEMENT_LABEL = "label";
    public static final String ELEMENT_STUDYFORM = "studyForm";
    
    public static final String ELEMENT_CSNONUSERS = "csNonUsers";
    
    
    public static final String CLINICAL_STUDY_PROTECTION_GROUP = "CLINICAL_STUDY_";
    public static final String PI_USER_GROUP = "PI_CLINICAL_STUDY_";
    public static final String CO0RDINATOR_USER_GROUP = "COORDINATORS_CLINICAL_STUDY_";
    public static final String USER_PROTECTION_GROUP = "USER_";
    
    public static final String CLINICAL_STUDY_TITLE = "title";
    public static final String USER_ATTRIBUTE_EMAILADDRESS = "emailAddress";
    
    public static final String ELEMENT_REPORT = "report";   
    public static final String ELEMENT_REPORT_NAME = "name";
    public static final String REPORT_ATTRIBUTE_NAME = "reportName";
    public static final String REPORT_PROTECTION_GROUP = "REPORT_";
    
    public static final String DIS_ALLOW_READ_WRITE="disallowReadWrite";
    public static final String ALLOW_READ="allowRead";
    public static final String ALLOW_READ_WRITE="allowReadWrite";
    
    public static final String UPDATE="UPDATE";
    public static final String READ="READ";
        
    public static final String getRoleName(String frmCntxtId, String userId)
    {
        String roleName = "ROLE_FORMCONTEXT_" + frmCntxtId + "_USER_" + userId;
        return roleName;
    }
    
    public static final String getFrmcntxtPG(String frmCntxtId)
    {
        String name = "FORMCONTEXT_" + frmCntxtId ;
        return name;
    }
    
    public static final String getUserGroup(String grpId)
    {
        String name = "FORM_USER_" + grpId;
        return name;
    }
    public static final String PARTICIPANT_REGISTRATION="participantRegistration";
    public static final String ALLOW_REG="ALLOW_REG";
    public static final String DISALLOW_REG="DIS_ALLOW_REG";
    public static final String XML_DISALLOW_REG="disallowParticipantRegistration";
    public static final String XML_ALLOW_REG="allowParticipantRegistration";
}
