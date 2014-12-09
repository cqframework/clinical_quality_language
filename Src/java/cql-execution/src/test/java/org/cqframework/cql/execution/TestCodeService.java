package org.cqframework.cql.execution;

public class TestCodeService implements CodeService {

    @Override
    public ValueSet[] findValueSetsByOid(String oid) {
        return new ValueSet[] { new TestValueSet(oid,"1") };
    }

    @Override
    public ValueSet findValueSet(String oid, String version) {
        return new TestValueSet(oid,"1");
    }

}
