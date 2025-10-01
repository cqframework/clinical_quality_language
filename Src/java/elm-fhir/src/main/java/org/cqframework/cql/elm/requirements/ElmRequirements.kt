package org.cqframework.cql.elm.requirements

import java.util.stream.Collectors
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.hl7.elm.r1.CodeDef
import org.hl7.elm.r1.CodeSystemDef
import org.hl7.elm.r1.ConceptDef
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.FunctionDef
import org.hl7.elm.r1.IncludeDef
import org.hl7.elm.r1.OperandDef
import org.hl7.elm.r1.ParameterDef
import org.hl7.elm.r1.Retrieve
import org.hl7.elm.r1.UsingDef
import org.hl7.elm.r1.ValueSetDef
import org.hl7.elm.r1.VersionedIdentifier

class ElmRequirements(libraryIdentifier: VersionedIdentifier, element: Element) :
    ElmRequirement(libraryIdentifier, element) {
    private val requirements: HashSet<ElmRequirement> = LinkedHashSet<ElmRequirement>()

    fun getRequirements(): Iterable<ElmRequirement> {
        return requirements
    }

    fun reportRequirement(requirement: ElmRequirement?) {
        if (requirement is ElmRequirements) {
            for (r in requirement.getRequirements()) {
                reportRequirement(r)
            }
        } else {
            if (requirement != null) {
                requirements.add(requirement)
            }
        }
    }

    val usingDefs: Iterable<ElmRequirement>
        get() =
            requirements
                .stream()
                .filter { x: ElmRequirement? -> x!!.element is UsingDef }
                .collect(Collectors.toList())

    val includeDefs: Iterable<ElmRequirement>
        get() =
            requirements
                .stream()
                .filter { x: ElmRequirement? -> x!!.element is IncludeDef }
                .collect(Collectors.toList())

    val codeSystemDefs: Iterable<ElmRequirement>
        get() =
            requirements
                .stream()
                .filter { x: ElmRequirement? -> x!!.element is CodeSystemDef }
                .collect(Collectors.toList())

    val valueSetDefs: Iterable<ElmRequirement>
        get() =
            requirements
                .stream()
                .filter { x: ElmRequirement? -> x!!.element is ValueSetDef }
                .collect(Collectors.toList())

    val codeDefs: Iterable<ElmRequirement>
        get() =
            requirements
                .stream()
                .filter { x: ElmRequirement? -> x!!.element is CodeDef }
                .collect(Collectors.toList())

    val conceptDefs: Iterable<ElmRequirement>
        get() =
            requirements
                .stream()
                .filter { x: ElmRequirement? -> x!!.element is ConceptDef }
                .collect(Collectors.toList())

    val parameterDefs: Iterable<ElmRequirement>
        get() =
            requirements
                .stream()
                .filter { x: ElmRequirement? -> x!!.element is ParameterDef }
                .collect(Collectors.toList())

    val expressionDefs: Iterable<ElmRequirement>
        get() =
            requirements
                .stream()
                .filter { x: ElmRequirement? ->
                    x!!.element is ExpressionDef && x.element !is FunctionDef
                }
                .collect(Collectors.toList())

    val functionDefs: Iterable<ElmRequirement>
        get() =
            requirements
                .stream()
                .filter { x: ElmRequirement? -> x!!.element is FunctionDef }
                .collect(Collectors.toList())

    val retrieves: Iterable<ElmRequirement>
        get() =
            requirements
                .stream()
                .filter { x: ElmRequirement? -> x!!.element is Retrieve }
                .collect(Collectors.toList())

    /*
    Collapse requirements: Determine the unique set of covering requirements given this set of requirements
    For dependencies, ensure dependencies are unique
    For parameters, unique by qualified name
    For expressions, unique by qualified name
    For data requirements, collapse according to the CQL specification: https://cql.hl7.org/05-languagesemantics.html#artifact-data-requirements
     */
    fun collapse(context: ElmRequirementsContext): ElmRequirements {
        val result = ElmRequirements(this.libraryIdentifier, this.element)

        // UsingDefs
        val models: MutableMap<String?, ElmRequirement?> = LinkedHashMap()
        for (r in this.usingDefs) {
            val ud = r.element as UsingDef
            val uri = ud.uri + (if (ud.version != null) "|" + ud.version else "")
            if (!models.containsKey(uri)) {
                models[uri] = r
                // TODO: How to report duplicate references, potentially warn about different names?
            }
        }

        for (r in models.values) {
            result.reportRequirement(r)
        }

        // IncludeDefs
        val libraries: MutableMap<String?, ElmRequirement?> = LinkedHashMap()
        for (r in this.includeDefs) {
            val id = r.element as IncludeDef
            val uri = id.path + (if (id.version != null) "|" + id.version else "")
            if (!libraries.containsKey(uri)) {
                libraries[uri] = r
                // TODO: How to report duplicate references, potentially warn about different names?
            }
        }

        for (r in libraries.values) {
            result.reportRequirement(r)
        }

        // CodeSystemDefs
        val codeSystems: MutableMap<String?, ElmRequirement?> = LinkedHashMap()
        for (r in this.codeSystemDefs) {
            val csd = r.element as CodeSystemDef
            val uri = csd.id + (if (csd.version != null) "|" + csd.version else "")
            if (!codeSystems.containsKey(uri)) {
                codeSystems[uri] = r
                // TODO: How to report duplicate references, potentially warn about different names?
            }
        }

        for (r in codeSystems.values) {
            result.reportRequirement(r)
        }

        // ValueSetDefs
        val valueSets: MutableMap<String?, ElmRequirement?> = LinkedHashMap()
        for (r in this.valueSetDefs) {
            val vsd = r.element as ValueSetDef
            val uri = vsd.id + (if (vsd.version != null) "|" + vsd.version else "")
            if (!valueSets.containsKey(uri)) {
                valueSets[uri] = r
                // TODO: How to report duplicate references, potentially warn about different names?
            }
        }

        for (r in valueSets.values) {
            result.reportRequirement(r)
        }

        // ConceptDefs
        val concepts: MutableMap<String?, ElmRequirement?> = LinkedHashMap()
        for (r in this.conceptDefs) {
            val cd = r.element as ConceptDef
            val uri =
                String.format(
                    "%s%s.%s",
                    if (r.libraryIdentifier.system != null) r.libraryIdentifier.system + "."
                    else "",
                    r.libraryIdentifier.id,
                    cd.name,
                )
            if (!concepts.containsKey(uri)) {
                concepts[uri] = r
                // TODO: How to report duplicate references, potentially warn about different names?
            }
        }

        for (r in concepts.values) {
            result.reportRequirement(r)
        }

        // CodeDefs
        val codes: MutableMap<String?, ElmRequirement?> = LinkedHashMap()
        for (r in this.codeDefs) {
            val cd = r.element as CodeDef
            val uri =
                String.format(
                    "%s#%s", // TODO: Look up CodeSystemDef to determine code system URI
                    cd.codeSystem!!.name,
                    cd.id,
                )

            if (!codes.containsKey(uri)) {
                codes[uri] = r
                // TODO: How to report duplicate references, potentially warn about different names?
            }
        }

        for (r in codes.values) {
            result.reportRequirement(r)
        }

        // ParameterDefs
        // NOTE: This purposely consolidates on unqualified name, the use case is from the
        // perspective of a particular
        // artifact,
        // parameters of the same name should be bound to the same input (i.e. single input
        // parameter namespace)
        val parameters: MutableMap<String?, ElmRequirement?> = LinkedHashMap()
        for (r in this.parameterDefs) {
            val pd = r.element as ParameterDef
            val uri = pd.name

            if (!parameters.containsKey(uri)) {
                parameters[uri] = r
                // TODO: How to report duplicate references, potentially warn about different names?
                // TODO: Note that it is potentially a hidden error here if parameters with the same
                // name have different
                // types
            }
        }

        for (r in parameters.values) {
            result.reportRequirement(r)
        }

        // ExpressionDefs
        val expressions: MutableMap<String?, ElmRequirement?> = LinkedHashMap()
        for (r in this.expressionDefs) {
            val ed = r.element as ExpressionDef
            val uri =
                String.format(
                    "%s%s.%s",
                    if (r.libraryIdentifier.system != null) r.libraryIdentifier.system + "."
                    else "",
                    r.libraryIdentifier.id,
                    ed.name,
                )

            if (!expressions.containsKey(uri)) {
                expressions[uri] = r
                // TODO: Do we need to report all the libraries that referred to this?
            }
        }

        for (r in expressions.values) {
            result.reportRequirement(r)
        }

        // FunctionDefs
        val functions: MutableMap<String?, ElmRequirement?> = LinkedHashMap()
        for (r in this.functionDefs) {
            val fd = r.element as FunctionDef
            val uri =
                String.format(
                    "%s%s.%s(%s)",
                    if (r.libraryIdentifier.system != null) r.libraryIdentifier.system + "."
                    else "",
                    r.libraryIdentifier.id,
                    fd.name,
                    getSignature(fd),
                )

            if (!functions.containsKey(uri)) {
                functions[uri] = r
                // TODO: Do we need to report all the libraries that referred to this?
            }
        }

        for (r in functions.values) {
            result.reportRequirement(r)
        }

        // Retrieves
        // Sort retrieves by type/profile to reduce search space
        val retrievesByType = LinkedHashMap<String?, MutableList<ElmRequirement?>?>()
        val unboundRequirements: MutableList<ElmRequirement?> = ArrayList()
        for (r in this.retrieves) {
            val retrieve = r.element as Retrieve
            if (retrieve.dataType != null) {
                val typeUri =
                    if (retrieve.templateId != null) retrieve.templateId
                    else retrieve.dataType!!.localPart
                var typeRetrieves: MutableList<ElmRequirement?>?
                if (retrievesByType.containsKey(typeUri)) {
                    typeRetrieves = retrievesByType.get(typeUri)
                } else {
                    typeRetrieves = ArrayList()
                    retrievesByType[typeUri] = typeRetrieves
                }
                typeRetrieves!!.add(r)
            } else {
                unboundRequirements.add(r)
            }
        }

        // Distribute unbound property requirements
        // If an ElmDataRequirement has a retrieve that does not have a dataType (i.e. it is not a
        // direct data access
        // layer retrieve
        // but rather is the result of requirements inference), then distribute the property
        // references it contains to
        // all data layer-bound retrieves of the same type
        // In other words, we can't unambiguously tie the property reference to any particular
        // retrieve of that type,
        // so apply it to all of them
        for (requirement in unboundRequirements) {
            if (requirement is ElmDataRequirement) {
                if (requirement.hasProperties()) {
                    val retrieveType = requirement.retrieve.resultType
                    val typeUri = context.typeResolver.getTypeUri(retrieveType)
                    if (typeUri != null) {
                        val typeRequirements = retrievesByType.get(typeUri)
                        if (typeRequirements != null) {
                            for (typeRequirement in typeRequirements) {
                                if (typeRequirement is ElmDataRequirement) {
                                    for (p in requirement.properties!!) {
                                        typeRequirement.addProperty(p)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Equivalent
        // Has the same context, type/profile, code path and date path
        // If two retrieves are "equivalent" they can be merged
        // TODO: code/date-range consolidation
        val requirementIdMap: MutableMap<String?, String?> = HashMap()
        for (entry in retrievesByType.entries) {
            // Determine unique set per type/profile
            val collapsedRetrieves = CollapsedElmRequirements()
            for (requirement in entry.value!!) {
                collapsedRetrieves.add(requirement!!)
            }

            // Collect target mappings
            for (idMapEntry in collapsedRetrieves.requirementIdMap.entries) {
                requirementIdMap[idMapEntry.key] = idMapEntry.value
            }

            for (r in collapsedRetrieves.getUniqueRequirements()) {
                result.reportRequirement(r)
            }
        }

        // Fixup references in the resulting requirements
        for (requirement in result.getRequirements()) {
            if (requirement.element is Retrieve) {
                val r = (requirement.element)
                if (r.includedIn != null) {
                    val mappedId = requirementIdMap[r.includedIn]
                    if (mappedId != null) {
                        r.includedIn = mappedId
                    }
                }

                for (includeElement in r.include) {
                    if (includeElement.includeFrom != null) {
                        val mappedId = requirementIdMap[includeElement.includeFrom]
                        if (mappedId != null) {
                            includeElement.includeFrom = mappedId
                        }
                    }
                }
            }
        }

        return result
    }

    private fun getSignature(fd: FunctionDef): String {
        val sb = StringBuilder()
        for (od in fd.operand) {
            if (sb.isNotEmpty()) {
                sb.append(",")
            }
            sb.append(getTypeName(od))
        }
        return sb.toString()
    }

    private fun getTypeName(od: OperandDef): String? {
        return if (od.operandType != null) {
            od.toString()
        } else if (od.operandTypeSpecifier != null) {
            od.operandTypeSpecifier.toString()
        } else {
            ""
        }
    }
}
