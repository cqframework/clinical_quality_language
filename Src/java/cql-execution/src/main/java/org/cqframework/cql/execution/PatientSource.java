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
     * This is necessary so that the PatientSource can correctly create,
     * scope, and return NativeObjects with the shift method.
     */
    public void initialize(Context context, Scriptable scope);

    /**
     * Returns the next patient as NativeObject JSON.
     *
     * The method name, "shift", was specifically selected to mirror
     * the JavaScript Array.shift method, so that a PatientSource in
     * the JavaScript can successfully interface with a Java-based
     * implementation or a JavaScript Array-based implementation
     * (useful for testing).
     *
     * @return The next patient as NativeObject JSON.
     */
    public NativeObject shift();

    /**
     * Reset the PatientSource to start at the first Patient.
     */
    public void reset();
}
