package org.opencds.cqf.cql.engine.fhir.data

import java.io.IOException
import java.net.URISyntaxException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.stream.Collectors
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.cqframework.cql.cql2elm.CqlCompiler
import org.cqframework.cql.cql2elm.CqlCompilerException.ErrorSeverity
import org.cqframework.cql.cql2elm.LibraryManager
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.VersionedIdentifier
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.instance.model.api.IIdType
import org.hl7.fhir.r4.model.Condition
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.IdType
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.Period
import org.hl7.fhir.r4.model.Procedure
import org.hl7.fhir.r4.model.ResourceType
import org.opencds.cqf.cql.engine.data.CompositeDataProvider
import org.opencds.cqf.cql.engine.execution.CqlEngine
import org.opencds.cqf.cql.engine.execution.EvaluationResult
import org.opencds.cqf.cql.engine.execution.EvaluationResultsForMultiLib
import org.opencds.cqf.cql.engine.execution.ExpressionResult
import org.opencds.cqf.cql.engine.fhir.model.R4FhirModelResolver
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Interval
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal object EvaluatedResourceTestUtils {
    private val log: Logger = LoggerFactory.getLogger(EvaluatedResourceTestUtils::class.java)

    val ENCOUNTER: Encounter =
        Encounter().setId(IdType(ResourceType.Encounter.name, "Encounter1")) as Encounter

    val CONDITION: Condition =
        Condition().setId(IdType(ResourceType.Condition.name, "Condition1")) as Condition

    val PATIENT: Patient = Patient().setId(IdType(ResourceType.Patient.name, "Patient1")) as Patient

    val PROCEDURE: Procedure =
        Procedure().setId(IdType(ResourceType.Procedure.name, "Procedure1")) as Procedure

    val RETRIEVE_PROVIDER: RetrieveProvider =
        object : RetrieveProvider {
            override fun retrieve(
                context: String?,
                contextPath: String?,
                contextValue: Any?,
                dataType: String,
                templateId: String?,
                codePath: String?,
                codes: Iterable<Code>?,
                valueSet: String?,
                datePath: String?,
                dateLowPath: String?,
                dateHighPath: String?,
                dateRange: Interval?,
            ): Iterable<Any?>? {
                return when (dataType) {
                    "Encounter" -> mutableListOf<Any?>(ENCOUNTER)
                    "Condition" -> mutableListOf<Any?>(CONDITION)
                    "Patient" -> mutableListOf<Any?>(PATIENT)
                    "Procedure" -> mutableListOf<Any?>(PROCEDURE)
                    else -> mutableListOf<Any?>()
                }
            }
        }

    fun setupCql(
        classToUse: Class<*>,
        librariesToPopulate: MutableList<Library?>,
        libraryManagerToUse: LibraryManager,
    ) {
        if (librariesToPopulate.isEmpty()) {
            try {
                val resourcePaths = getResources(classToUse)

                for (resourcePath in resourcePaths) {
                    try {
                        classToUse.getClassLoader().getResourceAsStream(resourcePath).use {
                            inputStream ->
                            val compiler = CqlCompiler(libraryManager = libraryManagerToUse)
                            log.info("compiling CQL file: {}", resourcePath)

                            val library = compiler.run(inputStream!!.asSource().buffered())

                            val errors =
                                compiler.exceptions.filter { it.severity == ErrorSeverity.Error }
                            if (errors.isNotEmpty()) {
                                System.err.println("Translation failed due to errors:")
                                val messages = mutableListOf<String>()
                                for (error in compiler.exceptions) {
                                    val tb = error.locator
                                    val lines =
                                        if (tb == null) "[n/a]"
                                        else
                                            "[${tb.startLine}:${tb.startChar}, ${tb.endLine}:${tb.endChar}]"
                                    System.err.printf("%s %s%n", lines, error.message)
                                    messages.add(lines + error.message)
                                }
                                throw IllegalArgumentException(messages.toString())
                            }
                            librariesToPopulate.add(library)
                        }
                    } catch (exception: Exception) {
                        val cqlFileName: String? =
                            resourcePath
                                .split("/".toRegex())
                                .dropLastWhile { it.isEmpty() }
                                .toTypedArray()[7]
                        val error: String =
                            "Could not retrieve CQL files on %s due to :%s"
                                .format(cqlFileName, exception.message)
                        throw RuntimeException(error, exception)
                    }
                }
            } catch (exception: IOException) {
                val error: String =
                    "Could not retrieve CQL files due to :%s".format(exception.message)
                throw RuntimeException(error, exception)
            } catch (exception: URISyntaxException) {
                val error: String =
                    "Could not retrieve CQL files due to :%s".format(exception.message)
                throw RuntimeException(error, exception)
            }
        }
    }

    @Throws(IOException::class, URISyntaxException::class)
    private fun getResources(classToUse: Class<*>): MutableList<String> {
        val foundResources = ArrayList<String>()
        val pattern = classToUse.getSimpleName() + "*.cql"

        val classLoader = classToUse.getClassLoader()
        val packagePath = classToUse.getPackage().name.replace('.', '/')

        val urlsWithinPackage = classLoader.getResources(packagePath)

        while (urlsWithinPackage.hasMoreElements()) {
            val subPathUrl = urlsWithinPackage.nextElement()

            // Resource is on the file system.
            val dirPath = Paths.get(subPathUrl.toURI())

            findResourcesInDirectory(dirPath, packagePath, pattern, foundResources)
        }

        return foundResources
    }

    @Throws(IOException::class)
    private fun findResourcesInDirectory(
        directory: Path,
        packagePath: String,
        pattern: String?,
        foundResources: MutableList<String>,
    ) {
        if (!Files.isDirectory(directory)) {
            return
        }

        // Use a PathMatcher for the glob pattern
        val pathMatcher = directory.fileSystem.getPathMatcher("glob:$pattern")

        Files.list(directory).use { stream ->
            stream
                .filter { path: Path -> !Files.isDirectory(path) }
                .filter { path: Path? ->
                    pathMatcher.matches(path!!.fileName)
                } // In the complex deps case, we want to load the "top" level libraries first, so
                // Level5 is the
                // furthest upstream
                .sorted(Comparator.reverseOrder())
                .forEach { path: Path? ->
                    val resourceName = path!!.fileName.toString()
                    // Construct the full resource path for the classloader
                    val fullResourcePath =
                        if (packagePath.isEmpty()) resourceName else "$packagePath/$resourceName"
                    foundResources.add(fullResourcePath)
                }
        }
    }

    fun getCqlEngineForFhir(
        cqlEngine: CqlEngine,
        expressionCaching: Boolean,
        r4ModelResolver: R4FhirModelResolver?,
        retrieveProvider: RetrieveProvider?,
    ): CqlEngine {
        cqlEngine.state.environment.registerDataProvider(
            "http://hl7.org/fhir",
            CompositeDataProvider(r4ModelResolver, retrieveProvider),
        )
        cqlEngine.cache.setExpressionCaching(expressionCaching)
        return cqlEngine
    }

    fun assertEntireEvaluationResult(
        evaluationResultsForMultiLib: EvaluationResultsForMultiLib?,
        libraryIdentifier: VersionedIdentifier?,
        expectedEvaluatedResources: Map<String, Collection<IBaseResource>>,
        expectedValues: Map<String, Collection<IBaseResource>>,
    ) {
        MatcherAssert.assertThat<EvaluationResultsForMultiLib?>(
            evaluationResultsForMultiLib,
            CoreMatchers.`is`(Matchers.notNullValue()),
        )
        val evaluationResult = evaluationResultsForMultiLib!!.getResultFor(libraryIdentifier)

        if (evaluationResult == null) {
            MatcherAssert.assertThat(
                "If the actual evaluationResults are null, we should be expecting an empty collection.",
                expectedValues.values,
                Matchers.empty(),
            )
            return
        }

        val expressionResults = evaluationResult.expressionResults

        for (expressionName in expressionResults.keys) {
            val expressionResult: ExpressionResult = expressionResults[expressionName]!!

            val actualEvaluatedResourcesForName = expressionResult.evaluatedResources!!
            val expectedEvaluatedResourcesForName = expectedEvaluatedResources[expressionName]!!

            assertResourcesEqual(expectedEvaluatedResourcesForName, actualEvaluatedResourcesForName)

            val actualValue = expressionResult.value
            val expectedValue = expectedValues[expressionName]!!

            assertValuesEqual(expectedValue, actualValue)
        }
    }

    fun assertEvaluationResult(
        evaluationResult: EvaluationResult,
        expressionName: String,
        expectedEvaluatedResources: Collection<IBaseResource>,
    ) {
        val expressionResult = evaluationResult[expressionName]
        val actualEvaluatedResources = expressionResult!!.evaluatedResources!!
        val actualValue = expressionResult.value

        assertResourcesEqual(expectedEvaluatedResources, actualEvaluatedResources)
        assertValuesEqual(expectedEvaluatedResources, actualValue)
    }

    fun assertEvaluationResult(
        evaluationResultsForMultiLib: EvaluationResultsForMultiLib?,
        libraryIdentifier: VersionedIdentifier?,
        expressionName: String,
        expectedEvaluatedResources: Collection<IBaseResource>,
        expectedValue: Collection<IBaseResource>,
    ) {
        MatcherAssert.assertThat(
            evaluationResultsForMultiLib,
            CoreMatchers.`is`(Matchers.notNullValue()),
        )
        val evaluationResult = evaluationResultsForMultiLib!!.getResultFor(libraryIdentifier)
        val expressionResult = evaluationResult!![expressionName]
        val actualEvaluatedResources = expressionResult!!.evaluatedResources!!
        val actualValue = expressionResult.value

        assertResourcesEqual(expectedEvaluatedResources, actualEvaluatedResources)
        assertValuesEqual(expectedValue, actualValue)
    }

    fun assertEvaluationResult(
        evaluationResult: EvaluationResult?,
        expressionName: String,
        expectedEvaluatedResources: Collection<IBaseResource>,
        expectedValue: Collection<IBaseResource>,
    ) {
        MatcherAssert.assertThat<EvaluationResult?>(
            evaluationResult,
            CoreMatchers.`is`(Matchers.notNullValue()),
        )
        val expressionResult = evaluationResult!![expressionName]
        val actualEvaluatedResources = expressionResult!!.evaluatedResources!!
        val actualValue = expressionResult.value

        assertResourcesEqual(expectedEvaluatedResources, actualEvaluatedResources)
        assertValuesEqual(expectedValue, actualValue)
    }

    fun forId(id: String?): VersionedIdentifier {
        return VersionedIdentifier().withId(id)
    }

    private fun extractResourcesInOrder(resourceCandidates: Collection<*>): List<IBaseResource> {
        return resourceCandidates
            .filter { obj: Any? -> IBaseResource::class.java.isInstance(obj) }
            .map { obj: Any? -> IBaseResource::class.java.cast(obj) }
            .sortedBy { it.idElement.idPart }
    }

    private fun assertValuesEqual(expectedValue: Collection<IBaseResource>, actualValue: Any?) {
        MatcherAssert.assertThat<Any?>(
            actualValue,
            CoreMatchers.instanceOf<Any?>(MutableList::class.java),
        )
        val actualValues = actualValue as MutableList<*>

        assertResourcesEqual(expectedValue, actualValues)
    }

    private fun assertResourcesEqual(
        expectedResources: Collection<*>,
        actualResources: Collection<*>,
    ) {
        MatcherAssert.assertThat(
            showMismatchError(expectedResources, actualResources),
            actualResources.size,
            CoreMatchers.`is`(expectedResources.size),
        )

        val expectedResourcesList = extractResourcesInOrder(expectedResources)
        val actualResourcesList = extractResourcesInOrder(actualResources)

        for (index in expectedResourcesList.indices) {
            val expectedResource = expectedResourcesList[0]
            val actualResource = actualResourcesList[0]

            assertResourcesEqual(expectedResource, actualResource)
        }
    }

    private fun showMismatchError(
        expectedResources: Collection<*>,
        actualResources: Collection<*>,
    ): String {
        return "Expected: %s, actual: %s"
            .format(showResources(expectedResources), showResources(actualResources))
    }

    private fun showResources(resources: Collection<*>): String {
        return resources
            .stream()
            .filter { obj: Any? -> IBaseResource::class.java.isInstance(obj) }
            .map { obj: Any? -> IBaseResource::class.java.cast(obj) }
            .map { obj: IBaseResource? -> obj!!.idElement }
            .map { obj: IIdType? -> obj!!.valueAsString }
            .collect(Collectors.joining(","))
    }

    private fun assertResourcesEqual(
        expectedResource: IBaseResource,
        actualResource: IBaseResource,
    ) {
        MatcherAssert.assertThat(
            actualResource.javaClass,
            CoreMatchers.equalTo<Class<out IBaseResource?>?>(expectedResource.javaClass),
        )
        MatcherAssert.assertThat(
            actualResource.idElement,
            CoreMatchers.equalTo(expectedResource.idElement),
        )
    }

    private class TestRetrieveProvider : RetrieveProvider {
        override fun retrieve(
            context: String?,
            contextPath: String?,
            contextValue: Any?,
            dataType: String,
            templateId: String?,
            codePath: String?,
            codes: Iterable<Code>?,
            valueSet: String?,
            datePath: String?,
            dateLowPath: String?,
            dateHighPath: String?,
            dateRange: Interval?,
        ): Iterable<Any?>? {
            return null
        }

        companion object {
            val ENCOUNTER: Encounter? =
                Encounter()
                    .setPeriod(Period().setStart(null).setEnd(null))
                    .setId(IdType(ResourceType.Encounter.name, "Encounter1")) as Encounter?

            val CONDITION: Condition? =
                Condition().setId(IdType(ResourceType.Condition.name, "Condition1")) as Condition?

            val PATIENT: Patient? =
                Patient().setId(IdType(ResourceType.Patient.name, "Patient1")) as Patient?

            val PROCEDURE: Procedure? =
                Procedure().setId(IdType(ResourceType.Procedure.name, "Procedure1")) as Procedure?
        }
    }
}
