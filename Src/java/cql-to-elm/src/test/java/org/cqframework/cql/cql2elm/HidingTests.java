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
        assertThat(warnings.toString(), translator.getWarnings().size(), is(0));
    }

    @Test
    public void testHiddenIdentifierFromReturn() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTestNoErrors("HidingTests/TestHiddenIdentifierFromReturn.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();

        assertThat(warnings.toString(), translator.getWarnings().size(), is(1));
        final Set<String> warningMessages = warnings.stream().map(Throwable::getMessage).collect(Collectors.toSet());
        assertThat(warningMessages, contains("A let identifier [var] is hiding another identifier of the same name. \n"));
    }

    @Test
    public void testHidingUnionWithSameAlias() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingUnionSameAlias.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();

        final List<String> warningMessages = warnings.stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(warningMessages.toString(), translator.getWarnings().size(), is(2));

        final List<String> distinct = translator.getWarnings().stream().map(Throwable::getMessage).distinct().collect(Collectors.toList());

        assertThat(distinct.size(), is(2));

        final String first = "You used a string literal: [X] here that matches an identifier in scope: [X]. Did you mean to use the identifier instead? \n";
        final String second = "You used a string literal: [Y] here that matches an identifier in scope: [Y]. Did you mean to use the identifier instead? \n";

        assertThat(distinct.toString(), distinct, containsInAnyOrder(first, second));
    }

    @Test
    public void testHidingUnionWithSameAliasEachHides() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingUnionSameAliasEachHides.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();

        final List<String> warningMessages = warnings.stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(warningMessages.toString(), translator.getWarnings().size(), is(4));

        final List<String> distinct = translator.getWarnings().stream().map(Throwable::getMessage).distinct().collect(Collectors.toList());

        assertThat(distinct.size(), is(3));

        final String first = "You used a string literal: [X] here that matches an identifier in scope: [X]. Did you mean to use the identifier instead? \n";
        final String second = "You used a string literal: [Y] here that matches an identifier in scope: [Y]. Did you mean to use the identifier instead? \n";
        final String third = "An alias identifier [IWantToBeHidden] is hiding another identifier of the same name. \n";

        assertThat(distinct.toString(), distinct, containsInAnyOrder(first, second, third));
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
        assertThat(warnings.stream().map(Throwable::getMessage).collect(Collectors.toList()), containsInAnyOrder("An alias identifier [SoMuchNesting] is hiding another identifier of the same name. \n"));
    }

    @Test
    public void testSoMuchNestingHidingComplex() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingSoMuchNestingHidingComplex.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();

        final List<String> collect = warnings.stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(collect.toString(), translator.getWarnings().size(), is(2));

        final List<String> distinct = translator.getWarnings().stream().map(Throwable::getMessage).distinct().collect(Collectors.toList());

        assertThat(distinct.size(), is(2));

        final String first = "An alias identifier [SoMuchNesting] is hiding another identifier of the same name. \n";
        final String second = "A let identifier [SoMuchNesting] is hiding another identifier of the same name. \n";

        assertThat(distinct, containsInAnyOrder(first, second));
    }

    @Test
    public void testHidingLetAlias() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingLetAlias.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();

        final List<String> warningMessages = warnings.stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(warningMessages.toString(), translator.getWarnings().size(), is(1));
        assertThat(warnings.stream().map(Throwable::getMessage).collect(Collectors.toList()), containsInAnyOrder("A let identifier [Alias] is hiding another identifier of the same name. \n"));
    }

    @Test
    public void testHiddenIdentifierArgumentToAlias() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTestNoErrors("HidingTests/TestHiddenIdentifierArgumentToAlias.cql");

        assertThat(translator.getWarnings().size(), is(1));
        assertThat(translator.getWarnings()
                        .stream()
                        .map(Throwable::getMessage)
                        .collect(Collectors.toList()),
                    contains("An alias identifier [testOperand] is hiding another identifier of the same name. \n"));
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
        assertThat(warningMessages, contains("An alias identifier [IWantToBeHidden] is hiding another identifier of the same name. \n"));
    }

    @Test
    public void testHidingParameterDefinition() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingParameterDefinition.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();
        final List<String> warningMessages = warnings.stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(warningMessages.toString(), warnings.size(), is(1));
        assertThat(warningMessages, contains("An alias identifier [Measurement Period] is hiding another identifier of the same name. \n"));
    }

    @Test
    public void testHidingIncludeDefinition() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingIncludeDefinition.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();
        final List<String> warningMessages = warnings.stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(warningMessages.toString(), warnings.size(), is(1));
        assertThat(warningMessages, contains("An alias identifier [FHIRHelpers] is hiding another identifier of the same name. \n"));
    }

    @Test
    public void testHidingCommaMissingInListConstruction() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingCommaMissingInListConstruction.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();
        final List<String> warningMessages = warnings.stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(warningMessages.toString(), warnings.size(), is(2));
        final List<String> distinctWarningMessages = warningMessages.stream().distinct().collect(Collectors.toList());
        assertThat(distinctWarningMessages.toString(), distinctWarningMessages.size(), is(1));
        assertThat(distinctWarningMessages, contains("An alias identifier [5] is hiding another identifier of the same name. \n"));
    }

    @Test
    public void testHidingStringLiteral() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingStringLiteral.cql");
        final List<CqlCompilerException> warnings = translator.getWarnings();
        final List<String> warningMessages = warnings.stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(warningMessages.toString(), warnings.size(), is(3));

        final List<String> distinctWarningMessages = warningMessages.stream().distinct().collect(Collectors.toList());
        assertThat(distinctWarningMessages.toString(), distinctWarningMessages.size(), is(2));

        final String stringLiteralIWantToBeHidden = "You used a string literal: [IWantToBeHidden] here that matches an identifier in scope: [IWantToBeHidden]. Did you mean to use the identifier instead? \n";
        final String stringLiteralIWantToHide = "You used a string literal: [IWantToHide] here that matches an identifier in scope: [IWantToHide]. Did you mean to use the identifier instead? \n";
        assertThat(distinctWarningMessages, containsInAnyOrder(stringLiteralIWantToBeHidden, stringLiteralIWantToHide));
    }
}
