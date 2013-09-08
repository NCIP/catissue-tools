package gov.nih.nci.cagrid.data.upgrades;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.common.CastorMappingUtil;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.data.utilities.WsddUtil;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.beans.method.MethodType;
import gov.nih.nci.cagrid.introduce.beans.method.MethodTypeExceptionsException;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.extension.utils.AxisJdomUtils;
import gov.nih.nci.cagrid.introduce.extension.utils.ExtensionUtilities;
import gov.nih.nci.cagrid.introduce.upgrade.common.StatusBase;
import gov.nih.nci.cagrid.introduce.upgrade.one.one.ExtensionUpgraderBase;
import gov.nih.nci.cagrid.wsenum.utils.IterImplType;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.axis.message.MessageElement;
import org.jdom.Element;
import org.jdom.JDOMException;


/**
 * DataServiceUpgrade1pt0to1pt1 Utility to upgrade a 1.0 data service to 1.2
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A> *
 * @created Feb 19, 2007
 * @version $Id: DataServiceUpgrade1pt0to1pt2.java,v 1.1 2007/02/19 21:52:52
 *          dervin Exp $
 */
public class DataServiceUpgrade1pt0to1pt2 extends ExtensionUpgraderBase {

    public DataServiceUpgrade1pt0to1pt2(ExtensionType extensionType, ServiceInformation serviceInformation,
        String servicePath, String fromVersion, String toVersion) {
        super("DataServiceUpgrade1pt0to1pt2", extensionType, serviceInformation,
            servicePath, fromVersion, toVersion);
    }


    protected void upgrade() throws Exception {
        try {
            // ensure we're upgrading appropriately
            validateUpgrade();
            // get the extension data in raw form
            Element extensionData = getExtensionDataElement();
            // update the data service libraries
            updateLibraries(extensionData);
            // fix the cadsr information block
            setCadsrInformation(extensionData);
            // move the configuration for the CQL query processor into
            // the service properties and remove it from the extension data
            reconfigureCqlQueryProcessor(extensionData);
            // add selected enum iterator
            setEnumIteratorSelection(extensionData);
            // update schemas
            updateDataSchemas();
            // change the version number
            setCurrentExtensionVersion();
            // set the method documentation strings
            setDescriptionStrings(extensionData);
            // add new attributes to extension data's Service Features
            updateServiceFeatures(extensionData);
            // fix up the castor mapping location
            moveCastorMappingFile();
            // store the modified extension data back into the service model
            setExtensionDataElement(extensionData);
            // if nothing has errored out by this point, the upgrade was a success
            getStatus().setStatus(StatusBase.UPGRADE_OK);
        } catch (UpgradeException ex) {
            getStatus().setStatus(StatusBase.UPGRADE_FAIL);
            throw ex;
        }
    }


    private void validateUpgrade() throws UpgradeException {
        if (!((getFromVersion() == null) || getFromVersion().equals("1.0"))) {
            throw new UpgradeException(getClass().getName() 
                + " upgrades FROM 1.0 TO 1.2, found FROM = " + getFromVersion());
        }
        if (!getToVersion().equals("1.2")) {
            throw new UpgradeException(getClass().getName() 
                + " upgrades FROM 1.0 TO 1.2, found TO = " + getToVersion());
        }
        String currentVersion = getExtensionType().getVersion();
        if (!((currentVersion == null) || currentVersion.equals("1.0"))) {
            throw new UpgradeException(getClass().getName() 
                + " upgrades FROM 1.0 TO 1.2, current version found is " + currentVersion);
        }
    }


    private void updateLibraries(Element extDataElement) throws UpgradeException {
        updateDataLibraries();
        if (serviceIsUsingEnumeration(extDataElement)) {
            updateEnumerationLibraries();
        }
        if (serviceIsUsingSdkDataSource(extDataElement)) {
            updateSdkQueryLibraries();
        }
    }


