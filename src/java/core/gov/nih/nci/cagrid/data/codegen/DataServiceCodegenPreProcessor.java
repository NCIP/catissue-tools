package gov.nih.nci.cagrid.data.codegen;

import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.ExtensionDataUtils;
import gov.nih.nci.cagrid.data.codegen.templates.StubCQLQueryProcessorTemplate;
import gov.nih.nci.cagrid.data.extension.CadsrInformation;
import gov.nih.nci.cagrid.data.extension.CadsrPackage;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.data.extension.ServiceFeatures;
import gov.nih.nci.cagrid.data.style.ServiceStyleContainer;
import gov.nih.nci.cagrid.data.style.ServiceStyleLoader;
import gov.nih.nci.cagrid.data.style.StyleCodegenPreProcessor;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertiesListType;
import gov.nih.nci.cagrid.introduce.beans.resource.ResourcePropertyType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionException;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionPreProcessor;
import gov.nih.nci.cagrid.introduce.extension.ExtensionTools;
import gov.nih.nci.cagrid.metadata.MetadataUtils;
import gov.nih.nci.cagrid.metadata.dataservice.DomainModel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.Logger;


/**
 * DataServiceCodegenPreProcessor Preprocessor for data service codegen
 * operations.
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * @created May 11, 2006
 * @version $Id: DataServiceCodegenPreProcessor.java,v 1.3 2007/08/24 14:14:50 dervin Exp $
 */
public class DataServiceCodegenPreProcessor implements CodegenExtensionPreProcessor {

	private static final String DEFAULT_DOMAIN_MODEL_XML_FILE = "domainModel.xml";
	private static final Logger LOG = Logger.getLogger(DataServiceCodegenPreProcessor.class);


	public void preCodegen(ServiceExtensionDescriptionType desc, ServiceInformation info)
		throws CodegenExtensionException {
        // check for and potentially create the stub query processor java file
        if (!stubQueryProcessorExists(info)) {
            createStubQueryProcessor(info);
        }
        
		modifyMetadata(desc, info);
        
        // execute the service style's pre codegen processor
        ExtensionTypeExtensionData extData = ExtensionTools.getExtensionData(desc, info);
        Data data = null;
        try {
            data = ExtensionDataUtils.getExtensionData(extData);
        } catch (Exception ex) {
            throw new CodegenExtensionException("Error getting extension data: " + ex.getMessage(), ex);
        }
        ServiceFeatures features = data.getServiceFeatures();
        if (features != null && features.getServiceStyle() != null) {
            try {
                ServiceStyleContainer container = ServiceStyleLoader.getStyle(features.getServiceStyle());
                StyleCodegenPreProcessor preProcessor = container.loadCodegenPreProcessor();
                if (preProcessor != null) {
                    preProcessor.codegenPreProcessStyle(desc, info);
                }
            } catch (Exception ex) {
                throw new CodegenExtensionException(
                    "Error executing style codegen post processor: " + ex.getMessage(), ex);
            }
        }
	}
    
    
    private boolean stubQueryProcessorExists(ServiceInformation info) {
        String stubName = ExtensionDataUtils.getQueryProcessorStubClassName(info);
        File stubJavaFile = new File(info.getBaseDirectory().getAbsolutePath() 
            + File.separator + "src" + File.separator 
            + stubName.replace('.', File.separatorChar) + ".java");
        return stubJavaFile.exists();
    }
    
    
    private void createStubQueryProcessor(ServiceInformation info) throws CodegenExtensionException {
        String stubName = ExtensionDataUtils.getQueryProcessorStubClassName(info);
        File stubJavaFile = new File(info.getBaseDirectory().getAbsolutePath() 
            + File.separator + "src" + File.separator 
            + stubName.replace('.', File.separatorChar) + ".java");
        stubJavaFile.getParentFile().mkdirs();
        StubCQLQueryProcessorTemplate stubTemplate = new StubCQLQueryProcessorTemplate();
        String stubJavaCode = stubTemplate.generate(info);
        try {
            FileWriter writer = new FileWriter(stubJavaFile);
            writer.write(stubJavaCode);
            writer.close();
        } catch (IOException ex) {
            throw new CodegenExtensionException("Error creating stub query processor: " 
                + ex.getMessage(), ex);
        }
    }
	
	
	private CadsrInformation getCadsrInformation(ServiceExtensionDescriptionType desc, ServiceInformation info) throws Exception {
		ExtensionTypeExtensionData extData = ExtensionTools.getExtensionData(desc, info);
		Data data = ExtensionDataUtils.getExtensionData(extData);
		CadsrInformation cadsrInfo = data.getCadsrInformation();
		if (cadsrInfo == null) {
			System.out.println("NO CADSR INFORMATION FOUND, USING DEFAULTS");
			cadsrInfo = new CadsrInformation();
			cadsrInfo.setNoDomainModel(true);
			data.setCadsrInformation(cadsrInfo);
			ExtensionDataUtils.storeExtensionData(extData, data);
		}
		return cadsrInfo;
	}


