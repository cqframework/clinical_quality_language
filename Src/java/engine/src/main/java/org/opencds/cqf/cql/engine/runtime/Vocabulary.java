package org.opencds.cqf.cql.engine.runtime;

import org.opencds.cqf.cql.engine.elm.executing.AndEvaluator;
import org.opencds.cqf.cql.engine.elm.executing.EqualEvaluator;
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator;
import org.opencds.cqf.cql.engine.elm.executing.OrEvaluator;

public abstract class Vocabulary implements CqlType {

    private String id;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    private String version;
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }

    private String name;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public Boolean equivalent(Object other) {
        if (!(other instanceof Vocabulary)) {
            return false;
        }
        Vocabulary otherV = (Vocabulary)other;
        return EquivalentEvaluator.equivalent(version, otherV.version);
    }

    @Override
    public Boolean equal(Object other) {
        if (!(other instanceof Vocabulary)) {
            return false;
        }
        Vocabulary otherV = (Vocabulary)other;
        return AndEvaluator.and(
            OrEvaluator.or(id == null && otherV.id == null, EqualEvaluator.equal(id, otherV.id)),
            OrEvaluator.or(version == null && otherV.version == null, EqualEvaluator.equal(version, otherV.version)));
    }
}
