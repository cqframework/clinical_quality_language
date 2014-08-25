package org.cqframework.cql.poc.translator;

import org.antlr.v4.runtime.tree.ParseTree;
import org.cqframework.cql.poc.translator.expressions.IdentifierExpression;
import org.cqframework.cql.poc.translator.expressions.LetStatement;
import org.cqframework.cql.poc.translator.expressions.QualifiedIdentifier;
import org.cqframework.cql.poc.translator.model.CqlLibrary;
import org.cqframework.cql.poc.translator.model.SourceDataCriteria;
import org.cqframework.cql.poc.translator.model.ValueSet;
import org.cqframework.cql.poc.translator.model.logger.TrackBack;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import static org.cqframework.cql.poc.translator.TestUtils.parseFile;
import static org.testng.Assert.*;

public class CMS146Test {

    private CqlLibrary library;

    @BeforeTest
    public void setup() throws IOException {
        ParseTree tree = parseFile("CMS146v2_Test_CQM.cql", true);
        CqlTranslatorVisitor visitor = new CqlTranslatorVisitor();
        visitor.visit(tree);
        library = visitor.getLibrary();
    }

    @Test
    public void testLibraryAndVersion() {
        assertEquals(library.getLibrary(), "CMS146");
        assertEquals(library.getVersion(), "2");
    }

    @Test
    public void testSourceDataCriteria() {
        Collection<SourceDataCriteria> actualSDC = library.getSourceDataCriteria();
        Collection<SourceDataCriteria> expectedSDC = Arrays.asList(
                new SourceDataCriteria(
                        SourceDataCriteria.Existence.Occurrence,
                        new QualifiedIdentifier(null, "Condition", false),
                        null,
                        new QualifiedIdentifier(null, "Acute Pharyngitis", true)),
                new SourceDataCriteria(
                        SourceDataCriteria.Existence.Occurrence,
                        new QualifiedIdentifier(null, "Condition", false),
                        null,
                        new QualifiedIdentifier(null, "Acute Tonsilitis", true)),
                new SourceDataCriteria(
                        SourceDataCriteria.Existence.Occurrence,
                        new QualifiedIdentifier(null, "MedicationTreatment", false),
                        new IdentifierExpression("Order"),
                        new QualifiedIdentifier(null, "Antibiotic Medications", true)),
                new SourceDataCriteria(
                        SourceDataCriteria.Existence.Occurrence,
                        new QualifiedIdentifier(null, "Encounter", false),
                        new IdentifierExpression("Performance"),
                        new QualifiedIdentifier(null, "Ambulatory/ED Visit", true)),
                new SourceDataCriteria(
                        SourceDataCriteria.Existence.Occurrence,
                        new QualifiedIdentifier(null, "SimpleObservation", false),
                        null,
                        new QualifiedIdentifier(null, "Group A Streptococcus Test", true))
        );
        assertTrue(actualSDC.containsAll(expectedSDC) && expectedSDC.containsAll(actualSDC), "should capture all source data criteria");
    }

    @Test
    public void testValueSets() {
        ValueSet pharyngitis = new ValueSet("2.16.840.1.113883.3.464.1003.102.12.1011", "Acute Pharyngitis");
        ValueSet tonsilitis = new ValueSet("2.16.840.1.113883.3.464.1003.102.12.1012", "Acute Tonsillitis");
        ValueSet ambulatory = new ValueSet("2.16.840.1.113883.3.464.1003.101.12.1061", "Ambulatory/ED Visit");
        ValueSet antiobiotics = new ValueSet("2.16.840.1.113883.3.464.1003.196.12.1001", "Antibiotic Medications");
        ValueSet strep = new ValueSet("2.16.840.1.113883.3.464.1003.198.12.1012", "Group A Streptococcus Test");

        Collection<ValueSet> actualVS = library.getValueSets();
        Collection<ValueSet> expectedVS = Arrays.asList(pharyngitis, tonsilitis, ambulatory, antiobiotics, strep);
        assertTrue(actualVS.containsAll(expectedVS) && expectedVS.containsAll(actualVS), "should capture all valuesets");

        for (ValueSet vs : expectedVS) {
            assertEquals(library.getValueSetByLocalStringIdentifier(vs.getLocalStringIdentifiers().iterator().next()), vs);
        }
    }

    @Test
    public void testVariables() {
        Collection<String> actualVars = library.getVariables().keySet();
        Collection<String> expectedVars = Arrays.asList("InDemographic", "Pharyngitis", "Antibiotics", "TargetEncounters",
                "TargetDiagnoses", "HasPriorAntibiotics", "HasTargetEncounter", "InInitialPopulation", "InDenominator",
                "InDenominatorExclusions", "InNumerator");

        assertTrue(actualVars.containsAll(expectedVars) && expectedVars.containsAll(actualVars), "should capture all variables");
    }

    @Test
    public void testTrackBacks() {
        for (SourceDataCriteria dc : library.getSourceDataCriteria()) {
            int expectedNumbers[] = {0, 0, 0, 0};
            switch (dc.getValueset().getIdentifier()) {
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
            assertNotNull(dc.getTrackerId());
            assertEquals(dc.getTrackbacks().size(), 1);

            TrackBack tb = dc.getTrackbacks().iterator().next();
            assertEquals(tb.getLibrary(), "CMS146");
            assertEquals(tb.getVersion(), "2");
            assertEquals(tb.getStartLine(), expectedNumbers[0]);
            assertEquals(tb.getStartChar(), expectedNumbers[1]);
            assertEquals(tb.getEndLine(), expectedNumbers[2]);
            assertEquals(tb.getEndChar(), expectedNumbers[3]);
        }

        for (ValueSet vs : library.getValueSets()) {
            int expectedNumbers[] = {0, 0, 0, 0};
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
            assertNotNull(vs.getTrackerId());
            assertEquals(vs.getTrackbacks().size(), 1);

            TrackBack tb = vs.getTrackbacks().iterator().next();
            assertEquals(tb.getLibrary(), "CMS146");
            assertEquals(tb.getVersion(), "2");
            assertEquals(tb.getStartLine(), expectedNumbers[0]);
            assertEquals(tb.getStartChar(), expectedNumbers[1]);
            assertEquals(tb.getEndLine(), expectedNumbers[2]);
            assertEquals(tb.getEndChar(), expectedNumbers[3]);
        }

        for (LetStatement ls : library.getVariables().values()) {
            int expectedNumbers[] = {0, 0, 0, 0};
            switch (ls.getIdentifier()) {
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
                    fail("Unknown variable: " + ls.getIdentifier());
            }
            assertNotNull(ls.getTrackerId());
            assertEquals(ls.getTrackbacks().size(), 1, "Expecting 1 trackback but had: " + ls.getTrackbacks());

            TrackBack tb = ls.getTrackbacks().iterator().next();
            assertEquals(tb.getLibrary(), "CMS146");
            assertEquals(tb.getVersion(), "2");
            assertEquals(tb.getStartLine(), expectedNumbers[0]);
            assertEquals(tb.getStartChar(), expectedNumbers[1]);
            assertEquals(tb.getEndLine(), expectedNumbers[2]);
            assertEquals(tb.getEndChar(), expectedNumbers[3]);
        }
    }
}
