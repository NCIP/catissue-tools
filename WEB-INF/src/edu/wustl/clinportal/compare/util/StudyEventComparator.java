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
package edu.wustl.clinportal.compare.util;

import java.util.Comparator;

import edu.wustl.clinportal.domain.ClinicalStudyEvent;



public class StudyEventComparator implements Comparator<ClinicalStudyEvent>
{
 
    /**
     * compare event according to StudyCalendarEventPoint.
     * if same StudyCalendarEventPoint then compares according to identifier
     */
    public int compare(ClinicalStudyEvent event1, ClinicalStudyEvent event2)
    {
        if (event1.getStudyCalendarEventPoint().equals(event2.getStudyCalendarEventPoint()))
        {
            if (event1.getId() != null && event2.getId() != null)
                return event1.getId().compareTo(event2.getId());
            else
                return 0;
        }
        else
            return event1.getStudyCalendarEventPoint().compareTo(event2.getStudyCalendarEventPoint());
    }

}
