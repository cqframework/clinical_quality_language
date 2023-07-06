package org.cqframework.cql.cql2elm.who;

import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.CqlCompilerOptions;
import org.cqframework.cql.cql2elm.TestUtils;
import org.hl7.elm.r1.*;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BaseTest {
   @Test
   public void testWho() throws IOException {
      var options = CqlCompilerOptions.defaultOptions();
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
