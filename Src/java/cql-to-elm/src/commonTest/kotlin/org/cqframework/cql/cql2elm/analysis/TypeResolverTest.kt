package org.cqframework.cql.cql2elm.analysis

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import org.hl7.cql.ast.Builder

class TypeResolverTest {

    private val registry = OperatorRegistry.createSystemRegistry()
    private val typeResolver = TypeResolver(registry)

    private fun resolveAndGetExprType(cql: String, exprName: String): org.hl7.cql.model.DataType? {
        val astResult = Builder().parseLibrary(cql)
        require(astResult.problems.isEmpty()) { "Parse errors: ${astResult.problems}" }
        val symbols = SymbolCollector().collect(astResult.library)
        val typeTable = typeResolver.resolve(astResult.library, symbols)
        val exprDef = symbols.expressionDefinitions[exprName]
        assertNotNull(exprDef, "Expression '$exprName' not found in symbol table")
        return typeTable[exprDef.expression]
    }

    @Test
    fun `integer literal has Integer type`() {
        val type = resolveAndGetExprType("library Test using System\ndefine X: 42", "X")
        assertNotNull(type)
        assertEquals(registry.type("Integer"), type)
    }

    @Test
    fun `decimal literal has Decimal type`() {
        val type = resolveAndGetExprType("library Test using System\ndefine X: 3.14", "X")
        assertNotNull(type)
        assertEquals(registry.type("Decimal"), type)
    }

    @Test
    fun `string literal has String type`() {
        val type = resolveAndGetExprType("library Test using System\ndefine X: 'hello'", "X")
        assertNotNull(type)
        assertEquals(registry.type("String"), type)
    }

    @Test
    fun `boolean literal has Boolean type`() {
        val type = resolveAndGetExprType("library Test using System\ndefine X: true", "X")
        assertNotNull(type)
        assertEquals(registry.type("Boolean"), type)
    }

    @Test
    fun `null literal has null type`() {
        val type = resolveAndGetExprType("library Test using System\ndefine X: null", "X")
        assertNull(type)
    }

    @Test
    fun `integer addition resolves to Integer`() {
        val type = resolveAndGetExprType("library Test using System\ndefine X: 1 + 2", "X")
        assertNotNull(type)
        assertEquals(registry.type("Integer"), type)
    }

    @Test
    fun `mixed Integer plus Decimal resolves to Decimal`() {
        val type = resolveAndGetExprType("library Test using System\ndefine X: 1 + 2.0", "X")
        assertNotNull(type)
        assertEquals(registry.type("Decimal"), type)
    }

    @Test
    fun `date-only literal has Date type`() {
        val type = resolveAndGetExprType("library Test using System\ndefine X: @2024-01-01", "X")
        assertNotNull(type)
        assertEquals(registry.type("Date"), type)
    }

    @Test
    fun `datetime literal has DateTime type`() {
        val type =
            resolveAndGetExprType("library Test using System\ndefine X: @2024-01-01T10:00:00", "X")
        assertNotNull(type)
        assertEquals(registry.type("DateTime"), type)
    }

    @Test
    fun `comparison operator resolves to Boolean`() {
        val type = resolveAndGetExprType("library Test using System\ndefine X: 1 < 2", "X")
        assertNotNull(type)
        assertEquals(registry.type("Boolean"), type)
    }

    @Test
    fun `operator resolution stored for binary expression with conversions`() {
        val cql = "library Test using System\ndefine X: 1 + 2.0"
        val astResult = Builder().parseLibrary(cql)
        require(astResult.problems.isEmpty())
        val symbols = SymbolCollector().collect(astResult.library)
        val typeTable = typeResolver.resolve(astResult.library, symbols)
        val exprDef = symbols.expressionDefinitions["X"]!!
        val resolution = typeTable.getOperatorResolution(exprDef.expression)
        assertNotNull(resolution, "Operator resolution should be stored for mixed-type addition")
        assertEquals(registry.type("Decimal"), resolution.operator.resultType)
    }

    @Test
    fun `time literal has Time type`() {
        val type = resolveAndGetExprType("library Test using System\ndefine X: @T10:00:00", "X")
        assertNotNull(type)
        assertEquals(registry.type("Time"), type)
    }

    @Test
    fun `quantity literal has Quantity type`() {
        val type = resolveAndGetExprType("library Test using System\ndefine X: 10 'mg'", "X")
        assertNotNull(type)
        assertEquals(registry.type("Quantity"), type)
    }
}
