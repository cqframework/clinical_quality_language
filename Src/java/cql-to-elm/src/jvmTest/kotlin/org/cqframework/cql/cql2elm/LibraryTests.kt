package org.cqframework.cql.cql2elm

import java.io.IOException
import kotlinx.io.asSource
import kotlinx.io.buffered
import kotlinx.io.files.Path
import org.cqframework.cql.cql2elm.tracking.TrackBack
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.hl7.cql_annotations.r1.CqlToElmError
import org.hl7.elm.r1.AggregateExpression
import org.hl7.elm.r1.Equal
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.Library
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource

@Suppress(
    "LargeClass",
    "NestedBlockDepth",
    "ComplexMethod",
    "MaxLineLength",
    "LongMethod",
    "PrintStackTrace"
)
internal class LibraryTests {
    @Test
    fun libraryReferences() {
        var translator: CqlTranslator?
        try {
            translator =
                CqlTranslator.fromSource(
                    LibraryTests::class
                        .java
                        .getResourceAsStream("LibraryTests/ReferencingLibrary.cql")!!
                        .asSource()
                        .buffered(),
                    libraryManager!!
                )
            MatcherAssert.assertThat(translator.errors.size, Matchers.`is`(0))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // @Test
    // public void testLibraryReferencesWithCacheDisabled() {
    // CqlTranslator translator = null;
    // try {
    // translator =
    // CqlTranslator.fromStream(LibraryTests.class.getResourceAsStream("LibraryTests/ReferencingLibrary.cql"),
    // modelManager, libraryManager.withDisableCache());
    // assertThat(translator.getErrors().size, is(0));
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }
    @Test
    fun includedLibraryWithSignatures() {
        val compilerOptions =
            CqlCompilerOptions(
                CqlCompilerException.ErrorSeverity.Info,
                LibraryBuilder.SignatureLevel.All
            )
        libraryManager = LibraryManager(modelManager!!, compilerOptions)
        libraryManager!!.librarySourceLoader.registerProvider(TestLibrarySourceProvider())
        try {
            val compiler = CqlCompiler(libraryManager!!)
            compiler.run(
                LibraryTests::class
                    .java
                    .getResourceAsStream("LibraryTests/ReferencingLibrary.cql")!!
                    .asSource()
                    .buffered()
            )

            MatcherAssert.assertThat(compiler.errors.size, Matchers.`is`(0))

            val includedLibDefs: MutableMap<String?, ExpressionDef> = HashMap()
            val includedLibraries = compiler.libraries
            includedLibraries.values.forEach { includedLibrary ->
                if (includedLibrary.statements != null) {
                    for (def in includedLibrary.statements!!.def) {
                        includedLibDefs[def.name] = def
                    }
                }
            }

            val baseLibDef: ExpressionDef = includedLibDefs["BaseLibSum"]!!
            MatcherAssert.assertThat(
                (baseLibDef.expression as AggregateExpression).signature.size,
                Matchers.`is`(1)
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Test
    fun alphanumericVersionIssue641() {
        // the issue identified with using DefaultLibrarySourceLoader only; thus
        // creating a fresh set below
        val modelManager = ModelManager()

        val compilerOptions =
            CqlCompilerOptions(
                CqlCompilerException.ErrorSeverity.Info,
                LibraryBuilder.SignatureLevel.All
            )
        val libraryManager = LibraryManager(modelManager, compilerOptions)

        val translationTestFile =
            LibraryTests::class.java.getResourceAsStream("LibraryTests/Issue641.cql")

        val s = LibraryTests::class.java.getResource("LibraryTests/Issue641.cql")!!.path
        val p = Path(s)
        libraryManager.librarySourceLoader.registerProvider(
            DefaultLibrarySourceProvider(p.parent!!)
        )

        try {
            val compiler = CqlCompiler(libraryManager)
            compiler.run(translationTestFile!!.asSource().buffered())

            println(compiler.errors)

            val includedLibDefs: MutableMap<String?, ExpressionDef> = HashMap()
            val includedLibraries = compiler.libraries
            includedLibraries.values.forEach { includedLibrary ->
                if (includedLibrary.statements != null) {
                    for (def in includedLibrary.statements!!.def) {
                        includedLibDefs[def.name] = def
                    }
                }
            }

            val baseLibDef: ExpressionDef = includedLibDefs["BaseLibSum"]!!
            MatcherAssert.assertThat(
                (baseLibDef.expression as AggregateExpression).signature.size,
                Matchers.`is`(1)
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Test
    fun invalidLibraryReferences() {
        var translator: CqlTranslator?
        try {
            translator =
                CqlTranslator.fromSource(
                    LibraryTests::class
                        .java
                        .getResourceAsStream("LibraryTests/InvalidReferencingLibrary.cql")!!
                        .asSource()
                        .buffered(),
                    libraryManager!!
                )
            MatcherAssert.assertThat(translator.errors.size, Matchers.`is`(Matchers.not(0)))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Test
    @Throws(IOException::class)
    fun privateAccessModifierReferencing() {
        val translator =
            TestUtils.createTranslatorFromStream("LibraryTests/AccessModifierReferencing.cql")
        MatcherAssert.assertThat(translator.errors.size, Matchers.`is`(Matchers.not(0)))

        val errors = translator.errors.map { e -> e.message }

        Assertions.assertTrue(
            errors.contains(
                "Identifier ICD-10:2014 in library Base is marked private and cannot be referenced from another library."
            )
        )
        Assertions.assertTrue(
            errors.contains(
                "Identifier f1 in library AccessModifierBase is marked private and cannot be referenced from another library."
            )
        )
        Assertions.assertTrue(
            errors.contains(
                "Identifier PrivateExpression in library Base is marked private and cannot be referenced from another library."
            )
        )
        Assertions.assertTrue(
            errors.contains(
                "Identifier Test Parameter in library Base is marked private and cannot be referenced from another library."
            )
        )
        Assertions.assertTrue(
            errors.contains(
                "Identifier Female Administrative Sex in library Base is marked private and cannot be referenced from another library."
            )
        )
        Assertions.assertTrue(
            errors.contains(
                "Identifier XYZ Code in library Base is marked private and cannot be referenced from another library."
            )
        )
        Assertions.assertTrue(
            errors.contains(
                "Identifier XYZ Concept in library Base is marked private and cannot be referenced from another library."
            )
        )
    }

    @Test
    @Throws(IOException::class)
    fun privateAccessModifierNonReferencing() {
        val translator =
            TestUtils.createTranslatorFromStream("LibraryTests/AccessModifierNonReferencing.cql")
        MatcherAssert.assertThat(translator.errors.size, Matchers.`is`(0))
    }

    @Test
    fun invalidLibraryReference() {
        var translator: CqlTranslator?
        try {
            translator =
                CqlTranslator.fromSource(
                    LibraryTests::class
                        .java
                        .getResourceAsStream("LibraryTests/InvalidLibraryReference.cql")!!
                        .asSource()
                        .buffered(),
                    libraryManager!!
                )
            MatcherAssert.assertThat(translator.errors.size, Matchers.`is`(Matchers.not(0)))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Test
    fun duplicateExpressionLibrary() {
        var translator: CqlTranslator?
        try {
            translator =
                CqlTranslator.fromSource(
                    LibraryTests::class
                        .java
                        .getResourceAsStream("LibraryTests/DuplicateExpressionLibrary.cql")!!
                        .asSource()
                        .buffered(),
                    libraryManager!!
                )
            MatcherAssert.assertThat(translator.errors.size, Matchers.`is`(1))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Test
    fun missingLibrary() {
        var translator: CqlTranslator?
        try {
            translator =
                CqlTranslator.fromSource(
                    LibraryTests::class
                        .java
                        .getResourceAsStream("LibraryTests/MissingLibrary.cql")!!
                        .asSource()
                        .buffered(),
                    libraryManager!!
                )
            MatcherAssert.assertThat(translator.errors.size, Matchers.`is`(1))
            MatcherAssert.assertThat(
                translator.errors[0],
                Matchers.instanceOf(CqlIncludeException::class.java)
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Test
    fun invalidBaseLibrary() {
        var translator: CqlTranslator?
        try {
            translator =
                CqlTranslator.fromSource(
                    LibraryTests::class
                        .java
                        .getResourceAsStream("LibraryTests/ReferencingInvalidBaseLibrary.cql")!!
                        .asSource()
                        .buffered(),
                    libraryManager!!
                )
            MatcherAssert.assertThat(translator.errors.size, Matchers.`is`(1))
            MatcherAssert.assertThat(
                translator.errors[0],
                Matchers.instanceOf(CqlCompilerException::class.java)
            )
            MatcherAssert.assertThat(translator.errors[0].locator, Matchers.notNullValue())
            MatcherAssert.assertThat(
                translator.errors[0].locator!!.library,
                Matchers.notNullValue()
            )
            MatcherAssert.assertThat(
                translator.errors[0].locator!!.library!!.id,
                Matchers.`is`("InvalidBaseLibrary")
            )

            MatcherAssert.assertThat(translator.toELM(), Matchers.notNullValue())
            MatcherAssert.assertThat(translator.toELM()!!.annotation, Matchers.notNullValue())
            MatcherAssert.assertThat(translator.toELM()!!.annotation.size, Matchers.greaterThan(0))
            var invalidBaseLibraryError: CqlToElmError? = null
            for (o in translator.toELM()!!.annotation) {
                if (o is CqlToElmError) {
                    invalidBaseLibraryError = o
                    break
                }
            }
            MatcherAssert.assertThat<CqlToElmError?>(
                invalidBaseLibraryError,
                Matchers.notNullValue()
            )
            MatcherAssert.assertThat(
                invalidBaseLibraryError!!.libraryId,
                Matchers.`is`("InvalidBaseLibrary")
            )
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // This test verifies that when a model load failure prevents proper creation of
    // the context expression, that doesn't lead to internal translator errors.
    @Test
    fun mixedVersionModelReferences() {
        var translator: CqlTranslator?
        try {
            translator =
                CqlTranslator.fromSource(
                    LibraryTests::class
                        .java
                        .getResourceAsStream("LibraryTests/TestMeasure.cql")!!
                        .asSource()
                        .buffered(),
                    libraryManager!!
                )
            MatcherAssert.assertThat(translator.errors.size, Matchers.`is`(3))

            for (error in translator.errors) {
                MatcherAssert.assertThat<TrackBack?>(error.locator, Matchers.notNullValue())
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Test
    fun translatorOptionsFlowDownWithAnnotations() {
        try {
            // Test Annotations are created for both libraries
            val options =
                CqlCompilerOptions(
                    CqlCompilerException.ErrorSeverity.Info,
                    LibraryBuilder.SignatureLevel.All,
                    CqlCompilerOptions.Options.EnableAnnotations
                )
            libraryManager = LibraryManager(modelManager!!, options)
            libraryManager!!.librarySourceLoader.registerProvider(TestLibrarySourceProvider())
            val compiler = CqlCompiler(libraryManager!!)
            compiler.run(
                LibraryTests::class
                    .java
                    .getResourceAsStream("LibraryTests/ReferencingLibrary.cql")!!
                    .asSource()
                    .buffered()
            )

            MatcherAssert.assertThat(compiler.errors.size, Matchers.`is`(0))
            val includedLibraries = compiler.libraries
            includedLibraries.values.forEach { includedLibrary ->
                // Ensure that some annotations are present.
                Assertions.assertTrue(
                    includedLibrary.statements!!.def.count { x -> x.annotation.isNotEmpty() } > 0
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Test
    fun translatorOptionsFlowDownWithoutAnnotations() {
        try {
            libraryManager = LibraryManager(modelManager!!, CqlCompilerOptions())
            libraryManager!!.librarySourceLoader.registerProvider(TestLibrarySourceProvider())

            // Test Annotations are created for both libraries
            val compiler = CqlCompiler(libraryManager!!)
            compiler.run(
                LibraryTests::class
                    .java
                    .getResourceAsStream("LibraryTests/ReferencingLibrary.cql")!!
                    .asSource()
                    .buffered()
            )

            MatcherAssert.assertThat(compiler.errors.size, Matchers.`is`(0))
            val includedLibraries = compiler.libraries
            includedLibraries.values.forEach { includedLibrary ->
                // Ensure that no annotations are present.
                Assertions.assertEquals(
                    0,
                    includedLibrary.statements!!.def.count { x -> x.annotation.isNotEmpty() }
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Test
    @Throws(IOException::class)
    fun syntaxErrorWithNoLibrary() {
        // Syntax errors in anonymous libraries are reported with the name of the source
        // file as the library identifier
        val translator = TestUtils.createTranslator("LibraryTests/SyntaxErrorWithNoLibrary.cql")
        MatcherAssert.assertThat(translator.errors.size, Matchers.greaterThanOrEqualTo(1))
        MatcherAssert.assertThat(
            translator.errors[0].locator!!.library!!.id,
            Matchers.equalTo("SyntaxErrorWithNoLibrary")
        )
    }

    @Test
    @Throws(IOException::class)
    fun syntaxErrorWithNoLibraryFromStream() {
        // Syntax errors in anonymous libraries are reported with the name of the source
        // file as the library identifier
        val translator =
            TestUtils.createTranslatorFromStream("LibraryTests/SyntaxErrorWithNoLibrary.cql")
        MatcherAssert.assertThat(translator.errors.size, Matchers.greaterThanOrEqualTo(1))
        MatcherAssert.assertThat(
            translator.errors[0].locator!!.library!!.id,
            Matchers.equalTo("Anonymous")
        )
    }

    @Test
    @Throws(IOException::class)
    fun syntaxErrorWithLibrary() {
        val translator = TestUtils.createTranslator("LibraryTests/SyntaxErrorWithLibrary.cql")
        MatcherAssert.assertThat(translator.errors.size, Matchers.greaterThanOrEqualTo(1))
        MatcherAssert.assertThat(
            translator.errors[0].locator!!.library!!.id,
            Matchers.equalTo("SyntaxErrorWithLibrary")
        )
    }

    @Test
    @Throws(IOException::class)
    fun syntaxErrorWithLibraryFromStream() {
        val translator =
            TestUtils.createTranslatorFromStream("LibraryTests/SyntaxErrorWithLibrary.cql")
        MatcherAssert.assertThat(translator.errors.size, Matchers.greaterThanOrEqualTo(1))
        MatcherAssert.assertThat(
            translator.errors[0].locator!!.library!!.id,
            Matchers.equalTo("SyntaxErrorWithLibrary")
        )
    }

    @Test
    @Throws(IOException::class)
    fun syntaxErrorReferencingLibrary() {
        val translator =
            TestUtils.createTranslator("LibraryTests/SyntaxErrorReferencingLibrary.cql")
        MatcherAssert.assertThat(translator.errors.size, Matchers.greaterThanOrEqualTo(2))
        MatcherAssert.assertThat(
            translator.errors[0].locator!!.library!!.id,
            Matchers.equalTo("SyntaxErrorReferencingLibrary")
        )
        MatcherAssert.assertThat(
            translator.errors[1].locator!!.library!!.id,
            Matchers.equalTo("SyntaxErrorWithLibrary")
        )
    }

    @Test
    @Throws(IOException::class)
    fun syntaxErrorReferencingLibraryFromStream() {
        val translator =
            TestUtils.createTranslatorFromStream("LibraryTests/SyntaxErrorReferencingLibrary.cql")
        MatcherAssert.assertThat(translator.errors.size, Matchers.greaterThanOrEqualTo(2))
        MatcherAssert.assertThat(
            translator.errors[0].locator!!.library!!.id,
            Matchers.equalTo("SyntaxErrorReferencingLibrary")
        )
        MatcherAssert.assertThat(
            translator.errors[1].locator!!.library!!.id,
            Matchers.equalTo("SyntaxErrorWithLibrary")
        )
    }

    private fun getExpressionDef(library: Library, name: String?): ExpressionDef {
        for (def in library.statements!!.def) {
            if (def.name.equals(name)) {
                return def
            }
        }
        throw IllegalArgumentException("Could not resolve name $name")
    }

    @Test
    @Throws(IOException::class)
    fun fluentFunctions1() {
        val translator = TestUtils.createTranslatorFromStream("LibraryTests/TestFluent3.cql")
        MatcherAssert.assertThat(translator.errors.size, Matchers.equalTo(0))
        val library = translator.toELM()
        val def = getExpressionDef(library!!, "Test")
        MatcherAssert.assertThat(def, Matchers.notNullValue())
        val e = def.expression
        MatcherAssert.assertThat<Expression?>(e, Matchers.notNullValue())
        MatcherAssert.assertThat<Expression?>(
            e,
            Matchers.instanceOf<Expression?>(Equal::class.java)
        )
        val eq = e as Equal
        MatcherAssert.assertThat(eq.operand, Matchers.notNullValue())
        MatcherAssert.assertThat(eq.operand.size, Matchers.equalTo(2))
        MatcherAssert.assertThat(eq.operand[0], Matchers.instanceOf(FunctionRef::class.java))
        MatcherAssert.assertThat(
            (eq.operand[0] as FunctionRef).libraryName,
            Matchers.equalTo("TestFluent1")
        )
    }

    @Test
    @Throws(IOException::class)
    fun fluentFunctions2() {
        val translator = TestUtils.createTranslatorFromStream("LibraryTests/TestFluent4.cql")
        MatcherAssert.assertThat(translator.errors.size, Matchers.equalTo(0))
        val library = translator.toELM()
        val def = getExpressionDef(library!!, "Test")
        MatcherAssert.assertThat(def, Matchers.notNullValue())
        val e = def.expression
        MatcherAssert.assertThat<Expression?>(e, Matchers.notNullValue())
        MatcherAssert.assertThat<Expression?>(
            e,
            Matchers.instanceOf<Expression?>(Equal::class.java)
        )
        val eq = e as Equal
        MatcherAssert.assertThat(eq.operand, Matchers.notNullValue())
        MatcherAssert.assertThat(eq.operand.size, Matchers.equalTo(2))
        MatcherAssert.assertThat(eq.operand[0], Matchers.instanceOf(FunctionRef::class.java))
        MatcherAssert.assertThat(
            (eq.operand[0] as FunctionRef).libraryName,
            Matchers.equalTo("TestFluent2")
        )
    }

    @Test
    @Throws(IOException::class)
    fun fluentFunctions5() {
        val translator = TestUtils.createTranslatorFromStream("LibraryTests/TestFluent5.cql")
        MatcherAssert.assertThat(
            translator.errors.size,
            Matchers.equalTo(1)
        ) // Expects invalid invocation
        MatcherAssert.assertThat(
            translator.errors[0].message,
            Matchers.equalTo(
                "Operator invalidInvocation with signature (System.String) is a fluent function and can only be invoked with fluent syntax."
            )
        )
    }

    @Test
    @Throws(IOException::class)
    fun fluentFunctions6() {
        val translator = TestUtils.createTranslatorFromStream("LibraryTests/TestFluent6.cql")
        MatcherAssert.assertThat(
            translator.errors.size,
            Matchers.equalTo(1)
        ) // Expects invalid fluent invocation
        MatcherAssert.assertThat(
            translator.errors[0].message,
            Matchers.equalTo(
                "Invocation of operator invalidInvocation with signature (System.String) uses fluent syntax, but the operator is not defined as a fluent function."
            )
        )
    }

    @Test
    @Throws(IOException::class)
    fun fluentFunctions7() {
        val translator = TestUtils.createTranslatorFromStream("LibraryTests/TestFluent7.cql")
        MatcherAssert.assertThat(translator.errors.size, Matchers.equalTo(0))
        val library = translator.toELM()
        val def = getExpressionDef(library!!, "Test")
        MatcherAssert.assertThat(def, Matchers.notNullValue())
        val e = def.expression
        MatcherAssert.assertThat<Expression?>(e, Matchers.notNullValue())
        MatcherAssert.assertThat<Expression?>(
            e,
            Matchers.instanceOf<Expression?>(Equal::class.java)
        )
        val eq = e as Equal
        MatcherAssert.assertThat(eq.operand, Matchers.notNullValue())
        MatcherAssert.assertThat(eq.operand.size, Matchers.equalTo(2))
        MatcherAssert.assertThat(eq.operand[0], Matchers.instanceOf(FunctionRef::class.java))
        MatcherAssert.assertThat(
            (eq.operand[0] as FunctionRef).libraryName,
            Matchers.equalTo("TF1")
        )
    }

    @Test
    @Throws(IOException::class)
    fun invalidInvocation() {
        val translator =
            TestUtils.createTranslatorFromStream("LibraryTests/TestInvalidFunction.cql")
        MatcherAssert.assertThat(translator.errors.size, Matchers.equalTo(1))
        MatcherAssert.assertThat(
            translator.errors[0].message,
            Matchers.equalTo(
                "Could not resolve call to operator invalidInvocation with signature ()."
            )
        )
    }

    @Test
    @Throws(IOException::class)
    fun expression() {
        // This test checks to the that the engine can compile short snippets of CQL
        val translator = TestUtils.createTranslatorFromStream("LibraryTests/expression.cql")
        MatcherAssert.assertThat(translator.errors.size, Matchers.equalTo(0))

        val compileLibrary = translator.translatedLibrary!!.library
        val statements = compileLibrary!!.statements!!.def
        MatcherAssert.assertThat(statements.size, Matchers.equalTo(1))
    }

    @Test
    @Throws(IOException::class)
    fun expression2() {
        // This test checks to the that the engine can compile short snippets of CQL
        val translator = TestUtils.createTranslatorFromStream("LibraryTests/expression2.cql")
        MatcherAssert.assertThat(translator.errors.size, Matchers.equalTo(0))

        val compileLibrary = translator.translatedLibrary!!.library
        val statements = compileLibrary!!.statements!!.def
        MatcherAssert.assertThat(statements.size, Matchers.equalTo(1))
    }

    @Test
    @Throws(IOException::class)
    fun forwardDeclaration() {
        val translator =
            TestUtils.createTranslatorFromStream("LibraryTests/TestForwardDeclaration.cql")
        MatcherAssert.assertThat(
            "Errors: " + translator.errors,
            translator.errors.size,
            Matchers.equalTo(0)
        )

        val compileLibrary = translator.translatedLibrary!!.library
        val statements = compileLibrary!!.statements!!.def
        MatcherAssert.assertThat(statements.size, Matchers.equalTo(2))
    }

    @Test
    @Throws(IOException::class)
    fun forwardDeclarationsNormalType() {
        val translator =
            TestUtils.createTranslatorFromStream(
                "LibraryTests/TestForwardDeclarationNormalType.cql"
            )
        MatcherAssert.assertThat(
            "Errors: " + translator.errors,
            translator.errors.size,
            Matchers.equalTo(0)
        )

        val compileLibrary = translator.translatedLibrary!!.library
        val statements = compileLibrary!!.statements!!.def
        MatcherAssert.assertThat(statements.size, Matchers.equalTo(6))
    }

    @Test
    @Throws(IOException::class)
    fun forwardDeclarationsGenericType() {
        val translator =
            TestUtils.createTranslatorFromStream(
                "LibraryTests/TestForwardDeclarationGenericType.cql"
            )
        MatcherAssert.assertThat(
            "Errors: " + translator.errors,
            translator.errors.size,
            Matchers.equalTo(0)
        )

        val compileLibrary = translator.translatedLibrary!!.library
        val statements = compileLibrary!!.statements!!.def
        MatcherAssert.assertThat(statements.size, Matchers.equalTo(5))
    }

    @Test
    @Throws(IOException::class)
    fun forwardDeclarationsImplicitConversion() {
        val translator =
            TestUtils.createTranslatorFromStream(
                "LibraryTests/TestForwardDeclarationImplicitConversion.cql"
            )
        MatcherAssert.assertThat(translator.errors.size, Matchers.equalTo(0))

        val compileLibrary = translator.translatedLibrary!!.library
        val statements = compileLibrary!!.statements!!.def
        MatcherAssert.assertThat(statements.size, Matchers.equalTo(3))
    }

    @Test
    @Throws(IOException::class)
    fun forwardDeclarationsScoringImplicitConversion() {
        val translator =
            TestUtils.createTranslatorFromStream(
                "LibraryTests/TestForwardDeclarationScoringImplicitConversion.cql"
            )
        MatcherAssert.assertThat(
            "Errors: " + translator.errors,
            translator.errors.size,
            Matchers.equalTo(0)
        )

        val compileLibrary = translator.translatedLibrary!!.library
        val statements = compileLibrary!!.statements!!.def
        MatcherAssert.assertThat(statements.size, Matchers.equalTo(3))
        val toString =
            statements.first { statement: ExpressionDef? -> statement!!.name.equals("toString") }

        val expression = toString.expression
        Assertions.assertNotNull(expression)
        Assertions.assertTrue(expression is FunctionRef)

        val functionRef = expression as FunctionRef

        Assertions.assertEquals("calledFunc", functionRef.name)
        Assertions.assertEquals(1, functionRef.operand.size)
        val operand = functionRef.operand[0]
        val resultType = operand.resultType
        Assertions.assertEquals("System.Decimal", resultType.toString())
    }

    @Test
    @Throws(IOException::class)
    fun forwardDeclarationsScoringImplicitConversionNonRelevantFunctionFirst() {
        val translator =
            TestUtils.createTranslatorFromStream(
                "LibraryTests/TestForwardDeclarationScoringImplicitConversionNonRelevantFunctionFirst.cql"
            )
        MatcherAssert.assertThat(
            "Errors: " + translator.errors,
            translator.errors.size,
            Matchers.equalTo(0)
        )

        val compileLibrary = translator.translatedLibrary!!.library
        val statements = compileLibrary!!.statements!!.def
        MatcherAssert.assertThat(statements.size, Matchers.equalTo(3))
        val toString =
            statements.first { statement: ExpressionDef? -> statement!!.name.equals("toString") }

        val expression = toString.expression
        Assertions.assertNotNull(expression)
        Assertions.assertTrue(expression is FunctionRef)

        val functionRef = expression as FunctionRef

        Assertions.assertEquals("calledFunc", functionRef.name)
        Assertions.assertEquals(1, functionRef.operand.size)
        val operand = functionRef.operand[0]
        val resultType = operand.resultType
        Assertions.assertEquals("System.Decimal", resultType.toString())
    }

    @Test
    @Throws(IOException::class)
    fun forwardDeclarationsScoringImplicitConversionMultipleParams() {
        val translator =
            TestUtils.createTranslatorFromStream(
                "LibraryTests/TestForwardDeclarationScoringImplicitConversionMultipleParams.cql"
            )
        MatcherAssert.assertThat(
            "Errors: " + translator.errors,
            translator.errors.size,
            Matchers.equalTo(0)
        )

        val compileLibrary = translator.translatedLibrary!!.library
        val statements = compileLibrary!!.statements!!.def
        MatcherAssert.assertThat(statements.size, Matchers.equalTo(4))
        val toString =
            statements.first { statement: ExpressionDef? -> statement!!.name.equals("caller") }

        val expression = toString.expression
        Assertions.assertNotNull(expression)
        Assertions.assertTrue(expression is FunctionRef)

        val functionRef = expression as FunctionRef

        Assertions.assertEquals("callee", functionRef.name)
        Assertions.assertEquals(3, functionRef.operand.size)

        var operand = functionRef.operand[0]
        var resultType = operand.resultType
        Assertions.assertEquals("System.Decimal", resultType.toString())

        operand = functionRef.operand[1]
        resultType = operand.resultType
        Assertions.assertEquals("System.Decimal", resultType.toString())
    }

    @Test
    @Throws(IOException::class)
    fun forwardDeclarationsScoringImplicitConversionMultipleParamsCannotResolve() {
        val translator =
            TestUtils.createTranslatorFromStream(
                "LibraryTests/TestForwardDeclarationScoringImplicitConversionMultipleParamsCannotResolve.cql"
            )
        MatcherAssert.assertThat(
            "Errors: " + translator.errors,
            translator.errors.size,
            Matchers.equalTo(1)
        )
    }

    @Test
    @Throws(IOException::class)
    fun nonForwardDeclarationsScoringImplicitConversion() {
        val translator =
            TestUtils.createTranslatorFromStream(
                "LibraryTests/TestNonForwardDeclarationScoringImplicitConversion.cql"
            )
        MatcherAssert.assertThat(
            "Errors: " + translator.errors,
            translator.errors.size,
            Matchers.equalTo(0)
        )

        val compileLibrary = translator.translatedLibrary!!.library
        val statements = compileLibrary!!.statements!!.def
        MatcherAssert.assertThat(statements.size, Matchers.equalTo(3))
        val toString =
            statements.first { statement: ExpressionDef? -> statement!!.name.equals("toString") }
        val expression = toString.expression
        Assertions.assertNotNull(expression)
        Assertions.assertTrue(expression is FunctionRef)

        val functionRef = expression as FunctionRef

        Assertions.assertEquals("calledFunc", functionRef.name)
        Assertions.assertEquals(1, functionRef.operand.size)

        val operand = functionRef.operand[0]
        val resultType = operand.resultType
        Assertions.assertEquals("System.Decimal", resultType.toString())
    }

    @Test
    @Throws(IOException::class)
    fun nonForwardDeclarationsScoringImplicitConversionMultipleParams() {
        val translator =
            TestUtils.createTranslatorFromStream(
                "LibraryTests/TestNonForwardDeclarationScoringImplicitConversionMultipleParams.cql"
            )
        MatcherAssert.assertThat(
            "Errors: " + translator.errors,
            translator.errors.size,
            Matchers.equalTo(0)
        )

        val compileLibrary = translator.translatedLibrary!!.library
        val statements = compileLibrary!!.statements!!.def
        MatcherAssert.assertThat(statements.size, Matchers.equalTo(3))
        val toString =
            statements.first { statement: ExpressionDef? -> statement!!.name.equals("caller") }
        val expression = toString.expression
        Assertions.assertNotNull(expression)
        Assertions.assertTrue(expression is FunctionRef)

        val functionRef = expression as FunctionRef

        Assertions.assertEquals("callee", functionRef.name)
        Assertions.assertEquals(2, functionRef.operand.size)

        var operand = functionRef.operand[0]
        var resultType = operand.resultType
        Assertions.assertEquals("System.Decimal", resultType.toString())

        operand = functionRef.operand[1]
        resultType = operand.resultType
        Assertions.assertEquals("System.Decimal", resultType.toString())
    }

    @Test
    @Throws(IOException::class)
    fun nonForwardDeclarationsScoringImplicitConversionMultipleParamsCannotResolve() {
        val translator =
            TestUtils.createTranslatorFromStream(
                "LibraryTests/TestNonForwardDeclarationScoringImplicitConversionMultipleParamsCannotResolve.cql"
            )
        MatcherAssert.assertThat(
            "Errors: " + translator.errors,
            translator.errors.size,
            Matchers.equalTo(1)
        )
    }

    @Test
    @Throws(IOException::class)
    fun issue1587_castErrorForUnresolvedCodeSystemRef() {
        val translator = TestUtils.createTranslatorFromStream("LibraryTests/Issue1587.cql")
        val errors = translator.errors

        // No cast error should be thrown
        Assertions.assertEquals(2, errors.size)
        val codeSystemRefError = errors[0]
        MatcherAssert.assertThat(
            codeSystemRefError.message,
            Matchers.containsString("Could not resolve reference to code system DoesNotExist")
        )

        val privateCodeSystemError = errors[1]
        MatcherAssert.assertThat(
            privateCodeSystemError.message,
            Matchers.containsString(
                "Identifier PrivateCodes in library Issue1587Include is marked private and cannot be referenced from another library."
            )
        )
    }

    @ParameterizedTest
    @MethodSource("sigParams")
    @Throws(IOException::class)
    fun forwardAmbiguousFailOnAmbiguousFunctionResolutionWithoutTypeInformationSignatureLevelNone(
        testFileName: String,
        signatureLevel: LibraryBuilder.SignatureLevel?
    ) {
        val translator = TestUtils.createTranslatorFromStream(testFileName, signatureLevel)
        val expectedWarningCount =
            if (
                LibraryBuilder.SignatureLevel.None == signatureLevel ||
                    LibraryBuilder.SignatureLevel.Differing == signatureLevel
            )
                2
            else 0
        MatcherAssert.assertThat(
            "Warnings: " + translator.warnings,
            translator.warnings.size,
            Matchers.equalTo(expectedWarningCount)
        )

        if (expectedWarningCount > 0) {
            MatcherAssert.assertThat(
                translator.warnings[0].message,
                Matchers.equalTo(
                    java.lang.String.format(
                        "The function TestAmbiguousFailOnAmbiguousFunctionResolutionWithoutTypeInformation.TestAny has multiple overloads and due to the SignatureLevel setting (%s), the overload signature is not being included in the output. This may result in ambiguous function resolution at runtime, consider setting the SignatureLevel to Overloads or All to ensure that the output includes sufficient information to support correct overload selection at runtime.",
                        signatureLevel!!.name
                    )
                )
            )
        }
    }

    companion object {
        private var modelManager: ModelManager? = null
        private var libraryManager: LibraryManager? = null

        @JvmStatic
        @BeforeAll
        fun setup() {
            modelManager = ModelManager()
            libraryManager = LibraryManager(modelManager!!)
            libraryManager!!.librarySourceLoader.registerProvider(TestLibrarySourceProvider())
        }

        @JvmStatic
        @AfterAll
        fun tearDown() {
            libraryManager!!.librarySourceLoader.clearProviders()
        }

        private const val FORWARD_AMBIGUOUS_FUNCTION_RESOLUTION_FILE =
            "LibraryTests/TestForwardAmbiguousFunctionResolutionWithoutTypeInformation.cql"
        private const val NON_FORWARD_AMBIGUOUS_FUNCTION_RESOLUTION_FILE =
            "LibraryTests/TestNonForwardAmbiguousFunctionResolutionWithoutTypeInformation.cql"

        @Suppress("UnusedPrivateMember")
        @JvmStatic
        private fun sigParams(): Array<Array<Any>> {
            return arrayOf(
                arrayOf(
                    FORWARD_AMBIGUOUS_FUNCTION_RESOLUTION_FILE,
                    LibraryBuilder.SignatureLevel.None
                ),
                arrayOf(
                    FORWARD_AMBIGUOUS_FUNCTION_RESOLUTION_FILE,
                    LibraryBuilder.SignatureLevel.Differing
                ),
                arrayOf(
                    FORWARD_AMBIGUOUS_FUNCTION_RESOLUTION_FILE,
                    LibraryBuilder.SignatureLevel.Overloads
                ),
                arrayOf(
                    FORWARD_AMBIGUOUS_FUNCTION_RESOLUTION_FILE,
                    LibraryBuilder.SignatureLevel.All
                ),
                arrayOf(
                    NON_FORWARD_AMBIGUOUS_FUNCTION_RESOLUTION_FILE,
                    LibraryBuilder.SignatureLevel.None
                ),
                arrayOf(
                    NON_FORWARD_AMBIGUOUS_FUNCTION_RESOLUTION_FILE,
                    LibraryBuilder.SignatureLevel.Differing
                ),
                arrayOf(
                    NON_FORWARD_AMBIGUOUS_FUNCTION_RESOLUTION_FILE,
                    LibraryBuilder.SignatureLevel.Overloads
                ),
                arrayOf(
                    NON_FORWARD_AMBIGUOUS_FUNCTION_RESOLUTION_FILE,
                    LibraryBuilder.SignatureLevel.All
                )
            )
        }
    }
}
