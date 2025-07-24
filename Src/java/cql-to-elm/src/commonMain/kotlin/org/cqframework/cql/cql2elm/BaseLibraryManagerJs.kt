package org.cqframework.cql.cql2elm

import kotlin.collections.MutableMap
import kotlinx.io.Source
import org.cqframework.cql.cql2elm.model.CompiledLibrary
import org.cqframework.cql.cql2elm.model.Model
import org.cqframework.cql.cql2elm.model.SystemModel
import org.cqframework.cql.cql2elm.ucum.UcumService
import org.cqframework.cql.cql2elm.utils.asSource
import org.cqframework.cql.elm.serializing.ElmLibraryReader
import org.cqframework.cql.elm.serializing.ElmLibraryReaderProvider
import org.cqframework.cql.shared.BigDecimal
import org.hl7.cql.model.ModelIdentifier
import org.hl7.cql.model.NamespaceManager
import org.hl7.elm.r1.VersionedIdentifier
import org.hl7.elm_modelinfo.r1.serializing.parseModelInfoXml

/**
 * A simple library manager factory suitable for JS environments. It accepts simple callbacks and
 * internally builds the library source loader, library manager, model manager, and UCUM service
 * needed to create the library manager.
 *
 * @param getModelXml a callback that returns the model info XML given the model's id, system, and
 *   version
 * @param getLibraryCql a callback that returns the CQL content of a library given its id, system,
 *   and version
 * @param validateUnit a callback for validating a UCUM unit. If the unit is valid, it should return
 *   null, otherwise it should return an error message.
 * @param cqlCompilerOptions the options to use when compiling CQL
 * @param modelCache caches models by their identifiers
 * @param libraryCache caches compiled libraries by their identifiers
 * @return an instance of BaseLibraryManager
 */
@Suppress("LongParameterList")
fun BaseLibraryManager.Companion.forJs(
    getModelXml: (id: String, system: String?, version: String?) -> String?,
    getLibraryCql: (id: String, system: String?, version: String?) -> String? = { _, _, _ -> null },
    validateUnit: (unit: String) -> String? = { null },
    cqlCompilerOptions: CqlCompilerOptions = CqlCompilerOptions.defaultOptions(),
    modelCache: MutableMap<ModelIdentifier, Model> = HashMap(),
    libraryCache: MutableMap<VersionedIdentifier, CompiledLibrary> = HashMap(),
): BaseLibraryManager {
    val namespaceManager = NamespaceManager()

    val librarySourceLoader =
        object : ILibrarySourceLoader {
            override fun getLibrarySource(libraryIdentifier: VersionedIdentifier): Source? {
                val cql =
                    getLibraryCql(
                        libraryIdentifier.id!!,
                        libraryIdentifier.system,
                        libraryIdentifier.version
                    )
                return cql?.asSource()
            }

            override fun getLibraryContent(
                libraryIdentifier: VersionedIdentifier,
                type: LibraryContentType
            ): Source? {
                // The current simple implementation for JS only supports requesting the library CQL
                // source through `getLibraryCql` and not compiled libraries which we would return
                // here.
                return null
            }
        }

    val modelManager =
        object : IModelManager {
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

    val ucumService =
        object : UcumService {
            override fun convert(
                value: BigDecimal,
                sourceUnit: String,
                destUnit: String
            ): BigDecimal {
                // We don't expect `convert` to be called during translation
                error("Unexpected call to convert")
            }

            override fun validate(unit: String): String? {
                return validateUnit(unit)
            }
        }

    val elmLibraryReaderProvider =
        object : ElmLibraryReaderProvider {
            override fun create(contentType: String): ElmLibraryReader {
                // The current simple implementation for JS does not support reading compiled ELM
                // libraries.
                error("Unexpected call to create")
            }
        }

    return BaseLibraryManager(
        modelManager,
        namespaceManager,
        librarySourceLoader,
        lazy { ucumService },
        cqlCompilerOptions,
        libraryCache,
        elmLibraryReaderProvider
    )
}

/**
 * A helper function for enabling a compiler option.
 *
 * @param option the compiler option to add
 */
fun BaseLibraryManager.addCompilerOption(option: String) {
    this.cqlCompilerOptions.options.add(CqlCompilerOptions.Options.valueOf(option))
}

/**
 * A helper function for disabling a compiler option.
 *
 * @param option the compiler option to remove
 */
fun BaseLibraryManager.removeCompilerOption(option: String) {
    this.cqlCompilerOptions.options.remove(CqlCompilerOptions.Options.valueOf(option))
}
