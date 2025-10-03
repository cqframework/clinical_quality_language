package org.opencds.cqf.cql.engine.fhir.model

import ca.uhn.fhir.context.BaseRuntimeChildDefinition
import ca.uhn.fhir.context.BaseRuntimeElementCompositeDefinition
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.RuntimeChildChoiceDefinition
import ca.uhn.fhir.context.RuntimeChildPrimitiveDatatypeDefinition
import ca.uhn.fhir.context.RuntimeChildResourceBlockDefinition
import ca.uhn.fhir.context.RuntimeChildResourceDefinition
import ca.uhn.fhir.model.api.TemporalPrecisionEnum
import java.lang.reflect.InvocationTargetException
import java.util.Calendar
import java.util.GregorianCalendar
import kotlin.Any
import kotlin.Boolean
import kotlin.Exception
import kotlin.IllegalArgumentException
import kotlin.Int
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.instance.model.api.IAnyResource
import org.hl7.fhir.instance.model.api.IBase
import org.hl7.fhir.instance.model.api.IBaseBackboneElement
import org.hl7.fhir.instance.model.api.IBaseElement
import org.hl7.fhir.instance.model.api.IBaseEnumFactory
import org.hl7.fhir.instance.model.api.IBaseEnumeration
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.instance.model.api.ICompositeType
import org.hl7.fhir.instance.model.api.IPrimitiveType
import org.opencds.cqf.cql.engine.exception.DataProviderException
import org.opencds.cqf.cql.engine.exception.InvalidCast
import org.opencds.cqf.cql.engine.exception.InvalidPrecision
import org.opencds.cqf.cql.engine.fhir.exception.UnknownType
import org.opencds.cqf.cql.engine.model.ModelResolver
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Precision
import org.opencds.cqf.cql.engine.runtime.TemporalHelper
import org.opencds.cqf.cql.engine.runtime.Time

// TODO: Probably quite a bit of redundancy here. Probably only really need the BaseType and the
// PrimitiveType
/*
 * type-to-class and contextPath resolutions are potentially expensive and can be cached
 * for improved performance.
 *
 * See <a href="https://github.com/DBCG/cql-evaluator/blob/master/evaluator.engine/src/main/java/org/opencds/cqf/cql/evaluator/engine/model/CachingModelResolverDecorator.java"/>
 * for a decorator that adds caching logic for ModelResolvers.
 */
abstract class FhirModelResolver<
    BaseType,
    BaseDateTimeType,
    TimeType,
    SimpleQuantityType,
    IdType,
    ResourceType,
    EnumerationType : IBaseEnumeration<*>,
    EnumFactoryType : IBaseEnumFactory<*>,
