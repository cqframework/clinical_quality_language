package org.cqframework.cql.execution;

import java.io.File;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

/**
 * {@code Engine} will take a CQL script, execute it, and return the results.
 * 
 * @author jwalonoski
 */
public class Engine {

	public static Results executeCql(File file) throws Exception
	{
		CqlTranslator rosetta = CqlTranslator.fromFile(file);
		String json = "var toast = " + rosetta.toJson();
		return execute(json);
	}
	
	public static Results executeCql(String cql) throws Exception
	{
		CqlTranslator rosetta = CqlTranslator.fromText(cql);
		String json = rosetta.toJson();		
		return execute(json);
	}
	
	public static Results executeJson(String json) throws Exception
	{
		return execute(json);
	}
	
	private static Results execute(String json) throws Exception
	{
		Context context = Context.enter();
		Scriptable scope = context.initStandardObjects();
		
		//context.evaluateReader(scope, in, sourceName, lineno, securityDomain)
		
		Object result = context.evaluateString(scope, json, "org.cqframework.cql.execution.Engine.execute", 1, null);

		System.out.println( Context.toString(result) );
		Context.exit();

		return null;
	}
}
