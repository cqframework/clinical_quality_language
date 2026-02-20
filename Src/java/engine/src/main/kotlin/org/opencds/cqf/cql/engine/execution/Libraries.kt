package org.opencds.cqf.cql.engine.execution

import org.hl7.cql.model.NamespaceManager.Companion.getNamePart
import org.hl7.cql.model.NamespaceManager.Companion.getUriPart
import org.hl7.elm.r1.*
import org.opencds.cqf.cql.engine.elm.executing.FunctionRefEvaluator.functionDefOperandsSignatureEqual
import org.opencds.cqf.cql.engine.exception.CqlException

/** This class provides static utility methods for resolving ELM elements from a ELM library. */
@Suppress("TooManyFunctions")
object Libraries {
    private fun libraryId(library: Library): String {
        return library.identifier?.id ?: "unknown"
    }

    fun <T> Collection<T>?.firstOrThrow(message: String, predicate: (T) -> Boolean): T =
        (this ?: emptyList()).firstOrNull(predicate) ?: throw CqlException(message)

    @JvmStatic
    fun resolveLibraryRef(libraryName: String?, relativeTo: Library): IncludeDef =
        relativeTo.includes?.def.firstOrThrow(
            "Could not resolve library reference '${libraryName}' in library '${libraryId(relativeTo)}'."
        ) {
            it.localIdentifier == libraryName
        }

    @JvmStatic
    @Suppress("ReturnCount")
    fun resolveAllExpressionRef(name: String?, relativeTo: Library): MutableList<ExpressionDef> {
        // Assumption: List of defs is sorted.
        val defs = relativeTo.statements?.def ?: mutableListOf()
        val index =
            relativeTo.statements
                ?.def
                ?.binarySearch(name, { x, k -> (x as ExpressionDef).name!!.compareTo(k as String) })
                ?: -1

        if (index < 0) {
            return mutableListOf()
        }

        var first = index
        var last = index + 1

        while (first > 0 && defs[first - 1].name.equals(name)) {
            first--
        }

        while (last < defs.size && defs[last].name.equals(name)) {
            last++
        }

        return defs.subList(first, last)
    }

    @JvmStatic
    fun resolveExpressionRef(name: String?, relativeTo: Library): ExpressionDef {
        // Assumption: List of defs is sorted.
        val defs = relativeTo.statements?.def
        if (defs != null) {
            val result =
                defs.binarySearch(
                    name,
                    { x, k -> (x as ExpressionDef).name!!.compareTo(k as String) },
                )
            if (result >= 0) {
                return defs[result]
            }
        }

        @Suppress("MaxLineLength")
        throw CqlException(
            "Could not resolve expression reference '${name}' in library '${libraryId(relativeTo)}'."
        )
    }

    @JvmStatic
    fun resolveCodeSystemRef(name: String?, relativeTo: Library): CodeSystemDef =
        relativeTo.codeSystems?.def.firstOrThrow(
            "Could not resolve code system reference '${name}' in library '${libraryId(relativeTo)}'."
        ) {
            it.name == name
        }

    @JvmStatic
    fun resolveValueSetRef(name: String?, relativeTo: Library): ValueSetDef =
        relativeTo.valueSets?.def.firstOrThrow(
            "Could not resolve value set reference '${name}' in library '${libraryId(relativeTo)}'."
        ) {
            it.name == name
        }

    @JvmStatic
    fun resolveCodeRef(name: String?, relativeTo: Library): CodeDef =
        relativeTo.codes?.def.firstOrThrow(
            "Could not resolve code reference '${name}' in library '${libraryId(relativeTo)}'."
        ) {
            it.name == name
        }

    @JvmStatic
    fun resolveParameterRef(name: String?, relativeTo: Library): ParameterDef =
        relativeTo.parameters?.def.firstOrThrow(
            "Could not resolve parameter reference '${name}' in library '${libraryId(relativeTo)}'."
        ) {
            it.name == name
        }

    @JvmStatic
    fun hasParameterDef(name: String?, relativeTo: Library): Boolean {
        val defs = relativeTo.parameters?.def ?: return false
        return defs.any { it.name == name }
    }

    @JvmStatic
    fun resolveConceptRef(name: String?, relativeTo: Library): ConceptDef =
        relativeTo.concepts?.def.firstOrThrow(
            "Could not resolve concept reference '${name}' in library '${libraryId(relativeTo)}'."
        ) {
            it.name == name
        }

    @JvmStatic
    fun getFunctionDefs(name: String?, relativeTo: Library): List<FunctionDef> {
        val defs = resolveAllExpressionRef(name, relativeTo)

        return defs.filterIsInstance<FunctionDef>().map { obj -> obj }
    }

    @JvmStatic
    @Suppress("ThrowsCount")
    fun resolveFunctionDef(
        name: String?,
        signature: List<TypeSpecifier>?,
        relativeTo: Library,
    ): FunctionDef {
        val functionDefs = getFunctionDefs(name, relativeTo)

        if (functionDefs.isEmpty()) {
            throw CqlException(
                "Library '${libraryId(relativeTo)}' does not have function '${name}'."
            )
        }

        if (signature == null) {
            if (functionDefs.size > 1) {
                throw CqlException(
                    @Suppress("MaxLineLength")
                    "Library '${libraryId(relativeTo)}' has multiple overloads for function '${name}'. A signature is required to disambiguate."
                )
            }

            return functionDefs.single()
        }

        val functionDefsWithMatchingSignature =
            functionDefs.filter { functionDefOperandsSignatureEqual(it, signature) }

        if (functionDefsWithMatchingSignature.isEmpty()) {
            throw CqlException(
                @Suppress("MaxLineLength")
                "Library '${libraryId(relativeTo)}' does not have a function '${name}' with the specified signature."
            )
        }

        return functionDefsWithMatchingSignature.single()
    }

    @JvmStatic
    fun toVersionedIdentifier(includeDef: IncludeDef): VersionedIdentifier {
        return VersionedIdentifier()
            .withSystem(getUriPart(includeDef.path))
            .withId(getNamePart(includeDef.path))
            .withVersion(includeDef.version)
    }
}
