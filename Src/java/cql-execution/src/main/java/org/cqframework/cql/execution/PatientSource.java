package org.cqframework.cql.execution;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;

/**
 * PatientSource is an interface for providers of patients to implement.
 * 
 * The most up-to-date JSON representation that these patients should adhere
 * to can be found in Src/coffeescript/cql-execution.
 * 
 * @author jwalonoski
 */
public interface PatientSource 
{
	/**
	 * Initialize the PatientSource with the JavaScript context and scope.
	 */
	public void initialize(Context context, Scriptable scope);
	
	/**
	 * @return The next patient as NativeObject JSON.
	 */
	public NativeObject shift();
	
	/**
	 * Reset the PatientSource to start at the first Patient.
	 */
	public void reset();
}
