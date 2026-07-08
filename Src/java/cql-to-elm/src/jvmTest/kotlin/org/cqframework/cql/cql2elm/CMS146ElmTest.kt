package org.cqframework.cql.cql2elm

import java.io.IOException
import java.util.ArrayList
import java.util.stream.Collectors
import javax.xml.namespace.QName
import kotlin.uuid.ExperimentalUuidApi
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.cqframework.cql.cql2elm.tracking.TrackBack
import org.cqframework.cql.cql2elm.tracking.Trackable.trackbacks
import org.cqframework.cql.cql2elm.tracking.Trackable.trackerId
import org.cqframework.cql.elm.visiting.FunctionalElmVisitor.Companion.from
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hl7.cql_annotations.r1.CqlToElmBase
import org.hl7.cql_annotations.r1.CqlToElmInfo
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.ObjectFactory
import org.hl7.elm.r1.Retrieve
import org.hl7.elm.r1.ValueSetDef
import org.hl7.elm.r1.ValueSetRef
import org.hl7.elm.r1.VersionedIdentifier
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@Suppress("LongMethod", "CyclomaticComplexMethod", "ForbiddenComment")
class CMS146ElmTest {
    @ParameterizedTest
    @MethodSource("signatureLevels")
    @Throws(IOException::class)
    fun signatureLevels(signatureLevel: LibraryBuilder.SignatureLevel) {
        val modelManager = ModelManager()
        val translator =
            CqlTranslator.fromSource(
                CMS146ElmTest::class
                    .java
                    .getResourceAsStream("CMS146v2_Test_CQM.cql")!!
                    .asSource()
                    .buffered(),
                LibraryManager(
                    modelManager,
                    CqlCompilerOptions(CqlCompilerException.ErrorSeverity.Warning, signatureLevel),
                ),
            )
        val library = translator.toELM()

        val annotations = library!!.annotation
        MatcherAssert.assertThat(annotations.size, Matchers.equalTo(3))

        val casts =
            annotations
                .stream()
                .filter { obj: CqlToElmBase? -> CqlToElmInfo::class.java.isInstance(obj) }
                .map { obj: CqlToElmBase? -> CqlToElmInfo::class.java.cast(obj) }
                .collect(Collectors.toList())

        MatcherAssert.assertThat(casts.size, Matchers.equalTo(1))
        MatcherAssert.assertThat(casts[0]!!.signatureLevel, Matchers.equalTo(signatureLevel.name))
    }

    @Test
    fun libraryAndVersion() {
        MatcherAssert.assertThat<VersionedIdentifier?>(
            library!!.identifier,
            Matchers.`is`<VersionedIdentifier>(
                of.createVersionedIdentifier().withId("CMS146").withVersion("2")
            ),
        )
    }

    @Test
    fun usingDataModel() {
        val models = library!!.usings!!.def
        MatcherAssert.assertThat(models, Matchers.hasSize(2))
        MatcherAssert.assertThat<String?>(
            models[1].uri,
            Matchers.`is`<String?>("http://hl7.org/fhir"),
        )
    }

    @Test
    fun clinicalRequests() {
        val v = from<MutableList<Retrieve>, Unit> { elm, acc -> if (elm is Retrieve) acc.add(elm) }
        val actualCR = mutableListOf<Retrieve>()
        v.visitLibrary(library!!, actualCR)

        val expectedCR =
            listOf(
                of.createRetrieve()
                    .withDataType(quickDataType("Patient"))
                    .withTemplateId("patient-qicore-qicore-patient"),
                of.createRetrieve()
                    .withDataType(quickDataType("Condition"))
                    .withTemplateId("condition-qicore-qicore-condition")
                    .withCodeProperty("code")
                    .withCodeComparator("in")
                    .withCodes(
                        of.createValueSetRef().withName("Acute Pharyngitis").withPreserve(true)
                    ),
                of.createRetrieve()
                    .withDataType(quickDataType("Condition"))
                    .withTemplateId("condition-qicore-qicore-condition")
                    .withCodeProperty("code")
                    .withCodeComparator("in")
                    .withCodes(
                        of.createValueSetRef().withName("Acute Tonsillitis").withPreserve(true)
                    ),
                of.createRetrieve()
                    .withDataType(quickDataType("MedicationPrescription"))
                    .withTemplateId("medicationprescription-qicore-qicore-medicationprescription")
                    .withCodeProperty("medication.code")
                    .withCodeComparator("in")
                    .withCodes(
                        of.createValueSetRef().withName("Antibiotic Medications").withPreserve(true)
                    ),
                of.createRetrieve()
                    .withDataType(quickDataType("Encounter"))
                    .withTemplateId("encounter-qicore-qicore-encounter")
                    .withCodeProperty("type")
                    .withCodeComparator("in")
                    .withCodes(
                        of.createValueSetRef().withName("Ambulatory/ED Visit").withPreserve(true)
                    ),
                of.createRetrieve()
                    .withDataType(quickDataType("Observation"))
                    .withTemplateId("observation-qicore-qicore-observation")
                    .withCodeProperty("code")
                    .withCodeComparator("in")
                    .withCodes(
                        of.createValueSetRef()
                            .withName("Group A Streptococcus Test")
                            .withPreserve(true)
                    ),
            )

        MatcherAssert.assertThat(actualCR, Matchers.`is`(expectedCR))
    }

