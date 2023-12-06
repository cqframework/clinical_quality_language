package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.hl7.elm.r1.*;
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

public class EscapeSequenceTests {

    private Map<String, ExpressionDef> defs;

    @BeforeTest
    public void setup() throws IOException {
        ModelManager modelManager = new ModelManager();
        LibraryManager libraryManager = new LibraryManager(modelManager);
        CqlTranslator translator = CqlTranslator.fromStream(org.cqframework.cql.cql2elm.EscapeSequenceTests.class.getResourceAsStream("EscapeSequenceTests.cql"),  libraryManager);
        assertThat(translator.getErrors().size(), is(0));
        Library library = translator.toELM();
        defs = new HashMap<>();
        for (ExpressionDef def: library.getStatements().getDef()) {
            defs.put(def.getName(), def);
        }
    }

    @Test
    public void testString() {
        ExpressionDef def = defs.get("EmptyString");
        assertThat(def, hasTypeAndResult(Literal.class, "System.String"));

        Literal literal = (Literal)def.getExpression();
        assertThat(literal.getValue(), is(""));

        def = defs.get("SingleQuoteEscapeString");
        assertThat(def, hasTypeAndResult(Literal.class, "System.String"));

        literal = (Literal)def.getExpression();
        assertThat(literal.getValue(), is("Hello 'World'"));

        def = defs.get("DoubleQuoteEscapeString");
        assertThat(def, hasTypeAndResult(Literal.class, "System.String"));

        literal = (Literal)def.getExpression();
        assertThat(literal.getValue(), is("Hello \"World\""));

        def = defs.get("BacktickEscapeString");
        assertThat(def, hasTypeAndResult(Literal.class, "System.String"));

        literal = (Literal)def.getExpression();
        assertThat(literal.getValue(), is("Hello `World`"));

        def = defs.get("StringWithDoubleQuotes");
        assertThat(def, hasTypeAndResult(Literal.class, "System.String"));

        literal = (Literal)def.getExpression();
        assertThat(literal.getValue(), is("This string has \"double-quotes\""));

        def = defs.get("StringWithBackticks");
        assertThat(def, hasTypeAndResult(Literal.class, "System.String"));

        literal = (Literal)def.getExpression();
        assertThat(literal.getValue(), is("This string has `backticks`"));

        def = defs.get("NEscapeString");
        assertThat(def, hasTypeAndResult(Literal.class, "System.String"));

        literal = (Literal)def.getExpression();
        assertThat(literal.getValue(), is("\n"));

        def = defs.get("FEscapeString");
        assertThat(def, hasTypeAndResult(Literal.class, "System.String"));

        literal = (Literal)def.getExpression();
        assertThat(literal.getValue(), is("\f"));

        def = defs.get("REscapeString");
        assertThat(def, hasTypeAndResult(Literal.class, "System.String"));

        literal = (Literal)def.getExpression();
        assertThat(literal.getValue(), is("\r"));

        def = defs.get("TEscapeString");
        assertThat(def, hasTypeAndResult(Literal.class, "System.String"));

        literal = (Literal)def.getExpression();
        assertThat(literal.getValue(), is("\t"));

        def = defs.get("SlashEscapeString");
        assertThat(def, hasTypeAndResult(Literal.class, "System.String"));

        literal = (Literal)def.getExpression();
        assertThat(literal.getValue(), is("/"));

        def = defs.get("BackslashEscapeString");
        assertThat(def, hasTypeAndResult(Literal.class, "System.String"));

        literal = (Literal)def.getExpression();
        assertThat(literal.getValue(), is("\\"));

        def = defs.get("CharacterEscapesString");
        assertThat(def, hasTypeAndResult(Literal.class, "System.String"));

        literal = (Literal)def.getExpression();
        assertThat(literal.getValue(), is("\f\n\r\t/\\"));

        def = defs.get("UnicodeEscapeString");
        assertThat(def, hasTypeAndResult(Literal.class, "System.String"));

        literal = (Literal)def.getExpression();
        assertThat(literal.getValue(), is("\u0020"));

        def = defs.get("EmbeddedEscapesString");
        assertThat(def, hasTypeAndResult(Literal.class, "System.String"));

        literal = (Literal)def.getExpression();
        assertThat(literal.getValue(), is("This is a string with 'multiple' embedded \t escapes\u0020\r\nno really, \r\n\f\t/\\lots of them"));
    }

