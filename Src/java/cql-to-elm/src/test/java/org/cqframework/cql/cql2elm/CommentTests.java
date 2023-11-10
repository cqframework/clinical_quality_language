package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.model.CompiledLibrary;
import org.hl7.cql_annotations.r1.Annotation;
import org.hl7.cql_annotations.r1.CqlToElmBase;
import org.hl7.cql_annotations.r1.Narrative;
import org.hl7.cql_annotations.r1.Tag;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.FunctionDef;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import jakarta.xml.bind.JAXBElement;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertTrue;

public class CommentTests {


    @Test
    public void testComments() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("TestComments.cql", 0, CqlCompilerOptions.Options.EnableAnnotations);
        CompiledLibrary library = translator.getTranslatedLibrary();
        assertThat(library.getLibrary().getAnnotation(), notNullValue());

        Map<String, ExpressionDef> defs = new HashMap<>();

        if (library.getLibrary().getStatements() != null) {
            for (ExpressionDef def : library.getLibrary().getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }

        // Validate that boolIpp has appropriate comment value
        // Comment should be: "/* Multi-line works fine */\n// Single-line comment does not work\n"
        ExpressionDef def = defs.get("boolIpp");
        assertThat(def, notNullValue());
        assertThat(def.getAnnotation(), notNullValue());
        assertThat(def.getAnnotation().size(), is(1));
        assertThat(def.getAnnotation().get(0), instanceOf(Annotation.class));
        Annotation a = (Annotation)def.getAnnotation().get(0);
        assertThat(a.getS().getContent(), notNullValue());
        assertThat(a.getS().getContent().size(), is(2));
        assertThat(a.getS().getContent().get(0), instanceOf(JAXBElement.class));
        JAXBElement e = (JAXBElement)a.getS().getContent().get(0);
        assertThat(e, notNullValue());
        assertThat(e.getValue(), instanceOf(Narrative.class));
        Narrative n = (Narrative)e.getValue();
        assertThat(n.getContent(), notNullValue());
        assertThat(n.getContent().size(), is(4));
        assertThat(n.getContent().get(0), instanceOf(String.class));
        String s = (String)n.getContent().get(0);
        assertThat(s, is("/* Multi-line works fine */\n// Single-line comment does not work\n"));


        // Validate that singleLineCommentTest has appropriate comment value
        // Comment should be: "// Unmixed single-line comment works\n"
        def = defs.get("singleLineCommentTest");
        assertThat(def, notNullValue());
        assertThat(def.getAnnotation(), notNullValue());
        assertThat(def.getAnnotation().size(), is(1));
        assertThat(def.getAnnotation().get(0), instanceOf(Annotation.class));
        a = (Annotation)def.getAnnotation().get(0);
        assertThat(a.getS().getContent(), notNullValue());
        assertThat(a.getS().getContent().size(), is(2));
        assertThat(a.getS().getContent().get(0), instanceOf(JAXBElement.class));
        e = (JAXBElement)a.getS().getContent().get(0);
        assertThat(e, notNullValue());
        assertThat(e.getValue(), instanceOf(Narrative.class));
        n = (Narrative)e.getValue();
        assertThat(n.getContent(), notNullValue());
        assertThat(n.getContent().size(), is(4));
        assertThat(n.getContent().get(0), instanceOf(String.class));
        s = (String)n.getContent().get(0);
        assertThat(s, is("// Unmixed single-line comment works\n"));
    }

    @Test
    public void testTags() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("TestTags.cql", 0);
        CompiledLibrary library = translator.getTranslatedLibrary();
        assertThat(library.getLibrary().getAnnotation(), notNullValue());
        Annotation a = null;
        for (Object o : library.getLibrary().getAnnotation()) {
            if (o instanceof Annotation) {
                a = (Annotation)o;
            }
        }
        assertThat(a, notNullValue());
        assertThat(a.getT(), notNullValue());
        assertThat(a.getT().size(), equalTo(3));
        for (int i = 0; i < a.getT().size(); i++) {
            Tag t = a.getT().get(i);
            switch (i) {
                case 0:
                    assertThat(t.getName(), equalTo("author"));
                    assertThat(t.getValue(), equalTo("Frederic Chopin"));
                    break;
                case 1:
                    assertThat(t.getName(), equalTo("description"));
                    assertThat(t.getValue(), equalTo("Test tags"));
                    break;
                case 2:
                    assertThat(t.getName(), equalTo("allowFluent"));
                    assertThat(t.getValue(), equalTo("true"));
                    break;
            }
        }

