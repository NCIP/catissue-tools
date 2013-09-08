package gov.nih.nci.cagrid.data.sdk32query;

import gov.nih.nci.cabio.domain.Gene;
import gov.nih.nci.cadsr.domain.Address;
import gov.nih.nci.cadsr.domain.ValueDomain;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.common.util.HQLCriteria;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.ApplicationService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.QueryException;

/** 
 *  SubclassCheckCache
 *  Checks that a given class has or does not have a class property in the
 *  caCORE backend data source.  This property is only present for classes
 *  which have subclasses stored in the database as well.
 * 
 * @author <A HREF="MAILTO:ervin@bmi.osu.edu">David W. Ervin</A>
 * 
 * @created Sep 19, 2006 
 * @version $Id: SubclassCheckCache.java,v 1.2 2007/01/12 16:29:37 dervin Exp $ 
 */
public class SubclassCheckCache {
	public static final String CABIO_URL = "http://cabio.nci.nih.gov/cacore32/http/remoteService";
	public static final String KRAMER_URL = "http://kramer:8080/example/http/remoteService";
	
	public static final String CLASS_PROPERTY_NOT_FOUND_ERROR = "could not resolve property: class of: ";
	
	public static final int MAX_EXCEPTION_UNROLL = 7;// levels, causes to follow

	private static Map cache = null;
	
	/**
	 * Executes a test query against the application service to see if the specified
	 * type has a class discriminator parameter, which indicates that it has subclass
	 * instances available in the data source.  If an exception indicating the type
	 * has no class discriminator is thrown, this method returns false.  If the query
	 * succedes, the method returns true.  In this case, this is a somewhat expensive
	 * operation on the first call using that class name.  All subsequent calls with
	 * the same class name are cached and return in order log(n) time, where n is the
	 * number of unique class names checked with this method.
	 * 
	 * @param className
	 * 		The name of the class to test
	 * @param queryService
	 * 		The application service to submit the test query to
	 * @return
	 * 		True if the class discriminator is present, false otherwise
	 * @throws QueryProcessingException
	 */
	public static boolean hasClassProperty(String className, ApplicationService queryService) throws QueryProcessingException {
		if (cache == null) {
			cache = Collections.synchronizedMap(new HashMap());
		}
		Boolean flag = (Boolean) cache.get(className);
		if (flag == null) {
			// sachin modified for to reduce time
			String testHql = "From " + className + " as c where c.class = " + className+" and 1=0";
			// try the query and catch application exceptions
			try {
				queryService.query(new HQLCriteria(testHql), className);
				flag = Boolean.TRUE;
			} catch (ApplicationException ex) {
				// check the exception message for the magical words from Hibernate...
				if (isClassPropertyNotFound(ex)) {
					flag = Boolean.FALSE;
				} else {
					// uh oh... some other exception happened
					throw new QueryProcessingException(ex.getMessage(), ex);
				}
			}
			cache.put(className, flag);
		}
		return flag.booleanValue();
	}
	
	
	private static boolean isClassPropertyNotFound(ApplicationException ex) {
		int currentLevel = 0;
		Throwable exception = ex;
		while (currentLevel < MAX_EXCEPTION_UNROLL && exception != null) {
			if (exception instanceof QueryException) {
				String message = exception.getMessage();
				if (message.startsWith(CLASS_PROPERTY_NOT_FOUND_ERROR)) {
					return true;
				}
			}
			exception = exception.getCause();
			currentLevel++;
		}
		return false;
	}
	
	
	public static void main(String[] args) {
		ApplicationService service = ApplicationService.getRemoteInstance(CABIO_URL);
		try {
			System.out.println(Gene.class.getName() + ": " + hasClassProperty(Gene.class.getName(), service));
			System.out.println(ValueDomain.class.getName() + ": " + hasClassProperty(ValueDomain.class.getName(), service));
			System.out.println(Address.class.getName() + ": " + hasClassProperty(Address.class.getName(), service));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
