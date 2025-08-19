package org.cqframework.cql.cql2elm

import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator
import kotlin.collections.set
import org.cqframework.cql.cql2elm.model.Model
import org.cqframework.cql.cql2elm.model.SystemModel
import org.hl7.cql.model.ModelIdentifier
import org.hl7.cql.model.NamespaceManager
import org.hl7.elm_modelinfo.r1.serializing.parseModelInfoXml

/**
 * A model manager factory suitable for JS environments.
 *
 * @param getModelXml a callback that returns the model info XML given the model's id, system, and
 *   version
 * @param modelCache caches models by their identifiers
 * @return an instance of [IModelManager]
 */
internal fun createModelManager(
    getModelXml: (id: String, system: String?, version: String?) -> String?,
    modelCache: MutableMap<ModelIdentifier, Model> = HashMap()
): IModelManager {
    return object : IModelManager {
        override fun resolveModel(modelIdentifier: ModelIdentifier): Model {
            if (modelCache.containsKey(modelIdentifier)) {
                return modelCache[modelIdentifier]!!
            }
            val modelXml =
                getModelXml(modelIdentifier.id, modelIdentifier.system, modelIdentifier.version)
            requireNotNull(modelXml) {
                "Could not get model info XML for model ${
                    if (modelIdentifier.system == null) modelIdentifier.id
                    else NamespaceManager.getPath(modelIdentifier.system, modelIdentifier.id)
                }, version ${modelIdentifier.version}."
            }
            val modelInfo = parseModelInfoXml(modelXml)
            val model =
                if (modelIdentifier.id == "System") {
                    SystemModel(modelInfo)
                } else {
                    Model(modelInfo, this)
                }
            modelCache[modelIdentifier] = model
            return model
        }

        override fun resolveModel(modelName: String): Model {
            return resolveModel(ModelIdentifier(modelName, version = null))
        }

        override fun resolveModel(modelName: String, version: String?): Model {
            return resolveModel(ModelIdentifier(modelName, version = version))
        }

        override fun resolveModelByUri(namespaceUri: String): Model {
            for ((_, model) in modelCache) {
                if (model.modelInfo.url == namespaceUri) {
                    return model
                }
            }
            error("Model with URI '$namespaceUri' not found.")
        }
    }
}
