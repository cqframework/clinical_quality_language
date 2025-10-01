package org.cqframework.cql.elm.requirements

import javax.xml.namespace.QName
import kotlin.Boolean
import kotlin.IllegalArgumentException
import kotlin.require
import kotlin.requireNotNull
import kotlin.text.format
import kotlin.text.lastIndexOf
import kotlin.text.substring
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.hl7.cql.model.ChoiceType
import org.hl7.cql.model.ClassType
import org.hl7.cql.model.DataType
import org.hl7.cql.model.IntervalType
import org.hl7.cql.model.ListType
import org.hl7.cql.model.NamedType
import org.hl7.cql.model.TupleType
import org.hl7.cql.model.TupleTypeElement
import org.hl7.elm.r1.ChoiceTypeSpecifier
import org.hl7.elm.r1.IntervalTypeSpecifier
import org.hl7.elm.r1.ListTypeSpecifier
import org.hl7.elm.r1.NamedTypeSpecifier
import org.hl7.elm.r1.TupleTypeSpecifier
import org.hl7.elm.r1.TypeSpecifier

class TypeResolver(val libraryManager: LibraryManager) {
    fun getTypeUri(type: DataType?): String? {
        if (type is ListType) {
            return getTypeUri(type.elementType)
        }
        if (type is ClassType) {
            if (type.identifier != null) {
                return type.identifier
            }
        }

        if (type is NamedType) {
            return dataTypeToQName(type).localPart
        }

        return null
    }

    fun dataTypeToProfileQName(type: DataType?): QName? {
        if (type is ClassType) {
            if (type.identifier != null) {
                val tailIndex = type.identifier!!.lastIndexOf('/')
                if (tailIndex > 0) {
                    val tail = type.identifier!!.substring(tailIndex + 1)
                    val namespace: String = type.identifier!!.substring(0, tailIndex)
                    return QName(namespace, tail)
                }
            }
        }

        if (type is NamedType) {
            return dataTypeToQName(type)
        }

        return null
    }

    /**
     * Return the QName for the given type (without target mapping) This is to preserve data
     * requirements reporting for profiled types when reported against unbound data requirements.
     * This will only work when the ELM tree has type references (which typically means it came
     * straight from the translator, although type resolution could be performed by a visitor on an
     * ELM tree).
     *
     * @param type The data type to determine a QName for
     * @return The QName for the given type (without target mapping)
     */
    fun dataTypeToQName(type: DataType?): QName {
        if (type is NamedType) {
            val namedType = type as NamedType
            val modelInfo = libraryManager.modelManager.resolveModel(namedType.namespace).modelInfo
            return QName(modelInfo.url, namedType.simpleName)
        }

        // ERROR:
        throw IllegalArgumentException("A named type is required in this context.")
    }

    fun resolveTypeName(typeName: QName): DataType {

        // NOTE: This resolution path is ignoring prefix, namespace is required
        require(!(typeName.namespaceURI == null || typeName.namespaceURI == "")) {
            "namespaceURI is required"
        }

        val model = libraryManager.modelManager.resolveModelByUri(typeName.namespaceURI)
        val result = model.resolveTypeName(typeName.localPart)
        requireNotNull(result) { String.format("Could not resolve type %s", typeName.toString()) }
        return result
    }

    fun resolveTypeSpecifier(typeSpecifier: TypeSpecifier): DataType {
        // If the typeSpecifier already has a type, use it
        val resultType = typeSpecifier.resultType
        if (resultType != null) {
            return resultType
        }

        return when (typeSpecifier) {
            is NamedTypeSpecifier -> {
                resolveNamedTypeSpecifier(typeSpecifier)
            }

            is TupleTypeSpecifier -> {
                resolveTupleTypeSpecifier(typeSpecifier)
            }

            is IntervalTypeSpecifier -> {
                resolveIntervalTypeSpecifier(typeSpecifier)
            }

            is ListTypeSpecifier -> {
                resolveListTypeSpecifier(typeSpecifier)
            }

            is ChoiceTypeSpecifier -> {
                resolveChoiceTypeSpecifier(typeSpecifier)
            }

            else -> {
                throw IllegalArgumentException(
                    "Unknown type specifier category: ${typeSpecifier.javaClass.simpleName}"
                )
            }
        }
    }

    private fun resolveNamedTypeSpecifier(typeSpecifier: NamedTypeSpecifier): DataType {
        return resolveTypeName(typeSpecifier.name!!)
    }

    private fun resolveTupleTypeSpecifier(typeSpecifier: TupleTypeSpecifier): DataType {
        val elements = ArrayList<TupleTypeElement>()
        for (element in typeSpecifier.element) {
            val tupleElement =
                TupleTypeElement(element.name!!, resolveTypeSpecifier(element.elementType!!), false)
            elements.add(tupleElement)
        }
        return TupleType(elements)
    }

    private fun resolveIntervalTypeSpecifier(typeSpecifier: IntervalTypeSpecifier): DataType {
        return IntervalType(resolveTypeSpecifier(typeSpecifier.pointType!!))
    }

    private fun resolveListTypeSpecifier(typeSpecifier: ListTypeSpecifier): DataType {
        return ListType(resolveTypeSpecifier(typeSpecifier.elementType!!))
    }

    private fun resolveChoiceTypeSpecifier(typeSpecifier: ChoiceTypeSpecifier): DataType {
        val choiceTypes = ArrayList<DataType>()
        for (choiceType in typeSpecifier.choice) {
            choiceTypes.add(resolveTypeSpecifier(choiceType))
        }
        return ChoiceType(choiceTypes)
    }

