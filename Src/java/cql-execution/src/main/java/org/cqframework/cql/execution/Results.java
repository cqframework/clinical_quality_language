package org.cqframework.cql.execution;

import java.util.ArrayList;
import java.util.List;

public class Results {

	public List<Object> results = new ArrayList<Object>();
	
	public void add(Object object) {
		if(object != null) {
			results.add(object);
		}
	}
	
	public void dump() {
		for(Object object : results) {
			System.out.println( object );
		}
	}
	
	public void clear() {
		results.clear();
	}
	
	public Results copy()
	{
		Results copy = new Results();
		for(Object object : results) {
			copy.add( object );
		}
		return copy;
	}
}
