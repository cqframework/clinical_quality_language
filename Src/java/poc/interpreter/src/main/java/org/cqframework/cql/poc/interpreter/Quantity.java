package org.cqframework.cql.poc.interpreter;

public class Quantity {
    private final Number quantity;
    private final String unit;

    public Quantity(Number quantity) {
        this(quantity, null);
    }

    public Quantity(Number quantity, String unit) {
        this.quantity = quantity;
        this.unit = unit;
    }

    public Number getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }
}
