/*
 * Created on January 5, 2007
 * @author
 *
 */

package edu.wustl.clinportal.action.annotations;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.CacheException;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.hibernate.HibernateException;

import edu.common.dynamicextensions.domain.Entity;
import edu.common.dynamicextensions.domain.userinterface.Container;
import edu.common.dynamicextensions.domaininterface.AssociationInterface;
import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.common.dynamicextensions.domaininterface.userinterface.ContainerInterface;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.ui.webui.util.WebUIManager;
import edu.common.dynamicextensions.ui.webui.util.WebUIManagerConstants;
import edu.wustl.clinportal.DEIntegration.DEIntegration;
import edu.wustl.clinportal.actionForm.AnnotationForm;
import edu.wustl.clinportal.annotations.PathObject;
import edu.wustl.clinportal.domain.ClinicalStudy;
import edu.wustl.clinportal.util.CatissueCoreCacheManager;
import edu.wustl.clinportal.util.global.Constants;
import edu.wustl.common.action.SecureAction;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.beans.SessionDataBean;
import edu.wustl.common.bizlogic.DefaultBizLogic;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.exception.ErrorKey;
import edu.wustl.common.util.global.CommonServiceLocator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.dao.DAO;
import edu.wustl.dao.JDBCDAO;
import edu.wustl.dao.daofactory.DAOConfigFactory;
import edu.wustl.dao.daofactory.IDAOFactory;
import edu.wustl.dao.exception.DAOException;
import edu.wustl.security.exception.UserNotAuthorizedException;
import edu.wustl.security.global.Permissions;
import edu.wustl.security.privilege.PrivilegeCache;
import edu.wustl.security.privilege.PrivilegeManager;

/**
 * @author falguni sachde
 */
/**
 * This class is responsible for loading the annotation information
 */
public class LoadAnnotationDefinitionAction extends SecureAction
{

	private transient Logger logger = Logger.getCommonLogger(LoadAnnotationDefinitionAction.class);

	/* (non-Javadoc)
	 * @see edu.wustl.common.action.BaseAction#executeAction(org.apache.struts.action.ActionMapping, 
	 * org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, 
	 * javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected ActionForward executeSecureAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{

		ActionForward actionfwd = null;
		/*
		 * 
		 */
		String comingFrom = (String) request.getSession()
				.getAttribute(Constants.FORWARD_CONTROLLER);
		AnnotationForm annotationForm = (AnnotationForm) form;
		annotationForm.setSelectedStaticEntityId(null);

		if (!isAuthorizedToExecute(request))
		{
			ActionErrors errors = new ActionErrors();
			ActionError error = new ActionError("access.execute.action.denied");
			errors.add(ActionErrors.GLOBAL_ERROR, error);
			saveErrors(request, errors);

			return mapping.findForward(edu.wustl.common.util.global.Constants.ACCESS_DENIED);
		}

		//annotationForm.setConditionVal(null);            
		//Ajax Code
		if (request.getParameter(AnnotationConstants.AJAX_OPERATION) != null
				&& !request.getParameter(AnnotationConstants.AJAX_OPERATION).equals("null"))
		{
			//If operation not null -> ajaxOperation
			processAjaxOperation(request, response);
		}
		else if (request.getParameter(WebUIManager.getOperationStatusParameterName()) != null)
		{
			//Return from dynamic extensions
			processResponseFromDynamicExtensions(request);
			loadAnnotations(annotationForm);
			request.setAttribute(Constants.OPERATION, Constants.LOAD_INTEGRATION_PAGE);
			if (comingFrom != null && !comingFrom.equals(""))
			{
				actionfwd = mapping.findForward(comingFrom);

			}
			else
			{
				actionfwd = mapping.findForward(Constants.SUCCESS);
			}
		}
		else
		{
			loadAnnotations(annotationForm);
			actionfwd = mapping.findForward(Constants.SUCCESS);
		}
		//On creation of clinical study directly move to Local extension page 
		if (request.getParameter(Constants.LOCAL_EXT) != null)// && new Long(annotationForm.getId())!= null )
		{
			//String conditions[]={request.getParameter(Constants.LOCAL_EXT)};
			String condition = (String) request.getParameter(Constants.SYSTEM_IDENTIFIER);
			if (condition == null || condition.equalsIgnoreCase("0"))
			{
				Long identifier = (Long) request.getAttribute(Constants.SYSTEM_IDENTIFIER);
				condition = identifier.toString();
			}
			String conditions[] = {condition};
			annotationForm.setConditionVal(conditions);
			CatissueCoreCacheManager catCoreCacheMger = CatissueCoreCacheManager.getInstance();

			annotationForm.setSelectedStaticEntityId(catCoreCacheMger.getObjectFromCache(
					"participantEntityId").toString());
			actionfwd = mapping.findForward(Constants.DEFINE_ENTITY);
		}
		//this is while edit operation for conditions
		if (request.getParameter(Constants.LINK) != null)
		{
			String link = request.getParameter(Constants.LINK);
			request.setAttribute(Constants.LINK, link);

			String containerId = request.getParameter(Constants.CONTAINERID);
			request
					.setAttribute(Constants.CONTAINERID, request
							.getParameter(Constants.CONTAINERID));
			if (containerId != null)
			{
				annotationForm.setSelectedStaticEntityId(request
						.getParameter(Constants.SELECTED_ENTITY_ID));

				EntityManagerInterface entityManager = EntityManager.getInstance();
				String containerCaption = entityManager.getContainerCaption(Long
						.valueOf(containerId));
				request.setAttribute(Constants.CONTAINER_NAME, containerCaption);

				//getCPConditions(annotationForm, containerId);
			}

			actionfwd = mapping.findForward(Constants.SUCCESS);
		}

