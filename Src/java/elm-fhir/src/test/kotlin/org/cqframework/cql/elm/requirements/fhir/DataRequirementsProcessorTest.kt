package org.cqframework.cql.elm.requirements.fhir

import ca.uhn.fhir.context.FhirContext
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlinx.io.IOException
import kotlinx.io.files.Path
import org.cqframework.cql.cql2elm.CqlCompiler
import org.cqframework.cql.cql2elm.CqlCompilerException
import org.cqframework.cql.cql2elm.CqlCompilerOptions
import org.cqframework.cql.cql2elm.CqlCompilerOptions.Companion.defaultOptions
import org.cqframework.cql.cql2elm.DefaultLibrarySourceProvider
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.ModelManager
import org.cqframework.cql.cql2elm.model.CompiledLibrary
import org.cqframework.cql.cql2elm.quick.FhirLibrarySourceProvider
import org.cqframework.cql.elm.requirements.fhir.utilities.SpecificationLevel
import org.hl7.cql.model.NamespaceInfo
import org.hl7.elm.r1.Exists
import org.hl7.elm.r1.FunctionDef
import org.hl7.elm.r1.Interval
import org.hl7.elm.r1.Literal
import org.hl7.elm.r1.ParameterRef
import org.hl7.elm.r1.Query
import org.hl7.elm.r1.Retrieve
import org.hl7.elm.r1.SingletonFrom
import org.hl7.elm.r1.ValueSetRef
import org.hl7.elm.r1.With
import org.hl7.fhir.r5.model.Coding
import org.hl7.fhir.r5.model.DataRequirement
import org.hl7.fhir.r5.model.Enumerations
import org.hl7.fhir.r5.model.Enumerations.FHIRTypes
import org.hl7.fhir.r5.model.Extension
import org.hl7.fhir.r5.model.Library
import org.hl7.fhir.r5.model.ParameterDefinition
import org.hl7.fhir.r5.model.Period
import org.hl7.fhir.r5.model.RelatedArtifact
import org.hl7.fhir.r5.model.StringType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Suppress(
    "LongMethod",
    "ComplexCondition",
    "NestedBlockDepth",
    "PrintStackTrace",
    "MaxLineLength",
    "ForbiddenComment",
    "UnusedPrivateMember",
)
class DataRequirementsProcessorTest {
    @Test
    fun dataRequirementsProcessor() {
        val cqlTranslatorOptions = CqlCompilerOptions()
        cqlTranslatorOptions.options.add(CqlCompilerOptions.Options.EnableAnnotations)
        try {
            /*
               OpioidCDSCommon.cql
               DataRequirements.cql
               AdultOutpatientEncountersFHIR4.xml
               AdvancedIllnessandFrailtyExclusionECQMFHIR4.xml
               BCSComponent.xml
               CCSComponent.xml
               FHIRHelpers.xml
               HBPComponent.xml
               HospiceFHIR4.xml
               MATGlobalCommonFunctionsFHIR4.xml
               PVSComponent.xml
               SupplementalDataElementsFHIR4.xml
               TSCComponent.xml
               BCSComponent-v0-0-001-FHIR-4-0-1.xml
               CCSComponent-v0-0-001-FHIR-4-0-1.xml
               HBPComponent-v0-0-001-FHIR-4-0-1.xml
               PVSComponent-v0-0-001-FHIR-4-0-1.xml
               TSCComponent-v0-0-001-FHIR-4-0-1.xml
               PreventiveCareandWellness-v0-0-001-FHIR-4-0-1.xml
            */
            val setup: Setup =
                setup(
                    "CompositeMeasures/cql/EXM124-9.0.000.cql",
                    cqlTranslatorOptions,
                ) // "OpioidCDS/cql/OpioidCDSCommon.cql", cqlTranslatorOptions);

            val dqReqTrans = DataRequirementsProcessor()
            val moduleDefinitionLibrary =
                dqReqTrans.gatherDataRequirements(
                    setup.manager,
                    setup.library,
                    cqlTranslatorOptions,
                    null,
                    false,
                )
            assertTrue(
                moduleDefinitionLibrary
                    .getType()
                    .getCode("http://terminology.hl7.org/CodeSystem/library-type")
                    .equals("module-definition", ignoreCase = true)
            )

            val context: FhirContext = fhirContext
            val parser = context.newJsonParser()
            val moduleDefString =
                parser.setPrettyPrint(true).encodeResourceToString(moduleDefinitionLibrary)
            logger.debug(moduleDefString)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    private fun getDependency(moduleDefinitionLibrary: Library, url: String?): RelatedArtifact? {
        for (r in moduleDefinitionLibrary.getRelatedArtifact()) {
            if (
                r.getType() == RelatedArtifact.RelatedArtifactType.DEPENDSON &&
                    r.getResource() == url
            ) {
                return r
            }
        }

        return null
    }

    private fun getParameter(
        moduleDefinitionLibrary: Library,
        parameterName: String?,
    ): ParameterDefinition? {
        for (pd in moduleDefinitionLibrary.getParameter()) {
            if (pd.hasName() && pd.getName() == parameterName) {
                return pd
            }
        }

        return null
    }

    @Test
    fun dataRequirementsProcessorOpioidIssueExpression() {
        val cqlTranslatorOptions = CqlCompilerOptions()
        cqlTranslatorOptions.collapseDataRequirements = true
        cqlTranslatorOptions.analyzeDataRequirements = true
        try {
            val ni = NamespaceInfo("fhir.cdc.opioid-cds", "http://fhir.org/guides/cdc/opioid-cds")
            val setup: Setup =
                setup(ni, "OpioidCDSSTU3/cql/OpioidCDSREC10.cql", cqlTranslatorOptions)

            val dqReqTrans = DataRequirementsProcessor()
            val expressions = mutableSetOf("Negative PCP Screenings Count Since Last POS")
            val moduleDefinitionLibrary =
                dqReqTrans.gatherDataRequirements(
                    setup.manager,
                    setup.library,
                    cqlTranslatorOptions,
                    expressions,
                    false,
                )
            Assertions.assertNotNull(moduleDefinitionLibrary)
            // OpioidCDSCommon
            var ra: RelatedArtifact? =
                getDependency(
                    moduleDefinitionLibrary,
                    "http://fhir.org/guides/cdc/opioid-cds/Library/OpioidCDSCommon|1.2.3",
                )
            Assertions.assertNotNull(
                ra,
                "Expected depends-on http://fhir.org/guides/cdc/opioid-cds/Library/OpioidCDSCommon|1.2.3",
            )

            // FHIRHelpers
            ra =
                getDependency(
                    moduleDefinitionLibrary,
                    "http://hl7.org/fhir/Library/FHIRHelpers|3.0.0",
                )
            Assertions.assertNotNull(
                ra,
                "Expected depends-on http://hl7.org/fhir/Library/FHIRHelpers|3.0.0",
            )

            // depends-on
            // http://fhir.org/guides/cdc/opioid-cds/ValueSet/observation-category-laboratory
            ra =
                getDependency(
                    moduleDefinitionLibrary,
                    "http://fhir.org/guides/cdc/opioid-cds/ValueSet/observation-category-laboratory",
                )
            Assertions.assertNotNull(
                ra,
                "Expected depends-on http://fhir.org/guides/cdc/opioid-cds/ValueSet/observation-category-laboratory",
            )

            // depends-on http://fhir.org/guides/cdc/opioid-cds/ValueSet/pcp-medications
            ra =
                getDependency(
                    moduleDefinitionLibrary,
                    "http://fhir.org/guides/cdc/opioid-cds/ValueSet/pcp-medications",
                )
            Assertions.assertNotNull(
                ra,
                "Expected depends-on http://fhir.org/guides/cdc/opioid-cds/ValueSet/pcp-medications",
            )

            // parameter "Negative PCP Screenings Count Since Last POS": integer
            Assertions.assertEquals(1, moduleDefinitionLibrary.getParameter().size)
            val pd = moduleDefinitionLibrary.getParameter()[0]
            assertEquals("Negative PCP Screenings Count Since Last POS", pd.getName())
            assertEquals(FHIRTypes.INTEGER, pd.getType())

            // dataRequirement Observation {
            //   ms: { code, category, value, status, status.value, effective },
            //   codeFilter {
            //     { category in
            // http://fhir.org/guides/cdc/opioid-cds/ValueSet/observation-category-laboratory },
            //     { code in http://fhir.org/guides/cdc/opioid-cds/ValueSet/pcp-medications }
            //   }
            // }
            Assertions.assertEquals(1, moduleDefinitionLibrary.getDataRequirement().size)
            val dr = moduleDefinitionLibrary.getDataRequirement()[0]
            assertEquals(FHIRTypes.OBSERVATION, dr.getType())
            Assertions.assertEquals(6, dr.getMustSupport().size)
            Assertions.assertEquals(
                1,
                dr.getMustSupport()
                    .stream()
                    .filter { x: StringType? -> x!!.value == "code" }
                    .count(),
            )
            Assertions.assertEquals(
                1,
                dr.getMustSupport()
                    .stream()
                    .filter { x: StringType? -> x!!.value == "category" }
                    .count(),
            )
            Assertions.assertEquals(
                1,
                dr.getMustSupport()
                    .stream()
                    .filter { x: StringType? -> x!!.value == "value" }
                    .count(),
            )
            Assertions.assertEquals(
                1,
                dr.getMustSupport()
                    .stream()
                    .filter { x: StringType? -> x!!.value == "status" }
                    .count(),
            )
            Assertions.assertEquals(
                1,
                dr.getMustSupport()
                    .stream()
                    .filter { x: StringType? -> x!!.value == "status.value" }
                    .count(),
            )
            Assertions.assertEquals(
                1,
                dr.getMustSupport()
                    .stream()
                    .filter { x: StringType? -> x!!.value == "effective" }
                    .count(),
            )
            Assertions.assertEquals(2, dr.getCodeFilter().size)
            var cf: DataRequirement.DataRequirementCodeFilterComponent? = null
            for (drcf in dr.getCodeFilter()) {
                if (
                    drcf.getPath() == "category" &&
                        (drcf.getValueSet() ==
                            "http://fhir.org/guides/cdc/opioid-cds/ValueSet/observation-category-laboratory")
                ) {
                    cf = drcf
                    break
                }
            }
            Assertions.assertNotNull(cf, "Expected filter on category")

            cf = null
            for (drcf in dr.getCodeFilter()) {
                if (
                    drcf.getPath() == "code" &&
                        (drcf.getValueSet() ==
                            "http://fhir.org/guides/cdc/opioid-cds/ValueSet/pcp-medications")
                ) {
                    cf = drcf
                    break
                }
            }
            Assertions.assertNotNull(cf, "Expected filter on code")
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    @Test
    fun dataRequirementsProcessorOpioidIssueLibrary() {
        val cqlTranslatorOptions = CqlCompilerOptions()
        cqlTranslatorOptions.collapseDataRequirements = true
        cqlTranslatorOptions.analyzeDataRequirements = true
        try {
            val ni = NamespaceInfo("fhir.cdc.opioid-cds", "http://fhir.org/guides/cdc/opioid-cds")
            val setup: Setup =
                setup(ni, "OpioidCDSSTU3/cql/OpioidCDSREC10.cql", cqlTranslatorOptions)
            val dqReqTrans = DataRequirementsProcessor()
            val moduleDefinitionLibrary =
                dqReqTrans.gatherDataRequirements(
                    setup.manager,
                    setup.library,
                    cqlTranslatorOptions,
                    null,
                    false,
                )
            Assertions.assertNotNull(moduleDefinitionLibrary)
            // FHIR-ModelInfo
            var ra: RelatedArtifact? =
                getDependency(
                    moduleDefinitionLibrary,
                    "http://fhir.org/guides/cqf/common/Library/FHIR-ModelInfo|3.0.0",
                )
            Assertions.assertNotNull(
                ra,
                "Expected depends-on http://fhir.org/guides/cqf/common/Library/FHIR-ModelInfo|3.0.0",
            )

            // FHIRHelpers
            ra =
                getDependency(
                    moduleDefinitionLibrary,
                    "http://hl7.org/fhir/Library/FHIRHelpers|3.0.0",
                )
            Assertions.assertNotNull(
                ra,
                "Expected depends-on http://hl7.org/fhir/Library/FHIRHelpers|3.0.0",
            )

            // OpioidCDSCommon
            ra =
                getDependency(
                    moduleDefinitionLibrary,
                    "http://fhir.org/guides/cdc/opioid-cds/Library/OpioidCDSCommon|1.2.3",
                )
            Assertions.assertNotNull(
                ra,
                "Expected depends-on http://fhir.org/guides/cdc/opioid-cds/Library/OpioidCDSCommon|1.2.3",
            )

            // OpioidCDSRoutines
            ra =
                getDependency(
                    moduleDefinitionLibrary,
                    "http://fhir.org/guides/cdc/opioid-cds/Library/OpioidCDSRoutines|1.2.3",
                )
            Assertions.assertNotNull(
                ra,
                "Expected depends-on http://fhir.org/guides/cdc/opioid-cds/Library/OpioidCDSRoutines|1.2.3",
            )

            // OpioidCDSCommonConfig
            ra =
                getDependency(
                    moduleDefinitionLibrary,
                    "http://fhir.org/guides/cdc/opioid-cds/Library/OpioidCDSCommonConfig|1.2.3",
                )
            Assertions.assertNotNull(
                ra,
                "Expected depends-on http://fhir.org/guides/cdc/opioid-cds/Library/OpioidCDSCommonConfig|1.2.3",
            )

            // depends-on
            // http://fhir.org/guides/cdc/opioid-cds/ValueSet/observation-category-laboratory
            ra =
                getDependency(
                    moduleDefinitionLibrary,
                    "http://fhir.org/guides/cdc/opioid-cds/ValueSet/observation-category-laboratory",
                )
            Assertions.assertNotNull(
                ra,
                "Expected depends-on http://fhir.org/guides/cdc/opioid-cds/ValueSet/observation-category-laboratory",
            )

            // depends-on http://fhir.org/guides/cdc/opioid-cds/ValueSet/pcp-medications
            ra =
                getDependency(
                    moduleDefinitionLibrary,
                    "http://fhir.org/guides/cdc/opioid-cds/ValueSet/pcp-medications",
                )
            Assertions.assertNotNull(
                ra,
                "Expected depends-on http://fhir.org/guides/cdc/opioid-cds/ValueSet/pcp-medications",
            )

            // depends-on http://fhir.org/guides/cdc/opioid-cds/ValueSet/cocaine-medications
            ra =
                getDependency(
                    moduleDefinitionLibrary,
                    "http://fhir.org/guides/cdc/opioid-cds/ValueSet/cocaine-medications",
                )
            Assertions.assertNotNull(
                ra,
                "Expected depends-on http://fhir.org/guides/cdc/opioid-cds/ValueSet/cocaine-medications",
            )

            // parameter "Negative PCP Screenings Count Since Last POS": integer
            var pd = getParameter(moduleDefinitionLibrary, "ContextPrescriptions")
            Assertions.assertNotNull(pd, "Expected parameter ContextPrescriptions")
            assertEquals(FHIRTypes.MEDICATIONREQUEST, pd!!.getType())
            assertEquals(Enumerations.OperationParameterUse.IN, pd.getUse())
            assertEquals("*", pd.getMax())

            pd = getParameter(moduleDefinitionLibrary, "Patient")
            Assertions.assertNotNull(pd, "Expected parameter Patient")
            assertEquals(FHIRTypes.PATIENT, pd!!.getType())
            assertEquals(Enumerations.OperationParameterUse.OUT, pd.getUse())
            assertEquals("1", pd.getMax())

            pd = getParameter(moduleDefinitionLibrary, "Lookback Year")
            Assertions.assertNotNull(pd, "Expected parameter Lookback Year")
            assertEquals(FHIRTypes.PERIOD, pd!!.getType())
            assertEquals(Enumerations.OperationParameterUse.OUT, pd.getUse())
            assertEquals("1", pd.getMax())

            pd = getParameter(moduleDefinitionLibrary, "PCP Screenings")
            Assertions.assertNotNull(pd, "Expected parameter PCP Screenings")
            assertEquals(FHIRTypes.OBSERVATION, pd!!.getType())
            assertEquals(Enumerations.OperationParameterUse.OUT, pd.getUse())
            assertEquals("*", pd.getMax())

            pd = getParameter(moduleDefinitionLibrary, "Positive PCP Screenings")
            Assertions.assertNotNull(pd, "Expected parameter Positive PCP Screenings")
            assertEquals(FHIRTypes.OBSERVATION, pd!!.getType())
            assertEquals(Enumerations.OperationParameterUse.OUT, pd.getUse())
            assertEquals("*", pd.getMax())

            pd = getParameter(moduleDefinitionLibrary, "Negative PCP Screenings")
            Assertions.assertNotNull(pd, "Expected parameter Negative PCP Screenings")
            assertEquals(FHIRTypes.OBSERVATION, pd!!.getType())
            assertEquals(Enumerations.OperationParameterUse.OUT, pd.getUse())
            assertEquals("*", pd.getMax())

            pd =
                getParameter(
                    moduleDefinitionLibrary,
                    "Negative PCP Screenings Count Since Last POS",
                )
            Assertions.assertNotNull(
                pd,
                "Expected parameter Negative PCP Screenings Count Since Last POS",
            )
            assertEquals(FHIRTypes.INTEGER, pd!!.getType())
            assertEquals(Enumerations.OperationParameterUse.OUT, pd.getUse())
            assertEquals("1", pd.getMax())

            pd = getParameter(moduleDefinitionLibrary, "Positive PCP Dates in Lookback Period")
            Assertions.assertNotNull(pd, "Expected parameter Positive PCP Dates in Lookback Period")
            assertEquals(FHIRTypes.STRING, pd!!.getType())
            assertEquals(Enumerations.OperationParameterUse.OUT, pd.getUse())
            assertEquals("*", pd.getMax())

            pd =
                getParameter(
                    moduleDefinitionLibrary,
                    "Has Positive Screening for PCP in Last 12 Months",
                )
            Assertions.assertNotNull(
                pd,
                "Expected parameter Has Positive Screening for PCP in Last 12 Months",
            )
            assertEquals(FHIRTypes.BOOLEAN, pd!!.getType())
            assertEquals(Enumerations.OperationParameterUse.OUT, pd.getUse())
            assertEquals("1", pd.getMax())

            pd = getParameter(moduleDefinitionLibrary, "PCP Summary")
            Assertions.assertNotNull(pd, "Expected parameter PCPSummary")
            assertEquals(FHIRTypes.STRING, pd!!.getType())
            assertEquals(Enumerations.OperationParameterUse.OUT, pd.getUse())
            assertEquals("1", pd.getMax())

            // dataRequirement Observation {
            //   ms: { code, category, value, status, status.value, effective },
            //   codeFilter {
            //     { category in
            // http://fhir.org/guides/cdc/opioid-cds/ValueSet/observation-category-laboratory },
            //     { code in http://fhir.org/guides/cdc/opioid-cds/ValueSet/pcp-medications }
            //   }
            // }
            Assertions.assertEquals(2, moduleDefinitionLibrary.getDataRequirement().size)
            var dr: DataRequirement? = null
            for (r in moduleDefinitionLibrary.getDataRequirement()) {
                if (r.getType() == FHIRTypes.OBSERVATION) {
                    dr = r
                    break
                }
            }
            Assertions.assertNotNull(dr)
            assertEquals(FHIRTypes.OBSERVATION, dr!!.getType())
            Assertions.assertEquals(6, dr.getMustSupport().size)
            Assertions.assertEquals(
                1,
                dr.getMustSupport()
                    .stream()
                    .filter { x: StringType? -> x!!.value == "code" }
                    .count(),
            )
            Assertions.assertEquals(
                1,
                dr.getMustSupport()
                    .stream()
                    .filter { x: StringType? -> x!!.value == "category" }
                    .count(),
            )
            Assertions.assertEquals(
                1,
                dr.getMustSupport()
                    .stream()
                    .filter { x: StringType? -> x!!.value == "value" }
                    .count(),
            )
            Assertions.assertEquals(
                1,
                dr.getMustSupport()
                    .stream()
                    .filter { x: StringType? -> x!!.value == "status" }
                    .count(),
            )
            Assertions.assertEquals(
                1,
                dr.getMustSupport()
                    .stream()
                    .filter { x: StringType? -> x!!.value == "status.value" }
                    .count(),
            )
            Assertions.assertEquals(
                1,
                dr.getMustSupport()
                    .stream()
                    .filter { x: StringType? -> x!!.value == "effective" }
                    .count(),
            )
            Assertions.assertEquals(2, dr.getCodeFilter().size)
            var cf: DataRequirement.DataRequirementCodeFilterComponent? = null
            for (drcf in dr.getCodeFilter()) {
                if (
                    drcf.getPath() == "category" &&
                        (drcf.getValueSet() ==
                            "http://fhir.org/guides/cdc/opioid-cds/ValueSet/observation-category-laboratory")
                ) {
                    cf = drcf
                    break
                }
            }
            Assertions.assertNotNull(cf, "Expected filter on category")

            cf = null
            for (drcf in dr.getCodeFilter()) {
                if (
                    drcf.getPath() == "code" &&
                        (drcf.getValueSet() ==
                            "http://fhir.org/guides/cdc/opioid-cds/ValueSet/pcp-medications")
                ) {
                    cf = drcf
                    break
                }
            }
            Assertions.assertNotNull(cf, "Expected filter on code")
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    @Test
    fun dataRequirementsProcessorWithExpressions() {
        val cqlTranslatorOptions = CqlCompilerOptions()
        try {
            // TODO - add expressions to expressions
            val expressions =
                mutableSetOf(
                    "Conditions Indicating End of Life or With Limited Life Expectancy"
                ) // Active Ambulatory Opioid Rx");
            val setup: Setup = setup("OpioidCDS/cql/OpioidCDSCommon.cql", cqlTranslatorOptions)

            val dqReqTrans = DataRequirementsProcessor()
            val moduleDefinitionLibrary =
                dqReqTrans.gatherDataRequirements(
                    setup.manager,
                    setup.library,
                    cqlTranslatorOptions,
                    expressions,
                    false,
                )
            assertTrue(
                moduleDefinitionLibrary
                    .getType()
                    .getCode("http://terminology.hl7.org/CodeSystem/library-type")
                    .equals("module-definition", ignoreCase = true)
            )

            val directReferenceCodes =
                moduleDefinitionLibrary.getExtensionsByUrl(
                    "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-directReferenceCode"
                )
            Assertions.assertEquals(4, directReferenceCodes.size)
            val directReferenceCode = directReferenceCodes[0]
            val coding = directReferenceCode.valueCoding
            assertEquals("http://hl7.org/fhir/condition-category", coding.getSystem())
            assertEquals("encounter-diagnosis", coding.getCode())
            assertEquals("Encounter Diagnosis", coding.getDisplay())

            Assertions.assertEquals(7, moduleDefinitionLibrary.getRelatedArtifact().size)
            var conditionCategoryCodes: RelatedArtifact? = null
            for (relatedArtifact in moduleDefinitionLibrary.getRelatedArtifact()) {
                if (
                    relatedArtifact.getType() == RelatedArtifact.RelatedArtifactType.DEPENDSON &&
                        relatedArtifact.getResource() != null &&
                        relatedArtifact.getResource() == "http://hl7.org/fhir/condition-category"
                ) {
                    conditionCategoryCodes = relatedArtifact
                    break
                }
            }
            assertTrue(conditionCategoryCodes != null)

            Assertions.assertEquals(1, moduleDefinitionLibrary.getParameter().size)
            var conditionsIndicatingEndOfLife: ParameterDefinition? = null
            for (parameter in moduleDefinitionLibrary.getParameter()) {
                if (
                    parameter.getName() ==
                        "Conditions Indicating End of Life or With Limited Life Expectancy"
                ) {
                    conditionsIndicatingEndOfLife = parameter
                    break
                }
            }
            assertTrue(conditionsIndicatingEndOfLife != null)

            Assertions.assertEquals(3, moduleDefinitionLibrary.getDataRequirement().size)
            var diagnosisRequirement: DataRequirement? = null
            for (requirement in moduleDefinitionLibrary.getDataRequirement()) {
                if (
                    requirement.getType() == FHIRTypes.CONDITION &&
                        requirement.getCodeFilter().size == 1
                ) {
                    val cfc = requirement.codeFilterFirstRep
                    if (
                        cfc.hasPath() &&
                            cfc.getPath() == "category" &&
                            cfc.getCode().size == 1 &&
                            cfc.codeFirstRep.hasCode() &&
                            cfc.codeFirstRep.getCode() == "encounter-diagnosis"
                    ) {
                        diagnosisRequirement = requirement
                        break
                    }
                }
            }
            assertTrue(diagnosisRequirement != null)

            val context: FhirContext = fhirContext
            val parser = context.newJsonParser()
            val moduleDefString =
                parser.setPrettyPrint(true).encodeResourceToString(moduleDefinitionLibrary)
            logger.debug(moduleDefString)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    @Test
    fun libraryDataRequirements() {
        val cqlTranslatorOptions = CqlCompilerOptions()

        try {
            //            CqlTranslator translator =
            // createTranslator("/ecqm/resources/library-EXM506-2.2.000.json",
            // cqlTranslatorOptions);
            val setup: Setup = setup("CompositeMeasures/cql/BCSComponent.cql", cqlTranslatorOptions)

            val dqReqTrans = DataRequirementsProcessor()
            val moduleDefinitionLibrary =
                dqReqTrans.gatherDataRequirements(
                    setup.manager,
                    setup.library,
                    cqlTranslatorOptions,
                    null,
                    false,
                )
            assertTrue(
                moduleDefinitionLibrary
                    .getType()
                    .getCode("http://terminology.hl7.org/CodeSystem/library-type")
                    .equals("module-definition", ignoreCase = true)
            )

            val directReferenceCodes =
                moduleDefinitionLibrary.getExtensionsByUrl(
                    "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-directReferenceCode"
                )
            Assertions.assertEquals(5, directReferenceCodes.size)
            val directReferenceCode = directReferenceCodes[0]
            val coding = directReferenceCode.valueCoding
            assertEquals("http://loinc.org", coding.getSystem())
            assertEquals("21112-8", coding.getCode())
            assertEquals("Birth date", coding.getDisplay())

            assertTrue(moduleDefinitionLibrary.getRelatedArtifact().size >= 45)
            var loincCodeSystem: RelatedArtifact? = null
            for (relatedArtifact in moduleDefinitionLibrary.getRelatedArtifact()) {
                if (
                    relatedArtifact.getType() == RelatedArtifact.RelatedArtifactType.DEPENDSON &&
                        relatedArtifact.getResource() != null &&
                        relatedArtifact.getResource() == "http://loinc.org"
                ) {
                    loincCodeSystem = relatedArtifact
                    break
                }
            }
            assertTrue(loincCodeSystem != null)

            assertTrue(moduleDefinitionLibrary.getParameter().size >= 16)
            var measurementPeriod: ParameterDefinition? = null
            for (parameter in moduleDefinitionLibrary.getParameter()) {
                if (parameter.getName() == "Measurement Period") {
                    measurementPeriod = parameter
                    break
                }
            }
            assertTrue(measurementPeriod != null)

            assertTrue(moduleDefinitionLibrary.getDataRequirement().size >= 15)
            var diagnosisRequirement: DataRequirement? = null
            for (requirement in moduleDefinitionLibrary.getDataRequirement()) {
                if (
                    requirement.getType() == FHIRTypes.CONDITION &&
                        requirement.getCodeFilter().size == 1
                ) {
                    val cfc = requirement.codeFilterFirstRep
                    if (
                        cfc.hasPath() &&
                            cfc.getPath() == "code" &&
                            cfc.hasValueSet() &&
                            (cfc.getValueSet() ==
                                "http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.198.12.1071")
                    ) {
                        diagnosisRequirement = requirement
                        break
                    }
                }
            }
            assertTrue(diagnosisRequirement != null)

            val context: FhirContext = fhirContext
            val parser = context.newJsonParser()
            val moduleDefString =
                parser.setPrettyPrint(true).encodeResourceToString(moduleDefinitionLibrary)
            logger.debug(moduleDefString)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    @Test
    fun libraryDataRequirementsRecursive() {
        val cqlTranslatorOptions = CqlCompilerOptions()
        cqlTranslatorOptions.collapseDataRequirements = true
        try {
            val setup: Setup =
                setup("DataRequirements/DataRequirementsLibraryTest.cql", cqlTranslatorOptions)

            val dqReqTrans = DataRequirementsProcessor()
            val moduleDefinitionLibrary =
                dqReqTrans.gatherDataRequirements(
                    setup.manager,
                    setup.library,
                    cqlTranslatorOptions,
                    null,
                    false,
                )
            assertTrue(
                moduleDefinitionLibrary
                    .getType()
                    .getCode("http://terminology.hl7.org/CodeSystem/library-type")
                    .equals("module-definition", ignoreCase = true)
            )
            var encounterRequirement: DataRequirement? = null
            for (dr in moduleDefinitionLibrary.getDataRequirement()) {
                if (dr.getType() == FHIRTypes.ENCOUNTER) {
                    encounterRequirement = dr
                    break
                }
            }
            assertTrue(encounterRequirement != null)

            val context: FhirContext = fhirContext
            val parser = context.newJsonParser()
            val moduleDefString =
                parser.setPrettyPrint(true).encodeResourceToString(moduleDefinitionLibrary)
            logger.debug(moduleDefString)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    @Test
    fun fhirHelpersDataRequirementsNonRecursiveWithWorkaround() {
        val cqlTranslatorOptions = CqlCompilerOptions()
        cqlTranslatorOptions.collapseDataRequirements = true
        try {
            val setup: Setup = setup("CMS135/cql/FHIRHelpers-4.4.000.cql", cqlTranslatorOptions)

            val dqReqTrans = DataRequirementsProcessor()
            val expressions = mutableSetOf<String>()
            val externalFunctionDefs = mutableSetOf<String>()
            for (ed in setup.library.library!!.statements!!.def) {
                if (ed is FunctionDef) {
                    if (ed.isExternal() != null && ed.isExternal() == true) {
                        externalFunctionDefs.add(ed.name!!)
                    }
                }
                expressions.add(ed.name!!)
            }

            for (efd in externalFunctionDefs) {
                expressions.remove(efd)
            }

            val moduleDefinitionLibrary =
                dqReqTrans.gatherDataRequirements(
                    setup.manager,
                    setup.library,
                    cqlTranslatorOptions,
                    expressions,
                    includeLogicDefinitions = true,
                    recursive = false,
                )

            // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
            assertEqualToExpectedModuleDefinitionLibrary(
                moduleDefinitionLibrary,
                "CMS135/resources/Library-FHIRHelpersWorkAround-EffectiveDataRequirements.json",
            )
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    @Test
    fun fhirHelpersDataRequirementsNonRecursive() {
        val cqlTranslatorOptions = CqlCompilerOptions()
        cqlTranslatorOptions.collapseDataRequirements = true
        try {
            val setup: Setup = setup("CMS135/cql/FHIRHelpers-4.4.000.cql", cqlTranslatorOptions)

            val dqReqTrans = DataRequirementsProcessor()
            val expressions =
                setup.library.library!!.statements!!.def.map { it.name!! }.toMutableSet()
            val moduleDefinitionLibrary =
                dqReqTrans.gatherDataRequirements(
                    setup.manager,
                    setup.library,
                    cqlTranslatorOptions,
                    expressions,
                    includeLogicDefinitions = true,
                    recursive = false,
                )

            // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
            assertEqualToExpectedModuleDefinitionLibrary(
                moduleDefinitionLibrary,
                "CMS135/resources/Library-FHIRHelpers-EffectiveDataRequirements.json",
            )
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    @Test
    fun cms135DataRequirementsNonRecursive() {
        val cqlTranslatorOptions = CqlCompilerOptions()
        cqlTranslatorOptions.collapseDataRequirements = true
        try {
            val setup: Setup = setup("CMS135/cql/CMS135FHIR-0.0.000.cql", cqlTranslatorOptions)

            val dqReqTrans = DataRequirementsProcessor()
            val moduleDefinitionLibrary =
                dqReqTrans.gatherDataRequirements(
                    setup.manager,
                    setup.library,
                    cqlTranslatorOptions,
                    null,
                    includeLogicDefinitions = true,
                    recursive = false,
                )

            // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
            assertEqualToExpectedModuleDefinitionLibrary(
                moduleDefinitionLibrary,
                "CMS135/resources/Library-EffectiveDataRequirements.json",
            )
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    @Test
    fun libraryDataRequirementsNonRecursive() {
        val cqlTranslatorOptions = defaultOptions()
        cqlTranslatorOptions.collapseDataRequirements = true
        try {
            val setup: Setup =
                setup("DataRequirements/DataRequirementsLibraryTest.cql", cqlTranslatorOptions)

            val dqReqTrans = DataRequirementsProcessor()
            val moduleDefinitionLibrary =
                dqReqTrans.gatherDataRequirements(
                    setup.manager,
                    setup.library,
                    cqlTranslatorOptions,
                    null,
                    includeLogicDefinitions = false,
                    recursive = false,
                )
            assertTrue(
                moduleDefinitionLibrary
                    .getType()
                    .getCode("http://terminology.hl7.org/CodeSystem/library-type")
                    .equals("module-definition", ignoreCase = true)
            )
            var encounterRequirement: DataRequirement? = null
            for (dr in moduleDefinitionLibrary.getDataRequirement()) {
                if (dr.getType() == FHIRTypes.ENCOUNTER) {
                    encounterRequirement = dr
                    break
                }
            }
            assertTrue(encounterRequirement == null)

            val context: FhirContext = fhirContext
            val parser = context.newJsonParser()
            val moduleDefString =
                parser.setPrettyPrint(true).encodeResourceToString(moduleDefinitionLibrary)
            logger.debug(moduleDefString)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    @Test
    fun dataRequirementsFHIRReferences() {
        val cqlTranslatorOptions = CqlCompilerOptions()

        try {
            val setup: Setup = setup("FHIRReferencesRevisited.cql", cqlTranslatorOptions)

            val dqReqTrans = DataRequirementsProcessor()
            val moduleDefinitionLibrary =
                dqReqTrans.gatherDataRequirements(
                    setup.manager,
                    setup.library,
                    cqlTranslatorOptions,
                    null,
                    false,
                )

            val context: FhirContext = fhirContext
            val parser = context.newJsonParser()
            val moduleDefString =
                parser.setPrettyPrint(true).encodeResourceToString(moduleDefinitionLibrary)
            logger.debug(moduleDefString)
            // TODO: Validate consolidation of requirements
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    private val compilerOptions: CqlCompilerOptions
        get() = CqlCompilerOptions()

    @Throws(IOException::class)
    private fun setupUncollapsedDataRequirementsGather(
        fileName: String,
        cqlTranslatorOptions: CqlCompilerOptions,
    ): Setup {
        cqlTranslatorOptions.collapseDataRequirements = false
        cqlTranslatorOptions.analyzeDataRequirements = false
        return setup(fileName, cqlTranslatorOptions)
    }

    @Throws(IOException::class)
    private fun setupUncollapsedDataRequirementsGather(
        namespace: NamespaceInfo?,
        fileName: String,
        cqlTranslatorOptions: CqlCompilerOptions,
    ): Setup {
        cqlTranslatorOptions.collapseDataRequirements = false
        cqlTranslatorOptions.analyzeDataRequirements = false
        return setup(namespace, fileName, cqlTranslatorOptions)
    }

    @Throws(IOException::class)
    private fun setupUncollapsedDataRequirementsAnalysis(
        fileName: String,
        cqlTranslatorOptions: CqlCompilerOptions,
    ): Setup {
        cqlTranslatorOptions.collapseDataRequirements = false
        cqlTranslatorOptions.analyzeDataRequirements = true
        return setup(fileName, cqlTranslatorOptions)
    }

    @Throws(IOException::class)
    private fun setupUncollapsedDataRequirementsAnalysis(
        namespace: NamespaceInfo?,
        fileName: String,
        cqlTranslatorOptions: CqlCompilerOptions,
    ): Setup {
        cqlTranslatorOptions.collapseDataRequirements = false
        cqlTranslatorOptions.analyzeDataRequirements = true
        return setup(namespace, fileName, cqlTranslatorOptions)
    }

    @Throws(IOException::class)
    private fun setupDataRequirementsGather(
        fileName: String,
        cqlTranslatorOptions: CqlCompilerOptions,
    ): Setup {
        cqlTranslatorOptions.collapseDataRequirements = true
        cqlTranslatorOptions.analyzeDataRequirements = false
        return setup(fileName, cqlTranslatorOptions)
    }

    @Throws(IOException::class)
    private fun setupDataRequirementsGather(
        namespace: NamespaceInfo?,
        fileName: String,
        cqlTranslatorOptions: CqlCompilerOptions,
    ): Setup {
        cqlTranslatorOptions.collapseDataRequirements = true
        cqlTranslatorOptions.analyzeDataRequirements = false
        return setup(namespace, fileName, cqlTranslatorOptions)
    }

    @Throws(IOException::class)
    private fun setupDataRequirementsAnalysis(
        fileName: String,
        cqlTranslatorOptions: CqlCompilerOptions,
    ): Setup {
        cqlTranslatorOptions.collapseDataRequirements = true
        cqlTranslatorOptions.analyzeDataRequirements = true
        return setup(fileName, cqlTranslatorOptions)
    }

    @Throws(IOException::class)
    private fun setupDataRequirementsAnalysis(
        namespace: NamespaceInfo?,
        fileName: String,
        cqlTranslatorOptions: CqlCompilerOptions,
    ): Setup {
        cqlTranslatorOptions.collapseDataRequirements = true
        cqlTranslatorOptions.analyzeDataRequirements = true
        return setup(namespace, fileName, cqlTranslatorOptions)
    }

    private fun getModuleDefinitionLibrary(
        setup: Setup,
        cqlTranslatorOptions: CqlCompilerOptions,
        parameters: MutableMap<String, Any?>?,
    ): Library {
        val dqReqTrans = DataRequirementsProcessor()
        val moduleDefinitionLibrary =
            dqReqTrans.gatherDataRequirements(
                setup.manager,
                setup.library,
                cqlTranslatorOptions,
                null,
                parameters,
                includeLogicDefinitions = false,
                recursive = false,
            )
        assertTrue(
            moduleDefinitionLibrary
                .getType()
                .getCode("http://terminology.hl7.org/CodeSystem/library-type")
                .equals("module-definition", ignoreCase = true)
        )
        return moduleDefinitionLibrary
    }

    private fun getModuleDefinitionLibrary(
        setup: Setup,
        cqlTranslatorOptions: CqlCompilerOptions,
        parameters: MutableMap<String, Any?>?,
        evaluationDateTime: ZonedDateTime?,
    ): Library {
        val dqReqTrans = DataRequirementsProcessor()
        val moduleDefinitionLibrary =
            dqReqTrans.gatherDataRequirements(
                setup.manager,
                setup.library,
                cqlTranslatorOptions,
                null,
                parameters,
                evaluationDateTime,
                includeLogicDefinitions = false,
                recursive = true,
            )
        assertEquals("EffectiveDataRequirements", moduleDefinitionLibrary.getName())
        assertTrue(
            moduleDefinitionLibrary
                .getType()
                .getCode("http://terminology.hl7.org/CodeSystem/library-type")
                .equals("module-definition", ignoreCase = true)
        )
        return moduleDefinitionLibrary
    }

    private fun getModuleDefinitionLibrary(
        setup: Setup,
        cqlTranslatorOptions: CqlCompilerOptions,
        parameters: MutableMap<String, Any?>?,
        evaluationDateTime: ZonedDateTime?,
        includeLogicDefinitions: Boolean,
    ): Library {
        val dqReqTrans = DataRequirementsProcessor()
        val moduleDefinitionLibrary =
            dqReqTrans.gatherDataRequirements(
                setup.manager,
                setup.library,
                cqlTranslatorOptions,
                null,
                parameters,
                evaluationDateTime,
                includeLogicDefinitions,
                false,
            )
        assertTrue(
            moduleDefinitionLibrary
                .getType()
                .getCode("http://terminology.hl7.org/CodeSystem/library-type")
                .equals("module-definition", ignoreCase = true)
        )
        return moduleDefinitionLibrary
    }

    @Suppress("LongParameterList")
    private fun getModuleDefinitionLibrary(
        setup: Setup,
        cqlTranslatorOptions: CqlCompilerOptions,
        parameters: MutableMap<String, Any?>?,
        evaluationDateTime: ZonedDateTime?,
        includeLogicDefinitions: Boolean,
        recursive: Boolean,
    ): Library {
        val dqReqTrans = DataRequirementsProcessor()
        val moduleDefinitionLibrary =
            dqReqTrans.gatherDataRequirements(
                setup.manager,
                setup.library,
                cqlTranslatorOptions,
                null,
                parameters,
                evaluationDateTime,
                includeLogicDefinitions,
                recursive,
            )
        assertTrue(
            moduleDefinitionLibrary
                .getType()
                .getCode("http://terminology.hl7.org/CodeSystem/library-type")
                .equals("module-definition", ignoreCase = true)
        )
        return moduleDefinitionLibrary
    }

    private fun getModuleDefinitionLibrary(
        setup: Setup,
        cqlTranslatorOptions: CqlCompilerOptions,
    ): Library {
        val dqReqTrans = DataRequirementsProcessor()
        val moduleDefinitionLibrary =
            dqReqTrans.gatherDataRequirements(
                setup.manager,
                setup.library,
                cqlTranslatorOptions,
                null,
                false,
            )
        assertTrue(
            moduleDefinitionLibrary
                .getType()
                .getCode("http://terminology.hl7.org/CodeSystem/library-type")
                .equals("module-definition", ignoreCase = true)
        )
        return moduleDefinitionLibrary
    }

    private fun getModuleDefinitionLibrary(
        setup: Setup,
        cqlTranslatorOptions: CqlCompilerOptions,
        expressions: MutableSet<String>?,
    ): Library {
        val dqReqTrans = DataRequirementsProcessor()
        val moduleDefinitionLibrary =
            dqReqTrans.gatherDataRequirements(
                setup.manager,
                setup.library,
                cqlTranslatorOptions,
                expressions,
                false,
            )
        assertTrue(
            moduleDefinitionLibrary
                .getType()
                .getCode("http://terminology.hl7.org/CodeSystem/library-type")
                .equals("module-definition", ignoreCase = true)
        )
        return moduleDefinitionLibrary
    }

    private fun getModuleDefinitionLibrary(
        setup: Setup,
        cqlTranslatorOptions: CqlCompilerOptions,
        expressions: MutableSet<String>?,
        includeLogicDefinitions: Boolean,
    ): Library {
        val dqReqTrans = DataRequirementsProcessor()
        val moduleDefinitionLibrary =
            dqReqTrans.gatherDataRequirements(
                setup.manager,
                setup.library,
                cqlTranslatorOptions,
                expressions,
                includeLogicDefinitions,
            )
        assertTrue(
            moduleDefinitionLibrary
                .getType()
                .getCode("http://terminology.hl7.org/CodeSystem/library-type")
                .equals("module-definition", ignoreCase = true)
        )
        return moduleDefinitionLibrary
    }

    private fun getModuleDefinitionLibrary(
        setup: Setup,
        cqlTranslatorOptions: CqlCompilerOptions,
        expressions: MutableSet<String>?,
        includeLogicDefinitions: Boolean,
        recursive: Boolean,
    ): Library {
        val dqReqTrans = DataRequirementsProcessor()
        val moduleDefinitionLibrary =
            dqReqTrans.gatherDataRequirements(
                setup.manager,
                setup.library,
                cqlTranslatorOptions,
                expressions,
                includeLogicDefinitions,
                recursive,
            )
        assertTrue(
            moduleDefinitionLibrary
                .getType()
                .getCode("http://terminology.hl7.org/CodeSystem/library-type")
                .equals("module-definition", ignoreCase = true)
        )
        return moduleDefinitionLibrary
    }

    private fun getModuleDefinitionLibrary(
        setup: Setup,
        cqlTranslatorOptions: CqlCompilerOptions,
        expressions: MutableSet<String>?,
        specificationLevel: SpecificationLevel,
    ): Library {
        val dqReqTrans = DataRequirementsProcessor()
        dqReqTrans.setSpecificationLevel(specificationLevel)
        val moduleDefinitionLibrary =
            dqReqTrans.gatherDataRequirements(
                setup.manager,
                setup.library,
                cqlTranslatorOptions,
                expressions,
                false,
            )
        assertTrue(
            moduleDefinitionLibrary
                .getType()
                .getCode("http://terminology.hl7.org/CodeSystem/library-type")
                .equals("module-definition", ignoreCase = true)
        )
        return moduleDefinitionLibrary
    }

    private fun getModuleDefinitionLibrary(
        setup: Setup,
        cqlTranslatorOptions: CqlCompilerOptions,
        expressions: MutableSet<String>?,
        includeLogicDefinitions: Boolean,
        specificationLevel: SpecificationLevel,
    ): Library {
        val dqReqTrans = DataRequirementsProcessor()
        dqReqTrans.setSpecificationLevel(specificationLevel)
        val moduleDefinitionLibrary =
            dqReqTrans.gatherDataRequirements(
                setup.manager,
                setup.library,
                cqlTranslatorOptions,
                expressions,
                includeLogicDefinitions,
            )
        assertTrue(
            moduleDefinitionLibrary
                .getType()
                .getCode("http://terminology.hl7.org/CodeSystem/library-type")
                .equals("module-definition", ignoreCase = true)
        )
        return moduleDefinitionLibrary
    }

    @Suppress("LongParameterList")
    private fun getModuleDefinitionLibrary(
        setup: Setup,
        cqlTranslatorOptions: CqlCompilerOptions,
        expressions: MutableSet<String>?,
        includeLogicDefinitions: Boolean,
        recursive: Boolean,
        specificationLevel: SpecificationLevel,
    ): Library {
        val dqReqTrans = DataRequirementsProcessor()
        dqReqTrans.setSpecificationLevel(specificationLevel)
        val moduleDefinitionLibrary =
            dqReqTrans.gatherDataRequirements(
                setup.manager,
                setup.library,
                cqlTranslatorOptions,
                expressions,
                includeLogicDefinitions,
                recursive,
            )
        assertTrue(
            moduleDefinitionLibrary
                .getType()
                .getCode("http://terminology.hl7.org/CodeSystem/library-type")
                .equals("module-definition", ignoreCase = true)
        )
        return moduleDefinitionLibrary
    }

    private fun outputModuleDefinitionLibrary(moduleDefinitionLibrary: Library?) {
        val context: FhirContext = fhirContext
        val parser = context.newJsonParser()
        val moduleDefString =
            parser.setPrettyPrint(true).encodeResourceToString(moduleDefinitionLibrary)
        println(moduleDefString)
    }

    private fun getDataRequirementsForType(
        dataRequirements: Iterable<DataRequirement>,
        type: FHIRTypes?,
    ): Iterable<DataRequirement> {
        val results: MutableList<DataRequirement> = ArrayList<DataRequirement>()
        for (dr in dataRequirements) {
            if (dr.getType() == type) {
                results.add(dr)
            }
        }
        return results
    }

    @Test
    @Throws(IOException::class)
    fun functionDataRequirements() {
        val compilerOptions = this.compilerOptions
        val manager =
            setupDataRequirementsGather("CMS104/MATGlobalCommonFunctionsFHIR4.cql", compilerOptions)
        val moduleDefinitionLibrary =
            getModuleDefinitionLibrary(manager, compilerOptions, mutableSetOf("PrincipalDiagnosis"))

        // DataRequirements of the PrincipalDiagnosis function:
        // [Condition]
        val expectedDataRequirements =
            getDataRequirementsForType(
                moduleDefinitionLibrary.getDataRequirement(),
                FHIRTypes.CONDITION,
            )
        assertTrue(expectedDataRequirements.iterator().hasNext())
        // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    @Throws(IOException::class)
    fun nonElectiveInpatientEncounterDataRequirements() {
        val compilerOptions = this.compilerOptions
        val manager = setupDataRequirementsGather("CMS104/TJCOverallFHIR.cql", compilerOptions)
        val moduleDefinitionLibrary =
            getModuleDefinitionLibrary(
                manager,
                compilerOptions,
                mutableSetOf("Non Elective Inpatient Encounter"),
            )

        // DataRequirements of the Non Elective Inpatient Encounter expression:
        // [Encounter: "Non-Elective Inpatient Encounter"]
        val actualDataRequirements =
            getDataRequirementsForType(
                moduleDefinitionLibrary.getDataRequirement(),
                FHIRTypes.ENCOUNTER,
            )
        assertTrue(actualDataRequirements.iterator().hasNext())
        val dr = actualDataRequirements.iterator().next()
        var actualDrcf: DataRequirement.DataRequirementCodeFilterComponent? = null
        for (drcf in dr.getCodeFilter()) {
            if (
                "type" == drcf.getPath() &&
                    ("http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.424" ==
                        drcf.getValueSet())
            ) {
                actualDrcf = drcf
                break
            }
        }
        assertTrue(actualDrcf != null)
        // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    @Throws(IOException::class)
    fun allStrokeEncounterDataRequirements() {
        val compilerOptions = this.compilerOptions
        val manager = setupDataRequirementsGather("CMS104/TJCOverallFHIR.cql", compilerOptions)
        val moduleDefinitionLibrary =
            getModuleDefinitionLibrary(
                manager,
                compilerOptions,
                mutableSetOf("All Stroke Encounter"),
            )

        // DataRequirements of the All Stroke Encounter expression:
        // [Encounter: "Non-Elective Inpatient Encounter"]          (from Non Elective Inpatient
        // Encounter)
        // [Condition]                                              (from PrincipalDiagnosis)
        val encounterDataRequirements =
            getDataRequirementsForType(
                moduleDefinitionLibrary.getDataRequirement(),
                FHIRTypes.ENCOUNTER,
            )
        assertTrue(encounterDataRequirements.iterator().hasNext())
        val dr = encounterDataRequirements.iterator().next()
        var actualDrcf: DataRequirement.DataRequirementCodeFilterComponent? = null
        for (drcf in dr.getCodeFilter()) {
            if (
                "type" == drcf.getPath() &&
                    ("http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.424" ==
                        drcf.getValueSet())
            ) {
                actualDrcf = drcf
                break
            }
        }
        assertTrue(actualDrcf != null)

        val conditionDataRequirements =
            getDataRequirementsForType(
                moduleDefinitionLibrary.getDataRequirement(),
                FHIRTypes.CONDITION,
            )
        assertTrue(conditionDataRequirements.iterator().hasNext())

        // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    @Throws(IOException::class)
    fun cms104DataRequirements() {
        val compilerOptions = this.compilerOptions
        val manager =
            setupDataRequirementsGather(
                "CMS104/DischargedonAntithromboticTherapyFHIR.cql",
                compilerOptions,
            )
        val moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions)

        // DataRequirements of the All Stroke Encounter expression:
        // [Encounter: "Non-Elective Inpatient Encounter"]          (from Non Elective Inpatient
        // Encounter)
        // [Condition]                                              (from PrincipalDiagnosis)
        val encounterDataRequirements =
            getDataRequirementsForType(
                moduleDefinitionLibrary.getDataRequirement(),
                FHIRTypes.ENCOUNTER,
            )
        var actualDrcf: DataRequirement.DataRequirementCodeFilterComponent? = null
        for (dr in encounterDataRequirements) {
            for (drcf in dr.getCodeFilter()) {
                if (
                    "type" == drcf.getPath() &&
                        ("http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.424" ==
                            drcf.getValueSet())
                ) {
                    actualDrcf = drcf
                    break
                }
            }
            if (actualDrcf != null) {
                break
            }
        }
        assertTrue(actualDrcf != null)

        val conditionDataRequirements =
            getDataRequirementsForType(
                moduleDefinitionLibrary.getDataRequirement(),
                FHIRTypes.CONDITION,
            )
        assertTrue(conditionDataRequirements.iterator().hasNext())

        // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    @Throws(IOException::class)
    fun dataRequirementsAnalysisCase1() {
        val compilerOptions = this.compilerOptions
        val manager = setupDataRequirementsAnalysis("TestCases/TestCase1.cql", compilerOptions)
        val moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions)

        /*
        1.
        Stated DataRequirement: ERSD Observations
        type: Observation
        codeFilter: { path:  code, valueSet:  'http://fakeurl.com/ersd-diagnosis' }
        */

        // Validate the ELM is correct
        val ed = manager.library.resolveExpressionRef("ESRD Observations")
        assertTrue(ed!!.expression is Retrieve)
        val r = ed.expression as Retrieve?
        assertEquals("code", r!!.codeProperty)
        assertTrue(r.codes is ValueSetRef)
        assertEquals("ESRD Diagnosis", (r.codes as ValueSetRef).name)

        // Validate the data requirement is reported in the module definition library
        var expectedDataRequirement: DataRequirement? = null
        for (dr in moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == FHIRTypes.OBSERVATION) {
                if (dr.getCodeFilter().size == 1) {
                    val cfc = dr.codeFilterFirstRep
                    if ("code" == cfc.getPath()) {
                        if ("http://fakeurl.com/ersd-diagnosis" == cfc.getValueSet()) {
                            expectedDataRequirement = dr
                        }
                    }
                }
            }
        }
        assertTrue(expectedDataRequirement != null)

        // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    @Throws(IOException::class)
    fun dataRequirementsAnalysisCase1b() {
        val compilerOptions = this.compilerOptions
        val manager = setupDataRequirementsAnalysis("TestCases/TestCase1b.cql", compilerOptions)
        val moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions)

        /*
        1b. Similar to 1, but not on a primary code path and with a constant
        DataRequirement
        type: Observation
        codeFilter: { path: status, code: Coding { code: 'final'}}
        */

        // Validate the ELM is correct
        val ed = manager.library.resolveExpressionRef("Observations")
        assertTrue(ed!!.expression is Query)
        val q = ed.expression as Query
        assertTrue(q.source.size == 1)
        val source = q.source[0]
        assertTrue(source.expression is Retrieve)
        val r = source.expression as Retrieve
        assertTrue(r.codeProperty == null)
        assertTrue(r.codes == null)
        assertEquals(1, r.codeFilter.size)
        val cfe = r.codeFilter[0]
        assertEquals("status", cfe.property)
        assertEquals("=", cfe.comparator)
        assertTrue(cfe.value is Literal)
        assertEquals("final", (cfe.value as Literal).value)

        // Validate the data requirement is reported in the module definition library
        var expectedDataRequirement: DataRequirement? = null
        for (dr in moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == FHIRTypes.OBSERVATION) {
                if (dr.getCodeFilter().size == 1) {
                    val cfc = dr.codeFilterFirstRep
                    if ("status" == cfc.getPath()) {
                        if (cfc.getCode().size == 1) {
                            val coding = cfc.codeFirstRep
                            if ("final" == coding.getCode()) {
                                expectedDataRequirement = dr
                            }
                        }
                    }
                }
            }
        }
        assertTrue(expectedDataRequirement != null)

        // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    @Throws(IOException::class)
    fun dataRequirementsAnalysisCase1c() {
        val compilerOptions = this.compilerOptions
        val manager = setupDataRequirementsAnalysis("TestCases/TestCase1c.cql", compilerOptions)
        val expressions = mutableSetOf("TestReferencedDataRequirement")
        val moduleDefinitionLibrary =
            getModuleDefinitionLibrary(manager, compilerOptions, expressions)

        /*
        1c: Referenced data requirement
        DataRequirement
        type: Medication
        codeFilter: { path: code, valueset: http://example.org/fhir/ValueSet/aspirin }

        define TestMedicationRequirement:
          [Medication]

        // If only TestReferencedDataRequirement is referenced, the data requirement should only be [Medication: code]
        define TestReferencedDataRequirement:
          TestMedicationRequirement R
            where R.code in "Aspirin"
        */

        // Validate the TestReferencedDataRequirement is the only output parameter...
        var expectedParameterDefinition: ParameterDefinition? = null
        Assertions.assertEquals(1, moduleDefinitionLibrary.getParameter().size)
        for (pd in moduleDefinitionLibrary.getParameter()) {
            if (
                "TestReferencedDataRequirement" == pd.getName() &&
                    pd.getUse() == Enumerations.OperationParameterUse.OUT &&
                    pd.hasMin() &&
                    pd.getMin() == 0 &&
                    "*" == pd.getMax() &&
                    pd.getType() == FHIRTypes.MEDICATION
            ) {
                expectedParameterDefinition = pd
            }
        }
        assertTrue(expectedParameterDefinition != null)

        // Validate the data requirement is reported correctly in the module definition library
        var expectedDataRequirement: DataRequirement? = null
        // TODO: This really should be 1, but we're using the recursive gather, so it reports the
        // [Medication] retrieve
        // in the referenced expression as well
        Assertions.assertEquals(2, moduleDefinitionLibrary.getDataRequirement().size)
        for (dr in moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == FHIRTypes.MEDICATION) {
                if (dr.getCodeFilter().size == 1) {
                    val cfc = dr.codeFilterFirstRep
                    if ("code" == cfc.getPath()) {
                        if ("http://example.org/fhir/ValueSet/aspirin" == cfc.getValueSet()) {
                            expectedDataRequirement = dr
                            break
                        }
                    }
                }
            }
        }
        assertTrue(expectedDataRequirement != null)

        // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    @Throws(IOException::class)
    fun dataRequirementsAnalysisCase2a() {
        val compilerOptions = this.compilerOptions
        val manager = setupDataRequirementsAnalysis("TestCases/TestCase2a.cql", compilerOptions)
        val moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions)

        /*
        2a
        DataRequirement
        type: Claim
        codeFilter: { path:  item.revenue, valueSet: 'http://fakeurl.com/hospice-encounter' }

        define "HospiceEncounterClaimsA":
          [Claim] CEncounter
            where exists CEncounter.item IEncounter
              where IEncounter.revenue in "Hospice Encounter"
        */

        // Validate the ELM is correct
        val ed = manager.library.resolveExpressionRef("HospiceEncounterClaimsA")
        assertTrue(ed!!.expression is Query)
        val q = ed.expression as Query
        assertTrue(q.source.size == 1)
        val source = q.source[0]
        assertTrue(source.expression is Retrieve)
        val r = source.expression as Retrieve
        assertTrue(r.codeProperty == null)
        assertTrue(r.codes == null)
        assertEquals(1, r.codeFilter.size)
        val cfe = r.codeFilter[0]
        assertEquals("item.revenue", cfe.property)
        assertEquals("in", cfe.comparator)
        assertTrue(cfe.value is ValueSetRef)
        assertEquals("Hospice Encounter", (cfe.value as ValueSetRef).name)

        // Validate the data requirement is reported in the module definition library
        var expectedDataRequirement: DataRequirement? = null
        for (dr in moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == FHIRTypes.CLAIM) {
                if (dr.getCodeFilter().size == 1) {
                    val cfc = dr.codeFilterFirstRep
                    if ("item.revenue" == cfc.getPath()) {
                        if ("http://fakeurl.com/hospice-encounter" == cfc.getValueSet()) {
                            expectedDataRequirement = dr
                        }
                    }
                }
            }
        }
        assertTrue(expectedDataRequirement != null)

        // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    @Throws(IOException::class)
    fun dataRequirementsAnalysisCase2b() {
        val compilerOptions = this.compilerOptions
        val manager = setupDataRequirementsAnalysis("TestCases/TestCase2b.cql", compilerOptions)
        val moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions)

        /*
        2b - Bound Measurement Period
        Interval[@2019-01-01, @2020-01-01 )
        DataRequirement
        type: Claim
        dateFilter: { path:  item.serviced.start, valuePeriod: "@2019-01-01-@2020-01-01" },

        define "HospiceEncounterClaimsBBoundDate":
          [Claim] CEncounter
            where exists CEncounter.item IEncounter
              where IEncounter.serviced."start" in "Measurement Period"
        */

        // Validate the ELM is correct
        val ed = manager.library.resolveExpressionRef("HospiceEncounterClaimsBBoundDate")
        assertTrue(ed!!.expression is Query)
        val q = ed.expression as Query
        assertTrue(q.source.size == 1)
        val source = q.source[0]
        assertTrue(source.expression is Retrieve)
        val r = source.expression as Retrieve
        assertTrue(r.dateProperty == null)
        assertTrue(r.dateRange == null)
        assertEquals(1, r.dateFilter.size)
        val dfe = r.dateFilter[0]
        assertEquals("item.serviced.start", dfe.property)
        assertTrue(dfe.value is ParameterRef)
        assertEquals("Measurement Period", (dfe.value as ParameterRef).name)

        // Validate the data requirement is reported in the module definition library
        var expectedDataRequirement: DataRequirement? = null
        for (dr in moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == FHIRTypes.CLAIM) {
                if (dr.getDateFilter().size == 1) {
                    val dfc = dr.dateFilterFirstRep
                    if ("item.serviced.start" == dfc.getPath()) {
                        val e =
                            dfc.getValue()
                                .getExtensionByUrl(
                                    "http://hl7.org/fhir/StructureDefinition/cqf-expression"
                                )
                        if (e != null && e.valueExpression != null) {
                            if ("Measurement Period" == e.valueExpression.getExpression()) {
                                expectedDataRequirement = dr
                            }
                        }
                    }
                }
            }
        }
        assertTrue(expectedDataRequirement != null)

        // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    @Throws(IOException::class)
    fun dataRequirementsAnalysisCase2e() {
        val compilerOptions = this.compilerOptions
        val manager = setupDataRequirementsAnalysis("TestCases/TestCase2e.cql", compilerOptions)
        // Evaluate this test as of 12/31/2022
        val evaluationDateTime = ZonedDateTime.of(2022, 12, 31, 0, 0, 0, 0, ZoneOffset.UTC)
        val moduleDefinitionLibrary =
            getModuleDefinitionLibrary(manager, compilerOptions, mutableMapOf(), evaluationDateTime)

        /*
        2e - Timing phrase 90 days or less before
        DataRequirement
        type: Condition
        dateFilter: { path: onset, value: Interval[Today() - 90 days, Today()] }

        define "Date Filter Expression":
          [Condition] C
            where onset as Period starts 90 days or less before Today()
        */
        val ed = manager.library.resolveExpressionRef("Date Filter Expression")
        assertTrue(ed!!.expression is Query)
        val q = ed.expression as Query
        assertTrue(q.source.size == 1)
        val source = q.source[0]
        assertTrue(source.expression is Retrieve)
        val r = source.expression as Retrieve
        assertTrue(r.dateFilter.size == 1)
        val dfe = r.dateFilter[0]
        assertEquals("onset", dfe.property)
        assertTrue(dfe.value is Interval)

        val expectedPeriodStart = evaluationDateTime.toOffsetDateTime().minusDays(90)
        val expectedPeriodEnd = evaluationDateTime.toOffsetDateTime().minusNanos(1000000)
        var expectedDataRequirement: DataRequirement? = null
        var hasFilter = false
        for (dr in moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == FHIRTypes.CONDITION) {
                if (dr.getDateFilter().size == 1) {
                    for (dfc in dr.getDateFilter()) {
                        if ("onset" == dfc.getPath()) {
                            if (dfc.getValue() is Period) {
                                val expectedPeriodStartString =
                                    expectedPeriodStart
                                        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                                        .replace("T00:00:00Z", "") // "2022-10-02"
                                val expectedPeriodEndString =
                                    expectedPeriodEnd
                                        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                                        .replace("T23:59:59.999Z", "") // "2022-12-30"
                                if (
                                    (dfc.getValue() as Period).hasStart() &&
                                        ((dfc.getValue() as Period).startElement.asStringValue() ==
                                            expectedPeriodStartString) &&
                                        (dfc.getValue() as Period).hasEnd() &&
                                        ((dfc.getValue() as Period).endElement.asStringValue() ==
                                            expectedPeriodEndString)
                                ) {
                                    hasFilter = true
                                }
                            }
                        }
                    }

                    if (hasFilter) {
                        expectedDataRequirement = dr
                    }
                }
            }
        }
        assertTrue(expectedDataRequirement != null)
    }

    @Test
    @Throws(IOException::class)
    fun dataRequirementsAnalysisCase2g() {
        val compilerOptions = this.compilerOptions
        val manager = setupDataRequirementsAnalysis("TestCases/TestCase2g.cql", compilerOptions)
        val moduleDefinitionLibrary =
            getModuleDefinitionLibrary(manager, compilerOptions, mutableMapOf())

        /*
        2g - Equal to a compile-time literal function
        DataRequirement
        type: Condition
        dateFilter: { path: onset, value: Today() }

        define DateTimeEqualToFunction:
          [Condition] C
            where C.onset as dateTime = Today()
        */
        val ed = manager.library.resolveExpressionRef("DateTimeEqualToFunction")
        assertTrue(ed!!.expression is Query)
        val q = ed.expression as Query
        assertTrue(q.source.size == 1)
        val source = q.source[0]
        assertTrue(source.expression is Retrieve)
        val r = source.expression as Retrieve
        assertTrue(r.dateFilter.size == 1)
        val dfe = r.dateFilter[0]
        assertEquals("onset", dfe.property)
        assertTrue(dfe.value is Interval)

        var expectedDataRequirement: DataRequirement? = null
        for (dr in moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == FHIRTypes.CONDITION) {
                if (dr.getDateFilter().size == 1) {
                    val dfc = dr.dateFilterFirstRep
                    if ("onset" == dfc.getPath()) {
                        if (dfc.getValue() is Period) {
                            if (
                                (dfc.getValue() as Period).hasStart() &&
                                    (dfc.getValue() as Period).hasEnd()
                            ) {
                                expectedDataRequirement = dr
                            }
                        }
                    }
                }
            }
        }
        assertTrue(expectedDataRequirement != null)
    }

    @Test
    @Throws(IOException::class)
    fun dataRequirementsAnalysisCase2i() {
        val compilerOptions = this.compilerOptions
        val manager = setupDataRequirementsAnalysis("TestCases/TestCase2i.cql", compilerOptions)
        val moduleDefinitionLibrary =
            getModuleDefinitionLibrary(manager, compilerOptions, mutableMapOf())

        /*
        2i - In a compile-time literal interval
        DataRequirement
        type: Condition
        dateFilter: { path: onset, value: Interval[@2022-12-31 - 90 days, @2022-12-31] }

        define "Date Filter Expression":
          [Condition] C
            where C.onset as dateTime in Interval[@2022-12-31 - 90 days, @2022-12-31]
        */
        val evaluationDateTime = ZonedDateTime.of(2022, 12, 31, 0, 0, 0, 0, ZoneId.systemDefault())
        val expectedPeriodStart = evaluationDateTime.toOffsetDateTime().minusDays(90)
        val ed = manager.library.resolveExpressionRef("Date Filter Expression")
        assertTrue(ed!!.expression is Query)
        val q = ed.expression as Query
        assertTrue(q.source.size == 1)
        val source = q.source[0]
        assertTrue(source.expression is Retrieve)
        val r = source.expression as Retrieve
        assertTrue(r.dateFilter.size == 1)
        val dfe = r.dateFilter[0]
        assertEquals("onset", dfe.property)
        assertTrue(dfe.value is Interval)

        var expectedDataRequirement: DataRequirement? = null
        for (dr in moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == FHIRTypes.CONDITION) {
                if (dr.getDateFilter().size == 1) {
                    val dfc = dr.dateFilterFirstRep
                    if ("onset" == dfc.getPath()) {
                        if (dfc.getValue() is Period) {
                            val expectedPeriodStartString =
                                expectedPeriodStart.format(
                                    DateTimeFormatter.ISO_LOCAL_DATE
                                ) // "2022-10-02"
                            if (
                                (dfc.getValue() as Period).hasStart() &&
                                    (dfc.getValue() as Period).hasEnd() &&
                                    ((dfc.getValue() as Period).startElement.asStringValue() ==
                                        expectedPeriodStartString)
                            ) {
                                expectedDataRequirement = dr
                            }
                        }
                    }
                }
            }
        }
        assertTrue(expectedDataRequirement != null)
    }

    @Test
    @Throws(IOException::class)
    fun dataRequirementsAnalysisCase2j() {
        val compilerOptions = this.compilerOptions
        val manager = setupDataRequirementsAnalysis("TestCases/TestCase2j.cql", compilerOptions)
        val moduleDefinitionLibrary =
            getModuleDefinitionLibrary(manager, compilerOptions, mutableMapOf())

        /*
        2j - Before and after
        DataRequirement
        type: Condition
        dateFilter: { path: onset, value: Interval[@2022-12-31T - 90 days, @2022-12-31T] }

        define "Date Filter Expression":
            [Condition] C
                where C.onset as dateTime >= @2022-12-31T - 90 days
                    and C.onset as dateTime <= @2022-12-31T
        */
        val evaluationDateTime = ZonedDateTime.of(2022, 12, 31, 0, 0, 0, 0, ZoneId.systemDefault())
        val expectedPeriodStart1 = evaluationDateTime.toOffsetDateTime().minusDays(90)
        val expectedPeriodEnd1 =
            ZonedDateTime.of(9999, 12, 31, 23, 59, 59, 999000000, ZoneId.of("UTC"))
                .toOffsetDateTime()
        val expectedPeriodStart2 =
            ZonedDateTime.of(1, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")).toOffsetDateTime()
        val expectedPeriodEnd2 = evaluationDateTime.toOffsetDateTime()
        val ed = manager.library.resolveExpressionRef("Date Filter Expression")
        assertTrue(ed!!.expression is Query)
        val q = ed.expression as Query
        assertTrue(q.source.size == 1)
        val source = q.source[0]
        assertTrue(source.expression is Retrieve)
        val r = source.expression as Retrieve
        assertTrue(r.dateFilter.size == 2)
        var dfe = r.dateFilter[0]
        assertEquals("onset", dfe.property)
        assertTrue(dfe.value is Interval)
        dfe = r.dateFilter[1]
        assertEquals("onset", dfe.property)
        assertTrue(dfe.value is Interval)

        var expectedDataRequirement: DataRequirement? = null
        var hasFilter1 = false
        var hasFilter2 = false
        for (dr in moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == FHIRTypes.CONDITION) {
                if (dr.getDateFilter().size == 2) {
                    for (dfc in dr.getDateFilter()) {
                        if ("onset" == dfc.getPath()) {
                            if (dfc.getValue() is Period) {
                                val expectedPeriodStart1String =
                                    expectedPeriodStart1.format(
                                        DateTimeFormatter.ISO_LOCAL_DATE
                                    ) // "2022-10-02"
                                val expectedPeriodEnd1String =
                                    expectedPeriodEnd1.format(
                                        DateTimeFormatter.ISO_OFFSET_DATE_TIME
                                    ) // "9999-12-31T23:59:59.999Z"
                                val expectedPeriodStart2String =
                                    expectedPeriodStart2
                                        .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                                        .replace(":00Z", ":00.000Z") // "0001-01-01T00:00:00.000Z"
                                val expectedPeriodEnd2String =
                                    expectedPeriodEnd2.format(
                                        DateTimeFormatter.ISO_LOCAL_DATE
                                    ) // "2022-12-31"
                                if (
                                    (dfc.getValue() as Period).hasStart() &&
                                        ((dfc.getValue() as Period).startElement.asStringValue() ==
                                            expectedPeriodStart1String) &&
                                        (dfc.getValue() as Period).hasEnd() &&
                                        ((dfc.getValue() as Period).endElement.asStringValue() ==
                                            expectedPeriodEnd1String)
                                ) {
                                    hasFilter1 = true
                                } else if (
                                    (dfc.getValue() as Period).hasEnd() &&
                                        (dfc.getValue() as Period).hasStart()
                                ) {
                                    val actualPeriodStart2String =
                                        (dfc.getValue() as Period).startElement.asStringValue()
                                    val actualPeriodEnd2String =
                                        (dfc.getValue() as Period).endElement.asStringValue()
                                    if (
                                        actualPeriodStart2String == expectedPeriodStart2String &&
                                            actualPeriodEnd2String == expectedPeriodEnd2String
                                    ) {
                                        // &&
                                        // ((Period)dfc.getValue()).getEndElement().asStringValue().equals(expectedPeriodEnd2String)
                                        // &&
                                        // ((Period)dfc.getValue()).getStartElement().asStringValue().equals(expectedPeriodStart2String)
                                        hasFilter2 = true
                                    }
                                }
                            }
                        }
                    }

                    if (hasFilter1 && hasFilter2) {
                        expectedDataRequirement = dr
                    }
                }
            }
        }
        assertTrue(expectedDataRequirement != null)
    }

    @Test
    @Throws(IOException::class)
    fun dataRequirementsAnalysisCase9a() {
        val compilerOptions = this.compilerOptions
        val manager = setupDataRequirementsAnalysis("TestCases/TestCase9a.cql", compilerOptions)
        val moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions)

        /*
        Singleton element that is a reference
        [MedicationRequest.encounter](http://hl7.org/fhir/medicationrequest-definitions.html#MedicationRequest.encounter)
        dataRequirement: { type: MedicationRequest, relatedDataRequirement: { type: Encounter, relatedByPath: encounter } }

        define MedicationRequestWithEncounter:
          [MedicationRequest] M
            with [Encounter] E
              such that E.id = Last(Split(M.encounter.reference, '/'))
        */

        // Validate the ELM is correct
        val ed = manager.library.resolveExpressionRef("MedicationRequestWithEncounter")
        assertTrue(ed!!.expression is Query)
        val q = ed.expression as Query
        assertTrue(q.source.size == 1)
        val source = q.source[0]
        assertTrue(source.expression is Retrieve)
        var r = source.expression as Retrieve
        assertEquals("MedicationRequest", r.dataType!!.localPart)
        assertEquals(1, r.include.size)
        val primarySourceId = r.localId
        val ie = r.include[0]
        assertEquals("Encounter", ie.relatedDataType!!.localPart)
        assertEquals("encounter.reference", ie.relatedProperty)
        assertFalse(ie.isIsReverse() == true)
        assertEquals(1, q.relationship.size)
        assertTrue(q.relationship[0] is With)
        val w = q.relationship[0] as With
        assertTrue(w.expression is Retrieve)
        r = w.expression as Retrieve
        assertEquals("Encounter", r.dataType!!.localPart)
        assertEquals(r.includedIn, primarySourceId)

        // Validate the data requirement is reported in the module definition library
        var expectedDataRequirement: DataRequirement? = null
        for (dr in moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == FHIRTypes.MEDICATIONREQUEST) {
                expectedDataRequirement = dr
            }
        }
        assertTrue(expectedDataRequirement != null)

        var includedDataRequirement: DataRequirement? = null
        for (dr in moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == FHIRTypes.ENCOUNTER) {
                val e =
                    dr.getExtensionByUrl(
                        "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-relatedRequirement"
                    )
                if (e != null) {
                    val targetId = e.getExtensionByUrl("targetId")
                    val targetProperty = e.getExtensionByUrl("targetProperty")
                    if (
                        targetId != null &&
                            targetProperty != null &&
                            targetProperty.valueStringType.value == "encounter"
                    ) {
                        includedDataRequirement = dr
                    }
                }
            }
        }
        assertTrue(includedDataRequirement != null)

        // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    // @Test
    // TODO: Enable include when the reference is in a let
    @Throws(IOException::class)
    fun testDataRequirementsAnalysisCase9d() {
        val compilerOptions = this.compilerOptions
        val manager = setupDataRequirementsAnalysis("TestCases/TestCase9d.cql", compilerOptions)
        val moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions)

        /*
        Element that is a choice, one of which is a reference, included in a let with the relationship in a where
        [MedicationRequest.medication](http://hl7.org/fhir/medicationrequest-definitions.html#MedicationRequest.medication[x])
        dataRequirement: { id: G10001, type: MedicationRequest }
        dataRequirement: { type: Medication, codeFilter: { path: code, valueset: Aspirin }, relatedRequirement { targetId : G10001, targetPath: medication } }

        define MedicationRequestWithAspirinInLet:
          [MedicationRequest] R
            let M: singleton from (
                [Medication] M
                    where M.id = Last(Split(R.medication.reference, '/'))
                        and M.code in "Aspirin"
            )
        */

        // Validate the ELM is correct
        val ed = manager.library.resolveExpressionRef("MedicationRequestWithAspirinInLet")
        assertTrue(ed!!.expression is Query)
        var q = ed.expression as Query
        assertTrue(q.source.size == 1)
        var source = q.source[0]
        assertTrue(source.expression is Retrieve)
        var r = source.expression as Retrieve
        assertEquals("MedicationRequest", r.dataType!!.localPart)
        assertEquals(1, r.include.size)
        val primarySourceId = r.localId
        val ie = r.include[0]
        assertEquals("Medication", ie.relatedDataType!!.localPart)
        assertEquals("medication.reference", ie.relatedProperty)
        assertFalse(ie.isIsReverse() == true)

        assertEquals(1, q.let.size)
        val lc = q.let[0]
        assertTrue(lc.expression is SingletonFrom)
        val sf = lc.expression as SingletonFrom?
        assertTrue(sf!!.operand is Query)
        q = sf.operand as Query
        assertEquals(1, q.source.size)
        source = q.source[0]
        assertTrue(source.expression is Retrieve)
        r = source.expression as Retrieve
        assertEquals("Medication", r.dataType!!.localPart)
        assertEquals(r.includedIn, primarySourceId)
        assertEquals(2, r.codeFilter.size)
        var cfe = r.codeFilter[0]
        assertEquals("id", cfe.property)
        assertEquals("=", cfe.comparator)
        cfe = r.codeFilter[1]
        assertEquals("code", cfe.property)
        assertEquals("in", cfe.comparator)

        // Validate the data requirement is reported in the module definition library
        var expectedDataRequirement: DataRequirement? = null
        for (dr in moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == FHIRTypes.MEDICATIONREQUEST) {
                expectedDataRequirement = dr
            }
        }
        assertTrue(expectedDataRequirement != null)

        var includedDataRequirement: DataRequirement? = null
        for (dr in moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == FHIRTypes.MEDICATION) {
                val e =
                    dr.getExtensionByUrl(
                        "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-relatedRequirement"
                    )
                if (e != null) {
                    val targetId = e.getExtensionByUrl("targetId")
                    val targetProperty = e.getExtensionByUrl("targetProperty")
                    if (
                        targetId != null &&
                            targetProperty != null &&
                            targetProperty.valueStringType.value == "medication"
                    ) {
                        includedDataRequirement = dr
                    }
                }
            }
        }
        assertTrue(includedDataRequirement != null)

        // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    @Throws(IOException::class)
    fun dataRequirementsAnalysisCase9e() {
        val compilerOptions = this.compilerOptions
        val manager = setupDataRequirementsAnalysis("TestCases/TestCase9e.cql", compilerOptions)
        val moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions)

        /*
        Element that is a choice, one of which is a reference, included in a nested query in a where clause
        [MedicationRequest.medication](http://hl7.org/fhir/medicationrequest-definitions.html#MedicationRequest.medication[x])
        dataRequirement: { id: G10001, type: MedicationRequest }
        dataRequirement: { type: Medication, codeFilter: { path: code, valueset: Aspirin }, relatedRequirement { targetId : G10001, targetPath: medication } }

        define MedicationRequestWithAspirinInWhere:
          [MedicationRequest] R
            where exists (
              [Medication] M
                where M.id = Last(Split(R.medication.reference, '/'))
                  and M.code in "Aspirin"
            )
        */

        // Validate the ELM is correct
        val ed = manager.library.resolveExpressionRef("MedicationRequestWithAspirinInWhere")
        assertTrue(ed!!.expression is Query)
        var q = ed.expression as Query
        assertTrue(q.source.size == 1)
        var source = q.source[0]
        assertTrue(source.expression is Retrieve)
        var r = source.expression as Retrieve
        assertEquals("MedicationRequest", r.dataType!!.localPart)
        assertEquals(1, r.include.size)
        val primarySourceId = r.localId
        val ie = r.include[0]
        assertEquals("Medication", ie.relatedDataType!!.localPart)
        assertEquals("medication.reference", ie.relatedProperty)
        assertFalse(ie.isIsReverse() == true)

        assertTrue(q.where != null)
        assertTrue(q.where is Exists)
        val ex = q.where as Exists?
        assertTrue(ex!!.operand is Query)
        q = ex.operand as Query
        assertEquals(1, q.source.size)
        source = q.source[0]
        assertTrue(source.expression is Retrieve)
        r = source.expression as Retrieve
        assertEquals("Medication", r.dataType!!.localPart)
        assertEquals(r.includedIn, primarySourceId)
        assertEquals(1, r.codeFilter.size)
        val cfe = r.codeFilter[0]
        assertEquals("code", cfe.property)
        assertEquals("in", cfe.comparator)

        // Validate the data requirement is reported in the module definition library
        var expectedDataRequirement: DataRequirement? = null
        for (dr in moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == FHIRTypes.MEDICATIONREQUEST) {
                expectedDataRequirement = dr
            }
        }
        assertTrue(expectedDataRequirement != null)

        var includedDataRequirement: DataRequirement? = null
        for (dr in moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == FHIRTypes.MEDICATION) {
                val e =
                    dr.getExtensionByUrl(
                        "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-relatedRequirement"
                    )
                if (e != null) {
                    val targetId = e.getExtensionByUrl("targetId")
                    val targetProperty = e.getExtensionByUrl("targetProperty")
                    if (
                        targetId != null &&
                            targetProperty != null &&
                            targetProperty.valueStringType.value == "medication"
                    ) {
                        includedDataRequirement = dr
                    }
                }
            }
        }
        assertTrue(includedDataRequirement != null)

        // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    @Throws(IOException::class)
    fun dataRequirementsAnalysisCase9f() {
        val compilerOptions = this.compilerOptions
        val manager = setupDataRequirementsAnalysis("TestCases/TestCase9f.cql", compilerOptions)
        val moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions)

        /*
        Element that is a choice, one of which is a reference, joined in a where clause in a multi-source query
        [MedicationRequest.medication](http://hl7.org/fhir/medicationrequest-definitions.html#MedicationRequest.medication[x])
        dataRequirement: { id: G10001, type: MedicationRequest }
        dataRequirement: { type: Medication, codeFilter: { path: code, valueset: Aspirin }, relatedRequirement { targetId : G10001, targetPath: medication } }

        define MedicationRequestWithAspirinInFrom:
          from
              [MedicationRequest] R,
              [Medication] M
            where M.id = Last(Split(R.medication.reference, '/'))
              and M.code in "Aspirin"
        */

        // Validate the ELM is correct
        val ed = manager.library.resolveExpressionRef("MedicationRequestWithAspirinInFrom")
        assertTrue(ed!!.expression is Query)
        val q = ed.expression as Query
        assertTrue(q.source.size == 2)
        var source = q.source[0]
        assertTrue(source.expression is Retrieve)
        var r = source.expression as Retrieve
        assertEquals("MedicationRequest", r.dataType!!.localPart)
        assertEquals(1, r.include.size)
        val primarySourceId = r.localId
        val ie = r.include[0]
        assertEquals("Medication", ie.relatedDataType!!.localPart)
        assertEquals("medication.reference", ie.relatedProperty)
        assertFalse(ie.isIsReverse() == true)

        source = q.source[1]
        assertTrue(source.expression is Retrieve)
        r = source.expression as Retrieve
        assertEquals("Medication", r.dataType!!.localPart)
        assertEquals(r.includedIn, primarySourceId)
        assertEquals(1, r.codeFilter.size)
        val cfe = r.codeFilter[0]
        assertEquals("code", cfe.property)
        assertEquals("in", cfe.comparator)

        // Validate the data requirement is reported in the module definition library
        var expectedDataRequirement: DataRequirement? = null
        for (dr in moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == FHIRTypes.MEDICATIONREQUEST) {
                expectedDataRequirement = dr
            }
        }
        assertTrue(expectedDataRequirement != null)

        var includedDataRequirement: DataRequirement? = null
        for (dr in moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == FHIRTypes.MEDICATION) {
                val e =
                    dr.getExtensionByUrl(
                        "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-relatedRequirement"
                    )
                if (e != null) {
                    val targetId = e.getExtensionByUrl("targetId")
                    val targetProperty = e.getExtensionByUrl("targetProperty")
                    if (
                        targetId != null &&
                            targetProperty != null &&
                            targetProperty.valueStringType.value == "medication"
                    ) {
                        includedDataRequirement = dr
                    }
                }
            }
        }
        assertTrue(includedDataRequirement != null)

        // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    @Throws(IOException::class)
    fun dataRequirementsAnalysisCase10a() {
        val compilerOptions = this.compilerOptions
        val manager = setupDataRequirementsAnalysis("TestCases/TestCase10a.cql", compilerOptions)
        val moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions)

        /*
        Element that is a dateTime, referenced in a date comparison

        define "ESRD Observations":
          [Observation: "ESRD Diagnosis"] O
            where O.instant same day or after @2022-02-15

        {
            type: Observation
            mustSupport: [ 'code', 'issued' ]
            codeFilter: {
                path: 'code',
                valueSet: 'http://fakeurl.com/ersd-diagnosis'
            },
            dateFilter: {
                path: 'issued',
                valuePeriod: {
                    low: @2022-02-15
                }
            }
        }
        */

        // Validate the ELM is correct
        val ed = manager.library.resolveExpressionRef("ESRD Observations")
        assertTrue(ed!!.expression is Query)
        val q = ed.expression as Query
        assertTrue(q.source.size == 1)
        val source = q.source[0]
        assertTrue(source.expression is Retrieve)
        val r = source.expression as Retrieve
        assertEquals("Observation", r.dataType!!.localPart)

        // Validate the data requirement is reported in the module definition library
        var expectedDataRequirement: DataRequirement? = null
        for (dr in moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == FHIRTypes.OBSERVATION) {
                expectedDataRequirement = dr
            }
        }
        assertTrue(expectedDataRequirement != null)

        Assertions.assertEquals(2, expectedDataRequirement!!.getMustSupport().size)
        Assertions.assertEquals(
            1,
            expectedDataRequirement
                .getMustSupport()
                .stream()
                .filter { s: StringType? -> s!!.value == "code" }
                .count(),
        )
        Assertions.assertEquals(
            1,
            expectedDataRequirement
                .getMustSupport()
                .stream()
                .filter { s: StringType? -> s!!.value == "issued" }
                .count(),
        )

        Assertions.assertEquals(1, expectedDataRequirement.getCodeFilter().size)
        val drcfc = expectedDataRequirement.getCodeFilter()[0]
        assertEquals("code", drcfc.getPath())
        assertEquals("http://fakeurl.com/ersd-diagnosis", drcfc.getValueSet())

        Assertions.assertEquals(1, expectedDataRequirement.getDateFilter().size)
        val drdfc = expectedDataRequirement.getDateFilter()[0]
        val ld = LocalDate.of(2022, 2, 15)
        Assertions.assertEquals(
            0,
            drdfc.valuePeriod
                .getStart()
                .compareTo(Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant())),
        )

        // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    @Throws(IOException::class)
    fun dataRequirementsAnalysisCase10b() {
        val compilerOptions = this.compilerOptions
        val manager = setupDataRequirementsAnalysis("TestCases/TestCase10b.cql", compilerOptions)
        val moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions)

        /*
        Multiple date elements referenced in a Coalesce

        define "ESRD Observations":
          [Observation: "ESRD Diagnosis"] O
            where Coalesce(O.effective, O.issued) same day or after @2022-02-15
        */

        // Validate the ELM is correct
        val ed = manager.library.resolveExpressionRef("ESRD Observations")
        assertTrue(ed!!.expression is Query)
        val q = ed.expression as Query
        assertTrue(q.source.size == 1)
        val source = q.source[0]
        assertTrue(source.expression is Retrieve)
        val r = source.expression as Retrieve
        assertEquals("Observation", r.dataType!!.localPart)

        // Validate the data requirement is reported in the module definition library
        var expectedDataRequirement: DataRequirement? = null
        for (dr in moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == FHIRTypes.OBSERVATION) {
                expectedDataRequirement = dr
            }
        }
        assertTrue(expectedDataRequirement != null)

        // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    @Throws(IOException::class)
    fun dataRequirementsAnalysisCase10c() {
        // TODO: Complete this test case
        val compilerOptions = this.compilerOptions
        val manager = setupDataRequirementsAnalysis("TestCases/TestCase10c.cql", compilerOptions)
        val moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions)

        /*
        Element that is a choice, two of which are date-valued, referenced in a comparison

        define "ESRD Observations":
          [Observation: "ESRD Diagnosis"] O
            where O.effective same day or after @2022-02-15
        */

        // Validate the ELM is correct
        val ed = manager.library.resolveExpressionRef("ESRD Observations")
        assertTrue(ed!!.expression is Query)
        val q = ed.expression as Query
        assertTrue(q.source.size == 1)
        val source = q.source[0]
        assertTrue(source.expression is Retrieve)
        val r = source.expression as Retrieve
        assertEquals("Observation", r.dataType!!.localPart)

        // Validate the data requirement is reported in the module definition library
        var expectedDataRequirement: DataRequirement? = null
        for (dr in moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == FHIRTypes.OBSERVATION) {
                expectedDataRequirement = dr
            }
        }
        assertTrue(expectedDataRequirement != null)

        // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    @Throws(IOException::class)
    fun hedisbcse() {
        val compilerOptions = this.compilerOptions
        compilerOptions.compatibilityLevel = "1.4"
        val manager = setupDataRequirementsAnalysis("BCSE/BCSE_HEDIS_MY2022.cql", compilerOptions)
        val moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions)
        Assertions.assertNotNull(moduleDefinitionLibrary)
    }

    /**
     * Asserts that the actual module definition library is equal to the expected module definition
     * library. The expected library is loaded from the JSON file, and the given timezone (if
     * specified) is used as the default timezone when parsing FHIR JSON. (When JSON files are
     * parsed, date strings like "2022-12-17" inside FHIR periods are parsed as FHIR dateTime values
     * with the timezone set to the default timezone.)
     */
    private fun assertEqualToExpectedModuleDefinitionLibrary(
        actualModuleDefinitionLibrary: Library,
        pathToExpectedModuleDefinitionLibrary: String,
        zoneId: ZoneId? = null,
    ) {
        val context: FhirContext = fhirContext
        val parser = context.newJsonParser()
        var expectedModuleDefinitionLibrary: Library?
        if (zoneId != null) {
            TimeZone.setDefault(TimeZone.getTimeZone(zoneId))
        }
        try {
            expectedModuleDefinitionLibrary =
                parser.parseResource(
                    DataRequirementsProcessorTest::class
                        .java
                        .getResourceAsStream(pathToExpectedModuleDefinitionLibrary)
                ) as Library?
        } finally {
            TimeZone.setDefault(null)
        }
        Assertions.assertNotNull(expectedModuleDefinitionLibrary)
        // outputModuleDefinitionLibrary(actualModuleDefinitionLibrary);
        actualModuleDefinitionLibrary.setDate(null)
        expectedModuleDefinitionLibrary!!.setDate(null)

        parser.setPrettyPrint(true)
        val jsonExpected = parser.encodeResourceToString(expectedModuleDefinitionLibrary)
        val jsonActual = parser.encodeResourceToString(actualModuleDefinitionLibrary)

        assertEquals(jsonExpected, jsonActual)
    }

    @Test
    @Throws(IOException::class)
    fun exmLogic() {
        val compilerOptions = this.compilerOptions
        val manager = setupDataRequirementsAnalysis("EXMLogic/EXMLogic.cql", compilerOptions)
        val moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions)
        Assertions.assertNotNull(moduleDefinitionLibrary)
        assertEqualToExpectedModuleDefinitionLibrary(
            moduleDefinitionLibrary,
            "EXMLogic/Library-EXMLogic-data-requirements.json",
        )

        // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    @Throws(IOException::class)
    fun withDependencies() {
        val compilerOptions = this.compilerOptions
        val manager =
            setupDataRequirementsAnalysis("WithDependencies/BSElements.cql", compilerOptions)
        val moduleDefinitionLibrary =
            getModuleDefinitionLibrary(
                manager,
                compilerOptions,
                mutableMapOf(),
                ZonedDateTime.of(2023, 1, 16, 0, 0, 0, 0, ZoneId.of("UTC")),
                includeLogicDefinitions = false,
                recursive = true,
            )
        Assertions.assertNotNull(moduleDefinitionLibrary)
        assertEqualToExpectedModuleDefinitionLibrary(
            moduleDefinitionLibrary,
            "WithDependencies/Library-BSElements-data-requirements.json",
            ZoneId.of("UTC"),
        )
        // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    @Throws(IOException::class)
    fun cms645() {
        val compilerOptions = this.compilerOptions
        val manager = setupDataRequirementsAnalysis("CMS645/CMS645Test.cql", compilerOptions)
        val moduleDefinitionLibrary =
            getModuleDefinitionLibrary(
                manager,
                compilerOptions,
                mutableMapOf(),
                ZonedDateTime.of(2023, 1, 16, 0, 0, 0, 0, ZoneId.of("UTC")),
            )
        Assertions.assertNotNull(moduleDefinitionLibrary)
        assertEqualToExpectedModuleDefinitionLibrary(
            moduleDefinitionLibrary,
            "CMS645/CMS645-ModuleDefinitionLibrary.json",
        )

        // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    @Throws(IOException::class)
    fun pcsbmi() {
        val compilerOptions = defaultOptions()
        val manager =
            setupDataRequirementsAnalysis("PCSBMI/PCSBMIScreenAndFollowUpFHIR.cql", compilerOptions)
        val moduleDefinitionLibrary =
            getModuleDefinitionLibrary(
                manager,
                compilerOptions,
                mutableMapOf(),
                ZonedDateTime.of(2023, 1, 16, 0, 0, 0, 0, ZoneId.of("UTC")),
                includeLogicDefinitions = false,
                recursive = true,
            )
        Assertions.assertNotNull(moduleDefinitionLibrary)
        assertEqualToExpectedModuleDefinitionLibrary(
            moduleDefinitionLibrary,
            "PCSBMI/PCSBMI-ModuleDefinitionLibrary.json",
        )

        // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    @Throws(IOException::class)
    fun cms143() {
        val compilerOptions = defaultOptions()
        compilerOptions.options.add(CqlCompilerOptions.Options.EnableResultTypes)
        // expressions.add("Qualifying Encounter");
        // expressions.add("Qualifying Encounter During Measurement Period");
        // expressions.add("Qualifying Encounter During Measurement Period Expanded");
        val expressions =
            mutableSetOf(
                "Initial Population",
                "Denominator",
                "Denominator Exception",
                "Numerator",
                "SDE Ethnicity",
                "SDE Race",
                "SDE Sex",
                "SDE Payer",
            )
        // var manager = setupUncollapsedDataRequirementsAnalysis(new
        // NamespaceInfo("gov.healthit.ecqi.ecqms",
        // "http://ecqi.healthit.gov/ecqms"), "CMS143/cql/TestUnion.cql", compilerOptions);
        val manager =
            setupDataRequirementsAnalysis(
                NamespaceInfo("gov.healthit.ecqi.ecqms", "http://ecqi.healthit.gov/ecqms"),
                "CMS143/cql/POAGOpticNerveEvaluationFHIR-0.0.003.cql",
                compilerOptions,
            )
        val moduleDefinitionLibrary =
            getModuleDefinitionLibrary(manager, compilerOptions, expressions)
        Assertions.assertNotNull(moduleDefinitionLibrary)

        // assertEqualToExpectedModuleDefinitionLibrary(
        //        moduleDefinitionLibrary,
        // "CMS143/resources/Library-EffectiveDataRequirements.json");
        outputModuleDefinitionLibrary(moduleDefinitionLibrary)
    }

    @Test
    @Throws(IOException::class)
    fun sdeSex() {
        val compilerOptions = defaultOptions()
        compilerOptions.options.add(CqlCompilerOptions.Options.EnableResultTypes)
        val expressions = mutableSetOf("SDE Sex")
        val manager =
            setupDataRequirementsAnalysis(
                NamespaceInfo("gov.healthit.ecqi.ecqms", "http://ecqi.healthit.gov/ecqms"),
                "CMS143/cql/POAGOpticNerveEvaluationFHIR-0.0.003.cql",
                compilerOptions,
            )
        val moduleDefinitionLibrary =
            getModuleDefinitionLibrary(manager, compilerOptions, expressions)
        Assertions.assertNotNull(moduleDefinitionLibrary)
        assertEqualToExpectedModuleDefinitionLibrary(
            moduleDefinitionLibrary,
            "CMS143/resources/Library-SDESex-EffectiveDataRequirements.json",
        )

        // Has direct reference codes to M#http://hl7.org/fhir/v3/AdministrativeGender and
        // F#http://hl7.org/fhir/v3/AdministrativeGender
        // Has relatedArtifact to code system http://hl7.org/fhir/v3/AdministrativeGender
        // Has relatedArtifact to Library SDE
        // Has one and only one DataRequirement for Patient with profile QICore Patient and
        // mustSupport gender

        // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    @Throws(IOException::class)
    fun sdePayer() {
        val compilerOptions = defaultOptions()
        compilerOptions.options.add(CqlCompilerOptions.Options.EnableResultTypes)
        val expressions = mutableSetOf("SDE Payer")
        val manager =
            setupDataRequirementsAnalysis(
                NamespaceInfo("gov.healthit.ecqi.ecqms", "http://ecqi.healthit.gov/ecqms"),
                "CMS143/cql/POAGOpticNerveEvaluationFHIR-0.0.003.cql",
                compilerOptions,
            )
        val moduleDefinitionLibrary =
            getModuleDefinitionLibrary(manager, compilerOptions, expressions)
        Assertions.assertNotNull(moduleDefinitionLibrary)
        assertEqualToExpectedModuleDefinitionLibrary(
            moduleDefinitionLibrary,
            "CMS143/resources/Library-SDEPayer-EffectiveDataRequirements.json",
        )

        // Has relatedArtifact to Library SDE
        // Has relatedArtifact to Value Set Payer
        // Has one and only one DatRequirement for Coverage with the Payer Type value set and
        // mustSupport type and
        // period

        // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    @Throws(IOException::class)
    fun sdeEthnicity() {
        val compilerOptions = defaultOptions()
        compilerOptions.options.add(CqlCompilerOptions.Options.EnableResultTypes)
        val expressions = mutableSetOf("SDE Ethnicity")
        val manager =
            setupDataRequirementsAnalysis(
                NamespaceInfo("gov.healthit.ecqi.ecqms", "http://ecqi.healthit.gov/ecqms"),
                "CMS143/cql/POAGOpticNerveEvaluationFHIR-0.0.003.cql",
                compilerOptions,
            )
        val moduleDefinitionLibrary =
            getModuleDefinitionLibrary(manager, compilerOptions, expressions)
        Assertions.assertNotNull(moduleDefinitionLibrary)
        assertEqualToExpectedModuleDefinitionLibrary(
            moduleDefinitionLibrary,
            "CMS143/resources/Library-SDEEthnicity-EffectiveDataRequirements.json",
        )

        // Has relatedArtifact to Library SDE
        // Has one and only one DatRequirement for Patient with the QICore Profile and mustSupport
        // ethnicity

        // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    @Throws(IOException::class)
    fun sdeRace() {
        val compilerOptions = defaultOptions()
        compilerOptions.options.add(CqlCompilerOptions.Options.EnableResultTypes)
        val expressions = mutableSetOf("SDE Race")
        val manager =
            setupDataRequirementsAnalysis(
                NamespaceInfo("gov.healthit.ecqi.ecqms", "http://ecqi.healthit.gov/ecqms"),
                "CMS143/cql/POAGOpticNerveEvaluationFHIR-0.0.003.cql",
                compilerOptions,
            )
        val moduleDefinitionLibrary =
            getModuleDefinitionLibrary(manager, compilerOptions, expressions)
        Assertions.assertNotNull(moduleDefinitionLibrary)
        assertEqualToExpectedModuleDefinitionLibrary(
            moduleDefinitionLibrary,
            "CMS143/resources/Library-SDERace-EffectiveDataRequirements.json",
        )

        // Has relatedArtifact to Library SDE
        // Has one and only one DatRequirement for Patient with the QICore Profile and mustSupport
        // race

        // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    @Throws(IOException::class)
    fun qualifyingEncounterMP() {
        val compilerOptions = defaultOptions()
        compilerOptions.options.add(CqlCompilerOptions.Options.EnableResultTypes)
        val expressions = mutableSetOf("Qualifying Encounter During Measurement Period")
        val manager =
            setupDataRequirementsAnalysis(
                NamespaceInfo("gov.healthit.ecqi.ecqms", "http://ecqi.healthit.gov/ecqms"),
                "CMS143/cql/POAGOpticNerveEvaluationFHIR-0.0.003.cql",
                compilerOptions,
            )
        val moduleDefinitionLibrary =
            getModuleDefinitionLibrary(manager, compilerOptions, expressions)
        Assertions.assertNotNull(moduleDefinitionLibrary)
        assertEqualToExpectedModuleDefinitionLibrary(
            moduleDefinitionLibrary,
            "CMS143/resources/Library-QualifyingEncounterMP-EffectiveDataRequirements.json",
        )

        // Has direct reference codes to VR and AMB
        // Has relatedArtifact to ActCode code system
        // Has relatedArtifact to Office Visit ValueSet
        // Has relatedArtifact to Opthalmological Services ValueSet
        // Has relatedArtifact to Outpatient Consultation ValueSet
        // Has relatedArtifact to Nursing Facility Visit ValueSet
        // Has relatedArtifact to Care Services in Long-Term Residentail Facility ValueSet
        // Has 5 DataRequirements for Encounter with the QICore Encounter Profile and mustSupport
        // type, period, and
        // class, one for each ValueSet

        // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    @Disabled(
        "Extra extensions in the actual library: QICoreCommon.toInterval and QICoreCommon.ToInterval?"
    )
    @Throws(IOException::class)
    fun cms135() {
        val compilerOptions = defaultOptions()
        compilerOptions.collapseDataRequirements = true
        compilerOptions.analyzeDataRequirements = false
        var manager: Setup = setup("CMS135/cql/FHIRHelpers-4.4.000.cql", compilerOptions)
        manager = nextSetup(manager.manager, "CMS135/cql/CMS135FHIR-0.0.000.cql")
        val expressions =
            mutableSetOf(
                "Initial Population",
                "Denominator",
                "Denominator Exclusions",
                "Numerator",
                "Denominator Exceptions",
            )
        val moduleDefinitionLibrary =
            getModuleDefinitionLibrary(
                manager,
                compilerOptions,
                expressions,
                includeLogicDefinitions = true,
                recursive = true,
            )
        Assertions.assertNotNull(moduleDefinitionLibrary)
        // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
        assertEqualToExpectedModuleDefinitionLibrary(
            moduleDefinitionLibrary,
            "CMS135/resources/Library-Measure-EffectiveDataRequirements.json",
        )

        val overloadDefinitions: MutableList<Extension?> = ArrayList<Extension?>()
        for (e in
            moduleDefinitionLibrary.getExtensionsByUrl(
                "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-logicDefinition"
            )) {
            if (
                e.getExtensionByUrl("name").valueStringType.value ==
                    "overlapsAfterHeartFailureOutpatientEncounter"
            ) {
                overloadDefinitions.add(e)
            }
        }

        Assertions.assertEquals(3, overloadDefinitions.size)
    }

    @Test
    @Throws(IOException::class)
    fun cms149() {
        val compilerOptions = this.compilerOptions
        val manager =
            setupDataRequirementsAnalysis(
                "CMS149/cql/DementiaCognitiveAssessmentFHIR-0.0.003.cql",
                compilerOptions,
            )
        val moduleDefinitionLibrary =
            getModuleDefinitionLibrary(
                manager,
                compilerOptions,
                mutableMapOf(),
                ZonedDateTime.of(2023, 1, 16, 0, 0, 0, 0, ZoneId.of("UTC")),
                includeLogicDefinitions = false,
                recursive = true,
            )
        Assertions.assertNotNull(moduleDefinitionLibrary)
        assertEqualToExpectedModuleDefinitionLibrary(
            moduleDefinitionLibrary,
            "CMS149/resources/Library-EffectiveDataRequirements.json",
        )

        // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    @Throws(IOException::class)
    fun cms986() {
        val compilerOptions = this.compilerOptions
        val manager =
            setupDataRequirementsAnalysis(
                "CMS986/cql/CMS986FHIRMalnutritionScore-0.3.000.cql",
                compilerOptions,
            )
        // expressions.add("Initial Population");
        // expressions.add("Measure Population");
        // expressions.add("Measure Population Exclusion");
        val expressions = mutableSetOf("Measure Observation 1")
        // expressions.add("SDE CMS Sex");
        // expressions.add("SDE Payer Type");
        // expressions.add("SDE Ethnicity");
        // expressions.add("SDE Race");
        val moduleDefinitionLibrary =
            getModuleDefinitionLibrary(
                manager,
                compilerOptions,
                expressions,
                true,
                SpecificationLevel.CRMI,
            )
        Assertions.assertNotNull(moduleDefinitionLibrary)

        // assertEqualToExpectedModuleDefinitionLibrary(
        //         moduleDefinitionLibrary,
        // "CMS986/resources/library-Measure-Observation-1-requirements.json"
        // );
        outputModuleDefinitionLibrary(moduleDefinitionLibrary)
    }

    private fun getLogicDefinitionByName(
        logicDefinitions: MutableList<Extension>,
        libraryName: String?,
        name: String?,
    ): Extension? {
        for (ld in logicDefinitions) {
            val ln = ld.getExtensionByUrl("libraryName")
            assertTrue(ln != null && ln.hasValue())
            val n = ld.getExtensionByUrl("name")
            assertTrue(n != null && n.hasValue())
            if (ln.valueStringType.value == libraryName && n.valueStringType.value == name) {
                return ld
            }
        }
        return null
    }

    @Test
    @Throws(IOException::class)
    fun deviceOrder() {
        val compilerOptions = defaultOptions()
        val manager =
            setupDataRequirementsGather("DeviceOrder/TestDeviceOrder.cql", compilerOptions)
        val moduleDefinitionLibrary =
            getModuleDefinitionLibrary(
                manager,
                compilerOptions,
                mutableMapOf(),
                ZonedDateTime.of(2023, 1, 16, 0, 0, 0, 0, ZoneId.of("UTC")),
                true,
            )
        Assertions.assertNotNull(moduleDefinitionLibrary)
        assertEqualToExpectedModuleDefinitionLibrary(
            moduleDefinitionLibrary,
            "DeviceOrder/Library-TestDeviceOrder-EffectiveDataRequirements.json",
        )

        // outputModuleDefinitionLibrary(moduleDefinitionLibrary);
        val logicDefinitions =
            moduleDefinitionLibrary.getExtensionsByUrl(
                "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-logicDefinition"
            )
        assertTrue(logicDefinitions != null)
        assertTrue(logicDefinitions.isNotEmpty())
        val logicDefinition =
            getLogicDefinitionByName(logicDefinitions, "TestDeviceOrder", "isDeviceOrder")
        assertTrue(logicDefinition != null)
    }

    @Test
    fun dataRequirementsProcessorWithPertinence() {
        val cqlTranslatorOptions = CqlCompilerOptions()

        cqlTranslatorOptions.options.add(CqlCompilerOptions.Options.EnableAnnotations)
        try {
            val setup: Setup =
                setup(
                    "CompositeMeasures/cql/pertinence-tag.cql",
                    cqlTranslatorOptions,
                ) // "OpioidCDS/cql/OpioidCDSCommon.cql", cqlTranslatorOptions);

            val dqReqTrans = DataRequirementsProcessor()
            val moduleDefinitionLibrary =
                dqReqTrans.gatherDataRequirements(
                    setup.manager,
                    setup.library,
                    cqlTranslatorOptions,
                    null,
                    false,
                )

            Assertions.assertEquals(3, moduleDefinitionLibrary.getDataRequirement().size)
            val dr = moduleDefinitionLibrary.getDataRequirement()[1]
            assertEquals(FHIRTypes.CONDITION, dr.getType())
            assertEquals(
                "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-pertinence",
                dr.getExtension()[0].getUrl(),
            )
            assertEquals("pathognomonic", (dr.getExtension()[0].getValue() as Coding).getCode())

            val context: FhirContext = fhirContext
            val parser = context.newJsonParser()
            val moduleDefString =
                parser.setPrettyPrint(true).encodeResourceToString(moduleDefinitionLibrary)
            logger.debug(moduleDefString)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    @Test
    fun dataRequirementsProcessorWithPertinenceAgain() {
        val cqlTranslatorOptions = CqlCompilerOptions()

        cqlTranslatorOptions.options.add(CqlCompilerOptions.Options.EnableAnnotations)
        try {
            val setup: Setup =
                setup(
                    "CompositeMeasures/cql/pertinence-tag-AdvancedIllnessandFrailtyExclusion_FHIR4-5.0.000.cql",
                    cqlTranslatorOptions,
                ) // "OpioidCDS/cql/OpioidCDSCommon.cql", cqlTranslatorOptions);

            val dqReqTrans = DataRequirementsProcessor()
            val moduleDefinitionLibrary =
                dqReqTrans.gatherDataRequirements(
                    setup.manager,
                    setup.library,
                    cqlTranslatorOptions,
                    null,
                    false,
                )

            val dr = moduleDefinitionLibrary.getDataRequirement()[1]
            assertEquals(FHIRTypes.CONDITION, dr.getType())
            assertEquals(
                "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-pertinence",
                dr.getExtension()[0].getUrl(),
            )
            assertEquals("weakly-negative", (dr.getExtension()[0].getValue() as Coding).getCode())

            val dr2 = moduleDefinitionLibrary.getDataRequirement()[2]
            assertEquals(FHIRTypes.ENCOUNTER, dr2.getType())
            assertEquals(
                "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-pertinence",
                dr2.getExtension()[0].getUrl(),
            )
            assertEquals("pathognomonic", (dr2.getExtension()[0].getValue() as Coding).getCode())

            val dr6 = moduleDefinitionLibrary.getDataRequirement()[6]
            assertEquals(FHIRTypes.DEVICEREQUEST, dr6.getType())
            assertEquals(
                "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-pertinence",
                dr6.getExtension()[0].getUrl(),
            )
            assertEquals(
                "strongly-positive",
                (dr6.getExtension()[0].getValue() as Coding).getCode(),
            )

            val context: FhirContext = fhirContext
            val parser = context.newJsonParser()
            val moduleDefString =
                parser.setPrettyPrint(true).encodeResourceToString(moduleDefinitionLibrary)
            logger.debug(moduleDefString)
        } catch (ioException: IOException) {
            ioException.printStackTrace()
        }
    }

    class Setup(val manager: LibraryManager, val library: CompiledLibrary)

    companion object {
        private val logger: Logger =
            LoggerFactory.getLogger(DataRequirementsProcessorTest::class.java)

        private val fhirContext: FhirContext
            get() = FhirContext.forR5Cached()

        private fun setup(options: CqlCompilerOptions, relativePath: String): LibraryManager {
            val modelManager = ModelManager()
            val libraryManager = LibraryManager(modelManager, options)
            val p = Path(relativePath)
            libraryManager.librarySourceLoader.registerProvider(
                DefaultLibrarySourceProvider(p.parent!!)
            )
            libraryManager.librarySourceLoader.registerProvider(FhirLibrarySourceProvider())

            return libraryManager
        }

        @Throws(IOException::class)
        fun setup(testFileName: String, options: CqlCompilerOptions): Setup {
            return setup(null, testFileName, options)
        }

        @Throws(IOException::class)
        fun setup(
            namespaceInfo: NamespaceInfo?,
            testFileName: String,
            options: CqlCompilerOptions,
        ): Setup {
            val translationTestFile =
                DataRequirementsProcessorTest::class.java.getResource(testFileName)!!
            val p = Path(translationTestFile.path)

            val manager: LibraryManager = setup(options, p.toString())

            if (namespaceInfo != null) {
                manager.namespaceManager.addNamespace(namespaceInfo)
            }

            val compiler = CqlCompiler(namespaceInfo, null, manager)

            val lib = compiler.run(p)

            assertTrue(
                compiler.exceptions.none { it.severity == CqlCompilerException.ErrorSeverity.Error }
            )

            manager.compiledLibraries[lib.identifier!!] = compiler.compiledLibrary!!

            return Setup(manager, compiler.compiledLibrary!!)
        }

        @Throws(IOException::class)
        fun nextSetup(manager: LibraryManager, testFileName: String): Setup {
            val translationTestFile =
                DataRequirementsProcessorTest::class.java.getResource(testFileName)!!

            val p = Path(translationTestFile.path)

            val compiler = CqlCompiler(null, null, manager)

            val lib = compiler.run(p)

            assertTrue(
                compiler.exceptions.none { it.severity == CqlCompilerException.ErrorSeverity.Error }
            )

            manager.compiledLibraries[lib.identifier!!] = compiler.compiledLibrary!!

            return Setup(manager, compiler.compiledLibrary!!)
        }
    }
}
