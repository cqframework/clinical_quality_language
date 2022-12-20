package org.opencds.cqf.cql.engine.debug;

/*
Specifies a Debug entry point (breakpoint)
Can be
* nodeId: corresponding to the localId element of a node in the ELM
* nodeType: corresponding to the type of the node (will match all nodes of that type)
* locator: corresponding to a range in the source (will match all nodes with a locator range that includes the locator)
* exceptionType: corresponding to the type of an exception (will match whenever an exception of this type occurs)
 */
public class DebugLocator {
    public enum DebugLocatorType {
        NODE_ID,
        NODE_TYPE,
        LOCATION,
        EXCEPTION_TYPE
    }

    private final DebugLocatorType type;
    public DebugLocatorType getLocatorType() {
        return type;
    }
    private final String locator;
    public String getLocator() {
        return locator;
    }

    private final Location location;
    public Location getLocation() {
        return location;
    }

    public DebugLocator(Location location) {
        this.type = DebugLocatorType.LOCATION;
        this.locator = location.toLocator();
        this.location = location;
    }

    private void guardLocator(String locator) {
        if (locator == null || locator.trim().isEmpty()) {
            throw new IllegalArgumentException("nodeId locator required");
        }
    }

    public DebugLocator(DebugLocatorType type, String locator) {
        this.type = type;
        switch (type) {
            case NODE_ID:
            case EXCEPTION_TYPE:
                guardLocator(locator);
                this.locator = locator;
                this.location = null;
            break;

            case NODE_TYPE:
                guardLocator(locator);
                if (!locator.endsWith("Evaluator")) {
                    this.locator = locator + "Evaluator";
                }
                else {
                    this.locator = locator;
                }
                this.location = null;
            break;

            case LOCATION:
                this.location = Location.fromLocator(locator);
                this.locator = locator;
            break;

            default: throw new IllegalArgumentException(String.format("Unknown debug locator type: %s", type.toString()));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DebugLocator other = (DebugLocator)o;

        if (type != other.type) {
            return false;
        }

        if (locator != other.locator) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = locator.hashCode();
        result = 31 * result + type.ordinal();
        return result;
    }

    @Override
    public String toString() {
        return "DebugLocator{" +
                " type=" + type.toString() +
                ", locator=" + locator +
                '}';
    }
}
