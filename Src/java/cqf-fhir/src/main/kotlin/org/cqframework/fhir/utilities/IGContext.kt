package org.cqframework.fhir.utilities

import java.io.File
import java.io.IOException
import org.cqframework.fhir.utilities.IGUtils.extractBinaryPaths
import org.cqframework.fhir.utilities.IGUtils.getImplementationGuideCanonicalBase
import org.cqframework.fhir.utilities.exception.IGInitializationException
import org.hl7.fhir.convertors.advisors.impl.BaseAdvisor_30_50
import org.hl7.fhir.convertors.advisors.impl.BaseAdvisor_40_50
import org.hl7.fhir.convertors.conv30_50.VersionConvertor_30_50
import org.hl7.fhir.convertors.conv40_50.VersionConvertor_40_50
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.r5.context.ILoggingService
import org.hl7.fhir.r5.formats.FormatUtilities
import org.hl7.fhir.r5.model.ImplementationGuide
import org.hl7.fhir.utilities.FileUtilities
import org.hl7.fhir.utilities.IniFile
import org.hl7.fhir.utilities.Utilities
import org.hl7.fhir.utilities.VersionUtilities
import org.slf4j.Logger
import org.slf4j.LoggerFactory

@Suppress("LocalVariableName")
open class IGContext {
    private class DefaultLogger : ILoggingService {
        private val log: Logger = LoggerFactory.getLogger(IGContext::class.java)

        override fun logMessage(s: String?) {
            log.warn(s)
        }

        override fun logDebugMessage(logCategory: ILoggingService.LogCategory?, s: String?) {
            log.debug("{}: {}", logCategory, s)
        }

        @Deprecated("Deprecated in FHIR core")
        override fun isDebugLogging(): Boolean {
            return log.isDebugEnabled
        }
    }

    val logger: ILoggingService?

    var rootDir: String? = null
        protected set

    var sourceIg: ImplementationGuide? = null
        protected set

    var fhirVersion: String? = null
        protected set

    var packageId: String? = null
        protected set

    var canonicalBase: String? = null
        protected set

    var binaryPaths: MutableList<String?>? = null
        protected set

    constructor(logger: ILoggingService?) {
        this.logger = logger
    }

    constructor() {
        this.logger = DefaultLogger()
    }

    fun initializeFromIg(rootDir: String?, igPath: String?, fhirVersion: String?) {
        var igPath = igPath
        this.rootDir = rootDir

        try {
            igPath = Utilities.path(rootDir, igPath)
        } catch (e: IOException) {
            val message =
                String.format(
                    "Exceptions occurred creating igPath from source rootDir: %s, and igPath: %s",
                    rootDir,
                    igPath,
                )
            logMessage(message)
            throw IGInitializationException(message, e)
        }

        if (fhirVersion != null) {
            loadSourceIG(igPath, fhirVersion)
        } else {
            loadSourceIG(igPath)
        }

        this.fhirVersion = sourceIg!!.getFhirVersion()[0].code
        packageId = sourceIg!!.getPackageId()
        canonicalBase = getImplementationGuideCanonicalBase(sourceIg!!.getUrl())

        /*
        try {
            packageManager = new NpmPackageManager(sourceIg, this.fhirVersion);
        } catch (IOException e) {
            String message = String.format("Exceptions occurred loading npm package manager from source Ig: %s",
                    sourceIg.getName());
            logMessage(message);
            throw new IGInitializationException(message, e);
        }
        */

        // Setup binary paths (cql source directories)
        binaryPaths = extractBinaryPaths(rootDir, sourceIg)
    }

    /*
     * Initializes from an ig.ini file in the root directory
     */
    fun initializeFromIni(iniFile: String) {
        try {
            val ini = IniFile(File(iniFile).absolutePath)
            val iniDir = FileUtilities.getDirectoryForFile(ini.fileName)
            val igPath = ini.getStringProperty("IG", "ig")
            var specifiedFhirVersion = ini.getStringProperty("IG", "fhir-version")
            if (specifiedFhirVersion == null || specifiedFhirVersion === "") {
                logMessage(
                    "fhir-version was not specified in the ini file. Trying FHIR version 4.0.1"
                )
                specifiedFhirVersion = "4.0.1"
            }

            initializeFromIg(iniDir, igPath, specifiedFhirVersion)
        } catch (e: Exception) {
            val message =
                String.format(
                    "Exceptions occurred initializing refresh from ini file '%s':%s",
                    iniFile,
                    e.message,
                )
            logMessage(message)
            throw IGInitializationException(message, e)
        }
    }

