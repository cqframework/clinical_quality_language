package org.cqframework.cql.poc.translator.expressions;

/**
 * Created by bobd on 7/23/14.
 */
public class IndexExpression extends Expression{

    Expression indexable;
    Expression property;

    public IndexExpression(Expression indexable, Expression property) {
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
}
