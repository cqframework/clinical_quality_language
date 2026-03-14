package org.cqframework.cql.cql2elm.analysis

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OperatorRegistryTest {

    private val registry = OperatorRegistry.createSystemRegistry()

    @Test
    fun `Add Integer Integer resolves to Integer`() {
        val intType = registry.type("Integer")
        val resolution = registry.resolve("Add", listOf(intType, intType))
        assertNotNull(resolution)
        assertEquals(intType, resolution.operator.resultType)
    }

    @Test
    fun `Add Integer Decimal resolves to Decimal with Integer to Decimal conversion`() {
        val intType = registry.type("Integer")
        val decType = registry.type("Decimal")
        val resolution = registry.resolve("Add", listOf(intType, decType))
        assertNotNull(resolution)
        assertEquals(decType, resolution.operator.resultType)
        assertTrue(resolution.hasConversions())
        // First operand (Integer) should have a ToDecimal conversion
        val conv = resolution.conversions[0]
        assertNotNull(conv)
        assertEquals("ToDecimal", registry.conversionOperatorName(conv))
    }

    @Test
    fun `Equal Integer Decimal resolves to Boolean with conversion`() {
        val intType = registry.type("Integer")
        val decType = registry.type("Decimal")
        val boolType = registry.type("Boolean")
        val resolution = registry.resolve("Equal", listOf(intType, decType))
        assertNotNull(resolution)
        assertEquals(boolType, resolution.operator.resultType)
        assertTrue(resolution.hasConversions())
    }

    @Test
    fun `Negate Integer resolves to Integer`() {
        val intType = registry.type("Integer")
        val resolution = registry.resolve("Negate", listOf(intType))
        assertNotNull(resolution)
        assertEquals(intType, resolution.operator.resultType)
    }

    @Test
    fun `Unknown operator returns null`() {
        val intType = registry.type("Integer")
        val resolution = registry.resolve("NoSuchOperator", listOf(intType, intType))
        assertNull(resolution)
    }

    @Test
    fun `Add Decimal Decimal resolves to Decimal`() {
        val decType = registry.type("Decimal")
        val resolution = registry.resolve("Add", listOf(decType, decType))
        assertNotNull(resolution)
        assertEquals(decType, resolution.operator.resultType)
    }

    @Test
    fun `And Boolean Boolean resolves to Boolean`() {
        val boolType = registry.type("Boolean")
        val resolution = registry.resolve("And", listOf(boolType, boolType))
        assertNotNull(resolution)
        assertEquals(boolType, resolution.operator.resultType)
    }

    @Test
    fun `Add Integer Long resolves to Long with Integer to Long conversion`() {
        val intType = registry.type("Integer")
        val longType = registry.type("Long")
        val resolution = registry.resolve("Add", listOf(intType, longType))
        assertNotNull(resolution)
        assertEquals(longType, resolution.operator.resultType)
        assertTrue(resolution.hasConversions())
        val conv = resolution.conversions[0]
        assertNotNull(conv)
        assertEquals("ToLong", registry.conversionOperatorName(conv))
    }

    @Test
    fun `Concatenate String String resolves to String`() {
        val strType = registry.type("String")
        val resolution = registry.resolve("Concatenate", listOf(strType, strType))
        assertNotNull(resolution)
        assertEquals(strType, resolution.operator.resultType)
    }

    @Test
    fun `Less Integer Integer resolves to Boolean`() {
        val intType = registry.type("Integer")
        val boolType = registry.type("Boolean")
        val resolution = registry.resolve("Less", listOf(intType, intType))
        assertNotNull(resolution)
        assertEquals(boolType, resolution.operator.resultType)
    }
}
