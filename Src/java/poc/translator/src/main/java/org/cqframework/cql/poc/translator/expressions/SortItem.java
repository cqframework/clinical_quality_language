package org.cqframework.cql.poc.translator.expressions;

/**
 * Created by bobd on 7/24/14.
 */
public class SortItem extends Expression {

    SortClause.SortDirection direction;
    QualifiedIdentifier identifier;

    public SortItem(SortClause.SortDirection direction, QualifiedIdentifier identifier) {
        super();
        this.direction = direction;
        this.identifier = identifier;
    }

    public SortClause.SortDirection getDirection() {
        return direction;
    }

    public void setDirection(SortClause.SortDirection direction) {
        this.direction = direction;
    }

    public QualifiedIdentifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(QualifiedIdentifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public String toCql() {
        return identifier.toCql() + " " + direction.name();
    }
}
