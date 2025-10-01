package org.cqframework.cql.tools.xsd2modelinfo

import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.util.stream.Collectors
import javax.xml.namespace.QName

class ModelImporterOptions {
    enum class SimpleTypeRestrictionPolicy {
        USE_BASETYPE,
        EXTEND_BASETYPE,
        IGNORE,
    }

    enum class ChoiceTypePolicy {
        USE_CHOICE,
        EXPAND,
    }

    enum class ElementRedeclarationPolicy {
        DISCARD_INVALID_REDECLARATIONS,
        RENAME_INVALID_REDECLARATIONS,
        FAIL_INVALID_REDECLARATIONS,
    }

    enum class VersionPolicy {
        CURRENT,
        INCLUDE_DEPRECATED,
    }

    /** The name of the data model. This will be used in the "using" statement of CQL. */
    @JvmField var model: String? = null

    /**
     * XSD allows choice types, indicating that an element may be one of a set of choices. In CQL
     * 1.2, a new choice type was introduced to the type system, allowing for first-class
     * representation of these types of elements:
     * * **USE_CHOICE**: Generate a single element with a choice type.
     * * **EXPAND**: Generate multiple elements, one for each type in the choice.
     */
    private var choiceTypePolicy: ChoiceTypePolicy? = null

    /**
     * XSD allows simple types to be restricted by certain constraints (e.g. string patterns,
     * numerical ranges, etc). The modelinfo is not sophisticated enough to properly represent the
     * constraints in the restriction. As such, there are multiple ways to represent a simple type
     * that is a restriction on another simple type:
     * * **USE_BASETYPE**: Replace all instances of the simple type with its base type (e.g., a
     *   restriction on xsd:string simply becomes System.String in the modelinfo). This is the
     *   default.
     * * **EXTEND_BASETYPE**: Create a new simple type representation that simply extends the base
     *   type. This maintains the distinction of the restriction type, but still doesn't retain the
     *   actual restriction constraints. Using this option may require CQL authors to perform
     *   additional casts.
     * * **IGNORE**: Create a new simple type with no relation to the base type. This is rarely the
     *   intended behavior.
     */
    private var simpleTypeRestrictionPolicy: SimpleTypeRestrictionPolicy? = null

    /**
     * XSD allows restrictions and extensions to redeclare elements with different types and/or
     * cardinalities. In CQL, redeclared element types must be a subtype of the original type, and
     * redeclared cardinalities must be narrower than the origin cardinality. If xsd-to-modelinfo
     * detects an invalid element declaration, it will behave according to one of these policies:
     * * **RENAME_INVALID_REDECLARATIONS**: Rename the element so it is a new element and does not
     *   clash with the basetype element. The element will be renamed by appending the redeclared
     *   type to the name using CamelCase (e.g., if element "foo" is redeclared to type "bar", and
     *   this is invalid, it will be renamed "fooBar").
     * * **DISCARD_INVALID_REDECLARATIONS**: Discard the redeclaration, falling back to the
     *   originally declared element type and cardinality.
     * * **FAIL_INVALID_REDECLARATIONS**: Throw an exception. This is the default behavior.
     */
    private var elementRedeclarationPolicy: ElementRedeclarationPolicy? = null

    /**
     * Determines whether deprecated elements should be included in the output.
     * * **CURRENT**: Include only elements for the latest version of model info.
     * * **INCLUDE_DEPRECATED**: Include deprecated elements in addition to elements for the latest
     *   version.
     */
    private var versionPolicy: VersionPolicy? = null

    /**
     * Some HL7 standards prefix all of their type names with an ID of the standard. For example,
     * the CDA R2 schema prefixes its types with "POCD_MT000040." This would require CQL authors to
     * include the prefix in retrieve statements (e.g., [POCD_MT000040.Procedure]).
     *
     * By setting normalizePrefix to the prefix string (e.g., CDA.POCD_MT000040.), the modelinfo
     * will indicate a label for each class to allow CQL authors to reference it without the prefix
     * (e.g., [Procedure]).
     *
     * NOTE: The passed in prefix must also include the model name prefix (e.g., if the model name
     * is "CDA" and the CDA schema adds a prefix "POCD_MT000040" to type names, then normalizePrefix
     * should be "CDA.POCD_MT000040."
     */
    @JvmField var normalizePrefix: String? = null

