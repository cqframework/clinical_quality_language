package org.cqframework.cql.cql2elm;

import org.antlr.v4.runtime.misc.NotNull;
import org.cqframework.cql.cql2elm.model.QueryContext;
import org.cqframework.cql.gen.cqlParser;
import org.hl7.cql.model.ListType;
import org.hl7.elm.r1.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;




/**
 * Created by Bryn on 12/27/2016.
 */
public class SystemMethodResolver {
    private final ObjectFactory of = new ObjectFactory();
    private final Cql2ElmVisitor visitor;
    private final LibraryBuilder builder;

    public SystemMethodResolver(Cql2ElmVisitor visitor, LibraryBuilder builder) {
        if (visitor == null) {
            throw new IllegalArgumentException("visitor is null");
        }

        if (builder == null) {
            throw new IllegalArgumentException("builder is null");
        }

        this.visitor = visitor;
        this.builder = builder;
    }

    private List<Expression> getParams(Expression target, @NotNull cqlParser.FunctionContext ctx) {
        List<Expression> params = new ArrayList<Expression>();
        params.add(target);
        if (ctx.paramList() != null && ctx.paramList().expression() != null) {
            for (cqlParser.ExpressionContext param : ctx.paramList().expression()) {
                params.add((Expression)visitor.visit(param));
            }
        }

        return params;
    }

    private void checkArgumentCount(@NotNull cqlParser.FunctionContext ctx, String functionName, int expectedCount) {
        int actualCount = 0;
        if (ctx.paramList() != null && ctx.paramList().expression() != null) {
            actualCount = ctx.paramList().expression().size();
        }
        if (actualCount != expectedCount) {
            throw new IllegalArgumentException(String.format("Expected %s argument for method %s.",
                    Integer.valueOf(expectedCount).toString(), functionName));
        }
    }

    private AliasedQuerySource enterQueryContext(Expression target) {
        QueryContext queryContext = new QueryContext();
        queryContext.setIsImplicit(true); // Set to an implicit context to allow for implicit resolution of property names
        List<AliasedQuerySource> sources = new ArrayList<>();
        AliasedQuerySource source = of.createAliasedQuerySource().withExpression(target).withAlias("$this");
        source.setResultType(target.getResultType());
        sources.add(source);
        queryContext.addQuerySources(sources);
        builder.pushQueryContext(queryContext);
        return source;
    }

    private Query createQuery(AliasedQuerySource source, LetClause let, Expression where, ReturnClause ret) {
        QueryContext queryContext = builder.peekQueryContext();
        Collection<LetClause> lets = null;
        if (let != null) {
            lets = new ArrayList<>();
            lets.add(let);
        }

        Query query = of.createQuery()
                .withSource(queryContext.getQuerySources())
                .withLet(lets)
                .withWhere(where)
                .withReturn(ret);

        if (ret != null) {
            query.setResultType(ret.getResultType());
        }
        else {
            query.setResultType(source.getResultType());
        }

        return query;
    }

    private void exitQueryContext() {
        builder.popQueryContext();
    }

    private Query createWhere(Expression target, String functionName, @NotNull cqlParser.FunctionContext ctx) {
        AliasedQuerySource source = enterQueryContext(target);
        try {

            checkArgumentCount(ctx, functionName, 1);
            Expression where = (Expression)visitor.visit(ctx.paramList().expression(0));
            if (visitor.getDateRangeOptimization()) {
                where = visitor.optimizeDateRangeInQuery(where, source);
            }

            return createQuery(source, null, where, null);
        }
        finally {
            exitQueryContext();
        }
    }

