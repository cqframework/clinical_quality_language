package org.cqframework.fhir.npm

import org.cqframework.fhir.utilities.IGContext
import org.hl7.cql.model.NamespaceInfo

class NpmProcessor(
    /**
     * The igContext for the npmProcessor (i.e. the root IG that defines dependencies accessible in
     * the context) Note that this may be null in the case that there is no IG context
     */
    val igContext: IGContext?
) {
    /**
     * Provides access to the Npm package manager. Note that this will be throw an exception in the
     * case that there is no ig context.
     */
    private var packageManager: NpmPackageManager? = null

    fun getPackageManager(): NpmPackageManager {
        checkNotNull(this.packageManager) {
            "Package manager is not available outside of an ig context"
        }
        return this.packageManager!!
    }

    // @Inject
    init {
        if (igContext != null) {
            packageManager = NpmPackageManager(igContext.getSourceIg())
        }
    }

    val igNamespace: NamespaceInfo?
        get() {
            if (igContext != null) {
                return NamespaceInfo(igContext.packageId!!, igContext.canonicalBase!!)
            }

            return null
        }

    val namespaces: MutableList<NamespaceInfo>
        get() {
            val namespaceInfos: MutableList<NamespaceInfo> = ArrayList()
            if (packageManager != null) {
                val packages = packageManager!!.npmList
                for (p in packages) {
                    if (
                        p.name() != null &&
                            !p.name().isEmpty() &&
                            p.canonical() != null &&
                            !p.canonical().isEmpty()
                    ) {
                        val ni = NamespaceInfo(p.name(), p.canonical())
                        namespaceInfos.add(ni)
                    }
                }
            }
            return namespaceInfos
        }
}
