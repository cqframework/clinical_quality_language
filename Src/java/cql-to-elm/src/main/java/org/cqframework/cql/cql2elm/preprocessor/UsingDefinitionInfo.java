package org.cqframework.cql.cql2elm.preprocessor;

import org.cqframework.cql.gen.cqlParser;

public class UsingDefinitionInfo extends BaseInfo {
    private String namespaceName;
    private String name;
    private String version;
    private String localName;

    public String getNamespaceName() {
        return namespaceName;
    }

    public void setNamespaceName(String value) {
        namespaceName = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        name = value;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String value) {
        version = value;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String value) {
        localName = value;
    }

    public UsingDefinitionInfo withNamespaceName(String value) {
        setNamespaceName(value);
        return this;
    }

    public UsingDefinitionInfo withName(String value) {
        setName(value);
        return this;
    }

    public UsingDefinitionInfo withVersion(String value) {
        setVersion(value);
        return this;
    }

    public UsingDefinitionInfo withLocalName(String value) {
        setLocalName(value);
        return this;
    }

    @Override
    public cqlParser.UsingDefinitionContext getDefinition() {
        return (cqlParser.UsingDefinitionContext)super.getDefinition();
    }

    public void setDefinition(cqlParser.UsingDefinitionContext value) {
        super.setDefinition(value);
    }

    public UsingDefinitionInfo withDefinition(cqlParser.UsingDefinitionContext value) {
        setDefinition(value);
        return this;
    }
}
