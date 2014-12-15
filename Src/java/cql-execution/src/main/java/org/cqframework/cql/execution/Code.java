package org.cqframework.cql.execution;

import java.util.Objects;

/**
 * {@code Code} represents a code inside a ValueSet, provided
 * by a CodeService.
 */
public class Code {
    /** The name, OID, UUID, or URL of this code. */
    public String code;
    /** The system name, OID, UUID, or URL of the system this code originates from. */
    public String system;
    /** The version of this code. */
    public String version;
    
    @Override
    public boolean equals(Object object)
    {
        if(! (object instanceof Code) ) return false;
        Code that = (Code) object;
        return this.code.equals(that.code) && this.system.equals(that.system) && this.version.equals(that.version);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(code, system, version);
    }
}
