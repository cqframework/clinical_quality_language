@file:OptIn(ExperimentalJsExport::class)

package org.cqframework.cql.cql2elm

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.jvm.JvmOverloads
import kotlinx.io.files.Path
import org.cqframework.cql.cql2elm.model.Model
import org.cqframework.cql.cql2elm.model.SystemModel
import org.cqframework.cql.cql2elm.utils.createConcurrentHashMap
import org.cqframework.cql.shared.JsOnlyExport
import org.hl7.cql.model.ModelIdentifier
import org.hl7.cql.model.NamespaceManager

/** Created by Bryn on 12/29/2016. */
@JsOnlyExport
@Suppress("NON_EXPORTABLE_TYPE")
class ModelManager
@JvmOverloads
constructor(
    val namespaceManager: NamespaceManager = NamespaceManager(),
    val enableDefaultModelInfoLoading: Boolean = true,
    val path: Path? = null,
    /**
     * The global cache is by [org.hl7.cql.model.ModelIdentifier], while the local cache is by name.
     * This is because the translator expects the [ModelManager] to only permit loading of a single
     * version of a given [Model] in a single translation context, while the global cache is for all
     * versions of [Model]s
     */
    val globalCache: MutableMap<ModelIdentifier, Model> = createConcurrentHashMap(),
) {
    val modelInfoLoader: ModelInfoLoader =
        ModelInfoLoader().also {
            it.setNamespaceManager(namespaceManager)
            if (path != null) {
                it.setPath(path)
            }
        }
    private val models = HashMap<String, Model>()
    private val loadingModels = HashSet<String>()
    private val modelsByUri = HashMap<String, Model>()

    /** @param globalCache cache for Models by ModelIdentifier. Expected to be thread-safe. */
    @JsExport.Ignore
    constructor(
        globalCache: MutableMap<ModelIdentifier, Model>
    ) : this(NamespaceManager(), true, null, globalCache)

    @JvmOverloads
    @JsExport.Ignore
    constructor(
        path: Path?,
        globalCache: MutableMap<ModelIdentifier, Model> = createConcurrentHashMap()
    ) : this(NamespaceManager(), true, path, globalCache)

    @JsExport.Ignore
    constructor(
        enableDefaultModelInfoLoading: Boolean,
        globalCache: MutableMap<ModelIdentifier, Model>
    ) : this(NamespaceManager(), enableDefaultModelInfoLoading, null, globalCache)

    @JvmOverloads
    @JsExport.Ignore
    constructor(
        enableDefaultModelInfoLoading: Boolean,
        path: Path? = null,
        globalCache: MutableMap<ModelIdentifier, Model> = createConcurrentHashMap()
    ) : this(NamespaceManager(), enableDefaultModelInfoLoading, path, globalCache)

    @JsExport.Ignore
    constructor(
        namespaceManager: NamespaceManager,
        globalCache: MutableMap<ModelIdentifier, Model>
    ) : this(namespaceManager, true, null, globalCache)

    @JvmOverloads
    @JsExport.Ignore
    constructor(
        namespaceManager: NamespaceManager,
        path: Path?,
        globalCache: MutableMap<ModelIdentifier, Model> = createConcurrentHashMap()
    ) : this(namespaceManager, true, path, globalCache)

    @JsExport.Ignore
    constructor(
        namespaceManager: NamespaceManager,
        enableDefaultModelInfoLoading: Boolean,
        globalCache: MutableMap<ModelIdentifier, Model>
    ) : this(namespaceManager, enableDefaultModelInfoLoading, null, globalCache)

    /**
     * A "well-known" model name is one that is allowed to resolve without a namespace in a
     * namespace-aware context
     */
    @Suppress("ReturnCount")
    fun isWellKnownModelName(unqualifiedIdentifier: String?): Boolean {
        if (unqualifiedIdentifier == null) {
            return false
        }

        when (unqualifiedIdentifier) {
            "FHIR",
            "QDM",
            "USCore",
            "QICore",
            "QUICK" -> return true
            else -> return false
        }
    }

    private fun buildModel(identifier: ModelIdentifier): Model {
        var model: Model?
        require(identifier.id.isNotEmpty()) { "Model identifier Id is required" }
        val modelPath = NamespaceManager.getPath(identifier.system, identifier.id)
        pushLoading(modelPath)
        try {
            val modelInfo = modelInfoLoader.getModelInfo(identifier)
            if (identifier.id.equals("System")) {
                model = SystemModel(modelInfo)
            } else {
                model = Model(modelInfo, this)
            }
        } catch (@Suppress("TooGenericExceptionCaught", "SwallowedException") e: Exception) {
            throw IllegalArgumentException(
                "Could not load model information for model ${identifier.id}, version ${identifier.version}."
            )
        } finally {
            popLoading(modelPath)
        }

        return model
    }

    private fun pushLoading(modelId: String) {
        require(!loadingModels.contains(modelId)) { "Circular model reference $modelId" }
        loadingModels.add(modelId)
    }

    private fun popLoading(modelId: String) {
        loadingModels.remove(modelId)
    }

    @JvmOverloads
    @JsExport.Ignore
    fun resolveModel(modelName: String, version: String? = null): Model {
        return resolveModel(ModelIdentifier(modelName, null, version))
    }

    /**
     * @param modelIdentifier the identifier of the model to resolve
     * @return the model
     * @throws IllegalArgumentException if an attempt to resolve multiple versions of the same model
     *   is made or if the model that resolved is not compatible with the requested version
     */
    @JsExport.Ignore
    fun resolveModel(modelIdentifier: ModelIdentifier): Model {
        val modelPath = NamespaceManager.getPath(modelIdentifier.system, modelIdentifier.id)
        var model = models.get(modelPath)
        if (model != null) {
            checkModelVersion(modelIdentifier, model)
        }

        if (model == null && this.globalCache.containsKey(modelIdentifier)) {
            model = this.globalCache.get(modelIdentifier)
            models.put(modelPath, model!!)
            modelsByUri.put(model.modelInfo.url!!, model)
        }

        if (model == null) {
            model = buildModel(modelIdentifier)
            this.globalCache.put(modelIdentifier, model)
            checkModelVersion(modelIdentifier, model)
            models.put(modelPath, model)
            modelsByUri.put(model.modelInfo.url!!, model)
        }

        return model
    }

    private fun checkModelVersion(modelIdentifier: ModelIdentifier, model: Model) {
        require(
            !(modelIdentifier.version != null &&
                !modelIdentifier.version.equals(model.modelInfo.version))
        ) {
            @Suppress("MaxLineLength")
            "Could not load model information for model ${modelIdentifier.id}, version ${modelIdentifier.version} because version ${model.modelInfo.version} is already loaded."
        }
    }

    fun resolveModelByUri(namespaceUri: String): Model {
        val model = modelsByUri.get(namespaceUri)
        requireNotNull(model) { "Could not resolve model with namespace $namespaceUri" }

        return model
    }
}
