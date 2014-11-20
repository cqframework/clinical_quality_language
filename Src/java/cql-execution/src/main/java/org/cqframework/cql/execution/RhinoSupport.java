package org.cqframework.cql.execution;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class RhinoSupport extends ScriptableObject 
{
	/** Generated serialization id */
	private static final long serialVersionUID = -4307545436196278762L;

	/**
	 * Allows the Javascript runtime to println to System.out.
	 */
	public static void print(Context cx, Scriptable thisObj, Object[] args, Function funObj) {
		for (Object arg : args) {
			System.out.println( Context.toString( arg ) );
		}
	}

	public static void load(Context cx, Scriptable thisObj, Object[] args, Function funObj) throws Exception 
	{
		RhinoSupport shell = (RhinoSupport) getTopLevelScope(thisObj);
		for (Object arg : args) {
			System.out.println("Loading file " + Context.toString( arg ));
			shell.processSource(cx, Context.toString( arg ));
		}
	}

    private void processSource(Context cx, String filename) throws Exception 
    {
        cx.evaluateReader(this, new InputStreamReader(getInputStream(filename)), filename, 1, null);
    }

    private InputStream getInputStream(String filename) throws Exception 
    {
        return RhinoSupport.class.getResourceAsStream(filename);
    }
	
	@Override
	public String getClassName() {
		return "RhinoSupport";
	}
}
