package org.cqframework.fhir.npm

import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import kotlinx.io.asSource
import kotlinx.io.buffered
import org.hl7.cql.model.ModelIdentifier
import org.hl7.cql.model.ModelInfoProvider
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.hl7.elm_modelinfo.r1.serializing.parseModelInfoXml
import org.hl7.fhir.r5.context.ILoggingService
import org.hl7.fhir.utilities.npm.NpmPackage

/** Provides a model info provider that can resolve CQL model info from an Npm package */
class NpmModelInfoProvider(
    private val packages: MutableList<NpmPackage>,
    private val reader: ILibraryReader,
    private val logger: ILoggingService,
) : ModelInfoProvider {
    override fun load(modelIdentifier: ModelIdentifier): ModelInfo? {
        // VersionedIdentifier.id: Name of the model
        // VersionedIdentifier.system: Namespace for the model, as a URL
        // VersionedIdentifier.version: Version of the model
        for (p in packages) {
            try {
                val identifier =
                    ModelIdentifier(
                        modelIdentifier.id,
                        modelIdentifier.system,
                        modelIdentifier.version,
                    )

                if (identifier.system == null) {
                    identifier.system = p.canonical()
                }

                val s =
                    p.loadByCanonicalVersion(
                        identifier.system + "/Library/" + identifier.id + "-ModelInfo",
                        identifier.version,
                    )
                if (s != null) {
                    val l = reader.readLibrary(s)
                    for (a in l!!.getContent()) {
                        if (a.getContentType() != null && a.getContentType() == "application/xml") {
                            if (modelIdentifier.system == null) {
                                modelIdentifier.system = identifier.system
                            }
                            val stream: InputStream = ByteArrayInputStream(a.getData())
                            val source = stream.asSource().buffered()
                            return parseModelInfoXml(source)
                        }
                    }
                }
            } catch (e: IOException) {
                logger.logDebugMessage(
                    ILoggingService.LogCategory.PROGRESS,
                    String.format(
                        "Exceptions occurred attempting to load npm library for model %s",
                        modelIdentifier,
                    ),
                )
            }
        }

        return null
    }
}
