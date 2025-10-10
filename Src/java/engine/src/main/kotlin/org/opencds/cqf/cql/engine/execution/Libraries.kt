package org.opencds.cqf.cql.engine.execution

import org.hl7.cql.model.NamespaceManager.Companion.getNamePart
import org.hl7.cql.model.NamespaceManager.Companion.getUriPart
import org.hl7.elm.r1.*
import org.opencds.cqf.cql.engine.exception.CqlException

/** This class provides static utility methods for resolving ELM elements from a ELM library. */
object Libraries {
    @JvmStatic
    fun resolveLibraryRef(libraryName: String?, relativeTo: Library): IncludeDef {
        for (includeDef in relativeTo.includes!!.def) {
            if (includeDef.localIdentifier.equals(libraryName)) {
                return includeDef
            }
        }

        throw CqlException("Could not resolve library reference '${libraryName}'.")
    }

    @JvmStatic
    fun resolveAllExpressionRef(name: String?, relativeTo: Library): MutableList<ExpressionDef> {
        // Assumption: List of defs is sorted.
        val defs = relativeTo.statements!!.def
        val index =
            defs.binarySearch(name, { x, k -> (x as ExpressionDef).name!!.compareTo(k as String) })

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
        val result =
            relativeTo.statements!!
                .def
                .binarySearch(name, { x, k -> (x as ExpressionDef).name!!.compareTo(k as String) })
        if (result >= 0) {
            return relativeTo.statements!!.def[result]
        }

        throw CqlException(
            "Could not resolve expression reference '${name}' in library '${relativeTo.identifier!!.id}'."
        )
    }

    @JvmStatic
    fun resolveCodeSystemRef(name: String?, relativeTo: Library): CodeSystemDef {
        for (codeSystemDef in relativeTo.codeSystems!!.def) {
            if (codeSystemDef.name.equals(name)) {
                return codeSystemDef
            }
        }

        throw CqlException(
            "Could not resolve code system reference '${name}' in library '${relativeTo.identifier!!.id}'."
        )
    }

    @JvmStatic
    fun resolveValueSetRef(name: String?, relativeTo: Library): ValueSetDef {
        for (valueSetDef in relativeTo.valueSets!!.def) {
            if (valueSetDef.name.equals(name)) {
                return valueSetDef
            }
        }

        throw CqlException(
            "Could not resolve value set reference '${name}' in library '${relativeTo.identifier!!.id}'."
        )
    }

    @JvmStatic
    fun resolveCodeRef(name: String?, relativeTo: Library): CodeDef {
        for (codeDef in relativeTo.codes!!.def) {
            if (codeDef.name.equals(name)) {
                return codeDef
            }
        }

        throw CqlException(
            "Could not resolve code reference '${name}' in library '${relativeTo.identifier!!.id}'."
        )
    }

    @JvmStatic
    fun resolveParameterRef(name: String?, relativeTo: Library): ParameterDef {
        for (parameterDef in relativeTo.parameters!!.def) {
            if (parameterDef.name.equals(name)) {
                return parameterDef
            }
        }

        throw CqlException(
            "Could not resolve parameter reference '${name}' in library '${relativeTo.identifier!!.id}'."
        )
    }

    @JvmStatic
    fun resolveConceptRef(name: String?, relativeTo: Library): ConceptDef {
        for (conceptDef in relativeTo.concepts!!.def) {
            if (conceptDef.name.equals(name)) {
                return conceptDef
            }
        }

        throw CqlException("Could not resolve concept reference '${name}'.")
    }

    @JvmStatic
    fun getFunctionDefs(name: String?, relativeTo: Library): List<FunctionDef> {
        val defs = resolveAllExpressionRef(name, relativeTo)

        return defs.filter { obj -> obj is FunctionDef }.map { obj -> obj as FunctionDef }
    }

    @JvmStatic
    fun toVersionedIdentifier(includeDef: IncludeDef): VersionedIdentifier {
        return VersionedIdentifier()
            .withSystem(getUriPart(includeDef.path))
            .withId(getNamePart(includeDef.path))
            .withVersion(includeDef.version)
    }
}
