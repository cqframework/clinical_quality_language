package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.model.Version;
import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.elm_modelinfo.r1.ModelInfo;

import javax.xml.bind.JAXB;
import java.io.*;
import java.nio.file.Path;

// NOTE: This implementation assumes modelinfo file names will always take the form:
// <modelname>-modelinfo[-<version>].cql
// And further that <filename> will never contain dashes, and that <version> will always be of the form <major>[.<minor>[.<patch>]]
// Usage outside these boundaries will result in errors or incorrect behavior.
public class DefaultModelInfoProvider implements ModelInfoProvider {

    public DefaultModelInfoProvider(Path path) {
        if (path == null || ! path.toFile().isDirectory()) {
            throw new IllegalArgumentException(String.format("path '%s' is not a valid directory", path));
        }

        this.path = path;
    }

    private Path path;

    public ModelInfo load(VersionedIdentifier modelIdentifier) {
        String modelName = modelIdentifier.getId();
        String modelVersion = modelIdentifier.getVersion();
        Path modelPath = this.path.resolve(String.format("%s-modelinfo%s.xml", modelName.toLowerCase(),
                modelVersion != null ? ("-" + modelVersion) : ""));
        File modelFile = modelPath.toFile();
        if (!modelFile.exists()) {
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File path, String name) {
                    return name.startsWith(modelName.toLowerCase()) && name.endsWith(".xml");
                }
            };

            File mostRecentFile = null;
            Version mostRecent = null;
            try {
                Version requestedVersion = modelVersion == null ? null : new Version(modelVersion);
                for (File file : path.toFile().listFiles(filter)) {
                    String fileName = file.getName();
                    int indexOfExtension = fileName.lastIndexOf(".");
                    if (indexOfExtension >= 0) {
                        fileName = fileName.substring(0, indexOfExtension);
                    }

                    int indexOfVersionSeparator = fileName.lastIndexOf("-");
                    if (indexOfVersionSeparator >= 0) {
                        Version version = new Version(fileName.substring(indexOfVersionSeparator + 1));
                        // If the file has a version, make sure it is compatible with the version we are looking for
                        if (indexOfVersionSeparator == (modelName + "-modelinfo").length() && requestedVersion == null || version.compatibleWith(requestedVersion)) {
                            if (mostRecent == null || version.compareTo(mostRecent) > 0) {
                                mostRecent = version;
                                mostRecentFile = file;
                            }
                        }
                    } else {
                        // If the file is named correctly, but has no version, consider it the most recent version
                        if (fileName.equals(modelName + "-modelinfo") && mostRecent == null) {
                            mostRecentFile = file;
                        }
                    }
                }

                modelFile = mostRecentFile;
            }
            catch (IllegalArgumentException e) {
                // do nothing, if the version can't be understood as a semantic version, don't allow unspecified version resolution
            }
        }
        try {
            if (modelFile != null) {
                InputStream is = new FileInputStream(modelFile);
                return JAXB.unmarshal(is, ModelInfo.class);
            }
        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException(String.format("Could not load definition for model info %s.", modelIdentifier.getId()), e);
        }

        return null;
    }
}