    private void updateDataLibraries() throws UpgradeException {
        FileFilter dataLibFilter = new FileFilter() {
            public boolean accept(File pathname) {
                String name = pathname.getName();
                return (name.endsWith(".jar") && (name.startsWith("caGrid-1.0-data")
                    || name.startsWith("caGrid-1.0-core") || name.startsWith("caGrid-1.0-caDSR") 
                    || name.startsWith("caGrid-1.0-metadata")));
            }
        };
        FileFilter newDataLibFilter = new FileFilter() {
            public boolean accept(File pathname) {
                String name = pathname.getName();
                return (name.endsWith(".jar") && 
                    (name.startsWith("caGrid-data")
                    || name.startsWith("caGrid-core") 
                    || name.startsWith("caGrid-caDSR") 
                    || name.startsWith("caGrid-metadata")));
            }
        };
        // locate the old data service libs in the service
        File serviceLibDir = new File(getServicePath() + File.separator + "lib");
        File[] serviceDataLibs = serviceLibDir.listFiles(dataLibFilter);
        // delete the old libraries
        for (File oldLib : serviceDataLibs) {
            oldLib.delete();
            getStatus().addDescriptionLine("caGrid 1.0 library " + oldLib.getName() + " removed");
        }
        // copy new libraries in
        File extLibDir = new File(ExtensionsLoader.EXTENSIONS_DIRECTORY + File.separator + "lib");
        File[] dataLibs = extLibDir.listFiles(newDataLibFilter);
        List<File> outLibs = new ArrayList<File>(dataLibs.length);
        for (File newLib : dataLibs) {
            File out = new File(serviceLibDir.getAbsolutePath() 
                + File.separator + newLib.getName());
            try {
                Utils.copyFile(newLib, out);
                getStatus().addDescriptionLine("caGrid 1.2 library " + newLib.getName() + " added");
            } catch (IOException ex) {
                throw new UpgradeException("Error copying new data service library: " 
                    + ex.getMessage(), ex);
            }
            outLibs.add(out);
        }
        
        // update the Eclipse .classpath file
        File classpathFile = new File(getServicePath() + File.separator + ".classpath");
        File[] outLibArray = new File[dataLibs.length];
        outLibs.toArray(outLibArray);
        try {
            ExtensionUtilities.syncEclipseClasspath(classpathFile, outLibArray);
            getStatus().addDescriptionLine("Eclipse .classpath file updated");
        } catch (Exception ex) {
            throw new UpgradeException("Error updating Eclipse .classpath file: " 
                + ex.getMessage(), ex);
        }
    }


    private void updateEnumerationLibraries() throws UpgradeException {
        FileFilter enumLibFilter = new FileFilter() {
            public boolean accept(File name) {
                String filename = name.getName();
                return filename.equals("caGrid-1.0-wsEnum.jar");
            }
        };
        FileFilter newEnumLibFilter = new FileFilter() {
            public boolean accept(File name) {
                String filename = name.getName();
                return filename.startsWith("caGrid-wsEnum-1.2") && filename.endsWith(".jar");
            }
        };
        // locate old enumeration libraries in the service
        File serviceLibDir = new File(getServicePath() + File.separator + "lib");
        File[] oldEnumLibs = serviceLibDir.listFiles(enumLibFilter);
        for (File oldLib : oldEnumLibs) {
            oldLib.delete();
            getStatus().addDescriptionLine("caGrid 1.0 library " + oldLib.getName() + " removed");
        }
        // copy in new libraries
        File extLibDir = new File(ExtensionsLoader.EXTENSIONS_DIRECTORY + File.separator + "lib");
        File[] newEnumLibs = extLibDir.listFiles(newEnumLibFilter);
        File[] outLibs = new File[newEnumLibs.length];
        for (int i = 0; i < newEnumLibs.length; i++) {
            File outFile = new File(serviceLibDir.getAbsolutePath() 
                + File.separator + newEnumLibs[i].getName());
            try {
                Utils.copyFile(newEnumLibs[i], outFile);
                getStatus().addDescriptionLine("caGrid 1.2 library " + newEnumLibs[i].getName() + " added");
            } catch (IOException ex) {
                throw new UpgradeException("Error copying new enumeration library: " 
                    + ex.getMessage(), ex);
            }
            outLibs[i] = outFile;
        }
        // update the Eclipse .classpath file
        File classpathFile = new File(getServicePath() + File.separator + ".classpath");
        try {
            ExtensionUtilities.syncEclipseClasspath(classpathFile, outLibs);
            getStatus().addDescriptionLine("Eclipse .classpath file updated");
        } catch (Exception ex) {
            throw new UpgradeException("Error updating Eclipse .classpath file: " 
                + ex.getMessage(), ex);
        }
        getStatus().addDescriptionLine("-- WS-Enumeration support upgraded");
    }


