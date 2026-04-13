@file:OptIn(ExperimentalJsExport::class)

package org.cqframework.cql.cql2elm

import kotlin.js.ExperimentalJsExport
import kotlin.js.JsExport
import kotlin.jvm.JvmOverloads
import org.cqframework.cql.cql2elm.model.LibraryRef
import org.cqframework.cql.cql2elm.model.PropertyResolution
import org.cqframework.cql.cql2elm.model.invocation.FunctionRefInvocation
import org.cqframework.cql.cql2elm.tracking.Trackable.resultType
import org.cqframework.cql.elm.IdObjectFactory
import org.hl7.cql.model.ChoiceType
import org.hl7.cql.model.ClassType
import org.hl7.cql.model.ClassTypeElement
import org.hl7.cql.model.DataType
import org.hl7.cql.model.IntervalType
import org.hl7.cql.model.ListType
import org.hl7.cql.model.SearchType
import org.hl7.cql.model.TupleType
import org.hl7.cql.model.TupleTypeElement
import org.hl7.elm.r1.AliasRef
import org.hl7.elm.r1.AliasedQuerySource
import org.hl7.elm.r1.Case
import org.hl7.elm.r1.CaseItem
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.Flatten
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.NamedTypeSpecifier
import org.hl7.elm.r1.Property
import org.hl7.elm.r1.Query
import org.hl7.elm.r1.ReturnClause
import org.hl7.elm.r1.TypeSpecifier

internal const val FP_THIS = "\$this"

/**
 * Owns property-path resolution and member-access expression building, plus the target-map
 * expansion used by model-info-informed retrieval.
 *
 * The class holds a back-reference to [LibraryBuilder] for lookups that cross into the symbol table
 * (type resolution, model mapping, included-library management). Once the builder is split into
 * focused components the back-reference will be replaced by direct collaborators.
 */
@Suppress("LargeClass", "TooManyFunctions", "ReturnCount", "MaxLineLength")
class PropertyResolver(private val lb: LibraryBuilder, private val of: IdObjectFactory) {
    /**
     * Resolve a property accessor against [sourceType]. Walks the type's class hierarchy until a
     * matching element is found. Supports [ClassType], [TupleType], [IntervalType], [ChoiceType],
     * and (via FHIRPath path-traversal) [ListType].
     */
    @Suppress("LongMethod", "CyclomaticComplexMethod", "NestedBlockDepth", "ThrowsCount")
    @JvmOverloads
    fun resolveProperty(
        sourceType: DataType?,
        identifier: String,
        mustResolve: Boolean = true,
    ): PropertyResolution? {
        var currentType: DataType? = sourceType
        while (currentType != null) {
            when {
                currentType is ClassType -> {
                    val classType: ClassType = currentType
                    if (identifier.startsWith("?") && lb.isCompatibleWith("1.5")) {
                        val searchPath: String = identifier.substring(1)
                        for (s: SearchType in classType.getSearches()) {
                            if ((s.name == searchPath)) return PropertyResolution(s)
                        }
                    } else {
                        for (e: ClassTypeElement in classType.elements) {
                            if ((e.name == identifier)) {
                                require(!e.prohibited) {
                                    "Element ${e.name} cannot be referenced because it is marked prohibited in type ${currentType.name}."
                                }
                                return PropertyResolution(e)
                            }
                        }
                    }
                }
                currentType is TupleType -> {
                    for (e: TupleTypeElement in currentType.elements) {
                        if ((e.name == identifier)) return PropertyResolution(e)
                    }
                }
                currentType is IntervalType -> {
                    return when (identifier) {
                        "low",
                        "high" -> PropertyResolution(currentType.pointType, identifier)
                        "lowClosed",
                        "highClosed" ->
                            PropertyResolution(
                                (lb.resolveTypeName("System", "Boolean"))!!,
                                identifier,
                            )
                        else ->
                            throw IllegalArgumentException(
                                "Invalid interval property name $identifier."
                            )
                    }
                }
                currentType is ChoiceType -> {
                    val resultTypes: MutableSet<DataType> = HashSet()
                    val resultTargetMaps: MutableMap<DataType, String> = HashMap()
                    var name: String? = null
                    for (choice: DataType in currentType.types) {
                        val resolution: PropertyResolution? =
                            resolveProperty(choice, identifier, false)
                        if (resolution != null) {
                            resultTypes.add(resolution.type)
                            if (resolution.targetMap != null) {
                                if (resultTargetMaps.containsKey(resolution.type)) {
                                    require(
                                        resultTargetMaps[resolution.type] == resolution.targetMap
                                    ) {
                                        "Inconsistent target maps ${resultTargetMaps[resolution.type]} and ${resolution.targetMap} for choice type ${resolution.type}"
                                    }
                                } else {
                                    resultTargetMaps[resolution.type] = resolution.targetMap
                                }
                            }
                            if (name == null) {
                                name = resolution.name
                            } else
                                require(name == resolution.name) {
                                    "Inconsistent property resolution for choice type $choice (was $name, is ${resolution.name})"
                                }
                        }
                    }
                    if (resultTypes.size > 1) {
                        return PropertyResolution(ChoiceType(resultTypes), name!!, resultTargetMaps)
                    }
                    if (resultTypes.size == 1) {
                        return PropertyResolution(
                            resultTypes.iterator().next(),
                            name!!,
                            resultTargetMaps,
                        )
                    }
                }
                currentType is ListType && lb.listTraversal -> {
                    // FHIRPath path traversal support: resolve property as list-of-property.
                    val resolution: PropertyResolution? =
                        resolveProperty(currentType.elementType, identifier)
                    return PropertyResolution(ListType(resolution!!.type), (resolution.targetMap)!!)
                }
            }
            if (currentType.baseType != DataType.ANY) {
                currentType = currentType.baseType
            } else {
                break
            }
        }
        require(!mustResolve) { "Member $identifier not found for type ${sourceType?.toLabel()}." }
        return null
    }

