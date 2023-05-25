package org.opencds.cqf.cql.engine.elm.visiting;

import org.hl7.elm.r1.Ratio;
import org.opencds.cqf.cql.engine.execution.CqlEngine;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.runtime.Quantity;

/*

    structured type Ratio
    {
      numerator Quantity
      denominator Quantity
    }

    The Ratio type represents a relationship between two quantities, such as a titre (e.g. 1:128), or a concentration
        (e.g. 5 'mg':10’mL'). The numerator and denominator elements must be present (i.e. can not be null).

*/

public class RatioEvaluator {

    public static Object internalEvaluate(Ratio elm, State state, CqlEngine visitor) {
        Quantity numerator = (Quantity) visitor.visitExpression(elm.getNumerator(), state);
        Quantity denominator = (Quantity) visitor.visitExpression(elm.getDenominator(), state);

        return new org.opencds.cqf.cql.engine.runtime.Ratio().setNumerator(numerator).setDenominator(denominator);
    }
}
