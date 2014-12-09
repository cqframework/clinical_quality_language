package org.cqframework.cql.execution;

public class TestValueSet extends ValueSet {
    public TestValueSet(String oid, String version) {
        super(oid, version);
    }
    
    public boolean hasCode(Code code) {
        return true;
    }
    
    public boolean hasCode(String code) {
        return true;
    }
    
    public boolean hasCode(String code, String system) {
        return true;
    }
    
    public boolean hasCode(String code, String system, String version) {
        return true;
    }
}