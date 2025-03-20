package org.cqframework.cql.cql2elm

import java.nio.file.Path
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import org.cqframework.cql.cql2elm.model.*
import org.hl7.cql.model.*

/** Created by Bryn on 12/29/2016. */
@Suppress("TooManyFunctions")
class ModelManager : CommonModelManager {
    val namespaceManager: NamespaceManager
    private var path: Path? = null
    var modelInfoLoader: ModelInfoLoader? = null
        private set

    private val models: MutableMap<String, Model> = HashMap()
    private val loadingModels: MutableSet<String> = HashSet()
    private val modelsByUri: MutableMap<String, Model> = HashMap()

    /**
     * The global cache is by @{org.hl7.cql.model.ModelIdentifier}, while the local cache is by
     * name. This is because the translator expects the ModelManager to only permit loading of a
     * single version of a given Model in a single translation context, while the global cache is
     * for all versions of Models
     */
    private val globalCache: MutableMap<ModelIdentifier, Model>
    private var isDefaultModelInfoLoadingEnabled = true

    constructor() {
        namespaceManager = NamespaceManager()
        globalCache = ConcurrentHashMap()
        initialize()
    }

    /** @param globalCache cache for Models by ModelIdentifier. Expected to be thread-safe. */
    constructor(globalCache: MutableMap<ModelIdentifier, Model>) {
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

    constructor(path: Path?, globalCache: MutableMap<ModelIdentifier, Model>) {
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
        globalCache: MutableMap<ModelIdentifier, Model>
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
        globalCache: MutableMap<ModelIdentifier, Model>
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
        globalCache: MutableMap<ModelIdentifier, Model>
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
        globalCache: MutableMap<ModelIdentifier, Model>
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
        globalCache: MutableMap<ModelIdentifier, Model>
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
        globalCache: MutableMap<ModelIdentifier, Model>
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

    private fun buildModel(identifier: ModelIdentifier): Model? {
        val model: Model?
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
        require(!loadingModels.contains(modelId)) { "Circular model reference $modelId" }
        loadingModels.add(modelId)
    }

    private fun popLoading(modelId: String) {
        loadingModels.remove(modelId)
    }

    override fun resolveModel(modelName: String): Model {
        return resolveModel(ModelIdentifier(modelName, version = null))
    }

    override fun resolveModel(modelName: String, version: String?): Model {
        return resolveModel(ModelIdentifier(modelName, version = version))
    }

    /**
     * @param modelIdentifier the identifier of the model to resolve
     * @return the model
     * @throws IllegalArgumentException if an attempt to resolve multiple versions of the same model
     *   is made or if the model that resolved is not compatible with the requested version
     */
    override fun resolveModel(modelIdentifier: ModelIdentifier): Model {
        val modelPath = NamespaceManager.getPath(modelIdentifier.system, modelIdentifier.id)
        var model = models[modelPath]
        model?.let { checkModelVersion(modelIdentifier, it) }
        if (model == null && globalCache.containsKey(modelIdentifier)) {
            model = globalCache[modelIdentifier]
            models[modelPath] = model!!
            modelsByUri[model.modelInfo.url!!] = model
        }

        if (model == null) {
            model = buildModel(modelIdentifier)
            globalCache[modelIdentifier] = model!!
            checkModelVersion(modelIdentifier, model)
            models[modelPath] = model
            modelsByUri[model.modelInfo.url!!] = model
        }
        return model
    }

    private fun checkModelVersion(modelIdentifier: ModelIdentifier, model: Model?) {
        require(
            modelIdentifier.version == null || modelIdentifier.version == model!!.modelInfo.version
        ) {
            "Could not load model information for model ${modelIdentifier.id}, version ${modelIdentifier.version}" +
                " because version ${model!!.modelInfo.version} is already loaded."
        }
    }

    override fun resolveModelByUri(namespaceUri: String): Model {
        return modelsByUri[namespaceUri]
            ?: throw IllegalArgumentException(
                "Could not resolve model with namespace $namespaceUri"
            )
    }
}
