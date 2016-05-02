package org.cqframework.cql.execution;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.elm.execution.Library;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;

import javax.xml.bind.JAXB;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class CqlExecutionTestBase<T> {
    static Map<String, Library> libraries = new HashMap<String, Library>();
    Library library = null;
    private File xmlFile = null;

    @BeforeMethod
    public void beforeEachTestMethod() {
        String fileName = this.getClass().getSimpleName();
        library = libraries.get(fileName);
        if (library == null) {
            LibraryManager libraryManager = new LibraryManager();
            try {
                File cqlFile = new File(URLDecoder.decode(this.getClass().getResource(fileName + ".cql").getFile(), "UTF-8"));

                ArrayList<CqlTranslator.Options> options = new ArrayList<>();
                options.add(CqlTranslator.Options.EnableDateRangeOptimization);
                CqlTranslator translator = CqlTranslator.fromFile(cqlFile, libraryManager, options.toArray(new CqlTranslator.Options[options.size()]));

                xmlFile = new File(cqlFile.getParent(), fileName + ".xml");
                xmlFile.createNewFile();

                PrintWriter pw = new PrintWriter(xmlFile, "UTF-8");
                pw.println(translator.toXml());
                pw.println();
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            library = JAXB.unmarshal(xmlFile, Library.class);
            libraries.put(fileName, library);
        }
    }

    @AfterClass
    public void oneTimeTearDown() {
//        if (xmlFile != null) {
//            xmlFile.delete();
//        }
    }
}
