package org.cqframework.cql.cql2elm;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This class implements the LibrarySourceProvider API, using a set of strings representing CQL
 * library content as a source.
 */
public class StringLibrarySourceProvider implements LibrarySourceProvider {

  private List<String> libraries;

  public StringLibrarySourceProvider(List<String> libraries) {
    this.libraries = libraries;
  }

  @Override
  public InputStream getLibrarySource(org.hl7.elm.r1.VersionedIdentifier libraryIdentifier) {
    String id = libraryIdentifier.getId();
    String version = libraryIdentifier.getVersion();

    var maybeQuotedIdPattern = "(\""+ id +"\"|"+ id +")";

    String matchText = "(?s).*library\\s+\"?" + maybeQuotedIdPattern;
    if (version != null) {
      matchText += ("\\s+version\\s+'" + version + "'\\s+(?s).*");
    } else {
      matchText += "\\s+(?s).*";
    }

    var matches = new ArrayList<String>();

    for (String library : this.libraries) {

      if (library.matches(matchText)) {
        matches.add(library);
      }
    }

    if (matches.size() > 1) {
      throw new IllegalArgumentException(String.format(
          "Multiple libraries for id : %s resolved.%nEnsure that there are no duplicates in the input set.",
          libraryIdentifier.toString()));
    }

    return matches.size() == 1 ? new ByteArrayInputStream(matches.get(0).getBytes()) : null;
  }
}
