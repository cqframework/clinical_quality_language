package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.LibraryBuilder.SignatureLevel;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.FunctionDef;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.ListTypeSpecifier;
import org.hl7.elm.r1.NamedTypeSpecifier;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static java.util.stream.Collectors.toList;

public class GenericOverloadsTests {

    private static final String CQL_TEST_FILE = "SignatureTests/GenericOverloadsTests.cql";
    private Map<String, ExpressionDef> defs;

    private Library getLibrary(boolean enableResultTypes, SignatureLevel level) throws IOException {
        final CqlTranslator translator = getTranslator(enableResultTypes, level);
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


    private static CqlTranslator getTranslator(boolean enableResultTypes, SignatureLevel level) throws IOException {
        var options = new CqlCompilerOptions();
        options.getOptions().clear();
        options.setSignatureLevel(level);
        if (enableResultTypes) {
            options.getOptions().add(CqlCompilerOptions.Options.EnableResultTypes);
        }

        return TestUtils.createTranslator(CQL_TEST_FILE, options);
    }

    private List<FunctionDef> stringifies(Library library) {
        return library.getStatements().getDef()
            .stream()
            .filter(x -> "Stringify".equals(x.getName()))
            .filter(FunctionDef.class::isInstance)
            .map(FunctionDef.class::cast)
            .collect(toList());
    }

    private void validateResultTypes(FunctionDef functionDef) {
        assertEquals(2, functionDef.getOperand().size());

        var operand = functionDef.getOperand().get(0);
        assertThat(operand.getOperandTypeSpecifier(), instanceOf(ListTypeSpecifier.class));
        var listSpecifier = (ListTypeSpecifier)operand.getOperandTypeSpecifier();
        assertThat(listSpecifier.getElementType(), instanceOf(NamedTypeSpecifier.class));
        var namedSpecifier = (NamedTypeSpecifier)listSpecifier.getElementType();
        assertNotNull(namedSpecifier.getName());
        assertNotNull(namedSpecifier.getResultType());

        var second = functionDef.getOperand().get(1);
        assertThat(second.getOperandTypeSpecifier(), instanceOf(ListTypeSpecifier.class));
        listSpecifier = (ListTypeSpecifier)operand.getOperandTypeSpecifier();
        assertThat(listSpecifier.getElementType(), instanceOf(NamedTypeSpecifier.class));
        namedSpecifier = (NamedTypeSpecifier)listSpecifier.getElementType();
        assertNotNull(namedSpecifier.getName());
        assertNotNull(namedSpecifier.getResultType());
    }

    @Test
    public void TestResultTypes() throws IOException {
        Library library = getLibrary(true, SignatureLevel.Overloads);

        var stringifies = stringifies(library);
        stringifies.forEach(this::validateResultTypes);
    }

    @Test
    public void TestNoResultTypes() throws IOException {
        Library library = getLibrary(false, SignatureLevel.Overloads);

        var stringifies = stringifies(library);
        stringifies.forEach(this::validateResultTypes);
    }

    @Test
    public void TestResultTypesSignatureNone() throws IOException {
        Library library = getLibrary(true, SignatureLevel.None);

        var stringifies = stringifies(library);
        stringifies.forEach(this::validateResultTypes);
    }

    @Test
    public void TestNoResultTypesSignatureNone() throws IOException {
        Library library = getLibrary(false, SignatureLevel.None);

        var stringifies = stringifies(library);
        stringifies.forEach(this::validateResultTypes);
    }
}