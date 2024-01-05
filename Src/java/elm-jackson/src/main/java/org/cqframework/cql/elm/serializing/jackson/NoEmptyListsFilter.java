package org.cqframework.cql.elm.serializing.jackson;

import java.util.Collection;

class NoEmptyListsFilter {

    // True if values should be filtered, false otherwise.
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return true;
        }

        if (obj instanceof Collection) {
            return ((Collection<?>) obj).isEmpty();
        }

        return false;
    }
}
