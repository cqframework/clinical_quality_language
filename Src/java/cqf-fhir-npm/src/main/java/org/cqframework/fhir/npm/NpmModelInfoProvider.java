package org.cqframework.fhir.npm;

import static kotlinx.io.CoreKt.buffered;
import static kotlinx.io.JvmCoreKt.asSource;
import static org.hl7.elm_modelinfo.r1.serializing.XmlModelInfoReaderKt.parseModelInfoXml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.hl7.cql.model.ModelIdentifier;
import org.hl7.cql.model.ModelInfoProvider;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.hl7.fhir.r5.context.ILoggingService;
import org.hl7.fhir.r5.model.Library;
import org.hl7.fhir.utilities.npm.NpmPackage;

/**
 * Provides a model info provider that can resolve CQL model info from an Npm package
 */
public class NpmModelInfoProvider implements ModelInfoProvider {

    public NpmModelInfoProvider(List<NpmPackage> packages, ILibraryReader reader, ILoggingService logger) {
        this.packages = packages;
        this.reader = reader;
        this.logger = logger;
    }

    private List<NpmPackage> packages;
    private ILibraryReader reader;
    private ILoggingService logger;

    public ModelInfo load(ModelIdentifier modelIdentifier) {
        // VersionedIdentifier.id: Name of the model
        // VersionedIdentifier.system: Namespace for the model, as a URL
        // VersionedIdentifier.version: Version of the model
        for (NpmPackage p : packages) {
            try {
                var identifier = new ModelIdentifier(
                        modelIdentifier.getId(), modelIdentifier.getSystem(), modelIdentifier.getVersion());

                if (identifier.getSystem() == null) {
                    identifier.setSystem(p.canonical());
                }

                InputStream s = p.loadByCanonicalVersion(
                        identifier.getSystem() + "/Library/" + identifier.getId() + "-ModelInfo",
                        identifier.getVersion());
                if (s != null) {
                    Library l = reader.readLibrary(s);
                    for (org.hl7.fhir.r5.model.Attachment a : l.getContent()) {
                        if (a.getContentType() != null && a.getContentType().equals("application/xml")) {
                            if (modelIdentifier.getSystem() == null) {
                                modelIdentifier.setSystem(identifier.getSystem());
                            }
                            InputStream is = new ByteArrayInputStream(a.getData());
                            var source = buffered(asSource(is));
                            return parseModelInfoXml(source);
                        }
                    }
                }
            } catch (IOException e) {
                logger.logDebugMessage(
                        ILoggingService.LogCategory.PROGRESS,
                        String.format(
                                "Exceptions occurred attempting to load npm library for model %s", modelIdentifier));
            }
        }

        return null;
    }
}
