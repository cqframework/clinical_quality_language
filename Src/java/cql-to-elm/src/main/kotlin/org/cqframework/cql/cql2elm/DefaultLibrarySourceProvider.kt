@file:Suppress("WildcardImport")

package org.cqframework.cql.cql2elm

import java.io.*
import java.nio.file.Path
import java.util.*
import org.cqframework.cql.cql2elm.model.Version
import org.hl7.elm.r1.VersionedIdentifier

// NOTE: This implementation is naive and assumes library file names will always take the form:
// <filename>[-<version>].cql
// And further that <filename> will never contain dashes, and that <version> will always be of the
// form
// <major>[.<minor>[.<patch>]]
// Usage outside these boundaries will result in errors or incorrect behavior.
class DefaultLibrarySourceProvider(path: Path) : LibrarySourceProvider, PathAware {
    private var path: Path? = null

    init {
        this.setPath(path)
    }

    override fun setPath(path: Path) {
        require(path.toFile().isDirectory) {
            String.format(Locale.US, "path '%s' is not a valid directory", path)
        }
        this.path = path
    }

    @Suppress("CyclomaticComplexMethod", "NestedBlockDepth", "LongMethod")
    override fun getLibrarySource(libraryIdentifier: VersionedIdentifier): InputStream? {
        val currentPath = path
        if (currentPath != null) {
            val libraryName: String = libraryIdentifier.id
            val libraryPath: Path =
                currentPath.resolve(
                    String.format(
                        Locale.US,
                        "%s%s.cql",
                        libraryName,
                        if (libraryIdentifier.version != null) ("-" + libraryIdentifier.version)
                        else ""
                    )
                )
            var libraryFile: File? = libraryPath.toFile()
            if (libraryFile?.exists() != true) {
                val filter = FilenameFilter { path, name ->
                    name.startsWith(libraryName) && name.endsWith(".cql")
                }
                var mostRecentFile: File? = null
                var mostRecent: Version? = null
                val requestedVersion: Version? =
                    if (libraryIdentifier.version == null) null
                    else Version(libraryIdentifier.version)
                for (file: File in currentPath.toFile().listFiles(filter)) {
                    var fileName: String = file.name
                    val indexOfExtension: Int = fileName.lastIndexOf(".")
                    if (indexOfExtension >= 0) {
                        fileName = fileName.substring(0, indexOfExtension)
                    }
                    val indexOfVersionSeparator: Int = fileName.indexOf("-")
                    if (indexOfVersionSeparator >= 0) {
                        val version = Version(fileName.substring(indexOfVersionSeparator + 1))
                        // If the file has a version, make sure it is compatible with the version we
                        // are looking for
                        if (
                            (indexOfVersionSeparator == libraryName.length &&
                                requestedVersion == null ||
                                version.compatibleWith(requestedVersion))
                        ) {
                            @Suppress("ComplexCondition")
                            if (
                                (mostRecent == null ||
                                    (((version.isComparable) &&
                                        (mostRecent.isComparable) &&
                                        (version.compareTo(mostRecent) > 0))))
                            ) {
                                mostRecent = version
                                mostRecentFile = file
                            } else if (version.matchStrictly(mostRecent)) {
                                mostRecent = version
                                mostRecentFile = file
                            }
                        }
                    } else {
                        // If the file is named correctly, but has no version, consider it the most
                        // recent version
                        if ((fileName == libraryName) && mostRecent == null) {
                            mostRecentFile = file
                        }
                    }
                }

                // Do not throw, allow the loader to throw, just report null
                // if (mostRecentFile == null) {
                //    throw new IllegalArgumentException(String.format("Could not resolve most
                // recent source library for
                // library %s.", libraryIdentifier.getId()));
                // }
                libraryFile = mostRecentFile
            }
            try {
                if (libraryFile != null) {
                    return FileInputStream(libraryFile)
                }
            } catch (e: FileNotFoundException) {
                throw IllegalArgumentException(
                    String.format(
                        Locale.US,
                        "Could not load source for library %s.",
                        libraryIdentifier.id
                    ),
                    e
                )
            }
        }
        return null
    }

    override fun getLibraryContent(
        libraryIdentifier: VersionedIdentifier,
        type: LibraryContentType
    ): InputStream? {
        if (LibraryContentType.CQL == type) {
            return getLibrarySource(libraryIdentifier)
        }

        return null
    }
}
