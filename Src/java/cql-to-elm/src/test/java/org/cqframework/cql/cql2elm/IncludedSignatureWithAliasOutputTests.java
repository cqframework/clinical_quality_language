package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.*;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class IncludedSignatureWithAliasOutputTests {

    private static final String CQL_TEST_FILE = "SignatureTests/IncludedSignatureWithAliasOutputTests.cql";
    private static final String LIBRARY_SOURCE_PROVIDER = "SignatureTests";
    private Map<String, ExpressionDef> defs;

    private Library getLibrary(LibraryBuilder.SignatureLevel signatureLevel) throws IOException {
        final CqlTranslator translator = getTranslator(signatureLevel);
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

    private static CqlTranslator getTranslator(LibraryBuilder.SignatureLevel signatureLevel) throws IOException {
        return TestUtils.getTranslator(CQL_TEST_FILE, LIBRARY_SOURCE_PROVIDER, signatureLevel);
    }

    @Test
    public void TestNone() throws IOException {
        final CqlTranslator translator = getTranslator(LibraryBuilder.SignatureLevel.None);
        assertThat(translator.getWarnings().size(), greaterThan(1));
        assertThat(translator.getWarnings().get(0).getMessage(), equalTo("The function SignatureOutputTests.MultipleOverloadTest has multiple overloads and due to the SignatureLevel setting (None), the overload signature is not being included in the output. This may result in ambiguous function resolution at runtime, consider setting the SignatureLevel to Overloads or All to ensure that the output includes sufficient information to support correct overload selection at runtime."));
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
