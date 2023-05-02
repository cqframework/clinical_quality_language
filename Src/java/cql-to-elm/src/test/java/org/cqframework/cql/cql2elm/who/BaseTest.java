package org.cqframework.cql.cql2elm.who;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.CqlTranslatorOptions;
import org.cqframework.cql.cql2elm.TestUtils;
import org.hl7.elm.r1.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.cqframework.cql.cql2elm.TestUtils.visitFile;
import static org.cqframework.cql.cql2elm.matchers.Quick2DataType.quick2DataType;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class BaseTest {
   @BeforeClass
   public void Setup() {
      // Reset test utils to clear any models loaded by other tests
      TestUtils.reset();
   }

   @Test
   public void testWho() throws IOException {
      var options = CqlTranslatorOptions.defaultOptions();
      CqlTranslator translator = TestUtils.runSemanticTest("who/TestSignature.cql", 0, options);
      Library library = translator.toELM();
      Map<String, ExpressionDef> defs = new HashMap<>();

      if (library.getStatements() != null) {
         for (ExpressionDef def : library.getStatements().getDef()) {
            defs.put(def.getName(), def);
         }
      }
   }
}
