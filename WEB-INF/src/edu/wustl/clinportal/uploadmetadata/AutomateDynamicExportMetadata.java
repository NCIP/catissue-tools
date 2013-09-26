/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */


package edu.wustl.clinportal.uploadmetadata;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.common.dynamicextensions.domaininterface.EntityGroupInterface;
import edu.common.dynamicextensions.entitymanager.EntityGroupManager;
import edu.common.dynamicextensions.entitymanager.EntityManager;
import edu.common.dynamicextensions.exception.DynamicExtensionsApplicationException;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.xmi.XMIUtilities;
import edu.common.dynamicextensions.xmi.exporter.XMIExporter;
import edu.wustl.clinportal.action.annotations.AnnotationConstants;
import edu.wustl.clinportal.annotations.xmi.XMIUtility;
import edu.wustl.common.beans.NameValueBean;

/**
 * This class is used for integrating DE models of Catissue within Clinportal
 * Will export dynamic models from Catissuesuite depending upon arguments
 * @author falguni_sachde
 *
 */
public class AutomateDynamicExportMetadata
{

	static private BufferedWriter logWriter = null;

	private static final String IMPORT_PARA_FILE = "ImportParameter.csv";

	private static final String EXPORT_LOG_FILE = "exportMetadata.txt";

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			//Remove parameter file if exist
			removeFile();
			Long start = Long.valueOf(System.currentTimeMillis());
			automateDEmodel(args);
			Long end = Long.valueOf(System.currentTimeMillis());
			System.out.println("-Time required to finish program is " + (end - start) / 1000
					+ "seconds");
			System.out.println("-The integration Export metatada task finishes.");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (logWriter != null)
				{
					logWriter.flush();
					logWriter.close();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * @param args[]
	 * @throws Exception
	 */
	private static void automateDEmodel(String[] args) throws Exception
	{
		//args[0] =operation,args[1]= model name ,args[2]=package name
		//LoggerConfig.configureLogger(System.getProperty("user.dir") + "log4j.properties");
		String modelName = null;
		String packageName = null;

		if (args.length == 3 && args[1] != null && args[2] != null)
		{
			modelName = args[1].toString();
			packageName = args[2].toString();
		}
		if (args[0].equals("exportModel"))
		{
			exportCatissueModel(modelName, packageName);
		}
	}

	/**
	 * This method exports catissue dynamic model and generate xmi for each model. 
	 * @param packageName 
	 * @param modelName 
	 * @throws IOException
	 * @throws DynamicExtensionsSystemException
	 */
	private static void exportCatissueModel(String modelName, String packageName)
			throws IOException, DynamicExtensionsSystemException
	{

		logWriter = new BufferedWriter(new FileWriter(EXPORT_LOG_FILE));
		logWriter.write("\n-AutomateDynamicMetadataUpload.exportCatissueModel() method start");

		//If model name is provided as argument ,only export the specific model from Catissue,else export all models.
		Collection<NameValueBean> entGroupBeans = new ArrayList<NameValueBean>();
		if (modelName != null && modelName.trim().length() > 0 && packageName != null
				&& packageName.trim().length() > 0)
		{
			entGroupBeans.add(new NameValueBean(modelName, packageName));
			System.out.println("Export from Catissue model ::" + modelName + " packageName:"
					+ packageName);
		}
		else
		{
			//this method returns all entity group for which systemGenerated=false
			entGroupBeans = EntityGroupManager.getInstance().getAllEntityGroupBeans();
			System.out.println("Export all DE models from Catissue ::");
		}
		System.out.println("--Export start :Total DE models size at catissue:"
				+ entGroupBeans.size());
		ArrayList<ArrayList<String>> parameterList = new ArrayList<ArrayList<String>>();
		String egroupName = null;
		String logicalpackageName = packageName;
		for (NameValueBean nameValueBean : entGroupBeans)
		{
			try
			{
				egroupName = nameValueBean.getName();
				EntityGroupInterface egroup = EntityManager.getInstance().getEntityGroupByName(
						egroupName);
				//If logicalpackageName is not provided then take it from taggedValue of Entity group object
				//If  logicalpackageName is not available raise an exception and exit the method
				if (logicalpackageName == null)
				{
					logicalpackageName = EntityGroupManager.getInstance().getTaggedValue(egroup,
							AnnotationConstants.TAG_KEY_PACKAGE_NAME);
				}
				System.out.println("--Exporting from catissuesuite group::" + egroupName);
				ArrayList<String> params = new ArrayList<String>();
				if (egroup != null && logicalpackageName != null)
				{
					Long egroupId = egroup.getId();
					params.add(egroupName);
					params.add(logicalpackageName);
					System.out.println("--Export xmi start :" + egroupName + "---"
							+ logicalpackageName);
					List<String> hookedEntityList = exportModel(egroupId, egroupName,
							logicalpackageName);
					List<String> mainContainerList = getMainContainerList(egroupId);
					StringBuffer sbuf = new StringBuffer();
					for (String containername : mainContainerList)
					{
						sbuf.append(containername + ":");
					}
					StringBuffer sEntitybuf = new StringBuffer();
					for (String hookentity : hookedEntityList)
					{
						sEntitybuf.append(hookentity + ":");
					}
					params.add(sbuf.toString());
					params.add(sEntitybuf.toString());
					parameterList.add(params);
					logicalpackageName = null;
				}
				else
				{
					System.out.println("ERROR:------");
					System.out
							.println("Either the logical package name or Entity GroupName is incorrectly specified.Please verify it.");
					logWriter
							.write("\n-Entity group not processed due to absence of logical package name of EntityClass for entity group--"
									+ egroupName);

				}

			}
			catch (DynamicExtensionsSystemException desysExp)
			{
				logWriter.write("\n-Exception while exporting entitygroup" + egroupName);
			}
			catch (DynamicExtensionsApplicationException e)
			{
				logWriter.write("\n-Exception while exporting entitygroup" + egroupName);
			}

		}
		generateParameterFile(parameterList);
		//System.out.println("--ExportModel task  finishes,All models are exported from Catissuesuite.");
		System.out.println("--Please see the exportmetadata.txt file for more details.");
	}

	/**
	 * This method generate csv file which contains all details of Exported catissue models
	 * @param parameterList
	 * @throws IOException
	 */
	private static void generateParameterFile(ArrayList<ArrayList<String>> parameterList)
			throws IOException
	{
		logWriter
				.write("\n-Generating ParameterFile for exported Catissue model infomration .No.of Catissue model exported:"
						+ parameterList.size());
		BufferedWriter writter = null;
		//0-egroupName,1-logicalpackageName,2-maincontainername,3-hookentity
		try
		{
			File file = new File(IMPORT_PARA_FILE);
			writter = new BufferedWriter(new FileWriter(file));
			for (ArrayList<String> modelParamList : parameterList)
			{
				StringBuffer modelparam = new StringBuffer();
				for (String param : modelParamList)
				{
					modelparam.append(param);
					modelparam.append(",");
				}
				writter.write(modelparam.toString());
				writter.write("\n");

			}
		}
		finally
		{
			writter.flush();
			writter.close();
		}

	}

	/**
	 * This method will return the list of container which are associated with given Entity group in Catissue
	 * @param egroupId
	 * @return
	 * @throws DynamicExtensionsSystemException
	 * @throws DynamicExtensionsApplicationException
	 * @throws IOException
	 */
	private static List<String> getMainContainerList(Long egroupId)
			throws DynamicExtensionsSystemException, DynamicExtensionsApplicationException,
			IOException
	{
		logWriter
				.write("\n-AutomateDynamicMetadataUpload.getMainContainerList() method entitygroupId:"
						+ egroupId);
		List<String> containerNames = new ArrayList<String>();
		Collection<NameValueBean> collnColn = EntityGroupManager.getInstance().getMainContainer(
				egroupId);

		for (NameValueBean nameValueBean : collnColn)
		{
			String containerName = nameValueBean.getName();

			containerNames.add(containerName);
		}
		return containerNames;
	}

	/**
	 * @param egroupId
	 * @param egroupName
	 * @param logicalpackageName 
	 * @return
	 * @throws IOException
	 * @throws DynamicExtensionsSystemException
	 */
	private static List<String> exportModel(Long egroupId, String egroupName,
			String logicalpackageName) throws IOException, DynamicExtensionsSystemException

	{
		XMIUtilities.cleanUpRepository();
		List<String> staticEntityList = null;
		logWriter
				.write("\n-Export dynamic model task start for catissue-entitygroup-" + egroupName);
		try
		{
			String filename = AnnotationConstants.CAT_ENTITY_GROUP_PREFIX_IN_CP + egroupName
					+ ".xmi";
			String groupName = egroupName;
			String xmiVersion = "1.2";

			XMIExporter xmiExporter = new XMIExporter();
			EntityGroupInterface entityGroup = XMIUtility.getEntityGroup(groupName);
			staticEntityList = XMIUtility.addCatissueHookEntitiesToGroup(entityGroup);

			if (entityGroup == null)
			{
				logWriter.write("\n-Specified group does not exist. Could not export to XMI"
						+ egroupName);
				throw new DynamicExtensionsSystemException(
						"Specified group does not exist. Could not export to XMI");
			}
			else
			{
				System.out.println("--Export XMI task starts");
				Long start = Long.valueOf(System.currentTimeMillis());
				xmiExporter.exportXMI(filename, entityGroup, xmiVersion, logicalpackageName);
				Long end = Long.valueOf(System.currentTimeMillis());
				System.out.println("--Time required to Export xmi only  is " + (end - start) / 1000
						+ "seconds");

			}
			logWriter.write("\n-Export  dynamic model task end for catissue-entitygroup-"
					+ egroupName + " ,The group is attached with " + staticEntityList.size()
					+ " static entity");

		}

		catch (Exception e)
		{
			e.printStackTrace();
			logWriter.write("\n-Error while exporting entitygroup=" + egroupName);
			throw new DynamicExtensionsSystemException(e.getMessage());
		}

		return staticEntityList;

	}

	/**
	 * @throws IOException
	 */
	private static void removeFile() throws IOException
	{
		String fileName = IMPORT_PARA_FILE;
		File objFile = new File(fileName);
		objFile.delete();

	}
}