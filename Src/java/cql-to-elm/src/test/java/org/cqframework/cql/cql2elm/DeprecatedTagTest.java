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

public class DeprecatedTagTest {
    @BeforeClass
    public void Setup() {
        // Reset test utils to clear any models loaded by other tests
        TestUtils.reset();
    }

    @Test
    public void testDeprecatedTagsetVisitor() throws IOException {
        CqlTranslator translator = TestUtils.runSemanticTest("DeprecatedTag.cql", 0, CqlTranslatorOptions.Options.EnableAnnotations);
        CompiledLibrary library = translator.getTranslatedLibrary();
        assertThat(library.getLibrary().getAnnotation(), notNullValue());
        //System.out.println(library.getLibrary().getAnnotation());
    }

}
