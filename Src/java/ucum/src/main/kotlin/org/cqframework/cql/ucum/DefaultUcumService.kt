package org.cqframework.cql.ucum

import org.cqframework.cql.cql2elm.ucum.UcumService
import org.fhir.ucum.Decimal
import org.fhir.ucum.UcumEssenceService

@ConsistentCopyVisibility
data class DefaultUcumService private constructor(private val ucumService: UcumService) :
    UcumService by ucumService {
    constructor() : this(createUcumService())

    companion object {
        private fun createUcumService(): UcumService {
            return try {
                UcumEssenceService(
                        UcumEssenceService::class.java.getResourceAsStream("/ucum-essence.xml")
                    )
                    .let { u ->
                        object : UcumService {
                            override fun convert(
                                value: java.math.BigDecimal,
                                sourceUnit: String,
                                destUnit: String,
                            ): java.math.BigDecimal {
                                val ucumValue = Decimal(value.toString())
                                val converted = u.convert(ucumValue, sourceUnit, destUnit)
                                return java.math.BigDecimal(converted.asDecimal())
                            }

                            override fun validate(unit: String): String? {
                                return u.validate(unit)
                            }
                        }
                    }
            } catch (e: org.fhir.ucum.UcumException) {
                throw IllegalStateException(
                    """Failed to create UCUM service. 
                    Please ensure the 'ucum-essence.xml' file is available on the classpath.
                    The 'ucum' module is a reference implementation that can be used for this purpose.""",
                    e,
                )
            }
        }
    }
}
