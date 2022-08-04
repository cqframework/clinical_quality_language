package org.cqframework.cql.cql2elm;

import java.io.InputStream;
import org.hl7.elm.r1.VersionedIdentifier;

/**
 *
 * @author mhadley
 */
public interface LibrarySourceLoader {

  void clearProviders();

  InputStream getLibrarySource(VersionedIdentifier libraryIdentifier);

  void registerProvider(LibrarySourceProvider provider);

  default InputStream getLibraryContent(VersionedIdentifier libraryIdentifier, LibraryContentType type) {
    if (LibraryContentType.CQL == type) {
      return getLibrarySource(libraryIdentifier);
    }

    return null;
  }

  default boolean isLibraryContentAvailable(VersionedIdentifier libraryIdentifier, LibraryContentType type) {
    if (LibraryContentType.CQL == type) {
      return getLibrarySource(libraryIdentifier) != null;
    }

    return false;
  }
}
