package org.cqframework.cql.cql2elm.model

import kotlinx.io.asSource
import kotlinx.io.buffered
import org.hl7.cql.model.ModelIdentifier
import org.hl7.cql.model.ModelInfoProvider
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.hl7.elm_modelinfo.r1.serializing.parseModelInfoXml

class GentestModelInfoProvider : ModelInfoProvider {
    override fun load(modelIdentifier: ModelIdentifier): ModelInfo? {
        if (modelIdentifier.id == "GENTEST") {
            val stream =
                GentestModelInfoProvider::class
                    .java
                    .getResourceAsStream(
                        "/org/cqframework/cql/cql2elm/ModelTests/test-modelinfowithgenerics-happy.xml"
                    )
            return parseModelInfoXml(stream!!.asSource().buffered())
        }

        return null
    }
}
