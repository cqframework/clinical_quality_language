package org.opencds.cqf.cql.engine.fhir.model

import ca.uhn.fhir.context.BaseRuntimeChildDefinition
import ca.uhn.fhir.context.BaseRuntimeElementCompositeDefinition
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.RuntimeChildResourceBlockDefinition
import ca.uhn.fhir.context.RuntimeChildResourceDefinition
import ca.uhn.fhir.context.RuntimeCompositeDatatypeDefinition
import ca.uhn.fhir.model.api.TemporalPrecisionEnum
import java.lang.reflect.InvocationTargetException
import java.util.Calendar
import java.util.GregorianCalendar
import java.util.Optional
import java.util.concurrent.ConcurrentHashMap
import javax.xml.namespace.QName
import org.hl7.fhir.instance.model.api.IAnyResource
import org.hl7.fhir.instance.model.api.IBase
import org.hl7.fhir.instance.model.api.IBaseBackboneElement
import org.hl7.fhir.instance.model.api.IBaseElement
import org.hl7.fhir.instance.model.api.IBaseEnumFactory
import org.hl7.fhir.instance.model.api.IBaseEnumeration
import org.hl7.fhir.instance.model.api.IBaseHasExtensions
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.instance.model.api.ICompositeType
import org.hl7.fhir.instance.model.api.IIdType
import org.hl7.fhir.instance.model.api.IPrimitiveType
import org.opencds.cqf.cql.engine.elm.executing.EquivalentEvaluator
import org.opencds.cqf.cql.engine.exception.InvalidPrecision
import org.opencds.cqf.cql.engine.fhir.exception.UnknownType
import org.opencds.cqf.cql.engine.fhir.fhirModelId
import org.opencds.cqf.cql.engine.fhir.fhirModelNamespaceUri
import org.opencds.cqf.cql.engine.model.ModelResolver
import org.opencds.cqf.cql.engine.runtime.BaseTemporal
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.ClassInstance
import org.opencds.cqf.cql.engine.runtime.ClassLayout
import org.opencds.cqf.cql.engine.runtime.Date
import org.opencds.cqf.cql.engine.runtime.DateTime
import org.opencds.cqf.cql.engine.runtime.Precision
import org.opencds.cqf.cql.engine.runtime.TemporalHelper
import org.opencds.cqf.cql.engine.runtime.Time
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.anyTypeName
import org.opencds.cqf.cql.engine.runtime.toCqlBoolean
import org.opencds.cqf.cql.engine.runtime.toCqlDecimal
import org.opencds.cqf.cql.engine.runtime.toCqlInteger
import org.opencds.cqf.cql.engine.runtime.toCqlList
import org.opencds.cqf.cql.engine.runtime.toCqlLong
import org.opencds.cqf.cql.engine.runtime.toCqlString

// TODO: Probably quite a bit of redundancy here. Probably only really need the BaseType and the
// PrimitiveType
/*
 * type-to-class and contextPath resolutions are potentially expensive and can be cached
 * for improved performance.
 *
 * See <a href="https://github.com/DBCG/cql-evaluator/blob/master/evaluator.engine/src/main/java/org/opencds/cqf/cql/evaluator/engine/model/CachingModelResolverDecorator.java"/>
 * for a decorator that adds caching logic for ModelResolvers.
 */
@Suppress("TooManyFunctions")
abstract class FhirModelResolver<
    BaseType : IBase,
    BaseDateTimeType : IPrimitiveType<java.util.Date>,
    TimeType : IPrimitiveType<String>,
    SimpleQuantityType : ICompositeType,
    IdType : IIdType,
    ResourceType : IBaseResource,
    EnumerationType : IBaseEnumeration<*>,
    EnumFactoryType : IBaseEnumFactory<*>,
