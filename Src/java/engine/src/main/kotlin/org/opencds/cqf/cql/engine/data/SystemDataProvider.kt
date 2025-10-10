package org.opencds.cqf.cql.engine.data

import java.lang.reflect.Field
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.math.BigDecimal
import org.opencds.cqf.cql.engine.model.BaseModelResolver
import org.opencds.cqf.cql.engine.runtime.*
import org.opencds.cqf.cql.engine.runtime.Date

open class SystemDataProvider : BaseModelResolver(), DataProvider {
    override fun retrieve(
        context: String?,
        contextPath: String?,
        contextValue: Any?,
        dataType: String,
        templateId: String?,
        codePath: String?,
        codes: Iterable<Code>?,
        valueSet: String?,
        datePath: String?,
        dateLowPath: String?,
        dateHighPath: String?,
        dateRange: Interval?,
    ): Iterable<Any?>? {
        throw IllegalArgumentException("SystemDataProvider does not support retrieval.")
    }

    @Deprecated("Use packageNames instead")
    override var packageName: String?
        get() = "org.opencds.cqf.cql.engine.runtime"
        set(value) {}

    private fun getProperty(clazz: Class<*>, path: String?): Field {
        try {
            val field = clazz.getDeclaredField(path)
            return field
        } catch (e: NoSuchFieldException) {
            if (clazz.getSuperclass() != null) {
                val field = getProperty(clazz.getSuperclass(), path)
                return field
            }
            throw IllegalArgumentException(
                "Could not determine field for path ${path} of type ${clazz.getSimpleName()}"
            )
        }
    }

    internal class AccessorKey(private val path: String?, private val type: Class<*>) {
        override fun equals(other: Any?): Boolean {
            if (other is AccessorKey) {
                return path == other.path && type == other.type
            }
            return false
        }

        override fun hashCode(): Int {
            return path.hashCode() + 31 * type.hashCode()
        }
    }

    private fun getReadAccessor(clazz: Class<*>, path: String?): Method? {
        // Field field = getProperty(clazz, path);
        val accessorKey = AccessorKey(path, clazz)
        if (readAccessorCache.containsKey(accessorKey)) {
            return readAccessorCache.get(accessorKey)
        }

        val accessorMethodName = "get${path!![0].uppercase()}${path.substring(1)}"
        var accessor: Method? = null
        try {
            accessor = clazz.getMethod(accessorMethodName)
            readAccessorCache.put(accessorKey, accessor)
        } catch (e: NoSuchMethodException) {
            return null
        }
        return accessor
    }

    private fun getWriteAccessor(clazz: Class<*>, path: String?): Method {
        val accessorKey = AccessorKey(path, clazz)
        if (writeAccessorCache.containsKey(accessorKey)) {
            return writeAccessorCache.get(accessorKey)!!
        }

        val field = getProperty(clazz, path)
        val accessorMethodName = "set${path!![0].uppercase()}${path.substring(1)}"
        var accessor: Method? = null
        try {
            accessor = clazz.getMethod(accessorMethodName, field.getType())
            writeAccessorCache.put(accessorKey, accessor)
            return accessor
        } catch (e: NoSuchMethodException) {
            // If there is no setMethod with the exact signature, look for a signature that would
            // accept a value of the
            // type of the backing field
            for (method in clazz.getMethods()) {
                if (method.getName() == accessorMethodName && method.getParameterCount() == 1) {
                    val parameterTypes = method.getParameterTypes()
                    if (parameterTypes[0].isAssignableFrom(field.getType())) {
                        return method
                    }
                }
            }
            throw IllegalArgumentException(
                "Could not determine accessor function for property ${path} of type ${clazz.getSimpleName()}"
            )
        }
    }