    @Test
    public void testIdentifier() {
        ExpressionDef def = defs.get("");
        assertThat(def, hasTypeAndResult(Literal.class, "System.String"));

        Literal literal = (Literal)def.getExpression();
        assertThat(literal.getValue(), is(def.getName()));

        def = defs.get("Hello 'World'");
        assertThat(def, hasTypeAndResult(Literal.class, "System.String"));

        literal = (Literal)def.getExpression();
        assertThat(literal.getValue(), is(def.getName()));

        def = defs.get("Hello \"World\"");
        assertThat(def, hasTypeAndResult(Literal.class, "System.String"));

        literal = (Literal)def.getExpression();
        assertThat(literal.getValue(), is(def.getName()));

        def = defs.get("Hello `World`");
        assertThat(def, hasTypeAndResult(Literal.class, "System.String"));

        literal = (Literal)def.getExpression();
        assertThat(literal.getValue(), is(def.getName()));

        def = defs.get("Hello 'World'2");
        assertThat(def, hasTypeAndResult(Literal.class, "System.String"));

        literal = (Literal)def.getExpression();
        assertThat(literal.getValue(), is(def.getName()));

        def = defs.get("Hello `World`2");
        assertThat(def, hasTypeAndResult(Literal.class, "System.String"));

        literal = (Literal)def.getExpression();
        assertThat(literal.getValue(), is(def.getName()));

        def = defs.get("\n");
        assertThat(def, hasTypeAndResult(Literal.class, "System.String"));

        literal = (Literal)def.getExpression();
        assertThat(literal.getValue(), is(def.getName()));

        def = defs.get("\f");
        assertThat(def, hasTypeAndResult(Literal.class, "System.String"));

        literal = (Literal)def.getExpression();
        assertThat(literal.getValue(), is(def.getName()));

        def = defs.get("\r");
        assertThat(def, hasTypeAndResult(Literal.class, "System.String"));

        literal = (Literal)def.getExpression();
        assertThat(literal.getValue(), is(def.getName()));

        def = defs.get("\t");
        assertThat(def, hasTypeAndResult(Literal.class, "System.String"));

        literal = (Literal)def.getExpression();
        assertThat(literal.getValue(), is(def.getName()));

        def = defs.get("/");
        assertThat(def, hasTypeAndResult(Literal.class, "System.String"));

        literal = (Literal)def.getExpression();
        assertThat(literal.getValue(), is(def.getName()));

        def = defs.get("\\");
        assertThat(def, hasTypeAndResult(Literal.class, "System.String"));

        literal = (Literal)def.getExpression();
        assertThat(literal.getValue(), is(def.getName()));

        def = defs.get("\f\n\r\t/\\");
        assertThat(def, hasTypeAndResult(Literal.class, "System.String"));

        literal = (Literal)def.getExpression();
        assertThat(literal.getValue(), is(def.getName()));

        def = defs.get("\u0020");
        assertThat(def, hasTypeAndResult(Literal.class, "System.String"));

        literal = (Literal)def.getExpression();
        assertThat(literal.getValue(), is(def.getName()));

        def = defs.get("This is an identifier with \"multiple\" embedded \t escapes\u0020\r\nno really, \r\n\f\t/\\lots of them");
        assertThat(def, hasTypeAndResult(Literal.class, "System.String"));

        literal = (Literal)def.getExpression();
        assertThat(literal.getValue(), is(def.getName()));
    }
}
