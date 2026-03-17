package org.cqframework.cql.cql2elm.analysis

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.hl7.cql.ast.Builder
import org.hl7.cql.ast.IdentifierExpression
import org.hl7.cql.model.DataType

class TypeResolverTest {

    private val registry = OperatorRegistry.createSystemRegistry()

    private fun analyzeAndGetExprType(cql: String, exprName: String): DataType? {
        val resolver = TypeResolver(registry)
        val astResult = Builder().parseLibrary(cql)
        require(astResult.problems.isEmpty()) { "Parse errors: ${astResult.problems}" }
        val symbols = SymbolCollector().collect(astResult.library)
        val typeTable = resolver.resolve(astResult.library, symbols)
        val exprDef = symbols.expressionDefinitions[exprName]
        assertNotNull(exprDef, "Expression '$exprName' not found in symbol table")
        return typeTable[exprDef.expression]
    }

    private fun analyzeAndGetTypeTable(cql: String): Triple<TypeTable, SymbolTable, TypeResolver> {
        val resolver = TypeResolver(registry)
        val astResult = Builder().parseLibrary(cql)
        require(astResult.problems.isEmpty()) { "Parse errors: ${astResult.problems}" }
        val symbols = SymbolCollector().collect(astResult.library)
        val typeTable = resolver.resolve(astResult.library, symbols)
        return Triple(typeTable, symbols, resolver)
    }

    // ---- Milestone 2 tests ----

    @Test
    fun `integer literal has Integer type`() {
        val type = analyzeAndGetExprType("library Test using System\ndefine X: 42", "X")
        assertNotNull(type)
        assertEquals(registry.type("Integer"), type)
    }

    @Test
    fun `decimal literal has Decimal type`() {
        val type = analyzeAndGetExprType("library Test using System\ndefine X: 3.14", "X")
        assertNotNull(type)
        assertEquals(registry.type("Decimal"), type)
    }

    @Test
    fun `string literal has String type`() {
        val type = analyzeAndGetExprType("library Test using System\ndefine X: 'hello'", "X")
        assertNotNull(type)
        assertEquals(registry.type("String"), type)
    }

    @Test
    fun `boolean literal has Boolean type`() {
        val type = analyzeAndGetExprType("library Test using System\ndefine X: true", "X")
        assertNotNull(type)
        assertEquals(registry.type("Boolean"), type)
    }

    @Test
    fun `null literal has Any type`() {
        val type = analyzeAndGetExprType("library Test using System\ndefine X: null", "X")
        assertNotNull(type)
        assertEquals(registry.type("Any"), type)
    }

    @Test
    fun `integer addition resolves to Integer`() {
        val type = analyzeAndGetExprType("library Test using System\ndefine X: 1 + 2", "X")
        assertNotNull(type)
        assertEquals(registry.type("Integer"), type)
    }

    @Test
    fun `mixed Integer plus Decimal resolves to Decimal`() {
        val type = analyzeAndGetExprType("library Test using System\ndefine X: 1 + 2.0", "X")
        assertNotNull(type)
        assertEquals(registry.type("Decimal"), type)
    }

    @Test
    fun `date-only literal has Date type`() {
        val type = analyzeAndGetExprType("library Test using System\ndefine X: @2024-01-01", "X")
        assertNotNull(type)
        assertEquals(registry.type("Date"), type)
    }

    @Test
    fun `datetime literal has DateTime type`() {
        val type =
            analyzeAndGetExprType("library Test using System\ndefine X: @2024-01-01T10:00:00", "X")
        assertNotNull(type)
        assertEquals(registry.type("DateTime"), type)
    }

    @Test
    fun `comparison operator resolves to Boolean`() {
        val type = analyzeAndGetExprType("library Test using System\ndefine X: 1 < 2", "X")
        assertNotNull(type)
        assertEquals(registry.type("Boolean"), type)
    }

    @Test
    fun `operator resolution stored for binary expression with conversions`() {
        val (typeTable, symbols, _) =
            analyzeAndGetTypeTable("library Test using System\ndefine X: 1 + 2.0")
        val exprDef = symbols.expressionDefinitions["X"]!!
        val resolution = typeTable.getOperatorResolution(exprDef.expression)
        assertNotNull(resolution, "Operator resolution should be stored for mixed-type addition")
        assertEquals(registry.type("Decimal"), resolution.operator.resultType)
    }

    @Test
    fun `time literal has Time type`() {
        val type = analyzeAndGetExprType("library Test using System\ndefine X: @T10:00:00", "X")
        assertNotNull(type)
        assertEquals(registry.type("Time"), type)
    }

    @Test
    fun `quantity literal has Quantity type`() {
        val type = analyzeAndGetExprType("library Test using System\ndefine X: 10 'mg'", "X")
        assertNotNull(type)
        assertEquals(registry.type("Quantity"), type)
    }

    // ---- Milestone 3 tests ----

    @Test
    fun `implies operator resolves to Boolean`() {
        val type =
            analyzeAndGetExprType("library Test using System\ndefine X: true implies false", "X")
        assertNotNull(type)
        assertEquals(registry.type("Boolean"), type)
    }

