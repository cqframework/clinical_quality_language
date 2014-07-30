package org.cqframework.cql.poc.translator.expressions;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by bobd on 7/25/14.
 */
public class AccessorExpression extends Expression {

    Expression expression;
    String identifier;
    boolean valuesetAccessor = false;

    public AccessorExpression(Expression expression, String identifier, boolean isValuesetAccessor) {
        super();
        this.expression = expression;
        this.identifier = identifier;
        this.valuesetAccessor = isValuesetAccessor;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean isValuesetAccessor() {
        return valuesetAccessor;
    }

    public void setValuesetAccessor(boolean valuesetAccessor) {
        this.valuesetAccessor = valuesetAccessor;
    }

    @Override
    public Object evaluate(Context ctx) {
        Object obj = getExpression().evaluate(ctx);
        if (obj == null) {
            return null;
        }
        try {
            Method meth = obj.getClass().getMethod("get", null);
            return meth.invoke(obj);
        } catch (Exception e) {
        }

        try {
            Field f = obj.getClass().getField(identifier);
            return f.get(obj);
        } catch (Exception e) {
        }

        if (obj instanceof Map) {
            return ((Map) obj).get(identifier);
        }
        return null;
    }

    @Override
    public String toCql() {
        return expression.toCql() + "." + identifier;
    }
}