>( // getters & setters
    // Data members
    var fhirContext: FhirContext
) : ModelResolver {
    /** Memo for [resolveType]; see the note there. Concurrent because a resolver is shared. */
    private val resolvedTypes = ConcurrentHashMap<String, Optional<Class<*>>>()

    protected abstract fun initialize()

    abstract fun castToSimpleQuantity(base: BaseType): SimpleQuantityType

    protected abstract fun getCalendar(dateTime: BaseDateTimeType): Calendar

    protected abstract fun getCalendarConstant(dateTime: BaseDateTimeType): Int

    protected abstract fun setCalendarConstant(target: BaseDateTimeType, value: BaseTemporal)

    protected abstract fun idToString(id: IdType): String

    protected abstract fun timeToString(time: TimeType): String

    protected abstract fun getResourceType(resource: ResourceType): String

    protected abstract fun enumConstructor(factory: EnumFactoryType): EnumerationType

    protected abstract fun enumChecker(`object`: Any): kotlin.Boolean

    protected abstract fun enumFactoryTypeGetter(enumeration: EnumerationType): Class<*>

    init {
        this.initialize()
    }

    override fun resolveId(target: Value?): kotlin.String? {
        if (target is ClassInstance && target.type.namespaceURI == fhirModelNamespaceUri) {
            val clazz = this.resolveType(target.type.localPart) ?: return null
            if (IBaseResource::class.java.isAssignableFrom(clazz)) {
                val id = target.elements["id"] as? ClassInstance ?: return null
                return (id.elements["value"] as? org.opencds.cqf.cql.engine.runtime.String)?.value
            }
        }
        return null
    }

    override fun getContextPath(
        contextType: kotlin.String?,
        targetType: kotlin.String?,
    ): kotlin.String? {
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
        val theValue = this.createHapiInstance(contextType)

        // Because we created this instance from the local FhirContext, we know it is an IBase
        // The model resolver interface is not generic, so we have to cast here
        @Suppress("UNCHECKED_CAST") val type = theValue.javaClass as Class<out IBase>

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

            // Potential Kotlin/Java interop issue here?
            // element.getChildren() returns ListBaseRuntimeChildDefinition>
            @Suppress("UNCHECKED_CAST")
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

    override fun `is`(valueType: String, type: QName): kotlin.Boolean? {
        // System.Any is a supertype of all types
        if (type == anyTypeName) {
            return true
        }

        if (type.namespaceURI != fhirModelNamespaceUri) {
            // FHIR model types only extend System.Any or other FHIR model types
            return false
        }

        val valueTypeClass = resolveType(valueType) ?: return null
        val typeClass = resolveType(type.localPart) ?: return null

        return typeClass.isAssignableFrom(valueTypeClass)
    }

    override fun objectEquivalent(
        left: ClassInstance,
        right: ClassInstance,
        equivalent: (l: Value?, r: Value?) -> Boolean,
    ): Boolean {
        // "id" elements are excluded from equivalence checking for instances of FHIR.Resource and
        // FHIR.Element (see https://hl7.org/fhir/fhirpath.html#changes)
        if (
            `is`(left.type.localPart, QName(fhirModelNamespaceUri, "Resource")) == true ||
                `is`(left.type.localPart, QName(fhirModelNamespaceUri, "Element")) == true
        ) {
            return EquivalentEvaluator.classInstanceElementsEquivalent(
                left.elements.filterKeys { it != "id" },
                right.elements.filterKeys { it != "id" },
                equivalent,
            )
        }

        return EquivalentEvaluator.classInstanceElementsEquivalent(
            left.elements,
            right.elements,
            equivalent,
        )
    }

    override fun createInstance(typeName: String?): Value {
        return toCqlValue(createHapiInstance(typeName!!), true)!!
    }

    /** The package names of Java objects supported by this model */
    open var packageNames = mutableListOf<String>()

    /**
     * Resolve the Java class that corresponds to the given model type.
     *
     * @param typeName E.g. "Patient"
     * @return The Java class that corresponds to the given model type, e.g.
     *   `org.hl7.fhir.r4.model.Patient`.
     */
    /**
     * Resolution is deterministic for a given type name, and a miss is expensive: the lookups below
     * fall through to a `Class.forName` per configured package -- twice -- each throwing and
     * discarding a `ClassNotFoundException`, and then to a scan of every registered resource. The
     * set of type names a model produces is small and closed, so the answers are cached.
     *
     * Negative results are cached too. They cost the most to compute and recur just as often.
     *
     * Read-then-write rather than `computeIfAbsent`: the result does not depend on what else is in
     * the map, so a racing duplicate computation is harmless, and this cannot deadlock if a
     * resolution path ever re-enters this method.
     */
    open fun resolveType(typeName: String?): Class<*>? {
        if (typeName == null) {
            return resolveTypeUncached(null)
        }
        // A present entry is authoritative even when it holds null: a name that resolved to nothing
        // must not be recomputed, or negative results would never actually be cached.
        val cached = resolvedTypes[typeName]
        return if (cached != null) cached.orElse(null)
        else
            resolveTypeUncached(typeName).also { resolvedTypes[typeName] = Optional.ofNullable(it) }
    }

    private fun resolveTypeUncached(typeName: String?): Class<*>? {
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
                val path = typeName.split(".")
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
                return Class.forName("$packageName.Enumerations$$typeName")
            } catch (_: ClassNotFoundException) {}
        }

        // Other Types in package.
        for (packageName in this.packageNames) {
            try {
                return Class.forName("$packageName.$typeName")
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
                "Could not resolve type $typeName. Primary package(s) for this resolver are ${this.packageNames.joinToString(",")}"
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

                    @Suppress("UNCHECKED_CAST")
                    val nextChildren =
                        c.getChildByName(childrenName).getChildren()
                            as List<BaseRuntimeChildDefinition>
                    resolveChildren(nextChildren, childName)
                }
            }
        }
        return null
    }

    // Resolutions
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
                @Suppress("UNCHECKED_CAST")
                return this.fhirContext.getElementDefinition(base.javaClass)
                    as BaseRuntimeElementCompositeDefinition<ICompositeType?>
            }

            else ->
                throw UnknownType(
                    "Unable to resolve the runtime definition for ${base.javaClass.name}"
                )
        }
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

    /**
     * Creates an instance of the Java class that corresponds to the given model type.
     *
     * @param typeName E.g. "Patient"
     * @return An instance of the HAPI FHIR class like `org.hl7.fhir.r4.model.Patient`.
     */
    fun createHapiInstance(typeName: String): Any {
        val clazz =
            this.resolveType(typeName) ?: throw UnknownType("Could not resolve type $typeName")
        return createHapiInstance(clazz)
    }

    protected fun createHapiInstance(clazz: Class<*>): Any {
        try {
            if (clazz.isEnum) {
                val factoryClass = this.resolveType(clazz.getName() + "EnumFactory")

                @Suppress("UNCHECKED_CAST")
                val factory = this.createHapiInstance(factoryClass!!) as EnumFactoryType
                return this.enumConstructor(factory)
            }

            return clazz.getDeclaredConstructor().newInstance()
        } catch (e: NoSuchMethodException) {
            throw UnknownType(
                "Could not create an instance of class ${clazz.name}.\nRoot cause: ${e.message}"
            )
        } catch (e: InvocationTargetException) {
            throw UnknownType(
                "Could not create an instance of class ${clazz.name}.\nRoot cause: ${e.message}"
            )
        } catch (e: InstantiationException) {
            throw UnknownType(
                "Could not create an instance of class ${clazz.name}.\nRoot cause: ${e.message}"
            )
        } catch (e: IllegalAccessException) {
            throw UnknownType(
                "Could not create an instance of class ${clazz.name}.\nRoot cause: ${e.message}"
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

            else -> throw InvalidPrecision("Invalid temporal precision $calendarConstant")
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

            else -> throw InvalidPrecision("Invalid temporal precision $calendarConstant")
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

                @Suppress("UNCHECKED_CAST")
                setCalendarConstant(target as BaseDateTimeType, value as BaseTemporal)
            }
            "DateType" -> {
                val targetValue = (value as Date).toJavaDate()

                @Suppress("UNCHECKED_CAST")
                target as BaseDateTimeType
                target.value = targetValue
                setCalendarConstant(target, value as BaseTemporal)
            }

            "TimeType" -> target.asIPrimitive<String>().value = value.toString()
            "Base64BinaryType" -> target.valueAsString = value as String?
            else -> target.asIPrimitive<Any>().value = value
        }
    }

    private fun <T : Any> Any?.asIPrimitive(): IPrimitiveType<T?> {
        @Suppress("UNCHECKED_CAST")
        return this as IPrimitiveType<T?>
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
            else -> throw IllegalArgumentException("Unknown precision $precision")
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
    fun toSimpleCqlType(primitiveType: IPrimitiveType<*>): Value? {
        val value = primitiveType.value

        if (value == null) {
            return null
        }

        val simpleName = primitiveType.javaClass.getSimpleName()
        @Suppress("UNCHECKED_CAST")
        return when (simpleName) {
            "InstantType",
            "DateTimeType" -> toDateTime(primitiveType as BaseDateTimeType)
            "DateType" -> toDate(primitiveType as BaseDateTimeType)
            "TimeType" -> toTime(primitiveType as TimeType)
            "IdType" -> this.idToString(primitiveType as IdType).toCqlString()
            "Base64BinaryType" -> primitiveType.valueAsString?.toCqlString()
            else ->
                when (value) {
                    is kotlin.Boolean -> value.toCqlBoolean()
                    is kotlin.Int -> value.toCqlInteger()
                    is kotlin.Long -> value.toCqlLong()
                    is java.math.BigDecimal -> value.toCqlDecimal()
                    is kotlin.String -> value.toCqlString()
                    else ->
                        throw IllegalArgumentException(
                            "Unable to convert a value of type ${value.javaClass.name} to a CQL simple type."
                        )
                }
        }
    }

    /**
     * Recursively converts a HAPI FHIR construct to a CQL-native equivalent.
     *
     * @param target The HAPI FHIR object to convert
     * @param expandPrimitivesAndEnumerationsWithNoValues Whether to convert a HAPI FHIR
     *   primitive/enumeration with no value to a structured type instance with a null `value`
     *   child, or to null.
     */
    fun toCqlValue(
        target: Any?,
        expandPrimitivesAndEnumerationsWithNoValues: kotlin.Boolean = false,
    ): ClassInstance? {
        if (target == null) {
            return null
        }

        if (target !is IBase) {
            throw IllegalArgumentException(
                "Unable to convert an instance of ${target.javaClass.name} to a CQL value. Expected an instance of IBase."
            )
        }

        if (this.enumChecker(target)) {
            @Suppress("UNCHECKED_CAST")
            target as EnumerationType

            val slots = primitiveSlots(target)

            // If the instance is a primitive (including or even especially an enumeration), and it
            // has no value, return null
            if (
                !expandPrimitivesAndEnumerationsWithNoValues && !target.hasValue() && slots == null
            ) {
                return null
            }

            // For FHIR enumerations, use the type of the backing Enum
            val factoryName = this.enumFactoryTypeGetter(target).simpleName
            val typeName = factoryName.substringBefore("EnumFactory")

            return primitiveInstance(typeName, slots, target.valueAsString?.toCqlString())
        }

        if (target is IPrimitiveType<*>) {
            val slots = primitiveSlots(target)

            // If the instance is a primitive (including or even especially an enumeration), and it
            // has no value, return null
            if (
                !expandPrimitivesAndEnumerationsWithNoValues && !target.hasValue() && slots == null
            ) {
                return null
            }

            val elementDefinition = this.fhirContext.getElementDefinition(target.javaClass)

            return primitiveInstance(elementDefinition.name, slots, toSimpleCqlType(target))
        }

        // Composite definitions include id, extension and modifierExtension (where applicable) as
        // children, so they're converted here along with the rest of the children, each into its
        // slot of the definition's layout. Children without a value are left null.
        val definition = resolveRuntimeDefinition(target)
        val children = definition.children
        val slots = arrayOfNulls<Value>(children.size)
        for ((i, child) in children.withIndex()) {
            val childValues = child.accessor.getValues(target)
            slots[i] =
                if (child.max == 1) {
                    childValues.firstOrNull()?.let { toCqlValue(it) }
                } else {
                    if (childValues.isEmpty()) null
                    else childValues.map { toCqlValue(it) }.toCqlList()
                }
        }

        return ClassInstance(fhirQName(typeName(definition)), layout(definition), slots)
    }

    private val qNames = ConcurrentHashMap<kotlin.String, QName>()

    private fun fhirQName(typeName: kotlin.String): QName =
        qNames.computeIfAbsent(typeName) { QName(fhirModelNamespaceUri, it, fhirModelId) }

    private val layouts = ConcurrentHashMap<BaseRuntimeElementCompositeDefinition<*>, ClassLayout>()

    /** The layout of converted instances of a composite type: its children, in definition order. */
    private fun layout(definition: BaseRuntimeElementCompositeDefinition<*>): ClassLayout =
        layouts.computeIfAbsent(definition) {
            ClassLayout(it.children.map { child -> child.elementName })
        }

    /**
     * Whether backbone elements are named by the path that defines them (e.g. Patient.Contact), as
     * in the model info for FHIR R4 and R5. Otherwise they're named by their HAPI class (e.g.
     * ContactComponent). The DSTU2/DSTU3 model infos use other naming schemes.
     */
    protected open val namesBackboneElementsByPath: kotlin.Boolean
        get() = false

    /**
     * The model info name of a composite type. HAPI's simple class names for backbone elements
     * aren't unique across resources (e.g. Claim and ExplanationOfBenefit both have an
     * ItemComponent), so where the model info names them by path, so do we.
     */
    private fun typeName(definition: BaseRuntimeElementCompositeDefinition<*>): String =
        // Backbone elements are nested classes; resources and datatypes are not
        if (namesBackboneElementsByPath && definition.implementingClass.enclosingClass != null) {
            backboneElementTypeNames[definition.implementingClass] ?: definition.name
        } else {
            definition.name
        }

    private val backboneElementTypeNames: Map<Class<*>, String> by lazy {
        val names = HashMap<Class<*>, String>()

        fun collect(definition: BaseRuntimeElementCompositeDefinition<*>, typeName: String) {
            for (child in
                definition.children.filterIsInstance<RuntimeChildResourceBlockDefinition>()) {
                val childDefinition =
                    child.getChildByName(child.elementName)
                        as BaseRuntimeElementCompositeDefinition<*>
                val childClass = childDefinition.implementingClass
                // Backbone elements are nested classes of the type that defines them. One reused
                // elsewhere (e.g. Questionnaire.item.item reuses Questionnaire.Item) keeps the name
                // of the path that defines it.
                if (childClass.enclosingClass != null && !names.containsKey(childClass)) {
                    val name = "$typeName.${child.elementName.replaceFirstChar { it.uppercase() }}"
                    names[childClass] = name
                    collect(childDefinition, name)
                }
            }
        }

        for (resourceType in fhirContext.resourceTypes) {
            collect(fhirContext.getResourceDefinition(resourceType), resourceType)
        }
        for (elementDefinition in fhirContext.elementDefinitions) {
            if (elementDefinition is RuntimeCompositeDatatypeDefinition) {
                collect(elementDefinition, elementDefinition.name)
            }
        }

        names
    }

    /**
     * The slots of a converted FHIR primitive, in [PRIMITIVE_LAYOUT] order, with its id and
     * extensions filled in, or null if it has neither. Like all FHIR types, primitives may have an
     * id and extensions, with or without a value.
     */
    private fun primitiveSlots(target: IBase): Array<Value?>? {
        val extension =
            if (target is IBaseHasExtensions && target.extension.isNotEmpty()) {
                target.extension.map { toCqlValue(it) }.toCqlList()
            } else {
                null
            }
        val id = if (target is IBaseElement) target.id?.toCqlString() else null
        if (extension == null && id == null) {
            return null
        }
        return arrayOf(id, extension, null)
    }

    private fun primitiveInstance(
        typeName: kotlin.String,
        slots: Array<Value?>?,
        value: Value?,
    ): ClassInstance {
        val instanceSlots = slots ?: arrayOfNulls(PRIMITIVE_LAYOUT.size)
        instanceSlots[PRIMITIVE_VALUE_SLOT] = value
        return ClassInstance(fhirQName(typeName), PRIMITIVE_LAYOUT, instanceSlots)
    }
}

/** The elements a FHIR primitive can have. */
private val PRIMITIVE_LAYOUT = ClassLayout(listOf("id", "extension", "value"))

private const val PRIMITIVE_VALUE_SLOT = 2
