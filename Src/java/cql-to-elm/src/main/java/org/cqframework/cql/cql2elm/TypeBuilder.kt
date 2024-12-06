@file:Suppress("WildcardImport")

package org.cqframework.cql.cql2elm

import javax.xml.namespace.QName
import org.cqframework.cql.cql2elm.model.Model
import org.cqframework.cql.elm.IdObjectFactory
import org.hl7.cql.model.*
import org.hl7.elm.r1.ParameterTypeSpecifier
import org.hl7.elm.r1.TupleElementDefinition
import org.hl7.elm.r1.TypeSpecifier
import org.hl7.elm_modelinfo.r1.ModelInfo

class TypeBuilder(private val of: IdObjectFactory, private val mr: ModelResolver) {
    class InternalModelResolver(private val modelManager: ModelManager) : ModelResolver {
        override fun getModel(modelName: String): Model {
            return modelManager.resolveModel(modelName)
        }
    }

    constructor(
        of: IdObjectFactory,
        modelManager: ModelManager
    ) : this(of, InternalModelResolver(modelManager))

    fun dataTypeToQName(type: DataType?): QName {
        if (type is NamedType) {
            val namedType: NamedType = type
            val modelInfo: ModelInfo = mr.getModel(namedType.namespace).modelInfo
            return QName(
                if (modelInfo.targetUrl != null) modelInfo.targetUrl else modelInfo.url,
                if (namedType.target != null) namedType.target else namedType.simpleName
            )
        }

        // ERROR:
        throw IllegalArgumentException("A named type is required in this context.")
    }

    fun dataTypesToTypeSpecifiers(types: List<DataType>): List<TypeSpecifier> {
        val result: ArrayList<TypeSpecifier> = ArrayList()
        for (type: DataType in types) {
            result.add(dataTypeToTypeSpecifier(type))
        }
        return result
    }

    @Suppress("ReturnCount")
    fun dataTypeToTypeSpecifier(type: DataType?): TypeSpecifier {
        // Convert the given type into an ELM TypeSpecifier representation.
        when (type) {
            is NamedType -> {
                return of.createNamedTypeSpecifier()
                    .withName(dataTypeToQName(type))
                    .withResultType(type) as TypeSpecifier
            }
            is ListType -> {
                return listTypeToTypeSpecifier(type)
            }
            is IntervalType -> {
                return intervalTypeToTypeSpecifier(type)
            }
            is TupleType -> {
                return tupleTypeToTypeSpecifier(type)
            }
            is ChoiceType -> {
                return choiceTypeToTypeSpecifier(type)
            }
            is TypeParameter -> {
                return typeParameterToTypeSpecifier(type)
            }
            else -> {
                throw IllegalArgumentException(
                    @Suppress("ImplicitDefaultLocale")
                    String.format("Could not convert type %s to a type specifier.", type)
                )
            }
        }
    }

    private fun listTypeToTypeSpecifier(type: ListType): TypeSpecifier {
        return of.createListTypeSpecifier()
            .withElementType(dataTypeToTypeSpecifier(type.elementType))
            .withResultType(type) as TypeSpecifier
    }

    private fun intervalTypeToTypeSpecifier(type: IntervalType): TypeSpecifier {
        return of.createIntervalTypeSpecifier()
            .withPointType(dataTypeToTypeSpecifier(type.pointType))
            .withResultType(type) as TypeSpecifier
    }

    private fun tupleTypeToTypeSpecifier(type: TupleType): TypeSpecifier {
        @Suppress("SpreadOperator")
        return of.createTupleTypeSpecifier()
            .withElement(*tupleTypeElementsToTupleElementDefinitions(type.elements))
            .withResultType(type) as TypeSpecifier
    }

    private fun tupleTypeElementsToTupleElementDefinitions(
        elements: Iterable<TupleTypeElement>
    ): Array<TupleElementDefinition> {
        val definitions: MutableList<TupleElementDefinition> = ArrayList()
        for (element: TupleTypeElement in elements) {
            definitions.add(
                of.createTupleElementDefinition()
                    .withName(element.name)
                    .withElementType(dataTypeToTypeSpecifier(element.type))
            )
        }
        return definitions.toTypedArray<TupleElementDefinition>()
    }

    private fun choiceTypeToTypeSpecifier(type: ChoiceType): TypeSpecifier {
        @Suppress("SpreadOperator")
        return of.createChoiceTypeSpecifier()
            .withChoice(*choiceTypeTypesToTypeSpecifiers(type))
            .withResultType(type) as TypeSpecifier
    }

    private fun choiceTypeTypesToTypeSpecifiers(choiceType: ChoiceType): Array<TypeSpecifier> {
        val specifiers: MutableList<TypeSpecifier> = ArrayList()
        for (type: DataType in choiceType.types) {
            specifiers.add(dataTypeToTypeSpecifier(type))
        }
        return specifiers.toTypedArray<TypeSpecifier>()
    }

    private fun typeParameterToTypeSpecifier(type: TypeParameter): TypeSpecifier {
        return ParameterTypeSpecifier().withParameterName(type.identifier)
    }
}
