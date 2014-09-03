package org.cqframework.cql.poc.interpreter;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.cqframework.cql.gen.cqlBaseVisitor;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;
import org.cqframework.cql.poc.interpreter.patient.Patient;
import org.cqframework.cql.poc.interpreter.patient.PatientDb;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A very simple and very incomplete CQL visitor as a proof of concept.
 */
public class CqlInterpreterVisitor extends cqlBaseVisitor {
    private Map<String, Object> vars = new HashMap<>();
    private Patient patient = null;
    private MeasurePeriod measurePeriod = null;

    public CqlInterpreterVisitor(Patient patient, MeasurePeriod measurePeriod) {
        this.patient = patient;
        this.measurePeriod = measurePeriod;
    }

    @Override
    public Object visitLetStatement(@NotNull cqlParser.LetStatementContext ctx) {
        String id = ctx.IDENTIFIER().toString();
        Object obj = visit(ctx.expression());
        vars.put(id, obj);
        return obj;
    }

    @Override
    public Object visitInequalityExpression(@NotNull cqlParser.InequalityExpressionContext ctx) {
        // TODO: Don't assume int
        int lhNum = ((Quantity) visit(ctx.expression(0))).getQuantity().intValue();
        int rhNum = ((Quantity) visit(ctx.expression(1))).getQuantity().intValue();
        boolean result;
        switch(ctx.getChild(1).getText()) {
            case "<" :
                result = lhNum < rhNum;
                break;
            case "<=" :
                result = lhNum < rhNum || lhNum == rhNum;
                break;
            case ">=" :
                result = lhNum > rhNum || lhNum == rhNum;
                break;
            case ">" :
                result = lhNum > rhNum;
                break;
            default :
                throw new IllegalArgumentException();

        }

        return result;
    }

    @Override
    public Quantity visitQuantityLiteral(@NotNull cqlParser.QuantityLiteralContext ctx) {
        String sNum = ctx.QUANTITY().getText();
        Number num = sNum.contains(".") ? Double.valueOf(sNum) : Integer.valueOf(sNum);

        return new Quantity(num, ctx.getText());
    }

    @Override
    public Object visitTermExpression(@NotNull cqlParser.TermExpressionContext ctx) {
        return visit(ctx.expressionTerm());
    }

    @Override
    public Object visitMethodExpressionTerm(@NotNull cqlParser.MethodExpressionTermContext ctx) {
        Object result = null;
        switch (ctx.expressionTerm().getText()) {
            case "AgeAt":
                cqlParser.TermExpressionContext exp = (cqlParser.TermExpressionContext) ctx.expression(0);
                // TODO: Don't assume date
                Date date = (Date) visit(exp.expressionTerm());
                result = new Quantity(patient.getAgeAt(date));
                break;
        }
        return result;
    }

    @Override
    public Object visitTimeBoundaryExpressionTerm(@NotNull cqlParser.TimeBoundaryExpressionTermContext ctx) {
        Date result = null;
        String subject = ctx.getChild(2).getText();
        switch (ctx.getChild(0).getText()) {
            case "start":
                if ("MeasurementPeriod".equals(subject))
                    result = measurePeriod.getStart();
                break;
            case "end":
                if ("MeasurementPeriod".equals(subject))
                    result = measurePeriod.getEnd();
                break;
        }

        return result;
    }

    public Map<String, Object> getVars() {
        return vars;
    }

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
