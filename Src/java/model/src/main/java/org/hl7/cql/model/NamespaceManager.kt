package org.hl7.cql.model

import java.util.*
import kotlin.collections.HashMap

class NamespaceManager {
    private val namespaces: MutableMap<String, String> = HashMap()
    private val reverseNamespaces: MutableMap<String, String> = HashMap()

    fun hasNamespaces(): Boolean {
        return namespaces.isNotEmpty()
    }

    fun ensureNamespaceRegistered(namespaceInfo: NamespaceInfo) {
        if (!namespaces.containsKey(namespaceInfo.name)) {
            addNamespace(namespaceInfo.name, namespaceInfo.uri)
        }
    }

    fun addNamespace(namespaceInfo: NamespaceInfo) {
        addNamespace(namespaceInfo.name, namespaceInfo.uri)
    }

    fun addNamespace(namespaceName: String, namespaceUri: String) {
        require(namespaceName.isNotEmpty()) { "namespaceName is required" }
        require(namespaceUri.isNotEmpty()) { "namespaceUri is required" }
        check(!namespaces.containsKey(namespaceName)) {
            String.format(Locale.US, "A namespace named %s is already defined.", namespaceName)
        }

        check(!reverseNamespaces.containsKey(namespaceUri)) {
            String.format(
                Locale.US,
                "A namespace name for uri %s is already defined.",
                namespaceUri
            )
        }

        namespaces[namespaceName] = namespaceUri
        reverseNamespaces[namespaceUri] = namespaceName
    }

    fun resolveNamespaceUri(namespaceName: String): String? {
        return namespaces[namespaceName]
    }

    fun getNamespaceInfoFromUri(namespaceUri: String): NamespaceInfo? {
        return reverseNamespaces[namespaceUri]?.let { NamespaceInfo(it, namespaceUri) }
    }

    companion object {
        @JvmStatic
        fun getPath(namespaceUri: String?, name: String): String {
            return namespaceUri?.let { String.format(Locale.US, "%s/%s", namespaceUri, name) }
                ?: name
        }

        @JvmStatic
        fun getUriPart(namespaceQualifiedName: String?): String? {
            return namespaceQualifiedName
                ?.lastIndexOf('/')
                ?.takeIf { it > 0 }
                ?.let { namespaceQualifiedName.substring(0, it) }
        }

        @JvmStatic
        fun getNamePart(namespaceQualifiedName: String?): String? {
            return namespaceQualifiedName
                ?.lastIndexOf("/")
                ?.takeIf { it >= 0 }
                ?.let { namespaceQualifiedName.substring(it + 1) } ?: namespaceQualifiedName
        }
    }
}
