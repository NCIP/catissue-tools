/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */


package edu.wustl.clinportal.bizlogic.test;

import edu.wustl.common.test.BaseTestCase;
import edu.wustl.common.util.logger.LoggerConfig;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationServiceProvider;
import gov.nih.nci.system.comm.client.ClientSession;

/**
 * @author ganesh_naikwade
 *
 */
public class ClinportalBaseTestCase extends BaseTestCase{

	/**
	 * @throws java.lang.Exception
	 */
	
//	Mock hibDAO;
//	Mock jdbcDAO;
	static ApplicationService appService = null;
	//static ApplicationService appServiceDEEntity = null;
	public ClinportalBaseTestCase(){
		super();
	}
	
	/**
	 * 
	 */
	public void setUp()
	{
		LoggerConfig.configureLogger("");
		System.setProperty("javax.net.ssl.trustStore", "D://jboss-4.2.2//server//default//conf//chap8.keystore");
		appService = ApplicationServiceProvider.getApplicationService();

		ClientSession cs = ClientSession.getInstance();
		try
		{
			cs.startSession("clin_portal@persistent.co.in", "Master5");
		}

		catch (Exception ex)
		{
			System.out.println(ex.getMessage());
			ex.printStackTrace();
			fail();
			System.exit(1);
		}
	}
	
	/*public static void initDEService()
	{
		appServiceDEEntity = ApplicationServiceProvider
				.getRemoteInstance("http://localhost:8080/newsurgery/http/remoteService");
	}*/


}
