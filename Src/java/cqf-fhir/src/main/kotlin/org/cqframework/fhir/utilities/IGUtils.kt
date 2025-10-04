package org.cqframework.fhir.utilities

import java.io.File
import java.io.IOException
import org.hl7.fhir.r5.model.ImplementationGuide
import org.hl7.fhir.utilities.Utilities

object IGUtils {
    @JvmStatic
    fun getImplementationGuideCanonicalBase(url: String?): String? {
        var canonicalBase: String? = null

        if (url != null && !url.isEmpty()) {
            canonicalBase = url.substring(0, url.indexOf("/ImplementationGuide/"))
        }

        return canonicalBase
    }

    @Throws(IOException::class)
    fun extractResourcePaths(
        rootDir: String?,
        sourceIg: ImplementationGuide,
    ): MutableList<String?> {
        val result = ArrayList<String?>()
        for (p in sourceIg.getDefinition().getParameter()) {
            if ("path-resource" == p.getCode().getCode()) {
                result.add(Utilities.path(rootDir, p.getValue()))
            }
        }

        val resources = File(Utilities.path(rootDir, "input/resources"))
        if (resources.exists() && resources.isDirectory()) {
            result.add(resources.absolutePath)
        }

        return result
    }

    /*
    Determines the CQL content path for the given implementation guide
    @rootDir: The root directory of the implementation guide source
    @sourceIg: The implementationGuide (as an R5 resource)
     */
    @JvmStatic
    @Suppress("MaxLineLength", "UnusedParameter")
    fun extractBinaryPaths(rootDir: String?, sourceIg: ImplementationGuide?): MutableList<String?> {
        val result: MutableList<String?> = ArrayList<String?>()

        // Although this is the correct way to read the cql path from an implementation guide,
        // the tooling cannot use this method, because if it's present in the IG, the publisher will
        // redo the CQL translation work. Instead, assume a path of input/cql, or
        // input/pagecontent/cql
        /*
        for (ImplementationGuide.ImplementationGuideDefinitionParameterComponent p : sourceIg.getDefinition().getParameter()) {
            // documentation for this list: https://confluence.hl7.org/display/FHIR/Implementation+Guide+Parameters
            if (p.getCode().equals("path-binary")) {
                result.add(Utilities.path(rootDir, p.getValue()));
            }
        }
        */
        var input = tryDirectory(rootDir, "input/cql")
        if (input != null && input.exists() && input.isDirectory()) {
            result.add(input.absolutePath)
        }

        input = tryDirectory(rootDir, "input/pagecontent/cql")
        if (input != null && input.exists() && input.isDirectory()) {
            result.add(input.absolutePath)
        }

        return result
    }

    /**
     * Tries to create a File for a path that may not exist.
     *
     * @param rootDir base directory for path
     * @param path child directories for path
     * @return File if directory exists, null otherwise
     */
    internal fun tryDirectory(rootDir: String?, path: String?): File? {
        var combinedPath: String?
        try {
            combinedPath = Utilities.path(rootDir, path)
        } catch (e: IOException) {
            return null
        }

        return File(combinedPath)
    }
}