    fun resolveTypeName(modelName: String, typeName: String): DataType {
        require(modelName != "") { "Unqualified type name cannot be resolved" }

        // NOTE: Assumption here is that the appropriate version of the given model has already been
        // resolved in this
        // context
        val model = libraryManager.modelManager.resolveModel(modelName)
        val result = model.resolveTypeName(typeName)
        requireNotNull(result) {
            String.format("Could not resolve type %s.%s", modelName, typeName)
        }
        return result
    }

    var stringType: DataType? = null
        get() {
            if (field == null) {
                field = resolveTypeName("System", "String")
            }
            return field
        }
        private set

    var codeType: DataType? = null
        get() {
            if (field == null) {
                field = resolveTypeName("System", "Code")
            }
            return field
        }
        private set

    var conceptType: DataType? = null
        get() {
            if (field == null) {
                field = resolveTypeName("System", "Concept")
            }
            return field
        }
        private set

    var valueSetType: DataType? = null
        get() {
            if (field == null) {
                field = resolveTypeName("System", "ValueSet")
            }
            return field
        }
        private set

    var codeSystemType: DataType? = null
        get() {
            if (field == null) {
                field = resolveTypeName("System", "CodeSystem")
            }
            return field
        }
        private set

    var dateType: DataType? = null
        get() {
            if (field == null) {
                field = resolveTypeName("System", "Date")
            }
            return field
        }
        private set

    var dateTimeType: DataType? = null
        get() {
            if (field == null) {
                field = resolveTypeName("System", "DateTime")
            }
            return field
        }
        private set

    var timeType: DataType? = null
        get() {
            if (field == null) {
                field = resolveTypeName("System", "Time")
            }
            return field
        }
        private set

    var booleanType: DataType? = null
        get() {
            if (field == null) {
                field = resolveTypeName("System", "Boolean")
            }
            return field
        }
        private set

    var integerType: DataType? = null
        get() {
            if (field == null) {
                field = resolveTypeName("System", "Integer")
            }
            return field
        }
        private set

    var decimalType: DataType? = null
        get() {
            if (field == null) {
                field = resolveTypeName("System", "Decimal")
            }
            return field
        }
        private set

    var quantityType: DataType? = null
        get() {
            if (field == null) {
                field = resolveTypeName("System", "Quantity")
            }
            return field
        }
        private set

    fun isTerminologyType(dataType: DataType?): Boolean {
        if (dataType != null) {
            return dataType.isSubTypeOf(this.codeType!!) ||
                dataType.isSubTypeOf(this.conceptType!!) ||
                dataType.isSubTypeOf(this.valueSetType!!) ||
                dataType.isSubTypeOf(this.codeSystemType!!) ||
                dataType.isSubTypeOf(this.stringType!!) ||
                (dataType is ListType &&
                    (dataType.elementType.isSubTypeOf(this.codeType!!) ||
                        dataType.elementType.isSubTypeOf(this.conceptType!!) ||
                        dataType.elementType.isSubTypeOf(this.stringType!!)))
        }

        return false
    }

    fun isDateType(dataType: DataType?): Boolean {
        if (dataType != null) {
            return dataType.isSubTypeOf(this.dateType!!) ||
                dataType.isSubTypeOf(this.dateTimeType!!) ||
                (dataType is IntervalType &&
                    (dataType.pointType.isSubTypeOf(this.dateType!!) ||
                        dataType.pointType.isSubTypeOf(this.dateTimeType!!)))
        }

        return false
    }

    fun isDateTimeType(dataType: DataType?): Boolean {
        if (dataType != null) {
            return dataType.isSubTypeOf(this.dateTimeType!!) ||
                (dataType is IntervalType && dataType.pointType.isSubTypeOf(this.dateTimeType!!))
        }

        return false
    }

    fun isTimeType(dataType: DataType?): Boolean {
        if (dataType != null) {
            return dataType.isSubTypeOf(this.timeType!!)
        }

        return false
    }

    fun isIntegerType(dataType: DataType?): Boolean {
        if (dataType != null) {
            return dataType.isSubTypeOf(this.integerType!!)
        }

        return false
    }

    fun isDecimalType(dataType: DataType?): Boolean {
        if (dataType != null) {
            return dataType.isSubTypeOf(this.decimalType!!)
        }

        return false
    }

    fun isQuantityType(dataType: DataType?): Boolean {
        if (dataType != null) {
            return dataType.isSubTypeOf(this.quantityType!!)
        }

        return false
    }

    fun isBooleanType(dataType: DataType?): Boolean {
        if (dataType != null) {
            return dataType.isSubTypeOf(this.booleanType!!)
        }

        return false
    }

    fun isStringType(dataType: DataType?): Boolean {
        if (dataType != null) {
            return dataType.isSubTypeOf(this.stringType!!)
        }

        return false
    }

    fun isListType(dataType: DataType?): Boolean {
        return dataType is ListType
    }

    fun isIntervalType(dataType: DataType?): Boolean {
        return dataType is IntervalType
    }

    fun isCodeType(dataType: DataType?): Boolean {
        if (dataType != null) {
            return dataType.isSubTypeOf(this.codeType!!)
        }

        return false
    }

    fun isConceptType(dataType: DataType?): Boolean {
        if (dataType != null) {
            return dataType.isSubTypeOf(this.conceptType!!)
        }

        return false
    }
}
