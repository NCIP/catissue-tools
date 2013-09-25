/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */

/*
 * Created on Sep 27, 2007
 * @author
 *
 */
package edu.wustl.clinportal.annotations.xmi;

import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.wustl.common.util.logger.LoggerConfig;

/**
 * @author preeti_lodha
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class XMIExporter
{
	static 
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}
	public static final String XMI_VERSION_1_1 = "1.1";
	public static final String XMI_VERSION_1_2 = "1.2";
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		

		try
		{
		if(args.length<3)
		{
			throw new Exception("Please specify all parameters. '-Dgroupname <groupname> -Dfilename <export filename> -Dversion <version>'");
		}
		String groupName = args[0];
		if(groupName==null)
		{
			throw new Exception("Please specify groupname to be exported");
		}
		else
		{
			String filename = args[1]; 
			if(filename==null)
			{
				throw new Exception("Kindly specify the filename where XMI should be exported.");
			}
			else
			{
				String xmiVersion = args[2]; 
				if(xmiVersion==null)
				{
					System.out.println("Export version not specified. Exporting as XMI 1.2");
					xmiVersion = XMI_VERSION_1_2;
				}
				edu.common.dynamicextensions.xmi.exporter.XMIExporter xmiExporter = new edu.common.dynamicextensions.xmi.exporter.XMIExporter();
				EntityGroupInterface entityGroup = XMIUtility.getEntityGroup(groupName);
				if(entityGroup!=null && XMI_VERSION_1_1.equalsIgnoreCase(xmiVersion))
				{
					XMIUtility.addHookEntitiesToGroup(entityGroup);
				}
				if(entityGroup==null)
				{
					throw new Exception("Specified group does not exist. Could not export to XMI");
				}
				else
				{
					xmiExporter.exportXMI(filename, entityGroup, xmiVersion);
				}
			}

		}
		}catch(Exception e)
		{
			e.printStackTrace();
		}

	}

}
