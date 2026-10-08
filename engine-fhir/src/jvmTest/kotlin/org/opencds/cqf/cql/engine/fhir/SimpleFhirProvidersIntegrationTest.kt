package org.opencds.cqf.cql.engine.fhir

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.io.Buffer
import kotlinx.io.writeString
import org.cqframework.cql.cql2elm.CqlCompilerOptions
import org.cqframework.cql.cql2elm.LibraryContentType
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.LibrarySourceProvider
import org.cqframework.cql.cql2elm.ModelManager
import org.hl7.elm.r1.VersionedIdentifier
import org.opencds.cqf.cql.engine.data.CompositeDataProvider
import org.opencds.cqf.cql.engine.execution.CqlEngine
import org.opencds.cqf.cql.engine.execution.Environment
import org.opencds.cqf.cql.engine.fhir.model.SimpleFhirModelResolver
import org.opencds.cqf.cql.engine.fhir.parser.fhirResourceJsonToCqlValue
import org.opencds.cqf.cql.engine.fhir.retrieve.SimpleFhirRetrieveProvider
import org.opencds.cqf.cql.engine.fhir.terminology.SimpleFhirTerminologyProvider
import org.opencds.cqf.cql.engine.util.localDateOf
import org.opencds.cqf.cql.engine.util.zoneIdOf

/**
 * Integration tests for the Simple FHIR providers: a [CqlEngine] evaluates CQL retrieves against an
 * in-memory FHIR JSON bundle, exercising [SimpleFhirRetrieveProvider] (data),
 * [SimpleFhirTerminologyProvider] (value-set membership), and [SimpleFhirModelResolver] (property
 * navigation, choice elements, subject-context filtering) together.
 *
 * The tests run on the JVM, where the FHIR model info loads from the classpath, but nothing in them
 * is JVM-specific: they can graduate to a `commonTest` source set once model-info delivery for the
 * non-JVM targets is settled.
 */
class SimpleFhirProvidersIntegrationTest {

    private val library =
        """
        library SimpleFhirProviderTests version '1.0.0'
        using FHIR version '4.0.1'
        valueset "Systolic BP": 'http://example.org/ValueSet/systolic-bp'
        context Patient
        define "BP Observations": [Observation: "Systolic BP"]
        define "BP Count": Count("BP Observations")
        define "Final BP Count":
          Count([Observation: "Systolic BP"] O where O.status.value = 'final')
        define "Latest BP Time":
          Max([Observation: "Systolic BP"] O return (O.effective as FHIR.dateTime).value)
        define "Years Since Latest BP":
          if "Latest BP Time" is null then null
          else years between (date from "Latest BP Time") and (date from Today())
        """
            .trimIndent()

    // LOINC 8480-6 is in the value set; LOINC 718-7 is deliberately outside it.
    private val valueSetBundle =
        """
        {"resourceType":"Bundle","type":"collection","entry":[{"resource":
          {"resourceType":"ValueSet","id":"systolic-bp","status":"active",
           "url":"http://example.org/ValueSet/systolic-bp",
           "compose":{"include":[{"system":"http://loinc.org","concept":[{"code":"8480-6"}]}]}}
        }]}
        """
            .trimIndent()

    /**
     * One shared data bundle, four subjects:
     * - two-obs: two in-set Observations (2020-03-01 final, 2023-01-10 preliminary) and one
     *   Observation whose code is outside the value set,
     * - anniversary: a single in-set Observation on 2020-03-01,
     * - partial: a single in-set Observation on 2020-03-02,
     * - empty: no Observations at all.
     */
    private val dataBundle =
        """
        {"resourceType":"Bundle","type":"collection","entry":[
          {"resource":{"resourceType":"Patient","id":"two-obs","gender":"female","birthDate":"1980-01-01"}},
          {"resource":{"resourceType":"Patient","id":"anniversary","gender":"female","birthDate":"1980-01-01"}},
          {"resource":{"resourceType":"Patient","id":"partial","gender":"female","birthDate":"1980-01-01"}},
          {"resource":{"resourceType":"Patient","id":"empty","gender":"female","birthDate":"1980-01-01"}},
          {"resource":{"resourceType":"Observation","id":"bp-1","status":"final",
            "code":{"coding":[{"system":"http://loinc.org","code":"8480-6"}]},
            "effectiveDateTime":"2020-03-01","subject":{"reference":"Patient/two-obs"}}},
          {"resource":{"resourceType":"Observation","id":"bp-2","status":"preliminary",
            "code":{"coding":[{"system":"http://loinc.org","code":"8480-6"}]},
            "effectiveDateTime":"2023-01-10","subject":{"reference":"Patient/two-obs"}}},
          {"resource":{"resourceType":"Observation","id":"not-bp","status":"final",
            "code":{"coding":[{"system":"http://loinc.org","code":"718-7"}]},
            "effectiveDateTime":"2024-06-01","subject":{"reference":"Patient/two-obs"}}},
          {"resource":{"resourceType":"Observation","id":"bp-3","status":"final",
            "code":{"coding":[{"system":"http://loinc.org","code":"8480-6"}]},
            "effectiveDateTime":"2020-03-01","subject":{"reference":"Patient/anniversary"}}},
          {"resource":{"resourceType":"Observation","id":"bp-4","status":"final",
            "code":{"coding":[{"system":"http://loinc.org","code":"8480-6"}]},
            "effectiveDateTime":"2020-03-02","subject":{"reference":"Patient/partial"}}}
        ]}
        """
            .trimIndent()

