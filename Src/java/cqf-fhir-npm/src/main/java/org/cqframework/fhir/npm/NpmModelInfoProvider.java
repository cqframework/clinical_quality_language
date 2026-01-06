package org.cqframework.fhir.npm;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.hl7.cql.model.ModelIdentifier;
import org.hl7.cql.model.ModelInfoProvider;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReaderFactory;
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
                var identifier = new ModelIdentifier()
                        .withId(modelIdentifier.getId())
                        .withVersion(modelIdentifier.getVersion())
                        .withSystem(modelIdentifier.getSystem());

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
                            InputStream is = new ByteArrayInputStream(a.getData());
                            ModelInfo mi = ModelInfoReaderFactory.getReader("application/xml")
                                    .read(is);
                            // Set the identifier system to the url of the loaded model info, not the package canonical
                            // since the library with the model info may not be in the same package as the ig
                            if (mi != null && mi.getUrl() != null && modelIdentifier.getSystem() == null) {
                                modelIdentifier.setSystem(mi.getUrl());
                            }
                            return mi;
                        }
                    }
                }
            } catch (IOException e) {
                logger.logDebugMessage(
                        ILoggingService.LogCategory.PROGRESS,
                        String.format(
                                "Exceptions occurred attempting to load npm library for model %s",
                                modelIdentifier.toString()));
            }
        }

        return null;
    }
}
