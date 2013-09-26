/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */

package edu.wustl.clinportal.client;

import edu.wustl.clinportal.domain.Participant;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.XMLPropertyHandler;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.common.util.logger.Logger;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationServiceProvider;
import gov.nih.nci.system.comm.client.ClientSession;

import java.util.List;

public class CaCoreAPIService 
{
	private ApplicationService appService = null;
	private ClientSession cs = null;
	String serverUrl=XMLPropertyHandler.getValue(Constants.SUITE_CACORE_SERVICE_URL);
	String keyPath=XMLPropertyHandler.getValue(Constants.SUITE_SERVICE_CRT_KEY_PATH);
		
	/**
	 * Method to initialize the CaCoreAPIService
	 * @throws Exception
	 */
	private void initialize(String loginName,String password) throws Exception
	{
		try
		{
			
			
			System.setProperty("javax.net.ssl.trustStore", keyPath);//"D://jboss_18080//server//default//conf//chap8.keystore");
			appService = ApplicationServiceProvider.getRemoteInstance(serverUrl);//"https://localhost:1443/catissuecore/http/remoteService");
			cs =ClientSession.getInstance();
					
			cs.startSession(loginName,password);
			//System.out.println("API session created !!!!!!");
										
		}
		catch(Exception ex)
		{
			System.out.println("Error in initializing cacoreAPIService or Please check your login information!");
			Logger.out.error("Error in initializing CaCoreAPIService "+ex); 
			Logger.out.error("Test client throws Exception = "+ ex);
			throw ex;
		}
	}
	
	
	private void terminateSession() throws Exception
	{
		cs.terminateSession();
		
	}
	
	
	/**
	 * Method to save object
	 * @param object object to be saved
	 * @return saved object
	 * @throws Exception generic exception occured
	 */
	public  Object createObject(Object object,String loginName,String password) throws Exception
	{
		try 
		{
			//Long csmUserId = Long.parseLong(sessionDataBean.getCsmUserId());
			initialize(loginName,password);
			object=appService.createObject(object);
			terminateSession();
			return object;
		} 
		catch (ApplicationException e) 
		{
			Logger.out.error("Error occured while adding object using CaCoreAPI for object:"+object.getClass());
			//throw new DAOException("Error occured while adding object using CaCoreAPI "+e.getMessage());
			throw new BizLogicException(ErrorKey.getErrorKey("error.common.cacoreaApiService"), e,
					"Error occurred while adding object using CaCoreAPI");
		}
	}
	/**
	 * Method to update object
	 * @param object object to update
	 * @return updated object
	 * @throws Exception generic exception occured
	 */
	public  Object updateObject(Object object,String loginName,String password) throws Exception
	{
		try 
		{
			//Long csmUserId = Long.parseLong(sessionDataBean.getCsmUserId());
			initialize(loginName,password);
			object=appService.updateObject(object);
			terminateSession();
			return object;
		}
		catch (ApplicationException e) 
		{
			Logger.out.error("Error occured while updating object using CaCoreAPI for object:"+object.getClass());
			//throw new DAOException("Error occured while updating object using CaCoreAPI "+e.getMessage());
			throw new BizLogicException(ErrorKey.getErrorKey("error.common.cacoreaApiService"), e,
					"Error occurred while updating object using CaCoreAPI");
		}
	}
	
	
	
	/**
	 * Method to get list of matching participant for given participant
	 * @param participant object of Participant
	 * @return list of matching participant objects
	 * @throws Exception generic exception occured
	 */
	public List getParticipantMatchingObects(Participant participant,String loginName,String password) throws Exception
	{
		try 
		{
			
			initialize(loginName,password);
			List participantList =(List)appService.getParticipantMatchingObects(participant);
			terminateSession();
			return participantList;
		} 
		catch (ApplicationException e) 
		{
			Logger.out.error("Error while retrieving matching participant list");
			//throw new DAOException("Error while retrieving matching participant list"+e.getMessage());
			throw new BizLogicException(ErrorKey.getErrorKey("error.common.cacoreaApiService"), e,
					"Error while getting participant matching objects");
		}
	}
		
	
}