    /**
     * The typeMap is used to hold mappings from XSD types to CQL System types. This allows XSD
     * types to be "retyped" to CQL System Types or to "extend" CQL System types. For extensions,
     * the original type's elements can also be mapped to the elements of the CQL System type (so
     * there are not duplicate concepts w/ different names).
     */
    @JvmField
    val typeMap: MutableMap<QName?, ModelImporterMapperValue> =
        HashMap<QName?, ModelImporterMapperValue>()

    fun withModel(model: String?): ModelImporterOptions {
        this.model = model
        return this
    }

    fun getChoiceTypePolicy(): ChoiceTypePolicy {
        return (if (choiceTypePolicy != null) choiceTypePolicy else ChoiceTypePolicy.EXPAND)!!
    }

    fun setChoiceTypePolicy(choiceTypePolicy: ChoiceTypePolicy?) {
        this.choiceTypePolicy = choiceTypePolicy
    }

    fun withChoiceTypePolicy(choiceTypePolicy: ChoiceTypePolicy?): ModelImporterOptions {
        this.choiceTypePolicy = choiceTypePolicy
        return this
    }

    fun getSimpleTypeRestrictionPolicy(): SimpleTypeRestrictionPolicy {
        return (if (simpleTypeRestrictionPolicy != null) simpleTypeRestrictionPolicy
        else SimpleTypeRestrictionPolicy.USE_BASETYPE)!!
    }

    fun setSimpleTypeRestrictionPolicy(simpleTypeRestrictionPolicy: SimpleTypeRestrictionPolicy?) {
        this.simpleTypeRestrictionPolicy = simpleTypeRestrictionPolicy
    }

    fun withSimpleTypeRestrictionPolicy(
        simpleTypeRestrictionPolicy: SimpleTypeRestrictionPolicy?
    ): ModelImporterOptions {
        this.simpleTypeRestrictionPolicy = simpleTypeRestrictionPolicy
        return this
    }

    fun getElementRedeclarationPolicy(): ElementRedeclarationPolicy {
        return (if (elementRedeclarationPolicy != null) elementRedeclarationPolicy
        else ElementRedeclarationPolicy.FAIL_INVALID_REDECLARATIONS)!!
    }

    fun setElementRedeclarationPolicy(elementRedeclarationPolicy: ElementRedeclarationPolicy?) {
        this.elementRedeclarationPolicy = elementRedeclarationPolicy
    }

    fun withElementRedeclarationPolicy(
        elementRedeclarationPolicy: ElementRedeclarationPolicy?
    ): ModelImporterOptions {
        this.elementRedeclarationPolicy = elementRedeclarationPolicy
        return this
    }

    fun getVersionPolicy(): VersionPolicy {
        return (if (versionPolicy != null) versionPolicy else VersionPolicy.CURRENT)!!
    }

    fun setVersionPolicy(versionPolicy: VersionPolicy?) {
        this.versionPolicy = versionPolicy
    }

    fun withVersionPolicy(versionPolicy: VersionPolicy?): ModelImporterOptions {
        this.versionPolicy = versionPolicy
        return this
    }

    fun withNormalizePrefix(normalizePrefix: String?): ModelImporterOptions {
        this.normalizePrefix = normalizePrefix
        return this
    }

