import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.MemberName.Companion.member
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.sun.xml.xsom.XSAttributeUse
import com.sun.xml.xsom.XSComplexType
import com.sun.xml.xsom.XSElementDecl
import com.sun.xml.xsom.XSParticle
import com.sun.xml.xsom.XSType
import com.sun.xml.xsom.parser.XSOMParser
import java.io.File
import javax.xml.XMLConstants
import javax.xml.parsers.SAXParserFactory
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

data class Config(val project: String, val xsd: String, val outputDir: String)

// Entry points for schema parsing and code generation
val configs =
    listOf(
        Config(
            project = "cql",
            xsd = "../../cql-lm/schema/model/modelinfo.xsd",
            outputDir = "../cql/build/generated/sources/cql/commonMain/kotlin",
        ),
        Config(
            project = "elm",
            xsd = "../../cql-lm/schema/elm/library.xsd",
            outputDir = "../elm/build/generated/sources/elm/commonMain/kotlin",
        ),
    )

val namespaceToPackageName =
    mapOf(
        "urn:hl7-org:elm-modelinfo:r1" to "org.hl7.elm_modelinfo.r1",
        "urn:hl7-org:elm:r1" to "org.hl7.elm.r1",
        "urn:hl7-org:cql-annotations:r1" to "org.hl7.cql_annotations.r1",
    )

fun getPackageName(namespace: String): String {
    return namespaceToPackageName[namespace] ?: error("Unknown namespace: $namespace")
}

// Reusable class names for KotlinPoet
val mutableListClassName = ClassName("kotlin.collections", "MutableList")
val mutableMapClassName = ClassName("kotlin.collections", "MutableMap")
val qNameClassName = ClassName("org.cqframework.cql.shared", "QName")
val bigDecimalClassName = ClassName("org.cqframework.cql.shared", "BigDecimal")
val jsonObjectClassName = ClassName("kotlinx.serialization.json", "JsonObject")
val jsonArrayClassName = ClassName("kotlinx.serialization.json", "JsonArray")
val jsonPrimitiveClassName = ClassName("kotlinx.serialization.json", "JsonPrimitive")
val jsonElementClassName = ClassName("kotlinx.serialization.json", "JsonElement")
val jsonNullClassName = ClassName("kotlinx.serialization.json", "JsonNull")
val xmlStringToQName =
    MemberName("org.cqframework.cql.shared.serializing", "xmlAttributeValueToQName")
val qNameToXmlString =
    MemberName("org.cqframework.cql.shared.serializing", "qNameToXmlAttributeValue")
val jsonStringToQName = MemberName("org.cqframework.cql.shared.serializing", "jsonStringToQName")
val xmlNodeClassName = ClassName("org.cqframework.cql.shared.serializing", "XmlNode")
val xmlElementClassName = ClassName("org.cqframework.cql.shared.serializing", "XmlNode", "Element")

fun getTypeName(type: XSType): ClassName {
    return when (type.targetNamespace) {
        XMLConstants.W3C_XML_SCHEMA_NS_URI ->
            when (type.name) {
                "string" -> String::class.asClassName()
                "int" -> Int::class.asClassName()
                "anySimpleType" -> String::class.asClassName()
                "boolean" -> Boolean::class.asClassName()
                "integer" -> Int::class.asClassName()
                "decimal" -> bigDecimalClassName
                "dateTime" -> String::class.asClassName()
                "time" -> String::class.asClassName()
                "date" -> String::class.asClassName()
                "base64Binary" -> String::class.asClassName()
                "anyURI" -> String::class.asClassName()
                "QName" -> qNameClassName
                "token" -> String::class.asClassName()
                "NCName" -> String::class.asClassName()
                "ID" -> String::class.asClassName()
                else -> error("Unknown type: ${type.name}")
            }
        else -> ClassName(getPackageName(type.targetNamespace), type.name)
    }
}

fun getXmlAttributeParserCode(type: XSType): CodeBlock {
    return when (type.targetNamespace) {
        XMLConstants.W3C_XML_SCHEMA_NS_URI ->
            when (type.name) {
                "string" -> CodeBlock.of("it")
                "int" -> CodeBlock.of("it.toInt()")
                "anySimpleType" -> CodeBlock.of("it")
                "boolean" -> CodeBlock.of("it.toBoolean()")
                "integer" -> CodeBlock.of("it.toInt()")
                "decimal" -> CodeBlock.of("%T(it)", bigDecimalClassName)
                "dateTime" -> CodeBlock.of("it")
                "time" -> CodeBlock.of("it")
                "date" -> CodeBlock.of("it")
                "base64Binary" -> CodeBlock.of("it")
                "anyURI" -> CodeBlock.of("it")
                "QName" -> CodeBlock.of("%M(it, namespaces)", xmlStringToQName)
                "token" -> CodeBlock.of("it")
                "NCName" -> CodeBlock.of("it")
                "ID" -> CodeBlock.of("it")
                else -> error("Unknown type: ${type.name}")
            }
        else -> CodeBlock.of("%T.fromValue(it)", getTypeName(type))
    }
}

