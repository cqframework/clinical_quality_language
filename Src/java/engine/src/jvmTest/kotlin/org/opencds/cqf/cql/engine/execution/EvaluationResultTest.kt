package org.opencds.cqf.cql.engine.execution

import kotlin.test.Test
import kotlin.test.assertEquals
import org.opencds.cqf.cql.engine.runtime.toCqlInteger

class EvaluationResultTest {

    @Test
    fun getterAndSetterTest() {
        val evaluationResult = EvaluationResult()
        val expressionRef = EvaluationExpressionRef("expr1")
        val expressionResult = ExpressionResult(5.toCqlInteger(), null)

        // Test setting and getting by reference
        evaluationResult[expressionRef] = expressionResult
        assertEquals(expressionResult, evaluationResult[expressionRef])
    }
}
