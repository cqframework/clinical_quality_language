package org.hl7.cql.model;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

public class ChoiceTypeTests {

    @Test
    public void testChoiceTypeIsSuperType() {
        ChoiceType first = new ChoiceType(
                Arrays.asList(new SimpleType("Period"), 
                new SimpleType("Interval"), 
                new SimpleType("DateTime")));

        ChoiceType second = new ChoiceType(
            Arrays.asList(new SimpleType("Period"), new SimpleType("DateTime")));

        assertTrue(first.isSuperTypeOf(second));
        assertFalse(second.isSuperTypeOf(first));
    }

    @Test
    public void testChoiceTypeIsSubType() {
        ChoiceType first = new ChoiceType(
                Arrays.asList(new SimpleType("Period"), 
                new SimpleType("Interval"), 
                new SimpleType("DateTime")));

        ChoiceType second = new ChoiceType(
            Arrays.asList(new SimpleType("Period"), new SimpleType("DateTime")));

        assertFalse(first.isSubTypeOf(second));
        assertTrue(second.isSubTypeOf(first));
    }

    @Test
    public void testChoiceTypeIsCompatible() {
        ChoiceType first = new ChoiceType(
                Arrays.asList(new SimpleType("Period"), 
                new SimpleType("Interval"), 
                new SimpleType("DateTime")));

        ChoiceType second = new ChoiceType(
            Arrays.asList(new SimpleType("Period"), new SimpleType("DateTime")));

        assertTrue(first.isCompatibleWith(second));
        assertFalse(second.isCompatibleWith(first));
    }
}