fun getXmlAttributeSerializerCode(type: XSType): CodeBlock {
    return when (type.targetNamespace) {
        XMLConstants.W3C_XML_SCHEMA_NS_URI ->
            when (type.name) {
                "string" -> CodeBlock.of("it")
                "int" -> CodeBlock.of("it.toString()")
                "anySimpleType" -> CodeBlock.of("it")
                "boolean" -> CodeBlock.of("it.toString()")
                "integer" -> CodeBlock.of("it.toString()")
                "decimal" -> CodeBlock.of("it.toPlainString()")
                "dateTime" -> CodeBlock.of("it")
                "time" -> CodeBlock.of("it")
                "date" -> CodeBlock.of("it")
                "base64Binary" -> CodeBlock.of("it")
                "anyURI" -> CodeBlock.of("it")
                "QName" -> CodeBlock.of("%M(it, namespaces, defaultNamespaces)", qNameToXmlString)
                "token" -> CodeBlock.of("it")
                "NCName" -> CodeBlock.of("it")
                "ID" -> CodeBlock.of("it")
                else -> error("Unknown type: ${type.name}")
            }
        else -> CodeBlock.of("it.value()")
    }
}

fun getJsonPrimitiveParserCode(type: XSType): CodeBlock {
    return when (type.targetNamespace) {
        XMLConstants.W3C_XML_SCHEMA_NS_URI ->
            when (type.name) {
                "string" -> CodeBlock.of("it.content")
                "int" ->
                    CodeBlock.of("it.%M", MemberName("kotlinx.serialization.json", "int", true))
                "anySimpleType" -> CodeBlock.of("it.content")
                "boolean" ->
                    CodeBlock.of("it.%M", MemberName("kotlinx.serialization.json", "boolean", true))
                "integer" ->
                    CodeBlock.of("it.%M", MemberName("kotlinx.serialization.json", "int", true))
                "decimal" -> CodeBlock.of("%T(it.content)", bigDecimalClassName)
                "dateTime" -> CodeBlock.of("it.content")
                "time" -> CodeBlock.of("it.content")
                "date" -> CodeBlock.of("it.content")
                "base64Binary" -> CodeBlock.of("it.content")
                "anyURI" -> CodeBlock.of("it.content")
                "QName" -> CodeBlock.of("%M(it.content)", jsonStringToQName)
                "token" -> CodeBlock.of("it.content")
                "NCName" -> CodeBlock.of("it.content")
                "ID" -> CodeBlock.of("it.content")
                else -> error("Unknown type: ${type.name}")
            }
        else -> CodeBlock.of("%T.fromValue(it.content)", getTypeName(type))
    }
}

fun getJsonPrimitiveSerializerCode(type: XSType): CodeBlock {
    return when (type.targetNamespace) {
        XMLConstants.W3C_XML_SCHEMA_NS_URI ->
            when (type.name) {
                "string" -> CodeBlock.of("%T(it)", jsonPrimitiveClassName)
                "int" -> CodeBlock.of("%T(it)", jsonPrimitiveClassName)
                "anySimpleType" -> CodeBlock.of("%T(it)", jsonPrimitiveClassName)
                "boolean" -> CodeBlock.of("%T(it)", jsonPrimitiveClassName)
                "integer" -> CodeBlock.of("%T(it)", jsonPrimitiveClassName)
                "decimal" ->
                    CodeBlock.of(
                        "%L kotlinx.serialization.json.JsonUnquotedLiteral(it.toPlainString())",
                        AnnotationSpec.builder(ClassName("kotlin", "OptIn"))
                            .addMember(
                                "%T::class",
                                ClassName("kotlinx.serialization", "ExperimentalSerializationApi"),
                            )
                            .build(),
                    )
                "dateTime" -> CodeBlock.of("%T(it)", jsonPrimitiveClassName)
                "time" -> CodeBlock.of("%T(it)", jsonPrimitiveClassName)
                "date" -> CodeBlock.of("%T(it)", jsonPrimitiveClassName)
                "base64Binary" -> CodeBlock.of("%T(it)", jsonPrimitiveClassName)
                "anyURI" -> CodeBlock.of("%T(it)", jsonPrimitiveClassName)
                "QName" -> CodeBlock.of("%T(it.toString())", jsonPrimitiveClassName)
                "token" -> CodeBlock.of("%T(it)", jsonPrimitiveClassName)
                "NCName" -> CodeBlock.of("%T(it)", jsonPrimitiveClassName)
                "ID" -> CodeBlock.of("%T(it)", jsonPrimitiveClassName)
                else -> error("Unknown type: ${type.name}")
            }
        else -> CodeBlock.of("%T(it.value())", jsonPrimitiveClassName)
    }
}

// Adds a class property and `with` method for an element
fun TypeSpec.Builder.addElement(
    elementDecl: XSElementDecl,
    typeName: TypeName,
    className: ClassName,
    isRepeated: Boolean,
) {
    // Create a list property if the element's `maxOccurs` isn't 0 or 1
    if (isRepeated) {
        val listType = mutableListClassName.parameterizedBy(typeName)

        addProperty(
            PropertySpec.builder("_${elementDecl.name}", listType.copy(nullable = true))
                .addModifiers(KModifier.INTERNAL)
                .mutable()
                .initializer("null")
                .build()
        )

        val valueParameter = ParameterSpec.builder("value", listType).build()
        addProperty(
            PropertySpec.builder(elementDecl.name, listType)
                .mutable()
                .getter(
                    FunSpec.getterBuilder()
                        .addStatement(
                            "if (this.%N == null) { this.%N = ArrayList() }",
                            className.member("_${elementDecl.name}"),
                            className.member("_${elementDecl.name}"),
                        )
                        .addStatement("return this.%N!!", className.member("_${elementDecl.name}"))
                        .build()
                )
                .setter(
                    FunSpec.setterBuilder()
                        .addParameter(valueParameter)
                        .addStatement(
                            "this.%N = %N",
                            className.member("_${elementDecl.name}"),
                            valueParameter,
                        )
                        .build()
                )
                .build()
        )
        addWithList(elementDecl.name, typeName, className, false)
    } else {
        addProperty(
            PropertySpec.builder(elementDecl.name, typeName.copy(nullable = true))
                .mutable()
                .initializer("null")
                .build()
        )
        addWith(elementDecl.name, typeName, className, false)
    }
}