    override fun resolvePath(target: Any?, path: String?): Any? {
        if (target == null) {
            return null
        }

        if (target is Tuple) {
            return target.getElement(path)
        }

        val clazz: Class<out Any?> = target.javaClass
        val accessor = getReadAccessor(clazz, path)
        if (accessor == null) {
            return null
        }

        try {
            return accessor.invoke(target)
        } catch (e: InvocationTargetException) {
            throw IllegalArgumentException(
                "Errors occurred attempting to invoke the accessor function for property ${path} of type ${clazz.getSimpleName()}"
            )
        } catch (e: IllegalAccessException) {
            throw IllegalArgumentException(
                "Could not invoke the accessor function for property ${path} of type ${clazz.getSimpleName()}"
            )
        }
    }

    override fun setValue(target: Any?, path: String?, value: Any?) {
        if (target == null) {
            return
        }

        val clazz: Class<out Any?> = target.javaClass
        val accessor = getWriteAccessor(clazz, path)
        try {
            accessor.invoke(target, value)
        } catch (e: InvocationTargetException) {
            throw IllegalArgumentException(
                "Errors occurred attempting to invoke the accessor function for property ${path} of type ${clazz.getSimpleName()}"
            )
        } catch (e: IllegalAccessException) {
            throw IllegalArgumentException(
                "Could not invoke the accessor function for property ${path} of type ${clazz.getSimpleName()}"
            )
        }
    }

    override fun resolveType(value: Any?): Class<*> {
        if (value == null) {
            return Any::class.java
        }

        return value.javaClass
    }

    override fun resolveType(typeName: String?): Class<*> {
        // Important: Boolean, Integer, and Long must be mapped to their Java Object types here
        when (typeName) {
            "Boolean" -> return Boolean::class.javaObjectType
            "Integer" -> return Int::class.javaObjectType
            "Decimal" -> return BigDecimal::class.java
            "String" -> return String::class.java
            "Quantity" -> return Quantity::class.java
            "Interval" -> return Interval::class.java
            "Long" -> return Long::class.javaObjectType
            "Tuple" -> return Tuple::class.java
            "DateTime" -> return DateTime::class.java
            "Date" -> return Date::class.java
            "Time" -> return Time::class.java
            else ->
                try {
                    return Class.forName("${packageName}.${typeName}")
                } catch (e: ClassNotFoundException) {
                    throw IllegalArgumentException(
                        "Could not resolve type ${packageName}.${typeName}."
                    )
                }
        }
    }

    override fun createInstance(typeName: String?): Any? {
        val clazz = resolveType(typeName)
        try {
            return clazz.getDeclaredConstructor().newInstance()
        } catch (e: InstantiationException) {
            throw IllegalArgumentException(
                "Could not create an instance of class ${clazz.getName()}."
            )
        } catch (e: InvocationTargetException) {
            throw IllegalArgumentException(
                "Could not create an instance of class ${clazz.getName()}."
            )
        } catch (e: ExceptionInInitializerError) {
            throw IllegalArgumentException(
                "Could not create an instance of class ${clazz.getName()}."
            )
        } catch (e: IllegalAccessException) {
            throw IllegalArgumentException(
                "Could not create an instance of class ${clazz.getName()}."
            )
        } catch (e: SecurityException) {
            throw IllegalArgumentException(
                "Could not create an instance of class ${clazz.getName()}."
            )
        } catch (e: NoSuchMethodException) {
            throw IllegalArgumentException(
                "Could not create an instance of class ${clazz.getName()}."
            )
        }
    }

    override fun objectEqual(left: Any?, right: Any?): Boolean? {
        if (left == null) {
            return null
        }

        if (right == null) {
            return null
        }

        return left == right
    }

    override fun objectEquivalent(left: Any?, right: Any?): Boolean? {
        if (left == null && right == null) {
            return true
        }

        if (left == null) {
            return false
        }

        if (left is CqlType) {
            return left.equivalent(right)
        }

        return left == right
    }

    override fun resolveId(target: Any?): String? {
        return null
    }

    override fun getContextPath(contextType: String?, targetType: String?): Any? {
        return null
    }

    companion object {
        private val readAccessorCache: MutableMap<AccessorKey?, Method?> =
            HashMap<AccessorKey?, Method?>()

        private val writeAccessorCache: MutableMap<AccessorKey?, Method> =
            HashMap<AccessorKey?, Method>()
    }
}
