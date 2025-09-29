package org.cqframework.cql.cql2elm

import java.io.File
import java.io.IOException
import java.util.Scanner
import java.util.concurrent.CompletableFuture
import java.util.stream.Collectors
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hl7.cql.model.DataType
import org.hl7.cql.model.IntervalType
import org.hl7.cql.model.SimpleType
import org.hl7.cql_annotations.r1.CqlToElmInfo
import org.hl7.elm.r1.As
import org.hl7.elm.r1.ChoiceTypeSpecifier
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.Interval
import org.hl7.elm.r1.NamedTypeSpecifier
import org.hl7.elm.r1.Null
import org.hl7.elm.r1.ProperContains
import org.hl7.elm.r1.Property
import org.hl7.elm.r1.Query
import org.hl7.elm.r1.TypeSpecifier
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Suppress("ForbiddenComment", "MaxLineLength")
internal class TranslationTests {
    // TODO: sameXMLAs? Couldn't find such a thing in hamcrest, but I don't want this to run on the
    // JSON, I want it to
    // verify the actual XML.
    @Test
    @Disabled
    @Throws(IOException::class)
    fun patientPropertyAccess() {
        val expectedXmlFile =
            File(Cql2ElmVisitorTest::class.java.getResource("PropertyTest_ELM.xml")!!.file)
        var expectedXml: String? = null
        Scanner(expectedXmlFile, "UTF-8").useDelimiter("\\Z").use { scanner ->
            expectedXml = scanner.next()
        }
        val propertyTestFile =
            File(Cql2ElmVisitorTest::class.java.getResource("PropertyTest.cql")!!.file)
        val modelManager = ModelManager()
        val actualXml =
            CqlTranslator.fromFile(propertyTestFile.path, LibraryManager(modelManager)).toXml()
        assertThat(actualXml, `is`(expectedXml))
    }

    @Test
    @Disabled
    @Throws(IOException::class)
    fun forPrintElm() {
        val propertyTestFile =
            File(
                TranslationTests::class
                    .java
                    .getResource("LibraryTests/SupplementalDataElements_FHIR4-2.0.0.cql")!!
                    .file
            )
        val modelManager = ModelManager()

        val compilerOptions =
            CqlCompilerOptions(
                CqlCompilerException.ErrorSeverity.Info,
                LibraryBuilder.SignatureLevel.All,
                CqlCompilerOptions.Options.EnableDateRangeOptimization,
                CqlCompilerOptions.Options.EnableAnnotations,
                CqlCompilerOptions.Options.EnableLocators,
                CqlCompilerOptions.Options.EnableResultTypes,
                CqlCompilerOptions.Options.DisableListDemotion,
                CqlCompilerOptions.Options.DisableListPromotion,
                CqlCompilerOptions.Options.DisableMethodInvocation
            )

        val translator =
            CqlTranslator.fromFile(
                propertyTestFile.path,
                LibraryManager(modelManager, compilerOptions)
            )
        println(translator.toJson())
    }

    @Test
    @Disabled
    @Throws(IOException::class)
    fun cms146v2XML() {
        val expectedXml = ""
        val cqlFile =
            File(Cql2ElmVisitorTest::class.java.getResource("CMS146v2_Test_CQM.cql")!!.file)
        val modelManager = ModelManager()
        val actualXml = CqlTranslator.fromFile(cqlFile.path, LibraryManager(modelManager)).toXml()
        assertThat(actualXml, `is`(expectedXml))
    }

    @Test
    @Throws(IOException::class)
    fun identifierLocation() {
        val translator = TestUtils.createTranslator("TranslatorTests/UnknownIdentifier.cql")
        assertEquals(1, translator.errors.size)

        val e = translator.errors[0]
        val tb = e.locator

        Assertions.assertEquals(6, tb!!.startLine)
        Assertions.assertEquals(6, tb.endLine)

        Assertions.assertEquals(5, tb.startChar)
        Assertions.assertEquals(10, tb.endChar)
    }

