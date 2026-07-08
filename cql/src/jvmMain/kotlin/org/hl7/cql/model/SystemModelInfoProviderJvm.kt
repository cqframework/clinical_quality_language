package org.hl7.cql.model

actual fun getSystemModelInfoXml(): String {
    val stream =
        SystemModelInfoProvider::class
            .java
            .getResourceAsStream("/org/hl7/elm/r1/system-modelinfo.xml")
    checkNotNull(stream) { "Could not find system model info" }
    return stream.reader().readText()
}
