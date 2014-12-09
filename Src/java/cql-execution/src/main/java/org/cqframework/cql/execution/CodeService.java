package org.cqframework.cql.execution;

public interface CodeService {
//    findValueSetsByOid: (oid) ->
//(valueSet for version, valueSet of @valueSets[oid])
//
    public ValueSet[] findValueSetsByOid(String oid);
//findValueSet: (oid, version) ->
//if version?
//  @valueSets[oid]?[version]
//else
//  results = @findValueSetsByOid(oid)
//  if results.length is 0 then null else results.reduce (a, b) -> if a.version > b.version then a else b
    public ValueSet findValueSet(String oid, String version);
}
