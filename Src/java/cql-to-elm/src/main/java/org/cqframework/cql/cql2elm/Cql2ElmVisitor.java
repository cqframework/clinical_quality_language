package org.cqframework.cql.cql2elm;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.cqframework.cql.elm.tracking.TrackBack;
import org.cqframework.cql.elm.tracking.Trackable;
import org.cqframework.cql.gen.cqlBaseVisitor;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;
import org.cqframework.cql.cql2elm.model.*;
import org.cqframework.cql.cql2elm.preprocessor.CqlPreprocessorVisitor;
import org.cqframework.cql.cql2elm.preprocessor.LibraryInfo;
import org.hl7.cql_annotations.r1.Annotation;
import org.hl7.cql_annotations.r1.Narrative;
import org.hl7.elm.r1.*;
import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.Interval;
import org.hl7.elm_modelinfo.r1.ClassInfo;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class Cql2ElmVisitor extends cqlBaseVisitor {
    private final ObjectFactory of = new ObjectFactory();
    private final org.hl7.cql_annotations.r1.ObjectFactory af = new org.hl7.cql_annotations.r1.ObjectFactory();
    private boolean annotate = false;
    private boolean dateRangeOptimization = false;

    private TokenStream tokenStream;

    private LibraryInfo libraryInfo = null;
    private Library library = null;
    private String currentContext = "UNKNOWN";

    //Put them here for now, but eventually somewhere else?
    private final HashMap<String, Library> libraries = new HashMap<>();
    private final Stack<QueryContext> queries = new Stack<>();
    private final Stack<TimingOperatorContext> timingOperators = new Stack<>();
    private final Stack<Narrative> narratives = new Stack<>();
    private int currentToken = -1;
    private int nextLocalId = 1;
    private final List<Retrieve> retrieves= new ArrayList<>();
    private final List<Expression> expressions = new ArrayList<>();
    private ModelHelper modelHelper = null;

    public void enableAnnotations() { annotate = true; }
    public void disableAnnotations() { annotate = false; }
    public void enableDateRangeOptimization() { dateRangeOptimization = true; }
    public void disableDateRangeOptimization() { dateRangeOptimization = false; }
    public TokenStream getTokenStream() { return tokenStream; }
    public void setTokenStream(TokenStream value) { tokenStream = value; }

    public LibraryInfo getLibraryInfo() { return libraryInfo; }
    public void setLibraryInfo(LibraryInfo value) { libraryInfo = value; }

    public Library getLibrary() {
        return library;
    }

    public List<Retrieve> getRetrieves() { return retrieves; }

    public List<Expression> getExpressions() {
        return expressions;
    }

    private int getNextLocalId() {
        return nextLocalId++;
    }

    private void PushNarrative(@NotNull ParseTree tree) {
        org.antlr.v4.runtime.misc.Interval sourceInterval = tree.getSourceInterval();

        // If there is a parent narrative
            // add the text from the current text pointer to the start of the new source context to the narrative
        Narrative parentNarrative = narratives.isEmpty() ? null : narratives.peek();
        if (parentNarrative != null && sourceInterval.a - 1 - currentToken >= 0) {
            org.antlr.v4.runtime.misc.Interval tokenInterval =
                    new org.antlr.v4.runtime.misc.Interval(currentToken, sourceInterval.a - 1);
            parentNarrative.getContent().add(tokenStream.getText(tokenInterval));
        }

        // advance the token pointer to the start of the new source context
        currentToken = sourceInterval.a;

        // Create a new narrative
            // add it to the parent narrative, if there is one
            // push it on the narrative stack
        Narrative newNarrative = af.createNarrative();
        narratives.push(newNarrative);
    }

    private Narrative PopNarrative(@NotNull ParseTree tree, Object o) {
        org.antlr.v4.runtime.misc.Interval sourceInterval = tree.getSourceInterval();

        // Pop the narrative off the narrative stack
        Narrative currentNarrative = narratives.pop();

        // Add the text from the current token pointer to the end of the current source context to the narrative
        if (sourceInterval.b - currentToken >= 0) {
            org.antlr.v4.runtime.misc.Interval tokenInterval =
                    new org.antlr.v4.runtime.misc.Interval(currentToken, sourceInterval.b);
            currentNarrative.getContent().add(tokenStream.getText(tokenInterval));
        }

        // Advance the token pointer after the end of the current source context
        currentToken = sourceInterval.b + 1;

        // If the narrative corresponds to an element returned by the parser
            // if the element doesn't have a localId
                // set the narrative's reference id
                // if there is a parent narrative
                    // add this narrative to the content of the parent
            // else
                // if there is a parent narrative
                    // add the contents of this narrative to that narrative
        if (o instanceof Element) {
            Element element = (Element)o;
            if (element.getLocalId() == null) {
                element.setLocalId(Integer.toString(getNextLocalId()));
                currentNarrative.setR(element.getLocalId());

                if (!narratives.isEmpty()) {
                    Narrative parentNarrative = narratives.peek();
                    parentNarrative.getContent().add(
                            new JAXBElement<>(
                                    new QName("urn:hl7-org:cql-annotations:r1", "s"),
                                    Narrative.class,
                                    currentNarrative));
                }

                // If the current element is an expression def, set the narrative as the annotation
                if (o instanceof ExpressionDef) {
                    ExpressionDef expressionDef = (ExpressionDef)o;
                    expressionDef.getAnnotation().add(af.createAnnotation().withS(currentNarrative));
                }
            }
            else {
                if (!narratives.isEmpty()) {
                    Narrative parentNarrative = narratives.peek();
                    parentNarrative.getContent().addAll(currentNarrative.getContent());
                }
            }
        }

        return currentNarrative;
    }

    @Override
    public Object visit(@NotNull ParseTree tree) {
        if (annotate) {
            PushNarrative(tree);
        }
        Object o = null;
        try {
            o = super.visit(tree);
        }
        finally {
            if (annotate) {
                PopNarrative(tree, o);
            }
        }

        if (o instanceof Trackable && tree instanceof ParserRuleContext && !(tree instanceof cqlParser.LogicContext)) {
            this.track((Trackable) o, (ParserRuleContext) tree);
        }
        if (o instanceof Expression) {
            addExpression(tree, (Expression) o);
        }

        return o;
    }

    @Override
    public Object visitLogic(@NotNull cqlParser.LogicContext ctx) {
        library = of.createLibrary();

        Object lastResult = null;

        // Loop through and call visit on each child (to ensure they are tracked)
        for (int i=0; i < ctx.getChildCount(); i++) {
            lastResult = visit(ctx.getChild(i));
        }

        // Return last result (consistent with super implementation and helps w/ testing)
        return lastResult;
    }

    @Override
    public VersionedIdentifier visitLibraryDefinition(@NotNull cqlParser.LibraryDefinitionContext ctx) {
        VersionedIdentifier vid = of.createVersionedIdentifier()
                .withId(parseString(ctx.identifier()))
                .withVersion(parseString(ctx.versionSpecifier()));
        library.setIdentifier(vid);

        return vid;
    }

    @Override
    public UsingDef visitUsingDefinition(@NotNull cqlParser.UsingDefinitionContext ctx) {
        return initializeModelHelper(parseString(ctx.identifier()));
    }

    @Override
    public Object visitIncludeDefinition(@NotNull cqlParser.IncludeDefinitionContext ctx) {
        IncludeDef library = of.createIncludeDef()
                .withLocalIdentifier(parseString(ctx.localIdentifier()))
                .withPath(parseString(ctx.identifier()))
                .withVersion(parseString(ctx.versionSpecifier()));

        addToLibrary(library);

        return library;
    }

    @Override
    public ParameterDef visitParameterDefinition(@NotNull cqlParser.ParameterDefinitionContext ctx) {
        ParameterDef param = of.createParameterDef()
                .withName(parseString(ctx.identifier()))
                .withDefault(parseExpression(ctx.expression()))
                .withParameterTypeSpecifier(parseTypeSpecifier(ctx.typeSpecifier()));

        addToLibrary(param);

        return param;
    }

    @Override
    public NamedTypeSpecifier visitNamedTypeSpecifier(@NotNull cqlParser.NamedTypeSpecifierContext ctx) {
        return of.createNamedTypeSpecifier().withName(resolveNamedType(parseString(ctx.identifier())));
    }

    @Override
    public TupleElementDefinition visitTupleElementDefinition(@NotNull cqlParser.TupleElementDefinitionContext ctx) {
        return of.createTupleElementDefinition()
                .withName(parseString(ctx.identifier()))
                .withType(parseTypeSpecifier(ctx.typeSpecifier()));
    }

    @Override
    public Object visitTupleTypeSpecifier(@NotNull cqlParser.TupleTypeSpecifierContext ctx) {
        TupleTypeSpecifier typeSpecifier = of.createTupleTypeSpecifier();
        for (cqlParser.TupleElementDefinitionContext definitionContext : ctx.tupleElementDefinition()) {
            typeSpecifier.getElement().add((TupleElementDefinition)visit(definitionContext));
        }

        return typeSpecifier;
    }

    @Override
    public IntervalTypeSpecifier visitIntervalTypeSpecifier(@NotNull cqlParser.IntervalTypeSpecifierContext ctx) {
        return of.createIntervalTypeSpecifier().withPointType(parseTypeSpecifier(ctx.typeSpecifier()));
    }

    @Override
    public ListTypeSpecifier visitListTypeSpecifier(@NotNull cqlParser.ListTypeSpecifierContext ctx) {
        return of.createListTypeSpecifier().withElementType(parseTypeSpecifier(ctx.typeSpecifier()));
    }

    @Override
    public ValueSetDef visitValuesetDefinition(@NotNull cqlParser.ValuesetDefinitionContext ctx) {
        ValueSetDef vs = of.createValueSetDef()
                .withName(parseString(ctx.identifier()))
                .withValueSet(of.createValueSet().withId(parseString(ctx.valuesetId()))
                        .withVersion(parseString(ctx.versionSpecifier()))
                );
        addToLibrary(vs);

        return vs;
    }

    @Override
    public String visitContextDefinition(@NotNull cqlParser.ContextDefinitionContext ctx) {
        currentContext = parseString(ctx.identifier());

        return currentContext;
    }

    @Override
    public ExpressionDef visitExpressionDefinition(@NotNull cqlParser.ExpressionDefinitionContext ctx) {
        ExpressionDef def = of.createExpressionDef()
                .withName(parseString(ctx.identifier()))
                .withContext(currentContext)
                .withExpression((Expression) visit(ctx.expression()));
        addToLibrary(def);

        return def;
    }

    @Override
    public Literal visitStringLiteral(@NotNull cqlParser.StringLiteralContext ctx) {
        return createLiteral(parseString(ctx.STRING()));
    }

    @Override
    public Literal visitBooleanLiteral(@NotNull cqlParser.BooleanLiteralContext ctx) {
        return createLiteral(Boolean.valueOf(ctx.getText()));
    }

    @Override
    public Object visitIntervalSelector(@NotNull cqlParser.IntervalSelectorContext ctx) {
        return of.createInterval()
                .withLow(parseExpression(ctx.expression(0)))
                .withLowClosed(ctx.getChild(1).getText().equals("["))
                .withHigh(parseExpression(ctx.expression(1)))
                .withHighClosed(ctx.getChild(5).getText().equals("]"));
    }

    @Override
    public Object visitTupleElementSelector(@NotNull cqlParser.TupleElementSelectorContext ctx) {
        return of.createTupleElement()
                .withName(parseString(ctx.identifier()))
                .withValue(parseExpression(ctx.expression()));
    }

    @Override
    public Object visitTupleSelector(@NotNull cqlParser.TupleSelectorContext ctx) {
        Tuple tuple = of.createTuple();
        for (cqlParser.TupleElementSelectorContext element : ctx.tupleElementSelector()) {
            tuple.getElement().add((TupleElement)visit(element));
        }
        return tuple;
    }

    @Override
    public Object visitListSelector(@NotNull cqlParser.ListSelectorContext ctx) {
        org.hl7.elm.r1.List list = of.createList().withTypeSpecifier(parseTypeSpecifier(ctx.typeSpecifier()));
        for (cqlParser.ExpressionContext element : ctx.expression()) {
            list.getElement().add(parseExpression(element));
        }

        return list;
    }

    @Override
    public Null visitNullLiteral(@NotNull cqlParser.NullLiteralContext ctx) {
        return of.createNull();
    }

    @Override
    public Expression visitQuantityLiteral(@NotNull cqlParser.QuantityLiteralContext ctx) {
        if (ctx.unit() != null) {
            DecimalFormat df = new DecimalFormat("#.#");
            df.setParseBigDecimal(true);
            try {
                return of.createQuantity()
                        .withValue((BigDecimal)df.parse(ctx.QUANTITY().getText()))
                        .withUnit(ctx.unit().getText());
            }
            catch (ParseException e) {
                // Should never occur, just return null
                return of.createNull();
            }
        }
        else {
            String quantity = ctx.QUANTITY().getText();
            return of.createLiteral()
                    .withValue(quantity)
                    .withValueType(resolveNamedType(quantity.contains(".") ? "Decimal" : "Integer"));
        }
    }

    @Override
    public Not visitNotExpression(@NotNull cqlParser.NotExpressionContext ctx) {
        return of.createNot().withOperand(parseExpression(ctx.expression()));
    }

    @Override
    public Exists visitExistenceExpression(@NotNull cqlParser.ExistenceExpressionContext ctx) {
        return of.createExists().withOperand(parseExpression(ctx.expression()));
    }

    @Override
    public BinaryExpression visitMultiplicationExpressionTerm(@NotNull cqlParser.MultiplicationExpressionTermContext ctx) {
        BinaryExpression exp = null;
        switch(ctx.getChild(1).getText()) {
            case "*":
                exp = of.createMultiply();
                break;
            case "/":
            case "div":
                exp = of.createDivide();
                break;
            case "mod" :
                exp = of.createModulo();
                break;
            default:
                System.err.println("Unsupported operator: " + ctx.getChild(1).getText());
        }

        if (exp != null) {
            exp.withOperand(
                    parseExpression(ctx.expressionTerm(0)),
                    parseExpression(ctx.expressionTerm(1)));
        }

        return exp;
    }

    @Override
    public Power visitPowerExpressionTerm(@NotNull cqlParser.PowerExpressionTermContext ctx) {
        return of.createPower().withOperand(
                parseExpression(ctx.expressionTerm(0)),
                parseExpression(ctx.expressionTerm(1)));
    }

    @Override
    public Object visitPolarityExpressionTerm(@NotNull cqlParser.PolarityExpressionTermContext ctx) {
        if (ctx.getChild(0).getText().equals("+")) {
            return visit(ctx.expressionTerm());
        }

        return of.createNegate().withOperand(parseExpression(ctx.expressionTerm()));
    }

    @Override
    public BinaryExpression visitAdditionExpressionTerm(@NotNull cqlParser.AdditionExpressionTermContext ctx) {
        BinaryExpression exp = null;
        switch(ctx.getChild(1).getText()) {
            case "+":
                exp = of.createAdd();
                break;
            case "-" :
                exp = of.createSubtract();
                break;
            default:
                System.err.println("Unsupported operator: " + ctx.getChild(1).getText());
        }

        if (exp != null) {
            exp.withOperand(
                    parseExpression(ctx.expressionTerm(0)),
                    parseExpression(ctx.expressionTerm(1)));
        }

        return exp;
    }

    @Override
    public Object visitPredecessorExpressionTerm(@NotNull cqlParser.PredecessorExpressionTermContext ctx) {
        return of.createPred().withOperand(parseExpression(ctx.expressionTerm()));
    }

    @Override
    public Object visitSuccessorExpressionTerm(@NotNull cqlParser.SuccessorExpressionTermContext ctx) {
        return of.createSucc().withOperand(parseExpression(ctx.expressionTerm()));
    }

    @Override
    public Object visitTimeBoundaryExpressionTerm(@NotNull cqlParser.TimeBoundaryExpressionTermContext ctx) {
        return ctx.getChild(0).getText().equals("start")
                ? of.createStart().withOperand(parseExpression(ctx.expressionTerm()))
                : of.createEnd().withOperand(parseExpression(ctx.expressionTerm()));
    }

    @Override
    public Object visitTimeUnitExpressionTerm(@NotNull cqlParser.TimeUnitExpressionTermContext ctx) {
        String component = ctx.dateTimeComponent().getText();

        switch (component) {
            case "date":
                return of.createDateOf().withOperand(parseExpression(ctx.expressionTerm()));
            case "time":
                return of.createTimeOf().withOperand(parseExpression(ctx.expressionTerm()));
            case "timezone":
                return of.createTimezoneOf().withOperand(parseExpression(ctx.expressionTerm()));
            case "year":
                return of.createYearOf().withOperand(parseExpression(ctx.expressionTerm()));
            case "month":
                return of.createMonthOf().withOperand(parseExpression(ctx.expressionTerm()));
            case "day":
                return of.createDayOf().withOperand(parseExpression(ctx.expressionTerm()));
            case "hour":
                return of.createHourOf().withOperand(parseExpression(ctx.expressionTerm()));
            case "minute":
                return of.createMinuteOf().withOperand(parseExpression(ctx.expressionTerm()));
            case "second":
                return of.createSecondOf().withOperand(parseExpression(ctx.expressionTerm()));
            case "millisecond":
                return of.createMillisecondOf().withOperand(parseExpression(ctx.expressionTerm()));
        }

        return of.createNull();
    }

    @Override
    public Object visitDurationExpressionTerm(@NotNull cqlParser.DurationExpressionTermContext ctx) {
        // duration in days of X <=> days between start of X and end of X
        switch (ctx.pluralDateTimePrecision().getText()) {
            case "years" :
                return of.createYearsBetween().withOperand(
                        of.createStart().withOperand(parseExpression(ctx.expressionTerm())),
                        of.createEnd().withOperand(parseExpression(ctx.expressionTerm()))
                );
            case "months" :
                return of.createMonthsBetween().withOperand(
                        of.createStart().withOperand(parseExpression(ctx.expressionTerm())),
                        of.createEnd().withOperand(parseExpression(ctx.expressionTerm()))
                );
            case "days" :
                return of.createDaysBetween().withOperand(
                        of.createStart().withOperand(parseExpression(ctx.expressionTerm())),
                        of.createEnd().withOperand(parseExpression(ctx.expressionTerm()))
                );
            case "hours" :
                return of.createHoursBetween().withOperand(
                        of.createStart().withOperand(parseExpression(ctx.expressionTerm())),
                        of.createEnd().withOperand(parseExpression(ctx.expressionTerm()))
                );
            case "minutes" :
                return of.createMinutesBetween().withOperand(
                        of.createStart().withOperand(parseExpression(ctx.expressionTerm())),
                        of.createEnd().withOperand(parseExpression(ctx.expressionTerm()))
                );
            case "seconds" :
                return of.createSecondsBetween().withOperand(
                        of.createStart().withOperand(parseExpression(ctx.expressionTerm())),
                        of.createEnd().withOperand(parseExpression(ctx.expressionTerm()))
                );
            case "milliseconds" :
                return of.createMillisecondsBetween().withOperand(
                        of.createStart().withOperand(parseExpression(ctx.expressionTerm())),
                        of.createEnd().withOperand(parseExpression(ctx.expressionTerm()))
                );
        }

        return of.createNull();
    }

    @Override
    public Object visitRangeExpression(@NotNull cqlParser.RangeExpressionContext ctx) {
        // X properly? between Y and Z
        Expression first = parseExpression(ctx.expression());
        Expression second = parseExpression(ctx.expressionTerm(0));
        Expression third = parseExpression(ctx.expressionTerm(1));
        boolean isProper = ctx.getChild(0).getText().equals("properly");
        return of.createAnd()
                .withOperand(
                        (isProper ? of.createGreater() : of.createGreaterOrEqual())
                                .withOperand(first, second),
                        (isProper ? of.createLess() : of.createLessOrEqual())
                                .withOperand(first, third)
                );
    }

    @Override
    public Object visitTimeRangeExpression(@NotNull cqlParser.TimeRangeExpressionContext ctx) {
        String component = ctx.pluralDateTimePrecision().getText();

        switch (component) {
            case "years":
                return of.createYearsBetween()
                        .withOperand(parseExpression(ctx.expressionTerm(0)), parseExpression(ctx.expressionTerm(1)));
            case "months":
                return of.createMonthsBetween()
                        .withOperand(parseExpression(ctx.expressionTerm(0)), parseExpression(ctx.expressionTerm(1)));
            case "days":
                return of.createDaysBetween()
                        .withOperand(parseExpression(ctx.expressionTerm(0)), parseExpression(ctx.expressionTerm(1)));
            case "hours":
                return of.createHoursBetween()
                        .withOperand(parseExpression(ctx.expressionTerm(0)), parseExpression(ctx.expressionTerm(1)));
            case "minutes":
                return of.createMinutesBetween()
                        .withOperand(parseExpression(ctx.expressionTerm(0)), parseExpression(ctx.expressionTerm(1)));
            case "seconds":
                return of.createSecondsBetween()
                        .withOperand(parseExpression(ctx.expressionTerm(0)), parseExpression(ctx.expressionTerm(1)));
            case "milliseconds":
                return of.createMillisecondsBetween()
                        .withOperand(parseExpression(ctx.expressionTerm(0)), parseExpression(ctx.expressionTerm(1)));
        }

        return of.createNull();
    }

    @Override
    public Object visitWidthExpressionTerm(@NotNull cqlParser.WidthExpressionTermContext ctx) {
        return of.createWidth().withOperand(parseExpression(ctx.expressionTerm()));
    }

    @Override
    public Expression visitParenthesizedTerm(@NotNull cqlParser.ParenthesizedTermContext ctx) {
        return parseExpression(ctx.expression());
    }

    @Override
    public Object visitMembershipExpression(@NotNull cqlParser.MembershipExpressionContext ctx) {
        String operator = ctx.getChild(1).getText();

        switch (operator) {
            case "in":
                return of.createIn().withOperand(
                        parseExpression(ctx.expression(0)),
                        parseExpression(ctx.expression(1))
                );
            case "contains":
                return of.createContains().withOperand(
                        parseExpression(ctx.expression(0)),
                        parseExpression(ctx.expression(1))
                );
        }

        return of.createNull();
    }

    @Override
    public And visitAndExpression(@NotNull cqlParser.AndExpressionContext ctx) {
        return of.createAnd().withOperand(
                parseExpression(ctx.expression(0)),
                parseExpression(ctx.expression(1)));
    }

    @Override
    public Expression visitOrExpression(@NotNull cqlParser.OrExpressionContext ctx) {
        if (ctx.getChild(1).getText().equals("xor")) {
            return of.createXor().withOperand(
                    parseExpression(ctx.expression(0)),
                    parseExpression(ctx.expression(1)));
        }
        else {
            return of.createOr().withOperand(
                    parseExpression(ctx.expression(0)),
                    parseExpression(ctx.expression(1)));
        }
    }

    @Override
    public Object visitInFixSetExpression(@NotNull cqlParser.InFixSetExpressionContext ctx) {
        String operator = ctx.getChild(1).getText();

        switch (operator) {
            case "union":
                return of.createUnion().withOperand(
                        parseExpression(ctx.expression(0)),
                        parseExpression(ctx.expression(1))
                );
            case "intersect":
                return of.createIntersect().withOperand(
                        parseExpression(ctx.expression(0)),
                        parseExpression(ctx.expression(1))
                );
            case "except":
                return of.createExcept().withOperand(
                        parseExpression(ctx.expression(0)),
                        parseExpression(ctx.expression(1))
                );
        }

        return of.createNull();
    }

    @Override
    public BinaryExpression visitEqualityExpression(@NotNull cqlParser.EqualityExpressionContext ctx) {
        BinaryExpression exp = "=".equals(parseString(ctx.getChild(1))) ? of.createEqual() : of.createNotEqual();
        return exp.withOperand(
                parseExpression(ctx.expression(0)),
                parseExpression(ctx.expression(1)));
    }

    @Override
    public BinaryExpression visitInequalityExpression(@NotNull cqlParser.InequalityExpressionContext ctx) {
        BinaryExpression exp;
        switch(parseString(ctx.getChild(1))) {
            case "<=":
                exp = of.createLessOrEqual();
                break;
            case "<":
                exp = of.createLess();
                break;
            case ">":
                exp = of.createGreater();
                break;
            case ">=":
                exp = of.createGreaterOrEqual();
                break;
            default:
                exp = of.createBinaryExpression();
        }
        return exp.withOperand(
                parseExpression(ctx.expression(0)),
                parseExpression(ctx.expression(1)));
    }

    @Override
    public List<String> visitQualifiedIdentifier(@NotNull cqlParser.QualifiedIdentifierContext ctx) {
        // Return the list of qualified identifiers for resolution by the containing element
        List<String> identifiers = new ArrayList<>();
        for (cqlParser.QualifierContext qualifierContext : ctx.qualifier()) {
            String qualifier = parseString(qualifierContext);
            identifiers.add(qualifier);
        }

        String identifier = parseString(ctx.identifier());
        identifiers.add(identifier);
        return identifiers;
    }

    public Expression resolveAccessor(Expression left, String memberIdentifier) {
        // if left is a LibraryRef
            // if right is an identifier
            // right may be an ExpressionRef, a ValueSetRef, or a ParameterRef -- need to resolve on the referenced library
        // if left is an ExpressionRef
            // if right is an identifier
            // return a Property with the ExpressionRef as source and identifier as Path
        // if left is a Property
            // if right is an identifier
            // modify the Property to append the identifier to the path
        // if left is an AliasRef
            // return a Property with a Path and no source, and Scope set to the Alias
        // if left is an Identifier
            // return a new Identifier with left as a qualifier
        // else
            // return an Identifier for resolution later by a method or accessor

        if (left instanceof LibraryRef) {
            Library referencedLibrary = resolveLibrary(((LibraryRef) left).getLibraryName());

            String expressionName = resolveExpressionName(referencedLibrary, memberIdentifier);
            if (expressionName != null) {
                return of.createExpressionRef()
                        .withLibraryName(((LibraryRef) left).getLibraryName())
                        .withName(memberIdentifier);
            }

            String valuesetName = resolveValuesetName(referencedLibrary, memberIdentifier);
            if (valuesetName != null) {
                return of.createValueSetRef()
                        .withLibraryName(((LibraryRef) left).getLibraryName())
                        .withName(memberIdentifier);
            }

            String parameterName = resolveParameterName(referencedLibrary, memberIdentifier);
            if (parameterName != null) {
                return of.createParameterRef()
                        .withLibraryName(((LibraryRef) left).getLibraryName())
                        .withName(memberIdentifier);
            }

            Identifier identifier = new Identifier();
            identifier.setLibraryName(((LibraryRef)left).getLibraryName());
            identifier.setIdentifier(memberIdentifier);
            return identifier;
        }

        else if (left instanceof ExpressionRef) {
            return of.createProperty()
                    .withSource(left)
                    .withPath(memberIdentifier);
        }

        else if (left instanceof AliasRef) {
            return of.createProperty()
                    .withScope(((AliasRef)left).getName())
                    .withPath(memberIdentifier);
        }

        else if (left instanceof Property) {
            Property property = (Property)left;
            property.setPath(String.format("%s.%s", property.getPath(), memberIdentifier));
            return property;
        }

        else if (left instanceof Identifier) {
            Identifier identifier = (Identifier)left;
            identifier.setIdentifier(String.format("%s.%s", identifier.getIdentifier(), memberIdentifier));
            return identifier;
        }

        // TODO: Error handling, this should throw, or return an Error() or something.

        return of.createNull();
    }

    @Override
    public Expression visitAccessorExpressionTerm(@NotNull cqlParser.AccessorExpressionTermContext ctx) {
        Expression left = parseExpression(ctx.expressionTerm());
        String memberIdentifier = parseString(ctx.identifier());
        return resolveAccessor(left, memberIdentifier);
    }

    public Expression resolveIdentifier(String identifier) {
        // An Identifier will always be:
        // 1: The name of an alias
        // 2: The name of an expression
        // 3: The name of a valueset
        // 4: The name of a parameter
        // 5: The name of a library
        // 6: An unresolved identifier that must be resolved later (by a method or accessor)

        String alias = resolveAlias(identifier);
        if (alias != null) {
            return of.createAliasRef().withName(identifier);
        }

        String expressionName = resolveExpressionName(identifier);
        if (expressionName != null) {
            return of.createExpressionRef().withName(expressionName);
        }

        String valuesetName = resolveValuesetName(identifier);
        if (valuesetName != null) {
            return of.createValueSetRef().withName(valuesetName);
        }

        String parameterName = resolveParameterName(identifier);
        if (parameterName != null) {
            return of.createParameterRef().withName(parameterName);
        }

        String libraryName = resolveLibraryName(identifier);
        if (libraryName != null) {
            LibraryRef libraryRef = new LibraryRef();
            libraryRef.setLibraryName(libraryName);

            return libraryRef;
        }

        Identifier id = new Identifier();
        id.setIdentifier(identifier);
        return id;
    }

    public Expression resolveQualifiedIdentifier(List<String> identifiers) {
        Expression current = null;
        for (String identifier : identifiers) {
            if (current == null) {
                current = resolveIdentifier(identifier);
            }
            else {
                current = resolveAccessor(current, identifier);
            }
        }

        return current;
    }

    @Override
    public Expression visitIdentifierTerm(@NotNull cqlParser.IdentifierTermContext ctx) {
        String identifier = parseString(ctx.identifier());
        return resolveIdentifier(identifier);
    }

    @Override
    public Object visitTerminal(@NotNull TerminalNode node) {
        String text = node.getText();
        int tokenType = node.getSymbol().getType();
        if (cqlLexer.STRING == tokenType || cqlLexer.QUOTEDIDENTIFIER == tokenType) {
            // chop off leading and trailing ' or "
            text = text.substring(1, text.length() - 1);
        }

        return text;
    }
    @Override
    public Object visitTermExpression(@NotNull cqlParser.TermExpressionContext ctx) {
        return visit(ctx.expressionTerm());
    }

    @Override
    public Object visitConversionExpressionTerm(@NotNull cqlParser.ConversionExpressionTermContext ctx) {
        return of.createConvert().withOperand(parseExpression(ctx.expression()))
                .withToType(resolveTypeSpecifierToQName(parseTypeSpecifier(ctx.typeSpecifier())));
    }

    @Override
    public Object visitTypeExpression(@NotNull cqlParser.TypeExpressionContext ctx) {
        if (ctx.getChild(1).getText().equals("is")) {
            return of.createIs()
                    .withOperand(parseExpression(ctx.expression()))
                    .withIsTypeSpecifier(parseTypeSpecifier(ctx.typeSpecifier()));
        }

        return of.createAs()
                .withOperand(parseExpression(ctx.expression()))
                .withAsTypeSpecifier(parseTypeSpecifier(ctx.typeSpecifier()))
                .withStrict(false);
    }

    @Override
    public Object visitCastExpression(@NotNull cqlParser.CastExpressionContext ctx) {
        return of.createAs()
                .withOperand(parseExpression(ctx.expression()))
                .withAsTypeSpecifier(parseTypeSpecifier(ctx.typeSpecifier()))
                .withStrict(true);
    }

    @Override
    public Expression visitBooleanExpression(@NotNull cqlParser.BooleanExpressionContext ctx) {
        Expression exp;
        Expression left = (Expression) visit(ctx.expression());
        String lastChild = ctx.getChild(ctx.getChildCount() - 1).getText();
        String nextToLast = ctx.getChild(ctx.getChildCount() - 2).getText();
        if (lastChild.equals("null")) {
            exp = of.createIsNull().withOperand(left);
        } else {
            exp = of.createEqual().withOperand(left, createLiteral(Boolean.valueOf(lastChild)));
        }
        if ("not".equals(nextToLast)) {
            exp = of.createNot().withOperand(exp);
        }

        return exp;
    }

    @Override
    public Object visitTimingExpression(@NotNull cqlParser.TimingExpressionContext ctx) {
        Expression left = parseExpression(ctx.expression(0));
        Expression right = parseExpression(ctx.expression(1));
        TimingOperatorContext timingOperatorContext = new TimingOperatorContext(left, right);
        timingOperators.push(timingOperatorContext);
        try {
            return visit(ctx.intervalOperatorPhrase());
        }
        finally {
            timingOperators.pop();
        }
    }

    @Override
    public Object visitConcurrentWithIntervalOperatorPhrase(@NotNull cqlParser.ConcurrentWithIntervalOperatorPhraseContext ctx) {
        // ('starts' | 'ends')? relativeQualifier? 'same' dateTimePrecision? 'as' ('start' | 'end')?

        TimingOperatorContext timingOperator = timingOperators.peek();
        ParseTree firstChild = ctx.getChild(0);
        if ("starts".equals(firstChild.getText())) {
            timingOperator.setLeft(of.createStart().withOperand(timingOperator.getLeft()));
        }

        if ("ends".equals(firstChild.getText())) {
            timingOperator.setLeft(of.createEnd().withOperand(timingOperator.getLeft()));
        }

        ParseTree lastChild = ctx.getChild(ctx.getChildCount() - 1);
        if ("start".equals(lastChild.getText())) {
            timingOperator.setRight(of.createStart().withOperand(timingOperator.getRight()));
        }

        if ("end".equals(lastChild.getText())) {
            timingOperator.setRight(of.createEnd().withOperand(timingOperator.getRight()));
        }

        BinaryExpression operator = null;
        if (ctx.dateTimePrecision() != null) {
            switch (ctx.dateTimePrecision().getText()) {
                case "year": operator = of.createSameYearAs(); break;
                case "month": operator = of.createSameMonthAs(); break;
                case "day": operator = of.createSameDayAs(); break;
                case "hour": operator = of.createSameHourAs(); break;
                case "minute": operator = of.createSameMinuteAs(); break;
                case "second": operator = of.createSameSecondAs(); break;
                // NOTE: No milliseconds here, because same millisecond as is equivalent to same as
                default: operator = of.createSameAs(); break;
            }
        }

        if (operator == null) {
            operator = of.createSameAs();
        }

        operator = operator.withOperand(timingOperator.getLeft(), timingOperator.getRight());

        if (ctx.relativeQualifier() != null) {
            switch (ctx.relativeQualifier().getText()) {
                case "or after":
                    return of.createOr().withOperand(
                            operator,
                            of.createGreater().withOperand(timingOperator.getLeft(), timingOperator.getRight())
                    );

                case "or before":
                    return of.createOr().withOperand(
                            operator,
                            of.createLess().withOperand(timingOperator.getLeft(), timingOperator.getRight())
                    );
            }
        }

        return operator;
    }

    @Override
    public Object visitIncludesIntervalOperatorPhrase(@NotNull cqlParser.IncludesIntervalOperatorPhraseContext ctx) {
        // properly? includes (start | end)?
        boolean isProper = false;
        boolean isRightPoint = false;
        TimingOperatorContext timingOperator = timingOperators.peek();
        for (ParseTree pt : ctx.children) {
            if ("properly".equals(pt.getText())) {
                isProper = true;
                continue;
            }

            if ("start".equals(pt.getText())) {
                timingOperator.setRight(of.createStart().withOperand(timingOperator.getRight()));
                isRightPoint = true;
                continue;
            }

            if ("end".equals(pt.getText())) {
                timingOperator.setRight(of.createEnd().withOperand(timingOperator.getRight()));
                isRightPoint = true;
                continue;
            }
        }

        if (isRightPoint) {
            // TODO: Handle is proper (no ELM representation for ProperContains)
            return of.createContains().withOperand(timingOperator.getLeft(), timingOperator.getRight());
        }

        if (isProper) {
            return of.createProperIncludes().withOperand(timingOperator.getLeft(), timingOperator.getRight());
        }

        return of.createIncludes().withOperand(timingOperator.getLeft(), timingOperator.getRight());
    }

    @Override
    public Object visitIncludedInIntervalOperatorPhrase(@NotNull cqlParser.IncludedInIntervalOperatorPhraseContext ctx) {
        // (starts | ends)? properly? (during | included in)
        boolean isProper = false;
        boolean isLeftPoint = false;
        TimingOperatorContext timingOperator = timingOperators.peek();
        for (ParseTree pt : ctx.children) {
            if ("starts".equals(pt.getText())) {
                timingOperator.setLeft(of.createStart().withOperand(timingOperator.getLeft()));
                isLeftPoint = true;
                continue;
            }

            if ("ends".equals(pt.getText())) {
                timingOperator.setLeft(of.createEnd().withOperand(timingOperator.getLeft()));
                isLeftPoint = true;
                continue;
            }

            if ("properly".equals(pt.getText())) {
                isProper = true;
                continue;
            }
        }

        if (isLeftPoint) {
            // TODO: Handle is proper (no ELM representation for ProperIn)
            return of.createIn().withOperand(timingOperator.getLeft(), timingOperator.getRight());
        }

        if (isProper) {
            return of.createProperIncludedIn().withOperand(timingOperator.getLeft(), timingOperator.getRight());
        }

        return of.createIncludedIn().withOperand(timingOperator.getLeft(), timingOperator.getRight());
    }

    @Override
    public Object visitBeforeOrAfterIntervalOperatorPhrase(@NotNull cqlParser.BeforeOrAfterIntervalOperatorPhraseContext ctx) {
        // ('starts' | 'ends')? quantityOffset? ('before' | 'after') ('start' | 'end')?

        // duration before/after
        // A starts 3 days before start B
        // days between start of A and start of B = 3
        // A starts 3 days after start B
        // days between start of A and start of B = -3

        // or more/less duration before/after
        // A starts 3 days or more before start B
        // days between start of A and start of B >= 3
        // A starts 3 days or more after start B
        // days between start of A and start of B <= -3
        // A starts 3 days or less before start B
        // days between start of A and start of B in (0, 3]
        // A starts 3 days or less after start B
        // days between start of A and start of B in [-3, 0)

        TimingOperatorContext timingOperator = timingOperators.peek();
        Boolean isBefore = false;
        for (ParseTree child : ctx.children) {
            if ("starts".equals(child.getText())) {
                timingOperator.setLeft(of.createStart().withOperand(timingOperator.getLeft()));
                continue;
            }

            if ("ends".equals(child.getText())) {
                timingOperator.setLeft(of.createStart().withOperand(timingOperator.getLeft()));
                continue;
            }

            if ("start".equals(child.getText())) {
                timingOperator.setRight(of.createStart().withOperand(timingOperator.getRight()));
                continue;
            }

            if ("end".equals(child.getText())) {
                timingOperator.setRight(of.createEnd().withOperand(timingOperator.getRight()));
                continue;
            }

            if ("before".equals(child.getText())) {
                isBefore = true;
                continue;
            }
        }

        if (ctx.quantityOffset() == null) {
            if (isBefore) {
                return of.createBefore().withOperand(timingOperator.getLeft(), timingOperator.getRight());
            }
            else {
                return of.createAfter().withOperand(timingOperator.getLeft(), timingOperator.getRight());
            }
        }
        else {
            Quantity quantity = (Quantity)visit(ctx.quantityOffset().quantityLiteral());
            Literal quantityLiteral = createLiteral(quantity.getValue().intValueExact());
            BinaryExpression betweenOperator = resolveBetweenOperator(quantity.getUnit(),
                    timingOperator.getLeft(), timingOperator.getRight());
            if (betweenOperator != null) {
                if (ctx.quantityOffset().offsetRelativeQualifier() == null) {
                    if (isBefore) {
                        return of.createEqual().withOperand(
                                betweenOperator,
                                quantityLiteral
                        );
                    }
                    else {
                        return of.createEqual().withOperand(
                                betweenOperator,
                                of.createNegate().withOperand(quantityLiteral)
                        );
                    }
                }
                else {
                    switch (ctx.quantityOffset().offsetRelativeQualifier().getText()) {
                        case "or more":
                            if (isBefore) {
                                return of.createGreaterOrEqual().withOperand(
                                        betweenOperator,
                                        quantityLiteral
                                );
                            }
                            else {
                                return of.createLessOrEqual().withOperand(
                                        betweenOperator,
                                        of.createNegate().withOperand(quantityLiteral)
                                );
                            }
                        case "or less":
                            if (isBefore) {
                                Interval quantityInterval = of.createInterval()
                                        .withLow(createLiteral(0)).withLowClosed(false)
                                        .withHigh(quantityLiteral).withHighClosed(true);
                                return of.createIn().withOperand(
                                        betweenOperator,
                                        quantityInterval
                                );
                            }
                            else {
                                Interval quantityInterval = of.createInterval()
                                        .withLow(of.createNegate().withOperand(quantityLiteral)).withLowClosed(true)
                                        .withHigh(createLiteral(0)).withHighClosed(false);
                                return of.createIn().withOperand(
                                        betweenOperator,
                                        quantityInterval
                                );
                            }
                    }
                }
            }
        }

        // TODO: Error handling
        return of.createNull();
    }

    private BinaryExpression resolveBetweenOperator(String unit, Expression left, Expression right) {
        if (unit != null) {
            switch (unit) {
                case "year":
                case "years": return of.createYearsBetween().withOperand(left, right);
                case "month":
                case "months": return of.createMonthsBetween().withOperand(left, right);
                case "week":
                case "weeks": return of.createMultiply().withOperand(
                        of.createDaysBetween().withOperand(left, right),
                        createLiteral(7));
                case "day":
                case "days": return of.createDaysBetween().withOperand(left, right);
                case "hour":
                case "hours": return of.createHoursBetween().withOperand(left, right);
                case "minute":
                case "minutes": return of.createMinutesBetween().withOperand(left, right);
                case "second":
                case "seconds": return of.createSecondsBetween().withOperand(left, right);
                case "millisecond":
                case "milliseconds": return of.createMillisecondsBetween().withOperand(left, right);
            }
        }

        return null;
    }

    @Override
    public Object visitWithinIntervalOperatorPhrase(@NotNull cqlParser.WithinIntervalOperatorPhraseContext ctx) {
        // ('starts' | 'ends')? 'properly'? 'within' quantityLiteral 'of' ('start' | 'end')?
        // A starts within 3 days of start B
        // days between start of A and start of B in [-3, 3]

        TimingOperatorContext timingOperator = timingOperators.peek();
        boolean isProper = false;
        for (ParseTree child : ctx.children) {
            if ("starts".equals(child.getText())) {
                timingOperator.setLeft(of.createStart().withOperand(timingOperator.getLeft()));
                continue;
            }

            if ("ends".equals(child.getText())) {
                timingOperator.setLeft(of.createStart().withOperand(timingOperator.getLeft()));
                continue;
            }

            if ("start".equals(child.getText())) {
                timingOperator.setRight(of.createStart().withOperand(timingOperator.getRight()));
                continue;
            }

            if ("end".equals(child.getText())) {
                timingOperator.setRight(of.createEnd().withOperand(timingOperator.getRight()));
                continue;
            }

            if ("properly".equals(child.getText())) {
                isProper = true;
                continue;
            }
        }

        Quantity quantity = (Quantity)visit(ctx.quantityLiteral());
        Literal quantityLiteral = createLiteral(quantity.getValue().intValueExact());
        Interval quantityInterval = of.createInterval()
                .withLow(of.createNegate().withOperand(quantityLiteral)).withLowClosed(!isProper)
                .withHigh(quantityLiteral).withHighClosed(!isProper);
        BinaryExpression betweenOperator = resolveBetweenOperator(quantity.getUnit(),
                timingOperator.getLeft(), timingOperator.getRight());
        if (betweenOperator != null) {
            return of.createIn().withOperand(betweenOperator, quantityInterval);
        }

        // TODO: Error handling
        return of.createNull();
    }

    @Override
    public Object visitMeetsIntervalOperatorPhrase(@NotNull cqlParser.MeetsIntervalOperatorPhraseContext ctx) {
        BinaryExpression operator;
        if (ctx.getChildCount() == 1) {
            operator = of.createMeets();
        }
        else {
            if ("before".equals(ctx.getChild(1).getText())) {
                operator = of.createMeetsBefore();
            }
            else {
                operator = of.createMeetsAfter();
            }
        }

        return operator.withOperand(timingOperators.peek().getLeft(), timingOperators.peek().getRight());
    }

    @Override
    public Object visitOverlapsIntervalOperatorPhrase(@NotNull cqlParser.OverlapsIntervalOperatorPhraseContext ctx) {
        BinaryExpression operator;
        if (ctx.getChildCount() == 1) {
            operator = of.createOverlaps();
        }
        else {
            if ("before".equals(ctx.getChild(1).getText())) {
                operator = of.createOverlapsBefore();
            }
            else {
                operator = of.createOverlapsAfter();
            }
        }

        return operator.withOperand(timingOperators.peek().getLeft(), timingOperators.peek().getRight());
    }

    @Override
    public Object visitStartsIntervalOperatorPhrase(@NotNull cqlParser.StartsIntervalOperatorPhraseContext ctx) {
        return of.createStarts().withOperand(timingOperators.peek().getLeft(), timingOperators.peek().getRight());
    }

    @Override
    public Object visitEndsIntervalOperatorPhrase(@NotNull cqlParser.EndsIntervalOperatorPhraseContext ctx) {
        return of.createEnds().withOperand(timingOperators.peek().getLeft(), timingOperators.peek().getRight());
    }

    @Override
    public Object visitIfThenElseExpressionTerm(@NotNull cqlParser.IfThenElseExpressionTermContext ctx) {
        return of.createIf()
                .withCondition(parseExpression(ctx.expression(0)))
                .withThen(parseExpression(ctx.expression(1)))
                .withElse(parseExpression(ctx.expression(2)));
    }

    @Override
    public Object visitCaseExpressionTerm(@NotNull cqlParser.CaseExpressionTermContext ctx) {
        Case result = of.createCase();
        Boolean hitElse = false;
        for (ParseTree pt : ctx.children) {
            if ("else".equals(pt.getText())) {
                hitElse = true;
                continue;
            }

            if (pt instanceof cqlParser.ExpressionContext) {
                if (hitElse) {
                    result.setElse(parseExpression(pt));
                }
                else {
                    result.setComparand(parseExpression(pt));
                }
            }

            if (pt instanceof cqlParser.CaseExpressionItemContext) {
                result.getCaseItem().add((CaseItem)visit(pt));
            }
        }

        return result;
    }

    @Override
    public Object visitCaseExpressionItem(@NotNull cqlParser.CaseExpressionItemContext ctx) {
        return of.createCaseItem()
                .withWhen(parseExpression(ctx.expression(0)))
                .withThen(parseExpression(ctx.expression(1)));
    }

    @Override
    public Object visitCoalesceExpressionTerm(@NotNull cqlParser.CoalesceExpressionTermContext ctx) {
        List<Expression> expressions = new ArrayList<>();

        for (cqlParser.ExpressionContext expression : ctx.expression()) {
            expressions.add(parseExpression(expression));
        }

        return of.createCoalesce().withOperand(expressions);
    }

    @Override
    public Object visitAggregateExpressionTerm(@NotNull cqlParser.AggregateExpressionTermContext ctx) {
        switch (ctx.getChild(0).getText()) {
            case "distinct": return of.createDistinct().withSource(parseExpression(ctx.expression()));
            case "collapse": return of.createCollapse().withOperand(parseExpression(ctx.expression()));
            case "expand": return of.createExpand().withOperand(parseExpression(ctx.expression()));
        }

        // TODO: Error-handling
        return of.createNull();
    }

    @Override
    public Retrieve visitRetrieve(@NotNull cqlParser.RetrieveContext ctx) {
        // TODO: Model prefixes are currently ignored here...
        String occ = ctx.occurrence() != null ? parseString(ctx.occurrence().namedTypeSpecifier().identifier()) : "Occurrence"; // TODO: Default occurrence label by model?
        String topic = parseString(ctx.topic().namedTypeSpecifier().identifier());
        String modality = ctx.modality() != null ? parseString(ctx.modality().namedTypeSpecifier().identifier()) : "";
        ClassDetail detail = getModelHelper().getClassDetail(occ, topic, modality);

        Retrieve retrieve = of.createRetrieve()
                .withDataType(resolveNamedType(
                        detail != null
                                ? detail.getClassInfo().getName()
                                : String.format("%s%s%s", topic, modality, occ)));

        if (ctx.valueset() != null) {
            if (ctx.valuesetPathIdentifier() != null) {
                retrieve.setCodeProperty(parseString(ctx.valuesetPathIdentifier()));
            }
            else if (detail != null && detail.getClassInfo().getPrimaryCodeAttribute() != null) {
                retrieve.setCodeProperty(detail.getClassInfo().getPrimaryCodeAttribute());
            }

            List<String> identifiers = (List<String>)visit(ctx.valueset());
            retrieve.setCodes(resolveQualifiedIdentifier(identifiers));
        }

        retrieves.add(retrieve);

        return retrieve;
    }

    @Override
    public Object visitSingleSourceClause(@NotNull cqlParser.SingleSourceClauseContext ctx) {
        List<AliasedQuerySource> sources = new ArrayList<>();
        sources.add((AliasedQuerySource)visit(ctx.aliasedQuerySource()));
        return sources;
    }

    @Override
    public Object visitMultipleSourceClause(@NotNull cqlParser.MultipleSourceClauseContext ctx) {
        List<AliasedQuerySource> sources = new ArrayList<>();
        for (cqlParser.AliasedQuerySourceContext source : ctx.aliasedQuerySource()) {
            sources.add((AliasedQuerySource)visit(source));
        }
        return sources;
    }

    @Override
    public Object visitQuery(@NotNull cqlParser.QueryContext ctx) {
        QueryContext queryContext = new QueryContext();
        List<AliasedQuerySource> sources = (List<AliasedQuerySource>)visit(ctx.sourceClause());
        for (AliasedQuerySource aqs : sources) {
            queryContext.addQuerySource(aqs);
        }
        queries.push(queryContext);
        try {

            List<DefineClause> dfcx = ctx.defineClause() != null ? (List<DefineClause>)visit(ctx.defineClause()) : null;
            // TODO: Add defined items to the query context and identifier resolution

            List<RelationshipClause> qicx = new ArrayList<>();
            if (ctx.queryInclusionClause() != null) {
                for (cqlParser.QueryInclusionClauseContext queryInclusionClauseContext : ctx.queryInclusionClause()) {
                    qicx.add((RelationshipClause) visit(queryInclusionClauseContext));
                }
            }

            Expression where = ctx.whereClause() != null ? (Expression) visit(ctx.whereClause()) : null;
            if (dateRangeOptimization && where != null) {
                for (AliasedQuerySource aqs : sources) {
                    where = optimizeDateRangeInQuery(where, aqs);
                }
            }

            Expression ret = ctx.returnClause() != null ? (Expression) visit(ctx.returnClause()) : null;
            SortClause sort = ctx.sortClause() != null ? (SortClause) visit(ctx.sortClause()) : null;

            return of.createQuery()
                    .withSource(sources)
                    .withDefine(dfcx)
                    .withRelationship(qicx)
                    .withWhere(where)
                    .withReturn(ret)
                    .withSort(sort);
        }
        finally {
            queries.pop();
        }
    }

    /**
     * Some systems may wish to optimize performance by restricting retrieves with available date ranges.  Specifying
     * date ranges in a retrieve was removed from the CQL grammar, but it is still possible to extract date ranges from
     * the where clause and put them in the Retrieve in ELM.  The <code>optimizeDateRangeInQuery</code> method
     * attempts to do this automatically.  If optimization is possible, it will remove the corresponding "during" from
     * the where clause and insert the date range into the Retrieve.
     *
     * @param aqs the AliasedQuerySource containing the ClinicalRequest to possibly refactor a date range into.
     * @param where the Where clause to search for potential date range optimizations
     * @return the where clause with optimized "durings" removed, or <code>null</code> if there is no longer a Where
     * clause after optimization.
     */
    private Expression optimizeDateRangeInQuery(Expression where, AliasedQuerySource aqs) {
        if (aqs.getExpression() instanceof Retrieve) {
            Retrieve retrieve = (Retrieve) aqs.getExpression();
            String alias = aqs.getAlias();
            if (where instanceof IncludedIn && attemptDateRangeOptimization((IncludedIn) where, retrieve, alias)) {
                where = null;
            } else if (where instanceof And && attemptDateRangeOptimization((And) where, retrieve, alias)) {
                // Now optimize out the trues from the Ands
                where = consolidateAnd((And) where);
            }
        }
        return where;
    }

    /**
     * Test an <code>IncludedIn</code> expression and determine if it is suitable to be refactored into the
     * <code>Retrieve</code> as a date range restriction.  If so, adjust the <code>Retrieve</code>
     * accordingly and return <code>true</code>.
     * @param during the <code>IncludedIn</code> expression to potentially refactor into the <code>Retrieve</code>
     * @param retrieve the <code>Retrieve</code> to add qualifying date ranges to (if applicable)
     * @param alias the alias of the <code>Retrieve</code> in the query.
     * @return <code>true</code> if the date range was set in the <code>Retrieve</code>; <code>false</code>
     * otherwise.
     */
    private boolean attemptDateRangeOptimization(IncludedIn during, Retrieve retrieve, String alias) {
        if (retrieve.getDateProperty() != null || retrieve.getDateRange() != null) {
            return false;
        }

        Expression left = during.getOperand().get(0);
        Expression right = during.getOperand().get(1);

        String propertyName = extractLHSPropertyNameEligibleForDateRangeOptimization(left, retrieve, alias);
        if (propertyName != null && isRHSEligibleForDateRangeOptimization(right)) {
            retrieve.setDateProperty(propertyName);
            retrieve.setDateRange(right);
            return true;
        }

        return false;
    }

    /**
     * Test an <code>And</code> expression and determine if it contains any operands (first-level or nested deeper)
     * than are <code>IncludedIn</code> expressions that can be refactored into a <code>Retrieve</code>.  If so,
     * adjust the <code>Retrieve</code> accordingly and reset the corresponding operand to a literal
     * <code>true</code>.  This <code>and</code> branch containing a <code>true</code> can be further consolidated
     * later.
     * @param and the <code>And</code> expression containing operands to potentially refactor into the
     *            <code>Retrieve</code>
     * @param retrieve the <code>Retrieve</code> to add qualifying date ranges to (if applicable)
     * @param alias the alias of the <code>Retrieve</code> in the query.
     * @return <code>true</code> if the date range was set in the <code>Retrieve</code> and the <code>And</code>
     * operands (or sub-operands) were modified; <code>false</code> otherwise.
     */
    private boolean attemptDateRangeOptimization(And and, Retrieve retrieve, String alias) {
        if (retrieve.getDateProperty() != null || retrieve.getDateRange() != null) {
            return false;
        }

        for (int i = 0; i < and.getOperand().size(); i++) {
            Expression operand = and.getOperand().get(i);
            if (operand instanceof IncludedIn && attemptDateRangeOptimization((IncludedIn) operand, retrieve, alias)) {
                // Replace optimized part in And with true -- to be optimized out later
                and.getOperand().set(i, createLiteral(true));
                return true;
            } else if (operand instanceof And && attemptDateRangeOptimization((And) operand, retrieve, alias)) {
                return true;
            }
        }

        return false;
    }

    /**
     * If any branches in the <code>And</code> tree contain a <code>true</code>, refactor it out.
     * @param and the <code>And</code> tree to attempt to consolidate
     * @return the potentially consolidated <code>And</code>
     */
    private Expression consolidateAnd(And and) {
        Expression result = and;
        Expression lhs = and.getOperand().get(0);
        Expression rhs = and.getOperand().get(1);
        if (isBooleanLiteral(lhs, true)) {
            result = rhs;
        } else if (isBooleanLiteral(rhs, true)) {
            result = lhs;
        } else if (lhs instanceof And) {
            and.getOperand().set(0, consolidateAnd((And) lhs));
        } else if (rhs instanceof And) {
            and.getOperand().set(1, consolidateAnd((And) rhs));
        }

        return result;
    }

    /**
     * Extract the property name from the left-hand side of an <code>IncludedIn</code>.  If no property is explicitly
     * stated, try to infer it from the model info.
     * @param lhs the left-hand-side of an <code>IncludedIn</code> expression
     * @param retrieve the <code>Retrieve</code> from the query containing the <code>IncludedIn</code>
     * @param alias the alias associated with the <code>Retrieve</code> in the query
     * @return the property name if available and eligible for potential refactoring to the Retrieve
     */
    private String extractLHSPropertyNameEligibleForDateRangeOptimization(Expression lhs, Retrieve retrieve, String alias) {
        String propertyName = null;
        if (lhs instanceof AliasRef) {
            QName datatype = retrieve.getDataType();
            if (getModelHelper().getModelInfo().getUrl().equals(datatype.getNamespaceURI())) {
                for (ClassInfo info : getModelHelper().getModelInfo().getClassInfo()) {
                    if (datatype.getLocalPart().equals(info.getName())) {
                        lhs = of.createProperty().withScope(((AliasRef) lhs).getName()).withPath(info.getPrimaryDateAttribute());
                        break;
                    }
                }
            }
        }

        if (lhs instanceof Property) {
            Property property = (Property) lhs;
            if (property.getScope() != null && property.getScope().equals(alias)) {
                propertyName = property.getPath();
            }
        }

        return propertyName;
    }

    /**
     * Determine if the right-hand side of an <code>IncludedIn</code> expression can be refactored into the date range
     * of a <code>Retrieve</code>.  Currently, refactoring is only supported when the RHS is a literal
     * DateTime interval, a literal DateTime, a parameter representing a DateTime interval or a DateTime, or an
     * expression reference representing a DateTime interval or a DateTime.
     * @param rhs the right-hand side of the <code>IncludedIn</code> to test for potential optimization
     * @return <code>true</code> if the RHS supports refactoring to a <code>Retrieve</code>, <code>false</code>
     * otherwise.
     */
    private boolean isRHSEligibleForDateRangeOptimization(Expression rhs) {
        Element targetElement = rhs;
        if (rhs instanceof ParameterRef) {
            String paramName = ((ParameterRef) rhs).getName();
            for (ParameterDef def : getLibrary().getParameters().getDef()) {
                if (paramName.equals(def.getName())) {
                    targetElement = def.getParameterTypeSpecifier();
                    if (targetElement == null) {
                        targetElement = def.getDefault();
                    }
                    break;
                }
            }
        } else if (rhs instanceof ExpressionRef && ! (rhs instanceof FunctionRef)) {
            // TODO: Support forward declaration, if necessary
            String expName = ((ExpressionRef) rhs).getName();
            for (ExpressionDef def : getLibrary().getStatements().getDef()) {
                if (expName.equals(def.getName())) {
                    targetElement = def.getExpression();
                }
            }
        }

        boolean isEligible = false;
        if (targetElement instanceof Interval) {
            Interval ivl = (Interval) targetElement;
            isEligible = isDateFunctionRef(ivl.getLow()) && isDateFunctionRef(ivl.getHigh());
        } else if (targetElement instanceof IntervalTypeSpecifier) {
            IntervalTypeSpecifier spec = (IntervalTypeSpecifier) targetElement;
            isEligible = isDateTimeTypeSpecifier(spec.getPointType());
        } else if (targetElement instanceof FunctionRef) {
            isEligible = isDateFunctionRef(targetElement);
        } else if (targetElement instanceof NamedTypeSpecifier) {
            isEligible = isDateTimeTypeSpecifier(targetElement);
        }
        return isEligible;
    }

    private boolean isDateFunctionRef(Element e) {
        return e != null && e instanceof FunctionRef && "Date".equals(((FunctionRef) e).getName());
    }

    private boolean isDateTimeTypeSpecifier(Element e) {
        boolean result = false;
        if (e instanceof NamedTypeSpecifier) {
            QName type = ((NamedTypeSpecifier) e).getName();
            result = "http://www.w3.org/2001/XMLSchema".equals(type.getNamespaceURI()) && "datetime".equals(type.getLocalPart());
        }

        return result;
    }

    @Override
    public Object visitDefineClause(@NotNull cqlParser.DefineClauseContext ctx) {
        List<DefineClause> defineClauseItems = new ArrayList<>();
        for (cqlParser.DefineClauseItemContext defineClauseItem : ctx.defineClauseItem()) {
            defineClauseItems.add((DefineClause)visit(defineClauseItem));
        }
        return defineClauseItems;
    }

    @Override
    public Object visitDefineClauseItem(@NotNull cqlParser.DefineClauseItemContext ctx) {
        return of.createDefineClause().withExpression(parseExpression(ctx.expression()))
                .withIdentifier(parseString(ctx.identifier()));
    }

    @Override
    public Object visitAliasedQuerySource(@NotNull cqlParser.AliasedQuerySourceContext ctx) {
        return of.createAliasedQuerySource().withExpression(parseExpression(ctx.querySource()))
                .withAlias(parseString(ctx.alias()));
    }

    @Override
    public Object visitWithClause(@NotNull cqlParser.WithClauseContext ctx) {
        AliasedQuerySource aqs = (AliasedQuerySource)visit(ctx.aliasedQuerySource());
        queries.peek().addQuerySource(aqs);
        try {
            Expression expression = (Expression)visit(ctx.expression());
            RelationshipClause result = of.createWith();
            return result.withExpression(aqs.getExpression()).withAlias(aqs.getAlias()).withSuchThat(expression);
        }
        finally {
            queries.peek().removeQuerySource(aqs);
        }
    }

    @Override
    public Object visitWithoutClause(@NotNull cqlParser.WithoutClauseContext ctx) {
        AliasedQuerySource aqs = (AliasedQuerySource)visit(ctx.aliasedQuerySource());
        queries.peek().addQuerySource(aqs);
        try {
            Expression expression = (Expression)visit(ctx.expression());
            RelationshipClause result = of.createWithout();
            return result.withExpression(aqs.getExpression()).withAlias(aqs.getAlias()).withSuchThat(expression);
        }
        finally {
            queries.peek().removeQuerySource(aqs);
        }
    }

    @Override
    public Object visitWhereClause(@NotNull cqlParser.WhereClauseContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Object visitReturnClause(@NotNull cqlParser.ReturnClauseContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public SortDirection visitSortDirection(@NotNull cqlParser.SortDirectionContext ctx) {
        if (ctx.getText().equals("desc")) {
            return SortDirection.DESC;
        }

        return SortDirection.ASC;
    }

    private SortDirection parseSortDirection(cqlParser.SortDirectionContext ctx) {
        if (ctx != null) {
            return visitSortDirection(ctx);
        }

        return SortDirection.ASC;
    }

    @Override
    public SortByItem visitSortByItem(@NotNull cqlParser.SortByItemContext ctx) {
        return of.createByExpression()
                .withExpression(parseExpression(ctx.expressionTerm()))
                .withDirection(parseSortDirection(ctx.sortDirection()));
    }

    @Override
    public Object visitSortClause(@NotNull cqlParser.SortClauseContext ctx) {
        if (ctx.sortDirection() != null) {
            return of.createSortClause()
                    .withBy(of.createByDirection().withDirection(parseSortDirection(ctx.sortDirection())));
        }

        List<SortByItem> sortItems = new ArrayList<>();
        if (ctx.sortByItem() != null) {
            for (cqlParser.SortByItemContext sortByItemContext : ctx.sortByItem()) {
                sortItems.add((SortByItem) visit(sortByItemContext));
            }
        }

        return of.createSortClause().withBy(sortItems);
    }

    @Override
    public Object visitQuerySource(@NotNull cqlParser.QuerySourceContext ctx) {
        if (ctx.expression() != null) {
            return visit(ctx.expression());
        }
        else if (ctx.retrieve() != null) {
            return visit(ctx.retrieve());
        }
        else {
            List<String> identifiers = (List<String>)visit(ctx.qualifiedIdentifier());
            return resolveQualifiedIdentifier(identifiers);
        }
    }

    @Override
    public Object visitIndexedExpressionTerm(@NotNull cqlParser.IndexedExpressionTermContext ctx) {
        return of.createIndexer()
                .withOperand(parseExpression(ctx.expressionTerm()))
                .withIndex(parseExpression(ctx.expression()));
    }

    @Override
    public Object visitMethodExpressionTerm(@NotNull cqlParser.MethodExpressionTermContext ctx) {
        FunctionRef fun = of.createFunctionRef();
        Expression left = parseExpression(ctx.expressionTerm());

        if (left instanceof Identifier) {
            fun.setLibraryName(((Identifier)left).getLibraryName());
            fun.setName(((Identifier)left).getIdentifier());
        }

        if (ctx.expression() != null) {
            for (cqlParser.ExpressionContext expressionContext : ctx.expression()) {
                fun.getOperand().add((Expression) visit(expressionContext));
            }
        }

        return fun;
    }

    @Override
    public Object visitReturnStatement(@NotNull cqlParser.ReturnStatementContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Object visitFunctionBody(@NotNull cqlParser.FunctionBodyContext ctx) {
        return visit(ctx.returnStatement());
    }

    @Override
    public Object visitFunctionDefinition(@NotNull cqlParser.FunctionDefinitionContext ctx) {
        FunctionDef fun = of.createFunctionDef().withName(parseString(ctx.identifier()));
        if (ctx.operandDefinition() != null) {
            for (cqlParser.OperandDefinitionContext opdef : ctx.operandDefinition()) {
                fun.getParameter().add(
                        of.createParameterDef()
                                .withName(parseString(opdef.identifier()))
                                .withParameterTypeSpecifier(parseTypeSpecifier(opdef.typeSpecifier()))
                );
            }
        }
        fun.setExpression(parseExpression(ctx.functionBody()));
        fun.setContext(currentContext);
        addToLibrary(fun);

        return fun;
    }

    private UsingDef initializeModelHelper(String identifier) {
        // TODO: This should load from a modelinfo file based on the modelIdentifier above. Hard-coding to QUICK for POC purposes.
        try {
            modelHelper = new ModelHelper(QuickModelHelper.load());
        }
        catch (ClassNotFoundException e) {
            // TODO: Should never occur...
            System.err.println("Couldn't load QuickModelHelper!");
            e.printStackTrace();
        }

        // TODO: Needs to write xmlns and schemalocation to the resulting ELM XML document...

        UsingDef usingDef = of.createUsingDef()
                .withLocalIdentifier(identifier)
                .withUri(modelHelper.getModelInfo().getUrl());
                // TODO: .withVersion? Or will version be part of the resolved Url?
        addToLibrary(usingDef);

        return usingDef;
    }

    private ModelHelper getModelHelper() {
        if (modelHelper == null) {
            // No model declared, so default to QUICK
            initializeModelHelper("QUICK");
        }

        return modelHelper;
    }

    private String parseString(ParseTree pt) {
        return pt == null ? null : (String)visit(pt);
    }

    private Expression parseExpression(ParseTree pt) {
        return pt == null ? null : (Expression)visit(pt);
    }

    private TypeSpecifier parseTypeSpecifier(ParseTree pt) {
        return pt == null ? null : (TypeSpecifier)visit(pt);
    }

    private String resolveSystemNamedType(String typeName) {
        switch (typeName) {
            case "Boolean": return "bool";
            case "Integer": return "int";
            case "Decimal": return "decimal";
            case "String": return "string";
            case "DateTime": return "datetime";
            default: return null;
        }
    }

    private QName resolveAxisType(String occurrence, String topic, String modality) {
        ClassDetail detail = getModelHelper().getClassDetail(occurrence, topic, modality);

        if (detail != null) {
            return resolveNamedType(detail.getClassInfo().getName());
        }

        return resolveNamedType(String.format("%s%s%s", topic, modality, occurrence));
    }

    private QName resolveTypeSpecifierToQName(TypeSpecifier typeSpecifier) {
        if (typeSpecifier instanceof NamedTypeSpecifier) {
            return ((NamedTypeSpecifier)typeSpecifier).getName();
        }

        return resolveNamedType("SIMPLE_TYPE_REQUIRED"); // Should throw?
    }

    private QName resolveNamedType(String typeName) {
        // Resolve system primitive types first
        String className = resolveSystemNamedType(typeName);
        if (className != null) {
            return new QName("http://www.w3.org/2001/XMLSchema", className);
        }

        // TODO: Should attempt resolution in all models and throw if ambiguous
        // Model qualifier should be required to resolve ambiguity
        // Requires CQL change to allow qualifiers in atomicTypeSpecifier (which should really be called namedTypeSpecifier)
        className = getModelHelper().resolveClassName(typeName);

        if (className != null) {
            return new QName(getModelHelper().getModelInfo().getUrl(), className);
        }

        // TODO: Error-handling
        return new QName("http://www.w3.org/2001/XMLSchema", typeName);
    }

    private Literal createLiteral(String val, String type) {
        return of.createLiteral().withValue(val).withValueType(resolveNamedType(type));
    }

    private Literal createLiteral(String string) {
        return createLiteral(String.valueOf(string), "String");
    }

    private Literal createLiteral(Boolean bool) {
        return createLiteral(String.valueOf(bool), "Boolean");
    }

    private Literal createLiteral(Integer integer) {
        return createLiteral(String.valueOf(integer), "Integer");
    }

    private boolean isBooleanLiteral(Expression expression, Boolean bool) {
        boolean ret = false;
        if (expression instanceof Literal) {
            Literal lit = (Literal) expression;
            ret = lit.getValueType().equals(resolveNamedType("Boolean"));
            if (ret && bool != null) {
                ret = bool.equals(Boolean.valueOf(lit.getValue()));
            }
        }
        return ret;
    }

    private String resolveAlias(String identifier) {
        for (QueryContext query : queries) {
            AliasedQuerySource source = query.resolveAlias(identifier);
            if (source != null) {
                return source.getAlias();
            }
        }

        return null;
    }

    private void addToLibrary(UsingDef usingDef) {
        if (library.getUsings() == null) {
            library.setUsings(of.createLibraryUsings());
        }
        library.getUsings().getDef().add(usingDef);
    }

    private void addToLibrary(ValueSetDef vs) {
        if (library.getValueSets() == null) {
            library.setValueSets(of.createLibraryValueSets());
        }
        library.getValueSets().getDef().add(vs);
    }

    private void addToLibrary(ExpressionDef expDef) {
        if (library.getStatements() == null) {
            library.setStatements(of.createLibraryStatements());
        }
        library.getStatements().getDef().add(expDef);
    }

    private String resolveValuesetName(String identifier) {
        if (libraryInfo != null) {
            return libraryInfo.resolveValuesetName(identifier);
        }

        return null;
    }

    private String resolveValuesetName(Library library, String identifier) {
        if (library.getValueSets() != null) {
            for (ValueSetDef current : library.getValueSets().getDef()) {
                if (current.getName().equals(identifier)) {
                    return identifier;
                }
            }
        }

        return null;
    }

    private String resolveExpressionName(String identifier) {
        if (libraryInfo != null) {
            return libraryInfo.resolveExpressionName(identifier);
        }

        return null;
    }

    private String resolveExpressionName(Library library, String identifier) {
        if (library.getStatements() != null) {
            for (ExpressionDef current : library.getStatements().getDef()) {
                if (!(current instanceof FunctionDef) && current.getName().equals(identifier)) {
                    return identifier;
                }
            }
        }

        return null;
    }

    private String resolveFunctionName(String identifier) {
        if (libraryInfo != null) {
            return libraryInfo.resolveFunctionName(identifier);
        }

        return null;
    }

    private String resolveFunctionName(Library library, String identifier) {
        if (library.getStatements() != null) {
            for (ExpressionDef current : library.getStatements().getDef()) {
                if (current instanceof FunctionDef && current.getName().equals(identifier)) {
                    return identifier;
                }
            }
        }

        return null;
    }

    private void addToLibrary(ParameterDef paramDef) {
        if (library.getParameters() == null) {
            library.setParameters(of.createLibraryParameters());
        }
        library.getParameters().getDef().add(paramDef);
    }

    private void addExpression(ParseTree ctx, Expression expression) {
        expressions.add(expression);
    }

    private String resolveParameterName(String identifier) {
        if (libraryInfo != null) {
            return libraryInfo.resolveParameterName(identifier);
        }

        return null;
    }

    private String resolveParameterName(Library library, String identifier) {
        if (library.getParameters() != null) {
            for (ParameterDef current : library.getParameters().getDef()) {
                if (current.getName().equals(identifier)) {
                    return identifier;
                }
            }
        }

        return null;
    }

    private void addToLibrary(IncludeDef includeDef) {
        if (library.getIncludes() == null) {
            library.setIncludes(of.createLibraryIncludes());
        }
        library.getIncludes().getDef().add(includeDef);

        Library referencedLibrary =
                new Library()
                        .withIdentifier(
                                new VersionedIdentifier()
                                        .withId(includeDef.getPath())
                                        .withVersion(includeDef.getVersion()));

        // TODO: Resolve and prepare the actual library
        libraries.put(includeDef.getLocalIdentifier(), referencedLibrary);
    }

    private String resolveLibraryName(String identifier) {
        if (library.getIncludes() != null) {
            for (IncludeDef current : library.getIncludes().getDef()) {
                if (current.getLocalIdentifier().equals(identifier)) {
                    return identifier;
                }
            }
        }

        return null;
    }

    private Library resolveLibrary(String identifier) {
        return libraries.get(identifier);
    }

    private TrackBack track(Trackable trackable, ParserRuleContext ctx) {
        TrackBack tb = new TrackBack(
                //of.createVersionedIdentifier().withId(library.getIdentifier().getId()).withVersion(library.getIdentifier().getVersion()),
                library.getIdentifier(),
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine() + 1, // 1-based instead of 0-based
                ctx.getStop().getLine(),
                ctx.getStop().getCharPositionInLine() + ctx.getStop().getText().length() // 1-based instead of 0-based
        );

        trackable.getTrackbacks().add(tb);

        return tb;
    }

    public static void main(String[] args) throws IOException, JAXBException {
        String inputFile = null;
        if (args.length > 0) inputFile = args[0];
        InputStream is = System.in;
        if (inputFile != null) {
            is = new FileInputStream(inputFile);
        }
        ANTLRInputStream input = new ANTLRInputStream(is);
        cqlLexer lexer = new cqlLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        cqlParser parser = new cqlParser(tokens);
        parser.setBuildParseTree(true);
        ParseTree tree = parser.logic();

        CqlPreprocessorVisitor preprocessor = new CqlPreprocessorVisitor();
        preprocessor.visit(tree);

        Cql2ElmVisitor visitor = new Cql2ElmVisitor();
        visitor.setLibraryInfo(preprocessor.getLibraryInfo());
        visitor.setTokenStream(tokens);
        visitor.enableAnnotations();
        visitor.visit(tree);

        /* ToString output
        System.out.println(visitor.getLibrary().toString());
        */

        /* XML output */
        JAXBContext jc = JAXBContext.newInstance(Library.class, Annotation.class, org.hl7.fhir.ClinicalStatement.class);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(new ObjectFactory().createLibrary(visitor.getLibrary()), System.out);
        //JAXB.marshal((new ObjectFactory()).createLibrary(visitor.getLibrary()), System.out);

        /* JSON output
        JAXBContext jc = JAXBContext.newInstance(Library.class);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty("eclipselink.media-type", "application/json");
        marshaller.marshal(new ObjectFactory().createLibrary(visitor.getLibrary()), System.out);
        */
    }
}
