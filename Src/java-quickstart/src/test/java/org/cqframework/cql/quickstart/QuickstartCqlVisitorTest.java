package org.cqframework.cql.quickstart;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;
import org.testng.annotations.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import static org.testng.Assert.*;

public class QuickstartCqlVisitorTest {
    @Test
    public void testLibraryAndVersion() {
        QuickstartCqlVisitor v = visitLogic("library MyTest version '99'");

        assertEquals(v.getLibrary(), "MyTest");
        assertEquals(v.getVersion(), "99");
    }

    @Test
    public void testNullVersion() {
        QuickstartCqlVisitor v = visitLogic("library MyTest");

        assertEquals(v.getLibrary(), "MyTest");
        assertNull(v.getVersion());
    }

    @Test
    public void testValuesets() {
        QuickstartCqlVisitor v = visitLogic(
                "valueset \"Acute Pharyngitis\" = ValueSet('2.16.840.1.113883.3.464.1003.102.12.1011')\n" +
                "valueset \"Acute Tonsillitis\" = ValueSet('2.16.840.1.113883.3.464.1003.102.12.1012')");

        Map<String, ValueSet> vsMap = v.getValuesets();
        assertEquals(vsMap.size(), 2);

        ValueSet vs = vsMap.get("Acute Pharyngitis");
        assertEquals(vs.getId(), "2.16.840.1.113883.3.464.1003.102.12.1011");

        vs = vsMap.get("Acute Tonsillitis");
        assertEquals(vs.getId(), "2.16.840.1.113883.3.464.1003.102.12.1012");
    }

    @Test
    public void testRetrieve() {
        QuickstartCqlVisitor v = visitLogic(
                "valueset \"Acute Pharyngitis\" = ValueSet('2.16.840.1.113883.3.464.1003.102.12.1011')\n" +
                "let pharyngitis = [Condition: \"Acute Pharyngitis\"]");

        Collection<Retrieve> rSet = v.getRetrieves();
        assertEquals(rSet.size(), 1);

        Retrieve r = rSet.iterator().next();
        assertEquals(r.getExistence(), Retrieve.Existence.Occurrence);
        assertEquals(r.getTopic(), "Condition");
        assertEquals(r.getValueset(), new ValueSet("2.16.840.1.113883.3.464.1003.102.12.1011"));
        assertNull(r.getModality());
    }

    @Test
    public void testRetrieveNonOccurrence() {
        QuickstartCqlVisitor v = visitLogic(
                "valueset \"Office Visit\" = ValueSet('2.16.840.1.113883.3.464.1003.101.12.1001')\n" +
                "let encounter = [NonOccurrence of Encounter, Performance: \"Office Visit\"]");

        Collection<Retrieve> rSet = v.getRetrieves();
        assertEquals(rSet.size(), 1);

        Retrieve r = rSet.iterator().next();
        assertEquals(r.getExistence(), Retrieve.Existence.NonOccurrence);
        assertEquals(r.getTopic(), "Encounter");
        assertEquals(r.getModality(), "Performance");
        assertEquals(r.getValueset(), new ValueSet("2.16.840.1.113883.3.464.1003.101.12.1001"));
    }

    @Test
    public void testMultipleRetrieveReferencesArentDoubleCounted() {
        QuickstartCqlVisitor v = visitLogic(
                "valueset \"Acute Pharyngitis\" = ValueSet('2.16.840.1.113883.3.464.1003.102.12.1011')\n" +
                "let pharyngitis = [Condition: \"Acute Pharyngitis\"]\n" +
                "let pharyngitis_two = [Condition: \"Acute Pharyngitis\"]");

        Collection<Retrieve> rSet = v.getRetrieves();
        assertEquals(rSet.size(), 1);
    }

    @Test (expectedExceptions = IllegalArgumentException.class)
    public void testRetrieveWithUndefinedValueset() {
        visitLogic(
                "valueset \"Acute Pharyngitis\" = ValueSet('2.16.840.1.113883.3.464.1003.102.12.1011')\n" +
                "let pharyngitis = [Condition: \"Acute Farenjidis\"]");
    }

    @Test
    public void testVariables() {
        QuickstartCqlVisitor v = visitLogic(
                "valueset \"Office Visit\" = ValueSet('2.16.840.1.113883.3.464.1003.101.12.1001')\n" +
                "valueset \"Acute Pharyngitis\" = ValueSet('2.16.840.1.113883.3.464.1003.102.12.1011')\n" +
                "let encounter = [NonOccurrence of Encounter, Performance: \"Office Visit\"]\n" +
                "let pharyngitis = [Condition: \"Acute Pharyngitis\"]");

        Collection<String> varSet = v.getVariables();
        assertEquals(varSet.size(), 2);

        assertTrue(varSet.contains("encounter"));
        assertTrue(varSet.contains("pharyngitis"));
    }

    @Test
    public void testChlamydiaScreeningMeasure() {
        QuickstartCqlVisitor v = visitLogicFile("/ChlamydiaScreening_CQM.cql", true);

        assertEquals(v.getLibrary(), "CMS153_CQM");
        assertEquals(v.getVersion(), "2");
        assertEquals(v.getVariables().size(), 6);
        assertEquals(v.getValuesets().size(), 14);
        assertEquals(v.getRetrieves().size(), 13);
    }

    private QuickstartCqlVisitor visitLogic(String logic) {
        return visitANTLRInputStream(new ANTLRInputStream(logic));
    }

    private QuickstartCqlVisitor visitLogicFile(String filename, boolean inClassPath) {
        QuickstartCqlVisitor visitor = null;
        try {
            InputStream is = inClassPath ? this.getClass().getResourceAsStream(filename) : new FileInputStream(filename);
            visitor = visitANTLRInputStream(new ANTLRInputStream(is));
        } catch (IOException e) {
            fail("Couldn't load file: " + filename);
        }

        return visitor;
    }

    private QuickstartCqlVisitor visitANTLRInputStream(ANTLRInputStream is) {
        cqlLexer lexer = new cqlLexer(is);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        cqlParser parser = new cqlParser(tokens);
        parser.setBuildParseTree(true);
        QuickstartCqlVisitor visitor = new QuickstartCqlVisitor();
        visitor.visit(parser.logic());

        return visitor;
    }

}
