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
        final CqlTranslator translator = TestUtils.runSemanticTest("TestCaseInsensitiveWarning.cql", 0, LibraryBuilder.SignatureLevel.All);
        final List<CqlCompilerException> warnings = translator.getWarnings();
        assertThat(warnings.toString(), translator.getWarnings().size(), is(1));
        final Set<String> warningMessages = warnings.stream().map(Throwable::getMessage).collect(Collectors.toSet());
        assertThat(warningMessages, contains("Identifier hiding detected: Identifier for identifiers: [Patients] resolved as an expression definition with case insensitive matching.\n"));
    }

    @Test
    public void testHiddenIdentifierFromReturn() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTest("TestHiddenIdentifierFromReturn.cql", 0);
        final List<CqlCompilerException> warnings = translator.getWarnings();

        assertThat(warnings.toString(), translator.getWarnings().size(), is(1));
        final Set<String> warningMessages = warnings.stream().map(Throwable::getMessage).collect(Collectors.toSet());
        assertThat(warningMessages, contains("Identifier hiding detected: Identifier for identifiers: [var] resolved as a let of a query with exact case matching.\n"));
    }

    @Test
    public void testHidingUnionWithSameAlias() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTest("TestHidingUnionSameAlias.cql", 0);
        final List<CqlCompilerException> warnings = translator.getWarnings();

        assertThat(warnings.toString(), translator.getWarnings().size(), is(0));
    }

    @Test
    public void testHidingUnionWithSameAliasEachHides() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTest("TestHidingUnionSameAliasEachHides.cql", 0);
        final List<CqlCompilerException> warnings = translator.getWarnings();

        assertThat(warnings.toString(), translator.getWarnings().size(), is(2));

        final List<String> distinct = translator.getWarnings().stream().map(Throwable::getMessage).distinct().collect(Collectors.toList());

        assertThat(distinct.size(), is(1));

        final String first = "Identifier hiding detected: Identifier for identifiers: [IWantToBeHidden] resolved as a context accessor with exact case matching.\n";

        assertThat(distinct, containsInAnyOrder(first));
    }

    @Test
    public void testSoMuchNestingNormal() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTest("TestSoMuchNestingNormal.cql", 0);
        final List<CqlCompilerException> warnings = translator.getWarnings();

        assertThat(warnings.toString(), translator.getWarnings().size(), is(0));
    }

    @Test
    public void testSoMuchNestingHidingSimple() throws IOException {
        // LUKETODO:  get rid of -1
        final CqlTranslator translator = TestUtils.runSemanticTest("TestSoMuchNestingHidingSimple.cql", -1);
        final List<CqlCompilerException> warnings = translator.getWarnings();

        // LUKETODO:  this doesn't work because "SoMuchNesting" resolves to null in LibraryBuilder.
        assertThat(warnings.toString(), translator.getWarnings().size(), is(1));
        // LUKETODO:  add more assertions
        assertThat(warnings.stream().map(Throwable::getMessage).collect(Collectors.toList()), containsInAnyOrder("Identifier hiding detected: Identifier for identifiers: [SoMuchNesting] resolved as a context accessor with exact case matching.\n"));
    }

    @Test
    public void testSoMuchNestingHidingComplex() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTest("TestSoMuchNestingHidingComplex.cql", 0);
        final List<CqlCompilerException> warnings = translator.getWarnings();

        final List<String> collect = warnings.stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(collect.toString(), translator.getWarnings().size(), is(2));

        final List<String> distinct = translator.getWarnings().stream().map(Throwable::getMessage).distinct().collect(Collectors.toList());

        assertThat(distinct.size(), is(2));

        final String first = "Identifier hiding detected: Identifier for identifiers: [SoMuchNesting] resolved as a context accessor with exact case matching.\n";
        final String second = "Identifier hiding detected: Identifier for identifiers: [SoMuchNesting] resolved as a let of a query with exact case matching.\n";

        assertThat(distinct, containsInAnyOrder(first, second));
    }

    @Test
    public void testHidingLetAlias() throws IOException {
        final CqlTranslator translator = TestUtils.runSemanticTest("TestHidingLetAlias.cql", 0);
        final List<CqlCompilerException> warnings = translator.getWarnings();

        final List<String> collect = warnings.stream().map(Throwable::getMessage).collect(Collectors.toList());
        assertThat(collect.toString(), translator.getWarnings().size(), is(1));

//        final List<String> distinct = translator.getWarnings().stream().map(Throwable::getMessage).distinct().collect(Collectors.toList());
//
//        assertThat(distinct.size(), is(2));
//
//        final String first = "Identifier hiding detected: Identifier for identifiers: [SoMuchNesting] resolved as a context accessor with exact case matching.\n";
//        final String second = "Identifier hiding detected: Identifier for identifiers: [SoMuchNesting] resolved as a let of a query with exact case matching.\n";
//
//        assertThat(distinct, containsInAnyOrder(first, second));
    }

    @Test
    public void testHiddenIdentifierArgumentToAlias() throws IOException {
        final CqlTranslator translator = TestUtils.createTranslatorFromStream("TestHiddenIdentifierArgumentToAlias.cql");

        assertThat(translator.getWarnings().size(), is(1));
        assertThat(translator.getWarnings()
                        .stream()
                        .map(Throwable::getMessage)
                        .collect(Collectors.toList()),
// LUKETODO:  do we need to be particular about what's hiding what??
//                    contains("Identifier hiding detected: Identifier for identifiers: [testOperand] resolved as an operand to a function with exact case matching.\n"));
                    contains("Identifier hiding detected: Identifier for identifiers: [testOperand] resolved as a context accessor with exact case matching.\n"));
    }

    @Test
    public void testReturnArgumentNotConsideredHiddenIdentifier() throws IOException {
        final CqlTranslator translator = TestUtils.createTranslatorFromStream("TestReturnArgumentNotConsideredHiddenIdentifier.cql");
        assertThat(translator.getWarnings().size(), is(0));
    }
}
