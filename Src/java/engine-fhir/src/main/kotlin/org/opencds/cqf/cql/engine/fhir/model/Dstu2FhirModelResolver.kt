package org.opencds.cqf.cql.engine.fhir.model

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import java.util.*
import org.hl7.fhir.dstu2.model.AnnotatedUuidType
import org.hl7.fhir.dstu2.model.Base
import org.hl7.fhir.dstu2.model.BaseDateTimeType
import org.hl7.fhir.dstu2.model.EnumFactory
import org.hl7.fhir.dstu2.model.Enumeration
import org.hl7.fhir.dstu2.model.Enumerations
import org.hl7.fhir.dstu2.model.IdType
import org.hl7.fhir.dstu2.model.Resource
import org.hl7.fhir.dstu2.model.SimpleQuantity
import org.hl7.fhir.dstu2.model.TimeType
import org.hl7.fhir.instance.model.api.IBaseResource
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

    override fun getContextPath(contextType: String?, targetType: String?): String? {
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
