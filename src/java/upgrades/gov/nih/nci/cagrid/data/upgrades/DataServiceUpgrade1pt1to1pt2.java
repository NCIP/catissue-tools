package gov.nih.nci.cagrid.data.upgrades;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;
import gov.nih.nci.cagrid.introduce.extension.utils.AxisJdomUtils;
import gov.nih.nci.cagrid.introduce.extension.utils.ExtensionUtilities;
import gov.nih.nci.cagrid.introduce.upgrade.common.StatusBase;
import gov.nih.nci.cagrid.introduce.upgrade.one.one.ExtensionUpgraderBase;

import org.apache.axis.message.MessageElement;
import org.jdom.Element;

public class DataServiceUpgrade1pt1to1pt2 extends ExtensionUpgraderBase {

	public DataServiceUpgrade1pt1to1pt2(ExtensionType extensionType,
			ServiceInformation serviceInformation, String servicePath,
			String fromVersion, String toVersion) {
		super(DataServiceUpgrade1pt1to1pt2.class.getSimpleName(),
				extensionType, serviceInformation, servicePath, fromVersion,
				toVersion);
	}

	
	protected void upgrade() throws Exception {
		try {
			validateUpgrade();
			
			updateLibraries();
			
			setCurrentExtensionVersion();
			
			getStatus().setStatus(StatusBase.UPGRADE_OK);
		} catch (UpgradeException ex) {
			getStatus().addDescriptionLine(ex.getMessage());
			getStatus().setStatus(StatusBase.UPGRADE_FAIL);
			throw ex;
		}
	}
	

	private void validateUpgrade() throws UpgradeException {
		if (!"1.1".equals(getFromVersion())) {
			throw new UpgradeException(getClass().getName()
					+ " upgrades FROM 1.1 TO 1.2, found FROM = "
					+ getFromVersion());
		}
		if (!getToVersion().equals("1.2")) {
			throw new UpgradeException(getClass().getName()
					+ " upgrades FROM 1.1 TO 1.2, found TO = " + getToVersion());
		}
		String currentVersion = getExtensionType().getVersion();
		if (!"1.1".equals(currentVersion)) {
			throw new UpgradeException(getClass().getName()
					+ " upgrades FROM 1.1 TO 1.2, current version found is "
					+ currentVersion);
		}
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
	
	
	private void setCurrentExtensionVersion() throws UpgradeException {
        getExtensionType().setVersion("1.2");
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
	
	
	private void updateLibraries() throws UpgradeException {
		Element extDataElement = getExtensionDataElement();
		
        updateDataLibraries();
        
        if (serviceIsUsingEnumeration(extDataElement)) {
        	getStatus().addDescriptionLine("-- Data Service WS-Enumeration Support Detected");
            // updateEnumerationLibraries();
        }
        
        if (serviceIsUsingSdkDataSource(extDataElement)) {
        	getStatus().addDescriptionLine("-- Data Service caCORE SDK Support Detected");
            updateSdkQueryLibraries();
        }
    }


    private void updateDataLibraries() throws UpgradeException {
        FileFilter oldDataLibFilter = new FileFilter() {
            public boolean accept(File pathname) {
                String name = pathname.getName();
                return (name.endsWith(".jar") && (name.startsWith("caGrid-1.1-data")
                    || name.startsWith("caGrid-1.1-core") || name.startsWith("caGrid-1.1-caDSR") 
                    || name.startsWith("caGrid-1.1-metadata")));
            }
        };
        FileFilter newDataLibFilter = new FileFilter() {
            public boolean accept(File pathname) {
                String name = pathname.getName();
                return (name.endsWith("1.2.jar") && 
                    (name.startsWith("caGrid-data-")
                    || name.startsWith("caGrid-core-") 
                    || name.startsWith("caGrid-caDSR-") 
                    || name.startsWith("caGrid-metadata-")));
            }
        };
        // locate the old data service libs in the service
        File serviceLibDir = new File(getServicePath() + File.separator + "lib");
        File[] serviceDataLibs = serviceLibDir.listFiles(oldDataLibFilter);
        // delete the old libraries
        for (File oldLib : serviceDataLibs) {
            oldLib.delete();
            getStatus().addDescriptionLine("caGrid 1.1 library " + oldLib.getName() + " removed");
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
                return filename.equals("caGrid-1.1-wsEnum.jar");
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
            getStatus().addDescriptionLine("caGrid 1.1 library " + oldLib.getName() + " removed");
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
                return (filename.startsWith("caGrid-1.1-sdkQuery") 
                    || filename.startsWith("caGrid-1.1-sdkQuery32"))
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
            if (oldLibs[i].getName().indexOf("caGrid-1.1-sdkQuery32") != -1) {
                isSdk32 = true;
            } else {
                if (oldLibs[i].getName().indexOf("caGrid-1.1-sdkQuery") != -1) {
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
            getStatus().addDescriptionLine("caGrid 1.1 library " + oldLib.getName() + " removed");
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
}
