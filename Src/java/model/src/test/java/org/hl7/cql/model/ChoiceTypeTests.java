package org.hl7.cql.model;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

public class ChoiceTypeTests {

    @Test
    public void testChoiceTypeIsCompatible() {
        ChoiceType first = new ChoiceType(
                Arrays.asList(new SimpleType("Period"), 
                new SimpleType("Interval"), 
                new SimpleType("DateTime")));

        ChoiceType second = new ChoiceType(
            Arrays.asList(new SimpleType("Period"), new SimpleType("DateTime")));

        assertTrue(first.isCompatibleWith(second));
        assertTrue(second.isCompatibleWith(first));

        assertTrue(first.isSuperSetOf(second));
        assertFalse(first.isSubSetOf(second));
        assertTrue(second.isSubSetOf(first));
        assertFalse(second.isSuperSetOf(first));
    }

    @Test
    public void testChoiceTypeIsNotCompatible() {
        ChoiceType first = new ChoiceType(
                Arrays.asList(new SimpleType("Period"),
                        new SimpleType("Interval"),
                        new SimpleType("DateTime")));

        ChoiceType second = new ChoiceType(
                Arrays.asList(new SimpleType("Integer"), new SimpleType("String")));

        assertFalse(first.isCompatibleWith(second));
        assertFalse(second.isCompatibleWith(first));
        assertFalse(first.isSubSetOf(second));
        assertFalse(first.isSuperSetOf(second));
    }
}