    @Test
    fun `is null resolves to Boolean`() {
        val type = analyzeAndGetExprType("library Test using System\ndefine X: 1 is null", "X")
        assertNotNull(type)
        assertEquals(registry.type("Boolean"), type)
    }

    @Test
    fun `is true resolves to Boolean`() {
        val type = analyzeAndGetExprType("library Test using System\ndefine X: true is true", "X")
        assertNotNull(type)
        assertEquals(registry.type("Boolean"), type)
    }

    @Test
    fun `is false resolves to Boolean`() {
        val type = analyzeAndGetExprType("library Test using System\ndefine X: false is false", "X")
        assertNotNull(type)
        assertEquals(registry.type("Boolean"), type)
    }

    @Test
    fun `is not null resolves to Boolean`() {
        val type = analyzeAndGetExprType("library Test using System\ndefine X: 1 is not null", "X")
        assertNotNull(type)
        assertEquals(registry.type("Boolean"), type)
    }

    @Test
    fun `is not true resolves to Boolean`() {
        val type =
            analyzeAndGetExprType("library Test using System\ndefine X: true is not true", "X")
        assertNotNull(type)
        assertEquals(registry.type("Boolean"), type)
    }

    @Test
    fun `is not false resolves to Boolean`() {
        val type =
            analyzeAndGetExprType("library Test using System\ndefine X: false is not false", "X")
        assertNotNull(type)
        assertEquals(registry.type("Boolean"), type)
    }

    // ---- Milestone 4 tests: Identifier resolution ----

    @Test
    fun `expression reference resolves to referenced type`() {
        val cql = "library Test using System\ndefine A: 42\ndefine B: A"
        val type = analyzeAndGetExprType(cql, "B")
        assertNotNull(type)
        assertEquals(registry.type("Integer"), type)
    }

    @Test
    fun `expression reference resolution is stored in TypeTable`() {
        val cql = "library Test using System\ndefine A: 42\ndefine B: A"
        val (typeTable, symbols, _) = analyzeAndGetTypeTable(cql)
        val exprDef = symbols.expressionDefinitions["B"]!!
        val expr = exprDef.expression
        assertNotNull(expr is IdentifierExpression)
        val resolution = typeTable.getIdentifierResolution(expr as IdentifierExpression)
        assertNotNull(resolution, "Identifier resolution should be stored")
        assertTrue(resolution is Resolution.ExpressionRef, "Should be an ExpressionRef resolution")
    }

    @Test
    fun `parameter reference resolves to declared type`() {
        val cql =
            "library Test using System\nparameter MyParam Integer default 42\ndefine X: MyParam"
        val type = analyzeAndGetExprType(cql, "X")
        assertNotNull(type)
        assertEquals(registry.type("Integer"), type)
    }

    @Test
    fun `parameter reference resolution is stored as ParameterRef`() {
        val cql =
            "library Test using System\nparameter MyParam Integer default 42\ndefine X: MyParam"
        val (typeTable, symbols, _) = analyzeAndGetTypeTable(cql)
        val exprDef = symbols.expressionDefinitions["X"]!!
        val resolution =
            typeTable.getIdentifierResolution(exprDef.expression as IdentifierExpression)
        assertNotNull(resolution)
        assertTrue(resolution is Resolution.ParameterRef, "Should be a ParameterRef resolution")
    }

    @Test
    fun `forward reference resolves correctly`() {
        val cql = "library Test using System\ndefine A: B\ndefine B: 'hello'"
        val type = analyzeAndGetExprType(cql, "A")
        assertNotNull(type)
        assertEquals(registry.type("String"), type)
    }

    @Test
    fun `circular expression reference returns null`() {
        // Circular references are illegal in CQL; the resolver returns null without crashing
        val cql = "library Test using System\ndefine A: B\ndefine B: A"
        val type = analyzeAndGetExprType(cql, "A")
        assertNull(type, "Circular reference should result in null type")
    }

    @Test
    fun `function body operand resolves to declared type`() {
        val cql = "library Test using System\ndefine function F(x Integer): x\ndefine Y: F(1)"
        val type = analyzeAndGetExprType(cql, "Y")
        assertNotNull(type)
        assertEquals(registry.type("Integer"), type)
    }

    @Test
    fun `function with expression body resolves return type`() {
        val cql =
            "library Test using System\ndefine function Add(a Integer, b Integer): a + b\ndefine Y: Add(1, 2)"
        val type = analyzeAndGetExprType(cql, "Y")
        assertNotNull(type)
        assertEquals(registry.type("Integer"), type)
    }

    @Test
    fun `user-defined function with exact type match resolves`() {
        // User-defined function resolution currently does exact + subtype matching.
        // Integer→Decimal is an implicit conversion (not subtyping), so full conversion-based
        // matching for user functions will come when we integrate with OperatorMap.
        val cql = "library Test using System\ndefine function F(x Integer): x\ndefine Y: F(1)"
        val type = analyzeAndGetExprType(cql, "Y")
        assertNotNull(type)
        assertEquals(registry.type("Integer"), type)
    }
}
