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
@Suppress("TooManyFunctions", "LargeClass", "MaxLineLength", "ForbiddenComment")
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

    fun resolveUsingRef(modelName: String): UsingDef? =
        lb.compiledLibrary.resolveUsingRef(modelName)

    // ========================================================================
    // Namespace / well-known name queries. A "well-known" name resolves
    // without a namespace qualifier when a namespaceInfo is active.
    // ========================================================================

    fun isWellKnownModelName(unqualifiedIdentifier: String?): Boolean =
        if (lb.namespaceInfo == null) false
        else lb.libraryManager.modelManager.isWellKnownModelName(unqualifiedIdentifier)

    fun isWellKnownLibraryName(unqualifiedIdentifier: String?): Boolean =
        if (lb.namespaceInfo == null) false
        else lb.libraryManager.isWellKnownLibraryName(unqualifiedIdentifier)

    fun resolveNamespaceUri(namespaceName: String, mustResolve: Boolean): String? {
        val namespaceUri = lb.libraryManager.namespaceManager.resolveNamespaceUri(namespaceName)
        require(namespaceUri != null || !mustResolve) {
            "Could not resolve namespace name $namespaceName"
        }
        return namespaceUri
    }

    // ========================================================================
    // Model lifecycle. Loading a model caches it on LibraryBuilder.models,
    // registers its conversions on the conversion map, and synthesizes a
    // UsingDef so downstream `resolveUsingRef(modelName)` can find it.
    // ========================================================================

    private fun loadModel(
        modelIdentifier: org.hl7.cql.model.ModelIdentifier
    ): org.cqframework.cql.cql2elm.model.Model {
        val model = lb.libraryManager.modelManager.resolveModel(modelIdentifier)
        loadConversionMap(model)
        return model
    }

    fun getModel(
        modelIdentifier: org.hl7.cql.model.ModelIdentifier,
        localIdentifier: String,
    ): org.cqframework.cql.cql2elm.model.Model {
        var model = lb.modelsInternal[localIdentifier]
        if (model == null) {
            model = loadModel(modelIdentifier)
            lb.defaultModelInternal = model
            lb.modelsInternal[localIdentifier] = model
            buildUsingDef(modelIdentifier, model, localIdentifier)
        }
        require(
            modelIdentifier.version == null || modelIdentifier.version == model!!.modelInfo.version
        ) {
            "Could not load model information for model ${modelIdentifier.id}, version ${modelIdentifier.version} because version ${model!!.modelInfo.version} is already loaded."
        }
        return model!!
    }

    fun getModel(modelName: String): org.cqframework.cql.cql2elm.model.Model {
        val usingDef = resolveUsingRef(modelName)
        if (usingDef == null && modelName == "FHIR") {
            // Special case: FHIR-derived models that include FHIR Helpers can ask for FHIR
            // even when the active library doesn't explicitly `using FHIR`.
            return lb.libraryManager.modelManager.resolveModelByUri("http://hl7.org/fhir")
        }
        requireNotNull(usingDef) { "Could not resolve model name $modelName" }
        return getModel(usingDef)
    }

    fun getModel(usingDef: UsingDef): org.cqframework.cql.cql2elm.model.Model =
        getModel(
            org.hl7.cql.model.ModelIdentifier(
                id = org.hl7.cql.model.NamespaceManager.getNamePart(usingDef.uri)!!,
                system = org.hl7.cql.model.NamespaceManager.getUriPart(usingDef.uri),
                version = usingDef.version,
            ),
            usingDef.localIdentifier!!,
        )

    val systemModel: org.cqframework.cql.cql2elm.model.SystemModel
        // TODO: Support loading different versions of the system library
        get() =
            getModel(org.hl7.cql.model.ModelIdentifier("System"), "System")
                as org.cqframework.cql.cql2elm.model.SystemModel

    private fun buildUsingDef(
        modelIdentifier: org.hl7.cql.model.ModelIdentifier,
        model: org.cqframework.cql.cql2elm.model.Model?,
        localIdentifier: String,
    ): UsingDef {
        val usingDef =
            lb.objectFactory
                .createUsingDef()
                .withLocalIdentifier(localIdentifier)
                .withVersion(modelIdentifier.version)
                .withUri(model!!.modelInfo.url)
        addUsing(usingDef)
        return usingDef
    }

    fun hasUsings(): Boolean {
        for (model in lb.modelsInternal.values) {
            if (model!!.modelInfo.name != "System") return true
        }
        return false
    }

    // ========================================================================
    // Library / include / translation lifecycle.
    // ========================================================================

    fun loadConversionMap(library: CompiledLibrary) {
        for (conversion in library.getConversions()) {
            lb.conversionMap.add(conversion)
        }
    }

    private fun loadConversionMap(model: org.cqframework.cql.cql2elm.model.Model) {
        for (conversion in model.getConversions()) {
            lb.conversionMap.add(conversion)
        }
    }

    fun loadSystemLibrary() {
        val systemLibrary =
            org.cqframework.cql.cql2elm.model.SystemLibraryHelper.load(systemModel, lb.typeBuilder)
        lb.libraries[systemLibrary.identifier!!.id!!] = systemLibrary
        loadConversionMap(systemLibrary)
    }

    fun resolveLibrary(identifier: String?): CompiledLibrary {
        if (identifier != "System") lb.checkLiteralContext()
        return lb.libraries[identifier]
            ?: throw IllegalArgumentException("Could not resolve library name $identifier.")
    }

    fun canResolveLibrary(includeDef: IncludeDef): Boolean {
        val libraryIdentifier =
            VersionedIdentifier()
                .withSystem(org.hl7.cql.model.NamespaceManager.getUriPart(includeDef.path))
                .withId(org.hl7.cql.model.NamespaceManager.getNamePart(includeDef.path))
                .withVersion(includeDef.version)
        return lb.libraryManager.canResolveLibrary(libraryIdentifier)
    }

    fun beginTranslation() {
        loadSystemLibrary()
    }

    fun endTranslation() {
        applyTargetModelMaps()
    }

    private fun applyTargetModelMaps() {
        if (lb.library.usings != null) {
            for (usingDef in lb.library.usings!!.def) {
                val model = getModel(usingDef)
                if (model.modelInfo.targetUrl != null) {
                    usingDef.uri = model.modelInfo.targetUrl
                    usingDef.version = model.modelInfo.targetVersion
                }
            }
        }
    }

    @Suppress("NestedBlockDepth")
    fun getModelMapping(sourceContext: org.hl7.elm.r1.Expression?): VersionedIdentifier? {
        var result: VersionedIdentifier? = null
        if (lb.library.usings != null) {
            for (usingDef in lb.library.usings!!.def) {
                val model = getModel(usingDef)
                if (model.modelInfo.targetUrl != null) {
                    if (result != null) {
                        lb.reportWarning(
                            "Duplicate mapped model ${model.modelInfo.name}:${model.modelInfo.targetUrl}${
                                if (model.modelInfo.targetVersion != null) "|" + model.modelInfo.targetVersion
                                else ""
                            }",
                            sourceContext,
                        )
                    }
                    result =
                        lb.objectFactory
                            .createVersionedIdentifier()
                            .withId(model.modelInfo.name)
                            .withSystem(model.modelInfo.targetUrl)
                            .withVersion(model.modelInfo.targetVersion)
                }
            }
        }
        return result
    }

    fun ensureLibraryIncluded(libraryName: String, sourceContext: org.hl7.elm.r1.Expression?) {
        var includeDef = lb.compiledLibrary.resolveIncludeRef(libraryName)
        if (includeDef == null) {
            val modelMapping = getModelMapping(sourceContext)
            var path = libraryName
            if (lb.namespaceInfo != null && modelMapping != null && modelMapping.system != null) {
                path = org.hl7.cql.model.NamespaceManager.getPath(modelMapping.system, path)
            }
            includeDef =
                lb.objectFactory.createIncludeDef().withLocalIdentifier(libraryName).withPath(path)
            if (modelMapping != null) {
                includeDef.version = modelMapping.version
            }
            lb.compiledLibrary.add(includeDef)
        }
    }
}
