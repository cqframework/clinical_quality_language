package org.cqframework.cql.cql2elm;

import org.hl7.elm.r1.VersionedIdentifier;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;

public class DefaultLibrarySourceProvider implements LibrarySourceProvider {

    public DefaultLibrarySourceProvider(Path path) {
        if (path == null || ! path.toFile().isDirectory()) {
            throw new IllegalArgumentException(String.format("path '%s' is not a valid directory", path));
        }

        this.path = path;
    }

    private Path path;

    @Override
    public InputStream getLibrarySource(VersionedIdentifier libraryIdentifier) {
        Path libraryPath = this.path.resolve(String.format("%s.cql", libraryIdentifier.getId()));
        try {
            return new FileInputStream(libraryPath.toFile());
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(String.format("Could not load source for library %s.", libraryIdentifier.getId()), e);
        }
    }
}
