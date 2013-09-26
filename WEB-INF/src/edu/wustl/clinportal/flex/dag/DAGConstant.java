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

public class DAGConstant {
	public static final String QUERY_OBJECT = "queryObject";
	public static final String CONSTRAINT_VIEW_NODE ="ConstraintViewNode";
	public static final String VIEW_ONLY_NODE="ViewOnlyNode";
	public static final String CONSTRAINT_ONLY_NODE ="ConstraintOnlyNode";
	public static final String HTML_STR="HTMLSTR";
	public static final String EXPRESSION="EXPRESSION";
	public static final String ADD_LIMIT="Add";
	public static final String EDIT_LIMIT="Edit";
	
	//Error Codes 
	public static  final int SUCCESS=0;
	public static  final int EMPTY_DAG=1;
	public static  final int MULTIPLE_ROOT=2;
	public static  final int NO_RESULT_PRESENT=3;
	public static  final int SQL_EXCEPTION=4;
	public static  final int DAO_EXCEPTION=5;
	public static  final int CLASS_NOT_FOUND=6;
	public static  final int NO_PATHS_PRESENT=7;
	public static  final int DYNAMIC_EXTENSION_EXCEPTION=8;
	public static  final int CYCLIC_GRAPH=9;
	
	
	
}