    @Test
    @Throws(IOException::class)
    fun annotationsPresent() {
        val translator =
            TestUtils.createTranslator(
                "CMS146v2_Test_CQM.cql",
                CqlCompilerOptions.Options.EnableAnnotations
            )
        assertEquals(0, translator.errors.size)
        val defs = translator.translatedLibrary!!.library!!.statements!!.def
        Assertions.assertNotNull(defs[1].annotation)
        Assertions.assertTrue(defs[1].annotation.isNotEmpty())
    }

    @Test
    @Throws(IOException::class)
    fun annotationsAbsent() {
        val translator = TestUtils.createTranslator("CMS146v2_Test_CQM.cql")
        assertEquals(0, translator.errors.size)
        val defs = translator.translatedLibrary!!.library!!.statements!!.def
        assertEquals(0, defs[1].annotation.size)
    }

    @Test
    @Throws(IOException::class)
    fun translatorOptionsPresent() {
        val translator =
            TestUtils.createTranslator(
                "CMS146v2_Test_CQM.cql",
                CqlCompilerOptions.Options.EnableAnnotations
            )
        assertEquals(0, translator.errors.size)
        val library = translator.translatedLibrary!!.library
        Assertions.assertNotNull(library!!.annotation)
        assertThat(library.annotation.size, Matchers.greaterThan(0))
        assertThat(library.annotation[0], Matchers.instanceOf(CqlToElmInfo::class.java))
        val info: CqlToElmInfo = library.annotation[0] as CqlToElmInfo
        assertThat(info.translatorOptions, `is`<String?>("EnableAnnotations"))
    }

    @Test
    @Throws(IOException::class)
    fun noImplicitCasts() {
        val translator = TestUtils.createTranslator("TestNoImplicitCast.cql")
        assertEquals(0, translator.errors.size)
        // Gets the "TooManyCasts" define
        var exp: Expression? =
            translator.translatedLibrary!!.library!!.statements!!.def[2].expression
        assertThat<Expression?>(exp, `is`<Expression?>(Matchers.instanceOf(Query::class.java)))

        var query = exp as Query
        var returnClause = query.`return`
        Assertions.assertNotNull(returnClause)
        Assertions.assertNotNull(returnClause!!.expression)
        assertThat<Expression?>(
            returnClause.expression,
            `is`<Expression?>(Matchers.instanceOf(FunctionRef::class.java))
        )

        var functionRef = returnClause.expression as FunctionRef?
        assertEquals(1, functionRef!!.operand.size)

        // For a widening cast, no As is required, it should be a direct property access.
        var operand: Expression? = functionRef.operand[0]
        assertThat<Expression?>(
            operand,
            `is`<Expression?>(Matchers.instanceOf(Property::class.java))
        )

        // Gets the "NeedsACast" define
        exp = translator.translatedLibrary!!.library!!.statements!!.def[4].expression
        assertThat<Expression?>(exp, `is`<Expression?>(Matchers.instanceOf(Query::class.java)))

        query = exp as Query
        returnClause = query.`return`
        Assertions.assertNotNull(returnClause)
        Assertions.assertNotNull(returnClause!!.expression)
        assertThat<Expression?>(
            returnClause.expression,
            `is`<Expression?>(Matchers.instanceOf(FunctionRef::class.java))
        )

        functionRef = returnClause.expression as FunctionRef?
        assertEquals(1, functionRef!!.operand.size)

        // For narrowing choice casts, an As is expected
        operand = functionRef.operand[0]
        assertThat(operand, `is`(Matchers.instanceOf(As::class.java)))

        val asDef = operand as As
        assertThat<TypeSpecifier?>(
            asDef.asTypeSpecifier,
            `is`<TypeSpecifier?>(Matchers.instanceOf(ChoiceTypeSpecifier::class.java))
        )
    }

