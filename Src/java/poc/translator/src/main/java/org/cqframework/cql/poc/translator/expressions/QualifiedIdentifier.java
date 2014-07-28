package org.cqframework.cql.poc.translator.expressions;

/**
 * Created by bobd on 7/24/14.
 */
public class QualifiedIdentifier extends  Expression{

    String qualifier;
    String identifier;
    boolean valuesetIdentifier;

    public QualifiedIdentifier(String qualifier, String identifier, boolean valuesetIdentifier) {
        this.qualifier = qualifier;
        this.identifier = identifier;
        this.valuesetIdentifier=valuesetIdentifier;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public boolean isValuesetIdentifier() {
        return valuesetIdentifier;
    }

    public void setValuesetIdentifier(boolean valuesetIdentifier) {
        this.valuesetIdentifier = valuesetIdentifier;
    }
}
