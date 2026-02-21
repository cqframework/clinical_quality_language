package org.opencds.cqf.cql.engine.data

import org.cqframework.cql.shared.BigDecimal
import org.opencds.cqf.cql.engine.model.BaseModelResolver
import org.opencds.cqf.cql.engine.runtime.*
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.util.JavaClass
import org.opencds.cqf.cql.engine.util.javaClass
import org.opencds.cqf.cql.engine.util.javaClassName
import org.opencds.cqf.cql.engine.util.kotlinClassToJavaClass

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
    }

    override fun resolveType(value: Any?): JavaClass<*> {
        if (value == null) {
            return kotlinClassToJavaClass(Any::class)
        }

        return value.javaClass
    }

    override fun resolveType(typeName: String?): JavaClass<*> {
        return kotlinClassToJavaClass(
            when (typeName) {
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
                    throw IllegalArgumentException(
                        "Could not resolve type ${packageName}.${typeName}."
                    )
            }
        )
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
}
