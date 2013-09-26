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
 * <p>Title: ForwardToProcessor Class>
 * <p>Description:	ForwardToProcessor populates data required for ForwardTo activity</p>
 * Copyright:    Copyright (c) year
 * Company: Washington University, School of Medicine, St. Louis.
 * @author falguni_sachde
 * @version 1.00
 *  
 */

package edu.wustl.clinportal.util;

import java.util.HashMap;

import edu.wustl.common.actionForm.AbstractActionForm;
import edu.wustl.common.domain.AbstractDomainObject;
import edu.wustl.common.util.AbstractForwardToProcessor;

/**
 * ForwardToPrintProcessor populates data required for ForwardTo activity
 * 
 */
public class ForwardToPrintProcessor extends AbstractForwardToProcessor
{

	public HashMap populateForwardToData(AbstractActionForm actionForm, AbstractDomainObject domainObject)
	{

		HashMap forwardToPrintMap = new HashMap();

//		if (domainObject instanceof SpecimenCollectionGroup)
//		{
//			SpecimenCollectionGroupForm specimenCollectionGroupForm = (SpecimenCollectionGroupForm) actionForm;
//			forwardToPrintMap.put("specimenCollectionGroupId", domainObject.getId().toString());
//			forwardToPrintMap.put("specimenCollectionGroupName", specimenCollectionGroupForm.getName());
//		}
//		else if (domainObject instanceof Specimen)
//		{
//			Specimen specimen = (Specimen) domainObject;
//		
//			if (actionForm.getForwardTo().equals("createNew") )
//			{
//				forwardToPrintMap.put("parentSpecimenId", domainObject.getId());
//			}
//		
//			else if (actionForm.getForwardTo().equals("sameCollectionGroup"))
//			{
//				
//				if (specimen.getSpecimenCollectionGroup().getId() != null)
//				{
//					forwardToPrintMap.put("specimenCollectionGroupId", specimen.getSpecimenCollectionGroup().getId().toString());
//					if (actionForm instanceof NewSpecimenForm)
//					{
//						forwardToPrintMap.put("specimenCollectionGroupName", ((NewSpecimenForm) actionForm).getSpecimenCollectionGroupName());
//					}
//
//				}
//			}
//			//Add Events
//			else if (actionForm.getForwardTo().equals("eventParameters"))
//			{
//				forwardToPrintMap.put("specimenId", domainObject.getId().toString());
//				forwardToPrintMap.put(Constants.SPECIMEN_LABEL, specimen.getLabel());
//				forwardToPrintMap.put("specimenClass", specimen.getClassName());
//			}
//			else if (actionForm.getForwardTo().equals("distribution") )
//			{
//				forwardToPrintMap.put("specimenObjectKey", domainObject);
//			}
//		
//			else if (actionForm.getForwardTo().equals("pageOfAliquot") || actionForm.getForwardTo().equals("pageOfCreateAliquot"))
//			{
//				forwardToPrintMap.put("parentSpecimenId", domainObject.getId().toString());
//			}
//			else if ( actionForm.getForwardTo().equals("CPQueryPrintSpecimenEdit") || actionForm.getForwardTo().equals("CPQueryPrintSpecimenAdd") || actionForm.getForwardTo().equals("CPQueryPrintDeriveSpecimen") || actionForm.getForwardTo().equals("printDeriveSpecimen"))
//			{
//				forwardToPrintMap.put("specimenId", domainObject.getId().toString());
//			}
//		
//			if (Constants.ALIQUOT.equals(((Specimen) domainObject).getLineage()) && actionForm.getOperation().equals(Constants.ADD))
//			{
//				forwardToPrintMap = (HashMap) ((Specimen) domainObject).getAliqoutMap();
//				return forwardToPrintMap;
//			} 
//			if (actionForm.getForwardTo().equals("deriveMultiple"))
//			{
//				forwardToPrintMap.put("specimenLabel", specimen.getLabel());
//			}
//
//		}
//		 
		return forwardToPrintMap;
	}
}