// Adds a `with` method for a non-repeated element or attribute
fun TypeSpec.Builder.addWith(
    fieldName: String,
    fieldType: TypeName,
    className: ClassName,
    override: Boolean,
) {
    val valueParameter = ParameterSpec.builder("value", fieldType.copy(nullable = true)).build()
    addFunction(
        FunSpec.builder("with${fieldName.replaceFirstChar { it.uppercase() }}")
            .addModifiers(KModifier.OPEN)
            .addParameter(valueParameter)
            .returns(className)
            .addStatement("this.%N = %N", className.member(fieldName), valueParameter)
            .addStatement("return this")
            .apply {
                if (override) {
                    addModifiers(KModifier.OVERRIDE)
                }
            }
            .build()
    )
}

// Adds a `with` method for a repeated element
fun TypeSpec.Builder.addWithList(
    fieldName: String,
    fieldType: TypeName,
    className: ClassName,
    override: Boolean,
) {
    val valuesParameter =
        ParameterSpec.builder("values", List::class.asClassName().parameterizedBy(fieldType))
            .build()
    addFunction(
        FunSpec.builder("with${fieldName.replaceFirstChar { it.uppercase() }}")
            .addModifiers(KModifier.OPEN)
            .addParameter(valuesParameter)
            .returns(className)
            .addStatement(
                "this.%N = %N.toMutableList()",
                className.member(fieldName),
                valuesParameter,
            )
            .addStatement("return this")
            .apply {
                if (override) {
                    addModifiers(KModifier.OVERRIDE)
                }
            }
            .build()
    )
}

// Returns true if the type is XML's anyType
fun typeIsAnyType(type: XSType): Boolean {
    return type.targetNamespace == XMLConstants.W3C_XML_SCHEMA_NS_URI && type.name == "anyType"
}

// Returns true if the type is derived from a non-any type
fun typeHasParent(type: XSType): Boolean {
    return !typeIsAnyType(type.baseType)
}

// Returns the elements declared in the base type and its ancestors
fun getInheritedElements(complexType: XSComplexType): List<XSParticle> {
    if (typeHasParent(complexType)) {
        val baseType = complexType.baseType.asComplexType()
        return getInheritedElements(baseType) + getOwnElements(baseType)
    }
    return emptyList()
}

// Returns the elements declared in the current type
fun getOwnElements(complexType: XSComplexType): List<XSParticle> {
    if (complexType.derivationMethod == XSType.EXTENSION) {
        return complexType.explicitContent?.asParticle()?.term?.asModelGroup()?.toList()
            ?: emptyList()
    }
    return complexType.contentType?.asParticle()?.term?.asModelGroup()?.toList() ?: emptyList()
}

// Returns the attributes declared in the base type and its ancestors
fun getInheritedAttributes(complexType: XSComplexType): List<XSAttributeUse> {
    if (typeHasParent(complexType)) {
        val baseType = complexType.baseType.asComplexType()
        return getInheritedAttributes(baseType) + getOwnAttributes(baseType)
    }
    return emptyList()
}

// Returns the attributes declared in the current type
fun getOwnAttributes(complexType: XSComplexType): List<XSAttributeUse> {
    return complexType.declaredAttributeUses.toList()
}

