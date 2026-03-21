package org.cqframework.cql.cql2elm.analysis

import org.cqframework.cql.cql2elm.ModelManager
import org.cqframework.cql.cql2elm.model.Model
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.cqframework.cql.shared.QName
import org.hl7.cql.ast.UsingDefinition
import org.hl7.cql.model.DataType
import org.hl7.cql.model.ListType
import org.hl7.cql.model.NamedType

/**
 * Unified model type resolution used by both analysis and codegen. Encapsulates the loaded models
 * (from [UsingDefinition]s) and provides type resolution, QName mapping, and context lookup —
 * eliminating the 5+ duplicated model-iteration loops previously scattered across [TypeResolver]
 * and `EmissionContext`.
 *
 * Built once per library from the [UsingDefinition] list and the [ModelManager]. Immutable after
 * construction — the set of loaded models does not change during analysis or emission.
 *
 * The System model is implicit per the CQL spec, so a [ModelContext] always exists — even for
 * libraries that declare no `using` statements. Use [systemOnly] for that case.
 */
class ModelContext
private constructor(
    private val modelManager: ModelManager?,
    private val loadedModels: List<LoadedModel>,
) {
    /** ELM types namespace — the namespace for System types in all scenarios. */
    val typesNamespace = "urn:hl7-org:elm-types:r1"

    companion object {
        /**
         * Create a ModelContext from a [ModelManager] and the library's [UsingDefinition]s. Models
         * that fail to resolve are silently dropped — downstream phases (TypeResolver,
         * SemanticValidator) will report more specific errors for unresolvable types.
         */
        operator fun invoke(
            modelManager: ModelManager,
            usingDefinitions: List<UsingDefinition>,
        ): ModelContext {
            val loaded =
                usingDefinitions
                    .filter { it.modelIdentifier.simpleName != "System" }
                    .mapNotNull { usingDef ->
                        val name = usingDef.modelIdentifier.simpleName
                        val version = usingDef.version?.value
                        try {
                            val model = modelManager.resolveModel(name, version)
                            LoadedModel(name, version, model)
                        } catch (_: Exception) {
                            null
                        }
                    }
            return ModelContext(modelManager, loaded)
        }

        /**
         * System-only ModelContext for libraries with no model declarations. All model-iteration
         * methods return null/empty. Methods that require a [ModelManager] (e.g., [resolveModel])
         * throw — they are only reachable when models are loaded, which requires a ModelManager.
         */
        fun systemOnly(): ModelContext =
            ModelContext(modelManager = null, loadedModels = emptyList())
    }

    /**
     * Model names in load order, for codegen contexts that need the list (e.g., emitUsings). Does
     * not include "System".
     */
    val loadedModelNames: List<String>
        get() = loadedModels.map { it.name }

    // ----- Type Resolution -----

    /**
     * Resolve a type by simple name from loaded (non-System) models. Returns the first match across
     * all loaded models, or null if none match.
     */
    fun resolveTypeName(typeName: String): DataType? {
        for (lm in loadedModels) {
            val dt = lm.model.resolveTypeName(typeName)
            if (dt != null) return dt
        }
        return null
    }

    /**
     * Resolve a type optionally qualified by model name. When [modelName] is null, searches all
     * loaded models. When non-null, resolves from that specific model only.
     */
    fun resolveModelType(modelName: String?, typeName: String): DataType? {
        if (modelName != null) {
            val mm = requireModelManager("resolveModelType with explicit model name")
            val model =
                try {
                    mm.resolveModel(modelName, null)
                } catch (_: Exception) {
                    return null
                }
            return model.resolveTypeName(typeName)
        }
        return resolveTypeName(typeName)
    }

    /**
     * Resolve the type of a context identifier (e.g., "Patient") from the loaded models. Returns
     * the context's type, or null if no model defines this context.
     */
    fun resolveContextType(contextName: String): DataType? {
        for (lm in loadedModels) {
            val ctx = lm.model.resolveContextName(contextName, mustResolve = false)
            if (ctx != null) return ctx.type
        }
        return null
    }

    /**
     * Resolve a retrieve expression's type: finds the model that owns [typeName] and returns
     * `ListType(dataType)`. Mirrors the original `TypeResolver.onRetrieve` pattern.
     */
    fun resolveRetrieveType(typeName: String): ListType? {
        for (lm in loadedModels) {
            val dt = lm.model.resolveTypeName(typeName)
            if (dt != null) return ListType(dt)
        }
        return null
    }

    // ----- Model Lookup -----

    /**
     * Resolve which [Model] provides a given type name. Used by codegen (Retrieve emission) when it
     * needs the full model object for templateId / URL extraction. Throws if no model can resolve
     * the type.
     */
    fun resolveModelForType(typeName: String): Model {
        for (lm in loadedModels) {
            if (lm.model.resolveTypeName(typeName) != null) {
                return lm.model
            }
        }
        throw IllegalArgumentException("Could not resolve type '$typeName' in any loaded model.")
    }

    /**
     * Resolve a model by name directly. Used by desugar phases (e.g., AgeIn) that need
     * model-specific metadata like patientBirthDatePropertyName.
     */
    fun resolveModel(modelName: String, version: String? = null): Model =
        requireModelManager("resolveModel").resolveModel(modelName, version)

    // ----- QName / TypeSpecifier Mapping -----

    /**
     * Resolve a simple type name to an ELM [QName] with the correct namespace. Checks loaded models
     * first (since system types are handled by the caller via [OperatorRegistry]). Falls back to
     * [typesNamespace] if no model claims the type.
     */
    fun typeNameToQName(typeName: String): QName {
        for (lm in loadedModels) {
            val dt = lm.model.resolveTypeName(typeName)
            if (dt != null && dt is NamedType) {
                return modelTypeToQName(lm.model, dt)
            }
        }
        // Fallback: assume system type namespace
        return QName(typesNamespace, typeName)
    }

    /**
     * Convert a resolved [DataType] to a [QName] with the correct namespace. For non-System named
     * types, resolves the namespace from the owning model's modelInfo. Throws if [type] is not a
     * [NamedType].
     */
    fun dataTypeToQName(type: DataType): QName {
        require(type is NamedType) { "A named type is required in this context." }
        if (type.namespace == "System") {
            // System types use the ELM types namespace; target name mapping is identity.
            return QName(typesNamespace, type.target ?: type.simpleName)
        }
        // Find the model that owns this type
        for (lm in loadedModels) {
            if (lm.model.resolveTypeName(type.simpleName) != null) {
                return modelTypeToQName(lm.model, type)
            }
        }
        // Fallback: use ELM types namespace (may be wrong, but matches legacy behavior)
        return QName(typesNamespace, type.target ?: type.simpleName)
    }

    /**
     * Convert a resolved [DataType] to an ELM TypeSpecifier. For [NamedType], uses
     * [dataTypeToQName] for correct namespace. For other types, delegates to [typeBuilder].
     */
    fun dataTypeToTypeSpecifier(
        type: DataType,
        typeBuilder: org.cqframework.cql.cql2elm.TypeBuilder,
    ): org.hl7.elm.r1.TypeSpecifier {
        if (type is NamedType) {
            return org.hl7.elm.r1.NamedTypeSpecifier().withName(dataTypeToQName(type)).apply {
                resultType = type
            }
        }
        return typeBuilder.dataTypeToTypeSpecifier(type)
    }

    // ----- Internals -----

    private fun requireModelManager(operation: String): ModelManager =
        modelManager
            ?: throw IllegalStateException(
                "ModelManager required for $operation, but this is a system-only ModelContext. " +
                    "Ensure the library declares a 'using' statement when model resolution is needed."
            )

    private fun modelTypeToQName(model: Model, namedType: NamedType): QName {
        val modelInfo = model.modelInfo
        val ns = modelInfo.targetUrl ?: modelInfo.url ?: typesNamespace
        val simpleName = namedType.target ?: namedType.simpleName
        return QName(ns, simpleName)
    }

    private data class LoadedModel(val name: String, val version: String?, val model: Model)
}
