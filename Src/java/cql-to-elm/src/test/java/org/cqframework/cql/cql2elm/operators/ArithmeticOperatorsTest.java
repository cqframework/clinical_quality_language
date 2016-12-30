package org.cqframework.cql.cql2elm.operators;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.ModelManager;
import org.hl7.elm.r1.Abs;
import org.hl7.elm.r1.Ceiling;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.Floor;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.Ln;
import org.hl7.elm.r1.Log;
import org.hl7.elm.r1.Negate;
import org.hl7.elm.r1.Quantity;
import org.hl7.elm.r1.Round;
import org.hl7.elm.r1.Truncate;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.cqframework.cql.cql2elm.LibraryManager;

import static org.cqframework.cql.cql2elm.matchers.ConvertsToDecimalFrom.convertsToDecimalFrom;
import static org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.hasTypeAndResult;
import static org.cqframework.cql.cql2elm.matchers.LiteralFor.literalFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ArithmeticOperatorsTest {

    private Map<String, ExpressionDef> defs;

    @BeforeTest
    public void setup() throws IOException {
        ModelManager modelManager = new ModelManager();
        CqlTranslator translator = CqlTranslator.fromStream(ArithmeticOperatorsTest.class.getResourceAsStream("../OperatorTests/ArithmeticOperators.cql"), modelManager, new LibraryManager(modelManager));
        Library library = translator.toELM();
        defs = new HashMap<>();
        for (ExpressionDef def: library.getStatements().getDef()) {
            defs.put(def.getName(), def);
        }
    }

    @Test
    public void testCeiling() {
        ExpressionDef def = defs.get("IntegerCeiling");
        assertThat(def, hasTypeAndResult(Ceiling.class, "System.Integer"));

        Ceiling ceiling = (Ceiling) def.getExpression();
        assertThat(ceiling.getOperand(), convertsToDecimalFrom(1));

        def = defs.get("DecimalCeiling");
        assertThat(def, hasTypeAndResult(Ceiling.class, "System.Integer"));

        ceiling = (Ceiling) def.getExpression();
        assertThat(ceiling.getOperand(), literalFor(1.0));
    }

    @Test
    public void testFloor() {
        ExpressionDef def = defs.get("IntegerFloor");
        assertThat(def, hasTypeAndResult(Floor.class, "System.Integer"));

        Floor floor = (Floor) def.getExpression();
        assertThat(floor.getOperand(), convertsToDecimalFrom(1));

        def = defs.get("DecimalFloor");
        assertThat(def, hasTypeAndResult(Floor.class, "System.Integer"));

        floor = (Floor) def.getExpression();
        assertThat(floor.getOperand(), literalFor(1.0));
    }

    @Test
    public void testTruncate() {
        ExpressionDef def = defs.get("IntegerTruncate");
        assertThat(def, hasTypeAndResult(Truncate.class, "System.Integer"));

        Truncate truncate = (Truncate) def.getExpression();
        assertThat(truncate.getOperand(), convertsToDecimalFrom(5));

        def = defs.get("DecimalTruncate");
        assertThat(def, hasTypeAndResult(Truncate.class, "System.Integer"));

        truncate = (Truncate) def.getExpression();
        assertThat(truncate.getOperand(), literalFor(5.5));
    }

    @Test
    public void testAbs() {
        ExpressionDef def = defs.get("IntegerAbs");
        assertThat(def, hasTypeAndResult(Abs.class, "System.Integer"));

        Abs abs = (Abs) def.getExpression();
        assertThat(abs.getOperand(), instanceOf(Negate.class));
        assertThat(((Negate) abs.getOperand()).getOperand(), literalFor(1));

        def = defs.get("DecimalAbs");
        assertThat(def, hasTypeAndResult(Abs.class, "System.Decimal"));

        abs = (Abs) def.getExpression();
        assertThat(abs.getOperand(), instanceOf(Negate.class));
        assertThat(((Negate) abs.getOperand()).getOperand(), literalFor(1.0));

        def = defs.get("QuantityAbs");
        assertThat(def, hasTypeAndResult(Abs.class, "System.Quantity"));

        abs = (Abs) def.getExpression();
        assertThat(abs.getOperand(), instanceOf(Negate.class));
        Negate n = (Negate) abs.getOperand();
        assertThat(n.getOperand(), instanceOf(Quantity.class));
        Quantity q = (Quantity) n.getOperand();
        assertThat(q.getValue(), is(BigDecimal.valueOf(1.0)));
        assertThat(q.getUnit(), is("cm"));
    }

    @Test
    public void testLog() {
        ExpressionDef def = defs.get("DecimalDecimalLog");
        assertThat(def, hasTypeAndResult(Log.class, "System.Decimal"));

        Log log = (Log) def.getExpression();
        assertThat(log.getOperand(), hasSize(2));
        assertThat(log.getOperand().get(0), literalFor(1000.0));
        assertThat(log.getOperand().get(1), literalFor(10.0));

        def = defs.get("DecimalIntegerLog");
        assertThat(def, hasTypeAndResult(Log.class, "System.Decimal"));

        log = (Log) def.getExpression();
        assertThat(log.getOperand(), hasSize(2));
        assertThat(log.getOperand().get(0), literalFor(1000.0));
        assertThat(log.getOperand().get(1), convertsToDecimalFrom(10));

        def = defs.get("IntegerDecimalLog");
        assertThat(def, hasTypeAndResult(Log.class, "System.Decimal"));

        log = (Log) def.getExpression();
        assertThat(log.getOperand(), hasSize(2));
        assertThat(log.getOperand().get(0), convertsToDecimalFrom(1000));
        assertThat(log.getOperand().get(1), literalFor(10.0));

        def = defs.get("IntegerIntegerLog");
        assertThat(def, hasTypeAndResult(Log.class, "System.Decimal"));

        log = (Log) def.getExpression();
        assertThat(log.getOperand(), hasSize(2));
        assertThat(log.getOperand().get(0), convertsToDecimalFrom(1000));
        assertThat(log.getOperand().get(1), convertsToDecimalFrom(10));
    }

    @Test
    public void testLn() {
        ExpressionDef def = defs.get("IntegerLn");
        assertThat(def, hasTypeAndResult(Ln.class, "System.Decimal"));

        Ln ln = (Ln) def.getExpression();
        assertThat(ln.getOperand(), convertsToDecimalFrom(1000));

        def = defs.get("DecimalLn");
        assertThat(def, hasTypeAndResult(Ln.class, "System.Decimal"));

        ln = (Ln) def.getExpression();
        assertThat(ln.getOperand(), literalFor(1000.0));
    }

    @Test
    public void testExp() {
        ExpressionDef def = defs.get("IntegerExp");
        assertThat(def, hasTypeAndResult(Exp.class, "System.Decimal"));

        Exp exp = (Exp)def.getExpression();
        assertThat(exp.getOperand(), convertsToDecimalFrom(1000));

        def = defs.get("DecimalExp");
        assertThat(def, hasTypeAndResult(Exp.class, "System.Decimal"));

        exp = (Exp)def.getExpression();
        assertThat(exp.getOperand(), literalFor(1000.0));
    }

    @Test
    public void testRound() {
        ExpressionDef def = defs.get("DecimalRound");
        assertThat(def, hasTypeAndResult(Round.class, "System.Decimal"));

        Round round = (Round) def.getExpression();
        assertThat(round.getOperand(), literalFor(10.55));
        assertThat(round.getPrecision(), nullValue());

        def = defs.get("DecimalRoundWithPrecision");
        assertThat(def, hasTypeAndResult(Round.class, "System.Decimal"));

        round = (Round) def.getExpression();
        assertThat(round.getOperand(), literalFor(10.5555));
        assertThat(round.getPrecision(), literalFor(2));
    }
}
