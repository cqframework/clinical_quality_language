package org.cqframework.cql.poc.translator.expressions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bobd on 7/25/14.
 */
public class CaseExpression {

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
}
