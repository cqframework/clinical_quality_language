package org.cqframework.fhir.npm;

import java.util.ArrayList;
import java.util.List;
import org.cqframework.fhir.utilities.IGContext;
import org.hl7.cql.model.NamespaceInfo;
import org.hl7.fhir.utilities.npm.NpmPackage;

public class NpmProcessor {
    /**
     * Provides access to the Npm package manager. Note that this will be throw an exception in the
     * case that there is no ig context.
     */
    private NpmPackageManager packageManager;

    public NpmPackageManager getPackageManager() {
        if (this.packageManager == null) {
            throw new IllegalStateException("Package manager is not available outside of an ig context");
        }
        return this.packageManager;
    }

    /**
     * The igContext for the npmProcessor (i.e. the root IG that defines dependencies accessible in
     * the context) Note that this may be null in the case that there is no IG context
     */
    private IGContext igContext;

    public IGContext getIgContext() {
        return this.igContext;
    }

    // @Inject
    public NpmProcessor(IGContext igContext) {
        this.igContext = igContext;
        if (igContext != null) {
            packageManager = new NpmPackageManager(igContext.getSourceIg());
        }
    }

    public NamespaceInfo getIgNamespace() {
        if (igContext != null) {
            return new NamespaceInfo(igContext.getPackageId(), igContext.getCanonicalBase());
        }

        return null;
    }

    public List<NamespaceInfo> getNamespaces() {
        List<NamespaceInfo> namespaceInfos = new ArrayList<>();
        if (packageManager != null) {
            List<NpmPackage> packages = packageManager.getNpmList();
            for (NpmPackage p : packages) {
                if (p.name() != null
                        && !p.name().isEmpty()
                        && p.canonical() != null
                        && !p.canonical().isEmpty()) {
                    NamespaceInfo ni = new NamespaceInfo(p.name(), p.canonical());
                    namespaceInfos.add(ni);
                }
            }
        }
        return namespaceInfos;
    }
}
