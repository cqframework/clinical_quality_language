package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.model.Version;
import org.hl7.cql.model.ModelIdentifier;
import org.hl7.cql.model.ModelInfoProvider;
import org.hl7.elm.r1.VersionedIdentifier;
import org.hl7.elm_modelinfo.r1.ModelInfo;
import org.hl7.elm_modelinfo.r1.serializing.ModelInfoReaderFactory;

import java.io.*;
import java.nio.file.Path;

// NOTE: This implementation assumes modelinfo file names will always take the form:
// <modelname>-modelinfo[-<version>].cql
// And further that <modelname> will never contain dashes, and that <version> will always be of the form <major>[.<minor>[.<patch>]]
// Usage outside these boundaries will result in errors or incorrect behavior.
public class DefaultModelInfoProvider implements ModelInfoProvider, PathAware {

    public DefaultModelInfoProvider() {

    }

    public DefaultModelInfoProvider(Path path) {
        setPath(path);
    }

    private Path path;

    public void setPath(Path path) {
        if (path == null || ! path.toFile().isDirectory()) {
            throw new IllegalArgumentException(String.format("path '%s' is not a valid directory", path));
        }

        this.path = path;
    }

    private void checkPath() {
        if (path == null || path.equals("")) {
            throw new IllegalArgumentException("Path is required for DefaultModelInfoProvider implementation");
        }
    }

    public ModelInfo load(ModelIdentifier modelIdentifier) {
        if (path != null) {
            String modelName = modelIdentifier.getId();
            String modelVersion = modelIdentifier.getVersion();
            Path modelPath = this.path.resolve(String.format("%s-modelinfo%s.xml", modelName.toLowerCase(),
                    modelVersion != null ? ("-" + modelVersion) : ""));
            File modelFile = modelPath.toFile();
            if (!modelFile.exists()) {
                FilenameFilter filter = new FilenameFilter() {
                    @Override
                    public boolean accept(File path, String name) {
                        return name.startsWith(modelName.toLowerCase() + "-modelinfo") && name.endsWith(".xml");
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

                        String[] fileNameComponents = fileName.split("-");
                        if (fileNameComponents.length == 3) {
                            Version version = new Version(fileNameComponents[2]);
                            if (requestedVersion == null || version.compatibleWith(requestedVersion)) {
                                if (mostRecent == null ||
                                        ((version != null && version.isComparable()) &&
                                                (mostRecent != null && mostRecent.isComparable()) &&
                                                version.compareTo(mostRecent) > 0)) {
                                    mostRecent = version;
                                    mostRecentFile = file;
                                } else if(version != null && version.matchStrictly(mostRecent)){
                                    mostRecent = version;
                                    mostRecentFile = file;
                                }
                            }
                        }
                        else {
                            if (mostRecent == null) {
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

                    return ModelInfoReaderFactory.getReader("application/xml").read(is);
                }
            } catch (IOException e) {
                throw new IllegalArgumentException(String.format("Could not load definition for model info %s.", modelIdentifier.getId()), e);
            }
        }

        return null;
    }
}
