package org.cqframework.cql.elm.requirements.fhir

import ca.uhn.fhir.context.FhirVersionEnum
import java.time.ZonedDateTime
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.Any
import kotlin.Boolean
import kotlin.Exception
import kotlin.IllegalArgumentException
import kotlin.Int
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.Iterable
import kotlin.collections.LinkedHashSet
import kotlin.collections.MutableList
import kotlin.collections.MutableMap
import kotlin.collections.MutableSet
import kotlin.collections.mutableListOf
import kotlin.plus
import kotlin.text.StringBuilder
import kotlin.text.endsWith
import kotlin.text.equals
import kotlin.text.format
import kotlin.text.isEmpty
import kotlin.text.lastIndexOf
import kotlin.text.startsWith
import kotlin.text.substring
import kotlin.text.trim
import org.cqframework.cql.cql2elm.CqlCompilerOptions
import org.cqframework.cql.cql2elm.LibraryManager
import org.cqframework.cql.cql2elm.model.CompiledLibrary
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.cqframework.cql.cql2elm.tracking.Trackable.trackbacks
import org.cqframework.cql.elm.evaluation.ElmAnalysisHelper
import org.cqframework.cql.elm.evaluation.ElmEvaluationHelper.evaluate
import org.cqframework.cql.elm.requirements.ElmDataRequirement
import org.cqframework.cql.elm.requirements.ElmPertinenceContext
import org.cqframework.cql.elm.requirements.ElmRequirement
import org.cqframework.cql.elm.requirements.ElmRequirements
import org.cqframework.cql.elm.requirements.ElmRequirementsContext
import org.cqframework.cql.elm.requirements.ElmRequirementsVisitor
import org.cqframework.cql.elm.requirements.fhir.utilities.SpecificationLevel
import org.cqframework.cql.elm.requirements.fhir.utilities.SpecificationSupport
import org.hl7.cql.model.DataType
import org.hl7.cql.model.IntervalType
import org.hl7.cql.model.ListType
import org.hl7.cql.model.NamedType
import org.hl7.cql.model.NamespaceManager.Companion.getNamePart
import org.hl7.cql.model.NamespaceManager.Companion.getUriPart
import org.hl7.cql_annotations.r1.Annotation
import org.hl7.cql_annotations.r1.Narrative
import org.hl7.elm.r1.AccessModifier
import org.hl7.elm.r1.Code
import org.hl7.elm.r1.CodeDef
import org.hl7.elm.r1.CodeFilterElement
import org.hl7.elm.r1.CodeRef
import org.hl7.elm.r1.CodeSystemDef
import org.hl7.elm.r1.Concept
import org.hl7.elm.r1.ConceptRef
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.FunctionDef
import org.hl7.elm.r1.IncludeDef
import org.hl7.elm.r1.IncludeElement
import org.hl7.elm.r1.List
import org.hl7.elm.r1.Literal
import org.hl7.elm.r1.ParameterDef
import org.hl7.elm.r1.Property
import org.hl7.elm.r1.Retrieve
import org.hl7.elm.r1.ToList
import org.hl7.elm.r1.UsingDef
import org.hl7.elm.r1.ValueSetDef
import org.hl7.elm.r1.ValueSetRef
import org.hl7.elm.r1.VersionedIdentifier
import org.hl7.fhir.exceptions.FHIRException
import org.hl7.fhir.r5.model.CanonicalType
import org.hl7.fhir.r5.model.CodeableConcept
import org.hl7.fhir.r5.model.Coding
import org.hl7.fhir.r5.model.DataRequirement
import org.hl7.fhir.r5.model.Enumerations
import org.hl7.fhir.r5.model.Enumerations.FHIRTypes
import org.hl7.fhir.r5.model.Extension
import org.hl7.fhir.r5.model.IntegerType
import org.hl7.fhir.r5.model.Library
import org.hl7.fhir.r5.model.ParameterDefinition
import org.hl7.fhir.r5.model.Period
import org.hl7.fhir.r5.model.RelatedArtifact
import org.hl7.fhir.r5.model.StringType
import org.hl7.fhir.utilities.validation.ValidationMessage
import org.opencds.cqf.cql.engine.fhir.converter.FhirTypeConverterFactory

class DataRequirementsProcessor {
    val validationMessages: MutableList<ValidationMessage?> = ArrayList<ValidationMessage?>()

    private var specificationSupport = SpecificationSupport()

    fun setSpecificationLevel(specificationLevel: SpecificationLevel) {
        specificationSupport = SpecificationSupport(specificationLevel)
    }

    @JvmOverloads
    fun gatherDataRequirements(
        libraryManager: LibraryManager,
        translatedLibrary: CompiledLibrary,
        options: CqlCompilerOptions,
        expressions: MutableSet<String?>?,
        includeLogicDefinitions: Boolean,
        recursive: Boolean = true,
    ): Library {
        return gatherDataRequirements(
            libraryManager,
            translatedLibrary,
            options,
            expressions,
            null,
            null,
            includeLogicDefinitions,
            recursive,
        )
    }

    fun gatherDataRequirements(
        libraryManager: LibraryManager,
        translatedLibrary: CompiledLibrary,
        options: CqlCompilerOptions,
        expressions: MutableSet<String?>?,
        parameters: MutableMap<String?, Any?>?,
        includeLogicDefinitions: Boolean,
        recursive: Boolean,
    ): Library {
        return gatherDataRequirements(
            libraryManager,
            translatedLibrary,
            options,
            expressions,
            parameters,
            null,
            includeLogicDefinitions,
            recursive,
        )
    }

