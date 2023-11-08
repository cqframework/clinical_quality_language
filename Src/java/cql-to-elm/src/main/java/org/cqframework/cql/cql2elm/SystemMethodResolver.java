package org.cqframework.cql.cql2elm;

import org.cqframework.cql.cql2elm.model.QueryContext;
import org.cqframework.cql.gen.cqlParser;
import org.hl7.cql.model.*;
import org.hl7.elm.r1.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;


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

    private List<Expression> getParams(Expression target, cqlParser.ParamListContext ctx) {
        List<Expression> params = new ArrayList<Expression>();
        params.add(target);
        if (ctx != null && ctx.expression() != null) {
            for (cqlParser.ExpressionContext param : ctx.expression()) {
                final Expression visit = (Expression) visitor.visit(param);
                params.add(visit);
            }
        }

        return params;
    }

    private void checkArgumentCount(cqlParser.ParamListContext ctx, String functionName, int expectedCount) {
        int actualCount = 0;
        if (ctx != null && ctx.expression() != null) {
            actualCount = ctx.expression().size();
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
        queryContext.addPrimaryQuerySources(sources);
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

    private Query createWhere(Expression target, String functionName, cqlParser.ParamListContext ctx) {
        AliasedQuerySource source = enterQueryContext(target);
        try {

            checkArgumentCount(ctx, functionName, 1);
            Expression where = (Expression)visitor.visit(ctx.expression(0));
            if (visitor.getDateRangeOptimization()) {
                where = visitor.optimizeDateRangeInQuery(where, source);
            }

            return createQuery(source, null, where, null);
        }
        finally {
            exitQueryContext();
        }
    }

    // X.ofType(T) === X $this where $this is T
    private Expression createOfType(Expression target, String functionName, cqlParser.ParamListContext ctx) {
        AliasedQuerySource source = enterQueryContext(target);
        try {
            checkArgumentCount(ctx, functionName, 1);
            Expression typeArgument = null;
            builder.pushTypeSpecifierContext();
            try {
                typeArgument = (Expression)visitor.visit(ctx.expression(0));
            }
            finally {
                builder.popTypeSpecifierContext();
            }

            if (!(typeArgument instanceof Literal)) {
                throw new IllegalArgumentException("Expected literal argument");
            }

            Literal typeLiteral = (Literal)typeArgument;
            if (!(DataTypes.equal(typeLiteral.getResultType(), builder.resolveTypeName("System", "String")))) {
                throw new IllegalArgumentException("Expected string literal argument");
            }

            String typeSpecifier = ((Literal)typeArgument).getValue();
            DataType isType = builder.resolveTypeSpecifier(typeSpecifier);

            AliasRef thisRef = of.createAliasRef().withName(source.getAlias());
            boolean isSingular = !(source.getResultType() instanceof ListType);
            DataType elementType = isSingular ? source.getResultType() : ((ListType)source.getResultType()).getElementType();
            thisRef.setResultType(elementType);

            Is is = of.createIs().withOperand(thisRef);
            if (isType instanceof NamedType) {
                is.setIsType(builder.dataTypeToQName(isType));
            }
            else {
                is.setIsTypeSpecifier(builder.dataTypeToTypeSpecifier(isType));
            }
            is.setResultType(builder.resolveTypeName("System", "Boolean"));

            return createQuery(source, null, is, null);
        }
        finally {
            exitQueryContext();
        }
    }

    private Expression createRepeat(Expression target, String functionName, cqlParser.ParamListContext ctx) {
        AliasedQuerySource source = enterQueryContext(target);
        try {
            boolean isSingular = !(source.getResultType() instanceof ListType);
            checkArgumentCount(ctx, functionName, 1);
            Expression select = (Expression)visitor.visit(ctx.expression(0));
            Repeat repeat = of.createRepeat();
            repeat.setSource(target);
            repeat.setElement(select);
            repeat.setScope("$this");
            // TODO: This isn't quite right, it glosses over the fact that the type of the result may include the result of invoking the element expression on intermediate results
            if (isSingular) {
                repeat.setResultType(new ListType(select.getResultType()));
            }
            else {
                repeat.setResultType(select.getResultType());
            }

            return repeat;
        }
        finally {
            exitQueryContext();
        }
    }

    private Expression createSelect(Expression target, String functionName, cqlParser.ParamListContext ctx) {
        boolean isListResult = false;
        boolean isSingular = false;
        AliasedQuerySource source = enterQueryContext(target);
        try {
            isSingular = !(source.getResultType() instanceof ListType);
            checkArgumentCount(ctx, functionName, 1);
            Expression select = (Expression)visitor.visit(ctx.expression(0));
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

    private void gatherChildTypes(DataType dataType, boolean recurse, Set<DataType> dataTypes) {
        if (dataType instanceof ClassType) {
            for (ClassTypeElement element : ((ClassType)dataType).getElements()) {
                DataType elementType = element.getType() instanceof ListType ?
                        ((ListType)element.getType()).getElementType() : element.getType();
                dataTypes.add(elementType);
                if (recurse) {
                    gatherChildTypes(elementType, recurse, dataTypes);
                }
            }
        }
        else if (dataType instanceof TupleType) {
            for (TupleTypeElement element : ((TupleType)dataType).getElements()) {
                DataType elementType = element.getType() instanceof ListType ?
                        ((ListType)element.getType()).getElementType() : element.getType();
                dataTypes.add(elementType);
                if (recurse) {
                    gatherChildTypes(elementType, recurse, dataTypes);
                }
            }
        }
        else if (dataType instanceof ListType) {
            DataType elementType = ((ListType)dataType).getElementType();
            dataTypes.add(elementType);
            if (recurse) {
                gatherChildTypes(elementType, recurse, dataTypes);
            }
        }
        else {
            dataTypes.add(builder.resolveTypeName("System.Any"));
        }
    }

    public Expression resolveMethod(Expression target, String functionName, cqlParser.ParamListContext ctx, boolean mustResolve) {
        final Supplier<List<Expression>> getParams = () -> getParams(target, ctx);
        switch (functionName) {
            case "aggregate": return builder.resolveFunction(null, "Aggregate", getParams.get());
            case "abs": return builder.resolveFunction(null, "Abs", getParams.get());
            case "all": {
                // .all(criteria) resolves as .where(criteria).select(true).allTrue()
                Query query = createWhere(target, functionName, ctx);
                ReturnClause returnClause = of.createReturnClause();
                returnClause.setExpression(builder.createLiteral(Boolean.valueOf(true)));
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
            case "allTrue": return builder.resolveFunction(null, "AllTrue", getParams.get());
            case "anyTrue": return builder.resolveFunction(null, "AnyTrue", getParams.get());
            case "allFalse": return builder.resolveFunction(null, "AllFalse", getParams.get());
            case "anyFalse": return builder.resolveFunction(null, "AnyFalse", getParams.get());
            case "ceiling": return builder.resolveFunction(null, "Ceiling", getParams.get());
            case "children": {
                checkArgumentCount(ctx, functionName, 0);
                Children children = of.createChildren();
                children.setSource(target);
                Set<DataType> dataTypes = new java.util.HashSet<DataType>();
                gatherChildTypes(target.getResultType(), false, dataTypes);
                if (dataTypes.size() == 1) {
                    children.setResultType(new ListType((DataType)dataTypes.toArray()[0]));
                }
                else {
                    children.setResultType(new ListType(new ChoiceType(dataTypes)));
                }
                return children;
            }
            case "combine": {
                checkArgumentCount(ctx, functionName, 1);
                List<Expression> elements = new ArrayList<>();
                Expression argument = (Expression)visitor.visit(ctx.expression(0));
                elements.add(target);
                elements.add(argument);
                DataType elementType = builder.ensureCompatibleTypes(target.getResultType(), argument.getResultType());
                org.hl7.elm.r1.List list = of.createList();
                list.setResultType(new ListType(elementType));
                list.getElement().add(builder.ensureCompatible(target, elementType));
                list.getElement().add(builder.ensureCompatible(argument, elementType));
                ArrayList<Expression> params = new ArrayList<Expression>();
                params.add(list);
                return builder.resolveFunction(null, "Flatten", params);
            }
            case "contains": {
                checkArgumentCount(ctx, functionName, 1);
                List<Expression> params = new ArrayList<Expression>();
                Expression argument = (Expression)visitor.visit(ctx.expression(0));
                params.add(argument);
                params.add(target);
                Expression result = builder.resolveFunction(null, "PositionOf", params);
                params = new ArrayList<Expression>();
                params.add(result);
                params.add(builder.createLiteral(0));
                return builder.resolveFunction(null, "GreaterOrEqual", params);
            }
            case "convertsToBoolean": return builder.resolveFunction(null, "ConvertsToBoolean", getParams.get());
            case "convertsToDate": return builder.resolveFunction(null, "ConvertsToDate", getParams.get());
            case "convertsToDateTime": return builder.resolveFunction(null, "ConvertsToDateTime", getParams.get());
            case "convertsToDecimal": return builder.resolveFunction(null, "ConvertsToDecimal", getParams.get());
            case "convertsToInteger": return builder.resolveFunction(null, "ConvertsToInteger", getParams.get());
            case "convertsToQuantity": return builder.resolveFunction(null, "ConvertsToQuantity", getParams.get());
            case "convertsToString": return builder.resolveFunction(null, "ConvertsToString", getParams.get());
            case "convertsToTime": return builder.resolveFunction(null, "ConvertsToTime", getParams.get());
            case "count": return builder.resolveFunction(null, "Count", getParams.get());
            case "descendents": {
                checkArgumentCount(ctx, functionName, 0);
                Descendents descendents = of.createDescendents();
                descendents.setSource(target);
                Set<DataType> dataTypes = new java.util.HashSet<DataType>();
                gatherChildTypes(target.getResultType(), true, dataTypes);
                if (dataTypes.size() == 1) {
                    descendents.setResultType(new ListType((DataType)dataTypes.toArray()[0]));
                }
                else {
                    descendents.setResultType(new ListType(new ChoiceType(dataTypes)));
                }
                return descendents;
            }
            case "distinct": return builder.resolveFunction(null, "Distinct", getParams.get());
            case "empty": {
                List<Expression> params = getParams.get();
                Expression exists = builder.resolveFunction(null, "Exists", params);
                params = new ArrayList<>();
                params.add(exists);
                return builder.resolveFunction(null, "Not", params);
            }
            case "endsWith": return builder.resolveFunction(null, "EndsWith", getParams.get());
            case "exclude": return builder.resolveFunction(null, "Except", getParams.get());
            case "exists": {
                if (ctx == null || ctx.expression() == null || ctx.expression().isEmpty()) {
                    List<Expression> params = getParams.get();
                    return builder.resolveFunction(null, "Exists", params);
                }
                else {
                    // .exists(criteria) resolves as a .where(criteria).exists()
                    Query query = createWhere(target, functionName, ctx);
                    List<Expression> params = new ArrayList<>();
                    params.add(query);
                    return builder.resolveFunction(null, "Exists", params);
                }
            }
            case "exp": return builder.resolveFunction(null, "Exp", getParams.get());
            case "first": return builder.resolveFunction(null, "First", getParams.get());
            case "floor": return builder.resolveFunction(null, "Floor", getParams.get());
            case "hasValue": {
                List<Expression> params = getParams.get();
                Expression isNull = builder.resolveFunction(null, "IsNull", params);
                params = new ArrayList<>();
                params.add(isNull);
                return builder.resolveFunction(null, "Not", params);
            }
            case "iif": {
                Expression result = target;
                List<Expression> params = null;
                if (result.getResultType() instanceof ListType) {
                    params = new ArrayList<>();
                    params.add(result);
                    result = builder.resolveFunction(null, "SingletonFrom", params);
                }
                Expression thenExpression = (Expression)visitor.visit(ctx.expression(0));
                Expression elseExpression = ctx.expression().size() == 2 ? (Expression)visitor.visit(ctx.expression(1)) : of.createNull();
                result = of.createIf().withCondition(result).withThen(thenExpression).withElse(elseExpression);
                return visitor.resolveIfThenElse((If)result);
            }
            case "indexOf": {
                checkArgumentCount(ctx, functionName, 1);
                List<Expression> params = new ArrayList<Expression>();
                Expression argument = (Expression)visitor.visit(ctx.expression(0));
                params.add(argument);
                params.add(target);
                return builder.resolveFunction(null, "PositionOf", params);
            }
            case "intersect": return builder.resolveFunction(null, "Intersect", getParams.get());
            case "is":
            case "as": {
                checkArgumentCount(ctx, functionName, 1);
                Expression typeArgument = null;
                builder.pushTypeSpecifierContext();
                try {
                    typeArgument = (Expression)visitor.visit(ctx.expression(0));
                }
                finally {
                    builder.popTypeSpecifierContext();
                }

                if (!(typeArgument instanceof Literal)) {
                    throw new IllegalArgumentException("Expected literal argument");
                }

                Literal typeLiteral = (Literal)typeArgument;
                if (!(DataTypes.equal(typeLiteral.getResultType(), builder.resolveTypeName("System", "String")))) {
                    throw new IllegalArgumentException("Expected string literal argument");
                }

                String typeSpecifier = ((Literal)typeArgument).getValue();
                DataType isType = builder.resolveTypeSpecifier(typeSpecifier);

                return functionName.equals("is") ? builder.buildIs(target, isType) : builder.buildAs(target, isType);
            }
            // TODO: isDistinct // resolves as .count() = .distinct().count() // somewhat tricky in that it needs to duplicate the target expression...
            case "last": return builder.resolveFunction(null, "Last", getParams.get());
            case "lastIndexOf": {
                checkArgumentCount(ctx, functionName, 1);
                List<Expression> params = new ArrayList<Expression>();
                Expression argument = (Expression)visitor.visit(ctx.expression(0));
                params.add(argument);
                params.add(target);
                return builder.resolveFunction(null, "LastPositionOf", params);
            }
            case "length": return builder.resolveFunction(null, "Length", getParams.get());
            case "ln": return builder.resolveFunction(null, "Ln", getParams.get());
            case "log": return builder.resolveFunction(null, "Log", getParams.get());
            case "lower": return builder.resolveFunction(null, "Lower", getParams.get());
            case "matches": return builder.resolveFunction(null, "Matches", getParams.get());
            case "memberOf": return builder.resolveFunction(null, "InValueSet", getParams.get());
            case "not": return builder.resolveFunction(null, "Not", getParams.get());
            //now could never resolve as a method because it has no arguments
            //case "now": return builder.resolveFunction(null, "Now", getParams.get());
            case "ofType": return createOfType(target, functionName, ctx);
            case "power": return builder.resolveFunction(null, "Power", getParams.get());
            case "repeat": return createRepeat(target, functionName, ctx);
            case "replace": return builder.resolveFunction(null, "Replace", getParams.get());
            case "replaceMatches": return builder.resolveFunction(null, "ReplaceMatches", getParams.get());
            case "round": return builder.resolveFunction(null, "Round", getParams.get());
            case "select": {
                return createSelect(target, functionName, ctx);
            }
            case "single": return builder.resolveFunction(null, "SingletonFrom", getParams.get());
            case "skip": return builder.resolveFunction(null, "Skip", getParams.get());
            case "sqrt": {
                checkArgumentCount(ctx, functionName, 0);
                List<Expression> params = new ArrayList<Expression>();
                params.add(target);
                params.add(builder.createLiteral(0.5));
                return builder.resolveFunction(null, "Power", params);
            }
            case "startsWith": return builder.resolveFunction(null, "StartsWith", getParams.get());
            case "subsetOf": return builder.resolveFunction(null, "IncludedIn", getParams.get());
            case "substring": return builder.resolveFunction(null, "Substring", getParams.get());
            case "subsumes": return builder.resolveFunction(null, "Subsumes", getParams.get());
            case "subsumedBy": return builder.resolveFunction(null, "SubsumedBy", getParams.get());
            case "supersetOf": return builder.resolveFunction(null, "Includes", getParams.get());
            case "tail": return builder.resolveFunction(null, "Tail", getParams.get());
            case "take": return builder.resolveFunction(null, "Take", getParams.get());
            //timeOfDay could never resolve as a method because it has no arguments
            //case "timeOfDay": return builder.resolveFunction(null, "TimeOfDay", getParams.get());
            case "toBoolean": return builder.resolveFunction(null, "ToBoolean", getParams.get());
            case "toChars": return builder.resolveFunction(null, "ToChars", getParams.get());
            case "toDate": return builder.resolveFunction(null, "ToDate", getParams.get());
            case "toDateTime": return builder.resolveFunction(null, "ToDateTime", getParams.get());
            //today could never resolve as a method because it has no arguments
            //case "today": return builder.resolveFunction(null, "Today", getParams.get());
            case "toDecimal": return builder.resolveFunction(null, "ToDecimal", getParams.get());
            case "toInteger": return builder.resolveFunction(null, "ToInteger", getParams.get());
            case "toQuantity": return builder.resolveFunction(null, "ToQuantity", getParams.get());
            case "toString": return builder.resolveFunction(null, "ToString", getParams.get());
            case "toTime": return builder.resolveFunction(null, "ToTime", getParams.get());
            case "trace": {
                checkArgumentCount(ctx, functionName, 1);
                List<Expression> params = new ArrayList<Expression>();
                params.add(target);
                params.add(builder.createLiteral(true));
                params.add(builder.createLiteral("TRACE"));
                params.add(builder.createLiteral("Trace"));
                params.add((Expression)visitor.visit(ctx.expression(0)));
                return builder.resolveFunction(null, "Message", params);
            }
            case "truncate": return builder.resolveFunction(null, "Truncate", getParams.get());
            case "upper": return builder.resolveFunction(null, "Upper", getParams.get());
            case "where": {
                return createWhere(target, functionName, ctx);
            }

            default: {
                return visitor.resolveFunction(null, functionName, getParams.get(), mustResolve, false, true);
            }
        }
    }
}
