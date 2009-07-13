package gov.nih.nci.cagrid.data.style;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.style.DataServiceStyle;
import gov.nih.nci.cagrid.introduce.extension.ExtensionsLoader;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

/** 
 *  ServiceStyleLoader
 *  Utility to load service styles available to the data service extension
 * 
 * @author David Ervin
 * 
 * @created Jul 9, 2007 12:34:36 PM
 * @version $Id: ServiceStyleLoader.java,v 1.2 2007/12/19 19:34:19 dervin Exp $ 
 */
public class ServiceStyleLoader {

    public static List<ServiceStyleContainer> getAvailableStyles() throws Exception {
        // list to store style descriptions
        List<ServiceStyleContainer> styles = new ArrayList<ServiceStyleContainer>();
        
        // locate the styles directory
        File extensionsDir = ExtensionsLoader.getInstance().getExtensionsDir();
        File stylesDir = new File(extensionsDir.getAbsolutePath() + File.separator 
            + "data" + File.separator + DataServiceConstants.SERVICE_STYLES_DIR_NAME);
        
        if (stylesDir.exists() && stylesDir.isDirectory()) {
            // load the styles
            File[] styleDirs = stylesDir.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            });
            for (File styleDir : styleDirs) {
                File styleXmlFile = new File(styleDir.getAbsolutePath() 
                    + File.separator + DataServiceConstants.SERVICE_STYLE_FILE_NAME);
                DataServiceStyle style = (DataServiceStyle) Utils.deserializeDocument(
                    styleXmlFile.getAbsolutePath(), DataServiceStyle.class);
                ServiceStyleContainer container = new ServiceStyleContainer(style, styleDir);
                styles.add(container);
            }
        }
        return styles;
    }
    
    
    public static ServiceStyleContainer getStyle(String name) throws Exception {
        List<ServiceStyleContainer> styles = getAvailableStyles();
        for (ServiceStyleContainer container : styles) {
            if (container.getServiceStyle().getName().equals(name)) {
                return container;
            }
        }
        return null;
    }
}
