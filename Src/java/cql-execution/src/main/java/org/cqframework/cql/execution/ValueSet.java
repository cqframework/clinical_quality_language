package org.cqframework.cql.execution;

import java.util.ArrayList;
import java.util.List;

public class ValueSet {
    public String oid;
    public String system;
    public List<Code> codes = new ArrayList<Code>();
    
    public boolean hasCode(Code code) {
        return codes.contains(code);
    }
    
    public boolean hasCode(String code) {
        for(Code c : codes) {
            if(c.code.equals(code)) return true;
        }
        return false;
    }
    
    public boolean hasCode(String code, String system) {
        for(Code c : codes) {
            if(c.code.equals(code) && c.system.equals(system)) return true;
        }
        return false;
    }
    
    public boolean hasCode(String code, String system, String version) {
        Code c = new Code();
        c.code = code;
        c.system = system;
        c.version = version;
        return hasCode(c);
    }
}
