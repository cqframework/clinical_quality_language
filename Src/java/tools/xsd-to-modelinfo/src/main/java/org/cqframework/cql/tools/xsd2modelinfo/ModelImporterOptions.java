package org.cqframework.cql.tools.xsd2modelinfo;

public class ModelImporterOptions {
    public static enum SimpleTypeRestrictionPolicy { USE_BASETYPE, EXTEND_BASETYPE, IGNORE }

    private SimpleTypeRestrictionPolicy simpleTypeRestrictionPolicy = SimpleTypeRestrictionPolicy.USE_BASETYPE;

    public SimpleTypeRestrictionPolicy getSimpleTypeRestrictionPolicy() {
        return simpleTypeRestrictionPolicy;
    }

    public void setSimpleTypeRestrictionPolicy(SimpleTypeRestrictionPolicy simpleTypeRestrictionPolicy) {
        this.simpleTypeRestrictionPolicy = simpleTypeRestrictionPolicy;
    }
}
