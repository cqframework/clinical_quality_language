package org.cqframework.cql.elm.tracking;

import java.util.Collection;

/**
 * Created by Bryn on 8/22/2016.
 */
public class ProfileType extends ClassType {
    public ProfileType(String name, DataType baseType, Collection<ClassTypeElement> elements) {
        super(name, baseType, elements);
    }

    public ProfileType() {
        super();
    }

    public ProfileType(String name) {
        super(name);
    }

    public ProfileType(String name, DataType baseType) {
        super(name, baseType);
    }

}
