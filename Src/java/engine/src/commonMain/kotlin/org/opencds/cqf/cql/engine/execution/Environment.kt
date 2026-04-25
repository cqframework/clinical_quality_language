package org.opencds.cqf.cql.engine.execution

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsName
import kotlin.jvm.JvmOverloads
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.shared.JsOnlyExport
import org.cqframework.cql.shared.QName
import org.hl7.elm.r1.*
import org.opencds.cqf.cql.engine.data.DataProvider
import org.opencds.cqf.cql.engine.data.ExternalFunctionProvider
import org.opencds.cqf.cql.engine.data.SystemDataProvider
import org.opencds.cqf.cql.engine.exception.CqlException
import org.opencds.cqf.cql.engine.runtime.CqlType
import org.opencds.cqf.cql.engine.runtime.systemModelNamespaceUri
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider

/**
 * The Environment class represents the current CQL execution environment. Meaning, things that are
 * set up outside of the CQL engine
 */
@OptIn(ExperimentalJsExport::class)
@JsOnlyExport
@Suppress("NON_EXPORTABLE_TYPE")
class Environment
@JvmOverloads
constructor(
    val libraryManager: LibraryManager?,
    dataProviders: MutableMap<String?, DataProvider?>? = null,
    val terminologyProvider: TerminologyProvider? = null,
) {
    val dataProviders = mutableMapOf<String?, DataProvider?>()

    // -- ExternalFunctionProviders -- TODO the registration of these... Should be
    // part of the LibraryManager?
    //
    private val externalFunctionProviders = HashMap<VersionedIdentifier, ExternalFunctionProvider>()

    // External function provider
    init {
        if (dataProviders != null) {
            for (dp in dataProviders.entries) {
                this.registerDataProvider(dp.key, dp.value!!)
            }
        }

        if (!this.dataProviders.containsKey(systemModelNamespaceUri)) {
            this.registerDataProvider(systemModelNamespaceUri, SystemDataProvider())
        }
    }

    fun registerExternalFunctionProvider(
        identifier: VersionedIdentifier,
        provider: ExternalFunctionProvider,
    ) {
        externalFunctionProviders[identifier] = provider
    }

    fun getExternalFunctionProvider(identifier: VersionedIdentifier?): ExternalFunctionProvider {
        val provider =
            externalFunctionProviders[identifier]
                ?: throw CqlException(
                    "Could not resolve external function provider for library '${identifier}'."
                )
        return provider
    }

    // -- DataProvider "Helpers"

    fun createInstance(typeName: QName): CqlType? {
        var typeName = typeName
        typeName = fixupQName(typeName)
        val dataProvider = resolveDataProvider(typeName)
        return dataProvider.createInstance(typeName.getLocalPart())
    }

    // -- DataProvider resolution
    fun registerDataProvider(modelUri: String?, dataProvider: DataProvider?) {
        dataProviders[modelUri] = dataProvider
    }

    @JsName("resolveDataProviderByQName")
    fun resolveDataProvider(dataType: QName): DataProvider {
        var dataType = dataType
        dataType = fixupQName(dataType)
        return resolveDataProviderByModelUri(dataType.getNamespaceURI())
    }

    fun resolveDataProviderByModelUri(modelUri: String?): DataProvider {
        return resolveDataProviderByModelUriOrNull(modelUri)
            ?: throw CqlException("Could not resolve data provider for model '${modelUri}'.")
    }

    fun resolveDataProviderByModelUriOrNull(modelUri: String?): DataProvider? {
        return dataProviders[modelUri]
    }

    fun fixupQName(typeName: QName): QName {
        // When a Json library is deserialized on Android
        if (typeName.getNamespaceURI().isEmpty()) {
            if (typeName.getLocalPart().startsWith("{")) {
                val closeIndex = typeName.getLocalPart().indexOf('}')
                if (closeIndex > 0 && typeName.getLocalPart().length > closeIndex) {
                    return QName(
                        typeName.getLocalPart().substring(1, closeIndex),
                        typeName.getLocalPart().substring(closeIndex + 1),
                    )
                }
            }
        }

        return typeName
    }

    fun resolveLibrary(identifier: VersionedIdentifier): Library? {
        return this.libraryManager!!.resolveLibrary(identifier).library
    }
}
