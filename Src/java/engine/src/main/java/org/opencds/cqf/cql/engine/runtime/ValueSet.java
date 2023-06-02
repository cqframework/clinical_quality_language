package org.opencds.cqf.cql.engine.runtime;

import java.util.ArrayList;
import java.util.List;

public class ValueSet extends Vocabulary {
    public ValueSet withId(String id) {
        setId(id);
        return this;
    }
    public ValueSet withVersion(String version) {
        setVersion(version);
        return this;
    }
    public ValueSet withName(String name) {
        setName(name);
        return this;
    }

    private List<CodeSystem> codeSystems = new ArrayList<CodeSystem>();
    public Iterable<CodeSystem> getCodeSystems() {
        return codeSystems;
    }
    public void setCodeSystems(List<CodeSystem> codeSystems) {
        this.codeSystems = new ArrayList<CodeSystem>();
        if (codeSystems != null) {
            for (CodeSystem cs : codeSystems) {
                if (cs != null) {
                    addCodeSystem(cs);
                }
            }
        }
    }
    public ValueSet withCodeSystems(List<CodeSystem> codeSystems) {
        setCodeSystems(codeSystems);
        return this;
    }
    public void addCodeSystem(CodeSystem codeSystem) {
        if (codeSystem == null) {
            throw new IllegalArgumentException("codeSystem is required");
        }
        codeSystems.add(codeSystem);
    }
    public ValueSet withCodeSystem(CodeSystem codeSystem) {
        addCodeSystem(codeSystem);
        return this;
    }
    public CodeSystem getCodeSystem(String id) {
        if (id == null) {
            return null;
        }

        for (CodeSystem cs : codeSystems) {
            if (id.equals(cs.getId())) {
                return cs;
            }
        }

        return null;
    }
    public CodeSystem getCodeSystem(String id, String version) {
        if (id == null) {
            return null;
        }

        for (CodeSystem cs : codeSystems) {
            if (id.equals(cs.getId()) && ((version == null && cs.getVersion() == null) || (version != null && version.equals(cs.getVersion())))) {
                return cs;
            }
        }

        return null;
    }

    @Override
    public Boolean equivalent(Object other) {
        if (!(other instanceof ValueSet)) {
            return false;
        }
        ValueSet otherV = (ValueSet)other;
        Boolean equivalent = super.equivalent(other) && codeSystems.size() == otherV.codeSystems.size();
        if (equivalent) {
            for (CodeSystem cs : codeSystems) {
                CodeSystem otherC = otherV.getCodeSystem(cs.getId());
                if (otherC == null) {
                    return false;
                }
            }
        }
        return equivalent;
    }

    @Override
    public Boolean equal(Object other) {
        if (!(other instanceof ValueSet)) {
            return false;
        }
        ValueSet otherV = (ValueSet)other;
        Boolean equal = super.equal(other) && codeSystems.size() == otherV.codeSystems.size();
        if (equal) {
            for (CodeSystem cs : codeSystems) {
                CodeSystem otherC = otherV.getCodeSystem(cs.getId(), cs.getVersion());
                if (otherC == null) {
                    return false;
                }
            }
        }
        return equal;
    }
}