    // TODO: Disabled the test for now, valuesets have been moved to expression definitions. These
    // are being checked in
    // the testVariables() test, but not as completely as this.
    @Test
    @Disabled
    fun valueSets() {
        val actualVS = library!!.valueSets!!.def

        val expectedVS =
            listOf<ValueSetDef?>(
                of.createValueSetDef()
                    .withName("Acute Pharyngitis")
                    .withId("2.16.840.1.113883.3.464.1003.102.12.1011"),
                of.createValueSetDef()
                    .withName("Acute Tonsillitis")
                    .withId("2.16.840.1.113883.3.464.1003.102.12.1012"),
                of.createValueSetDef()
                    .withName("Ambulatory/ED Visit")
                    .withId("2.16.840.1.113883.3.464.1003.101.12.1061"),
                of.createValueSetDef()
                    .withName("Antibiotic Medications")
                    .withId("2.16.840.1.113883.3.464.1003.196.12.1001"),
                of.createValueSetDef()
                    .withName("Group A Streptococcus Test")
                    .withId("2.16.840.1.113883.3.464.1003.198.12.1012"),
            )

        MatcherAssert.assertThat(actualVS, Matchers.`is`(expectedVS))
    }

    @Test
    fun variables() {
        val actualVars: MutableCollection<String?> = ArrayList<String?>()
        for (def in library!!.statements!!.def) {
            actualVars.add(def.name)
        }

        val expectedVars: MutableCollection<String?> =
            mutableListOf(
                "Patient",
                "InDemographic",
                "Pharyngitis",
                "Antibiotics",
                "TargetEncounters",
                "TargetDiagnoses",
                "HasPriorAntibiotics",
                "HasTargetEncounter",
                "InInitialPopulation",
                "InDenominator",
                "InDenominatorExclusions",
                "InNumerator",
            )

        MatcherAssert.assertThat(actualVars, Matchers.`is`(expectedVars))
    }

