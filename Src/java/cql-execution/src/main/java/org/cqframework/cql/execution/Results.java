package org.cqframework.cql.execution;

import java.util.ArrayList;
import java.util.List;

/**
 * Results of CQL execution. Individual results (zero or more)
 * are inserted into an ordered list, with some helper methods,
 * from the Engine and JavaScript execution environment.
 * 
 * The JavaScript can technically add results of any class that
 * inherit from Object, but in practice, these should be Strings
 * containing JSON.
 * 
 * @author jwalonoski
 */
public class Results {

	/** The results themselves. */
	public List<Object> results = new ArrayList<Object>();
	
	/** Add non-null objects into the results set. */
	public void add(Object object) {
		if(object != null) {
			results.add(object);
		}
	}
	
	/** Dump the contents of the results to standard out. */
	public void dump() {
		for(Object object : results) {
			System.out.println( object );
		}
	}
	
	/** Clear the result set. */
	public void clear() {
		results.clear();
	}
	
	/** Return a deep-copy of the results set. */
	public Results copy()
	{
		Results copy = new Results();
		for(Object object : results) {
			copy.add( object );
		}
		return copy;
	}
}
