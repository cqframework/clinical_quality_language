package org.opencds.cqf.cql.engine.execution;

import static kotlinx.io.CoreKt.buffered;
import static kotlinx.io.JvmCoreKt.asSource;

import java.io.ByteArrayInputStream;
import java.util.Map;
import kotlinx.io.Source;
import org.cqframework.cql.cql2elm.LibraryContentType;
import org.cqframework.cql.cql2elm.LibrarySourceProvider;
import org.hl7.elm.r1.VersionedIdentifier;

/**
 * This class provides CQL content for a given VersionedIdentifier based on a
 * pre-populated Map. This is mostly useful for testing scenarios
 */
public class MapLibrarySourceProvider implements LibrarySourceProvider {

    private Map<org.hl7.elm.r1.VersionedIdentifier, String> libraries = null;

    public MapLibrarySourceProvider(Map<org.hl7.elm.r1.VersionedIdentifier, String> libraries) {
        this.libraries = libraries;
    }

    @Override
    public Source getLibrarySource(org.hl7.elm.r1.VersionedIdentifier libraryIdentifier) {
        String text = this.libraries.get(libraryIdentifier);
        return buffered(asSource(new ByteArrayInputStream(text.getBytes())));
    }

    @Override
    public Source getLibraryContent(VersionedIdentifier libraryIdentifier, LibraryContentType type) {
        if (LibraryContentType.CQL == type) {
            return getLibrarySource(libraryIdentifier);
        }

        return null;
    }
}
