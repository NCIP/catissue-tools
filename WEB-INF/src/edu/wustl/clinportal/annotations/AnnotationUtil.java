/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright:TODO</p>
 *@author 
 *@version 1.0
 */

package edu.wustl.clinportal.annotations;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.CacheException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import edu.common.dynamicextensions.domain.integration.EntityMap;
import edu.common.dynamicextensions.domain.integration.EntityMapCondition;
import edu.common.dynamicextensions.domain.integration.FormContext;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.entitymanager.EntityManagerInterface;
import edu.common.dynamicextensions.exception.DataTypeFactoryInitializationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.clinportal.action.annotations.AnnotationConstants;
import edu.wustl.clinportal.util.CatissueCoreCacheManager;
import edu.wustl.clinportal.util.global.Utility;
import edu.wustl.common.beans.NameValueBean;
import edu.wustl.common.util.global.Constants;

/**
 * @author falguni_sachde
 *
 */
public class AnnotationUtil

{

	/**
	 * 
	 */
	public static Map map = new HashMap();

	/**
	 * @param xmlFileName
	 * @param displayNam
	 * @return
	 * @throws DataTypeFactoryInitializationException
	 */
	public final List<NameValueBean> populateStaticEntityList(String xmlFileName, String displayNam)
			throws DataTypeFactoryInitializationException
	{
		List list = new ArrayList();

		SAXReader saxReader = new SAXReader();
		InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(xmlFileName);

		Document document = null;

		try
		{
			document = saxReader.read(inputStream);
			Element className = null;
			Element displayName = null;
			Element conditionInvoker = null;

			Element primAttriElement = document.getRootElement();
			Iterator primAttrEleItr = primAttriElement.elementIterator("static-entity");

			Element prAttriElement1 = null;

			while (primAttrEleItr.hasNext())
			{
				prAttriElement1 = (Element) primAttrEleItr.next();

				className = prAttriElement1.element("name");
				displayName = prAttriElement1.element("displayName");
				conditionInvoker = prAttriElement1.element("conditionInvoker");
				list
						.add(new NameValueBean(displayName.getStringValue(), className
								.getStringValue()));

				if (displayNam != null)
				{
					if (className.getText().equals(displayNam))
					{
						map.put("name", className.getText());
						map.put("displayName", displayName.getText());
						map.put("conditionInvoker", conditionInvoker.getText());
					}
				}

			}
		}
		catch (DocumentException documentException)
		{
			throw new DataTypeFactoryInitializationException(documentException);
		}

		return list;
	}

	/**
	 * @param conditions
	 * @param entityMapObj
	 * @return
	 * @throws CacheException
	 */
	public Collection getFormContextCollection(String[] conditions, EntityMap entityMapObj)
			throws CacheException
	{
		CatissueCoreCacheManager caCoreCacheMgr = CatissueCoreCacheManager.getInstance();
		Collection frmContxtColn = new HashSet();
		FormContext formContext = new FormContext();
		Collection entityMapCondColn = new HashSet();
		if (conditions != null)
		{
			for (int i = 0; i < conditions.length; i++)
			{

				/*Here every time new form context can be created for each condition 
				 *though form is one , just to make different form context as "noOfEntries" attribute
				 *for form Context can change or can be assigned in future */
				formContext.setEntityMap(entityMapObj);
				boolean check = checkForAll(conditions);

				if (!check
						&& !conditions[i].equals(Integer.valueOf(Constants.SELECT_OPTION_VALUE)
								.toString()) && !conditions[i].equals(edu.wustl.clinportal.util.global.Constants.ALL))
				{
					EntityMapCondition entyMapCondn = new EntityMapCondition();
					entyMapCondn.setFormContext(formContext);
					entyMapCondn.setStaticRecordId(Long.valueOf(conditions[i]));
					Long typeId = (Long) caCoreCacheMgr
							.getObjectFromCache(AnnotationConstants.CLINICAL_STUDY_ENTITY_ID);
					entyMapCondn.setTypeId(typeId);
					entityMapCondColn.add(entyMapCondn);
					formContext.setEntityMapConditionCollection(entityMapCondColn);

				}
			}
		}
		frmContxtColn.add(formContext);
		return frmContxtColn;

	}

	/**
	 * @param conditions
	 * @return
	 */
	public boolean checkForAll(String[] conditions)
	{
		boolean returnVal = false;
		if (conditions != null)
		{
			for (int i = 0; i < conditions.length; i++)
			{
				if (conditions[i].equals(edu.wustl.clinportal.util.global.Constants.ALL))
				{
					returnVal = true;
					break;
				}
			}
		}

		return returnVal;
	}

	/**
	 * @param annotationForm 
	 * @throws DynamicExtensionsApplicationException 
	 * @throws DynamicExtensionsSystemException 
	 * @throws CacheException 
	 * @throws IllegalStateException 
	 * 
	 */
	public static List getSystemEntityList() throws DynamicExtensionsSystemException,
			DynamicExtensionsApplicationException, IllegalStateException, CacheException
	{
		List<NameValueBean> systemEntityList = new ArrayList<NameValueBean>();
		AnnotationUtil util = new AnnotationUtil();
		List<NameValueBean> staticEntyInfoLst = util.populateStaticEntityList(
				"StaticEntityInformation.xml", null);
		CatissueCoreCacheManager cacheManager = CatissueCoreCacheManager.getInstance();
		if (cacheManager.getObjectFromCache(AnnotationConstants.STATIC_ENTITY_LIST) == null)
		{
			systemEntityList.add(new NameValueBean(Constants.SELECT_OPTION,
					Constants.SELECT_OPTION_VALUE));
			if (staticEntyInfoLst != null && !staticEntyInfoLst.isEmpty())
			{
				Iterator listIterator = staticEntyInfoLst.iterator();
				while (listIterator.hasNext())
				{
					NameValueBean nameValueBean = (NameValueBean) listIterator.next();
					systemEntityList.add(new NameValueBean(nameValueBean.getName(), Utility
							.getEntityId(nameValueBean.getValue())));
				}
			}
			cacheManager.addObjectToCache(AnnotationConstants.STATIC_ENTITY_LIST,
					(Serializable) systemEntityList);
		}
		else
		{
			systemEntityList = (List<NameValueBean>) cacheManager
					.getObjectFromCache(AnnotationConstants.STATIC_ENTITY_LIST);
		}
		return systemEntityList;
	}

	/**
	 * @param deContainerId
	 * @return
	 * @throws DynamicExtensionsApplicationException 
	 * @throws DynamicExtensionsSystemException 
	 */
	public static String getDEContainerName(Long deContainerId)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException
	{
		String containerName = null;
		if (deContainerId != null)
		{
			EntityManagerInterface entityManager = EntityManager.getInstance();
			containerName = entityManager.getContainerCaption(deContainerId);
		}
		return containerName;
	}

}
