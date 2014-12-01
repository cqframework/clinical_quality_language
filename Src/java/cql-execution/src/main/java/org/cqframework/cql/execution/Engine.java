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
	
	/** 
	 * Set the PatientSource to be used by all CQL scripts.
	 * If the PatientSource is {@code null}, script execution
	 * will fail and throw an Exception.
	 */
	public static void setPatientSource(PatientSource source) 
	{
		patientSource = source;		
	}
	
	/** Get the current PatientSource being used by all CQL scripts. */
	public static PatientSource getPatientSource()
	{
		return patientSource;
	}
	
	/**
	 * Adds an object to the currently executing CQL scripts result set.
	 * Should be called from the CQL (Javascript) only.
	 */
	public static void add(Object object) {
		results.add(object);
	}
	
	/** Dump the contents of the results to standard out. */
	public static void dump() {
		results.dump();
	}
	
	/** Clear the result set and reset the patient source. */
	public static void reset() {
		results.clear();
		if(patientSource!=null) {
			patientSource.reset();			
		}
	}
	
	/** 
	 * Gets a copy of result set from the last CQL script
	 * that was executed.
	 */
	public static Results getLastResults() {
		return results.copy();
	}

	/**
	 * Execute a CQL script.
	 * @param file File containing the full CQL script ("includes" are not allowed).
	 * @return The result set of the execution.
	 * @throws Exception if the patient source is {@code null}.
	 */
	public static Results executeCql(File file) throws Exception
	{
		CqlTranslator rosetta = CqlTranslator.fromFile(file);
		String json = "var toast = " + rosetta.toJson();
		return execute(json);
	}
	
	/**
	 * Execute a CQL script.
	 * @param cql String containing the full CQL script ("includes" are not allowed).
	 * @return The result set of the execution.
	 * @throws Exception if the patient source is {@code null}.
	 */
	public static Results executeCql(String cql) throws Exception
	{
		CqlTranslator rosetta = CqlTranslator.fromText(cql);
		String json = rosetta.toJson();		
		return execute(json);
	}
	
	/**
	 * Execute a JSON expression or JavaScript script.
	 * @param json The full script to execute.
	 * @return The result set of the execution.
	 * @throws Exception if the patient source is {@code null},
	 * even if the script doesn't actually use it.
	 */
	public static Results executeJson(String json) throws Exception
	{
		return execute(json);
	}
	
	/**
	 * Execute JavaScript representing a CQL measure in the Rhino engine.
	 * @param json The script to execute.
	 * @return The result set of the execution.
	 * @throws Exception if the patient source is {@code null}.
	 */
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