    private Expression createSelect(Expression target, String functionName, @NotNull cqlParser.FunctionContext ctx) {
        boolean isListResult = false;
        boolean isSingular = false;
        AliasedQuerySource source = enterQueryContext(target);
        try {
            isSingular = !(source.getResultType() instanceof ListType);
            checkArgumentCount(ctx, functionName, 1);
            Expression select = (Expression)visitor.visit(ctx.paramList().expression(0));
            QueryContext queryContext = builder.peekQueryContext();
            LetClause let = of.createLetClause().withExpression(select).withIdentifier("$a");
            let.setResultType(select.getResultType());
            queryContext.addLetClause(let);

            isListResult = select.getResultType() instanceof ListType;
            QueryLetRef letRef = of.createQueryLetRef().withName("$a");
            letRef.setResultType(select.getResultType());
            List<Expression> params = new ArrayList<>();
            params.add(letRef);
            Expression where = builder.resolveFunction(null, "IsNull", params);
            params = new ArrayList<>();
            params.add(where);
            where = builder.resolveFunction(null, "Not", params);

            ReturnClause returnClause = of.createReturnClause();
            letRef = of.createQueryLetRef().withName("$a");
            letRef.setResultType(select.getResultType());
            returnClause.setExpression(letRef);
            returnClause.setResultType(isSingular ? letRef.getResultType() : new ListType(letRef.getResultType()));

            Query query = createQuery(source, let, where, returnClause);

            if (!isSingular && isListResult) {
                params = new ArrayList<>();
                params.add(query);
                return builder.resolveFunction(null, "Flatten", params);
            }
            else {
                return query;
            }
        }
        finally {
            exitQueryContext();
        }
    }

