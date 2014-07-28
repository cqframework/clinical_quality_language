package org.cqframework.cql.poc.translator.expressions;

import java.util.List;

/**
 * Created by bobd on 7/23/14.
 */
public class QueryExpression extends Expression{

    AliasedQuerySource aliaseQuerySource;
    List<QueryInclusionClauseExpression> queryInclusionClauseExpressions;
    Expression whereClauseExpression;
    Expression returnClause;
    SortClause sortClause;

    public QueryExpression(AliasedQuerySource aliaseQuerySource,List<QueryInclusionClauseExpression> queryInclusionClauseExpressions,
                           Expression whereClauseExpression, Expression returnClause, SortClause sortClause) {
        this.aliaseQuerySource = aliaseQuerySource;
        this.queryInclusionClauseExpressions = queryInclusionClauseExpressions;
        this.whereClauseExpression = whereClauseExpression;
        this.returnClause = returnClause;
        this.sortClause = sortClause;
    }

    public AliasedQuerySource getAliaseQuerySource() {
        return aliaseQuerySource;
    }

    public void setAliaseQuerySource(AliasedQuerySource aliaseQuerySource) {
        this.aliaseQuerySource = aliaseQuerySource;
    }

    public List<QueryInclusionClauseExpression> getQueryInclusionClauseExpressions() {
        return queryInclusionClauseExpressions;
    }

    public void setQueryInclusionClauseExpressions(List<QueryInclusionClauseExpression> queryInclusionClauseExpressions) {
        this.queryInclusionClauseExpressions = queryInclusionClauseExpressions;
    }

    public Expression getWhereClauseExpression() {
        return whereClauseExpression;
    }

    public void setWhereClauseExpression(Expression whereClauseExpression) {
        this.whereClauseExpression = whereClauseExpression;
    }

    public Expression getReturnClause() {
        return returnClause;
    }

    public void setReturnClause(Expression returnClause) {
        this.returnClause = returnClause;
    }

    public SortClause getSortClause() {
        return sortClause;
    }

    public void setSortClause(SortClause sortClause) {
        this.sortClause = sortClause;
    }
}
