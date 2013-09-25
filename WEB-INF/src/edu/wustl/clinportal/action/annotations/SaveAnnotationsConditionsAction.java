/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */

/**
 *<p>Title: </p>
 *<p>Description:  </p>
 *<p>Copyright:TODO</p>
 *@author 
 *@version 1.0
 */

package edu.wustl.clinportal.action.annotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.ehcache.CacheException;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import edu.common.dynamicextensions.domain.integration.EntityMap;
import edu.common.dynamicextensions.domain.integration.EntityMapCondition;
import edu.common.dynamicextensions.domain.integration.FormContext;
import edu.wustl.clinportal.actionForm.AnnotationForm;
import edu.wustl.clinportal.annotations.AnnotationUtil;
import edu.wustl.clinportal.bizlogic.AnnotationBizLogic;
import edu.wustl.clinportal.util.CatissueCoreCacheManager;
import edu.wustl.common.action.BaseAction;
import edu.wustl.common.exception.BizLogicException;
import edu.wustl.common.util.global.Constants;

public class SaveAnnotationsConditionsAction extends BaseAction
{

	/**
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @throws Exception
	 * @see edu.wustl.common.action.BaseAction#
	 * executeAction(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, 
	 * javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	protected ActionForward executeAction(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		ActionForward actionfwd = null;

		AnnotationForm annotationForm = (AnnotationForm) form;

		saveConditions(annotationForm, request);
		actionfwd = mapping.findForward(Constants.SUCCESS);

		return actionfwd;
	}

	/**
	 * 
	 * @param annotationForm
	 * @param request
	 * @throws CacheException
	 * @throws BizLogicException
	 */
	private void saveConditions(AnnotationForm annotationForm, HttpServletRequest request)
			throws CacheException, BizLogicException
	{

		String containerId = request.getParameter("containerId");
		AnnotationBizLogic bizLogic = new AnnotationBizLogic();
		List dynamicList = new ArrayList();
		dynamicList = bizLogic.getListOfStaticEntities(Long.valueOf(containerId));
		CatissueCoreCacheManager catCoreCacheMger = CatissueCoreCacheManager.getInstance();

		if (dynamicList != null && !dynamicList.isEmpty())
		{
			EntityMap entityMap = (EntityMap) dynamicList.get(0);
			Collection formCollPrev = entityMap.getFormContextCollection();
			Collection currFormColl = new HashSet();
			int conditionValindex = 0;
			AnnotationUtil util = new AnnotationUtil();
			Boolean check = util.checkForAll(annotationForm.getConditionVal());

			if (formCollPrev != null && !formCollPrev.isEmpty())
			{
				Iterator formCollIt = formCollPrev.iterator();

				if (annotationForm.getConditionVal() != null)
				{
					while (formCollIt.hasNext())
					{
						FormContext formContext = (FormContext) formCollIt.next();
						if ((formContext.getNoOfEntries() == null || formContext.getNoOfEntries()
								.equals(""))
								&& (formContext.getStudyFormLabel() == null || formContext
										.getStudyFormLabel().equals("")))
						{
							if (formContext.getEntityMapConditionCollection() != null
									&& !formContext.getEntityMapConditionCollection().isEmpty())
							{
								Iterator entityMapcondnIt = formContext
										.getEntityMapConditionCollection().iterator();
								while (entityMapcondnIt.hasNext())
								{
									if (conditionValindex < annotationForm.getConditionVal().length
											&& !check)
									{
										//Use existing condition objects in edit operation
										if (conditionValindex < formContext
												.getEntityMapConditionCollection().size())
										{
											EntityMapCondition condn = (EntityMapCondition) entityMapcondnIt
													.next();
											condn.setStaticRecordId(Long.valueOf(annotationForm
													.getConditionVal()[conditionValindex]));
										}
										else
										{//if current condns are more than previously added then make new condn obj
											EntityMapCondition condn = new EntityMapCondition();
											condn.setStaticRecordId(Long.valueOf(annotationForm
													.getConditionVal()[conditionValindex]));
											condn.setFormContext(formContext);
											condn
													.setTypeId(Long
															.valueOf(catCoreCacheMger
																	.getObjectFromCache(
																			AnnotationConstants.COLLECTION_PROTOCOL_ENTITY_ID)
																	.toString()));
											formContext.getEntityMapConditionCollection()
													.add(condn);
										}
										conditionValindex++;
									}
									else if (annotationForm.getConditionVal().length <= formContext
											.getEntityMapConditionCollection().size())
									{//if previously added conditions were more than current one 
										//then deassociate previous 
										EntityMapCondition condn = (EntityMapCondition) entityMapcondnIt
												.next();
										condn.setFormContext(null);
									}
								}
							}
							//previously no condition exists but now conditions are added
							if (annotationForm.getConditionVal() != null
									&& annotationForm.getConditionVal().length > conditionValindex
									&& !check)
							{
								while (annotationForm.getConditionVal().length > conditionValindex)
								{
									EntityMapCondition condn = new EntityMapCondition();
									condn.setStaticRecordId(Long.valueOf(annotationForm
											.getConditionVal()[conditionValindex]));
									condn.setFormContext(formContext);
									condn
											.setTypeId(Long
													.valueOf(catCoreCacheMger
															.getObjectFromCache(
																	AnnotationConstants.COLLECTION_PROTOCOL_ENTITY_ID)
															.toString()));
									formContext.getEntityMapConditionCollection().add(condn);
									conditionValindex++;
								}
							}
							currFormColl.add(formContext);
						}
					}
				}
				entityMap.setFormContextCollection(currFormColl);
				bizLogic.updateEntityMap(entityMap);
			}
		}
	}
}