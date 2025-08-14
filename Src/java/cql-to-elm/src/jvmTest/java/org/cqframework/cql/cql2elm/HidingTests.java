package org.cqframework.cql.cql2elm;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class HidingTests {

    @Test
    void caseInsensitiveWarning() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTest(
                "HidingTests/TestHidingCaseInsensitiveWarning.cql", 0, LibraryBuilder.SignatureLevel.All);
        final List<CqlCompilerException> warnings = translator.getWarnings();
        assertLocatorsExist(warnings);
        assertThat(warnings.toString(), warnings.size(), is(0));
    }

    @Test
    void hiddenIdentifierFromReturn() throws IOException {
        final CqlTranslator translator =
                TestUtils.runSemanticTestNoErrors("HidingTests/TestHiddenIdentifierFromReturn.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();
        assertLocatorsExist(warnings);
        assertThat(warnings.toString(), warnings.size(), is(1));
        final Set<String> warningMessages =
                warnings.stream().map(Throwable::getMessage).collect(Collectors.toSet());
        assertThat(
                warningMessages,
                contains(String.format("A let identifier var is hiding another identifier of the same name.")));
    }

    @Test
    void hidingUnionWithSameAlias() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingUnionSameAlias.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();
        assertLocatorsExist(warnings);
        final List<String> warningMessages =
                warnings.stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(warningMessages.toString(), warnings.size(), is(2));

        final List<String> distinct =
                warnings.stream().map(Throwable::getMessage).distinct().collect(Collectors.toList());

        assertThat(distinct.size(), is(2));

        final String first = String.format(
                "String literal 'X' matches the identifier X. Consider whether the identifier was intended instead.");
        final String second = String.format(
                "String literal 'Y' matches the identifier Y. Consider whether the identifier was intended instead.");

        assertThat(distinct.toString(), distinct, containsInAnyOrder(first, second));
    }

    @Test
    void hidingUnionWithSameAliasEachHides() throws IOException {
        final CqlTranslator translator =
                TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingUnionSameAliasEachHides.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();
        assertLocatorsExist(warnings);
        final List<String> warningMessages =
                warnings.stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(warningMessages.toString(), warnings.size(), is(4));

        final List<String> distinct =
                warnings.stream().map(Throwable::getMessage).distinct().collect(Collectors.toList());

        assertThat(distinct.size(), is(3));

        final String first = String.format(
                "String literal 'X' matches the identifier X. Consider whether the identifier was intended instead.");
        final String second = String.format(
                "String literal 'Y' matches the identifier Y. Consider whether the identifier was intended instead.");
        final String third =
                String.format("An alias identifier IWantToBeHidden is hiding another identifier of the same name.");

        assertThat(distinct.toString(), distinct, containsInAnyOrder(first, second, third));
    }

    @Test
    void soMuchNestingNormal() throws IOException {
        final CqlTranslator translator =
                TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingSoMuchNestingNormal.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();
        assertLocatorsExist(warnings);
        assertThat(warnings.toString(), warnings.size(), is(0));
    }

    @Test
    void soMuchNestingHidingSimple() throws IOException {
        final CqlTranslator translator =
                TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingSoMuchNestingHidingSimple.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();
        assertLocatorsExist(warnings);
        assertThat(warnings.toString(), warnings.size(), is(1));
        assertThat(
                warnings.stream().map(Throwable::getMessage).collect(Collectors.toList()),
                containsInAnyOrder(String.format(
                        "An alias identifier SoMuchNesting is hiding another identifier of the same name.")));
    }

    @Test
    void soMuchNestingHidingComplex() throws IOException {
        final CqlTranslator translator =
                TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingSoMuchNestingHidingComplex.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();
        assertLocatorsExist(warnings);
        final List<String> collect =
                warnings.stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(collect.toString(), warnings.size(), is(2));

        final List<String> distinct =
                warnings.stream().map(Throwable::getMessage).distinct().collect(Collectors.toList());

        assertThat(distinct.size(), is(2));

        final String first =
                String.format("An alias identifier SoMuchNesting is hiding another identifier of the same name.");
        final String second =
                String.format("A let identifier SoMuchNesting is hiding another identifier of the same name.");

        assertThat(distinct, containsInAnyOrder(first, second));
    }

    @Test
    void hidingLetAlias() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingLetAlias.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();
        assertLocatorsExist(warnings);
        final List<String> warningMessages =
                warnings.stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(warningMessages.toString(), warnings.size(), is(1));
        assertThat(
                warnings.stream().map(Throwable::getMessage).collect(Collectors.toList()),
                containsInAnyOrder(
                        String.format("A let identifier Alias is hiding another identifier of the same name.")));
    }

    @Test
    void hiddenIdentifierArgumentToAlias() throws IOException {
        final CqlTranslator translator =
                TestUtils.runSemanticTestNoErrors("HidingTests/TestHiddenIdentifierArgumentToAlias.cql");

        assertLocatorsExist(translator.getWarnings());
        assertThat(translator.getWarnings().size(), is(1));
        assertThat(
                translator.getWarnings().stream().map(Throwable::getMessage).collect(Collectors.toList()),
                contains(String.format(
                        "An alias identifier testOperand is hiding another identifier of the same name.")));
    }

    @Test
    void returnArgumentNotConsideredHiddenIdentifier() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTestNoErrors(
                "HidingTests/TestHidingReturnArgumentNotConsideredHiddenIdentifier.cql");
        assertLocatorsExist(translator.getWarnings());
        assertThat(translator.getWarnings().size(), is(0));
    }

    @Test
    void hidingFunctionDefinitionWithOverloads() throws IOException {
        final CqlTranslator translator =
                TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingFunctionDefinitionWithOverloads.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();
        assertLocatorsExist(translator.getWarnings());
        final List<String> warningMessages =
                warnings.stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(warningMessages.toString(), warnings.size(), is(1));
        assertThat(
                warningMessages,
                contains(String.format(
                        "An alias identifier IWantToBeHidden is hiding another identifier of the same name.")));
    }

    @Test
    void hidingParameterDefinition() throws IOException {
        final CqlTranslator translator =
                TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingParameterDefinition.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();
        assertLocatorsExist(warnings);
        final List<String> warningMessages =
                warnings.stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(warningMessages.toString(), warnings.size(), is(1));
        assertThat(
                warningMessages,
                contains(String.format(
                        "An alias identifier Measurement Period is hiding another identifier of the same name.")));
    }

    @Test
    void hidingIncludeDefinition() throws IOException {
        final CqlTranslator translator =
                TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingIncludeDefinition.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();
        assertLocatorsExist(warnings);
        final List<String> warningMessages =
                warnings.stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(warningMessages.toString(), warnings.size(), is(1));
        assertThat(
                warningMessages,
                contains(String.format(
                        "An alias identifier FHIRHelpers is hiding another identifier of the same name.")));
    }

    private void assertLocatorsExist(List<CqlCompilerException> exceptions) {
        for (var exception : exceptions) {
            assertNotNull(exception.getLocator());
        }
    }

    @Test
    void hidingCommaMissingInListConstruction() throws IOException {
        final CqlTranslator translator =
                TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingCommaMissingInListConstruction.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();
        assertLocatorsExist(warnings);
        final List<String> warningMessages =
                warnings.stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(warningMessages.toString(), warnings.size(), is(2));
        final List<String> distinctWarningMessages =
                warningMessages.stream().distinct().collect(Collectors.toList());
        assertThat(distinctWarningMessages.toString(), distinctWarningMessages.size(), is(1));
        assertThat(
                distinctWarningMessages,
                contains(String.format("An alias identifier 5 is hiding another identifier of the same name.")));
    }

    @Test
    void hidingStringLiteral() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingStringLiteral.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();
        assertLocatorsExist(warnings);
        final List<String> warningMessages =
                warnings.stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(warningMessages.toString(), warnings.size(), is(3));

        final List<String> distinctWarningMessages =
                warningMessages.stream().distinct().collect(Collectors.toList());
        assertThat(distinctWarningMessages.toString(), distinctWarningMessages.size(), is(2));

        final String stringLiteralIWantToBeHidden = String.format(
                "String literal 'IWantToBeHidden' matches the identifier IWantToBeHidden. Consider whether the identifier was intended instead.");
        final String stringLiteralIWantToHide = String.format(
                "String literal 'IWantToHide' matches the identifier IWantToHide. Consider whether the identifier was intended instead.");
        assertThat(distinctWarningMessages, containsInAnyOrder(stringLiteralIWantToBeHidden, stringLiteralIWantToHide));
    }
}