>( // getters & setters
    // Data members
    var fhirContext: FhirContext
) : ModelResolver {
    protected abstract fun initialize()

    protected abstract fun equalsDeep(left: BaseType, right: BaseType): Boolean

    abstract fun castToSimpleQuantity(base: BaseType): SimpleQuantityType

    protected abstract fun getCalendar(dateTime: BaseDateTimeType): Calendar

    protected abstract fun getCalendarConstant(dateTime: BaseDateTimeType): Int

    protected abstract fun setCalendarConstant(target: BaseDateTimeType, value: BaseTemporal)

    protected abstract fun idToString(id: IdType): String

    protected abstract fun timeToString(time: TimeType): String

    protected abstract fun getResourceType(resource: ResourceType): String

    protected abstract fun enumConstructor(factory: EnumFactoryType): EnumerationType

    protected abstract fun enumChecker(`object`: Any): Boolean

    protected abstract fun enumFactoryTypeGetter(enumeration: EnumerationType): Class<*>

    init {
        this.initialize()
    }

    override fun resolveId(target: Any?): String? {
        if (target is IBaseResource) {
            return target.idElement.idPart
        }
        return null
    }

    override fun getContextPath(contextType: String?, targetType: String?): Any? {
        if (targetType == null || contextType == null) {
            return null
        }

        if (
            contextType == "Unfiltered" ||
                contextType == "Unspecified" ||
                contextType == "Population"
        ) {
            return null
        }

        if (targetType == contextType) {
            return "id"
        }

        val resourceDefinition = this.fhirContext.getResourceDefinition(targetType)
        val theValue = this.createInstance(contextType)
        val type = theValue.javaClass as Class<out IBase>

        val children = resourceDefinition.children
        for (child in children) {
            val visitedElements: MutableSet<String?> = HashSet()
            val path = this.innerGetContextPath(visitedElements, child, type)
            if (path != null) {
                return path
            }
        }

        return null
    }

    protected fun innerGetContextPath(
        visitedElements: MutableSet<String?>,
        child: BaseRuntimeChildDefinition,
        type: Class<out IBase?>?,
    ): String? {
        visitedElements.add(child.elementName)

        if (child is RuntimeChildResourceDefinition) {
            for (resourceClass in child.resourceTypes) {
                if (resourceClass == type) {
                    return child.elementName
                }
            }

            return null
        }

        if (child is RuntimeChildResourceBlockDefinition) {
            val currentName = child.elementName
            val element = child.getChildByName(currentName)
            val children = element.getChildren() as List<BaseRuntimeChildDefinition>

            for (nextChild in children) {
                if (visitedElements.contains(nextChild.elementName)) {
                    continue
                }

                val path = this.innerGetContextPath(visitedElements, nextChild, type)
                if (path != null) {
                    return "$currentName.$path"
                }
            }
        }

        return null
    }

    override fun objectEqual(left: Any?, right: Any?): Boolean? {
        if (left == null) {
            return null
        }

        if (right == null) {
            return null
        }

        return this.equalsDeep(left as BaseType, right as BaseType)
    }

    override fun objectEquivalent(left: Any?, right: Any?): Boolean {
        if (left == null && right == null) {
            return true
        }

        if (left == null || right == null) {
            return false
        }

        return this.equalsDeep(left as BaseType, right as BaseType)
    }

    override fun createInstance(typeName: String?): Any {
        return createInstance(resolveType(typeName)!!)
    }

    @get:Deprecated("Deprecated in Java")
    @set:Deprecated("Deprecated in Java")
    override var packageName: String?
        get() {
            if (packageNames.isEmpty()) {
                return null
            }
            return packageNames[0]
        }
        set(packageName) {
            this.packageNames =
                if (packageName != null) mutableListOf(packageName) else mutableListOf()
        }

    override var packageNames = mutableListOf<String?>()

    override fun resolvePath(target: Any?, path: String?): Any? {
        var target = target
        val identifiers =
            path!!.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (identifier in identifiers) {
            // handling indexes: i.e. item[0].code
            if (identifier.contains("[")) {
                val index = Character.getNumericValue(identifier[identifier.indexOf("[") + 1])
                target = resolveProperty(target, identifier.replace("\\[\\d\\]".toRegex(), ""))!!
                target = (target as ArrayList<*>)[index]
            } else {
                target = resolveProperty(target, identifier)
            }
        }

        return target
    }

    override fun resolveType(typeName: String?): Class<*>? {
        // For Dstu2
        var typeName = typeName
        if (typeName!!.startsWith("FHIR.")) {
            typeName = typeName.replace("FHIR.", "")
        }
        // dataTypes
        val definition = this.fhirContext.getElementDefinition(typeName)
        if (definition != null) {
            return definition.getImplementingClass()
        }

        try {
            // Resources
            return this.fhirContext.getResourceDefinition(typeName).implementingClass
        } catch (_: Exception) {}

        try {
            if (typeName.contains(".")) {
                val path =
                    typeName.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val resourceDefinition =
                    if (this.fhirContext.resourceTypes.contains(path[0]))
                        this.fhirContext.getResourceDefinition(path[0])
                    else this.fhirContext.getElementDefinition(path[0])
                var childName = path[1][0].lowercaseChar().toString() + path[1].substring(1)
                var childDefinition =
                    resourceDefinition!!.getChildByName(childName)
                        ?: return resolveChildren(resourceDefinition.children, childName)
                var childElement = childDefinition.getChildByName(childName)
                for (i in 2..<path.size) {
                    childName = path[i][0].lowercaseChar().toString() + path[i].substring(1)
                    childDefinition = childElement.getChildByName(childName)
                    childElement = childDefinition.getChildByName(childName)
                }
                return childElement.getImplementingClass()
            }
        } catch (_: Exception) {}

        // Special case for enumerations. They are often in the "Enumerations" class.
        for (packageName in this.packageNames) {
            try {
                return Class.forName(String.format("%s.Enumerations$%s", packageName, typeName))
            } catch (_: ClassNotFoundException) {}
        }

        // Other Types in package.
        for (packageName in this.packageNames) {
            try {
                return Class.forName(String.format("%s.%s", packageName, typeName))
            } catch (_: ClassNotFoundException) {}
        }

        // Scan all resources.
        // Really, HAPI ought to register inner classes, right?
        val clazz = deepSearch(typeName)
        if (clazz != null) {
            return clazz
        }

        try {
            // Just give me SOMETHING.
            return Class.forName(typeName)
        } catch (_: ClassNotFoundException) {
            throw UnknownType(
                String.format(
                    "Could not resolve type %s. Primary package(s) for this resolver are %s",
                    typeName,
                    this.packageNames.joinToString(","),
                )
            )
        }
    }

    // This method walks the entire child tree of a resource/element until the specified child is
    // found
    // This is for cases when the child path is not well-defined (STU3 and 4.0.0)
    private fun resolveChildren(
        children: List<BaseRuntimeChildDefinition>,
        childName: String,
    ): Class<*>? {
        for (c in children) {
            if (c.validChildNames.contains(childName)) {
                return c.getChildByName(childName).getImplementingClass()
            }
            if (c is RuntimeChildResourceBlockDefinition) {
                for (childrenName in c.validChildNames) {
                    if (c.elementName == childrenName) continue
                    val nextChildren =
                        c.getChildByName(childrenName).getChildren()
                            as List<BaseRuntimeChildDefinition>
                    resolveChildren(nextChildren, childName)
                }
            }
        }
        return null
    }

    override fun resolveType(value: Any?): Class<*>? {
        if (value == null) {
            return Any::class.java
        }

        // For FHIR enumerations, return the type of the backing Enum
        if (this.enumChecker(value)) {
            val factoryName = this.enumFactoryTypeGetter(value as EnumerationType).getSimpleName()
            return this.resolveType(factoryName.substringBefore("EnumFactory"))
        }

        return value.javaClass
    }

    override fun setValue(target: Any?, path: String?, value: Any?) {
        var value = value
        if (target == null) {
            return
        }

        if (target is IBaseEnumeration<*> && path == "value") {
            target.valueAsString = value as String?
            return
        }

        val base = target as IBase
        val definition: BaseRuntimeElementCompositeDefinition<*>
        if (base is IPrimitiveType<*>) {
            setPrimitiveValue(value, base)
            return
        } else {
            definition = resolveRuntimeDefinition(base)
        }

        var child = definition.getChildByName(path)
        if (child == null) {
            child = resolveChoiceProperty(definition, path)
        }

        if (child == null) {
            throw DataProviderException(String.format("Unable to resolve path %s.", path))
        }

        try {
            if (value is Iterable<*>) {
                for (`val` in value) {
                    child.mutator.addValue(base, setBaseValue(`val`!!, base))
                }
            } else {
                child.mutator.setValue(base, setBaseValue(value, base))
            }
        } catch (le: IllegalArgumentException) {
            if (value!!.javaClass.getSimpleName() == "Quantity") {
                try {
                    value = this.castToSimpleQuantity(value as BaseType)!!
                } catch (_: FHIRException) {
                    throw InvalidCast("Unable to cast Quantity to SimpleQuantity")
                }
                child.mutator.setValue(base, setBaseValue(value, base))
            } else {
                throw DataProviderException(
                    String.format("Configuration error encountered: %s", le.message)
                )
            }
        }
    }

    // Resolutions
    protected open fun resolveProperty(target: Any?, path: String): Any? {
        if (target == null) {
            return null
        }

        if (target is IBaseEnumeration<*> && path == "value") {
            return target.valueAsString
        }

        // TODO: Consider using getResourceType everywhere?
        if (target is IAnyResource && this.getResourceType(target as ResourceType) == path) {
            return target
        }

        val base = target as IBase
        val definition: BaseRuntimeElementCompositeDefinition<*>
        if (base is IPrimitiveType<*>) {
            return toJavaPrimitive(
                if (path == "value") (target as IPrimitiveType<*>).getValue() else target,
                base,
            )
        } else {
            definition = resolveRuntimeDefinition(base)
        }

        var child = definition.getChildByName(path)
        if (child == null) {
            child = resolveChoiceProperty(definition, path)
        }

        if (child == null) {
            return null
        }

        val values = child.accessor.getValues(base)

        if (values == null || values.isEmpty()) {
            return null
        }

        // If the instance is a primitive (including (or even especially an enumeration), and it has
        // no value, return
        // null
        if (child is RuntimeChildPrimitiveDatatypeDefinition) {
            val value = values[0]
            if (value is IPrimitiveType<*>) {
                if (!value.hasValue()) {
                    return null
                }
            }
        }

        if (
            child is RuntimeChildChoiceDefinition &&
                !child.elementName.equals(path, ignoreCase = true)
        ) {
            if (
                !values[0]!!
                    .javaClass
                    .getSimpleName()
                    .equals(
                        child.getChildByName(path).getImplementingClass().getSimpleName(),
                        ignoreCase = true,
                    )
            ) {
                return null
            }
        }

        return toJavaPrimitive(if (child.max < 1) values else values[0], base)
    }

    protected fun resolveRuntimeDefinition(base: IBase): BaseRuntimeElementCompositeDefinition<*> {
        when (base) {
            is IAnyResource -> {
                return this.fhirContext.getResourceDefinition(base)
            }

            is IBaseBackboneElement,
            is IBaseElement -> {
                return (this.fhirContext.getElementDefinition(base.javaClass)
                    as BaseRuntimeElementCompositeDefinition<*>?)!!
            }

            is ICompositeType -> {
                return this.fhirContext.getElementDefinition(base.javaClass)
                    as BaseRuntimeElementCompositeDefinition<ICompositeType?>
            }

            else ->
                throw UnknownType(
                    String.format(
                        "Unable to resolve the runtime definition for %s",
                        base.javaClass.getName(),
                    )
                )
        }
    }

    protected fun resolveChoiceProperty(
        definition: BaseRuntimeElementCompositeDefinition<*>,
        path: String?,
    ): BaseRuntimeChildDefinition? {
        for (child in definition.children) {
            if (child is RuntimeChildChoiceDefinition) {
                if (child.elementName.startsWith(path!!)) {
                    return child
                }
            }
        }

        return null
    }

    private fun deepSearch(typeName: String): Class<*>? {
        // Special case for "Codes". This suffix is often removed from the HAPI type.
        val codelessName = typeName.replace("Codes", "").lowercase()
        val lowerName = typeName.lowercase()

        val elements = this.fhirContext.elementDefinitions
        for (element in elements) {
            val innerClasses = element.getImplementingClass().getDeclaredClasses()
            for (clazz in innerClasses) {
                val clazzLowerName = clazz.getSimpleName().lowercase()
                if (clazzLowerName == lowerName || clazzLowerName == codelessName) {
                    return clazz
                }
            }
        }

        return null
    }

    // Creators
    protected fun createInstance(clazz: Class<*>): Any {
        try {
            if (clazz.isEnum) {
                val factoryClass = this.resolveType(clazz.getName() + "EnumFactory")
                val factory = this.createInstance(factoryClass!!) as EnumFactoryType
                return this.enumConstructor(factory)
            }

            return clazz.getDeclaredConstructor().newInstance()
        } catch (e: NoSuchMethodException) {
            throw UnknownType(
                String.format(
                    "Could not create an instance of class %s.\nRoot cause: %s",
                    clazz.getName(),
                    e.message,
                )
            )
        } catch (e: InvocationTargetException) {
            throw UnknownType(
                String.format(
                    "Could not create an instance of class %s.\nRoot cause: %s",
                    clazz.getName(),
                    e.message,
                )
            )
        } catch (e: InstantiationException) {
            throw UnknownType(
                String.format(
                    "Could not create an instance of class %s.\nRoot cause: %s",
                    clazz.getName(),
                    e.message,
                )
            )
        } catch (e: IllegalAccessException) {
            throw UnknownType(
                String.format(
                    "Could not create an instance of class %s.\nRoot cause: %s",
                    clazz.getName(),
                    e.message,
                )
            )
        }
    }

    protected fun toTime(value: TimeType): Time {
        return Time(timeToString(value))
    }

    // Transformations
    protected fun toDateTime(
        value: BaseDateTimeType,
        calendarConstant: Int = this.getCalendarConstant(value),
    ): DateTime {
        val calendar = this.getCalendar(value)

        // TimeZone tz = calendar.getTimeZone() == null ? TimeZone.getDefault() :
        // calendar.getTimeZone();
        // ZoneOffset zoneOffset =
        // tz.toZoneId().getRules().getStandardOffset(calendar.toInstant());
        val zoneOffset = (calendar as GregorianCalendar).toZonedDateTime().offset
        when (calendarConstant) {
            Calendar.YEAR ->
                return DateTime(
                    TemporalHelper.zoneToOffset(zoneOffset),
                    calendar.get(Calendar.YEAR),
                )
            Calendar.MONTH ->
                return DateTime(
                    TemporalHelper.zoneToOffset(zoneOffset),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                )

            Calendar.DAY_OF_MONTH ->
                return DateTime(
                    TemporalHelper.zoneToOffset(zoneOffset),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH),
                )

            Calendar.HOUR_OF_DAY ->
                return DateTime(
                    TemporalHelper.zoneToOffset(zoneOffset),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.HOUR_OF_DAY),
                )

            Calendar.MINUTE ->
                return DateTime(
                    TemporalHelper.zoneToOffset(zoneOffset),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                )

            Calendar.SECOND ->
                return DateTime(
                    TemporalHelper.zoneToOffset(zoneOffset),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    calendar.get(Calendar.SECOND),
                )

            Calendar.MILLISECOND ->
                return DateTime(
                    TemporalHelper.zoneToOffset(zoneOffset),
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH),
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    calendar.get(Calendar.SECOND),
                    calendar.get(Calendar.MILLISECOND),
                )

            else ->
                throw InvalidPrecision(
                    String.format("Invalid temporal precision %s", calendarConstant)
                )
        }
    }

    protected fun toDate(
        value: BaseDateTimeType,
        calendarConstant: Int = this.getCalendarConstant(value),
    ): Date {
        val calendar = this.getCalendar(value)
        // TimeZone tz = calendar.getTimeZone() == null ? TimeZone.getDefault() :
        // calendar.getTimeZone();
        // ZoneOffset zoneOffset =
        // tz.toZoneId().getRules().getStandardOffset(calendar.toInstant());
        return when (calendarConstant) {
            Calendar.YEAR -> Date(calendar.get(Calendar.YEAR))
            Calendar.MONTH -> Date(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1)

            Calendar.DAY_OF_MONTH ->
                Date(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DAY_OF_MONTH),
                )

            else ->
                throw InvalidPrecision(
                    String.format("Invalid temporal precision %s", calendarConstant)
                )
        }
    }

    fun setBaseValue(value: Any?, target: IBase?): IBase? {
        if (target is IPrimitiveType<*>) {
            setPrimitiveValue(value, target)
        }
        return value as IBase?
    }

    fun setPrimitiveValue(value: Any?, target: IPrimitiveType<*>) {
        val simpleName = target.javaClass.getSimpleName()
        when (simpleName) {
            "DateTimeType",
            "InstantType" -> {
                // Ensure offset is taken into account from the ISO datetime String instead of the
                // default timezone
                target.valueAsString = (value as DateTime).toDateString()
                setCalendarConstant(target as BaseDateTimeType, value as BaseTemporal)
            }
            "DateType" -> {
                val targetValue = (value as Date).toJavaDate()
                (target as IPrimitiveType<java.util.Date>).value = targetValue
                setCalendarConstant(target as BaseDateTimeType, value as BaseTemporal)
            }

            "TimeType" -> (target as IPrimitiveType<String>).value = value.toString()
            "Base64BinaryType" -> target.valueAsString = value as String?
            else -> (target as IPrimitiveType<Any?>).value = value
        }
    }

    fun toTemporalPrecisionEnum(precision: Precision): TemporalPrecisionEnum {
        return when (precision) {
            Precision.YEAR -> TemporalPrecisionEnum.YEAR
            Precision.MONTH -> TemporalPrecisionEnum.MONTH
            Precision.DAY -> TemporalPrecisionEnum.DAY
            Precision.HOUR,
            Precision.MINUTE -> TemporalPrecisionEnum.MINUTE
            Precision.SECOND -> TemporalPrecisionEnum.SECOND
            Precision.MILLISECOND -> TemporalPrecisionEnum.MILLI
            else ->
                throw IllegalArgumentException(
                    String.format("Unknown precision %s", precision.toString())
                )
        }
    }

    /*
     * // TODO: Find HAPI registry of Primitive Type conversions public Object
     * fromJavaPrimitive(Object value, Object target) { String simpleName =
     * target.getClass().getSimpleName(); switch(simpleName) { case "DateTimeType":
     * case "InstantType": return ((DateTime)value).toJavaDate(); case "DateType":
     * return ((org.opencds.cqf.cql.engine.runtime.Date)value).toJavaDate(); case
     * "TimeType": return ((Time) value).toString(); }
     *
     * if (value instanceof Time) { return ((Time) value).toString(); } else {
     * return value; } }
     */
    fun toJavaPrimitive(result: Any?, source: Any): Any? {
        if (source is IPrimitiveType<*> && !source.hasValue()) {
            return null
        }

        val simpleName = source.javaClass.getSimpleName()
        @Suppress("UNCHECKED_CAST")
        return when (simpleName) {
            "InstantType",
            "DateTimeType" -> toDateTime(source as BaseDateTimeType)
            "DateType" -> toDate(source as BaseDateTimeType)
            "TimeType" -> toTime(source as TimeType)
            "IdType" -> this.idToString(source as IdType)
            "Base64BinaryType" -> (source as IPrimitiveType<*>).valueAsString
            else -> result
        }
    }
}
