package org.cqframework.cql.cql2elm;

import java.io.InputStream;
import java.util.List;

import org.hl7.elm.r1.VersionedIdentifier;

/**
 *
 * @author mhadley
 */
public interface LibrarySourceLoader {

  void clearProviders();

  LibraryContentMeta getLibrarySource(VersionedIdentifier libraryIdentifier, List<LibraryContentType> typeList);

  void registerProvider(LibrarySourceProvider provider);
  
}
