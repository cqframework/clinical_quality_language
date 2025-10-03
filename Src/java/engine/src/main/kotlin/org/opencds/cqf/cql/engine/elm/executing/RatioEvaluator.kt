package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.hl7.elm.r1.Ratio
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.Quantity

/*

    structured type Ratio
    {
      numerator Quantity
      denominator Quantity
    }

    The Ratio type represents a relationship between two quantities, such as a titre (e.g. 1:128), or a concentration
        (e.g. 5 'mg':10’mL'). The numerator and denominator elements must be present (i.e. can not be null).

*/
object RatioEvaluator {
    fun internalEvaluate(
        elm: Ratio?,
        state: State?,
        visitor: ElmLibraryVisitor<Any?, State?>,
    ): Any? {
        val numerator = visitor.visitExpression(elm!!.numerator!!, state) as Quantity
        val denominator = visitor.visitExpression(elm.denominator!!, state) as Quantity

        return org.opencds.cqf.cql.engine.runtime
            .Ratio()
            .withNumerator(numerator)
            .withDenominator(denominator)
    }
}
