package org.opencds.cqf.cql.engine.execution;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.cqframework.cql.cql2elm.LibraryBuilder;
import org.cqframework.cql.elm.execution.ExpressionDef;
import org.cqframework.cql.elm.execution.FunctionDef;
import org.cqframework.cql.elm.execution.Library;
import org.fhir.ucum.UcumException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.ITest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Factory;
import org.testng.annotations.Test;

public class CqlMainSuiteTest implements ITest {

    private static final Logger logger = LoggerFactory.getLogger(CqlMainSuiteTest.class);

    private ExpressionDef expression;
    private Context context;

    @Factory(dataProvider = "dataMethod")
    public CqlMainSuiteTest(Context context, ExpressionDef expression) {
        this.expression = expression;
        this.context = context;
    }

    @DataProvider
    public static Object[][] dataMethod() throws UcumException, IOException {
        String[] listOfFiles = {
            "portable/CqlTestSuite.cql",
        };

        List<Object[]> testsToRun = new ArrayList<>();
        for (String file: listOfFiles) {
            Library library = translate(file);
            Context context = new Context(library, ZonedDateTime.of(2018, 1, 1, 7, 0, 0, 0, TimeZone.getDefault().toZoneId()));
            if (library.getStatements() != null) {
                for (ExpressionDef expression : library.getStatements().getDef()) {
                    if (expression instanceof FunctionDef) {
                        continue;
                    }
                    if (expression.getName().startsWith("test")) {
                        testsToRun.add(new Object[] {
                            context,
                            expression
                        });
                    }
                }
            }
        }
        return testsToRun.toArray(new Object[testsToRun.size()][]);
    }

    @Override
    public String getTestName() {
        return expression.getName();
    }

    // This test is for the various CQL operators
    @Test
    public void testMainSuite() throws IOException, UcumException {
        Assert.assertEquals(
            ((String)expression.evaluate(context)),
            getTestName().replaceAll("test_", "") + " TEST PASSED"
        );
    }

    private static Library translate(String file) throws UcumException, IOException {
        return new TranslatorHelper().translate(file, LibraryBuilder.SignatureLevel.All);
    }
}
