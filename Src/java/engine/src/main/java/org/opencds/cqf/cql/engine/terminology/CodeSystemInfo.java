package org.opencds.cqf.cql.engine.terminology;

import org.opencds.cqf.cql.engine.runtime.CodeSystem;

public class CodeSystemInfo {
    public static CodeSystemInfo fromCodeSystem(CodeSystem cs) {
        return new CodeSystemInfo().withId(cs.getId()).withVersion(cs.getVersion());
    }

    private String id;
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public CodeSystemInfo withId(String id) {
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
    public CodeSystemInfo withVersion(String version) {
        this.setVersion(version);
        return this;
    }
}
