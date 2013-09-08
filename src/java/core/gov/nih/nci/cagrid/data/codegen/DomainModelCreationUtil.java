package gov.nih.nci.cagrid.data.codegen;

import gov.nih.nci.cadsr.umlproject.domain.Project;
import gov.nih.nci.cagrid.cadsr.client.CaDSRServiceClient;
import gov.nih.nci.cagrid.data.extension.CadsrInformation;
import gov.nih.nci.cagrid.data.extension.CadsrPackage;
import gov.nih.nci.cagrid.data.extension.ClassMapping;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;
import gov.nih.nci.cagrid.metadata.dataservice.UMLClass;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.axis.types.URI.MalformedURIException;

/** 
 *  DomainModelCreationUtil
 *  TODO:DOCUMENT ME
 * 
 * @author David Ervin
 * 
 * @created Apr 2, 2007 1:35:01 PM
 * @version $Id: DomainModelCreationUtil.java,v 1.3 2007/12/18 19:10:26 dervin Exp $ 
 */
public class DomainModelCreationUtil {
    
    private static final Logger LOG = Logger.getLogger(DomainModelCreationUtil.class.getName());
    
    private DomainModelCreationUtil() {
        // no instantiation of this class, all methods are static
    }
    

    public static DomainModel createDomainModel(CadsrInformation cadsrInfo) 
        throws MalformedURIException, RemoteException {
        // init the cadsr service client
        String cadsrUrl = cadsrInfo.getServiceUrl();
        LOG.info("Initializing caDSR client (URL = " + cadsrUrl + ")");
        CaDSRServiceClient cadsrClient = new CaDSRServiceClient(cadsrUrl);

        // create the prototype project
        Project proj = new Project();
        proj.setLongName(cadsrInfo.getProjectLongName());
        proj.setVersion(cadsrInfo.getProjectVersion());

        // Set of selected (fully qualified) class names
        Set<String> selectedClasses = new HashSet<String>();

        // Set of targetable class names
        Set<String> targetableClasses = new HashSet<String>();

        // walk through the selected packages
        for (int i = 0; cadsrInfo.getPackages() != null && i < cadsrInfo.getPackages().length; i++) {
            CadsrPackage packageInfo = cadsrInfo.getPackages(i);
            String packName = packageInfo.getName();
            // get selected classes from the package
            if (packageInfo.getCadsrClass() != null) {
                for (int j = 0; j < packageInfo.getCadsrClass().length; j++) {
                    ClassMapping currentClass = packageInfo.getCadsrClass(j);
                    if (currentClass.isSelected()) {
                        String fullClassName = packName + "." + currentClass.getClassName();
                        selectedClasses.add(fullClassName);
                        if (currentClass.isTargetable()) {
                            targetableClasses.add(fullClassName);
                        }
                    }
                }
            }
        }

        // build the domain model
        LOG.info("Contacting caDSR to build domain model.  This might take a while...");
        System.out.println("Contacting caDSR to build domain model.  This might take a while...");
        
        String classNames[] = new String[selectedClasses.size()];
        selectedClasses.toArray(classNames);
        DomainModel model = cadsrClient.generateDomainModelForClasses(proj, classNames);
        
        LOG.info("Setting targetability in the domain model");
        System.out.println("Setting targetability in the domain model");
        UMLClass[] exposedClasses = model.getExposedUMLClassCollection().getUMLClass();
        for (int i = 0; exposedClasses != null && i < exposedClasses.length; i++) {
            String fullClassName = exposedClasses[i].getPackageName() + "." + exposedClasses[i].getClassName();
            exposedClasses[i].setAllowableAsTarget(targetableClasses.contains(fullClassName));
        }
        return model;
    }
}
