package org.opencds.cqf.cql.engine.data;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;

import org.opencds.cqf.cql.engine.model.BaseModelResolver;
import org.opencds.cqf.cql.engine.runtime.Code;
import org.opencds.cqf.cql.engine.runtime.CqlType;
import org.opencds.cqf.cql.engine.runtime.Date;
import org.opencds.cqf.cql.engine.runtime.DateTime;
import org.opencds.cqf.cql.engine.runtime.Interval;
import org.opencds.cqf.cql.engine.runtime.Quantity;
import org.opencds.cqf.cql.engine.runtime.Time;
import org.opencds.cqf.cql.engine.runtime.Tuple;

public class SystemDataProvider extends BaseModelResolver implements DataProvider {

    @Override
    public Iterable<Object> retrieve(String context, String contextPath, Object contextValue, String dataType,
            String templateId, String codePath, Iterable<Code> codes, String valueSet, String datePath,
            String dateLowPath, String dateHighPath, Interval dateRange) {
        throw new IllegalArgumentException("SystemDataProvider does not support retrieval.");
    }

    @SuppressWarnings("deprecation")
    @Override
    public String getPackageName() {
        return "org.opencds.cqf.cql.engine.runtime";
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setPackageName(String packageName) {

    }

    private Field getProperty(Class<?> clazz, String path) {
        try {
            Field field = clazz.getDeclaredField(path);
            return field;
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) {
                Field field = getProperty(clazz.getSuperclass(), path);
                return field;
            }
            throw new IllegalArgumentException(String.format("Could not determine field for path %s of type %s", path, clazz.getSimpleName()));
        }
    }

    private Method getReadAccessor(Class<?> clazz, String path) {
        // Field field = getProperty(clazz, path);
        String accessorMethodName = String.format("%s%s%s", "get", path.substring(0, 1).toUpperCase(), path.substring(1));
        Method accessor = null;
        try {
            accessor = clazz.getMethod(accessorMethodName);
        } catch (NoSuchMethodException e) {
            return null;
        }
        return accessor;
    }

    private Method getWriteAccessor(Class<?> clazz, String path) {
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

        if (target instanceof Tuple) {
            return ((Tuple)target).getElement(path);
        }

        Class<? extends Object> clazz = target.getClass();
        Method accessor = getReadAccessor(clazz, path);
        if (accessor == null) {
            return null;
        }

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
    public Class<?> resolveType(Object value) {
        if (value == null) {
            return Object.class;
        }

        return value.getClass();
    }

    @Override
    public Class<?> resolveType(String typeName) {
        switch (typeName) {
            case "Boolean": return Boolean.class;
            case "Integer": return Integer.class;
            case "Decimal": return BigDecimal.class;
            case "String": return String.class;
            case "Quantity": return Quantity.class;
            case "Interval": return Interval.class;
            case "Long": return Long.class;
            case "Tuple": return Tuple.class;
            case "DateTime": return DateTime.class;
            case "Date": return Date.class;
            case "Time": return Time.class;
            default:
                try {
                    return Class.forName(String.format("%s.%s", getPackageName(), typeName));
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException(String.format("Could not resolve type %s.%s.", getPackageName(), typeName));
                }
        }
    }

	@Override
    public Object createInstance(String typeName) {
        Class<?> clazz = resolveType(typeName);
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | InvocationTargetException |
            ExceptionInInitializerError | IllegalAccessException | SecurityException | NoSuchMethodException e) {
            throw new IllegalArgumentException(String.format("Could not create an instance of class %s.", clazz.getName()));
        }
    }

    @Override
    public Boolean objectEqual(Object left, Object right) {
        if (left == null) {
            return null;
        }

        if (right == null) {
            return null;
        }

        return left.equals(right);
    }

    @Override
    public Boolean objectEquivalent(Object left, Object right) {
        if (left == null && right == null) {
            return true;
        }

        if (left == null) {
            return false;
        }

        if (left instanceof CqlType) {
            return ((CqlType)left).equivalent(right);
        }

        return left.equals(right);
    }

    @Override
    public String resolveId(Object target) {
        return null;
    }

    @Override
    public Object getContextPath(String contextType, String targetType) {
        return null;
    }
}