    @JsExport.Ignore
    fun buildProperty(
        scope: String?,
        path: String?,
        isSearch: Boolean,
        resultType: DataType?,
    ): Property {
        val result =
            if (isSearch) of.createSearch().withScope(scope).withPath(path)
            else of.createProperty().withScope(scope).withPath(path)
        result.resultType = resultType
        return result
    }

    @JsExport.Ignore
    fun buildProperty(
        source: Expression?,
        path: String?,
        isSearch: Boolean,
        resultType: DataType?,
    ): Property {
        val result =
            if (isSearch) of.createSearch().withSource(source).withPath(path)
            else of.createProperty().withSource(source).withPath(path)
        result.resultType = resultType
        return result
    }

    /**
     * Build a member-access expression `left.memberIdentifier`. Dispatches on the kind of `left`:
     * - [LibraryRef]: resolve the member as a definition in the referenced library
     * - [AliasRef]: build a [Property] referenced by the alias's scope
     * - `left.resultType` is a [ListType]: build a flatten-style [Query] (FHIRPath path traversal)
     * - otherwise: build a [Property] on the left expression
     */
    @Suppress("LongMethod", "NestedBlockDepth")
    fun resolveAccessor(left: Expression, memberIdentifier: String): Expression? {
        when {
            left is LibraryRef -> return lb.resolveLibraryMemberAccessor(left, memberIdentifier)
            left is AliasRef -> {
                val resolution = resolveProperty(left.resultType, memberIdentifier)
                val result =
                    buildProperty(
                        left.name,
                        resolution!!.name,
                        resolution.isSearch,
                        resolution.type,
                    )
                return applyTargetMap(result, resolution.targetMap)
            }
            left.resultType is ListType && lb.listTraversal -> {
                val listType: ListType = left.resultType as ListType
                val resolution = resolveProperty(listType.elementType, memberIdentifier)
                var accessor: Expression? =
                    buildProperty(
                        of.createAliasRef().withName(FP_THIS),
                        resolution!!.name,
                        resolution.isSearch,
                        resolution.type,
                    )
                accessor = applyTargetMap(accessor, resolution.targetMap)
                val not: Expression = lb.buildIsNotNull(accessor)

                // Recreate property — it needs to be accessed twice.
                accessor =
                    buildProperty(
                        of.createAliasRef().withName(FP_THIS),
                        resolution.name,
                        resolution.isSearch,
                        resolution.type,
                    )
                accessor = applyTargetMap(accessor, resolution.targetMap)
                val source: AliasedQuerySource =
                    of.createAliasedQuerySource().withExpression(left).withAlias(FP_THIS)
                source.resultType = left.resultType
                val query: Query =
                    of.createQuery()
                        .withSource(listOf(source))
                        .withWhere(not)
                        .withReturn(
                            of.createReturnClause().withDistinct(false).withExpression(accessor)
                        )
                query.resultType = ListType(accessor!!.resultType!!)
                if (accessor.resultType is ListType) {
                    val result: Flatten = of.createFlatten().withOperand(query)
                    result.resultType = accessor.resultType
                    return result
                }
                return query
            }
            else -> {
                val resolution = resolveProperty(left.resultType, memberIdentifier)
                var result: Expression? =
                    buildProperty(left, resolution!!.name, resolution.isSearch, resolution.type)
                result = applyTargetMap(result, resolution.targetMap)
                return result
            }
        }
    }

