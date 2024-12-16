@file:Suppress("WildcardImport")

package org.cqframework.cql.cql2elm

import java.nio.file.Path
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import org.cqframework.cql.cql2elm.model.*
import org.hl7.cql.model.*

/** Created by Bryn on 12/29/2016. */
@Suppress("TooManyFunctions")
class ModelManager {
    val namespaceManager: NamespaceManager
    private var path: Path? = null
    var modelInfoLoader: ModelInfoLoader? = null
        private set

    private val models: MutableMap<String, Model?> = HashMap()
    private val loadingModels: MutableSet<String> = HashSet()
    private val modelsByUri: MutableMap<String, Model?> = HashMap()
    private val globalCache: MutableMap<ModelIdentifier, Model?>
    var isDefaultModelInfoLoadingEnabled = true
        private set

    constructor() {
        namespaceManager = NamespaceManager()
        globalCache = ConcurrentHashMap()
        initialize()
    }

    /** @param globalCache cache for Models by ModelIdentifier. Expected to be thread-safe. */
    constructor(globalCache: MutableMap<ModelIdentifier, Model?>) {
        namespaceManager = NamespaceManager()
        this.globalCache = globalCache
        initialize()
    }

    constructor(path: Path?) {
        namespaceManager = NamespaceManager()
        globalCache = ConcurrentHashMap()
        this.path = path
        initialize()
    }

    constructor(path: Path?, globalCache: MutableMap<ModelIdentifier, Model?>) {
        namespaceManager = NamespaceManager()
        this.globalCache = globalCache
        this.path = path
        initialize()
    }

    constructor(enableDefaultModelInfoLoading: Boolean) {
        namespaceManager = NamespaceManager()
        globalCache = ConcurrentHashMap()
        isDefaultModelInfoLoadingEnabled = enableDefaultModelInfoLoading
        initialize()
    }

    constructor(
        enableDefaultModelInfoLoading: Boolean,
        globalCache: MutableMap<ModelIdentifier, Model?>
    ) {
        namespaceManager = NamespaceManager()
        this.globalCache = globalCache
        isDefaultModelInfoLoadingEnabled = enableDefaultModelInfoLoading
        initialize()
    }

    constructor(enableDefaultModelInfoLoading: Boolean, path: Path?) {
        namespaceManager = NamespaceManager()
        globalCache = ConcurrentHashMap()
        this.path = path
        isDefaultModelInfoLoadingEnabled = enableDefaultModelInfoLoading
        initialize()
    }

    constructor(
        enableDefaultModelInfoLoading: Boolean,
        path: Path?,
        globalCache: MutableMap<ModelIdentifier, Model?>
    ) {
        namespaceManager = NamespaceManager()
        this.globalCache = globalCache
        this.path = path
        isDefaultModelInfoLoadingEnabled = enableDefaultModelInfoLoading
        initialize()
    }

    constructor(namespaceManager: NamespaceManager) {
        this.namespaceManager = namespaceManager
        globalCache = ConcurrentHashMap()
        initialize()
    }

    constructor(
        namespaceManager: NamespaceManager,
        globalCache: MutableMap<ModelIdentifier, Model?>
    ) {
        this.namespaceManager = namespaceManager
        this.globalCache = globalCache
        initialize()
    }

    constructor(namespaceManager: NamespaceManager, path: Path?) {
        this.namespaceManager = namespaceManager
        globalCache = ConcurrentHashMap()
        this.path = path
        initialize()
    }

    constructor(
        namespaceManager: NamespaceManager,
        path: Path?,
        globalCache: MutableMap<ModelIdentifier, Model?>
    ) {
        this.namespaceManager = namespaceManager
        this.globalCache = globalCache
        this.path = path
        initialize()
    }

    constructor(namespaceManager: NamespaceManager, enableDefaultModelInfoLoading: Boolean) {
        this.namespaceManager = namespaceManager
        globalCache = ConcurrentHashMap()
        isDefaultModelInfoLoadingEnabled = enableDefaultModelInfoLoading
        initialize()
    }

    constructor(
        namespaceManager: NamespaceManager,
        enableDefaultModelInfoLoading: Boolean,
        globalCache: MutableMap<ModelIdentifier, Model?>
    ) {
        this.namespaceManager = namespaceManager
        this.globalCache = globalCache
        isDefaultModelInfoLoadingEnabled = enableDefaultModelInfoLoading
        initialize()
    }

