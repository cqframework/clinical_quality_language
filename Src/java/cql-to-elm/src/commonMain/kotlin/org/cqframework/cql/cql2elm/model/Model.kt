package org.cqframework.cql.cql2elm.model

import org.cqframework.cql.cql2elm.CommonModelManager
import org.hl7.cql.model.ClassType
import org.hl7.cql.model.DataType
import org.hl7.cql.model.ModelContext
import org.hl7.cql.model.NamedType
import org.hl7.elm_modelinfo.r1.ModelInfo
import kotlin.jvm.JvmOverloads

open class Model(val modelInfo: ModelInfo, modelManager: CommonModelManager?) {
    private var index: Map<String, DataType> = HashMap()
    private val classIndex: MutableMap<String, ClassType> = HashMap()
    private val conversions: MutableList<Conversion> = ArrayList()
    private val contexts: MutableList<ModelContext> = ArrayList()

    private val nameIndex: MutableMap<String, DataType> = HashMap()
    private val defaultContext: String?

    init {

        val importer = ModelImporter(modelInfo, modelManager)
        index = importer.types
        for (c in importer.conversions) {
            conversions.add(c)
        }

        for (c in importer.contexts) {
            contexts.add(c)
        }

        defaultContext = importer.defaultContextName

        for (t in index.values) {
            if (t is ClassType && t.label != null) {
                classIndex[casify(t.label!!)] = t
            }

            if (t is NamedType) {
                nameIndex[casify((t as NamedType).simpleName)] = t
            }
        }
    }

    fun getConversions(): List<Conversion> {
        return conversions
    }

    fun resolveTypeName(typeName: String): DataType? {
        val normalizedTypeName = casify(typeName)
        return index[normalizedTypeName] ?: nameIndex[normalizedTypeName]
    }

    @Suppress("ReturnCount")
    @JvmOverloads
    fun resolveContextName(contextName: String, mustResolve: Boolean = true): ModelContext? {
        for (context in contexts) {
            if (context.name == contextName) {
                return context
            }
        }

        // Resolve to a "default" context definition if the context name matches a type name exactly
        val contextType = resolveTypeName(contextName)
        if (contextType is ClassType) {
            var keyName: String? = null
            for (cte in contextType.elements) {
                if (cte.name == "id") {
                    keyName = cte.name
                    break
                }
            }

            return ModelContext(
                contextName,
                contextType,
                if (keyName != null) listOf(keyName) else emptyList(),
                null
            )
        }

        // If we failed to resolve the context name and mustResolve is true
        // then throw an error
        require(!mustResolve) {
            // ERROR:
            "Could not resolve context name $contextName in model ${modelInfo.name}."
        }

        return null
    }

    fun resolveLabel(label: String): ClassType? {
        return classIndex[casify(label)]
    }

    private fun casify(typeName: String): String {
        return if ((modelInfo.isCaseSensitive() != null && modelInfo.isCaseSensitive()!!))
            typeName.lowercase()
        else typeName
    }
}