        ExpressionDef d = library.resolveExpressionRef("TestExpression");
        assertThat(d.getAnnotation(), notNullValue());
        for (Object o : d.getAnnotation()) {
            if (o instanceof Annotation) {
                a = (Annotation)o;
            }
        }
        assertThat(a, notNullValue());
        assertThat(a.getT(), notNullValue());
        assertThat(a.getT().size(), equalTo(2));
        for (int i = 0; i < a.getT().size(); i++) {
            Tag t = a.getT().get(i);
            switch (i) {
                case 0:
                    assertThat(t.getName(), equalTo("author"));
                    assertThat(t.getValue(), equalTo("Frederic Chopin"));
                    break;
                case 1:
                    assertThat(t.getName(), equalTo("description"));
                    assertThat(t.getValue(), equalTo("Test tags"));
                    break;
            }
        }

        for (ExpressionDef e : library.getLibrary().getStatements().getDef()) {
            if (e instanceof FunctionDef) {
                d = e;
                break;
            }
        }
        assertThat(d.getAnnotation(), notNullValue());
        for (Object o : d.getAnnotation()) {
            if (o instanceof Annotation) {
                a = (Annotation)o;
            }
        }
        assertThat(a, notNullValue());
        assertThat(a.getT(), notNullValue());
        assertThat(a.getT().size(), equalTo(2));
        for (int i = 0; i < a.getT().size(); i++) {
            Tag t = a.getT().get(i);
            switch (i) {
                case 0:
                    assertThat(t.getName(), equalTo("author"));
                    assertThat(t.getValue(), equalTo("Frederic Chopin"));
                    break;
                case 1:
                    assertThat(t.getName(), equalTo("description"));
                    assertThat(t.getValue(), equalTo("Test tags"));
                    break;
            }
        }

        d = library.resolveExpressionRef("TestMultiline");
        assertThat(d.getAnnotation(), notNullValue());
        for (Object o : d.getAnnotation()) {
            if (o instanceof Annotation) {
                a = (Annotation)o;
            }
        }
        assertThat(a, notNullValue());
        assertThat(a.getT(), notNullValue());
        assertThat(a.getT().size(), equalTo(3));
        for (int i = 0; i < a.getT().size(); i++) {
            Tag t = a.getT().get(i);
            switch (i) {
                case 0:
                    assertThat(t.getName(), equalTo("author"));
                    assertThat(t.getValue(), equalTo("Frederic Chopin"));
                    break;
                case 1:
                    assertThat(t.getName(), equalTo("description"));
                    assertThat(t.getValue(), equalTo("This is a multi-line description that\n spans multiple lines."));
                    break;
                case 2:
                    assertThat(t.getName(), equalTo("following"));
                    assertThat(t.getValue(), equalTo("true"));
                    break;
            }
        }


        d = library.resolveExpressionRef("TestMultiTagInline");
        assertThat(d.getAnnotation(), notNullValue());
        for (Object o : d.getAnnotation()) {
            if (o instanceof Annotation) {
                a = (Annotation)o;
            }
        }
        assertThat(a, notNullValue());
        assertThat(a.getT(), notNullValue());
        assertThat(a.getT().size(), equalTo(2));
        for (int i = 0; i < a.getT().size(); i++) {
            Tag t = a.getT().get(i);
            switch (i) {
                case 0:
                    assertThat(t.getName(), equalTo("test"));
                    assertThat(t.getValue(), nullValue());
                    break;
                case 1:
                    assertThat(t.getName(), equalTo("pertinence"));
                    assertThat(t.getValue(), equalTo("strongly-positive"));
                    break;
            }
        }


        d = library.resolveExpressionRef("TestDateMultiTag");
        assertThat(d.getAnnotation(), notNullValue());
        for (Object o : d.getAnnotation()) {
            if (o instanceof Annotation) {
                a = (Annotation)o;
            }
        }
        assertThat(a, notNullValue());
        assertThat(a.getT(), notNullValue());
        assertThat(a.getT().size(), equalTo(4));
        for (int i = 0; i < a.getT().size(); i++) {
            Tag t = a.getT().get(i);
            switch (i) {
                case 0:
                    assertThat(t.getName(), equalTo("test"));
                    assertThat(t.getValue(), equalTo("@1980-12-01"));
                    break;
                case 1:
                    assertThat(t.getName(), equalTo("val"));
                    assertThat(t.getValue(), equalTo("val1"));
                    break;
                case 2:
                    assertThat(t.getName(), equalTo("asof"));
                    assertThat(t.getValue(), equalTo("@2020-10-01"));
                    break;
                case 3:
                    assertThat(t.getName(), equalTo("parameter"));
                    assertThat(t.getValue(), equalTo("abcd"));
                    break;
            }
        }

