package org.cqframework.cql.cql2elm

import kotlin.collections.ArrayList
import org.cqframework.cql.cql2elm.model.Model
import org.cqframework.cql.cql2elm.tracking.Trackable.withResultType
import org.cqframework.cql.elm.IdObjectFactory
import org.cqframework.cql.shared.QName
import org.hl7.cql.model.*
import org.hl7.elm.r1.ParameterTypeSpecifier
import org.hl7.elm.r1.TupleElementDefinition
import org.hl7.elm.r1.TypeSpecifier
import org.hl7.elm_modelinfo.r1.ModelInfo

@Suppress("TooManyFunctions")
class TypeBuilder(private val of: IdObjectFactory, private val mr: ModelResolver) {
    class InternalModelResolver(private val modelManager: ModelManager) : ModelResolver {
        override fun getModel(modelName: String): Model {
            return modelManager.resolveModel(modelName)
        }
    }

    constructor(
        of: IdObjectFactory,
        modelManager: ModelManager,
    ) : this(of, InternalModelResolver(modelManager))

    fun dataTypeToQName(type: DataType?): QName = dataTypeToQName(type, true)

    /**
     * Identity variant of [dataTypeToQName]: always uses the type's own simple name, never the
     * model-info `target` substitution. Used where the QName must preserve the true CQL-level type
     * distinction for pure identity comparison (overload resolution), as opposed to runtime
     * representation (e.g. the literal resource type name a data provider retrieves by).
     */
    fun dataTypeToIdentityQName(type: DataType?): QName = dataTypeToQName(type, false)

    private fun dataTypeToQName(type: DataType?, useTarget: Boolean): QName {
        if (type is NamedType) {
            val namedType: NamedType = type
            val modelInfo: ModelInfo = mr.getModel(namedType.namespace).modelInfo
            return QName(
                if (modelInfo.targetUrl != null) modelInfo.targetUrl!! else modelInfo.url!!,
                if (useTarget && namedType.target != null) namedType.target!!
                else namedType.simpleName,
            )
        }

        // ERROR:
        throw IllegalArgumentException("A named type is required in this context.")
    }

    fun dataTypesToTypeSpecifiers(types: List<DataType>): List<TypeSpecifier> =
        dataTypesToTypeSpecifiers(types, true)

    fun dataTypesToIdentityTypeSpecifiers(types: List<DataType>): List<TypeSpecifier> =
        dataTypesToTypeSpecifiers(types, false)

    private fun dataTypesToTypeSpecifiers(
        types: List<DataType>,
        useTarget: Boolean,
    ): List<TypeSpecifier> {
        val result: ArrayList<TypeSpecifier> = ArrayList()
        for (type: DataType in types) {
            result.add(dataTypeToTypeSpecifier(type, useTarget))
        }
        return result
    }

    @Suppress("ReturnCount")
    fun dataTypeToTypeSpecifier(type: DataType?): TypeSpecifier =
        dataTypeToTypeSpecifier(type, true)

    /**
     * Identity variant of [dataTypeToTypeSpecifier]: builds the same specifier tree but resolves
     * every [NamedType] name via [dataTypeToIdentityQName], preserving the true CQL-level type
     * names for overload-dispatch identity regardless of model-info `target` substitutions.
     */
    @Suppress("ReturnCount")
    fun dataTypeToIdentityTypeSpecifier(type: DataType?): TypeSpecifier =
        dataTypeToTypeSpecifier(type, false)

    @Suppress("ReturnCount")
    private fun dataTypeToTypeSpecifier(type: DataType?, useTarget: Boolean): TypeSpecifier {
        // Convert the given type into an ELM TypeSpecifier representation.
        when (type) {
            is NamedType -> {
                return of.createNamedTypeSpecifier()
                    .withName(dataTypeToQName(type, useTarget))
                    .withResultType(type)
            }
            is ListType -> {
                return listTypeToTypeSpecifier(type, useTarget)
            }
            is IntervalType -> {
                return intervalTypeToTypeSpecifier(type, useTarget)
            }
            is TupleType -> {
                return tupleTypeToTypeSpecifier(type, useTarget)
            }
            is ChoiceType -> {
                return choiceTypeToTypeSpecifier(type, useTarget)
            }
            is TypeParameter -> {
                return typeParameterToTypeSpecifier(type)
            }
            else -> {
                throw IllegalArgumentException("Could not convert type $type to a type specifier.")
            }
        }
    }

    private fun listTypeToTypeSpecifier(type: ListType, useTarget: Boolean): TypeSpecifier {
        return of.createListTypeSpecifier()
            .withElementType(dataTypeToTypeSpecifier(type.elementType, useTarget))
            .withResultType(type)
    }

    private fun intervalTypeToTypeSpecifier(type: IntervalType, useTarget: Boolean): TypeSpecifier {
        return of.createIntervalTypeSpecifier()
            .withPointType(dataTypeToTypeSpecifier(type.pointType, useTarget))
            .withResultType(type)
    }

    private fun tupleTypeToTypeSpecifier(type: TupleType, useTarget: Boolean): TypeSpecifier {
        return of.createTupleTypeSpecifier()
            .withElement(tupleTypeElementsToTupleElementDefinitions(type.elements, useTarget))
            .withResultType(type)
    }

    private fun tupleTypeElementsToTupleElementDefinitions(
        elements: Iterable<TupleTypeElement>,
        useTarget: Boolean,
    ): List<TupleElementDefinition> {
        val definitions: MutableList<TupleElementDefinition> = ArrayList()
        for (element: TupleTypeElement in elements) {
            definitions.add(
                of.createTupleElementDefinition()
                    .withName(element.name)
                    .withElementType(dataTypeToTypeSpecifier(element.type, useTarget))
            )
        }
        return definitions
    }

    private fun choiceTypeToTypeSpecifier(type: ChoiceType, useTarget: Boolean): TypeSpecifier {
        return of.createChoiceTypeSpecifier()
            .withChoice(choiceTypeTypesToTypeSpecifiers(type, useTarget))
            .withResultType(type)
    }

    private fun choiceTypeTypesToTypeSpecifiers(
        choiceType: ChoiceType,
        useTarget: Boolean,
    ): List<TypeSpecifier> {
        val specifiers: MutableList<TypeSpecifier> = ArrayList()
        for (type: DataType in choiceType.types) {
            specifiers.add(dataTypeToTypeSpecifier(type, useTarget))
        }
        return specifiers
    }

    private fun typeParameterToTypeSpecifier(type: TypeParameter): TypeSpecifier {
        return ParameterTypeSpecifier().withParameterName(type.identifier)
    }
}
