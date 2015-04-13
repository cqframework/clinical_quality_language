package org.cqframework.cql.tools.xsd2modelinfo;

import javax.xml.namespace.QName;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ModelImporterOptions {
    public static enum SimpleTypeRestrictionPolicy {USE_BASETYPE, EXTEND_BASETYPE, IGNORE}

    /**
     * The name of the data model.  This will be used in the "using" statement of CQL.
     */
    private String model;

    /**
     * XSD allows simple types to be restricted by certain constraints (e.g. string patterns, numerical ranges, etc).
     * The modelinfo is not sophisticated enough to properly represent the constraints in the restriction.  As such,
     * there are multiple ways to represent a simple type that is a restriction on another simple type:
     * <ul>
     * <li><b>USE_BASETYPE</b>: Replace all instances of the simple type with its base type (e.g., a restriction on
     * xsd:string simply becomes System.String in the modelinfo).  This is the default.</li>
     * <li><b>EXTEND_BASETYPE</b>: Create a new simple type representation that simply extends the base type.  This
     * maintains the distinction of the restriction type, but still doesn't retain the actual restriction
     * constraints.  Using this option may require CQL authors to perform additional casts.</li>
     * <li><b>IGNORE</b>: Create a new simple type with no relation to the base type.  This is rarely the intended
     * behavior.</li>
     * </ul>
     */
    private SimpleTypeRestrictionPolicy simpleTypeRestrictionPolicy = SimpleTypeRestrictionPolicy.USE_BASETYPE;

    /**
     * Some HL7 standards prefix all of their type names with an ID of the standard.  For example, the CDA R2 schema
     * prefixes its types with "POCD_MT000040."  This would require CQL authors to include the prefix in retrieve
     * statements (e.g., [POCD_MT000040.Procedure]).
     * <p>
     * By setting normalizePrefix to the prefix string (e.g., CDA.POCD_MT000040.), the modelinfo will indicate a label
     * for each class to allow CQL authors to reference it without the prefix (e.g., [Procedure]).
     * <p>
     * NOTE: The passed in prefix must also include the model name prefix (e.g., if the model name is "CDA" and the CDA
     * schema adds a prefix "POCD_MT000040" to type names, then normalizePrefix should be "CDA.POCD_MT000040."
     */
    private String normalizePrefix = null;

    /**
     * The typeMap allows XSD types to be fully replaced by CQL System types.  This is for cases where you want to map
     * a specific data model type directly to a CQL type.  The definition of the original data model type will be
     * discarded and all references will be to the CQL System type.
     */
    private final Map<QName, String> typeMap = new HashMap<>();

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public ModelImporterOptions withModel(String model) {
        this.model = model;
        return this;
    }

    public SimpleTypeRestrictionPolicy getSimpleTypeRestrictionPolicy() {
        return simpleTypeRestrictionPolicy;
    }

    public void setSimpleTypeRestrictionPolicy(SimpleTypeRestrictionPolicy simpleTypeRestrictionPolicy) {
        this.simpleTypeRestrictionPolicy = simpleTypeRestrictionPolicy;
    }

    public ModelImporterOptions withSimpleTypeRestrictionPolicy(SimpleTypeRestrictionPolicy simpleTypeRestrictionPolicy) {
        this.simpleTypeRestrictionPolicy = simpleTypeRestrictionPolicy;
        return this;
    }

    public String getNormalizePrefix() {
        return normalizePrefix;
    }

    public void setNormalizePrefix(String normalizePrefix) {
        this.normalizePrefix = normalizePrefix;
    }

    public ModelImporterOptions withNormalizePrefix(String normalizePrefix) {
        this.normalizePrefix = normalizePrefix;
        return this;
    }

    public Map<QName, String> getTypeMap() {
        return typeMap;
    }

    public void loadProperties(File propertiesFile) throws IOException {
        Properties properties = new Properties();
        try (FileInputStream is = new FileInputStream(propertiesFile)) {
            properties.load(is);
        }

        String model = properties.getProperty("model");
        if (model != null && !model.isEmpty()) {
            setModel(model);
        }

        String normalizePrefix = properties.getProperty("normalize-prefix");
        if (normalizePrefix != null && !normalizePrefix.isEmpty()) {
            setNormalizePrefix(normalizePrefix);
        }

        String simpleTypeRestrictionPolicy = properties.getProperty("simpletype-restriction-policy");
        if (simpleTypeRestrictionPolicy != null && !simpleTypeRestrictionPolicy.isEmpty()) {
            setSimpleTypeRestrictionPolicy(SimpleTypeRestrictionPolicy.valueOf(simpleTypeRestrictionPolicy));
        }

        // Have to iterate properties to find the type-map properties
        for (String p : properties.stringPropertyNames()) {
            if (p.startsWith("type-map.")) {
                typeMap.put(QName.valueOf(p.substring(9)), properties.getProperty(p));
            }
        }
    }

    public static ModelImporterOptions loadFromProperties(File propertiesFile) throws IOException {
        ModelImporterOptions options = new ModelImporterOptions();
        options.loadProperties(propertiesFile);
        return options;
    }
}