    // TODO: Disabled the test for now, needs to be updated to use annotations, will update after
    // all syntax changes.
    @OptIn(ExperimentalUuidApi::class)
    @Test
    @Disabled
    fun trackBacks() {
        val v = from<MutableList<Retrieve>, Unit> { elm, acc -> if (elm is Retrieve) acc.add(elm) }
        val retrieves = mutableListOf<Retrieve>()
        v.visitLibrary(library!!, retrieves)
        for (dc in retrieves) {
            var expectedNumbers: IntArray? = IntArray(4)
            when ((dc.codes as ValueSetRef).name) {
                "Acute Pharyngitis" -> expectedNumbers = intArrayOf(19, 6, 19, 37)
                "Acute Tonsillitis" -> expectedNumbers = intArrayOf(19, 47, 19, 77)
                "Antibiotic Medications" -> expectedNumbers = intArrayOf(22, 5, 22, 58)
                "Ambulatory/ED Visit" -> expectedNumbers = intArrayOf(25, 5, 25, 51)
                "Group A Streptococcus Test" -> expectedNumbers = intArrayOf(49, 13, 49, 61)
                else -> Assertions.fail<Any?>("Unknown source data criteria: $dc")
            }

            val trackerId = dc.trackerId
            val trackbacks = dc.trackbacks
            MatcherAssert.assertThat<Any?>(trackerId, Matchers.notNullValue())

            // TODO: some objects get multiple trackers when they shouldn't
            // assertThat(dc.getTrackbacks().size, is(1));
            val tb: TrackBack = trackbacks.iterator().next()
            MatcherAssert.assertThat<VersionedIdentifier?>(
                tb.library,
                Matchers.`is`<VersionedIdentifier>(
                    of.createVersionedIdentifier().withId("CMS146").withVersion("2")
                ),
            )
            MatcherAssert.assertThat(tb.startLine, Matchers.`is`(expectedNumbers!![0]))
            MatcherAssert.assertThat(tb.startChar, Matchers.`is`(expectedNumbers[1]))
            MatcherAssert.assertThat(tb.endLine, Matchers.`is`(expectedNumbers[2]))
            MatcherAssert.assertThat(tb.endChar, Matchers.`is`(expectedNumbers[3]))
        }

        for (vs in library!!.valueSets!!.def) {
            var expectedNumbers: IntArray? = IntArray(4)
            when (vs.id) {
                "2.16.840.1.113883.3.464.1003.102.12.1011" ->
                    expectedNumbers = intArrayOf(7, 1, 7, 83)
                "2.16.840.1.113883.3.464.1003.102.12.1012" ->
                    expectedNumbers = intArrayOf(8, 1, 8, 83)
                "2.16.840.1.113883.3.464.1003.101.12.1061" ->
                    expectedNumbers = intArrayOf(9, 1, 9, 85)
                "2.16.840.1.113883.3.464.1003.196.12.1001" ->
                    expectedNumbers = intArrayOf(10, 1, 10, 88)
                "2.16.840.1.113883.3.464.1003.198.12.1012" ->
                    expectedNumbers = intArrayOf(11, 1, 11, 92)
                else -> Assertions.fail<Any?>("Unknown valueset: $vs")
            }

            val trackerId = vs.trackerId
            val trackbacks = vs.trackbacks
            MatcherAssert.assertThat<Any?>(trackerId, Matchers.notNullValue())
            MatcherAssert.assertThat(trackbacks.size, Matchers.`is`(1))

            val tb: TrackBack = trackbacks.iterator().next()
            MatcherAssert.assertThat<VersionedIdentifier?>(
                tb.library,
                Matchers.`is`<VersionedIdentifier>(
                    of.createVersionedIdentifier().withId("CMS146").withVersion("2")
                ),
            )
            MatcherAssert.assertThat(tb.startLine, Matchers.`is`(expectedNumbers!![0]))
            MatcherAssert.assertThat(tb.startChar, Matchers.`is`(expectedNumbers[1]))
            MatcherAssert.assertThat(tb.endLine, Matchers.`is`(expectedNumbers[2]))
            MatcherAssert.assertThat(tb.endChar, Matchers.`is`(expectedNumbers[3]))
        }

        for (ls in library!!.statements!!.def) {
            var expectedNumbers: IntArray? = IntArray(4)
            when (ls.name) {
                "InDemographic" -> expectedNumbers = intArrayOf(15, 1, 16, 85)
                "Pharyngitis" -> expectedNumbers = intArrayOf(18, 1, 19, 78)
                "Antibiotics" -> expectedNumbers = intArrayOf(21, 1, 22, 58)
                "TargetEncounters" -> expectedNumbers = intArrayOf(24, 1, 28, 56)
                "TargetDiagnoses" -> expectedNumbers = intArrayOf(30, 1, 31, 96)
                "HasPriorAntibiotics" -> expectedNumbers = intArrayOf(33, 1, 34, 123)
                "HasTargetEncounter" -> expectedNumbers = intArrayOf(36, 1, 37, 29)
                "InInitialPopulation" -> expectedNumbers = intArrayOf(39, 1, 40, 40)
                "InDenominator" -> expectedNumbers = intArrayOf(42, 1, 43, 8)
                "InDenominatorExclusions" -> expectedNumbers = intArrayOf(45, 1, 46, 23)
                "InNumerator" -> expectedNumbers = intArrayOf(48, 1, 49, 137)
                else -> Assertions.fail("Unknown variable: " + ls.name)
            }

            val trackerId = ls.trackerId
            val trackbacks = ls.trackbacks
            MatcherAssert.assertThat<Any?>(trackerId, Matchers.notNullValue())
            MatcherAssert.assertThat(trackbacks.size, Matchers.`is`(1))

            val tb: TrackBack = trackbacks.iterator().next()
            MatcherAssert.assertThat<VersionedIdentifier?>(
                tb.library,
                Matchers.`is`<VersionedIdentifier>(
                    of.createVersionedIdentifier().withId("CMS146").withVersion("2")
                ),
            )
            MatcherAssert.assertThat(tb.startLine, Matchers.`is`(expectedNumbers!![0]))
            MatcherAssert.assertThat(tb.startChar, Matchers.`is`(expectedNumbers[1]))
            MatcherAssert.assertThat(tb.endLine, Matchers.`is`(expectedNumbers[2]))
            MatcherAssert.assertThat(tb.endChar, Matchers.`is`(expectedNumbers[3]))
        }
    }

    private fun quickDataType(dataTypeName: String): QName {
        return QName("http://hl7.org/fhir", dataTypeName, "quick")
    }

    companion object {
        private var translator: CqlTranslator? = null
        private var library: Library? = null
        private var of = ObjectFactory()

        @JvmStatic
        @BeforeAll
        @Throws(IOException::class)
        fun setup() {
            val modelManager = ModelManager()
            translator =
                CqlTranslator.fromSource(
                    CMS146ElmTest::class
                        .java
                        .getResourceAsStream("CMS146v2_Test_CQM.cql")!!
                        .asSource()
                        .buffered(),
                    LibraryManager(
                        modelManager,
                        CqlCompilerOptions(
                            CqlCompilerException.ErrorSeverity.Warning,
                            LibraryBuilder.SignatureLevel.None,
                        ),
                    ),
                )
            MatcherAssert.assertThat(translator!!.errors.size, Matchers.`is`(0))
            library = translator!!.toELM()
        }

        @JvmStatic
        fun signatureLevels(): Array<Array<Any>> {
            return arrayOf(
                arrayOf(LibraryBuilder.SignatureLevel.None),
                arrayOf(LibraryBuilder.SignatureLevel.Differing),
                arrayOf(LibraryBuilder.SignatureLevel.Overloads),
                arrayOf(LibraryBuilder.SignatureLevel.All),
            )
        }
    }
}