// Creates a class for a complex type with nested classes for nested anonymous complex types
fun buildClass(complexType: XSComplexType, className: ClassName): TypeSpec {
    return TypeSpec.classBuilder(className)
        .apply {
            if (complexType.isAbstract) {
                addModifiers(KModifier.ABSTRACT)
            } else {
                addModifiers(KModifier.OPEN)
            }

            // Add superclass if the class has a base type
            if (typeHasParent(complexType)) {
                superclass(getTypeName(complexType.baseType))
            }

            // Add the `content` property and `_content` backing property if the complex type is
            // mixed (`Narrative` class)
            if (complexType.isMixed) {
                val listType = mutableListClassName.parameterizedBy(Any::class.asTypeName())

                addProperty(
                    PropertySpec.builder("_content", listType.copy(nullable = true))
                        .addModifiers(KModifier.INTERNAL)
                        .mutable()
                        .initializer("null")
                        .build()
                )

                val valueParameter = ParameterSpec.builder("value", listType).build()
                addProperty(
                    PropertySpec.builder("content", listType)
                        .mutable()
                        .getter(
                            FunSpec.getterBuilder()
                                .addStatement(
                                    "if (this.%N == null) { this.%N = ArrayList() }",
                                    className.member("_content"),
                                    className.member("_content"),
                                )
                                .addStatement("return this.%N!!", className.member("_content"))
                                .build()
                        )
                        .setter(
                            FunSpec.setterBuilder()
                                .addParameter(valueParameter)
                                .addStatement(
                                    "this.%N = %N",
                                    className.member("_content"),
                                    valueParameter,
                                )
                                .build()
                        )
                        .build()
                )
            }

            // Add properties and `with` methods for own attributes
            getOwnAttributes(complexType).forEach { attribute ->
                val typeName = getTypeName(attribute.decl.type)
                if (attribute.defaultValue == null) {
                    addProperty(
                        PropertySpec.builder(attribute.decl.name, typeName.copy(nullable = true))
                            .mutable()
                            .initializer("null")
                            .build()
                    )
                } else {
                    val defaultValue =
                        when (attribute.decl.type.targetNamespace) {
                            XMLConstants.W3C_XML_SCHEMA_NS_URI ->
                                when (attribute.decl.type.name) {
                                    "boolean" ->
                                        CodeBlock.of("%L", attribute.defaultValue.value.toBoolean())
                                    "anyURI" -> CodeBlock.of("%S", attribute.defaultValue.value)
                                    else -> error("Unknown type: ${attribute.decl.type.name}")
                                }
                            else ->
                                CodeBlock.of(
                                    "%T.%N",
                                    typeName,
                                    attribute.defaultValue.value.uppercase(),
                                )
                        }

                    addProperty(
                        PropertySpec.builder(
                                "_${attribute.decl.name}",
                                typeName.copy(nullable = true),
                            )
                            .addModifiers(KModifier.INTERNAL)
                            .mutable()
                            .initializer("null")
                            .build()
                    )

                    val valueParameter =
                        ParameterSpec.builder("value", typeName.copy(nullable = true)).build()
                    addProperty(
                        PropertySpec.builder(attribute.decl.name, typeName.copy(nullable = true))
                            .mutable()
                            .getter(
                                FunSpec.getterBuilder()
                                    .addStatement(
                                        "return this.%N ?: %L",
                                        className.member("_${attribute.decl.name}"),
                                        defaultValue,
                                    )
                                    .build()
                            )
                            .setter(
                                FunSpec.setterBuilder()
                                    .addParameter(valueParameter)
                                    .addStatement(
                                        "this.%N = %N",
                                        className.member("_${attribute.decl.name}"),
                                        valueParameter,
                                    )
                                    .build()
                            )
                            .build()
                    )
                }
                if (
                    attribute.decl.type.targetNamespace == XMLConstants.W3C_XML_SCHEMA_NS_URI &&
                        attribute.decl.type.name == "boolean"
                ) {
                    addFunction(
                        FunSpec.builder(
                                "is${attribute.decl.name.replaceFirstChar { it.uppercase() }}"
                            )
                            .returns(Boolean::class.asClassName().copy(nullable = true))
                            .addStatement("return this.%N", className.member(attribute.decl.name))
                            .build()
                    )
                }
                addWith(attribute.decl.name, getTypeName(attribute.decl.type), className, false)
            }

            // Add `with` methods for inherited attributes
            getInheritedAttributes(complexType).forEach { attribute ->
                addWith(attribute.decl.name, getTypeName(attribute.decl.type), className, true)
            }

            // Add properties and `with` methods for own elements
            getOwnElements(complexType).forEach { particle ->
                val elementDecl = particle.term.asElementDecl()
                if (elementDecl != null) {
                    if (elementDecl.type.name == null) {
                        val nestedClassName =
                            className.nestedClass(
                                elementDecl.name.replaceFirstChar { it.uppercase() }
                            )

                        // Add a nested class for the anonymous complex type
                        addType(buildClass(elementDecl.type.asComplexType(), nestedClassName))

                        addElement(elementDecl, nestedClassName, className, particle.isRepeated)
                    } else {
                        addElement(
                            elementDecl,
                            getTypeName(elementDecl.type),
                            className,
                            particle.isRepeated,
                        )
                    }
                }
            }

            // Add `with` methods for inherited elements
            getInheritedElements(complexType).forEach { particle ->
                val elementDecl = particle.term.asElementDecl()
                if (elementDecl != null) {
                    if (particle.isRepeated) {
                        addWithList(
                            elementDecl.name,
                            getTypeName(elementDecl.type),
                            className,
                            true,
                        )
                    } else {
                        addWith(elementDecl.name, getTypeName(elementDecl.type), className, true)
                    }
                }
            }
        }
        .addFunction(
            FunSpec.builder("equals")
                .addModifiers(KModifier.OVERRIDE)
                .returns(Boolean::class)
                .apply {
                    addParameter("other", Any::class.asClassName().copy(nullable = true))

                    beginControlFlow("if (other is %T)", className)

                    // Check the equality of inherited attributes and elements
                    if (typeHasParent(complexType)) {
                        addStatement("if (!super.equals(other)) return false")
                    }

                    // Check the equality of own attributes
                    getOwnAttributes(complexType).forEach { attribute ->
                        addStatement(
                            "if (this.%N != other.%N) return false",
                            className.member(attribute.decl.name),
                            className.member(attribute.decl.name),
                        )
                    }

                    // Check the equality of own elements
                    getOwnElements(complexType).forEach { particle ->
                        val elementDecl = particle.term.asElementDecl()
                        if (elementDecl != null) {
                            addStatement(
                                "if (this.%N != other.%N) return false",
                                className.member(elementDecl.name),
                                className.member(elementDecl.name),
                            )
                        }
                    }

                    addStatement("return true")
                    endControlFlow()
                    addStatement("return false")
                }
                .build()
        )
        .addFunction(
            FunSpec.builder("hashCode")
                .addModifiers(KModifier.OVERRIDE)
                .returns(Int::class)
                .apply {
                    addStatement("var result = 0")

                    if (typeHasParent(complexType)) {
                        addStatement("result = super.hashCode()")
                    }

                    getOwnAttributes(complexType).forEach { attribute ->
                        addStatement(
                            "result = 31 * result + (%N?.hashCode() ?: 0)",
                            className.member(attribute.decl.name),
                        )
                    }

                    getOwnElements(complexType).forEach { particle ->
                        val elementDecl = particle.term.asElementDecl()
                        if (elementDecl != null) {
                            addStatement(
                                "result = 31 * result + (%N?.hashCode() ?: 0)",
                                className.member(elementDecl.name),
                            )
                        }
                    }

                    addStatement("return result")
                }
                .build()
        )
        .addType(TypeSpec.companionObjectBuilder().build())
        .build()
}

