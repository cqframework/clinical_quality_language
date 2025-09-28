package org.cqframework.cql.cql2elm

import kotlinx.io.asSource
import kotlinx.io.buffered
import org.hl7.cql.model.ModelIdentifier
import org.hl7.cql.model.ModelInfoProvider
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.hl7.elm_modelinfo.r1.serializing.parseModelInfoXml

/** Created by Bryn on 12/11/2016. */
class TestFhirModelInfoProvider(private val clazz: Class<*>) : ModelInfoProvider {
    override fun load(modelIdentifier: ModelIdentifier): ModelInfo? {
        if (modelIdentifier.id == "FHIR") {
            val source = clazz.getResourceAsStream("fhir-modelinfo-1.8.xml")!!.asSource().buffered()
            return parseModelInfoXml(source)
        }

        return null
    }
}
