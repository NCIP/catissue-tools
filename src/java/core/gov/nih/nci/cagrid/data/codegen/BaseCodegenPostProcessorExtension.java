package gov.nih.nci.cagrid.data.codegen;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.ExtensionDataUtils;
import gov.nih.nci.cagrid.data.extension.AdditionalLibraries;
import gov.nih.nci.cagrid.data.extension.CadsrInformation;
import gov.nih.nci.cagrid.data.extension.CadsrPackage;
import gov.nih.nci.cagrid.data.extension.ClassMapping;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.data.mapping.ClassToQname;
import gov.nih.nci.cagrid.data.mapping.Mappings;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.namespace.SchemaElementType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionException;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionPostProcessor;
import gov.nih.nci.cagrid.introduce.extension.ExtensionTools;
import gov.nih.nci.cagrid.introduce.extension.utils.ExtensionUtilities;

import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;

/** 
 *  BaseCodegenPostProcessorExtension
 *  Base class for the DS codegen post processor extension
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Jun 16, 2006 
 * @version $Id: BaseCodegenPostProcessorExtension.java,v 1.3 2007/12/18 19:10:26 dervin Exp $ 
 */
public abstract class BaseCodegenPostProcessorExtension implements CodegenExtensionPostProcessor {
	private static final Logger logger = Logger.getLogger(DataServiceOperationProviderCodegenPostProcessor.class);
    
    public void postCodegen(ServiceExtensionDescriptionType desc, ServiceInformation info)
        throws CodegenExtensionException {
        modifyEclipseClasspath(desc, info);
        generateClassToQnameMapping(getExtensionData(desc, info), info);
        writeOutAuditorConfiguration(getExtensionData(desc, info), info);
        performCodegenProcess(desc, info);
    }
    
    
    protected abstract void performCodegenProcess(ServiceExtensionDescriptionType desc, ServiceInformation info) throws CodegenExtensionException;
    

