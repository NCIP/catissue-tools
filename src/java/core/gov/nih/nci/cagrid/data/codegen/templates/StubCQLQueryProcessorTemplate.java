package gov.nih.nci.cagrid.data.codegen.templates;

public class StubCQLQueryProcessorTemplate
{
  protected final String NL = System.getProperties().getProperty("line.separator");
  protected final String TEXT_1 = "package ";
  protected final String TEXT_2 = ";" + NL + "" + NL + "import gov.nih.nci.cagrid.cqlquery.CQLQuery;" + NL + "import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;" + NL + "import gov.nih.nci.cagrid.data.MalformedQueryException;" + NL + "import gov.nih.nci.cagrid.data.QueryProcessingException;" + NL + "import gov.nih.nci.cagrid.data.cql.CQLQueryProcessor;" + NL + "" + NL + "import java.util.Properties;" + NL + "" + NL + "/** " + NL + " *  StubCQLQueryProcessor" + NL + " *  This CQL Query Processor is provided as a stub to begin implementing CQL against your" + NL + " *  backend data source.  If another CQL query processor implementation is used, " + NL + " *  this file may be safely ignored." + NL + " *  If this class is deleted, the introduce service synchronization process" + NL + " *  will recreate it." + NL + " *  " + NL + " * @deprecated The stub CQL query processor is a placeholder to provide a starting point for" + NL + " * implementation of CQL against a backend data source." + NL + " */" + NL + "public class StubCQLQueryProcessor extends CQLQueryProcessor {" + NL + "" + NL + "\tpublic CQLQueryResults processQuery(CQLQuery cqlQuery) throws MalformedQueryException, QueryProcessingException {" + NL + "\t\t// TODO Auto-generated method stub" + NL + "\t\tthrow new UnsupportedOperationException(\"processQuery() is not yet implemented\");" + NL + "\t}" + NL + "" + NL + "" + NL + "\tpublic Properties getRequiredParameters() {" + NL + "\t\t// TODO Auto-generated method stub" + NL + "\t\treturn new Properties();" + NL + "\t}" + NL + "}";
  protected final String TEXT_3 = NL;

  public String generate(Object argument)
  {
    StringBuffer stringBuffer = new StringBuffer();
     gov.nih.nci.cagrid.introduce.common.ServiceInformation serviceInfo = (gov.nih.nci.cagrid.introduce.common.ServiceInformation) argument;
	String stubClassName = gov.nih.nci.cagrid.data.ExtensionDataUtils.getQueryProcessorStubClassName(serviceInfo);
    String stubPackageName = stubClassName.substring(0, stubClassName.lastIndexOf('.'));


    stringBuffer.append(TEXT_1);
    stringBuffer.append(stubPackageName);
    stringBuffer.append(TEXT_2);
    stringBuffer.append(TEXT_3);
    return stringBuffer.toString();
  }
}
