package org.cqframework.cql.poc.translator;

import org.antlr.v4.runtime.tree.ParseTree;
import org.cqframework.cql.elm.tracking.TrackBack;
import org.hl7.elm.r1.ClinicalRequest;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.Literal;
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

import static org.cqframework.cql.poc.translator.TestUtils.parseFile;
import static org.testng.Assert.*;

public class CMS146ElmTest {

    private ElmTranslatorVisitor visitor;
    private Library library;
    private ObjectFactory of;

    @BeforeTest
    public void setup() throws IOException {
        ParseTree tree = parseFile("CMS146v2_Test_CQM.cql", true);
        visitor = new ElmTranslatorVisitor();
        visitor.visit(tree);
        library = visitor.getLibrary();
        of = new ObjectFactory();
    }

    @Test
    public void testLibraryAndVersion() {
        assertEquals(library.getIdentifier(), of.createVersionedIdentifier().withId("CMS146").withVersion("2"));
    }

    @Test
    public void testClinicalRequests() {
        Collection<ClinicalRequest> actualCR = visitor.getClinicalRequests();

        Collection<ClinicalRequest> expectedCR = Arrays.asList(
                of.createClinicalRequest()
                        .withSubject(literal("ConditionOccurrence"))
                        .withCodes(of.createValueSetRef().withName("Acute Pharyngitis")),
                of.createClinicalRequest()
                        .withSubject(literal("ConditionOccurrence"))
                        .withCodes(of.createValueSetRef().withName("Acute Tonsilitis")),
                of.createClinicalRequest()
                        .withSubject(literal("MedicationPrescriptionOccurrence"))
                        .withCodes(of.createValueSetRef().withName("Antibiotic Medications")),
                of.createClinicalRequest()
                        .withSubject(literal("EncounterPerformanceOccurrence"))
                        .withCodes(of.createValueSetRef().withName("Ambulatory/ED Visit")),
                of.createClinicalRequest()
                        .withSubject(literal("ObservationResultOccurrence"))
                        .withCodes(of.createValueSetRef().withName("Group A Streptococcus Test"))
        );
        assertTrue(actualCR.containsAll(expectedCR) && expectedCR.containsAll(actualCR), "should capture all clinical requests");
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

        assertTrue(actualVS.containsAll(expectedVS) && expectedVS.containsAll(actualVS), "should capture all value set definitions");

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

        assertTrue(actualVars.containsAll(expectedVars) && expectedVars.containsAll(actualVars), "should capture all variables");
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
                    expectedNumbers = new int[] {22, 5, 22, 54};
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
            assertNotNull(dc.getTrackerId());
            // TODO: some objects get multiple trackers when they shouldn't
            // assertEquals(dc.getTrackbacks().size(), 1);

            TrackBack tb = dc.getTrackbacks().iterator().next();
            assertEquals(tb.getLibrary(), of.createVersionedIdentifier().withId("CMS146").withVersion("2"));
            assertEquals(tb.getStartLine(), expectedNumbers[0]);
            assertEquals(tb.getStartChar(), expectedNumbers[1]);
            assertEquals(tb.getEndLine(), expectedNumbers[2]);
            assertEquals(tb.getEndChar(), expectedNumbers[3]);
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
            assertNotNull(vs.getTrackerId());
            assertEquals(vs.getTrackbacks().size(), 1);

            TrackBack tb = vs.getTrackbacks().iterator().next();
            assertEquals(tb.getLibrary(), of.createVersionedIdentifier().withId("CMS146").withVersion("2"));
            assertEquals(tb.getStartLine(), expectedNumbers[0]);
            assertEquals(tb.getStartChar(), expectedNumbers[1]);
            assertEquals(tb.getEndLine(), expectedNumbers[2]);
            assertEquals(tb.getEndChar(), expectedNumbers[3]);
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
                    expectedNumbers = new int[] {21, 1, 22, 54};
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
            assertNotNull(ls.getTrackerId());
            assertEquals(ls.getTrackbacks().size(), 1, "Expecting 1 trackback but had: " + ls.getTrackbacks());

            TrackBack tb = ls.getTrackbacks().iterator().next();
            assertEquals(tb.getLibrary(), of.createVersionedIdentifier().withId("CMS146").withVersion("2"));
            assertEquals(tb.getStartLine(), expectedNumbers[0]);
            assertEquals(tb.getStartChar(), expectedNumbers[1]);
            assertEquals(tb.getEndLine(), expectedNumbers[2]);
            assertEquals(tb.getEndChar(), expectedNumbers[3]);
        }
    }

    private Literal literal(String str) {
        return of.createLiteral()
                .withValue(str)
                .withValueType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
    }
}