    // test for https://github.com/cqframework/clinical_quality_language/issues/1293
    @Test
    @Throws(IOException::class)
    fun defaultContextIsUnfiltered() {
        val translator =
            TestUtils.createTranslator(
                "DefaultContext.cql",
                CqlCompilerOptions.Options.EnableAnnotations,
                CqlCompilerOptions.Options.EnableResultTypes,
                CqlCompilerOptions.Options.EnableDetailedErrors
            )
        assertEquals(0, translator.errors.size)
        val library = translator.translatedLibrary!!.library
        assertThat(library!!.statements!!.def.size, `is`(2))
        val def = library.statements!!.def[0]
        assertThat(def.context, `is`("Unfiltered"))
    }

    @Test
    @Throws(IOException::class)
    fun tenDividedByTwo() {
        val translator = TestUtils.createTranslator("TenDividedByTwo.cql")
        assertEquals(0, translator.errors.size, "Errors: " + translator.errors)
    }

    @Test
    @Throws(IOException::class)
    fun divideMultiple() {
        val translator = TestUtils.createTranslator("DivideMultiple.cql")
        assertEquals(0, translator.errors.size, "Errors: " + translator.errors)
    }

    @Test
    @Throws(IOException::class)
    fun divideVariables() {
        val translator = TestUtils.createTranslator("DivideVariables.cql")
        assertEquals(0, translator.errors.size, "Errors: " + translator.errors)
    }

    @Test
    @Throws(IOException::class)
    fun arithmetic_Mixed() {
        val translator = TestUtils.createTranslator("Arithmetic_Mixed.cql")
        assertEquals(0, translator.errors.size, "Errors: " + translator.errors)
    }

    @Test
    @Throws(IOException::class)
    fun arithmetic_Parenthetical() {
        val translator = TestUtils.createTranslator("Arithmetic_Parenthetical.cql")
        assertEquals(0, translator.errors.size, "Errors: " + translator.errors)
    }

    @Test
    @Throws(IOException::class)
    fun roundUp() {
        val translator = TestUtils.createTranslator("RoundUp.cql")
        assertEquals(0, translator.errors.size, "Errors: " + translator.errors)
    }

    @Test
    @Throws(IOException::class)
    fun roundDown() {
        val translator = TestUtils.createTranslator("RoundDown.cql")
        assertEquals(0, translator.errors.size, "Errors: " + translator.errors)
    }

    @Test
    @Throws(IOException::class)
    fun log_BaseTen() {
        val translator = TestUtils.createTranslator("Log_BaseTen.cql")
        assertEquals(0, translator.errors.size, "Errors: " + translator.errors)
    }

    @Test
    @Throws(IOException::class)
    fun median_odd() {
        val translator = TestUtils.createTranslator("Median_odd.cql")
        assertEquals(0, translator.errors.size, "Errors: " + translator.errors)
    }

    @Test
    @Throws(IOException::class)
    fun median_dup_vals_odd() {
        val translator = TestUtils.createTranslator("Median_dup_vals_odd.cql")
        assertEquals(0, translator.errors.size, "Errors: " + translator.errors)
    }

    @Test
    @Throws(IOException::class)
    fun geometricMean_Zero() {
        val translator = TestUtils.createTranslator("GeometricMean_Zero.cql")
        assertEquals(0, translator.errors.size, "Errors: " + translator.errors)
    }

    @Test
    @Disabled(
        "Could not resolve call to operator Equal with signature (tuple{Foo:System.Any},tuple{Bar:System.Any}"
    )
    @Throws(IOException::class)
    fun tupleDifferentKeys() {
        val translator = TestUtils.createTranslator("TupleDifferentKeys.cql")
        assertEquals(0, translator.errors.size, "Errors: " + translator.errors)
    }

    @Suppress("MaxLineLength")
    @Test
    @Disabled(
        "Could not resolve call to operator Equal with signature (tuple{a:System.String,b:System.Any},tuple{a:System.String,c:System.Any})"
    )
    @Throws(IOException::class)
    fun uncertTuplesWithDiffNullFields() {
        val translator = TestUtils.createTranslator("UncertTuplesWithDiffNullFields.cql")
        assertEquals(0, translator.errors.size, "Errors: " + translator.errors)
    }