    private void updateSdkQueryLibraries() throws UpgradeException {
        FileFilter sdkLibFilter = new FileFilter() {
            public boolean accept(File name) {
                String filename = name.getName();
                return (filename.startsWith("caGrid-1.0-sdkQuery") 
                    || filename.startsWith("caGrid-1.0-sdkQuery32"))
                    && filename.endsWith(".jar");
            }
        };
        FileFilter newSdkLibFilter = new FileFilter() {
            public boolean accept(File name) {
                String filename = name.getName();
                return (filename.startsWith("caGrid-sdkQuery") 
                    || filename.startsWith("caGrid-sdkQuery32"))
                    && filename.endsWith(".jar");
            }
        };
        // locate old libraries in the service
        File serviceLibDir = new File(getServicePath() + File.separator + "lib");
        boolean isSdk31 = false;
        boolean isSdk32 = false;
        File[] oldLibs = serviceLibDir.listFiles(sdkLibFilter);
        // first must see which version of SDK we're using
        for (int i = 0; i < oldLibs.length; i++) {
            if (oldLibs[i].getName().indexOf("caGrid-1.0-sdkQuery32") != -1) {
                isSdk32 = true;
            } else {
                if (oldLibs[i].getName().indexOf("caGrid-1.0-sdkQuery") != -1) {
                    isSdk31 = true;
                }
            }
        }
        if ((!isSdk31 && !isSdk32) || (isSdk31 && isSdk32)) {
            throw new UpgradeException("Unable to determine SDK version to upgrade");
        }
        // tell user what we think the sdk version was
        getStatus().addDescriptionLine("caCORE SDK version determined to be " 
            + (isSdk31 ? "3.1" : "3.2 / 3.2.1"));
        // delete old libs
        for (File oldLib : oldLibs) {
            oldLib.delete();
            getStatus().addDescriptionLine("caGrid 1.0 library " + oldLib.getName() + " removed");
        }
        // locate new libraries
        File[] newLibs = null;
        if (isSdk31) {
            File sdk31LibDir = new File(ExtensionsLoader.EXTENSIONS_DIRECTORY + File.separator 
                + "data" + File.separator + "sdk31" + File.separator + "lib");
            newLibs = sdk31LibDir.listFiles(newSdkLibFilter);
        } else if (isSdk32) {
            File sdk32LibDir = new File(ExtensionsLoader.EXTENSIONS_DIRECTORY + File.separator 
                + "data" + File.separator + "sdk32" + File.separator + "lib");
            newLibs = sdk32LibDir.listFiles(newSdkLibFilter);
        }
        // copy the libraries in
        File[] outLibs = new File[newLibs.length];
        for (int i = 0; i < newLibs.length; i++) {
            File output = new File(getServicePath() + File.separator + "lib" 
                + File.separator + newLibs[i].getName());
            try {
                Utils.copyFile(newLibs[i], output);
                getStatus().addDescriptionLine("caGrid 1.2 library " + newLibs[i].getName() + " added");
            } catch (IOException ex) {
                throw new UpgradeException("Error copying SDK Query Processor library: " 
                    + ex.getMessage(), ex);
            }
            outLibs[i] = output;
        }
        // sync up the Eclipse .classpath file
        File classpathFile = new File(getServicePath() + File.separator + ".classpath");
        try {
            ExtensionUtilities.syncEclipseClasspath(classpathFile, outLibs);
            getStatus().addDescriptionLine("Eclipse .classpath file updated");
        } catch (Exception ex) {
            throw new UpgradeException("Error updating Eclipse .classpath file: " 
                + ex.getMessage(), ex);
        }
        getStatus().addDescriptionLine("-- caCORE SDK Support upgraded");
    }


