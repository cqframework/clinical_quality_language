package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.model.TranslatedLibrary;
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
        CqlTranslator translator = TestUtils.runSemanticTest("TestComments.cql", 0, CqlTranslator.Options.EnableAnnotations);
        TranslatedLibrary library = translator.getTranslatedLibrary();
        assertThat(library.getLibrary().getAnnotation(), notNullValue());
    }

    @Test
    public void testTags() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("TestTags.cql", 0);
        TranslatedLibrary library = translator.getTranslatedLibrary();
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
                    assertThat(t.getValue(), equalTo("This is a multi-line description that spans multiple lines."));
                    break;
                case 2:
                    assertThat(t.getName(), equalTo("following"));
                    assertThat(t.getValue(), equalTo("true"));
                    break;
            }
        }

    }
}
