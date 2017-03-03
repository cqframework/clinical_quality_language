package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.model.Version;
import org.hl7.elm.r1.VersionedIdentifier;

import java.io.*;
import java.nio.file.Path;

// NOTE: This implementation is naive and assumes library file names will always take the form:
// <filename>[-<version>].cql
// And further that <filename> will never contain dashes, and that <version> will always be of the form <major>[.<minor>[.<patch>]]
// Usage outside these boundaries will result in errors or incorrect behavior.
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
        String libraryName = libraryIdentifier.getId();
        Path libraryPath = this.path.resolve(String.format("%s%s.cql", libraryName,
                libraryIdentifier.getVersion() != null ? ("-" + libraryIdentifier.getVersion()) : ""));
        File libraryFile = libraryPath.toFile();
        if (!libraryFile.exists()) {
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File path, String name) {
                    return name.startsWith(libraryName) && name.endsWith(".cql");
                }
            };

            File mostRecentFile = null;
            Version mostRecent = null;
            for (File file : path.toFile().listFiles(filter)) {
                String fileName = file.getName();
                int indexOfExtension = fileName.lastIndexOf(".");
                if (indexOfExtension >= 0) {
                    fileName = fileName.substring(0, indexOfExtension);
                }

                int indexOfVersionSeparator = fileName.indexOf("-");
                if (indexOfVersionSeparator >= 0) {
                    Version version = new Version(fileName.substring(indexOfVersionSeparator + 1));
                    if (mostRecent == null || version.compareTo(mostRecent) > 0) {
                        mostRecent = version;
                        mostRecentFile = file;
                    }
                }
            }

            if (mostRecentFile == null) {
                throw new IllegalArgumentException(String.format("Could not resolve most recent source library for library %s.", libraryIdentifier.getId()));
            }

            libraryFile = mostRecentFile;
        }
        try {
            return new FileInputStream(libraryFile);
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(String.format("Could not load source for library %s.", libraryIdentifier.getId()), e);
        }
    }
}
