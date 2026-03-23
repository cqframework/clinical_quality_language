package org.cqframework.cql.cql2elm.codegen

import org.cqframework.cql.cql2elm.StringEscapeUtils.unescapeCql
import org.hl7.cql.ast.AccessModifier as AstAccessModifier
import org.hl7.cql.ast.CodeDefinition
import org.hl7.cql.ast.CodeSystemDefinition
import org.hl7.cql.ast.ConceptDefinition
import org.hl7.cql.ast.Definition
import org.hl7.cql.ast.IncludeDefinition
import org.hl7.cql.ast.ValueSetDefinition
import org.hl7.elm.r1.AccessModifier as ElmAccessModifier
import org.hl7.elm.r1.CodeDef
import org.hl7.elm.r1.CodeRef
import org.hl7.elm.r1.CodeSystemDef
import org.hl7.elm.r1.CodeSystemRef
import org.hl7.elm.r1.ConceptDef
import org.hl7.elm.r1.IncludeDef
import org.hl7.elm.r1.ValueSetDef

internal fun EmissionContext.emitIncludes(definitions: List<Definition>): List<IncludeDef> {
    return definitions.filterIsInstance<IncludeDefinition>().map { emitInclude(it) }
}

internal fun EmissionContext.emitInclude(definition: IncludeDefinition): IncludeDef {
    val includeDef = IncludeDef()
    val name = definition.libraryIdentifier.simpleName
    includeDef.localIdentifier = definition.alias?.value ?: name
    // TODO: For namespaced libraries, the legacy uses NamespaceManager.getPath(namespaceUri, name)
    // to produce a URI-based path. For now we use the simple name, which is correct for
    // non-namespaced libraries.
    includeDef.path = name
    includeDef.version = definition.version?.value
    return includeDef
}

internal fun EmissionContext.emitCodeSystemDefs(
    definitions: List<Definition>
): List<CodeSystemDef> {
    return definitions.filterIsInstance<CodeSystemDefinition>().map { emitCodeSystemDef(it) }
}

internal fun EmissionContext.emitCodeSystemDef(definition: CodeSystemDefinition): CodeSystemDef {
    val csDef = CodeSystemDef()
    csDef.name = unescapeCql(definition.name.value)
    csDef.id = definition.id
    csDef.version = definition.version?.value
    csDef.accessLevel = emitAccessModifier(definition.access)
    return csDef
}

internal fun EmissionContext.emitValueSetDefs(definitions: List<Definition>): List<ValueSetDef> {
    return definitions.filterIsInstance<ValueSetDefinition>().map { emitValueSetDef(it) }
}

internal fun EmissionContext.emitValueSetDef(definition: ValueSetDefinition): ValueSetDef {
    val vsDef = ValueSetDef()
    vsDef.name = unescapeCql(definition.name.value)
    vsDef.id = definition.id
    vsDef.version = definition.version?.value
    vsDef.accessLevel = emitAccessModifier(definition.access)
    for (csRef in definition.codesystems) {
        vsDef.codeSystem.add(
            CodeSystemRef()
                .withName(unescapeCql(csRef.identifier.value))
                .withLibraryName(csRef.libraryName?.value)
        )
    }
    return vsDef
}

internal fun EmissionContext.emitCodeDefs(definitions: List<Definition>): List<CodeDef> {
    return definitions.filterIsInstance<CodeDefinition>().map { emitCodeDef(it) }
}

internal fun EmissionContext.emitCodeDef(definition: CodeDefinition): CodeDef {
    val codeDef = CodeDef()
    codeDef.name = unescapeCql(definition.name.value)
    codeDef.id = definition.id
    codeDef.display = definition.display
    codeDef.accessLevel = emitAccessModifier(definition.access)
    codeDef.codeSystem =
        CodeSystemRef()
            .withName(unescapeCql(definition.system.identifier.value))
            .withLibraryName(definition.system.libraryName?.value)
    return codeDef
}

internal fun EmissionContext.emitConceptDefs(definitions: List<Definition>): List<ConceptDef> {
    return definitions.filterIsInstance<ConceptDefinition>().map { emitConceptDef(it) }
}

internal fun EmissionContext.emitConceptDef(definition: ConceptDefinition): ConceptDef {
    val conceptDef = ConceptDef()
    conceptDef.name = unescapeCql(definition.name.value)
    conceptDef.display = definition.display
    conceptDef.accessLevel = emitAccessModifier(definition.access)
    for (codeRef in definition.codes) {
        conceptDef.code.add(
            CodeRef()
                .withName(unescapeCql(codeRef.identifier.value))
                .withLibraryName(codeRef.libraryName?.value)
        )
    }
    return conceptDef
}

private fun emitAccessModifier(access: AstAccessModifier?): ElmAccessModifier {
    return when (access) {
        AstAccessModifier.PRIVATE -> ElmAccessModifier.PRIVATE
        else -> ElmAccessModifier.PUBLIC
    }
}
