package org.cqframework.cql.poc.translator.model;

import org.cqframework.cql.poc.translator.expressions.IdentifierExpression;
import org.cqframework.cql.poc.translator.expressions.QualifiedIdentifier;
import org.cqframework.cql.poc.translator.model.logger.Trackable;

public class SourceDataCriteria extends Trackable {
    public static enum Existence { Occurrence, NonOccurrence, UnknownOccurrence }

    private final Existence existence;
    private final QualifiedIdentifier topic;
    private final IdentifierExpression modality;
    private final IdentifierExpression valuesetPathIdentifier;
    private final QualifiedIdentifier valueset;

    public SourceDataCriteria(Existence existence, QualifiedIdentifier topic, IdentifierExpression modality, IdentifierExpression valuesetPathIdentifier, QualifiedIdentifier valueset) {
        super();

        this.existence = existence;
        this.topic = topic;
        this.modality = modality;
        this.valuesetPathIdentifier = valuesetPathIdentifier;
        this.valueset = valueset;
    }

    public SourceDataCriteria(Existence existence, QualifiedIdentifier topic, IdentifierExpression modality, QualifiedIdentifier valueset) {
        this(existence, topic, modality, null, valueset);
    }

    public Existence getExistence() {
        return existence;
    }

    public QualifiedIdentifier getTopic() {
        return topic;
    }

    public IdentifierExpression getModality() {
        return modality;
    }

    public IdentifierExpression getValuesetPathIdentifier() {
        return valuesetPathIdentifier;
    }

    public QualifiedIdentifier getValueset() {
        return valueset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SourceDataCriteria that = (SourceDataCriteria) o;

        if (existence != that.existence) return false;
        if (modality != null ? !modality.equals(that.modality) : that.modality != null) return false;
        if (!topic.equals(that.topic)) return false;
        if (valuesetPathIdentifier != null ? !valuesetPathIdentifier.equals(that.valuesetPathIdentifier) : that.valuesetPathIdentifier != null) return false;
        if (valueset != null ? !valueset.equals(that.valueset) : that.valueset != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = existence.hashCode();
        result = 31 * result + topic.hashCode();
        result = 31 * result + (modality != null ? modality.hashCode() : 0);
        result = 31 * result + (valueset != null ? valueset.hashCode() : 0);
        result = 31 * result + (valuesetPathIdentifier != null ? valuesetPathIdentifier.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DataRetrieve{" +
                "existence=" + existence +
                ", topic='" + topic + '\'' +
                ", modality='" + modality + '\'' +
                ", valuesetPathIdentifier='" + valuesetPathIdentifier + '\'' +
                ", valueset='" + valueset + '\'' +
                '}';
    }
}