    /**
     * Gathers the data requirements for the given translated library, returning a Library resource
     * with the effective data requirements for the given expression or set of expressions in the
     * given compiled library.
     *
     * @param libraryManager The library manager used to support compilation of the library
     * @param translatedLibrary The compiled library to gather data requirements from
     * @param options The translator options used to compile the library
     * @param expressions The expressions to gather data requirements for, null for all expressions
     *   in the library
     * @param parameters Any parameters to the expressions to be analyzed. If null, analysis will
     *   only be performed on ELM, whereas if provided, analysis will be performed by attempting to
     *   evaluate compile-time evaluable data requirements comparands
     * @param evaluationDateTime The date time of the evaluation (used to provide the request date
     *   time to the engine in the case that compile-time evaluable expressions are evaluated
     * @param includeLogicDefinitions True to include logic definition extensions in the output
     *   containing the source for each expression from which data requirements are gathered
     * @param recursive True to indicate the data requirements gather should be recursive
     * @return
     */
    fun gatherDataRequirements(
        libraryManager: LibraryManager,
        translatedLibrary: CompiledLibrary,
        options: CqlCompilerOptions,
        expressions: MutableSet<String?>?,
        parameters: MutableMap<String?, Any?>?,
        evaluationDateTime: ZonedDateTime?,
        includeLogicDefinitions: Boolean,
        recursive: Boolean,
    ): Library {
        val visitor = ElmRequirementsVisitor()
        val context =
            ElmRequirementsContext(libraryManager, options, visitor, parameters, evaluationDateTime)

        var expressionDefs: MutableList<ExpressionDef>?
        if (expressions == null) {
            visitor.visitLibrary(translatedLibrary.library!!, context)
            expressionDefs =
                if (
                    translatedLibrary.library != null &&
                        translatedLibrary.library!!.statements != null
                ) {
                    translatedLibrary.library!!.statements!!.def
                } else {
                    ArrayList()
                }
        } else {
            expressionDefs = ArrayList()
            context.enterLibrary(translatedLibrary.identifier)
            try {
                // Always visit using definitions
                for (usingDef in translatedLibrary.library!!.usings!!.def) {
                    visitor.visitUsingDef(usingDef, context)
                }

                for (expression in expressions) {
                    if (expression != null) {
                        val ed = translatedLibrary.resolveExpressionRef(expression)
                        if (ed != null) {
                            expressionDefs.add(ed)
                            visitor.visitElement(ed, context)
                        } else {
                            // If the expression is the name of any functions, include those in the
                            // gather
                            // TODO: Provide a mechanism to specify a function def (need signature)
                            val fds = translatedLibrary.resolveFunctionRef(expression)
                            for (fd in fds) {
                                expressionDefs.add(fd)
                                visitor.visitElement(fd, context)
                            }
                        }
                    }
                }
            } finally {
                context.exitLibrary()
            }
        }

        // In the non-recursive case
        // Collect top-level dependencies that have the same library identifier as the primary
        // library
        // Collect data requirements reported or inferred on expressions in the library
        // In the recursive case
        // Collect all top-level dependencies
        // Collect all reported or inferred data requirements
        var requirements = ElmRequirements(translatedLibrary.identifier, translatedLibrary.library)
        if (recursive) {
            // Collect all the dependencies
            requirements.reportRequirement(context.requirements)
            // Collect reported data requirements from each expression
            for (reportedRequirements in context.reportedRequirements) {
                requirements.reportRequirement(reportedRequirements)
            }
            for (inferredRequirement in context.inferredRequirements) {
                requirements.reportRequirement(inferredRequirement)
            }
        } else {
            gatherLibrarySpecificRequirements(
                requirements,
                translatedLibrary.identifier,
                context.requirements,
            )
            for (ed in expressionDefs) {
                // Just being defensive here, can happen when there are errors deserializing the
                // measure
                // Collect both inferred and reported requirements here, since reported requirements
                // will not
                // include
                // directly inferred requirements
                val reportedRequirements = context.getReportedRequirements(ed)
                gatherLibrarySpecificRequirements(
                    requirements,
                    translatedLibrary.identifier,
                    reportedRequirements,
                )

                val inferredRequirement = context.getInferredRequirements(ed)
                gatherLibrarySpecificRequirements(
                    requirements,
                    translatedLibrary.identifier,
                    inferredRequirement,
                )
            }
        }

        // Collapse the requirements
        if (options.collapseDataRequirements) {
            for (requirement in requirements.requirements) {
                collapseExtensionReference(context, requirement)
            }
            requirements = requirements.collapse(context)
        }

        return createLibrary(
            context,
            requirements,
            translatedLibrary.identifier!!,
            expressionDefs,
            parameters,
            evaluationDateTime,
            includeLogicDefinitions,
        )
    }

    private fun gatherLibrarySpecificRequirements(
        requirements: ElmRequirements,
        libraryIdentifier: VersionedIdentifier?,
        sourceRequirements: ElmRequirements,
    ) {
        for (requirement in sourceRequirements.requirements) {
            gatherLibrarySpecificRequirements(requirements, libraryIdentifier, requirement)
        }
    }

    private fun gatherLibrarySpecificRequirements(
        requirements: ElmRequirements,
        libraryIdentifier: VersionedIdentifier?,
        requirement: ElmRequirement?,
    ) {
        if (requirement != null && requirement.getLibraryIdentifier().equals(libraryIdentifier)) {
            requirements.reportRequirement(requirement)
        }
    }

