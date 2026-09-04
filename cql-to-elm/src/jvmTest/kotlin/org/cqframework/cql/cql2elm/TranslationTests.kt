package org.cqframework.cql.cql2elm

import java.io.File
import java.io.IOException
import java.util.Scanner
import java.util.concurrent.CompletableFuture
import javax.xml.namespace.QName
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.hl7.cql.model.IntervalType
import org.hl7.cql.model.SimpleType
import org.hl7.cql_annotations.r1.CqlToElmInfo
import org.hl7.elm.r1.As
import org.hl7.elm.r1.ChoiceTypeSpecifier
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.Interval
import org.hl7.elm.r1.NamedTypeSpecifier
import org.hl7.elm.r1.Null
import org.hl7.elm.r1.ProperContains
import org.hl7.elm.r1.Property
import org.hl7.elm.r1.Query

@Suppress("ForbiddenComment", "MaxLineLength")
internal class TranslationTests {
    // TODO: sameXMLAs? Couldn't find such a thing in hamcrest, but I don't want this to run on the
    // JSON, I want it to
    // verify the actual XML.
    @Test
    @Ignore
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
        assertEquals(expectedXml, actualXml)
    }

    @Test
    @Ignore
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
                CqlCompilerOptions.Options.DisableMethodInvocation,
            )

        val translator =
            CqlTranslator.fromFile(
                propertyTestFile.path,
                LibraryManager(modelManager, compilerOptions),
            )
        println(translator.toJson())
    }

    @Test
    @Ignore
    @Throws(IOException::class)
    fun cms146v2XML() {
        val expectedXml = ""
        val cqlFile =
            File(Cql2ElmVisitorTest::class.java.getResource("CMS146v2_Test_CQM.cql")!!.file)
        val modelManager = ModelManager()
        val actualXml = CqlTranslator.fromFile(cqlFile.path, LibraryManager(modelManager)).toXml()
        assertEquals(expectedXml, actualXml)
    }

    @Test
    @Throws(IOException::class)
    fun identifierLocation() {
        val translator = TestUtils.createTranslator("TranslatorTests/UnknownIdentifier.cql")
        assertEquals(1, translator.errors.size)

        val e = translator.errors[0]
        val tb = e.locator

        assertEquals(6, tb!!.startLine)
        assertEquals(6, tb.endLine)

        assertEquals(5, tb.startChar)
        assertEquals(10, tb.endChar)
    }

    @Test
    @Throws(IOException::class)
    fun annotationsPresent() {
        val translator =
            TestUtils.createTranslator(
                "CMS146v2_Test_CQM.cql",
                CqlCompilerOptions.Options.EnableAnnotations,
            )
        assertEquals(0, translator.errors.size)
        val defs = translator.translatedLibrary!!.library!!.statements!!.def
        assertNotNull(defs[1].annotation)
        assertTrue(defs[1].annotation.isNotEmpty())
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
                CqlCompilerOptions.Options.EnableAnnotations,
            )
        assertEquals(0, translator.errors.size)
        val library = translator.translatedLibrary!!.library
        assertNotNull(library!!.annotation)
        assertTrue(library.annotation.isNotEmpty())
        assertIs<CqlToElmInfo>(library.annotation[0])
        val info = library.annotation[0] as CqlToElmInfo
        assertEquals("EnableAnnotations", info.translatorOptions)
    }

    @Test
    @Throws(IOException::class)
    fun noImplicitCasts() {
        val translator = TestUtils.createTranslator("TestNoImplicitCast.cql")
        assertEquals(0, translator.errors.size)
        // Gets the "TooManyCasts" define
        var exp = translator.translatedLibrary!!.library!!.statements!!.def[2].expression
        assertIs<Query>(exp)

        var query = exp
        var returnClause = query.`return`
        assertNotNull(returnClause)
        assertNotNull(returnClause.expression)
        assertIs<FunctionRef>(returnClause.expression)

        var functionRef = returnClause.expression as FunctionRef?
        assertEquals(1, functionRef!!.operand.size)

        // For a widening cast, no As is required, it should be a direct property access.
        var operand = functionRef.operand[0]
        assertIs<Property>(operand)

        // Gets the "NeedsACast" define
        exp = translator.translatedLibrary!!.library!!.statements!!.def[4].expression
        assertIs<Query>(exp)

        query = exp
        returnClause = query.`return`
        assertNotNull(returnClause)
        assertNotNull(returnClause.expression)
        assertIs<FunctionRef>(returnClause.expression)

        functionRef = returnClause.expression as FunctionRef?
        assertEquals(1, functionRef!!.operand.size)

        // For narrowing choice casts, an As is expected
        operand = functionRef.operand[0]
        assertIs<As>(operand)

        val asDef = operand
        assertIs<ChoiceTypeSpecifier>(asDef.asTypeSpecifier)
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
                CqlCompilerOptions.Options.EnableDetailedErrors,
            )
        assertEquals(0, translator.errors.size)
        val library = translator.translatedLibrary!!.library
        assertEquals(2, library!!.statements!!.def.size)
        val def = library.statements!!.def[0]
        assertEquals("Unfiltered", def.context)
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
    @Ignore(
        "Could not resolve call to operator Equal with signature (tuple{Foo:System.Any},tuple{Bar:System.Any}"
    )
    @Throws(IOException::class)
    fun tupleDifferentKeys() {
        val translator = TestUtils.createTranslator("TupleDifferentKeys.cql")
        assertEquals(0, translator.errors.size, "Errors: " + translator.errors)
    }

    @Suppress("MaxLineLength")
    @Test
    @Ignore(
        "Could not resolve call to operator Equal with signature (tuple{a:System.String,b:System.Any},tuple{a:System.String,c:System.Any})"
    )
    @Throws(IOException::class)
    fun uncertTuplesWithDiffNullFields() {
        val translator = TestUtils.createTranslator("UncertTuplesWithDiffNullFields.cql")
        assertEquals(0, translator.errors.size, "Errors: " + translator.errors)
    }

    @Test
    @Ignore(
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
        assertEquals(0, translator.errors.size, "Errors: " + translator.errors)

        val compileLibrary = translator.translatedLibrary!!.library
        val statements = compileLibrary!!.statements!!.def
        assertEquals(3, statements.size)
    }

    @Test
    @Throws(IOException::class)
    fun forwardDeclarationSameTypeDifferentNamespaceGenericTypes() {
        val translator =
            TestUtils.createTranslator(
                "TestForwardDeclarationSameTypeDifferentNamespaceGenericTypes.cql"
            )
        assertEquals(0, translator.errors.size, "Errors: " + translator.errors)

        val compileLibrary = translator.translatedLibrary!!.library
        val statements = compileLibrary!!.statements!!.def
        assertEquals(3, statements.size)
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

        assertEquals(5, statements.size)

        var test = statements[0]
        assertIs<ProperContains>(test.expression)
        var properContains = test.expression as ProperContains?
        assertIs<Interval>(properContains!!.operand[0])
        var interval = properContains.operand[0] as Interval

        var intervalResultType = interval.resultType
        assertIs<IntervalType>(intervalResultType)
        var intervalType = intervalResultType as IntervalType?
        assertIs<SimpleType>(intervalType!!.pointType)
        var pointType = intervalType.pointType as SimpleType
        assertEquals("System.Integer", pointType.name)
        assertIs<As>(properContains.operand[1])
        var asDef = properContains.operand[1] as As
        assertEquals("{urn:hl7-org:elm-types:r1}Integer", asDef.asType.toString())
        assertIs<Null>(asDef.operand)

        test = statements[1]
        assertIs<ProperContains>(test.expression)
        properContains = test.expression as ProperContains
        assertIs<Interval>(properContains.operand[0])
        interval = properContains.operand[0] as Interval

        intervalResultType = interval.resultType
        assertIs<IntervalType>(intervalResultType)
        intervalType = intervalResultType
        assertIs<SimpleType>(intervalType.pointType)
        pointType = intervalType.pointType as SimpleType
        assertEquals("System.Integer", pointType.name)
        assertIs<As>(properContains.operand[1])
        asDef = properContains.operand[1] as As
        assertEquals("{urn:hl7-org:elm-types:r1}Integer", asDef.asType.toString())
        assertIs<Null>(asDef.operand)

        test = statements[2]
        assertIs<ProperContains>(test.expression)
        properContains = test.expression as ProperContains
        assertIs<Interval>(properContains.operand[0])
        interval = properContains.operand[0] as Interval

        intervalResultType = interval.resultType
        assertIs<IntervalType>(intervalResultType)
        intervalType = intervalResultType
        assertIs<SimpleType>(intervalType.pointType)
        pointType = intervalType.pointType as SimpleType
        assertEquals("System.Any", pointType.name)
        assertIs<Null>(properContains.operand[1])

        test = statements[3]
        assertIs<ProperContains>(test.expression)
        properContains = test.expression as ProperContains
        assertIs<Interval>(properContains.operand[0])
        interval = properContains.operand[0] as Interval
        intervalResultType = interval.resultType
        assertIs<IntervalType>(intervalResultType)
        intervalType = intervalResultType
        assertIs<SimpleType>(intervalType.pointType)
        pointType = intervalType.pointType as SimpleType
        assertEquals("System.Any", pointType.name)
        assertIs<Null>(properContains.operand[1])

        test = statements[4]
        assertIs<ProperContains>(test.expression)
        properContains = test.expression as ProperContains
        assertIs<Interval>(properContains.operand[0])
        interval = properContains.operand[0] as Interval

        intervalResultType = interval.resultType
        assertIs<IntervalType>(intervalResultType)
        intervalType = intervalResultType
        assertIs<SimpleType>(intervalType.pointType)
        pointType = intervalType.pointType as SimpleType
        assertEquals("System.Integer", pointType.name)
        assertIs<As>(properContains.operand[1])
    }

    @Test
    @Throws(IOException::class)
    fun hidingVariousUseCases() {
        val translator = TestUtils.runSemanticTest("HidingTests/TestHidingVariousUseCases.cql", 0)
        val warnings = translator.warnings
        val warningMessages = warnings.map { it.message }

        assertEquals(13, translator.warnings.size, warningMessages.toString())

        val distinct = warningMessages.distinct()

        assertEquals(11, distinct.size, warningMessages.toString())

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

        for (message in
            listOf(
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
                hidingAliasLet,
            )) {
            assertContains(distinct, message)
        }
    }

    @Test
    @Throws(IOException::class)
    fun abstractClassNotRetrievable() {
        // See:  https://github.com/cqframework/clinical_quality_language/issues/1392
        val translator = TestUtils.runSemanticTest("abstractClassNotRetrievable.cql", 1)
        val errors = translator.errors
        val errorMessages = errors.map { it.message }
        assertContains(
            errorMessages,
            "Specified data type DomainResource does not support retrieval.",
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
                LibraryBuilder.SignatureLevel.Overloads,
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

        assertEquals(2, statements.size)
        val encounterPeriod = statements[1]
        assertEquals("EncounterPeriod", encounterPeriod.name)
        assertIs<Query>(encounterPeriod.expression)
        val query = encounterPeriod.expression as Query?
        assertIs<FunctionRef>(query!!.`return`!!.expression)
        val functionRef = query.`return`!!.expression as FunctionRef?
        assertEquals("FHIRHelpers", functionRef!!.libraryName)
        assertEquals("ToInterval", functionRef.name)
        assertNotNull(functionRef.signature)
        assertEquals(1, functionRef.signature.size)
        assertIs<NamedTypeSpecifier>(functionRef.signature[0])
        val namedTypeSpecifier = functionRef.signature[0] as NamedTypeSpecifier
        assertEquals("Period", namedTypeSpecifier.name!!.localPart)
    }

    @Test
    fun contextHasResultType() {
        val translator =
            TestUtils.createTranslatorFromText(
                """
                library Lib1
                using FHIR version '4.0.1'
                context Patient
            """
                    .trimIndent(),
                CqlCompilerOptions.Options.EnableResultTypes,
            )
        val library = translator.toELM()
        assertNotNull(library)
        val modelContextRetrieve = library.statements!!.def[0]
        assertEquals(QName("http://hl7.org/fhir", "Patient"), modelContextRetrieve.resultTypeName)
    }
}
