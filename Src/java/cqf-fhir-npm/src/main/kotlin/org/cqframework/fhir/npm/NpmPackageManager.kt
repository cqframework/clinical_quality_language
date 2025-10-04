package org.cqframework.fhir.npm

import ca.uhn.fhir.model.primitive.IdDt
import java.io.IOException
import org.hl7.fhir.r5.context.ILoggingService
import org.hl7.fhir.r5.model.ImplementationGuide
import org.hl7.fhir.utilities.VersionUtilities
import org.hl7.fhir.utilities.npm.FilesystemPackageCacheManager
import org.hl7.fhir.utilities.npm.NpmPackage
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class NpmPackageManager
@JvmOverloads
constructor(
    val sourceIg: ImplementationGuide,
    fspcm: FilesystemPackageCacheManager? = null,
    npmList: MutableList<NpmPackage>? = null,
) : ILoggingService {
    private val fspcm: FilesystemPackageCacheManager
    @JvmField val npmList: MutableList<NpmPackage> = npmList ?: ArrayList()

    init {

        try {
            this.fspcm = fspcm ?: FilesystemPackageCacheManager.Builder().build()
            loadDependencies()
        } catch (e: IOException) {
            logErrorMessage(e.message)
            throw NpmPackageManagerException(e.message, e)
        }
    }

    @Throws(IOException::class)
    fun loadDependencies() {
        for (fhirVersion in sourceIg.getFhirVersion()) {
            val coreFhirVersion = VersionUtilities.packageForVersion(fhirVersion.code)
            logMessage("Loading core FHIR version $coreFhirVersion")
            npmList.add(fspcm.loadPackage(coreFhirVersion, fhirVersion.code))
        }
        for (dependency in sourceIg.getDependsOn()) {
            var dependencyPackage: NpmPackage? = null
            if (dependency.hasPackageId() && !hasPackage(dependency.getPackageId(), false)) {
                logMessage("Loading package: " + dependency.getPackageId())
                dependencyPackage =
                    fspcm.loadPackage(
                        dependency.getPackageId(),
                        if (dependency.hasVersion()) dependency.getVersion() else "current",
                    )
                npmList.add(dependencyPackage)
            } else if (dependency.hasUri() && !hasPackage(dependency.getUri(), true)) {
                val id = IdDt(dependency.getUri())
                logMessage("Loading package: " + id.idPart)
                dependencyPackage =
                    fspcm.loadPackage(
                        id.idPart,
                        if (dependency.hasVersion()) dependency.getVersion() else "current",
                    )
                npmList.add(dependencyPackage)
            } else {
                val dependencyIdentifier = if (dependency.hasId()) dependency.getId() else ""
                logWarningMessage(
                    ("Dependency " +
                        dependencyIdentifier +
                        "missing packageId and uri, so can't be referred to in markdown in the IG")
                )
            }

            if (dependencyPackage != null) {
                loadDependencies(dependencyPackage)
            }
        }
    }

    @Throws(IOException::class)
    fun loadDependencies(parentPackage: NpmPackage) {
        for (dependency in parentPackage.dependencies()) {
            if (hasPackage(dependency, false)) continue
            logMessage("Loading package: $dependency")
            val childPackage = fspcm.loadPackage(dependency)
            npmList.add(childPackage)
            if (!childPackage.dependencies().isEmpty()) {
                loadDependencies(childPackage)
            }
        }
    }

    fun hasPackage(packageId: String, isUrl: Boolean): Boolean {
        for (npmPackage in npmList) {
            return if (!isUrl) {
                (npmPackage.npm.has("name") &&
                    packageId.startsWith(npmPackage.npm.get("name").asString()))
            } else {
                (npmPackage.npm.has("canonical") &&
                    packageId == npmPackage.npm.get("canonical").asString())
            }
        }
        return false
    }

    override fun logMessage(message: String?) {
        logger.info(message)
    }

    override fun logDebugMessage(category: ILoggingService.LogCategory?, message: String?) {
        logger.debug(message)
    }

    fun logWarningMessage(message: String?) {
        logger.warn(message)
    }

    fun logErrorMessage(message: String?) {
        logger.error(message)
    }

    @Deprecated("Deprecated in FHIR core")
    override fun isDebugLogging(): Boolean {
        return logger.isDebugEnabled
    }

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(NpmPackageManager::class.java)
    }
}
