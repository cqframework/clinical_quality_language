package org.cqframework.cql.cql2elm

import kotlinx.io.Source
import org.cqframework.cql.cql2elm.model.Model
import org.cqframework.cql.cql2elm.model.SystemModel
import org.cqframework.cql.cql2elm.ucum.UcumService
import org.cqframework.cql.cql2elm.utils.asSource
import org.cqframework.cql.elm.serializing.BigDecimal
import org.hl7.cql.model.ModelIdentifier
import org.hl7.cql.model.NamespaceManager
import org.hl7.elm.r1.VersionedIdentifier
import org.hl7.elm_modelinfo.r1.serializing.xmlutil.ModelInfoReaderProvider
import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport

@OptIn(ExperimentalJsExport::class)
@JsExport
fun getSimpleLibraryManager(
    getModelXml: (
        id: String,
        system: String?,
        version: String?
    ) -> String,
    getLibraryCql: (
        id: String,
        system: String?,
        version: String?
    ) -> String? = { _, _, _ -> null },
    validateUnit: (unit: String) -> String? = { null },
    cqlCompilerOptions: CqlCompilerOptions = CqlCompilerOptions.defaultOptions()
): CommonLibraryManager {
    val namespaceManager = NamespaceManager()

    val librarySourceLoader = object : CommonLibrarySourceLoader {
        override fun getLibrarySource(libraryIdentifier: VersionedIdentifier): Source? {
            val cql = getLibraryCql(libraryIdentifier.id!!, libraryIdentifier.system, libraryIdentifier.version)
            return cql?.asSource()
        }

        override fun getLibraryContent(
            libraryIdentifier: VersionedIdentifier,
            type: LibraryContentType
        ): Source? {
            return null
        }
    }

    val modelManager = object : CommonModelManager {
        private val globalCache: MutableMap<ModelIdentifier, Model> = HashMap()
        private val modelsByUri: MutableMap<String, Model> = HashMap()

        override fun resolveModel(modelIdentifier: ModelIdentifier): Model {
            if (globalCache.containsKey(modelIdentifier)) {
                return globalCache[modelIdentifier]!!
            }
            val modelXml = getModelXml(modelIdentifier.id, modelIdentifier.system, modelIdentifier.version)
            val modelInfo = ModelInfoReaderProvider().create("application/xml").read(modelXml)
            val model = if (modelIdentifier.id == "System") {
                SystemModel(modelInfo)
            } else {
                Model(modelInfo, this)
            }
            modelsByUri[model.modelInfo.url!!] = model
            globalCache[modelIdentifier] = model
            return model
        }

        override fun resolveModel(modelName: String): Model {
            return resolveModel(ModelIdentifier(modelName, version = null))
        }

        override fun resolveModel(modelName: String, version: String?): Model {
            return resolveModel(ModelIdentifier(modelName, version = version))
        }

        override fun resolveModelByUri(namespaceUri: String): Model {
            return modelsByUri[namespaceUri]!!
        }
    }

    val ucumService = object : UcumService {
        override fun convert(value: BigDecimal, sourceUnit: String, destUnit: String): BigDecimal {
            // We don't expect `convert` to be called during translation
            error("Unexpected call to convert")
        }

        override fun validate(unit: String): String? {
            return validateUnit(unit)
        }
    }

    return CommonLibraryManager(modelManager, namespaceManager, librarySourceLoader, lazy { ucumService }, cqlCompilerOptions)
}
