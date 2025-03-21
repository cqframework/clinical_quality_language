package org.opencds.cqf.cql.engine.elm.executing;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.xml.namespace.QName;
import org.hl7.elm.r1.*;
import org.junit.jupiter.api.Test;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.opencds.cqf.cql.engine.execution.Environment;
import org.opencds.cqf.cql.engine.execution.State;

class FunctionRefEvaluatorTest {
    @Test
    void pickFunctionDef() {
        var env = new Environment(null);
        var state = new State(env);
        state.init(new Library().withIdentifier(new VersionedIdentifier().withId("lib")));

        var cqlException = assertThrows(
                CqlException.class,
                () -> FunctionRefEvaluator.pickFunctionDef(state, "func", List.of(1, 2, 3), List.of(), List.of()));
        assertEquals(
                "Could not resolve call to operator 'func(java.lang.Integer, java.lang.Integer, java.lang.Integer)' in library 'lib'.",
                cqlException.getMessage());
    }

    @Test
    void functionDefOperandsSignatureEqual() {
        var functionDefWithOneOperand = new FunctionDef().withOperand(Collections.singletonList(new OperandDef()));
        List<TypeSpecifier> signatureWithTwoOperands = List.of(new NamedTypeSpecifier(), new NamedTypeSpecifier());

        assertFalse(FunctionRefEvaluator.functionDefOperandsSignatureEqual(
                functionDefWithOneOperand, signatureWithTwoOperands));
    }

    @Test
    void operandDefTypeSpecifierEqual() {
        var integerTypeName = new QName("urn:hl7-org:elm-types:r1", "Integer");
        var integerNamedTypeSpecifier = new NamedTypeSpecifier().withName(integerTypeName);
        var listTypeSpecifier = new ListTypeSpecifier().withElementType(integerNamedTypeSpecifier);

        var listOperandDef = new OperandDef().withOperandTypeSpecifier(listTypeSpecifier);
        var integerOperandDef = new OperandDef().withOperandType(integerTypeName);

        assertTrue(FunctionRefEvaluator.operandDefTypeSpecifierEqual(listOperandDef, listTypeSpecifier));
        assertTrue(FunctionRefEvaluator.operandDefTypeSpecifierEqual(integerOperandDef, integerNamedTypeSpecifier));
        assertFalse(FunctionRefEvaluator.operandDefTypeSpecifierEqual(integerOperandDef, null));
    }

    @Test
    void typesToString() {
        var env = new Environment(null);
        var state = new State(env);

        var actual = FunctionRefEvaluator.typesToString(state, List.of("a", "b", "c"));
        assertEquals("java.lang.String, java.lang.String, java.lang.String", actual);

        actual = FunctionRefEvaluator.typesToString(state, Arrays.asList(1, 2, null));
        assertEquals("java.lang.Integer, java.lang.Integer, null", actual);

        actual = FunctionRefEvaluator.typesToString(state, null);
        assertEquals("", actual);
    }
}
