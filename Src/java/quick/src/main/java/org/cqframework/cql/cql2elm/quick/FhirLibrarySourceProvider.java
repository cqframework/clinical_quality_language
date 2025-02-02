package org.cqframework.cql.cql2elm.quick;

import static kotlinx.io.CoreKt.buffered;
import static kotlinx.io.JvmCoreKt.asSource;

import java.io.InputStream;
import kotlinx.io.Source;
import org.cqframework.cql.cql2elm.LibraryContentType;
import org.cqframework.cql.cql2elm.LibrarySourceProvider;
import org.hl7.cql.model.NamespaceAware;
import org.hl7.cql.model.NamespaceInfo;
import org.hl7.cql.model.NamespaceManager;
import org.hl7.elm.r1.VersionedIdentifier;

/**
 * Created by Bryn on 3/28/2017.
 */
public class FhirLibrarySourceProvider implements LibrarySourceProvider, NamespaceAware {

    private static final String NAMESPACE_NAME = "FHIR";
    private static final String NAMESPACE_URI = "http://hl7.org/fhir";

    @Override
    public Source getLibrarySource(VersionedIdentifier libraryIdentifier) {
        InputStream result = FhirLibrarySourceProvider.class.getResourceAsStream(
                String.format("/org/hl7/fhir/%s-%s.cql", libraryIdentifier.getId(), libraryIdentifier.getVersion()));

        if (result != null && namespaceManager != null && namespaceManager.hasNamespaces()) {
            // If the context already has a namespace registered for FHIR, use that.
            NamespaceInfo namespaceInfo = namespaceManager.getNamespaceInfoFromUri(NAMESPACE_URI);
            if (namespaceInfo == null) {
                namespaceInfo = new NamespaceInfo(NAMESPACE_NAME, NAMESPACE_URI);
                namespaceManager.ensureNamespaceRegistered(namespaceInfo);
            }
            libraryIdentifier.setSystem(NAMESPACE_URI);
        }

        if (result == null) {
            return null;
        }

        return buffered(asSource(result));
    }

    private NamespaceManager namespaceManager;

    @Override
    public void setNamespaceManager(NamespaceManager namespaceManager) {
        this.namespaceManager = namespaceManager;
    }

    @Override
    public Source getLibraryContent(VersionedIdentifier libraryIdentifier, LibraryContentType type) {
        if (LibraryContentType.CQL == type) {
            return getLibrarySource(libraryIdentifier);
        }

        return null;
    }
}
