package org.opencds.cqf.cql.engine.runtime;

import java.math.BigDecimal;



import org.opencds.cqf.cql.engine.elm.execution.EqualEvaluator;
import org.opencds.cqf.cql.engine.elm.execution.EquivalentEvaluator;

public class Quantity implements CqlType, Comparable<Quantity> {

    private final String DEFAULT_UNIT = "1";

    public Quantity() {
        this.value = new BigDecimal("0.0");
        this.unit = DEFAULT_UNIT;
    }

    private BigDecimal value;
    public BigDecimal getValue() {
        return value;
    }
    public void setValue(BigDecimal value) {
        this.value = value;
    }
    public Quantity withValue(BigDecimal value) {
        setValue(value);
        return this;
    }

    private String unit;
    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
    public Quantity withUnit(String unit) {
        setUnit(unit);
        return this;
    }
    public Quantity withDefaultUnit() {
        setUnit(DEFAULT_UNIT);
        return this;
    }

    public boolean isDefaultUnit(String unit) {
        return unit == null || unit.equals("") || unit.equals(DEFAULT_UNIT);
    }

    public boolean unitsEqual(String leftUnit, String rightUnit) {
        if (isDefaultUnit(leftUnit) && isDefaultUnit(rightUnit)) {
            return true;
        }

        if (isDefaultUnit(leftUnit)) {
            return false;
        }

        switch (leftUnit) {
            case "year":
            case "years": return "year".equals(rightUnit) || "years".equals(rightUnit);
            case "month":
            case "months": return "month".equals(rightUnit) || "months".equals(rightUnit);
            case "week":
            case "weeks":
            case "wk": return "week".equals(rightUnit) || "weeks".equals(rightUnit) || "wk".equals(rightUnit);
            case "day":
            case "days":
            case "d": return "day".equals(rightUnit) || "days".equals(rightUnit) || "d".equals(rightUnit);
            case "hour":
            case "hours":
            case "h": return "hour".equals(rightUnit) || "hours".equals(rightUnit) || "h".equals(rightUnit);
            case "minute":
            case "minutes":
            case "min": return "minute".equals(rightUnit) || "minutes".equals(rightUnit) || "min".equals(rightUnit);
            case "second":
            case "seconds":
            case "s": return "second".equals(rightUnit) || "seconds".equals(rightUnit) || "s".equals(rightUnit);
            case "millisecond":
            case "milliseconds":
            case "ms": return "millisecond".equals(rightUnit) || "milliseconds".equals(rightUnit) || "ms".equals(rightUnit);
            default: return leftUnit.equals(rightUnit);
        }
    }

    public boolean unitsEquivalent(String leftUnit, String rightUnit) {
        if (isDefaultUnit(leftUnit) && isDefaultUnit(rightUnit)) {
            return true;
        }

        if (isDefaultUnit(leftUnit)) {
            return false;
        }

        switch (leftUnit) {
            case "year":
            case "years":
            case "a": return "year".equals(rightUnit) || "years".equals(rightUnit) || "a".equals(rightUnit);
            case "month":
            case "months":
            case "mo": return "month".equals(rightUnit) || "months".equals(rightUnit) || "mo".equals(rightUnit);
            case "week":
            case "weeks":
            case "wk": return "week".equals(rightUnit) || "weeks".equals(rightUnit) || "wk".equals(rightUnit);
            case "day":
            case "days":
            case "d": return "day".equals(rightUnit) || "days".equals(rightUnit) || "d".equals(rightUnit);
            case "hour":
            case "hours":
            case "h": return "hour".equals(rightUnit) || "hours".equals(rightUnit) || "h".equals(rightUnit);
            case "minute":
            case "minutes":
            case "min": return "minute".equals(rightUnit) || "minutes".equals(rightUnit) || "min".equals(rightUnit);
            case "second":
            case "seconds":
            case "s": return "second".equals(rightUnit) || "seconds".equals(rightUnit) || "s".equals(rightUnit);
            case "millisecond":
            case "milliseconds":
            case "ms": return "millisecond".equals(rightUnit) || "milliseconds".equals(rightUnit) || "ms".equals(rightUnit);
            default: return leftUnit.equals(rightUnit);
        }
    }

    @Override
    public int compareTo(Quantity other) {
        if (unitsEqual(this.getUnit(), other.getUnit())) {
            return this.getValue().compareTo(other.getValue());
        }
        return -1;
    }

    public Integer nullableCompareTo(Quantity other) {
        if (unitsEqual(this.getUnit(), other.getUnit())) {
            return this.getValue().compareTo(other.getValue());
        }
        return null;
    }

    @Override
    public Boolean equivalent(Object other) {
        if (unitsEquivalent(this.getUnit(), ((Quantity)other).getUnit())) {
            return EquivalentEvaluator.equivalent(this.getValue(), ((Quantity)other).getValue());
        }
        return false;
    }

    @Override
    public Boolean equal(Object other) {
        if (unitsEqual(this.getUnit(), ((Quantity)other).getUnit())) {
            return EqualEvaluator.equal(this.getValue(), ((Quantity)other).getValue());
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("%s '%s'", getValue(), getUnit());
    }
}
