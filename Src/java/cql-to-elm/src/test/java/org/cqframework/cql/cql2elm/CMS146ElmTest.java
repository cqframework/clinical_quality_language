package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.CqlCompilerException.ErrorSeverity;
import org.cqframework.cql.cql2elm.LibraryBuilder.SignatureLevel;
import org.cqframework.cql.elm.tracking.TrackBack;
import org.hl7.cql_annotations.r1.CqlToElmBase;
import org.hl7.cql_annotations.r1.CqlToElmInfo;
import org.hl7.elm.r1.*;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

public class CMS146ElmTest {

    private CqlTranslator translator;
    private Library library;
    private ObjectFactory of;

    @BeforeTest
    public void setup() throws IOException {
        ModelManager modelManager = new ModelManager();
        translator = CqlTranslator.fromStream(CMS146ElmTest.class.getResourceAsStream("CMS146v2_Test_CQM.cql"), new LibraryManager(modelManager, new CqlCompilerOptions(ErrorSeverity.Warning, SignatureLevel.None)));
        assertThat(translator.getErrors().size(), is(0));
        library = translator.toELM();
        of = new ObjectFactory();
    }

    @DataProvider(name = "sigLevels")
    public static Object[][] primeNumbers() {
        return new Object[][] {{SignatureLevel.None}, {SignatureLevel.Differing}, {SignatureLevel.Overloads}, {SignatureLevel.All}};
    }

    @Test(dataProvider = "sigLevels")
    public void testSignatureLevels(SignatureLevel signatureLevel) throws IOException {
        final ModelManager modelManager = new ModelManager();
        final CqlTranslator translator = CqlTranslator.fromStream(CMS146ElmTest.class.getResourceAsStream("CMS146v2_Test_CQM.cql"), new LibraryManager(modelManager, new CqlCompilerOptions(ErrorSeverity.Warning, signatureLevel)));
        final Library library = translator.toELM();

        final List<CqlToElmBase> annotations = library.getAnnotation();
        assertThat(annotations.size(), equalTo(3));

        final List<CqlToElmInfo> casts =
                annotations.stream()
                        .filter(CqlToElmInfo.class::isInstance)
                        .map(CqlToElmInfo.class::cast)
                        .collect(Collectors.toList());

        assertThat(casts.size(), equalTo(1));
        assertThat(casts.get(0).getSignatureLevel(), equalTo(signatureLevel.name()));
    }

    @Test
    public void testLibraryAndVersion() {
        assertThat(library.getIdentifier(), is(of.createVersionedIdentifier().withId("CMS146").withVersion("2")));
    }

    @Test
    public void testUsingDataModel() {
        List<UsingDef> models = library.getUsings().getDef();
        assertThat(models, hasSize(2));
        assertThat(models.get(1).getUri(), is("http://hl7.org/fhir"));
    }

    @Test
    public void testClinicalRequests() {
        Collection<Retrieve> actualCR = translator.toRetrieves();

        Collection<Retrieve> expectedCR = Arrays.asList(
                of.createRetrieve()
                        .withDataType(quickDataType("Condition"))
                        .withTemplateId("condition-qicore-qicore-condition")
                        .withCodeProperty("code")
                        .withCodeComparator("in")
                        .withCodes(of.createValueSetRef().withName("Acute Pharyngitis").withPreserve(true)),
                of.createRetrieve()
                        .withDataType(quickDataType("Condition"))
                        .withTemplateId("condition-qicore-qicore-condition")
                        .withCodeProperty("code")
                        .withCodeComparator("in")
                        .withCodes(of.createValueSetRef().withName("Acute Tonsillitis").withPreserve(true)),
                of.createRetrieve()
                        .withDataType(quickDataType("MedicationPrescription"))
                        .withTemplateId("medicationprescription-qicore-qicore-medicationprescription")
                        .withCodeProperty("medication.code")
                        .withCodeComparator("in")
                        .withCodes(of.createValueSetRef().withName("Antibiotic Medications").withPreserve(true)),
                of.createRetrieve()
                        .withDataType(quickDataType("Encounter"))
                        .withTemplateId("encounter-qicore-qicore-encounter")
                        .withCodeProperty("type")
                        .withCodeComparator("in")
                        .withCodes(of.createValueSetRef().withName("Ambulatory/ED Visit").withPreserve(true)),
                of.createRetrieve()
                        .withDataType(quickDataType("Observation"))
                        .withTemplateId("observation-qicore-qicore-observation")
                        .withCodeProperty("code")
                        .withCodeComparator("in")
                        .withCodes(of.createValueSetRef().withName("Group A Streptococcus Test").withPreserve(true))
        );

        assertThat(actualCR, is(expectedCR));
    }

    // TODO: Disabled the test for now, valuesets have been moved to expression definitions. These are being checked in
    // the testVariables() test, but not as completely as this.
    @Test(enabled=false)
    public void testValueSets() {
        Collection<ValueSetDef> actualVS = library.getValueSets().getDef();

        Collection<ValueSetDef> expectedVS = Arrays.asList(
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
                        .withId("2.16.840.1.113883.3.464.1003.198.12.1012")
        );

        assertThat(actualVS, is(expectedVS));
    }

    @Test
    public void testVariables() {
        Collection<String> actualVars = new ArrayList<>();
        for (ExpressionDef def : library.getStatements().getDef()) {
            actualVars.add(def.getName());
        }

        Collection<String> expectedVars = Arrays.asList("Patient", "InDemographic", "Pharyngitis", "Antibiotics", "TargetEncounters",
                "TargetDiagnoses", "HasPriorAntibiotics", "HasTargetEncounter", "InInitialPopulation", "InDenominator",
                "InDenominatorExclusions", "InNumerator");

        assertThat(actualVars, is(expectedVars));
    }