    @Test
    @Disabled(
        "Could not resolve call to operator Collapse with signature (System.Any,System.Quantity)"
    )
    @Throws(IOException::class)
    fun nullIvlCollapse_NullCollapse() {
        val translator = TestUtils.createTranslator("NullIvlCollapse_NullCollapse.cql")
        assertEquals(0, translator.errors.size, "Errors: " + translator.errors)
    }

    @Test
    @Throws(IOException::class)
    fun median_q_diff_units() {
        val translator = TestUtils.createTranslator("Median_q_diff_units.cql")
        assertEquals(0, translator.errors.size, "Errors: " + translator.errors)
    }

    @Test
    @Throws(IOException::class)
    fun forwardDeclarationSameTypeDifferentNamespaceNormalTypes() {
        val translator =
            TestUtils.createTranslator(
                "TestForwardDeclarationSameTypeDifferentNamespaceNormalTypes.cql"
            )
        assertThat("Errors: " + translator.errors, translator.errors.size, Matchers.equalTo(0))

        val compileLibrary = translator.translatedLibrary!!.library
        val statements = compileLibrary!!.statements!!.def
        assertThat(statements.size, Matchers.equalTo(3))
    }

    @Test
    @Throws(IOException::class)
    fun forwardDeclarationSameTypeDifferentNamespaceGenericTypes() {
        val translator =
            TestUtils.createTranslator(
                "TestForwardDeclarationSameTypeDifferentNamespaceGenericTypes.cql"
            )
        assertThat("Errors: " + translator.errors, translator.errors.size, Matchers.equalTo(0))

        val compileLibrary = translator.translatedLibrary!!.library
        val statements = compileLibrary!!.statements!!.def
        assertThat(statements.size, Matchers.equalTo(3))
    }

    // This test creates a bunch of translators on the common pool to suss out any race conditions.
    // It's not fool-proof, but is reasonably consistent on my local machine.
    @Test
    @Throws(IOException::class)
    fun multiThreadedTranslation() {
        val futures = mutableListOf<CompletableFuture<*>>()
        (0..9).forEach { _ ->
            futures.add(
                CompletableFuture.runAsync {
                    try {
                        TestUtils.createTranslator("CMS146v2_Test_CQM.cql")
                    } catch (e: IOException) {
                        @Suppress("TooGenericExceptionThrown") throw RuntimeException(e)
                    }
                }
            )
        }

        val cfs = futures.toTypedArray<CompletableFuture<*>?>()

        CompletableFuture.allOf(*cfs).join()
    }

