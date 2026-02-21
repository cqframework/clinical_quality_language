package org.opencds.cqf.cql.engine.execution

import javax.xml.namespace.QName
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import org.hl7.elm.r1.NamedTypeSpecifier
import org.opencds.cqf.cql.engine.data.StaticFunction
import org.opencds.cqf.cql.engine.data.SystemExternalFunctionProvider
import org.opencds.cqf.cql.engine.exception.CqlException

private val integerOperandType =
    NamedTypeSpecifier().apply { name = QName("urn:hl7-org:elm-types:r1", "Integer") }

object ExternalFunctions {
    @JvmStatic @Suppress("FunctionOnlyReturningConstant") fun externalFunc() = 5
}

class FunctionEvaluationApiTest : CqlTestBase() {

    @Test
    fun evaluateSimpleFunctionOnce() {
        val evaluationFunctionRef = EvaluationFunctionRef("func", null, listOf(1, 2))
        val results =
            engine
                .evaluate {
                    library("FunctionEvaluationApiTest") { expressions(evaluationFunctionRef) }
                }
                .onlyResultOrThrow
        assertEquals(3, results[evaluationFunctionRef]!!.value)
    }

    @Test
    fun evaluateSameFunctionMultipleTimesWithDifferentArgs() {
        val evaluationFunctionRef1 = EvaluationFunctionRef("func", null, listOf(1, 2))
        val evaluationFunctionRef2 = EvaluationFunctionRef("func", null, listOf(3, 4))
        val results =
            engine
                .evaluate {
                    library("FunctionEvaluationApiTest") {
                        expressions(evaluationFunctionRef1, evaluationFunctionRef2)
                    }
                }
                .onlyResultOrThrow
        assertEquals(3, results[evaluationFunctionRef1]!!.value)
        assertEquals(7, results[evaluationFunctionRef2]!!.value)
    }

    @Test
    fun evaluateFunctionAndExpression() {
        val evaluationFunctionRef = EvaluationFunctionRef("func", null, listOf(1, 2))
        val evaluationExpressionRef = EvaluationExpressionRef("expr")
        val results =
            engine
                .evaluate {
                    library("FunctionEvaluationApiTest") {
                        expressions(evaluationFunctionRef, evaluationExpressionRef)
                    }
                }
                .onlyResultOrThrow
        assertEquals(3, results[evaluationFunctionRef]!!.value)
        assertEquals(5, results[evaluationExpressionRef]!!.value)
    }

    @Test
    fun throwsWhenFunctionNotFound() {
        val evaluationFunctionRef = EvaluationFunctionRef("nonexistentFunc", null, listOf())
        assertFailsWith<CqlException> {
            engine
                .evaluate {
                    library("FunctionEvaluationApiTest") { expressions(evaluationFunctionRef) }
                }
                .onlyResultOrThrow
        }
    }

    @Test
    fun evaluateFunctionWithOverloads() {
        val evaluationFunctionRef =
            EvaluationFunctionRef(
                "funcWithOverloads",
                listOf(integerOperandType, integerOperandType),
                listOf(1, 2),
            )
        val results =
            engine
                .evaluate {
                    library("FunctionEvaluationApiTest") { expressions(evaluationFunctionRef) }
                }
                .onlyResultOrThrow
        assertEquals(3, results[evaluationFunctionRef]!!.value)
    }

    @Test
    fun throwsWhenOverloadNotFound() {
        val evaluationFunctionRef = EvaluationFunctionRef("funcWithOverloads", listOf(), listOf())
        assertFailsWith<CqlException> {
            engine
                .evaluate {
                    library("FunctionEvaluationApiTest") { expressions(evaluationFunctionRef) }
                }
                .onlyResultOrThrow
        }
    }

    @Test
    fun throwsWhenFunctionHasOverloadsButSignatureNotProvided() {
        val evaluationFunctionRef = EvaluationFunctionRef("funcWithOverloads", null, listOf(1, 2))
        assertFailsWith<CqlException> {
            engine
                .evaluate {
                    library("FunctionEvaluationApiTest") { expressions(evaluationFunctionRef) }
                }
                .onlyResultOrThrow
        }
    }

    @Test
    fun evaluateFluentFunction() {
        val evaluationFunctionRef = EvaluationFunctionRef("fluentFunc", null, listOf(1))
        val results =
            engine
                .evaluate {
                    library("FunctionEvaluationApiTest") { expressions(evaluationFunctionRef) }
                }
                .onlyResultOrThrow
        assertEquals(2, results[evaluationFunctionRef]!!.value)
    }

    @Test
    fun evaluateExternalFunction() {
        engine.state.environment.registerExternalFunctionProvider(
            toElmIdentifier("FunctionEvaluationApiTest"),
            SystemExternalFunctionProvider(
                ExternalFunctions.javaClass.declaredMethods.map {
                    StaticFunction(
                        it.name,
                        { arguments -> it.invoke(it.declaringClass, *arguments!!.toTypedArray()) },
                    )
                }
            ),
        )

        val evaluationFunctionRef = EvaluationFunctionRef("externalFunc", null, listOf())
        val results =
            engine
                .evaluate {
                    library("FunctionEvaluationApiTest") { expressions(evaluationFunctionRef) }
                }
                .onlyResultOrThrow
        assertEquals(5, results[evaluationFunctionRef]!!.value)
    }
}