    private val modelManager = ModelManager()
    private val fhirModel = modelManager.resolveModel("FHIR", "4.0.1")

    private val libraryManager =
        LibraryManager(modelManager, CqlCompilerOptions()).apply {
            librarySourceLoader.registerProvider(
                object : LibrarySourceProvider {
                    override fun getLibrarySource(libraryIdentifier: VersionedIdentifier) =
                        if (libraryIdentifier.id == "SimpleFhirProviderTests") {
                            Buffer().apply { writeString(library) }
                        } else {
                            null
                        }

                    override fun getLibraryContent(
                        libraryIdentifier: VersionedIdentifier,
                        type: LibraryContentType,
                    ) =
                        if (type == LibraryContentType.CQL) getLibrarySource(libraryIdentifier)
                        else null
                }
            )
        }

    private fun parseBundle(json: String) =
        fhirResourceJsonToCqlValue(Buffer().apply { writeString(json) }, fhirModel)

    private fun evaluate(
        define: String,
        subjectId: String,
        year: Int = 2026,
        month: Int = 3,
        day: Int = 1,
    ): Any? {
        val terminologyProvider = SimpleFhirTerminologyProvider(parseBundle(valueSetBundle))
        val dataProvider =
            CompositeDataProvider(
                SimpleFhirModelResolver(fhirModel),
                SimpleFhirRetrieveProvider(parseBundle(dataBundle), terminologyProvider),
            )
        val engine =
            CqlEngine(
                Environment(
                    libraryManager,
                    mutableMapOf(fhirModelNamespaceUri to dataProvider),
                    terminologyProvider,
                )
            )
        val results =
            engine
                .evaluate {
                    library("SimpleFhirProviderTests") { expressions(define) }
                    contextParameter = "Patient" to subjectId
                    evaluationDateTime = localDateOf(year, month, day).atStartOfDay(zoneIdOf("UTC"))
                }
                .onlyResultOrThrow
        return when (val value = results[define]?.value) {
            is org.opencds.cqf.cql.engine.runtime.Integer -> value.value
            is org.opencds.cqf.cql.engine.runtime.String -> value.value
            is org.opencds.cqf.cql.engine.runtime.Boolean -> value.value
            else -> value
        }
    }

    // ---- Retrieve + terminology: value-set filtering ----

    @Test
    fun retrievesOnlyResourcesWhoseCodeIsInTheValueSet() {
        // Patient two-obs has three Observations; only the two LOINC 8480-6 ones count.
        assertEquals(2, evaluate("BP Count", subjectId = "two-obs"))
    }

    // ---- Retrieve + model resolver: subject-context filtering from a shared bundle ----

    @Test
    fun filtersRetrievesToTheContextSubject() {
        assertEquals(1, evaluate("BP Count", subjectId = "anniversary"))
        assertEquals(0, evaluate("BP Count", subjectId = "empty"))
    }

    // ---- Model resolver: primitive property navigation ----

    @Test
    fun navigatesPrimitivePropertiesThroughValueAccess() {
        // status.value = 'final' keeps bp-1 and drops the preliminary bp-2.
        assertEquals(1, evaluate("Final BP Count", subjectId = "two-obs"))
    }

    // ---- Model resolver: choice elements and aggregates over projections ----

    @Test
    fun takesMaxOverAChoiceElementProjection() {
        // Max over (effective as FHIR.dateTime).value picks 2023-01-10 over 2020-03-01;
        // 2023-01-10 -> 2026-03-01 is 3 whole years.
        assertEquals(3, evaluate("Years Since Latest BP", subjectId = "two-obs"))
    }

    // ---- Date arithmetic against the evaluation clock ----

    @Test
    fun countsAnExactAnniversaryAsAFullYear() {
        // 2020-03-01 -> 2026-03-01 is exactly 6 years.
        assertEquals(6, evaluate("Years Since Latest BP", subjectId = "anniversary"))
    }

    @Test
    fun doesNotCountAPartialYear() {
        // 2020-03-02 -> 2026-03-01 is one day short of 6 years.
        assertEquals(5, evaluate("Years Since Latest BP", subjectId = "partial"))
    }

    @Test
    fun honorsTheEvaluationDateTime() {
        // The same subject and data, one day earlier on the clock: 2026-02-28 is before the
        // 2020-03-01 anniversary, so only 5 whole years have elapsed.
        assertEquals(
            5,
            evaluate("Years Since Latest BP", subjectId = "anniversary", month = 2, day = 28),
        )
    }

    // ---- Null propagation ----

    @Test
    fun propagatesNullWhenNoDataMatches() {
        assertNull(evaluate("Years Since Latest BP", subjectId = "empty"))
    }
}
