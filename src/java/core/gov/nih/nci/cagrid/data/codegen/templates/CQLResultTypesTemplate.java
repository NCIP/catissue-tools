package gov.nih.nci.cagrid.data.codegen.templates;

import gov.nih.nci.cagrid.data.extension.CadsrInformation;
import gov.nih.nci.cagrid.data.extension.CadsrPackage;
import gov.nih.nci.cagrid.data.extension.ClassMapping;
import gov.nih.nci.cagrid.introduce.beans.namespace.NamespaceType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;

public class CQLResultTypesTemplate
{
  protected final String NL = System.getProperties().getProperty("line.separator");
  protected final String TEXT_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NL + "<xs:schema targetNamespace=\"";
  protected final String TEXT_2 = "/CQLResultTypes\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" elementFormDefault=\"qualified\">" + NL + "    " + NL + "    <!-- Exposed Objects' schemas are imported here -->" + NL;
  protected final String TEXT_3 = NL + NL + NL + "    <xs:group name=\"PermissibleCQLObjectResults\">" + NL + "        <xs:choice>        " + NL + "        <!-- element references to exposed objects -->" + NL;
  protected final String TEXT_4 = " " + NL + "        </xs:choice>" + NL + "    </xs:group>" + NL + "</xs:schema>";

  public String generate(Object argument)
  {
    StringBuffer stringBuffer = new StringBuffer();
     
	gov.nih.nci.cagrid.data.codegen.ResultTypeGeneratorInformation info = (gov.nih.nci.cagrid.data.codegen.ResultTypeGeneratorInformation) argument;
  	ServiceInformation serviceInfo = info.getServiceInfo();
	ServiceType service = serviceInfo.getServices().getService(0);
	CadsrInformation cadsrInfo = info.getCadsrInfo();
	

    stringBuffer.append(TEXT_1);
    stringBuffer.append(service.getNamespace());
    stringBuffer.append(TEXT_2);
    
		if (cadsrInfo != null) {
			CadsrPackage[] packages = cadsrInfo.getPackages();
			if (packages != null) {
				for (int i = 0; i < packages.length; i++) {
					CadsrPackage pack = packages[i];
					String psNS = pack.getMappedNamespace();
					NamespaceType ns = CommonTools.getNamespaceType(serviceInfo.getNamespaces(), psNS);
					String location ="";
					if (ns != null){
						location = ns.getLocation();
					}
					stringBuffer.append("\t<xs:import namespace=\"" + psNS 
						+ "\" schemaLocation=\"" + location + "\"/>\n");
				}
			}
		}
	
    stringBuffer.append(TEXT_3);
    
		if (cadsrInfo != null) {
			CadsrPackage[] packages = cadsrInfo.getPackages();
			if (packages != null) {
				for (int i = 0; i < packages.length; i++) {
					CadsrPackage pack = packages[i];
					String psNS = pack.getMappedNamespace();
					ClassMapping[] packClasses = pack.getCadsrClass();
					if (packClasses != null) {
						for (int j = 0; j < packClasses.length; j++) {
							if (packClasses[j].isTargetable() && packClasses[j].getElementName() != null) {
								stringBuffer.append("\t\t<xs:element ref=\"ns0:");
								stringBuffer.append(packClasses[j].getElementName());
								stringBuffer.append("\" xmlns:ns0=\"");
								stringBuffer.append(psNS);
								stringBuffer.append("\"/>\n");
							}
						}
					}
				}
			}
		}
    stringBuffer.append(TEXT_4);
    return stringBuffer.toString();
  }
}
