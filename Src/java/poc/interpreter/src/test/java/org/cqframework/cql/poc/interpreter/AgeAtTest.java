package org.cqframework.cql.poc.interpreter;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;
import org.cqframework.cql.poc.interpreter.patient.Patient;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.testng.Assert.assertEquals;

public class AgeAtTest {
    @Test
    public void testPatientsAtStartOfMP() throws ParseException {
        ParseTree tree = getParseTree("let InDemographic = AgeAt(start of MeasurementPeriod) >= 16");
        MeasurePeriod mp = MeasurePeriod.forYear(2013);

        Patient p = new Patient(1, "Bob", convertToDate("1997-01-01"));
        CqlInterpreterVisitor visitor = new CqlInterpreterVisitor(p, mp);
        visitor.visit(tree);
        assertEquals(visitor.getVars().get("InDemographic"), true);

        p = new Patient(2, "Sue", convertToDate("1997-01-02"));
        visitor = new CqlInterpreterVisitor(p, mp);
        visitor.visit(tree);
        assertEquals(visitor.getVars().get("InDemographic"), false);
    }

    @Test
    public void testPatientsAtEndOfMP() throws ParseException {
        ParseTree tree = getParseTree("let InDemographic = AgeAt(end of MeasurementPeriod) >= 16");
        MeasurePeriod mp = MeasurePeriod.forYear(2013);

        Patient p = new Patient(1, "Bob", convertToDate("1997-12-31"));
        CqlInterpreterVisitor visitor = new CqlInterpreterVisitor(p, mp);
        visitor.visit(tree);
        assertEquals(visitor.getVars().get("InDemographic"), true);

        p = new Patient(2, "Sue", convertToDate("1998-01-01"));
        visitor = new CqlInterpreterVisitor(p, mp);
        visitor.visit(tree);
        assertEquals(visitor.getVars().get("InDemographic"), false);
    }

    private ParseTree getParseTree(String logic) {
        ANTLRInputStream input = new ANTLRInputStream(logic);
        cqlLexer lexer = new cqlLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        cqlParser parser = new cqlParser(tokens);
        parser.setBuildParseTree(true);
        return parser.logic();
    }

    private Date convertToDate(String s) throws ParseException {
        return new SimpleDateFormat("yyyy-MM-dd").parse(s);
    }
}
