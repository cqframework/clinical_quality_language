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
import org.cqframework.cql.cql2elm.CqlCompilerOptions;
import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.hl7.elm.r1.*;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class ListOperatorsTest {

    private Map<String, ExpressionDef> defs;

    @BeforeTest
    public void setup() throws IOException {
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

        Slice slice = (Slice) def.getExpression();
        assertThat(slice.getSource(), listOfLiterals(1, 2, 3));
        assertThat(slice.getStartIndex(), literalFor(1));
        assertThat(slice.getEndIndex(), instanceOf(Null.class));
    }

    @Test
    public void testTail() {
        ExpressionDef def = defs.get("ListTail");
        assertThat(def, hasTypeAndResult(Slice.class, "list<System.Integer>"));

        Slice slice = (Slice) def.getExpression();
        assertThat(slice.getSource(), listOfLiterals(1, 2, 3));
        assertThat(slice.getStartIndex(), literalFor(1));
        assertThat(slice.getEndIndex(), instanceOf(Null.class));
    }

    @Test
    public void testTake() {
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
    public void testFlatten() {
        ExpressionDef def = defs.get("ListFlatten");
        Flatten flatten = (Flatten) def.getExpression();
        assertThat(flatten.getOperand() instanceof List, is(true));

        ExpressionDef defFlatten = defs.get("Flatten Lists and Elements");
        Flatten flatten2 = (Flatten) defFlatten.getExpression();
        assertThat(flatten2.getOperand() instanceof List, is(true));
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
        assertThat(
                def,
                hasTypeAndResult(
                        Union.class,
                        "list<choice<System.Integer,System.String>>")); // TODO: This will probably break randomly....
        // :)
    }
}
