package org.cqframework.cql.execution;

/**
 * CodeService is an interface for providers of ValueSets and Codes.
 * 
 * @author jwalonoski
 */
public interface CodeService {
    /**
     * Find ValueSets by OID.
     * @param oid The OID of the ValueSet.
     * @return An array, possibly null or empty, of ValueSets with the given OID
     * (the array may contain multiple versions of the same ValueSet).
     */
    ValueSet[] findValueSetsByOid(String oid);

    /**
     * Find a particular version of a ValueSet.
     * @param oid The OID of the ValueSet.
     * @param version The version of the ValueSet.
     * @return The version of the ValueSet, if it exists and is available, otherwise null.
     */
    ValueSet findValueSet(String oid, String version);
}