    private void moveCastorMappingFile() throws UpgradeException {
        File oldCastorMapping = new File(getServicePath() + File.separator + "xml-mapping.xml");
        if (oldCastorMapping.exists()) {
            Properties introduceProperties = new Properties();
            try {
                introduceProperties.load(new FileInputStream(
                    getServicePath() + File.separator + "introduce.properties"));
            } catch (IOException ex) {
                throw new UpgradeException("Error loading introduce properties for this service: " 
                    + ex.getMessage(), ex);
            }

            File newCastorMapping = new File(
                CastorMappingUtil.getCustomCastorMappingFileName(getServiceInformation()));
            try {
                Utils.copyFile(oldCastorMapping, newCastorMapping);
                getStatus().addDescriptionLine("Castor mapping file moved:");
                getStatus().addDescriptionLine("\tFrom " + oldCastorMapping.getAbsolutePath());
                getStatus().addDescriptionLine("\tTo " + newCastorMapping.getAbsolutePath());
            } catch (IOException ex) {
                throw new UpgradeException("Error moving castor mapping file: " + ex.getMessage(), ex);
            }
            // fix the server-config.wsdd file's castrorMapping parameter
            File serverConfigFile = new File(getServicePath() + File.separator + "server-config.wsdd");
            try {
                WsddUtil.setServiceParameter(serverConfigFile.getAbsolutePath(), 
                    getServiceInformation().getServices().getService(0).getName(), 
                    DataServiceConstants.CASTOR_MAPPING_WSDD_PARAMETER, 
                    CastorMappingUtil.getCustomCastorMappingName(getServiceInformation()));
                getStatus().addDescriptionLine("Edited server-config.wsdd to reference moved castor mapping");
            } catch (Exception ex) {
                throw new UpgradeException("Error setting castor mapping parameter in server-config.wsdd: "
                    + ex.getMessage(), ex);
            }
            // fix the client config file
            String mainServiceName = getServiceInformation().getIntroduceServiceProperties().getProperty(
                IntroduceConstants.INTRODUCE_SKELETON_SERVICE_NAME);
            ServiceType mainService = CommonTools.getService(
                getServiceInformation().getServices(), mainServiceName);
            String servicePackageName = mainService.getPackageName();
            String packageDir = servicePackageName.replace('.', File.separatorChar);
            File clientConfigFile = new File(getServicePath() + File.separator + "src" + File.separator 
                + packageDir + File.separator + "client" + File.separator + "client-config.wsdd");
            try {
                WsddUtil.setGlobalClientParameter(clientConfigFile.getAbsolutePath(),
                    DataServiceConstants.CASTOR_MAPPING_WSDD_PARAMETER, 
                    CastorMappingUtil.getCustomCastorMappingName(getServiceInformation()));
                getStatus().addDescriptionLine("Edited client-config.wsdd to reference moved castor mapping");
            } catch (Exception ex) {
                throw new UpgradeException("Error setting castor mapping parameter in client-config.wsdd: "
                    + ex.getMessage(), ex);
            }
            oldCastorMapping.delete();
        }
    }


