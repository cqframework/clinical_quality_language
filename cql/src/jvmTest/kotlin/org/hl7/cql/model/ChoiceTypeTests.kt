package org.hl7.cql.model

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ChoiceTypeTests {
    @Test
    fun choiceTypeIsCompatible() {
        val first =
            ChoiceType(setOf(SimpleType("Period"), SimpleType("Interval"), SimpleType("DateTime")))

        val second = ChoiceType(setOf(SimpleType("Period"), SimpleType("DateTime")))

        Assertions.assertTrue(first.isCompatibleWith(second))
        Assertions.assertTrue(second.isCompatibleWith(first))

        Assertions.assertTrue(first.isSuperSetOf(second))
        Assertions.assertFalse(first.isSubSetOf(second))
        Assertions.assertTrue(second.isSubSetOf(first))
        Assertions.assertFalse(second.isSuperSetOf(first))
    }

    @Test
    fun choiceTypeIsNotCompatible() {
        val first =
            ChoiceType(setOf(SimpleType("Period"), SimpleType("Interval"), SimpleType("DateTime")))

        val second = ChoiceType(setOf(SimpleType("Integer"), SimpleType("String")))

        Assertions.assertFalse(first.isCompatibleWith(second))
        Assertions.assertFalse(second.isCompatibleWith(first))
        Assertions.assertFalse(first.isSubSetOf(second))
        Assertions.assertFalse(first.isSuperSetOf(second))
    }
}
