@file:OptIn(ExperimentalJsExport::class)

package org.cqframework.cql.cql2elm

import kotlin.js.ExperimentalJsExport
import org.cqframework.cql.cql2elm.model.CompiledLibrary
import org.cqframework.cql.cql2elm.model.ResolvedIdentifierContext
import org.hl7.cql.model.NamespaceManager
import org.hl7.elm.r1.CodeDef
import org.hl7.elm.r1.CodeSystemDef
import org.hl7.elm.r1.ConceptDef
import org.hl7.elm.r1.ContextDef
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.IncludeDef
import org.hl7.elm.r1.ParameterDef
import org.hl7.elm.r1.UsingDef
import org.hl7.elm.r1.ValueSetDef
import org.hl7.elm.r1.VersionedIdentifier

/**
 * Owns dual-update semantics for the ELM library and its in-memory symbol table: adding a
 * definition updates both the [org.hl7.elm.r1.Library] tree and the [CompiledLibrary]'s resolve-
 * indexes in one operation. Resolver methods read exclusively from the CompiledLibrary.
 *
 * Extracted from [LibraryBuilder] as part of the ongoing split of builder responsibilities. Holds a
 * back-reference to [LibraryBuilder] for cross-cutting concerns (conversion-map loading,
 * parse-error recording, library-manager access) that will move to focused collaborators once the
 * split completes.
 */
@Suppress("TooManyFunctions")
class SymbolTable(private val lb: LibraryBuilder) {
    fun addUsing(usingDef: UsingDef) {
        if (lb.library.usings == null) {
            lb.library.usings = lb.objectFactory.createLibraryUsings()
        }
        lb.library.usings!!.def.add(usingDef)
        lb.compiledLibrary.add(usingDef)
    }

    fun addInclude(includeDef: IncludeDef) {
        require(lb.library.identifier != null && lb.library.identifier!!.id != null) {
            "Unnamed libraries cannot reference other libraries."
        }
        if (lb.library.includes == null) {
            lb.library.includes = lb.objectFactory.createLibraryIncludes()
        }
        lb.library.includes!!.def.add(includeDef)
        lb.compiledLibrary.add(includeDef)
        val libraryIdentifier =
            VersionedIdentifier()
                .withSystem(NamespaceManager.getUriPart(includeDef.path))
                .withId(NamespaceManager.getNamePart(includeDef.path))
                .withVersion(includeDef.version)
        val errors = ArrayList<CqlCompilerException>()
        val referencedLibrary = lb.libraryManager.resolveLibrary(libraryIdentifier, errors)
        for (error in errors) {
            lb.recordParsingException(error)
        }

        // Translation of a referenced library may implicitly specify the namespace; if so the
        // resolved library's namespace URI will differ from the IncludeDef's current one, and
        // we update the path to match.
        val currentNamespaceUri = NamespaceManager.getUriPart(includeDef.path)
        @Suppress("ComplexCondition")
        if (
            currentNamespaceUri == null && libraryIdentifier.system != null ||
                currentNamespaceUri != null && currentNamespaceUri != libraryIdentifier.system
        ) {
            includeDef.path =
                NamespaceManager.getPath(libraryIdentifier.system, libraryIdentifier.id!!)
        }
        lb.libraries[includeDef.localIdentifier!!] = referencedLibrary
        lb.loadConversionMap(referencedLibrary)
    }

    fun addParameter(paramDef: ParameterDef) {
        if (lb.library.parameters == null) {
            lb.library.parameters = lb.objectFactory.createLibraryParameters()
        }
        lb.library.parameters!!.def.add(paramDef)
        lb.compiledLibrary.add(paramDef)
    }

    fun addCodeSystem(cs: CodeSystemDef) {
        if (lb.library.codeSystems == null) {
            lb.library.codeSystems = lb.objectFactory.createLibraryCodeSystems()
        }
        lb.library.codeSystems!!.def.add(cs)
        lb.compiledLibrary.add(cs)
    }

    fun addValueSet(vs: ValueSetDef) {
        if (lb.library.valueSets == null) {
            lb.library.valueSets = lb.objectFactory.createLibraryValueSets()
        }
        lb.library.valueSets!!.def.add(vs)
        lb.compiledLibrary.add(vs)
    }

    fun addCode(cd: CodeDef) {
        if (lb.library.codes == null) {
            lb.library.codes = lb.objectFactory.createLibraryCodes()
        }
        lb.library.codes!!.def.add(cd)
        lb.compiledLibrary.add(cd)
    }

    fun addConcept(cd: ConceptDef) {
        if (lb.library.concepts == null) {
            lb.library.concepts = lb.objectFactory.createLibraryConcepts()
        }
        lb.library.concepts!!.def.add(cd)
        lb.compiledLibrary.add(cd)
    }

    fun addContext(cd: ContextDef) {
        if (lb.library.contexts == null) {
            lb.library.contexts = lb.objectFactory.createLibraryContexts()
        }
        lb.library.contexts!!.def.add(cd)
    }

    fun addExpression(expDef: ExpressionDef) {
        if (lb.library.statements == null) {
            lb.library.statements = lb.objectFactory.createLibraryStatements()
        }
        lb.library.statements!!.def.add(expDef)
        lb.compiledLibrary.add(expDef)
    }

    fun removeExpression(expDef: ExpressionDef) {
        if (lb.library.statements != null) {
            lb.library.statements!!.def.remove(expDef)
            lb.compiledLibrary.remove(expDef)
        }
    }

    fun resolve(identifier: String): ResolvedIdentifierContext =
        lb.compiledLibrary.resolve(identifier)

    fun resolveIncludeRef(identifier: String): IncludeDef? =
        lb.compiledLibrary.resolveIncludeRef(identifier)

    fun resolveCodeSystemRef(identifier: String): CodeSystemDef? =
        lb.compiledLibrary.resolveCodeSystemRef(identifier)

    fun resolveValueSetRef(identifier: String): ValueSetDef? =
        lb.compiledLibrary.resolveValueSetRef(identifier)

    fun resolveCodeRef(identifier: String): CodeDef? = lb.compiledLibrary.resolveCodeRef(identifier)

    fun resolveConceptRef(identifier: String): ConceptDef? =
        lb.compiledLibrary.resolveConceptRef(identifier)
}
