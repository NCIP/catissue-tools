package gov.nih.nci.cagrid.data.codegen;

import gov.nih.nci.cagrid.data.codegen.templates.EnumerationServiceClientTemplate;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

/** 
 *  WsEnumerationFeatureCodegen
 *  Codegen features for ws-enumeration data service
 * 
 * @author David Ervin
 * 
 * @created May 2, 2007 11:21:58 AM
 * @version $Id: WsEnumerationFeatureCodegen.java,v 1.1 2007/07/12 17:20:52 dervin Exp $ 
 */
public class WsEnumerationFeatureCodegen extends FeatureCodegen {

   public WsEnumerationFeatureCodegen(ServiceInformation info, ServiceType mainService, Properties serviceProps) {
        super(info, mainService, serviceProps);
    }


    public void codegenFeature() throws CodegenExtensionException {
        createDataSourceClient();
    }
    
    
    private void createDataSourceClient() throws CodegenExtensionException {
        EnumerationServiceClientTemplate template = new EnumerationServiceClientTemplate();
        String clientClassContents = template.generate(getServiceInformation());
        // figgure out the class name
        String classStart = "public class ";
        int nameStart = clientClassContents.indexOf(classStart) + classStart.length();
        int nameEnd = clientClassContents.indexOf(' ', nameStart);
        String clientClassName = clientClassContents.substring(nameStart, nameEnd);
        // and the package name
        String pack = "package ";
        int packNameStart = clientClassContents.indexOf(pack) + pack.length();
        int packNameEnd = clientClassContents.indexOf(';', packNameStart);
        String clientPackage = clientClassContents.substring(packNameStart, packNameEnd);
        // write it out to disk
        File clientClassFile = new File(getServiceInformation().getBaseDirectory().getAbsolutePath() + File.separator
            + "src" + File.separator + clientPackage.replace('.', File.separatorChar) + File.separator
            + clientClassName + ".java");
        try {
            FileWriter classWriter = new FileWriter(clientClassFile);
            classWriter.write(clientClassContents);
            classWriter.flush();
            classWriter.close();
        } catch (IOException ex) {
            throw new CodegenExtensionException("Error creating Data Source enumeration client class: "
                + ex.getMessage(), ex);
        }
    }
}
