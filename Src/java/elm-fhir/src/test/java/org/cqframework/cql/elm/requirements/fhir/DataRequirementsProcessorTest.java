package org.cqframework.cql.elm.requirements.fhir;

import ca.uhn.fhir.context.FhirContext;
import org.cqframework.cql.cql2elm.*;
import org.cqframework.cql.cql2elm.quick.FhirLibrarySourceProvider;
import org.fhir.ucum.UcumEssenceService;
import org.fhir.ucum.UcumException;
import org.fhir.ucum.UcumService;
import org.hl7.cql.model.NamespaceInfo;
import org.hl7.elm.r1.*;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r5.model.*;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.testng.Assert.*;


public class DataRequirementsProcessorTest {
    private static Logger logger = LoggerFactory.getLogger(DataRequirementsProcessorTest.class);

    private static ModelManager modelManager;
    private static LibraryManager libraryManager;
    private static UcumService ucumService;
    private static FhirContext fhirContext;
    private static FhirContext getFhirContext() {
        return FhirContext.forR5Cached();
    }

    @Test
    public void TestDataRequirementsProcessor() {
        CqlTranslatorOptions cqlTranslatorOptions = new CqlTranslatorOptions();
        cqlTranslatorOptions.getFormats().add(CqlTranslator.Format.JSON);
        cqlTranslatorOptions.getOptions().add(CqlTranslatorOptions.Options.EnableAnnotations);
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
            CqlTranslator translator = createTranslator("CompositeMeasures/cql/EXM124-9.0.000.cql", cqlTranslatorOptions);//"OpioidCDS/cql/OpioidCDSCommon.cql", cqlTranslatorOptions);
            translator.toELM();
            assertTrue(translator.getErrors().isEmpty());
            libraryManager.cacheLibrary(translator.getTranslatedLibrary());

            DataRequirementsProcessor dqReqTrans = new DataRequirementsProcessor();
            org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = dqReqTrans.gatherDataRequirements(libraryManager, translator.getTranslatedLibrary(), cqlTranslatorOptions, null, false);
            assertTrue(moduleDefinitionLibrary.getType().getCode("http://terminology.hl7.org/CodeSystem/library-type").equalsIgnoreCase("module-definition"));

            FhirContext context =  getFhirContext();
            IParser parser = context.newJsonParser();
            String moduleDefString = parser.setPrettyPrint(true).encodeResourceToString(moduleDefinitionLibrary);
            logger.debug(moduleDefString);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private RelatedArtifact getDependency(org.hl7.fhir.r5.model.Library moduleDefinitionLibrary, String url) {
        for (RelatedArtifact r : moduleDefinitionLibrary.getRelatedArtifact()) {
            if (r.getType() == RelatedArtifact.RelatedArtifactType.DEPENDSON
                    && r.getResource().equals(url)) {
                return r;
            }
        }

        return null;
    }

    private ParameterDefinition getParameter(org.hl7.fhir.r5.model.Library moduleDefinitionLibrary, String parameterName) {
        for (ParameterDefinition pd : moduleDefinitionLibrary.getParameter()) {
            if (pd.hasName() && pd.getName().equals(parameterName)) {
                return pd;
            }
        }

        return null;
    }

    @Test
    public void TestDataRequirementsProcessorOpioidIssueExpression() {
        CqlTranslatorOptions cqlTranslatorOptions = new CqlTranslatorOptions();
        cqlTranslatorOptions.getFormats().add(CqlTranslator.Format.JSON);
        cqlTranslatorOptions.setCollapseDataRequirements(true);
        cqlTranslatorOptions.setAnalyzeDataRequirements(true);
        try {
            NamespaceInfo ni = new NamespaceInfo("fhir.cdc.opioid-cds", "http://fhir.org/guides/cdc/opioid-cds");
            CqlTranslator translator = createTranslator(ni, "OpioidCDSSTU3/cql/OpioidCDSREC10_bug_repo.cql", cqlTranslatorOptions);
            translator.toELM();
            assertTrue(translator.getErrors().isEmpty());
            libraryManager.cacheLibrary(translator.getTranslatedLibrary());
            DataRequirementsProcessor dqReqTrans = new DataRequirementsProcessor();
            Set<String> expressions = new HashSet<String>();
            expressions.add("Negative PCP Screenings Count Since Last POS");
            org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = dqReqTrans.gatherDataRequirements(libraryManager,
                    translator.getTranslatedLibrary(), cqlTranslatorOptions, expressions, false);
            assertNotNull(moduleDefinitionLibrary);

            RelatedArtifact ra = null;
            // OpioidCDSCommon
            ra = getDependency(moduleDefinitionLibrary, "http://fhir.org/guides/cdc/opioid-cds/Library/OpioidCDSCommon|1.2.3");
            assertNotNull(ra, "Expected depends-on http://fhir.org/guides/cdc/opioid-cds/Library/OpioidCDSCommon|1.2.3");

            // FHIRHelpers
            ra = getDependency(moduleDefinitionLibrary, "http://hl7.org/fhir/Library/FHIRHelpers|3.0.0");
            assertNotNull(ra, "Expected depends-on http://hl7.org/fhir/Library/FHIRHelpers|3.0.0");

            // depends-on http://fhir.org/guides/cdc/opioid-cds/ValueSet/observation-category-laboratory
            ra = getDependency(moduleDefinitionLibrary, "http://fhir.org/guides/cdc/opioid-cds/ValueSet/observation-category-laboratory");
            assertNotNull(ra, "Expected depends-on http://fhir.org/guides/cdc/opioid-cds/ValueSet/observation-category-laboratory");

            // depends-on http://fhir.org/guides/cdc/opioid-cds/ValueSet/pcp-medications
            ra = getDependency(moduleDefinitionLibrary, "http://fhir.org/guides/cdc/opioid-cds/ValueSet/pcp-medications");
            assertNotNull(ra, "Expected depends-on http://fhir.org/guides/cdc/opioid-cds/ValueSet/pcp-medications");

            // parameter "Negative PCP Screenings Count Since Last POS": integer
            assertTrue(moduleDefinitionLibrary.getParameter().size() == 1);
            ParameterDefinition pd = moduleDefinitionLibrary.getParameter().get(0);
            assertEquals(pd.getName(), "Negative PCP Screenings Count Since Last POS");
            assertEquals(pd.getType(), Enumerations.FHIRAllTypes.INTEGER);

            // dataRequirement Observation {
            //   ms: { code, category, value, status, status.value, effective },
            //   codeFilter {
            //     { category in http://fhir.org/guides/cdc/opioid-cds/ValueSet/observation-category-laboratory },
            //     { code in http://fhir.org/guides/cdc/opioid-cds/ValueSet/pcp-medications }
            //   }
            // }
            assertTrue(moduleDefinitionLibrary.getDataRequirement().size() == 1);
            DataRequirement dr= moduleDefinitionLibrary.getDataRequirement().get(0);
            assertEquals(dr.getType(), Enumerations.FHIRAllTypes.OBSERVATION);
            assertTrue(dr.getMustSupport().size() == 6);
            assertTrue(dr.getMustSupport().stream().filter(x -> x.getValue().equals("code")).count() == 1);
            assertTrue(dr.getMustSupport().stream().filter(x -> x.getValue().equals("category")).count() == 1);
            assertTrue(dr.getMustSupport().stream().filter(x -> x.getValue().equals("value")).count() == 1);
            assertTrue(dr.getMustSupport().stream().filter(x -> x.getValue().equals("status")).count() == 1);
            assertTrue(dr.getMustSupport().stream().filter(x -> x.getValue().equals("status.value")).count() == 1);
            assertTrue(dr.getMustSupport().stream().filter(x -> x.getValue().equals("effective")).count() == 1);
            assertTrue(dr.getCodeFilter().size() == 2);
            DataRequirement.DataRequirementCodeFilterComponent cf = null;
            for (DataRequirement.DataRequirementCodeFilterComponent drcf : dr.getCodeFilter()) {
                if (drcf.getPath().equals("category") && drcf.getValueSet().equals("http://fhir.org/guides/cdc/opioid-cds/ValueSet/observation-category-laboratory")) {
                    cf = drcf;
                    break;
                }
            }
            assertNotNull(cf, "Expected filter on category");

            cf = null;
            for (DataRequirement.DataRequirementCodeFilterComponent drcf : dr.getCodeFilter()) {
                if (drcf.getPath().equals("code") && drcf.getValueSet().equals("http://fhir.org/guides/cdc/opioid-cds/ValueSet/pcp-medications")) {
                    cf = drcf;
                    break;
                }
            }
            assertNotNull(cf, "Expected filter on code");

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Test
    public void TestDataRequirementsProcessorOpioidIssueLibrary() {
        CqlTranslatorOptions cqlTranslatorOptions = new CqlTranslatorOptions();
        cqlTranslatorOptions.getFormats().add(CqlTranslator.Format.JSON);
        cqlTranslatorOptions.setCollapseDataRequirements(true);
        cqlTranslatorOptions.setAnalyzeDataRequirements(true);
        try {
            NamespaceInfo ni = new NamespaceInfo("fhir.cdc.opioid-cds", "http://fhir.org/guides/cdc/opioid-cds");
            CqlTranslator translator = createTranslator(ni, "OpioidCDSSTU3/cql/OpioidCDSREC10_bug_repo.cql", cqlTranslatorOptions);
            translator.toELM();
            assertTrue(translator.getErrors().isEmpty());
            libraryManager.cacheLibrary(translator.getTranslatedLibrary());
            DataRequirementsProcessor dqReqTrans = new DataRequirementsProcessor();
            org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = dqReqTrans.gatherDataRequirements(libraryManager,
                    translator.getTranslatedLibrary(), cqlTranslatorOptions, null, false);
            assertNotNull(moduleDefinitionLibrary);

            RelatedArtifact ra = null;
            // FHIR-ModelInfo
            ra = getDependency(moduleDefinitionLibrary, "http://fhir.org/guides/cqf/common/Library/FHIR-ModelInfo|3.0.0");
            assertNotNull(ra, "Expected depends-on http://fhir.org/guides/cqf/common/Library/FHIR-ModelInfo|3.0.0");

            // FHIRHelpers
            ra = getDependency(moduleDefinitionLibrary, "http://hl7.org/fhir/Library/FHIRHelpers|3.0.0");
            assertNotNull(ra, "Expected depends-on http://hl7.org/fhir/Library/FHIRHelpers|3.0.0");

            // OpioidCDSCommon
            ra = getDependency(moduleDefinitionLibrary, "http://fhir.org/guides/cdc/opioid-cds/Library/OpioidCDSCommon|1.2.3");
            assertNotNull(ra, "Expected depends-on http://fhir.org/guides/cdc/opioid-cds/Library/OpioidCDSCommon|1.2.3");

            // OpioidCDSRoutines
            ra = getDependency(moduleDefinitionLibrary, "http://fhir.org/guides/cdc/opioid-cds/Library/OpioidCDSRoutines|1.2.3");
            assertNotNull(ra, "Expected depends-on http://fhir.org/guides/cdc/opioid-cds/Library/OpioidCDSRoutines|1.2.3");

            // OpioidCDSCommonConfig
            ra = getDependency(moduleDefinitionLibrary, "http://fhir.org/guides/cdc/opioid-cds/Library/OpioidCDSCommonConfig|1.2.3");
            assertNotNull(ra, "Expected depends-on http://fhir.org/guides/cdc/opioid-cds/Library/OpioidCDSCommonConfig|1.2.3");

            // depends-on http://fhir.org/guides/cdc/opioid-cds/ValueSet/observation-category-laboratory
            ra = getDependency(moduleDefinitionLibrary, "http://fhir.org/guides/cdc/opioid-cds/ValueSet/observation-category-laboratory");
            assertNotNull(ra, "Expected depends-on http://fhir.org/guides/cdc/opioid-cds/ValueSet/observation-category-laboratory");

            // depends-on http://fhir.org/guides/cdc/opioid-cds/ValueSet/pcp-medications
            ra = getDependency(moduleDefinitionLibrary, "http://fhir.org/guides/cdc/opioid-cds/ValueSet/pcp-medications");
            assertNotNull(ra, "Expected depends-on http://fhir.org/guides/cdc/opioid-cds/ValueSet/pcp-medications");

            // depends-on http://fhir.org/guides/cdc/opioid-cds/ValueSet/cocaine-medications
            ra = getDependency(moduleDefinitionLibrary, "http://fhir.org/guides/cdc/opioid-cds/ValueSet/cocaine-medications");
            assertNotNull(ra, "Expected depends-on http://fhir.org/guides/cdc/opioid-cds/ValueSet/cocaine-medications");

            // parameter "Negative PCP Screenings Count Since Last POS": integer
            ParameterDefinition pd = null;

            pd = getParameter(moduleDefinitionLibrary, "ContextPrescriptions");
            assertNotNull(pd, "Expected parameter ContextPrescriptions");
            assertEquals(pd.getType(), Enumerations.FHIRAllTypes.MEDICATIONREQUEST);
            assertEquals(pd.getUse(), Enumerations.OperationParameterUse.IN);
            assertEquals(pd.getMax(), "*");

            pd = getParameter(moduleDefinitionLibrary, "Patient");
            assertNotNull(pd, "Expected parameter Patient");
            assertEquals(pd.getType(), Enumerations.FHIRAllTypes.PATIENT);
            assertEquals(pd.getUse(), Enumerations.OperationParameterUse.OUT);
            assertEquals(pd.getMax(), "1");

            pd = getParameter(moduleDefinitionLibrary, "Lookback Year");
            assertNotNull(pd, "Expected parameter Lookback Year");
            assertEquals(pd.getType(), Enumerations.FHIRAllTypes.PERIOD);
            assertEquals(pd.getUse(), Enumerations.OperationParameterUse.OUT);
            assertEquals(pd.getMax(), "1");

            pd = getParameter(moduleDefinitionLibrary, "PCP Screenings");
            assertNotNull(pd, "Expected parameter PCP Screenings");
            assertEquals(pd.getType(), Enumerations.FHIRAllTypes.OBSERVATION);
            assertEquals(pd.getUse(), Enumerations.OperationParameterUse.OUT);
            assertEquals(pd.getMax(), "*");

            pd = getParameter(moduleDefinitionLibrary, "Positive PCP Screenings");
            assertNotNull(pd, "Expected parameter Positive PCP Screenings");
            assertEquals(pd.getType(), Enumerations.FHIRAllTypes.OBSERVATION);
            assertEquals(pd.getUse(), Enumerations.OperationParameterUse.OUT);
            assertEquals(pd.getMax(), "*");

            pd = getParameter(moduleDefinitionLibrary, "Negative PCP Screenings");
            assertNotNull(pd, "Expected parameter Negative PCP Screenings");
            assertEquals(pd.getType(), Enumerations.FHIRAllTypes.OBSERVATION);
            assertEquals(pd.getUse(), Enumerations.OperationParameterUse.OUT);
            assertEquals(pd.getMax(), "*");

            pd = getParameter(moduleDefinitionLibrary, "Negative PCP Screenings Count Since Last POS");
            assertNotNull(pd, "Expected parameter Negative PCP Screenings Count Since Last POS");
            assertEquals(pd.getType(), Enumerations.FHIRAllTypes.INTEGER);
            assertEquals(pd.getUse(), Enumerations.OperationParameterUse.OUT);
            assertEquals(pd.getMax(), "1");

            pd = getParameter(moduleDefinitionLibrary, "Positive PCP Dates in Lookback Period");
            assertNotNull(pd, "Expected parameter Positive PCP Dates in Lookback Period");
            assertEquals(pd.getType(), Enumerations.FHIRAllTypes.STRING);
            assertEquals(pd.getUse(), Enumerations.OperationParameterUse.OUT);
            assertEquals(pd.getMax(), "*");

            pd = getParameter(moduleDefinitionLibrary, "Has Positive Screening for PCP in Last 12 Months");
            assertNotNull(pd, "Expected parameter Has Positive Screening for PCP in Last 12 Months");
            assertEquals(pd.getType(), Enumerations.FHIRAllTypes.BOOLEAN);
            assertEquals(pd.getUse(), Enumerations.OperationParameterUse.OUT);
            assertEquals(pd.getMax(), "1");

            pd = getParameter(moduleDefinitionLibrary, "PCP Summary");
            assertNotNull(pd, "Expected parameter PCPSummary");
            assertEquals(pd.getType(), Enumerations.FHIRAllTypes.STRING);
            assertEquals(pd.getUse(), Enumerations.OperationParameterUse.OUT);
            assertEquals(pd.getMax(), "1");

            // dataRequirement Observation {
            //   ms: { code, category, value, status, status.value, effective },
            //   codeFilter {
            //     { category in http://fhir.org/guides/cdc/opioid-cds/ValueSet/observation-category-laboratory },
            //     { code in http://fhir.org/guides/cdc/opioid-cds/ValueSet/pcp-medications }
            //   }
            // }
            assertTrue(moduleDefinitionLibrary.getDataRequirement().size() == 2);
            DataRequirement dr = null;
            for (DataRequirement r : moduleDefinitionLibrary.getDataRequirement()) {
                if (r.getType() == Enumerations.FHIRAllTypes.OBSERVATION) {
                    dr = r;
                    break;
                }
            }
            assertNotNull(dr);
            assertEquals(dr.getType(), Enumerations.FHIRAllTypes.OBSERVATION);
            assertTrue(dr.getMustSupport().size() == 6);
            assertTrue(dr.getMustSupport().stream().filter(x -> x.getValue().equals("code")).count() == 1);
            assertTrue(dr.getMustSupport().stream().filter(x -> x.getValue().equals("category")).count() == 1);
            assertTrue(dr.getMustSupport().stream().filter(x -> x.getValue().equals("value")).count() == 1);
            assertTrue(dr.getMustSupport().stream().filter(x -> x.getValue().equals("status")).count() == 1);
            assertTrue(dr.getMustSupport().stream().filter(x -> x.getValue().equals("status.value")).count() == 1);
            assertTrue(dr.getMustSupport().stream().filter(x -> x.getValue().equals("effective")).count() == 1);
            assertTrue(dr.getCodeFilter().size() == 2);
            DataRequirement.DataRequirementCodeFilterComponent cf = null;
            for (DataRequirement.DataRequirementCodeFilterComponent drcf : dr.getCodeFilter()) {
                if (drcf.getPath().equals("category") && drcf.getValueSet().equals("http://fhir.org/guides/cdc/opioid-cds/ValueSet/observation-category-laboratory")) {
                    cf = drcf;
                    break;
                }
            }
            assertNotNull(cf, "Expected filter on category");

            cf = null;
            for (DataRequirement.DataRequirementCodeFilterComponent drcf : dr.getCodeFilter()) {
                if (drcf.getPath().equals("code") && drcf.getValueSet().equals("http://fhir.org/guides/cdc/opioid-cds/ValueSet/pcp-medications")) {
                    cf = drcf;
                    break;
                }
            }
            assertNotNull(cf, "Expected filter on code");

        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Test
    public void TestDataRequirementsProcessorWithExpressions() {
        CqlTranslatorOptions cqlTranslatorOptions = new CqlTranslatorOptions();
        cqlTranslatorOptions.getFormats().add(CqlTranslator.Format.JSON);
        try {
            Set<String> expressions = new HashSet<>();
            // TODO - add expressions to expressions
            expressions.add("Conditions Indicating End of Life or With Limited Life Expectancy");//Active Ambulatory Opioid Rx");
            CqlTranslator translator = createTranslator("OpioidCDS/cql/OpioidCDSCommon.cql", cqlTranslatorOptions);
            translator.toELM();
            assertTrue(translator.getErrors().isEmpty());
            libraryManager.cacheLibrary(translator.getTranslatedLibrary());
            DataRequirementsProcessor dqReqTrans = new DataRequirementsProcessor();
            org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = dqReqTrans.gatherDataRequirements(libraryManager, translator.getTranslatedLibrary(), cqlTranslatorOptions, expressions, false);
            assertTrue(moduleDefinitionLibrary.getType().getCode("http://terminology.hl7.org/CodeSystem/library-type").equalsIgnoreCase("module-definition"));

            List<Extension> directReferenceCodes = moduleDefinitionLibrary.getExtensionsByUrl("http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-directReferenceCode");
            assertTrue(directReferenceCodes.size() == 4);
            Extension directReferenceCode = directReferenceCodes.get(0);
            Coding coding = directReferenceCode.getValueCoding();
            assertEquals("http://hl7.org/fhir/condition-category", coding.getSystem());
            assertEquals("encounter-diagnosis", coding.getCode());
            assertEquals("Encounter Diagnosis", coding.getDisplay());

            assertTrue(moduleDefinitionLibrary.getRelatedArtifact().size() == 6);
            RelatedArtifact conditionCategoryCodes = null;
            for (RelatedArtifact relatedArtifact : moduleDefinitionLibrary.getRelatedArtifact()) {
                if (relatedArtifact.getType() == RelatedArtifact.RelatedArtifactType.DEPENDSON
                    && relatedArtifact.getResource() != null && relatedArtifact.getResource().equals("http://hl7.org/fhir/condition-category")) {
                    conditionCategoryCodes = relatedArtifact;
                    break;
                }
            }
            assertTrue(conditionCategoryCodes != null);

            assertTrue(moduleDefinitionLibrary.getParameter().size() == 1);
            ParameterDefinition conditionsIndicatingEndOfLife = null;
            for (ParameterDefinition parameter : moduleDefinitionLibrary.getParameter()) {
                if (parameter.getName().equals("Conditions Indicating End of Life or With Limited Life Expectancy")) {
                    conditionsIndicatingEndOfLife = parameter;
                    break;
                }
            }
            assertTrue(conditionsIndicatingEndOfLife != null);

            assertTrue(moduleDefinitionLibrary.getDataRequirement().size() == 3);
            DataRequirement diagnosisRequirement = null;
            for (DataRequirement requirement : moduleDefinitionLibrary.getDataRequirement()) {
                if (requirement.getType() == Enumerations.FHIRAllTypes.CONDITION && requirement.getCodeFilter().size() == 1) {
                    DataRequirement.DataRequirementCodeFilterComponent cfc = requirement.getCodeFilterFirstRep();
                    if (cfc.hasPath() && cfc.getPath().equals("category")
                            && cfc.getCode().size() == 1
                            && cfc.getCodeFirstRep().hasCode()
                            && cfc.getCodeFirstRep().getCode().equals("encounter-diagnosis")) {
                        diagnosisRequirement = requirement;
                        break;
                    }
                }
            }
            assertTrue(diagnosisRequirement != null);

            FhirContext context =  getFhirContext();
            IParser parser = context.newJsonParser();
            String moduleDefString = parser.setPrettyPrint(true).encodeResourceToString(moduleDefinitionLibrary);
            logger.debug(moduleDefString);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Test
    public void TestLibraryDataRequirements() {
        CqlTranslatorOptions cqlTranslatorOptions = new CqlTranslatorOptions();
        cqlTranslatorOptions.getFormats().add(CqlTranslator.Format.JSON);
        try {
//            CqlTranslator translator = createTranslator("/ecqm/resources/library-EXM506-2.2.000.json", cqlTranslatorOptions);
            CqlTranslator translator = createTranslator("CompositeMeasures/cql/BCSComponent.cql", cqlTranslatorOptions);
            translator.toELM();
            assertTrue(translator.getErrors().isEmpty());
            libraryManager.cacheLibrary(translator.getTranslatedLibrary());
            DataRequirementsProcessor dqReqTrans = new DataRequirementsProcessor();
            org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = dqReqTrans.gatherDataRequirements(libraryManager, translator.getTranslatedLibrary(), cqlTranslatorOptions, null, false);
            assertTrue(moduleDefinitionLibrary.getType().getCode("http://terminology.hl7.org/CodeSystem/library-type").equalsIgnoreCase("module-definition"));

            List<Extension> directReferenceCodes = moduleDefinitionLibrary.getExtensionsByUrl("http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-directReferenceCode");
            assertTrue(directReferenceCodes.size() == 5);
            Extension directReferenceCode = directReferenceCodes.get(0);
            Coding coding = directReferenceCode.getValueCoding();
            assertEquals("http://loinc.org", coding.getSystem());
            assertEquals("21112-8", coding.getCode());
            assertEquals("Birth date", coding.getDisplay());

            assertTrue(moduleDefinitionLibrary.getRelatedArtifact().size() >= 45);
            RelatedArtifact loincCodeSystem = null;
            for (RelatedArtifact relatedArtifact : moduleDefinitionLibrary.getRelatedArtifact()) {
                if (relatedArtifact.getType() == RelatedArtifact.RelatedArtifactType.DEPENDSON
                        && relatedArtifact.getResource() != null && relatedArtifact.getResource().equals("http://loinc.org")) {
                    loincCodeSystem = relatedArtifact;
                    break;
                }
            }
            assertTrue(loincCodeSystem != null);

            assertTrue(moduleDefinitionLibrary.getParameter().size() >= 16);
            ParameterDefinition measurementPeriod = null;
            for (ParameterDefinition parameter : moduleDefinitionLibrary.getParameter()) {
                if (parameter.getName().equals("Measurement Period")) {
                    measurementPeriod = parameter;
                    break;
                }
            }
            assertTrue(measurementPeriod != null);

            assertTrue(moduleDefinitionLibrary.getDataRequirement().size() >= 15);
            DataRequirement diagnosisRequirement = null;
            for (DataRequirement requirement : moduleDefinitionLibrary.getDataRequirement()) {
                if (requirement.getType() == Enumerations.FHIRAllTypes.CONDITION && requirement.getCodeFilter().size() == 1) {
                    DataRequirement.DataRequirementCodeFilterComponent cfc = requirement.getCodeFilterFirstRep();
                    if (cfc.hasPath() && cfc.getPath().equals("code")
                            && cfc.hasValueSet()
                            && cfc.getValueSet().equals("http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.464.1003.198.12.1071")) {
                        diagnosisRequirement = requirement;
                        break;
                    }
                }
            }
            assertTrue(diagnosisRequirement != null);

            FhirContext context =  getFhirContext();
            IParser parser = context.newJsonParser();
            String moduleDefString = parser.setPrettyPrint(true).encodeResourceToString(moduleDefinitionLibrary);
            logger.debug(moduleDefString);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Test
    public void TestLibraryDataRequirementsRecursive() {
        CqlTranslatorOptions cqlTranslatorOptions = new CqlTranslatorOptions();
        cqlTranslatorOptions.getFormats().add(CqlTranslator.Format.JSON);
        cqlTranslatorOptions.setCollapseDataRequirements(true);
        try {
            CqlTranslator translator = createTranslator("DataRequirements/DataRequirementsLibraryTest.cql", cqlTranslatorOptions);
            translator.toELM();
            assertTrue(translator.getErrors().isEmpty());
            libraryManager.cacheLibrary(translator.getTranslatedLibrary());

            DataRequirementsProcessor dqReqTrans = new DataRequirementsProcessor();
            org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = dqReqTrans.gatherDataRequirements(libraryManager, translator.getTranslatedLibrary(), cqlTranslatorOptions, null, false);
            assertTrue(moduleDefinitionLibrary.getType().getCode("http://terminology.hl7.org/CodeSystem/library-type").equalsIgnoreCase("module-definition"));
            DataRequirement encounterRequirement = null;
            for (DataRequirement dr : moduleDefinitionLibrary.getDataRequirement()) {
                if (dr.getType() == Enumerations.FHIRAllTypes.ENCOUNTER) {
                    encounterRequirement = dr;
                    break;
                }
            }
            assertTrue(encounterRequirement != null);

            FhirContext context =  getFhirContext();
            IParser parser = context.newJsonParser();
            String moduleDefString = parser.setPrettyPrint(true).encodeResourceToString(moduleDefinitionLibrary);
            logger.debug(moduleDefString);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Test
    public void TestDataRequirementsFHIRReferences() {
        CqlTranslatorOptions cqlTranslatorOptions = new CqlTranslatorOptions();
        cqlTranslatorOptions.getFormats().add(CqlTranslator.Format.JSON);
        try {
            CqlTranslator translator = createTranslator("FHIRReferencesRevisited.cql", cqlTranslatorOptions);
            translator.toELM();
            assertTrue(translator.getErrors().isEmpty());
            libraryManager.cacheLibrary(translator.getTranslatedLibrary());
            DataRequirementsProcessor dqReqTrans = new DataRequirementsProcessor();
            org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = dqReqTrans.gatherDataRequirements(libraryManager, translator.getTranslatedLibrary(), cqlTranslatorOptions, null, false);

            FhirContext context =  getFhirContext();
            IParser parser = context.newJsonParser();
            String moduleDefString = parser.setPrettyPrint(true).encodeResourceToString(moduleDefinitionLibrary);
            logger.debug(moduleDefString);
            // TODO: Validate consolidation of requirements
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private CqlTranslatorOptions getTranslatorOptions() {
        CqlTranslatorOptions cqlTranslatorOptions = new CqlTranslatorOptions();
        cqlTranslatorOptions.getFormats().add(CqlTranslator.Format.JSON);
        return cqlTranslatorOptions;
    }

    private CqlTranslator setupDataRequirementsGather(String fileName, CqlTranslatorOptions cqlTranslatorOptions) throws IOException {
        cqlTranslatorOptions.setCollapseDataRequirements(true);
        cqlTranslatorOptions.setAnalyzeDataRequirements(false);
        CqlTranslator translator = createTranslator(fileName, cqlTranslatorOptions);
        translator.toELM();
        assertTrue(translator.getErrors().isEmpty());
        libraryManager.cacheLibrary(translator.getTranslatedLibrary());
        return translator;
    }

    private CqlTranslator setupDataRequirementsAnalysis(String fileName, CqlTranslatorOptions cqlTranslatorOptions) throws IOException {
        cqlTranslatorOptions.setCollapseDataRequirements(true);
        cqlTranslatorOptions.setAnalyzeDataRequirements(true);
        CqlTranslator translator = createTranslator(fileName, cqlTranslatorOptions);
        translator.toELM();
        assertTrue(translator.getErrors().isEmpty());
        libraryManager.cacheLibrary(translator.getTranslatedLibrary());
        return translator;
    }

    private org.hl7.fhir.r5.model.Library getModuleDefinitionLibrary(CqlTranslator translator, CqlTranslatorOptions cqlTranslatorOptions) {
        DataRequirementsProcessor dqReqTrans = new DataRequirementsProcessor();
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = dqReqTrans.gatherDataRequirements(libraryManager, translator.getTranslatedLibrary(), cqlTranslatorOptions, null, false);
        assertTrue(moduleDefinitionLibrary.getType().getCode("http://terminology.hl7.org/CodeSystem/library-type").equalsIgnoreCase("module-definition"));
        return moduleDefinitionLibrary;
    }

    private org.hl7.fhir.r5.model.Library getModuleDefinitionLibrary(CqlTranslator translator, CqlTranslatorOptions cqlTranslatorOptions, Set<String> expressions) {
        DataRequirementsProcessor dqReqTrans = new DataRequirementsProcessor();
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = dqReqTrans.gatherDataRequirements(libraryManager, translator.getTranslatedLibrary(), cqlTranslatorOptions, expressions, false);
        assertTrue(moduleDefinitionLibrary.getType().getCode("http://terminology.hl7.org/CodeSystem/library-type").equalsIgnoreCase("module-definition"));
        return moduleDefinitionLibrary;
    }

    private void outputModuleDefinitionLibrary(org.hl7.fhir.r5.model.Library moduleDefinitionLibrary) {
        FhirContext context =  getFhirContext();
        IParser parser = context.newJsonParser();
        String moduleDefString = parser.setPrettyPrint(true).encodeResourceToString(moduleDefinitionLibrary);
        System.out.println(moduleDefString);
    }

    private Iterable<DataRequirement> getDataRequirementsForType(Iterable<DataRequirement> dataRequirements, Enumerations.FHIRAllTypes type) {
        List<DataRequirement> results = new ArrayList<DataRequirement>();
        for (DataRequirement dr : dataRequirements) {
            if (dr.getType() == type) {
                results.add(dr);
            }
        }
        return results;
    }

    @Test
    public void TestFunctionDataRequirements() throws IOException {
        CqlTranslatorOptions translatorOptions = getTranslatorOptions();
        CqlTranslator translator = setupDataRequirementsGather("CMS104/MATGlobalCommonFunctionsFHIR4.cql", translatorOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(translator, translatorOptions, Collections.singleton("PrincipalDiagnosis"));

        // DataRequirements of the PrinicipalDiagnosis function:
            // [Condition]
        Iterable<DataRequirement> expectedDataRequirements = getDataRequirementsForType(moduleDefinitionLibrary.getDataRequirement(), Enumerations.FHIRAllTypes.CONDITION);
        assertTrue(expectedDataRequirements.iterator().hasNext());
        outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestNonElectiveInpatientEncounterDataRequirements() throws IOException {
        CqlTranslatorOptions translatorOptions = getTranslatorOptions();
        CqlTranslator translator = setupDataRequirementsGather("CMS104/TJCOverallFHIR.cql", translatorOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(translator, translatorOptions, Collections.singleton("Non Elective Inpatient Encounter"));

        // DataRequirements of the Non Elective Inpatient Encounter expression:
            // [Encounter: "Non-Elective Inpatient Encounter"]
        Iterable<DataRequirement> actualDataRequirements = getDataRequirementsForType(moduleDefinitionLibrary.getDataRequirement(), Enumerations.FHIRAllTypes.ENCOUNTER);
        assertTrue(actualDataRequirements.iterator().hasNext());
        DataRequirement dr = actualDataRequirements.iterator().next();
        DataRequirement.DataRequirementCodeFilterComponent actualDrcf = null;
        for (DataRequirement.DataRequirementCodeFilterComponent drcf : dr.getCodeFilter()) {
            if ("type".equals(drcf.getPath()) && "http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.424".equals(drcf.getValueSet())) {
                actualDrcf = drcf;
                break;
            }
        }
        assertTrue(actualDrcf != null);
        outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestAllStrokeEncounterDataRequirements() throws IOException {
        CqlTranslatorOptions translatorOptions = getTranslatorOptions();
        CqlTranslator translator = setupDataRequirementsGather("CMS104/TJCOverallFHIR.cql", translatorOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(translator, translatorOptions, Collections.singleton("All Stroke Encounter"));

        // DataRequirements of the All Stroke Encounter expression:
            // [Encounter: "Non-Elective Inpatient Encounter"]          (from Non Elective Inpatient Encounter)
            // [Condition]                                              (from PrincipalDiagnosis)
        Iterable<DataRequirement> encounterDataRequirements = getDataRequirementsForType(moduleDefinitionLibrary.getDataRequirement(), Enumerations.FHIRAllTypes.ENCOUNTER);
        assertTrue(encounterDataRequirements.iterator().hasNext());
        DataRequirement dr = encounterDataRequirements.iterator().next();
        DataRequirement.DataRequirementCodeFilterComponent actualDrcf = null;
        for (DataRequirement.DataRequirementCodeFilterComponent drcf : dr.getCodeFilter()) {
            if ("type".equals(drcf.getPath()) && "http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.424".equals(drcf.getValueSet())) {
                actualDrcf = drcf;
                break;
            }
        }
        assertTrue(actualDrcf != null);

        Iterable<DataRequirement> conditionDataRequirements = getDataRequirementsForType(moduleDefinitionLibrary.getDataRequirement(), Enumerations.FHIRAllTypes.CONDITION);
        assertTrue(conditionDataRequirements.iterator().hasNext());

        outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestCMS104DataRequirements() throws IOException {
        CqlTranslatorOptions translatorOptions = getTranslatorOptions();
        CqlTranslator translator = setupDataRequirementsGather("CMS104/DischargedonAntithromboticTherapyFHIR.cql", translatorOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(translator, translatorOptions, null);

        // DataRequirements of the All Stroke Encounter expression:
        // [Encounter: "Non-Elective Inpatient Encounter"]          (from Non Elective Inpatient Encounter)
        // [Condition]                                              (from PrincipalDiagnosis)
        Iterable<DataRequirement> encounterDataRequirements = getDataRequirementsForType(moduleDefinitionLibrary.getDataRequirement(), Enumerations.FHIRAllTypes.ENCOUNTER);
        DataRequirement.DataRequirementCodeFilterComponent actualDrcf = null;
        for (DataRequirement dr : encounterDataRequirements) {
            for (DataRequirement.DataRequirementCodeFilterComponent drcf : dr.getCodeFilter()) {
                if ("type".equals(drcf.getPath()) && "http://cts.nlm.nih.gov/fhir/ValueSet/2.16.840.1.113883.3.117.1.7.1.424".equals(drcf.getValueSet())) {
                    actualDrcf = drcf;
                    break;
                }
            }
            if (actualDrcf != null) {
                break;
            }
        }
        assertTrue(actualDrcf != null);

        Iterable<DataRequirement> conditionDataRequirements = getDataRequirementsForType(moduleDefinitionLibrary.getDataRequirement(), Enumerations.FHIRAllTypes.CONDITION);
        assertTrue(conditionDataRequirements.iterator().hasNext());

        outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestDataRequirementsAnalysisCase1() throws IOException {
        CqlTranslatorOptions translatorOptions = getTranslatorOptions();
        CqlTranslator translator = setupDataRequirementsAnalysis("TestCases/TestCase1.cql", translatorOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(translator, translatorOptions);

        /*
        1.
        Stated DataRequirement: ERSD Observations
        type: Observation
        codeFilter: { path:  code, valueSet:  'http://fakeurl.com/ersd-diagnosis' }
        */

        // Validate the ELM is correct
        ExpressionDef ed = translator.getTranslatedLibrary().resolveExpressionRef("ESRD Observations");
        assertTrue(ed.getExpression() instanceof Retrieve);
        Retrieve r = (Retrieve)ed.getExpression();
        assertEquals(r.getCodeProperty(), "code");
        assertTrue(r.getCodes() instanceof ValueSetRef);
        assertEquals(((ValueSetRef)r.getCodes()).getName(), "ESRD Diagnosis");

        // Validate the data requirement is reported in the module definition library
        DataRequirement expectedDataRequirement = null;
        for (DataRequirement dr : moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == Enumerations.FHIRAllTypes.OBSERVATION) {
                if (dr.getCodeFilter().size() == 1) {
                    DataRequirement.DataRequirementCodeFilterComponent cfc = dr.getCodeFilterFirstRep();
                    if ("code".equals(cfc.getPath())) {
                        if ("http://fakeurl.com/ersd-diagnosis".equals(cfc.getValueSet())) {
                            expectedDataRequirement = dr;
                        }
                    }
                }
            }
        }
        assertTrue(expectedDataRequirement != null);

        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestDataRequirementsAnalysisCase1b() throws IOException {
        CqlTranslatorOptions translatorOptions = getTranslatorOptions();
        CqlTranslator translator = setupDataRequirementsAnalysis("TestCases/TestCase1b.cql", translatorOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(translator, translatorOptions);

        /*
        1b. Similar to 1, but not on a primary code path and with a constant
        DataRequirement
        type: Observation
        codeFilter: { path: status, code: Coding { code: 'final'}}
        */

        // Validate the ELM is correct
        ExpressionDef ed = translator.getTranslatedLibrary().resolveExpressionRef("Observations");
        assertTrue(ed.getExpression() instanceof Query);
        Query q = (Query)ed.getExpression();
        assertTrue(q.getSource() != null && q.getSource().size() == 1);
        AliasedQuerySource source = q.getSource().get(0);
        assertTrue(source.getExpression() instanceof Retrieve);
        Retrieve r = (Retrieve)source.getExpression();
        assertTrue(r.getCodeProperty() == null);
        assertTrue(r.getCodes() == null);
        assertTrue(r.getCodeFilter() != null);
        assertTrue(r.getCodeFilter().size() == 1);
        CodeFilterElement cfe = r.getCodeFilter().get(0);
        assertEquals(cfe.getProperty(), "status");
        assertEquals(cfe.getComparator(), "=");
        assertTrue(cfe.getValue() instanceof Literal);
        assertEquals(((Literal)cfe.getValue()).getValue(), "final");

        // Validate the data requirement is reported in the module definition library
        DataRequirement expectedDataRequirement = null;
        for (DataRequirement dr : moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == Enumerations.FHIRAllTypes.OBSERVATION) {
                if (dr.getCodeFilter().size() == 1) {
                    DataRequirement.DataRequirementCodeFilterComponent cfc = dr.getCodeFilterFirstRep();
                    if ("status".equals(cfc.getPath())) {
                        if (cfc.getCode().size() == 1) {
                            Coding coding = cfc.getCodeFirstRep();
                            if ("final".equals(coding.getCode())) {
                                expectedDataRequirement = dr;
                            }
                        }
                    }
                }
            }
        }
        assertTrue(expectedDataRequirement != null);

        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestDataRequirementsAnalysisCase1c() throws IOException {
        CqlTranslatorOptions translatorOptions = getTranslatorOptions();
        CqlTranslator translator = setupDataRequirementsAnalysis("TestCases/TestCase1c.cql", translatorOptions);
        Set<String> expressions = new HashSet<>();
        expressions.add("TestReferencedDataRequirement");
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(translator, translatorOptions, expressions);

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
        ParameterDefinition expectedParameterDefinition = null;
        assertEquals(moduleDefinitionLibrary.getParameter().size(), 1);
        for (ParameterDefinition pd : moduleDefinitionLibrary.getParameter()) {
            if ("TestReferencedDataRequirement".equals(pd.getName()) && pd.getUse() == Enumerations.OperationParameterUse.OUT
            && pd.hasMin() && pd.getMin() == 0 && "*".equals(pd.getMax()) && pd.getType() == Enumerations.FHIRAllTypes.MEDICATION) {
                expectedParameterDefinition = pd;
            }
        }
        assertTrue(expectedParameterDefinition != null);

        // Validate the data requirement is reported correctly in the module definition library

        DataRequirement expectedDataRequirement = null;
        // TODO: This really should be 1, but we're using the recursive gather, so it reports the [Medication] retrieve in the referenced expression as well
        assertEquals(moduleDefinitionLibrary.getDataRequirement().size(), 2);
        for (DataRequirement dr : moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == Enumerations.FHIRAllTypes.MEDICATION) {
                if (dr.getCodeFilter().size() == 1) {
                    DataRequirement.DataRequirementCodeFilterComponent cfc = dr.getCodeFilterFirstRep();
                    if ("code".equals(cfc.getPath())) {
                        if ("http://example.org/fhir/ValueSet/aspirin".equals(cfc.getValueSet())) {
                            expectedDataRequirement = dr;
                            break;
                        }
                    }
                }
            }
        }
        assertTrue(expectedDataRequirement != null);

        outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestDataRequirementsAnalysisCase2a() throws IOException {
        CqlTranslatorOptions translatorOptions = getTranslatorOptions();
        CqlTranslator translator = setupDataRequirementsAnalysis("TestCases/TestCase2a.cql", translatorOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(translator, translatorOptions);

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
        ExpressionDef ed = translator.getTranslatedLibrary().resolveExpressionRef("HospiceEncounterClaimsA");
        assertTrue(ed.getExpression() instanceof Query);
        Query q = (Query)ed.getExpression();
        assertTrue(q.getSource() != null && q.getSource().size() == 1);
        AliasedQuerySource source = q.getSource().get(0);
        assertTrue(source.getExpression() instanceof Retrieve);
        Retrieve r = (Retrieve)source.getExpression();
        assertTrue(r.getCodeProperty() == null);
        assertTrue(r.getCodes() == null);
        assertTrue(r.getCodeFilter() != null);
        assertTrue(r.getCodeFilter().size() == 1);
        CodeFilterElement cfe = r.getCodeFilter().get(0);
        assertEquals(cfe.getProperty(), "item.revenue");
        assertEquals(cfe.getComparator(), "in");
        assertTrue(cfe.getValue() instanceof ValueSetRef);
        assertEquals(((ValueSetRef)cfe.getValue()).getName(), "Hospice Encounter");

        // Validate the data requirement is reported in the module definition library
        DataRequirement expectedDataRequirement = null;
        for (DataRequirement dr : moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == Enumerations.FHIRAllTypes.CLAIM) {
                if (dr.getCodeFilter().size() == 1) {
                    DataRequirement.DataRequirementCodeFilterComponent cfc = dr.getCodeFilterFirstRep();
                    if ("item.revenue".equals(cfc.getPath())) {
                        if ("http://fakeurl.com/hospice-encounter".equals(cfc.getValueSet())) {
                            expectedDataRequirement = dr;
                        }
                    }
                }
            }
        }
        assertTrue(expectedDataRequirement != null);

        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestDataRequirementsAnalysisCase2g() throws IOException {
        CqlTranslatorOptions translatorOptions = getTranslatorOptions();
        CqlTranslator translator = setupDataRequirementsAnalysis("TestCases/TestCase2g.cql", translatorOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(translator, translatorOptions);

        /*
        2g - Equal to a compile-time literal function
        DataRequirement
        type: Condition
        dateFilter: { path: onset, value: Today() }

        define DateTimeEqualToFunction:
          [Condition] C
            where C.onset as dateTime = Today()
        */

        ExpressionDef ed = translator.getTranslatedLibrary().resolveExpressionRef("DateTimeEqualToFunction");
        assertTrue(ed.getExpression() instanceof Query);
        Query q = (Query)ed.getExpression();
        assertTrue(q.getSource() != null && q.getSource().size() == 1);
        AliasedQuerySource source = q.getSource().get(0);
        assertTrue(source.getExpression() instanceof Retrieve);
        Retrieve r = (Retrieve)source.getExpression();
        assertTrue(r.getDateProperty() != null && r.getDateProperty().equals("onset"));
    }

    @Test
    public void TestDataRequirementsAnalysisCase2b() throws IOException {
        CqlTranslatorOptions translatorOptions = getTranslatorOptions();
        CqlTranslator translator = setupDataRequirementsAnalysis("TestCases/TestCase2b.cql", translatorOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(translator, translatorOptions);

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
        ExpressionDef ed = translator.getTranslatedLibrary().resolveExpressionRef("HospiceEncounterClaimsBBoundDate");
        assertTrue(ed.getExpression() instanceof Query);
        Query q = (Query)ed.getExpression();
        assertTrue(q.getSource() != null && q.getSource().size() == 1);
        AliasedQuerySource source = q.getSource().get(0);
        assertTrue(source.getExpression() instanceof Retrieve);
        Retrieve r = (Retrieve)source.getExpression();
        assertTrue(r.getDateProperty() == null);
        assertTrue(r.getDateRange() == null);
        assertTrue(r.getDateFilter() != null);
        assertTrue(r.getDateFilter().size() == 1);
        DateFilterElement dfe = r.getDateFilter().get(0);
        assertEquals(dfe.getProperty(), "item.serviced.start");
        assertTrue(dfe.getValue() instanceof ParameterRef);
        assertEquals(((ParameterRef)dfe.getValue()).getName(), "Measurement Period");

        // Validate the data requirement is reported in the module definition library
        DataRequirement expectedDataRequirement = null;
        for (DataRequirement dr : moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == Enumerations.FHIRAllTypes.CLAIM) {
                if (dr.getDateFilter().size() == 1) {
                    DataRequirement.DataRequirementDateFilterComponent dfc = dr.getDateFilterFirstRep();
                    if ("item.serviced.start".equals(dfc.getPath())) {
                        Extension e = dfc.getValue().getExtensionByUrl("http://hl7.org/fhir/StructureDefinition/cqf-expression");
                        if (e != null && e.getValueExpression() != null) {
                            if ("Measurement Period".equals(e.getValueExpression().getExpression())) {
                                expectedDataRequirement = dr;
                            }
                        }
                    }
                }
            }
        }
        assertTrue(expectedDataRequirement != null);

        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestDataRequirementsAnalysisCase9a() throws IOException {
        CqlTranslatorOptions translatorOptions = getTranslatorOptions();
        CqlTranslator translator = setupDataRequirementsAnalysis("TestCases/TestCase9a.cql", translatorOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(translator, translatorOptions);

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
        ExpressionDef ed = translator.getTranslatedLibrary().resolveExpressionRef("MedicationRequestWithEncounter");
        assertTrue(ed.getExpression() instanceof Query);
        Query q = (Query)ed.getExpression();
        assertTrue(q.getSource() != null && q.getSource().size() == 1);
        AliasedQuerySource source = q.getSource().get(0);
        assertTrue(source.getExpression() instanceof Retrieve);
        Retrieve r = (Retrieve)source.getExpression();
        assertTrue(r.getDataType().getLocalPart().equals("MedicationRequest"));
        assertTrue(r.getInclude().size() == 1);
        String primarySourceId = r.getLocalId();
        IncludeElement ie = r.getInclude().get(0);
        assertTrue(ie.getRelatedDataType().getLocalPart().equals("Encounter"));
        assertTrue(ie.getRelatedProperty().equals("encounter.reference"));
        assertTrue(!ie.isIsReverse());
        assertTrue(q.getRelationship().size() == 1);
        assertTrue(q.getRelationship().get(0) instanceof With);
        With w = (With)q.getRelationship().get(0);
        assertTrue(w.getExpression() instanceof Retrieve);
        r = (Retrieve)w.getExpression();
        assertTrue(r.getDataType().getLocalPart().equals("Encounter"));
        assertTrue(r.getIncludedIn().equals(primarySourceId));

        // Validate the data requirement is reported in the module definition library
        DataRequirement expectedDataRequirement = null;
        for (DataRequirement dr : moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == Enumerations.FHIRAllTypes.MEDICATIONREQUEST) {
                expectedDataRequirement = dr;
            }
        }
        assertTrue(expectedDataRequirement != null);

        DataRequirement includedDataRequirement = null;
        for (DataRequirement dr : moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == Enumerations.FHIRAllTypes.ENCOUNTER) {
                Extension e = dr.getExtensionByUrl("http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-relatedRequirement");
                if (e != null) {
                    Extension targetId = e.getExtensionByUrl("targetId");
                    Extension targetProperty = e.getExtensionByUrl("targetProperty");
                    if (targetId != null && targetProperty != null && targetProperty.getValueStringType().getValue().equals("encounter")) {
                        includedDataRequirement = dr;
                    }
                }
            }
        }
        assertTrue(includedDataRequirement != null);

        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    //@Test
    // TODO: Enable include when the reference is in a let
    public void TestDataRequirementsAnalysisCase9d() throws IOException {
        CqlTranslatorOptions translatorOptions = getTranslatorOptions();
        CqlTranslator translator = setupDataRequirementsAnalysis("TestCases/TestCase9d.cql", translatorOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(translator, translatorOptions);

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
        ExpressionDef ed = translator.getTranslatedLibrary().resolveExpressionRef("MedicationRequestWithAspirinInLet");
        assertTrue(ed.getExpression() instanceof Query);
        Query q = (Query)ed.getExpression();
        assertTrue(q.getSource() != null && q.getSource().size() == 1);
        AliasedQuerySource source = q.getSource().get(0);
        assertTrue(source.getExpression() instanceof Retrieve);
        Retrieve r = (Retrieve)source.getExpression();
        assertTrue(r.getDataType().getLocalPart().equals("MedicationRequest"));
        assertTrue(r.getInclude().size() == 1);
        String primarySourceId = r.getLocalId();
        IncludeElement ie = r.getInclude().get(0);
        assertTrue(ie.getRelatedDataType().getLocalPart().equals("Medication"));
        assertTrue(ie.getRelatedProperty().equals("medication.reference"));
        assertTrue(!ie.isIsReverse());

        assertTrue(q.getLet().size() == 1);
        LetClause lc = q.getLet().get(0);
        assertTrue(lc.getExpression() instanceof SingletonFrom);
        SingletonFrom sf = (SingletonFrom)lc.getExpression();
        assertTrue(sf.getOperand() instanceof Query);
        q = (Query)sf.getOperand();
        assertTrue(q.getSource().size() == 1);
        source = q.getSource().get(0);
        assertTrue(source.getExpression() instanceof Retrieve);
        r = (Retrieve)source.getExpression();
        assertTrue(r.getDataType().getLocalPart().equals("Medication"));
        assertTrue(r.getIncludedIn().equals(primarySourceId));
        assertTrue(r.getCodeFilter().size() == 2);
        CodeFilterElement cfe = r.getCodeFilter().get(0);
        assertTrue(cfe.getProperty().equals("id"));
        assertTrue(cfe.getComparator().equals("="));
        cfe = r.getCodeFilter().get(1);
        assertTrue(cfe.getProperty().equals("code"));
        assertTrue(cfe.getComparator().equals("in"));

        // Validate the data requirement is reported in the module definition library
        DataRequirement expectedDataRequirement = null;
        for (DataRequirement dr : moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == Enumerations.FHIRAllTypes.MEDICATIONREQUEST) {
                expectedDataRequirement = dr;
            }
        }
        assertTrue(expectedDataRequirement != null);

        DataRequirement includedDataRequirement = null;
        for (DataRequirement dr : moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == Enumerations.FHIRAllTypes.MEDICATION) {
                Extension e = dr.getExtensionByUrl("http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-relatedRequirement");
                if (e != null) {
                    Extension targetId = e.getExtensionByUrl("targetId");
                    Extension targetProperty = e.getExtensionByUrl("targetProperty");
                    if (targetId != null && targetProperty != null && targetProperty.getValueStringType().getValue().equals("medication")) {
                        includedDataRequirement = dr;
                    }
                }
            }
        }
        assertTrue(includedDataRequirement != null);

        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestDataRequirementsAnalysisCase9e() throws IOException {
        CqlTranslatorOptions translatorOptions = getTranslatorOptions();
        CqlTranslator translator = setupDataRequirementsAnalysis("TestCases/TestCase9e.cql", translatorOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(translator, translatorOptions);

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
        ExpressionDef ed = translator.getTranslatedLibrary().resolveExpressionRef("MedicationRequestWithAspirinInWhere");
        assertTrue(ed.getExpression() instanceof Query);
        Query q = (Query)ed.getExpression();
        assertTrue(q.getSource() != null && q.getSource().size() == 1);
        AliasedQuerySource source = q.getSource().get(0);
        assertTrue(source.getExpression() instanceof Retrieve);
        Retrieve r = (Retrieve)source.getExpression();
        assertTrue(r.getDataType().getLocalPart().equals("MedicationRequest"));
        assertTrue(r.getInclude().size() == 1);
        String primarySourceId = r.getLocalId();
        IncludeElement ie = r.getInclude().get(0);
        assertTrue(ie.getRelatedDataType().getLocalPart().equals("Medication"));
        assertTrue(ie.getRelatedProperty().equals("medication.reference"));
        assertTrue(!ie.isIsReverse());

        assertTrue(q.getWhere() != null);
        assertTrue(q.getWhere() instanceof Exists);
        Exists ex = (Exists)q.getWhere();
        assertTrue(ex.getOperand() instanceof Query);
        q = (Query)ex.getOperand();
        assertTrue(q.getSource().size() == 1);
        source = q.getSource().get(0);
        assertTrue(source.getExpression() instanceof Retrieve);
        r = (Retrieve)source.getExpression();
        assertTrue(r.getDataType().getLocalPart().equals("Medication"));
        assertTrue(r.getIncludedIn().equals(primarySourceId));
        assertTrue(r.getCodeFilter().size() == 1);
        CodeFilterElement cfe = r.getCodeFilter().get(0);
        assertTrue(cfe.getProperty().equals("code"));
        assertTrue(cfe.getComparator().equals("in"));

        // Validate the data requirement is reported in the module definition library
        DataRequirement expectedDataRequirement = null;
        for (DataRequirement dr : moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == Enumerations.FHIRAllTypes.MEDICATIONREQUEST) {
                expectedDataRequirement = dr;
            }
        }
        assertTrue(expectedDataRequirement != null);

        DataRequirement includedDataRequirement = null;
        for (DataRequirement dr : moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == Enumerations.FHIRAllTypes.MEDICATION) {
                Extension e = dr.getExtensionByUrl("http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-relatedRequirement");
                if (e != null) {
                    Extension targetId = e.getExtensionByUrl("targetId");
                    Extension targetProperty = e.getExtensionByUrl("targetProperty");
                    if (targetId != null && targetProperty != null && targetProperty.getValueStringType().getValue().equals("medication")) {
                        includedDataRequirement = dr;
                    }
                }
            }
        }
        assertTrue(includedDataRequirement != null);

        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestDataRequirementsAnalysisCase9f() throws IOException {
        CqlTranslatorOptions translatorOptions = getTranslatorOptions();
        CqlTranslator translator = setupDataRequirementsAnalysis("TestCases/TestCase9f.cql", translatorOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(translator, translatorOptions);

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
        ExpressionDef ed = translator.getTranslatedLibrary().resolveExpressionRef("MedicationRequestWithAspirinInFrom");
        assertTrue(ed.getExpression() instanceof Query);
        Query q = (Query)ed.getExpression();
        assertTrue(q.getSource() != null && q.getSource().size() == 2);
        AliasedQuerySource source = q.getSource().get(0);
        assertTrue(source.getExpression() instanceof Retrieve);
        Retrieve r = (Retrieve)source.getExpression();
        assertTrue(r.getDataType().getLocalPart().equals("MedicationRequest"));
        assertTrue(r.getInclude().size() == 1);
        String primarySourceId = r.getLocalId();
        IncludeElement ie = r.getInclude().get(0);
        assertTrue(ie.getRelatedDataType().getLocalPart().equals("Medication"));
        assertTrue(ie.getRelatedProperty().equals("medication.reference"));
        assertTrue(!ie.isIsReverse());

        source = q.getSource().get(1);
        assertTrue(source.getExpression() instanceof Retrieve);
        r = (Retrieve)source.getExpression();
        assertTrue(r.getDataType().getLocalPart().equals("Medication"));
        assertTrue(r.getIncludedIn().equals(primarySourceId));
        assertTrue(r.getCodeFilter().size() == 1);
        CodeFilterElement cfe = r.getCodeFilter().get(0);
        assertTrue(cfe.getProperty().equals("code"));
        assertTrue(cfe.getComparator().equals("in"));

        // Validate the data requirement is reported in the module definition library
        DataRequirement expectedDataRequirement = null;
        for (DataRequirement dr : moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == Enumerations.FHIRAllTypes.MEDICATIONREQUEST) {
                expectedDataRequirement = dr;
            }
        }
        assertTrue(expectedDataRequirement != null);

        DataRequirement includedDataRequirement = null;
        for (DataRequirement dr : moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == Enumerations.FHIRAllTypes.MEDICATION) {
                Extension e = dr.getExtensionByUrl("http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-relatedRequirement");
                if (e != null) {
                    Extension targetId = e.getExtensionByUrl("targetId");
                    Extension targetProperty = e.getExtensionByUrl("targetProperty");
                    if (targetId != null && targetProperty != null && targetProperty.getValueStringType().getValue().equals("medication")) {
                        includedDataRequirement = dr;
                    }
                }
            }
        }
        assertTrue(includedDataRequirement != null);

        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestDataRequirementsAnalysisCase10a() throws IOException {
        CqlTranslatorOptions translatorOptions = getTranslatorOptions();
        CqlTranslator translator = setupDataRequirementsAnalysis("TestCases/TestCase10a.cql", translatorOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(translator, translatorOptions);

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
        ExpressionDef ed = translator.getTranslatedLibrary().resolveExpressionRef("ESRD Observations");
        assertTrue(ed.getExpression() instanceof Query);
        Query q = (Query)ed.getExpression();
        assertTrue(q.getSource() != null && q.getSource().size() == 1);
        AliasedQuerySource source = q.getSource().get(0);
        assertTrue(source.getExpression() instanceof Retrieve);
        Retrieve r = (Retrieve)source.getExpression();
        assertTrue(r.getDataType().getLocalPart().equals("Observation"));

        // Validate the data requirement is reported in the module definition library
        DataRequirement expectedDataRequirement = null;
        for (DataRequirement dr : moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == Enumerations.FHIRAllTypes.OBSERVATION) {
                expectedDataRequirement = dr;
            }
        }
        assertTrue(expectedDataRequirement != null);

        assertTrue(expectedDataRequirement.getMustSupport().size() == 2);
        boolean hasCode = false;
        assertTrue(expectedDataRequirement.getMustSupport().stream().filter(s -> s.getValue().equals("code")).count() == 1);
        assertTrue(expectedDataRequirement.getMustSupport().stream().filter(s -> s.getValue().equals("issued")).count() == 1);

        assertTrue(expectedDataRequirement.getCodeFilter().size() == 1);
        DataRequirement.DataRequirementCodeFilterComponent drcfc = expectedDataRequirement.getCodeFilter().get(0);
        assertTrue(drcfc.getPath().equals("code"));
        assertTrue(drcfc.getValueSet().equals("http://fakeurl.com/ersd-diagnosis"));

        assertTrue(expectedDataRequirement.getDateFilter().size() == 1);
        DataRequirement.DataRequirementDateFilterComponent drdfc = expectedDataRequirement.getDateFilter().get(0);
        LocalDate ld = LocalDate.of(2022, 2, 15);
        assertTrue(drdfc.getValuePeriod().getStart().compareTo(Date.from(ld.atStartOfDay(ZoneId.systemDefault()).toInstant())) == 0);

        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestDataRequirementsAnalysisCase10b() throws IOException {
        CqlTranslatorOptions translatorOptions = getTranslatorOptions();
        CqlTranslator translator = setupDataRequirementsAnalysis("TestCases/TestCase10b.cql", translatorOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(translator, translatorOptions);

        /*
        Multiple date elements referenced in a Coalesce

        define "ESRD Observations":
          [Observation: "ESRD Diagnosis"] O
            where Coalesce(O.effective, O.issued) same day or after @2022-02-15
        */

        // Validate the ELM is correct
        ExpressionDef ed = translator.getTranslatedLibrary().resolveExpressionRef("ESRD Observations");
        assertTrue(ed.getExpression() instanceof Query);
        Query q = (Query)ed.getExpression();
        assertTrue(q.getSource() != null && q.getSource().size() == 1);
        AliasedQuerySource source = q.getSource().get(0);
        assertTrue(source.getExpression() instanceof Retrieve);
        Retrieve r = (Retrieve)source.getExpression();
        assertTrue(r.getDataType().getLocalPart().equals("Observation"));

        // Validate the data requirement is reported in the module definition library
        DataRequirement expectedDataRequirement = null;
        for (DataRequirement dr : moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == Enumerations.FHIRAllTypes.OBSERVATION) {
                expectedDataRequirement = dr;
            }
        }
        assertTrue(expectedDataRequirement != null);

        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestDataRequirementsAnalysisCase10c() throws IOException {
        // TODO: Complete this test case
        CqlTranslatorOptions translatorOptions = getTranslatorOptions();
        CqlTranslator translator = setupDataRequirementsAnalysis("TestCases/TestCase10c.cql", translatorOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(translator, translatorOptions);

        /*
        Element that is a choice, two of which are date-valued, referenced in a comparison

        define "ESRD Observations":
          [Observation: "ESRD Diagnosis"] O
            where O.effective same day or after @2022-02-15
        */

        // Validate the ELM is correct
        ExpressionDef ed = translator.getTranslatedLibrary().resolveExpressionRef("ESRD Observations");
        assertTrue(ed.getExpression() instanceof Query);
        Query q = (Query)ed.getExpression();
        assertTrue(q.getSource() != null && q.getSource().size() == 1);
        AliasedQuerySource source = q.getSource().get(0);
        assertTrue(source.getExpression() instanceof Retrieve);
        Retrieve r = (Retrieve)source.getExpression();
        assertTrue(r.getDataType().getLocalPart().equals("Observation"));

        // Validate the data requirement is reported in the module definition library
        DataRequirement expectedDataRequirement = null;
        for (DataRequirement dr : moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == Enumerations.FHIRAllTypes.OBSERVATION) {
                expectedDataRequirement = dr;
            }
        }
        assertTrue(expectedDataRequirement != null);

        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestHEDISBCSE() throws IOException {
        CqlTranslatorOptions translatorOptions = getTranslatorOptions();
        translatorOptions.setCompatibilityLevel("1.4");
        CqlTranslator translator = setupDataRequirementsAnalysis("BCSE/BCSE_HEDIS_MY2022.cql", translatorOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(translator, translatorOptions);
        assertNotNull(moduleDefinitionLibrary);
    }

    private void assertEqualToExpectedModuleDefinitionLibrary(org.hl7.fhir.r5.model.Library actualModuleDefinitionLibrary, String pathToExpectedModuleDefinitionLibrary) {
        FhirContext context =  getFhirContext();
        IParser parser = context.newJsonParser();
        org.hl7.fhir.r5.model.Library expectedModuleDefinitionLibrary = (org.hl7.fhir.r5.model.Library)parser.parseResource(DataRequirementsProcessorTest.class.getResourceAsStream(pathToExpectedModuleDefinitionLibrary));
        assertNotNull(expectedModuleDefinitionLibrary);
        actualModuleDefinitionLibrary.setDate(null);
        expectedModuleDefinitionLibrary.setDate(null);
        assertTrue(actualModuleDefinitionLibrary.equalsDeep(expectedModuleDefinitionLibrary));
    }

    @Test
    public void TestEXMLogic() throws IOException {
        CqlTranslatorOptions translatorOptions = getTranslatorOptions();
        translatorOptions.setAnalyzeDataRequirements(false);
        CqlTranslator translator = setupDataRequirementsAnalysis("EXMLogic/EXMLogic.cql", translatorOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(translator, translatorOptions);
        assertNotNull(moduleDefinitionLibrary);
        assertEqualToExpectedModuleDefinitionLibrary(moduleDefinitionLibrary, "EXMLogic/Library-EXMLogic-data-requirements.json");

        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestWithDependencies() throws IOException {
        CqlTranslatorOptions translatorOptions = getTranslatorOptions();
        translatorOptions.setAnalyzeDataRequirements(false);
        CqlTranslator translator = setupDataRequirementsAnalysis("WithDependencies/BSElements.cql", translatorOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(translator, translatorOptions);
        assertNotNull(moduleDefinitionLibrary);
        assertEqualToExpectedModuleDefinitionLibrary(moduleDefinitionLibrary, "WithDependencies/Library-BSElements-data-requirements.json");

        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestDataRequirementsProcessorWithPertinence() {
        CqlTranslatorOptions cqlTranslatorOptions = new CqlTranslatorOptions();
        cqlTranslatorOptions.getFormats().add(CqlTranslator.Format.JSON);
        cqlTranslatorOptions.getOptions().add(CqlTranslatorOptions.Options.EnableAnnotations);
        try {
            CqlTranslator translator = createTranslator("CompositeMeasures/cql/pertinence-tag.cql", cqlTranslatorOptions);//"OpioidCDS/cql/OpioidCDSCommon.cql", cqlTranslatorOptions);
            translator.toELM();
            assertTrue(translator.getErrors().isEmpty());
            libraryManager.cacheLibrary(translator.getTranslatedLibrary());

            DataRequirementsProcessor dqReqTrans = new DataRequirementsProcessor();
            org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = dqReqTrans.gatherDataRequirements(libraryManager, translator.getTranslatedLibrary(), cqlTranslatorOptions, null, false);

            assertTrue(moduleDefinitionLibrary.getDataRequirement().size() == 3);
            DataRequirement dr = moduleDefinitionLibrary.getDataRequirement().get(1);
            assertEquals(dr.getType(), Enumerations.FHIRAllTypes.CONDITION);
            assertEquals(dr.getExtension().get(0).getUrl(), "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-pertinence");
            assertEquals(((Coding) dr.getExtension().get(0).getValue()).getCode(), "pathognomonic");


            FhirContext context = getFhirContext();
            IParser parser = context.newJsonParser();
            String moduleDefString = parser.setPrettyPrint(true).encodeResourceToString(moduleDefinitionLibrary);
            logger.debug(moduleDefString);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Test
    public void TestDataRequirementsProcessorWithPertinenceAgain() {
        CqlTranslatorOptions cqlTranslatorOptions = new CqlTranslatorOptions();
        cqlTranslatorOptions.getFormats().add(CqlTranslator.Format.JSON);
        cqlTranslatorOptions.getOptions().add(CqlTranslatorOptions.Options.EnableAnnotations);
        try {
            CqlTranslator translator = createTranslator("CompositeMeasures/cql/pertinence-tag-AdvancedIllnessandFrailtyExclusion_FHIR4-5.0.000.cql", cqlTranslatorOptions);//"OpioidCDS/cql/OpioidCDSCommon.cql", cqlTranslatorOptions);
            translator.toELM();
            System.out.println(translator.getErrors());
            assertTrue(translator.getErrors().isEmpty());
            libraryManager.cacheLibrary(translator.getTranslatedLibrary());

            DataRequirementsProcessor dqReqTrans = new DataRequirementsProcessor();
            org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = dqReqTrans.gatherDataRequirements(libraryManager, translator.getTranslatedLibrary(), cqlTranslatorOptions, null, false);

            DataRequirement dr = moduleDefinitionLibrary.getDataRequirement().get(1);
            assertEquals(dr.getType(), Enumerations.FHIRAllTypes.CONDITION);
            assertEquals(dr.getExtension().get(0).getUrl(), "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-pertinence");
            assertEquals(((Coding) dr.getExtension().get(0).getValue()).getCode(), "weakly-negative");

            DataRequirement dr2 = moduleDefinitionLibrary.getDataRequirement().get(2);
            assertEquals(dr2.getType(), Enumerations.FHIRAllTypes.ENCOUNTER);
            assertEquals(dr2.getExtension().get(0).getUrl(), "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-pertinence");
            assertEquals(((Coding) dr2.getExtension().get(0).getValue()).getCode(), "pathognomonic");

            DataRequirement dr5 = moduleDefinitionLibrary.getDataRequirement().get(5);
            assertEquals(dr5.getType(), Enumerations.FHIRAllTypes.DEVICEREQUEST);
            assertEquals(dr5.getExtension().get(0).getUrl(), "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-pertinence");
            assertEquals(((Coding) dr5.getExtension().get(0).getValue()).getCode(), "strongly-positive");

            FhirContext context = getFhirContext();
            IParser parser = context.newJsonParser();
            String moduleDefString = parser.setPrettyPrint(true).encodeResourceToString(moduleDefinitionLibrary);
            logger.debug(moduleDefString);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private static void setup(String relativePath) {
        modelManager = new ModelManager();
        libraryManager = new LibraryManager(modelManager);
        libraryManager.getLibrarySourceLoader().registerProvider(new DefaultLibrarySourceProvider(Paths.get(relativePath)));
        libraryManager.getLibrarySourceLoader().registerProvider(new FhirLibrarySourceProvider());
        try {
            ucumService = new UcumEssenceService(UcumEssenceService.class.getResourceAsStream("/ucum-essence.xml"));
        }
        catch (UcumException e) {
            e.printStackTrace();
        }
    }

    private static void tearDown() {
        ucumService = null;
        libraryManager = null;
        modelManager = null;
    }

    public static void reset() {
        tearDown();
    }

    private static ModelManager getModelManager() {
        if (modelManager == null) {
            setup(null);
        }

        return modelManager;
    }

    private static LibraryManager getLibraryManager() {
        if (libraryManager == null) {
            setup(null);
        }

        return libraryManager;
    }

    private static UcumService getUcumService() {
        if (ucumService == null) {
            setup(null);
        }

        return ucumService;
    }

    public static CqlTranslator createTranslator(String testFileName, CqlTranslatorOptions.Options... options) throws IOException {
        return createTranslator(null, testFileName, new CqlTranslatorOptions(options));
    }

    public static CqlTranslator createTranslator(String testFileName, CqlTranslatorOptions options) throws IOException {
        return createTranslator(null, testFileName, options);
    }

    public static CqlTranslator createTranslator(NamespaceInfo namespaceInfo, String testFileName, CqlTranslatorOptions.Options... options) throws IOException {
        return createTranslator(namespaceInfo, testFileName, new CqlTranslatorOptions(options));
    }

    public static CqlTranslator createTranslator(NamespaceInfo namespaceInfo, String testFileName, CqlTranslatorOptions options) throws IOException {
        File translationTestFile = new File(DataRequirementsProcessorTest.class.getResource(testFileName).getFile());
        reset();
        setup(translationTestFile.getParent());
        CqlTranslator translator = CqlTranslator.fromFile(namespaceInfo, translationTestFile, getModelManager(), getLibraryManager(), getUcumService(), options);
        return translator;
    }
}
