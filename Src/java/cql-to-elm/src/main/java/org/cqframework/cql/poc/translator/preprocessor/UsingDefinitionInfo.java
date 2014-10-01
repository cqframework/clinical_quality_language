package org.cqframework.cql.poc.translator.preprocessor;

public class UsingDefinitionInfo {
    private String name;
    private String version;

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

    public UsingDefinitionInfo withName(String value) {
        setName(value);
        return this;
    }

    public UsingDefinitionInfo withVersion(String value) {
        setVersion(value);
        return this;
    }
}
