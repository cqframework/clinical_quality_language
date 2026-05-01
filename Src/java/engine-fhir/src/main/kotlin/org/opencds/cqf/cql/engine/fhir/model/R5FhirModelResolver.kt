package org.opencds.cqf.cql.engine.fhir.model

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import java.util.*
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.instance.model.api.IBaseResource
import org.hl7.fhir.r5.model.Base
import org.hl7.fhir.r5.model.BaseDateTimeType
import org.hl7.fhir.r5.model.EnumFactory
import org.hl7.fhir.r5.model.Enumeration
import org.hl7.fhir.r5.model.Enumerations.FHIRTypes
import org.hl7.fhir.r5.model.IdType
import org.hl7.fhir.r5.model.Quantity
import org.hl7.fhir.r5.model.Resource
import org.hl7.fhir.r5.model.SimpleQuantity
import org.hl7.fhir.r5.model.TimeType
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

            // This is magic reflection to handle the fact that we need to manually
            // initialize this map, but it's a private field with no accessor.
            @Suppress("UNCHECKED_CAST")
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

    override fun getContextPath(contextType: String?, targetType: String?): String? {
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
