package org.cqframework.cql.cql2elm

import java.nio.file.Path
import java.util.*
import kotlin.collections.ArrayList
import org.hl7.cql.model.ModelIdentifier
import org.hl7.cql.model.ModelInfoProvider
import org.hl7.cql.model.NamespaceAware
import org.hl7.cql.model.NamespaceManager
import org.hl7.elm_modelinfo.r1.ModelInfo

class ModelInfoLoader : NamespaceAware, PathAware {
    private var path: Path? = null
    private var namespaceManager: NamespaceManager? = null
    private val providers: MutableList<ModelInfoProvider> = ArrayList()
    private var initialized = false

    private fun getProviders(): List<ModelInfoProvider> {
        if (!initialized) {
            initialized = true
            val it = ModelInfoProviderFactory.providers(false)
            while (it.hasNext()) {
                val provider = it.next()
                registerModelInfoProvider(provider)
            }
        }
        return providers
    }

    fun getModelInfo(modelIdentifier: ModelIdentifier): ModelInfo {
        var modelInfo: ModelInfo? = null
        for (provider in getProviders()) {
            modelInfo = provider.load(modelIdentifier)
            if (modelInfo != null) {
                break
            }
        }
        requireNotNull(modelInfo) {
            String.format(
                Locale.US,
                "Could not resolve model info provider for model %s, version %s.",
                if (modelIdentifier.system == null) modelIdentifier.id
                else NamespaceManager.getPath(modelIdentifier.system, modelIdentifier.id),
                modelIdentifier.version
            )
        }
        return modelInfo
    }

    @JvmOverloads
    fun registerModelInfoProvider(provider: ModelInfoProvider, priority: Boolean = false) {
        if (namespaceManager != null && provider is NamespaceAware) {
            provider.setNamespaceManager(namespaceManager!!)
        }
        if (path != null && provider is PathAware) {
            provider.setPath(path!!)
        }

        if (priority) {
            providers.add(0, provider)
        } else {
            providers.add(provider)
        }
    }

    fun unregisterModelInfoProvider(provider: ModelInfoProvider) {
        providers.remove(provider)
    }

    fun clearModelInfoProviders() {
        providers.clear()
        initialized = false
    }

    override fun setNamespaceManager(namespaceManager: NamespaceManager) {
        this.namespaceManager = namespaceManager
        for (provider in getProviders()) {
            if (provider is NamespaceAware) {
                (provider as NamespaceAware).setNamespaceManager(namespaceManager)
            }
        }
    }

    override fun setPath(path: Path) {
        require(path.toFile().isDirectory) {
            String.format(Locale.US, "path '%s' is not a valid directory", path)
        }
        this.path = path
        for (provider in getProviders()) {
            if (provider is PathAware) {
                provider.setPath(path)
            }
        }
    }
}