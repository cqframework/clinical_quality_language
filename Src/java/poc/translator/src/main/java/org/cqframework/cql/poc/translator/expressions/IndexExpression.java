package org.cqframework.cql.poc.translator.expressions;

import java.lang.reflect.Array;
import java.util.List;

/**
 * Created by bobd on 7/23/14.
 */
public class IndexExpression extends Expression{

    Expression indexable;
    Expression property;

    public IndexExpression(Expression indexable, Expression property) {
        super();
        this.indexable = indexable;
        this.property = property;
    }

    public Expression getIndexable() {
        return indexable;
    }

    public void setIndexable(Expression indexable) {
        this.indexable = indexable;
    }

    public Expression getProperty() {
        return property;
    }

    public void setProperty(Expression property) {
        this.property = property;
    }

    @Override
    public Object evaluate(Context ctx) {
        Object id_val = indexable.evaluate(ctx);
        Integer index = indexValue(property.evaluate(ctx));
        if(id_val == null && index == null){
            return null;
        }
        return  getIndexedValue(id_val,index);
    }

    private Object getIndexedValue(Object o, int index){
        if(o instanceof List){
            return ((List) o).get(index);
        }
        if(o instanceof Array){
            return ((Object[])o)[index];
        }
        return null;
    }
    private int indexValue(Object o){
        if(o instanceof Number){
            return ((Number) o).intValue();
        }else if(o instanceof QuantityLiteral){
            return ((QuantityLiteral) o).getQuantity().intValue();
        }
        return -1;
    }
    @Override
    public String toCql() {
        return indexable.toCql()+"["+property.toCql()+"]";
    }
}
