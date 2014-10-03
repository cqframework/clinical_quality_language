package org.cqframework.cql.cql2elm;

import org.cqframework.cql.elm.tracking.TrackBack;
import org.hl7.elm.r1.ClinicalRequest;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.ModelReference;
import org.hl7.elm.r1.ObjectFactory;
import org.hl7.elm.r1.ValueSet;
import org.hl7.elm.r1.ValueSetDef;
import org.hl7.elm.r1.ValueSetRef;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.cqframework.cql.cql2elm.TestUtils.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

public class CMS146ElmTest {

    private Cql2ElmVisitor visitor;
    private Library library;
    private ObjectFactory of;

    @BeforeTest
    public void setup() throws IOException {
        visitor = visitFile("CMS146v2_Test_CQM.cql", true);
        library = visitor.getLibrary();
        of = new ObjectFactory();
    }

    @Test
    public void testLibraryAndVersion() {
        assertThat(library.getIdentifier(), is(of.createVersionedIdentifier().withId("CMS146").withVersion("2")));
    }

    @Test
    public void testUsingDataModel() {
        List<ModelReference> models = library.getDataModels().getModelReference();
        assertThat(models, hasSize(1));
        assertThat(models.get(0).getReferencedModel().getValue(), is("http://org.hl7.fhir"));
        assertThat(models.get(0).getDescription(), is(nullValue()));
    }

    @Test
    public void testClinicalRequests() {
        Collection<ClinicalRequest> actualCR = visitor.getClinicalRequests();

        Collection<ClinicalRequest> expectedCR = Arrays.asList(
                of.createClinicalRequest()
                        .withDataType(quickDataType("ConditionOccurrence"))
                        .withCodeProperty("code")
                        .withCodes(of.createValueSetRef().withName("Acute Pharyngitis")),
                of.createClinicalRequest()
                        .withDataType(quickDataType("ConditionOccurrence"))
                        .withCodeProperty("code")
                        .withCodes(of.createValueSetRef().withName("Acute Tonsilitis")),
                of.createClinicalRequest()
                        .withDataType(quickDataType("MedicationTreatmentOrderOccurrence"))
                        .withCodes(of.createValueSetRef().withName("Antibiotic Medications")),
                of.createClinicalRequest()
                        .withDataType(quickDataType("EncounterPerformanceOccurrence"))
                        .withCodeProperty("class")
                        .withCodes(of.createValueSetRef().withName("Ambulatory/ED Visit")),
                of.createClinicalRequest()
                        .withDataType(quickDataType("SimpleObservationOccurrence"))
                        .withCodeProperty("code")
                        .withCodes(of.createValueSetRef().withName("Group A Streptococcus Test"))
        );

        assertThat(actualCR, is(expectedCR));
    }

    @Test
    public void testValueSets() {
        Collection<ValueSetDef> actualVS = library.getValueSets().getDef();

        Collection<ValueSetDef> expectedVS = Arrays.asList(
                of.createValueSetDef()
                        .withName("Acute Pharyngitis")
                        .withValueSet(of.createValueSet().withId("2.16.840.1.113883.3.464.1003.102.12.1011")),
                of.createValueSetDef()
                        .withName("Acute Tonsillitis")
                        .withValueSet(of.createValueSet().withId("2.16.840.1.113883.3.464.1003.102.12.1012")),
                of.createValueSetDef()
                        .withName("Ambulatory/ED Visit")
                        .withValueSet(of.createValueSet().withId("2.16.840.1.113883.3.464.1003.101.12.1061")),
                of.createValueSetDef()
                        .withName("Antibiotic Medications")
                        .withValueSet(of.createValueSet().withId("2.16.840.1.113883.3.464.1003.196.12.1001")),
                of.createValueSetDef()
                        .withName("Group A Streptococcus Test")
                        .withValueSet(of.createValueSet().withId("2.16.840.1.113883.3.464.1003.198.12.1012"))
        );

        assertThat(actualVS, is(expectedVS));
    }

    @Test
    public void testVariables() {
        Collection<String> actualVars = new ArrayList<>();
        for (ExpressionDef def : library.getStatements().getDef()) {
            actualVars.add(def.getName());
        }

        Collection<String> expectedVars = Arrays.asList("InDemographic", "Pharyngitis", "Antibiotics", "TargetEncounters",
                "TargetDiagnoses", "HasPriorAntibiotics", "HasTargetEncounter", "InInitialPopulation", "InDenominator",
                "InDenominatorExclusions", "InNumerator");

        assertThat(actualVars, is(expectedVars));
    }

    @Test
    public void testTrackBacks() {
        for (ClinicalRequest dc : visitor.getClinicalRequests()) {
            int expectedNumbers[] = {0, 0, 0, 0};
            switch (((ValueSetRef) dc.getCodes()).getName()) {
                case "Acute Pharyngitis":
                    expectedNumbers = new int[] {19, 6, 19, 37};
                    break;
                case "Acute Tonsilitis":
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
            int expectedNumbers[] = {0, 0, 0, 0};
            switch (((ValueSet) vs.getValueSet()).getId()) {
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
            int expectedNumbers[] = {0, 0, 0, 0};
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
        return new QName("http://org.hl7.fhir", dataTypeName, "quick");
    }
}
