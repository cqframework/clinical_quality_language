package org.cqframework.cql.execution;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.tools.shell.Global;
import org.testng.Assert;
import org.testng.annotations.Test;

public class JavascriptEngineTest {

	@Test
	public void testJavascriptEngine()
	{
		Context context = Context.enter();
		Scriptable scope = context.initStandardObjects();
		String javascript = "true";
		Object result = context.evaluateString(scope, javascript, "org.cqframework.cql.execution.JavascriptEngineTest.testJavascriptEngine", 1, null);
		Context.exit();
		
		Assert.assertTrue( Context.toBoolean(result) );
	}
	
	@Test
	public void testJavascriptVersion()
	{
		Context context = Context.enter();
		Scriptable scope = context.initStandardObjects();
		
		StringBuilder javascript = new StringBuilder();
		javascript.append("var Context = org.mozilla.javascript.Context,");
		javascript.append("\n\tcurrentContext = Context.getCurrentContext(),");
		javascript.append("\n\trhinoVersion = currentContext.getImplementationVersion();");
		javascript.append("\nrhinoVersion");
		
		Object result = context.evaluateString(scope, javascript.toString(), "org.cqframework.cql.execution.JavascriptEngineTest.testJavascriptVersion", 1, null);
		Context.exit();
		
		Assert.assertTrue( Context.toString(result).contains("Rhino") );		
	}
	
	@Test
	public void testJavascriptResults()
	{
		Context context = Context.enter();
		Scriptable scope = new ImporterTopLevel(context);
		
		TestPatientSource patients = new TestPatientSource();
		patients.initialize(context, scope);
		Engine.setPatientSource(patients);
		Engine.reset();
		
		StringBuilder javascript = new StringBuilder();
		javascript.append("importPackage(org.cqframework.cql.execution);");
		javascript.append("\nvar source = Engine.getPatientSource();");
		javascript.append("\nvar patient = null;");
		javascript.append("\nwhile( (patient = source.shift()) != null) {");
		javascript.append("\n  Engine.add( JSON.stringify(patient) );");
		javascript.append("\n}");
		javascript.append("\n");
		
		context.evaluateString(scope, javascript.toString(), "org.cqframework.cql.execution.JavascriptEngineTest.testJavascriptResults", 1, null);
		Context.exit();
		
//		Engine.dump();
		
		Assert.assertEquals( Engine.getLastResults().results.size(), TestPatientSource.maxPatients);		
	}
	
	@Test
	public void testJavascriptRequire()
	{
		Context context = Context.enter();
		ScriptableObject scope = context.initStandardObjects();
		
		Global global = new Global(context);
		boolean sandboxed = false;
		List<String> modulePath = new ArrayList<String>();
		String mainModule = "../../coffeescript/cql-execution/lib/";
		modulePath.add(mainModule);
		Require require = global.installRequire(context, modulePath, sandboxed);
		require.install(scope);
		
	    Scriptable arguments = context.newArray(scope, new Object[] {});
	    scope.defineProperty("arguments", arguments, ScriptableObject.DONTENUM);

		TestPatientSource patients = new TestPatientSource();
		patients.initialize(context, scope);
		Engine.setPatientSource(patients);
		Engine.reset();
	    
		Object result = null;
		try {
			File lib = new File(mainModule);
			File script = new File( lib, "age-exec.js" );
			
			String uri = script.toURI().toURL().toExternalForm();
			ScriptableObject.putProperty(scope, "moduleUri", uri);
			
			require.requireMain(context, "age-exec");
		
//			System.out.println( "Results: " + Engine.getLastResults().results.size() );
//			System.out.flush();
//			Engine.dump();
			
		} catch(Exception e) {
			System.err.println(e.getClass().getName() + " -- " + e.getLocalizedMessage());
			File f = new File(".");
			System.err.println(f.getAbsolutePath());
			Assert.fail(e.getLocalizedMessage());
		}
		Context.exit();

		Assert.assertNotNull( Context.toString(result) );	
	}
}