    /**
     * If the requirement has property references to `url` and `extension`, and a code filter on
     * `url` to a literal extension value, replace the `url` and `extension` property references
     * with a new property reference to the tail of the extension Also remove `us-core-` and
     * `qicore-` as wellknown prefixes
     *
     * @param requirement
     *
     * TODO: Use the structure definition element slice name as the name of the property, rather
     *   than the hard-coded removal of well-known prefixes
     */
    private fun collapseExtensionReference(
        context: ElmRequirementsContext,
        requirement: ElmRequirement?,
    ) {
        if (requirement is ElmDataRequirement) {
            if (requirement.hasProperties()) {
                var urlProperty: Property? = null
                var extensionProperty: Property? = null
                for (p in requirement.properties) {
                    if (p.path.equals("url")) {
                        urlProperty = p
                        continue
                    }

                    if (p.path.equals("extension")) {
                        extensionProperty = p
                        continue
                    }
                }

                if (urlProperty != null) {
                    val r = requirement.retrieve
                    if (r != null) {
                        var extensionFilterElement: CodeFilterElement? = null
                        var extensionFilterComponent:
                            DataRequirement.DataRequirementCodeFilterComponent? =
                            null
                        for (cfe in r.codeFilter) {
                            val cfc =
                                toCodeFilterComponent(
                                    context,
                                    requirement.getLibraryIdentifier(),
                                    cfe.property,
                                    cfe.value,
                                )

                            if (
                                cfc.hasPath() &&
                                    cfc.hasCode() &&
                                    "url" == cfc.getPath() &&
                                    cfc.codeFirstRep.hasCode() &&
                                    cfc.codeFirstRep.getCode().startsWith("http://")
                            ) {
                                extensionFilterElement = cfe
                                extensionFilterComponent = cfc
                                break
                            }
                        }

                        if (extensionFilterElement != null && extensionFilterComponent != null) {
                            var extensionName = extensionFilterComponent.codeFirstRep.getCode()
                            val tailIndex = extensionName.lastIndexOf("/")
                            if (tailIndex > 0) {
                                extensionName = extensionName.substring(tailIndex + 1)
                            }
                            if (extensionName.startsWith("us-core-")) {
                                extensionName = extensionName.substring(8)
                            }
                            if (extensionName.startsWith("qicore-")) {
                                extensionName = extensionName.substring(7)
                            }
                            r.codeFilter.remove(extensionFilterElement)
                            requirement.removeProperty(urlProperty)
                            if (extensionProperty != null) {
                                requirement.removeProperty(extensionProperty)
                            }
                            requirement.addProperty(Property().withPath(extensionName))
                        }
                    }
                }
            }
        }
    }

    private fun createLibrary(
        context: ElmRequirementsContext,
        requirements: ElmRequirements,
        libraryIdentifier: VersionedIdentifier,
        expressionDefs: Iterable<ExpressionDef?>,
        parameters: MutableMap<String?, Any?>?,
        evaluationDateTime: ZonedDateTime?,
        includeLogicDefinitions: Boolean,
    ): Library {
        val returnLibrary = Library()
        returnLibrary.setStatus(Enumerations.PublicationStatus.ACTIVE)
        val libraryType = CodeableConcept()
        val typeCoding = Coding().setCode("module-definition")
        typeCoding.setSystem("http://terminology.hl7.org/CodeSystem/library-type")
        libraryType.addCoding(typeCoding)
        returnLibrary.setName("EffectiveDataRequirements")
        returnLibrary.setType(libraryType)
        returnLibrary.setSubject(extractSubject(context))
        returnLibrary.getExtension().addAll(extractDirectReferenceCodes(context, requirements))
        returnLibrary.getRelatedArtifact().addAll(extractRelatedArtifacts(context, requirements))
        returnLibrary.getDataRequirement().addAll(extractDataRequirements(context, requirements))
        returnLibrary
            .getParameter()
            .addAll(extractParameters(context, requirements, libraryIdentifier, expressionDefs))
        if (includeLogicDefinitions) {
            returnLibrary.getExtension().addAll(extractLogicDefinitions(context, requirements))
        }
        return returnLibrary
    }

    private fun extractSubject(context: ElmRequirementsContext?): CodeableConcept? {
        // TODO: Determine context (defaults to Patient if not set, so not critical until we have a
        // non-patient-context
        // use case)
        return null
    }

    private fun extractDirectReferenceCodes(
        context: ElmRequirementsContext,
        requirements: ElmRequirements,
    ): MutableList<Extension?> {
        val result: MutableList<Extension?> = ArrayList()

        for (def in requirements.codeDefs) {
            result.add(
                toDirectReferenceCode(
                    context,
                    def.getLibraryIdentifier(),
                    (def.getElement() as CodeDef?)!!,
                )
            )
        }

        return result
    }

    private fun toDirectReferenceCode(
        context: ElmRequirementsContext,
        libraryIdentifier: VersionedIdentifier?,
        def: CodeDef,
    ): Extension {
        val e = Extension()
        e.setUrl(specificationSupport.directReferenceCodeExtensionUrl)
        e.setValue(toCoding(context, libraryIdentifier, context.toCode(def)))
        return e
    }

