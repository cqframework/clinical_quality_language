package org.cqframework.cql.cql2elm

import kotlinx.io.Source
import kotlinx.io.buffered
import kotlinx.io.files.FileNotFoundException
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
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
        require(SystemFileSystem.metadataOrNull(path)?.isDirectory == true) {
            "path '$path' is not a valid directory"
        }
        this.path = path
    }

    @Suppress("CyclomaticComplexMethod", "NestedBlockDepth", "LongMethod")
    override fun getLibrarySource(libraryIdentifier: VersionedIdentifier): Source? {
        val currentPath = path
        if (currentPath != null) {
            val libraryName = libraryIdentifier.id!!
            val libraryPath =
                Path(
                    currentPath,
                    "$libraryName${
                if (libraryIdentifier.version != null) ("-" + libraryIdentifier.version)
                else ""
            }.cql"
                )
            var libraryFile: Path? = libraryPath
            if (!SystemFileSystem.exists(libraryFile!!)) {
                var mostRecentFile: Path? = null
                var mostRecent: Version? = null
                val requestedVersion: Version? =
                    if (libraryIdentifier.version == null) null
                    else Version(libraryIdentifier.version!!)
                for (file in
                    SystemFileSystem.list(currentPath).filter { path ->
                        val fileName = path.name
                        fileName.startsWith(libraryName) && fileName.endsWith(".cql")
                    }) {
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
                                    ((version.isComparable) &&
                                        (mostRecent.isComparable) &&
                                        (version > mostRecent)))
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
                // recent source library for library %s.", libraryIdentifier.getId()));
                // }
                libraryFile = mostRecentFile
            }
            try {
                if (libraryFile != null) {
                    return SystemFileSystem.source(libraryFile).buffered()
                }
            } catch (e: FileNotFoundException) {
                throw IllegalArgumentException(
                    "Could not load source for library ${libraryIdentifier.id}.",
                    e
                )
            }
        }
        return null
    }

    override fun getLibraryContent(
        libraryIdentifier: VersionedIdentifier,
        type: LibraryContentType
    ): Source? {
        if (LibraryContentType.CQL == type) {
            return getLibrarySource(libraryIdentifier)
        }

        return null
    }
}
