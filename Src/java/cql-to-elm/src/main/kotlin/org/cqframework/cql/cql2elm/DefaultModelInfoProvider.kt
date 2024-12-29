@file:Suppress("WildcardImport")

package org.cqframework.cql.cql2elm

import java.io.*
import java.nio.file.Path
import org.cqframework.cql.cql2elm.model.Version
import org.hl7.cql.model.ModelIdentifier
import org.hl7.cql.model.ModelInfoProvider
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReaderFactory

// NOTE: This implementation assumes modelinfo file names will always take the form:
// <modelname>-modelinfo[-<version>].cql
// And further that <modelname> will never contain dashes, and that <version> will always be of the
// form
// <major>[.<minor>[.<patch>]]
// Usage outside these boundaries will result in errors or incorrect behavior.
class DefaultModelInfoProvider() : ModelInfoProvider, PathAware {
    constructor(path: Path) : this() {
        this.setPath(path)
    }

    private var path: Path? = null

    override fun setPath(path: Path) {
        require(path.toFile().isDirectory) { "path '$path' is not a valid directory" }
        this.path = path
    }

    @Suppress("UnusedPrivateMember")
    private fun checkPath() {
        require(!(path == null || path!!.equals(""))) {
            "Path is required for DefaultModelInfoProvider implementation"
        }
    }

    @Suppress("CyclomaticComplexMethod", "NestedBlockDepth", "LongMethod")
    override fun load(modelIdentifier: ModelIdentifier): ModelInfo? {
        val currentPath = path
        if (currentPath != null) {
            val modelName = modelIdentifier.id
            val modelVersion = modelIdentifier.version
            val modelPath =
                currentPath.resolve(
                    "${modelName.lowercase()}-modelinfo${if (modelVersion != null) "-$modelVersion" else ""}.xml"
                )
            var modelFile = modelPath.toFile()
            if (!modelFile.exists()) {
                val filter = FilenameFilter { _, name ->
                    name.startsWith(modelName.lowercase() + "-modelinfo") && name.endsWith(".xml")
                }
                var mostRecentFile: File? = null
                var mostRecent: Version? = null
                try {
                    val requestedVersion = if (modelVersion == null) null else Version(modelVersion)
                    for (file in currentPath.toFile().listFiles(filter)!!) {
                        var fileName = file.name
                        val indexOfExtension = fileName.lastIndexOf(".")
                        if (indexOfExtension >= 0) {
                            fileName = fileName.substring(0, indexOfExtension)
                        }
                        val fileNameComponents =
                            fileName
                                .split("-".toRegex())
                                .dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        @Suppress("MagicNumber")
                        if (fileNameComponents.size == 3) {
                            val version = Version(fileNameComponents[2])
                            if (
                                requestedVersion == null || version.compatibleWith(requestedVersion)
                            ) {
                                @Suppress("ComplexCondition")
                                if (
                                    mostRecent == null ||
                                        version.isComparable &&
                                            mostRecent.isComparable &&
                                            version > mostRecent
                                ) {
                                    mostRecent = version
                                    mostRecentFile = file
                                } else if (version.matchStrictly(mostRecent)) {
                                    mostRecent = version
                                    mostRecentFile = file
                                }
                            }
                        } else {
                            if (mostRecent == null) {
                                mostRecentFile = file
                            }
                        }
                    }
                    modelFile = mostRecentFile!!
                } catch (@Suppress("SwallowedException") e: IllegalArgumentException) {
                    // do nothing, if the version can't be understood as a semantic version, don't
                    // allow unspecified
                    // version resolution
                }
            }
            try {
                val inputStream: InputStream = FileInputStream(modelFile)
                return ModelInfoReaderFactory.getReader("application/xml")?.read(inputStream)
            } catch (e: IOException) {
                throw IllegalArgumentException(
                    "Could not load definition for model info ${modelIdentifier.id}.",
                    e
                )
            }
        }
        return null
    }
}
