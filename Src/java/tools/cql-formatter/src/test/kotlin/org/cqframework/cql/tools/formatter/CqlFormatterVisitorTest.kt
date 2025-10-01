package org.cqframework.cql.tools.formatter

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

/**
 * Created by Christopher on 7/20/2017.
 */
internal class CqlFormatterVisitorTest {
    var inError: Boolean = false

    @Throws(IOException::class)
    private fun runTest(fileName: String) {
        val input = CqlFormatterVisitor.getInputStreamAsString(getInput(fileName))
        val result = CqlFormatterVisitor.getFormattedOutput(getInput(fileName))
        inError = result.errors.isNotEmpty()
        Assertions.assertTrue(inputMatchesOutput(input, result.output))
    }

    @Test
    @Throws(IOException::class)
    fun formatterSpecific() {
        runTest("comments.cql")
        // I commented these catches out because it seems to me that the formatter should not clobber input when these
        // errors occur...
        // I don't understand why this first one ever ran, it should have reported an error, and should not have
        // clobbered input
        // And the second one correctly reported an error, but why was it allowed to clobber the input?
        // At any rate, they both work correctly now (I had to add null to the characters to ignore for comparison
        // though)
        // try {
        // this test has an extra "`", which is not ignored - causing a syntax error.
        runTest("git-issue-206-a.cql")
        // } catch (AssertionError ae) {
        //    Assertions.assertFalse(inError);
        // }
        // try {
        // this test has an extra """, which is not ignored - causing a syntax error.
        runTest("git-issue-206-b.cql")
        // } catch (AssertionError ae) {
        //    Assertions.assertTrue(inError);
        // }
        runTest("git-issue-210-a.cql")
        Assertions.assertFalse(inError)
        runTest("git-issue-210-b.cql")
        Assertions.assertFalse(inError)
        runTest("git-issue-210-c.cql")
        Assertions.assertFalse(inError)
        runTest("git-issue-246.cql")
        Assertions.assertFalse(inError)
        runTest("comment-after.cql")
        Assertions.assertFalse(inError)
        runTest("comment-before.cql")
        Assertions.assertFalse(inError)
        runTest("comment-first.cql")
        Assertions.assertFalse(inError)
        runTest("comment-in-clause.cql")
        Assertions.assertFalse(inError)
        runTest("comment-last.cql")
        Assertions.assertFalse(inError)
        runTest("git-issue-349.cql")
        Assertions.assertFalse(inError)
        runTest("git-issue-613.cql")
        Assertions.assertFalse(inError)
        runTest("git-issue-437.cql")
        Assertions.assertFalse(inError)
        runTest("git-issue-377.cql")
        Assertions.assertFalse(inError)
        try {
            runTest("invalid-syntax.cql")
        } catch (ae: AssertionError) {
            Assertions.assertTrue(inError)
        }
    }

