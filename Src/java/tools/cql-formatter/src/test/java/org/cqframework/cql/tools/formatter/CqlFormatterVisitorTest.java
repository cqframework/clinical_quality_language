package org.cqframework.cql.tools.formatter;

import org.cqframework.cql.cql2elm.Cql2ElmVisitor;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * Created by Christopher on 7/20/2017.
 */
public class CqlFormatterVisitorTest {

    private void runTest(String fileName) throws IOException {
        String input = getInputStreamAsString(getInput(fileName));
        String output = Main.getFormattedOutput(getInput(fileName));
        Assert.assertTrue(inputMatchesOutput(input, output));
    }

    @Test
    public void TestFormatterSpecific() throws IOException {
        runTest("comments.cql");
        runTest("invalid-syntax.cql");
    }

    @Test
    public void RunCql2ElmRegressionTestSuite() throws IOException {
        runTest("CMS146v2_Test_CQM.cql");
        runTest("CodeAndConceptTest.cql");
        runTest("DateTimeLiteralTest.cql");
        runTest("EscapeSequenceTests.cql");
        runTest("InTest.cql");
        runTest("ParameterTest.cql");
        runTest("PropertyTest.cql");
        runTest("SignatureResolutionTest.cql");
        runTest("TranslationTests.cql");
        runTest("LibraryTests/BaseLibrary.cql");
        runTest("LibraryTests/DuplicateExpressionLibrary.cql");
        runTest("LibraryTests/FHIRHelpers-1.8.cql");
        runTest("LibraryTests/InvalidLibraryReference.cql");
        runTest("LibraryTests/InvalidReferencingLibrary.cql");
        runTest("LibraryTests/MissingLibrary.cql");
        runTest("LibraryTests/ReferencingLibrary.cql");
        runTest("ModelTests/ModelTest.cql");
        runTest("OperatorTests/AggregateOperators.cql");
        runTest("OperatorTests/ArithmeticOperators.cql");
        runTest("OperatorTests/ComparisonOperators.cql");
        runTest("OperatorTests/CqlComparisonOperators.cql");
        runTest("OperatorTests/CqlIntervalOperators.cql");
        runTest("OperatorTests/CqlListOperators.cql");
        runTest("OperatorTests/DateTimeOperators.cql");
        runTest("OperatorTests/ForwardReferences.cql");
        runTest("OperatorTests/Functions.cql");
        runTest("OperatorTests/ImplicitConversions.cql");
        runTest("OperatorTests/IntervalOperatorPhrases.cql");
        runTest("OperatorTests/IntervalOperators.cql");
        runTest("OperatorTests/InvalidCastExpression.cql");
        runTest("OperatorTests/InvalidSortClauses.cql");
        runTest("OperatorTests/ListOperators.cql");
        runTest("OperatorTests/LogicalOperators.cql");
        runTest("OperatorTests/MessageOperators.cql");
        runTest("OperatorTests/MultiSourceQuery.cql");
        runTest("OperatorTests/NameHiding.cql");
        runTest("OperatorTests/NullologicalOperators.cql");
        runTest("OperatorTests/RecursiveFunctions.cql");
        runTest("OperatorTests/Sorting.cql");
        runTest("OperatorTests/StringOperators.cql");
        runTest("OperatorTests/TimeOperators.cql");
        runTest("OperatorTests/TupleAndClassConversions.cql");
        runTest("OperatorTests/TypeOperators.cql");
        runTest("OperatorTests/UndeclaredForward.cql");
        runTest("OperatorTests/UndeclaredSignature.cql");
        runTest("PathTests/PathTests.cql");
    }

    private boolean inputMatchesOutput(String input, String output) {
        return input.replaceAll("\\s", "").equals(output.replaceAll("\\s", ""));
    }

    private InputStream getInput(String fileName) {
        InputStream is = Cql2ElmVisitor.class.getResourceAsStream(fileName);

        if (is == null) {
            is = CqlFormatterVisitorTest.class.getResourceAsStream(fileName);

            if (is == null) {
                throw new IllegalArgumentException(String.format("Invalid test resource: %s not in %s or %s", fileName, Cql2ElmVisitor.class.getSimpleName(), CqlFormatterVisitor.class.getSimpleName()));
            }
        }

        return is;
    }

    private String getInputStreamAsString(InputStream is) {
        return new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining("\n"));
    }
}