    private fun extractRelatedArtifacts(
        context: ElmRequirementsContext?,
        requirements: ElmRequirements,
    ): MutableList<RelatedArtifact?> {
        val result: MutableList<RelatedArtifact?> = ArrayList()

        // Report model dependencies
        // URL for a model info is: [baseCanonical]/Library/[model-name]-ModelInfo
        for (def in requirements.usingDefs) {
            // System model info is an implicit dependency, do not report
            if (!(def.getElement() as UsingDef).localIdentifier.equals("System")) {
                result.add(
                    toRelatedArtifact(def.getLibraryIdentifier(), (def.getElement() as UsingDef?)!!)
                )
            }
        }

        // Report library dependencies
        for (def in requirements.includeDefs) {
            result.add(
                toRelatedArtifact(def.getLibraryIdentifier(), (def.getElement() as IncludeDef?)!!)
            )
        }

        // Report CodeSystem dependencies
        for (def in requirements.codeSystemDefs) {
            result.add(
                toRelatedArtifact(
                    def.getLibraryIdentifier(),
                    (def.getElement() as CodeSystemDef?)!!,
                )
            )
        }

        // Report ValueSet dependencies
        for (def in requirements.valueSetDefs) {
            result.add(
                toRelatedArtifact(def.getLibraryIdentifier(), (def.getElement() as ValueSetDef?)!!)
            )
        }

        return result
    }

    private fun isEquivalentDefinition(
        existingPd: ParameterDefinition,
        pd: ParameterDefinition,
    ): Boolean {
        // TODO: Consider cardinality
        return pd.getType() == existingPd.getType()
    }

    private fun extractParameters(
        context: ElmRequirementsContext?,
        requirements: ElmRequirements,
        libraryIdentifier: VersionedIdentifier,
        expressionDefs: Iterable<ExpressionDef?>,
    ): MutableList<ParameterDefinition?> {
        val result: MutableList<ParameterDefinition?> = ArrayList()

        // TODO: Support library qualified parameters
        // Until then, name clashes should result in a warning
        val pds: MutableMap<String?, ParameterDefinition> = HashMap()
        for (def in requirements.parameterDefs) {
            val pd =
                toParameterDefinition(
                    def.getLibraryIdentifier(),
                    (def.getElement() as ParameterDef?)!!,
                )
            if (pds.containsKey(pd.getName())) {
                val existingPd: ParameterDefinition = pds[pd.getName()]!!
                if (!isEquivalentDefinition(existingPd, pd)) {
                    // Issue a warning that the parameter has a duplicate name but an incompatible
                    // type
                    validationMessages.add(
                        ValidationMessage(
                            ValidationMessage.Source.Publisher,
                            ValidationMessage.IssueType.NOTSUPPORTED,
                            "CQL Library Packaging",
                            String.format(
                                "Parameter declaration %s.%s is already defined in a different library with a different type. Parameter binding may result in errors during evaluation.",
                                def.getLibraryIdentifier().id,
                                pd.getName(),
                            ),
                            ValidationMessage.IssueSeverity.WARNING,
                        )
                    )
                }
            } else {
                pds[pd.getName()] = pd
                result.add(pd)
            }
        }

        for (def in expressionDefs) {
            if (
                def != null &&
                    (def !is FunctionDef) &&
                    (def.accessLevel == null || def.accessLevel == AccessModifier.PUBLIC)
            ) {
                result.add(toOutputParameterDefinition(libraryIdentifier, def))
            }
        }

        return result
    }

    private fun getAnnotation(e: Element): Annotation? {
        for (o in e.annotation) {
            if (o is Annotation) {
                return o
            }
        }

        return null
    }

    private fun toNarrativeText(a: Annotation): String {
        val sb = StringBuilder()
        if (a.s != null) {
            addNarrativeText(sb, a.s!!)
        }
        return sb.toString()
    }

    private fun addNarrativeText(sb: StringBuilder, n: Narrative) {
        for (s in n.content) {
            if (s is Narrative) {
                addNarrativeText(sb, (s as Narrative?)!!)
            } else if (s is String) {
                sb.append(s as String?)
            }
        }
    }

    private fun extractLogicDefinitions(
        context: ElmRequirementsContext?,
        requirements: ElmRequirements,
    ): MutableList<Extension?> {
        val result: MutableList<Extension?> = ArrayList()

        var sequence = 0
        for (req in requirements.expressionDefs) {
            val def = req.getElement() as ExpressionDef
            val a = getAnnotation(def)
            if (a != null) {
                result.add(toLogicDefinition(req, def, toNarrativeText(a), sequence++))
            }
        }

        for (req in requirements.functionDefs) {
            val def = req.getElement() as FunctionDef
            val a = getAnnotation(def)
            if (a != null) {
                result.add(toLogicDefinition(req, def, toNarrativeText(a), sequence++))
            }
        }

        return result
    }

    private fun toString(value: String?): StringType {
        val result = StringType()
        result.value = value
        return result
    }

    private fun toInteger(value: Int): IntegerType {
        val result = IntegerType()
        result.value = value
        return result
    }

    private fun toLogicDefinition(
        req: ElmRequirement,
        def: ExpressionDef,
        text: String?,
        sequence: Int,
    ): Extension {
        val e = Extension()
        e.setUrl(specificationSupport.logicDefinitionExtensionUrl)
        // TODO: Include the libraryUrl
        e.addExtension(
            Extension().setUrl("libraryName").setValue(toString(req.getLibraryIdentifier().id))
        )
        e.addExtension(Extension().setUrl("name").setValue(toString(def.name)))
        e.addExtension(Extension().setUrl("statement").setValue(toString(text)))
        e.addExtension(Extension().setUrl("displaySequence").setValue(toInteger(sequence)))
        return e
    }

