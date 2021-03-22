package org.cqframework.cql.cql2elm;

import java.util.HashMap;
import java.util.Map;

public class NamespaceManager {
    private final Map<String, String> namespaces;
    private final Map<String, String> reverseNamespaces;

    public NamespaceManager() {
        namespaces = new HashMap<>();
        reverseNamespaces = new HashMap<>();
    }

    public boolean hasNamespaces() {
        return namespaces.size() > 0;
    }

    public void ensureNamespaceRegistered(NamespaceInfo namespaceInfo) {
        if (namespaceInfo == null) {
            throw new IllegalArgumentException("namespaceInfo is required");
        }

        if (!namespaces.containsKey(namespaceInfo.getName())) {
            addNamespace(namespaceInfo.getName(), namespaceInfo.getUri());
        }
    }

    public void addNamespace(NamespaceInfo namespaceInfo) {
        if (namespaceInfo == null) {
            throw new IllegalArgumentException("namespaceInfo is required");
        }

        addNamespace(namespaceInfo.getName(), namespaceInfo.getUri());
    }

    public void addNamespace(String namespaceName, String namespaceUri) {
        if (namespaceName == null || namespaceName.isEmpty()) {
            throw new IllegalArgumentException("namespaceName is required");
        }

        if (namespaceUri == null || namespaceUri.isEmpty()) {
            throw new IllegalArgumentException("namespaceUri is required");
        }

        if (namespaces.containsKey(namespaceName)) {
            throw new IllegalArgumentException(String.format("A namespace named %s is already defined.", namespaceName));
        }

        if (reverseNamespaces.containsKey(namespaceUri)) {
            throw new IllegalArgumentException(String.format("A namespace name for uri %s is already defined.", namespaceUri));
        }

        namespaces.put(namespaceName, namespaceUri);
        reverseNamespaces.put(namespaceUri, namespaceName);
    }

    public String resolveNamespaceUri(String namespaceName) {
        if (namespaces.containsKey(namespaceName)) {
            return namespaces.get(namespaceName);
        }

        return null;
    }

    public NamespaceInfo getNamespaceInfoFromUri(String namespaceUri) {
        if (reverseNamespaces.containsKey(namespaceUri)) {
            return new NamespaceInfo(reverseNamespaces.get(namespaceUri), namespaceUri);
        }

        return null;
    }

    public static String getPath(String namespaceUri, String name) {
        if (namespaceUri != null) {
            return String.format("%s/%s", namespaceUri, name);
        }

        return name;
    }

    public static String getUriPart(String namespaceQualifiedName) {
        if (namespaceQualifiedName == null) {
            return null;
        }

        int i = namespaceQualifiedName.lastIndexOf('/');
        if (i > 0) {
            return namespaceQualifiedName.substring(0, i);
        }

        return null;
    }

    public static String getNamePart(String namespaceQualifiedName) {
        if (namespaceQualifiedName == null) {
            return null;
        }

        int i = namespaceQualifiedName.lastIndexOf("/");
        if (i > 0) {
            return namespaceQualifiedName.substring(i + 1);
        }

        return namespaceQualifiedName;
    }
}
