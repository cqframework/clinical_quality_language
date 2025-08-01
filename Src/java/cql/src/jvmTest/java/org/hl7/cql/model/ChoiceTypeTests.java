package org.hl7.cql.model;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChoiceTypeTests {

    @Test
    void choiceTypeIsCompatible() {
        ChoiceType first = new ChoiceType(
                Set.of(new SimpleType("Period"), new SimpleType("Interval"), new SimpleType("DateTime")));

        ChoiceType second = new ChoiceType(Set.of(new SimpleType("Period"), new SimpleType("DateTime")));

        assertTrue(first.isCompatibleWith(second));
        assertTrue(second.isCompatibleWith(first));

        assertTrue(first.isSuperSetOf(second));
        assertFalse(first.isSubSetOf(second));
        assertTrue(second.isSubSetOf(first));
        assertFalse(second.isSuperSetOf(first));
    }

    @Test
    void choiceTypeIsNotCompatible() {
        ChoiceType first = new ChoiceType(
                Set.of(new SimpleType("Period"), new SimpleType("Interval"), new SimpleType("DateTime")));

        ChoiceType second = new ChoiceType(Set.of(new SimpleType("Integer"), new SimpleType("String")));

        assertFalse(first.isCompatibleWith(second));
        assertFalse(second.isCompatibleWith(first));
        assertFalse(first.isSubSetOf(second));
        assertFalse(first.isSuperSetOf(second));
    }
}