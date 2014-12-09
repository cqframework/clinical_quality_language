package org.cqframework.cql.execution;

public class TestCodeService implements CodeService {

    @Override
    public ValueSet[] findValueSetsByOid(String oid) {
        return new ValueSet[] { new TestValueSet(oid,"Test System") };
    }

    @Override
    public ValueSet findValueSet(String oid, String version) {
        return new TestValueSet(oid,"Test System");
    }
    
    public class TestValueSet extends ValueSet {
        public TestValueSet(String oid, String system) {
            this.oid = oid;
            this.system = system;
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

}
