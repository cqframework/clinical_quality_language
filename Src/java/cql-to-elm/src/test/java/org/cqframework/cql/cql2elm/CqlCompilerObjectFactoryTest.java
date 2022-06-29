package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.Add;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.ObjectFactory;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.testng.AssertJUnit.assertEquals;

public class CqlCompilerObjectFactoryTest {

    class ModifiedAdd extends Add {
        @Override
        public String toString() {
            return "You Got Me";
        }
    }

    class MyNewFactory extends ObjectFactory {
        @Override
        public Add createAdd() {
            return new ModifiedAdd();
        }
    }

    @Test
    public void testObjectFactoryOverride() throws IOException {
        // Overrides the default factory.
        MyNewFactory nOf = new MyNewFactory();

        // Loads Library to test.
        ModelManager modelManager = new ModelManager();
        LibraryManager libraryManager = new LibraryManager(modelManager, new MyNewFactory(), new org.hl7.cql_annotations.r1.ObjectFactory());
        CqlTranslator translator = CqlTranslator.fromStream(
                CqlCompilerObjectFactoryTest.class.getResourceAsStream("SimpleMath.cql"),
                modelManager, libraryManager);
        assertThat(translator.getErrors().size(), is(0));

        Library library = translator.toELM();
        ExpressionDef add = library.getStatements().getDef().stream()
                .filter(def -> def.getName().equals("IntegerAdd"))
                .findFirst().get();
        assertEquals("You Got Me", add.getExpression().toString());
    }

}
