package org.cqframework.cql.poc.translator.expressions;

/**
 * Created by bobd on 7/24/14.
 */
public class QuantityLiteral extends Expression{

    String unit;
    Number quantity;

    public QuantityLiteral(Number value, String unit){
        this.quantity=value;
        this.unit = unit;
    }

    public QuantityLiteral(String value, String unit){
        this.quantity=Double.parseDouble(value);
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Number getQuantity() {
        return quantity;
    }

    public void setQuantity(Number quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toCql() {
        return (unit==null)? quantity.toString() : unit.toString()+" "+unit;
    }
}
