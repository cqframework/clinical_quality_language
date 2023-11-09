package org.cqframework.cql.cql2elm;

import org.testng.annotations.Test;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class HidingTests {

    @Test
    public void testCaseInsensitiveWarning() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTest("HidingTests/TestHidingCaseInsensitiveWarning.cql", 0, LibraryBuilder.SignatureLevel.All);
        final List<CqlCompilerException> warnings = translator.getWarnings();
        assertThat(warnings.toString(), translator.getWarnings().size(), is(1));
        final Set<String> warningMessages = warnings.stream().map(Throwable::getMessage).collect(Collectors.toSet());
        assertThat(warningMessages, contains("Identifier hiding detected: Identifier for identifiers: [patients] resolved as an expression definition with case insensitive matching.\n"));
    }

    @Test
    public void testHiddenIdentifierFromReturn() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTestNoErrors("HidingTests/TestHiddenIdentifierFromReturn.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();

        assertThat(warnings.toString(), translator.getWarnings().size(), is(1));
        final Set<String> warningMessages = warnings.stream().map(Throwable::getMessage).collect(Collectors.toSet());
        assertThat(warningMessages, contains("Identifier hiding detected: Identifier for identifiers: [var] resolved as a let of a query with exact case matching.\n"));
    }

    @Test
    public void testHidingUnionWithSameAlias() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingUnionSameAlias.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();

        assertThat(warnings.toString(), translator.getWarnings().size(), is(0));
    }

    @Test
    public void testHidingUnionWithSameAliasEachHides() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingUnionSameAliasEachHides.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();

        assertThat(warnings.toString(), translator.getWarnings().size(), is(2));

        final List<String> distinct = translator.getWarnings().stream().map(Throwable::getMessage).distinct().collect(Collectors.toList());

        assertThat(distinct.size(), is(1));

        final String first = "Identifier hiding detected: Identifier for identifiers: [IWantToBeHidden] resolved as an alias of a query with exact case matching.\n";

        assertThat(distinct, containsInAnyOrder(first));
    }

    @Test
    public void testSoMuchNestingNormal() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingSoMuchNestingNormal.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();

        assertThat(warnings.toString(), translator.getWarnings().size(), is(0));
    }

    @Test
    public void testSoMuchNestingHidingSimple() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingSoMuchNestingHidingSimple.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();

        assertThat(warnings.toString(), translator.getWarnings().size(), is(1));
        assertThat(warnings.stream().map(Throwable::getMessage).collect(Collectors.toList()), containsInAnyOrder("Identifier hiding detected: Identifier for identifiers: [SoMuchNesting] resolved as an alias of a query with exact case matching.\n"));
    }

    @Test
    public void testSoMuchNestingHidingComplex() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingSoMuchNestingHidingComplex.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();

        final List<String> collect = warnings.stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(collect.toString(), translator.getWarnings().size(), is(2));

        final List<String> distinct = translator.getWarnings().stream().map(Throwable::getMessage).distinct().collect(Collectors.toList());

        assertThat(distinct.size(), is(2));

        final String first = "Identifier hiding detected: Identifier for identifiers: [SoMuchNesting] resolved as an alias of a query with exact case matching.\n";
        final String second = "Identifier hiding detected: Identifier for identifiers: [SoMuchNesting] resolved as a let of a query with exact case matching.\n";

        assertThat(distinct, containsInAnyOrder(first, second));
    }

    @Test
    public void testHidingLetAlias() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingLetAlias.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();

        final List<String> warningMessages = warnings.stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(warningMessages.toString(), translator.getWarnings().size(), is(1));
        assertThat(warningMessages, contains("Identifier hiding detected: Identifier for identifiers: [Alias] resolved as a let of a query with exact case matching.\n"));
    }

    @Test
    public void testHiddenIdentifierArgumentToAlias() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTestNoErrors("HidingTests/TestHiddenIdentifierArgumentToAlias.cql");

        assertThat(translator.getWarnings().size(), is(1));
        assertThat(translator.getWarnings()
                        .stream()
                        .map(Throwable::getMessage)
                        .collect(Collectors.toList()),
                    contains("Identifier hiding detected: Identifier for identifiers: [testOperand] resolved as an alias of a query with exact case matching.\n"));
    }

    @Test
    public void testReturnArgumentNotConsideredHiddenIdentifier() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingReturnArgumentNotConsideredHiddenIdentifier.cql");
        assertThat(translator.getWarnings().size(), is(0));
    }

    @Test
    public void testHidingFunctionDefinitionWithOverloads() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingFunctionDefinitionWithOverloads.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();
        final List<String> warningMessages = warnings.stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(warningMessages.toString(), warnings.size(), is(1));
        assertThat(warningMessages, contains("Identifier hiding detected: Identifier for identifiers: [IWantToBeHidden] resolved as an alias of a query with exact case matching.\n"));
    }

    @Test
    public void testHidingParameterDefinition() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingParameterDefinition.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();
        final List<String> warningMessages = warnings.stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(warningMessages.toString(), warnings.size(), is(1));
        assertThat(warningMessages, contains("Identifier hiding detected: Identifier for identifiers: [Measurement Period] resolved as an alias of a query with exact case matching.\n"));
    }

    @Test
    public void testHidingIncludeDefinition() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingIncludeDefinition.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();
        final List<String> warningMessages = warnings.stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(warningMessages.toString(), warnings.size(), is(1));
        assertThat(warningMessages, contains("Identifier hiding detected: Identifier for identifiers: [FHIRHelpers] resolved as an alias of a query with exact case matching.\n"));
    }

    @Test
    public void testHidingCommaMissingInListConstruction() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingCommaMissingInListConstruction.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();
        final List<String> warningMessages = warnings.stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(warningMessages.toString(), warnings.size(), is(2));
        final List<String> distinctWarningMessages = warningMessages.stream().distinct().collect(Collectors.toList());
        assertThat(distinctWarningMessages.toString(), distinctWarningMessages.size(), is(1));
        assertThat(distinctWarningMessages, contains("Identifier hiding detected: Identifier for identifiers: [5] resolved as an alias of a query with exact case matching.\n"));
    }
}
