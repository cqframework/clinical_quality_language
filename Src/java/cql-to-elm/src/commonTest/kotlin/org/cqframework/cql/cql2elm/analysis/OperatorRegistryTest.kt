package org.cqframework.cql.cql2elm.analysis

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import org.hl7.cql.model.DataType
import org.hl7.cql.model.ListType

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

    // --- Generic instantiation with Any (null-guard) ---

    @Test
    fun `In Any ListInteger resolves to In Any ListAny`() {
        // In(T, List<T>) with (Any, List<Integer>): T binds to Any from arg0 (first binding),
        // arg1 List<Integer> compatible with List<Any>. Null-guard has no effect on first binding.
        val anyType = DataType.ANY
        val intType = registry.type("Integer")
        val listInt = ListType(intType)
        val resolution = registry.resolve("In", listOf(anyType, listInt))
        assertNotNull(resolution, "In(Any, List<Integer>) should resolve")
        assertEquals(anyType, resolution.operator.signature.operandTypes[0])
        assertEquals(ListType(anyType), resolution.operator.signature.operandTypes[1])
    }

    @Test
    fun `Contains ListInteger Any resolves with null-guard preventing widening`() {
        // Contains(List<T>, T) with (List<Integer>, Any)
        // Left-to-right: List<T> vs List<Integer> → T=Integer. Then T vs Any:
        // Without null-guard: Any.isSuperTypeOf(Integer)=false,
        // callType(Any).isSuperTypeOf(bound(Integer))=true → rebind T=Any
        // With null-guard: callType==Any, T already bound → return true, T stays Integer
        val anyType = DataType.ANY
        val intType = registry.type("Integer")
        val listInt = ListType(intType)
        val resolution = registry.resolve("Contains", listOf(listInt, anyType))
        assertNotNull(resolution, "Contains(List<Integer>, Any) should resolve")
        // With null-guard: T=Integer, operator is Contains(List<Integer>, Integer)
        assertEquals(listInt, resolution.operator.signature.operandTypes[0])
        assertEquals(intType, resolution.operator.signature.operandTypes[1])
        // Cast conversion at arg1 (Any → Integer)
        assertTrue(resolution.hasConversions(), "Should have conversions for Any→Integer cast")
        val conv1 = resolution.conversions[1]
        assertNotNull(conv1, "Conversion at position 1 should not be null")
        assertTrue(conv1.isCast, "Conversion should be a cast")
        assertEquals(intType, conv1.toType)
    }

    @Test
    fun `IndexOf ListInteger Any resolves with null-guard preventing widening`() {
        // IndexOf(List<T>, T) with (List<Integer>, Any)
        // Same pattern as Contains: T binds to Integer from arg0, null-guard prevents widening
        val anyType = DataType.ANY
        val intType = registry.type("Integer")
        val listInt = ListType(intType)
        val resolution = registry.resolve("IndexOf", listOf(listInt, anyType))
        assertNotNull(resolution, "IndexOf(List<Integer>, Any) should resolve")
        assertEquals(listInt, resolution.operator.signature.operandTypes[0])
        assertEquals(intType, resolution.operator.signature.operandTypes[1])
        assertTrue(resolution.hasConversions(), "Should have conversions for Any→Integer cast")
        val conv1 = resolution.conversions[1]
        assertNotNull(conv1, "Conversion at position 1 should not be null")
        assertTrue(conv1.isCast, "Conversion should be a cast")
        assertEquals(intType, conv1.toType)
    }
}
