package gov.nih.nci.cagrid.data.codegen;

import gov.nih.nci.cagrid.data.extension.CadsrInformation;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;


/**
 * @author oster
 * 
 */
public class ResultTypeGeneratorInformation {
	private ServiceInformation serviceInfo;
	private CadsrInformation cadsrInfo;


	public ResultTypeGeneratorInformation() {

	}


	public ResultTypeGeneratorInformation(ServiceInformation serviceInfo, CadsrInformation cadsrInfo) {
		this.serviceInfo = serviceInfo;
		this.cadsrInfo = cadsrInfo;
	}


	public CadsrInformation getCadsrInfo() {
		return this.cadsrInfo;
	}


	public void setCadsrInfo(CadsrInformation cadsrInfo) {
		this.cadsrInfo = cadsrInfo;
	}


	public ServiceInformation getServiceInfo() {
		return this.serviceInfo;
	}


	public void setServiceInfo(ServiceInformation serviceInfo) {
		this.serviceInfo = serviceInfo;
	}

}