	private void modifyMetadata(ServiceExtensionDescriptionType desc, ServiceInformation info)
		throws CodegenExtensionException {
		String localDomainModelFilename = getDestinationDomainModelFilename(info);

		// find the service's etc directory, where the domain model goes
		String domainModelFile = info.getBaseDirectory().getAbsolutePath()
			+ File.separator + "etc" + File.separator + localDomainModelFilename;

		// get the caDSR information
		CadsrInformation cadsrInfo = null;
		try {
			cadsrInfo = getCadsrInformation(desc, info);
		} catch (Exception ex) {
			throw new CodegenExtensionException("Error loading Cadsr Information from extension data", ex);
		}
		
		// get the resource property for the domain model
		ResourcePropertyType dmResourceProp = getDomainModelResourceProp(info);
		
		if (cadsrInfo.isUseSuppliedModel()) {
			// the model is already in the service's etc dir with the name specified
			// in the resource property.  Make sure the resource property has
			// the populate from file flag set, and we're done
			dmResourceProp.setPopulateFromFile(true);
		} else if (!isNoDomainModel(cadsrInfo)) {
			// the domain model is to be generated from the caDSR
			LOG.info("No domain model supplied, generating from caDSR");
			generateDomainModel(cadsrInfo, info, domainModelFile);
			// set the domain model file name
			dmResourceProp.setFileLocation(localDomainModelFilename);
		}

		// if the domainModel.xml doesn't exist, don't try to populate the
		// domain model metadata on service startup
		File dmFile = new File(domainModelFile);
		dmResourceProp.setPopulateFromFile(dmFile.exists());
	}
    
    
    private boolean isNoDomainModel(CadsrInformation cadsrInfo) {
        if (cadsrInfo.isNoDomainModel()) {
            return true;
        }
        // states where the no domain model flag has NOT been set, but
        // the caDSR information doesn't contain a project / package / classes
        // are also treated as no domain model
        if (cadsrInfo.getProjectLongName() == null ||
            cadsrInfo.getProjectVersion() == null ||
            cadsrInfo.getPackages() == null ||
            cadsrInfo.getPackages().length == 0) {
            LOG.warn("No domain model was specified, falling back to no model state");
            return true;
        }
        for (CadsrPackage pkg : cadsrInfo.getPackages()) {
            if (pkg.getCadsrClass() != null && pkg.getCadsrClass().length != 0) {
                return false;
            }
        }
        return true;
    }


	/**
	 * Gets the local part of the filename from which the Domain Model resource
	 * property (metadata) will be populated at runtime.  This value is configured
	 * by the user when selecting a pre-built domain model in the data service
	 * creation GUI in Introduce, or the default (domainModel.xml) value
	 * if a domain model is to be generated from the caDSR
	 * 
	 * @param info
	 * 		The service information
	 * @return
	 * 		The local domain model xml file name
	 */
	private String getDestinationDomainModelFilename(ServiceInformation info) {
		ServiceType mainServ = info.getServiceDescriptor().getServices().getService()[0];
		if (mainServ.getResourcePropertiesList() != null
			&& mainServ.getResourcePropertiesList().getResourceProperty() != null) {
			ResourcePropertyType[] resourceProperty = mainServ.getResourcePropertiesList().getResourceProperty();
			for (int i = 0; i < resourceProperty.length; i++) {
				ResourcePropertyType rp = resourceProperty[i];
				if (rp.getQName().equals(DataServiceConstants.DOMAIN_MODEL_QNAME)) {
					String fileLocation = rp.getFileLocation();
					if (fileLocation == null || fileLocation.trim().equals("")) {
						rp.setFileLocation(DEFAULT_DOMAIN_MODEL_XML_FILE);
					}
					return rp.getFileLocation();
				}
			}
		}
		return null;
	}


