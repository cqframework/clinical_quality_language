package org.cqframework.cql.data;

import org.cqframework.cql.runtime.Code;
import org.cqframework.cql.runtime.Interval;
import org.cqframework.cql.runtime.Quantity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;

/**
 * Created by Bryn on 5/4/2016.
 */
public class SystemDataProvider implements DataProvider {

    @Override
    public Iterable<Object> retrieve(String context, String dataType, String templateId, String codePath, Iterable<Code> codes, String valueSet, String datePath, String dateLowPath, String dateHighPath, Interval dateRange) {
        throw new IllegalArgumentException("SystemDataProvider does not support retrieval.");
    }

    @Override
    public String getPackageName() {
        return "org.cqframework.cql.runtime";
    }

    private Field getProperty(Class clazz, String path) {
        try {
            Field field = clazz.getDeclaredField(path);
            return field;
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(String.format("Could not determine field for path %s of type %s", path, clazz.getSimpleName()));
        }
    }

    private Method getReadAccessor(Class clazz, String path) {
        Field field = getProperty(clazz, path);
        String accessorMethodName = String.format("%s%s%s", "get", path.substring(0, 1).toUpperCase(), path.substring(1));
        Method accessor = null;
        try {
            accessor = clazz.getMethod(accessorMethodName);
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(String.format("Could not determine accessor function for property %s of type %s", path, clazz.getSimpleName()));
        }
        return accessor;
    }

    private Method getWriteAccessor(Class clazz, String path) {
        Field field = getProperty(clazz, path);
        String accessorMethodName = String.format("%s%s%s", "set", path.substring(0, 1).toUpperCase(), path.substring(1));
        Method accessor = null;
        try {
            accessor = clazz.getMethod(accessorMethodName, field.getType());
            return accessor;
        } catch (NoSuchMethodException e) {
            // If there is no setMethod with the exact signature, look for a signature that would accept a value of the type of the backing field
            for (Method method : clazz.getMethods()) {
                if (method.getName().equals(accessorMethodName) && method.getParameterCount() == 1) {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes[0].isAssignableFrom(field.getType())) {
                        return method;
                    }
                }
            }
            throw new IllegalArgumentException(String.format("Could not determine accessor function for property %s of type %s", path, clazz.getSimpleName()));
        }
    }

    @Override
    public Object resolvePath(Object target, String path) {
        if (target == null) {
            return null;
        }

        Class<? extends Object> clazz = target.getClass();
        Method accessor = getReadAccessor(clazz, path);
        try {
            return accessor.invoke(target);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(String.format("Errors occurred attempting to invoke the accessor function for property %s of type %s", path, clazz.getSimpleName()));
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(String.format("Could not invoke the accessor function for property %s of type %s", path, clazz.getSimpleName()));
        }
    }

    public void setValue(Object target, String path, Object value) {
        if (target == null) {
            return;
        }

        Class<? extends Object> clazz = target.getClass();
        Method accessor = getWriteAccessor(clazz, path);
        try {
            accessor.invoke(target, value);
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(String.format("Errors occurred attempting to invoke the accessor function for property %s of type %s", path, clazz.getSimpleName()));
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(String.format("Could not invoke the accessor function for property %s of type %s", path, clazz.getSimpleName()));
        }
    }

    @Override
    public Class resolveType(String typeName) {
        switch (typeName) {
            case "Boolean": return Boolean.class;
            case "Integer": return Integer.class;
            case "Decimal": return BigDecimal.class;
            case "String": return String.class;
            case "Quantity": return Quantity.class;
            case "Interval": return Interval.class;
            //case "Tuple": return Tuple.class;
            default:
                try {
                    return Class.forName(String.format("%s.%s", getPackageName(), typeName));
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException(String.format("Could not resolve type %s.%s.", getPackageName(), typeName));
                }
        }
    }
}