    public Expression resolveMethod(Expression target, @NotNull cqlParser.FunctionContext ctx, boolean mustResolve) {
        String functionName = visitor.parseString(ctx.identifier());
        switch (functionName) {
            case "all": {
                // .all(criteria) resolves as .where(criteria).select(true).allTrue()
                Query query = createWhere(target, functionName, ctx);
                ReturnClause returnClause = of.createReturnClause();
                returnClause.setExpression(visitor.createLiteral(Boolean.valueOf(true)));
                if (query.getResultType() instanceof ListType) {
                    returnClause.setResultType(new ListType(returnClause.getExpression().getResultType()));
                }
                else {
                    returnClause.setResultType(returnClause.getExpression().getResultType());
                }
                query.setReturn(returnClause);
                query.setResultType(returnClause.getResultType());

                List<Expression> params = new ArrayList<>();
                params.add(query);
                return builder.resolveFunction(null, "AllTrue", params);
            }
            case "allTrue": return builder.resolveFunction(null, "AllTrue", getParams(target, ctx));
            case "anyTrue": return builder.resolveFunction(null, "AnyTrue", getParams(target, ctx));
            case "allFalse": return builder.resolveFunction(null, "AllFalse", getParams(target, ctx));
            case "anyFalse": return builder.resolveFunction(null, "AnyFalse", getParams(target, ctx));
            // TODO: children...
            case "contains": {
                checkArgumentCount(ctx, functionName, 1);
                List<Expression> params = new ArrayList<Expression>();
                Expression argument = (Expression)visitor.visit(ctx.paramList().expression(0));
                params.add(argument);
                params.add(target);
                Expression result = builder.resolveFunction(null, "PositionOf", params);
                params = new ArrayList<Expression>();
                params.add(result);
                params.add(visitor.createLiteral(0));
                return builder.resolveFunction(null, "GreaterOrEqual", params);
            }
            case "count": return builder.resolveFunction(null, "Count", getParams(target, ctx));
            // TODO: descendents...
            case "distinct": return builder.resolveFunction(null, "Distinct", getParams(target, ctx));
            case "empty": {
                List<Expression> params = getParams(target, ctx);
                Expression exists = builder.resolveFunction(null, "Exists", params);
                params = new ArrayList<>();
                params.add(exists);
                return builder.resolveFunction(null, "Not", params);
            }
            case "endsWith": return builder.resolveFunction(null, "EndsWith", getParams(target, ctx));
            case "exists": {
                if (ctx.paramList() == null || ctx.paramList().expression() == null || ctx.paramList().expression().isEmpty()) {
                    List<Expression> params = getParams(target, ctx);
                    return builder.resolveFunction(null, "Exists", params);
                }
                else {
                    // .exists(criteria) resolves as a .where(criteria).exists()
                    Query query = createWhere(target, functionName, ctx);
                    List<Expression> params = new ArrayList();
                    params.add(query);
                    return builder.resolveFunction(null, "Exists", params);
                }
            }
            case "first": return builder.resolveFunction(null, "First", getParams(target, ctx));
            case "iif": {
                Expression result = target;
                List<Expression> params = null;
                if (result.getResultType() instanceof ListType) {
                    params = new ArrayList();
                    params.add(result);
                    result = builder.resolveFunction(null, "SingletonFrom", params);
                }
                Expression thenExpression = (Expression)visitor.visit(ctx.paramList().expression(0));
                Expression elseExpression = ctx.paramList().expression().size() == 2 ? (Expression)visitor.visit(ctx.paramList().expression(1)) : of.createNull();
                result = of.createIf().withCondition(result).withThen(thenExpression).withElse(elseExpression);
                return visitor.resolveIfThenElse((If)result);
            }
            case "indexOf": {
                checkArgumentCount(ctx, functionName, 1);
                List<Expression> params = new ArrayList<Expression>();
                Expression argument = (Expression)visitor.visit(ctx.paramList().expression(0));
                params.add(argument);
                params.add(target);
                return builder.resolveFunction(null, "PositionOf", params);
            }
            // TODO: isDistinct // resolves as .count() = .distinct().count() // somewhat tricky in that it needs to duplicate the target expression...
            case "last": return builder.resolveFunction(null, "Last", getParams(target, ctx));
            case "length": return builder.resolveFunction(null, "Length", getParams(target, ctx));
            case "matches": return builder.resolveFunction(null, "Matches", getParams(target, ctx));
            case "not": return builder.resolveFunction(null, "Not", getParams(target, ctx));
            // TODO: ofType // resolves as .where($this is type).select($this as type)
            // TODO: repeat // involves a new ELM operator to support recursive evaluation...
            case "replace": return builder.resolveFunction(null, "Replace", getParams(target, ctx));
            // TODO: replaceMatches // involves a new ELM operator
            case "select": {
                return createSelect(target, functionName, ctx);
            }
            case "single": return builder.resolveFunction(null, "SingletonFrom", getParams(target, ctx));
            // TODO: skip // Involves a new ELM operator Skip...
            case "startsWith": return builder.resolveFunction(null, "StartsWith", getParams(target, ctx));
            case "subsetOf": return builder.resolveFunction(null, "IncludedIn", getParams(target, ctx));
            case "substring": return builder.resolveFunction(null, "Substring", getParams(target, ctx));
            case "supersetOf": return builder.resolveFunction(null, "Includes", getParams(target, ctx));
            // TODO: tail // involves a new ELM operator Tail...
            // TODO: take // involves a new ELM operator Take...
            case "toBoolean": return builder.resolveFunction(null, "ToBoolean", getParams(target, ctx));
            case "toDateTime": return builder.resolveFunction(null, "ToDateTime", getParams(target, ctx));
            case "toDecimal": return builder.resolveFunction(null, "ToDecimal", getParams(target, ctx));
            case "toInteger": return builder.resolveFunction(null, "ToInteger", getParams(target, ctx));
            case "toString": return builder.resolveFunction(null, "ToString", getParams(target, ctx));
            case "toTime": return builder.resolveFunction(null, "ToTime", getParams(target, ctx));
            // TODO: trace
            case "where": {
                return createWhere(target, functionName, ctx);
            }

            default: {
                if (mustResolve) {
                    throw new IllegalArgumentException(String.format("Unknown method %s.", functionName));
                }

                return null;
            }
        }
    }
}