    @Test
    @Throws(IOException::class)
    fun runCql2ElmRegressionTestSuite() {
        runTest("CMS146v2_Test_CQM.cql")
        runTest("CodeAndConceptTest.cql")
        runTest("DateTimeLiteralTest.cql")
        runTest("EscapeSequenceTests.cql")
        runTest("InTest.cql")
        runTest("InvalidEquality.cql")
        runTest("InValueSetTest.cql")
        runTest("LocalFunctionResolutionTest.cql")
        runTest("ParameterTest.cql")
        runTest("ParameterTestInvalid.cql")
        // runTest("ParserPerformance.cql");
        runTest("PropertyTest.cql")
        runTest("QuantityLiteralTest.cql")
        runTest("RatioLiteralTest.cql")
        runTest("SignatureTests/SignatureOutputTests.cql")
        runTest("SignatureResolutionTest.cql")
        runTest("TestChoiceAssignment.cql")
        runTest("TestIncludedIn.cql")
        runTest("TestPatientContext.cql")
        runTest("TestPointIntervalSignatures.cql")
        runTest("TestRelatedContextRetrieve.cql")
        runTest("TestUnion.cql")
        runTest("TranslationTests.cql")
        runTest("TricksyParse.cql")
        runTest("fhir/r4/EqualityWithConversions.cql")
        runTest("fhir/r4/TestChoiceTypes.cql")
        runTest("fhir/r4/TestDoubleListPromotion.cql")
        runTest("fhir/r4/TestFHIRTiming.cql")
        runTest("fhir/r4/TestImplicitFHIRHelpers.cql")
        runTest("fhir/r4/TestIntervalImplicitConversion.cql")
        runTest("fhir/r4/TestURIConversion.cql")
        runTest("fhir/stu3/EqualityWithConversions.cql")
        runTest("fhir/stu3/TestChoiceTypes.cql")
        runTest("fhir/stu3/TestDoubleListPromotion.cql")
        runTest("fhir/stu3/TestFHIRTiming.cql")
        runTest("fhir/stu3/TestImplicitFHIRHelpers.cql")
        runTest("fhir/stu3/TestIntervalImplicitConversion.cql")
        runTest("fhir/stu3/TestURIConversion.cql")
        runTest("fhir/v18/PathTests.cql")
        runTest("LibraryTests/BaseLibrary.cql")
        runTest("LibraryTests/DuplicateExpressionLibrary.cql")
        runTest("LibraryTests/InvalidBaseLibrary.cql")
        runTest("LibraryTests/InvalidLibraryReference.cql")
        runTest("LibraryTests/InvalidReferencingLibrary.cql")
        runTest("LibraryTests/MissingLibrary.cql")
        runTest("LibraryTests/ReferencingInvalidBaseLibrary.cql")
        runTest("LibraryTests/ReferencingLibrary.cql")
        runTest("ModelTests/ModelTest.cql")
        runTest("OperatorTests/AgeOperators.cql")
        runTest("OperatorTests/AggregateOperators.cql")
        runTest("OperatorTests/ArithmeticOperators.cql")
        runTest("OperatorTests/ComparisonOperators.cql")
        runTest("OperatorTests/CqlComparisonOperators.cql")
        runTest("OperatorTests/CqlIntervalOperators.cql")
        runTest("OperatorTests/CqlListOperators.cql")
        runTest("OperatorTests/DateTimeOperators.cql")
        runTest("OperatorTests/ForwardReferences.cql")
        runTest("OperatorTests/Functions.cql")
        runTest("OperatorTests/ImplicitConversions.cql")
        runTest("OperatorTests/IntervalOperatorPhrases.cql")
        runTest("OperatorTests/IntervalOperators.cql")
        runTest("OperatorTests/InvalidCastExpression.cql")
        runTest("OperatorTests/InvalidSortClauses.cql")
        runTest("OperatorTests/ListOperators.cql")
        runTest("OperatorTests/LogicalOperators.cql")
        runTest("OperatorTests/MessageOperators.cql")
        runTest("OperatorTests/MultiSourceQuery.cql")
        runTest("OperatorTests/NameHiding.cql")
        runTest("OperatorTests/NullologicalOperators.cql")
        runTest("OperatorTests/Query.cql")
        runTest("OperatorTests/RecursiveFunctions.cql")
        runTest("OperatorTests/Sorting.cql")
        runTest("OperatorTests/StringOperators.cql")
        runTest("OperatorTests/TimeOperators.cql")
        runTest("OperatorTests/TupleAndClassConversions.cql")
        runTest("OperatorTests/TypeOperators.cql")
        runTest("OperatorTests/UndeclaredForward.cql")
        runTest("OperatorTests/UndeclaredSignature.cql")
    }

    private fun inputMatchesOutput(input: String, output: String): Boolean {
        return input.replace("[\\s\\u0000]".toRegex(), "") == output.replace("[\\s\\u0000]".toRegex(), "")
    }

    private fun getInput(fileName: String): InputStream {
        var `is`: InputStream?
        try {
            `is` = FileInputStream("../../cql-to-elm/src/jvmTest/resources/org/cqframework/cql/cql2elm/$fileName")
        } catch (e: FileNotFoundException) {
            `is` = CqlFormatterVisitorTest::class.java.getResourceAsStream(fileName)

            requireNotNull(`is`) { String.format("Invalid test resource: %s", fileName) }
        }

        return `is`
    }
}
