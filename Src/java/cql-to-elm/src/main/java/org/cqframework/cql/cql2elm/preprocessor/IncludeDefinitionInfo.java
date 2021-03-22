package org.cqframework.cql.cql2elm.preprocessor;

import org.antlr.v4.runtime.misc.Interval;
import org.cqframework.cql.gen.cqlParser;

public class IncludeDefinitionInfo extends BaseInfo {
    private String namespaceName;
    private String name;
    private String version;
    private String localName;

    public String getNamespaceName() {
        return namespaceName;
    }

    public void setNamespaceName(String namespaceName) {
        this.namespaceName = namespaceName;
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

    public IncludeDefinitionInfo withName(String value) {
        setName(value);
        return this;
    }

    public IncludeDefinitionInfo withVersion(String value) {
        setVersion(value);
        return this;
    }

    public IncludeDefinitionInfo withLocalName(String value) {
        setLocalName(value);
        return this;
    }

    @Override
    public cqlParser.IncludeDefinitionContext getDefinition() {
        return (cqlParser.IncludeDefinitionContext)super.getDefinition();
    }

    public void setDefinition(cqlParser.IncludeDefinitionContext value) {
        super.setDefinition(value);
    }

    public IncludeDefinitionInfo withDefinition(cqlParser.IncludeDefinitionContext value) {
        setDefinition(value);
        return this;
    }
}
