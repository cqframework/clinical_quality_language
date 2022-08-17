package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.model.CompiledLibrary;
import org.hl7.cql_annotations.r1.Annotation;
import org.hl7.cql_annotations.r1.Tag;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.FunctionDef;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class CommentTests {
    @BeforeClass
    public void Setup() {
        // Reset test utils to clear any models loaded by other tests
        TestUtils.reset();
    }

    @Test
    public void testComments() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("TestComments.cql", 0, CqlTranslatorOptions.Options.EnableAnnotations);
        CompiledLibrary library = translator.getTranslatedLibrary();
        assertThat(library.getLibrary().getAnnotation(), notNullValue());
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
        assertThat(a.getT().size(), equalTo(2));
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
