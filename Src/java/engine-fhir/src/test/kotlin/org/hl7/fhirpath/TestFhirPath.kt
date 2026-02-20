package org.hl7.fhirpath

import ca.uhn.fhir.context.FhirContext
import jakarta.xml.bind.JAXB
import java.io.InputStreamReader
import java.lang.Boolean
import java.math.BigDecimal
import java.time.Instant
import java.time.ZoneOffset
import java.util.*
import java.util.function.Function
import kotlin.Any
import kotlin.Exception
import kotlin.IllegalArgumentException
import kotlin.RuntimeException
import kotlin.String
import kotlin.plus
import org.cqframework.cql.cql2elm.CqlCompilerException
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhirpath.TranslatorHelper.toElmIdentifier
import org.hl7.fhirpath.tests.InvalidType
import org.hl7.fhirpath.tests.Output
import org.hl7.fhirpath.tests.OutputType
import org.hl7.fhirpath.tests.Test
import org.hl7.fhirpath.tests.Tests
import org.opencds.cqf.cql.engine.data.CompositeDataProvider
import org.opencds.cqf.cql.engine.elm.executing.ToQuantityEvaluator
import org.opencds.cqf.cql.engine.elm.executing.ToStringEvaluator
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.execution.EvaluationResult
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.fhir.model.FhirModelResolver
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Time
import org.slf4j.Logger
import org.slf4j.LoggerFactory

abstract class TestFhirPath {
    open class TestCase(val name: String?, val cql: String?)

    class Error(name: String?, cql: String?) : TestCase(name, cql) // Compile time (Semantic) error

    class Invalid(name: String?, cql: String?) : TestCase(name, cql) // Runtime (Invalid) error

    class Pass(
        name: String?,
        cql: String?,
        val resource: IBaseResource?,
        val results: MutableList<Any?>?,
    ) : TestCase(name, cql) // Successful execution

    private val libraryId = toElmIdentifier("TestFHIRPath")
    private val header =
        """
        library TestFHIRPath
        using FHIR version '4.0.1'
        include FHIRHelpers version '4.0.1' called FHIRHelpers



        """
            .trimIndent()

    private val parametersTemplate =
        """
        parameter %s %s
        parameter "%%context" %s
        parameter "%%resource" %s
        parameter "%%rootResource" %s context %s



        """
            .trimIndent()

    private val definesTemplate =
        """
        define Test: %s

        """
            .trimIndent()

    abstract fun compareResults(
        expectedResult: Any?,
        actualResult: Any?,
        state: State?,
        resolver: FhirModelResolver<*, *, *, *, *, *, *, *>,
    ): kotlin.Boolean?

    protected fun runTest(
        test: Test,
        basePathInput: String?,
        fhirContext: FhirContext,
        provider: CompositeDataProvider?,
        resolver: FhirModelResolver<*, *, *, *, *, *, *, *>,
    ) {
        val testCase = buildTestCase(test, basePathInput, fhirContext)
        val engine = TranslatorHelper.getEngine(testCase.cql!!)
        engine.state.environment.registerDataProvider("http://hl7.org/fhir", provider)
        if (testCase is Pass && testCase.resource != null) {
            val resource = testCase.resource
            engine.state.setParameter(null, resource.fhirType(), resource)
            engine.state.setParameter(null, "%context", resource)
            engine.state.setParameter(null, "%resource", resource)
            engine.state.setParameter(null, "%rootResource", resource)
        }

        var result: EvaluationResult?
        try {
            result =
                engine
                    .evaluate {
                        library(libraryId)
                        parameters = engine.state.parameters
                    }
                    .onlyResultOrThrow
        } catch (e: CqlException) {
            if (testCase is Pass) {
                throw failWithContext(
                    "Runtime error and was expecting a result",
                    testCase,
                    "N/A",
                    e,
                )
            }

            if (testCase is Error && e.cause is CqlCompilerException) {
                return // Expected a compile time error.
            }

            if (testCase is Invalid) {
                return // Expected a runtime error
            }

            log.warn(
                "%s failed as expected, but unable to determine the cause of the failure. Possible bug in engine. Skipping."
                    .format(testCase.name)
            )
            return
        }

        if (testCase is Invalid) {
            log.warn(
                "%s was marked as Invalid, but it got a result. Possible bug in test suite. Skipping."
                    .format(testCase.name)
            )
            return
        }

        val testValue = result["Test"]!!.value
        val actualList =
            testValue as? MutableList<*>
                ?: if (testValue == null) mutableListOf() else listOf<Any?>(testValue)

        // Catch-all to prevent ClassCastException
        if (testCase !is Pass) {
            throw failWithContext(
                "expected a non-Pass test case for %s, but got %s"
                    .format(testCase.name, testCase.javaClass.getSimpleName()),
                testCase,
                actualList,
                null,
            )
        }

        // Invalid and Semantic errors have been handled above, so we can assume Pass here
        if (actualList.size != testCase.results!!.size) {
            throw failWithContext(
                "Incorrect number of results. Expected %d, Actual %d"
                    .format(testCase.results.size, actualList.size),
                testCase,
                actualList,
                null,
            )
        }

        for (i in actualList.indices) {
            val expected = testCase.results[i]
            val actual: Any? = actualList[i]
            val comparison = compareResults(expected, actual, engine.state, resolver)
            if (Boolean.TRUE != comparison) {
                throw failWithContext(
                    "Result mismatch at index %d".format(i),
                    testCase,
                    actual,
                    null,
                )
            }
        }
    }

