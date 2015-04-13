package org.cqframework.cql.tools.xsd2modelinfo;

public class ModelImporterOptions {
    public static enum SimpleTypeRestrictionPolicy { USE_BASETYPE, EXTEND_BASETYPE, IGNORE }

    /**
     * XSD allows simple types to be restricted by certain constraints (e.g. string patterns, numerical ranges, etc).
     * The modelinfo is not sophisticated enough to properly represent the constraints in the restriction.  As such,
     * there are multiple ways to represent a simple type that is a restriction on another simple type:
     * <ul>
     *     <li><b>USE_BASETYPE</b>: Replace all instances of the simple type with its base type (e.g., a restriction on
     *         xsd:string simply becomes System.String in the modelinfo).  This is the default.</li>
     *     <li><b>EXTEND_BASETYPE</b>: Create a new simple type representation that simply extends the base type.  This
     *         maintains the distinction of the restriction type, but still doesn't retain the actual restriction
     *         constraints.  Using this option may require CQL authors to perform additional casts.</li>
     *     <li><b>IGNORE</b>: Create a new simple type with no relation to the base type.  This is rarely the intended
     *         behavior.</li>
     * </ul>
     */
    private SimpleTypeRestrictionPolicy simpleTypeRestrictionPolicy = SimpleTypeRestrictionPolicy.USE_BASETYPE;

    /**
     * Some HL7 standards prefix all of their type names with an ID of the standard.  For example, the CDA R2 schema
     * prefixes its types with "POCD_MT000040."  This would require CQL authors to include the prefix in retrieve
     * statements (e.g., [POCD_MT000040.Procedure]).
     *
     * By setting normalizePrefix to the prefix string (e.g., POCD_MT000040.), the modelinfo will indicate a label
     * for each class to allow CQL authors to reference it without the prefix (e.g., [Procedure]).
     */
    private String normalizePrefix = null;

    public SimpleTypeRestrictionPolicy getSimpleTypeRestrictionPolicy() {
        return simpleTypeRestrictionPolicy;
    }

    public void setSimpleTypeRestrictionPolicy(SimpleTypeRestrictionPolicy simpleTypeRestrictionPolicy) {
        this.simpleTypeRestrictionPolicy = simpleTypeRestrictionPolicy;
    }

    public String getNormalizePrefix() {
        return normalizePrefix;
    }

    public void setNormalizePrefix(String normalizePrefix) {
        this.normalizePrefix = normalizePrefix;
    }
}
