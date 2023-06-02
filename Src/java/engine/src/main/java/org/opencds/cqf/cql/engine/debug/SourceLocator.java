package org.opencds.cqf.cql.engine.debug;

import org.cqframework.cql.elm.execution.Element;
import org.cqframework.cql.elm.execution.Library;
import org.opencds.cqf.cql.engine.elm.execution.Executable;

public class SourceLocator {

    public SourceLocator(String librarySystemId, String libraryName, String libraryVersion, String nodeId, String nodeType, Location sourceLocation) {
        this.librarySystemId = librarySystemId;
        this.libraryName = libraryName;
        this.libraryVersion = libraryVersion;
        this.nodeId = nodeId;
        this.nodeType = nodeType;
        this.sourceLocation = sourceLocation;
    }

    public static SourceLocator fromNode(Executable node, Library currentLibrary) {
        if (node instanceof Element) {
            Element element = (Element)node;
            return new SourceLocator(
                    currentLibrary != null ? currentLibrary.getIdentifier().getSystem() : "http://cql.hl7.org/Library/unknown",
                    currentLibrary != null ? currentLibrary.getIdentifier().getId() : "?",
                    currentLibrary != null ? currentLibrary.getIdentifier().getVersion() : null,
                    element.getLocalId(),
                    stripEvaluator(element.getClass().getSimpleName()),
                    element.getLocator() != null ? Location.fromLocator(element.getLocator()) : null
            );
        }
        else {
            return new SourceLocator(
                    currentLibrary.getIdentifier().getSystem(),
                    currentLibrary.getIdentifier().getId(),
                    currentLibrary.getIdentifier().getVersion(),
                    null,
                    stripEvaluator(node.getClass().getSimpleName()),
                    null
            );
        }
    }

    public static String stripEvaluator(String nodeType) {
        if (nodeType == null) {
            return nodeType;
        }

        if (nodeType.endsWith("Evaluator")) {
            return nodeType.substring(0, nodeType.lastIndexOf("Evaluator"));
        }

        return nodeType;
    }

    private String librarySystemId;
    public String getLibrarySystemId() {
        return librarySystemId;
    }

    private String libraryName;
    public String getLibraryName() {
        return libraryName;
    }

    private String libraryVersion;
    public String getLibraryVersion() {
        return libraryVersion;
    }

    private String nodeId;
    public String getNodeId() {
        return nodeId;
    }

    private String nodeType;
    public String getNodeType() {
        return nodeType;
    }

    private Location sourceLocation;
    public Location getSourceLocation() {
        return sourceLocation;
    }

    private String getLocation() {
        return String.format("%s%s",
                sourceLocation != null ? sourceLocation.toLocator() : "?",
                nodeId != null || nodeType != null ?
                        ("(" + (nodeId != null ? nodeId : nodeType) + ")")
                        : "(?)"
        );
    }

    public String toString() {
        String location = getLocation();
        return String.format("%s%s",
                libraryName == null ? "?" : libraryName,
                location != null ? ("." + location) : "");
    }
}
