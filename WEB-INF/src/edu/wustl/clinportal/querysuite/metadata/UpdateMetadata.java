
package edu.wustl.clinportal.querysuite.metadata;

import java.io.IOException;
import java.sql.SQLException;

import edu.common.dynamicextensions.exception.DynamicExtensionsSystemException;
import edu.wustl.dao.exception.DAOException;

/**
 * 
 * @author deepali_ahirrao
 * @version
 */
public class UpdateMetadata
{

	/**
	 * 
	 * @param args
	 * @throws SQLException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws DAOException
	 * @throws DynamicExtensionsSystemException 
	 */
	public static void main(String[] args) throws SQLException, IOException,
			ClassNotFoundException, DAOException, DynamicExtensionsSystemException
	{
		addMetadata();

	}

	/**
	 * 
	 * @param jdbcdao
	 * @throws SQLException
	 * @throws IOException
	 * @throws DAOException
	 * @throws DynamicExtensionsSystemException 
	 */
	private static void addMetadata() throws SQLException, IOException, DAOException,
			DynamicExtensionsSystemException
	{
		AddConsentTierMetadata addConsent = new AddConsentTierMetadata();
		addConsent.addConsentTierMetadata();

		AddConsentResponseMetadata addResponse = new AddConsentResponseMetadata();
		addResponse.addConsentResponseMetadata();

		AddAttribute addAttribute = new AddAttribute();
		addAttribute.addAttribute();

		AddAssociation addAssociation = new AddAssociation();
		addAssociation.addAssociation();
	}

}
