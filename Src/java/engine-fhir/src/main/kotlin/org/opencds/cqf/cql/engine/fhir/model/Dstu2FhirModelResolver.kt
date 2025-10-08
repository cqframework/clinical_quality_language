package org.opencds.cqf.cql.engine.fhir.model

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import java.util.*
import org.hl7.fhir.dstu2.model.Age
import org.hl7.fhir.dstu2.model.AnnotatedUuidType
import org.hl7.fhir.dstu2.model.Base
import org.hl7.fhir.dstu2.model.BaseDateTimeType
import org.hl7.fhir.dstu2.model.Count
import org.hl7.fhir.dstu2.model.Distance
import org.hl7.fhir.dstu2.model.Duration
import org.hl7.fhir.dstu2.model.EnumFactory
import org.hl7.fhir.dstu2.model.Enumeration
import org.hl7.fhir.dstu2.model.Enumerations
import org.hl7.fhir.dstu2.model.IdType
import org.hl7.fhir.dstu2.model.IntegerType
import org.hl7.fhir.dstu2.model.OidType
import org.hl7.fhir.dstu2.model.PositiveIntType
import org.hl7.fhir.dstu2.model.Quantity
import org.hl7.fhir.dstu2.model.Resource
import org.hl7.fhir.dstu2.model.SimpleQuantity
import org.hl7.fhir.dstu2.model.StringType
import org.hl7.fhir.dstu2.model.TimeType
import org.hl7.fhir.dstu2.model.UnsignedIntType
import org.hl7.fhir.dstu2.model.UriType
import org.hl7.fhir.dstu2.model.UuidType
import org.hl7.fhir.instance.model.api.IBaseResource
import org.opencds.cqf.cql.engine.exception.InvalidCast
import org.opencds.cqf.cql.engine.runtime.BaseTemporal

