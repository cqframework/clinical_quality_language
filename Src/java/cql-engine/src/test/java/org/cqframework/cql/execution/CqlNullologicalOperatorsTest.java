package org.cqframework.cql.execution;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.elm.execution.Library;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class CqlNullologicalOperatorsTest {
    private static final String fileName = CqlNullologicalOperatorsTest.class.getSimpleName();
    private static File xmlFile = null;

    @BeforeClass
    public static void oneTimeSetUp() {
        ArrayList<CqlTranslator.Options> options = new ArrayList<>();
        options.add(CqlTranslator.Options.EnableDateRangeOptimization);

        try {
            File cqlFile = new File(URLDecoder.decode(CqlNullologicalOperatorsTest.class.getResource(fileName + ".cql").getFile(),"UTF-8"));
            LibraryManager libraryManager = new LibraryManager();
            CqlTranslator translator = CqlTranslator.fromFile(cqlFile, libraryManager, options.toArray(new CqlTranslator.Options[options.size()]));

            xmlFile = File.createTempFile(fileName, ".xml");
            try (PrintWriter pw = new PrintWriter(xmlFile, "UTF-8")) {
                pw.println(translator.toXml());
                pw.println();
                pw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @AfterClass
    public static void oneTimeTearDown() {
        if (xmlFile != null) {
            xmlFile.delete();
        }
    }

    @Test
    public void testIsNull() throws JAXBException {
        // load an ELM document into the Execution tree
        Library library = JAXB.unmarshal(xmlFile, Library.class);

        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "IsNullTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "IsNullFalseEmptyString").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "IsNullAlsoFalseAbcString").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "IsNullAlsoFalseNumber1").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "IsNullAlsoFalseNumberZero").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    @Test
    public void testIsFalse() throws JAXBException {
        // load an ELM document into the Execution tree
        Library library = JAXB.unmarshal(xmlFile, Library.class);

        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "IsFalseFalse").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "IsFalseTrue").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "IsFalseNull").getExpression().evaluate(context);
        assertThat(result, is(false));
    }

    @Test
    public void testIsTrue() throws JAXBException {
        // load an ELM document into the Execution tree
        Library library = JAXB.unmarshal(xmlFile, Library.class);

        Context context = new Context(library);
        Object result = context.resolveExpressionRef(library, "IsTrueTrue").getExpression().evaluate(context);
        assertThat(result, is(true));

        result = context.resolveExpressionRef(library, "IsTrueFalse").getExpression().evaluate(context);
        assertThat(result, is(false));

        result = context.resolveExpressionRef(library, "IsTrueNull").getExpression().evaluate(context);
        assertThat(result, is(false));
    }
}
