package org.cqframework.cql.poc.translator.expressions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bobd on 7/25/14.
 */
public class CaseExpression extends Expression{

    Expression condition;
    List<CaseItem> caseItems = new ArrayList<>();
    Expression _else;

    public CaseExpression(Expression condition, List<CaseItem> caseItems, Expression _else) {
        this.condition = condition;
        this.caseItems = caseItems;
        this._else=_else;
    }

    public Expression getCondition() {
        return condition;
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    public List<CaseItem> getCaseItems() {
        return caseItems;
    }

    public void setCaseItems(List<CaseItem> caseItems) {
        this.caseItems = caseItems;
    }

    public Expression getElse() {
        return _else;
    }

    public void setElse(Expression _else) {
        this._else = _else;
    }

    @Override
    public String toCql() {
        StringBuffer buff = new StringBuffer();
        buff.append("case ");
        if(condition != null){
            buff.append(condition.toCql());
        }
        buff.append("\n");
        if(caseItems !=null){
            for (CaseItem caseItem : caseItems) {
                buff.append(caseItem.toCql());
                buff.append("\n");
            }
        }
        buff.append("else ");
        buff.append(_else.toCql());
        return buff.toString();
    }
}