    constructor(
        namespaceManager: NamespaceManager,
        enableDefaultModelInfoLoading: Boolean,
        path: Path?
    ) {
        this.namespaceManager = namespaceManager
        globalCache = ConcurrentHashMap()
        this.path = path
        isDefaultModelInfoLoadingEnabled = enableDefaultModelInfoLoading
        initialize()
    }

    constructor(
        namespaceManager: NamespaceManager,
        enableDefaultModelInfoLoading: Boolean,
        path: Path?,
        globalCache: MutableMap<ModelIdentifier, Model?>
    ) {
        this.namespaceManager = namespaceManager
        this.globalCache = globalCache
        this.path = path
        isDefaultModelInfoLoadingEnabled = enableDefaultModelInfoLoading
        initialize()
    }

    private fun initialize() {
        modelInfoLoader = ModelInfoLoader()
        modelInfoLoader!!.setNamespaceManager(namespaceManager)
        if (path != null) {
            modelInfoLoader!!.setPath(path!!)
        }
    }

    /**
     * The global cache is by @{org.hl7.cql.model.ModelIdentifier}, while the local cache is by
     * name. This is because the translator expects the ModelManager to only permit loading of a
     * single version of a given Model in a single translation context, while the global cache is
     * for all versions of Models
     */
    fun getGlobalCache(): Map<ModelIdentifier, Model?> {
        return globalCache
    }

    /*
    A "well-known" model name is one that is allowed to resolve without a namespace in a namespace-aware context
     */
    fun isWellKnownModelName(unqualifiedIdentifier: String?): Boolean {
        return if (unqualifiedIdentifier == null) {
            false
        } else
            when (unqualifiedIdentifier) {
                "FHIR",
                "QDM",
                "USCore",
                "QICore",
                "QUICK" -> true
                else -> false
            }
    }

    private fun buildModel(identifier: ModelIdentifier): Model? {
        val model: Model?
        require(identifier.id.isNotEmpty()) { "Model identifier Id is required" }
        val modelPath = NamespaceManager.getPath(identifier.system, identifier.id)
        pushLoading(modelPath)
        model =
            try {
                val modelInfo = modelInfoLoader!!.getModelInfo(identifier)
                if (identifier.id == "System") {
                    SystemModel(modelInfo)
                } else {
                    Model(modelInfo, this)
                }
            } finally {
                popLoading(modelPath)
            }
        return model
    }

    private fun pushLoading(modelId: String) {
        require(!loadingModels.contains(modelId)) {
            String.format(Locale.US, "Circular model reference %s", modelId)
        }
        loadingModels.add(modelId)
    }

    private fun popLoading(modelId: String) {
        loadingModels.remove(modelId)
    }

    @JvmOverloads
    fun resolveModel(modelName: String, version: String? = null): Model {
        return resolveModel(ModelIdentifier(modelName, version = version))
    }

    /**
     * @param modelIdentifier the identifier of the model to resolve
     * @return the model
     * @throws IllegalArgumentException if an attempt to resolve multiple versions of the same model
     *   is made or if the model that resolved is not compatible with the requested version
     */
    fun resolveModel(modelIdentifier: ModelIdentifier): Model {
        val modelPath = NamespaceManager.getPath(modelIdentifier.system, modelIdentifier.id)
        var model = models[modelPath]
        model?.let { checkModelVersion(modelIdentifier, it) }
        if (model == null && globalCache.containsKey(modelIdentifier)) {
            model = globalCache[modelIdentifier]
            models[modelPath] = model
        }
        if (model == null) {
            model = buildModel(modelIdentifier)
            globalCache[modelIdentifier] = model
            checkModelVersion(modelIdentifier, model)
            models[modelPath] = model
            modelsByUri[model!!.modelInfo.url] = model
        }
        return model
    }

    private fun checkModelVersion(modelIdentifier: ModelIdentifier, model: Model?) {
        require(
            !(modelIdentifier.version != null &&
                modelIdentifier.version != model!!.modelInfo.version)
        ) {
            String.format(
                Locale.US,
                "Could not load model information for model %s, version %s because version %s is already loaded.",
                modelIdentifier.id,
                modelIdentifier.version,
                model!!.modelInfo.version
            )
        }
    }

    fun resolveModelByUri(namespaceUri: String): Model {
        return modelsByUri[namespaceUri]
            ?: throw IllegalArgumentException(
                String.format(Locale.US, "Could not resolve model with namespace %s", namespaceUri)
            )
    }
}
