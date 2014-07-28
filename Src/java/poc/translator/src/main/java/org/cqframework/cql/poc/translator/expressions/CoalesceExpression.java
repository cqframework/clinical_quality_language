package org.cqframework.cql.poc.translator.expressions;

import java.util.Iterator;
import java.util.List;

/**
 * Created by bobd on 7/25/14.
 */
public class CoalesceExpression extends Expression {

    List<Expression> expressions;

    public CoalesceExpression(List<Expression> expressions) {
        this.expressions = expressions;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    public void setExpressions(List<Expression> expressions) {
        this.expressions = expressions;
    }

    @Override
    public String toCql() {
        StringBuffer buff = new StringBuffer();
        buff.append("coalesce (");
        for (Iterator<Expression> i = expressions.iterator();i.hasNext();) {
            Expression expression = i.next();
            buff.append(expression.toCql());
            if(i.hasNext()) {
                buff.append(",");
            }
        }
        buff.append(")");
        return buff.toString();
    }
}
