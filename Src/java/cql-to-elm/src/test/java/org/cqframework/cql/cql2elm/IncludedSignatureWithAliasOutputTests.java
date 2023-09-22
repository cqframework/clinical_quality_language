package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.*;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class IncludedSignatureWithAliasOutputTests {

    private Map<String, ExpressionDef> defs;

    private Library getLibrary(LibraryBuilder.SignatureLevel signatureLevel) throws IOException {
        File testFile = new File(URLDecoder.decode(Cql2ElmVisitorTest.class.getResource("SignatureTests/IncludedSignatureWithAliasOutputTests.cql").getFile(), "UTF-8"));
        ModelManager modelManager = new ModelManager();
        var options = new CqlCompilerOptions(CqlCompilerException.ErrorSeverity.Info, signatureLevel);
        options.getOptions().add(CqlCompilerOptions.Options.EnableAnnotations);
        LibraryManager libraryManager = new LibraryManager(modelManager, options);
        libraryManager.getLibrarySourceLoader().registerProvider(new TestLibrarySourceProvider("SignatureTests"));
        CqlTranslator translator = CqlTranslator.fromFile(testFile,  libraryManager);
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
    public void TestNone() throws IOException {
        Library library = getLibrary(LibraryBuilder.SignatureLevel.None);

        // Verify none of the outputs have signatures
        ExpressionDef def = defs.get("TestOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestOverloadOneInt");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestOverloadOneDecimal");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestOverloadTwoInts");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestOverloadTwoDecimals");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestOverloadOneIntOneDecimal");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestOverloadOneIntTwoDecimal");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));
    }

    @Test
    public void TestDiffering() throws IOException {
        Library library = getLibrary(LibraryBuilder.SignatureLevel.Differing);

        ExpressionDef def = defs.get("TestOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestOverloadOneInt");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestOverloadOneDecimal");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestOverloadTwoInts");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestOverloadTwoDecimals");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestOverloadOneIntOneDecimal");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(2));

        def = defs.get("TestOverloadOneIntTwoDecimal");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));
    }

    @Test
    public void TestOverloads() throws IOException {
        Library library = getLibrary(LibraryBuilder.SignatureLevel.Overloads);

        ExpressionDef def = defs.get("TestOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestOverloadOneInt");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(1));

        def = defs.get("TestOverloadOneDecimal");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(1));

        def = defs.get("TestOverloadTwoInts");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(2));

        def = defs.get("TestOverloadTwoDecimals");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(2));

        def = defs.get("TestOverloadOneIntOneDecimal");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(2));

        def = defs.get("TestOverloadOneIntTwoDecimal");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));
    }

    @Test
    public void TestAll() throws IOException {
        Library library = getLibrary(LibraryBuilder.SignatureLevel.All);

        ExpressionDef def = defs.get("TestOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestOverloadOneInt");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(1));

        def = defs.get("TestOverloadOneDecimal");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(1));

        def = defs.get("TestOverloadTwoInts");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(2));

        def = defs.get("TestOverloadTwoDecimals");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(2));

        def = defs.get("TestOverloadOneIntOneDecimal");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(2));

        def = defs.get("TestOverloadOneIntTwoDecimal");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(3));
    }

}