open class Dstu2FhirModelResolver(fhirContext: FhirContext) :
    FhirModelResolver<
        Base,
        BaseDateTimeType,
        TimeType,
        SimpleQuantity,
        IdType,
        Resource,
        Enumeration<*>,
        EnumFactory<*>,
    >(fhirContext) {
    constructor() : this(FhirContext.forDstu2())

    init {
        this.packageNames =
            (mutableListOf(
                "ca.uhn.fhir.model.dstu2",
                "org.hl7.fhir.dstu2.model",
                "ca.uhn.fhir.model.primitive",
            ))
        require(fhirContext.version.version == FhirVersionEnum.DSTU2) {
            "The supplied context is not configured for DSTU2"
        }
    }

    override fun initialize() {
        // HAPI has some bugs where it's missing annotations on certain types. This patches that.
        this.fhirContext.registerCustomType(AnnotatedUuidType::class.java)

        // The context loads Resources on demand which can cause resolution to fail in certain cases
        // This forces all Resource types to be loaded.

        // force calling of validateInitialized();
        this.fhirContext.getResourceDefinition(Enumerations.ResourceType.ACCOUNT.toCode())

        val myNameToResourceType: MutableMap<String?, Class<out IBaseResource?>>
        try {
            val f = this.fhirContext.javaClass.getDeclaredField("myNameToResourceType")
            f.setAccessible(true)

            // This is magic reflection to handle the fact that we need to manually
            // initialize this map, but it's a private field with no accessor.
            @Suppress("UNCHECKED_CAST")
            myNameToResourceType =
                f.get(this.fhirContext) as MutableMap<String?, Class<out IBaseResource?>>

            val toLoad: MutableList<Class<out IBaseResource?>> =
                ArrayList<Class<out IBaseResource?>>(myNameToResourceType.size)

            for (type in Enumerations.ResourceType.entries) {
                // These are abstract types that should never be resolved directly.
                when (type) {
                    Enumerations.ResourceType.DOMAINRESOURCE,
                    Enumerations.ResourceType.RESOURCE,
                    Enumerations.ResourceType.NULL -> continue
                    else -> {}
                }
                if (myNameToResourceType.containsKey(type.toCode().lowercase()))
                    toLoad.add(myNameToResourceType[type.toCode().lowercase()]!!)
            }

            // Sends a list of all classes to be loaded in bulk.
            val m =
                this.fhirContext.javaClass.getDeclaredMethod(
                    "scanResourceTypes",
                    MutableCollection::class.java,
                )
            m.setAccessible(true)
            m.invoke(this.fhirContext, toLoad)
        } catch (_: Exception) {
            // intentionally ignored
        }
    }

    override fun equalsDeep(left: Base, right: Base): Boolean {
        return left.equalsDeep(right)
    }

    override fun castToSimpleQuantity(base: Base): SimpleQuantity {
        return base.castToSimpleQuantity(base)
    }

    override fun getCalendar(dateTime: BaseDateTimeType): Calendar {
        return dateTime.toCalendar()
    }

    override fun getCalendarConstant(dateTime: BaseDateTimeType): Int {
        return dateTime.precision.calendarConstant
    }

    override fun setCalendarConstant(target: BaseDateTimeType, value: BaseTemporal) {
        target.setPrecision(toTemporalPrecisionEnum(value.precision!!))
    }

    override fun timeToString(time: TimeType): String {
        return time.value
    }

    override fun idToString(id: IdType): String {
        return id.idPart
    }

    override fun getResourceType(resource: Resource): String {
        return resource.fhirType()
    }

    override fun enumConstructor(factory: EnumFactory<*>): Enumeration<*> {
        return Enumeration(factory)
    }

    override fun enumChecker(`object`: Any): Boolean {
        return `object` is Enumeration<*>
    }

    override fun enumFactoryTypeGetter(enumeration: Enumeration<*>): Class<*> {
        val value: Enum<*>? = enumeration.getValue()
        if (value != null) {
            val enumSimpleName = value.javaClass.getSimpleName()
            return this.resolveType(enumSimpleName + "EnumFactory")!!
        } else {
            val myEnumFactoryField = enumeration.javaClass.getDeclaredField("myEnumFactory")
            myEnumFactoryField.setAccessible(true)
            val factory = myEnumFactoryField.get(enumeration) as EnumFactory<*>
            return factory.javaClass
        }
    }

    /*
    Casting of derived primitives:
    Datatypes that derive from datatypes other than Element are actually profiles
    // Types that exhibit this behavior are:
    // url: uri
    // canonical: uri
    // uuid: uri
    // oid: uri
    // positiveInt: integer
    // unsignedInt: integer
    // code: string
    // markdown: string
    // id: string

     */
    override fun `is`(value: Any?, type: Class<*>?): Boolean? {
        if (value == null) {
            return null
        }

        if (type!!.isAssignableFrom(value.javaClass)) {
            return true
        }

        // TODO: These should really be using profile validation
        // TODO: These should not return true unless the constraints that are used in the as logic
        // below return true
        if (value is UriType) {
            when (type.getSimpleName()) {
                "UrlType" -> return true
                "CanonicalType" -> return true
                "AnnotatedUuidType",
                "UuidType" -> return true
                "OidType" -> return true
            }
        }

        // TODO: These should really be using profile validation
        // TODO: These should not return true unless the constraints that are used in the as logic
        // below return true
        if (value is IntegerType) {
            when (type.getSimpleName()) {
                "PositiveIntType" -> return true
                "UnsignedIntType" -> return true
            }
        }

        // TODO: These should really be using profile validation
        // TODO: These should not return true unless the constraints that are used in the as logic
        // below return true
        if (value is StringType) {
            when (type.getSimpleName()) {
                "CodeType" -> return true
                "MarkdownType" -> return true
                "IdType" -> return true
            }
        }

        // TODO: These should really be using profile validation
        // TODO: These should not return true unless the constraints that are used in the as logic
        // below return true
        if (value is Quantity) {
            when (type.getSimpleName()) {
                "Age",
                "Distance",
                "Duration",
                "Count",
                "SimpleQuantity" -> return true
            }
        }

        return false
    }

    override fun `as`(value: Any?, type: Class<*>?, isStrict: Boolean): Any? {
        if (value == null) {
            return null
        }

        if (type!!.isAssignableFrom(value.javaClass)) {
            return value
        }

        if (value is UriType) {
            when (type.getSimpleName()) {
                "AnnotatedUuidType",
                "UuidType" ->
                    return if (value.hasValue() && value.value.startsWith("urn:uuid:"))
                        UuidType(value.primitiveValue())
                    else null

                "OidType" ->
                    return if (value.hasValue() && value.value.startsWith("urn:oid:"))
                        OidType(value.primitiveValue())
                    else null // castToOid(uriType); Throws an exception, not implemented
            }
        }

        if (value is IntegerType) {
            when (type.getSimpleName()) {
                "PositiveIntType" ->
                    return if (value.hasValue() && value.value > 0)
                        PositiveIntType(value.primitiveValue())
                    else
                        null // integerType.castToPositiveInt(integerType); Throws an exception, not
                // implemented
                "UnsignedIntType" ->
                    return if (value.hasValue() && value.value >= 0)
                        UnsignedIntType(value.primitiveValue())
                    else
                        null // castToUnsignedInt(integerType); Throws an exception, not implemented
            }
        }

        if (value is StringType) {
            when (type.getSimpleName()) {
                "CodeType" -> return value.castToCode(value)
                "MarkdownType" -> return value.castToMarkdown(value)
                "IdType" ->
                    return if (value.hasValue()) IdType(value.primitiveValue())
                    else null // stringType.castToId(stringType); Throws an exception, not
            // implemented
            }
        }

        if (value is Quantity) {
            when (type.getSimpleName()) {
                "Age" -> {
                    val age = Age()
                    age.setValue(value.getValue())
                    age.setCode(value.getCode())
                    age.setUnit(value.getUnit())
                    age.setSystem(value.getSystem())
                    age.setComparator(value.getComparator())
                    // TODO: Ensure age constraints are met, else return null (Except that we can't
                    // do this
                    // unless we can be assured that the same constraints have resulted in true when
                    // we called 'is'
                    // As it's written now, any Quantity will return true if you ask if it's an Age
                    // Same is true for all the subtypes, and so that's a type-system level issue
                    // waiting to bite
                    return age
                }

                "Distance" -> {
                    val distance = Distance()
                    distance.setValue(value.getValue())
                    distance.setCode(value.getCode())
                    distance.setUnit(value.getUnit())
                    distance.setSystem(value.getSystem())
                    distance.setComparator(value.getComparator())
                    // TODO: Ensure distance constraints are met, else return null
                    return distance
                }

                "Duration" -> {
                    val duration = Duration()
                    duration.setValue(value.getValue())
                    duration.setCode(value.getCode())
                    duration.setUnit(value.getUnit())
                    duration.setSystem(value.getSystem())
                    duration.setComparator(value.getComparator())
                    // TODO: Ensure duration constraints are met, else return null
                    return duration
                }

                "Count" -> {
                    val count = Count()
                    count.setValue(value.getValue())
                    count.setCode(value.getCode())
                    count.setUnit(value.getUnit())
                    count.setSystem(value.getSystem())
                    count.setComparator(value.getComparator())
                    // TODO: Ensure count constraints are met, else return null
                    return count
                }

                "SimpleQuantity" ->
                    return value.castToSimpleQuantity(
                        value
                    ) // NOTE: This is wrong in that it is copying the comparator, it should be
            }
        }

        if (isStrict) {
            throw InvalidCast(
                "Cannot cast a value of type ${value.javaClass.name} as ${type.name}."
            )
        }

        return null
    }

    override fun getContextPath(contextType: String?, targetType: String?): Any? {
        if (targetType == null || contextType == null) {
            return null
        }

        if ("Patient" == contextType) {
            when (targetType) {
                "QuestionnaireResponse" -> return "subject"
                else -> {}
            }
        }

        return super.getContextPath(contextType, targetType)
    }
}
