
package edu.wustl.clinportal.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

/*
 * Created on Feb 20, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */

/**
 * @author rukhsana
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class GenerateSchema
{

	/**
	 * @param args
	 * @throws HibernateException
	 * @throws IOException
	 * @throws Exception
	 */
	public static void main(String[] args) throws HibernateException, IOException, Exception
	{
		boolean isToPrntOnCnsle = false;
		boolean isToExecuteOnDB = false;
		if (args.length != 0)
		{
			String arg = args[0];
			if (arg.equalsIgnoreCase("true"))
			{
				isToPrntOnCnsle = true;
				isToExecuteOnDB = true;
			}
			if (arg.equalsIgnoreCase("false"))
			{
				isToPrntOnCnsle = false;
				isToExecuteOnDB = false;
			}
		}

		File file = new File("db.properties");
		BufferedInputStream stram = new BufferedInputStream(new FileInputStream(file));
		Properties prop = new Properties();
		prop.load(stram);
		stram.close();

		Configuration cfg = new Configuration();
		cfg.setProperties(prop);
		cfg.addDirectory(new File("./WEB-INF/src"));
		cfg.addJar(new File("./WEB-INF/lib/DynamicExtensions.jar"));
		new SchemaExport(cfg).setOutputFile("catissuecore.sql").create(isToPrntOnCnsle,
				isToExecuteOnDB);

	}
}