    private fun extractDataRequirements(
        context: ElmRequirementsContext,
        requirements: ElmRequirements,
    ): MutableList<DataRequirement?> {
        val result: MutableList<DataRequirement?> = ArrayList()

        val retrieveMap: MutableMap<String?, Retrieve> = HashMap()
        for (retrieve in requirements.retrieves) {
            if (retrieve.getElement().localId != null) {
                retrieveMap[retrieve.getElement().localId] = (retrieve.getElement() as Retrieve?)!!
            }
        }

        for (retrieve in requirements.retrieves) {
            if ((retrieve.getElement() as Retrieve).dataType != null) {
                result.add(
                    toDataRequirement(
                        context,
                        retrieve.getLibraryIdentifier(),
                        (retrieve.getElement() as Retrieve?)!!,
                        retrieveMap,
                        if (retrieve is ElmDataRequirement) retrieve.properties else null,
                        if (retrieve is ElmDataRequirement) retrieve.pertinenceContext else null,
                    )
                )
            }
        }

        return result
    }

    private fun toRelatedArtifact(
        libraryIdentifier: VersionedIdentifier?,
        usingDef: UsingDef,
    ): RelatedArtifact? {
        return RelatedArtifact()
            .setType(RelatedArtifact.RelatedArtifactType.DEPENDSON)
            .setDisplay(
                if (usingDef.localIdentifier != null)
                    String.format("%s model information", usingDef.localIdentifier)
                else null
            ) // Could potentially look for a well-known comment tag too, @description?
            .setResource(
                getModelInfoReferenceUrl(usingDef.uri, usingDef.localIdentifier!!, usingDef.version)
            )
    }

    /*
    Override the referencing URL for the FHIR-ModelInfo library
    This is required because models do not have a "namespace" in the same way that libraries do,
    so there is no way for the UsingDefinition to have a Uri that is different than the expected URI that the
    providers understand. I.e. model names and model URIs are one-to-one.
     */
    private fun mapModelInfoUri(uri: String, name: String): String {
        if (name == "FHIR" && uri == "http://hl7.org/fhir") {
            return "http://fhir.org/guides/cqf/common"
        }
        return uri
    }

    private fun getModelInfoReferenceUrl(uri: String?, name: String, version: String?): String {
        if (uri != null) {
            return String.format(
                "%s/Library/%s-ModelInfo%s",
                mapModelInfoUri(uri, name),
                name,
                if (version != null) ("|$version") else "",
            )
        }

        return String.format(
            "Library/%s-ModelInfo%s",
            name,
            if (version != null) ("|$version") else "",
        )
    }

    private fun toRelatedArtifact(
        libraryIdentifier: VersionedIdentifier?,
        includeDef: IncludeDef,
    ): RelatedArtifact? {
        return RelatedArtifact()
            .setType(RelatedArtifact.RelatedArtifactType.DEPENDSON)
            .setDisplay(
                if (includeDef.localIdentifier != null)
                    String.format("Library %s", includeDef.localIdentifier)
                else null
            ) // Could potentially look for a well-known comment tag too, @description?
            .setResource(getReferenceUrl(includeDef.path, includeDef.version))
    }

    private fun getReferenceUrl(path: String?, version: String?): String {
        val uri = getUriPart(path)
        val name = getNamePart(path)

        if (uri != null) {
            // The translator has no way to correctly infer the namespace of the FHIRHelpers
            // library, since it will
            // happily provide that source to any namespace that wants it
            // So override the declaration here so that it points back to the FHIRHelpers library in
            // the base
            // specification
            // if (name.equals("FHIRHelpers") && !(uri.equals("http://hl7.org/fhir") ||
            // uri.equals("http://fhir.org/guides/cqf/common"))) {
            //    uri = "http://fhir.org/guides/cqf/common";
            // }
            return String.format(
                "%s/Library/%s%s",
                uri,
                name,
                if (version != null) ("|$version") else "",
            )
        }

        return String.format("Library/%s%s", path, if (version != null) ("|$version") else "")
    }

    private fun toRelatedArtifact(
        libraryIdentifier: VersionedIdentifier?,
        codeSystemDef: CodeSystemDef,
    ): RelatedArtifact? {
        return RelatedArtifact()
            .setType(RelatedArtifact.RelatedArtifactType.DEPENDSON)
            .setDisplay(String.format("Code system %s", codeSystemDef.name))
            .setResource(toReference(codeSystemDef))
    }

    private fun toRelatedArtifact(
        libraryIdentifier: VersionedIdentifier?,
        valueSetDef: ValueSetDef,
    ): RelatedArtifact? {
        return RelatedArtifact()
            .setType(RelatedArtifact.RelatedArtifactType.DEPENDSON)
            .setDisplay(String.format("Value set %s", valueSetDef.name))
            .setResource(toReference(valueSetDef))
    }

    private fun toParameterDefinition(
        libraryIdentifier: VersionedIdentifier?,
        def: ParameterDef,
    ): ParameterDefinition {
        val isList = AtomicBoolean(false)
        val resultType = def.resultType
        val typeCode = FHIRTypes.fromCode(toFHIRParameterTypeCode(resultType!!, def.name, isList))

        return ParameterDefinition()
            .setName(def.name)
            .setUse(Enumerations.OperationParameterUse.IN)
            .setMin(0)
            .setMax(if (isList.get()) "*" else "1")
            .setType(typeCode)
    }

