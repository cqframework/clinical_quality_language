package org.opencds.cqf.cql.engine.debug;

import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.Library;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class DebugUtilities {

    private static final Logger logger = LoggerFactory.getLogger(DebugUtilities.class);

    private DebugUtilities() {}

    public static void logDebugResult(Element node, Library currentLibrary, Object result) {
        var debugLocation = toDebugLocation(node);
        var debugString = toDebugString(result);
        logger.debug(
                "{}.{}: {}",
                currentLibrary != null ? currentLibrary.getIdentifier().getId() : "unknown",
                debugLocation,
                debugString);
    }

    public static String toDebugLocation(Element node) {
        String result = "";
        if (node.getLocator() != null) {
            result = node.getLocator();
        }
        if (node.getLocalId() != null) {
            result += "(" + node.getLocalId() + ")";
        }
        return result;
    }

    public static String toDebugString(Object result) {
        if (result instanceof Iterable) {
            Iterable<?> iterable = (Iterable<?>) result;
            return "{" +
                    StreamSupport.stream(iterable.spliterator(), false)
                            .map(DebugUtilities::toDebugString)
                            .collect(Collectors.joining(","))
                    + "}";
        }

        if (result != null) {
            return result.toString();
        }

        return "<null>";
    }
}
