package org.opencds.cqf.cql.engine.runtime;

import org.opencds.cqf.cql.engine.elm.executing.EqualEvaluator;
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.elm.executing.MultiplyEvaluator;

public class Ratio implements CqlType {

    private Quantity numerator;
    private Quantity denominator;

    public Quantity getNumerator() {
        return numerator;
    }

    public Ratio setNumerator(Quantity numerator) {
        this.numerator = numerator;
        return this;
    }

    public Quantity getDenominator() {
        return denominator;
    }

    public Ratio setDenominator(Quantity denominator) {
        this.denominator = denominator;
        return this;
    }

    /**
     * For ratios, equivalent means that the numerator and denominator represent the same ratio (e.g. 1:100 ~ 10:1000).
     */
    @Override
    public Boolean equivalent(Object other) {
        var otherRatio = (Ratio) other;
        return EquivalentEvaluator.equivalent(
                MultiplyEvaluator.multiply(this.getNumerator(), otherRatio.getDenominator()),
                MultiplyEvaluator.multiply(otherRatio.getNumerator(), this.getDenominator()));
    }

    @Override
    public Boolean equal(Object other) {
        return EqualEvaluator.equal(this.getNumerator(), ((Ratio) other).getNumerator())
                && EqualEvaluator.equal(this.getDenominator(), ((Ratio) other).getDenominator());
    }

    @Override
    public String toString() {
        return String.format("%s:%s", this.numerator.toString(), this.denominator.toString());
    }
}
