package org.cqframework.cql.execution;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.elm.execution.Library;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import javax.xml.bind.JAXB;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;

public abstract class CqlExecutionTestBase<T> {
    static Class testClass = null;
    static Library library = null;

    static private File xmlFile = null;

    @BeforeClass
    public static void oneTimeSetUp() {
        if (testClass == null) {
            throw new IllegalArgumentException("You must set the class of the test.");
        }

        ArrayList<CqlTranslator.Options> options = new ArrayList<>();
        options.add(CqlTranslator.Options.EnableDateRangeOptimization);

        try {
            LibraryManager libraryManager = new LibraryManager();

            String fileName = testClass.getSimpleName();
            File cqlFile = new File(URLDecoder.decode(testClass.getResource(fileName + ".cql").getFile(), "UTF-8"));
            CqlTranslator translator = CqlTranslator.fromFile(cqlFile, libraryManager, options.toArray(new CqlTranslator.Options[options.size()]));

            xmlFile = new File(cqlFile.getParent(), fileName + ".xml");
            xmlFile.createNewFile();
            try (PrintWriter pw = new PrintWriter(xmlFile, "UTF-8")) {
                pw.println(translator.toXml());
                pw.println();
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            library = JAXB.unmarshal(xmlFile, Library.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void oneTimeTearDown() {
//        if (xmlFile != null) {
//            xmlFile.delete();
//        }
    }
}
