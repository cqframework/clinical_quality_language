package org.opencds.cqf.cql.engine.elm.executing

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor
import org.opencds.cqf.cql.engine.execution.State
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.Quantity
import org.opencds.cqf.cql.engine.runtime.Ratio

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
        elm: org.hl7.elm.r1.Ratio?,
        state: State?,
        visitor: ElmLibraryVisitor<CqlType?, State?>,
    ): Ratio {
        val numerator = visitor.visitExpression(elm!!.numerator!!, state) as Quantity
        val denominator = visitor.visitExpression(elm.denominator!!, state) as Quantity

        return Ratio().withNumerator(numerator).withDenominator(denominator)
    }
}
