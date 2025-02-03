package org.cqframework.cql.cql2elm.operators;

import static org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.hasTypeAndResult;
import static org.cqframework.cql.cql2elm.matchers.ListOfLiterals.listOfLiterals;
import static org.cqframework.cql.cql2elm.matchers.LiteralFor.literalFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.cqframework.cql.cql2elm.*;
import org.cqframework.cql.cql2elm.CqlCompilerException.ErrorSeverity;
import org.cqframework.cql.cql2elm.LibraryBuilder.SignatureLevel;
import org.hl7.elm.r1.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class StringOperatorsTest {

    private static Map<String, ExpressionDef> defs;

    @BeforeAll
    static void setup() throws IOException {
        ModelManager modelManager = new ModelManager();
        CqlTranslator translator = CqlTranslator.fromStream(
                StringOperatorsTest.class.getResourceAsStream("../OperatorTests/StringOperators.cql"),
                new LibraryManager(modelManager, new CqlCompilerOptions(ErrorSeverity.Warning, SignatureLevel.None)));
        assertThat(translator.getErrors().size(), is(0));
        Library library = translator.toELM();
        defs = new HashMap<>();
        for (ExpressionDef def : library.getStatements().getDef()) {
            defs.put(def.getName(), def);
        }
    }

    @Test
    void add() {
        ExpressionDef def = defs.get("StringAdd");
        assertThat(def, hasTypeAndResult(Concatenate.class, "System.String"));
    }

    @Test
    void concatenate() {
        ExpressionDef def = defs.get("StringConcatenate");
        assertThat(def, hasTypeAndResult(Concatenate.class, "System.String"));
    }

    @Test
    void concatenateWithAmpersand() {
        ExpressionDef def = defs.get("StringConcatenateWithAmpersand");
        assertThat(def, hasTypeAndResult(Concatenate.class, "System.String"));

        Concatenate concatenate = (Concatenate) def.getExpression();
        for (Expression operand : concatenate.getOperand()) {
            assertThat(operand.getClass() == Coalesce.class, is(true));
        }
    }

    @Test
    void combine() {
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
    void split() {
        ExpressionDef def = defs.get("StringSplit");
        assertThat(def, hasTypeAndResult(Split.class, "list<System.String>"));
        Split split = (Split) def.getExpression();
        assertThat(split.getStringToSplit(), literalFor("First,Second,Third,Fourth"));
        assertThat(split.getSeparator(), literalFor(","));
    }

    @Test
    void splitOnMatches() {
        ExpressionDef def = defs.get("StringSplitOnMatches");
        assertThat(def, hasTypeAndResult(SplitOnMatches.class, "list<System.String>"));
        SplitOnMatches splitOnMatches = (SplitOnMatches) def.getExpression();
        assertThat(splitOnMatches.getStringToSplit(), literalFor("First,Second,Third,Fourth"));
        assertThat(splitOnMatches.getSeparatorPattern(), literalFor(","));
    }

    @Test
    void upper() {
        ExpressionDef def = defs.get("StringUpper");
        assertThat(def, hasTypeAndResult(Upper.class, "System.String"));
        Upper upper = (Upper) def.getExpression();
        assertThat(upper.getOperand(), literalFor("John"));
    }

    @Test
    void lower() {
        ExpressionDef def = defs.get("StringLower");
        assertThat(def, hasTypeAndResult(Lower.class, "System.String"));
        Lower lower = (Lower) def.getExpression();
        assertThat(lower.getOperand(), literalFor("John"));
    }

    @Test
    void positionOf() {
        ExpressionDef def = defs.get("StringPositionOf");
        assertThat(def, hasTypeAndResult(PositionOf.class, "System.Integer"));
        PositionOf positionOf = (PositionOf) def.getExpression();
        assertThat(positionOf.getPattern(), literalFor("J"));
        assertThat(positionOf.getString(), literalFor("John"));
    }

    @Test
    void lastPositionOf() {
        ExpressionDef def = defs.get("StringLastPositionOf");
        assertThat(def, hasTypeAndResult(LastPositionOf.class, "System.Integer"));
        LastPositionOf lastPositionOf = (LastPositionOf) def.getExpression();
        assertThat(lastPositionOf.getPattern(), literalFor("J"));
        assertThat(lastPositionOf.getString(), literalFor("John"));
    }

    @Test
    void substring() {
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
    void length() {
        ExpressionDef def = defs.get("StringLength");
        assertThat(def, hasTypeAndResult(Length.class, "System.Integer"));

        Length length = (Length) def.getExpression();
        assertThat(length.getOperand(), literalFor("John"));
    }

    @Test
    void startsWith() {
        ExpressionDef def = defs.get("StringStartsWith");
        assertThat(def, hasTypeAndResult(StartsWith.class, "System.Boolean"));
    }

    @Test
    void endsWith() {
        ExpressionDef def = defs.get("StringEndsWith");
        assertThat(def, hasTypeAndResult(EndsWith.class, "System.Boolean"));
    }

    @Test
    void matches() {
        ExpressionDef def = defs.get("StringMatches");
        assertThat(def, hasTypeAndResult(Matches.class, "System.Boolean"));
    }

    @Test
    void replaceMatches() {
        ExpressionDef def = defs.get("StringReplaceMatches");
        assertThat(def, hasTypeAndResult(ReplaceMatches.class, "System.String"));
    }
}
