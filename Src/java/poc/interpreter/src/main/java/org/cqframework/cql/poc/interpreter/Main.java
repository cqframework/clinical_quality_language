package org.cqframework.cql.poc.interpreter;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;
import org.cqframework.cql.poc.interpreter.patient.Patient;
import org.cqframework.cql.poc.interpreter.patient.PatientDb;

/**
 * A simple proof of concept / test program that demonstrates using an interpreter to process CQL.
 *
 * For example purposes only.  Currently only AgeAt expressions are supported.
 */
public class Main {
    public static void main(String[] args) {
        String logic = "let InDemographic = AgeAt(start of MeasurementPeriod) >= 16";
        ANTLRInputStream input = new ANTLRInputStream(logic);
        cqlLexer lexer = new cqlLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        cqlParser parser = new cqlParser(tokens);
        parser.setBuildParseTree(true);
        ParseTree tree = parser.logic();

        MeasurePeriod mp = MeasurePeriod.forYear(2013);
        System.out.println("|---------------------------------------------------------------------------------------");
        System.out.println("| MEASURE PERIOD: " + mp.getStart() + " - " + mp.getEnd());
        System.out.println("|---------------------------------------------------------------------------------------");
        System.out.println("| LOGIC: " + logic);
        System.out.println("|---------------------------------------------------------------------------------------");
        for (Patient p : PatientDb.instance().getPatients()) {
            System.out.println("| PATIENT " + p.getId() + ": " + p.getName() + "(" + p.getBirthdate() + ")");
            CqlInterpreterVisitor visitor = new CqlInterpreterVisitor(p, mp);
            visitor.visit(tree);

            for (String key : visitor.getVars().keySet()) {
                System.out.println("|   " + key + " = " + visitor.getVars().get(key));
            }
            System.out.println("|---------------------------------------------------------------------------------------");
        }
    }
}
