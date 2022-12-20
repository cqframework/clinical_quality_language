package org.opencds.cqf.cql.engine.runtime;

import org.opencds.cqf.cql.engine.elm.execution.EqualEvaluator;
import org.opencds.cqf.cql.engine.elm.execution.EquivalentEvaluator;

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

    @Override
    public Boolean equivalent(Object other) {
        return EquivalentEvaluator.equivalent(this.getNumerator(), ((Ratio) other).getNumerator())
                && EquivalentEvaluator.equivalent(this.getDenominator(), ((Ratio) other).getDenominator());
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
