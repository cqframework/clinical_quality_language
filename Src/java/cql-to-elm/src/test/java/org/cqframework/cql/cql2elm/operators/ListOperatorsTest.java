package org.cqframework.cql.cql2elm.operators;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.ModelManager;
import org.hl7.elm.r1.*;
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

public class ListOperatorsTest {

    private Map<String, ExpressionDef> defs;

    @BeforeTest
    public void setup() throws IOException {
        ModelManager modelManager = new ModelManager();
        CqlTranslator translator = CqlTranslator.fromStream(ListOperatorsTest.class.getResourceAsStream("../OperatorTests/ListOperators.cql"), modelManager, new LibraryManager(modelManager));
        Library library = translator.toELM();
        defs = new HashMap<>();
        for (ExpressionDef def: library.getStatements().getDef()) {
            defs.put(def.getName(), def);
        }
    }

    @Test
    public void testIndexOf() {
        ExpressionDef def = defs.get("ListIndexOf");
        assertThat(def, hasTypeAndResult(IndexOf.class, "System.Integer"));

        IndexOf indexOf = (IndexOf) def.getExpression();
        assertThat(indexOf.getSource(), listOfLiterals(1, 2, 3));
        assertThat(indexOf.getElement(), literalFor(2));
    }

    @Test
    public void testFirst() {
        ExpressionDef def = defs.get("ListFirst");
        assertThat(def, hasTypeAndResult(First.class, "System.Integer"));

        First first = (First) def.getExpression();
        assertThat(first.getSource(), listOfLiterals(1, 2, 3, 4, 5));
    }

    @Test
    public void testLast() {
        ExpressionDef def = defs.get("ListLast");
        assertThat(def, hasTypeAndResult(Last.class, "System.Integer"));

        Last last = (Last) def.getExpression();
        assertThat(last.getSource(), listOfLiterals(1, 2, 3));
    }

    @Test
    public void testSkip() {
        ExpressionDef def = defs.get("ListSkip");
        assertThat(def, hasTypeAndResult(Slice.class, "list<System.Integer>"));

        Slice slice = (Slice)def.getExpression();
        assertThat(slice.getSource(), listOfLiterals(1, 2, 3));
        assertThat(slice.getStartIndex(), literalFor(1));
    }

    @Test
    public void testTail() {
        ExpressionDef def = defs.get("ListTail");
        assertThat(def, hasTypeAndResult(Slice.class, "list<System.Integer>"));

        Slice slice = (Slice)def.getExpression();
        assertThat(slice.getSource(), listOfLiterals(1, 2, 3));
        assertThat(slice.getStartIndex(), literalFor(1));
    }

    @Test
    public void testTake() {
        ExpressionDef def = defs.get("ListTake");
        assertThat(def, hasTypeAndResult(Slice.class, "list<System.Integer>"));

        Slice slice = (Slice)def.getExpression();
        assertThat(slice.getSource(), listOfLiterals(1, 2, 3));
        assertThat(slice.getStartIndex(), literalFor(0));
        assertThat(slice.getEndIndex(), literalFor(1));
    }

    @Test
    public void testLength() {
        ExpressionDef def = defs.get("ListLength");
        assertThat(def, hasTypeAndResult(Length.class, "System.Integer"));

        Length length = (Length) def.getExpression();
        assertThat(length.getOperand(), listOfLiterals(1, 2, 3, 4, 5));
    }

    @Test
    public void testChoiceType() {
        ExpressionDef def = defs.get("ListUnionWithChoice");
        assertThat(def, hasTypeAndResult(Union.class, "list<choice<System.Integer,System.String>>")); // TODO: This will probably break randomly.... :)
    }
}