// Returns all subtypes of a complex type (including indirect subtypes)
fun getAllSubtypes(complexType: XSComplexType): List<XSComplexType> {
    return complexType.subtypes.map { subtype -> getAllSubtypes(subtype) + subtype }.flatten()
}

// Adds the `fromXmlElement`, `toXmlElement`, `fromJsonObject`, and `toJsonObject` extension
// functions for the class and nested classes
fun FileSpec.Builder.addSerializers(
    complexType: XSComplexType,
    className: ClassName,
): FileSpec.Builder {

    // Add `froXmlElement` static function
    addFunction(
        FunSpec.builder("fromXmlElement")
            .receiver(className.nestedClass("Companion"))
            .addModifiers(KModifier.INTERNAL)
            .addParameter("xmlElement", xmlElementClassName)
            .addParameter(
                "namespacesFromParent",
                Map::class.asClassName()
                    .parameterizedBy(String::class.asClassName(), String::class.asClassName()),
            )
            .returns(className)
            .apply {

                // Build the namespaces map from the inherited and own declarations
                addStatement(
                    "val namespaceOverrides = xmlElement.attributes.filter { it.key == \"xmlns\" || it.key.startsWith(\"xmlns:\") }.map { it.key.substringAfter(\":\", \"\") to it.value }.toMap()"
                )
                addStatement("val namespaces = namespacesFromParent + namespaceOverrides")

                // If the element has an `xsi:type` attribute, use the subtype's `fromXmlElement`
                beginControlFlow(
                    "namespaces.entries.find { it.value == %S }?.key?.let",
                    XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
                )
                beginControlFlow("xmlElement.attributes[\"\$it:type\"]?.let")
                beginControlFlow("when (%M(it, namespaces))", xmlStringToQName)
                getAllSubtypes(complexType).forEach { subtype ->
                    addStatement(
                        "%T(%S, %S) -> return %T.%M(xmlElement, namespaces)",
                        qNameClassName,
                        subtype.targetNamespace,
                        subtype.name,
                        getTypeName(subtype),
                        MemberName(getPackageName(subtype.targetNamespace), "fromXmlElement", true),
                    )
                }
                endControlFlow()
                endControlFlow()
                endControlFlow()

                // If the class is abstract and `xsi:type` didn't match any of the subtypes, throw a
                // runtime error
                if (complexType.isAbstract) {
                    addStatement(
                        "error(%S)",
                        "Cannot deserialize abstract class ${className.canonicalName}",
                    )
                } else {
                    // Build the instance from attributes and child elements
                    addStatement("val instance = %T()", className)

                    // Read properties from attributes
                    (getInheritedAttributes(complexType) + getOwnAttributes(complexType)).forEach {
                        attribute ->
                        beginControlFlow("xmlElement.attributes[%S]?.let", attribute.decl.name)
                        addStatement(
                            "instance.%N = %L",
                            attribute.decl.name,
                            getXmlAttributeParserCode(attribute.decl.type),
                        )
                        endControlFlow()
                    }

                    // Read properties from child elements
                    if (complexType.isMixed) {
                        addStatement(
                            "org.cqframework.cql.elm.serializing.getNarrativeContentFromXml(xmlElement, instance, namespaces)"
                        )
                    } else {
                        beginControlFlow("xmlElement.children.forEach")
                        beginControlFlow("if (it is %T)", xmlElementClassName)
                        beginControlFlow("when (%M(it.tagName, namespaces))", xmlStringToQName)
                        (getInheritedElements(complexType) + getOwnElements(complexType)).forEach {
                            particle ->
                            val elementDecl = particle.term.asElementDecl()
                            if (elementDecl != null) {

                                val elementClassName =
                                    if (elementDecl.type.name == null)
                                        className.nestedClass(
                                            elementDecl.name.replaceFirstChar { it.uppercase() }
                                        )
                                    else getTypeName(elementDecl.type)

                                beginControlFlow(
                                    "%T(%S, %S) ->",
                                    qNameClassName,
                                    elementDecl.targetNamespace,
                                    elementDecl.name,
                                )

                                if (particle.isRepeated) {
                                    addStatement(
                                        "instance.%N.add(%T.%M(it, namespaces))",
                                        elementDecl.name,
                                        elementClassName,
                                        MemberName(
                                            getPackageName(elementDecl.type.targetNamespace),
                                            "fromXmlElement",
                                            true,
                                        ),
                                    )
                                } else {
                                    addStatement(
                                        "instance.%N = %T.%M(it, namespaces)",
                                        elementDecl.name,
                                        elementClassName,
                                        MemberName(
                                            getPackageName(elementDecl.type.targetNamespace),
                                            "fromXmlElement",
                                            true,
                                        ),
                                    )
                                }

                                endControlFlow()
                            }
                        }
                        endControlFlow()
                        endControlFlow()
                        endControlFlow()
                    }
                    addStatement("return instance")
                }
            }
            .build()
    )

    // Add `toXmlElement` function
    addFunction(
        FunSpec.builder("toXmlElement")
            .receiver(className)
            .addParameter("tagName", qNameClassName)
            .addParameter("withXsiType", Boolean::class)
            .addParameter(
                "namespaces",
                mutableMapClassName.parameterizedBy(
                    String::class.asClassName(),
                    String::class.asClassName(),
                ),
            )
            .addParameter(
                "defaultNamespaces",
                Map::class.asClassName()
                    .parameterizedBy(String::class.asClassName(), String::class.asClassName()),
            )
            .returns(xmlElementClassName)
            .apply {
                // If this is an instance of a subclass, use the `toXmlElement` method of the
                // subclass
                beginControlFlow("when (this)")
                complexType.subtypes.forEach { subtype ->
                    addStatement(
                        "is %T -> return this.%M(tagName, true, namespaces, defaultNamespaces)",
                        getTypeName(subtype),
                        MemberName(getPackageName(subtype.targetNamespace), "toXmlElement", true),
                    )
                }
                endControlFlow()

                // Write attributes
                addStatement("val attributes = mutableMapOf<String, String>()")

                // Write the `xsi:type` attribute if required
                addStatement(
                    """
                    if (withXsiType) {
                      attributes[%M(%T(%S, "type"), namespaces, defaultNamespaces)] = %M(%T(%S, %S), namespaces, defaultNamespaces)
                    }
                    """
                        .trimIndent(),
                    qNameToXmlString,
                    qNameClassName,
                    XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI,
                    qNameToXmlString,
                    qNameClassName,
                    complexType.targetNamespace,
                    complexType.name ?: "null",
                )

                (getInheritedAttributes(complexType) + getOwnAttributes(complexType)).forEach {
                    attribute ->
                    if (attribute.defaultValue == null) {
                        beginControlFlow("this.%N?.let", attribute.decl.name)
                        addStatement(
                            "attributes[%S] = %L",
                            attribute.decl.name,
                            getXmlAttributeSerializerCode(attribute.decl.type),
                        )
                        endControlFlow()
                    } else {
                        beginControlFlow("this.%N?.let", "_${attribute.decl.name}")
                        addStatement(
                            "attributes[%S] = %L",
                            attribute.decl.name,
                            getXmlAttributeSerializerCode(attribute.decl.type),
                        )
                        endControlFlow()
                    }
                }

                // Write child elements
                addStatement("val children = mutableListOf<%T>()", xmlNodeClassName)
                if (complexType.isMixed) {
                    addStatement(
                        "org.cqframework.cql.elm.serializing.addNarrativeContentToXml(this, children, namespaces, defaultNamespaces)"
                    )
                } else {
                    (getInheritedElements(complexType) + getOwnElements(complexType)).forEach {
                        particle ->
                        val elementDecl = particle.term.asElementDecl()
                        if (elementDecl != null) {

                            if (particle.isRepeated) {
                                beginControlFlow("this.%N?.let", "_${elementDecl.name}")
                                addStatement(
                                    """
                                    it.forEach {
                                      children.add(it.%M(
                                        %T(%S, %S),
                                        false,
                                        namespaces,
                                        defaultNamespaces
                                      ))
                                    }
                                    """
                                        .trimIndent(),
                                    MemberName(
                                        getPackageName(elementDecl.type.targetNamespace),
                                        "toXmlElement",
                                        true,
                                    ),
                                    qNameClassName,
                                    elementDecl.targetNamespace,
                                    elementDecl.name,
                                )
                                endControlFlow()
                            } else {
                                beginControlFlow("this.%N?.let", elementDecl.name)
                                addStatement(
                                    """
                                    children.add(it.%M(
                                      %T(%S, %S),
                                      false,
                                      namespaces,
                                      defaultNamespaces
                                    ))
                                    """
                                        .trimIndent(),
                                    MemberName(
                                        getPackageName(elementDecl.type.targetNamespace),
                                        "toXmlElement",
                                        true,
                                    ),
                                    qNameClassName,
                                    elementDecl.targetNamespace,
                                    elementDecl.name,
                                )
                                endControlFlow()
                            }
                        }
                    }
                }
                addStatement(
                    "return %T(%M(tagName, namespaces, defaultNamespaces), attributes, children)",
                    xmlElementClassName,
                    qNameToXmlString,
                )
            }
            .build()
    )

    // Add `fromJsonObject` static function
    addFunction(
        FunSpec.builder("fromJsonObject")
            .receiver(className.nestedClass("Companion"))
            .addModifiers(KModifier.INTERNAL)
            .addParameter("jsonObject", jsonObjectClassName)
            .returns(className)
            .apply {
                // If the object has a `type` field, use the subtype's `fromJsonObject`
                beginControlFlow("jsonObject[\"type\"]?.let")
                beginControlFlow("if (it is %T && it.isString)", jsonPrimitiveClassName)
                beginControlFlow("when (it.content)")
                getAllSubtypes(complexType).forEach { subtype ->
                    addStatement(
                        "%S -> return %T.%M(jsonObject)",
                        subtype.name,
                        getTypeName(subtype),
                        MemberName(getPackageName(subtype.targetNamespace), "fromJsonObject", true),
                    )
                }
                endControlFlow()
                endControlFlow()
                endControlFlow()

                // If the class is abstract and `type` didn't match any of the subtypes, throw a
                // runtime error
                if (complexType.isAbstract) {
                    addStatement(
                        "error(%S)",
                        "Cannot deserialize abstract class ${className.canonicalName}",
                    )
                } else {
                    // Build the instance from JSON object fields
                    addStatement("val instance = %T()", className)

                    (getInheritedAttributes(complexType) + getOwnAttributes(complexType)).forEach {
                        attribute ->
                        beginControlFlow("jsonObject[%S]?.let", attribute.decl.name)
                        beginControlFlow(
                            "if (it is %T && it !is %T)",
                            jsonPrimitiveClassName,
                            jsonNullClassName,
                        )
                        addStatement(
                            "instance.%N = %L",
                            attribute.decl.name,
                            getJsonPrimitiveParserCode(attribute.decl.type),
                        )
                        endControlFlow()
                        endControlFlow()
                    }
                    if (complexType.isMixed) {
                        addStatement(
                            "org.cqframework.cql.elm.serializing.getNarrativeContentFromJson(jsonObject, instance)"
                        )
                    } else {
                        (getInheritedElements(complexType) + getOwnElements(complexType)).forEach {
                            particle ->
                            val elementDecl = particle.term.asElementDecl()
                            if (elementDecl != null) {

                                val elementClassName =
                                    if (elementDecl.type.name == null)
                                        className.nestedClass(
                                            elementDecl.name.replaceFirstChar { it.uppercase() }
                                        )
                                    else getTypeName(elementDecl.type)

                                beginControlFlow("jsonObject[%S]?.let", elementDecl.name)

                                if (particle.isRepeated) {
                                    addStatement(
                                        """
                                        if (it is %T) {
                                          it.forEach {
                                            if (it is %T) {
                                              instance.%N.add(%T.%M(it))
                                            }
                                          }
                                        }
                                        """
                                            .trimIndent(),
                                        jsonArrayClassName,
                                        jsonObjectClassName,
                                        elementDecl.name,
                                        elementClassName,
                                        MemberName(
                                            getPackageName(elementDecl.type.targetNamespace),
                                            "fromJsonObject",
                                            true,
                                        ),
                                    )
                                } else {
                                    addStatement(
                                        """
                                        if (it is %T) {
                                          instance.%N = %T.%M(it)
                                        }
                                        """
                                            .trimIndent(),
                                        jsonObjectClassName,
                                        elementDecl.name,
                                        elementClassName,
                                        MemberName(
                                            getPackageName(elementDecl.type.targetNamespace),
                                            "fromJsonObject",
                                            true,
                                        ),
                                    )
                                }

                                endControlFlow()
                            }
                        }
                    }
                    addStatement("return instance")
                }
            }
            .build()
    )

    // Add `toJsonObject` function
    addFunction(
        FunSpec.builder("toJsonObject")
            .receiver(className)
            .addParameter("withType", Boolean::class)
            .returns(jsonObjectClassName)
            .apply {
                // If this is an instance of a subclass, use the `toJsonObject` method of the
                // subclass
                beginControlFlow("when (this)")
                complexType.subtypes.forEach { subtype ->
                    addStatement(
                        "is %T -> return this.%M(true)",
                        getTypeName(subtype),
                        MemberName(getPackageName(subtype.targetNamespace), "toJsonObject", true),
                    )
                }
                endControlFlow()

                // Write the object fields
                addStatement("val entries = mutableMapOf<String, %T>()", jsonElementClassName)

                // Write the `type` field if required
                addStatement(
                    "if (withType) { entries[\"type\"] = %T(%S) }",
                    jsonPrimitiveClassName,
                    complexType.name ?: "null",
                )

                (getInheritedAttributes(complexType) + getOwnAttributes(complexType)).forEach {
                    attribute ->
                    if (attribute.defaultValue == null) {
                        beginControlFlow("this.%N?.let", attribute.decl.name)
                        addStatement(
                            "entries[%S] = %L",
                            attribute.decl.name,
                            getJsonPrimitiveSerializerCode(attribute.decl.type),
                        )
                        endControlFlow()
                    } else {
                        beginControlFlow("this.%N?.let", "_${attribute.decl.name}")
                        addStatement(
                            "entries[%S] = %L",
                            attribute.decl.name,
                            getJsonPrimitiveSerializerCode(attribute.decl.type),
                        )
                        endControlFlow()
                    }
                }

                if (complexType.isMixed) {
                    addStatement(
                        "org.cqframework.cql.elm.serializing.addNarrativeContentToJson(this, entries)"
                    )
                } else {
                    (getInheritedElements(complexType) + getOwnElements(complexType)).forEach {
                        particle ->
                        val elementDecl = particle.term.asElementDecl()
                        if (elementDecl != null) {

                            if (particle.isRepeated) {
                                addStatement(
                                    "entries[%S] = %T(this.%N?.map { it.%M(false) } ?: emptyList())",
                                    elementDecl.name,
                                    jsonArrayClassName,
                                    "_${elementDecl.name}",
                                    MemberName(
                                        getPackageName(elementDecl.type.targetNamespace),
                                        "toJsonObject",
                                        true,
                                    ),
                                )
                            } else {
                                addStatement(
                                    "this.%N?.let { entries[%S] = it.%M(false) }",
                                    elementDecl.name,
                                    elementDecl.name,
                                    MemberName(
                                        getPackageName(elementDecl.type.targetNamespace),
                                        "toJsonObject",
                                        true,
                                    ),
                                )
                            }
                        }
                    }
                }
                addStatement("return %T(entries)", jsonObjectClassName)
            }
            .build()
    )

    // Handle nested classes (nested anonymous complex types)
    (getInheritedElements(complexType) + getOwnElements(complexType)).forEach { particle ->
        val elementDecl = particle.term.asElementDecl()
        if (elementDecl != null) {
            if (elementDecl.type.name == null) {
                val nestedClassName =
                    className.nestedClass(elementDecl.name.replaceFirstChar { it.uppercase() })
                addSerializers(elementDecl.type.asComplexType(), nestedClassName)
            }
        }
    }

    return this
}

