package org.cqframework.fhir.utilities

import java.io.File
import java.net.URI
import org.apache.commons.lang3.SystemUtils

object Uris {
    private const val FILE_UNC_PREFIX = "file:////"
    private const val FILE_SCHEME = "file"

    fun getHead(uri: URI): URI? {
        val path = uri.rawPath
        if (path != null) {
            val index = path.lastIndexOf("/")
            if (index > -1) {
                return withPath(uri, path.substring(0, index))
            }

            return uri
        }

        return uri
    }

    fun withPath(uri: URI, path: String?): URI? {
        try {
            return URI.create(
                ((if (uri.scheme != null) uri.scheme + ":" else "") +
                    "//" +
                    createAuthority(uri.rawAuthority) +
                    createPath(path) +
                    createQuery(uri.rawQuery) +
                    createFragment(uri.rawFragment))
            )
        } catch (e: Exception) {
            return null
        }
    }

    fun addPath(uri: URI, path: String?): URI? {
        return withPath(uri, stripTrailingSlash(uri.rawPath) + createPath(path))
    }

    @JvmStatic
    fun parseOrNull(uriString: String): URI? {
        try {
            var uri = URI(uriString)
            if (SystemUtils.IS_OS_WINDOWS && FILE_SCHEME == uri.scheme) {
                uri = File(uri.getSchemeSpecificPart()).toURI()
            }

            return uri
        } catch (e: Exception) {
            return null
        }
    }

    fun toClientUri(uri: URI?): String? {
        if (uri == null) {
            return null
        }

        var uriString = uri.toString()
        if (SystemUtils.IS_OS_WINDOWS && uriString.startsWith(FILE_UNC_PREFIX)) {
            uriString = uriString.replace(FILE_UNC_PREFIX, "file://")
        }

        return uriString
    }

    private fun createAuthority(rawAuthority: String?): String {
        return if (rawAuthority != null) rawAuthority else ""
    }

    private fun stripTrailingSlash(path: String?): String {
        if (path == null || path.isEmpty()) {
            return ""
        }

        if (path.endsWith("/")) {
            return path.substring(0, path.length - 1)
        }

        return path
    }

    private fun createPath(pathValue: String?): String {
        return ensurePrefix("/", pathValue)
    }

    private fun createQuery(queryValue: String?): String {
        return ensurePrefix("?", queryValue)
    }

    private fun createFragment(fragmentValue: String?): String {
        return ensurePrefix("#", fragmentValue)
    }

    private fun ensurePrefix(prefix: String, value: String?): String {
        return if (value == null || value.isEmpty()) {
            ""
        } else if (value.startsWith(prefix)) {
            value
        } else {
            prefix + value
        }
    }
}