    private void updateDataSchemas() throws UpgradeException {
        String serviceName = getServiceInformation().getServices().getService(0).getName();
        // extension data has been updated, but is never used in the service
        // so it can be removed
        getStatus().addDescriptionLine("Data Service Extension data schema has been updated, " +
                "but is not required and should be removed");
        File serviceExtensionDataSchema = new File(getServicePath() + File.separator + "schema" 
            + File.separator + serviceName + File.separator + "DataServiceExtensionData.xsd");
        String targetNamespace = null;
        try {
            targetNamespace = CommonTools.getTargetNamespace(serviceExtensionDataSchema);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new UpgradeException("Error determining namespace of extension data schema: " 
                + ex.getMessage(), ex);
        }
        
        // verify the namespace exists and is not in use by the service
        NamespaceType nsType = CommonTools.getNamespaceType(
            getServiceInformation().getNamespaces(), targetNamespace);
        if (nsType != null 
            && !CommonTools.isNamespaceTypeInUse(
                nsType, getServiceInformation().getServiceDescriptor())) {
            // safe to remove the namespace
            CommonTools.removeNamespace(getServiceInformation().getServiceDescriptor(), targetNamespace);
            serviceExtensionDataSchema.delete();
            getStatus().addDescriptionLine("Data Service Extension data schema was not used, " +
                    "and so was removed from the service");
        }
        
        // if the CQL schema changes, upgrade it here
    }


    private void updateServiceFeatures(Element extDataElement) throws UpgradeException {
        Element serviceFeaturesElement = extDataElement.getChild(
            "ServiceFeatures", extDataElement.getNamespace());
        // BDT feature didn't exist in 1.0, so set it to false for 1.1
        serviceFeaturesElement.setAttribute("useBdt", String.valueOf(false));
        // determine if using SDK
        if (serviceIsUsingSdkDataSource(extDataElement)) {
            System.out.println("The data service is using SDK data source");
            // since this part of the upgrade runs AFTER upgraded the SDK libraries
            // have been updated, look for the caGrid-1.1 libs...
            FileFilter sdkLibFilter = new FileFilter() {
                public boolean accept(File name) {
                    String filename = name.getName();
                    return (filename.startsWith("caGrid-sdkQuery") 
                        || filename.startsWith("caGrid-sdkQuery32"))
                        && filename.endsWith(".jar");
                }
            };
            File serviceLibDir = new File(getServicePath() + File.separator + "lib");
            boolean isSdk31 = false;
            boolean isSdk32 = false;
            File[] oldLibs = serviceLibDir.listFiles(sdkLibFilter);
            // first must see which version of SDK we're using
            for (int i = 0; i < oldLibs.length; i++) {
                if (oldLibs[i].getName().indexOf("caGrid-sdkQuery32") != -1) {
                    isSdk32 = true;
                } else {
                    if (oldLibs[i].getName().indexOf("caGrid-sdkQuery") != -1) {
                        isSdk31 = true;
                    }
                }
            }
            if ((!isSdk31 && !isSdk32) || (isSdk31 && isSdk32)) {
                throw new UpgradeException("Unable to determine SDK version to be switched to a style!");
            }
            
            String styleName = isSdk31 ? "caCORE SDK v 3.1" : "caCORE SDK v 3.2(.1)";
            System.out.println("Set service style to " + styleName);
            serviceFeaturesElement.setAttribute("serviceStyle", styleName);
        } else {
            System.out.println("NO SDK SERVICE USED");
        }
        // use sdk data source attribute no longer exists
        serviceFeaturesElement.removeAttribute("useSdkDataSource");
    }


    private void setCurrentExtensionVersion() throws UpgradeException {
        getExtensionType().setVersion("1.2");
    }


    private void reconfigureCqlQueryProcessor(Element extensionData) throws UpgradeException {
        // make sure to replace the processor jar with the new one in the libs listing
        Element additionalLibraries = extensionData.getChild(
            "AdditionalLibraries", extensionData.getNamespace());
        // additional libs are optional
        if (additionalLibraries != null) {
            List libsEls = additionalLibraries.getChildren();
            Iterator libsElsIt = libsEls.iterator();
            while (libsElsIt.hasNext()) {
                Element nextLib = (Element) libsElsIt.next();
                String jarName = nextLib.getText();
                // TODO: change the names from 1.2-dev to 1.2 for the final 1.2 release
                if (jarName.equals("caGrid-1.0-sdkQuery.jar")) {
                    nextLib.setText("caGrid-sdkQuery-core-1.2-dev.jar");
                } else if (jarName.equals("caGrid-1.0-sdkQuery32.jar")) {
                    nextLib.setText("caGrid-sdkQuery32-core-1.2-dev.jar");
                }
            }
        }
        // service properties now contain CQL Query Processor configuration
        // get the current config properties out of the data element
        Element procConfig = extensionData.getChild("CQLProcessorConfig", extensionData.getNamespace());
        // processor config is optional
        if (procConfig != null) {
            Iterator configuredPropElemIter = procConfig.getChildren(
                "Property", procConfig.getNamespace()).iterator();
            while (configuredPropElemIter.hasNext()) {
                Element propElem = (Element) configuredPropElemIter.next();
                String key = propElem.getAttributeValue("name");
                String value = propElem.getAttributeValue("value");
                // add the property to the service properties
                String extendedKey = "cqlQueryProcessorConfig_" + key;
                CommonTools.setServiceProperty(getServiceInformation().getServiceDescriptor(), 
                    extendedKey, value, false);
            }
        }
        // remove all the processor config properties from the model
        extensionData.removeChild("CQLProcessorConfig", extensionData.getNamespace());

        getStatus().addDescriptionLine(
            "CQL Query Processor configuration properties are now managed in service properties");
    }


