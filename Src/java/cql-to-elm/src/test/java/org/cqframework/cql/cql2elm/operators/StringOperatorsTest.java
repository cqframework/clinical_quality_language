package org.cqframework.cql.cql2elm.operators;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.hl7.elm.r1.Combine;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.Length;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.Lower;
import org.hl7.elm.r1.PositionOf;
import org.hl7.elm.r1.Split;
import org.hl7.elm.r1.Substring;
import org.hl7.elm.r1.Upper;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.cqframework.cql.cql2elm.LibraryManager;

import static org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.hasTypeAndResult;
import static org.cqframework.cql.cql2elm.matchers.ListOfLiterals.listOfLiterals;
import static org.cqframework.cql.cql2elm.matchers.LiteralFor.literalFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

public class StringOperatorsTest {

    private Map<String, ExpressionDef> defs;

    @BeforeTest
    public void setup() throws IOException {
        CqlTranslator translator = CqlTranslator.fromStream(StringOperatorsTest.class.getResourceAsStream("../OperatorTests/StringOperators.cql"), new LibraryManager());
        Library library = translator.toELM();
        defs = new HashMap<>();
        for (ExpressionDef def: library.getStatements().getDef()) {
            defs.put(def.getName(), def);
        }
    }

    @Test
    public void testCombine() {
        ExpressionDef def = defs.get("StringCombine");
        assertThat(def, hasTypeAndResult(Combine.class, "System.String"));
        Combine combine = (Combine) def.getExpression();
        assertThat(combine.getSource(), listOfLiterals("First", "Second", "Third", "Fourth"));
        assertThat(combine.getSeparator(), literalFor(","));

        def = defs.get("StringCombineNoSeparator");
        assertThat(def, hasTypeAndResult(Combine.class, "System.String"));
        combine = (Combine) def.getExpression();
        assertThat(combine.getSource(), listOfLiterals("abc", "def", "ghi", "jkl"));
        assertThat(combine.getSeparator(), nullValue());
    }

    @Test
    public void testSplit() {
        ExpressionDef def = defs.get("StringSplit");
        assertThat(def, hasTypeAndResult(Split.class, "list<System.String>"));
        Split split = (Split) def.getExpression();
        assertThat(split.getStringToSplit(), literalFor("First,Second,Third,Fourth"));
        assertThat(split.getSeparator(), literalFor(","));
    }

    @Test
    public void testUpper() {
        ExpressionDef def = defs.get("StringUpper");
        assertThat(def, hasTypeAndResult(Upper.class, "System.String"));
        Upper upper = (Upper) def.getExpression();
        assertThat(upper.getOperand(), literalFor("John"));
    }

    @Test
    public void testLower() {
        ExpressionDef def = defs.get("StringLower");
        assertThat(def, hasTypeAndResult(Lower.class, "System.String"));
        Lower lower = (Lower) def.getExpression();
        assertThat(lower.getOperand(), literalFor("John"));
    }

    @Test
    public void testPositionOf() {
        ExpressionDef def = defs.get("StringPositionOf");
        assertThat(def, hasTypeAndResult(PositionOf.class, "System.Integer"));
        PositionOf positionOf = (PositionOf) def.getExpression();
        assertThat(positionOf.getPattern(), literalFor("J"));
        assertThat(positionOf.getString(), literalFor("John"));
    }

    @Test
    public void testSubstring() {
        ExpressionDef def = defs.get("StringSubstring");
        assertThat(def, hasTypeAndResult(Substring.class, "System.String"));
        Substring substring = (Substring) def.getExpression();
        // Note: these casts to Expression are necessary because of bug in expression.xsd (DSTU comment #824)
        assertThat((Expression) substring.getStringToSub(), literalFor("JohnDoe"));
        assertThat((Expression) substring.getStartIndex(), literalFor(5));

        def = defs.get("StringSubstringWithLength");
        assertThat(def, hasTypeAndResult(Substring.class, "System.String"));
        substring = (Substring) def.getExpression();
        assertThat((Expression) substring.getStringToSub(), literalFor("JohnDoe"));
        assertThat((Expression) substring.getStartIndex(), literalFor(1));
        assertThat((Expression) substring.getLength(), literalFor(4));
    }

    @Test
    public void testLength() {
        ExpressionDef def = defs.get("StringLength");
        assertThat(def, hasTypeAndResult(Length.class, "System.Integer"));

        Length length = (Length) def.getExpression();
        assertThat(length.getOperand(), literalFor("John"));
    }
}
