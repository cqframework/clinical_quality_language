package org.cqframework.cql.execution;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.Context;
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

		System.out.println( Context.toString(result) );
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

		System.out.println( Context.toString(result) );
		Context.exit();
		
		Assert.assertTrue( Context.toString(result).contains("Rhino") );		
	}
	
	@Test
	public void testJavascriptRequire()
	{
		final Context context = Context.enter();
		final ScriptableObject scope = context.initStandardObjects();
		
		Global global = new Global(context);
		boolean sandboxed = false;
		List<String> modulePath = new ArrayList<String>();
		String mainModule = "../../coffeescript/cql-execution/lib/";
		modulePath.add(mainModule);
		final Require require = global.installRequire(context, modulePath, sandboxed);
		require.install(scope);
		
//		String[] names = { "print", "load" };
//		scope.defineFunctionProperties(names, scope.getClass(), ScriptableObject.DONTENUM);

	    Scriptable arguments = context.newArray(scope, new Object[] {});
	    scope.defineProperty("arguments", arguments, ScriptableObject.DONTENUM);

		Object result = null;
		try {
			
//	        File require = new File(EngineTest.class.getResource("r.js").getFile());
//			FileReader reader = new FileReader( require );
//			context.evaluateReader(scope, reader, "r.js", 1, null);

			File lib = new File(mainModule);
			File script = new File( lib, "age-exec.js" );
			
			String uri = script.toURI().toURL().toExternalForm();
			ScriptableObject.putProperty(scope, "moduleUri", uri);
			
			FileReader reader = new FileReader( script );
			require.requireMain(context, "age-exec");
			result = context.evaluateReader(scope, reader, "age-exec", 1, null);
			
			System.out.println( Context.toString(result) );
		} catch(Exception e) {
			System.err.println(e.getLocalizedMessage());
			File f = new File(".");
			System.err.println(f.getAbsolutePath());
			Assert.fail(e.getLocalizedMessage());
		} finally {
			Context.exit();
		}
		
		Assert.assertNotNull( Context.toString(result) );		
	}
	
	public void testJavascriptIncludes()
	{
		RhinoSupport support = new RhinoSupport();
		Context context = Context.enter();
		ScriptableObject scope = context.initStandardObjects(support, true);
		
		String[] names = { "print", "load" };
		scope.defineFunctionProperties(names, scope.getClass(), ScriptableObject.DONTENUM);

	    Scriptable arguments = context.newArray(scope, new Object[] {});
	    scope.defineProperty("arguments", arguments, ScriptableObject.DONTENUM);

		Object result = null;
		try {
			
	        File require = new File(EngineTest.class.getResource("r.js").getFile());
			FileReader reader = new FileReader( require );
			context.evaluateReader(scope, reader, "r.js", 1, null);

			File lib = new File("../../coffeescript/cql-execution/lib/");
			reader = new FileReader( new File( lib, "age-exec.js") );
			result = context.evaluateReader(scope, reader, "age-exec.js", 1, null);
			
			System.out.println( Context.toString(result) );
		} catch(Exception e) {
			System.err.println(e.getLocalizedMessage());
			File f = new File(".");
			System.err.println(f.getAbsolutePath());
			Assert.fail(e.getLocalizedMessage());
		} finally {
			Context.exit();
		}
		
		Assert.assertNotNull( Context.toString(result) );		
	}
}
