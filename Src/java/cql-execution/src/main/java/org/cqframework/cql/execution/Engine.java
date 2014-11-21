package org.cqframework.cql.execution;

import java.io.File;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;

/**
 * {@code Engine} will take a CQL script, execute it, and return the results.
 * 
 * @author jwalonoski
 */
public class Engine {
	
	private static Results results = new Results();
	private static PatientSource patientSource;
	
	public static void setPatientSource(PatientSource source) 
	{
		patientSource = source;		
	}
	
	public static PatientSource getPatientSource()
	{
		return patientSource;
	}
	
	public static void add(Object object) {
		results.add(object);
	}
	
	public static void dump() {
		results.dump();
	}
	
	public static void reset() {
		results.clear();
		patientSource.reset();
	}
	
	public static Results getLastResults() {
		return results.copy();
	}

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
		if(patientSource == null) {
			throw new Exception("Engine must have a PatientSource to execute against!");
		}
		
		reset();
		
		Context context = Context.enter();
		Scriptable scope = new ImporterTopLevel(context);

		patientSource.initialize(context, scope);
		
		StringBuilder javascript = new StringBuilder();
		javascript.append("importPackage(org.cqframework.cql.execution);");
		javascript.append("\nvar source = new PatientSource(Engine.getPatientSource());");
		javascript.append("\nvar patient = null;");
		javascript.append("\nwhile( (patient = source.getNextPatient()) != null) {");
		javascript.append("\n  Engine.add( JSON.stringify(patient) );");
		javascript.append("\n}");
		javascript.append("\n");
		
		context.evaluateString(scope, javascript.toString(), "<Engine.execute>", 1, null);
		Context.exit();
		
		return results.copy();
	}
}
