package org.cqframework.cql.poc.translator;

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
import org.cqframework.cql.poc.translator.model.*;
import org.cqframework.cql.poc.translator.preprocessor.CqlPreprocessorVisitor;
import org.cqframework.cql.poc.translator.preprocessor.LibraryInfo;
import org.hl7.cql_annotations.r1.Annotation;
import org.hl7.cql_annotations.r1.Narrative;
import org.hl7.elm.r1.*;
import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.Interval;

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

public class ElmTranslatorVisitor extends cqlBaseVisitor {
    private final ObjectFactory of = new ObjectFactory();
    private final org.hl7.cql_annotations.r1.ObjectFactory af = new org.hl7.cql_annotations.r1.ObjectFactory();
    private boolean annotate = false;

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
    private final List<ClinicalRequest> clinicalRequests = new ArrayList<>();
    private final List<Expression> expressions = new ArrayList<>();
    private ModelHelper modelHelper = null;

    public void enableAnnotations() { annotate = true; }
    public void disableAnnotations() { annotate = false; }
    public TokenStream getTokenStream() { return tokenStream; }
    public void setTokenStream(TokenStream value) { tokenStream = value; }

    public LibraryInfo getLibraryInfo() { return libraryInfo; }
    public void setLibraryInfo(LibraryInfo value) { libraryInfo = value; }

    public Library getLibrary() {
        return library;
    }

    public List<ClinicalRequest> getClinicalRequests() {
        return clinicalRequests;
    }

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
                .withId(parseString(ctx.IDENTIFIER()))
                .withVersion(parseString(ctx.STRING()));
        library.setIdentifier(vid);

