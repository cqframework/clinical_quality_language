package org.cqframework.cql.cql2elm;

import org.cqframework.cql.elm.tracking.DataType;
import org.cqframework.cql.elm.tracking.TupleType;
import org.cqframework.cql.elm.tracking.TupleTypeElement;

import javax.xml.bind.annotation.XmlType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

public class DataTypes {
    public static void verifyType(DataType actualType, DataType expectedType) {
        if (!subTypeOf(actualType, expectedType)) {
            throw new IllegalArgumentException(String.format(
                    "Expected an expression of type '%s', but found an expression of type '%s'.",
                    expectedType != null ? expectedType.toString() : "<unknown>",
                    actualType != null ? actualType.toString() : "<unknown>"
            ));
        }
    }

    public static boolean equal(DataType a, DataType b) {
        return a != null && b != null && a.equals(b);
    }

    public static boolean equivalent(DataType a, DataType b) {
        return false;
        //return a != null && b != null & a.equivalentTo(b);
    }

    public static boolean subTypeOf(DataType a, DataType b) {
        return a != null && b != null && a.isSubTypeOf(b);
    }

    public static boolean superTypeOf(DataType a, DataType b) {
        return a != null && b != null && a.isSuperTypeOf(b);
    }
}
