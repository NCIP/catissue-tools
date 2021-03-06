/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */

		 
import java.text.ParseException;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;

import edu.wustl.clinportal.domain.Address;
import edu.wustl.clinportal.domain.Biohazard;
import edu.wustl.clinportal.domain.CancerResearchGroup;
import edu.wustl.clinportal.domain.Capacity;
import edu.wustl.clinportal.domain.CellSpecimen;
import edu.wustl.clinportal.domain.CollectionEventParameters;
import edu.wustl.clinportal.domain.CollectionProtocol;
import edu.wustl.clinportal.domain.CollectionProtocolEvent;
import edu.wustl.clinportal.domain.CollectionProtocolRegistration;
import edu.wustl.clinportal.domain.ConsentTier;
import edu.wustl.clinportal.domain.ConsentTierResponse;
import edu.wustl.clinportal.domain.ConsentTierStatus;
import edu.wustl.clinportal.domain.Department;
import edu.wustl.clinportal.domain.DistributedItem;
import edu.wustl.clinportal.domain.Distribution;
import edu.wustl.clinportal.domain.DistributionProtocol;
import edu.wustl.clinportal.domain.ExistingSpecimenOrderItem;
import edu.wustl.clinportal.domain.ExternalIdentifier;
import edu.wustl.clinportal.domain.FluidSpecimen;
import edu.wustl.clinportal.domain.Institution;
import edu.wustl.clinportal.domain.MolecularSpecimen;
import edu.wustl.clinportal.domain.NewSpecimenArrayOrderItem;
import edu.wustl.clinportal.domain.OrderDetails;
import edu.wustl.clinportal.domain.Participant;
import edu.wustl.clinportal.domain.Quantity;
import edu.wustl.clinportal.domain.ReceivedEventParameters;
import edu.wustl.clinportal.domain.Site;
import edu.wustl.clinportal.domain.Specimen;
import edu.wustl.clinportal.domain.SpecimenArray;
import edu.wustl.clinportal.domain.SpecimenArrayContent;
import edu.wustl.clinportal.domain.SpecimenArrayType;
import edu.wustl.clinportal.domain.SpecimenCharacteristics;
import edu.wustl.clinportal.domain.SpecimenCollectionGroup;
import edu.wustl.clinportal.domain.SpecimenOrderItem;
import edu.wustl.clinportal.domain.SpecimenRequirement;
import edu.wustl.clinportal.domain.StorageContainer;
import edu.wustl.clinportal.domain.StorageType;
import edu.wustl.clinportal.domain.TissueSpecimen;
import edu.wustl.clinportal.domain.User;
import edu.wustl.clinportal.util.EventsUtil;
import edu.wustl.common.util.Utility;
import edu.wustl.common.util.global.Constants;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.clinportal.domain.Specimen;


/**
 * @author ashish_gupta
 *
 */
