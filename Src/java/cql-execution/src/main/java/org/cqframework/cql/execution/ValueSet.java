package org.cqframework.cql.execution;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * {@code ValueSet} represents a set of codes, provided
 * by a CodeService.
 */
public class ValueSet {
    /** The OID of this ValueSet. */
    public String oid;
    /** The version number of this ValueSet. */
    public String version;
    /** The list of codes inside this ValueSet. */
    public List<Code> codes = new ArrayList<Code>();
    
    public ValueSet(String oid, String version) {
        this.oid = oid;
        this.version = version;
    }
    
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
    
    @Override
    public boolean equals(Object object)
    {
        if(! (object instanceof ValueSet) ) return false;
        ValueSet that = (ValueSet) object;
        return this.oid.equals(that.oid) && this.version.equals(that.version);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(oid, version);
    }
}