    private void setEnumIteratorSelection(Element extDataElement) throws UpgradeException {
        if (serviceIsUsingEnumeration(extDataElement)) {
            CommonTools.setServiceProperty(getServiceInformation().getServiceDescriptor(),
                DataServiceConstants.ENUMERATION_ITERATOR_TYPE_PROPERTY, 
                IterImplType.CAGRID_CONCURRENT_COMPLETE.toString(), false);
            getStatus().addDescriptionLine("Server side WS-Enumeration implementation upgraded to " 
                + IterImplType.CAGRID_CONCURRENT_COMPLETE);
        }
    }
    

    private void setCadsrInformation(Element extensionData) {
        // additional libraries / jar names elements are unchanged
        // get cadsr information
        Element cadsrInfo = extensionData.getChild("CadsrInformation", extensionData.getNamespace());
        // cadsr info is optional
        if (cadsrInfo != null) {
            // now we have a noDomainModel boolean flag...
            boolean hasCadsrUrl = cadsrInfo.getAttributeValue("serviceUrl") != null;
            boolean usingSuppliedModel = (cadsrInfo.getAttributeValue("useSuppliedModel") != null)
                && cadsrInfo.getAttributeValue("useSuppliedModel").equals("true");
            boolean noDomainModel = (!hasCadsrUrl && !usingSuppliedModel);
            cadsrInfo.setAttribute("noDomainModel", String.valueOf(noDomainModel));
            getStatus().addDescriptionLine("Cadsr Information block updated: flag for 'no domain model' set to " + String.valueOf(noDomainModel));
        }
        getStatus().addIssue("caDSR Service URL not updated", 
            "If the caDSR grid service's URL has changed, you will need to change it manually");
    }