        d = library.resolveExpressionRef("TestDateIntervalParameter");
        assertThat(d.getAnnotation(), notNullValue());
        for (Object o : d.getAnnotation()) {
            if (o instanceof Annotation) {
                a = (Annotation)o;
            }
        }
        assertThat(a, notNullValue());
        assertThat(a.getT(), notNullValue());
        assertThat(a.getT().size(), equalTo(4));
        for (int i = 0; i < a.getT().size(); i++) {
            Tag t = a.getT().get(i);
            switch (i) {
                case 0:
                    assertThat(t.getName(), equalTo("test"));
                    assertThat(t.getValue(), equalTo("@1980-12-01"));
                    break;
                case 1:
                    assertThat(t.getName(), equalTo("val"));
                    assertThat(t.getValue(), equalTo("val1"));
                    break;
                case 2:
                    assertThat(t.getName(), equalTo("asof"));
                    assertThat(t.getValue(), equalTo("@2020-10-01"));
                    break;
                case 3:
                    assertThat(t.getName(), equalTo("parameter"));
                    assertThat(t.getValue(), equalTo("\"Measurement Interval\" [@2019,@2020]"));
                    break;
            }
        }

        d = library.resolveExpressionRef("TestMultilineValue");
        assertThat(d.getAnnotation(), notNullValue());
        for (Object o : d.getAnnotation()) {
            if (o instanceof Annotation) {
                a = (Annotation)o;
            }
        }
        assertThat(a, notNullValue());
        assertThat(a.getT(), notNullValue());
        assertThat(a.getT().size(), equalTo(1));
        for (int i = 0; i < a.getT().size(); i++) {
            Tag t = a.getT().get(i);
            switch (i) {
                case 0:
                    assertThat(t.getName(), equalTo("test"));
                    assertThat(t.getValue(), equalTo("this is a\n" +
                            "multi-line tag value"));
                    break;
            }
        }

        d = library.resolveExpressionRef("TestParameterAtFirstLine");
        assertThat(d.getAnnotation(), notNullValue());
        for (Object o : d.getAnnotation()) {
            if (o instanceof Annotation) {
                a = (Annotation)o;
            }
        }
        assertThat(a, notNullValue());
        assertThat(a.getT(), notNullValue());
        assertThat(a.getT().size(), equalTo(3));
        for (int i = 0; i < a.getT().size(); i++) {
            Tag t = a.getT().get(i);
            switch (i) {
                case 0:
                    assertThat(t.getName(), equalTo("parameter"));
                    assertThat(t.getValue(), equalTo("\"abcd\" [1,10]"));
                    break;
                case 1:
                    assertThat(t.getName(), equalTo("test"));
                    assertThat(t.getValue(), equalTo("this is a\n" +
                            "multi-line tag value"));
                    break;
                case 2:
                    assertThat(t.getName(), equalTo("pertinence"));
                    assertThat(t.getValue(), equalTo("weakly-negative"));
                    break;
            }
        }

        d = library.resolveExpressionRef("AnotherTestCase");
        assertThat(d.getAnnotation(), notNullValue());
        for (Object o : d.getAnnotation()) {
            if (o instanceof Annotation) {
                a = (Annotation)o;
            }
        }
        assertThat(a, notNullValue());
        assertThat(a.getT(), notNullValue());
        assertThat(a.getT().size(), equalTo(2));
        for (int i = 0; i < a.getT().size(); i++) {
            Tag t = a.getT().get(i);
            switch (i) {
                case 0:
                    assertThat(t.getName(), equalTo("tagname"));
                    assertThat(t.getValue(), equalTo("tag value"));
                    break;
                case 1:
                    assertThat(t.getName(), equalTo("tagname2"));
                    assertThat(t.getValue(), equalTo("tag value2 this is\n" +
                            "a long tag value"));
                    break;
            }
        }


        ExpressionDef dInvalid = library.resolveExpressionRef("TestInvalid");
        assertThat(dInvalid.getAnnotation(), notNullValue());
        Annotation aInvalid = null;
        for (Object o : dInvalid.getAnnotation()) {
            if (o instanceof Annotation) {
                aInvalid = (Annotation)o;
            }
        }
        assertThat(aInvalid, nullValue());


    }


}