    fun applyProperties(properties: Properties) {
        val model = properties.getProperty("model")
        if (model != null && !model.isEmpty()) {
            this.model = model
        }

        val normalizePrefix = properties.getProperty("normalize-prefix")
        if (normalizePrefix != null && !normalizePrefix.isEmpty()) {
            this.normalizePrefix = normalizePrefix
        }

        val choiceTypePolicy = properties.getProperty("choicetype-policy")
        if (choiceTypePolicy != null && !choiceTypePolicy.isEmpty()) {
            setChoiceTypePolicy(ChoiceTypePolicy.valueOf(choiceTypePolicy))
        }

        val simpleTypeRestrictionPolicy = properties.getProperty("simpletype-restriction-policy")
        if (simpleTypeRestrictionPolicy != null && !simpleTypeRestrictionPolicy.isEmpty()) {
            setSimpleTypeRestrictionPolicy(
                SimpleTypeRestrictionPolicy.valueOf(simpleTypeRestrictionPolicy)
            )
        }

        val elementRedeclarationPolicy = properties.getProperty("element-redeclaration-policy")
        if (elementRedeclarationPolicy != null && !elementRedeclarationPolicy.isEmpty()) {
            setElementRedeclarationPolicy(
                ElementRedeclarationPolicy.valueOf(elementRedeclarationPolicy)
            )
        }

        val versionPolicy = properties.getProperty("version-policy")
        if (versionPolicy != null && !versionPolicy.isEmpty()) {
            setVersionPolicy(VersionPolicy.valueOf(versionPolicy))
        }

        // Iterate the properties (sorting to ensure class extensions come before element mappings)
        for (p in properties.stringPropertyNames().stream().sorted().collect(Collectors.toList())) {
            var matcher: Matcher = RETYPE_PATTERN.matcher(p)
            if (matcher.matches()) {
                typeMap[QName.valueOf(matcher.group(1))] =
                    ModelImporterMapperValue.newRetype(properties.getProperty(p))
                continue
            }

            matcher = EXTEND_PATTERN.matcher(p)
            if (matcher.matches()) {
                typeMap[QName.valueOf(matcher.group(1))] =
                    ModelImporterMapperValue.newExtend(properties.getProperty(p))
                continue
            }

            matcher = EXTEND_EL_PATTERN.matcher(p)
            if (matcher.matches()) {
                val value: ModelImporterMapperValue = typeMap[QName.valueOf(matcher.group(1))]!!
                requireNotNull(value) {
                    String.format("Class element mapping declared before class mapping: %s", p)
                }
                require(value.relationship != ModelImporterMapperValue.Relationship.RETYPE) {
                    String.format("Cannot map class elements for retyped classes: %s", p)
                }
                value.addClassElementMapping(matcher.group(2), properties.getProperty(p))
            }
        }
    }

    fun exportProperties(): Properties {
        val properties = Properties()
        if (model != null) {
            properties.setProperty("model", model)
        }
        if (normalizePrefix != null) {
            properties.setProperty("normalize-prefix", normalizePrefix)
        }
        if (choiceTypePolicy != null) {
            properties.setProperty("choicetype-policy", choiceTypePolicy!!.name)
        }
        if (simpleTypeRestrictionPolicy != null) {
            properties.setProperty(
                "simpletype-restriction-policy",
                simpleTypeRestrictionPolicy!!.name,
            )
        }
        if (elementRedeclarationPolicy != null) {
            properties.setProperty(
                "element-redeclaration-policy",
                elementRedeclarationPolicy!!.name,
            )
        }
        if (versionPolicy != null) {
            properties.setProperty("version-policy", versionPolicy!!.name)
        }
        if (!typeMap.isEmpty()) {
            for (entry in typeMap.entries) {
                val key: QName = entry.key!!
                val value = entry.value
                if (value.relationship == ModelImporterMapperValue.Relationship.RETYPE) {
                    properties.setProperty(
                        String.format("retype.%s", key.toString()),
                        value.targetSystemClass,
                    )
                } else if (value.relationship == ModelImporterMapperValue.Relationship.EXTEND) {
                    properties.setProperty(
                        String.format("extend.%s", key.toString()),
                        value.targetSystemClass,
                    )
                    for (el in value.targetClassElementMap.keys) {
                        val elValue = value.targetClassElementMap[el]
                        properties.setProperty(
                            String.format("extend.%s[%s]", key.toString(), el),
                            elValue,
                        )
                    }
                }
            }
        }

        return properties
    }

    companion object {
        private val RETYPE_PATTERN: Pattern = Pattern.compile("\\s*retype\\.(.+)\\s*")
        private val EXTEND_PATTERN: Pattern = Pattern.compile("\\s*extend\\.([^\\[]+)\\s*")
        private val EXTEND_EL_PATTERN: Pattern =
            Pattern.compile("\\s*extend\\.([^\\[]+)\\[([^\\]]+)\\]\\s*")

        @JvmStatic
        @Throws(IOException::class)
        fun loadFromProperties(propertiesIS: InputStream?): ModelImporterOptions {
            val properties = Properties()
            properties.load(propertiesIS)

            val options = ModelImporterOptions()
            options.applyProperties(properties)
            return options
        }

        @JvmStatic
        @Throws(IOException::class)
        fun loadFromProperties(propertiesFile: File): ModelImporterOptions {
            FileInputStream(propertiesFile).use { `is` ->
                return loadFromProperties(`is`)
            }
        }
    }
}