    private fun toOutputParameterDefinition(
        libraryIdentifier: VersionedIdentifier,
        def: ExpressionDef,
    ): ParameterDefinition {
        val isList = AtomicBoolean(false)
        var typeCode: FHIRTypes? = null

        val defResultType = def.resultType
        try {
            typeCode = FHIRTypes.fromCode(toFHIRResultTypeCode(defResultType!!, def.name, isList))
        } catch (fhirException: FHIRException) {
            validationMessages.add(
                ValidationMessage(
                    ValidationMessage.Source.Publisher,
                    ValidationMessage.IssueType.NOTSUPPORTED,
                    "CQL Library Packaging",
                    String.format(
                        "Result type %s of library %s is not supported; implementations may not be able to use the result of this expression",
                        defResultType!!.toLabel(),
                        libraryIdentifier.id,
                    ),
                    ValidationMessage.IssueSeverity.WARNING,
                )
            )
        }

        return ParameterDefinition()
            .setName(def.name)
            .setUse(Enumerations.OperationParameterUse.OUT)
            .setMin(0)
            .setMax(if (isList.get()) "*" else "1")
            .setType(typeCode)
    }

    private fun toFHIRResultTypeCode(
        dataType: DataType,
        defName: String?,
        isList: AtomicBoolean,
    ): String {
        val isValid = AtomicBoolean(true)
        val resultCode = toFHIRTypeCode(dataType, isValid, isList)
        if (!isValid.get()) {
            // Issue a warning that the result type is not supported
            validationMessages.add(
                ValidationMessage(
                    ValidationMessage.Source.Publisher,
                    ValidationMessage.IssueType.NOTSUPPORTED,
                    "CQL Library Packaging",
                    String.format(
                        "Result type %s of definition %s is not supported; implementations may not be able to use the result of this expression",
                        dataType.toLabel(),
                        defName,
                    ),
                    ValidationMessage.IssueSeverity.WARNING,
                )
            )
        }

        return resultCode
    }

    private fun toFHIRParameterTypeCode(
        dataType: DataType,
        parameterName: String?,
        isList: AtomicBoolean,
    ): String {
        val isValid = AtomicBoolean(true)
        val resultCode = toFHIRTypeCode(dataType, isValid, isList)
        if (!isValid.get()) {
            // Issue a warning that the parameter type is not supported
            validationMessages.add(
                ValidationMessage(
                    ValidationMessage.Source.Publisher,
                    ValidationMessage.IssueType.NOTSUPPORTED,
                    "CQL Library Packaging",
                    String.format(
                        "Parameter type %s of parameter %s is not supported; reported as FHIR.Any",
                        dataType.toLabel(),
                        parameterName,
                    ),
                    ValidationMessage.IssueSeverity.WARNING,
                )
            )
        }

        return resultCode
    }

    private fun toFHIRTypeCode(
        dataType: DataType?,
        isValid: AtomicBoolean,
        isList: AtomicBoolean,
    ): String {
        isList.set(false)
        if (dataType is ListType) {
            isList.set(true)
            return toFHIRTypeCode(dataType.elementType, isValid)
        }

        return toFHIRTypeCode(dataType, isValid)
    }

    private fun toFHIRTypeCode(dataType: DataType?, isValid: AtomicBoolean): String {
        isValid.set(true)
        if (dataType is NamedType) {
            when ((dataType as NamedType).name) {
                "System.Boolean" -> return "boolean"
                "System.Integer" -> return "integer"
                "System.Decimal" -> return "decimal"
                "System.Date" -> return "date"
                "System.DateTime" -> return "dateTime"
                "System.Time" -> return "time"
                "System.String" -> return "string"
                "System.Quantity" -> return "Quantity"
                "System.Ratio" -> return "Ratio"
                "System.Any" -> return "Any"
                "System.Code" -> return "Coding"
                "System.Concept" -> return "CodeableConcept"
            }

            if ("FHIR" == (dataType as NamedType).namespace) {
                return (dataType as NamedType).simpleName
            }
        }

        if (dataType is IntervalType) {
            if (dataType.pointType is NamedType) {
                when ((dataType.pointType as NamedType).name) {
                    "System.Date",
                    "System.DateTime" -> return "Period"
                    "System.Quantity" -> return "Range"
                }
            }
        }

        isValid.set(false)
        return "Any"
    }

    /**
     * @param trackable
     * @param libraryIdentifier
     * @return
     *
     * TODO: This function is used to determine the library identifier in which the reference
     *   element was declared This is only possible if the ELM includes trackbacks, which are
     *   typically only available in ELM coming straight from the translator (i.e. de-compiled ELM
     *   won't have this) The issue is that when code filter expressions are distributed, the
     *   references may cross declaration contexts (i.e. a code filter expression from the library
     *   in which it was first expressed may be evaluated in the context of a data requirement
     *   inferred from a retrieve in a different library. If the library aliases are consistent,
     *   this isn't an issue, but if the library aliases are different, this will result in a
     *   failure to resolve the reference (or worse, an incorrect reference). This is being reported
     *   as a warning currently, but it is really an issue with the data requirement distribution,
     *   it should be rewriting references as it distributes (or better yet, ELM should have a
     *   library identifier that is fully resolved, rather than relying on library-specific aliases
     *   for library referencing elements.
     */
    private fun getDeclaredLibraryIdentifier(
        trackable: Element,
        libraryIdentifier: VersionedIdentifier?,
    ): VersionedIdentifier? {
        val trackbacks = trackable.trackbacks
        for (tb in trackbacks) {
            if (tb.library != null) {
                return tb.library
            }
        }

        validationMessages.add(
            ValidationMessage(
                ValidationMessage.Source.Publisher,
                ValidationMessage.IssueType.PROCESSING,
                "Data requirements processing",
                String.format(
                    "Library referencing element (%s) is potentially being resolved in a different context than it was declared. Ensure library aliases are consistent",
                    trackable.javaClass.simpleName,
                ),
                ValidationMessage.IssueSeverity.WARNING,
            )
        )

        return libraryIdentifier
    }

