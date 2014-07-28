package org.cqframework.cql.poc.translator.expressions;

import java.util.List;

/**
 * Created by bobd on 7/25/14.
 */
public class CoalaseExpression {

    List<Expression> expressions;

    public CoalaseExpression(List<Expression> expressions) {
        this.expressions = expressions;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    public void setExpressions(List<Expression> expressions) {
        this.expressions = expressions;
    }
}