	protected void modifyEclipseClasspath(ServiceExtensionDescriptionType desc, ServiceInformation info) throws CodegenExtensionException {
		String serviceDir = info.getBaseDirectory().getAbsolutePath();
		// get the eclipse classpath document
		File classpathFile = new File(serviceDir + File.separator + ".classpath");
		if (classpathFile.exists()) {
			logger.info("Modifying eclipse .classpath file");
			Set<File> libs = new HashSet<File>();
			ExtensionTypeExtensionData data = ExtensionTools.getExtensionData(desc, info);
			AdditionalLibraries additionalLibs = null;
			try {
				additionalLibs = ExtensionDataUtils.getExtensionData(data).getAdditionalLibraries();
			} catch (Exception ex) {
				throw new CodegenExtensionException("Error retrieving extension data");
			}
			if (additionalLibs != null && additionalLibs.getJarName() != null) {
				for (int i = 0; i < additionalLibs.getJarName().length; i++) {
					String jarFilename = additionalLibs.getJarName(i);
					libs.add(new File(serviceDir + File.separator + "lib" + File.separator + jarFilename));
				}
			}
			File[] libFiles = new File[libs.size()];
			libs.toArray(libFiles);
			try {
				logger.info("Adding libraries to classpath file:");
				for (int i = 0; i < libFiles.length; i++) {
					logger.info("\t" + libFiles[i].getAbsolutePath());
				}
				ExtensionUtilities.syncEclipseClasspath(classpathFile, libFiles);
			} catch (Exception ex) {
				throw new CodegenExtensionException("Error modifying Eclipse .classpath file: " + ex.getMessage(), ex);
			}
		} else {
			logger.warn("Eclipse .classpath file " + classpathFile.getAbsolutePath() + " not found!");
		}
	}
	
	
	protected void generateClassToQnameMapping(Data extData, ServiceInformation info)
		throws CodegenExtensionException {
		try {
            Mappings mappings = new Mappings();
            List<ClassToQname> classMappings = new LinkedList<ClassToQname>();
            // the first placeto look for mappings is in the caDSR information, which 
            // is derived from the data service Domain Model.  If no domain model is to be used,
            // the mappings are still required to do anything with caCORE SDK beans, or BDT in general
            CadsrInformation cadsrInfo = extData.getCadsrInformation();
            if (cadsrInfo != null && !cadsrInfo.isNoDomainModel()
                && cadsrInfo.getPackages() != null) {
                logger.debug("caDSR information / domain model found in service model.");
                logger.debug("Generating class to qname mapping from the information");
                logger.debug("stored in the service model");
                // load the caDSR package to namespace mapping information
                for (int pack = 0; pack < cadsrInfo.getPackages().length; pack++) {
                    CadsrPackage currentPackage = cadsrInfo.getPackages(pack);
                    for (int clazz = 0; currentPackage.getCadsrClass() != null 
                        && clazz < currentPackage.getCadsrClass().length; clazz++) {
                        ClassMapping map = currentPackage.getCadsrClass(clazz);
                        if (map.getElementName() != null) {
                            String classname = currentPackage.getName() + "." + map.getClassName();
                            QName qname = new QName(currentPackage.getMappedNamespace(), map.getElementName());
                            ClassToQname toQname = new ClassToQname();
                            toQname.setClassName(classname);
                            toQname.setQname(qname.toString());
                            classMappings.add(toQname);
                        }
                    }
                }
                ClassToQname[] mapArray = new ClassToQname[classMappings.size()];
                classMappings.toArray(mapArray);
                mappings.setMapping(mapArray);
            } else {
                logger.warn("No caDSR information / domain model found in service model.");
                logger.warn("Falling back to schema information for class to qname mapping.");
                NamespaceType[] namespaces = info.getNamespaces().getNamespace();
                // a set of namespaces to ignore
                Set<String> nsIgnores = new HashSet<String>();
                nsIgnores.add(IntroduceConstants.W3CNAMESPACE);
                nsIgnores.add(info.getServices().getService(0).getNamespace());
                nsIgnores.add(DataServiceConstants.BDT_DATA_SERVICE_NAMESPACE);
                nsIgnores.add(DataServiceConstants.ENUMERATION_DATA_SERVICE_NAMESPACE);
                nsIgnores.add(DataServiceConstants.DATA_SERVICE_NAMESPACE);
                nsIgnores.add(DataServiceConstants.CQL_QUERY_URI);
                nsIgnores.add(DataServiceConstants.CQL_RESULT_SET_URI);
                
                for (int nsIndex = 0; nsIndex < namespaces.length; nsIndex++) {
                    NamespaceType currentNs = namespaces[nsIndex];
                    // ignore any unneeded namespaces
                    if (!nsIgnores.contains(currentNs.getNamespace())) {
                        SchemaElementType[] schemaElements = currentNs.getSchemaElement();
                        for (int elemIndex = 0; schemaElements != null && elemIndex < schemaElements.length; elemIndex++) {
                            SchemaElementType currentElement = schemaElements[elemIndex];
                            ClassToQname toQname = new ClassToQname();
                            QName qname = new QName(currentNs.getNamespace(), currentElement.getType());
                            String shortClassName = currentElement.getClassName();
                            if (shortClassName == null) { 
                                shortClassName = currentElement.getType();
                            }
                            String fullClassName = currentNs.getPackageName() + "." + shortClassName;
                            toQname.setQname(qname.toString());
                            toQname.setClassName(fullClassName);
                            classMappings.add(toQname);
                        }
                    }
                }
            }
            
            ClassToQname[] mapArray = new ClassToQname[classMappings.size()];
            classMappings.toArray(mapArray);
            mappings.setMapping(mapArray);
            // create the filename where the mapping will be stored
            String mappingFilename = info.getBaseDirectory().getAbsolutePath() + File.separator + "etc" 
                + File.separator + DataServiceConstants.CLASS_TO_QNAME_XML;
            // serialize the mapping to that file
            Utils.serializeDocument(mappingFilename, mappings, DataServiceConstants.MAPPING_QNAME);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new CodegenExtensionException(
                "Error generating class to QName mapping: " + ex.getMessage(), ex);
		}		
	}
    
    
    protected void writeOutAuditorConfiguration(Data extData, ServiceInformation serviceInfo) throws CodegenExtensionException {
        if (CommonTools.servicePropertyExists(
            serviceInfo.getServiceDescriptor(), 
            DataServiceConstants.DATA_SERVICE_AUDITORS_CONFIG_FILE_PROPERTY)) {
            try {
                // get the name of the auditor config file
                String filename = CommonTools.getServicePropertyValue(
                    serviceInfo.getServiceDescriptor(), 
                    DataServiceConstants.DATA_SERVICE_AUDITORS_CONFIG_FILE_PROPERTY);
                if (extData.getDataServiceAuditors() != null) {
                    File outFile = new File(serviceInfo.getBaseDirectory().getAbsolutePath() 
                        + File.separator + "etc" + File.separator + filename);
                    FileWriter writer = new FileWriter(outFile);
                    Utils.serializeObject(extData.getDataServiceAuditors(), 
                        DataServiceConstants.DATA_SERVICE_AUDITORS_QNAME, writer);
                    writer.flush();
                    writer.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new CodegenExtensionException(
                    "Error writing auditor configuration: " + ex.getMessage(), ex);
            }
        }
    }
    
    
    protected Data getExtensionData(ServiceExtensionDescriptionType desc, ServiceInformation info) 
        throws CodegenExtensionException {
        ExtensionTypeExtensionData extData = ExtensionTools.getExtensionData(desc, info);
        Data data = null;
        try {
            data = ExtensionDataUtils.getExtensionData(extData);
        } catch (Exception ex) {
            throw new CodegenExtensionException("Error getting extension data: " + ex.getMessage(), ex);
        }       
        return data;
    }
}