    private fun toCodeFilterComponent(
        context: ElmRequirementsContext,
        libraryIdentifier: VersionedIdentifier?,
        property: String?,
        value: Expression?,
    ): DataRequirement.DataRequirementCodeFilterComponent {
        val cfc = DataRequirement.DataRequirementCodeFilterComponent()

        cfc.setPath(property)

        // TODO: Support retrieval when the target is a CodeSystemRef
        if (value is ValueSetRef) {
            val declaredLibraryIdentifier = getDeclaredLibraryIdentifier(value, libraryIdentifier)
            cfc.setValueSet(
                toReference(context.resolveValueSetRef(declaredLibraryIdentifier, value))
            )
        }

        if (value is ToList) {
            resolveCodeFilterCodes(context, libraryIdentifier, cfc, value.operand)
        }

        if (value is List) {
            for (e in value.element) {
                resolveCodeFilterCodes(context, libraryIdentifier, cfc, e)
            }
        }

        if (value is Literal) {
            cfc.addCode().setCode(value.value)
        }

        return cfc
    }

    private fun toFhirValue(
        context: ElmRequirementsContext,
        value: Expression?,
    ): org.hl7.fhir.r5.model.DataType? {
        if (value == null) {
            return null
        }

        if (context.parameters == null) {
            return ElmAnalysisHelper.toFhirValue(context, value)
        } else {
            // Attempt to use an evaluation visitor to evaluate the value (must be compile-time
            // literal or this will
            // produce a runtime error)
            val result =
                evaluate(
                    context.resolveLibrary(context.getCurrentLibraryIdentifier()).library,
                    value,
                    context.parameters,
                    context.evaluationDateTime,
                )

            if (result is org.hl7.fhir.r5.model.DataType) {
                return result
            }

            if (result == null) {
                return null
            }

            val converter = FhirTypeConverterFactory().create(FhirVersionEnum.R5)
            val fhirResult = converter.toFhirType(result)
            if (fhirResult is org.hl7.fhir.r5.model.DataType) {
                return fhirResult
            }
            throw IllegalArgumentException(
                String.format(
                    "toFhirValue not implemented for result of type %s",
                    result.javaClass.getSimpleName(),
                )
            )
        }
    }

    private fun toDateFilterComponent(
        context: ElmRequirementsContext,
        libraryIdentifier: VersionedIdentifier?,
        property: String?,
        value: Expression?,
    ): DataRequirement.DataRequirementDateFilterComponent {
        val dfc = DataRequirement.DataRequirementDateFilterComponent()

        dfc.setPath(property)

        context.enterLibrary(libraryIdentifier)
        try {
            dfc.setValue(toFhirValue(context, value))
        } catch (e: Exception) {
            val p = Period()
            p.addExtension(
                "http://hl7.org/fhir/uv/crmi-analysisException",
                StringType(
                    String.format("Error attempting to determine filter value: %s", e.message)
                ),
            )
            dfc.setValue(p)
        } finally {
            context.exitLibrary()
        }

        return dfc
    }

    /**
     * Remove .reference from the path if the path is being used as a reference search
     *
     * @param path
     * @return
     */
    private fun stripReference(path: String): String {
        if (path.endsWith(".reference")) {
            return path.take(path.lastIndexOf("."))
        }
        return path
    }