        return vid;
    }

    @Override
    public ModelReference visitUsingDefinition(@NotNull cqlParser.UsingDefinitionContext ctx) {
        return initializeModelHelper(parseString(ctx.IDENTIFIER()));
    }

    @Override
    public Object visitIncludeDefinition(@NotNull cqlParser.IncludeDefinitionContext ctx) {
        LibraryReference library = of.createLibraryReference()
                .withName(ctx.IDENTIFIER(1).getText())
                .withPath(ctx.IDENTIFIER(0).getText())
                .withVersion(parseString(ctx.STRING()));

        addToLibrary(library);

        return library;
    }

    @Override
    public ParameterDef visitParameterDefinition(@NotNull cqlParser.ParameterDefinitionContext ctx) {
        ParameterDef param = of.createParameterDef()
                .withName(parseString(ctx.IDENTIFIER()))
                .withDefault(parseExpression(ctx.expression()))
                .withParameterTypeSpecifier(parseTypeSpecifier(ctx.typeSpecifier()));

        addToLibrary(param);

        return param;
    }

    @Override
    public NamedTypeSpecifier visitAtomicTypeSpecifier(@NotNull cqlParser.AtomicTypeSpecifierContext ctx) {
        return of.createNamedTypeSpecifier().withName(resolveNamedType(ctx.IDENTIFIER().getText()));
    }

    @Override
    public PropertyTypeSpecifier visitTupleElementDefinition(@NotNull cqlParser.TupleElementDefinitionContext ctx) {
        return of.createPropertyTypeSpecifier()
                .withName(ctx.IDENTIFIER().getText())
                .withType(parseTypeSpecifier(ctx.typeSpecifier()));
    }

    @Override
    public Object visitTupleTypeSpecifier(@NotNull cqlParser.TupleTypeSpecifierContext ctx) {
        ObjectTypeSpecifier typeSpecifier = of.createObjectTypeSpecifier();
        for (cqlParser.TupleElementDefinitionContext definitionContext : ctx.tupleElementDefinition()) {
            typeSpecifier.getProperty().add((PropertyTypeSpecifier)visit(definitionContext));
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
    public ValueSetDef visitValuesetDefinitionByExpression(@NotNull cqlParser.ValuesetDefinitionByExpressionContext ctx) {
        ValueSetDef vs = of.createValueSetDef()
                .withName(parseString(ctx.VALUESET()))
                .withValueSet(parseExpression(ctx.expression()));
        addToLibrary(vs);

        return vs;
    }

    @Override
    public ValueSetDef visitValuesetDefinitionByConstructor(@NotNull cqlParser.ValuesetDefinitionByConstructorContext ctx) {
        ValueSetDef vs = of.createValueSetDef()
                .withName(parseString(ctx.VALUESET()))
                .withValueSet(of.createValueSet().withId(parseString(ctx.STRING())));
        addToLibrary(vs);

        return vs;
    }

    @Override
    public String visitContextDefinition(@NotNull cqlParser.ContextDefinitionContext ctx) {
        currentContext = parseString(ctx.IDENTIFIER());

        return currentContext;
    }

    @Override
    public ExpressionDef visitExpressionDefinition(@NotNull cqlParser.ExpressionDefinitionContext ctx) {
        ExpressionDef def = of.createExpressionDef()
                .withName(parseString(ctx.IDENTIFIER()))
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
        return createLiteral(Boolean.valueOf(parseString(ctx)));
    }

    @Override
    public Object visitIntervalSelector(@NotNull cqlParser.IntervalSelectorContext ctx) {
        return of.createInterval()
                .withBegin(parseExpression(ctx.expression(0)))
                .withBeginOpen(ctx.getChild(1).getText().equals("("))
                .withEnd(parseExpression(ctx.expression(1)))
                .withEndOpen(ctx.getChild(5).getText().equals(")"));
    }

    @Override
    public Object visitTupleElementSelector(@NotNull cqlParser.TupleElementSelectorContext ctx) {
        return of.createPropertyExpression()
                .withName(ctx.IDENTIFIER().getText())
                .withValue(parseExpression(ctx.expression()));
    }

    @Override
    public Object visitTupleSelector(@NotNull cqlParser.TupleSelectorContext ctx) {
        ObjectExpression objectExpression = of.createObjectExpression();
        for (cqlParser.TupleElementSelectorContext element : ctx.tupleElementSelector()) {
            objectExpression.getProperty().add((PropertyExpression)visit(element));
        }
        return objectExpression;
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
    public Object visitValuesetLiteral(@NotNull cqlParser.ValuesetLiteralContext ctx) {
        return of.createValueSetRef().withName(parseString(ctx.VALUESET()));
    }

    @Override
    public Not visitNotExpression(@NotNull cqlParser.NotExpressionContext ctx) {
        return of.createNot().withOperand(parseExpression(ctx.expression()));
    }

    @Override
    public IsNotEmpty visitExistenceExpression(@NotNull cqlParser.ExistenceExpressionContext ctx) {
        return of.createIsNotEmpty().withOperand(parseExpression(ctx.expression()));
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
                ? of.createBegin().withOperand(parseExpression(ctx.expressionTerm()))
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
                        of.createBegin().withOperand(parseExpression(ctx.expressionTerm())),
                        of.createEnd().withOperand(parseExpression(ctx.expressionTerm()))
                );
            case "months" :
                return of.createMonthsBetween().withOperand(
                        of.createBegin().withOperand(parseExpression(ctx.expressionTerm())),
                        of.createEnd().withOperand(parseExpression(ctx.expressionTerm()))
                );
            case "days" :
                return of.createDaysBetween().withOperand(
                        of.createBegin().withOperand(parseExpression(ctx.expressionTerm())),
                        of.createEnd().withOperand(parseExpression(ctx.expressionTerm()))
                );
            case "hours" :
                return of.createHoursBetween().withOperand(
                        of.createBegin().withOperand(parseExpression(ctx.expressionTerm())),
                        of.createEnd().withOperand(parseExpression(ctx.expressionTerm()))
                );
            case "minutes" :
                return of.createMinutesBetween().withOperand(
                        of.createBegin().withOperand(parseExpression(ctx.expressionTerm())),
                        of.createEnd().withOperand(parseExpression(ctx.expressionTerm()))
                );
            case "seconds" :
                return of.createSecondsBetween().withOperand(
                        of.createBegin().withOperand(parseExpression(ctx.expressionTerm())),
                        of.createEnd().withOperand(parseExpression(ctx.expressionTerm()))
                );
            case "milliseconds" :
                return of.createMillisecondsBetween().withOperand(
                        of.createBegin().withOperand(parseExpression(ctx.expressionTerm())),
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
                return of.createDifference().withOperand(
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
    public Expression visitQualifiedIdentifier(@NotNull cqlParser.QualifiedIdentifierContext ctx) {
        // QualifiedIdentifier can only appear as a query source, so it can only be an
        // ExpressionRef, a library qualified ExpressionRef, or an Alias qualified property ref.
        if (ctx.qualifier() != null) {
            String alias = resolveAlias(ctx.qualifier().getText());
            if (alias != null) {
                return of.createProperty().withPath(parseString(ctx.IDENTIFIER())).withScope(alias);
            }
        }

        String alias = resolveAlias(ctx.IDENTIFIER().getText());
        if (alias != null) {
            return of.createAliasRef().withName(alias);
        }

        return of.createExpressionRef()
                .withLibraryName(parseString(ctx.qualifier()))
                .withName(parseString(ctx.IDENTIFIER()));
    }

    @Override
    public Expression visitValueset(@NotNull cqlParser.ValuesetContext ctx) {
        Expression exp;
        if (ctx.VALUESET() != null) {
            exp = of.createValueSetRef()
                    .withName(parseString(ctx.VALUESET()));
            if (ctx.qualifier() != null) {
                ((ValueSetRef)exp).withLibraryName(parseString(ctx.qualifier()));
            }
        } else {
            exp = of.createExpressionRef()
                    .withLibraryName(parseString(ctx.qualifier()))
                    .withName(parseString(ctx.IDENTIFIER()));
        }

        return exp;
    }

    @Override
    public Expression visitAccessorExpressionTerm(@NotNull cqlParser.AccessorExpressionTermContext ctx) {
        Expression left = parseExpression(ctx.expressionTerm());

        // if left is a LibraryRef
            // if right is an IDENTIFIER
                // right may be a ParameterRef or an ExpressionRef -- need to resolve on the referenced library
                // return an ExpressionRef with the LibraryName set to the name of the LibraryRef
            // if right is a VALUESET
                // return a ValueSetRef with the LibraryName set to the name of the LibraryRef
        // if left is an ExpressionRef
            // if right is an IDENTIFIER
                // return a Property with the ExpressionRef as source and IDENTIFIER as Path
            // if right is a VALUESET, throw
        // if left is a PropertyRef
            // if right is an IDENTIFIER
                // modify the Property to append the IDENTIFIER to the PATH
            // if right is a VALUESET, throw
        // if left is an AliasRef
            // return a Property with a Path and no source, and Scope set to the Alias
        // if left is an Identifier
            // return a new Identifier with left as a qualifier
        // else
            // return an Identifier for resolution later by a method or accessor

        if (left instanceof LibraryRef) {
            if (ctx.IDENTIFIER() != null) {
                Library referencedLibrary = resolveLibrary(((LibraryRef)left).getLibraryName());

                String parameterName = resolveParameterName(referencedLibrary, ctx.IDENTIFIER().getText());
                if (parameterName != null) {
                    return of.createParameterRef()
                            .withLibraryName(((LibraryRef)left).getLibraryName())
                            .withName(ctx.IDENTIFIER().getText());
                }

                String expressionName = resolveExpressionName(referencedLibrary, ctx.IDENTIFIER().getText());
                if (expressionName != null) {
                    return of.createExpressionRef()
                            .withLibraryName(((LibraryRef)left).getLibraryName())
                            .withName(ctx.IDENTIFIER().getText());
                }

                Identifier identifier = new Identifier();
                identifier.setLibraryName(((LibraryRef)left).getLibraryName());
                identifier.setIdentifier(ctx.IDENTIFIER().getText());
            }

            if (ctx.VALUESET() != null) {
                return of.createValueSetRef()
                        .withLibraryName(((LibraryRef)left).getLibraryName())
                        .withName(ctx.VALUESET().getText());
            }
        }

        else if (left instanceof ExpressionRef && ctx.IDENTIFIER() != null) {
            return of.createProperty()
                    .withSource(left)
                    .withPath(ctx.IDENTIFIER().getText());
        }

        else if (left instanceof AliasRef && ctx.IDENTIFIER() != null) {
            return of.createProperty()
                    .withScope(((AliasRef)left).getName())
                    .withPath(ctx.IDENTIFIER().getText());
        }

        else if (left instanceof Property && ctx.IDENTIFIER() != null) {
            Property property = (Property)left;
            property.setPath(String.format("%s.%s", property.getPath(), ctx.IDENTIFIER().getText()));
            return property;
        }

        // TODO: Error handling, this should throw, or return an Error() or something.

        return of.createNull();
    }

    @Override
    public Expression visitIdentifierTerm(@NotNull cqlParser.IdentifierTermContext ctx) {
        // An Identifier will always be:
            // 1: The name of a library
            // 2: The name of a parameter
            // 3: The name of an expression
            // 4: The name of an alias
            // 5: An unresolved identifier that must be resolved later (by a method or accessor)

        String identifier = ctx.IDENTIFIER().getText();

        String libraryName = resolveLibraryName(identifier);
        if (libraryName != null) {
            LibraryRef libraryRef = new LibraryRef();
            libraryRef.setLibraryName(libraryName);

            return libraryRef;
        }

        String parameterName = resolveParameterName(identifier);
        if (parameterName != null) {
            return of.createParameterRef().withName(parameterName);
        }

        String expressionName = resolveExpressionName(identifier);
        if (expressionName != null) {
            return of.createExpressionRef().withName(expressionName);
        }

        String alias = resolveAlias(identifier);
        if (alias != null) {
            return of.createAliasRef().withName(identifier);
        }

        Identifier id = new Identifier();
        id.setIdentifier(identifier);
        return id;
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
                .withStrict(false); // CQL doesn't support the notion of a strict type-cast
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
            timingOperator.setLeft(of.createBegin().withOperand(timingOperator.getLeft()));
        }

        if ("ends".equals(firstChild.getText())) {
            timingOperator.setLeft(of.createEnd().withOperand(timingOperator.getLeft()));
        }

        ParseTree lastChild = ctx.getChild(ctx.getChildCount() - 1);
        if ("start".equals(lastChild.getText())) {
            timingOperator.setRight(of.createBegin().withOperand(timingOperator.getRight()));
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
                case "at least":
                    return of.createOr().withOperand(
                            operator,
                            of.createGreater().withOperand(timingOperator.getLeft(), timingOperator.getRight())
                    );

                case "at most":
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
                timingOperator.setRight(of.createBegin().withOperand(timingOperator.getRight()));
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
                timingOperator.setLeft(of.createBegin().withOperand(timingOperator.getLeft()));
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

        // at least/most duration before/after
        // A starts at least 3 days before start B
        // days between start of A and start of B >= 3
        // A starts at least 3 days after start B
        // days between start of A and start of B <= -3
        // A starts at most 3 days before start B
        // days between start of A and start of B <= 3
        // A starts at most 3 days after start B
        // days between start of A and start of B >= -3

        TimingOperatorContext timingOperator = timingOperators.peek();
        Boolean isBefore = false;
        for (ParseTree child : ctx.children) {
            if ("starts".equals(child.getText())) {
                timingOperator.setLeft(of.createBegin().withOperand(timingOperator.getLeft()));
                continue;
            }

            if ("ends".equals(child.getText())) {
                timingOperator.setLeft(of.createBegin().withOperand(timingOperator.getLeft()));
                continue;
            }

            if ("start".equals(child.getText())) {
                timingOperator.setRight(of.createBegin().withOperand(timingOperator.getRight()));
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
                if (ctx.quantityOffset().relativeQualifier() == null) {
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
                    switch (ctx.quantityOffset().relativeQualifier().getText()) {
                        case "at least":
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
                        case "at most":
                            if (isBefore) {
                                return of.createLessOrEqual().withOperand(
                                        betweenOperator,
                                        quantityLiteral
                                );
                            }
                            else {
                                return of.createGreaterOrEqual().withOperand(
                                        betweenOperator,
                                        of.createNegate().withOperand(quantityLiteral)
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
                timingOperator.setLeft(of.createBegin().withOperand(timingOperator.getLeft()));
                continue;
            }

            if ("ends".equals(child.getText())) {
                timingOperator.setLeft(of.createBegin().withOperand(timingOperator.getLeft()));
                continue;
            }

            if ("start".equals(child.getText())) {
                timingOperator.setRight(of.createBegin().withOperand(timingOperator.getRight()));
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
                .withBegin(of.createNegate().withOperand(quantityLiteral)).withBeginOpen(isProper)
                .withEnd(quantityLiteral).withEndOpen(isProper);
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
        return of.createBegins().withOperand(timingOperators.peek().getLeft(), timingOperators.peek().getRight());
    }

    @Override
    public Object visitStartedByIntervalOperatorPhrase(@NotNull cqlParser.StartedByIntervalOperatorPhraseContext ctx) {
        return of.createBegunBy().withOperand(timingOperators.peek().getLeft(), timingOperators.peek().getRight());
    }

    @Override
    public Object visitEndsIntervalOperatorPhrase(@NotNull cqlParser.EndsIntervalOperatorPhraseContext ctx) {
        return of.createEnds().withOperand(timingOperators.peek().getLeft(), timingOperators.peek().getRight());
    }

    @Override
    public Object visitEndedByIntervalOperatorPhrase(@NotNull cqlParser.EndedByIntervalOperatorPhraseContext ctx) {
        return of.createEndedBy().withOperand(timingOperators.peek().getLeft(), timingOperators.peek().getRight());
    }

    @Override
    public Object visitIfThenElseExpressionTerm(@NotNull cqlParser.IfThenElseExpressionTermContext ctx) {
        return of.createConditional()
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
    public ClinicalRequest visitRetrieve(@NotNull cqlParser.RetrieveContext ctx) {
        String occ = ctx.occurrence() != null ? ctx.occurrence().getText() : "Occurrence"; // TODO: Default occurrence label by model?
        String topic = parseString(ctx.topic());
        String modality = ctx.modality() != null ? ctx.modality().getText() : "";
        ClassDetail detail = getModelHelper().getClassDetail(occ, topic, modality);

        ClinicalRequest request = of.createClinicalRequest()
                .withDataType(resolveNamedType(
                        detail != null
                                ? detail.getClassInfo().getName()
                                : String.format("%s%s%s", topic, modality, occ)));

        if (ctx.valueset() != null) {
            if (ctx.valuesetPathIdentifier() != null) {
                request.setCodeProperty(parseString(ctx.valuesetPathIdentifier()));
            }
            else if (detail != null && detail.getClassInfo().getPrimaryCodeAttribute() != null) {
                request.setCodeProperty(detail.getClassInfo().getPrimaryCodeAttribute());
            }

            request.setCodes(parseExpression(ctx.valueset()));
        }

        if (ctx.expression() != null) {
            if (ctx.duringPathIdentifier() != null) {
                request.setDateProperty(parseString(ctx.duringPathIdentifier()));
            }
            else if (detail != null && detail.getClassInfo().getPrimaryDateAttribute() != null) {
                request.setDateProperty(detail.getClassInfo().getPrimaryDateAttribute());
            }

            request.setDateRange(parseExpression(ctx.expression()));
        }

        clinicalRequests.add(request);

        return request;
    }

    @Override
    public Object visitQuery(@NotNull cqlParser.QueryContext ctx) {
        QueryContext queryContext = new QueryContext();
        AliasedQuerySource aqs = (AliasedQuerySource) visit(ctx.aliasedQuerySource());
        queryContext.addQuerySource(aqs);
        queries.push(queryContext);
        try {
            List<RelationshipClause> qicx = new ArrayList<>();
            if (ctx.queryInclusionClause() != null) {
                for (cqlParser.QueryInclusionClauseContext queryInclusionClauseContext : ctx.queryInclusionClause()) {
                    qicx.add((RelationshipClause) visit(queryInclusionClauseContext));
                }
            }
            Expression where = ctx.whereClause() != null ? (Expression) visit(ctx.whereClause()) : null;
            Expression ret = ctx.returnClause() != null ? (Expression) visit(ctx.returnClause()) : null;
            SortClause sort = ctx.sortClause() != null ? (SortClause) visit(ctx.sortClause()) : null;

            return of.createQuery()
                    .withSource(aqs)
                    .withRelationship(qicx)
                    .withWhere(where)
                    .withReturn(ret)
                    .withSort(sort);
        }
        finally {
            queries.pop();
        }
    }

    @Override
    public Object visitAliasedQuerySource(@NotNull cqlParser.AliasedQuerySourceContext ctx) {
        return of.createAliasedQuerySource().withExpression(parseExpression(ctx.querySource()))
                .withAlias(ctx.alias().getText());
    }

    @Override
    public Object visitQueryInclusionClause(@NotNull cqlParser.QueryInclusionClauseContext ctx) {
        boolean negated = "without".equals(ctx.getChild(0).getText());
        AliasedQuerySource aqs = (AliasedQuerySource) visit(ctx.aliasedQuerySource());
        queries.peek().addQuerySource(aqs);
        try {
            Expression expression = (Expression) visit(ctx.expression());
            RelationshipClause result = negated ? of.createWithout() : of.createWith();
            return result.withExpression(aqs.getExpression()).withAlias(aqs.getAlias()).withWhere(expression);
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
        ParseTree o = ctx.expression();
        if (o == null) {
            o = ctx.retrieve();
        }
        if (o == null) {
            o = ctx.qualifiedIdentifier();
        }
        return visit(o);
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
    public Object visitFunctionDefinition(@NotNull cqlParser.FunctionDefinitionContext ctx) {
        FunctionDef fun = of.createFunctionDef().withName(parseString(ctx.IDENTIFIER()));
        if (ctx.operandDefinition() != null) {
            for (cqlParser.OperandDefinitionContext opdef : ctx.operandDefinition()) {
                fun.getParameter().add(
                        of.createParameterDef()
                                .withName(parseString(opdef.IDENTIFIER()))
                                .withParameterTypeSpecifier(parseTypeSpecifier(opdef.typeSpecifier()))
                );
            }
        }
        fun.setExpression(parseExpression(ctx.functionBody()));
        fun.setContext(currentContext);
        addToLibrary(fun);

        return fun;
    }

    // TODO: Retrieve definition
    // NOTE: Not spending any time here until we know whether we actually need retrieve definitions

    @Override
    @SuppressWarnings("PMD.UselessOverridingMethod")
    public Object visitValuesetIdentifier(@NotNull cqlParser.ValuesetIdentifierContext ctx) {
        // TODO:
        return super.visitValuesetIdentifier(ctx);
    }

    @Override
    @SuppressWarnings("PMD.UselessOverridingMethod")
    public Object visitDuringIdentifier(@NotNull cqlParser.DuringIdentifierContext ctx) {
        // TODO:
        return super.visitDuringIdentifier(ctx);
    }

    @Override
    @SuppressWarnings("PMD.UselessOverridingMethod")
    public Object visitRetrieveDefinition(@NotNull cqlParser.RetrieveDefinitionContext ctx) {
        // TODO:
        return super.visitRetrieveDefinition(ctx);
    }

    private ModelReference initializeModelHelper(String identifier) {
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

        ModelReference model = of.createModelReference()
                .withReferencedModel(of.createModelReferenceReferencedModel().withValue(modelHelper.getModelInfo().getUrl()));
        addToLibrary(model);

        return model;
    }

    private ModelHelper getModelHelper() {
        if (modelHelper == null) {
            // No model declared, so default to QUICK
            initializeModelHelper("QUICK");
        }

        return modelHelper;
    }

    private String parseString(ParseTree pt) {
        if (pt == null) return null;

        String text = pt.getText();
        if (pt instanceof TerminalNode) {
            int tokenType = ((TerminalNode) pt).getSymbol().getType();
            if (cqlLexer.STRING == tokenType || cqlLexer.VALUESET == tokenType) {
                // chop off leading and trailing ' or "
                text = text.substring(1, text.length() - 1);
            }
        }

        return text;
    }

    private Expression parseExpression(ParseTree pt) {
        return pt == null ? null : (Expression) visit(pt);
    }

    private TypeSpecifier parseTypeSpecifier(ParseTree pt) {
        return pt == null ? null : (TypeSpecifier) visit(pt);
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

    private String resolveAlias(String identifier) {
        for (QueryContext query : queries) {
            AliasedQuerySource source = query.resolveAlias(identifier);
            if (source != null) {
                return source.getAlias();
            }
        }

        return null;
    }

    private void addToLibrary(ModelReference model) {
        if (library.getDataModels() == null) {
            library.setDataModels(of.createLibraryDataModels());
        }
        library.getDataModels().getModelReference().add(model);
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

    private String resolveExpressionName(String identifier) {
        if (libraryInfo != null) {
            return libraryInfo.resolveExpressionName(identifier);
        }

        return null;
    }

    private String resolveExpressionName(Library library, String identifier) {
        if (library.getStatements() != null) {
            for (ExpressionDef current : library.getStatements().getDef()) {
                if (!(current instanceof FunctionDef) && !(current instanceof ClinicalRequestDef)
                        && current.getName().equals(identifier)) {
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

    private void addToLibrary(LibraryReference libraryReference) {
        if (library.getLibraries() == null) {
            library.setLibraries(of.createLibraryLibraries());
        }
        library.getLibraries().getLibraryReference().add(libraryReference);

        Library referencedLibrary =
                new Library()
                        .withIdentifier(
                                new VersionedIdentifier()
                                        .withId(libraryReference.getPath())
                                        .withVersion(libraryReference.getVersion()));

        // TODO: Resolve and prepare the actual library
        libraries.put(libraryReference.getName(), referencedLibrary);
    }

    private String resolveLibraryName(String identifier) {
        if (library.getLibraries() != null) {
            for (LibraryReference current : library.getLibraries().getLibraryReference()) {
                if (current.getName().equals(identifier)) {
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

        ElmTranslatorVisitor visitor = new ElmTranslatorVisitor();
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
