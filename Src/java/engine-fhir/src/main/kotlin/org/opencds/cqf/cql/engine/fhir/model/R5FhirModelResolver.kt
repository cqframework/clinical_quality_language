package org.opencds.cqf.cql.engine.fhir.model

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import java.util.*
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r5.model.Age
import org.hl7.fhir.r5.model.Base
import org.hl7.fhir.r5.model.BaseDateTimeType
import org.hl7.fhir.r5.model.CanonicalType
import org.hl7.fhir.r5.model.CodeType
import org.hl7.fhir.r5.model.Count
import org.hl7.fhir.r5.model.Distance
import org.hl7.fhir.r5.model.Duration
import org.hl7.fhir.r5.model.EnumFactory
import org.hl7.fhir.r5.model.Enumeration
import org.hl7.fhir.r5.model.Enumerations.FHIRTypes
import org.hl7.fhir.r5.model.IdType
import org.hl7.fhir.r5.model.IntegerType
import org.hl7.fhir.r5.model.MarkdownType
import org.hl7.fhir.r5.model.MoneyQuantity
import org.hl7.fhir.r5.model.OidType
import org.hl7.fhir.r5.model.PositiveIntType
import org.hl7.fhir.r5.model.PrimitiveType
import org.hl7.fhir.r5.model.Quantity
import org.hl7.fhir.r5.model.Resource
import org.hl7.fhir.r5.model.SimpleQuantity
import org.hl7.fhir.r5.model.StringType
import org.hl7.fhir.r5.model.TimeType
import org.hl7.fhir.r5.model.UnsignedIntType
import org.hl7.fhir.r5.model.UriType
import org.hl7.fhir.r5.model.UrlType
import org.hl7.fhir.r5.model.UuidType
import org.opencds.cqf.cql.engine.exception.InvalidCast
import org.opencds.cqf.cql.engine.runtime.BaseTemporal