    @Suppress("LongMethod")
    @Test
    @Throws(IOException::class)
    fun resolutionProperlyIncludesTests() {
        val translator = TestUtils.runSemanticTest("ResolutionTests/ProperlyIncludesTests.cql", 0)
        val compiledLibrary = translator.translatedLibrary!!.library
        val statements = compiledLibrary!!.statements!!.def

        assertThat(statements.size, Matchers.equalTo(5))

        var test = statements[0]
        assertThat<Expression?>(
            test.expression,
            Matchers.instanceOf<Expression?>(ProperContains::class.java)
        )
        var properContains = test.expression as ProperContains?
        assertThat(properContains!!.operand[0], Matchers.instanceOf(Interval::class.java))
        var interval = properContains.operand[0] as Interval

        var intervalResultType = interval.resultType
        assertThat<DataType?>(
            intervalResultType,
            Matchers.instanceOf<DataType?>(IntervalType::class.java)
        )
        var intervalType = intervalResultType as IntervalType?
        assertThat(intervalType!!.pointType, Matchers.instanceOf(SimpleType::class.java))
        var pointType: SimpleType = intervalType.pointType as SimpleType
        assertThat(pointType.name, Matchers.equalTo("System.Integer"))
        assertThat(properContains.operand[1], Matchers.instanceOf(As::class.java))
        var asDef = properContains.operand[1] as As
        assertThat(asDef.asType.toString(), Matchers.equalTo("{urn:hl7-org:elm-types:r1}Integer"))
        assertThat<Expression?>(asDef.operand, Matchers.instanceOf<Expression?>(Null::class.java))

        test = statements[1]
        assertThat<Expression?>(
            test.expression,
            Matchers.instanceOf<Expression?>(ProperContains::class.java)
        )
        properContains = test.expression as ProperContains?
        assertThat(properContains!!.operand[0], Matchers.instanceOf(Interval::class.java))
        interval = properContains.operand[0] as Interval

        intervalResultType = interval.resultType
        assertThat<DataType?>(
            intervalResultType,
            Matchers.instanceOf<DataType?>(IntervalType::class.java)
        )
        intervalType = intervalResultType as IntervalType?
        assertThat(intervalType!!.pointType, Matchers.instanceOf(SimpleType::class.java))
        pointType = intervalType.pointType as SimpleType
        assertThat(pointType.name, Matchers.equalTo("System.Integer"))
        assertThat(properContains.operand[1], Matchers.instanceOf(As::class.java))
        asDef = properContains.operand[1] as As
        assertThat(asDef.asType.toString(), Matchers.equalTo("{urn:hl7-org:elm-types:r1}Integer"))
        assertThat<Expression?>(asDef.operand, Matchers.instanceOf<Expression?>(Null::class.java))

        test = statements[2]
        assertThat<Expression?>(
            test.expression,
            Matchers.instanceOf<Expression?>(ProperContains::class.java)
        )
        properContains = test.expression as ProperContains?
        assertThat(properContains!!.operand[0], Matchers.instanceOf(Interval::class.java))
        interval = properContains.operand[0] as Interval

        intervalResultType = interval.resultType
        assertThat<DataType?>(
            intervalResultType,
            Matchers.instanceOf<DataType?>(IntervalType::class.java)
        )
        intervalType = intervalResultType as IntervalType?
        assertThat(intervalType!!.pointType, Matchers.instanceOf(SimpleType::class.java))
        pointType = intervalType.pointType as SimpleType
        assertThat(pointType.name, Matchers.equalTo("System.Any"))
        assertThat(properContains.operand[1], Matchers.instanceOf(Null::class.java))

        test = statements[3]
        assertThat<Expression?>(
            test.expression,
            Matchers.instanceOf<Expression?>(ProperContains::class.java)
        )
        properContains = test.expression as ProperContains?
        assertThat(properContains!!.operand[0], Matchers.instanceOf(Interval::class.java))
        interval = properContains.operand[0] as Interval
        intervalResultType = interval.resultType
        assertThat<DataType?>(
            intervalResultType,
            Matchers.instanceOf<DataType?>(IntervalType::class.java)
        )
        intervalType = intervalResultType as IntervalType?
        assertThat(intervalType!!.pointType, Matchers.instanceOf(SimpleType::class.java))
        pointType = intervalType.pointType as SimpleType
        assertThat(pointType.name, Matchers.equalTo("System.Any"))
        assertThat(properContains.operand[1], Matchers.instanceOf(Null::class.java))

        test = statements[4]
        assertThat<Expression?>(
            test.expression,
            Matchers.instanceOf<Expression?>(ProperContains::class.java)
        )
        properContains = test.expression as ProperContains?
        assertThat(properContains!!.operand[0], Matchers.instanceOf(Interval::class.java))
        interval = properContains.operand[0] as Interval

        intervalResultType = interval.resultType
        assertThat<DataType?>(
            intervalResultType,
            Matchers.instanceOf<DataType?>(IntervalType::class.java)
        )
        intervalType = intervalResultType as IntervalType?
        assertThat(intervalType!!.pointType, Matchers.instanceOf(SimpleType::class.java))
        pointType = intervalType.pointType as SimpleType
        assertThat(pointType.name, Matchers.equalTo("System.Integer"))
        assertThat(properContains.operand[1], Matchers.instanceOf(As::class.java))
    }

