package org.cqframework.cql.poc.translator.expressions;

import java.util.List;

/**
 * Created by bobd on 7/23/14.
 */
public class MethodExpression extends Expression{

    List<Expression> paremeters;
    Expression method;
    public MethodExpression(Expression method, List<Expression> expressions){
        this.paremeters = expressions;
        this.method = method;
    }

    public List<Expression> getParemeters() {
        return paremeters;
    }

    public void setParemeters(List<Expression> paremeters) {
        this.paremeters = paremeters;
    }

    public Expression getMethod() {
        return method;
    }

    public void setMethod(Expression method) {
        this.method = method;
    }
}
