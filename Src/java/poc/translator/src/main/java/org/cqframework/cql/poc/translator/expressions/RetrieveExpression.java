package org.cqframework.cql.poc.translator.expressions;

import org.cqframework.cql.poc.translator.model.SourceDataCriteria;

public class RetrieveExpression extends Expression {

    SourceDataCriteria dataCriteria;
    IdentifierExpression duringPathIdentifier;
    Expression duringExpression;


    public RetrieveExpression(SourceDataCriteria dataCriteria, IdentifierExpression duringPathIdentifier, Expression duringExpression) {
        super();
        this.dataCriteria = dataCriteria;
        this.duringPathIdentifier = duringPathIdentifier;
        this.duringExpression = duringExpression;
    }

    public RetrieveExpression(SourceDataCriteria.Existence existenceModifier, QualifiedIdentifier topic, IdentifierExpression modality, IdentifierExpression valuesetPathIdentifier, QualifiedIdentifier valueset, IdentifierExpression duringPathIdentifier, Expression duringExpression) {
        this(new SourceDataCriteria(existenceModifier, topic, modality, valuesetPathIdentifier, valueset), duringPathIdentifier, duringExpression);
    }

    public SourceDataCriteria getDataCriteria() {
        return dataCriteria;
    }

    public void setDataCriteria(SourceDataCriteria dataCriteria) {
        this.dataCriteria = dataCriteria;
    }

    public IdentifierExpression getDuringPathIdentifier() {
        return duringPathIdentifier;
    }

    public void setDuringPathIdentifier(IdentifierExpression duringPathIdentifier) {
        this.duringPathIdentifier = duringPathIdentifier;
    }

    public Expression getDuringExpression() {
        return duringExpression;
    }

    public void setDuringExpression(Expression duringExpression) {
        this.duringExpression = duringExpression;
    }

    @Override
    public String toCql() {
        return null;
    }
}