    @Test
    @Throws(IOException::class)
    fun hidingVariousUseCases() {
        val translator = TestUtils.runSemanticTest("HidingTests/TestHidingVariousUseCases.cql", 0)
        val warnings = translator.warnings
        val warningMessages =
            warnings
                .stream()
                .map { obj: CqlCompilerException? -> obj!!.message }
                .collect(Collectors.toList())

        assertThat(warningMessages.toString(), translator.warnings.size, `is`(13))

        val distinct = warningMessages.distinct()

        assertThat(warningMessages.toString(), distinct.size, `is`(11))

        val hidingDefinition =
            "An alias identifier Definition is hiding another identifier of the same name."
        val hidingVarLet = "A let identifier var is hiding another identifier of the same name."
        val hidingContextValueSet =
            "An alias identifier ValueSet is hiding another identifier of the same name."
        val hidingLetValueSet =
            "A let identifier ValueSet is hiding another identifier of the same name."
        val hidingContextCode =
            "An alias identifier Code is hiding another identifier of the same name."
        val hidingLetCode = "A let identifier Code is hiding another identifier of the same name."
        val hidingContextCodeSystem =
            "An alias identifier CodeSystem is hiding another identifier of the same name."
        val hidingLetCodeSystem =
            "A let identifier CodeSystem is hiding another identifier of the same name."
        val hidingContextFhir =
            "An alias identifier FHIR is hiding another identifier of the same name."
        val hidingLetFhir = "A let identifier FHIR is hiding another identifier of the same name."
        val hidingAliasLet = "A let identifier Alias is hiding another identifier of the same name."

        assertThat(
            distinct,
            Matchers.containsInAnyOrder(
                hidingDefinition,
                hidingVarLet,
                hidingContextValueSet,
                hidingLetValueSet,
                hidingContextCode,
                hidingLetCode,
                hidingContextCodeSystem,
                hidingLetCodeSystem,
                hidingContextFhir,
                hidingLetFhir,
                hidingAliasLet
            )
        )
    }

    @Test
    @Throws(IOException::class)
    fun abstractClassNotRetrievable() {
        // See:  https://github.com/cqframework/clinical_quality_language/issues/1392
        val translator = TestUtils.runSemanticTest("abstractClassNotRetrievable.cql", 1)
        val errors = translator.errors
        val errorMessages =
            errors
                .stream()
                .map { obj: CqlCompilerException? -> obj!!.message }
                .collect(Collectors.toList())
        assertThat(
            errorMessages,
            Matchers.contains("Specified data type DomainResource does not support retrieval.")
        )
    }

    @Test
    @Throws(IOException::class)
    fun mappingExpansionsRespectSignatureLevel() {
        // See: https://github.com/cqframework/clinical_quality_language/issues/1475
        val translator =
            TestUtils.runSemanticTest(
                "MappingExpansionsRespectSignatureLevel.cql",
                0,
                LibraryBuilder.SignatureLevel.Overloads
            )

        /*
        ExpressionDef: EncounterPeriod
          expression is Query
            return
              expression is FunctionRef
                name FHIRHelpers.ToInterval
                signature is NamedTypeSpecifier FHIR.Period
         */
        val compiledLibrary = translator.translatedLibrary!!.library
        val statements = compiledLibrary!!.statements!!.def

        assertThat(statements.size, `is`(2))
        val encounterPeriod = statements[1]
        assertThat(encounterPeriod.name, `is`("EncounterPeriod"))
        assertThat<Expression?>(
            encounterPeriod.expression,
            Matchers.instanceOf<Expression?>(Query::class.java)
        )
        val query = encounterPeriod.expression as Query?
        assertThat<Expression?>(
            query!!.`return`!!.expression,
            Matchers.instanceOf<Expression?>(FunctionRef::class.java)
        )
        val functionRef = query.`return`!!.expression as FunctionRef?
        assertThat(functionRef!!.libraryName, `is`("FHIRHelpers"))
        assertThat(functionRef.name, `is`("ToInterval"))
        assertThat<Any?>(functionRef.signature, Matchers.notNullValue())
        assertThat(functionRef.signature.size, `is`(1))
        assertThat(functionRef.signature[0], Matchers.instanceOf(NamedTypeSpecifier::class.java))
        val namedTypeSpecifier = functionRef.signature[0] as NamedTypeSpecifier
        assertThat(namedTypeSpecifier.name!!.localPart, `is`("Period"))
    }
}
