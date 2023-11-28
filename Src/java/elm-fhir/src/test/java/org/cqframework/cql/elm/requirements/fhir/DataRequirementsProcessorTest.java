package org.cqframework.cql.elm.requirements.fhir;

import ca.uhn.fhir.context.FhirContext;
import org.cqframework.cql.cql2elm.*;
import org.cqframework.cql.cql2elm.model.CompiledLibrary;
import org.cqframework.cql.cql2elm.quick.FhirLibrarySourceProvider;
import org.hl7.cql.model.NamespaceInfo;
import org.hl7.elm.r1.*;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.r5.model.*;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.testng.Assert.*;


public class DataRequirementsProcessorTest {
    private static Logger logger = LoggerFactory.getLogger(DataRequirementsProcessorTest.class);

    private static FhirContext getFhirContext() {
        return FhirContext.forR5Cached();
    }

    @Test
    public void TestDataRequirementsProcessor() {
        CqlCompilerOptions cqlTranslatorOptions = new CqlCompilerOptions();
        cqlTranslatorOptions.getOptions().add(CqlCompilerOptions.Options.EnableAnnotations);
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
            var setup = setup("CompositeMeasures/cql/EXM124-9.0.000.cql", cqlTranslatorOptions);//"OpioidCDS/cql/OpioidCDSCommon.cql", cqlTranslatorOptions);



            DataRequirementsProcessor dqReqTrans = new DataRequirementsProcessor();
            org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = dqReqTrans.gatherDataRequirements(setup.manager(), setup.library(), cqlTranslatorOptions, null, false);
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
        CqlCompilerOptions cqlTranslatorOptions = new CqlCompilerOptions();
        cqlTranslatorOptions.setCollapseDataRequirements(true);
        cqlTranslatorOptions.setAnalyzeDataRequirements(true);
        try {
            NamespaceInfo ni = new NamespaceInfo("fhir.cdc.opioid-cds", "http://fhir.org/guides/cdc/opioid-cds");
            var setup = setup(ni, "OpioidCDSSTU3/cql/OpioidCDSREC10.cql", cqlTranslatorOptions);

            DataRequirementsProcessor dqReqTrans = new DataRequirementsProcessor();
            Set<String> expressions = new HashSet<String>();
            expressions.add("Negative PCP Screenings Count Since Last POS");
            org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = dqReqTrans.gatherDataRequirements(setup.manager(), setup.library(), cqlTranslatorOptions, expressions, false);
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
            assertEquals(pd.getType(), Enumerations.FHIRTypes.INTEGER);

            // dataRequirement Observation {
            //   ms: { code, category, value, status, status.value, effective },
            //   codeFilter {
            //     { category in http://fhir.org/guides/cdc/opioid-cds/ValueSet/observation-category-laboratory },
            //     { code in http://fhir.org/guides/cdc/opioid-cds/ValueSet/pcp-medications }
            //   }
            // }
            assertTrue(moduleDefinitionLibrary.getDataRequirement().size() == 1);
            DataRequirement dr= moduleDefinitionLibrary.getDataRequirement().get(0);
            assertEquals(dr.getType(), Enumerations.FHIRTypes.OBSERVATION);
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
        CqlCompilerOptions cqlTranslatorOptions = new CqlCompilerOptions();
        cqlTranslatorOptions.setCollapseDataRequirements(true);
        cqlTranslatorOptions.setAnalyzeDataRequirements(true);
        try {
            NamespaceInfo ni = new NamespaceInfo("fhir.cdc.opioid-cds", "http://fhir.org/guides/cdc/opioid-cds");
            var setup = setup(ni, "OpioidCDSSTU3/cql/OpioidCDSREC10.cql", cqlTranslatorOptions);
            DataRequirementsProcessor dqReqTrans = new DataRequirementsProcessor();
            org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = dqReqTrans.gatherDataRequirements(setup.manager(), setup.library(), cqlTranslatorOptions, null, false);
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
            assertEquals(pd.getType(), Enumerations.FHIRTypes.MEDICATIONREQUEST);
            assertEquals(pd.getUse(), Enumerations.OperationParameterUse.IN);
            assertEquals(pd.getMax(), "*");

            pd = getParameter(moduleDefinitionLibrary, "Patient");
            assertNotNull(pd, "Expected parameter Patient");
            assertEquals(pd.getType(), Enumerations.FHIRTypes.PATIENT);
            assertEquals(pd.getUse(), Enumerations.OperationParameterUse.OUT);
            assertEquals(pd.getMax(), "1");

            pd = getParameter(moduleDefinitionLibrary, "Lookback Year");
            assertNotNull(pd, "Expected parameter Lookback Year");
            assertEquals(pd.getType(), Enumerations.FHIRTypes.PERIOD);
            assertEquals(pd.getUse(), Enumerations.OperationParameterUse.OUT);
            assertEquals(pd.getMax(), "1");

            pd = getParameter(moduleDefinitionLibrary, "PCP Screenings");
            assertNotNull(pd, "Expected parameter PCP Screenings");
            assertEquals(pd.getType(), Enumerations.FHIRTypes.OBSERVATION);
            assertEquals(pd.getUse(), Enumerations.OperationParameterUse.OUT);
            assertEquals(pd.getMax(), "*");

            pd = getParameter(moduleDefinitionLibrary, "Positive PCP Screenings");
            assertNotNull(pd, "Expected parameter Positive PCP Screenings");
            assertEquals(pd.getType(), Enumerations.FHIRTypes.OBSERVATION);
            assertEquals(pd.getUse(), Enumerations.OperationParameterUse.OUT);
            assertEquals(pd.getMax(), "*");

            pd = getParameter(moduleDefinitionLibrary, "Negative PCP Screenings");
            assertNotNull(pd, "Expected parameter Negative PCP Screenings");
            assertEquals(pd.getType(), Enumerations.FHIRTypes.OBSERVATION);
            assertEquals(pd.getUse(), Enumerations.OperationParameterUse.OUT);
            assertEquals(pd.getMax(), "*");

            pd = getParameter(moduleDefinitionLibrary, "Negative PCP Screenings Count Since Last POS");
            assertNotNull(pd, "Expected parameter Negative PCP Screenings Count Since Last POS");
            assertEquals(pd.getType(), Enumerations.FHIRTypes.INTEGER);
            assertEquals(pd.getUse(), Enumerations.OperationParameterUse.OUT);
            assertEquals(pd.getMax(), "1");

            pd = getParameter(moduleDefinitionLibrary, "Positive PCP Dates in Lookback Period");
            assertNotNull(pd, "Expected parameter Positive PCP Dates in Lookback Period");
            assertEquals(pd.getType(), Enumerations.FHIRTypes.STRING);
            assertEquals(pd.getUse(), Enumerations.OperationParameterUse.OUT);
            assertEquals(pd.getMax(), "*");

            pd = getParameter(moduleDefinitionLibrary, "Has Positive Screening for PCP in Last 12 Months");
            assertNotNull(pd, "Expected parameter Has Positive Screening for PCP in Last 12 Months");
            assertEquals(pd.getType(), Enumerations.FHIRTypes.BOOLEAN);
            assertEquals(pd.getUse(), Enumerations.OperationParameterUse.OUT);
            assertEquals(pd.getMax(), "1");

            pd = getParameter(moduleDefinitionLibrary, "PCP Summary");
            assertNotNull(pd, "Expected parameter PCPSummary");
            assertEquals(pd.getType(), Enumerations.FHIRTypes.STRING);
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
                if (r.getType() == Enumerations.FHIRTypes.OBSERVATION) {
                    dr = r;
                    break;
                }
            }
            assertNotNull(dr);
            assertEquals(dr.getType(), Enumerations.FHIRTypes.OBSERVATION);
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
        CqlCompilerOptions cqlTranslatorOptions = new CqlCompilerOptions();
        try {
            Set<String> expressions = new HashSet<>();
            // TODO - add expressions to expressions
            expressions.add("Conditions Indicating End of Life or With Limited Life Expectancy");//Active Ambulatory Opioid Rx");
            var setup = setup("OpioidCDS/cql/OpioidCDSCommon.cql", cqlTranslatorOptions);


            DataRequirementsProcessor dqReqTrans = new DataRequirementsProcessor();
            org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = dqReqTrans.gatherDataRequirements(setup.manager(), setup.library(), cqlTranslatorOptions, expressions, false);
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
                if (requirement.getType() == Enumerations.FHIRTypes.CONDITION && requirement.getCodeFilter().size() == 1) {
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
        CqlCompilerOptions cqlTranslatorOptions = new CqlCompilerOptions();

        try {
//            CqlTranslator translator = createTranslator("/ecqm/resources/library-EXM506-2.2.000.json", cqlTranslatorOptions);
            var setup = setup("CompositeMeasures/cql/BCSComponent.cql", cqlTranslatorOptions);

            DataRequirementsProcessor dqReqTrans = new DataRequirementsProcessor();
            org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = dqReqTrans.gatherDataRequirements(setup.manager(), setup.library(), cqlTranslatorOptions, null, false);
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
                if (requirement.getType() == Enumerations.FHIRTypes.CONDITION && requirement.getCodeFilter().size() == 1) {
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
        CqlCompilerOptions cqlTranslatorOptions = new CqlCompilerOptions();
        cqlTranslatorOptions.setCollapseDataRequirements(true);
        try {
            var setup = setup("DataRequirements/DataRequirementsLibraryTest.cql", cqlTranslatorOptions);


            DataRequirementsProcessor dqReqTrans = new DataRequirementsProcessor();
            org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = dqReqTrans.gatherDataRequirements(setup.manager(), setup.library(), cqlTranslatorOptions, null, false);
            assertTrue(moduleDefinitionLibrary.getType().getCode("http://terminology.hl7.org/CodeSystem/library-type").equalsIgnoreCase("module-definition"));
            DataRequirement encounterRequirement = null;
            for (DataRequirement dr : moduleDefinitionLibrary.getDataRequirement()) {
                if (dr.getType() == Enumerations.FHIRTypes.ENCOUNTER) {
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
        CqlCompilerOptions cqlTranslatorOptions = new CqlCompilerOptions();

        try {
            var setup = setup("FHIRReferencesRevisited.cql", cqlTranslatorOptions);


            DataRequirementsProcessor dqReqTrans = new DataRequirementsProcessor();
            org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = dqReqTrans.gatherDataRequirements(setup.manager(), setup.library(), cqlTranslatorOptions, null, false);

            FhirContext context =  getFhirContext();
            IParser parser = context.newJsonParser();
            String moduleDefString = parser.setPrettyPrint(true).encodeResourceToString(moduleDefinitionLibrary);
            logger.debug(moduleDefString);
            // TODO: Validate consolidation of requirements
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private CqlCompilerOptions getCompilerOptions() {
        return new CqlCompilerOptions();
    }

    private Setup setupUncollapsedDataRequirementsGather(String fileName, CqlCompilerOptions cqlTranslatorOptions) throws IOException {
        cqlTranslatorOptions.setCollapseDataRequirements(false);
        cqlTranslatorOptions.setAnalyzeDataRequirements(false);
        return setup(fileName, cqlTranslatorOptions);
    }

    private Setup setupUncollapsedDataRequirementsGather(NamespaceInfo namespace, String fileName, CqlCompilerOptions cqlTranslatorOptions) throws IOException {
        cqlTranslatorOptions.setCollapseDataRequirements(false);
        cqlTranslatorOptions.setAnalyzeDataRequirements(false);
        return setup(namespace, fileName, cqlTranslatorOptions);
    }

    private Setup setupUncollapsedDataRequirementsAnalysis(String fileName, CqlCompilerOptions cqlTranslatorOptions) throws IOException {
        cqlTranslatorOptions.setCollapseDataRequirements(false);
        cqlTranslatorOptions.setAnalyzeDataRequirements(true);
        return setup(fileName, cqlTranslatorOptions);
    }

    private Setup setupUncollapsedDataRequirementsAnalysis(NamespaceInfo namespace, String fileName, CqlCompilerOptions cqlTranslatorOptions) throws IOException {
        cqlTranslatorOptions.setCollapseDataRequirements(false);
        cqlTranslatorOptions.setAnalyzeDataRequirements(true);
        return setup(namespace, fileName, cqlTranslatorOptions);
    }

    private Setup setupDataRequirementsGather(String fileName, CqlCompilerOptions cqlTranslatorOptions) throws IOException {
        cqlTranslatorOptions.setCollapseDataRequirements(true);
        cqlTranslatorOptions.setAnalyzeDataRequirements(false);
        return setup(fileName, cqlTranslatorOptions);
    }

    private Setup setupDataRequirementsGather(NamespaceInfo namespace, String fileName, CqlCompilerOptions cqlTranslatorOptions) throws IOException {
        cqlTranslatorOptions.setCollapseDataRequirements(true);
        cqlTranslatorOptions.setAnalyzeDataRequirements(false);
        return setup(namespace, fileName, cqlTranslatorOptions);
    }

    private Setup setupDataRequirementsAnalysis(String fileName, CqlCompilerOptions cqlTranslatorOptions) throws IOException {
        cqlTranslatorOptions.setCollapseDataRequirements(true);
        cqlTranslatorOptions.setAnalyzeDataRequirements(true);
        return setup(fileName, cqlTranslatorOptions);
    }

    private Setup setupDataRequirementsAnalysis(NamespaceInfo namespace, String fileName, CqlCompilerOptions cqlTranslatorOptions) throws IOException {
        cqlTranslatorOptions.setCollapseDataRequirements(true);
        cqlTranslatorOptions.setAnalyzeDataRequirements(true);
        return setup(namespace, fileName, cqlTranslatorOptions);
    }

    private org.hl7.fhir.r5.model.Library getModuleDefinitionLibrary(Setup setup, CqlCompilerOptions cqlTranslatorOptions, Map<String, Object> parameters) {
        DataRequirementsProcessor dqReqTrans = new DataRequirementsProcessor();
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = dqReqTrans.gatherDataRequirements(setup.manager(), setup.library(), cqlTranslatorOptions, null, parameters, false,false);
        assertTrue(moduleDefinitionLibrary.getType().getCode("http://terminology.hl7.org/CodeSystem/library-type").equalsIgnoreCase("module-definition"));
        return moduleDefinitionLibrary;
    }

    private org.hl7.fhir.r5.model.Library getModuleDefinitionLibrary(Setup setup, CqlCompilerOptions cqlTranslatorOptions, Map<String, Object> parameters, ZonedDateTime evaluationDateTime) {
        DataRequirementsProcessor dqReqTrans = new DataRequirementsProcessor();
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = dqReqTrans.gatherDataRequirements(setup.manager(), setup.library(), cqlTranslatorOptions, null, parameters, evaluationDateTime, false,false);
        assertEquals(moduleDefinitionLibrary.getName(), "EffectiveDataRequirements");
        assertTrue(moduleDefinitionLibrary.getType().getCode("http://terminology.hl7.org/CodeSystem/library-type").equalsIgnoreCase("module-definition"));
        return moduleDefinitionLibrary;
    }

    private org.hl7.fhir.r5.model.Library getModuleDefinitionLibrary(Setup setup, CqlCompilerOptions cqlTranslatorOptions, Map<String, Object> parameters, ZonedDateTime evaluationDateTime, boolean includeLogicDefinitions) {
        DataRequirementsProcessor dqReqTrans = new DataRequirementsProcessor();
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = dqReqTrans.gatherDataRequirements(setup.manager(), setup.library(), cqlTranslatorOptions, null, parameters, evaluationDateTime, includeLogicDefinitions,false);
        assertTrue(moduleDefinitionLibrary.getType().getCode("http://terminology.hl7.org/CodeSystem/library-type").equalsIgnoreCase("module-definition"));
        return moduleDefinitionLibrary;
    }

    private org.hl7.fhir.r5.model.Library getModuleDefinitionLibrary(Setup setup, CqlCompilerOptions cqlTranslatorOptions) {
        DataRequirementsProcessor dqReqTrans = new DataRequirementsProcessor();
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = dqReqTrans.gatherDataRequirements(setup.manager(), setup.library(), cqlTranslatorOptions, null, false);
        assertTrue(moduleDefinitionLibrary.getType().getCode("http://terminology.hl7.org/CodeSystem/library-type").equalsIgnoreCase("module-definition"));
        return moduleDefinitionLibrary;
    }

    private org.hl7.fhir.r5.model.Library getModuleDefinitionLibrary(Setup setup, CqlCompilerOptions cqlTranslatorOptions, Set<String> expressions) {
        DataRequirementsProcessor dqReqTrans = new DataRequirementsProcessor();
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = dqReqTrans.gatherDataRequirements(setup.manager(), setup.library(), cqlTranslatorOptions, expressions, false);
        assertTrue(moduleDefinitionLibrary.getType().getCode("http://terminology.hl7.org/CodeSystem/library-type").equalsIgnoreCase("module-definition"));
        return moduleDefinitionLibrary;
    }

    private void outputModuleDefinitionLibrary(org.hl7.fhir.r5.model.Library moduleDefinitionLibrary) {
        FhirContext context =  getFhirContext();
        IParser parser = context.newJsonParser();
        String moduleDefString = parser.setPrettyPrint(true).encodeResourceToString(moduleDefinitionLibrary);
        System.out.println(moduleDefString);
    }

    private Iterable<DataRequirement> getDataRequirementsForType(Iterable<DataRequirement> dataRequirements, Enumerations.FHIRTypes type) {
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
        CqlCompilerOptions compilerOptions = getCompilerOptions();
        var manager  = setupDataRequirementsGather("CMS104/MATGlobalCommonFunctionsFHIR4.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions, Collections.singleton("PrincipalDiagnosis"));

        // DataRequirements of the PrincipalDiagnosis function:
            // [Condition]
        Iterable<DataRequirement> expectedDataRequirements = getDataRequirementsForType(moduleDefinitionLibrary.getDataRequirement(), Enumerations.FHIRTypes.CONDITION);
        assertTrue(expectedDataRequirements.iterator().hasNext());
        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestNonElectiveInpatientEncounterDataRequirements() throws IOException {
        CqlCompilerOptions compilerOptions = getCompilerOptions();
        var manager  = setupDataRequirementsGather("CMS104/TJCOverallFHIR.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions, Collections.singleton("Non Elective Inpatient Encounter"));

        // DataRequirements of the Non Elective Inpatient Encounter expression:
            // [Encounter: "Non-Elective Inpatient Encounter"]
        Iterable<DataRequirement> actualDataRequirements = getDataRequirementsForType(moduleDefinitionLibrary.getDataRequirement(), Enumerations.FHIRTypes.ENCOUNTER);
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
        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestAllStrokeEncounterDataRequirements() throws IOException {
        CqlCompilerOptions compilerOptions = getCompilerOptions();
        var manager  = setupDataRequirementsGather("CMS104/TJCOverallFHIR.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions, Collections.singleton("All Stroke Encounter"));

        // DataRequirements of the All Stroke Encounter expression:
            // [Encounter: "Non-Elective Inpatient Encounter"]          (from Non Elective Inpatient Encounter)
            // [Condition]                                              (from PrincipalDiagnosis)
        Iterable<DataRequirement> encounterDataRequirements = getDataRequirementsForType(moduleDefinitionLibrary.getDataRequirement(), Enumerations.FHIRTypes.ENCOUNTER);
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

        Iterable<DataRequirement> conditionDataRequirements = getDataRequirementsForType(moduleDefinitionLibrary.getDataRequirement(), Enumerations.FHIRTypes.CONDITION);
        assertTrue(conditionDataRequirements.iterator().hasNext());

        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestCMS104DataRequirements() throws IOException {
        CqlCompilerOptions compilerOptions = getCompilerOptions();
        var manager  = setupDataRequirementsGather("CMS104/DischargedonAntithromboticTherapyFHIR.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions);

        // DataRequirements of the All Stroke Encounter expression:
        // [Encounter: "Non-Elective Inpatient Encounter"]          (from Non Elective Inpatient Encounter)
        // [Condition]                                              (from PrincipalDiagnosis)
        Iterable<DataRequirement> encounterDataRequirements = getDataRequirementsForType(moduleDefinitionLibrary.getDataRequirement(), Enumerations.FHIRTypes.ENCOUNTER);
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

        Iterable<DataRequirement> conditionDataRequirements = getDataRequirementsForType(moduleDefinitionLibrary.getDataRequirement(), Enumerations.FHIRTypes.CONDITION);
        assertTrue(conditionDataRequirements.iterator().hasNext());

        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestDataRequirementsAnalysisCase1() throws IOException {
        CqlCompilerOptions compilerOptions = getCompilerOptions();
        var manager = setupDataRequirementsAnalysis("TestCases/TestCase1.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions);

        /*
        1.
        Stated DataRequirement: ERSD Observations
        type: Observation
        codeFilter: { path:  code, valueSet:  'http://fakeurl.com/ersd-diagnosis' }
        */

        // Validate the ELM is correct
        ExpressionDef ed = manager.library().resolveExpressionRef("ESRD Observations");
        assertTrue(ed.getExpression() instanceof Retrieve);
        Retrieve r = (Retrieve)ed.getExpression();
        assertEquals(r.getCodeProperty(), "code");
        assertTrue(r.getCodes() instanceof ValueSetRef);
        assertEquals(((ValueSetRef)r.getCodes()).getName(), "ESRD Diagnosis");

        // Validate the data requirement is reported in the module definition library
        DataRequirement expectedDataRequirement = null;
        for (DataRequirement dr : moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == Enumerations.FHIRTypes.OBSERVATION) {
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
        CqlCompilerOptions compilerOptions = getCompilerOptions();
        var manager = setupDataRequirementsAnalysis("TestCases/TestCase1b.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions);

        /*
        1b. Similar to 1, but not on a primary code path and with a constant
        DataRequirement
        type: Observation
        codeFilter: { path: status, code: Coding { code: 'final'}}
        */

        // Validate the ELM is correct
        ExpressionDef ed = manager.library().resolveExpressionRef("Observations");
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
            if (dr.getType() == Enumerations.FHIRTypes.OBSERVATION) {
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
        CqlCompilerOptions compilerOptions = getCompilerOptions();
        var manager = setupDataRequirementsAnalysis("TestCases/TestCase1c.cql", compilerOptions);
        Set<String> expressions = new HashSet<>();
        expressions.add("TestReferencedDataRequirement");
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions, expressions);

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
            && pd.hasMin() && pd.getMin() == 0 && "*".equals(pd.getMax()) && pd.getType() == Enumerations.FHIRTypes.MEDICATION) {
                expectedParameterDefinition = pd;
            }
        }
        assertTrue(expectedParameterDefinition != null);

        // Validate the data requirement is reported correctly in the module definition library

        DataRequirement expectedDataRequirement = null;
        // TODO: This really should be 1, but we're using the recursive gather, so it reports the [Medication] retrieve in the referenced expression as well
        assertEquals(moduleDefinitionLibrary.getDataRequirement().size(), 2);
        for (DataRequirement dr : moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == Enumerations.FHIRTypes.MEDICATION) {
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

        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestDataRequirementsAnalysisCase2a() throws IOException {
        CqlCompilerOptions compilerOptions = getCompilerOptions();
        var manager = setupDataRequirementsAnalysis("TestCases/TestCase2a.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions);

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
        ExpressionDef ed = manager.library().resolveExpressionRef("HospiceEncounterClaimsA");
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
            if (dr.getType() == Enumerations.FHIRTypes.CLAIM) {
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
    public void TestDataRequirementsAnalysisCase2b() throws IOException {
        CqlCompilerOptions compilerOptions = getCompilerOptions();
        var manager = setupDataRequirementsAnalysis("TestCases/TestCase2b.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions);

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
        ExpressionDef ed = manager.library().resolveExpressionRef("HospiceEncounterClaimsBBoundDate");
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
            if (dr.getType() == Enumerations.FHIRTypes.CLAIM) {
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
    public void TestDataRequirementsAnalysisCase2e() throws IOException {
        CqlCompilerOptions compilerOptions = getCompilerOptions();
        var manager = setupDataRequirementsAnalysis("TestCases/TestCase2e.cql", compilerOptions);
        // Evaluate this test as of 12/31/2022
        ZonedDateTime evaluationDateTime = ZonedDateTime.of(2022, 12, 31, 0, 0, 0, 0, ZoneId.of("Z"));
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions, new HashMap<String, Object>(), evaluationDateTime);

        /*
        2e - Timing phrase 90 days or less before
        DataRequirement
        type: Condition
        dateFilter: { path: onset, value: Interval[Today() - 90 days, Today()] }

        define "Date Filter Expression":
          [Condition] C
            where onset as Period starts 90 days or less before Today()
        */

        ExpressionDef ed = manager.library().resolveExpressionRef("Date Filter Expression");
        assertTrue(ed.getExpression() instanceof Query);
        Query q = (Query)ed.getExpression();
        assertTrue(q.getSource() != null && q.getSource().size() == 1);
        AliasedQuerySource source = q.getSource().get(0);
        assertTrue(source.getExpression() instanceof Retrieve);
        Retrieve r = (Retrieve)source.getExpression();
        assertTrue(r.getDateFilter() != null && r.getDateFilter().size() == 1);
        DateFilterElement dfe = r.getDateFilter().get(0);
        assertEquals(dfe.getProperty(), "onset");
        assertTrue(dfe.getValue() instanceof Interval);

        OffsetDateTime expectedPeriodStart = evaluationDateTime.toOffsetDateTime().minusDays(90);
        OffsetDateTime expectedPeriodEnd = evaluationDateTime.toOffsetDateTime().minusNanos(1000000);
        DataRequirement expectedDataRequirement = null;
        boolean hasFilter = false;
        for (DataRequirement dr : moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == Enumerations.FHIRTypes.CONDITION) {
                if (dr.getDateFilter().size() == 1) {
                    for (DataRequirement.DataRequirementDateFilterComponent dfc : dr.getDateFilter()) {
                        if ("onset".equals(dfc.getPath())) {
                            if (dfc.getValue() instanceof Period) {
                                String expectedPeriodStartString = expectedPeriodStart.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME).replace(":00Z", ":00.000Z"); //"2022-10-02T00:00:00.000-07:00"
                                String expectedPeriodEndString = expectedPeriodEnd.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME); //"2022-12-30T23:59:59.999-07:00"
                                if (((Period)dfc.getValue()).hasStart() && ((Period)dfc.getValue()).getStartElement().asStringValue().equals(expectedPeriodStartString)
                                        && ((Period)dfc.getValue()).hasEnd() && ((Period)dfc.getValue()).getEndElement().asStringValue().equals(expectedPeriodEndString)) {
                                    hasFilter = true;
                                }
                            }
                        }
                    }

                    if (hasFilter) {
                        expectedDataRequirement = dr;
                    }
                }
            }
        }
        assertTrue(expectedDataRequirement != null);
    }

    @Test
    public void TestDataRequirementsAnalysisCase2g() throws IOException {
        CqlCompilerOptions compilerOptions = getCompilerOptions();
        var manager = setupDataRequirementsAnalysis("TestCases/TestCase2g.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions, new HashMap<String, Object>());

        /*
        2g - Equal to a compile-time literal function
        DataRequirement
        type: Condition
        dateFilter: { path: onset, value: Today() }

        define DateTimeEqualToFunction:
          [Condition] C
            where C.onset as dateTime = Today()
        */

        ExpressionDef ed = manager.library().resolveExpressionRef("DateTimeEqualToFunction");
        assertTrue(ed.getExpression() instanceof Query);
        Query q = (Query)ed.getExpression();
        assertTrue(q.getSource() != null && q.getSource().size() == 1);
        AliasedQuerySource source = q.getSource().get(0);
        assertTrue(source.getExpression() instanceof Retrieve);
        Retrieve r = (Retrieve)source.getExpression();
        assertTrue(r.getDateFilter() != null && r.getDateFilter().size() == 1);
        DateFilterElement dfe = r.getDateFilter().get(0);
        assertEquals(dfe.getProperty(), "onset");
        assertTrue(dfe.getValue() instanceof Interval);

        DataRequirement expectedDataRequirement = null;
        for (DataRequirement dr : moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == Enumerations.FHIRTypes.CONDITION) {
                if (dr.getDateFilter().size() == 1) {
                    DataRequirement.DataRequirementDateFilterComponent dfc = dr.getDateFilterFirstRep();
                    if ("onset".equals(dfc.getPath())) {
                        if (dfc.getValue() instanceof Period) {
                            if (((Period)dfc.getValue()).hasStart() && ((Period)dfc.getValue()).hasEnd()) {
                                expectedDataRequirement = dr;
                            }
                        }
                    }
                }
            }
        }
        assertTrue(expectedDataRequirement != null);
    }

    @Test
    public void TestDataRequirementsAnalysisCase2i() throws IOException {
        CqlCompilerOptions compilerOptions = getCompilerOptions();
        var manager = setupDataRequirementsAnalysis("TestCases/TestCase2i.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions, new HashMap<String, Object>());

        /*
        2i - In a compile-time literal interval
        DataRequirement
        type: Condition
        dateFilter: { path: onset, value: Interval[@2022-12-31 - 90 days, @2022-12-31] }

        define "Date Filter Expression":
          [Condition] C
            where C.onset as dateTime in Interval[@2022-12-31 - 90 days, @2022-12-31]
        */

        ZonedDateTime evaluationDateTime = ZonedDateTime.of(2022, 12, 31, 0, 0, 0, 0, ZoneId.systemDefault());
        OffsetDateTime expectedPeriodStart = evaluationDateTime.toOffsetDateTime().minusDays(90);
        ExpressionDef ed = manager.library().resolveExpressionRef("Date Filter Expression");
        assertTrue(ed.getExpression() instanceof Query);
        Query q = (Query)ed.getExpression();
        assertTrue(q.getSource() != null && q.getSource().size() == 1);
        AliasedQuerySource source = q.getSource().get(0);
        assertTrue(source.getExpression() instanceof Retrieve);
        Retrieve r = (Retrieve)source.getExpression();
        assertTrue(r.getDateFilter() != null && r.getDateFilter().size() == 1);
        DateFilterElement dfe = r.getDateFilter().get(0);
        assertEquals(dfe.getProperty(), "onset");
        assertTrue(dfe.getValue() instanceof Interval);

        DataRequirement expectedDataRequirement = null;
        for (DataRequirement dr : moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == Enumerations.FHIRTypes.CONDITION) {
                if (dr.getDateFilter().size() == 1) {
                    DataRequirement.DataRequirementDateFilterComponent dfc = dr.getDateFilterFirstRep();
                    if ("onset".equals(dfc.getPath())) {
                        if (dfc.getValue() instanceof Period) {
                            String expectedPeriodStartString = expectedPeriodStart.format(DateTimeFormatter.ISO_LOCAL_DATE); // "2022-10-02"
                            if (((Period)dfc.getValue()).hasStart() && ((Period)dfc.getValue()).hasEnd() && ((Period)dfc.getValue()).getStartElement().asStringValue().equals(expectedPeriodStartString)) {
                                expectedDataRequirement = dr;
                            }
                        }
                    }
                }
            }
        }
        assertTrue(expectedDataRequirement != null);
    }

    @Test
    public void TestDataRequirementsAnalysisCase2j() throws IOException {
        CqlCompilerOptions compilerOptions = getCompilerOptions();
        var manager = setupDataRequirementsAnalysis("TestCases/TestCase2j.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions, new HashMap<String, Object>());

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

        ZonedDateTime evaluationDateTime = ZonedDateTime.of(2022, 12, 31, 0, 0, 0, 0, ZoneId.systemDefault());
        OffsetDateTime expectedPeriodStart1 = evaluationDateTime.toOffsetDateTime().minusDays(90);
        OffsetDateTime expectedPeriodEnd1 = ZonedDateTime.of(9999, 12, 31, 23, 59, 59, 999000000, ZoneId.of("UTC")).toOffsetDateTime();
        OffsetDateTime expectedPeriodStart2 = ZonedDateTime.of(1, 1, 1, 0, 0, 0, 0, ZoneId.of("UTC")).toOffsetDateTime();
        OffsetDateTime expectedPeriodEnd2 = evaluationDateTime.toOffsetDateTime();
        ExpressionDef ed = manager.library().resolveExpressionRef("Date Filter Expression");
        assertTrue(ed.getExpression() instanceof Query);
        Query q = (Query)ed.getExpression();
        assertTrue(q.getSource() != null && q.getSource().size() == 1);
        AliasedQuerySource source = q.getSource().get(0);
        assertTrue(source.getExpression() instanceof Retrieve);
        Retrieve r = (Retrieve)source.getExpression();
        assertTrue(r.getDateFilter() != null && r.getDateFilter().size() == 2);
        DateFilterElement dfe = r.getDateFilter().get(0);
        assertEquals(dfe.getProperty(), "onset");
        assertTrue(dfe.getValue() instanceof Interval);
        dfe = r.getDateFilter().get(1);
        assertEquals(dfe.getProperty(), "onset");
        assertTrue(dfe.getValue() instanceof Interval);

        DataRequirement expectedDataRequirement = null;
        boolean hasFilter1 = false;
        boolean hasFilter2 = false;
        for (DataRequirement dr : moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == Enumerations.FHIRTypes.CONDITION) {
                if (dr.getDateFilter().size() == 2) {
                    for (DataRequirement.DataRequirementDateFilterComponent dfc : dr.getDateFilter()) {
                        if ("onset".equals(dfc.getPath())) {
                            if (dfc.getValue() instanceof Period) {
                                String expectedPeriodStart1String = expectedPeriodStart1.format(DateTimeFormatter.ISO_LOCAL_DATE); // "2022-10-02"
                                String expectedPeriodEnd1String = expectedPeriodEnd1.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME); // "9999-12-31T23:59:59.999Z"
                                String expectedPeriodStart2String = expectedPeriodStart2.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME).replace(":00Z", ":00.000Z"); // "0001-01-01T00:00:00.000Z"
                                String expectedPeriodEnd2String = expectedPeriodEnd2.format(DateTimeFormatter.ISO_LOCAL_DATE); // "2022-12-31"
                                if (((Period)dfc.getValue()).hasStart() && ((Period)dfc.getValue()).getStartElement().asStringValue().equals(expectedPeriodStart1String)
                                        && ((Period)dfc.getValue()).hasEnd() && ((Period)dfc.getValue()).getEndElement().asStringValue().equals(expectedPeriodEnd1String)) {
                                    hasFilter1 = true;
                                }
                                else if (((Period)dfc.getValue()).hasEnd()
                                        && ((Period)dfc.getValue()).hasStart()) {
                                    String actualPeriodStart2String = ((Period)dfc.getValue()).getStartElement().asStringValue();
                                    String actualPeriodEnd2String = ((Period)dfc.getValue()).getEndElement().asStringValue();
                                    if (actualPeriodStart2String.equals(expectedPeriodStart2String) && actualPeriodEnd2String.equals(expectedPeriodEnd2String)) {
                                        // && ((Period)dfc.getValue()).getEndElement().asStringValue().equals(expectedPeriodEnd2String)
                                        // && ((Period)dfc.getValue()).getStartElement().asStringValue().equals(expectedPeriodStart2String)
                                        hasFilter2 = true;
                                    }
                                }
                            }
                        }
                    }

                    if (hasFilter1 && hasFilter2) {
                        expectedDataRequirement = dr;
                    }
                }
            }
        }
        assertTrue(expectedDataRequirement != null);
    }

    @Test
    public void TestDataRequirementsAnalysisCase9a() throws IOException {
        CqlCompilerOptions compilerOptions = getCompilerOptions();
        var manager = setupDataRequirementsAnalysis("TestCases/TestCase9a.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions);

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
        ExpressionDef ed = manager.library().resolveExpressionRef("MedicationRequestWithEncounter");
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
            if (dr.getType() == Enumerations.FHIRTypes.MEDICATIONREQUEST) {
                expectedDataRequirement = dr;
            }
        }
        assertTrue(expectedDataRequirement != null);

        DataRequirement includedDataRequirement = null;
        for (DataRequirement dr : moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == Enumerations.FHIRTypes.ENCOUNTER) {
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
        CqlCompilerOptions compilerOptions = getCompilerOptions();
        var manager = setupDataRequirementsAnalysis("TestCases/TestCase9d.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions);

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
        ExpressionDef ed = manager.library().resolveExpressionRef("MedicationRequestWithAspirinInLet");
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
            if (dr.getType() == Enumerations.FHIRTypes.MEDICATIONREQUEST) {
                expectedDataRequirement = dr;
            }
        }
        assertTrue(expectedDataRequirement != null);

        DataRequirement includedDataRequirement = null;
        for (DataRequirement dr : moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == Enumerations.FHIRTypes.MEDICATION) {
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
        CqlCompilerOptions compilerOptions = getCompilerOptions();
        var manager = setupDataRequirementsAnalysis("TestCases/TestCase9e.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions);

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
        ExpressionDef ed = manager.library().resolveExpressionRef("MedicationRequestWithAspirinInWhere");
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
            if (dr.getType() == Enumerations.FHIRTypes.MEDICATIONREQUEST) {
                expectedDataRequirement = dr;
            }
        }
        assertTrue(expectedDataRequirement != null);

        DataRequirement includedDataRequirement = null;
        for (DataRequirement dr : moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == Enumerations.FHIRTypes.MEDICATION) {
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
        CqlCompilerOptions compilerOptions = getCompilerOptions();
        var manager = setupDataRequirementsAnalysis("TestCases/TestCase9f.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions);

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
        ExpressionDef ed = manager.library().resolveExpressionRef("MedicationRequestWithAspirinInFrom");
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
            if (dr.getType() == Enumerations.FHIRTypes.MEDICATIONREQUEST) {
                expectedDataRequirement = dr;
            }
        }
        assertTrue(expectedDataRequirement != null);

        DataRequirement includedDataRequirement = null;
        for (DataRequirement dr : moduleDefinitionLibrary.getDataRequirement()) {
            if (dr.getType() == Enumerations.FHIRTypes.MEDICATION) {
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
        CqlCompilerOptions compilerOptions = getCompilerOptions();
        var manager = setupDataRequirementsAnalysis("TestCases/TestCase10a.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions);

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
        ExpressionDef ed = manager.library().resolveExpressionRef("ESRD Observations");
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
            if (dr.getType() == Enumerations.FHIRTypes.OBSERVATION) {
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
        CqlCompilerOptions compilerOptions = getCompilerOptions();
        var manager = setupDataRequirementsAnalysis("TestCases/TestCase10b.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions);

        /*
        Multiple date elements referenced in a Coalesce

        define "ESRD Observations":
          [Observation: "ESRD Diagnosis"] O
            where Coalesce(O.effective, O.issued) same day or after @2022-02-15
        */

        // Validate the ELM is correct
        ExpressionDef ed = manager.library().resolveExpressionRef("ESRD Observations");
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
            if (dr.getType() == Enumerations.FHIRTypes.OBSERVATION) {
                expectedDataRequirement = dr;
            }
        }
        assertTrue(expectedDataRequirement != null);

        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestDataRequirementsAnalysisCase10c() throws IOException {
        // TODO: Complete this test case
        CqlCompilerOptions compilerOptions = getCompilerOptions();
        var manager = setupDataRequirementsAnalysis("TestCases/TestCase10c.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions);

        /*
        Element that is a choice, two of which are date-valued, referenced in a comparison

        define "ESRD Observations":
          [Observation: "ESRD Diagnosis"] O
            where O.effective same day or after @2022-02-15
        */

        // Validate the ELM is correct
        ExpressionDef ed = manager.library().resolveExpressionRef("ESRD Observations");
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
            if (dr.getType() == Enumerations.FHIRTypes.OBSERVATION) {
                expectedDataRequirement = dr;
            }
        }
        assertTrue(expectedDataRequirement != null);

        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestHEDISBCSE() throws IOException {
        CqlCompilerOptions compilerOptions = getCompilerOptions();
        compilerOptions.setCompatibilityLevel("1.4");
        var manager = setupDataRequirementsAnalysis("BCSE/BCSE_HEDIS_MY2022.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions);
        assertNotNull(moduleDefinitionLibrary);
    }

    private void assertEqualToExpectedModuleDefinitionLibrary(org.hl7.fhir.r5.model.Library actualModuleDefinitionLibrary, String pathToExpectedModuleDefinitionLibrary) {
        FhirContext context =  getFhirContext();
        IParser parser = context.newJsonParser();
        org.hl7.fhir.r5.model.Library expectedModuleDefinitionLibrary = (org.hl7.fhir.r5.model.Library)parser.parseResource(DataRequirementsProcessorTest.class.getResourceAsStream(pathToExpectedModuleDefinitionLibrary));
        assertNotNull(expectedModuleDefinitionLibrary);
        //outputModuleDefinitionLibrary(actualModuleDefinitionLibrary);
        actualModuleDefinitionLibrary.setDate(null);
        expectedModuleDefinitionLibrary.setDate(null);
        assertTrue(actualModuleDefinitionLibrary.equalsDeep(expectedModuleDefinitionLibrary));
    }

    @Test
    public void TestEXMLogic() throws IOException {
        CqlCompilerOptions compilerOptions = getCompilerOptions();
        compilerOptions.setAnalyzeDataRequirements(false);
        var manager = setupDataRequirementsAnalysis("EXMLogic/EXMLogic.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions);
        assertNotNull(moduleDefinitionLibrary);
        assertEqualToExpectedModuleDefinitionLibrary(moduleDefinitionLibrary, "EXMLogic/Library-EXMLogic-data-requirements.json");

        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestWithDependencies() throws IOException {
        CqlCompilerOptions compilerOptions = getCompilerOptions();
        compilerOptions.setAnalyzeDataRequirements(false);
        var manager = setupDataRequirementsAnalysis("WithDependencies/BSElements.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions, new HashMap<String, Object>(), ZonedDateTime.of(2023, 1, 16, 0, 0, 0, 0, ZoneId.of("UTC")));
        assertNotNull(moduleDefinitionLibrary);
        assertEqualToExpectedModuleDefinitionLibrary(moduleDefinitionLibrary, "WithDependencies/Library-BSElements-data-requirements.json");

        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestCMS645() throws IOException {
        CqlCompilerOptions compilerOptions = getCompilerOptions();
        compilerOptions.setAnalyzeDataRequirements(false);
        var manager = setupDataRequirementsAnalysis("CMS645/CMS645Test.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions, new HashMap<String, Object>(), ZonedDateTime.of(2023, 1, 16, 0, 0, 0, 0, ZoneId.of("UTC")));
        assertNotNull(moduleDefinitionLibrary);
        assertEqualToExpectedModuleDefinitionLibrary(moduleDefinitionLibrary, "CMS645/CMS645-ModuleDefinitionLibrary.json");

        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestPCSBMI() throws IOException {
        CqlCompilerOptions compilerOptions = CqlCompilerOptions.defaultOptions();
        var manager = setupDataRequirementsAnalysis("PCSBMI/PCSBMIScreenAndFollowUpFHIR.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions, new HashMap<String, Object>(), ZonedDateTime.of(2023, 1, 16, 0, 0, 0, 0, ZoneId.of("UTC")));
        assertNotNull(moduleDefinitionLibrary);
        assertEqualToExpectedModuleDefinitionLibrary(moduleDefinitionLibrary, "PCSBMI/PCSBMI-ModuleDefinitionLibrary.json");

        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestCMS143() throws IOException {
        CqlCompilerOptions compilerOptions = CqlCompilerOptions.defaultOptions();
        compilerOptions.getOptions().add(CqlCompilerOptions.Options.EnableResultTypes);
        Set<String> expressions = new HashSet<>();
        //expressions.add("Qualifying Encounter");
        //expressions.add("Qualifying Encounter During Measurement Period");
        //expressions.add("Qualifying Encounter During Measurement Period Expanded");
        expressions.add("Initial Population");
        expressions.add("Denominator");
        expressions.add("Denominator Exception");
        expressions.add("Numerator");
        expressions.add("SDE Ethnicity");
        expressions.add("SDE Race");
        expressions.add("SDE Sex");
        expressions.add("SDE Payer");
        //var manager = setupUncollapsedDataRequirementsAnalysis(new NamespaceInfo("gov.healthit.ecqi.ecqms", "http://ecqi.healthit.gov/ecqms"), "CMS143/cql/TestUnion.cql", compilerOptions);
        var manager = setupDataRequirementsAnalysis(new NamespaceInfo("gov.healthit.ecqi.ecqms", "http://ecqi.healthit.gov/ecqms"), "CMS143/cql/POAGOpticNerveEvaluationFHIR-0.0.003.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions, expressions);
        assertNotNull(moduleDefinitionLibrary);
        assertEqualToExpectedModuleDefinitionLibrary(moduleDefinitionLibrary, "CMS143/resources/Library-EffectiveDataRequirements.json");

        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestSDESex() throws IOException {
        CqlCompilerOptions compilerOptions = CqlCompilerOptions.defaultOptions();
        compilerOptions.getOptions().add(CqlCompilerOptions.Options.EnableResultTypes);
        Set<String> expressions = new HashSet<>();
        expressions.add("SDE Sex");
        var manager = setupDataRequirementsAnalysis(new NamespaceInfo("gov.healthit.ecqi.ecqms", "http://ecqi.healthit.gov/ecqms"), "CMS143/cql/POAGOpticNerveEvaluationFHIR-0.0.003.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions, expressions);
        assertNotNull(moduleDefinitionLibrary);
        assertEqualToExpectedModuleDefinitionLibrary(moduleDefinitionLibrary, "CMS143/resources/Library-SDESex-EffectiveDataRequirements.json");

        // Has direct reference codes to M#http://hl7.org/fhir/v3/AdministrativeGender and F#http://hl7.org/fhir/v3/AdministrativeGender
        // Has relatedArtifact to code system http://hl7.org/fhir/v3/AdministrativeGender
        // Has relatedArtifact to Library SDE
        // Has one and only one DataRequirement for Patient with profile QICore Patient and mustSupport gender

        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestSDEPayer() throws IOException {
        CqlCompilerOptions compilerOptions = CqlCompilerOptions.defaultOptions();
        compilerOptions.getOptions().add(CqlCompilerOptions.Options.EnableResultTypes);
        Set<String> expressions = new HashSet<>();
        expressions.add("SDE Payer");
        var manager = setupDataRequirementsAnalysis(new NamespaceInfo("gov.healthit.ecqi.ecqms", "http://ecqi.healthit.gov/ecqms"), "CMS143/cql/POAGOpticNerveEvaluationFHIR-0.0.003.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions, expressions);
        assertNotNull(moduleDefinitionLibrary);
        assertEqualToExpectedModuleDefinitionLibrary(moduleDefinitionLibrary, "CMS143/resources/Library-SDEPayer-EffectiveDataRequirements.json");

        // Has relatedArtifact to Library SDE
        // Has relatedArtifact to Value Set Payer
        // Has one and only one DatRequirement for Coverage with the Payer Type value set and mustSupport type and period

        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestSDEEthnicity() throws IOException {
        CqlCompilerOptions compilerOptions = CqlCompilerOptions.defaultOptions();
        compilerOptions.getOptions().add(CqlCompilerOptions.Options.EnableResultTypes);
        Set<String> expressions = new HashSet<>();
        expressions.add("SDE Ethnicity");
        var manager = setupDataRequirementsAnalysis(new NamespaceInfo("gov.healthit.ecqi.ecqms", "http://ecqi.healthit.gov/ecqms"), "CMS143/cql/POAGOpticNerveEvaluationFHIR-0.0.003.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions, expressions);
        assertNotNull(moduleDefinitionLibrary);
        assertEqualToExpectedModuleDefinitionLibrary(moduleDefinitionLibrary, "CMS143/resources/Library-SDEEthnicity-EffectiveDataRequirements.json");

        // Has relatedArtifact to Library SDE
        // Has one and only one DatRequirement for Patient with the QICore Profile and mustSupport ethnicity

        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestSDERace() throws IOException {
        CqlCompilerOptions compilerOptions = CqlCompilerOptions.defaultOptions();
        compilerOptions.getOptions().add(CqlCompilerOptions.Options.EnableResultTypes);
        Set<String> expressions = new HashSet<>();
        expressions.add("SDE Race");
        var manager = setupDataRequirementsAnalysis(new NamespaceInfo("gov.healthit.ecqi.ecqms", "http://ecqi.healthit.gov/ecqms"), "CMS143/cql/POAGOpticNerveEvaluationFHIR-0.0.003.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions, expressions);
        assertNotNull(moduleDefinitionLibrary);
        assertEqualToExpectedModuleDefinitionLibrary(moduleDefinitionLibrary, "CMS143/resources/Library-SDERace-EffectiveDataRequirements.json");

        // Has relatedArtifact to Library SDE
        // Has one and only one DatRequirement for Patient with the QICore Profile and mustSupport race

        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestQualifyingEncounterMP() throws IOException {
        CqlCompilerOptions compilerOptions = CqlCompilerOptions.defaultOptions();
        compilerOptions.getOptions().add(CqlCompilerOptions.Options.EnableResultTypes);
        Set<String> expressions = new HashSet<>();
        expressions.add("Qualifying Encounter During Measurement Period");
        var manager = setupDataRequirementsAnalysis(new NamespaceInfo("gov.healthit.ecqi.ecqms", "http://ecqi.healthit.gov/ecqms"), "CMS143/cql/POAGOpticNerveEvaluationFHIR-0.0.003.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions, expressions);
        assertNotNull(moduleDefinitionLibrary);
        assertEqualToExpectedModuleDefinitionLibrary(moduleDefinitionLibrary, "CMS143/resources/Library-QualifyingEncounterMP-EffectiveDataRequirements.json");

        // Has direct reference codes to VR and AMB
        // Has relatedArtifact to ActCode code system
        // Has relatedArtifact to Office Visit ValueSet
        // Has relatedArtifact to Opthalmological Services ValueSet
        // Has relatedArtifact to Outpatient Consultation ValueSet
        // Has relatedArtifact to Nursing Facility Visit ValueSet
        // Has relatedArtifact to Care Services in Long-Term Residentail Facility ValueSet
        // Has 5 DataRequirements for Encounter with the QICore Encounter Profile and mustSupport type, period, and class, one for each ValueSet

        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }

    @Test
    public void TestCMS149() throws IOException {
        CqlCompilerOptions compilerOptions = getCompilerOptions();
        compilerOptions.setAnalyzeDataRequirements(false);
        var manager = setupDataRequirementsAnalysis("CMS149/cql/DementiaCognitiveAssessmentFHIR-0.0.003.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions, new HashMap<String, Object>(), ZonedDateTime.of(2023, 1, 16, 0, 0, 0, 0, ZoneId.of("UTC")));
        assertNotNull(moduleDefinitionLibrary);
        assertEqualToExpectedModuleDefinitionLibrary(moduleDefinitionLibrary, "CMS149/resources/Library-EffectiveDataRequirements.json");

        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);
    }
    
    private Extension getLogicDefinitionByName(List<Extension> logicDefinitions, String libraryName, String name) {
        for (Extension ld : logicDefinitions) {
            Extension ln = ld.getExtensionByUrl("libraryName");
            assertTrue(ln != null && ln.hasValue());
            Extension n = ld.getExtensionByUrl("name");
            assertTrue(n != null && n.hasValue());
            if (ln.getValueStringType().getValue().equals(libraryName) && n.getValueStringType().getValue().equals(name)) {
                return ld;
            }
        }
        return null;
    }

    @Test
    public void TestDeviceOrder() throws IOException {
        CqlCompilerOptions compilerOptions = CqlCompilerOptions.defaultOptions();
        var manager = setupDataRequirementsGather("DeviceOrder/TestDeviceOrder.cql", compilerOptions);
        org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = getModuleDefinitionLibrary(manager, compilerOptions, new HashMap<String, Object>(), ZonedDateTime.of(2023, 1, 16, 0, 0, 0, 0, ZoneId.of("UTC")), true);
        assertNotNull(moduleDefinitionLibrary);
        assertEqualToExpectedModuleDefinitionLibrary(moduleDefinitionLibrary, "DeviceOrder/Library-TestDeviceOrder-EffectiveDataRequirements.json");
        //outputModuleDefinitionLibrary(moduleDefinitionLibrary);

        List<Extension> logicDefinitions = moduleDefinitionLibrary.getExtensionsByUrl("http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-logicDefinition");
        assertTrue(logicDefinitions != null);
        assertTrue(logicDefinitions.size() > 0);
        Extension logicDefinition = getLogicDefinitionByName(logicDefinitions, "TestDeviceOrder", "isDeviceOrder");
        assertTrue(logicDefinition != null);
    }

    @Test
    public void TestDataRequirementsProcessorWithPertinence() {
        CqlCompilerOptions cqlTranslatorOptions = new CqlCompilerOptions();

        cqlTranslatorOptions.getOptions().add(CqlCompilerOptions.Options.EnableAnnotations);
        try {
            var setup = setup("CompositeMeasures/cql/pertinence-tag.cql", cqlTranslatorOptions);//"OpioidCDS/cql/OpioidCDSCommon.cql", cqlTranslatorOptions);



            DataRequirementsProcessor dqReqTrans = new DataRequirementsProcessor();
            org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = dqReqTrans.gatherDataRequirements(setup.manager(), setup.library(), cqlTranslatorOptions, null, false);

            assertTrue(moduleDefinitionLibrary.getDataRequirement().size() == 3);
            DataRequirement dr = moduleDefinitionLibrary.getDataRequirement().get(1);
            assertEquals(dr.getType(), Enumerations.FHIRTypes.CONDITION);
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
        CqlCompilerOptions cqlTranslatorOptions = new CqlCompilerOptions();

        cqlTranslatorOptions.getOptions().add(CqlCompilerOptions.Options.EnableAnnotations);
        try {
            var setup = setup("CompositeMeasures/cql/pertinence-tag-AdvancedIllnessandFrailtyExclusion_FHIR4-5.0.000.cql", cqlTranslatorOptions);//"OpioidCDS/cql/OpioidCDSCommon.cql", cqlTranslatorOptions);



            DataRequirementsProcessor dqReqTrans = new DataRequirementsProcessor();
            org.hl7.fhir.r5.model.Library moduleDefinitionLibrary = dqReqTrans.gatherDataRequirements(setup.manager(), setup.library(), cqlTranslatorOptions, null, false);

            DataRequirement dr = moduleDefinitionLibrary.getDataRequirement().get(1);
            assertEquals(dr.getType(), Enumerations.FHIRTypes.CONDITION);
            assertEquals(dr.getExtension().get(0).getUrl(), "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-pertinence");
            assertEquals(((Coding) dr.getExtension().get(0).getValue()).getCode(), "weakly-negative");

            DataRequirement dr2 = moduleDefinitionLibrary.getDataRequirement().get(2);
            assertEquals(dr2.getType(), Enumerations.FHIRTypes.ENCOUNTER);
            assertEquals(dr2.getExtension().get(0).getUrl(), "http://hl7.org/fhir/us/cqfmeasures/StructureDefinition/cqfm-pertinence");
            assertEquals(((Coding) dr2.getExtension().get(0).getValue()).getCode(), "pathognomonic");

            DataRequirement dr5 = moduleDefinitionLibrary.getDataRequirement().get(5);
            assertEquals(dr5.getType(), Enumerations.FHIRTypes.DEVICEREQUEST);
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

    public static class Setup {
        private final LibraryManager manager;
        private final CompiledLibrary library;

        public Setup(LibraryManager manager, CompiledLibrary library) {
            this.manager = manager;
            this.library = library;
        }

        public LibraryManager manager() {
            return this.manager;
        }

        public CompiledLibrary library() {
            return this.library;
        }
    }

    private static LibraryManager setup(CqlCompilerOptions options, String relativePath) {
        var modelManager = new ModelManager();
        var libraryManager = new LibraryManager(modelManager, options);
        libraryManager.getLibrarySourceLoader().registerProvider(new DefaultLibrarySourceProvider(Paths.get(relativePath)));
        libraryManager.getLibrarySourceLoader().registerProvider(new FhirLibrarySourceProvider());

        return libraryManager;
    }

    public static Setup setup(String testFileName, CqlCompilerOptions options) throws IOException {
        return setup(null, testFileName, options);
    }

    public static Setup setup(NamespaceInfo namespaceInfo, String testFileName, CqlCompilerOptions options) throws IOException {
        File translationTestFile = new File(DataRequirementsProcessorTest.class.getResource(testFileName).getFile());
        var manager = setup(options, translationTestFile.getParent());

        if (namespaceInfo != null) {
            manager.getNamespaceManager().addNamespace(namespaceInfo);
        }

        var compiler = new CqlCompiler(namespaceInfo, manager);

        var lib = compiler.run(translationTestFile);

        assertTrue(compiler.getErrors().isEmpty());

        manager.getCompiledLibraries().put(lib.getIdentifier(), compiler.getCompiledLibrary());

        return new Setup(manager, compiler.getCompiledLibrary());
    }
}