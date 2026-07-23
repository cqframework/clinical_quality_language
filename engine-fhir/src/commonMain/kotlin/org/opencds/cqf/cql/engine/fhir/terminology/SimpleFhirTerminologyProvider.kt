package org.opencds.cqf.cql.engine.fhir.terminology

import org.cqframework.cql.shared.JsOnlyExport
import org.cqframework.cql.shared.QName
import org.opencds.cqf.cql.engine.fhir.fhirModelNamespaceUri
import org.opencds.cqf.cql.engine.fhir.ktStringOrNull
import org.opencds.cqf.cql.engine.runtime.ClassInstance
import org.opencds.cqf.cql.engine.runtime.Code
import org.opencds.cqf.cql.engine.runtime.List
import org.opencds.cqf.cql.engine.terminology.CodeSystemInfo
import org.opencds.cqf.cql.engine.terminology.TerminologyProvider
import org.opencds.cqf.cql.engine.terminology.ValueSetInfo

@JsOnlyExport
class SimpleFhirTerminologyProvider(val bundle: ClassInstance) : TerminologyProvider {
    init {
        require(bundle.type == QName(fhirModelNamespaceUri, "Bundle"))
    }

    override fun `in`(code: Code, valueSet: ValueSetInfo): kotlin.Boolean {
        val codes = expand(valueSet)
        return codes.any {
            code.code == it.code && (code.system == null || code.system == it.system)
        }
    }

    @Suppress("NON_EXPORTABLE_TYPE")
    override fun expand(valueSet: ValueSetInfo): Iterable<Code> {
        val valueSetResource = findValueSetResource(bundle, valueSet) ?: return emptyList()

        return getCodesFromValueSetResource(valueSetResource)
    }

    override fun lookup(code: Code, codeSystem: CodeSystemInfo): Code? {
        if (code.system != null && code.system != codeSystem.id) {
            return null
        }

        val codeSystemResource = findCodeSystemResource(bundle, codeSystem) ?: return null

        val concepts = (codeSystemResource.elements["concept"] as? List) ?: return null

        val codes =
            flattenCodes(
                concepts,
                codeSystemResource.get("system.value")?.ktStringOrNull,
                codeSystemResource.get("version.value")?.ktStringOrNull,
            )

        return codes.find { code.code == it.code }
    }
}

private fun findValueSetResource(
    bundle: ClassInstance,
    valueSetInfo: ValueSetInfo,
): ClassInstance? {
    val entries = bundle.elements["entry"] as? List ?: return null

    val valueSetTypeQName = QName(fhirModelNamespaceUri, "ValueSet")

    return entries
        .asSequence()
        .mapNotNull { entry -> entry?.get("resource") as? ClassInstance }
        .find { resource ->
            resource.type == valueSetTypeQName &&
                valueSetInfo.id == resource.get("url.value")?.ktStringOrNull &&
                (valueSetInfo.version == null ||
                    valueSetInfo.version == resource.get("version.value")?.ktStringOrNull)
        }
}

private fun getCodesFromValueSetResource(valueSet: ClassInstance): kotlin.collections.List<Code> {
    val expansion = valueSet.get("expansion.contains") as? List
    if (expansion != null) {
        return flattenCodes(expansion)
    }

    val includes = valueSet.get("compose.include") as? List
    if (includes != null) {
        return includes.flatMap { include ->
            flattenCodes(
                include?.get("concept") as? List ?: List.EMPTY_LIST,
                include?.get("system.value")?.ktStringOrNull,
                include?.get("version.value")?.ktStringOrNull,
            )
        }
    }

    return emptyList()
}

private fun flattenCodes(
    list: List,
    system: kotlin.String? = null,
    version: kotlin.String? = null,
): kotlin.collections.List<Code> {
    return list.flatMap { item ->
        listOf(
            Code()
                .withSystem(item?.get("system.value")?.ktStringOrNull ?: system)
                .withVersion(item?.get("version.value")?.ktStringOrNull ?: version)
                .withCode(item?.get("code.value")?.ktStringOrNull)
                .withDisplay(item?.get("display.value")?.ktStringOrNull)
        ) + flattenCodes(item?.get("contains") as? List ?: List.EMPTY_LIST, system, version)
    }
}

private fun findCodeSystemResource(
    bundle: ClassInstance,
    codeSystemInfo: CodeSystemInfo,
): ClassInstance? {
    val entries = bundle.elements["entry"] as? List ?: return null

    val codeSystemTypeQName = QName(fhirModelNamespaceUri, "CodeSystem")

    return entries
        .asSequence()
        .mapNotNull { entry -> entry?.get("resource") as? ClassInstance }
        .find { resource ->
            resource.type == codeSystemTypeQName &&
                codeSystemInfo.id == resource.get("id.value")?.ktStringOrNull &&
                (codeSystemInfo.version == null ||
                    codeSystemInfo.version == resource.get("version.value")?.ktStringOrNull)
        }
}