    /**
     * Expand a model-info target-map against [source]. Handles the four supported targetMap
     * syntaxes: type-cased maps (`T1:map1;T2:map2`), function-wrapped maps
     * (`qualified.function(%value)`), indexed property paths (`path[key=value].path`), and plain
     * property access (`%value.property`).
     */
    @Suppress("LongMethod", "CyclomaticComplexMethod", "NestedBlockDepth", "ThrowsCount")
    fun applyTargetMap(source: Expression?, targetMap: String?): Expression? {
        var targetMap: String? = targetMap
        if (targetMap == null || (targetMap == "null")) return source

        // Remove "choice" paths; the targetMap grammar never needs them explicit.
        targetMap = targetMap.replace("[x]", "")

        if (targetMap.contains(";")) return applyTypeCaseTargetMap(source, targetMap)
        if (targetMap.contains("(")) return applyFunctionTargetMap(source, targetMap)
        if (targetMap.contains("[")) return applyIndexerTargetMap(source, targetMap)
        if (targetMap.startsWith("%value.")) return applyValueTargetMap(source, targetMap)

        throw IllegalArgumentException("TargetMapping not implemented: $targetMap")
    }

    private fun applyTypeCaseTargetMap(source: Expression?, targetMap: String): Expression? {
        val typeCases = targetMap.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val c: Case = of.createCase()
        for (typeCase in typeCases) {
            if (typeCase.isNotEmpty()) {
                val splitIndex = typeCase.indexOf(':')
                require(splitIndex > 0) { "Malformed type case in targetMap $targetMap" }
                val typeCaseElement = typeCase.substring(0, splitIndex)
                val typeCaseType = lb.resolveTypeName(typeCaseElement)
                val typeCaseMap = typeCase.substring(splitIndex + 1)
                val ci: CaseItem =
                    of.createCaseItem()
                        .withWhen(
                            of.createIs()
                                .withOperand(applyTargetMap(source, typeCaseMap))
                                .withIsType(lb.dataTypeToQName(typeCaseType))
                        )
                        .withThen(applyTargetMap(source, typeCaseMap))
                ci.then!!.resultType = typeCaseType
                c.caseItem.add(ci)
            }
        }
        return when (c.caseItem.size) {
            0 -> lb.buildNull(source!!.resultType)
            1 -> c.caseItem[0].then
            else -> {
                c.`else` = (lb.buildNull(source!!.resultType))
                c.resultType = source.resultType
                c
            }
        }
    }

