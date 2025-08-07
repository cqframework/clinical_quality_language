package org.opencds.cqf.cql.engine.runtime;

import org.opencds.cqf.cql.engine.elm.executing.EqualEvaluator;
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.elm.executing.MultiplyEvaluator;
import org.opencds.cqf.cql.engine.execution.State;

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
     *
     * Note: This method has limited support for unit conversion. If
     * this or other involve different, non-trivial units, use
     * {@link #fullEquivalent}.
     */
    @Override
    public Boolean equivalent(Object other) {
        return fullEquivalent((Ratio) other, null);
    }

    /**
     * For ratios, equivalent means that the numerator and denominator
     * represent the same ratio, possibly accounting for different but
     * compatible units (e.g. 2 'kg' : 4000 ~ 3 'g' : 6).
     *
     * @param other The other ratio.
     * @param state The state provides the UCUM service in can unit
     *              conversion is necessary.
     */
    public Boolean fullEquivalent(final Ratio other, final State state) {
        return EquivalentEvaluator.equivalent(
                MultiplyEvaluator.multiply(this.getNumerator(), other.getDenominator(), state),
                MultiplyEvaluator.multiply(other.getNumerator(), this.getDenominator(), state),
                state);
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
