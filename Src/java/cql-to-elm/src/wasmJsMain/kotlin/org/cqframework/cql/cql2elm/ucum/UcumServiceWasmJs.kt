@file:OptIn(ExperimentalJsExport::class)

package org.cqframework.cql.cql2elm.ucum

actual val defaultLazyUcumService =
    lazy<UcumService> { error("No default UCUM service available.") }

@JsExport
@JsName("createUcumService")
fun createUcumServiceReference(
    convertUnit: (value: String, sourceUnit: String, destUnit: String) -> String,
    validateUnit: (unit: String) -> String?,
): JsReference<Lazy<UcumService>> {
    return createUcumService(convertUnit, validateUnit).toJsReference()
}