    private void setDescriptionStrings(Element extDataElement) throws UpgradeException {
        // get the query method
        MethodType queryMethod = null;
        MethodType enumerationMethod = null;
        MethodType[] allMethods = 
            getServiceInformation().getServices().getService(0).getMethods().getMethod();
        for (int i = 0; i < allMethods.length; i++) {
            if (allMethods[i].getName().equals(DataServiceConstants.QUERY_METHOD_NAME)) {
                queryMethod = allMethods[i];
            } else if (allMethods[i].getName().equals(DataServiceConstants.ENUMERATION_QUERY_METHOD_NAME)) {
                enumerationMethod = allMethods[i];
            }
        }

        // query method is REQUIRED
        if (queryMethod == null) {
            throw new UpgradeException("No standard query method found in the data service!");
        }

        // set descriptions for query method
        queryMethod.setDescription(DataServiceConstants.QUERY_METHOD_DESCRIPTION);
        queryMethod.getInputs().getInput(0).setDescription(DataServiceConstants.QUERY_METHOD_PARAMETER_DESCRIPTION);
        queryMethod.getOutput().setDescription(DataServiceConstants.QUERY_METHOD_OUTPUT_DESCRIPTION);
        MethodTypeExceptionsException[] queryExceptions = queryMethod.getExceptions().getException();
        for (int i = 0; i < queryExceptions.length; i++) {
            if (queryExceptions[i].getQname().equals(DataServiceConstants.QUERY_PROCESSING_EXCEPTION_QNAME)) {
                queryExceptions[i].setDescription(DataServiceConstants.QUERY_PROCESSING_EXCEPTION_DESCRIPTION);
            } else if (queryExceptions[i].getQname().equals(DataServiceConstants.MALFORMED_QUERY_EXCEPTION_QNAME)) {
                queryExceptions[i].setDescription(DataServiceConstants.MALFORMED_QUERY_EXCEPTION_DESCRIPTION);
            }
        }

        // enumeration query method is optional
        if (serviceIsUsingEnumeration(extDataElement)) {
            if (enumerationMethod == null) {
                throw new UpgradeException("No enumeration query method found in the data service!");
            }
            enumerationMethod.setDescription(DataServiceConstants.ENUMERATION_QUERY_METHOD_DESCRIPTION);
            enumerationMethod.getInputs().getInput(0).setDescription(
                DataServiceConstants.QUERY_METHOD_PARAMETER_DESCRIPTION);
            enumerationMethod.getOutput().setDescription(
                DataServiceConstants.ENUMERATION_QUERY_METHOD_OUTPUT_DESCRIPTION);
            MethodTypeExceptionsException[] enumExceptions = enumerationMethod.getExceptions().getException();
            for (int i = 0; i < enumExceptions.length; i++) {
                if (enumExceptions[i].getQname().equals(DataServiceConstants.QUERY_PROCESSING_EXCEPTION_QNAME)) {
                    enumExceptions[i].setDescription(DataServiceConstants.QUERY_PROCESSING_EXCEPTION_DESCRIPTION);
                } else if (enumExceptions[i].getQname().equals(DataServiceConstants.MALFORMED_QUERY_EXCEPTION_QNAME)) {
                    enumExceptions[i].setDescription(DataServiceConstants.MALFORMED_QUERY_EXCEPTION_DESCRIPTION);
                }
            }
        }
        getStatus().addDescriptionLine("Query method and parameters now have description strings");
    }


    private boolean serviceIsUsingEnumeration(Element extDataElement) throws UpgradeException {
        Element serviceFeaturesElement = extDataElement.getChild("ServiceFeatures", extDataElement.getNamespace());
        String useEnumValue = serviceFeaturesElement.getAttributeValue("useWsEnumeration");
        return Boolean.valueOf(useEnumValue).booleanValue();
    }


    private boolean serviceIsUsingSdkDataSource(Element extDataElement) throws UpgradeException {
        Element serviceFeaturesElement = extDataElement.getChild("ServiceFeatures", extDataElement.getNamespace());
        String useSdkValue = serviceFeaturesElement.getAttributeValue("useSdkDataSource");
        return Boolean.valueOf(useSdkValue).booleanValue();
    }


    private void setExtensionDataElement(Element extensionData) throws UpgradeException {
        ExtensionTypeExtensionData ext = getExtensionType().getExtensionData();
        MessageElement rawExtensionData = null;
        try {
            rawExtensionData = AxisJdomUtils.fromElement(extensionData);
        } catch (JDOMException ex) {
            throw new UpgradeException("Error converting extension data to Axis MessageElement: " 
                + ex.getMessage(), ex);
        }
        ext.set_any(new MessageElement[]{rawExtensionData});
    }


    private Element getExtensionDataElement() throws UpgradeException {
        MessageElement[] anys = getExtensionType().getExtensionData().get_any();
        MessageElement rawDataElement = null;
        for (int i = 0; (anys != null) && (i < anys.length); i++) {
            if (anys[i].getQName().equals(Data.getTypeDesc().getXmlType())) {
                rawDataElement = anys[i];
                break;
            }
        }
        if (rawDataElement == null) {
            throw new UpgradeException("No extension data was found for the data service extension");
        }
        Element extensionDataElement = AxisJdomUtils.fromMessageElement(rawDataElement);
        return extensionDataElement;
    }
}