    // TODO: Disabled the test for now, needs to be updated to use annotations, will update after all syntax changes.
    @Test(enabled=false)
    public void testTrackBacks() {
        for (Retrieve dc : translator.toRetrieves()) {
            int expectedNumbers[] = new int[4];
            switch (((ValueSetRef) dc.getCodes()).getName()) {
                case "Acute Pharyngitis":
                    expectedNumbers = new int[] {19, 6, 19, 37};
                    break;
                case "Acute Tonsillitis":
                    expectedNumbers = new int[] {19, 47, 19, 77};
                    break;
                case "Antibiotic Medications":
                    expectedNumbers = new int[] {22, 5, 22, 58};
                    break;
                case "Ambulatory/ED Visit":
                    expectedNumbers = new int[] {25, 5, 25, 51};
                    break;
                case "Group A Streptococcus Test":
                    expectedNumbers = new int[] {49, 13, 49, 61};
                    break;
                default:
                    fail("Unknown source data criteria: " + dc);
            }
            assertThat(dc.getTrackerId(), notNullValue());
            // TODO: some objects get multiple trackers when they shouldn't
            // assertThat(dc.getTrackbacks().size(), is(1));

            TrackBack tb = dc.getTrackbacks().iterator().next();
            assertThat(tb.getLibrary(), is(of.createVersionedIdentifier().withId("CMS146").withVersion("2")));
            assertThat(tb.getStartLine(), is(expectedNumbers[0]));
            assertThat(tb.getStartChar(), is(expectedNumbers[1]));
            assertThat(tb.getEndLine(), is(expectedNumbers[2]));
            assertThat(tb.getEndChar(), is(expectedNumbers[3]));
        }

        for (ValueSetDef vs : library.getValueSets().getDef()) {
            int expectedNumbers[] = new int[4];
            switch (vs.getId()) {
                case "2.16.840.1.113883.3.464.1003.102.12.1011":
                    expectedNumbers = new int[] {7, 1, 7, 83};
                    break;
                case "2.16.840.1.113883.3.464.1003.102.12.1012":
                    expectedNumbers = new int[] {8, 1, 8, 83};
                    break;
                case "2.16.840.1.113883.3.464.1003.101.12.1061":
                    expectedNumbers = new int[] {9, 1, 9, 85};
                    break;
                case "2.16.840.1.113883.3.464.1003.196.12.1001":
                    expectedNumbers = new int[] {10, 1, 10, 88};
                    break;
                case "2.16.840.1.113883.3.464.1003.198.12.1012":
                    expectedNumbers = new int[] {11, 1, 11, 92};
                    break;
                default:
                    fail("Unknown valueset: " + vs);
            }
            assertThat(vs.getTrackerId(), notNullValue());
            assertThat(vs.getTrackbacks().size(), is(1));

            TrackBack tb = vs.getTrackbacks().iterator().next();
            assertThat(tb.getLibrary(), is(of.createVersionedIdentifier().withId("CMS146").withVersion("2")));
            assertThat(tb.getStartLine(), is(expectedNumbers[0]));
            assertThat(tb.getStartChar(), is(expectedNumbers[1]));
            assertThat(tb.getEndLine(), is(expectedNumbers[2]));
            assertThat(tb.getEndChar(), is(expectedNumbers[3]));
        }

        for (ExpressionDef ls : library.getStatements().getDef()) {
            int expectedNumbers[] = new int[4];
            switch (ls.getName()) {
                case "InDemographic":
                    expectedNumbers = new int[] {15, 1, 16, 85};
                    break;
                case "Pharyngitis":
                    expectedNumbers = new int[] {18, 1, 19, 78};
                    break;
                case "Antibiotics":
                    expectedNumbers = new int[] {21, 1, 22, 58};
                    break;
                case "TargetEncounters":
                    expectedNumbers = new int[] {24, 1, 28, 56};
                    break;
                case "TargetDiagnoses":
                    expectedNumbers = new int[] {30, 1, 31, 96};
                    break;
                case "HasPriorAntibiotics":
                    expectedNumbers = new int[] {33, 1, 34, 123};
                    break;
                case "HasTargetEncounter":
                    expectedNumbers = new int[] {36, 1, 37, 29};
                    break;
                case "InInitialPopulation":
                    expectedNumbers = new int[] {39, 1, 40, 40};
                    break;
                case "InDenominator":
                    expectedNumbers = new int[] {42, 1, 43, 8};
                    break;
                case "InDenominatorExclusions":
                    expectedNumbers = new int[] {45, 1, 46, 23};
                    break;
                case "InNumerator":
                    expectedNumbers = new int[] {48, 1, 49, 137};
                    break;
                default:
                    fail("Unknown variable: " + ls.getName());
            }
            assertThat(ls.getTrackerId(), notNullValue());
            assertThat(ls.getTrackbacks().size(), is(1));

            TrackBack tb = ls.getTrackbacks().iterator().next();
            assertThat(tb.getLibrary(), is(of.createVersionedIdentifier().withId("CMS146").withVersion("2")));
            assertThat(tb.getStartLine(), is(expectedNumbers[0]));
            assertThat(tb.getStartChar(), is(expectedNumbers[1]));
            assertThat(tb.getEndLine(), is(expectedNumbers[2]));
            assertThat(tb.getEndChar(), is(expectedNumbers[3]));
        }
    }

    private QName quickDataType(String dataTypeName) {
        return new QName("http://hl7.org/fhir", dataTypeName, "quick");
    }
}
