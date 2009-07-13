package gov.nih.nci.cagrid.data.style.cacore32.test;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.ExtensionDataUtils;
import gov.nih.nci.cagrid.data.common.CastorMappingUtil;
import gov.nih.nci.cagrid.data.creation.CreationStep;
import gov.nih.nci.cagrid.data.creation.DataTestCaseInfo;
import gov.nih.nci.cagrid.data.extension.Data;
import gov.nih.nci.cagrid.data.extension.ServiceFeatures;
import gov.nih.nci.cagrid.introduce.IntroduceConstants;
import gov.nih.nci.cagrid.introduce.beans.ServiceDescription;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionType;
import gov.nih.nci.cagrid.introduce.beans.extension.ExtensionTypeExtensionData;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

import java.io.File;
import java.io.InputStream;

/** 
 *  CreateSDK32StyleServiceStep
 *  Step to test creation of a data service using cacore32 style
 * 
 * @author David Ervin
 * 
 * @created Jul 18, 2007 2:53:54 PM
 * @version $Id: CreateSDK32StyleServiceStep.java,v 1.4 2007/09/05 14:31:33 dervin Exp $ 
 */
public class CreateSDK32StyleServiceStep extends CreationStep {

    public static final String STYLE_NAME = "caCORE SDK v 3.2(.1)";


    public CreateSDK32StyleServiceStep(DataTestCaseInfo serviceInfo, String introduceDir) {
        super(serviceInfo, introduceDir);
    }
    
    
    /**
     * Extended to turn on the cacore31 style in the service model
     */
    protected void postSkeletonCreation() throws Throwable {
        setServiceStyle();
        extractCastorMappingFiles();
    }
    
    
    private void setServiceStyle() throws Throwable {
        Data extensionData = getExtensionData();
        ServiceFeatures features = extensionData.getServiceFeatures();
        if (features == null) {
            features = new ServiceFeatures();
            extensionData.setServiceFeatures(features);
        }
        features.setServiceStyle(STYLE_NAME);
        storeExtensionData(extensionData);
    }
    
    
    private void extractCastorMappingFiles() throws Throwable {
        ServiceInformation info = new ServiceInformation(new File(serviceInfo.getDir()));
        String marshallingMapping = CastorMappingUtil.getMarshallingCastorMappingFileName(info);
        String unmarshallingMapping = CastorMappingUtil.getUnmarshallingCastorMappingFileName(info);
        // copy the resource xml-mapping.xml file to the castor mapping location
        InputStream inStream = getClass().getResourceAsStream("resources/xml-mapping.xml");
        assertNotNull("Original xml-mapping.xml file could not be loaded", inStream);
        StringBuffer contents = Utils.inputStreamToStringBuffer(inStream);
        inStream.close();
        Utils.stringBufferToFile(contents, marshallingMapping);
        // again for unmarshalling
        inStream = getClass().getResourceAsStream("resources/unmarshaller-xml-mapping.xml");
        contents = Utils.inputStreamToStringBuffer(inStream);
        inStream.close();
        Utils.stringBufferToFile(contents, unmarshallingMapping);
    }
    
    
    private ServiceDescription getServiceDescription() throws Throwable {
        // verify the service model exists
        System.out.println("Verifying the service model file exists");
        File serviceModelFile = new File(serviceInfo.getDir() + File.separator + IntroduceConstants.INTRODUCE_XML_FILE);
        assertTrue("Service model file did not exist: " + serviceModelFile.getAbsolutePath(), serviceModelFile.exists());
        assertTrue("Service model file cannot be read: " + serviceModelFile.getAbsolutePath(), serviceModelFile.canRead());
        
        // deserialize the service model
        System.out.println("Deserializing service description from introduce.xml");
        ServiceDescription serviceDesc = (ServiceDescription) Utils.deserializeDocument(
            serviceModelFile.getAbsolutePath(), ServiceDescription.class);
        return serviceDesc;
    }
    
    
    private Data getExtensionData() throws Throwable {
        ServiceDescription serviceDesc = getServiceDescription();
        // get the extension data, set service style to cacore31
        ExtensionType[] extensions = serviceDesc.getExtensions().getExtension();
        ExtensionType dataExtension = null;
        for (int i = 0; i < extensions.length; i++) {
            if (extensions[i].getName().equals("data")) {
                dataExtension = extensions[i];
                break;
            }
        }
        if (dataExtension.getExtensionData() == null) {
            dataExtension.setExtensionData(new ExtensionTypeExtensionData());
        }
        assertNotNull("Data service extension was not found in the service model", dataExtension);
        Data extensionData = ExtensionDataUtils.getExtensionData(dataExtension.getExtensionData());
        return extensionData;
    }
    
    
    private void storeExtensionData(Data data) throws Throwable {
        File serviceModelFile = new File(serviceInfo.getDir() + File.separator + IntroduceConstants.INTRODUCE_XML_FILE);
        ServiceDescription serviceDesc = getServiceDescription();
        
        // get the extension data, set service style to cacore31
        ExtensionType[] extensions = serviceDesc.getExtensions().getExtension();
        ExtensionType dataExtension = null;
        for (int i = 0; i < extensions.length; i++) {
            if (extensions[i].getName().equals("data")) {
                dataExtension = extensions[i];
                break;
            }
        }
        if (dataExtension.getExtensionData() == null) {
            dataExtension.setExtensionData(new ExtensionTypeExtensionData());
        }
        assertNotNull("Data service extension was not found in the service model", dataExtension);
        ExtensionDataUtils.storeExtensionData(dataExtension.getExtensionData(), data);
        Utils.serializeDocument(serviceModelFile.getAbsolutePath(), serviceDesc, IntroduceConstants.INTRODUCE_SKELETON_QNAME);
    }
}
