package org.cqframework.cql.cql2elm.preprocessor;

public class IncludeDefinitionInfo {
    private String name;
    private String version;
    private String localName;

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
}
