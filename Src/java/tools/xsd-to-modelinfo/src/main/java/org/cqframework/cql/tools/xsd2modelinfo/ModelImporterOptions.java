package org.cqframework.cql.tools.xsd2modelinfo;

import javax.xml.namespace.QName;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ModelImporterOptions {
    public enum SimpleTypeRestrictionPolicy {USE_BASETYPE, EXTEND_BASETYPE, IGNORE}
    public enum ChoiceTypePolicy {USE_CHOICE, EXPAND}
    public enum ElementRedeclarationPolicy {DISCARD_INVALID_REDECLARATIONS, RENAME_INVALID_REDECLARATIONS, FAIL_INVALID_REDECLARATIONS }
    private static final Pattern RETYPE_PATTERN = Pattern.compile("\\s*retype\\.(.+)\\s*");
    private static final Pattern EXTEND_PATTERN = Pattern.compile("\\s*extend\\.([^\\[]+)\\s*");
    private static final Pattern EXTEND_EL_PATTERN = Pattern.compile("\\s*extend\\.([^\\[]+)\\[([^\\]]+)\\]\\s*");

    /**
     * The name of the data model.  This will be used in the "using" statement of CQL.
     */
    private String model = null;

    /**
     * XSD allows choice types, indicating that an element may be one of a set of choices. In CQL 1.2, a new choice
     * type was introduced to the type system, allowing for first-class representation of these types of elements:
     * <ul>
     *     <li><b>USE_CHOICE</b>: Generate a single element with a choice type.</li>
     *     <li><b>EXPAND</b>: Generate multiple elements, one for each type in the choice.</li>
     * </ul>
     */
    private ChoiceTypePolicy choiceTypePolicy = null;

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
    private SimpleTypeRestrictionPolicy simpleTypeRestrictionPolicy = null;

    /**
     * XSD allows restrictions and extensions to redeclare elements with different types and/or cardinalities.  In CQL,
     * redeclared element types must be a subtype of the original type, and redeclared cardinalities must be narrower
     * than the origin cardinality.  If xsd-to-modelinfo detects an invalid element declaration, it will behave
     * according to one of these policies:
     * <ul>
     * <li><b>RENAME_INVALID_REDECLARATIONS</b>: Rename the element so it is a new element and does not clash with the
     * basetype element.  The element will be renamed by appending the redeclared type to the name using CamelCase
     * (e.g., if element "foo" is redeclared to type "bar", and this is invalid, it will be renamed "fooBar").</li>
     * <li><b>DISCARD_INVALID_REDECLARATIONS</b>: Discard the redeclaration, falling back to the originally declared
     * element type and cardinality.</li>
     * <li><b>FAIL_INVALID_REDECLARATIONS</b>: Throw an exception. This is the default behavior.</li>
     * </ul>
     */
    private ElementRedeclarationPolicy elementRedeclarationPolicy = null;

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
     * The typeMap is used to hold mappings from XSD types to CQL System types.  This allows XSD types to be "retyped"
     * to CQL System Types or to "extend" CQL System types.  For extensions, the original type's elements can also be
     * mapped to the elements of the CQL System type (so there are not duplicate concepts w/ different names).
     */
    private final Map<QName, ModelImporterMapperValue> typeMap = new HashMap<>();

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

    public ChoiceTypePolicy getChoiceTypePolicy() {
        return choiceTypePolicy != null ? choiceTypePolicy : ChoiceTypePolicy.EXPAND;
    }

    public void setChoiceTypePolicy(ChoiceTypePolicy choiceTypePolicy) {
        this.choiceTypePolicy = choiceTypePolicy;
    }

    public ModelImporterOptions withChoiceTypePolicy(ChoiceTypePolicy choiceTypePolicy) {
        this.choiceTypePolicy = choiceTypePolicy;
        return this;
    }

    public SimpleTypeRestrictionPolicy getSimpleTypeRestrictionPolicy() {
        return simpleTypeRestrictionPolicy != null ? simpleTypeRestrictionPolicy : SimpleTypeRestrictionPolicy.USE_BASETYPE;
    }

    public void setSimpleTypeRestrictionPolicy(SimpleTypeRestrictionPolicy simpleTypeRestrictionPolicy) {
        this.simpleTypeRestrictionPolicy = simpleTypeRestrictionPolicy;
    }

    public ModelImporterOptions withSimpleTypeRestrictionPolicy(SimpleTypeRestrictionPolicy simpleTypeRestrictionPolicy) {
        this.simpleTypeRestrictionPolicy = simpleTypeRestrictionPolicy;
        return this;
    }

    public ElementRedeclarationPolicy getElementRedeclarationPolicy() {
        return elementRedeclarationPolicy != null ? elementRedeclarationPolicy : ElementRedeclarationPolicy.FAIL_INVALID_REDECLARATIONS;
    }

    public void setElementRedeclarationPolicy(ElementRedeclarationPolicy elementRedeclarationPolicy) {
        this.elementRedeclarationPolicy = elementRedeclarationPolicy;
    }

    public ModelImporterOptions withElementRedeclarationPolicy(ElementRedeclarationPolicy elementRedeclarationPolicy) {
        this.elementRedeclarationPolicy = elementRedeclarationPolicy;
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

    public Map<QName, ModelImporterMapperValue> getTypeMap() {
        return typeMap;
    }

    public void applyProperties(Properties properties) {
        String model = properties.getProperty("model");
        if (model != null && !model.isEmpty()) {
            setModel(model);
        }

        String normalizePrefix = properties.getProperty("normalize-prefix");
        if (normalizePrefix != null && !normalizePrefix.isEmpty()) {
            setNormalizePrefix(normalizePrefix);
        }

        String choiceTypePolicy = properties.getProperty("choicetype-policy");
        if (choiceTypePolicy != null && !choiceTypePolicy.isEmpty()) {
            setChoiceTypePolicy(ChoiceTypePolicy.valueOf(choiceTypePolicy));
        }

        String simpleTypeRestrictionPolicy = properties.getProperty("simpletype-restriction-policy");
        if (simpleTypeRestrictionPolicy != null && !simpleTypeRestrictionPolicy.isEmpty()) {
            setSimpleTypeRestrictionPolicy(SimpleTypeRestrictionPolicy.valueOf(simpleTypeRestrictionPolicy));
        }

        String elementRedeclarationPolicy = properties.getProperty("element-redeclaration-policy");
        if (elementRedeclarationPolicy != null && !elementRedeclarationPolicy.isEmpty()) {
            setElementRedeclarationPolicy(ElementRedeclarationPolicy.valueOf(elementRedeclarationPolicy));
        }

        // Iterate the properties (sorting to ensure class extensions come before element mappings)
        for (String p : properties.stringPropertyNames().stream().sorted().collect(Collectors.toList())) {
            Matcher matcher = RETYPE_PATTERN.matcher(p);
            if (matcher.matches()) {
                typeMap.put(QName.valueOf(matcher.group(1)), ModelImporterMapperValue.newRetype(properties.getProperty(p)));
                continue;
            }

            matcher = EXTEND_PATTERN.matcher(p);
            if (matcher.matches()) {
                typeMap.put(QName.valueOf(matcher.group(1)), ModelImporterMapperValue.newExtend(properties.getProperty(p)));
                continue;
            }

            matcher = EXTEND_EL_PATTERN.matcher(p);
            if (matcher.matches()) {
                ModelImporterMapperValue value = typeMap.get(QName.valueOf(matcher.group(1)));
                if (value == null) {
                    throw new IllegalArgumentException(String.format("Class element mapping declared before class mapping: %s", p));
                } else if (value.getRelationship() == ModelImporterMapperValue.Relationship.RETYPE) {
                    throw new IllegalArgumentException(String.format("Cannot map class elements for retyped classes: %s", p));
                }
                value.addClassElementMapping(matcher.group(2), properties.getProperty(p));
            }
        }
    }

    public Properties exportProperties() {
        Properties properties = new Properties();
        if (model != null) {
            properties.setProperty("model", model);
        }
        if (normalizePrefix != null) {
            properties.setProperty("normalize-prefix", normalizePrefix);
        }
        if (choiceTypePolicy != null) {
            properties.setProperty("choicetype-policy", choiceTypePolicy.name());
        }
        if (simpleTypeRestrictionPolicy != null) {
            properties.setProperty("simpletype-restriction-policy", simpleTypeRestrictionPolicy.name());
        }
        if (elementRedeclarationPolicy != null) {
            properties.setProperty("element-redeclaration-policy", elementRedeclarationPolicy.name());
        }
        if (! typeMap.isEmpty()) {
            for (Map.Entry<QName, ModelImporterMapperValue> entry : typeMap.entrySet()) {
                QName key = entry.getKey();
                ModelImporterMapperValue value = entry.getValue();
                if (value.getRelationship() == ModelImporterMapperValue.Relationship.RETYPE) {
                    properties.setProperty(String.format("retype.%s", key.toString()), value.getTargetSystemClass());
                } else if (value.getRelationship() == ModelImporterMapperValue.Relationship.EXTEND) {
                    properties.setProperty(String.format("extend.%s", key.toString()), value.getTargetSystemClass());
                    for (String el : value.getTargetClassElementMap().keySet()) {
                        String elValue = value.getTargetClassElementMap().get(el);
                        properties.setProperty(String.format("extend.%s[%s]", key.toString(), el), elValue);
                    }
                }

            }
        }

        return properties;
    }

    public static ModelImporterOptions loadFromProperties(InputStream propertiesIS) throws IOException {
        Properties properties = new Properties();
        properties.load(propertiesIS);

        ModelImporterOptions options = new ModelImporterOptions();
        options.applyProperties(properties);
        return options;
    }

    public static ModelImporterOptions loadFromProperties(File propertiesFile) throws IOException {
        try (FileInputStream is = new FileInputStream(propertiesFile)) {
            return ModelImporterOptions.loadFromProperties(is);
        }
    }
}