    private fun buildTestCase(
        test: Test,
        basePathInput: String?,
        fhirContext: FhirContext,
    ): TestCase {
        val resource =
            Optional.ofNullable<String?>(test.getInputfile())
                .map<IBaseResource>(
                    Function { inputFile: String? ->
                        loadResourceFile(basePathInput + inputFile, fhirContext)
                    }
                )
                .orElse(null)

        val cql = buildCql(test, resource)
        val invalid =
            Optional.ofNullable<InvalidType>(test.getExpression().getInvalid())
                .orElse(InvalidType.FALSE)
        val expectedResults = loadExpectedResults(test)
        return when (invalid) {
            InvalidType.TRUE -> Invalid(test.getName(), cql)
            InvalidType.SEMANTIC -> Error(test.getName(), cql)
            InvalidType.FALSE -> Pass(test.getName(), cql, resource, expectedResults)
        }
    }

    private fun buildCql(test: Test, resource: IBaseResource?): String {
        val inputExpression = test.getExpression().getValue()
        val predicate = Optional.ofNullable<kotlin.Boolean>(test.isPredicate).orElse(false)
        val expressionOutput = test.getOutput().size == 1 && test.getOutput()[0].getType() == null

        var testExpression = inputExpression
        if (predicate) {
            testExpression = "exists { %s }".format(inputExpression)
        } else if (expressionOutput) {
            val outputExpression = test.getOutput()[0].getValue()
            testExpression =
                if ("null" == outputExpression) {
                    "(%s) is %s".format(inputExpression, outputExpression)
                } else if ("{ }" == outputExpression) {
                    "not exists (%s)".format(inputExpression)
                } else if ("null" == inputExpression) {
                    "(%s) is %s".format(outputExpression, inputExpression)
                } else {
                    "(%s) = (%s)".format(inputExpression, outputExpression)
                }
        }

        // if have a resource, add the CQL parameters.
        val params =
            Optional.ofNullable<IBaseResource?>(resource)
                .map(
                    Function { res: IBaseResource? ->
                        parametersTemplate.format(
                            *Collections.nCopies(6, res!!.fhirType()).toTypedArray()
                        )
                    }
                )
                .orElse("")

        val defines: String = definesTemplate.format(testExpression)
        return header + params + defines
    }

    private fun failWithContext(
        message: String?,
        test: TestCase,
        actual: Any?,
        e: Exception?,
    ): RuntimeException {
        val expectedString = if (test is Pass) ToStringEvaluator.toString(test.results) else "N/A"
        val actualString = ToStringEvaluator.toString(actual)
        val error =
            """
            Failed Test: %s
            - Message: %s
            - Expected: %s
            - Actual: %s
            - CQL:%n%n%s

            """
                .trimIndent()
        return RuntimeException(
            error.format(test.name, message, expectedString, actualString, test.cql),
            e,
        )
    }

    private fun loadResourceFile(resourceFilePath: String, context: FhirContext): IBaseResource? {
        return context
            .newXmlParser()
            .parseResource(
                InputStreamReader(TestFhirPath::class.java.getResourceAsStream(resourceFilePath)!!)
            )
    }

    private fun readOutput(output: Output): Any? {
        if (output.getType() == null) {
            return null
        }

        return when (output.getType()) {
            OutputType.BOOLEAN -> output.getValue().toBoolean()
            OutputType.DECIMAL -> BigDecimal(output.getValue())
            OutputType.DATE -> Date(output.getValue())
            OutputType.DATE_TIME ->
                DateTime(
                    output.getValue(),
                    ZoneOffset.systemDefault().rules.getOffset(Instant.now()),
                )

            OutputType.TIME -> Time(output.getValue())
            OutputType.INTEGER -> output.getValue().toInt()
            OutputType.STRING,
            OutputType.CODE -> output.getValue()
            OutputType.QUANTITY -> ToQuantityEvaluator.toQuantity(output.getValue())
            else ->
                throw IllegalArgumentException(
                    "Output type [ %s ] is not supported in tests"
                        .format(if (output.getType() == null) "null" else output.getType())
                )
        }
    }

    private fun loadExpectedResults(test: Test): MutableList<Any?> {
        // Special case for tests are "expression output" tests, which have a single output with no
        // type
        if (test.getOutput().size == 1 && test.getOutput()[0].getType() == null) {
            return mutableListOf(true)
        }

        return test.getOutput()!!.map { this.readOutput(it) }.toMutableList()
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(TestFhirPath::class.java)

        @JvmStatic
        protected fun loadTestsFile(testsFilePath: String): Tests? {
            try {
                val testsFileRaw = TestFhirPath::class.java.getResourceAsStream(testsFilePath)
                return JAXB.unmarshal(testsFileRaw, Tests::class.java)
            } catch (e: Exception) {
                // e.printStackTrace();
                throw IllegalArgumentException(
                    "Couldn't load tests file [ %s ]".format(testsFilePath),
                    e,
                )
            }
        }
    }
}
