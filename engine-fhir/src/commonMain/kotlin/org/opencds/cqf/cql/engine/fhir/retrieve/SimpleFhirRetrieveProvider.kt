package org.opencds.cqf.cql.engine.fhir.retrieve

import org.cqframework.cql.shared.JsOnlyExport
import org.cqframework.cql.shared.QName
import org.opencds.cqf.cql.engine.elm.executing.InEvaluator
import org.opencds.cqf.cql.engine.fhir.fhirModelNamespaceUri
import org.opencds.cqf.cql.engine.fhir.ktStringOrNull
import org.opencds.cqf.cql.engine.retrieve.RetrieveProvider
import org.opencds.cqf.cql.engine.runtime.Boolean
import org.opencds.cqf.cql.engine.runtime.ClassInstance
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.Interval
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.runtime.String
import org.opencds.cqf.cql.engine.runtime.Value
import org.opencds.cqf.cql.engine.runtime.toCqlString
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo

@JsOnlyExport
class SimpleFhirRetrieveProvider(
    val bundle: ClassInstance,
    val terminologyProvider: TerminologyProvider,
) : RetrieveProvider {
    init {
        require(bundle.type == QName(fhirModelNamespaceUri, "Bundle"))
    }

    @Suppress("NON_EXPORTABLE_TYPE")
    override fun retrieve(
        context: kotlin.String?,
        contextPath: kotlin.String?,
        contextValue: kotlin.String?,
        dataType: kotlin.String,
        templateId: kotlin.String?,
        codePath: kotlin.String?,
        codes: Iterable<Code>?,
        valueSet: kotlin.String?,
        datePath: kotlin.String?,
        dateLowPath: kotlin.String?,
        dateHighPath: kotlin.String?,
        dateRange: Interval?,
    ): Iterable<Value?>? {
        val resources =
            (bundle.elements["entry"] as? List)?.asSequence()?.mapNotNull { entry ->
                (entry as? ClassInstance)?.elements?.get("resource") as? ClassInstance
            } ?: return null

        // Filter by resource type and context
        val resourceTypeQName = QName(fhirModelNamespaceUri, dataType)
        var results =
            resources
                .filter { resource -> resource.type == resourceTypeQName }
                .filter { resource -> contextMatches(resource, context, contextPath, contextValue) }

        // Filter by profile
        if (
            templateId != null && templateId != "http://hl7.org/fhir/StructureDefinition/$dataType"
        ) {
            results = results.filter { resource -> resourceHasProfile(resource, templateId) }
        }

        // Filter by date
        if (dateRange != null) {
            results =
                results.filter { resource ->
                    dateInRange(resource, dateRange, datePath, dateLowPath, dateHighPath)
                }
        }

        // Filter by code or value set
        if (codePath != null) {
            results =
                results.filter { resource ->
                    codeMatches(resource, codePath, codes, valueSet, terminologyProvider)
                }
        }

        return results.asIterable()
    }
}

private fun contextMatches(
    resource: ClassInstance,
    context: kotlin.String?,
    contextPath: kotlin.String?,
    contextValue: kotlin.String?,
): kotlin.Boolean {
    if (context == null || contextValue == null || contextPath == null) {
        return true
    }

    val contextPathResolved = resource.get(contextPath)

    if (
        contextPathResolved is ClassInstance &&
            contextPathResolved.type == QName(fhirModelNamespaceUri, "id")
    ) {
        return contextValue == contextPathResolved.elements["value"]?.ktStringOrNull
    }

    if (
        contextPathResolved is ClassInstance &&
            contextPathResolved.type == QName(fhirModelNamespaceUri, "Reference")
    ) {
        val reference = contextPathResolved.get("reference.value")?.ktStringOrNull
        val id = reference?.split("/")?.lastOrNull()

        return contextValue == id
    }

    return false
}

private fun resourceHasProfile(resource: ClassInstance, templateId: kotlin.String): kotlin.Boolean {
    val meta = resource.elements["meta"] as? ClassInstance ?: return false
    val profiles = meta.elements["profile"] as? List ?: return false
    return profiles.any { canonical ->
        (canonical as? ClassInstance)?.elements?.get("value") == templateId.toCqlString()
    }
}

private fun dateInRange(
    resource: ClassInstance,
    dateRange: Interval,
    datePath: kotlin.String?,
    dateLowPath: kotlin.String?,
    dateHighPath: kotlin.String?,
): kotlin.Boolean {
    val path = datePath ?: dateLowPath ?: dateHighPath

    requireNotNull(path) { "Date path is required" }

    return InEvaluator.`in`(dateRange, resource.get(path)?.get("value"), null, null) == Boolean.TRUE
}

private fun codeMatches(
    resource: ClassInstance,
    codePath: kotlin.String,
    codes: Iterable<Code>?,
    valueSet: kotlin.String?,
    terminologyProvider: TerminologyProvider,
): kotlin.Boolean {
    val actualCodes = getCodesFromResolvedCodePath(resource.get(codePath))

    return actualCodes.any { actualCode ->
        codeMatches(actualCode, codes, valueSet, terminologyProvider)
    }
}

private fun getCodesFromResolvedCodePath(value: Value?): kotlin.collections.List<Code> {
    return when (value) {
        is ClassInstance if value.type == QName(fhirModelNamespaceUri, "CodeableConcept") ->
            getCodesFromResolvedCodePath(value.elements["coding"])

        is ClassInstance if value.type == QName(fhirModelNamespaceUri, "Coding") ->
            listOf(codingToCode(value))

        is ClassInstance if value.type == QName(fhirModelNamespaceUri, "code") ->
            listOf(Code().withCode(value.elements["value"]?.ktStringOrNull))

        is List -> value.flatMap { getCodesFromResolvedCodePath(it) }

        else -> emptyList()
    }
}

private fun codingToCode(coding: ClassInstance): Code {
    val code = coding.get("code.value")?.ktStringOrNull
    val system = coding.get("system.value")?.ktStringOrNull
    val version = coding.get("version.value")?.ktStringOrNull
    val display = coding.get("display.value")?.ktStringOrNull
    return Code().withCode(code).withSystem(system).withVersion(version).withDisplay(display)
}

private fun codeMatches(
    actualCode: Code,
    codes: Iterable<Code>?,
    valueSet: kotlin.String?,
    terminologyProvider: TerminologyProvider,
): kotlin.Boolean {
    if (codes != null) {
        for (code in codes) {
            if (
                actualCode.code == code.code &&
                    (actualCode.system == null || actualCode.system == code.system)
            ) {
                return true
            }
        }
    }
    if (valueSet != null) {
        return terminologyProvider.`in`(actualCode, ValueSetInfo().withId(valueSet))
    }
    return false
}
