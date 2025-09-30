package org.cqframework.cql.elm.requirements

import java.time.ZonedDateTime
import java.util.*
import javax.xml.namespace.QName
import org.cqframework.cql.cql2elm.CqlCompilerOptions
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.TypeBuilder
import org.cqframework.cql.cql2elm.model.CompiledLibrary
import org.cqframework.cql.cql2elm.model.LibraryRef
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.cqframework.cql.elm.IdObjectFactory
import org.hl7.cql.model.ClassType
import org.hl7.cql.model.DataType
import org.hl7.cql.model.NamespaceManager.Companion.getNamePart
import org.hl7.cql.model.NamespaceManager.Companion.getUriPart
import org.hl7.elm.r1.AliasRef
import org.hl7.elm.r1.Code
import org.hl7.elm.r1.CodeDef
import org.hl7.elm.r1.CodeRef
import org.hl7.elm.r1.CodeSystemDef
import org.hl7.elm.r1.CodeSystemRef
import org.hl7.elm.r1.Concept
import org.hl7.elm.r1.ConceptDef
import org.hl7.elm.r1.ConceptRef
import org.hl7.elm.r1.ContextDef
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.ExpressionRef
import org.hl7.elm.r1.FunctionDef
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.IncludeDef
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.NamedTypeSpecifier
import org.hl7.elm.r1.Null
import org.hl7.elm.r1.ParameterDef
import org.hl7.elm.r1.ParameterRef
import org.hl7.elm.r1.Property
import org.hl7.elm.r1.Query
import org.hl7.elm.r1.QueryLetRef
import org.hl7.elm.r1.Retrieve
import org.hl7.elm.r1.UsingDef
import org.hl7.elm.r1.ValueSetDef
import org.hl7.elm.r1.ValueSetRef
import org.hl7.elm.r1.VersionedIdentifier

