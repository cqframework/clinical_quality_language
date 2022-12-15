package org.opencds.cqf.cql.engine.terminology;

import org.opencds.cqf.cql.engine.runtime.CodeSystem;
import org.opencds.cqf.cql.engine.runtime.ValueSet;

import java.util.ArrayList;
import java.util.List;

public class ValueSetInfo {
    public static ValueSetInfo fromValueSet(ValueSet vs) {
        ValueSetInfo vsi = new ValueSetInfo().withId(vs.getId()).withVersion(vs.getVersion());
        for (CodeSystem cs : vs.getCodeSystems()) {
            vsi.withCodeSystem(CodeSystemInfo.fromCodeSystem(cs));
        }
        return vsi;
    }

    private String id;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public ValueSetInfo withId(String id) {
        this.setId(id);
        return this;
    }

    private String version;
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public ValueSetInfo withVersion(String version) {
        this.setVersion(version);
        return this;
    }

    private List<CodeSystemInfo> codeSystems;
    public List<CodeSystemInfo> getCodeSystems() {
        if (codeSystems == null) {
            codeSystems = new ArrayList<CodeSystemInfo>();
        }
        return codeSystems;
    }
    public ValueSetInfo withCodeSystem(CodeSystemInfo codeSystem) {
        getCodeSystems().add(codeSystem);
        return this;
    }
}
