package org.cqframework.cql.execution;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.tools.shell.Global;

/**
 * {@code Engine} will take a CQL script, execute it, and return the results.
 * 
 * @author jwalonoski
 */
public class Engine {

    private static String[] requiredScripts = {
        "cql-code-service.js",
        "cql-datatypes.js",
        "cql-exec.js",
        "cql-patient.js",
        "template-exec.js"
    };

    private static Results results = new Results();
    private static PatientSource patientSource;
    private static CodeService codeService;
    private static Path workingArea;

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
     * Set the CodeService to be used by all CQL scripts.
     * If the CodeService is {@code null}, script execution
     * may fail (if it relies on codes and valuesets) and throw an Exception.
     */
    public static void setCodeService(CodeService service)
    {
        codeService = service;
    }

    /** Get the current CodeService being used by all CQL scripts. */
    public static CodeService getCodeService()
    {
        return codeService;
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
        String json = "(function() { module.exports = " + rosetta.toJson() + "; }).call(this);";
        return execute( json );
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
     * @param javascript The script to execute.
     * @return The result set of the execution.
     * @throws Exception if the patient source is {@code null}.
     */
    private static Results execute(String javascript) throws Exception
    {
        if(patientSource == null) {
            throw new Exception("Engine must have a PatientSource to execute against!");
        }

        reset();
        prepWorkingArea(javascript);

        Context context = Context.enter();
        ScriptableObject scope = new ImporterTopLevel(context);

        patientSource.initialize(context, scope);

        Global global = new Global(context);
        boolean sandboxed = false;
        List<String> modulePath = new ArrayList<String>();
        String mainModule = workingArea.toString();
        modulePath.add(mainModule);
        Require require = global.installRequire(context, modulePath, sandboxed);
        require.install(scope);

        Scriptable arguments = context.newArray(scope, new Object[] {});
        scope.defineProperty("arguments", arguments, ScriptableObject.DONTENUM);

        try {
            File lib = new File(mainModule);
            File script = new File( lib, "template-exec.js" );

            String uri = script.toURI().toURL().toExternalForm();
            ScriptableObject.putProperty(scope, "moduleUri", uri);

            require.requireMain(context, "template-exec");

//			System.out.println( "Results: " + results.results.size() );
//			System.out.flush();
//			dump();

        } catch(Exception e) {
            System.err.println(e.getClass().getName() + " -- " + e.getLocalizedMessage());
            System.err.println(workingArea.toAbsolutePath().toString());
            e.printStackTrace();
        }

        Context.exit();

        cleanWorkingArea();

        return results.copy();
    }

    private static void prepWorkingArea(String script) throws IOException
    {
        workingArea = Files.createTempDirectory("cqlExecutionEngine");

        for( String filename : requiredScripts )
        {
            File file = new File(Engine.class.getResource(filename).getFile());
            Path source = Paths.get(file.toURI());
            Files.copy(source, workingArea.resolve(source.getFileName()) );
        }

        Path engineScript = Files.createFile(workingArea.resolve("engine-script.js"));
        Files.write(engineScript, script.getBytes("UTF-8"), StandardOpenOption.WRITE);
    }

    private static void cleanWorkingArea()
    {
        String[] files = workingArea.toFile().list();
        for(String file : files)
        {
            try {
                Files.delete( workingArea.resolve(file) );
            } catch (IOException e) {
                // Oh well.
                e.toString();
            }
        }

        try {
            Files.delete(workingArea);
        } catch (IOException e) {
            // So it goes.
            e.toString();
        }
        workingArea = null;
    }
}
