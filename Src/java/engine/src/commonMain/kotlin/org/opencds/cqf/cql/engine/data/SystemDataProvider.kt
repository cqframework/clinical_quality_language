package org.opencds.cqf.cql.engine.data

import kotlin.reflect.KClass
import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.runtime.*
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.util.javaClassName

open class SystemDataProvider : BaseDataProvider {
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

    //    private fun getProperty(clazz: Class<*>, path: String?): Field {
    //        try {
    //            val field = clazz.getDeclaredField(path)
    //            return field
    //        } catch (e: NoSuchFieldException) {
    //            if (clazz.getSuperclass() != null) {
    //                val field = getProperty(clazz.getSuperclass(), path)
    //                return field
    //            }
    //            throw IllegalArgumentException(
    //                "Could not determine field for path ${path} of type ${clazz.getSimpleName()}"
    //            )
    //        }
    //    }

    //    internal class AccessorKey(private val path: String?, private val type: Class<*>) {
    //        override fun equals(other: Any?): Boolean {
    //            if (other is AccessorKey) {
    //                return path == other.path && type == other.type
    //            }
    //            return false
    //        }
    //
    //        override fun hashCode(): Int {
    //            return path.hashCode() + 31 * type.hashCode()
    //        }
    //    }

    //    private fun getReadAccessor(clazz: Class<*>, path: String?): Method? {
    //        // Field field = getProperty(clazz, path);
    //        val accessorKey = AccessorKey(path, clazz)
    //        if (readAccessorCache.containsKey(accessorKey)) {
    //            return readAccessorCache.get(accessorKey)
    //        }
    //
    //        val accessorMethodName = "get${path!![0].uppercase()}${path.substring(1)}"
    //        var accessor: Method? = null
    //        try {
    //            accessor = clazz.getMethod(accessorMethodName)
    //            readAccessorCache.put(accessorKey, accessor)
    //        } catch (e: NoSuchMethodException) {
    //            return null
    //        }
    //        return accessor
    //    }

    //    private fun getWriteAccessor(clazz: Class<*>, path: String?): Method {
    //        val accessorKey = AccessorKey(path, clazz)
    //        if (writeAccessorCache.containsKey(accessorKey)) {
    //            return writeAccessorCache.get(accessorKey)!!
    //        }
    //
    //        val field = getProperty(clazz, path)
    //        val accessorMethodName = "set${path!![0].uppercase()}${path.substring(1)}"
    //        var accessor: Method? = null
    //        try {
    //            accessor = clazz.getMethod(accessorMethodName, field.getType())
    //            writeAccessorCache.put(accessorKey, accessor)
    //            return accessor
    //        } catch (e: NoSuchMethodException) {
    //            // If there is no setMethod with the exact signature, look for a signature that
    // would
    //            // accept a value of the
    //            // type of the backing field
    //            for (method in clazz.getMethods()) {
    //                if (method.getName() == accessorMethodName && method.getParameterCount() == 1)
    // {
    //                    val parameterTypes = method.getParameterTypes()
    //                    if (parameterTypes[0].isAssignableFrom(field.getType())) {
    //                        return method
    //                    }
    //                }
    //            }
    //            throw IllegalArgumentException(
    //                "Could not determine accessor function for property ${path} of type
    // ${clazz.getSimpleName()}"
    //            )
    //        }
    //    }

    override fun resolvePath(target: Any?, path: String?): Any? {
        if (target == null) {
            return null
        }

        if (target is Tuple) {
            return target.getElement(path)
        }

        return when (target) {
            is Quantity -> {
                when (path) {
                    "value" -> target.value
                    "unit" -> target.unit
                    else -> null
                }
            }
            is Ratio -> {
                when (path) {
                    "numerator" -> target.numerator
                    "denominator" -> target.denominator
                    else -> null
                }
            }
            is Code -> {
                when (path) {
                    "code" -> target.code
                    "display" -> target.display
                    "system" -> target.system
                    "version" -> target.version
                    else -> null
                }
            }
            is Concept -> {
                when (path) {
                    "display" -> target.display
                    "codes" -> target.codes
                    else -> null
                }
            }
            is Interval -> {
                when (path) {
                    "low" -> target.low
                    "lowClosed" -> target.lowClosed
                    "high" -> target.high
                    "highClosed" -> target.highClosed
                    else -> null
                }
            }
            else -> null
        }

        //        val clazz: Class<out Any?> = target.javaClass
        //        val accessor = getReadAccessor(clazz, path)
        //        if (accessor == null) {
        //            return null
        //        }
        //
        //        try {
        //            return accessor.invoke(target)
        //        } catch (e: InvocationTargetException) {
        //            throw IllegalArgumentException(
        //                "Errors occurred attempting to invoke the accessor function for property
        // ${path} of type ${clazz.getSimpleName()}"
        //            )
        //        } catch (e: IllegalAccessException) {
        //            throw IllegalArgumentException(
        //                "Could not invoke the accessor function for property ${path} of type
        // ${clazz.getSimpleName()}"
        //            )
        //        }
    }