    private fun toDataRequirement(
        context: ElmRequirementsContext,
        libraryIdentifier: VersionedIdentifier,
        retrieve: Retrieve,
        retrieveMap: MutableMap<String?, Retrieve>,
        properties: Iterable<Property>?,
        pertinenceContext: ElmPertinenceContext?,
    ): DataRequirement {
        val dr = DataRequirement()
        try {
            dr.setType(FHIRTypes.fromCode(retrieve.dataType!!.localPart))
        } catch (fhirException: FHIRException) {
            validationMessages.add(
                ValidationMessage(
                    ValidationMessage.Source.Publisher,
                    ValidationMessage.IssueType.NOTSUPPORTED,
                    "CQL Library Packaging",
                    String.format(
                        "Result type %s of library %s is not supported; implementations may not be able to use the result of this expression",
                        retrieve.dataType!!.localPart,
                        libraryIdentifier.id,
                    ),
                    ValidationMessage.IssueSeverity.WARNING,
                )
            )
        }

        // Set the id attribute of the data requirement if it will be referenced from an included
        // retrieve
        if (retrieve.localId != null && retrieve.include.isNotEmpty()) {
            for (ie in retrieve.include) {
                if (ie.includeFrom != null) {
                    dr.setId(retrieve.localId)
                }
            }
        }

        // Set profile if specified
        if (retrieve.templateId != null) {
            dr.setProfile(mutableListOf<CanonicalType?>(CanonicalType(retrieve.templateId)))
        }

        // collect must supports
        val ps: MutableSet<String?> = LinkedHashSet()

        // Set code path if specified
        if (retrieve.codeProperty != null) {
            dr.getCodeFilter()
                .add(
                    toCodeFilterComponent(
                        context,
                        libraryIdentifier,
                        retrieve.codeProperty,
                        retrieve.codes,
                    )
                )
            ps.add(retrieve.codeProperty)
        }

        // Add any additional code filters
        for (cfe in retrieve.codeFilter) {
            dr.getCodeFilter()
                .add(toCodeFilterComponent(context, libraryIdentifier, cfe.property, cfe.value))
        }

        // Set date path if specified
        if (retrieve.dateProperty != null) {
            dr.getDateFilter()
                .add(
                    toDateFilterComponent(
                        context,
                        libraryIdentifier,
                        retrieve.dateProperty,
                        retrieve.dateRange,
                    )
                )
            ps.add(retrieve.dateProperty)
        }

        // Add any additional date filters
        for (dfe in retrieve.dateFilter) {
            dr.getDateFilter()
                .add(toDateFilterComponent(context, libraryIdentifier, dfe.property, dfe.value))
        }

        // TODO: Add any other filters (use the cqfm-valueFilter extension until the content
        // infrastructure IG is
        // available)

        // Add any related data requirements
        if (retrieve.includedIn != null) {
            val relatedRetrieve: Retrieve = retrieveMap[retrieve.includedIn]!!
            var includeElement: IncludeElement? = null
            for (ie in relatedRetrieve.include) {
                if (ie.includeFrom != null && ie.includeFrom.equals(retrieve.localId)) {
                    includeElement = ie
                    break
                }
            }
            if (includeElement != null) {
                val relatedRequirement =
                    Extension().setUrl(specificationSupport.relatedRequirementExtensionUrl)
                relatedRequirement.addExtension("targetId", StringType(retrieve.includedIn))
                relatedRequirement.addExtension(
                    "targetProperty",
                    StringType(stripReference(includeElement.relatedProperty!!)),
                )
                dr.addExtension(relatedRequirement)
            }
        }

        // Add any properties as mustSupport items
        if (properties != null) {
            for (p in properties) {
                if (!ps.contains(p.path)) {
                    ps.add(p.path)
                }
            }
        }
        for (s in ps) {
            dr.addMustSupport(s)
        }

        if (
            pertinenceContext != null &&
                pertinenceContext.pertinenceValue != null &&
                !(pertinenceContext.pertinenceValue!!.trim { it <= ' ' }.isEmpty())
        ) {
            val extension = Extension()
            extension.setUrl(specificationSupport.pertinenceExtensionUrl)

            val coding = Coding()
            coding.setSystem("http://hl7.org/fhir/uv/cpg/CodeSystem/cpg-casefeature-pertinence")
            coding.setCode(pertinenceContext.pertinenceValue)
            extension.setValue(coding)

            dr.getExtension().add(extension)
        }

        return dr
    }

    private fun resolveCodeFilterCodes(
        context: ElmRequirementsContext,
        libraryIdentifier: VersionedIdentifier?,
        cfc: DataRequirement.DataRequirementCodeFilterComponent,
        e: Expression?,
    ) {
        if (e is CodeRef) {
            val declaredLibraryIdentifier = getDeclaredLibraryIdentifier(e, libraryIdentifier)
            cfc.addCode(
                toCoding(
                    context,
                    libraryIdentifier,
                    context.toCode(context.resolveCodeRef(declaredLibraryIdentifier, e)),
                )
            )
        }

        if (e is Code) {
            cfc.addCode(toCoding(context, libraryIdentifier, e))
        }

        if (e is ConceptRef) {
            val declaredLibraryIdentifier = getDeclaredLibraryIdentifier(e, libraryIdentifier)
            val c =
                toCodeableConcept(
                    context,
                    libraryIdentifier,
                    context.toConcept(
                        libraryIdentifier,
                        context.resolveConceptRef(declaredLibraryIdentifier, e),
                    ),
                )
            for (code in c.getCoding()) {
                cfc.addCode(code)
            }
        }

        if (e is Concept) {
            val c = toCodeableConcept(context, libraryIdentifier, e)
            for (code in c.getCoding()) {
                cfc.addCode(code)
            }
        }

        if (e is Literal) {
            cfc.addCode().setCode(e.value)
        }
    }

    private fun toCoding(
        context: ElmRequirementsContext,
        libraryIdentifier: VersionedIdentifier?,
        code: Code,
    ): Coding {
        val declaredLibraryIdentifier =
            getDeclaredLibraryIdentifier(code.system!!, libraryIdentifier)
        val codeSystemDef = context.resolveCodeSystemRef(declaredLibraryIdentifier, code.system)
        val coding = Coding()
        coding.setCode(code.code)
        coding.setDisplay(code.display)
        coding.setSystem(codeSystemDef.id)
        coding.setVersion(codeSystemDef.version)
        return coding
    }

    private fun toCodeableConcept(
        context: ElmRequirementsContext,
        libraryIdentifier: VersionedIdentifier?,
        concept: Concept,
    ): CodeableConcept {
        val codeableConcept = CodeableConcept()
        codeableConcept.setText(concept.display)
        for (code in concept.code) {
            codeableConcept.addCoding(toCoding(context, libraryIdentifier, code))
        }
        return codeableConcept
    }

    private fun toReference(codeSystemDef: CodeSystemDef): String {
        return codeSystemDef.id +
            (if (codeSystemDef.version != null) ("|" + codeSystemDef.version) else "")
    }

    private fun toReference(valueSetDef: ValueSetDef): String {
        return valueSetDef.id +
            (if (valueSetDef.version != null) ("|" + valueSetDef.version) else "")
    }
}
