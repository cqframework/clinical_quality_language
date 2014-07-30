package org.cqframework.cql.poc.translator.expressions;

import java.util.Map;

/**
 * Created by bobd on 7/23/14.
 */
public class TupleExpression extends Expression {

    Map<String, Expression> values;

    public TupleExpression(Map<String, Expression> values) {
        super();
        this.values = values;
    }

    public Map<String, Expression> getValues() {
        return values;
    }

    public void setValues(Map<String, Expression> values) {
        this.values = values;
    }

    @Override
    public String toCql() {
        return null;
    }
}
