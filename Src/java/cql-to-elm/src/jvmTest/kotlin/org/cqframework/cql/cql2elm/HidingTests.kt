package org.cqframework.cql.cql2elm

import java.io.IOException
import java.util.stream.Collectors
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class HidingTests {
    @Test
    @Throws(IOException::class)
    fun caseInsensitiveWarning() {
        val translator =
            TestUtils.runSemanticTest(
                "HidingTests/TestHidingCaseInsensitiveWarning.cql",
                0,
                LibraryBuilder.SignatureLevel.All
            )
        val warnings = translator.warnings
        assertLocatorsExist(warnings)
        MatcherAssert.assertThat(warnings.toString(), warnings.size, Matchers.`is`(0))
    }

    @Test
    @Throws(IOException::class)
    fun hiddenIdentifierFromReturn() {
        val translator =
            TestUtils.runSemanticTestNoErrors("HidingTests/TestHiddenIdentifierFromReturn.cql")
        val warnings = translator.warnings
        assertLocatorsExist(warnings)
        MatcherAssert.assertThat(warnings.toString(), warnings.size, Matchers.`is`(1))
        val warningMessages =
            warnings
                .stream()
                .map { obj: CqlCompilerException? -> obj!!.message }
                .collect(Collectors.toSet())
        MatcherAssert.assertThat(
            warningMessages,
            Matchers.contains("A let identifier var is hiding another identifier of the same name.")
        )
    }

    @Test
    @Throws(IOException::class)
    fun hidingUnionWithSameAlias() {
        val translator =
            TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingUnionSameAlias.cql")
        val warnings = translator.warnings
        assertLocatorsExist(warnings)
        val warningMessages =
            warnings
                .stream()
                .map { obj: CqlCompilerException? -> obj!!.message }
                .collect(Collectors.toList())
        MatcherAssert.assertThat(warningMessages.toString(), warnings.size, Matchers.`is`(2))

        val distinct =
            warnings
                .stream()
                .map { obj: CqlCompilerException? -> obj!!.message }
                .distinct()
                .collect(Collectors.toList())

        MatcherAssert.assertThat(distinct.size, Matchers.`is`(2))

        val first =
            "String literal 'X' matches the identifier X. Consider whether the identifier was intended instead."
        val second =
            "String literal 'Y' matches the identifier Y. Consider whether the identifier was intended instead."

        MatcherAssert.assertThat(
            distinct.toString(),
            distinct,
            Matchers.containsInAnyOrder(first, second)
        )
    }

    @Test
    @Throws(IOException::class)
    fun hidingUnionWithSameAliasEachHides() {
        val translator =
            TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingUnionSameAliasEachHides.cql")
        val warnings = translator.warnings
        assertLocatorsExist(warnings)
        val warningMessages =
            warnings
                .stream()
                .map { obj: CqlCompilerException? -> obj!!.message }
                .collect(Collectors.toList())
        MatcherAssert.assertThat(warningMessages.toString(), warnings.size, Matchers.`is`(4))

        val distinct =
            warnings
                .stream()
                .map { obj: CqlCompilerException? -> obj!!.message }
                .distinct()
                .collect(Collectors.toList())

        MatcherAssert.assertThat(distinct.size, Matchers.`is`(3))

        val first =
            "String literal 'X' matches the identifier X. Consider whether the identifier was intended instead."
        val second =
            "String literal 'Y' matches the identifier Y. Consider whether the identifier was intended instead."
        val third =
            "An alias identifier IWantToBeHidden is hiding another identifier of the same name."

        MatcherAssert.assertThat(
            distinct.toString(),
            distinct,
            Matchers.containsInAnyOrder(first, second, third)
        )
    }

    @Test
    @Throws(IOException::class)
    fun soMuchNestingNormal() {
        val translator =
            TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingSoMuchNestingNormal.cql")
        val warnings = translator.warnings
        assertLocatorsExist(warnings)
        MatcherAssert.assertThat(warnings.toString(), warnings.size, Matchers.`is`(0))
    }

    @Test
    @Throws(IOException::class)
    fun soMuchNestingHidingSimple() {
        val translator =
            TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingSoMuchNestingHidingSimple.cql")
        val warnings = translator.warnings
        assertLocatorsExist(warnings)
        MatcherAssert.assertThat(warnings.toString(), warnings.size, Matchers.`is`(1))
        MatcherAssert.assertThat(
            warnings
                .stream()
                .map { obj: CqlCompilerException? -> obj!!.message }
                .collect(Collectors.toList()),
            Matchers.containsInAnyOrder(
                "An alias identifier SoMuchNesting is hiding another identifier of the same name."
            )
        )
    }

    @Test
    @Throws(IOException::class)
    fun soMuchNestingHidingComplex() {
        val translator =
            TestUtils.runSemanticTestNoErrors(
                "HidingTests/TestHidingSoMuchNestingHidingComplex.cql"
            )
        val warnings = translator.warnings
        assertLocatorsExist(warnings)
        val collect =
            warnings
                .stream()
                .map { obj: CqlCompilerException? -> obj!!.message }
                .collect(Collectors.toList())
        MatcherAssert.assertThat(collect.toString(), warnings.size, Matchers.`is`(2))

        val distinct =
            warnings
                .stream()
                .map { obj: CqlCompilerException? -> obj!!.message }
                .distinct()
                .collect(Collectors.toList())

        MatcherAssert.assertThat(distinct.size, Matchers.`is`(2))

        val first =
            "An alias identifier SoMuchNesting is hiding another identifier of the same name."
        val second = "A let identifier SoMuchNesting is hiding another identifier of the same name."

        MatcherAssert.assertThat(distinct, Matchers.containsInAnyOrder(first, second))
    }

    @Test
    @Throws(IOException::class)
    fun hidingLetAlias() {
        val translator = TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingLetAlias.cql")
        val warnings = translator.warnings
        assertLocatorsExist(warnings)
        val warningMessages =
            warnings
                .stream()
                .map { obj: CqlCompilerException? -> obj!!.message }
                .collect(Collectors.toList())
        MatcherAssert.assertThat(warningMessages.toString(), warnings.size, Matchers.`is`(1))
        MatcherAssert.assertThat(
            warnings
                .stream()
                .map { obj: CqlCompilerException? -> obj!!.message }
                .collect(Collectors.toList()),
            Matchers.containsInAnyOrder<Any?>(
                "A let identifier Alias is hiding another identifier of the same name."
            )
        )
    }

    @Test
    @Throws(IOException::class)
    fun hiddenIdentifierArgumentToAlias() {
        val translator =
            TestUtils.runSemanticTestNoErrors("HidingTests/TestHiddenIdentifierArgumentToAlias.cql")

        assertLocatorsExist(translator.warnings)
        MatcherAssert.assertThat(translator.warnings.size, Matchers.`is`(1))
        MatcherAssert.assertThat(
            translator.warnings
                .stream()
                .map { obj: Throwable? -> obj!!.message }
                .collect(Collectors.toList()),
            Matchers.contains(
                "An alias identifier testOperand is hiding another identifier of the same name."
            )
        )
    }

    @Test
    @Throws(IOException::class)
    fun returnArgumentNotConsideredHiddenIdentifier() {
        val translator =
            TestUtils.runSemanticTestNoErrors(
                "HidingTests/TestHidingReturnArgumentNotConsideredHiddenIdentifier.cql"
            )
        assertLocatorsExist(translator.warnings)
        MatcherAssert.assertThat(translator.warnings.size, Matchers.`is`(0))
    }

    @Test
    @Throws(IOException::class)
    fun hidingFunctionDefinitionWithOverloads() {
        val translator =
            TestUtils.runSemanticTestNoErrors(
                "HidingTests/TestHidingFunctionDefinitionWithOverloads.cql"
            )
        val warnings = translator.warnings
        assertLocatorsExist(translator.warnings)
        val warningMessages =
            warnings
                .stream()
                .map { obj: CqlCompilerException? -> obj!!.message }
                .collect(Collectors.toList())
        MatcherAssert.assertThat(warningMessages.toString(), warnings.size, Matchers.`is`(1))
        MatcherAssert.assertThat(
            warningMessages,
            Matchers.contains(
                "An alias identifier IWantToBeHidden is hiding another identifier of the same name."
            )
        )
    }

    @Test
    @Throws(IOException::class)
    fun hidingParameterDefinition() {
        val translator =
            TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingParameterDefinition.cql")
        val warnings = translator.warnings
        assertLocatorsExist(warnings)
        val warningMessages =
            warnings
                .stream()
                .map { obj: CqlCompilerException? -> obj!!.message }
                .collect(Collectors.toList())
        MatcherAssert.assertThat(warningMessages.toString(), warnings.size, Matchers.`is`(1))
        MatcherAssert.assertThat(
            warningMessages,
            Matchers.contains(
                "An alias identifier Measurement Period is hiding another identifier of the same name."
            )
        )
    }

    @Test
    @Throws(IOException::class)
    fun hidingIncludeDefinition() {
        val translator =
            TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingIncludeDefinition.cql")
        val warnings = translator.warnings
        assertLocatorsExist(warnings)
        val warningMessages =
            warnings
                .stream()
                .map { obj: CqlCompilerException? -> obj!!.message }
                .collect(Collectors.toList())
        MatcherAssert.assertThat(warningMessages.toString(), warnings.size, Matchers.`is`(1))
        MatcherAssert.assertThat(
            warningMessages,
            Matchers.contains(
                "An alias identifier FHIRHelpers is hiding another identifier of the same name."
            )
        )
    }

    private fun assertLocatorsExist(exceptions: List<CqlCompilerException>) {
        for (exception in exceptions) {
            Assertions.assertNotNull(exception.locator)
        }
    }

    @Test
    @Throws(IOException::class)
    fun hidingCommaMissingInListConstruction() {
        val translator =
            TestUtils.runSemanticTestNoErrors(
                "HidingTests/TestHidingCommaMissingInListConstruction.cql"
            )
        val warnings = translator.warnings
        assertLocatorsExist(warnings)
        val warningMessages =
            warnings
                .stream()
                .map { obj: CqlCompilerException? -> obj!!.message }
                .collect(Collectors.toList())
        MatcherAssert.assertThat(warningMessages.toString(), warnings.size, Matchers.`is`(2))
        val distinctWarningMessages =
            warningMessages.stream().distinct().collect(Collectors.toList())
        MatcherAssert.assertThat(
            distinctWarningMessages.toString(),
            distinctWarningMessages.size,
            Matchers.`is`(1)
        )
        MatcherAssert.assertThat(
            distinctWarningMessages,
            Matchers.contains(
                "An alias identifier 5 is hiding another identifier of the same name."
            )
        )
    }

    @Suppress("MaxLineLength")
    @Test
    @Throws(IOException::class)
    fun hidingStringLiteral() {
        val translator =
            TestUtils.runSemanticTestNoErrors("HidingTests/TestHidingStringLiteral.cql")
        val warnings = translator.warnings
        assertLocatorsExist(warnings)
        val warningMessages =
            warnings
                .stream()
                .map { obj: CqlCompilerException? -> obj!!.message }
                .collect(Collectors.toList())
        MatcherAssert.assertThat(warningMessages.toString(), warnings.size, Matchers.`is`(3))

        val distinctWarningMessages =
            warningMessages.stream().distinct().collect(Collectors.toList())
        MatcherAssert.assertThat(
            distinctWarningMessages.toString(),
            distinctWarningMessages.size,
            Matchers.`is`(2)
        )

        val stringLiteralIWantToBeHidden =
            "String literal 'IWantToBeHidden' matches the identifier IWantToBeHidden. Consider whether the identifier was intended instead."
        val stringLiteralIWantToHide =
            "String literal 'IWantToHide' matches the identifier IWantToHide. Consider whether the identifier was intended instead."
        MatcherAssert.assertThat(
            distinctWarningMessages,
            Matchers.containsInAnyOrder(stringLiteralIWantToBeHidden, stringLiteralIWantToHide)
        )
    }
}