	private void generateDomainModel(CadsrInformation cadsrInfo, ServiceInformation info, String domainModelFile) throws CodegenExtensionException {		
		if (cadsrInfo != null) {
			DomainModel model = null;
            try {
                model = DomainModelCreationUtil.createDomainModel(cadsrInfo);
            } catch (Exception ex) {
                throw new CodegenExtensionException("Error creating domain model: " + ex.getMessage(), ex);
            }
            
            System.out.println("Created data service Domain Model!");
            LOG.info("Created data service Domain Model!");
            
			// get the data service's description
            ServiceType dataService = null;
            String serviceName = info.getIntroduceServiceProperties().getProperty(
                IntroduceConstants.INTRODUCE_SKELETON_SERVICE_NAME);
            ServiceType[] services = info.getServices().getService();
            for (int i = 0; i < services.length; i++) {
                if (services[i].getName().equals(serviceName)) {
                    dataService = services[i];
                    break;
                }
            }
            if (dataService == null) {
                // this REALLY should never happen...
                throw new CodegenExtensionException("No data service found in service information");
            }

			LOG.debug("Serializing domain model to file " + domainModelFile);
			try {
				FileWriter domainModelFileWriter = new FileWriter(domainModelFile);
				MetadataUtils.serializeDomainModel(model, domainModelFileWriter);
				domainModelFileWriter.flush();
				domainModelFileWriter.close();
				LOG.debug("Serialized domain model");
			} catch (Exception ex) {
				throw new CodegenExtensionException("Error serializing the domain model to disk: " 
					+ ex.getMessage(), ex);
			}

			// add the metadata to the service information as a resource property
			ResourcePropertyType domainModelResourceProperty = new ResourcePropertyType();
			domainModelResourceProperty.setPopulateFromFile(true);
			domainModelResourceProperty.setQName(DataServiceConstants.DOMAIN_MODEL_QNAME);
			domainModelResourceProperty.setRegister(true);
			ResourcePropertiesListType propertyList = dataService.getResourcePropertiesList();
			if (propertyList == null) {
				propertyList = new ResourcePropertiesListType();
			}
			ResourcePropertyType[] propertyArray = propertyList.getResourceProperty();
			if (propertyArray == null) {
				propertyArray = new ResourcePropertyType[]{domainModelResourceProperty};
			} else {
				ResourcePropertyType[] newProperties = new ResourcePropertyType[propertyArray.length];
				System.arraycopy(propertyArray, 0, newProperties, 0, propertyArray.length);
				propertyArray = newProperties;
			}
			// start packing up the resource property info
			propertyList.setResourceProperty(propertyArray);
			dataService.setResourcePropertiesList(propertyList);
		}
	}
	
	
	private ResourcePropertyType getDomainModelResourceProp(ServiceInformation info) {
		ServiceType baseService = info.getServices().getService(0);
		
		ResourcePropertyType[] typedProps = CommonTools.getResourcePropertiesOfType(
			info.getServices().getService(0), DataServiceConstants.DOMAIN_MODEL_QNAME);
		if (typedProps == null || typedProps.length == 0) {
			ResourcePropertyType dmProp = new ResourcePropertyType();
			dmProp.setQName(DataServiceConstants.DOMAIN_MODEL_QNAME);
			dmProp.setRegister(true);
			CommonTools.addResourcePropety(baseService, dmProp);
			return dmProp;
		} else {
			return typedProps[0];
		}
	}
}
