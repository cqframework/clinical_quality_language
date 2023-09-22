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

/**
 * Created by Bryn on 4/12/2018.
 */
public class SignatureOutputTests {

    private Map<String, ExpressionDef> defs;

    private Library getLibrary(LibraryBuilder.SignatureLevel signatureLevel) throws IOException {
        File testFile = new File(URLDecoder.decode(Cql2ElmVisitorTest.class.getResource("SignatureTests/SignatureOutputTests.cql").getFile(), "UTF-8"));
        ModelManager modelManager = new ModelManager();
        var compilerOptions = new CqlCompilerOptions(CqlCompilerException.ErrorSeverity.Info, signatureLevel, CqlCompilerOptions.Options.EnableAnnotations);
        CqlTranslator translator = CqlTranslator.fromFile(testFile, new LibraryManager(modelManager, compilerOptions));
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
        ExpressionDef def = defs.get("TestAdd");
        assertThat(((OperatorExpression)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestDateAdd");
        assertThat(((OperatorExpression)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestDateTime");
        assertThat(((OperatorExpression)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestAvg");
        assertThat(((AggregateExpression)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestDivide");
        assertThat(((OperatorExpression)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestIntegerOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestDecimalOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestMultipleOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestIntegerMultipleOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestDecimalMultipleOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestIntegerAndDecimalMultipleOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));
    }

    @Test
    public void TestDiffering() throws IOException {
        Library library = getLibrary(LibraryBuilder.SignatureLevel.Differing);
        // TestAvg->Avg->signature(1)
        // TestDivide->Divide->signature(2)
        // TestIntegerOverload->OverloadTest->signature(1)
        ExpressionDef def = defs.get("TestAdd");
        assertThat(((OperatorExpression)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestDateAdd");
        assertThat(((OperatorExpression)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestDateTime");
        assertThat(((OperatorExpression)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestAvg");
        assertThat(((AggregateExpression)def.getExpression()).getSignature().size(), is(1));

        def = defs.get("TestDivide");
        assertThat(((OperatorExpression)def.getExpression()).getSignature().size(), is(2));

        def = defs.get("TestIntegerOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(1));

        def = defs.get("TestDecimalOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestMultipleOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestIntegerMultipleOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestDecimalMultipleOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestIntegerAndDecimalMultipleOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));
    }

    @Test
    public void TestOverloads() throws IOException {
        Library library = getLibrary(LibraryBuilder.SignatureLevel.Overloads);
        // TestAdd->operand->signature(2)
        // TestDateAdd->operand->signature(2)
        // TestAvg->Avg->signature(1)
        // TestDivide->Divide->signature(2)
        // TestIntegerMultipleOverload->MultipleOverloadTest->signature(1)
        // TestDecimalMultipleOverload->MultipleOverloadTest->signature(2)
        ExpressionDef def = defs.get("TestAdd");
        assertThat(((OperatorExpression)def.getExpression()).getSignature().size(), is(2));

        def = defs.get("TestDateAdd");
        assertThat(((OperatorExpression)def.getExpression()).getSignature().size(), is(2));

        def = defs.get("TestDateTime");
        assertThat(((OperatorExpression)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestAvg");
        assertThat(((AggregateExpression)def.getExpression()).getSignature().size(), is(1));

        def = defs.get("TestDivide");
        assertThat(((OperatorExpression)def.getExpression()).getSignature().size(), is(2));

        def = defs.get("TestIntegerOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestDecimalOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestMultipleOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestIntegerMultipleOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(1));

        def = defs.get("TestDecimalMultipleOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(2));

        def = defs.get("TestIntegerAndDecimalMultipleOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));
    }

    @Test
    public void TestAll() throws IOException {
        Library library = getLibrary(LibraryBuilder.SignatureLevel.All);
        // TestAdd->operand->signature(2)
        // TestDateAdd->operand->signature(2)
        // TestDateTime->DateTime->signature(3)
        // TestAvg->Avg->signature(1)
        // TestDivide->Divide->signature(2)
        // TestIntegerOverload->OverloadTest->signature(1)
        // TestDecimalOverload->OverloadTest->signature(1)
        // TestIntegerMultipleOverload->MultipleOverloadTest->signature(1)
        // TestDecimalMultipleOverload->MultipleOverloadTest->signature(2)
        ExpressionDef def = defs.get("TestAdd");
        assertThat(((OperatorExpression)def.getExpression()).getSignature().size(), is(2));

        def = defs.get("TestDateAdd");
        assertThat(((OperatorExpression)def.getExpression()).getSignature().size(), is(2));

        def = defs.get("TestDateTime");
        assertThat(((OperatorExpression)def.getExpression()).getSignature().size(), is(3));

        def = defs.get("TestAvg");
        assertThat(((AggregateExpression)def.getExpression()).getSignature().size(), is(1));

        def = defs.get("TestDivide");
        assertThat(((OperatorExpression)def.getExpression()).getSignature().size(), is(2));

        def = defs.get("TestIntegerOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(1));

        def = defs.get("TestDecimalOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(1));

        def = defs.get("TestMultipleOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(0));

        def = defs.get("TestIntegerMultipleOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(1));

        def = defs.get("TestDecimalMultipleOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(2));

        def = defs.get("TestIntegerAndDecimalMultipleOverload");
        assertThat(((FunctionRef)def.getExpression()).getSignature().size(), is(3));
    }

}