    @Suppress("LongMethod")
    private fun applyFunctionTargetMap(source: Expression?, targetMap: String): Expression? {
        val invocationStart = targetMap.indexOf("(")
        val qualifiedFunctionName = targetMap.substring(0, invocationStart)
        val nameParts =
            qualifiedFunctionName
                .split("\\.".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()
        var libraryName: String? = null
        var functionName = qualifiedFunctionName
        if (nameParts.size == 2) {
            libraryName = nameParts[0]
            functionName = nameParts[1]
            lb.ensureLibraryIncluded(libraryName, source)
        }
        val functionArgument = targetMap.substring(invocationStart + 1, targetMap.lastIndexOf(')'))
        val argumentSource =
            if (functionArgument == "%value") source else applyTargetMap(source, functionArgument)

        // FHIRHelpers.ToInterval special case: it has multiple overloads whose target mappings
        // don't carry the source type, so we force the FHIR model to load and fix the signature
        // to FHIR.Period (the only real argument type across overloads).
        var argumentSignature: TypeSpecifier? = null
        if (
            lb.libraryManager.cqlCompilerOptions.signatureLevel !=
                LibraryBuilder.SignatureLevel.None &&
                qualifiedFunctionName == "FHIRHelpers.ToInterval"
        ) {
            var fhirVersion = "4.0.1"
            val qiCoreModel = lb.getModel("QICore")
            val version = qiCoreModel.modelInfo.version
            if (version == "3.3.0") fhirVersion = "4.0.0"
            else if (version!!.startsWith("3")) fhirVersion = "3.0.1"
            lb.libraryManager.modelManager.resolveModel("FHIR", fhirVersion)
            argumentSignature =
                NamedTypeSpecifier()
                    .withName(lb.dataTypeToQName(lb.resolveTypeName("FHIR", "Period")))
        }

        if (argumentSource!!.resultType is ListType) {
            val query: Query =
                of.createQuery()
                    .withSource(
                        listOf(
                            of.createAliasedQuerySource()
                                .withExpression(argumentSource)
                                .withAlias(FP_THIS)
                        )
                    )
            val fr: FunctionRef =
                of.createFunctionRef()
                    .withLibraryName(libraryName)
                    .withName(functionName)
                    .withOperand(listOf(of.createAliasRef().withName(FP_THIS)))
            if (argumentSignature != null) fr.signature.add(argumentSignature)
            query.`return` = of.createReturnClause().withDistinct(false).withExpression(fr)
            query.resultType = source!!.resultType
            return query
        } else {
            val fr: FunctionRef =
                of.createFunctionRef()
                    .withLibraryName(libraryName)
                    .withName(functionName)
                    .withOperand(listOf(argumentSource))
            fr.resultType = source!!.resultType
            if (argumentSignature != null) fr.signature.add(argumentSignature)
            return fr
        }
    }

    @Suppress("LongMethod", "CyclomaticComplexMethod", "NestedBlockDepth")
    private fun applyIndexerTargetMap(source: Expression?, targetMap: String): Expression? {
        val indexerStart = targetMap.indexOf("[")
        val indexerEnd = targetMap.indexOf("]")
        val indexer = targetMap.substring(indexerStart + 1, indexerEnd)
        val indexerPath = targetMap.substring(0, indexerStart)
        var result: Expression? = null

        // Walk the path up to the indexer.
        val indexerPaths =
            indexerPath.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (path in indexerPaths) {
            if (path == "%parent") {
                require(source is Property) {
                    "Cannot expand target map $targetMap for non-property-accessor type ${source!!::class.simpleName}"
                }
                result =
                    source.source
                        ?: source.scope?.let { lb.resolveIdentifier(source.scope!!, true) }
                requireNotNull(result) {
                    "Cannot resolve %%parent reference in targetMap $targetMap"
                }
            } else {
                val p: Property = of.createProperty().withSource(result).withPath(path)
                result = p
            }
        }

        val querySource: AliasedQuerySource =
            of.createAliasedQuerySource().withExpression(result).withAlias(FP_THIS)
        var criteria: Expression? = null
        for (indexerItem in
            indexer.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            val indexerItems =
                indexerItem.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            require(indexerItems.size == 2) {
                "Invalid indexer item $indexerItem in targetMap $targetMap"
            }
            var left: Expression? = null
            for (path in
                indexerItems[0]
                    .split("\\.".toRegex())
                    .dropLastWhile { it.isEmpty() }
                    .toTypedArray()) {
                left =
                    if (left == null) of.createProperty().withScope(FP_THIS).withPath(path)
                    else of.createProperty().withSource(left).withPath(path)

                // HACK: FHIR model resolution workaround — model-info expansion lacks type info.
                if (path == "coding") {
                    val first = of.createFirst().withSource(left)
                    first.resultType = lb.getModel("FHIR").resolveTypeName("FHIR.coding")
                    left = first
                }
                if (path == "url") {
                    left!!.resultType = lb.getModel("FHIR").resolveTypeName("FHIR.uri")
                    val ref: FunctionRef =
                        of.createFunctionRef()
                            .withLibraryName("FHIRHelpers")
                            .withName("ToString")
                            .withOperand(listOf(left!!))
                    left =
                        lb.resolveCall(
                            ref.libraryName,
                            ref.name!!,
                            FunctionRefInvocation(ref),
                            allowPromotionAndDemotion = false,
                            allowFluent = false,
                        )
                }
            }

            // Another FHIR model-resolution HACK: type-lose path fixups.
            if (indexerItems[0] == "code.coding.system") {
                left!!.resultType = lb.getModel("FHIR").resolveTypeName("FHIR.uri")
                val ref =
                    of.createFunctionRef()
                        .withLibraryName("FHIRHelpers")
                        .withName("ToString")
                        .withOperand(listOf(left))
                left =
                    lb.resolveCall(
                        ref.libraryName,
                        ref.name!!,
                        FunctionRefInvocation(ref),
                        allowPromotionAndDemotion = false,
                        allowFluent = false,
                    )
            }
            if (indexerItems[0] == "code.coding.code") {
                left!!.resultType = lb.getModel("FHIR").resolveTypeName("FHIR.code")
                val ref: FunctionRef =
                    of.createFunctionRef()
                        .withLibraryName("FHIRHelpers")
                        .withName("ToString")
                        .withOperand(listOf(left))
                left =
                    lb.resolveCall(
                        ref.libraryName,
                        ref.name!!,
                        FunctionRefInvocation(ref),
                        allowPromotionAndDemotion = false,
                        allowFluent = false,
                    )
            }
            val rightValue = indexerItems[1].substring(1, indexerItems[1].length - 1)
            val right: Expression = lb.createLiteral(StringEscapeUtils.unescapeCql(rightValue))
            val criteriaItem: Expression = of.createEqual().withOperand(listOf(left!!, right))
            criteria =
                if (criteria == null) criteriaItem
                else of.createAnd().withOperand(listOf(criteria, criteriaItem))
        }
        val query: Query = of.createQuery().withSource(listOf(querySource)).withWhere(criteria)
        result = query
        if (indexerEnd + 1 < targetMap.length) {
            var targetPath = targetMap.substring(indexerEnd + 1)
            if (targetPath.startsWith(".")) targetPath = targetPath.substring(1)
            if (targetPath.isNotEmpty()) {
                query.`return` =
                    of.createReturnClause()
                        .withDistinct(false)
                        .withExpression(
                            of.createProperty()
                                .withSource(of.createAliasRef().withName(FP_THIS))
                                .withPath(targetPath)
                        )
            }
        }
        if (source!!.resultType !is ListType) {
            result = of.createSingletonFrom().withOperand(result)
        }
        result.resultType = source.resultType
        return result
    }

    private fun applyValueTargetMap(source: Expression?, targetMap: String): Expression {
        val propertyName = targetMap.substring(@Suppress("MagicNumber") 7)
        // A list source applies the mapping element-wise via a query.
        if (source!!.resultType is ListType) {
            val s: AliasedQuerySource =
                of.createAliasedQuerySource().withExpression(source).withAlias(FP_THIS)
            val p: Property = of.createProperty().withScope(FP_THIS).withPath(propertyName)
            p.resultType = (source.resultType as ListType).elementType
            val r: ReturnClause = of.createReturnClause().withDistinct(false).withExpression(p)
            val q: Query = of.createQuery().withSource(listOf(s)).withReturn(r)
            q.resultType = source.resultType
            return q
        }
        val p: Property = of.createProperty().withSource(source).withPath(propertyName)
        p.resultType = source.resultType
        return p
    }
}
