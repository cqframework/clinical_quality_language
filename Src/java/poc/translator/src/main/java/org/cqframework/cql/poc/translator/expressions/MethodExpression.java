package org.cqframework.cql.poc.translator.expressions;

import java.util.List;
import java.util.ListIterator;

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

    @Override
    public String toCql() {
        StringBuffer buff = new StringBuffer();
        buff.append(method.toCql());
        buff.append("(");

        for (ListIterator<Expression> i = paremeters.listIterator(); i.hasNext();) {
            Expression parameter = i.next();
            buff.append(parameter.toCql());
            if(i.hasNext()){
                buff.append(",");
            }

        }
        buff.append(")");
        return buff.toString();
    }
}