public class APIDemo
{

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		/*
		Variables.applicationHome = System.getProperty("user.dir");
		Logger.out = org.apache.log4j.Logger.getLogger("");
		PropertyConfigurator.configure(Variables.applicationHome + "\\WEB-INF\\src\\"
				+ "ApplicationResources.properties");

		System
				.setProperty("gov.nih.nci.security.configFile",
						"C:/jboss-4.0.0/server/default/catissuecore-properties/ApplicationSecurityConfig.xml");
		System
				.setProperty("app.propertiesFile",
						"C:/jboss-4.0.0/server/default/catissuecore-properties/caTissueCore_Properties.xml");
		CDEManager.init();
		XMLPropertyHandler.init("caTissueCore_Properties.xml");
		ApplicationProperties.initBundle("ApplicationResources");

		APIDemo apiDemo = new APIDemo();
		SessionDataBean sessionDataBean = new SessionDataBean();
		sessionDataBean.setUserName("admin@admin.com");

		BizLogicFactory bizLogicFactory = BizLogicFactory.getInstance();

		//				Department dept = apiDemo.initDepartment();
		//				apiDemo.addData(bizLogicFactory, sessionDataBean, dept);
		//		
		//				Biohazard hazardObj=apiDemo.initBioHazard();
		//				apiDemo.addData(bizLogicFactory, sessionDataBean, hazardObj);
		//		
		//				CancerResearchGroup cancerResearchGroup = apiDemo.initCancerResearchGroup();
		//				apiDemo.addData(bizLogicFactory, sessionDataBean, cancerResearchGroup);
		//		
		//				Institution institution = apiDemo.initInstitution();
		//				apiDemo.addData(bizLogicFactory, sessionDataBean, institution);
		//		
		//				Site site = apiDemo.initSite();
		//				apiDemo.addData(bizLogicFactory, sessionDataBean, site);
		//		
		//				StorageType storageType = apiDemo.initStorageType();
		//				apiDemo.addData(bizLogicFactory, sessionDataBean, storageType);
		//		
		//				SpecimenArrayType specimenArrayType = apiDemo.initSpecimenArrayType();
		//				apiDemo.addData(bizLogicFactory, sessionDataBean, specimenArrayType);
		//		
		//				User user = apiDemo.initUser();
		//				apiDemo.addData(bizLogicFactory, sessionDataBean, user);
		//		
		//				Participant participant = apiDemo.initParticipant();
		//				apiDemo.addData(bizLogicFactory, sessionDataBean, participant);
		//		
		//				StorageContainer storageContainer = apiDemo.initStorageContainer();
		//				apiDemo.addData(bizLogicFactory, sessionDataBean, storageContainer);
		//		
		//				CollectionProtocol collectionProtocol = apiDemo.initCollectionProtocol();
		//				apiDemo.addData(bizLogicFactory, sessionDataBean, collectionProtocol);
		//		
		//				DistributionProtocol distributionProtocol = apiDemo.initDistributionProtocol();
		//				apiDemo.addData(bizLogicFactory, sessionDataBean, distributionProtocol);
		//		
		//			    CollectionProtocolRegistration collectionProtocolRegistration = apiDemo.initCollectionProtocolRegistration();
		//				apiDemo.addData(bizLogicFactory, sessionDataBean, collectionProtocolRegistration);

		//				SpecimenCollectionGroup specimenCollectionGroup = apiDemo.initSpecimenCollectionGroup();
		//				apiDemo.addData(bizLogicFactory, sessionDataBean, specimenCollectionGroup);

//						Specimen specimen = apiDemo.initSpecimen();
//						apiDemo.addData(bizLogicFactory, sessionDataBean, specimen);

						Distribution distribution = apiDemo.initDistribution();
						apiDemo.addData(bizLogicFactory, sessionDataBean, distribution);
		*/				
	}

	/**
	 * @param bizLogicFactory
	 * @param sessionDataBean
	 * @param obj
	 * @throws Exception
	private void addData(BizLogicFactory bizLogicFactory, SessionDataBean sessionDataBean,
			Object obj) throws Exception
	{
		IBizLogic bizLogic = bizLogicFactory.getBizLogic(obj.getClass().getName());
		bizLogic.insert(obj, sessionDataBean, Constants.HIBERNATE_DAO);
	}
	 */

	/**
	 * @return Department
	 */
	public Department initDepartment()
	{
		Department dept = new Department();
		dept.setName("dt" + UniqueKeyGeneratorUtil.getUniqueKey());
		return dept;
	}

	/**
	 * @return Biohazard
	 */
	public Biohazard initBioHazard()
	{
		Biohazard bioHazard = new Biohazard();
		bioHazard.setComment("NueroToxicProtein");
		bioHazard.setName("bh" + UniqueKeyGeneratorUtil.getUniqueKey());
		bioHazard.setType("Toxic");
		return bioHazard;
	}

	/**
	 * @return CancerResearchGroup
	 */
	public CancerResearchGroup initCancerResearchGroup()
	{
		CancerResearchGroup cancerResearchGroup = new CancerResearchGroup();
		cancerResearchGroup.setName("crg" + UniqueKeyGeneratorUtil.getUniqueKey());
		return cancerResearchGroup;
	}

	/**
	 * @return Institution
	 */
	public Institution initInstitution()
	{
		Institution institutionObj = new Institution();
		institutionObj.setName("inst" + UniqueKeyGeneratorUtil.getUniqueKey());
		return institutionObj;
	}

	/**
	 * @return User
	 */
	public User initUser()
	{
		User userObj = new User();
		userObj.setEmailAddress(UniqueKeyGeneratorUtil.getUniqueKey()+ "@admin.com");
		userObj.setLoginName(userObj.getEmailAddress());
		userObj.setLastName("last" + UniqueKeyGeneratorUtil.getUniqueKey());
		userObj.setFirstName("name" + UniqueKeyGeneratorUtil.getUniqueKey());

		Address address = new Address();
		address.setStreet("Main street");
		address.setCity("New hampshier");
		address.setState("D.C.");
		address.setZipCode("12345");
		address.setCountry("United States");
		address.setPhoneNumber("21222324");
		address.setFaxNumber("21222324");		
 
		userObj.setAddress(address);

//		Institution institution = new Institution();
//		institution.setId(new Long(1));
		Institution institution = (Institution) ClientDemo.dataModelObjectMap.get("Institution");
		userObj.setInstitution(institution);

//		Department department = new Department();
//		department.setId(new Long(1));
		Department department = (Department)ClientDemo.dataModelObjectMap.get("Department");
		userObj.setDepartment(department);

//		CancerResearchGroup cancerResearchGroup = new CancerResearchGroup();
//		cancerResearchGroup.setId(new Long(1));
		CancerResearchGroup cancerResearchGroup = (CancerResearchGroup)ClientDemo.dataModelObjectMap.get("CancerResearchGroup");
		userObj.setCancerResearchGroup(cancerResearchGroup);

		//userObj.setRoleId("1");
		//userObj.setComment("");
		userObj.setPageOf(Constants.PAGEOF_SIGNUP);
		//userObj.setActivityStatus("Active");
		//userObj.setCsmUserId(new Long(1));
		//userObj.setFirstTimeLogin(Boolean.valueOf(false));
		return userObj;
	}
	
	public User initAdminUser()
	{
		User userObj = new User();
		userObj.setEmailAddress(UniqueKeyGeneratorUtil.getUniqueKey()+ "@admin.com");
		userObj.setLoginName(userObj.getEmailAddress());
		userObj.setLastName("last" + UniqueKeyGeneratorUtil.getUniqueKey());
		userObj.setFirstName("name" + UniqueKeyGeneratorUtil.getUniqueKey());

		Address address = new Address();
		address.setStreet("Main street");
		address.setCity("New hampshier");
		address.setState("D.C.");
		address.setZipCode("12345");
		address.setCountry("United States");
		address.setPhoneNumber("21222324");
		address.setFaxNumber("21222324");		
 
		userObj.setAddress(address);

//		Institution institution = new Institution();
//		institution.setId(new Long(1));
		Institution institution = (Institution) ClientDemo.dataModelObjectMap.get("Institution");
		userObj.setInstitution(institution);

//		Department department = new Department();
//		department.setId(new Long(1));
		Department department = (Department)ClientDemo.dataModelObjectMap.get("Department");
		userObj.setDepartment(department);

//		CancerResearchGroup cancerResearchGroup = new CancerResearchGroup();
//		cancerResearchGroup.setId(new Long(1));
		CancerResearchGroup cancerResearchGroup = (CancerResearchGroup)ClientDemo.dataModelObjectMap.get("CancerResearchGroup");
		userObj.setCancerResearchGroup(cancerResearchGroup);

		userObj.setRoleId("1");
		userObj.setActivityStatus("Active");
		//userObj.setComment("");
		userObj.setPageOf(Constants.PAGEOF_USER_ADMIN);		
		//userObj.setCsmUserId(new Long(1));
		//userObj.setFirstTimeLogin(Boolean.valueOf(false));
		return userObj;
	}

	/**
	 * @return StorageType
	 */
	public StorageType initStorageType()
	{
		StorageType storageTypeObj = new StorageType();
		Capacity capacity = new Capacity();

		storageTypeObj.setName("st" + UniqueKeyGeneratorUtil.getUniqueKey());
		storageTypeObj.setDefaultTempratureInCentigrade(new Double(-30));
		storageTypeObj.setOneDimensionLabel("label 1");
		storageTypeObj.setTwoDimensionLabel("label 2");

		capacity.setOneDimensionCapacity(new Integer(3));
		capacity.setTwoDimensionCapacity(new Integer(3));
		storageTypeObj.setCapacity(capacity);		

		Collection holdsStorageTypeCollection = new HashSet();
		holdsStorageTypeCollection.add(storageTypeObj);

		storageTypeObj.setHoldsStorageTypeCollection(holdsStorageTypeCollection);
		storageTypeObj.setActivityStatus("Active");

		Collection holdsSpecimenClassCollection = new HashSet();
		holdsSpecimenClassCollection.add("Tissue");
		holdsSpecimenClassCollection.add("Fluid");
		holdsSpecimenClassCollection.add("Molecular");
		holdsSpecimenClassCollection.add("Cell");
		storageTypeObj.setHoldsSpecimenClassCollection(holdsSpecimenClassCollection);
		return storageTypeObj;
	}

	/**
	 * @return SpecimenArrayType
	 */
	public SpecimenArrayType initSpecimenArrayType()
	{
		SpecimenArrayType specimenArrayType = new SpecimenArrayType();
		specimenArrayType.setName("sat" + UniqueKeyGeneratorUtil.getUniqueKey());
		specimenArrayType.setSpecimenClass("Molecular");

		Collection specimenTypeCollection = new HashSet();
		specimenTypeCollection.add("DNA");
		specimenTypeCollection.add("RNA");
		specimenTypeCollection.add("RNA, nuclear");
		specimenTypeCollection.add("cDNA");
		specimenTypeCollection.add("protein");		
		specimenArrayType.setSpecimenTypeCollection(specimenTypeCollection);
		
		specimenArrayType.setComment("");
		Capacity capacity = new Capacity();
		capacity.setOneDimensionCapacity(new Integer(4));
		capacity.setTwoDimensionCapacity(new Integer(4));
		specimenArrayType.setCapacity(capacity);
		return specimenArrayType;
	}

	/**
	 * @return StorageContainer
	 */
	public StorageContainer initStorageContainer()
	{
		StorageContainer storageContainer = new StorageContainer();
		storageContainer.setName("sc" + UniqueKeyGeneratorUtil.getUniqueKey());

		StorageType storageType = (StorageType) ClientDemo.dataModelObjectMap.get("StorageType");
		/*	
		new StorageType();
		storageType.setId(new Long(4));
		*/
		storageContainer.setStorageType(storageType);
		Site site = (Site) ClientDemo.dataModelObjectMap.get("Site"); 
		/*new Site();
		site.setId(new Long(1));
		*/
		storageContainer.setSite(site);

		Integer conts = new Integer(1);
		storageContainer.setNoOfContainers(conts);
		storageContainer.setTempratureInCentigrade(new Double(-30));
		storageContainer.setBarcode("barc" + UniqueKeyGeneratorUtil.getUniqueKey());

		Capacity capacity = new Capacity();
		capacity.setOneDimensionCapacity(new Integer(1));
		capacity.setTwoDimensionCapacity(new Integer(2));
		storageContainer.setCapacity(capacity);

		CollectionProtocol collectionProtocol = (CollectionProtocol) ClientDemo.dataModelObjectMap.get("CollectionProtocol");
		
		/*	
		new CollectionProtocol();
		collectionProtocol.setId(new Long(3));
		*/
		Collection collectionProtocolCollection = new HashSet();
		collectionProtocolCollection.add(collectionProtocol);
		storageContainer.setCollectionProtocolCollection(collectionProtocolCollection);

		Collection holdsStorageTypeCollection = new HashSet();
		holdsStorageTypeCollection.add(storageType);
		storageContainer.setHoldsStorageTypeCollection(holdsStorageTypeCollection);

		Collection holdsSpecimenClassCollection = new HashSet();		
		holdsSpecimenClassCollection.add("Tissue");
		holdsSpecimenClassCollection.add("Fluid");
		holdsSpecimenClassCollection.add("Molecular");
		holdsSpecimenClassCollection.add("Cell");
		storageContainer.setHoldsSpecimenClassCollection(holdsSpecimenClassCollection);
		
/*		Container parent = new Container();
		parent.setId(new Long(2));
		storageContainer.setParent(parent);    
*/
		storageContainer.setPositionDimensionOne(new Integer(1));
		storageContainer.setPositionDimensionTwo(new Integer(2));

		storageContainer.setActivityStatus("Active");
		storageContainer.setFull(Boolean.valueOf(false));
		return storageContainer;
	}

	/**
	 * @return StorageContainer
	 */
	public StorageContainer initStorageContainerForSpecimenArray()
	{
		StorageContainer storageContainer = new StorageContainer();
		storageContainer.setName("sc" + UniqueKeyGeneratorUtil.getUniqueKey());

		StorageType storageType = (StorageType) ClientDemo.dataModelObjectMap.get("StorageType");		
		storageContainer.setStorageType(storageType);
		
		Site site = (Site) ClientDemo.dataModelObjectMap.get("Site"); 		
		storageContainer.setSite(site);

		Integer conts = new Integer(1);
		storageContainer.setNoOfContainers(conts);
		storageContainer.setTempratureInCentigrade(new Double(-30));
		storageContainer.setBarcode("barc" + UniqueKeyGeneratorUtil.getUniqueKey());

		Capacity capacity = new Capacity();
		capacity.setOneDimensionCapacity(new Integer(1));
		capacity.setTwoDimensionCapacity(new Integer(2));
		storageContainer.setCapacity(capacity);

		CollectionProtocol collectionProtocol = (CollectionProtocol) ClientDemo.dataModelObjectMap.get("CollectionProtocol");		
	
		Collection collectionProtocolCollection = new HashSet();
		collectionProtocolCollection.add(collectionProtocol);
		storageContainer.setCollectionProtocolCollection(collectionProtocolCollection);

		Collection holdsStorageTypeCollection = new HashSet();
		holdsStorageTypeCollection.add(storageType);
		storageContainer.setHoldsStorageTypeCollection(holdsStorageTypeCollection);
				
		storageContainer.setStorageType(storageType);
		
		SpecimenArrayType specimenArrayType = (SpecimenArrayType) ClientDemo.dataModelObjectMap.get("SpecimenArrayType");
		Collection holdsSpecimenArrayTypeCollection = new HashSet();		
		holdsSpecimenArrayTypeCollection.add(specimenArrayType);
		storageContainer.setHoldsSpecimenArrayTypeCollection(holdsSpecimenArrayTypeCollection);

		storageContainer.setPositionDimensionOne(new Integer(1));
		storageContainer.setPositionDimensionTwo(new Integer(2));

		storageContainer.setActivityStatus("Active");
		storageContainer.setFull(Boolean.valueOf(false));
		return storageContainer;
	}	
	
	/**
	 * @return Site
	 */
	public Site initSite()
	{
		Site siteObj = new Site();
//		User userObj = new User();
//		userObj.setId(new Long(1));
		User userObj = (User) ClientDemo.dataModelObjectMap.get("User");

		siteObj.setEmailAddress("admin@admin.com");
		siteObj.setName("sit" + UniqueKeyGeneratorUtil.getUniqueKey());
		siteObj.setType("Laboratory");
		siteObj.setActivityStatus("Active");
		siteObj.setCoordinator(userObj);
		
	 
		Address addressObj = new Address();
		addressObj.setCity("Saint Louis");
		addressObj.setCountry("United States");
		addressObj.setFaxNumber("555-55-5555");
		addressObj.setPhoneNumber("123678");
		addressObj.setState("Missouri");
		addressObj.setStreet("4939 Children's Place");
		addressObj.setZipCode("63110");
		siteObj.setAddress(addressObj);
		return siteObj;
	}

	/**
	 * @return Participant
	 */
	public Participant initParticipant()
	{
		Participant participant = new Participant();
		participant.setLastName("last" + UniqueKeyGeneratorUtil.getUniqueKey());
		participant.setFirstName("frst" + UniqueKeyGeneratorUtil.getUniqueKey());
		participant.setMiddleName("mdl" + UniqueKeyGeneratorUtil.getUniqueKey());

		/*try
		{
			System.out.println("-----------------------");
			participant.setBirthDate(Utility.parseDate("08/15/1975", Utility
					.datePattern("08/15/1975")));
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			System.out.println("-----------------------"+e);
			e.printStackTrace();
		}		
		try
		{
			System.out.println("-----------------------");
			participant.setDeathDate(Utility.parseDate("08/15/1974", Utility
					.datePattern("08/15/1974")));
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			System.out.println("-----------------------"+e);
			e.printStackTrace();
		}*/
		
		participant.setVitalStatus("Dead");
		participant.setGender("Female Gender");
		participant.setSexGenotype("XX");

		Collection raceCollection = new HashSet();
		raceCollection.add("White");
		raceCollection.add("Asian");
		participant.setRaceCollection(raceCollection);
		participant.setActivityStatus("Active");
		participant.setEthnicity("Hispanic or Latino");
		//participant.setSocialSecurityNumber("333-33-3333");

//		Collection participantMedicalIdentifierCollection = new HashSet();
//		/*participantMedicalIdentifierCollection.add("Washington University School of Medicine");
//		 participantMedicalIdentifierCollection.add("1111");
//		 */
//		participant.setParticipantMedicalIdentifierCollection(participantMedicalIdentifierCollection);
		return participant;
	}

	/**
	 * @return Participant
	 */
	public Participant initParticipantWithNoRequiredFields()
	{
		Participant participant = new Participant();
		participant.setActivityStatus("Active");
		return participant;
	}
	
	/**
	 * @return CollectionProtocol
	 */
	public CollectionProtocol initCollectionProtocol()
	{
		CollectionProtocol collectionProtocol = new CollectionProtocol();
		//Setting consent tiers for this protocol.
		Collection consentTierColl = new HashSet();
		
		ConsentTier c1 = new ConsentTier();
		c1.setStatement("Consent for aids research");
		consentTierColl.add(c1);
		ConsentTier c2 = new ConsentTier();
		c2.setStatement("Consent for cancer research");
		consentTierColl.add(c2);		
		ConsentTier c3 = new ConsentTier();
		c3.setStatement("Consent for Tb research");
		consentTierColl.add(c3);
		
		collectionProtocol.setConsentTierCollection(consentTierColl);
		
		collectionProtocol.setAliquotInSameContainer(new Boolean(false));
		collectionProtocol.setDescriptionURL("");
		collectionProtocol.setActivityStatus("Active");
		collectionProtocol.setEndDate(null);
		collectionProtocol.setEnrollment(null);
		collectionProtocol.setIrbIdentifier("7777");
		collectionProtocol.setTitle("Aids Study Collection Protocol For Consent tracking");
		collectionProtocol.setShortTitle("Cp Consent");
		collectionProtocol.setUnsignedConsentDocumentURL("C:\\consent.pdf");
		try
		{
			collectionProtocol.setStartDate(Utility.parseDate("08/15/1975", Utility
					.datePattern("08/15/1975")));
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}

		Collection collectionProtocolEventCollectionSet = new HashSet();
	 
//		CollectionProtocolEvent collectionProtocolEvent = new CollectionProtocolEvent();
		CollectionProtocolEvent collectionProtocolEvent = (CollectionProtocolEvent)ClientDemo.dataModelObjectMap.get("CollectionProtocolEvent"); 

		collectionProtocolEvent.setClinicalStatus("New Diagnosis");
		collectionProtocolEvent.setStudyCalendarEventPoint(new Double(1));
	 

		Collection specimenRequirementCollection = new HashSet();
//		SpecimenRequirement specimenRequirement = new SpecimenRequirement();
//		specimenRequirement.setSpecimenClass("Molecular");
//		specimenRequirement.setSpecimenType("DNA");
//		specimenRequirement.setTissueSite("Placenta");
//		specimenRequirement.setPathologyStatus("Malignant");
//		Quantity quantity = new Quantity();
//		quantity.setValue(new Double(10));
//		specimenRequirement.setQuantity(quantity);
		SpecimenRequirement specimenRequirement  =(SpecimenRequirement)ClientDemo.dataModelObjectMap.get("SpecimenRequirement");
		specimenRequirementCollection.add(specimenRequirement);
		
		collectionProtocolEvent.setSpecimenRequirementCollection(specimenRequirementCollection);

		//Setting collection point label
		collectionProtocolEvent.setCollectionPointLabel("User entered value");
		
		collectionProtocolEventCollectionSet.add(collectionProtocolEvent);
		collectionProtocol
				.setCollectionProtocolEventCollection(collectionProtocolEventCollectionSet);

//		User principalInvestigator = new User();
//		principalInvestigator.setId(new Long(1));
		User principalInvestigator = (User)ClientDemo.dataModelObjectMap.get("User");
		collectionProtocol.setPrincipalInvestigator(principalInvestigator);
		
//		User protocolCordinator = new User();
//		protocolCordinator.setId(new Long(principalInvestigator.getId().longValue()-1));
		User protocolCordinator = (User)ClientDemo.dataModelObjectMap.get("User1");
		Collection protocolCordinatorCollection = new HashSet();
		protocolCordinatorCollection.add(protocolCordinator);
		collectionProtocol.setCoordinatorCollection(protocolCordinatorCollection);		
		return collectionProtocol;
	}

	/**
	 * @return Specimen
	 */
	
	public Collection addConsentInSpecimen()
	{
//		Setting Consent Tier Response
		Collection consentTierStatusCollection = new HashSet();
		ConsentTierStatus  consentTierStatus = new ConsentTierStatus();		
		ConsentTier consentTier = new ConsentTier();
		consentTier.setId(new Long(1));
		consentTierStatus.setConsentTier(consentTier);
		consentTierStatus.setStatus("No");
		consentTierStatusCollection.add(consentTierStatus);
		
		ConsentTierStatus  consentTierStatus1 = new ConsentTierStatus();		
		ConsentTier consentTier1 = new ConsentTier();
		consentTier1.setId(new Long(2));
		consentTierStatus1.setConsentTier(consentTier1);
		consentTierStatus1.setStatus("No");
		consentTierStatusCollection.add(consentTierStatus1);
		
		ConsentTierStatus  consentTierStatus2 = new ConsentTierStatus();		
		ConsentTier consentTier2 = new ConsentTier();
		consentTier2.setId(new Long(3));
		consentTierStatus2.setConsentTier(consentTier2);
		consentTierStatus2.setStatus("No");
		consentTierStatusCollection.add(consentTierStatus2);
		return consentTierStatusCollection;
	}
	public Specimen initMolecularSpecimen()
	{
		MolecularSpecimen molecularSpecimen = new MolecularSpecimen();
		SpecimenCollectionGroup specimenCollectionGroup = (SpecimenCollectionGroup)ClientDemo.dataModelObjectMap.get("SpecimenCollectionGroup");
		molecularSpecimen.setSpecimenCollectionGroup(specimenCollectionGroup);
		
		molecularSpecimen.setLabel("spec" + UniqueKeyGeneratorUtil.getUniqueKey());
		molecularSpecimen.setBarcode("bar" + UniqueKeyGeneratorUtil.getUniqueKey());
		molecularSpecimen.setType("DNA");
		molecularSpecimen.setAvailable(new Boolean(true));
		molecularSpecimen.setActivityStatus("Active");

		SpecimenCharacteristics specimenCharacteristics = (SpecimenCharacteristics)ClientDemo.dataModelObjectMap.get("SpecimenCharacteristics");
		molecularSpecimen.setSpecimenCharacteristics(specimenCharacteristics);
		molecularSpecimen.setPathologicalStatus("Malignant");

		Quantity quantity = new Quantity();
		quantity.setValue(new Double(10));
		molecularSpecimen.setInitialQuantity(quantity);
		molecularSpecimen.setAvailableQuantity(quantity);
		molecularSpecimen.setConcentrationInMicrogramPerMicroliter(new Double(10));
		molecularSpecimen.setComment("");
		molecularSpecimen.setLineage("Aliquot");
		// Is virtually located
		molecularSpecimen.setStorageContainer(null); 
		molecularSpecimen.setPositionDimensionOne(null);
		molecularSpecimen.setPositionDimensionTwo(null);
		
		Collection externalIdentifierCollection = new HashSet();
		ExternalIdentifier externalIdentifier = new ExternalIdentifier();
		externalIdentifier.setName("eid" + UniqueKeyGeneratorUtil.getUniqueKey());
		externalIdentifier.setValue("11");
		externalIdentifier.setSpecimen(molecularSpecimen);
		
		externalIdentifierCollection.add(externalIdentifier);
		molecularSpecimen.setExternalIdentifierCollection(externalIdentifierCollection);
		CollectionEventParameters collectionEventParameters = new CollectionEventParameters();
		collectionEventParameters.setComment("");
		collectionEventParameters.setSpecimen(molecularSpecimen);

		User user = (User)ClientDemo.dataModelObjectMap.get("User");	
		collectionEventParameters.setUser(user);
		
		try
		{
			collectionEventParameters.setTimestamp(Utility.parseDate("08/15/1975", Utility
					.datePattern("08/15/1975")));
					
		}
		catch (ParseException e1)
		{
			System.out.println(" exception in APIDemo");
			e1.printStackTrace();
		}
		
		collectionEventParameters.setContainer("No Additive Vacutainer");
		collectionEventParameters.setCollectionProcedure("Needle Core Biopsy");
		ReceivedEventParameters receivedEventParameters = new ReceivedEventParameters();
		receivedEventParameters.setUser(user);
				
		try
		{
			System.out.println("--- Start ---- 10");
			receivedEventParameters.setTimestamp(Utility.parseDate("08/15/1975", Utility
					.datePattern("08/15/1975")));
		}
		catch (ParseException e)
		{
			System.out.println("APIDemo");
			e.printStackTrace();
		}
		
		receivedEventParameters.setReceivedQuality("Acceptable");
		receivedEventParameters.setComment("");
		receivedEventParameters.setSpecimen(molecularSpecimen);
		Collection specimenEventCollection = new HashSet();
		specimenEventCollection.add(collectionEventParameters);
		specimenEventCollection.add(receivedEventParameters);
		molecularSpecimen.setSpecimenEventCollection(specimenEventCollection);

		Biohazard biohazard = (Biohazard)ClientDemo.dataModelObjectMap.get("Biohazard");
		Collection biohazardCollection = new HashSet();
		biohazardCollection.add(biohazard);
		molecularSpecimen.setBiohazardCollection(biohazardCollection);
		System.out.println(" -------- end -----------");

//		Created on date is same as Collection Date
		molecularSpecimen.setCreatedOn(collectionEventParameters.getTimestamp());
	
		Collection consentTierStatusCollection = addConsentInSpecimen();
		molecularSpecimen.setConsentTierStatusCollection(consentTierStatusCollection);
		return molecularSpecimen;
	}
	
	/**
	 * @return Specimen
	 */
	public Specimen initTissueSpecimen()
	{
		TissueSpecimen tissueSpecimen = new TissueSpecimen();

		SpecimenCollectionGroup specimenCollectionGroup = (SpecimenCollectionGroup)ClientDemo.dataModelObjectMap.get("SpecimenCollectionGroup");
		tissueSpecimen.setSpecimenCollectionGroup(specimenCollectionGroup);
		
		tissueSpecimen.setLabel("spec" + UniqueKeyGeneratorUtil.getUniqueKey());
		tissueSpecimen.setBarcode("bar" + UniqueKeyGeneratorUtil.getUniqueKey());
		tissueSpecimen.setType("Fixed Tissue Block");
		tissueSpecimen.setAvailable(new Boolean(true));
		tissueSpecimen.setActivityStatus("Active");

		SpecimenCharacteristics specimenCharacteristics = (SpecimenCharacteristics)ClientDemo.dataModelObjectMap.get("SpecimenCharacteristics");
		tissueSpecimen.setSpecimenCharacteristics(specimenCharacteristics);
		tissueSpecimen.setPathologicalStatus("Malignant");

		Quantity quantity = new Quantity();
		quantity.setValue(new Double(10));
		tissueSpecimen.setInitialQuantity(quantity);
		tissueSpecimen.setAvailableQuantity(quantity);
		
		tissueSpecimen.setComment("");
		tissueSpecimen.setLineage("Aliquot");
		// Is virtually located
		

		
		tissueSpecimen.setStorageContainer(null); 
		tissueSpecimen.setPositionDimensionOne(null);
		tissueSpecimen.setPositionDimensionTwo(null);
		
		Collection externalIdentifierCollection = new HashSet();
		ExternalIdentifier externalIdentifier = new ExternalIdentifier();
		externalIdentifier.setName("eid" + UniqueKeyGeneratorUtil.getUniqueKey());
		externalIdentifier.setValue("11");
		externalIdentifier.setSpecimen(tissueSpecimen);
		
		externalIdentifierCollection.add(externalIdentifier);
		tissueSpecimen.setExternalIdentifierCollection(externalIdentifierCollection);
		CollectionEventParameters collectionEventParameters = new CollectionEventParameters();
		collectionEventParameters.setComment("");
		collectionEventParameters.setSpecimen(tissueSpecimen);

		User user = (User)ClientDemo.dataModelObjectMap.get("User");	
		collectionEventParameters.setUser(user);
		
		try
		{
			collectionEventParameters.setTimestamp(Utility.parseDate("08/15/1975", Utility
					.datePattern("08/15/1975")));
					
		}
		catch (ParseException e1)
		{
			System.out.println(" exception in APIDemo");
			e1.printStackTrace();
		}
		
		collectionEventParameters.setContainer("No Additive Vacutainer");
		collectionEventParameters.setCollectionProcedure("Needle Core Biopsy");
		ReceivedEventParameters receivedEventParameters = new ReceivedEventParameters();
		receivedEventParameters.setUser(user);
				
		try
		{
			System.out.println("--- Start ---- 10");
			receivedEventParameters.setTimestamp(Utility.parseDate("08/15/1975", Utility
					.datePattern("08/15/1975")));
		}
		catch (ParseException e)
		{
			System.out.println("APIDemo");
			e.printStackTrace();
		}
		
		receivedEventParameters.setReceivedQuality("Acceptable");
		receivedEventParameters.setComment("");
		receivedEventParameters.setSpecimen(tissueSpecimen);
		Collection specimenEventCollection = new HashSet();
		specimenEventCollection.add(collectionEventParameters);
		specimenEventCollection.add(receivedEventParameters);
		tissueSpecimen.setSpecimenEventCollection(specimenEventCollection);

		Biohazard biohazard = (Biohazard)ClientDemo.dataModelObjectMap.get("Biohazard");
		Collection biohazardCollection = new HashSet();
		biohazardCollection.add(biohazard);
		tissueSpecimen.setBiohazardCollection(biohazardCollection);
		System.out.println(" -------- end -----------");
		
		//Created on date is same as Collection Date
		
		tissueSpecimen.setCreatedOn(collectionEventParameters.getTimestamp());
		Collection consentTierStatusCollection = addConsentInSpecimen();
		tissueSpecimen.setConsentTierStatusCollection(consentTierStatusCollection);

		return tissueSpecimen;
	}
	
	/**
	 * @return Specimen
	 */
	public Specimen initFluidSpecimen()
	{
		FluidSpecimen fluidSpecimen = new FluidSpecimen();

		SpecimenCollectionGroup specimenCollectionGroup = (SpecimenCollectionGroup)ClientDemo.dataModelObjectMap.get("SpecimenCollectionGroup");
		fluidSpecimen.setSpecimenCollectionGroup(specimenCollectionGroup);
		
		fluidSpecimen.setLabel("spec" + UniqueKeyGeneratorUtil.getUniqueKey());
		fluidSpecimen.setBarcode("bar" + UniqueKeyGeneratorUtil.getUniqueKey());
		fluidSpecimen.setType("Urine");
		fluidSpecimen.setAvailable(new Boolean(true));
		fluidSpecimen.setActivityStatus("Active");

		SpecimenCharacteristics specimenCharacteristics = (SpecimenCharacteristics)ClientDemo.dataModelObjectMap.get("SpecimenCharacteristics");
		fluidSpecimen.setSpecimenCharacteristics(specimenCharacteristics);
		fluidSpecimen.setPathologicalStatus("Malignant");

		Quantity quantity = new Quantity();
		quantity.setValue(new Double(10));
		fluidSpecimen.setInitialQuantity(quantity);
		fluidSpecimen.setAvailableQuantity(quantity);
		
		fluidSpecimen.setComment("");
		fluidSpecimen.setLineage("Aliquot");
		// Is virtually located
		

		
		fluidSpecimen.setStorageContainer(null); 
		fluidSpecimen.setPositionDimensionOne(null);
		fluidSpecimen.setPositionDimensionTwo(null);
		
		Collection externalIdentifierCollection = new HashSet();
		ExternalIdentifier externalIdentifier = new ExternalIdentifier();
		externalIdentifier.setName("eid" + UniqueKeyGeneratorUtil.getUniqueKey());
		externalIdentifier.setValue("11");
		externalIdentifier.setSpecimen(fluidSpecimen);
		
		externalIdentifierCollection.add(externalIdentifier);
		fluidSpecimen.setExternalIdentifierCollection(externalIdentifierCollection);
		CollectionEventParameters collectionEventParameters = new CollectionEventParameters();
		collectionEventParameters.setComment("");
		collectionEventParameters.setSpecimen(fluidSpecimen);

		User user = (User)ClientDemo.dataModelObjectMap.get("User");	
		collectionEventParameters.setUser(user);
		
		try
		{
			collectionEventParameters.setTimestamp(Utility.parseDate("08/15/1975", Utility
					.datePattern("08/15/1975")));
					
		}
		catch (ParseException e1)
		{
			System.out.println(" exception in APIDemo");
			e1.printStackTrace();
		}
		
		collectionEventParameters.setContainer("No Additive Vacutainer");
		collectionEventParameters.setCollectionProcedure("Needle Core Biopsy");
		ReceivedEventParameters receivedEventParameters = new ReceivedEventParameters();
		receivedEventParameters.setUser(user);
				
		try
		{
			System.out.println("--- Start ---- 10");
			receivedEventParameters.setTimestamp(Utility.parseDate("08/15/1975", Utility
					.datePattern("08/15/1975")));
		}
		catch (ParseException e)
		{
			System.out.println("APIDemo");
			e.printStackTrace();
		}
		
		receivedEventParameters.setReceivedQuality("Acceptable");
		receivedEventParameters.setComment("");
		receivedEventParameters.setSpecimen(fluidSpecimen);
		Collection specimenEventCollection = new HashSet();
		specimenEventCollection.add(collectionEventParameters);
		specimenEventCollection.add(receivedEventParameters);
		fluidSpecimen.setSpecimenEventCollection(specimenEventCollection);

		Biohazard biohazard = (Biohazard)ClientDemo.dataModelObjectMap.get("Biohazard");
		Collection biohazardCollection = new HashSet();
		biohazardCollection.add(biohazard);
		fluidSpecimen.setBiohazardCollection(biohazardCollection);
		System.out.println(" -------- end -----------");

//		Created on date is same as Collection Date
		fluidSpecimen.setCreatedOn(collectionEventParameters.getTimestamp());
		Collection consentTierStatusCollection = addConsentInSpecimen();
		fluidSpecimen.setConsentTierStatusCollection(consentTierStatusCollection);
		return fluidSpecimen;
	}
	
	/**
	 * @return Specimen
	 */
	public Specimen initCellSpecimen()
	{
		CellSpecimen cellSpecimen = new CellSpecimen();

		SpecimenCollectionGroup specimenCollectionGroup = (SpecimenCollectionGroup)ClientDemo.dataModelObjectMap.get("SpecimenCollectionGroup");
		cellSpecimen.setSpecimenCollectionGroup(specimenCollectionGroup);
		
		cellSpecimen.setLabel("spec" + UniqueKeyGeneratorUtil.getUniqueKey());
		cellSpecimen.setBarcode("bar" + UniqueKeyGeneratorUtil.getUniqueKey());
		cellSpecimen.setType("Fixed Cell Block");
		cellSpecimen.setAvailable(new Boolean(true));
		cellSpecimen.setActivityStatus("Active");

		SpecimenCharacteristics specimenCharacteristics = (SpecimenCharacteristics)ClientDemo.dataModelObjectMap.get("SpecimenCharacteristics");
		cellSpecimen.setSpecimenCharacteristics(specimenCharacteristics);
		cellSpecimen.setPathologicalStatus("Malignant");

		Quantity quantity = new Quantity();
		quantity.setValue(new Double(10));
		cellSpecimen.setInitialQuantity(quantity);
		cellSpecimen.setAvailableQuantity(quantity);
		cellSpecimen.setComment("");
		cellSpecimen.setLineage("Aliquot");
		// Is virtually located
		

		
		cellSpecimen.setStorageContainer(null); 
		cellSpecimen.setPositionDimensionOne(null);
		cellSpecimen.setPositionDimensionTwo(null);
		
		Collection externalIdentifierCollection = new HashSet();
		ExternalIdentifier externalIdentifier = new ExternalIdentifier();
		externalIdentifier.setName("eid" + UniqueKeyGeneratorUtil.getUniqueKey());
		externalIdentifier.setValue("11");
		externalIdentifier.setSpecimen(cellSpecimen);
		
		externalIdentifierCollection.add(externalIdentifier);
		cellSpecimen.setExternalIdentifierCollection(externalIdentifierCollection);
		CollectionEventParameters collectionEventParameters = new CollectionEventParameters();
		collectionEventParameters.setComment("");
		collectionEventParameters.setSpecimen(cellSpecimen);

		User user = (User)ClientDemo.dataModelObjectMap.get("User");	
		collectionEventParameters.setUser(user);
		
		try
		{
			collectionEventParameters.setTimestamp(Utility.parseDate("08/15/1975", Utility
					.datePattern("08/15/1975")));
					
		}
		catch (ParseException e1)
		{
			System.out.println(" exception in APIDemo");
			e1.printStackTrace();
		}
		
		collectionEventParameters.setContainer("No Additive Vacutainer");
		collectionEventParameters.setCollectionProcedure("Needle Core Biopsy");
		ReceivedEventParameters receivedEventParameters = new ReceivedEventParameters();
		receivedEventParameters.setUser(user);
				
		try
		{
			System.out.println("--- Start ---- 10");
			receivedEventParameters.setTimestamp(Utility.parseDate("08/15/1975", Utility
					.datePattern("08/15/1975")));
		}
		catch (ParseException e)
		{
			System.out.println("APIDemo");
			e.printStackTrace();
		}
		
		receivedEventParameters.setReceivedQuality("Acceptable");
		receivedEventParameters.setComment("");
		receivedEventParameters.setSpecimen(cellSpecimen);
		Collection specimenEventCollection = new HashSet();
		specimenEventCollection.add(collectionEventParameters);
		specimenEventCollection.add(receivedEventParameters);
		cellSpecimen.setSpecimenEventCollection(specimenEventCollection);

		Biohazard biohazard = (Biohazard)ClientDemo.dataModelObjectMap.get("Biohazard");
		Collection biohazardCollection = new HashSet();
		biohazardCollection.add(biohazard);
		cellSpecimen.setBiohazardCollection(biohazardCollection);
		System.out.println(" -------- end -----------");
        //	Created on date is same as Collection Date
		cellSpecimen.setCreatedOn(collectionEventParameters.getTimestamp());
		Collection consentTierStatusCollection = addConsentInSpecimen();
		cellSpecimen.setConsentTierStatusCollection(consentTierStatusCollection);
		return cellSpecimen;
	}

	/**
	 * @return SpecimenCollectionGroup
	 */
	public SpecimenCollectionGroup initSpecimenCollectionGroup()
	{
		SpecimenCollectionGroup specimenCollectionGroup = new SpecimenCollectionGroup();

//		Site site = new Site();
//		site.setId(new Long(1));
		Site site = (Site)ClientDemo.dataModelObjectMap.get("Site");
		specimenCollectionGroup.setSpecimenCollectionSite(site);

		specimenCollectionGroup.setClinicalDiagnosis("Abdominal fibromatosis");
		specimenCollectionGroup.setClinicalStatus("Operative");
		specimenCollectionGroup.setActivityStatus("Active");

//		CollectionProtocolEvent collectionProtocol = new CollectionProtocolEvent();
//		collectionProtocol.setId(new Long(1));
		CollectionProtocolEvent collectionProtocolEvent = (CollectionProtocolEvent)ClientDemo.dataModelObjectMap.get("CollectionProtocolEvent");	
		specimenCollectionGroup.setCollectionProtocolEvent(collectionProtocolEvent);

//		CollectionProtocolRegistration collectionProtocolRegistration = new CollectionProtocolRegistration();

//		Participant participant = new Participant();
//		participant.setId(new Long(1));
	
//		collectionProtocolRegistration.setParticipant(participant);
//		collectionProtocolRegistration.setId(new Long(1));
		
		
		//collectionProtocolRegistration.setProtocolParticipantIdentifier("");
		CollectionProtocolRegistration collectionProtocolRegistration = (CollectionProtocolRegistration)ClientDemo.dataModelObjectMap.get("CollectionProtocolRegistration");
		
//		CollectionProtocolRegistration collectionProtocolRegistration = new CollectionProtocolRegistration();
//		CollectionProtocol collectionProtocol = new CollectionProtocol();
//		collectionProtocol.setId(new Long("1000"));
//		Participant participant = new Participant();
//		participant.setId(new Long("1000"));
//		collectionProtocolRegistration.setCollectionProtocol(collectionProtocol);
//		collectionProtocolRegistration.setParticipant(participant);
		specimenCollectionGroup.setCollectionProtocolRegistration(collectionProtocolRegistration);

		specimenCollectionGroup.setName("scg" + UniqueKeyGeneratorUtil.getUniqueKey());
		
//		Setting Consent Tier Status.
		Collection consentTierStatusCollection = new HashSet();
		
		ConsentTierStatus  consentTierStatus = new ConsentTierStatus();		
		ConsentTier consentTier = new ConsentTier();
		consentTier.setId(new Long(1));
		consentTierStatus.setConsentTier(consentTier);
		consentTierStatus.setStatus("Yes");
		consentTierStatusCollection.add(consentTierStatus);
		
		ConsentTierStatus  consentTierStatus1 = new ConsentTierStatus();		
		ConsentTier consentTier1 = new ConsentTier();
		consentTier1.setId(new Long(2));
		consentTierStatus1.setConsentTier(consentTier1);
		consentTierStatus1.setStatus("Yes");
		consentTierStatusCollection.add(consentTierStatus1);
		
		ConsentTierStatus  consentTierStatus2 = new ConsentTierStatus();		
		ConsentTier consentTier2 = new ConsentTier();
		consentTier2.setId(new Long(3));
		consentTierStatus2.setConsentTier(consentTier2);
		consentTierStatus2.setStatus("Yes");
		consentTierStatusCollection.add(consentTierStatus2);
		
		specimenCollectionGroup.setConsentTierStatusCollection(consentTierStatusCollection);

		
		specimenCollectionGroup.setSurgicalPathologyNumber("");
		

		//Adding Events
		CollectionEventParameters collectionEventParameters = new CollectionEventParameters();
		collectionEventParameters.setCollectionProcedure("Not Specified");
		collectionEventParameters.setComment("Default Comment");
		collectionEventParameters.setContainer("Not Specified");
		collectionEventParameters.setSpecimenCollectionGroup(specimenCollectionGroup);
		//Setting default system date and time
		Calendar cal = Calendar.getInstance();
		String dateOfEvent = Utility.parseDateToString(cal.getTime(), Constants.DATE_PATTERN_MM_DD_YYYY);
		String timeInHrs = Integer.toString(cal.get(Calendar.HOUR_OF_DAY));
		String timeInMinutes = Integer.toString(cal.get(Calendar.MINUTE));
		
		collectionEventParameters.setTimestamp(EventsUtil.setTimeStamp(dateOfEvent, timeInHrs, timeInMinutes));
		//Setting collector and receiver
		User user = (User)ClientDemo.dataModelObjectMap.get("User");
		collectionEventParameters.setUser(user);
		
		ReceivedEventParameters receivedEventParameters = new ReceivedEventParameters();
		receivedEventParameters.setComment("Default Comment");
		receivedEventParameters.setReceivedQuality("Not Specified");
		receivedEventParameters.setSpecimenCollectionGroup(specimenCollectionGroup);
		receivedEventParameters.setTimestamp(EventsUtil.setTimeStamp(dateOfEvent, timeInHrs, timeInMinutes));
		
		receivedEventParameters.setUser(user);		
		
		Collection specimenEventParamsColl = new HashSet();
		specimenEventParamsColl.add(collectionEventParameters);
		specimenEventParamsColl.add(receivedEventParameters);
		
		specimenCollectionGroup.setSpecimenEventParametersCollection(specimenEventParamsColl);
		return specimenCollectionGroup;
	}

	/**
	 * @return Distribution
	 */
	public Distribution initDistribution()
	{
		Distribution distribution = new Distribution();

		distribution.setActivityStatus("Active");

		Specimen specimen = (Specimen) ClientDemo.dataModelObjectMap.get("Specimen");
		
		/*
		= new MolecularSpecimen();
	//	specimen.setBarcode("");
	//	specimen.setLabel("new label");
		specimen.setId(new Long(10));
		Quantity quantity = new Quantity();
		quantity.setValue(new Double(15));
		specimen.setAvailableQuantity(quantity);
		*/
		
		
		
		DistributedItem distributedItem = new DistributedItem();
		distributedItem.setQuantity(new Double(2));
		distributedItem.setSpecimen(specimen);
		Collection distributedItemCollection = new HashSet();
		distributedItemCollection.add(distributedItem);
		distribution.setDistributedItemCollection(distributedItemCollection);
		
		DistributionProtocol distributionProtocol = (DistributionProtocol) ClientDemo.dataModelObjectMap.get("DistributionProtocol");		
		distribution.setDistributionProtocol(distributionProtocol);
		
		Site toSite = (Site) ClientDemo.dataModelObjectMap.get("Site");
		//toSite.setId(new Long("1000"));
		distribution.setToSite(toSite);
		/*	
		new Site();
		toSite.setId(new Long(1));
		distribution.setToSite(toSite);
		*/
		/*
		try
		{
			distribution.setTimestamp(Utility.parseDate("08/15/1975", Utility
					.datePattern("08/15/1975")));
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		*/
		distribution.setComment("");

		User user = (User) ClientDemo.dataModelObjectMap.get("User");	
		/*	
		new User();
		user.setId(new Long(1));
		*/
		distribution.setDistributedBy(user);
	 
		return distribution;
	}

	/**
	 * @return Distribution
	 */
	public Distribution initDistribution(Specimen specimen)
	{
		Distribution distribution = new Distribution();

		distribution.setActivityStatus("Active");		
		DistributedItem distributedItem = new DistributedItem();
		distributedItem.setQuantity(new Double(2));
		distributedItem.setSpecimen(specimen);
		Collection distributedItemCollection = new HashSet();
		distributedItemCollection.add(distributedItem);
		distribution.setDistributedItemCollection(distributedItemCollection);
		
		DistributionProtocol distributionProtocol = (DistributionProtocol) ClientDemo.dataModelObjectMap.get("DistributionProtocol");		
		distribution.setDistributionProtocol(distributionProtocol);
		
		Site toSite = (Site) ClientDemo.dataModelObjectMap.get("Site");		
		distribution.setToSite(toSite);	
		distribution.setComment("");

		User user = (User) ClientDemo.dataModelObjectMap.get("User");
		distribution.setDistributedBy(user);
		return distribution;
	}
	
	/**
	 * @return DistributionProtocol
	 */
	public DistributionProtocol initDistributionProtocol()
	{
		DistributionProtocol distributionProtocol = new DistributionProtocol();

		User principalInvestigator = (User) ClientDemo.dataModelObjectMap.get("User");
		/*	
		new User();
		principalInvestigator.setId(new Long(1));
		*/
		distributionProtocol.setPrincipalInvestigator(principalInvestigator);
		distributionProtocol.setTitle("DP"+ UniqueKeyGeneratorUtil.getUniqueKey());
		distributionProtocol.setShortTitle("DP1");
		distributionProtocol.setIrbIdentifier("55555");
		
		try
		{
			distributionProtocol.setStartDate(Utility.parseDate("08/15/1975", Utility
					.datePattern("08/15/1975")));
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		
		distributionProtocol.setDescriptionURL("");
		distributionProtocol.setEnrollment(new Integer(10));

		SpecimenRequirement specimenRequirement = (SpecimenRequirement) ClientDemo.dataModelObjectMap.get("SpecimenRequirement");
		/*	
		new SpecimenRequirement();
		specimenRequirement.setPathologyStatus("Malignant");
		specimenRequirement.setTissueSite("Placenta");
		specimenRequirement.setSpecimenType("DNA");
		specimenRequirement.setSpecimenClass("Molecular");
		Quantity quantity = new Quantity();
		quantity.setValue(new Double(10));
		specimenRequirement.setQuantity(quantity);
		*/
			
		Collection specimenRequirementCollection = new HashSet();
		specimenRequirementCollection.add(specimenRequirement);
		distributionProtocol.setSpecimenRequirementCollection(specimenRequirementCollection);

		distributionProtocol.setActivityStatus("Active");
		return distributionProtocol;
	}

	/**
	 * @return CollectionProtocolRegistration
	 */
	public CollectionProtocolRegistration initCollectionProtocolRegistration()
	{
		CollectionProtocolRegistration collectionProtocolRegistration = new CollectionProtocolRegistration();
//		CollectionProtocol collectionProtocol = new CollectionProtocol();
//		collectionProtocol.setId(new Long(1));
		CollectionProtocol collectionProtocol = (CollectionProtocol)ClientDemo.dataModelObjectMap.get("CollectionProtocol");
		collectionProtocolRegistration.setCollectionProtocol(collectionProtocol);

//		Participant participant = new Participant();
//		participant.setId(new Long(1));
		Participant participant = (Participant)ClientDemo.dataModelObjectMap.get("Participant");
		collectionProtocolRegistration.setParticipant(participant);

		collectionProtocolRegistration.setProtocolParticipantIdentifier("");
		collectionProtocolRegistration.setActivityStatus("Active");
		/*
		try
		{
			collectionProtocolRegistration.setRegistrationDate(Utility.parseDate("08/15/1975",
					Utility.datePattern("08/15/1975")));
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		*/
		//Setting Consent Tier Responses.
		try
		{
			collectionProtocolRegistration.setConsentSignatureDate(Utility.parseDate("11/23/2006",Utility.datePattern("11/23/2006")));
		}
		catch (ParseException e)
		{			
			e.printStackTrace();
		}
		collectionProtocolRegistration.setSignedConsentDocumentURL("F:/doc/consentDoc.doc");
		
		User user = new User();
		user.setId(new Long(1));
		collectionProtocolRegistration.setConsentWitness(user);
		
		Collection consentTierResponseCollection = new HashSet();
		
		ConsentTierResponse r1 = new ConsentTierResponse();
		ConsentTier consentTier = new ConsentTier();
		consentTier.setId(new Long(1));
		r1.setConsentTier(consentTier);
		r1.setResponse("Yes");
		consentTierResponseCollection.add(r1);
		
		ConsentTierResponse r2 = new ConsentTierResponse();
		ConsentTier consentTier2 = new ConsentTier();
		consentTier2.setId(new Long(2));
		r2.setConsentTier(consentTier2);
		r2.setResponse("Yes");
		consentTierResponseCollection.add(r2);
		
		ConsentTierResponse r3 = new ConsentTierResponse();
		ConsentTier consentTier3 = new ConsentTier();
		consentTier3.setId(new Long(3));
		r3.setConsentTier(consentTier3);
		r3.setResponse("No");
		consentTierResponseCollection.add(r3);
		
		collectionProtocolRegistration.setConsentTierResponseCollection(consentTierResponseCollection);
		return collectionProtocolRegistration;
	}
	
	public SpecimenArray initSpecimenArray()
	{
		SpecimenArray specimenArray = new SpecimenArray();
		
		SpecimenArrayType specimenArrayType = (SpecimenArrayType) ClientDemo.dataModelObjectMap.get("SpecimenArrayType");
		/*	
		new SpecimenArrayType();
		specimenArrayType.setId(new Long(9));
		*/
		specimenArray.setSpecimenArrayType(specimenArrayType);
		
		specimenArray.setBarcode("bar" + UniqueKeyGeneratorUtil.getUniqueKey());
		specimenArray.setName("sa" + UniqueKeyGeneratorUtil.getUniqueKey()); 
		
		User createdBy = (User) ClientDemo.dataModelObjectMap.get("User");
		/*
		new User();
		createdBy.setId(new Long(1));
		*/
		specimenArray.setCreatedBy(createdBy);
		
		Capacity capacity = specimenArrayType.getCapacity();
/*		capacity.setOneDimensionCapacity(1);
		capacity.setTwoDimensionCapacity(2);
*/		specimenArray.setCapacity(capacity);
		
		specimenArray.setComment("");
		StorageContainer storageContainer = (StorageContainer) ClientDemo.dataModelObjectMap.get("StorageContainer");
		/*	
		new StorageContainer();
		storageContainer.setId(new Long(1));		
		storageContainer=new StorageContainer();
		storageContainer.setId(new Long(1));
		*/
		specimenArray.setStorageContainer(storageContainer);
		specimenArray.setPositionDimensionOne(new Integer(1));
		specimenArray.setPositionDimensionTwo(new Integer(1));
		
		Collection specimenArrayContentCollection = new HashSet();
		SpecimenArrayContent specimenArrayContent = new SpecimenArrayContent();
		
		Specimen specimen = (Specimen) ClientDemo.dataModelObjectMap.get("Specimen");
		
/*		specimen.setLabel("Specimen 12");
		//specimen.setType("DNA");
		specimen.setId(new Long(10));
*/		
		specimenArrayContent.setSpecimen(specimen);
		specimenArrayContent.setPositionDimensionOne(new Integer(1));
		specimenArrayContent.setPositionDimensionTwo(new Integer(1));
		Quantity quantity = new Quantity();
		quantity.setValue(new Double(2));
		specimenArrayContent.setInitialQuantity(quantity);
		specimenArrayContentCollection.add(specimenArrayContent);
		specimenArray.setSpecimenArrayContentCollection(specimenArrayContentCollection);
		return specimenArray;
	}
	
	
	public SpecimenArray initSpecimenArray(Specimen specimen, StorageContainer storageContainer, User createdBy)
	{
		SpecimenArray specimenArray = new SpecimenArray();		
		
		SpecimenArrayType specimenArrayType = (SpecimenArrayType) ClientDemo.dataModelObjectMap.get("SpecimenArrayType");		
		specimenArray.setSpecimenArrayType(specimenArrayType);
		
		specimenArray.setBarcode("bar" + UniqueKeyGeneratorUtil.getUniqueKey());
		specimenArray.setName("sa" + UniqueKeyGeneratorUtil.getUniqueKey()); 		
			
		specimenArray.setCreatedBy(createdBy);
		
		Capacity capacity = specimenArrayType.getCapacity();
		specimenArray.setCapacity(capacity);
		
		specimenArray.setComment("");		
		
		specimenArray.setStorageContainer(storageContainer);
		specimenArray.setPositionDimensionOne(new Integer(1));
		specimenArray.setPositionDimensionTwo(new Integer(1));
		
		Collection specimenArrayContentCollection = new HashSet();
		SpecimenArrayContent specimenArrayContent = new SpecimenArrayContent();		

		specimenArrayContent.setSpecimen(specimen);
		specimenArrayContent.setPositionDimensionOne(new Integer(1));
		specimenArrayContent.setPositionDimensionTwo(new Integer(1));
		Quantity quantity = new Quantity();
		quantity.setValue(new Double(1));
		specimenArrayContent.setInitialQuantity(quantity);
		specimenArrayContentCollection.add(specimenArrayContent);
		specimenArray.setSpecimenArrayContentCollection(specimenArrayContentCollection);
		return specimenArray;
	}
	
	
	public SpecimenCharacteristics initSpecimenCharacteristics()
	{
		SpecimenCharacteristics specimenCharacteristics = new SpecimenCharacteristics();
		specimenCharacteristics.setTissueSide("Left");
		specimenCharacteristics.setTissueSite("Placenta");
		specimenCharacteristics.setId(new Long(1));
		
		return specimenCharacteristics;
	}
	public SpecimenRequirement initSpecimenRequirement()
	{
		SpecimenRequirement specimenRequirement = new SpecimenRequirement();
		specimenRequirement.setSpecimenClass("Molecular");
		specimenRequirement.setSpecimenType("DNA");
		specimenRequirement.setTissueSite("Placenta");
		specimenRequirement.setPathologyStatus("Malignant");
		Quantity quantity = new Quantity();
		quantity.setValue(new Double(10));
		specimenRequirement.setQuantity(quantity);
		return specimenRequirement;
	}
	public CollectionProtocolEvent initCollectionProtocolEvent()
	{
		CollectionProtocolEvent collectionProtocolEvent = new CollectionProtocolEvent();
		collectionProtocolEvent.setId(new Long(1));
		return collectionProtocolEvent;
	}
	
	//Update methods starts
	public void updateInstitution(Institution institution)
	{
		institution.setName("inst"+UniqueKeyGeneratorUtil.getUniqueKey());
	}
	
	public void updateDepartment(Department department)
	{
		department.setName("dt"+UniqueKeyGeneratorUtil.getUniqueKey());
	}
	
	public void updateCancerResearchGroup(CancerResearchGroup cancerResearchGroup)
	{
		cancerResearchGroup.setName("crg"+UniqueKeyGeneratorUtil.getUniqueKey());		
	}
	
	public void updateBiohazard(Biohazard bioHazard)
	{
		bioHazard.setComment("Radioactive");
		bioHazard.setName("bh" + UniqueKeyGeneratorUtil.getUniqueKey());
		bioHazard.setType("Radioactive"); //Toxic
	}
	
	public void updateSite(Site siteObj)
	{
		siteObj.setEmailAddress("admin1@admin.com");
		siteObj.setName("sit" + UniqueKeyGeneratorUtil.getUniqueKey());
		siteObj.setType("Repository");
		siteObj.setActivityStatus("Active");		
		siteObj.getAddress().setCity("Saint Louis1");
		siteObj.getAddress().setCountry("United States");
		siteObj.getAddress().setFaxNumber("555-55-55551");
		siteObj.getAddress().setPhoneNumber("1236781");
		siteObj.getAddress().setState("Missouri");
		siteObj.getAddress().setStreet("4939 Children's Place1");
		siteObj.getAddress().setZipCode("63111");		
	}
	
	public void updateCollectionProtocolRegistration(CollectionProtocolRegistration collectionProtocolRegistration)
	{		
		CollectionProtocol collectionProtocol = (CollectionProtocol)ClientDemo.dataModelObjectMap.get("CollectionProtocol");
		collectionProtocolRegistration.setCollectionProtocol(collectionProtocol);

		Participant participant = (Participant)ClientDemo.dataModelObjectMap.get("Participant");
		collectionProtocolRegistration.setParticipant(null);

		collectionProtocolRegistration.setProtocolParticipantIdentifier("11111");
		collectionProtocolRegistration.setActivityStatus("Active");
	}
	
	public void updateSpecimenCollectionGroup(SpecimenCollectionGroup specimenCollectionGroup)
	{
		Site site = (Site)ClientDemo.dataModelObjectMap.get("Site");
		specimenCollectionGroup.setSpecimenCollectionSite(site);
		specimenCollectionGroup.setClinicalDiagnosis("Dentinoma");//Abdominal fibromatosis
		specimenCollectionGroup.setClinicalStatus("New Diagnosis"); //Operative
		specimenCollectionGroup.setActivityStatus("Active");

		CollectionProtocolEvent collectionProtocolEvent = (CollectionProtocolEvent)ClientDemo.dataModelObjectMap.get("CollectionProtocolEvent");	
		specimenCollectionGroup.setCollectionProtocolEvent(collectionProtocolEvent);

		CollectionProtocolRegistration collectionProtocolRegistration = (CollectionProtocolRegistration)ClientDemo.dataModelObjectMap.get("CollectionProtocolRegistration");
		
		specimenCollectionGroup.setCollectionProtocolRegistration(collectionProtocolRegistration);

		specimenCollectionGroup.setName("scg" + UniqueKeyGeneratorUtil.getUniqueKey());
		
		specimenCollectionGroup.setSurgicalPathologyNumber("1234");
		
		//Uncomment to set these collection and received events for all specimens associated with this scg.
		//specimenCollectionGroup.isApplyEventsToSpecimens(true);

	}	
	
	
	public void updateParticipant(Participant participant)
	{
		participant.setLastName("last" + UniqueKeyGeneratorUtil.getUniqueKey());
		participant.setFirstName("frst" + UniqueKeyGeneratorUtil.getUniqueKey());
		participant.setMiddleName("mdl" + UniqueKeyGeneratorUtil.getUniqueKey());

		/*try
		{
			System.out.println("-----------------------");
			participant.setBirthDate(Utility.parseDate("08/15/1975", Utility
					.datePattern("08/15/1975")));
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			System.out.println("-----------------------"+e);
			e.printStackTrace();
		}		
		try
		{
			System.out.println("-----------------------");
			participant.setDeathDate(Utility.parseDate("08/15/1974", Utility
					.datePattern("08/15/1974")));
		}
		catch (ParseException e)
		{
			// TODO Auto-generated catch block
			System.out.println("-----------------------"+e);
			e.printStackTrace();
		}*/
		
		participant.setVitalStatus("Alive"); //Dead
		participant.setGender("Male Gender"); //
		participant.setSexGenotype(""); //XX

		Collection raceCollection = new HashSet();
		raceCollection.add("Black or African American"); //White
		raceCollection.add("Unknown"); //Asian
		participant.setRaceCollection(raceCollection);
		participant.setActivityStatus("Active"); //Active
		participant.setEthnicity("Unknown"); //Hispanic or Latino
		//participant.setSocialSecurityNumber("333-33-3333");

		Collection participantMedicalIdentifierCollection = new HashSet();
		/*participantMedicalIdentifierCollection.add("Washington University School of Medicine");
		 participantMedicalIdentifierCollection.add("1111");
		 */
		participant
				.setParticipantMedicalIdentifierCollection(participantMedicalIdentifierCollection);
	}
	
	public void updateDistributionProtocol(DistributionProtocol distributionProtocol)
	{
		User principalInvestigator = (User) ClientDemo.dataModelObjectMap.get("User");
		/*	
		new User();
		principalInvestigator.setId(new Long(1));
		*/
		distributionProtocol.setPrincipalInvestigator(principalInvestigator);
		distributionProtocol.setTitle("DP"+ UniqueKeyGeneratorUtil.getUniqueKey());
		distributionProtocol.setShortTitle("DP"); //DP1
		distributionProtocol.setIrbIdentifier("11111");//55555
		
		try
		{
			distributionProtocol.setStartDate(Utility.parseDate("08/15/1976", Utility
					.datePattern("08/15/1976"))); //08/15/1975
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		
		distributionProtocol.setDescriptionURL("");
		distributionProtocol.setEnrollment(new Integer(20)); //10

		SpecimenRequirement specimenRequirement = (SpecimenRequirement) ClientDemo.dataModelObjectMap.get("SpecimenRequirement");
		specimenRequirement.setPathologyStatus("Non-Malignant"); //Malignant
		specimenRequirement.setTissueSite("Anal canal"); //Placenta
		specimenRequirement.setSpecimenType("Bile"); //DNA
		specimenRequirement.setSpecimenClass("Fluid"); //Molecular
		Quantity quantity = new Quantity();
		quantity.setValue(new Double(20)); //10
		specimenRequirement.setQuantity(quantity);
			
		Collection specimenRequirementCollection = new HashSet();
		specimenRequirementCollection.add(specimenRequirement);
		distributionProtocol.setSpecimenRequirementCollection(specimenRequirementCollection);

		distributionProtocol.setActivityStatus("Active"); //Active
	}
	
	public void updateCollectionProtocol(CollectionProtocol collectionProtocol)
	{
		collectionProtocol.setAliquotInSameContainer(new Boolean(false)); //true
		collectionProtocol.setDescriptionURL("");
		collectionProtocol.setActivityStatus("Active"); //Active
		collectionProtocol.setEndDate(null);
		collectionProtocol.setEnrollment(null);
		collectionProtocol.setIrbIdentifier("11111");//77777
		collectionProtocol.setTitle("cp consent" + UniqueKeyGeneratorUtil.getUniqueKey());
		collectionProtocol.setShortTitle("cp concent"); //pc!
		
		try
		{
			collectionProtocol.setStartDate(Utility.parseDate("08/15/1975", Utility
					.datePattern("08/15/1975")));
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}

		Collection collectionProtocolEventCollectionSet = new HashSet();
	 
		//CollectionProtocolEvent collectionProtocolEvent = new CollectionProtocolEvent();
		CollectionProtocolEvent collectionProtocolEvent = (CollectionProtocolEvent)ClientDemo.dataModelObjectMap.get("CollectionProtocolEvent"); 
		collectionProtocolEvent.setClinicalStatus("Not Specified");//New Diagnosis
		collectionProtocolEvent.setStudyCalendarEventPoint(new Double(2)); //1
	 

		Collection specimenRequirementCollection = new HashSet();
		//SpecimenRequirement specimenRequirement = new SpecimenRequirement();
		//specimenRequirement.setSpecimenClass("Molecular");
		//specimenRequirement.setSpecimenType("DNA");
		//specimenRequirement.setTissueSite("Placenta");
		//specimenRequirement.setPathologyStatus("Malignant");
		//Quantity quantity = new Quantity();
		//quantity.setValue(new Double(10));
		//specimenRequirement.setQuantity(quantity);
		
		SpecimenRequirement specimenRequirement  =(SpecimenRequirement)ClientDemo.dataModelObjectMap.get("SpecimenRequirement");
		specimenRequirement.setSpecimenClass("Fluid"); //Molecular
		specimenRequirement.setSpecimenType("Bile"); //DNA
		specimenRequirement.setTissueSite("Anal canal"); //Placenta
		specimenRequirement.setPathologyStatus("Non-Malignant");//Malignant
		specimenRequirementCollection.add(specimenRequirement);
		
		collectionProtocolEvent.setSpecimenRequirementCollection(specimenRequirementCollection);

		collectionProtocolEventCollectionSet.add(collectionProtocolEvent);
		collectionProtocol
				.setCollectionProtocolEventCollection(collectionProtocolEventCollectionSet);

		//User principalInvestigator = new User();
		//principalInvestigator.setId(new Long(1));
		User principalInvestigator = (User)ClientDemo.dataModelObjectMap.get("User");
		collectionProtocol.setPrincipalInvestigator(principalInvestigator);
		
		User protocolCordinator = (User)ClientDemo.dataModelObjectMap.get("User1");		
		Collection protocolCordinatorCollection = new HashSet();
		protocolCordinatorCollection.add(protocolCordinator);
		collectionProtocol.setCoordinatorCollection(protocolCordinatorCollection);
		
	}
	public void updateSpecimen(Specimen updateSpecimen)
	{				
		SpecimenCollectionGroup specimenCollectionGroup = (SpecimenCollectionGroup)ClientDemo.dataModelObjectMap.get("SpecimenCollectionGroup");
		updateSpecimen.setSpecimenCollectionGroup(specimenCollectionGroup);
		
		//updateSpecimen.setLabel("spec" + UniqueKeyGeneratorUtil.getUniqueKey());
		updateSpecimen.setBarcode("bar" + UniqueKeyGeneratorUtil.getUniqueKey());
		updateSpecimen.setType("DNA");
		updateSpecimen.setAvailable(new Boolean(true));
		updateSpecimen.setActivityStatus("Active");
		
		SpecimenCharacteristics specimenCharacteristics = (SpecimenCharacteristics)ClientDemo.dataModelObjectMap.get("SpecimenCharacteristics");
		updateSpecimen.setSpecimenCharacteristics(specimenCharacteristics);
		
		updateSpecimen.setPathologicalStatus("Non-Malignant"); //Malignant
		
		//updateSpecimen.setAvailableQuantity(quantity);
		//updateSpecimen.setConcentrationInMicrogramPerMicroliter(new Double(10));
		updateSpecimen.setComment("");
		
		updateSpecimen.setStorageContainer(null); 
		updateSpecimen.setPositionDimensionOne(null);
		updateSpecimen.setPositionDimensionTwo(null);
		
		Collection externalIdentifierCollection = new HashSet();
		ExternalIdentifier externalIdentifier = new ExternalIdentifier();
		externalIdentifier.setName("eid" + UniqueKeyGeneratorUtil.getUniqueKey());
		externalIdentifier.setValue("11");
		externalIdentifier.setSpecimen(updateSpecimen);
		
		externalIdentifierCollection.add(externalIdentifier);
		updateSpecimen.setExternalIdentifierCollection(externalIdentifierCollection);
		CollectionEventParameters collectionEventParameters = new CollectionEventParameters();
		collectionEventParameters.setComment("");
		
		User user = (User)ClientDemo.dataModelObjectMap.get("User");	
		collectionEventParameters.setUser(user);
		
		try
		{
			collectionEventParameters.setTimestamp(Utility.parseDate("08/15/1976", Utility
					.datePattern("08/15/1976"))); //08/15/1975
					
		}
		catch (ParseException e1)
		{
			System.out.println(" exception in APIDemo");
			e1.printStackTrace();
		}
		
		collectionEventParameters.setContainer("ACD Vacutainer"); //No Additive Vacutainer
		collectionEventParameters.setCollectionProcedure("Lavage");
		ReceivedEventParameters receivedEventParameters = new ReceivedEventParameters();
		receivedEventParameters.setUser(user);
		
		try
		{
			System.out.println("--- Start ---- 10");
			receivedEventParameters.setTimestamp(Utility.parseDate("08/15/1976", Utility
					.datePattern("08/15/1976"))); //08/15/1976
		}
		catch (ParseException e)
		{
			System.out.println("APIDemo");
			e.printStackTrace();
		}
		
		receivedEventParameters.setReceivedQuality("Clotted"); //Acceptable
		receivedEventParameters.setComment("");
		Collection specimenEventCollection = new HashSet();
		specimenEventCollection.add(collectionEventParameters);
		specimenEventCollection.add(receivedEventParameters);
		updateSpecimen.setSpecimenEventCollection(specimenEventCollection);
		
		Biohazard biohazard = (Biohazard)ClientDemo.dataModelObjectMap.get("Biohazard");
		Collection biohazardCollection = new HashSet();
		biohazardCollection.add(biohazard);
		updateSpecimen.setBiohazardCollection(biohazardCollection);		
		
	}
	
	
	public void updateStorageType(StorageType updateStorageType)
	{		
		Capacity capacity = updateStorageType.getCapacity();
		
		updateStorageType.setDefaultTempratureInCentigrade(new Double(30));//-30
		updateStorageType.setOneDimensionLabel("Label-1"); //label 1
		updateStorageType.setTwoDimensionLabel("Label-2"); //label 2

		capacity.setOneDimensionCapacity(new Integer(2));//3
		capacity.setTwoDimensionCapacity(new Integer(2));//3
		updateStorageType.setCapacity(capacity);
	

//		Collection holdsStorageTypeCollection = new HashSet();
//		holdsStorageTypeCollection.add(updateStorageType);
//
//		updateStorageType.setHoldsStorageTypeCollection(holdsStorageTypeCollection);
//		updateStorageType.setActivityStatus("Active");
//
//		Collection holdsSpecimenClassCollection = new HashSet();		
//		holdsSpecimenClassCollection.add("Fluid");
//		holdsSpecimenClassCollection.add("Molecular");
//		updateStorageType.setHoldsSpecimenClassCollection(holdsSpecimenClassCollection);
		
	}
	
	/**
	 * @return StorageContainer
	 */
	public void updateStorageContainer(StorageContainer storageContainer)
	{	
		StorageType storageType = (StorageType) ClientDemo.dataModelObjectMap.get("StorageType");		
		storageContainer.setStorageType(storageType);
		
		Site site = (Site) ClientDemo.dataModelObjectMap.get("Site"); 		
		storageContainer.setSite(site);
		
		storageContainer.setTempratureInCentigrade(new Double(30)); //-30
		storageContainer.setBarcode("barc" + UniqueKeyGeneratorUtil.getUniqueKey());

//		Capacity capacity = storageContainer.getCapacity();
//		capacity.setOneDimensionCapacity(new Integer(1));
//		capacity.setTwoDimensionCapacity(new Integer(2));
//		storageContainer.setCapacity(capacity);

		CollectionProtocol collectionProtocol = (CollectionProtocol) ClientDemo.dataModelObjectMap.get("CollectionProtocol");		
		
		Collection collectionProtocolCollection = new HashSet();
		collectionProtocolCollection.add(collectionProtocol);
		storageContainer.setCollectionProtocolCollection(collectionProtocolCollection);

		Collection holdsStorageTypeCollection = new HashSet();
		holdsStorageTypeCollection.add(storageType);
		storageContainer.setHoldsStorageTypeCollection(holdsStorageTypeCollection);

		Collection holdsSpecimenClassCollection = new HashSet();		
		holdsSpecimenClassCollection.add("Tissue");
		holdsSpecimenClassCollection.add("Fluid");
		holdsSpecimenClassCollection.add("Molecular");
		holdsSpecimenClassCollection.add("Cell");
		storageContainer.setHoldsSpecimenClassCollection(holdsSpecimenClassCollection);
		
		storageContainer.setPositionDimensionOne(new Integer(1));
		storageContainer.setPositionDimensionTwo(new Integer(2));

		storageContainer.setActivityStatus("Active");
		storageContainer.setFull(Boolean.valueOf(false));		
	}
	
	/**
	 * @return SpecimenArrayType
	 */
	public void updateSpecimenArrayType(SpecimenArrayType specimenArrayType)
	{		
		specimenArrayType.setSpecimenClass("Molecular"); 

		Collection specimenTypeCollection = new HashSet();
		specimenTypeCollection.add("DNA"); //
		specimenTypeCollection.add("RNA"); //
		specimenArrayType.setSpecimenTypeCollection(specimenTypeCollection);
		specimenArrayType.setComment("");
		Capacity capacity = specimenArrayType.getCapacity();
		capacity.setOneDimensionCapacity(new Integer(3)); //4
		capacity.setTwoDimensionCapacity(new Integer(3)); //4
		specimenArrayType.setCapacity(capacity);		
	}
	
	public void updateSpecimenArray(SpecimenArray specimenArray)
	{		
		SpecimenArrayType specimenArrayType = (SpecimenArrayType) ClientDemo.dataModelObjectMap.get("SpecimenArrayType");
		specimenArray.setSpecimenArrayType(specimenArrayType);
		
		specimenArray.setBarcode("bar" + UniqueKeyGeneratorUtil.getUniqueKey());
		specimenArray.setName("sa" + UniqueKeyGeneratorUtil.getUniqueKey()); 
		
		User createdBy = (User) ClientDemo.dataModelObjectMap.get("User");
		specimenArray.setCreatedBy(createdBy);
		
//		Capacity capacity = specimenArrayType.getCapacity();
//		specimenArray.setCapacity(capacity);
		
		specimenArray.setComment("");
		StorageContainer storageContainer = (StorageContainer) ClientDemo.dataModelObjectMap.get("StorageContainer");
		
		specimenArray.setStorageContainer(storageContainer);
		specimenArray.setPositionDimensionOne(new Integer(1));
		specimenArray.setPositionDimensionTwo(new Integer(1));
		
//		Collection specimenArrayContentCollection = new HashSet();
//		SpecimenArrayContent specimenArrayContent = new SpecimenArrayContent();
//		
//		Specimen specimen = (Specimen) ClientDemo.dataModelObjectMap.get("Specimen");
//		specimenArrayContent.setSpecimen(specimen);
//		specimenArrayContent.setPositionDimensionOne(new Integer(1));
//		specimenArrayContent.setPositionDimensionTwo(new Integer(1));
//		Quantity quantity = new Quantity();
//		quantity.setValue(new Double(2));
//		specimenArrayContent.setInitialQuantity(quantity);
//		specimenArrayContentCollection.add(specimenArrayContent);
//		specimenArray.setSpecimenArrayContentCollection(specimenArrayContentCollection);		
	}
	
	
	
	/**
	 * This function initializes data into Order domain object
	 */
	public OrderDetails initOrder()
    {           
          OrderDetails order = new OrderDetails();  
          order.setComment("Comment");
          
          //Obtain Distribution Protocol
          DistributionProtocol distributionProtocolObj = (DistributionProtocol)ClientDemo.dataModelObjectMap.get("DistributionProtocol");
          
          /*DistributionProtocol distributionProtocol = new DistributionProtocol();
          distributionProtocol.setId(new Long(2));*/

          order.setDistributionProtocol(distributionProtocolObj);
          order.setName("Request1 ");
          order.setStatus("New");
          try
          {
                order.setRequestedDate(Utility.parseDate("04-02-1984", Constants.DATE_PATTERN_MM_DD_YYYY));
          }

          catch (ParseException e)
          {
                Logger.out.debug(""+e);
          }
          Collection orderItemCollection = new HashSet();       

          Specimen specimen = (Specimen) ClientDemo.dataModelObjectMap.get("Specimen");

          ExistingSpecimenOrderItem exSpOrderItem = new ExistingSpecimenOrderItem();
          exSpOrderItem.setDescription("OrderDetails Item 1 of Order_Id ");
          exSpOrderItem.setStatus("New");           
          
          Quantity quantity = new Quantity();
          quantity.setValue(new Double(1));
          exSpOrderItem.setRequestedQuantity(quantity);
          exSpOrderItem.setSpecimen(specimen);
               
          orderItemCollection.add(exSpOrderItem);
          order.setOrderItemCollection(orderItemCollection);
          return order;

    }
		/**
	 * This function is to update the Order domain object.
	 */
		public OrderDetails updateOrderDetails(OrderDetails orderObj)
		{
			orderObj.setComment("UpdatedComment");
			
			//Obtain Distribution Protocol
	        DistributionProtocol distributionProtocolObj = (DistributionProtocol)ClientDemo.dataModelObjectMap.get("DistributionProtocol");
			
			orderObj.setDistributionProtocol(distributionProtocolObj);
            orderObj.setName("Updated Request Name");
            orderObj.setStatus("Pending");
            try
            {
            	orderObj.setRequestedDate(Utility.parseDate("05-02-1984", Constants.DATE_PATTERN_MM_DD_YYYY));
            }
            catch (ParseException e)
            {
                  Logger.out.debug(""+e);
            }
            Collection orderItemCollection = new HashSet(); 
            ExistingSpecimenOrderItem existingOrderItem =(ExistingSpecimenOrderItem) orderObj.getOrderItemCollection().iterator().next();
            existingOrderItem.setDescription("Updated OrderDetails Item 1 of Order_Id ");
            existingOrderItem.setStatus("Pending - Protocol Review");          
            existingOrderItem.setOrderDetails(orderObj);
           
            
            return orderObj;
		}


			
	private int getUniqueId()
	{
		return 1; 
	}
}
