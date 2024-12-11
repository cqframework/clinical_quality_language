@file:Suppress("WildcardImport")

package org.cqframework.cql.cql2elm

import java.util.*
import org.cqframework.cql.cql2elm.model.QueryContext
import org.cqframework.cql.gen.cqlParser
import org.hl7.cql.model.*
import org.hl7.elm.r1.*

/** Created by Bryn on 12/27/2016. */
@Suppress("TooManyFunctions")
class SystemMethodResolver(
    private val visitor: Cql2ElmVisitor,
    private val builder: LibraryBuilder
) {
    private val of = builder.objectFactory

    private fun getParams(
        target: Expression,
        ctx: cqlParser.ParamListContext?
    ): MutableList<Expression?> {
        val params: MutableList<Expression?> = ArrayList()
        params.add(target)
        if (ctx?.expression() != null) {
            for (param in ctx.expression()) {
                params.add(visitor.visit(param) as Expression?)
            }
        }
        return params
    }

    private fun checkArgumentCount(
        ctx: cqlParser.ParamListContext?,
        functionName: String,
        expectedCount: Int
    ) {
        var actualCount = 0
        if (ctx?.expression() != null) {
            actualCount = ctx.expression().size
        }
        require(actualCount == expectedCount) {
            String.format(
                Locale.US,
                "Expected %s argument for method %s.",
                Integer.valueOf(expectedCount).toString(),
                functionName
            )
        }
    }

    private fun enterQueryContext(target: Expression): AliasedQuerySource {
        val queryContext = QueryContext()
        queryContext.isImplicit = true
        // Set to an implicit context to allow for implicit resolution of property names
        val sources: MutableList<AliasedQuerySource> = ArrayList()
        val source = of.createAliasedQuerySource().withExpression(target).withAlias("\$this")
        source.resultType = target.resultType
        sources.add(source)
        queryContext.addPrimaryQuerySources(sources)
        builder.pushQueryContext(queryContext)
        return source
    }

    private fun createQuery(
        source: AliasedQuerySource,
        let: LetClause?,
        where: Expression?,
        ret: ReturnClause?
    ): Query {
        val queryContext = builder.peekQueryContext()
        var lets: MutableCollection<LetClause?>? = null
        if (let != null) {
            lets = ArrayList()
            lets.add(let)
        }
        val query =
            of.createQuery()
                .withSource(queryContext.querySources)
                .withLet(lets)
                .withWhere(where)
                .withReturn(ret)
        if (ret != null) {
            query.resultType = ret.resultType
        } else {
            query.resultType = source.resultType
        }
        return query
    }

    private fun exitQueryContext() {
        builder.popQueryContext()
    }

    private fun createWhere(
        target: Expression,
        functionName: String,
        ctx: cqlParser.ParamListContext?
    ): Query {
        val source = enterQueryContext(target)
        return try {
            checkArgumentCount(ctx, functionName, 1)
            var where = visitor.visit(ctx!!.expression(0)!!) as Expression?
            if (visitor.dateRangeOptimization) {
                where = visitor.optimizeDateRangeInQuery(where, source)
            }
            createQuery(source, null, where, null)
        } finally {
            exitQueryContext()
        }
    }

    // X.ofType(T) === X $this where $this is T
    private fun createOfType(
        target: Expression,
        functionName: String,
        ctx: cqlParser.ParamListContext?
    ): Expression {
        val source = enterQueryContext(target)
        return try {
            checkArgumentCount(ctx, functionName, 1)
            builder.pushTypeSpecifierContext()
            val typeArgument: Expression? =
                try {
                    visitor.visit(ctx!!.expression(0)!!) as Expression?
                } finally {
                    builder.popTypeSpecifierContext()
                }
            require(typeArgument is Literal) { "Expected literal argument" }
            require(
                DataTypes.equal(
                    typeArgument.resultType,
                    builder.resolveTypeName("System", "String")
                )
            ) {
                "Expected string literal argument"
            }
            val typeSpecifier = typeArgument.value
            val isType = builder.resolveTypeSpecifier(typeSpecifier)
            val thisRef = of.createAliasRef().withName(source.alias)
            val isSingular = source.resultType !is ListType
            val elementType =
                if (isSingular) source.resultType else (source.resultType as ListType).elementType
            thisRef.resultType = elementType
            val isExpression = of.createIs().withOperand(thisRef)
            if (isType is NamedType) {
                isExpression.isType = builder.dataTypeToQName(isType)
            } else {
                isExpression.isTypeSpecifier = builder.dataTypeToTypeSpecifier(isType)
            }
            isExpression.resultType = builder.resolveTypeName("System", "Boolean")
            createQuery(source, null, isExpression, null)
        } finally {
            exitQueryContext()
        }
    }

    private fun createRepeat(
        target: Expression,
        functionName: String,
        ctx: cqlParser.ParamListContext?
    ): Expression {
        val source = enterQueryContext(target)
        return try {
            val isSingular = source.resultType !is ListType
            checkArgumentCount(ctx, functionName, 1)
            val select = visitor.visit(ctx!!.expression(0)!!) as Expression?
            val repeat = of.createRepeat()
            repeat.source = target
            repeat.element = select
            repeat.scope = "\$this"
            @Suppress("ForbiddenComment")
            // TODO: This isn't quite right, it glosses over the fact that the type of the result
            // may include the result
            // of invoking the element expression on intermediate results
            if (isSingular) {
                repeat.resultType = ListType(select!!.resultType!!)
            } else {
                repeat.resultType = select!!.resultType
            }
            repeat
        } finally {
            exitQueryContext()
        }
    }

    private fun createSelect(
        target: Expression,
        functionName: String,
        ctx: cqlParser.ParamListContext?
    ): Expression? {
        val isListResult: Boolean
        val isSingular: Boolean
        val source = enterQueryContext(target)
        return try {
            isSingular = source.resultType !is ListType
            checkArgumentCount(ctx, functionName, 1)
            val select = visitor.visit(ctx!!.expression(0)!!) as Expression?
            val queryContext = builder.peekQueryContext()
            val let = of.createLetClause().withExpression(select).withIdentifier("\$a")
            let.resultType = select!!.resultType
            queryContext.addLetClause(let)
            isListResult = select.resultType is ListType
            var letRef = of.createQueryLetRef().withName("\$a")
            letRef.resultType = select.resultType
            var params: MutableList<Expression?> = ArrayList()
            params.add(letRef)
            var where = builder.resolveFunction(null, "IsNull", params)
            params = ArrayList()
            params.add(where)
            where = builder.resolveFunction(null, "Not", params)
            val returnClause = of.createReturnClause()
            letRef = of.createQueryLetRef().withName("\$a")
            letRef.resultType = select.resultType
            returnClause.expression = letRef
            returnClause.resultType =
                if (isSingular) letRef.resultType else ListType(letRef.resultType!!)
            val query = createQuery(source, let, where, returnClause)
            if (!isSingular && isListResult) {
                params = ArrayList()
                params.add(query)
                builder.resolveFunction(null, "Flatten", params)
            } else {
                query
            }
        } finally {
            exitQueryContext()
        }
    }

    private fun gatherChildTypes(
        dataType: DataType,
        recurse: Boolean,
        dataTypes: MutableSet<DataType>
    ) {
        if (dataType is ClassType) {
            for (element in dataType.elements) {
                val elementType =
                    if (element.type is ListType) (element.type as ListType).elementType
                    else element.type
                dataTypes.add(elementType)
                if (recurse) {
                    gatherChildTypes(elementType, recurse, dataTypes)
                }
            }
        } else if (dataType is TupleType) {
            for (element in dataType.elements) {
                val elementType =
                    if (element.type is ListType) (element.type as ListType).elementType
                    else element.type
                dataTypes.add(elementType)
                if (recurse) {
                    gatherChildTypes(elementType, recurse, dataTypes)
                }
            }
        } else if (dataType is ListType) {
            val elementType = dataType.elementType
            dataTypes.add(elementType)
            if (recurse) {
                gatherChildTypes(elementType, recurse, dataTypes)
            }
        } else {
            dataTypes.add(builder.resolveTypeName("System.Any")!!)
        }
    }

    @Suppress("LongMethod", "CyclomaticComplexMethod")
    fun resolveMethod(
        target: Expression,
        functionName: String,
        ctx: cqlParser.ParamListContext?,
        mustResolve: Boolean
    ): Expression? {
        return when (functionName) {
            "aggregate" -> builder.resolveFunction(null, "Aggregate", getParams(target, ctx))
            "abs" -> builder.resolveFunction(null, "Abs", getParams(target, ctx))
            "all" -> {

                // .all(criteria) resolves as .where(criteria).select(true).allTrue()
                val query = createWhere(target, functionName, ctx)
                val returnClause = of.createReturnClause()
                returnClause.expression = builder.createLiteral(java.lang.Boolean.valueOf(true))
                if (query.resultType is ListType) {
                    returnClause.resultType = ListType(returnClause.expression.resultType!!)
                } else {
                    returnClause.resultType = returnClause.expression.resultType
                }
                query.setReturn(returnClause)
                query.resultType = returnClause.resultType
                val params: MutableList<Expression?> = ArrayList()
                params.add(query)
                builder.resolveFunction(null, "AllTrue", params)
            }
            "allTrue" -> builder.resolveFunction(null, "AllTrue", getParams(target, ctx))
            "anyTrue" -> builder.resolveFunction(null, "AnyTrue", getParams(target, ctx))
            "allFalse" -> builder.resolveFunction(null, "AllFalse", getParams(target, ctx))
            "anyFalse" -> builder.resolveFunction(null, "AnyFalse", getParams(target, ctx))
            "ceiling" -> builder.resolveFunction(null, "Ceiling", getParams(target, ctx))
            "children" -> {
                checkArgumentCount(ctx, functionName, 0)
                val children = of.createChildren()
                children.source = target
                val dataTypes: MutableSet<DataType> = HashSet()
                gatherChildTypes(target.resultType!!, false, dataTypes)
                if (dataTypes.size == 1) {
                    children.resultType = ListType(dataTypes.iterator().next())
                } else {
                    children.resultType = ListType(ChoiceType(dataTypes))
                }
                children
            }
            "combine" -> {
                checkArgumentCount(ctx, functionName, 1)
                val elements: MutableList<Expression?> = ArrayList()
                val argument = visitor.visit(ctx!!.expression(0)!!) as Expression?
                elements.add(target)
                elements.add(argument)
                val elementType =
                    builder.ensureCompatibleTypes(target.resultType, argument!!.resultType!!)!!
                val list = of.createList()
                list.resultType = ListType(elementType)
                list.element.add(builder.ensureCompatible(target, elementType))
                list.element.add(builder.ensureCompatible(argument, elementType))
                val params = ArrayList<Expression?>()
                params.add(list)
                builder.resolveFunction(null, "Flatten", params)
            }
            "contains" -> {
                checkArgumentCount(ctx, functionName, 1)
                var params: MutableList<Expression?> = ArrayList()
                val argument = visitor.visit(ctx!!.expression(0)!!) as Expression?
                params.add(argument)
                params.add(target)
                val result = builder.resolveFunction(null, "PositionOf", params)
                params = ArrayList()
                params.add(result)
                params.add(builder.createLiteral(0))
                builder.resolveFunction(null, "GreaterOrEqual", params)
            }
            "convertsToBoolean" ->
                builder.resolveFunction(null, "ConvertsToBoolean", getParams(target, ctx))
            "convertsToDate" ->
                builder.resolveFunction(null, "ConvertsToDate", getParams(target, ctx))
            "convertsToDateTime" ->
                builder.resolveFunction(null, "ConvertsToDateTime", getParams(target, ctx))
            "convertsToDecimal" ->
                builder.resolveFunction(null, "ConvertsToDecimal", getParams(target, ctx))
            "convertsToInteger" ->
                builder.resolveFunction(null, "ConvertsToInteger", getParams(target, ctx))
            "convertsToQuantity" ->
                builder.resolveFunction(null, "ConvertsToQuantity", getParams(target, ctx))
            "convertsToString" ->
                builder.resolveFunction(null, "ConvertsToString", getParams(target, ctx))
            "convertsToTime" ->
                builder.resolveFunction(null, "ConvertsToTime", getParams(target, ctx))
            "count" -> builder.resolveFunction(null, "Count", getParams(target, ctx))
            "descendents" -> {
                checkArgumentCount(ctx, functionName, 0)
                val descendents = of.createDescendents()
                descendents.source = target
                val dataTypes: MutableSet<DataType> = HashSet()
                gatherChildTypes(target.resultType!!, true, dataTypes)
                if (dataTypes.size == 1) {
                    descendents.resultType = ListType(dataTypes.toTypedArray()[0])
                } else {
                    descendents.resultType = ListType(ChoiceType(dataTypes))
                }
                descendents
            }
            "distinct" -> builder.resolveFunction(null, "Distinct", getParams(target, ctx))
            "empty" -> {
                var params = getParams(target, ctx)
                val exists = builder.resolveFunction(null, "Exists", params)
                params = ArrayList()
                params.add(exists)
                builder.resolveFunction(null, "Not", params)
            }
            "endsWith" -> builder.resolveFunction(null, "EndsWith", getParams(target, ctx))
            "exclude" -> builder.resolveFunction(null, "Except", getParams(target, ctx))
            "exists" -> {
                if (ctx?.expression() == null || ctx.expression().isEmpty()) {
                    val params: List<Expression?> = getParams(target, ctx)
                    builder.resolveFunction(null, "Exists", params)
                } else {
                    // .exists(criteria) resolves as a .where(criteria).exists()
                    val query = createWhere(target, functionName, ctx)
                    val params: MutableList<Expression?> = ArrayList()
                    params.add(query)
                    builder.resolveFunction(null, "Exists", params)
                }
            }
            "exp" -> builder.resolveFunction(null, "Exp", getParams(target, ctx))
            "first" -> builder.resolveFunction(null, "First", getParams(target, ctx))
            "floor" -> builder.resolveFunction(null, "Floor", getParams(target, ctx))
            "hasValue" -> {
                var params = getParams(target, ctx)
                val isNull = builder.resolveFunction(null, "IsNull", params)
                params = ArrayList()
                params.add(isNull)
                builder.resolveFunction(null, "Not", params)
            }
            "iif" -> {
                var result: Expression? = target
                val params: MutableList<Expression?>?
                if (result!!.resultType is ListType) {
                    params = ArrayList()
                    params.add(result)
                    result = builder.resolveFunction(null, "SingletonFrom", params)
                }
                val thenExpression = visitor.visit(ctx!!.expression(0)!!) as Expression?
                val elseExpression =
                    if (ctx.expression().size == 2)
                        visitor.visit(ctx.expression(1)!!) as Expression?
                    else of.createNull()
                result =
                    of.createIf()
                        .withCondition(result)
                        .withThen(thenExpression)
                        .withElse(elseExpression)
                visitor.resolveIfThenElse(result)
            }
            "indexOf" -> {
                checkArgumentCount(ctx, functionName, 1)
                val params: MutableList<Expression?> = ArrayList()
                val argument = visitor.visit(ctx!!.expression(0)!!) as Expression?
                params.add(argument)
                params.add(target)
                builder.resolveFunction(null, "PositionOf", params)
            }
            "intersect" -> builder.resolveFunction(null, "Intersect", getParams(target, ctx))
            "is",
            "as" -> {
                checkArgumentCount(ctx, functionName, 1)
                builder.pushTypeSpecifierContext()
                val typeArgument: Expression? =
                    try {
                        visitor.visit(ctx!!.expression(0)!!) as Expression?
                    } finally {
                        builder.popTypeSpecifierContext()
                    }
                require(typeArgument is Literal) { "Expected literal argument" }
                require(
                    DataTypes.equal(
                        typeArgument.resultType,
                        builder.resolveTypeName("System", "String")
                    )
                ) {
                    "Expected string literal argument"
                }
                val typeSpecifier = typeArgument.value
                val isType = builder.resolveTypeSpecifier(typeSpecifier)
                if (functionName == "is") builder.buildIs(target, isType)
                else builder.buildAs(target, isType)
            }
            "last" -> builder.resolveFunction(null, "Last", getParams(target, ctx))
            "lastIndexOf" -> {
                checkArgumentCount(ctx, functionName, 1)
                val params: MutableList<Expression?> = ArrayList()
                val argument = visitor.visit(ctx!!.expression(0)!!) as Expression?
                params.add(argument)
                params.add(target)
                builder.resolveFunction(null, "LastPositionOf", params)
            }
            "length" -> builder.resolveFunction(null, "Length", getParams(target, ctx))
            "ln" -> builder.resolveFunction(null, "Ln", getParams(target, ctx))
            "log" -> builder.resolveFunction(null, "Log", getParams(target, ctx))
            "lower" -> builder.resolveFunction(null, "Lower", getParams(target, ctx))
            "matches" -> builder.resolveFunction(null, "Matches", getParams(target, ctx))
            "memberOf" -> builder.resolveFunction(null, "InValueSet", getParams(target, ctx))
            "not" -> builder.resolveFunction(null, "Not", getParams(target, ctx))
            "ofType" -> createOfType(target, functionName, ctx)
            "power" -> builder.resolveFunction(null, "Power", getParams(target, ctx))
            "repeat" -> createRepeat(target, functionName, ctx)
            "replace" -> builder.resolveFunction(null, "Replace", getParams(target, ctx))
            "replaceMatches" ->
                builder.resolveFunction(null, "ReplaceMatches", getParams(target, ctx))
            "round" -> builder.resolveFunction(null, "Round", getParams(target, ctx))
            "select" -> {
                createSelect(target, functionName, ctx)
            }
            "single" -> builder.resolveFunction(null, "SingletonFrom", getParams(target, ctx))
            "skip" -> builder.resolveFunction(null, "Skip", getParams(target, ctx))
            "sqrt" -> {
                checkArgumentCount(ctx, functionName, 0)
                val params: MutableList<Expression?> = ArrayList()
                params.add(target)
                params.add(builder.createLiteral(@Suppress("MagicNumber") 0.5))
                builder.resolveFunction(null, "Power", params)
            }
            "startsWith" -> builder.resolveFunction(null, "StartsWith", getParams(target, ctx))
            "subsetOf" -> builder.resolveFunction(null, "IncludedIn", getParams(target, ctx))
            "substring" -> builder.resolveFunction(null, "Substring", getParams(target, ctx))
            "subsumes" -> builder.resolveFunction(null, "Subsumes", getParams(target, ctx))
            "subsumedBy" -> builder.resolveFunction(null, "SubsumedBy", getParams(target, ctx))
            "supersetOf" -> builder.resolveFunction(null, "Includes", getParams(target, ctx))
            "tail" -> builder.resolveFunction(null, "Tail", getParams(target, ctx))
            "take" -> builder.resolveFunction(null, "Take", getParams(target, ctx))
            "toBoolean" -> builder.resolveFunction(null, "ToBoolean", getParams(target, ctx))
            "toChars" -> builder.resolveFunction(null, "ToChars", getParams(target, ctx))
            "toDate" -> builder.resolveFunction(null, "ToDate", getParams(target, ctx))
            "toDateTime" -> builder.resolveFunction(null, "ToDateTime", getParams(target, ctx))
            "toDecimal" -> builder.resolveFunction(null, "ToDecimal", getParams(target, ctx))
            "toInteger" -> builder.resolveFunction(null, "ToInteger", getParams(target, ctx))
            "toQuantity" -> builder.resolveFunction(null, "ToQuantity", getParams(target, ctx))
            "toString" -> builder.resolveFunction(null, "ToString", getParams(target, ctx))
            "toTime" -> builder.resolveFunction(null, "ToTime", getParams(target, ctx))
            "trace" -> {
                checkArgumentCount(ctx, functionName, 1)
                val params: MutableList<Expression?> = ArrayList()
                params.add(target)
                params.add(builder.createLiteral(true))
                params.add(builder.createLiteral("TRACE"))
                params.add(builder.createLiteral("Trace"))
                params.add(visitor.visit(ctx!!.expression(0)!!) as Expression?)
                builder.resolveFunction(null, "Message", params)
            }
            "truncate" -> builder.resolveFunction(null, "Truncate", getParams(target, ctx))
            "union" -> builder.resolveFunction(null, "Union", getParams(target, ctx))
            "upper" -> builder.resolveFunction(null, "Upper", getParams(target, ctx))
            "where" -> {
                createWhere(target, functionName, ctx)
            }
            else -> {
                visitor.resolveFunction(
                    null,
                    functionName,
                    getParams(target, ctx),
                    mustResolve,
                    false,
                    true
                )
            }
        }
    }
}
