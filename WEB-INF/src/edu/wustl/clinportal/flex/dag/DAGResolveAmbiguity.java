/*L
 *  Copyright Washington University in St. Louis
 *  Copyright SemanticBits
 *  Copyright Persistent Systems
 *  Copyright Krishagni
 *
 *  Distributed under the OSI-approved BSD 3-Clause License.
 *  See http://ncip.github.com/catissue-tools/LICENSE.txt for details.
 */


package edu.wustl.clinportal.flex.dag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import edu.common.dynamicextensions.domaininterface.EntityInterface;
import edu.wustl.cab2b.client.ui.dag.ambiguityresolver.AmbiguityObject;
import edu.wustl.cab2b.client.ui.query.IPathFinder;
import edu.wustl.cab2b.common.util.Constants;
import edu.wustl.common.querysuite.metadata.path.ICuratedPath;
import edu.wustl.common.querysuite.metadata.path.IPath;
import edu.wustl.common.util.logger.Logger;

/**
 * @author falguni_sachde
 *
 */
public class DAGResolveAmbiguity
{

	private Vector<AmbiguityObject> m_ambiguityObjts;

	private Map<AmbiguityObject, List<IPath>> m_ambObToPathsMap = new HashMap<AmbiguityObject, List<IPath>>();

	private IPathFinder m_pathFinder;

	/**
	 * @param ambiguityObjects
	 * @param pathFinder
	 */
	public DAGResolveAmbiguity(Vector<AmbiguityObject> ambiguityObjects, IPathFinder pathFinder)
	{
		m_ambiguityObjts = ambiguityObjects;
		m_pathFinder = pathFinder;
	}

	/**
	 * @param ambiguityObject
	 * @param pathFinder
	 */
	public DAGResolveAmbiguity(AmbiguityObject ambiguityObject, IPathFinder pathFinder)
	{
		m_ambiguityObjts = new Vector<AmbiguityObject>();
		m_ambiguityObjts.add(ambiguityObject);
		m_pathFinder = pathFinder;
	}

	/**
	 * @return
	 */
	public Map<AmbiguityObject, List<IPath>> getPathsForAllAmbiguities()
	{
		for (int i = 0; i < m_ambiguityObjts.size(); i++)
		{
			AmbiguityObject ambiguityObject = m_ambiguityObjts.get(i);
			Map<String, List<IPath>> allPathMap = getPaths(ambiguityObject.getSourceEntity(),
					ambiguityObject.getTargetEntity());

			List<IPath> curratedPathList = allPathMap.get(Constants.CURATED_PATH);
			List<IPath> generalPathList = allPathMap.get(Constants.GENERAL_PATH);

			if (curratedPathList.size() == 1)
			{
				m_ambObToPathsMap.put(ambiguityObject, curratedPathList);
			}
			else
			{
				m_ambObToPathsMap.put(ambiguityObject, generalPathList);
			}

		}
		return m_ambObToPathsMap;
	}

	/**
	 * Method to get all possible paths for given source and destination entity
	 * 
	 * @param sourceEntity
	 * @param destinationEntity
	 * @return
	 */
	public Map<String, List<IPath>> getPaths(EntityInterface sourceEntity,
			EntityInterface destinationEntity)
	{
		Set<ICuratedPath> allCuratedPaths = m_pathFinder.getCuratedPaths(sourceEntity,
				destinationEntity);
		Logger.out.debug("  getCuratedPaths() executed : " + allCuratedPaths.size());

		List<IPath> selectedPaths = new ArrayList<IPath>();
		List<IPath> curatedPaths = new ArrayList<IPath>();
		for (ICuratedPath iCuratedPaths : allCuratedPaths)
		{
			Set<IPath> iPathSet = iCuratedPaths.getPaths();
			if (iPathSet != null && !iPathSet.isEmpty())
			{
				for (IPath iPath : iPathSet)
				{
					if (iCuratedPaths.isSelected())
					{
						selectedPaths.add(iPath);
					}
					else
					{
						curatedPaths.add(iPath);
					}
				}
			}
		}

		List<IPath> generalPaths = m_pathFinder
				.getAllPossiblePaths(sourceEntity, destinationEntity);

		Map<String, List<IPath>> allPathMap = new HashMap<String, List<IPath>>(3);
		allPathMap.put(Constants.SELECTED_PATH, selectedPaths);
		allPathMap.put(Constants.CURATED_PATH, curatedPaths);
		allPathMap.put(Constants.GENERAL_PATH, generalPaths);

		return allPathMap;
	}
}
