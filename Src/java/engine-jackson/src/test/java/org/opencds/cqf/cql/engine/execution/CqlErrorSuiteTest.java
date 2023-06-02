package org.opencds.cqf.cql.engine.execution;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.cqframework.cql.cql2elm.LibraryBuilder;
import org.cqframework.cql.elm.execution.ExpressionDef;
import org.cqframework.cql.elm.execution.Library;
import org.fhir.ucum.UcumException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

public class CqlErrorSuiteTest {

    private static final Logger logger = LoggerFactory.getLogger(CqlErrorSuiteTest.class);

    private ExpressionDef expression;
    private Context context;



    @Factory(dataProvider = "dataMethod")
    public CqlErrorSuiteTest(Context context, ExpressionDef expression) {
        this.expression = expression;
        this.context = context;
    }

    @DataProvider
    public static Object[][] dataMethod() throws UcumException, IOException {
        //TODO: Remove this comment when portable/CqlErrorTestSuite.cql has real tests.
        String[] listOfFiles = {
        //    "portable/CqlErrorTestSuite.cql",
        };

        List<Object[]> testsToRun = new ArrayList<>();
        for (String file: listOfFiles) {
            Library library = translate(file);
            Context context = new Context(library, ZonedDateTime.of(2018, 1, 1, 7, 0, 0, 0, TimeZone.getDefault().toZoneId()));
            if (library.getStatements() != null) {
                for (ExpressionDef expression : library.getStatements().getDef()) {
                    testsToRun.add(new Object[] {
                        context,
                        expression
                    });
                }
            }
        }
        return testsToRun.toArray(new Object[testsToRun.size()][]);
    }

    // This test is for the various CQL operators
    @Test
    public void testErrorSuite() throws IOException, UcumException {
        try {
            expression.evaluate(context);
            logger.error("Test " + expression.getName() + " should result in an error");
            Assert.fail();
        }
        catch (Exception e) {
            // pass
            logger.info(expression.getName() + " TEST PASSED");
        }
    }

    private static Library translate(String file) throws UcumException, IOException {
        return new TranslatorHelper().translate(file, LibraryBuilder.SignatureLevel.All);
    }
}
