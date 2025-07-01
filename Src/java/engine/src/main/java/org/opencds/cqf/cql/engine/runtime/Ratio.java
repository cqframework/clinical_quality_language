package org.opencds.cqf.cql.engine.runtime;

import org.opencds.cqf.cql.engine.elm.executing.DivideEvaluator;
import org.opencds.cqf.cql.engine.elm.executing.EqualEvaluator;
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;

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
     * Calculates the value of this ratio by dividing the numerator by the denominator.
     *
     * @return The resulting `Quantity` after division.
     */
    public Quantity divide() {
        return (Quantity) DivideEvaluator.divide(this.numerator, this.denominator, null);
    }

    /**
     * For ratios, equivalent means that the numerator and denominator represent the same ratio (e.g. 1:100 ~ 10:1000).
     */
    @Override
    public Boolean equivalent(Object other) {
        return EquivalentEvaluator.equivalent(this.divide(), ((Ratio) other).divide());
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