		return actionfwd;
	}

	/**
	 * @param request
	 * @throws UserNotAuthorizedException 
	 * @throws BizLogicException 
	 * @throws CacheException 
	 * @throws DAOException 
	 * @throws DynamicExtensionsSystemException 
	 * @throws DynamicExtensionsApplicationException 
	 */
	private void processResponseFromDynamicExtensions(HttpServletRequest request)
			throws BizLogicException, UserNotAuthorizedException, CacheException, DAOException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		String operationStatus = request.getParameter(WebUIManager
				.getOperationStatusParameterName());
		if ((operationStatus != null)
				&& (operationStatus.trim().equals(WebUIManagerConstants.SUCCESS)))
		{
			String dynExtContainerId = request.getParameter(WebUIManager
					.getContainerIdentifierParameterName());
			String staticEntityId = CatissueCoreCacheManager.getInstance().getObjectFromCache(
					AnnotationConstants.RECORD_ENTRY_ENTITY_ID).toString();
			//getStaticEntityIdForLinking(request);

			String[] staticRecordId = getStaticRecordIdForLinking(request);

			logger.info("Need to link static entity [" + staticEntityId + "] to dyn ent ["
					+ dynExtContainerId + "]");
			//           CatissueCoreCacheManager.getInstance().addObjectToCache(AnnotationConstants.FORM_ID, Long.decode(dynExtContainerId));
			if (dynExtContainerId != null)
			{
				request.getSession().setAttribute(AnnotationConstants.FORM_ID,
						Long.decode(dynExtContainerId));
			}

			linkEntities(request, staticEntityId, dynExtContainerId, staticRecordId);
		}
	}

	/**
	 * @param request 
	 * @param staticEntityId
	 * @param dynExtContainerId
	 * @param staticRecordIds
	 * @throws UserNotAuthorizedException 
	 * @throws BizLogicException 
	 * @throws CacheException 
	 * @throws DAOException 
	 * @throws DynamicExtensionsSystemException 
	 * @throws DynamicExtensionsApplicationException 
	 */
	private void linkEntities(HttpServletRequest request, String staticEntityId,
			String dynExtContainerId, String[] staticRecordIds) throws BizLogicException,
			UserNotAuthorizedException, CacheException, DAOException,
			DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		DEIntegration integrate = new DEIntegration();
		if ((staticEntityId != null) && (dynExtContainerId != null))
		{
			boolean iscategory = integrate.isCategory(Long.valueOf(dynExtContainerId));
			integrate.addAssociation(Long.valueOf(staticEntityId), Long.valueOf(dynExtContainerId),
					false, iscategory);
			//AnnotationUtil.addAssociation(new Long(staticEntityId), new Long(dynExtContainerId), false);

		}
		else if (dynExtContainerId != null)
		{
			//Retrieving the container
			//Session session = null;
			String appName = CommonServiceLocator.getInstance().getAppName();
			IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
			DAO dao = daoFactory.getDAO();
			try
			{
				//session = DBUtil.currentSession();
				//session = dao.getCleanSession();
				dao.openSession(null);
				ContainerInterface dynamicContainer = null;
				EntityInterface staticEntity = null;
				/*staticEntity = (EntityInterface) session.load(Entity.class, Long
						.valueOf(staticEntityId));*/
				staticEntity = (EntityInterface) dao.retrieveById(Entity.class.getName(), Long
						.valueOf(staticEntityId));
				/*dynamicContainer = (Container) session.load(Container.class, Long
						.valueOf(dynExtContainerId));*/
				dynamicContainer = (Container) dao.retrieveById(Container.class.getName(), Long
						.valueOf(dynExtContainerId));
				AssociationInterface association = getAssociationForEntity(staticEntity,
						(EntityInterface) dynamicContainer.getAbstractEntity());

				//  Get entity group that is used by caB2B for path finder purpose.
				edu.wustl.cab2b.common.util.Utility.getEntityGroup(staticEntity);

				staticEntity = EntityManager.getInstance().persistEntityMetadataForAnnotation(
						staticEntity, true, false, association);

				Set<PathObject> processedPathList = new HashSet<PathObject>();
				//Adding paths from second level as first level paths between static entity 
				//and top level dynamic entity have already been added
				addQueryPathsForEntityHierarchy((EntityInterface) dynamicContainer
						.getAbstractEntity()/*,entityGroupInterface*/, processedPathList);
			}
			catch (HibernateException e1)
			{
				logger.error("Error while linking Entities", e1);
				throw new BizLogicException(ErrorKey.getErrorKey("error.common.hibernate"), e1,
						"Hibernate Exception while linking entities");
			}
			finally
			{
				try
				{
					//DBUtil.closeSession();
					//dao.closeCleanSession();
					dao.closeSession();
				}
				catch (HibernateException e)
				{
					logger.error("Error while closing session", e);
					throw new BizLogicException(ErrorKey.getErrorKey("error.common.hibernate"), e,
							"Hibernate Exception while closing session");
				}
			}
		}
	}

	/**
	 * @param dynamicEntity
	 * @param processedPathList
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws BizLogicException 
	 */
	private void addQueryPathsForEntityHierarchy(
			EntityInterface dynamicEntity/*,EntityGroupInterface entityGroupInterface*/,
			Set<PathObject> processedPathList) throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, BizLogicException
	{
		Collection<AssociationInterface> assnColl = dynamicEntity.getAllAssociations();
		for (AssociationInterface association : assnColl)
		{
			PathObject pathObject = new PathObject();
			pathObject.setSourceEntity(dynamicEntity);
			pathObject.setTargetEntity(association.getTargetEntity());

			if (processedPathList.contains(pathObject))
			{
				return;
			}
			else
			{
				processedPathList.add(pathObject);
			}

			boolean ispathAdded = isPathAdded(dynamicEntity.getId(), association.getTargetEntity()
					.getId());
			if (!ispathAdded && dynamicEntity.getId() != null)
			{
				JDBCDAO jdbcdao = null;
				String appName = CommonServiceLocator.getInstance().getAppName();
				IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
				try
				{

					jdbcdao = daoFactory.getJDBCDAO();
					jdbcdao.openSession(null);
					edu.wustl.clinportal.bizlogic.AnnotationUtil.addPathsForQuery(dynamicEntity
							.getId(), association.getTargetEntity(), association.getId(), false,
							jdbcdao);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
				finally
				{
					try
					{
						jdbcdao.commit();
						jdbcdao.closeSession();
					}
					catch (DAOException e)
					{
						throw new DynamicExtensionsSystemException(
								"Exception in addInheritancePathforSystemGenerated paths." + e);
					}
				}

				edu.wustl.clinportal.bizlogic.AnnotationUtil.addEntitiesToCache(false, association
						.getTargetEntity(), dynamicEntity);
			}
			addQueryPathsForEntityHierarchy(
					association.getTargetEntity()/*,entityGroupInterface*/, processedPathList);
		}
	}

	/**
	 * @param staticEntityId
	 * @param dynamicEntityId
	 * @return
	 */
	private boolean isPathAdded(Long staticEntityId, Long dynamicEntityId/*, Long deAssociationId*/)
	{
		boolean ispathAdded = false;
		//Connection conn = null;
		String appName = CommonServiceLocator.getInstance().getAppName();
		IDAOFactory daoFactory = DAOConfigFactory.getInstance().getDAOFactory(appName);
		//DAO dao = null;
		JDBCDAO jdbcdao = null;

		/*try
		{
			//conn = DBUtil.getConnection();
			dao = daoFactory.getDAO();
			conn = dao.getCleanConnection();
			
		}
		catch (DAOException e)
		{
			logger.error("Exception while getting connection ", e);
		}*/
		//PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try
		{
			/*String checkForPathQuery = "select path_id from path where FIRST_ENTITY_ID = ? and LAST_ENTITY_ID = ?";
			
			preparedStatement = conn.prepareStatement(checkForPathQuery);
			preparedStatement.setLong(1, staticEntityId);

			preparedStatement.setLong(2, dynamicEntityId);
			resultSet = preparedStatement.executeQuery();*/

			jdbcdao = daoFactory.getJDBCDAO();
			jdbcdao.openSession(null);
			String checkForPathQuery = "select path_id from path where FIRST_ENTITY_ID = "
					+ staticEntityId + " and LAST_ENTITY_ID = " + dynamicEntityId;
			resultSet = jdbcdao.getQueryResultSet(checkForPathQuery);
			if (resultSet != null)
			{
				while (resultSet.next())
				{
					ispathAdded = true;
					break;
				}
			}

			//System.out.println("ispathAdded  ----- " + ispathAdded);

		}
		catch (DAOException e)
		{
			logger.error("Exception while getting connection ", e);
		}
		catch (SQLException e)
		{
			logger.error("SQLException while checking if path is added ", e);
		}
		finally
		{

			try
			{
				resultSet.close();
				//preparedStatement.close();
				//DBUtil.closeConnection();
				jdbcdao.closeSession();
			}
			/*catch (HibernateException e)
			{
				logger.error("HibernateException while closing database resources", e);
			}*/
			catch (SQLException e)
			{
				logger.error("SQLException while closing database resources", e);
			}
			catch (DAOException e)
			{
				logger.error("DAOException while closing database resources", e);
			}

		}
		return ispathAdded;
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @throws CacheException
	 */
	private String[] getStaticRecordIdForLinking(HttpServletRequest request) throws CacheException
	{

		String[] staticRecordId = (String[]) request.getSession().getAttribute(
				AnnotationConstants.SELECTED_STATIC_RECORDID);
		request.getSession().removeAttribute(AnnotationConstants.SELECTED_STATIC_RECORDID);

		return staticRecordId;
	}

	/**
	 * @param request
	 * @param response
	 * @throws IOException 
	 * @throws DynamicExtensionsApplicationException 
	 * @throws DynamicExtensionsSystemException 
	 * @throws DAOException 
	 * @throws CacheException 
	 */
	private void processAjaxOperation(HttpServletRequest request, HttpServletResponse response)
			throws IOException, DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, DAOException, CacheException
	{
		String operation = request.getParameter(AnnotationConstants.AJAX_OPERATION);
		if ((operation != null)
				&& (operation.equalsIgnoreCase(AnnotationConstants.AJAX_OPERATION_SELECT_GROUP)))
		{
			String groupId = request
					.getParameter(AnnotationConstants.AJAX_OPERATION_SELECTED_GROUPID);
			String entitiesXML = getEntitiesForGroupAsXML(groupId, request);
			sendResponse(entitiesXML, response);
		}
	}

	/**
	 * @param containerId 
	 * @return
	 */
	private String getDynamicExtentionsEditURL(Long containerId)
	{
		//TODO change this with new api
		//String dExtEditEntityURL = "BuildDynamicEntity.do?containerId=" + containerId + "^_self";
		return "BuildDynamicEntity.do?containerId=" + containerId + "^_self";
	}

	private String getDynamicExtentionsEditCondnURL(Long containerId, Long staticEntityId)
	{
		//TODO change this with new api
		String dExtEditEntityURL = "DefineAnnotations.do?link=editCondn&amp;containerId="
				+ containerId + "&amp;selectedStaticEntityId=" + staticEntityId + "^_self";
		return dExtEditEntityURL;
	}

	/**
	 * @param groupId
	 * @param request
	 * @return
	 * @throws DynamicExtensionsApplicationException 
	 * @throws DynamicExtensionsSystemException 
	 * @throws DAOException 
	 * @throws CacheException 
	 */
	private String getEntitiesForGroupAsXML(String groupId, HttpServletRequest request)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException,
			DAOException, CacheException
	{
		StringBuffer entitiesXML = new StringBuffer("<?xml version='1.0' encoding='UTF-8'?>");
		//entitiesXML.append();
		if (groupId != null)
		{
			Long lGroupId = Long.valueOf(groupId);
			Collection<NameValueBean> entityContColl = EntityManager.getInstance()
					.getMainContainer(lGroupId);

			if (entityContColl != null)
			{
				entitiesXML.append("<rows>");
				Iterator<NameValueBean> contnerCollItr = entityContColl.iterator();
				int entityIndex = 1;
				while (contnerCollItr.hasNext())
				{
					NameValueBean container = contnerCollItr.next();
					entitiesXML.append(getEntityXMLString(container, entityIndex, request));
					entityIndex++;
				}
				entitiesXML.append("</rows>");
			}
		}
		return entitiesXML.toString();
	}

	/**
	 * @param container
	 * @param entityIndex 
	 * @param request
	 * @return
	 * @throws DAOException 
	 * @throws DynamicExtensionsSystemException 
	 * @throws DynamicExtensionsApplicationException 
	 * @throws CacheException 
	 */
	private String getEntityXMLString(NameValueBean container, int entityIndex,
			HttpServletRequest request) throws DAOException, DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, CacheException
	{
		StringBuffer entityXML = new StringBuffer();
		CatissueCoreCacheManager catCoreCacheMgr = CatissueCoreCacheManager.getInstance();
		Long staticEntityId = (Long) catCoreCacheMgr
				.getObjectFromCache(AnnotationConstants.RECORD_ENTRY_ENTITY_ID);
		if (container != null)
		{
			int index = 1;
			String editDExtEntyURL = getDynamicExtentionsEditURL(Long.valueOf(container.getValue()));

			String editDECondnURL = getDynamicExtentionsEditCondnURL(Long.valueOf(container
					.getValue()), staticEntityId);

			entityXML.append(getXMLForEntityMap(container.getName(), staticEntityId, entityIndex
					+ index, editDExtEntyURL, editDECondnURL, request));

		}

		return entityXML.toString();
	}

	/**
	 * @param caption
	 * @param entityMapObj
	 * @param dExtEditEntityURL 
	 * @param i
	 * @return
	 * @throws DynamicExtensionsApplicationException 
	 * @throws DynamicExtensionsSystemException 
	 * @throws CacheException 
	 */
	private StringBuffer getXMLForEntityMap(String containercaption, Long staticEntityId,
			int rowId, String dExtEditEntityURL, String editDExtCondnURL, HttpServletRequest request)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException,
			CacheException
	{
		String staticEntityName = getEntityName(staticEntityId, request);
		StringBuffer entityMapXML = new StringBuffer();
		entityMapXML.append("<row id='" + rowId + "'><cell>");
		//entityMapXML.append("<cell>0</cell>");
		entityMapXML.append(containercaption + "^" + dExtEditEntityURL + "</cell><cell>");
		entityMapXML.append(staticEntityName);
		entityMapXML.append("</cell><cell></cell><cell></cell><cell>");
		entityMapXML.append(edu.wustl.clinportal.util.global.Constants.EDIT_CONDN);
		entityMapXML.append("^");
		entityMapXML.append(editDExtCondnURL);
		entityMapXML.append("</cell></row>");
		return entityMapXML;
	}

	/**
	 * @param entityId
	 * @return
	 * @throws DynamicExtensionsApplicationException 
	 * @throws DynamicExtensionsSystemException 
	 * @throws CacheException 
	 */
	private String getEntityName(Long entityId, HttpServletRequest request)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException,
			CacheException
	{
		String entityName = "";
		if (entityId != null)
		{
			EntityManager.getInstance();
			List staticEntityList = (List) request.getSession().getAttribute(
					AnnotationConstants.STATIC_ENTITY_LIST);

			if (staticEntityList != null && !staticEntityList.isEmpty())
			{
				Iterator listIterator = staticEntityList.iterator();
				while (listIterator.hasNext())
				{
					NameValueBean nameValueBean = (NameValueBean) listIterator.next();
					if (nameValueBean.getValue().equalsIgnoreCase(entityId.toString()))
					{
						entityName = nameValueBean.getName();
					}
				}
			}

		}
		return entityName;
	}

	/**
	 * @throws IOException 
	 * 
	 */
	private void sendResponse(String responseXML, HttpServletResponse response) throws IOException
	{
		PrintWriter out = response.getWriter();
		out.write(responseXML);
	}

	/**
	 * @param annotationForm
	 * @throws DynamicExtensionsApplicationException 
	 * @throws DynamicExtensionsSystemException 
	 * @throws CacheException 
	 * @throws IllegalStateException 
	 */
	private void loadAnnotations(AnnotationForm annotationForm)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException,
			IllegalStateException, CacheException
	{
		if (annotationForm != null)
		{
			//load list of system entities

			//Load list of groups
			loadGroupList(annotationForm);

			List condInstancesLst = populateConditionalInstanceList();
			annotationForm.setConditionalInstancesList(condInstancesLst);
			annotationForm.setConditionVal(new String[]{Constants.ALL});

		}
	}

	/**
	 * @return
	 * @throws BizLogicException 
	 */
	private List populateConditionalInstanceList()
	{
		List condInstLst = new ArrayList();

		try
		{
			DefaultBizLogic bizLogic = new DefaultBizLogic();
			String[] displayNames = {"shortTitle", "title"};
			condInstLst = bizLogic.getList(ClinicalStudy.class.getName(), displayNames, "id", true);
			condInstLst = modifyName(condInstLst);
			while (condInstLst.size() != 0)
			{
				condInstLst.remove(0);
			}
			condInstLst.add(0, new NameValueBean(
					edu.wustl.clinportal.util.global.Constants.HOLDS_ANY, Constants.ALL));
		}
		catch (BizLogicException e)
		{
			logger.error("Error getting Conditional Instance List");
		}
		return condInstLst;
	}

	/**
	 * @param condInstancesLst
	 * @return
	 */
	private List modifyName(List condInstancesLst)
	{

		List list = new ArrayList();
		for (Object x : condInstancesLst)
		{

			NameValueBean bean = (NameValueBean) x;
			String[] split = bean.getName().split(",");
			if (split != null && split.length > 1)
			{
				String name;
				name = split[0] + " (" + split[1].trim() + ")";
				bean.setName(name);

			}
			list.add(bean);

		}
		return list;
	}

	/**
	 * @param annotationForm 
	 * @throws DynamicExtensionsApplicationException 
	 * @throws DynamicExtensionsSystemException 
	 * 
	 */
	private void loadGroupList(AnnotationForm annotationForm)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		//List of groups
		Collection<NameValueBean> annotGrpsLst = getAnnotationGroups();
		String groupsXML = getGroupsXML(annotGrpsLst);
		annotationForm.setAnnotationGroupsXML(groupsXML);
	}

	/**
	 * @param annotnGrpLst
	 * @return
	 */
	private String getGroupsXML(Collection<NameValueBean> annotnGrpLst)
	{
		StringBuffer groupsXML = new StringBuffer();
		if (annotnGrpLst != null)
		{
			groupsXML.append("<?xml version='1.0' encoding='UTF-8'?><rows>");
			NameValueBean groupBean = null;
			Iterator<NameValueBean> iterator = annotnGrpLst.iterator();
			while (iterator.hasNext())
			{
				groupBean = iterator.next();
				if (groupBean != null)
				{
					groupsXML.append("<row id='" + groupBean.getValue() + "' ><cell>");
					//groupsXML.append("<cell>0</cell>");
					groupsXML.append(groupBean.getName());
					groupsXML.append("</cell></row>");
				}
			}
			groupsXML.append("</rows>");
		}
		return groupsXML.toString();
	}

	/**
	 * @return
	 * @throws DynamicExtensionsApplicationException 
	 * @throws DynamicExtensionsSystemException 
	 */
	private Collection<NameValueBean> getAnnotationGroups()
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		EntityManagerInterface entityManager = EntityManager.getInstance();
		return entityManager.getAllEntityGroupBeans();
	}

	/**
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	protected boolean isAuthorizedToExecute(HttpServletRequest request) throws Exception
	{
		PrivilegeManager privilegeManager = PrivilegeManager.getInstance();
		PrivilegeCache privilegeCache = privilegeManager
				.getPrivilegeCache(getUserLoginName(request));

		boolean isAuthorized = privilegeCache.hasPrivilege(
				getObjectIdForSecureMethodAccess(request), Permissions.EXECUTE);

		return isAuthorized;
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	protected SessionDataBean getSessionData(HttpServletRequest request)
	{

		return super.getSessionData(request);

	}

	/**
	 * getAssociationForEntity.
	 * @param staticEntity
	 * @param dynamicEntity
	 * @return
	 */
	private AssociationInterface getAssociationForEntity(EntityInterface staticEntity,
			EntityInterface dynamicEntity)
	{
		Collection<AssociationInterface> asstionColln = staticEntity.getAssociationCollection();
		for (AssociationInterface associationInteface : asstionColln)
		{
			if (associationInteface.getTargetEntity() != null
					&& associationInteface.getTargetEntity().equals(dynamicEntity))
			{
				return associationInteface;
			}
		}
		return null;
	}

}
