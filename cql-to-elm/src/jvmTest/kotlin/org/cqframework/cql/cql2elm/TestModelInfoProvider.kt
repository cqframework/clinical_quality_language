package org.cqframework.cql.cql2elm

import kotlinx.io.asSource
import kotlinx.io.buffered
import org.hl7.cql.model.ModelIdentifier
import org.hl7.cql.model.ModelInfoProvider
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.hl7.elm_modelinfo.r1.serializing.parseModelInfoXml

class TestModelInfoProvider : ModelInfoProvider {
    override fun load(modelIdentifier: ModelIdentifier): ModelInfo? {
        if (modelIdentifier.id == "Test") {
            val stream =
                TestModelInfoProvider::class
                    .java
                    .getResourceAsStream("ModelTests/test-modelinfo.xml")
            return parseModelInfoXml(stream!!.asSource().buffered())
        }

        return null
    }
}