    private fun loadSourceIG(igPath: String?): ImplementationGuide {
        try {
            try {
                sourceIg = FormatUtilities.loadFile(igPath) as ImplementationGuide
            } catch (_: IOException) {
                try {
                    val versionConvertor_40_50 = VersionConvertor_40_50(BaseAdvisor_40_50())
                    sourceIg =
                        versionConvertor_40_50.convertResource(
                            org.hl7.fhir.r4.formats.FormatUtilities.loadFile(igPath)
                        ) as ImplementationGuide
                } catch (_: IOException) {
                    val src = FileUtilities.fileToBytes(igPath)
                    val fmt = FormatUtilities.determineFormat(src)

                    val parser =
                        org.hl7.fhir.dstu3.formats.FormatUtilities.makeParser(fmt.toString())
                    val versionConvertor_30_50 = VersionConvertor_30_50(BaseAdvisor_30_50())
                    sourceIg =
                        versionConvertor_30_50.convertResource(parser.parse(src))
                            as ImplementationGuide
                } catch (_: FHIRException) {
                    val src = FileUtilities.fileToBytes(igPath)
                    val fmt = FormatUtilities.determineFormat(src)

                    val parser =
                        org.hl7.fhir.dstu3.formats.FormatUtilities.makeParser(fmt.toString())
                    val versionConvertor_30_50 = VersionConvertor_30_50(BaseAdvisor_30_50())
                    sourceIg =
                        versionConvertor_30_50.convertResource(parser.parse(src))
                            as ImplementationGuide
                }
            } catch (_: FHIRException) {
                try {
                    val versionConvertor_40_50 = VersionConvertor_40_50(BaseAdvisor_40_50())
                    sourceIg =
                        versionConvertor_40_50.convertResource(
                            org.hl7.fhir.r4.formats.FormatUtilities.loadFile(igPath)
                        ) as ImplementationGuide
                } catch (_: IOException) {
                    val src = FileUtilities.fileToBytes(igPath)
                    val fmt = FormatUtilities.determineFormat(src)

                    val parser =
                        org.hl7.fhir.dstu3.formats.FormatUtilities.makeParser(fmt.toString())
                    val versionConvertor_30_50 = VersionConvertor_30_50(BaseAdvisor_30_50())
                    sourceIg =
                        versionConvertor_30_50.convertResource(parser.parse(src))
                            as ImplementationGuide
                } catch (_: FHIRException) {
                    val src = FileUtilities.fileToBytes(igPath)
                    val fmt = FormatUtilities.determineFormat(src)

                    val parser =
                        org.hl7.fhir.dstu3.formats.FormatUtilities.makeParser(fmt.toString())
                    val versionConvertor_30_50 = VersionConvertor_30_50(BaseAdvisor_30_50())
                    sourceIg =
                        versionConvertor_30_50.convertResource(parser.parse(src))
                            as ImplementationGuide
                }
            }
        } catch (e: IOException) {
            throw IGInitializationException(
                String.format("error initializing IG from igPath: %s", igPath),
                e,
            )
        } catch (e: FHIRException) {
            throw IGInitializationException(
                String.format("error initializing IG from igPath: %s", igPath),
                e,
            )
        }

        return sourceIg!!
    }

    private fun loadSourceIG(igPath: String?, specifiedFhirVersion: String?): ImplementationGuide {
        try {
            if (VersionUtilities.isR3Ver(specifiedFhirVersion)) {
                val src = FileUtilities.fileToBytes(igPath)
                val fmt = FormatUtilities.determineFormat(src)
                val parser = org.hl7.fhir.dstu3.formats.FormatUtilities.makeParser(fmt.toString())
                val versionConvertor_30_50 = VersionConvertor_30_50(BaseAdvisor_30_50())
                sourceIg =
                    versionConvertor_30_50.convertResource(parser.parse(src)) as ImplementationGuide
            } else if (VersionUtilities.isR4Ver(specifiedFhirVersion)) {
                val res = org.hl7.fhir.r4.formats.FormatUtilities.loadFile(igPath)
                val versionConvertor_40_50 = VersionConvertor_40_50(BaseAdvisor_40_50())
                sourceIg = versionConvertor_40_50.convertResource(res) as ImplementationGuide
            } else if (VersionUtilities.isR5Ver(specifiedFhirVersion)) {
                sourceIg = FormatUtilities.loadFile(igPath) as ImplementationGuide
            } else {
                throw FHIRException("Unknown Version '$specifiedFhirVersion'")
            }
        } catch (e: IOException) {
            val message = String.format("Exceptions occurred loading IG path: %s", igPath)
            logMessage(message)
            throw IGInitializationException(message, e)
        }

        return sourceIg!!
    }

    fun logMessage(msg: String?) {
        logger?.logMessage(msg)
        /*
        else {
            // Maybe????
            System.out.println(msg);
        }
         */
    }
}
