
package edu.wustl.clinportal.util;

import java.io.File;

import edu.common.dynamicextensions.domaininterface.CategoryInterface;
import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.common.dynamicextensions.util.CategoryHelper;
import edu.common.dynamicextensions.util.CategoryHelperInterface;
import edu.common.dynamicextensions.util.parser.CategoryGenerator;
import edu.wustl.common.util.logger.Logger;
import edu.wustl.common.util.logger.LoggerConfig;

/**
 * 
 * @author mandar_shidhore
 *
 */
public class CategoryCreator
{

	static
	{
		LoggerConfig.configureLogger(System.getProperty("user.dir"));
	}
	//Logger
	private static final Logger LOGGER = Logger.getCommonLogger(CategoryCreator.class);

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		try
		{
			if (args.length == 0)
			{
				throw new Exception("ERROR ---Please Specify the path for .csv file");
			}
			String filePath = args[0];
			createCategory(filePath);

		}
		catch (Exception ex)
		{
			LOGGER.info("Exception: " + ex.getMessage());
			throw new RuntimeException(ex);
		}
	}

	/**
	 * @param csvFilePath
	 */
	public static void createCategory(String csvFilePath)
	{

		LOGGER.info("--Start executing Form definition file " + csvFilePath);
		try
		{
			validateFileExist(csvFilePath);
			CategoryGenerator categryFileParser = new CategoryGenerator(csvFilePath);
			CategoryHelperInterface categoryHelper = new CategoryHelper();

			boolean isEdited = true;
			for (CategoryInterface category : categryFileParser.getCategoryList())
			{

				if (category.getId() == null)
				{
					isEdited = false;
				}
				categoryHelper.saveCategory(category);

				if (isEdited)
				{
					LOGGER.info("");
					LOGGER.info("Edited category " + category.getName() + " successfully!!\n");

				}
				else
				{
					LOGGER.info("");
					LOGGER.info("Saved category " + category.getName() + " successfully!! \n");

				}

			}
			LOGGER.info("Form definition file " + csvFilePath + " executed successfully.");
		}
		catch (Exception ex)
		{
			LOGGER.info("Exception: " + ex.getMessage());
			ex.printStackTrace();
			throw new RuntimeException(ex);

		}
	}

	/**
	 * @param csvFilePath
	 * @throws DynamicExtensionsSystemException
	 */
	private static void validateFileExist(String csvFilePath)
			throws DynamicExtensionsSystemException
	{
		File objFile = new File(csvFilePath);
		if (!objFile.exists())
		{
			throw new DynamicExtensionsSystemException(
					"Please verify that form definition file exist at path: " + csvFilePath);
		}
	}

}