    override fun setValue(target: Any?, path: String?, value: Any?) {
        if (target == null) {
            return
        }

        when (target) {
            is Quantity -> {
                when (path) {
                    "value" -> target.value = value as BigDecimal?
                    "unit" -> target.unit = value as String?
                    else -> throw IllegalArgumentException("Could not set ${path} on Quantity.")
                }
            }
            is Ratio -> {
                when (path) {
                    "numerator" -> target.numerator = value as Quantity?
                    "denominator" -> target.denominator = value as Quantity?
                    else -> throw IllegalArgumentException("Could not set ${path} on Ratio.")
                }
            }
            is Code -> {
                when (path) {
                    "code" -> target.code = value as String?
                    "display" -> target.display = value as String?
                    "system" -> target.system = value as String?
                    "version" -> target.version = value as String?
                    else -> throw IllegalArgumentException("Could not set ${path} on Code.")
                }
            }
            is Concept -> {

                when (path) {
                    "display" -> target.display = value as String?
                    "codes" ->
                        target.codes = @Suppress("UNCHECKED_CAST") (value as MutableList<Code?>?)
                    else -> throw IllegalArgumentException("Could not set ${path} on Concept.")
                }
            }
            is Interval -> {
                when (path) {
                    "low" -> target.low = value
                    "high" -> target.high = value
                    else -> throw IllegalArgumentException("Could not set ${path} on Interval.")
                }
            }
            else ->
                throw IllegalArgumentException(
                    "Could not set ${path} on type ${target.javaClassName}."
                )
        }

        //        val clazz: Class<out Any?> = target.javaClass
        //        val accessor = getWriteAccessor(clazz, path)
        //        try {
        //            accessor.invoke(target, value)
        //        } catch (e: InvocationTargetException) {
        //            throw IllegalArgumentException(
        //                "Errors occurred attempting to invoke the accessor function for property
        // ${path} of type ${clazz.getSimpleName()}"
        //            )
        //        } catch (e: IllegalAccessException) {
        //            throw IllegalArgumentException(
        //                "Could not invoke the accessor function for property ${path} of type
        // ${clazz.getSimpleName()}"
        //            )
        //        }
    }

    override fun resolveKType(value: Any?): KClass<*> {
        if (value == null) {
            return Any::class
        }

        return value::class
    }

    override fun resolveKType(typeName: String?): KClass<*> {
        return when (typeName) {
            "Boolean" -> Boolean::class
            "Integer" -> Int::class
            "Long" -> Long::class
            "Decimal" -> BigDecimal::class
            "String" -> String::class
            "Date" -> Date::class
            "DateTime" -> DateTime::class
            "Time" -> Time::class
            "Quantity" -> Quantity::class
            "Ratio" -> Ratio::class
            "Code" -> Code::class
            "Concept" -> Concept::class
            "CodeSystem" -> CodeSystem::class
            "ValueSet" -> ValueSet::class
            "Interval" -> Interval::class
            "Tuple" -> Tuple::class
            else ->
                throw IllegalArgumentException("Could not resolve type ${packageName}.${typeName}.")
        //            else ->
        //                try {
        //                    return Class.forName("${packageName}.${typeName}")
        //                } catch (e: ClassNotFoundException) {
        //                    throw IllegalArgumentException(
        //                        "Could not resolve type ${packageName}.${typeName}."
        //                    )
        //                }
        }
    }

    override fun createInstance(typeName: String?): Any? {
        return when (typeName) {
            "Quantity" -> Quantity()
            "Ratio" -> Ratio()
            "Code" -> Code()
            "Concept" -> Concept()
            "CodeSystem" -> CodeSystem()
            "ValueSet" -> ValueSet()
            else -> throw IllegalArgumentException("Could not create an instance of $typeName.")
        }

        //        val clazz = resolveType(typeName)
        //        try {
        //            return clazz.getDeclaredConstructor().newInstance()
        //        } catch (e: InstantiationException) {
        //            throw IllegalArgumentException(
        //                "Could not create an instance of class ${clazz.getName()}."
        //            )
        //        } catch (e: InvocationTargetException) {
        //            throw IllegalArgumentException(
        //                "Could not create an instance of class ${clazz.getName()}."
        //            )
        //        } catch (e: ExceptionInInitializerError) {
        //            throw IllegalArgumentException(
        //                "Could not create an instance of class ${clazz.getName()}."
        //            )
        //        } catch (e: IllegalAccessException) {
        //            throw IllegalArgumentException(
        //                "Could not create an instance of class ${clazz.getName()}."
        //            )
        //        } catch (e: SecurityException) {
        //            throw IllegalArgumentException(
        //                "Could not create an instance of class ${clazz.getName()}."
        //            )
        //        } catch (e: NoSuchMethodException) {
        //            throw IllegalArgumentException(
        //                "Could not create an instance of class ${clazz.getName()}."
        //            )
        //        }
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

        return left == right
    }

    override fun resolveId(target: Any?): String? {
        return null
    }

    override fun getContextPath(contextType: String?, targetType: String?): Any? {
        return null
    }

    //    companion object {
    //        private val readAccessorCache: MutableMap<AccessorKey?, Method?> =
    //            HashMap<AccessorKey?, Method?>()
    //
    //        private val writeAccessorCache: MutableMap<AccessorKey?, Method> =
    //            HashMap<AccessorKey?, Method>()
    //    }
}