open class XsdKotlinGenTask : DefaultTask() {

    @TaskAction
    fun generate() {

        for (config in configs) {

            val saxParserFactory = SAXParserFactory.newInstance()
            val xsomParser = XSOMParser(saxParserFactory)

            val file = File(project.projectDir, config.xsd)
            if (!file.exists()) {
                error("XSD file not found: ${file.absolutePath}")
            }

            xsomParser.parse(file)

            xsomParser.result.schemas.forEach { schema ->

                // Generate classes for simple types (enums like `AccessModifier`)
                schema.simpleTypes.values.forEach simpleTypesLoop@{ simpleType ->
                    if (simpleType.targetNamespace == XMLConstants.W3C_XML_SCHEMA_NS_URI) {
                        return@simpleTypesLoop
                    }

                    val typeName = getTypeName(simpleType)

                    val valueParameter = ParameterSpec.builder("value", String::class).build()

                    FileSpec.builder(typeName)
                        .addType(
                            TypeSpec.enumBuilder(typeName)
                                .primaryConstructor(
                                    FunSpec.constructorBuilder()
                                        .addParameter("value", String::class)
                                        .build()
                                )
                                .addProperty(
                                    PropertySpec.builder("value", String::class)
                                        .initializer("value")
                                        .addModifiers(KModifier.PRIVATE)
                                        .build()
                                )
                                .addFunction(
                                    FunSpec.builder("value")
                                        .returns(String::class)
                                        .addStatement("return this.value")
                                        .build()
                                )
                                .addType(
                                    TypeSpec.companionObjectBuilder()
                                        .addFunction(
                                            FunSpec.builder("fromValue")
                                                .addParameter(valueParameter)
                                                .returns(typeName)
                                                .addCode(
                                                    """
                                                    for (c in entries) {
                                                      if (c.value == %N) {
                                                        return c
                                                      }
                                                    }
                                                    throw IllegalArgumentException(%N)
                                                    """
                                                        .trimIndent(),
                                                    valueParameter,
                                                    valueParameter,
                                                )
                                                .build()
                                        )
                                        .build()
                                )
                                .apply {
                                    simpleType.asRestriction().declaredFacets.forEach { facet ->
                                        addEnumConstant(
                                            facet.value.value.uppercase(),
                                            TypeSpec.anonymousClassBuilder()
                                                .addSuperclassConstructorParameter(
                                                    "%S",
                                                    facet.value.value,
                                                )
                                                .build(),
                                        )
                                    }
                                }
                                .build()
                        )
                        .build()
                        .writeTo(File(project.projectDir, config.outputDir))
                }

                // Generate classes and parsers/serializers for complex types
                schema.complexTypes.values.forEach complexTypesLoop@{ complexType ->
                    // Skip XML's anyType
                    if (typeIsAnyType(complexType)) {
                        return@complexTypesLoop
                    }

                    val className = getTypeName(complexType)

                    // Generate the class and XML and JSON parsers and serializers
                    FileSpec.builder(className)
                        .addType(buildClass(complexType, className))
                        .addSerializers(complexType, className)
                        .build()
                        .writeTo(File(project.projectDir, config.outputDir))
                }

                // Generate ObjectFactory.kt for each namespace
                schema.complexTypes.values
                    .map { it.targetNamespace }
                    .distinct()
                    .forEach { namespace ->
                        if (namespaceToPackageName.containsKey(namespace)) {
                            val objectFactoryClassName =
                                ClassName(getPackageName(namespace), "ObjectFactory")

                            FileSpec.builder(objectFactoryClassName)
                                .addType(
                                    TypeSpec.classBuilder(objectFactoryClassName)
                                        .addModifiers(KModifier.OPEN)
                                        .apply {
                                            // Object factories have `create` methods for each
                                            // non-abstract complex type
                                            schema.complexTypes.values.forEach { complexType ->
                                                if (
                                                    complexType.targetNamespace == namespace &&
                                                        !complexType.isAbstract
                                                ) {
                                                    val className = getTypeName(complexType)
                                                    addFunction(
                                                        FunSpec.builder("create${complexType.name}")
                                                            .addModifiers(KModifier.OPEN)
                                                            .returns(className)
                                                            .addStatement("return %T()", className)
                                                            .build()
                                                    )

                                                    // Add `create` methods for nested anonymous
                                                    // complex types
                                                    getOwnElements(complexType).forEach { particle
                                                        ->
                                                        val elementDecl =
                                                            particle.term.asElementDecl()
                                                        if (elementDecl != null) {
                                                            if (elementDecl.type.name == null) {
                                                                val nestedClassName =
                                                                    className.nestedClass(
                                                                        elementDecl.name
                                                                            .replaceFirstChar {
                                                                                it.uppercase()
                                                                            }
                                                                    )
                                                                addFunction(
                                                                    FunSpec.builder(
                                                                            "create${complexType.name}${elementDecl.name.replaceFirstChar { it.uppercase() }}"
                                                                        )
                                                                        .addModifiers(
                                                                            KModifier.OPEN
                                                                        )
                                                                        .returns(nestedClassName)
                                                                        .addStatement(
                                                                            "return %T()",
                                                                            nestedClassName,
                                                                        )
                                                                        .build()
                                                                )
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        .build()
                                )
                                .build()
                                .writeTo(File(project.projectDir, config.outputDir))
                        }
                    }
            }
        }
    }
}
