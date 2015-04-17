package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class DefaultLibrarySourceProvider implements LibrarySourceProvider {

    public DefaultLibrarySourceProvider(String path) {
        if (path == null || path.equals("")) {
            throw new IllegalArgumentException("path is empty");
        }

        this.path = path;
    }

    private String path;

    @Override
    public InputStream getLibrarySource(VersionedIdentifier libraryIdentifier) {
        File libraryFile = new File(String.format("%s/%s.cql", this.path, libraryIdentifier.getId()));
        try {
            return new FileInputStream(libraryFile);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(String.format("Could not load source for library %s.", libraryIdentifier.getId()), e);
        }
    }
}
