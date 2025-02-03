package org.cqframework.cql.cql2elm.operators;

import static org.cqframework.cql.cql2elm.matchers.HasTypeAndResult.hasTypeAndResult;
import static org.cqframework.cql.cql2elm.matchers.ListOfLiterals.listOfLiterals;
import static org.cqframework.cql.cql2elm.matchers.LiteralFor.literalFor;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.cqframework.cql.cql2elm.*;
import org.hl7.elm.r1.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ListOperatorsTest {

    private static Map<String, ExpressionDef> defs;

    @BeforeAll
    static void setup() throws IOException {
        ModelManager modelManager = new ModelManager();
        CqlTranslator translator = CqlTranslator.fromStream(
                ListOperatorsTest.class.getResourceAsStream("../OperatorTests/ListOperators.cql"),
                new LibraryManager(modelManager, new CqlCompilerOptions()));
        assertThat(translator.getErrors().size(), is(0));
        Library library = translator.toELM();
        defs = new HashMap<>();
        for (ExpressionDef def : library.getStatements().getDef()) {
            defs.put(def.getName(), def);
        }
    }

    @Test
    void indexOf() {
        ExpressionDef def = defs.get("ListIndexOf");
        assertThat(def, hasTypeAndResult(IndexOf.class, "System.Integer"));

        IndexOf indexOf = (IndexOf) def.getExpression();
        assertThat(indexOf.getSource(), listOfLiterals(1, 2, 3));
        assertThat(indexOf.getElement(), literalFor(2));
    }

    @Test
    void first() {
        ExpressionDef def = defs.get("ListFirst");
        assertThat(def, hasTypeAndResult(First.class, "System.Integer"));

        First first = (First) def.getExpression();
        assertThat(first.getSource(), listOfLiterals(1, 2, 3, 4, 5));
    }

    @Test
    void last() {
        ExpressionDef def = defs.get("ListLast");
        assertThat(def, hasTypeAndResult(Last.class, "System.Integer"));

        Last last = (Last) def.getExpression();
        assertThat(last.getSource(), listOfLiterals(1, 2, 3));
    }

    @Test
    void skip() {
        ExpressionDef def = defs.get("ListSkip");
        assertThat(def, hasTypeAndResult(Slice.class, "list<System.Integer>"));

        Slice slice = (Slice) def.getExpression();
        assertThat(slice.getSource(), listOfLiterals(1, 2, 3));
        assertThat(slice.getStartIndex(), literalFor(1));
        assertThat(slice.getEndIndex(), instanceOf(Null.class));
    }

    @Test
    void tail() {
        ExpressionDef def = defs.get("ListTail");
        assertThat(def, hasTypeAndResult(Slice.class, "list<System.Integer>"));

        Slice slice = (Slice) def.getExpression();
        assertThat(slice.getSource(), listOfLiterals(1, 2, 3));
        assertThat(slice.getStartIndex(), literalFor(1));
        assertThat(slice.getEndIndex(), instanceOf(Null.class));
    }

    @Test
    void take() {
        ExpressionDef def = defs.get("ListTake");
        assertThat(def, hasTypeAndResult(Slice.class, "list<System.Integer>"));

        Slice slice = (Slice) def.getExpression();
        assertThat(slice.getSource(), listOfLiterals(1, 2, 3));
        assertThat(slice.getStartIndex(), literalFor(0));
        Coalesce coalesce = (Coalesce) slice.getEndIndex();
        assertThat(coalesce.getOperand().size(), is(2));
        assertThat(coalesce.getOperand().get(0), literalFor(1));
        assertThat(coalesce.getOperand().get(1), literalFor(0));
    }

    @Test
    void flatten() {
        ExpressionDef def = defs.get("ListFlatten");
        Flatten flatten = (Flatten) def.getExpression();
        assertThat(flatten.getOperand() instanceof List, is(true));

        ExpressionDef defFlatten = defs.get("Flatten Lists and Elements");
        Flatten flatten2 = (Flatten) defFlatten.getExpression();
        assertThat(flatten2.getOperand() instanceof List, is(true));
    }

    @Test
    void length() {
        ExpressionDef def = defs.get("ListLength");
        assertThat(def, hasTypeAndResult(Length.class, "System.Integer"));

        Length length = (Length) def.getExpression();
        assertThat(length.getOperand(), listOfLiterals(1, 2, 3, 4, 5));
    }

    @Test
    void choiceType() {
        ExpressionDef def = defs.get("ListUnionWithChoice");
        assertThat(
                def,
                hasTypeAndResult(
                        Union.class,
                        "list<choice<System.Integer,System.String>>")); // TODO: This will probably break randomly....
        // :)
    }
}
