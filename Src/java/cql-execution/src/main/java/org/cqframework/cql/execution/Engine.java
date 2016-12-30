package org.cqframework.cql.execution;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.ModelManager;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.tools.debugger.Main;
import org.mozilla.javascript.tools.shell.Global;

import static java.nio.file.FileVisitResult.*;
import org.cqframework.cql.cql2elm.LibraryManager;


/**
 * {@code Engine} will take a CQL script, execute it, and return the results.
 * 
 * @author jwalonoski
 */
public class Engine {

    private static final String JS_PATH = "org/cqframework/cql/execution/javascript";
    private static Results results = new Results();
    private static PatientSource patientSource;
    private static CodeService codeService;
    private static Path workingArea;
    private static boolean debugJavascript = false;
    private static ModelManager modelManager = new ModelManager();
    private static LibraryManager libraryManager = new LibraryManager(modelManager);

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
    
    /** Set whether or not to debug the Javascript version of the CQL scripts. */
    public static void setDebugJavascript(boolean debug)
    {
        debugJavascript = debug;
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
        CqlTranslator rosetta = CqlTranslator.fromFile(file, modelManager, libraryManager);
        String json = "(function() { module.exports = " + rosetta.toJson() + "; }).call(this);";
        return execute(json, true);
    }

    /**
     * Execute a CQL script.
     * @param cql String containing the full CQL script ("includes" are not allowed).
     * @return The result set of the execution.
     * @throws Exception if the patient source is {@code null}.
     */
    public static Results executeCql(String cql) throws Exception
    {
        CqlTranslator rosetta = CqlTranslator.fromText(cql, modelManager, libraryManager);
        String json = rosetta.toJson();
        return execute(json,true);
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
        return execute(json,false);
    }

    /**
     * Execute JavaScript representing a CQL measure in the Rhino engine.
     * @param javascript The script to execute.
     * @param isMeasure Whether or not the script should be wrapped in the
     * template-exec function that imports CQL javascripts and execute the
     * javascript as a Clinical Quality Measure. If {@code false} the javascript
     * will be executed as a plain-old javascript.
     * @return The result set of the execution.
     * @throws Exception if the patient source is {@code null}.
     */
    private static Results execute(String javascript, boolean isMeasure) throws Exception
    {
        if(patientSource == null) {
            throw new Exception("Engine must have a PatientSource to execute against!");
        }

        reset();
        prepWorkingArea(javascript);

        Main dbg = null;
        Context context = null;
        if(debugJavascript) {
            ContextFactory factory = new ContextFactory();
            if(isMeasure) {
                dbg = new Main("template-exec.js");
            } else {
                dbg = new Main("engine-script.js");
            }
            dbg.attachTo(factory);
            dbg.setBreakOnEnter(true);
            dbg.setBreakOnExceptions(true);
            dbg.setBreakOnReturn(false);
            context = factory.enterContext();
            
            // redirect std I/O
            System.setIn(dbg.getIn());
            System.setOut(dbg.getOut());
            System.setErr(dbg.getErr());
        } else {
            context = Context.enter();
        }
        ScriptableObject scope = new ImporterTopLevel(context);

        if(debugJavascript) {
            dbg.setScope(scope);
            dbg.setSize(640, 400);
            dbg.setVisible(true);
            //dbg.setExitAction(new ExitOnClose());
        }

        patientSource.initialize(context, scope);

        Global global = new Global(context);
        boolean sandboxed = false;
        List<String> modulePath = new ArrayList<String>();
        String mainModule = workingArea.toUri().toString(); //toString();
        modulePath.add(mainModule);
        Require require = global.installRequire(context, modulePath, sandboxed);
        require.install(scope);

        Scriptable arguments = context.newArray(scope, new Object[] {});
        scope.defineProperty("arguments", arguments, ScriptableObject.DONTENUM);

        try {
            File lib = new File(mainModule);
            File script = null;

            if(isMeasure) {
                script = new File( lib, "template-exec.js" );
            } else {
                script = new File( lib, "engine-script.js" );
            }
            
            String uri = script.toURI().toURL().toExternalForm();
            ScriptableObject.putProperty(scope, "moduleUri", uri);

            if(isMeasure) {
                require.requireMain(context, "template-exec");                
            } else {
                require.requireMain(context, "engine-script");
            }

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

    private static void prepWorkingArea(String script) throws IOException, URISyntaxException {
        workingArea = Files.createTempDirectory("cqlExecutionEngine");

        URL jsURL = Engine.class.getResource("javascript/");
        if (jsURL.getProtocol().equals("file")) {
            copyJavaScriptLibrariesFromFileSystem(Paths.get(jsURL.toURI()), workingArea);
        } else if (jsURL.getProtocol().equals("jar")) {
            copyJavaScriptLibrariesFromJar(jsURL, workingArea);
        }

        Path engineScript = Files.createFile(workingArea.resolve("engine-script.js"));
        Files.write(engineScript, script.getBytes("UTF-8"), StandardOpenOption.WRITE);
    }

    private static void cleanWorkingArea() {
        try {
            Files.walkFileTree(workingArea, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.deleteIfExists(file);
                    return CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.deleteIfExists(dir);
                    return CONTINUE;
                }
            });
        } catch (IOException e) {
            System.err.println("Couldn't delete temp directory: " + workingArea.toString());
        }
        workingArea = null;
    }

    private static void copyJavaScriptLibrariesFromFileSystem(final Path source, final Path target) throws URISyntaxException, IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                try {
                    Files.copy(dir, target.resolve(source.relativize(dir)), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException x) {
                    System.err.println("Problem copying directory: " + dir);
                    x.printStackTrace();
                    return SKIP_SUBTREE;
                }
                return CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.copy(file, target.resolve(source.relativize(file)));
                return CONTINUE;
            }
        });
    }

    private static void copyJavaScriptLibrariesFromJar(final URL source, final Path target) throws URISyntaxException, IOException {
        String fsPath = source.toURI().toString().substring(0, source.toURI().toString().indexOf("!"));
        try(
            FileSystem fs = FileSystems.newFileSystem(URI.create(fsPath), new HashMap<String, String>());
            JarFile jar = new JarFile(fsPath.substring(9))
        ){
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                String name = entries.nextElement().getName();
                if (name.endsWith("/")) {
                    // Path.relativize doesn't work correctly with the trailing slash
                    name = name.substring(0, name.length() - 1);
                }
                Path root = fs.getPath(JS_PATH);
                Path sourcePath = fs.getPath(name);
                if (sourcePath.startsWith(root)) {
                    Files.copy(sourcePath, target.resolve(root.relativize(sourcePath).toString()), StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }
}
