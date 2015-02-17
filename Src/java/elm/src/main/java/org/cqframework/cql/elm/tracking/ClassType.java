package org.cqframework.cql.elm.tracking;

import java.util.Collection;

public class ClassType extends TupleType {

    public ClassType(String name, DataType baseType, Collection<TupleTypeElement> elements) {
        super(name, baseType, elements);
    }

    public ClassType() {
        this(null, null, null);
    }

    public ClassType(String name) {
        this(name, null, null);
    }

    public ClassType(String name, DataType baseType) {
        this(name, baseType, null);
    }

    public ClassType(Collection<TupleTypeElement> elements) {
        this(null, null, elements);
    }

    private String identifier;
    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }

    private String topic;
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    private String primaryCodePath;
    public String getPrimaryCodePath() { return primaryCodePath; }
    public void setPrimaryCodePath(String primaryCodePath) { this.primaryCodePath = primaryCodePath; }
}
