package org.opencds.cqf.cql.engine.elm.executing

import javax.xml.namespace.QName
import org.hl7.elm.r1.ChoiceTypeSpecifier
import org.hl7.elm.r1.FunctionDef
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.ListTypeSpecifier
import org.hl7.elm.r1.NamedTypeSpecifier
import org.hl7.elm.r1.OperandDef
import org.hl7.elm.r1.VersionedIdentifier
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.execution.Environment
import org.opencds.cqf.cql.engine.execution.State

internal class FunctionRefEvaluatorTest {
    @Test
    fun pickFunctionDef() {
        val env = Environment(null)
        val state = State(env)
        state.init(Library().withIdentifier(VersionedIdentifier().withId("lib")))

        val cqlException =
            Assertions.assertThrows(CqlException::class.java) {
                FunctionRefEvaluator.pickFunctionDef(
                    state,
                    "func",
                    mutableListOf<Any?>(1, 2, 3),
                    listOf(),
                    listOf(),
                )
            }
        Assertions.assertEquals(
            "Could not resolve call to operator 'func(java.lang.Integer, java.lang.Integer, java.lang.Integer)' in library 'lib'.",
            cqlException.message,
        )
    }

    @Test
    fun functionDefOperandsSignatureEqual() {
        val functionDefWithOneOperand = FunctionDef().withOperand(listOf(OperandDef()))
        val signatureWithTwoOperands = listOf(NamedTypeSpecifier(), NamedTypeSpecifier())

        Assertions.assertFalse(
            FunctionRefEvaluator.functionDefOperandsSignatureEqual(
                functionDefWithOneOperand,
                signatureWithTwoOperands,
            )
        )
    }

    @Test
    fun operandDefTypeSpecifierEqual() {
        val integerTypeName = QName("urn:hl7-org:elm-types:r1", "Integer")
        val integerNamedTypeSpecifier = NamedTypeSpecifier().withName(integerTypeName)
        val listTypeSpecifier = ListTypeSpecifier().withElementType(integerNamedTypeSpecifier)

        val listOperandDef = OperandDef().withOperandTypeSpecifier(listTypeSpecifier)
        val integerOperandDef = OperandDef().withOperandType(integerTypeName)

        Assertions.assertTrue(
            FunctionRefEvaluator.operandDefTypeSpecifierEqual(listOperandDef, listTypeSpecifier)
        )
        Assertions.assertTrue(
            FunctionRefEvaluator.operandDefTypeSpecifierEqual(
                integerOperandDef,
                integerNamedTypeSpecifier,
            )
        )
        Assertions.assertFalse(
            FunctionRefEvaluator.operandDefTypeSpecifierEqual(integerOperandDef, null)
        )
    }

    @Test
    fun choiceTypeSpecifierOrderShouldNotAffectSignatureMatch() {
        // Simulates the ChoiceType deterministic serialization issue:
        // The FunctionDef operand has choice types in sorted order (from ChoiceType sorting),
        // but the FunctionRef signature has them in a different order (e.g., from model info).
        // The engine should match them regardless of order.
        val fhirNs = "http://hl7.org/fhir"
        val dateTimeType = NamedTypeSpecifier().withName(QName(fhirNs, "dateTime"))
        val periodType = NamedTypeSpecifier().withName(QName(fhirNs, "Period"))
        val ageType = NamedTypeSpecifier().withName(QName(fhirNs, "Age"))
        val rangeType = NamedTypeSpecifier().withName(QName(fhirNs, "Range"))
        val stringType = NamedTypeSpecifier().withName(QName(fhirNs, "string"))

        // FunctionDef operand: sorted alphabetically (as ChoiceType now produces)
        val sortedChoice =
            ChoiceTypeSpecifier()
                .withChoice(listOf(ageType, dateTimeType, periodType, rangeType, stringType))
        val functionDef =
            FunctionDef()
                .withOperand(listOf(OperandDef().withOperandTypeSpecifier(sortedChoice)))

        // FunctionRef signature: model-info order (dateTime, Age, Period, Range, string)
        val modelOrderChoice =
            ChoiceTypeSpecifier()
                .withChoice(listOf(dateTimeType, ageType, periodType, rangeType, stringType))

        Assertions.assertTrue(
            FunctionRefEvaluator.functionDefOperandsSignatureEqual(
                functionDef,
                listOf(modelOrderChoice),
            ),
            "ChoiceTypeSpecifier matching should be order-independent",
        )
    }

    @Test
    fun typesToString() {
        val env = Environment(null)
        val state = State(env)

        var actual =
            FunctionRefEvaluator.typesToString(state, mutableListOf<String?>("a", "b", "c"))
        Assertions.assertEquals("java.lang.String, java.lang.String, java.lang.String", actual)

        actual = FunctionRefEvaluator.typesToString(state, mutableListOf(1, 2, null))
        Assertions.assertEquals("java.lang.Integer, java.lang.Integer, null", actual)

        actual = FunctionRefEvaluator.typesToString(state, null)
        Assertions.assertEquals("", actual)
    }
}
