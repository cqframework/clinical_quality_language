package org.opencds.cqf.cql.engine.execution;

public class NamespaceHelper {

    /**
     * Gets the the namespace part of a fully qualified name.
     *
     * Returns null if <code>namspaceQualifiedName</code> is not qualified by namespace.
     * Returns null if <code>namspaceQualifiedName</code> is null
     * @param namespaceQualifiedName The fully qualified name
     * @return the namespace part
     */
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

    /**
     * Gets the the name part of a fully qualified name.
     *
     * Returns null if <code>namspaceQualifiedName</code> is null
     * @param namespaceQualifiedName The fully qualified name
     * @return the name part
     */
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
