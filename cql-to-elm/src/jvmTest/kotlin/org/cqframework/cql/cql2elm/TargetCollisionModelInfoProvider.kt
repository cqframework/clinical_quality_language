package org.cqframework.cql.cql2elm

import kotlinx.io.asSource
import kotlinx.io.buffered
import org.hl7.cql.model.ModelIdentifier
import org.hl7.cql.model.ModelInfoProvider
import org.hl7.elm_modelinfo.r1.ModelInfo
import org.hl7.elm_modelinfo.r1.serializing.parseModelInfoXml

class TargetCollisionModelInfoProvider : ModelInfoProvider {
    override fun load(modelIdentifier: ModelIdentifier): ModelInfo? {
        if (modelIdentifier.id == "TargetCollision") {
            val stream =
                TargetCollisionModelInfoProvider::class
                    .java
                    .getResourceAsStream("TargetCollisionTests/target-collision-modelinfo.xml")
            return parseModelInfoXml(stream!!.asSource().buffered())
        }

        return null
    }
}
