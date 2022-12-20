package org.opencds.cqf.cql.engine.runtime;

public class CodeSystem extends Vocabulary {

    public CodeSystem withId(String id) {
        setId(id);
        return this;
    }
    public CodeSystem withVersion(String version) {
        setVersion(version);
        return this;
    }
    public CodeSystem withName(String name) {
        setName(name);
        return this;
    }

    @Override
    public Boolean equivalent(Object other) {
        if (!(other instanceof CodeSystem)) {
            return false;
        }
        return super.equivalent(other);
    }

    @Override
    public Boolean equal(Object other) {
        if (!(other instanceof CodeSystem)) {
            return false;
        }
        return super.equal(other);
    }
}