class ElmRequirementsContext(
    libraryManager: LibraryManager,
    options: CqlCompilerOptions,
    visitor: ElmRequirementsVisitor,
    parameters: MutableMap<String?, Any?>?,
    evaluationDateTime: ZonedDateTime?,
) {
    var options: CqlCompilerOptions

    val libraryManager: LibraryManager

    val parameters: MutableMap<String?, Any?>?

    val evaluationDateTime: ZonedDateTime?

    val typeResolver: TypeResolver

    private val typeBuilder: TypeBuilder?

    // Arbitrary starting point for generated local Ids.
    // If the input ELM does not have local Ids, some of the optimization
    // outputs require references to be established between ELM nodes,
    // so local ids are generated if not present in those cases.
    private var nextLocalId = 10000

    fun generateLocalId(): String {
        nextLocalId++
        return String.format("G%d", nextLocalId)
    }

    private val expressionDefStack = Stack<ElmExpressionDefContext>()

    fun enterExpressionDef(expressionDef: ExpressionDef) {
        val expressionDefContext =
            ElmExpressionDefContext(this.currentLibraryIdentifier, expressionDef)
        expressionDefStack.push(expressionDefContext)
    }

    fun exitExpressionDef(inferredRequirements: ElmRequirement?) {
        require(!expressionDefStack.empty()) { "Not in an expressionDef context" }
        val expressionDefContext = expressionDefStack.pop()
        val ed = expressionDefContext.expressionDef
        reportExpressionDef(ed)
        this.reportedRequirements[ed] = expressionDefContext.reportedRequirements
        this.inferredRequirements[ed] = inferredRequirements
    }

    val currentExpressionDefContext: ElmExpressionDefContext?
        get() {
            require(!expressionDefStack.empty()) { "Expression definition is not in progress" }
            return expressionDefStack.peek()
        }

    fun inExpressionDefContext(): Boolean {
        return !expressionDefStack.empty()
    }

    private val pertinenceContextStack: MutableList<ElmPertinenceContext?> =
        ArrayList<ElmPertinenceContext?>()

    fun enterPertinenceContext(expressionDef: ExpressionDef): Boolean {
        val pertinenceContext = ElmPertinenceContext(expressionDef)
        if (pertinenceContext.checkPertinenceTag()) {
            pertinenceContextStack.add(0, pertinenceContext)
            return true
        }
        return false
    }

    fun peekPertinenceContext(): ElmPertinenceContext? {
        var context: ElmPertinenceContext? = null
        for (c in pertinenceContextStack) {
            context = c
        }
        return context
    }

    fun exitPertinenceContext() {
        if (pertinenceContextStack.size > 0) {
            pertinenceContextStack.removeAt(0)
        }
    }

    /*
    Reported requirements are collected during the traversal, reported at query boundaries, or at retrieves
    that are outside of a query scope.
    These are collected by the ElmExpressionDefContext as expression defs are visited, and reported to the context after
    the visit is complete
     */
    private val reportedRequirements: MutableMap<ExpressionDef?, ElmRequirements?> =
        LinkedHashMap<ExpressionDef?, ElmRequirements?>()

    fun getReportedRequirements(): Iterable<ElmRequirements?> {
        return reportedRequirements.values
    }

    fun getReportedRequirements(ed: ExpressionDef?): ElmRequirements? {
        return reportedRequirements[ed]
    }

    /*
    Inferred requirements are the result of the traversal, the computed/inferred data requirements for an expression.
    These are calculated by the visit and reported to the context here after the visit is complete
     */
    private val inferredRequirements: MutableMap<ExpressionDef?, ElmRequirement?> =
        LinkedHashMap<ExpressionDef?, ElmRequirement?>()

    fun getInferredRequirements(): Iterable<ElmRequirement?> {
        return inferredRequirements.values
    }

    fun getInferredRequirements(ed: ExpressionDef?): ElmRequirement? {
        return inferredRequirements[ed]
    }

    private val libraryStack = Stack<VersionedIdentifier?>()

    fun enterLibrary(libraryIdentifier: VersionedIdentifier) {
        libraryStack.push(libraryIdentifier)
    }

    fun exitLibrary() {
        libraryStack.pop()
    }

    val currentLibraryIdentifier: VersionedIdentifier
        get() {
            require(!libraryStack.empty()) { "Not in a library context" }

            return libraryStack.peek()!!
        }

    /*
    Prepares a library visit if necessary (i.e. localLibraryName is not null) and returns the associated translated
    library. If there is no localLibraryName, returns the current library.
     */
    private fun prepareLibraryVisit(
        libraryIdentifier: VersionedIdentifier,
        localLibraryName: String?,
    ): CompiledLibrary {
        var targetLibrary = resolveLibrary(libraryIdentifier)
        if (localLibraryName != null) {
            val includeDef = targetLibrary.resolveIncludeRef(localLibraryName)
            if (!visited.contains(includeDef)) {
                visitor.visitElement(includeDef!!, this)
            }
            targetLibrary = resolveLibraryFromIncludeDef(includeDef!!)
            enterLibrary(targetLibrary.identifier!!)
        }
        return targetLibrary
    }

    private fun unprepareLibraryVisit(localLibraryName: String?) {
        if (localLibraryName != null) {
            exitLibrary()
        }
    }

    fun enterQueryContext(query: Query) {
        this.currentExpressionDefContext!!.enterQueryContext(query)
    }

    fun exitQueryContext(): ElmQueryContext {
        return this.currentExpressionDefContext!!.exitQueryContext()
    }

    val currentQueryContext: ElmQueryContext
        get() = this.currentExpressionDefContext!!.currentQueryContext

    fun inQueryContext(): Boolean {
        return this.currentExpressionDefContext!!.inQueryContext()
    }

    fun resolveAlias(aliasName: String?): ElmQueryAliasContext {
        return this.currentExpressionDefContext!!.resolveAlias(aliasName)
    }

    fun resolveLet(letName: String?): ElmQueryLetContext {
        return this.currentExpressionDefContext!!.resolveLet(letName)
    }

    private val visited: MutableSet<Element?> = LinkedHashSet<Element?>()

    val requirements: ElmRequirements

    val visitor: ElmRequirementsVisitor

    private fun isDefinition(elm: Element?): Boolean {
        return elm is Library ||
            elm is UsingDef ||
            elm is IncludeDef ||
            elm is CodeSystemDef ||
            elm is ValueSetDef ||
            elm is CodeDef ||
            elm is ConceptDef ||
            elm is ParameterDef ||
            elm is ContextDef ||
            elm is ExpressionDef
    }

    private fun reportRequirement(requirement: ElmRequirement) {
        if (isDefinition(requirement.element)) {
            visited.add(requirement.element)
            requirements.reportRequirement(requirement)
        } else {
            if (expressionDefStack.empty()) {
                requirements.reportRequirement(requirement)
            } else {
                expressionDefStack.peek().reportRequirement(requirement)
            }
        }
    }

    private fun reportRequirement(element: Element) {
        reportRequirement(ElmRequirement(this.currentLibraryIdentifier, element))
    }

    fun reportUsingDef(usingDef: UsingDef) {
        reportRequirement(usingDef)
    }

    fun reportIncludeDef(includeDef: IncludeDef) {
        reportRequirement(includeDef)
    }

    fun reportContextDef(contextDef: ContextDef) {
        reportRequirement(contextDef)
    }

    fun reportCodeDef(codeDef: CodeDef) {
        reportRequirement(codeDef)
    }

    fun reportCodeSystemDef(codeSystemDef: CodeSystemDef) {
        reportRequirement(codeSystemDef)
    }

    fun reportConceptDef(conceptDef: ConceptDef) {
        reportRequirement(conceptDef)
    }

    fun reportParameterDef(parameterDef: ParameterDef) {
        reportRequirement(parameterDef)
    }

    fun reportValueSetDef(valueSetDef: ValueSetDef) {
        reportRequirement(valueSetDef)
    }

    fun reportExpressionDef(expressionDef: ExpressionDef) {
        if (expressionDef !is FunctionDef) {
            reportRequirement(expressionDef)
        }
    }

    fun reportFunctionDef(functionDef: FunctionDef) {
        reportRequirement(functionDef)
    }

    fun reportCodeRef(codeRef: CodeRef) {
        val targetLibrary = prepareLibraryVisit(this.currentLibraryIdentifier, codeRef.libraryName)
        try {
            val cd = targetLibrary.resolveCodeRef(codeRef.name!!)
            if (!visited.contains(cd)) {
                visitor.visitElement(cd!!, this)
            }
        } finally {
            unprepareLibraryVisit(codeRef.libraryName)
        }
    }

    fun reportCodeSystemRef(codeSystemRef: CodeSystemRef) {
        val targetLibrary =
            prepareLibraryVisit(this.currentLibraryIdentifier, codeSystemRef.libraryName)
        try {
            val csd = targetLibrary.resolveCodeSystemRef(codeSystemRef.name!!)
            if (!visited.contains(csd)) {
                visitor.visitElement(csd!!, this)
            }
        } finally {
            unprepareLibraryVisit(codeSystemRef.libraryName)
        }
    }

    fun reportConceptRef(conceptRef: ConceptRef) {
        val targetLibrary =
            prepareLibraryVisit(this.currentLibraryIdentifier, conceptRef.libraryName)
        try {
            val cd = targetLibrary.resolveConceptRef(conceptRef.name!!)
            if (!visited.contains(cd)) {
                visitor.visitElement(cd!!, this)
            }
        } finally {
            unprepareLibraryVisit(conceptRef.libraryName)
        }
    }

    fun reportParameterRef(parameterRef: ParameterRef) {
        val targetLibrary =
            prepareLibraryVisit(this.currentLibraryIdentifier, parameterRef.libraryName)
        try {
            val pd = targetLibrary.resolveParameterRef(parameterRef.name!!)
            if (!visited.contains(pd)) {
                visitor.visitElement(pd!!, this)
            }
        } finally {
            unprepareLibraryVisit(parameterRef.libraryName)
        }
    }

    fun reportValueSetRef(valueSetRef: ValueSetRef) {
        val targetLibrary =
            prepareLibraryVisit(this.currentLibraryIdentifier, valueSetRef.libraryName)
        try {
            val vsd = targetLibrary.resolveValueSetRef(valueSetRef.name!!)
            if (!visited.contains(vsd)) {
                visitor.visitElement(vsd!!, this)
            }
        } finally {
            unprepareLibraryVisit(valueSetRef.libraryName)
        }
    }

    fun reportExpressionRef(expressionRef: ExpressionRef): ElmRequirement? {
        val targetLibrary =
            prepareLibraryVisit(this.currentLibraryIdentifier, expressionRef.libraryName)
        try {
            val ed = targetLibrary.resolveExpressionRef(expressionRef.name!!)
            if (!visited.contains(ed)) {
                visitor.visitElement(ed!!, this)
            }
            val inferredRequirements = getInferredRequirements(ed)

            // Report data requirements for this expression def to the current context (that are not
            // already part of the
            // inferred requirements
            val reportedRequirements = getReportedRequirements(ed)
            if (reportedRequirements != null) {
                reportRequirements(reportedRequirements, inferredRequirements)
            }
            // Return the inferred requirements for the expression def
            return inferredRequirements
        } finally {
            unprepareLibraryVisit(expressionRef.libraryName)
        }
    }

    fun reportFunctionRef(functionRef: FunctionRef) {
        val targetLibrary =
            prepareLibraryVisit(this.currentLibraryIdentifier, functionRef.libraryName)
        try {
            var signature: MutableList<DataType>?
            signature = ArrayList<DataType>()
            for (ts in functionRef.signature) {
                signature.add(typeResolver.resolveTypeSpecifier(ts))
            }
            // Signature sizes will only be different in the case that the signature is not present
            // in the ELM, so needs
            // to be constructed
            if (signature.size != functionRef.operand.size) {
                for (e in functionRef.operand) {
                    val resultType = e.resultType
                    if (resultType != null) {
                        signature!!.add(resultType)
                    } else if (e.resultTypeName != null) {
                        signature!!.add(typeResolver.resolveTypeName(e.resultTypeName!!))
                    } else if (e.resultTypeSpecifier != null) {
                        signature!!.add(typeResolver.resolveTypeSpecifier(e.resultTypeSpecifier!!))
                    } else {
                        // Signature could not be constructed, fall back to reporting all function
                        // defs
                        signature = null
                        break
                    }
                }
            }

            val fds: Iterable<FunctionDef?> =
                targetLibrary.resolveFunctionRef(functionRef.name!!, signature)
            for (fd in fds) {
                if (!visited.contains(fd)) {
                    visitor.visitElement(fd!!, this)
                }
            }
        } finally {
            unprepareLibraryVisit(functionRef.libraryName)
        }
    }

    fun reportRetrieve(retrieve: Retrieve) {
        // Report the retrieve as an overall data requirement
        reportRequirement(retrieve)
        // Data Requirements analysis is done within the query processing
        /*
        ElmDataRequirement retrieveRequirement = new ElmDataRequirement(getCurrentLibraryIdentifier(), retrieve);
        if (!queryStack.empty()) {
            getCurrentQueryContext().reportRetrieve(retrieveRequirement);
        }
        else {
            reportRequirement(retrieveRequirement);
        }
        */
    }

    /*
    Report the requirements inferred from visit of an expression tree, typically an ExpressionDef
    Except do not report a requirement if it is present in the inferred requirements for the expression,
    or if it can be correlated with a data requirement in the current query context
    (The alternative is to calculate total requirements as part of the inference mechanism, but that
    complicates the inferencing calculations, as they would always have to be based on a collection
    of requirements, rather than the current focus of either a DataRequirement or a QueryRequirement)
     */
    fun reportRequirements(requirement: ElmRequirement, inferredRequirements: ElmRequirement?) {
        if (requirement is ElmRequirements) {
            for (childRequirement in requirement.getRequirements()) {
                if (
                    inferredRequirements == null ||
                        !inferredRequirements.hasRequirement(childRequirement)
                ) {
                    reportRequirement(childRequirement)
                }
            }
        } else if (requirement is ElmQueryRequirement) {
            for (dataRequirement in requirement.getDataRequirements()) {
                if (
                    inferredRequirements == null ||
                        !inferredRequirements.hasRequirement(dataRequirement)
                ) {
                    reportRequirement(dataRequirement)
                }
            }
        } else if (requirement is ElmOperatorRequirement) {
            for (r in requirement.getRequirements()) {
                if (inferredRequirements == null || !inferredRequirements.hasRequirement(r)) {
                    reportRequirements(r!!, inferredRequirements)
                }
            }
        } else {
            reportRequirement(requirement)
        }
    }

    private fun getType(expression: Expression?): QName? {
        if (expression != null) {
            if (expression.resultTypeName != null) {
                return expression.resultTypeName
            } else if (expression.resultTypeSpecifier is NamedTypeSpecifier) {
                return (expression.resultTypeSpecifier as NamedTypeSpecifier).name
            }
        }

        return null
    }

    private fun getProfiledType(type: DataType?): QName? {
        return typeResolver.dataTypeToProfileQName(type)
    }

    private val unboundDataRequirements: MutableMap<QName?, ElmDataRequirement?> =
        LinkedHashMap<QName?, ElmDataRequirement?>()

    init {
        this.libraryManager = libraryManager
        this.options = options
        this.typeResolver = TypeResolver(libraryManager)
        this.typeBuilder = TypeBuilder(IdObjectFactory(), this.libraryManager.modelManager)

        this.visitor = visitor
        this.requirements = ElmRequirements(VersionedIdentifier().withId("result"), Null())
        this.parameters = parameters
        this.evaluationDateTime = evaluationDateTime
    }

    private fun getDataRequirementForTypeName(
        typeName: QName,
        profiledTypeName: QName?,
    ): ElmDataRequirement? {
        var type: DataType? = null
        try {
            type = typeResolver.resolveTypeName(typeName)
        } catch (e: Exception) {
            // ignore an exception resolving the type, just don't attempt to build an unbound
            // requirement
            // We should only be building unbound requirements for retrievable types, so if we can't
            // determine
            // retrievability, ignore the requirement
        }

        if (type != null && type is ClassType && type.isRetrievable) {
            var requirement = unboundDataRequirements[profiledTypeName ?: typeName]
            if (requirement == null) {
                val retrieve = Retrieve()
                retrieve.dataType = typeName
                if (
                    profiledTypeName != null &&
                        profiledTypeName.namespaceURI != null &&
                        profiledTypeName.localPart != null
                ) {
                    retrieve.templateId =
                        profiledTypeName.namespaceURI + "/" + profiledTypeName.localPart
                } else if (typeName.namespaceURI != null && typeName.localPart != null) {
                    retrieve.templateId = typeName.namespaceURI + "/" + typeName.localPart
                }
                requirement = ElmDataRequirement(this.currentLibraryIdentifier, retrieve)
                unboundDataRequirements[typeName] = requirement
                reportRequirement(requirement)
            }

            return requirement
        }

        return null
    }

    fun reportProperty(property: Property): ElmPropertyRequirement? {
        // if scope is specified, it's a reference to an alias in a current query context
        // if source is an AliasRef, it's a reference to an alias in a current query context
        // if source is a LetRef, it's a reference to a let in a current query context
        // if source is a Property, add the current property to a qualifier
        // Otherwise, report it as an unbound property reference to the type of source
        if (property.scope != null || property.source is AliasRef) {
            val aliasName =
                if (property.scope != null) property.scope else (property.source as AliasRef).name
            var aliasContext = this.currentQueryContext.resolveAlias(aliasName)
            var inCurrentScope = true
            if (aliasContext == null) {
                // This is a reference to an alias in an outer scope
                aliasContext = resolveAlias(aliasName)
                inCurrentScope = false
            }
            val propertyRequirement =
                ElmPropertyRequirement(
                    this.currentLibraryIdentifier,
                    property,
                    aliasContext.querySource,
                    inCurrentScope,
                )

            aliasContext.reportProperty(propertyRequirement)
            return propertyRequirement
        }

        if (property.source is QueryLetRef) {
            val letName = (property.source as QueryLetRef).name
            var letContext = this.currentQueryContext.resolveLet(letName)
            var inCurrentScope = true
            if (letContext == null) {
                // This is a reference to a let definition in an outer scope
                letContext = resolveLet(letName)
                inCurrentScope = false
            }
            val propertyRequirement =
                ElmPropertyRequirement(
                    this.currentLibraryIdentifier,
                    property,
                    letContext.letClause,
                    inCurrentScope,
                )

            letContext.reportProperty(propertyRequirement)
            return propertyRequirement
        }

        if (property.source is Property) {
            val sourceProperty = property.source as Property?
            val qualifiedProperty = Property()
            qualifiedProperty.source = sourceProperty!!.source
            qualifiedProperty.scope = sourceProperty.scope
            qualifiedProperty.resultTypeName = property.resultTypeName
            qualifiedProperty.resultTypeSpecifier = property.resultTypeSpecifier
            qualifiedProperty.localId = sourceProperty.localId
            qualifiedProperty.path = sourceProperty.path + "." + property.path
            qualifiedProperty.resultType = property.resultType
            return reportProperty(qualifiedProperty)
        } else {
            val typeName = getType(property.source)
            if (typeName != null) {
                val sourceResultType = property.source?.resultType
                val requirement =
                    getDataRequirementForTypeName(typeName, getProfiledType(sourceResultType))
                if (requirement != null) {
                    val propertyRequirement =
                        ElmPropertyRequirement(
                            this.currentLibraryIdentifier,
                            property,
                            property.source!!,
                            false,
                        )
                    requirement.reportProperty(propertyRequirement)
                    return propertyRequirement
                }
            }
        }

        return null
    }

    fun toConcept(conceptDef: ElmRequirement): Concept {
        return toConcept(conceptDef.libraryIdentifier, conceptDef.element as ConceptDef)
    }

    fun toConcept(libraryIdentifier: VersionedIdentifier, conceptDef: ConceptDef): Concept {
        val concept = Concept()
        concept.display = conceptDef.display
        for (codeRef in conceptDef.code) {
            concept.code.add(toCode(resolveCodeRef(libraryIdentifier, codeRef)!!))
        }
        return concept
    }

    fun toCode(codeDef: CodeDef): Code {
        return Code()
            .withCode(codeDef.id)
            .withSystem(codeDef.codeSystem)
            .withDisplay(codeDef.display)
    }

    fun resolveCodeRef(codeRef: ElmRequirement): CodeDef? {
        return resolveCodeRef(codeRef.libraryIdentifier, codeRef.element as CodeRef)
    }

    fun resolveCodeRef(libraryIdentifier: VersionedIdentifier, codeRef: CodeRef): CodeDef? {
        // If the reference is to another library, resolve to that library
        if (codeRef.libraryName != null) {
            return resolveLibrary(libraryIdentifier, codeRef.libraryName!!)
                .resolveCodeRef(codeRef.name!!)
        }

        return resolveLibrary(libraryIdentifier).resolveCodeRef(codeRef.name!!)
    }

    fun resolveConceptRef(conceptRef: ElmRequirement): ConceptDef? {
        return resolveConceptRef(conceptRef.libraryIdentifier, conceptRef.element as ConceptRef)
    }

    fun resolveConceptRef(
        libraryIdentifier: VersionedIdentifier,
        conceptRef: ConceptRef,
    ): ConceptDef? {
        if (conceptRef.libraryName != null) {
            return resolveLibrary(libraryIdentifier, conceptRef.libraryName!!)
                .resolveConceptRef(conceptRef.name!!)
        }

        return resolveLibrary(libraryIdentifier).resolveConceptRef(conceptRef.name!!)
    }

    fun resolveCodeSystemRef(codeSystemRef: ElmRequirement): CodeSystemDef? {
        return resolveCodeSystemRef(
            codeSystemRef.libraryIdentifier,
            codeSystemRef.element as CodeSystemRef,
        )
    }

    fun resolveCodeSystemRef(
        libraryIdentifier: VersionedIdentifier,
        codeSystemRef: CodeSystemRef,
    ): CodeSystemDef? {
        if (codeSystemRef.libraryName != null) {
            return resolveLibrary(libraryIdentifier, codeSystemRef.libraryName!!)
                .resolveCodeSystemRef(codeSystemRef.name!!)
        }

        return resolveLibrary(libraryIdentifier).resolveCodeSystemRef(codeSystemRef.name!!)
    }

    fun resolveValueSetRef(valueSetRef: ElmRequirement): ValueSetDef? {
        return resolveValueSetRef(valueSetRef.libraryIdentifier, valueSetRef.element as ValueSetRef)
    }

    fun resolveValueSetRef(
        libraryIdentifier: VersionedIdentifier,
        valueSetRef: ValueSetRef,
    ): ValueSetDef? {
        if (valueSetRef.libraryName != null) {
            return resolveLibrary(libraryIdentifier, valueSetRef.libraryName!!)
                .resolveValueSetRef(valueSetRef.name!!)
        }

        return resolveLibrary(libraryIdentifier).resolveValueSetRef(valueSetRef.name!!)
    }

    fun resolveLibrary(libraryRef: ElmRequirement): CompiledLibrary {
        return resolveLibrary(
            libraryRef.libraryIdentifier,
            (libraryRef.element as LibraryRef).libraryName!!,
        )
    }

    fun resolveIncludeRef(
        libraryIdentifier: VersionedIdentifier,
        localLibraryName: String,
    ): IncludeDef? {
        val targetLibrary = resolveLibrary(libraryIdentifier)
        return targetLibrary.resolveIncludeRef(localLibraryName)
    }

    fun resolveLibrary(
        libraryIdentifier: VersionedIdentifier,
        localLibraryName: String,
    ): CompiledLibrary {
        val includeDef = resolveIncludeRef(libraryIdentifier, localLibraryName)
        return resolveLibraryFromIncludeDef(includeDef!!)
    }

    fun resolveLibraryFromIncludeDef(includeDef: IncludeDef): CompiledLibrary {
        val targetLibraryIdentifier =
            VersionedIdentifier()
                .withSystem(getUriPart(includeDef.path))
                .withId(getNamePart(includeDef.path))
                .withVersion(includeDef.version)

        return resolveLibrary(targetLibraryIdentifier)
    }

    fun resolveLibrary(libraryIdentifier: VersionedIdentifier): CompiledLibrary {
        // TODO: Need to support loading from ELM so we don't need options.
        val referencedLibrary = libraryManager.resolveLibrary(libraryIdentifier)

        // TODO: Report translation errors here...
        // for (CqlTranslatorException error : errors) {
        //    this.recordParsingException(error);
        // }
        return referencedLibrary
    }
}
