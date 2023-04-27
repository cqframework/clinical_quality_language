package org.cqframework.cql.cql2elm.quick;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.cqframework.cql.cql2elm.CqlCompilerException;
import org.cqframework.cql.cql2elm.CqlTranslator;
import org.cqframework.cql.cql2elm.CqlTranslatorOptions;
import org.cqframework.cql.cql2elm.LibraryBuilder.SignatureLevel;
import org.cqframework.cql.cql2elm.LibraryManager;
import org.cqframework.cql.cql2elm.ModelManager;
import org.hl7.elm.r1.As;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.FunctionRef;
import org.hl7.elm.r1.Less;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.Query;
import org.testng.annotations.Test;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertNotNull;

public class SignatureTest {

    private Map<String, ExpressionDef> defs;

    private Library getLibrary() throws IOException {
        File testFile = new File(URLDecoder.decode(SignatureTest.class.getResource("SignatureTest.cql").getFile(), "UTF-8"));
        ModelManager modelManager = new ModelManager();

        CqlTranslatorOptions options = CqlTranslatorOptions.defaultOptions();
        options.setEnableCqlOnly(true);
        options.setSignatureLevel(SignatureLevel.All);
        var libraryManager =  new LibraryManager(modelManager);
        libraryManager.disableCache();
        CqlTranslator translator = CqlTranslator.fromFile(testFile, modelManager,libraryManager, null, options);
        for (CqlCompilerException error : translator.getErrors()) {
            System.err.println(String.format("(%d,%d): %s",
                    error.getLocator().getStartLine(), error.getLocator().getStartChar(), error.getMessage()));
        }
        assertThat(translator.getErrors().size(), is(0));
        defs = new HashMap<>();
        Library library = translator.toELM();
        if (library.getStatements() != null) {
            for (ExpressionDef def : library.getStatements().getDef()) {
                defs.put(def.getName(), def);
            }
        }
        return library;
    }

    @Test
    public void TestFhirHelpersHasSignature() throws IOException {
        Library library = getLibrary();

        ExpressionDef def = library.getStatements().getDef().get(0);
        var where = (Less)((Query)def.getExpression()).getWhere();
        assertNotNull(where.getSignature());
        assertThat(where.getSignature().size(), is(2));

        var left = (As)where.getOperand().get(0);

        var toValue = (FunctionRef)left.getOperand();
        assertNotNull(toValue.getSignature());
        assertThat(toValue.getSignature().size(), is(1));
    }
}
