package gov.nih.nci.cagrid.data.common;

import gov.nih.nci.cadsr.umlproject.domain.Project;
import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.ExtensionDataUtils;
import gov.nih.nci.cagrid.data.auditing.DataServiceAuditors;
import gov.nih.nci.cagrid.data.extension.AdditionalLibraries;
import gov.nih.nci.cagrid.data.extension.CadsrInformation;
import gov.nih.nci.cagrid.data.extension.CadsrPackage;
import gov.nih.nci.cagrid.data.extension.ClassMapping;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** 
 *  ExtensionDataManager
 *  Manages storage / retrieval of information in the data service extension data
 * 
 * @author David Ervin
 * 
 * @created Apr 11, 2007 10:04:04 AM
 * @version $Id: ExtensionDataManager.java,v 1.3 2007/12/18 19:10:26 dervin Exp $ 
 */
public class ExtensionDataManager {
    
    private ExtensionTypeExtensionData extensionData;
    
    public ExtensionDataManager(ExtensionTypeExtensionData data) {
        this.extensionData = data;
    }
    
    
    /**
     * Gets the jar names of the additional libraries added to the service
     * for supporting the query processor class
     * 
     * @return
     *      The names of the additional jars, or <code>null</code> if none are present
     * @throws Exception
     */
    public String[] getAdditionalJarNames() throws Exception {
        AdditionalLibraries libs = getAdditionalLibraries();
        return libs.getJarName();
    }
    
    
    /**
     * Adds an additional jar
     * 
     * @param jarName
     *      The name of the jar to add
     * @throws Exception
     */
    public void addAdditionalJar(String jarName) throws Exception {
        AdditionalLibraries libs = getAdditionalLibraries();
        String[] jarNames = null;
        if (libs.getJarName() == null) {
            jarNames = new String[] {jarName};
        } else {
            boolean shouldAdd = true;
            for (String name : libs.getJarName()) {
                if (name.equals(jarName)) {
                    shouldAdd = false;
                    break;
                }
            }
            if (shouldAdd) {
                jarNames = (String[]) Utils.appendToArray(libs.getJarName(), jarName);
            }
        }
        libs.setJarName(jarNames);
        storeAdditionalLibraries(libs);
    }
    
    
    /**
     * Sets the additional jars
     * 
     * @param jarNames
     *      All the jar names to be set as additional jars
     * @throws Exception
     */
    public void setAdditionalJars(String[] jarNames) throws Exception {
        AdditionalLibraries libs = getAdditionalLibraries();
        libs.setJarName(jarNames);
        storeAdditionalLibraries(libs);
    }
    
    
    /**
     * Stores a domain model's source
     * 
     * @param noDomainModel
     * @param supplied
     * @throws Exception
     */
    public void storeDomainModelSource(
        boolean noDomainModel, boolean supplied) throws Exception {
        CadsrInformation info = getCadsrInformation();
        info.setNoDomainModel(noDomainModel);
        info.setUseSuppliedModel(supplied);
        storeCadsrInformation(info);
    }
    
    
    /**
     * Stores the cadsr grid service URL
     * 
     * @param url
     * @throws Exception
     */
    public void storeCadsrServiceUrl(String url) throws Exception {
        CadsrInformation info = getCadsrInformation();
        info.setServiceUrl(url);
        storeCadsrInformation(info);
    }
    
    
    /**
     * Stores the project long name and version
     * 
     * @param project
     * @throws Exception
     */
    public void storeCadsrProjectInformation(Project project) throws Exception {
        CadsrInformation info = getCadsrInformation();
        info.setProjectLongName(project.getLongName());
        info.setProjectVersion(project.getVersion());
        storeCadsrInformation(info);
    }
    
    
    /**
     * Replaces the cadsr package information in the extension data
     * 
     * @param packages
     * @throws Exception
     */
    public void storeCadsrPackages(CadsrPackage[] packages) throws Exception {
        CadsrInformation info = getCadsrInformation();
        info.setPackages(packages);
        storeCadsrInformation(info);
    }
    
    
    /**
     * Stores a Cadsr Package.  If an existing package has the same name,
     * the existing package will be replaced
     * 
     * @param pack
     * @throws Exception
     */
    public void storeCadsrPackage(CadsrPackage pack) throws Exception {
        CadsrInformation info = getCadsrInformation();
        CadsrPackage[] currentPackages = info.getPackages();
        if (currentPackages == null) {
            currentPackages = new CadsrPackage[] {pack};
        } else {
            boolean replaced = false;
            for (int i = 0; i < currentPackages.length; i++) {
                if (currentPackages[i].getName().equals(pack.getName())) {
                    currentPackages[i] = pack;
                    replaced = true;
                    break;
                }
            }
            if (!replaced) {
                currentPackages = (CadsrPackage[]) Utils.appendToArray(currentPackages, pack);
            }
        }
        info.setPackages(currentPackages);
        storeCadsrInformation(info);
    }
    
    
    /**
     * Gets the mapped namespace for a package in the cadsr information
     * 
     * @param packageName
     * @return
     *      The mapped namespace or NULL if no package was found
     * @throws Exception
     */
    public String getMappedNamespaceForPackage(String packageName) throws Exception {
        CadsrInformation info = getCadsrInformation();
        if (info.getPackages() != null) {
            for (CadsrPackage pack : info.getPackages()) {
                if (pack.getName().equals(packageName)) {
                    return pack.getMappedNamespace();
                }
            }
        }
        return null;
    }
    
    
    /**
     * Removes a cadsr package from the service extension data
     * 
     * @param packageName
     * @throws Exception
     */
    public void removeCadsrPackage(String packageName) throws Exception {
        CadsrInformation info = getCadsrInformation();
        CadsrPackage[] packs = info.getPackages();
        for (CadsrPackage currentPackage : packs) {
            if (currentPackage.getName().equals(packageName)) {
                packs = (CadsrPackage[]) Utils.removeFromArray(packs, currentPackage);
                break;
            }
        }
        info.setPackages(packs);
        storeCadsrInformation(info);
    }
    
    
    /**
     * Gets a class mapping
     * 
     * @param packageName
     * 
     *      The name of the package in which the class resides
     * @param className
     *      The name of the class
     * @return
     *      The class mapping, or null if none is found
     * @throws Exception
     */
    public ClassMapping getClassMapping(String packageName, String className) throws Exception {
        CadsrInformation info = getCadsrInformation();
        if (info.getPackages() != null) {
            for (CadsrPackage pack : info.getPackages()) {
                if (pack.getName().equals(packageName)) {
                    for (ClassMapping mapping : pack.getCadsrClass()) {
                        if (mapping.getClassName().equals(className)) {
                            return mapping;
                        }
                    }
                }
            }
        }
        return null;
    }
    
    
    /**
     * Sets that a class is selected as part of the exposed model
     * 
     * @param packageName
     *      The package name
     * @param className
     *      The class name
     * @param selected
     *      The selection state
     * @return
     *      True if the class was found in the model, false otherwise
     * @throws Exception
     */
    public boolean setClassSelectedInModel(String packageName, String className, boolean selected) throws Exception {
        CadsrInformation info = getCadsrInformation();
        if (info.getPackages() != null) {
            for (CadsrPackage pack : info.getPackages()) {
                if (pack.getName().equals(packageName)) {
                    for (ClassMapping mapping : pack.getCadsrClass()) {
                        if (mapping.getClassName().equals(className)) {
                            mapping.setSelected(selected);
                            storeCadsrInformation(info);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    
    /**
     * Sets that a class is targetable in the exposed domain model
     * 
     * @param packageName
     *      The package name
     * @param className
     *      The class name
     * @param targetable
     *      The targetability state
     * @return
     *      True if the class was found in the model, false otherwise
     * @throws Exception
     */
    public boolean setClassTargetableInModel(String packageName, String className, boolean targetable) throws Exception {
        CadsrInformation info = getCadsrInformation();
        if (info.getPackages() != null) {
            for (CadsrPackage pack : info.getPackages()) {
                if (pack.getName().equals(packageName)) {
                    for (ClassMapping mapping : pack.getCadsrClass()) {
                        if (mapping.getClassName().endsWith(className)) {
                            mapping.setTargetable(targetable);
                            storeCadsrInformation(info);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    
    
    /**
     * Sets the element name a class is mapped to in the exposed domain model
     * 
     * @param packageName
     * @param className
     * @param elementName
     * @return
     *      True if the class was found in the model, false otherwise
     * @throws Exception
     */
    public boolean setClassElementNameInModel(String packageName, String className, String elementName) throws Exception {
        CadsrInformation info = getCadsrInformation();
        if (info.getPackages() != null) {
            for (CadsrPackage pack : info.getPackages()) {
                if (pack.getName().equals(packageName)) {
                    for (ClassMapping mapping : pack.getCadsrClass()) {
                        if (mapping.getClassName().endsWith(className)) {
                            mapping.setElementName(elementName);
                            storeCadsrInformation(info);
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    
    
    /**
     * Gets all class mappings in a package
     * 
     * @param packName
     * @return
     *      A list of class mappings, or null if none are found
     * @throws Exception
     */
    public List<ClassMapping> getClassMappingsInPackage(String packName) throws Exception {
        CadsrInformation info = getCadsrInformation();
        if (info.getPackages() != null) {
            for (CadsrPackage pack : info.getPackages()) {
                if (pack.getName().equals(packName) && pack.getCadsrClass() != null) {
                    List<ClassMapping> mappings = Arrays.asList(pack.getCadsrClass());
                    return mappings;
                }
            }
        }
        return null;
    }
    
    
    /**
     * Gets the configured caDSR url
     * 
     * @return
     *      The caDSR url, or null if none is found
     * @throws Exception
     */
    public String getCadsrUrl() throws Exception {
        CadsrInformation info = getCadsrInformation();
        return info.getServiceUrl();
    }
    
    
    /**
     * Gets the names of all cadsr packages stored in the extension data
     * 
     * @return
     *      A list of package names, or null if none are stored
     * @throws Exception
     */
    public List<String> getCadsrPackageNames() throws Exception {
        CadsrInformation info = getCadsrInformation();
        if (info.getPackages() != null) {
            List<String> names = new ArrayList<String>(info.getPackages().length);
            for (CadsrPackage pack : info.getPackages()) {
                names.add(pack.getName());
            }
            return names;
        }
        return null;
    }
    
    
    /**
     * Gets the stored long name of the caDSR project used in the domain model
     * @return
     *      The project long name, or null if not found
     * @throws Exception
     */
    public String getCadsrProjectLongName() throws Exception {
        CadsrInformation info = getCadsrInformation();
        return info.getProjectLongName();
    }
    
    
    /**
     * Gets the stored version of the caDSR project used in the domain model
     * @return
     *      The project's version, or null if not found
     * @throws Exception
     */
    public String getCadsrProjectVersion() throws Exception {
        CadsrInformation info = getCadsrInformation();
        return info.getProjectVersion();
    }
    
    
    /**
     * Gets the flag indicating if no domain model is to be used
     * 
     * @return
     *      The no domain model flag
     * @throws Exception
     */
    public boolean isNoDomainModel() throws Exception {
        CadsrInformation info = getCadsrInformation();
        return info.isNoDomainModel();
    }
    
    
    /**
     * Gets the flag indicating a supplied domain model is to be used
     * 
     * @return
     *      The supplied domain model flag
     * @throws Exception
     */
    public boolean isSuppliedDomainModel() throws Exception {
        CadsrInformation info = getCadsrInformation();
        return info.isUseSuppliedModel();
    }
    
    
    /**
     * Gets the flag indicating the service is to use BDT
     * 
     * @return
     *      The use BDT flag
     * @throws Exception
     */
    public boolean isUseBdt() throws Exception {
        Data data = getExtensionData();
        return data.getServiceFeatures().isUseBdt();
    }
    
    
    /**
     * Gets the flag indicating the service is to use WS-Enumeration
     * 
     * @return
     *      The use WS-Enumeration flag
     * @throws Exception
     */
    public boolean isUseWsEnumeration() throws Exception {
        Data data = getExtensionData();
        return data.getServiceFeatures().isUseWsEnumeration();
    }
    
    
    /**
     * Gets the service style
     * 
     * @return
     *      The service style, or <code>null</code> if none is supplied
     * @throws Exception
     */
    public String getServiceStyle() throws Exception {
        Data data = getExtensionData();
        return data.getServiceFeatures().getServiceStyle();
    }
    
    
    /**
     * Gets the data service auditors configuration
     * 
     * @return
     *      The service auditors configuration
     * @throws Exception
     */
    public DataServiceAuditors getAuditorsConfiguration() throws Exception {
        Data data = getExtensionData();
        if (data.getDataServiceAuditors() == null) {
            data.setDataServiceAuditors(new DataServiceAuditors());
            saveExtensionData(data);
        }
        return data.getDataServiceAuditors();
    }
    
    
    /**
     * Stores the data service auditors configuration
     * 
     * @param auditors
     * @throws Exception
     */
    public void storeAuditorsConfiguration(DataServiceAuditors auditors) throws Exception {
        Data data = getExtensionData();
        data.setDataServiceAuditors(auditors);
        saveExtensionData(data);
    }
    
    
    private Data getExtensionData() throws Exception {
        return ExtensionDataUtils.getExtensionData(extensionData);
    }
    
    
    private void saveExtensionData(Data data) throws Exception {
        ExtensionDataUtils.storeExtensionData(extensionData, data);
    }
    
    
    private AdditionalLibraries getAdditionalLibraries() throws Exception {
        Data data = getExtensionData();
        AdditionalLibraries libs = data.getAdditionalLibraries();
        if (libs == null) {
            libs = new AdditionalLibraries();
        }
        return libs;
    }
    
    
    private void storeAdditionalLibraries(AdditionalLibraries libs) throws Exception {
        Data data = getExtensionData();
        data.setAdditionalLibraries(libs);
        saveExtensionData(data);
    }
    
    
    /**
     * Gets the full caDSR information stored with this service extension data
     * @return
     *      The caDSR information of the extension data
     * @throws Exception
     */
    public CadsrInformation getCadsrInformation() throws Exception {
        Data data = getExtensionData();
        CadsrInformation info = data.getCadsrInformation();
        if (info == null) {
            return new CadsrInformation();
        }
        return info;
    }
    
    
    private void storeCadsrInformation(CadsrInformation info) throws Exception {
        Data data = getExtensionData();
        data.setCadsrInformation(info);
        saveExtensionData(data);
    }
}