open class R5FhirModelResolver(fhirContext: FhirContext) :
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
    constructor() : this(FhirContext.forR5())

    init {
        this.packageNames = mutableListOf("org.hl7.fhir.r5.model")
        require(fhirContext.version.version == FhirVersionEnum.R5) {
            "The supplied context is not configured for R5"
        }
    }

    override fun initialize() {
        // The context loads Resources on demand which can cause resolution to fail in
        // certain cases
        // This forces all Resource types to be loaded.

        // force calling of validateInitialized();

        this.fhirContext.getResourceDefinition(FHIRTypes.ACCOUNT.toCode())

        val myNameToResourceType: MutableMap<String?, Class<out IBaseResource?>>
        try {
            val f = this.fhirContext.javaClass.getDeclaredField("myNameToResourceType")
            f.setAccessible(true)
            myNameToResourceType =
                f.get(this.fhirContext) as MutableMap<String?, Class<out IBaseResource?>>

            val toLoad = ArrayList<Class<out IBaseResource?>?>(myNameToResourceType.size)

            for (type in FHIRTypes.entries) {
                // These are abstract types that should never be resolved directly.
                when (type) {
                    FHIRTypes.DOMAINRESOURCE,
                    FHIRTypes.RESOURCE,
                    FHIRTypes.NULL -> continue
                    else -> {}
                }
                if (myNameToResourceType.containsKey(type.toCode().lowercase(Locale.getDefault())))
                    toLoad.add(myNameToResourceType[type.toCode().lowercase(Locale.getDefault())])
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

    override fun resolveProperty(target: Any?, path: String): Any? {
        // This is kind of a hack to get around contained resources - HAPI doesn't have
        // ResourceContainer type for R5
        if (target is Resource && target.fhirType() == path) {
            return target
        }

        // Account for extensions on primitives
        if (target is PrimitiveType<*> && path == "extension") {
            return target.getExtension()
        }

        return super.resolveProperty(target, path)
    }

    override fun castToSimpleQuantity(base: Base): SimpleQuantity {
        when (base) {
            is SimpleQuantity -> return base
            is Quantity -> {
                val sq = SimpleQuantity()
                sq.valueElement = base.valueElement
                sq.comparatorElement = base.comparatorElement
                sq.unitElement = base.unitElement
                sq.systemElement = base.systemElement
                sq.codeElement = base.codeElement
                return sq
            }

            else ->
                throw FHIRException(
                    "Unable to convert a " + base.javaClass.getName() + " to an SimpleQuantity"
                )
        }
    }

    override fun getCalendar(dateTime: BaseDateTimeType): Calendar {
        return dateTime.valueAsCalendar
    }

    override fun getCalendarConstant(dateTime: BaseDateTimeType): Int {
        return dateTime.precision.calendarConstant
    }

    override fun setCalendarConstant(target: BaseDateTimeType, value: BaseTemporal) {
        target.precision = toTemporalPrecisionEnum(value.precision!!)
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
        return enumeration.getEnumFactory().javaClass
    }

    override fun resolveType(typeName: String?): Class<*>? {
        // TODO: Might be able to patch some of these by registering custom types in
        // HAPI.

        var typeName = typeName
        when (typeName) {
            "ConfidentialityClassification" -> typeName = $$"Composition$DocumentConfidentiality"
            "ContractResourceStatusCodes" -> typeName = $$"Contract$ContractStatus"
            "EventStatus" -> typeName = $$"Procedure$ProcedureStatus"
            "FinancialResourceStatusCodes" -> typeName = $$"ClaimResponse$ClaimResponseStatus"
            "SampledDataDataType" -> typeName = "StringType"
            "ClaimProcessingCodes" -> typeName = $$"ClaimResponse$RemittanceOutcome"
            "vConfidentialityClassification" -> typeName = $$"Composition$DocumentConfidentiality"
            "ContractResourcePublicationStatusCodes" ->
                typeName = $$"Contract$ContractPublicationStatus"
            "CurrencyCode" -> typeName = "CodeType"
            "MedicationAdministrationStatus" -> typeName = "CodeType"
            "MedicationDispenseStatus" -> typeName = "CodeType"
            "MedicationKnowledgeStatus" -> typeName = "CodeType"
            "Messageheader_Response_Request" -> typeName = "CodeType"
            "MimeType" -> typeName = "CodeType"
            else -> {}
        }

        return super.resolveType(typeName)
    }

    /*
     * Casting of derived primitives: Datatypes that derive from datatypes other
     * than Element are
     * actually profiles // Types that exhibit this behavior are: // url: uri //
     * canonical: uri //
     * uuid: uri // oid: uri // positiveInt: integer // unsignedInt: integer //
     * code: string //
     * markdown: string // id: string
     *
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
                else -> {}
            }
        }

        // TODO: These should really be using profile validation
        // TODO: These should not return true unless the constraints that are used in the as logic
        // below return true
        if (value is IntegerType) {
            when (type.getSimpleName()) {
                "PositiveIntType" -> return true
                "UnsignedIntType" -> return true
                else -> {}
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
                else -> {}
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
                "SimpleQuantity",
                "MoneyQuantity" -> return true
                else -> {}
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
                "UrlType" ->
                    return if (value.hasPrimitiveValue()) UrlType(value.primitiveValue()) else null
                "CanonicalType" ->
                    return if (value.hasPrimitiveValue()) CanonicalType(value.primitiveValue())
                    else null
                "AnnotatedUuidType",
                "UuidType" ->
                    return if (value.hasPrimitiveValue() && value.value.startsWith("urn:uuid:"))
                        UuidType(value.primitiveValue())
                    else null

                "OidType" ->
                    return if (value.hasPrimitiveValue() && value.value.startsWith("urn:oid:"))
                        OidType(value.primitiveValue())
                    else null // castToOid(uriType); Throws an exception, not implemented
                else -> {}
            }
        }

        if (value is IntegerType) {
            when (type.getSimpleName()) {
                "PositiveIntType" ->
                    return if (value.hasPrimitiveValue() && value.value > 0)
                        PositiveIntType(value.primitiveValue())
                    else null // integerType.castToPositiveInt(integerType); Throws an
                "UnsignedIntType" ->
                    return if (value.hasPrimitiveValue() && value.value >= 0)
                        UnsignedIntType(value.primitiveValue())
                    else null // castToUnsignedInt(integerType); Throws an exception, not
                else -> {}
            }
        }

        if (value is StringType) {
            when (type.getSimpleName()) {
                "CodeType" ->
                    return if (value.hasPrimitiveValue()) CodeType(value.primitiveValue()) else null
                "MarkdownType" ->
                    return if (value.hasPrimitiveValue()) MarkdownType(value.primitiveValue())
                    else null
                "IdType" ->
                    return if (value.hasPrimitiveValue()) IdType(value.primitiveValue())
                    else null // stringType.castToId(stringType);
                else -> {}
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
                    return castToSimpleQuantity(value) // NOTE: This is wrong in that it is
                "MoneyQuantity" -> {
                    val moneyQuantity = MoneyQuantity()
                    moneyQuantity.setValue(value.getValue())
                    moneyQuantity.setCode(value.getCode())
                    moneyQuantity.setUnit(value.getUnit())
                    moneyQuantity.setSystem(value.getSystem())
                    moneyQuantity.setComparator(value.getComparator())
                    // TODO: Ensure money constraints are met, else return null
                    return moneyQuantity
                }

                else -> {}
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
                "MedicationStatement",
                "QuestionnaireResponse" -> return "subject"
                "Task" -> return "for"
                "Coverage" -> return "beneficiary"
                else -> {}
            }
        }

        return super.getContextPath(contextType, targetType)
    }
}
