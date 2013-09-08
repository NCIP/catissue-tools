package gov.nih.nci.cagrid.data.ui.domain;

import gov.nih.nci.cagrid.data.extension.ClassMapping;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;

import java.util.EventListener;

/** 
 *  DomainModelClassSelectionListener
 *  Listens for changes to the class selection in the domain model
 * 
 * @author David Ervin
 * 
 * @created Apr 10, 2007 3:16:54 PM
 * @version $Id: DomainModelClassSelectionListener.java,v 1.2 2007/08/21 21:02:11 dervin Exp $ 
 */
public interface DomainModelClassSelectionListener extends EventListener {

    public void classSelected(String packageName, ClassMapping mapping, NamespaceType packageNamespace);
    
    
    public void classDeselected(String packageName, String className);
    
    
    public void classesCleared();
}
