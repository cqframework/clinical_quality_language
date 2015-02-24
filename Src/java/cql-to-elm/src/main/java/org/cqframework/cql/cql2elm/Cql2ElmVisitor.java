package org.cqframework.cql.cql2elm;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.cqframework.cql.cql2elm.preprocessor.*;
import org.cqframework.cql.elm.tracking.*;
import org.cqframework.cql.gen.cqlBaseVisitor;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;
import org.cqframework.cql.cql2elm.model.*;
import org.hl7.cql_annotations.r1.Narrative;
import org.hl7.elm.r1.*;
import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.Interval;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;
import java.util.List;

public class Cql2ElmVisitor extends cqlBaseVisitor {
    private final ObjectFactory of = new ObjectFactory();
    private final org.hl7.cql_annotations.r1.ObjectFactory af = new org.hl7.cql_annotations.r1.ObjectFactory();
    private boolean annotate = false;
    private boolean dateRangeOptimization = false;

    private TokenStream tokenStream;

    private LibraryInfo libraryInfo = null;
    private Library library = null;
    private TranslatedLibrary translatedLibrary = null;
    private String currentContext = "Patient"; // default context to patient

    //Put them here for now, but eventually somewhere else?
    private final Map<String, TranslatedLibrary> libraries = new HashMap<>();
    private final Stack<QueryContext> queries = new Stack<>();
    private final Stack<TimingOperatorContext> timingOperators = new Stack<>();
    private final Stack<Narrative> narratives = new Stack<>();
    private int currentToken = -1;
    private int nextLocalId = 1;
    private final List<Retrieve> retrieves = new ArrayList<>();
    private final List<Expression> expressions = new ArrayList<>();
    private final List<CqlTranslatorException> errors = new ArrayList<>();
    private Map<String, Model> models = new HashMap<>();
    private boolean implicitPatientCreated = false;

    public void enableAnnotations() {
        annotate = true;
    }

    public void disableAnnotations() {
        annotate = false;
    }

    public void enableDateRangeOptimization() {
        dateRangeOptimization = true;
    }

    public void disableDateRangeOptimization() {
        dateRangeOptimization = false;
    }

    public TokenStream getTokenStream() {
        return tokenStream;
    }

    public void setTokenStream(TokenStream value) {
        tokenStream = value;
    }

    public LibraryInfo getLibraryInfo() {
        return libraryInfo;
    }

    public void setLibraryInfo(LibraryInfo value) {
        libraryInfo = value;
    }

    public Library getLibrary() {
        return library;
    }

    public TranslatedLibrary getTranslatedLibrary() {
        return translatedLibrary;
    }

    public List<Retrieve> getRetrieves() {
        return retrieves;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    public List<CqlTranslatorException> getErrors() {
        return errors;
    }

    private int getNextLocalId() {
        return nextLocalId++;
    }

    private void pushNarrative(@NotNull ParseTree tree) {
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

    private Narrative popNarrative(@NotNull ParseTree tree, Object o) {
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
            Element element = (Element) o;
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
                    ExpressionDef expressionDef = (ExpressionDef) o;
                    expressionDef.getAnnotation().add(af.createAnnotation().withS(currentNarrative));
                }
            } else {
                if (!narratives.isEmpty()) {
                    Narrative parentNarrative = narratives.peek();
                    parentNarrative.getContent().addAll(currentNarrative.getContent());
                }
            }
        } else {
            if (!narratives.isEmpty()) {
                Narrative parentNarrative = narratives.peek();
                parentNarrative.getContent().addAll(currentNarrative.getContent());
            }
        }

        return currentNarrative;
    }

    @Override
    public Object visit(@NotNull ParseTree tree) {
        if (annotate) {
            pushNarrative(tree);
        }
        Object o = null;
        try {
            try {
                o = super.visit(tree);
            } catch (CqlTranslatorException e) {
                errors.add(e);
            } catch (Exception e) {
                errors.add(new CqlTranslatorException(
                        e.getMessage(),
                        tree instanceof ParserRuleContext ? getTrackBack((ParserRuleContext) tree) : null,
                        e));
                o = of.createNull();
            }
        } finally {
            if (annotate) {
                popNarrative(tree, o);
            }
        }

        if (o instanceof Trackable && tree instanceof ParserRuleContext && !(tree instanceof cqlParser.LogicContext)) {
            this.track((Trackable) o, (ParserRuleContext) tree);
        }
        if (o instanceof Expression) {
            addExpression((Expression) o);
        }

        return o;
    }

    @Override
    public Object visitLogic(@NotNull cqlParser.LogicContext ctx) {
        library = of.createLibrary()
                .withSchemaIdentifier(of.createVersionedIdentifier()
                        .withId("urn:hl7-org:elm") // TODO: Pull this from the ELM library namespace
                        .withVersion("r1"));
        translatedLibrary = new TranslatedLibrary();

        loadSystemLibrary();

        if (this.libraryInfo.getLibraryName() == null) {
            this.libraryInfo.setLibraryName("Anonymous");
        }

        Object lastResult = null;
        LibraryManager.beginTranslation(this.libraryInfo.getLibraryName());
        try {
            // Loop through and call visit on each child (to ensure they are tracked)
            for (int i = 0; i < ctx.getChildCount(); i++) {
                lastResult = visit(ctx.getChild(i));
            }

            // Return last result (consistent with super implementation and helps w/ testing)
            return lastResult;
        }
        finally {
            LibraryManager.endTranslation(this.libraryInfo.getLibraryName());
        }
    }

    @Override
    public VersionedIdentifier visitLibraryDefinition(@NotNull cqlParser.LibraryDefinitionContext ctx) {
        VersionedIdentifier vid = of.createVersionedIdentifier()
                .withId(parseString(ctx.identifier()))
                .withVersion(parseString(ctx.versionSpecifier()));
        setLibraryIdentifier(vid);

        return vid;
    }

    @Override
    public UsingDef visitUsingDefinition(@NotNull cqlParser.UsingDefinitionContext ctx) {
        Model model = getModel(parseString(ctx.identifier()), parseString(ctx.versionSpecifier()));
        return translatedLibrary.resolveUsingRef(model.getModelInfo().getName());
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

        DataType paramType = null;
        if (param.getParameterTypeSpecifier() != null) {
            paramType = param.getParameterTypeSpecifier().getResultType();
        }

        if (param.getDefault() != null) {
            if (paramType != null) {
                DataTypes.verifyType(param.getDefault().getResultType(), paramType);
            }
            else {
                paramType = param.getDefault().getResultType();
            }
        }

        if (paramType == null) {
            throw new IllegalArgumentException(String.format("Could not determine parameter type for parameter %s.", param.getName()));
        }

        param.setResultType(paramType);

        addToLibrary(param);

        return param;
    }

    @Override
    public NamedTypeSpecifier visitNamedTypeSpecifier(@NotNull cqlParser.NamedTypeSpecifierContext ctx) {
        DataType resultType = resolveTypeName(parseString(ctx.modelIdentifier()), parseString(ctx.identifier()));
        NamedTypeSpecifier result = of.createNamedTypeSpecifier()
                .withName(dataTypeToQName(resultType));

        // Fluent API would be nice here, but resultType isn't part of the model so...
        result.setResultType(resultType);

        return result;
    }

    @Override
    public TupleElementDefinition visitTupleElementDefinition(@NotNull cqlParser.TupleElementDefinitionContext ctx) {
        TupleElementDefinition result = of.createTupleElementDefinition()
                .withName(parseString(ctx.identifier()))
                .withType(parseTypeSpecifier(ctx.typeSpecifier()));

        return result;
    }

    @Override
    public Object visitTupleTypeSpecifier(@NotNull cqlParser.TupleTypeSpecifierContext ctx) {
        TupleType resultType = new TupleType();
        TupleTypeSpecifier typeSpecifier = of.createTupleTypeSpecifier();
        for (cqlParser.TupleElementDefinitionContext definitionContext : ctx.tupleElementDefinition()) {
            TupleElementDefinition element = (TupleElementDefinition)visit(definitionContext);
            resultType.addElement(new TupleTypeElement(element.getName(), element.getType().getResultType()));
            typeSpecifier.getElement().add(element);
        }

        typeSpecifier.setResultType(resultType);

        return typeSpecifier;
    }

    @Override
    public IntervalTypeSpecifier visitIntervalTypeSpecifier(@NotNull cqlParser.IntervalTypeSpecifierContext ctx) {
        IntervalTypeSpecifier result = of.createIntervalTypeSpecifier().withPointType(parseTypeSpecifier(ctx.typeSpecifier()));
        IntervalType intervalType = new IntervalType(result.getPointType().getResultType());
        result.setResultType(intervalType);
        return result;
    }

    @Override
    public ListTypeSpecifier visitListTypeSpecifier(@NotNull cqlParser.ListTypeSpecifierContext ctx) {
        ListTypeSpecifier result = of.createListTypeSpecifier().withElementType(parseTypeSpecifier(ctx.typeSpecifier()));
        ListType listType = new ListType(result.getElementType().getResultType());
        result.setResultType(listType);
        return result;
    }

    @Override
    public String visitCodeSystemVersion(@NotNull cqlParser.CodeSystemVersionContext ctx) {
        return parseString(ctx.codeSystemId()) + ' ' + parseString(ctx.versionSpecifier());
    }

    @Override
    public String visitCodeSystemVersions(@NotNull cqlParser.CodeSystemVersionsContext ctx) {
        StringBuilder result = new StringBuilder();
        for (cqlParser.CodeSystemVersionContext childCtx : ctx.codeSystemVersion()) {
            String codeSystemVersion = (String) visit(childCtx);
            result.append(result.length() > 0 ? " " : "").append(codeSystemVersion);
        }

        return result.toString();
    }

    @Override
    public ValueSetDef visitValuesetDefinition(@NotNull cqlParser.ValuesetDefinitionContext ctx) {
        ValueSetDef vs = of.createValueSetDef()
                .withName(parseString(ctx.identifier()))
                .withId(parseString(ctx.valuesetId()))
                .withVersion(parseString(ctx.versionSpecifier()))
                .withCodeSystemVersions(
                        ctx.codeSystemVersions() != null
                                ? (String) visit(ctx.codeSystemVersions())
                                : null
                );
        addToLibrary(vs);

        return vs;
    }

    @Override
    public Object visitContextDefinition(@NotNull cqlParser.ContextDefinitionContext ctx) {
        currentContext = parseString(ctx.identifier());

        // If this is the first time a context definition is encountered, output a patient definition:
        // define Patient = element of [<Patient model type>]
        if (!implicitPatientCreated) {
            String patientTypeName = getModel().getModelInfo().getPatientClassName();
            if (patientTypeName == null || patientTypeName.equals("")) {
                throw new IllegalArgumentException("Model definition does not contain enough information to construct a patient context.");
            }
            DataType patientType = resolveTypeName(patientTypeName);
            Retrieve patientRetrieve = of.createRetrieve().withDataType(dataTypeToQName(patientType));
            patientRetrieve.setResultType(new ListType(patientType));
            String patientClassIdentifier = getModel().getModelInfo().getPatientClassIdentifier();
            if (patientClassIdentifier != null) {
                patientRetrieve.setTemplateId(patientClassIdentifier);
            }

            ExpressionDef patientExpressionDef = of.createExpressionDef()
                    .withName("Patient")
                    .withContext(currentContext)
                    .withExpression(of.createSingletonFrom().withOperand(patientRetrieve));
            patientExpressionDef.getExpression().setResultType(patientType);
            patientExpressionDef.setResultType(patientType);
            addToLibrary(patientExpressionDef);
            implicitPatientCreated = true;
            return currentContext;
        }

        return currentContext;
    }

    @Override
    public ExpressionDef visitExpressionDefinition(@NotNull cqlParser.ExpressionDefinitionContext ctx) {
        String identifier = parseString(ctx.identifier());
        ExpressionDef def = translatedLibrary.resolveExpressionRef(identifier);
        if (def == null) {
            def = of.createExpressionDef()
                    .withName(identifier)
                    .withContext(currentContext)
                    .withExpression((Expression) visit(ctx.expression()));
            def.setResultType(def.getExpression().getResultType());
            addToLibrary(def);
        }

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
        Interval result = of.createInterval()
                .withLow(parseExpression(ctx.expression(0)))
                .withLowClosed(ctx.getChild(1).getText().equals("["))
                .withHigh(parseExpression(ctx.expression(1)))
                .withHighClosed(ctx.getChild(5).getText().equals("]"));

        DataType lowType = result.getLow().getResultType();
        DataType highType = result.getHigh().getResultType();
        if ((lowType != null) && (highType != null)) {
            DataTypes.verifyType(highType, lowType);
        }

        DataType pointType = lowType != null ? lowType : highType;
        if (pointType != null) {
            IntervalType resultType = new IntervalType(pointType);
            result.setResultType(resultType);
        }
        else {
            throw new IllegalArgumentException("Could not determine a point type for interval selector.");
        }
        return result;
    }

    @Override
    public Object visitTupleElementSelector(@NotNull cqlParser.TupleElementSelectorContext ctx) {
        TupleElement result = of.createTupleElement()
                .withName(parseString(ctx.identifier()))
                .withValue(parseExpression(ctx.expression()));
        result.setResultType(result.getValue().getResultType());
        return result;
    }

    @Override
    public Object visitTupleSelector(@NotNull cqlParser.TupleSelectorContext ctx) {
        Tuple tuple = of.createTuple();
        TupleType tupleType = new TupleType();
        for (cqlParser.TupleElementSelectorContext elementContext : ctx.tupleElementSelector()) {
            TupleElement element = (TupleElement)visit(elementContext);
            tupleType.addElement(new TupleTypeElement(element.getName(), element.getResultType()));
            tuple.getElement().add(element);
        }
        tuple.setResultType(tupleType);
        return tuple;
    }

    @Override
    public Object visitListSelector(@NotNull cqlParser.ListSelectorContext ctx) {
        TypeSpecifier elementTypeSpecifier = parseTypeSpecifier(ctx.typeSpecifier());
        org.hl7.elm.r1.List list = of.createList();
        ListType listType = null;
        if (elementTypeSpecifier != null) {
            ListTypeSpecifier listTypeSpecifier = of.createListTypeSpecifier().withElementType(elementTypeSpecifier);
            listType = new ListType(elementTypeSpecifier.getResultType());
            listTypeSpecifier.setResultType(listType);
        }

        for (cqlParser.ExpressionContext elementContext : ctx.expression()) {
            Expression element = parseExpression(elementContext);
            if (listType == null) {
                if (element.getResultType() != null) {
                    listType = new ListType(element.getResultType());
                }
            }
            else {
                DataTypes.verifyType(element.getResultType(), listType.getElementType());
            }
            list.getElement().add(element);
        }

        if (listType == null) {
            // An empty untyped list is list<Any>
            listType = new ListType(resolveTypeName("System", "Any"));
        }

        list.setResultType(listType);

        return list;
    }

    @Override
    public Null visitNullLiteral(@NotNull cqlParser.NullLiteralContext ctx) {
        Null result = of.createNull();
        result.setResultType(resolveTypeName("System", "Any"));
        return result;
    }

    @Override
    public Expression visitQuantityLiteral(@NotNull cqlParser.QuantityLiteralContext ctx) {
        if (ctx.unit() != null) {
            DecimalFormat df = new DecimalFormat("#.#");
            df.setParseBigDecimal(true);
            try {
                Quantity result = of.createQuantity()
                        .withValue((BigDecimal) df.parse(ctx.QUANTITY().getText()))
                        .withUnit(ctx.unit().getText());
                result.setResultType(resolveTypeName("Quantity"));
                return result;
            } catch (ParseException e) {
                // Should never occur, just return null
                return of.createNull();
            }
        } else {
            String quantity = ctx.QUANTITY().getText();
            DataType resultType = resolveTypeName(quantity.contains(".") ? "Decimal" : "Integer");
            Literal result = of.createLiteral()
                    .withValue(quantity)
                    .withValueType(dataTypeToQName(resultType));
            result.setResultType(resultType);
            return result;
        }
    }

    @Override
    public Not visitNotExpression(@NotNull cqlParser.NotExpressionContext ctx) {
        Not result = of.createNot().withOperand(parseExpression(ctx.expression()));
        resolveUnaryCall("System", "Not", result);
        return result;
    }

    @Override
    public Exists visitExistenceExpression(@NotNull cqlParser.ExistenceExpressionContext ctx) {
        Exists result = of.createExists().withOperand(parseExpression(ctx.expression()));
        resolveUnaryCall("System", "Exists", result);
        return result;
    }

    @Override
    public BinaryExpression visitMultiplicationExpressionTerm(@NotNull cqlParser.MultiplicationExpressionTermContext ctx) {
        BinaryExpression exp = null;
        String operatorName = null;
        switch (ctx.getChild(1).getText()) {
            case "*":
                exp = of.createMultiply();
                operatorName = "Multiply";
                break;
            case "/":
                exp = of.createDivide();
                operatorName = "Divide";
                break;
            case "div":
                exp = of.createTruncatedDivide();
                operatorName = "TruncatedDivide";
                break;
            case "mod":
                exp = of.createModulo();
                operatorName = "Modulo";
                break;
            default:
                throw new IllegalArgumentException(String.format("Unsupported operator: %s.", ctx.getChild(1).getText()));
        }

        exp.withOperand(
                parseExpression(ctx.expressionTerm(0)),
                parseExpression(ctx.expressionTerm(1)));

        resolveBinaryCall("System", operatorName, exp);

        return exp;
    }

    @Override
    public Power visitPowerExpressionTerm(@NotNull cqlParser.PowerExpressionTermContext ctx) {
        Power power = of.createPower().withOperand(
                parseExpression(ctx.expressionTerm(0)),
                parseExpression(ctx.expressionTerm(1)));

        resolveBinaryCall("System", "Power", power);

        return power;
    }

    @Override
    public Object visitPolarityExpressionTerm(@NotNull cqlParser.PolarityExpressionTermContext ctx) {
        if (ctx.getChild(0).getText().equals("+")) {
            return visit(ctx.expressionTerm());
        }

        Negate result = of.createNegate().withOperand(parseExpression(ctx.expressionTerm()));
        resolveUnaryCall("System", "Negate", result);
        return result;
    }

    @Override
    public BinaryExpression visitAdditionExpressionTerm(@NotNull cqlParser.AdditionExpressionTermContext ctx) {
        BinaryExpression exp = null;
        String operatorName = null;
        switch (ctx.getChild(1).getText()) {
            case "+":
                exp = of.createAdd();
                operatorName = "Add";
                break;
            case "-":
                exp = of.createSubtract();
                operatorName = "Subtract";
                break;
            default:
                throw new IllegalArgumentException(String.format("Unsupported operator: %s.", ctx.getChild(1).getText()));
        }

        exp.withOperand(
                parseExpression(ctx.expressionTerm(0)),
                parseExpression(ctx.expressionTerm(1)));

        resolveBinaryCall("System", operatorName, exp);
        return exp;
    }

    @Override
    public Object visitPredecessorExpressionTerm(@NotNull cqlParser.PredecessorExpressionTermContext ctx) {
        Predecessor result = of.createPredecessor().withOperand(parseExpression(ctx.expressionTerm()));
        resolveUnaryCall("System", "Predecessor", result);
        return result;
    }

    @Override
    public Object visitSuccessorExpressionTerm(@NotNull cqlParser.SuccessorExpressionTermContext ctx) {
        Successor result = of.createSuccessor().withOperand(parseExpression(ctx.expressionTerm()));
        resolveUnaryCall("System", "Successor", result);
        return result;
    }

    @Override
    public Object visitElementExtractorExpressionTerm(@NotNull cqlParser.ElementExtractorExpressionTermContext ctx) {
        SingletonFrom result = of.createSingletonFrom().withOperand(parseExpression(ctx.expressionTerm()));

        if (!(result.getOperand().getResultType() instanceof ListType)) {
            throw new IllegalArgumentException("List type expected.");
        }

        result.setResultType(((ListType)result.getOperand().getResultType()).getElementType());

        resolveUnaryCall("System", "SingletonFrom", result);
        return result;
    }

    @Override
    public Object visitTimeBoundaryExpressionTerm(@NotNull cqlParser.TimeBoundaryExpressionTermContext ctx) {
        UnaryExpression result = null;
        String operatorName = null;

        if (ctx.getChild(0).getText().equals("start")) {
            result = of.createStart().withOperand(parseExpression(ctx.expressionTerm()));
            operatorName = "Start";
        }
        else {
            result = of.createEnd().withOperand(parseExpression(ctx.expressionTerm()));
            operatorName = "End";
        }

        if (!(result.getOperand().getResultType() instanceof IntervalType)) {
            throw new IllegalArgumentException("Interval type expected.");
        }

        result.setResultType(((IntervalType)result.getOperand().getResultType()).getPointType());

        resolveUnaryCall("System", operatorName, result);
        return result;
    }

    private DateTimePrecision parseDateTimePrecision(String dateTimePrecision) {
        switch (dateTimePrecision) {
            case "year":
            case "years":
                return DateTimePrecision.YEAR;
            case "month":
            case "months":
                return DateTimePrecision.MONTH;
            case "day":
            case "days":
                return DateTimePrecision.DAY;
            case "hour":
            case "hours":
                return DateTimePrecision.HOUR;
            case "minute":
            case "minutes":
                return DateTimePrecision.MINUTE;
            case "second":
            case "seconds":
                return DateTimePrecision.SECOND;
            case "millisecond":
            case "milliseconds":
                return DateTimePrecision.MILLISECOND;
            default:
                throw new IllegalArgumentException(String.format("Unknown precision '%s'.", dateTimePrecision));
        }
    }

    @Override
    public Object visitTimeUnitExpressionTerm(@NotNull cqlParser.TimeUnitExpressionTermContext ctx) {
        String component = ctx.dateTimeComponent().getText();

        UnaryExpression result = null;
        String operatorName = null;
        switch (component) {
            case "date":
                result = of.createDateFrom().withOperand(parseExpression(ctx.expressionTerm()));
                operatorName = "Date";
                break;
            case "time":
                result = of.createTimeFrom().withOperand(parseExpression(ctx.expressionTerm()));
                operatorName = "Time";
                break;
            case "timezone":
                result = of.createTimezoneFrom().withOperand(parseExpression(ctx.expressionTerm()));
                operatorName = "Timezone";
                break;
            case "year":
            case "month":
            case "day":
            case "hour":
            case "minute":
            case "second":
            case "millisecond":
                result = of.createDateTimeComponentFrom()
                        .withOperand(parseExpression(ctx.expressionTerm()))
                        .withPrecision(parseDateTimePrecision(component));
                operatorName = "DateTimeComponentFrom";
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown precision '%s'.", component));
        }

        resolveUnaryCall("System", operatorName, result);
        return result;
    }

    @Override
    public Object visitDurationExpressionTerm(@NotNull cqlParser.DurationExpressionTermContext ctx) {
        // duration in days of X <=> days between start of X and end of X
        Expression operand = parseExpression(ctx.expressionTerm());

        Start start = of.createStart().withOperand(operand);
        resolveUnaryCall("System", "Start", start);

        End end = of.createEnd().withOperand(operand);
        resolveUnaryCall("System", "End", end);

        DurationBetween result = of.createDurationBetween()
                .withPrecision(parseDateTimePrecision(ctx.pluralDateTimePrecision().getText()))
                .withOperand(start, end);

        resolveBinaryCall("System", "DurationBetween", result);
        return result;
    }

    @Override
    public Object visitRangeExpression(@NotNull cqlParser.RangeExpressionContext ctx) {
        // X properly? between Y and Z
        Expression first = parseExpression(ctx.expression());
        Expression second = parseExpression(ctx.expressionTerm(0));
        Expression third = parseExpression(ctx.expressionTerm(1));
        boolean isProper = ctx.getChild(0).getText().equals("properly");
        BinaryExpression result = of.createAnd()
                .withOperand(
                        (isProper ? of.createGreater() : of.createGreaterOrEqual())
                                .withOperand(first, second),
                        (isProper ? of.createLess() : of.createLessOrEqual())
                                .withOperand(first, third)
                );

        resolveBinaryCall("System", isProper ? "Greater" : "GreaterOrEqual", (BinaryExpression)result.getOperand().get(0));
        resolveBinaryCall("System", isProper ? "Less" : "LessOrEqual", (BinaryExpression)result.getOperand().get(1));
        resolveBinaryCall("System", "And", result);
        return result;
    }

    @Override
    public Object visitTimeRangeExpression(@NotNull cqlParser.TimeRangeExpressionContext ctx) {
        BinaryExpression result = of.createDurationBetween()
                .withPrecision(parseDateTimePrecision(ctx.pluralDateTimePrecision().getText()))
                .withOperand(parseExpression(ctx.expressionTerm(0)), parseExpression(ctx.expressionTerm(1)));

        resolveBinaryCall("System", "DurationBetween", result);
        return result;
    }

    @Override
    public Object visitWidthExpressionTerm(@NotNull cqlParser.WidthExpressionTermContext ctx) {
        UnaryExpression result = of.createWidth().withOperand(parseExpression(ctx.expressionTerm()));
        resolveUnaryCall("System", "Width", result);
        return result;
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
                if (ctx.dateTimePrecisionSpecifier() != null) {
                    In in = of.createIn()
                            .withPrecision(parseDateTimePrecision(ctx.dateTimePrecisionSpecifier().dateTimePrecision().getText()))
                            .withOperand(
                                    parseExpression(ctx.expression(0)),
                                    parseExpression(ctx.expression(1))
                            );

                    resolveBinaryCall("System", "In", in);
                    return in;
                } else {
                    Expression left = parseExpression(ctx.expression(0));
                    Expression right = parseExpression(ctx.expression(1));
                    if (right instanceof ValueSetRef) {
                        InValueSet in = of.createInValueSet()
                                .withCode(left)
                                .withValueset((ValueSetRef) right);
                        resolveCall("System", "InValueSet", in, in.getCode().getResultType());
                        return in;
                    }

                    In in = of.createIn().withOperand(left, right);
                    return in;
                }
            case "contains":
                if (ctx.dateTimePrecisionSpecifier() != null) {
                    Contains contains = of.createContains()
                            .withPrecision(parseDateTimePrecision(ctx.dateTimePrecisionSpecifier().dateTimePrecision().getText()))
                            .withOperand(
                                    parseExpression(ctx.expression(0)),
                                    parseExpression(ctx.expression(1))
                            );

                    resolveBinaryCall("System", "Contains", contains);
                    return contains;
                } else {
                    Expression left = parseExpression(ctx.expression(0));
                    Expression right = parseExpression(ctx.expression(1));
                    if (left instanceof ValueSetRef) {
                        InValueSet in = of.createInValueSet()
                                .withCode(right)
                                .withValueset((ValueSetRef) left);
                        resolveCall("System", "InValueSet", in, in.getCode().getResultType());
                        return in;
                    }

                    Contains contains = of.createContains().withOperand(left, right);
                    resolveBinaryCall("System", "Contains", contains);
                    return contains;
                }
        }

        throw new IllegalArgumentException(String.format("Unknown operator: %s", operator));
    }

    @Override
    public And visitAndExpression(@NotNull cqlParser.AndExpressionContext ctx) {
        And and = of.createAnd().withOperand(
                parseExpression(ctx.expression(0)),
                parseExpression(ctx.expression(1)));

        resolveBinaryCall("System", "And", and);
        return and;
    }

    @Override
    public Expression visitOrExpression(@NotNull cqlParser.OrExpressionContext ctx) {
        if (ctx.getChild(1).getText().equals("xor")) {
            Xor xor = of.createXor().withOperand(
                    parseExpression(ctx.expression(0)),
                    parseExpression(ctx.expression(1)));
            resolveBinaryCall("System", "Xor", xor);
            return xor;
        } else {
            Or or = of.createOr().withOperand(
                    parseExpression(ctx.expression(0)),
                    parseExpression(ctx.expression(1)));
            resolveBinaryCall("System", "Or", or);
            return or;
        }
    }

    @Override
    public Object visitInFixSetExpression(@NotNull cqlParser.InFixSetExpressionContext ctx) {
        String operator = ctx.getChild(1).getText();

        switch (operator) {
            case "union":
                Union union = of.createUnion().withOperand(
                        parseExpression(ctx.expression(0)),
                        parseExpression(ctx.expression(1))
                );
                resolveBinaryCall("System", "Union", union);
                return union;
            case "intersect":
                Intersect intersect = of.createIntersect().withOperand(
                        parseExpression(ctx.expression(0)),
                        parseExpression(ctx.expression(1))
                );
                resolveBinaryCall("System", "Intersect", intersect);
                return intersect;
            case "except":
                Except except = of.createExcept().withOperand(
                        parseExpression(ctx.expression(0)),
                        parseExpression(ctx.expression(1))
                );
                resolveBinaryCall("System", "Except", except);
                return except;
        }

        return of.createNull();
    }

    @Override
    public Expression visitEqualityExpression(@NotNull cqlParser.EqualityExpressionContext ctx) {
        BinaryExpression equal = of.createEqual().withOperand(
                parseExpression(ctx.expression(0)),
                parseExpression(ctx.expression(1)));

        resolveBinaryCall("System", "Equal", equal);
        if (!"=".equals(parseString(ctx.getChild(1)))) {
            Not not = of.createNot().withOperand(equal);
            resolveUnaryCall("System", "Not", not);
            return not;
        }

        return equal;
    }

    @Override
    public BinaryExpression visitInequalityExpression(@NotNull cqlParser.InequalityExpressionContext ctx) {
        BinaryExpression exp;
        String operatorName;
        switch (parseString(ctx.getChild(1))) {
            case "<=":
                operatorName = "LessOrEqual";
                exp = of.createLessOrEqual();
                break;
            case "<":
                operatorName = "Less";
                exp = of.createLess();
                break;
            case ">":
                operatorName = "Greater";
                exp = of.createGreater();
                break;
            case ">=":
                operatorName = "GreaterOrEqual";
                exp = of.createGreaterOrEqual();
                break;
            default:
                throw new IllegalArgumentException(String.format("Unknown operator: %s", ctx.getChild(1).getText()));
        }
        exp.withOperand(
                parseExpression(ctx.expression(0)),
                parseExpression(ctx.expression(1)));

        resolveBinaryCall("System", operatorName, exp);
        return exp;
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
            TranslatedLibrary referencedLibrary = resolveLibrary(((LibraryRef) left).getLibraryName());

            Element element = referencedLibrary.resolve(memberIdentifier);

            if (element instanceof ExpressionDef) {
                Expression result = of.createExpressionRef()
                        .withLibraryName(((LibraryRef) left).getLibraryName())
                        .withName(memberIdentifier);
                result.setResultType(element.getResultType());
                return result;
            }

            if (element instanceof ParameterDef) {
                Expression result = of.createParameterRef()
                        .withLibraryName(((LibraryRef) left).getLibraryName())
                        .withName(memberIdentifier);
                result.setResultType(element.getResultType());
                return result;
            }

            if (element instanceof ValueSetDef) {
                ValueSetRef result = of.createValueSetRef()
                        .withLibraryName(((LibraryRef) left).getLibraryName())
                        .withName(memberIdentifier);
                result.setResultType(new ListType(resolveTypeName("Code")));
                return result;
            }

            IdentifierRef identifier = new IdentifierRef();
            identifier.setLibraryName(((LibraryRef) left).getLibraryName());
            identifier.setName(memberIdentifier);
            return identifier;
        }
        else if (left instanceof AliasRef) {
            Property result = of.createProperty()
                    .withScope(((AliasRef) left).getName())
                    .withPath(memberIdentifier);
            result.setResultType(resolveProperty(left.getResultType(), memberIdentifier));
            return result;
        }
        else {
            Property result = of.createProperty()
                    .withSource(left)
                    .withPath(memberIdentifier);
            result.setResultType(resolveProperty(left.getResultType(), memberIdentifier));
            return result;
        }
//        else if (left instanceof ExpressionRef) {
//            Property result = of.createProperty()
//                    .withSource(left)
//                    .withPath(memberIdentifier);
//            result.setResultType(resolveProperty(left.getResultType(), memberIdentifier));
//            return result;
//        }
//        else if (left instanceof Property) {
//            Property property = (Property) left;
//            property.setPath(String.format("%s.%s", property.getPath(), memberIdentifier));
//            property.setResultType(resolveProperty(left.getResultType(), memberIdentifier));
//            return property;
//        } else if (left instanceof IdentifierRef) {
//            IdentifierRef identifier = (IdentifierRef) left;
//            identifier.setName(String.format("%s.%s", identifier.getName(), memberIdentifier));
//            return identifier;
//        }

//        throw new IllegalArgumentException(String.format("Unexpected left hand expression type: %s.", left.getClass().getSimpleName()));
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
        // 2: The name of a query define clause
        // 3: The name of an expression
        // 4: The name of a parameter
        // 5: The name of a valueset
        // 6: The name of a library
        // 7: An unresolved identifier that must be resolved later (by a method or accessor)

        AliasedQuerySource alias = resolveAlias(identifier);
        if (alias != null) {
            AliasRef result = of.createAliasRef().withName(identifier);
            if (alias.getResultType() instanceof ListType) {
                result.setResultType(((ListType)alias.getResultType()).getElementType());
            }
            else {
                result.setResultType(alias.getResultType());
            }
            return result;
        }

        DefineClause define = resolveQueryDefine(identifier);
        if (define != null) {
            QueryDefineRef result = of.createQueryDefineRef().withName(identifier);
            result.setResultType(define.getResultType());
            return result;
        }

        Element element = translatedLibrary.resolve(identifier);
        if (element == null) {
            ExpressionDefinitionInfo expressionInfo = libraryInfo.resolveExpressionReference(identifier);

            if (expressionInfo != null) {
                String saveContext = currentContext;
                currentContext = expressionInfo.getContext();
                try {
                    ExpressionDef expressionDef = visitExpressionDefinition(expressionInfo.getDefinition());
                    element = expressionDef;
                } finally {
                    currentContext = saveContext;
                }
            }

            ParameterDefinitionInfo parameterInfo = libraryInfo.resolveParameterReference(identifier);
            if (parameterInfo != null) {
                ParameterDef parameterDef = visitParameterDefinition(parameterInfo.getDefinition());
                element = parameterDef;
            }
        }

        if (element instanceof ExpressionDef) {
            ExpressionRef expressionRef = of.createExpressionRef().withName(((ExpressionDef) element).getName());
            expressionRef.setResultType(element.getResultType());
            return expressionRef;
        }

        if (element instanceof ParameterDef) {
            ParameterRef parameterRef = of.createParameterRef().withName(((ParameterDef) element).getName());
            parameterRef.setResultType(element.getResultType());
            return parameterRef;
        }

        if (element instanceof ValueSetDef) {
            ValueSetRef valuesetRef = of.createValueSetRef().withName(((ValueSetDef) element).getName());
            valuesetRef.setResultType(element.getResultType());
            return valuesetRef;
        }

        if (element instanceof IncludeDef) {
            LibraryRef libraryRef = new LibraryRef();
            libraryRef.setLibraryName(((IncludeDef) element).getLocalIdentifier());
            return libraryRef;
        }

        IdentifierRef id = of.createIdentifierRef();
        id.setName(identifier);
        return id;
    }

    public Expression resolveQualifiedIdentifier(List<String> identifiers) {
        Expression current = null;
        for (String identifier : identifiers) {
            if (current == null) {
                current = resolveIdentifier(identifier);
            } else {
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
        TypeSpecifier targetType = parseTypeSpecifier(ctx.typeSpecifier());
        Convert result = of.createConvert().withOperand(parseExpression(ctx.expression()))
                .withToType(dataTypeToQName(targetType.getResultType()));
        result.setResultType(targetType.getResultType());
        return result;
    }

    @Override
    public Object visitTypeExpression(@NotNull cqlParser.TypeExpressionContext ctx) {
        if (ctx.getChild(1).getText().equals("is")) {
            Is is = of.createIs()
                    .withOperand(parseExpression(ctx.expression()))
                    .withIsTypeSpecifier(parseTypeSpecifier(ctx.typeSpecifier()));
            is.setResultType(resolveTypeName("Boolean"));
            return is;
        }

        As as = of.createAs()
                .withOperand(parseExpression(ctx.expression()))
                .withAsTypeSpecifier(parseTypeSpecifier(ctx.typeSpecifier()))
                .withStrict(false);
        as.setResultType(as.getAsTypeSpecifier().getResultType());
        return as;
    }

    @Override
    public Object visitCastExpression(@NotNull cqlParser.CastExpressionContext ctx) {
        As as = of.createAs()
                .withOperand(parseExpression(ctx.expression()))
                .withAsTypeSpecifier(parseTypeSpecifier(ctx.typeSpecifier()))
                .withStrict(true);
        as.setResultType(as.getAsTypeSpecifier().getResultType());
        return as;
    }

    @Override
    public Expression visitBooleanExpression(@NotNull cqlParser.BooleanExpressionContext ctx) {
        Expression exp;
        Expression left = (Expression) visit(ctx.expression());
        String lastChild = ctx.getChild(ctx.getChildCount() - 1).getText();
        String nextToLast = ctx.getChild(ctx.getChildCount() - 2).getText();
        if (lastChild.equals("null")) {
            exp = of.createIsNull().withOperand(left);
            resolveUnaryCall("System", "IsNull", (UnaryExpression)exp);
        } else {
            exp = of.createEqual().withOperand(left, createLiteral(Boolean.valueOf(lastChild)));
            resolveBinaryCall("System", "Equal", (BinaryExpression)exp);
        }

        if ("not".equals(nextToLast)) {
            exp = of.createNot().withOperand(exp);
            resolveUnaryCall("System", "Not", (UnaryExpression)exp);
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
        } finally {
            timingOperators.pop();
        }
    }

    @Override
    public Object visitConcurrentWithIntervalOperatorPhrase(@NotNull cqlParser.ConcurrentWithIntervalOperatorPhraseContext ctx) {
        // ('starts' | 'ends')? 'same' dateTimePrecision? (relativeQualifier | 'as') ('start' | 'end')?
        TimingOperatorContext timingOperator = timingOperators.peek();
        ParseTree firstChild = ctx.getChild(0);
        if ("starts".equals(firstChild.getText())) {
            Start start = of.createStart().withOperand(timingOperator.getLeft());
            resolveUnaryCall("System", "Start", start);
            timingOperator.setLeft(start);
        }

        if ("ends".equals(firstChild.getText())) {
            End end = of.createEnd().withOperand(timingOperator.getLeft());
            resolveUnaryCall("System", "End", end);
            timingOperator.setLeft(end);
        }

        ParseTree lastChild = ctx.getChild(ctx.getChildCount() - 1);
        if ("start".equals(lastChild.getText())) {
            Start start = of.createStart().withOperand(timingOperator.getRight());
            resolveUnaryCall("System", "Start", start);
            timingOperator.setRight(start);
        }

        if ("end".equals(lastChild.getText())) {
            End end = of.createEnd().withOperand(timingOperator.getRight());
            resolveUnaryCall("System", "End", end);
            timingOperator.setRight(end);
        }

        String operatorName = null;
        BinaryExpression operator = null;
        if (ctx.relativeQualifier() == null) {
            if (ctx.dateTimePrecision() != null) {
                operator = of.createSameAs().withPrecision(parseDateTimePrecision(ctx.dateTimePrecision().getText()));
            } else {
                operator = of.createSameAs();
            }
            operatorName = "SameAs";
        } else {
            switch (ctx.relativeQualifier().getText()) {
                case "or after": {
                    if (ctx.dateTimePrecision() != null) {
                        operator = of.createSameOrAfter().withPrecision(parseDateTimePrecision(ctx.dateTimePrecision().getText()));
                    } else {
                        operator = of.createSameOrAfter();
                    }
                    operatorName = "SameOrAfter";
                }
                break;
                case "or before": {
                    if (ctx.dateTimePrecision() != null) {
                        operator = of.createSameOrBefore().withPrecision(parseDateTimePrecision(ctx.dateTimePrecision().getText()));
                    } else {
                        operator = of.createSameOrBefore();
                    }
                    operatorName = "SameOrBefore";
                }
                break;
                default:
                    throw new IllegalArgumentException(String.format("Unknown relative qualifier: '%s'.", ctx.relativeQualifier().getText()));
            }
        }

        operator = operator.withOperand(timingOperator.getLeft(), timingOperator.getRight());
        resolveBinaryCall("System", operatorName, operator);

        return operator;
    }

    @Override
    public Object visitIncludesIntervalOperatorPhrase(@NotNull cqlParser.IncludesIntervalOperatorPhraseContext ctx) {
        // 'properly'? 'includes' dateTimePrecisionSpecifier? ('start' | 'end')?
        boolean isProper = false;
        boolean isRightPoint = false;
        TimingOperatorContext timingOperator = timingOperators.peek();
        for (ParseTree pt : ctx.children) {
            if ("properly".equals(pt.getText())) {
                isProper = true;
                continue;
            }

            if ("start".equals(pt.getText())) {
                Start start = of.createStart().withOperand(timingOperator.getRight());
                resolveUnaryCall("System", "Start", start);
                timingOperator.setRight(start);
                isRightPoint = true;
                continue;
            }

            if ("end".equals(pt.getText())) {
                End end = of.createEnd().withOperand(timingOperator.getRight());
                resolveUnaryCall("System", "End", end);
                timingOperator.setRight(end);
                isRightPoint = true;
                continue;
            }
        }

        String dateTimePrecision = ctx.dateTimePrecisionSpecifier() != null
                ? ctx.dateTimePrecisionSpecifier().dateTimePrecision().getText()
                : null;

        if (!isRightPoint &&
                !(timingOperator.getRight().getResultType() instanceof IntervalType
                        || timingOperator.getRight().getResultType() instanceof ListType)) {
            isRightPoint = true;
        }

        if (isRightPoint) {
            // TODO: Handle is proper (no ELM representation for ProperContains)
            if (dateTimePrecision != null) {
                Contains contains = of.createContains().withPrecision(parseDateTimePrecision(dateTimePrecision))
                        .withOperand(timingOperator.getLeft(), timingOperator.getRight());
                resolveBinaryCall("System", "Contains", contains);
                return contains;
            }

            Contains contains = of.createContains().withOperand(timingOperator.getLeft(), timingOperator.getRight());
            resolveBinaryCall("System", "Contains", contains);
            return contains;
        }

        if (isProper) {

            if (dateTimePrecision != null) {
                ProperIncludes properIncludes = of.createProperIncludes().withPrecision(parseDateTimePrecision(dateTimePrecision))
                        .withOperand(timingOperator.getLeft(), timingOperator.getRight());
                resolveBinaryCall("System", "ProperIncludes", properIncludes);
                return properIncludes;
            }

            ProperIncludes properIncludes = of.createProperIncludes().withOperand(timingOperator.getLeft(), timingOperator.getRight());
            resolveBinaryCall("System", "ProperIncludes", properIncludes);
            return properIncludes;
        }

        if (dateTimePrecision != null) {
            Includes includes = of.createIncludes().withPrecision(parseDateTimePrecision(dateTimePrecision))
                    .withOperand(timingOperator.getLeft(), timingOperator.getRight());
            resolveBinaryCall("System", "Includes", includes);
            return includes;
        }

        Includes includes = of.createIncludes().withOperand(timingOperator.getLeft(), timingOperator.getRight());
        resolveBinaryCall("System", "Includes", includes);
        return includes;
    }

    @Override
    public Object visitIncludedInIntervalOperatorPhrase(@NotNull cqlParser.IncludedInIntervalOperatorPhraseContext ctx) {
        // ('starts' | 'ends')? 'properly'? ('during' | 'included in') dateTimePrecisionSpecifier?
        boolean isProper = false;
        boolean isLeftPoint = false;
        TimingOperatorContext timingOperator = timingOperators.peek();
        for (ParseTree pt : ctx.children) {
            if ("starts".equals(pt.getText())) {
                Start start = of.createStart().withOperand(timingOperator.getLeft());
                resolveUnaryCall("System", "Start", start);
                timingOperator.setLeft(start);
                isLeftPoint = true;
                continue;
            }

            if ("ends".equals(pt.getText())) {
                End end = of.createEnd().withOperand(timingOperator.getLeft());
                resolveUnaryCall("System", "End", end);
                timingOperator.setLeft(end);
                isLeftPoint = true;
                continue;
            }

            if ("properly".equals(pt.getText())) {
                isProper = true;
                continue;
            }
        }

        String dateTimePrecision = ctx.dateTimePrecisionSpecifier() != null
                ? ctx.dateTimePrecisionSpecifier().dateTimePrecision().getText()
                : null;

        if (!isLeftPoint &&
                !(timingOperator.getLeft().getResultType() instanceof IntervalType
                        || timingOperator.getLeft().getResultType() instanceof ListType)) {
            isLeftPoint = true;
        }

        if (isLeftPoint) {
            // TODO: Handle is proper (no ELM representation for ProperIn)
            if (isProper) {
                throw new IllegalArgumentException("Properly modifier can only be used with interval-to-interval comparisons.");
            }

            if (dateTimePrecision != null) {
                In in = of.createIn().withPrecision(parseDateTimePrecision(dateTimePrecision))
                        .withOperand(timingOperator.getLeft(), timingOperator.getRight());
                resolveBinaryCall("System", "In", in);
                return in;
            }

            In in = of.createIn().withOperand(timingOperator.getLeft(), timingOperator.getRight());
            resolveBinaryCall("System", "In", in);
            return in;
        }

        if (isProper) {
            if (dateTimePrecision != null) {
                ProperIncludedIn properIncludedIn = of.createProperIncludedIn().withPrecision(parseDateTimePrecision(dateTimePrecision))
                        .withOperand(timingOperator.getLeft(), timingOperator.getRight());
                resolveBinaryCall("System", "ProperIncludedIn", properIncludedIn);
                return properIncludedIn;
            }

            ProperIncludedIn properIncludedIn = of.createProperIncludedIn().withOperand(timingOperator.getLeft(), timingOperator.getRight());
            resolveBinaryCall("System", "ProperIncludedIn", properIncludedIn);
            return properIncludedIn;
        }

        if (dateTimePrecision != null) {
            IncludedIn includedIn = of.createIncludedIn().withPrecision(parseDateTimePrecision(dateTimePrecision))
                    .withOperand(timingOperator.getLeft(), timingOperator.getRight());
            resolveBinaryCall("System", "IncludedIn", includedIn);
            return includedIn;
        }

        IncludedIn includedIn = of.createIncludedIn().withOperand(timingOperator.getLeft(), timingOperator.getRight());
        resolveBinaryCall("System", "IncludedIn", includedIn);
        return includedIn;
    }

    @Override
    public Object visitBeforeOrAfterIntervalOperatorPhrase(@NotNull cqlParser.BeforeOrAfterIntervalOperatorPhraseContext ctx) {
        // ('starts' | 'ends')? quantityOffset? ('before' | 'after') dateTimePrecisionSpecifier? ('start' | 'end')?

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
                Start start = of.createStart().withOperand(timingOperator.getLeft());
                resolveUnaryCall("System", "Start", start);
                timingOperator.setLeft(start);
                continue;
            }

            if ("ends".equals(child.getText())) {
                End end = of.createEnd().withOperand(timingOperator.getLeft());
                resolveUnaryCall("System", "End", end);
                timingOperator.setLeft(end);
                continue;
            }

            if ("start".equals(child.getText())) {
                Start start = of.createStart().withOperand(timingOperator.getRight());
                resolveUnaryCall("System", "Start", start);
                timingOperator.setRight(start);
                continue;
            }

            if ("end".equals(child.getText())) {
                End end = of.createEnd().withOperand(timingOperator.getRight());
                resolveUnaryCall("System", "End", end);
                timingOperator.setRight(end);
                continue;
            }

            if ("before".equals(child.getText())) {
                isBefore = true;
                continue;
            }
        }

        String dateTimePrecision = ctx.dateTimePrecisionSpecifier() != null
                ? ctx.dateTimePrecisionSpecifier().dateTimePrecision().getText()
                : null;

        if (ctx.quantityOffset() == null) {
            if (isBefore) {
                if (dateTimePrecision != null) {
                    Before before = of.createBefore()
                            .withPrecision(parseDateTimePrecision(dateTimePrecision))
                            .withOperand(timingOperator.getLeft(), timingOperator.getRight());
                    resolveBinaryCall("System", "Before", before);
                    return before;
                }

                Before before = of.createBefore().withOperand(timingOperator.getLeft(), timingOperator.getRight());
                resolveBinaryCall("System", "Before", before);
                return before;
            } else {
                if (dateTimePrecision != null) {
                    After after = of.createAfter()
                            .withPrecision(parseDateTimePrecision(dateTimePrecision))
                            .withOperand(timingOperator.getLeft(), timingOperator.getRight());
                    resolveBinaryCall("System", "After", after);
                    return after;
                }

                After after = of.createAfter().withOperand(timingOperator.getLeft(), timingOperator.getRight());
                resolveBinaryCall("System", "After", after);
                return after;
            }
        } else {
            Quantity quantity = (Quantity)visit(ctx.quantityOffset().quantityLiteral());
            Literal quantityLiteral = createLiteral(quantity.getValue().intValueExact());
            BinaryExpression betweenOperator = resolveBetweenOperator(quantity.getUnit(),
                    timingOperator.getLeft(), timingOperator.getRight());
            if (betweenOperator != null) {
                if (ctx.quantityOffset().offsetRelativeQualifier() == null) {
                    if (isBefore) {
                        Equal equal = of.createEqual().withOperand(betweenOperator, quantityLiteral);
                        resolveBinaryCall("System", "Equal", equal);
                        return equal;
                    } else {
                        Negate negate = of.createNegate().withOperand(quantityLiteral);
                        resolveUnaryCall("System", "Negate", negate);
                        Equal equal = of.createEqual().withOperand(betweenOperator, negate);
                        resolveBinaryCall("System", "Equal", equal);
                        return equal;
                    }
                } else {
                    switch (ctx.quantityOffset().offsetRelativeQualifier().getText()) {
                        case "or more":
                            if (isBefore) {
                                GreaterOrEqual greaterOrEqual = of.createGreaterOrEqual().withOperand(
                                        betweenOperator,
                                        quantityLiteral
                                );
                                resolveBinaryCall("System", "GreaterOrEqual", greaterOrEqual);
                                return greaterOrEqual;
                            } else {
                                Negate negate = of.createNegate().withOperand(quantityLiteral);
                                resolveUnaryCall("System", "Negate", negate);
                                LessOrEqual lessOrEqual = of.createLessOrEqual().withOperand(betweenOperator, negate);
                                resolveBinaryCall("System", "LessOrEqual", lessOrEqual);
                                return lessOrEqual;
                            }
                        case "or less":
                            if (isBefore) {
                                Interval quantityInterval = of.createInterval()
                                        .withLow(createLiteral(0)).withLowClosed(false)
                                        .withHigh(quantityLiteral).withHighClosed(true);
                                quantityInterval.setResultType(new IntervalType(quantityInterval.getLow().getResultType()));
                                In in = of.createIn().withOperand(betweenOperator, quantityInterval);
                                resolveBinaryCall("System", "In", in);
                                return in;
                            } else {
                                Negate negate = of.createNegate().withOperand(quantityLiteral);
                                resolveUnaryCall("System", "Negate", negate);
                                Interval quantityInterval = of.createInterval()
                                        .withLow(negate).withLowClosed(true)
                                        .withHigh(createLiteral(0)).withHighClosed(false);
                                quantityInterval.setResultType(new IntervalType(quantityInterval.getLow().getResultType()));
                                In in = of.createIn().withOperand(betweenOperator, quantityInterval);
                                resolveBinaryCall("System", "In", in);
                                return in;
                            }
                    }
                }
            }
        }

        throw new IllegalArgumentException("Unable to resolve interval operator phrase.");
    }

    private BinaryExpression resolveBetweenOperator(String unit, Expression left, Expression right) {
        if (unit != null) {
            DurationBetween between = of.createDurationBetween().withPrecision(parseDateTimePrecision(unit)).withOperand(left, right);
            resolveBinaryCall("System", "DurationBetween", between);
            return between;
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
                Start start = of.createStart().withOperand(timingOperator.getLeft());
                resolveUnaryCall("System", "Start", start);
                timingOperator.setLeft(start);
                continue;
            }

            if ("ends".equals(child.getText())) {
                End end = of.createEnd().withOperand(timingOperator.getLeft());
                resolveUnaryCall("System", "End", end);
                continue;
            }

            if ("start".equals(child.getText())) {
                Start start = of.createStart().withOperand(timingOperator.getRight());
                resolveUnaryCall("System", "Start", start);
                timingOperator.setRight(start);
                continue;
            }

            if ("end".equals(child.getText())) {
                End end = of.createEnd().withOperand(timingOperator.getRight());
                resolveUnaryCall("System", "End", end);
                timingOperator.setRight(end);
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
        quantityInterval.setResultType(new IntervalType(quantityLiteral.getResultType()));
        BinaryExpression betweenOperator = resolveBetweenOperator(quantity.getUnit(),
                timingOperator.getLeft(), timingOperator.getRight());
        if (betweenOperator != null) {
            In in = of.createIn().withOperand(betweenOperator, quantityInterval);
            resolveBinaryCall("System", "In", in);
            return in;
        }

        throw new IllegalArgumentException("Could not resolve interval operator phrase.");
    }

    @Override
    public Object visitMeetsIntervalOperatorPhrase(@NotNull cqlParser.MeetsIntervalOperatorPhraseContext ctx) {
        String operatorName = null;
        BinaryExpression operator;
        String dateTimePrecision = ctx.dateTimePrecisionSpecifier() != null
                ? ctx.dateTimePrecisionSpecifier().dateTimePrecision().getText()
                : null;

        if (ctx.getChildCount() == (1 + (dateTimePrecision == null ? 0 : 1))) {
            operator = dateTimePrecision != null
                    ? of.createMeets().withPrecision(parseDateTimePrecision(dateTimePrecision))
                    : of.createMeets();
            operatorName = "Meets";
        } else {
            if ("before".equals(ctx.getChild(1).getText())) {
                operator = dateTimePrecision != null
                        ? of.createMeetsBefore().withPrecision(parseDateTimePrecision(dateTimePrecision))
                        : of.createMeetsBefore();
                operatorName = "MeetsBefore";
            } else {
                operator = dateTimePrecision != null
                        ? of.createMeetsAfter().withPrecision(parseDateTimePrecision(dateTimePrecision))
                        : of.createMeetsAfter();
                operatorName = "MeetsAfter";
            }
        }

        operator.withOperand(timingOperators.peek().getLeft(), timingOperators.peek().getRight());
        resolveBinaryCall("System", operatorName, operator);
        return operator;
    }

    @Override
    public Object visitOverlapsIntervalOperatorPhrase(@NotNull cqlParser.OverlapsIntervalOperatorPhraseContext ctx) {
        String operatorName = null;
        BinaryExpression operator;
        String dateTimePrecision = ctx.dateTimePrecisionSpecifier() != null
                ? ctx.dateTimePrecisionSpecifier().dateTimePrecision().getText()
                : null;

        if (ctx.getChildCount() == (1 + dateTimePrecision == null ? 0 : 1)) {
            operator = dateTimePrecision != null
                    ? of.createOverlaps().withPrecision(parseDateTimePrecision(dateTimePrecision))
                    : of.createOverlaps();
            operatorName = "Overlaps";
        } else {
            if ("before".equals(ctx.getChild(1).getText())) {
                operator = dateTimePrecision != null
                        ? of.createOverlapsBefore().withPrecision(parseDateTimePrecision(dateTimePrecision))
                        : of.createOverlapsBefore();
                operatorName = "OverlapsBefore";
            } else {
                operator = dateTimePrecision != null
                        ? of.createOverlapsAfter().withPrecision(parseDateTimePrecision(dateTimePrecision))
                        : of.createOverlapsAfter();
                operatorName = "OverlapsAfter";
            }
        }

        operator.withOperand(timingOperators.peek().getLeft(), timingOperators.peek().getRight());
        resolveBinaryCall("System", operatorName, operator);
        return operator;
    }

    @Override
    public Object visitStartsIntervalOperatorPhrase(@NotNull cqlParser.StartsIntervalOperatorPhraseContext ctx) {
        String dateTimePrecision = ctx.dateTimePrecisionSpecifier() != null
                ? ctx.dateTimePrecisionSpecifier().dateTimePrecision().getText()
                : null;

        Starts starts = (dateTimePrecision != null
                ? of.createStarts().withPrecision(parseDateTimePrecision(dateTimePrecision))
                : of.createStarts()
        ).withOperand(timingOperators.peek().getLeft(), timingOperators.peek().getRight());

        resolveBinaryCall("System", "Starts", starts);
        return starts;
    }

    @Override
    public Object visitEndsIntervalOperatorPhrase(@NotNull cqlParser.EndsIntervalOperatorPhraseContext ctx) {
        String dateTimePrecision = ctx.dateTimePrecisionSpecifier() != null
                ? ctx.dateTimePrecisionSpecifier().dateTimePrecision().getText()
                : null;

        Ends ends = (dateTimePrecision != null
                ? of.createEnds().withPrecision(parseDateTimePrecision(dateTimePrecision))
                : of.createEnds()
        ).withOperand(timingOperators.peek().getLeft(), timingOperators.peek().getRight());

        resolveBinaryCall("System", "Ends", ends);
        return ends;
    }

    @Override
    public Object visitIfThenElseExpressionTerm(@NotNull cqlParser.IfThenElseExpressionTermContext ctx) {
        If ifObject = of.createIf()
                .withCondition(parseExpression(ctx.expression(0)))
                .withThen(parseExpression(ctx.expression(1)))
                .withElse(parseExpression(ctx.expression(2)));

        DataTypes.verifyType(ifObject.getCondition().getResultType(), resolveTypeName("Boolean"));
        DataType thenType = ifObject.getThen().getResultType();
        DataTypes.verifyType(ifObject.getElse().getResultType(), thenType);

        ifObject.setResultType(thenType);
        return ifObject;
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
                    DataTypes.verifyType(result.getResultType(), result.getElse().getResultType());
                } else {
                    result.setComparand(parseExpression(pt));
                }
            }

            if (pt instanceof cqlParser.CaseExpressionItemContext) {
                CaseItem caseItem = (CaseItem)visit(pt);
                if (result.getComparand() != null) {
                    DataTypes.verifyType(caseItem.getWhen().getResultType(), result.getComparand().getResultType());
                }
                else {
                    DataTypes.verifyType(caseItem.getWhen().getResultType(), resolveTypeName("Boolean"));
                }
                if (result.getResultType() == null) {
                    result.setResultType(caseItem.getThen().getResultType());
                }
                else {
                    DataTypes.verifyType(result.getResultType(), caseItem.getThen().getResultType());
                }
                result.getCaseItem().add(caseItem);
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

        Expression first = null;
        for (cqlParser.ExpressionContext expression : ctx.expression()) {
            if (first == null) {
                first = parseExpression(expression);
                expressions.add(first);
            }
            else {
                Expression other = parseExpression(expression);
                DataTypes.verifyType(first.getResultType(), other.getResultType());
            }
        }

        Coalesce coalesce = of.createCoalesce().withOperand(expressions);
        coalesce.setResultType(first.getResultType());
        return coalesce;
    }

    @Override
    public Object visitAggregateExpressionTerm(@NotNull cqlParser.AggregateExpressionTermContext ctx) {
        switch (ctx.getChild(0).getText()) {
            case "distinct":
                Distinct distinct = of.createDistinct().withSource(parseExpression(ctx.expression()));
                resolveCall("System", "Distinct", distinct, distinct.getSource().getResultType());
                return distinct;
            case "collapse":
                Collapse collapse = of.createCollapse().withOperand(parseExpression(ctx.expression()));
                resolveCall("System", "Collapse", collapse, collapse.getOperand().getResultType());
                return collapse;
            case "expand":
                Expand expand = of.createExpand().withOperand(parseExpression(ctx.expression()));
                resolveCall("System", "Expand", expand, expand.getOperand().getResultType());
                return expand;
        }

        throw new IllegalArgumentException(String.format("Unknown aggregate operator %s.", ctx.getChild(0).getText()));
    }

    @Override
    public Retrieve visitRetrieve(@NotNull cqlParser.RetrieveContext ctx) {
        String model = parseString(ctx.topic().namedTypeSpecifier().modelIdentifier());
        String topic = parseString(ctx.topic().namedTypeSpecifier().identifier());
        ClassType classType = resolveTopic(model, topic);
        NamedType namedType = classType;
        if (namedType == null) {
            namedType = (NamedType)resolveTypeName(model, topic);
            if (namedType == null) {
                throw new IllegalArgumentException(String.format("Could not resolve type name %s.", topic));
            }
        }

        Retrieve retrieve = of.createRetrieve()
                .withDataType(dataTypeToQName((DataType)namedType))
                .withTemplateId(classType != null ? classType.getIdentifier() : topic);

        if (ctx.valueset() != null) {
            if (ctx.valuesetPathIdentifier() != null) {
                retrieve.setCodeProperty(parseString(ctx.valuesetPathIdentifier()));
            } else if (classType != null && classType.getPrimaryCodePath() != null) {
                retrieve.setCodeProperty(classType.getPrimaryCodePath());
            }

            List<String> identifiers = (List<String>) visit(ctx.valueset());
            retrieve.setCodes(resolveQualifiedIdentifier(identifiers));
        }

        retrieves.add(retrieve);

        retrieve.setResultType(new ListType((DataType)namedType));

        return retrieve;
    }

    @Override
    public Object visitSingleSourceClause(@NotNull cqlParser.SingleSourceClauseContext ctx) {
        List<AliasedQuerySource> sources = new ArrayList<>();
        sources.add((AliasedQuerySource) visit(ctx.aliasedQuerySource()));
        return sources;
    }

    @Override
    public Object visitMultipleSourceClause(@NotNull cqlParser.MultipleSourceClauseContext ctx) {
        List<AliasedQuerySource> sources = new ArrayList<>();
        for (cqlParser.AliasedQuerySourceContext source : ctx.aliasedQuerySource()) {
            sources.add((AliasedQuerySource) visit(source));
        }
        return sources;
    }

    @Override
    public Object visitQuery(@NotNull cqlParser.QueryContext ctx) {
        QueryContext queryContext = new QueryContext();
        List<AliasedQuerySource> sources = (List<AliasedQuerySource>) visit(ctx.sourceClause());
        queryContext.addQuerySources(sources);
        queries.push(queryContext);
        try {

            List<DefineClause> dfcx = ctx.defineClause() != null ? (List<DefineClause>) visit(ctx.defineClause()) : null;
            if (dfcx != null) {
                queryContext.addDefineClauses(dfcx);
            }

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

            ReturnClause ret = ctx.returnClause() != null ? (ReturnClause) visit(ctx.returnClause()) : null;
            if ((ret == null) && (sources.size() > 1)) {
                ret = of.createReturnClause()
                        .withDistinct(true);

                Tuple returnExpression = of.createTuple();
                TupleType returnType = new TupleType();
                Boolean anyLists = false;
                for (AliasedQuerySource aqs : sources) {
                    TupleElement element =
                            of.createTupleElement()
                                    .withName(aqs.getAlias())
                                    .withValue(of.createAliasRef().withName(aqs.getAlias()));
                    element.setResultType(element.getValue().getResultType());
                    returnType.addElement(new TupleTypeElement(element.getName(), element.getResultType()));
                    returnExpression.getElement().add(element);
                    if (aqs.getResultType() instanceof ListType) {
                        anyLists = true;
                    }
                }

                returnExpression.setResultType(anyLists ? new ListType(returnType) : returnType);
                ret.setExpression(returnExpression);
                ret.setResultType(returnExpression.getResultType());
            }

            SortClause sort = ctx.sortClause() != null ? (SortClause) visit(ctx.sortClause()) : null;

            Query query = of.createQuery()
                    .withSource(sources)
                    .withDefine(dfcx)
                    .withRelationship(qicx)
                    .withWhere(where)
                    .withReturn(ret)
                    .withSort(sort);

            if (ret == null) {
                query.setResultType(sources.get(0).getResultType());
            }
            else {
                query.setResultType(ret.getResultType());
            }

            return query;
        } finally {
            queries.pop();
        }
    }

    // TODO: Expand this optimization to work the DateLow/DateHigh property attributes

    /**
     * Some systems may wish to optimize performance by restricting retrieves with available date ranges.  Specifying
     * date ranges in a retrieve was removed from the CQL grammar, but it is still possible to extract date ranges from
     * the where clause and put them in the Retrieve in ELM.  The <code>optimizeDateRangeInQuery</code> method
     * attempts to do this automatically.  If optimization is possible, it will remove the corresponding "during" from
     * the where clause and insert the date range into the Retrieve.
     *
     * @param aqs   the AliasedQuerySource containing the ClinicalRequest to possibly refactor a date range into.
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
     *
     * @param during   the <code>IncludedIn</code> expression to potentially refactor into the <code>Retrieve</code>
     * @param retrieve the <code>Retrieve</code> to add qualifying date ranges to (if applicable)
     * @param alias    the alias of the <code>Retrieve</code> in the query.
     * @return <code>true</code> if the date range was set in the <code>Retrieve</code>; <code>false</code>
     * otherwise.
     */
    private boolean attemptDateRangeOptimization(IncludedIn during, Retrieve retrieve, String alias) {
        if (retrieve.getDateProperty() != null || retrieve.getDateRange() != null) {
            return false;
        }

        Expression left = during.getOperand().get(0);
        Expression right = during.getOperand().get(1);

        if (left instanceof Property) {
            Property property = (Property) left;
            if (alias.equals(property.getScope()) && isRHSEligibleForDateRangeOptimization(right)) {
                retrieve.setDateProperty(property.getPath());
                retrieve.setDateRange(right);
                return true;
            }
        }

        return false;
    }

    /**
     * Test an <code>And</code> expression and determine if it contains any operands (first-level or nested deeper)
     * than are <code>IncludedIn</code> expressions that can be refactored into a <code>Retrieve</code>.  If so,
     * adjust the <code>Retrieve</code> accordingly and reset the corresponding operand to a literal
     * <code>true</code>.  This <code>and</code> branch containing a <code>true</code> can be further consolidated
     * later.
     *
     * @param and      the <code>And</code> expression containing operands to potentially refactor into the
     *                 <code>Retrieve</code>
     * @param retrieve the <code>Retrieve</code> to add qualifying date ranges to (if applicable)
     * @param alias    the alias of the <code>Retrieve</code> in the query.
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
     *
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
     * Determine if the right-hand side of an <code>IncludedIn</code> expression can be refactored into the date range
     * of a <code>Retrieve</code>.  Currently, refactoring is only supported when the RHS is a literal
     * DateTime interval, a literal DateTime, a parameter representing a DateTime interval or a DateTime, or an
     * expression reference representing a DateTime interval or a DateTime.
     *
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
        } else if (rhs instanceof ExpressionRef && !(rhs instanceof FunctionRef)) {
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
        return e != null && e instanceof FunctionRef && "DateTime".equals(((FunctionRef) e).getName());
    }

    private boolean isDateTimeTypeSpecifier(Element e) {
        return e.getResultType().equals(resolveTypeName("System", "DateTime"));
    }

    @Override
    public Object visitDefineClause(@NotNull cqlParser.DefineClauseContext ctx) {
        List<DefineClause> defineClauseItems = new ArrayList<>();
        for (cqlParser.DefineClauseItemContext defineClauseItem : ctx.defineClauseItem()) {
            defineClauseItems.add((DefineClause) visit(defineClauseItem));
        }
        return defineClauseItems;
    }

    @Override
    public Object visitDefineClauseItem(@NotNull cqlParser.DefineClauseItemContext ctx) {
        DefineClause defineClause = of.createDefineClause().withExpression(parseExpression(ctx.expression()))
                .withIdentifier(parseString(ctx.identifier()));
        defineClause.setResultType(defineClause.getExpression().getResultType());
        return defineClause;
    }

    @Override
    public Object visitAliasedQuerySource(@NotNull cqlParser.AliasedQuerySourceContext ctx) {
        AliasedQuerySource source = of.createAliasedQuerySource().withExpression(parseExpression(ctx.querySource()))
                .withAlias(parseString(ctx.alias()));
        source.setResultType(source.getExpression().getResultType());
        return source;
    }

    @Override
    public Object visitWithClause(@NotNull cqlParser.WithClauseContext ctx) {
        AliasedQuerySource aqs = (AliasedQuerySource) visit(ctx.aliasedQuerySource());
        queries.peek().addQuerySource(aqs);
        try {
            Expression expression = (Expression) visit(ctx.expression());
            DataTypes.verifyType(expression.getResultType(), resolveTypeName("Boolean"));
            RelationshipClause result = of.createWith();
            result.withExpression(aqs.getExpression()).withAlias(aqs.getAlias()).withSuchThat(expression);
            result.setResultType(aqs.getResultType());
            return result;
        } finally {
            queries.peek().removeQuerySource(aqs);
        }
    }

    @Override
    public Object visitWithoutClause(@NotNull cqlParser.WithoutClauseContext ctx) {
        AliasedQuerySource aqs = (AliasedQuerySource) visit(ctx.aliasedQuerySource());
        queries.peek().addQuerySource(aqs);
        try {
            Expression expression = (Expression) visit(ctx.expression());
            DataTypes.verifyType(expression.getResultType(), resolveTypeName("Boolean"));
            RelationshipClause result = of.createWithout();
            result.withExpression(aqs.getExpression()).withAlias(aqs.getAlias()).withSuchThat(expression);
            result.setResultType(aqs.getResultType());
            return result;
        } finally {
            queries.peek().removeQuerySource(aqs);
        }
    }

    @Override
    public Object visitWhereClause(@NotNull cqlParser.WhereClauseContext ctx) {
        Expression result = (Expression)visit(ctx.expression());
        DataTypes.verifyType(result.getResultType(), resolveTypeName("Boolean"));
        return result;
    }

    @Override
    public Object visitReturnClause(@NotNull cqlParser.ReturnClauseContext ctx) {
        ReturnClause returnClause = of.createReturnClause();
        if (ctx.getChild(1) instanceof TerminalNode) {
            switch (ctx.getChild(1).getText()) {
                case "all":
                    returnClause.setDistinct(false);
                    break;
                case "distinct":
                    returnClause.setDistinct(true);
                    break;
                default:
                    break;
            }
        }

        returnClause.setExpression(parseExpression(ctx.expression()));

        returnClause.setResultType(returnClause.getExpression().getResultType());

        return returnClause;
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
        } else if (ctx.retrieve() != null) {
            return visit(ctx.retrieve());
        } else {
            List<String> identifiers = (List<String>) visit(ctx.qualifiedIdentifier());
            return resolveQualifiedIdentifier(identifiers);
        }
    }

    @Override
    public Object visitIndexedExpressionTerm(@NotNull cqlParser.IndexedExpressionTermContext ctx) {
        Indexer indexer = of.createIndexer()
                .withOperand(parseExpression(ctx.expressionTerm()))
                .withIndex(parseExpression(ctx.expression()));

        resolveCall("System", "Indexer", indexer, indexer.getOperand().getResultType(), indexer.getIndex().getResultType());
        return indexer;
    }

    @Override
    public Object visitMethodExpressionTerm(@NotNull cqlParser.MethodExpressionTermContext ctx) {
        FunctionRef fun = of.createFunctionRef();
        Expression left = parseExpression(ctx.expressionTerm());

        if (left instanceof IdentifierRef) {
            fun.setLibraryName(((IdentifierRef) left).getLibraryName());
            fun.setName(((IdentifierRef)left).getName());
        }

        if (ctx.expression() != null) {
            for (cqlParser.ExpressionContext expressionContext : ctx.expression()) {
                fun.getOperand().add((Expression) visit(expressionContext));
            }
        }

        // Process Age-related functions
        if (fun.getLibraryName() == null) {
            String ageRelatedFunctionName = resolveAgeRelatedFunction(fun.getName());
            if (ageRelatedFunctionName != null) {
                switch (ageRelatedFunctionName) {
                    case "CalculateAgeInYears":
                    case "CalculateAgeInMonths":
                    case "CalculateAgeInDays":
                    case "CalculateAgeInHours":
                    case "CalculateAgeInMinutes":
                    case "CalculateAgeInSeconds":
                    case "CalculateAgeInMilliseconds": {
                        CalculateAge operator = of.createCalculateAge()
                                .withPrecision(resolveAgeRelatedFunctionPrecision(ageRelatedFunctionName));

                        if (fun.getOperand().size() > 0) {
                            operator.setOperand(fun.getOperand().get(0));
                        } else {
                            Expression source = resolveIdentifier("Patient");
                            Property property = of.createProperty().withSource(source).withPath(getModel().getModelInfo().getPatientBirthDatePropertyName());
                            property.setResultType(resolveProperty(source.getResultType(), property.getPath()));
                            operator.setOperand(property);
                        }

                        resolveUnaryCall(null, "CalculateAge", operator);
                        return operator;
                    }

                    case "CalculateAgeInYearsAt":
                    case "CalculateAgeInMonthsAt":
                    case "CalculateAgeInDaysAt":
                    case "CalculateAgeInHoursAt":
                    case "CalculateAgeInMinutesAt":
                    case "CalculateAgeInSecondsAt":
                    case "CalculateAgeInMillisecondsAt": {
                        CalculateAgeAt operator = of.createCalculateAgeAt()
                                .withPrecision(resolveAgeRelatedFunctionPrecision(ageRelatedFunctionName));

                        operator.getOperand().addAll(fun.getOperand());
                        if (operator.getOperand().size() == 1) {
                            Expression source = resolveIdentifier("Patient");
                            Property property = of.createProperty().withSource(source).withPath(getModel().getModelInfo().getPatientBirthDatePropertyName());
                            property.setResultType(resolveProperty(source.getResultType(), property.getPath()));
                            operator.getOperand().add(0, property);
                        }

                        resolveBinaryCall(null, "CalculateAgeAt", operator);
                        return operator;
                    }
                }
            }
        }

        List<DataType> signature = new ArrayList<>();
        for (Expression operand : fun.getOperand()) {
            signature.add(operand.getResultType());
        }

        resolveCall(fun.getLibraryName(), fun.getName(), fun, signature.toArray(new DataType[signature.size()]));

        return fun;
    }

    private String resolveAgeRelatedFunction(String functionName) {
        switch (functionName) {
            case "AgeInYears":
            case "AgeInMonths":
            case "AgeInDays":
            case "AgeInHours":
            case "AgeInMinutes":
            case "AgeInSeconds":
            case "AgeInMilliseconds":
            case "AgeInYearsAt":
            case "AgeInMonthsAt":
            case "AgeInDaysAt":
            case "AgeInHoursAt":
            case "AgeInMinutesAt":
            case "AgeInSecondsAt":
            case "AgeInMillisecondsAt":
                return "Calculate" + functionName;
            default:
                return null;
        }
    }

    private DateTimePrecision resolveAgeRelatedFunctionPrecision(String functionName) {
        switch (functionName) {
            case "CalculateAgeInYears":
            case "CalculateAgeInYearsAt":
                return DateTimePrecision.YEAR;
            case "CalculateAgeInMonths":
            case "CalculateAgeInMonthsAt":
                return DateTimePrecision.MONTH;
            case "CalculateAgeInDays":
            case "CalculateAgeInDaysAt":
                return DateTimePrecision.DAY;
            case "CalculateAgeInHours":
            case "CalculateAgeInHoursAt":
                return DateTimePrecision.HOUR;
            case "CalculateAgeInMinutes":
            case "CalculateAgeInMinutesAt":
                return DateTimePrecision.MINUTE;
            case "CalculateAgeInSeconds":
            case "CalculateAgeInSecondsAt":
                return DateTimePrecision.SECOND;
            case "CalculateAgeInMilliseconds":
            case "CalculateAgeInMillisecondsAt":
                return DateTimePrecision.MILLISECOND;
            default:
                throw new IllegalArgumentException(String.format("Unknown precision '%s'.", functionName));
        }
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
        fun.setResultType(fun.getExpression().getResultType());
        addToLibrary(fun);

        return fun;
    }

    private UsingDef buildUsingDef(VersionedIdentifier modelIdentifier, Model model) {
        UsingDef usingDef = of.createUsingDef()
                .withLocalIdentifier(modelIdentifier.getId())
                .withVersion(modelIdentifier.getVersion())
                .withUri(model.getModelInfo().getUrl());
        // TODO: Needs to write xmlns and schemalocation to the resulting ELM XML document...

        addToLibrary(usingDef);
        return usingDef;
    }

    private Model buildModel(VersionedIdentifier identifier) {
        Model model = null;
        try {
            ModelInfoProvider provider = ModelInfoLoader.getModelInfoProvider(identifier);
            if (identifier.getId().equals("System")) {
                model = new SystemModel(provider.load());
            }
            else {
                model = new Model(provider.load(), getModel("System"));
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(String.format("Could not load model information for model %s, version %s.",
                    identifier.getId(), identifier.getVersion()));
        }

        return model;
    }

    private Model getModel() {
        return getModel((String)null);
    }

    private Model getModel(String modelName) {
        return getModel(modelName, null);
    }

    private Model getModel(String modelName, String version) {
        if (modelName == null) {
            modelName = "QUICK"; // Default to QUICK
        }

        VersionedIdentifier modelIdentifier = new VersionedIdentifier().withId(modelName).withVersion(version);
        return getModel(modelIdentifier);
    }

    private Model getModel(VersionedIdentifier modelIdentifier) {
        Model model = models.get(modelIdentifier.getId());
        if (model == null) {
            model = buildModel(modelIdentifier);
            models.put(modelIdentifier.getId(), model);
            // Add the model using def to the output
            buildUsingDef(modelIdentifier, model);
        }

        if (modelIdentifier.getVersion() != null && !modelIdentifier.getVersion().equals(model.getModelInfo().getVersion())) {
            throw new IllegalArgumentException(String.format("Could not load model information for model %s, version %s because version %s is already loaded.",
                    modelIdentifier.getId(), modelIdentifier.getVersion(), model.getModelInfo().getVersion()));
        }

        return model;
    }

    private String parseString(ParseTree pt) {
        return pt == null ? null : (String) visit(pt);
    }

    private Expression parseExpression(ParseTree pt) {
        return pt == null ? null : (Expression) visit(pt);
    }

    private TypeSpecifier parseTypeSpecifier(ParseTree pt) {
        return pt == null ? null : (TypeSpecifier) visit(pt);
    }

    private QName dataTypeToQName(DataType type) {
        if (type instanceof NamedType) {
            NamedType namedType = (NamedType)type;
            org.hl7.elm_modelinfo.r1.ModelInfo modelInfo = getModel(namedType.getNamespace()).getModelInfo();
            return new QName(modelInfo.getUrl(), namedType.getSimpleName());
        }

        throw new IllegalArgumentException("A named type is required in this context.");
    }

    private ClassType resolveTopic(String modelName, String topic) {
        ClassType result = null;
        if (modelName == null || modelName.equals("")) {
            for (Model model : models.values()) {
                ClassType modelResult = model.resolveTopic(topic);
                if (modelResult != null) {
                    if (result != null) {
                        throw new IllegalArgumentException(String.format("Topic %s is ambiguous between %s and %s.",
                                topic, result.getName(), modelResult.getName()));
                    }

                    result = modelResult;
                }
            }
        }
        else {
            result = getModel(modelName).resolveTopic(topic);
        }

        return result;
    }

    private DataType resolveTypeName(String typeName) {
        return resolveTypeName(null, typeName);
    }

    private DataType resolveTypeName(String modelName, String typeName) {
        DataType result = null;
        if (modelName == null || modelName.equals("")) {
            for (Model model : models.values()) {
                DataType modelResult = model.resolveTypeName(typeName);
                if (modelResult != null) {
                    if (result != null) {
                        throw new IllegalArgumentException(String.format("Type name %s is ambiguous between %s and %s.",
                                typeName, ((NamedType)result).getName(), ((NamedType)modelResult).getName()));
                    }

                    result = modelResult;
                }
            }
        }
        else {
            result = getModel(modelName).resolveTypeName(typeName);
        }
        return result;
    }

    private DataType resolveProperty(DataType sourceType, String identifier) {
        DataType currentType = sourceType;
        while (currentType != null) {
            if (currentType instanceof ClassType) {
                ClassType classType = (ClassType)currentType;
                for (ClassTypeElement e : classType.getElements()) {
                    if (e.getName().equals(identifier)) {
                        return e.getType();
                    }
                }
            }
            if (currentType instanceof TupleType) {
                TupleType tupleType = (TupleType)currentType;
                for (TupleTypeElement e : tupleType.getElements()) {
                    if (e.getName().equals(identifier)) {
                        return e.getType();
                    }
                }
            }

            if (currentType.getBaseType() != null) {
                currentType = currentType.getBaseType();
            }
            else {
                break;
            }
        }

        // TODO: Resolve property accessors for interval types? The grammar does not provide for this, nor does the specification at this point...

        throw new IllegalArgumentException(String.format("Member %s not found for type %s.", identifier, sourceType));
    }

    private Expression resolveCall(String libraryName, String operatorName, Expression invocation, DataType... signature) {
        for (int i = 0; i < signature.length; i++) {
            if (signature[i] == null) {
                throw new IllegalArgumentException(String.format("Could not determine signature for invocation of operator %s%s.",
                        libraryName == null ? "" : libraryName + ".", operatorName));
            }
        }

        CallContext callContext = new CallContext(libraryName, operatorName, signature);
        Operator operator = resolveCall(callContext);
        checkOperator(callContext, operator);
        invocation.setResultType(operator.getResultType());
        return invocation;
    }

    private Expression resolveUnaryCall(String libraryName, String operatorName, UnaryExpression expression) {
        return resolveCall(libraryName, operatorName, expression, expression.getOperand().getResultType());
    }

    private Expression resolveBinaryCall(String libraryName, String operatorName, BinaryExpression expression) {
        return resolveCall(libraryName, operatorName, expression, expression.getOperand().get(0).getResultType(), expression.getOperand().get(1).getResultType());
    }

    private Operator resolveCall(CallContext callContext) {
        Operator result = null;
        if (callContext.getLibraryName() == null || callContext.getLibraryName().equals("")) {
            result = translatedLibrary.resolveCall(callContext.getOperatorName(), callContext.getSignature());
            if (result == null) {
                for (TranslatedLibrary library : libraries.values()) {
                    Operator libraryResult = library.resolveCall(callContext.getOperatorName(), callContext.getSignature());
                    if (libraryResult != null) {
                        if (result != null) {
                            throw new IllegalArgumentException(String.format("Operator name %s is ambiguous between %s and %s.",
                                    callContext.getOperatorName(), result.getName(), libraryResult.getName()));
                        }

                        result = libraryResult;
                    }
                }
            }
        }
        else {
            result = this.resolveLibrary(callContext.getLibraryName()).resolveCall(callContext.getOperatorName(), callContext.getSignature());
        }

        return result;
    }

    private void checkOperator(CallContext callContext, Operator operator) {
        if (operator == null) {
            throw new IllegalArgumentException(String.format("Could not resolve call to operator %s with signature %s.", callContext.getOperatorName(), callContext.getSignature()));
        }
    }

    private Literal createLiteral(String val, String type) {
        DataType resultType = resolveTypeName(type);
        Literal result = of.createLiteral().withValue(val).withValueType(dataTypeToQName(resultType));
        result.setResultType(resultType);
        return result;
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
            ret = lit.getValueType().equals(dataTypeToQName(resolveTypeName("Boolean")));
            if (ret && bool != null) {
                ret = bool.equals(Boolean.valueOf(lit.getValue()));
            }
        }
        return ret;
    }

    private AliasedQuerySource resolveAlias(String identifier) {
        for (QueryContext query : queries) {
            AliasedQuerySource source = query.resolveAlias(identifier);
            if (source != null) {
                return source;
            }
        }

        return null;
    }

    private DefineClause resolveQueryDefine(String identifier) {
        for (QueryContext query : queries) {
            DefineClause define = query.resolveDefine(identifier);
            if (define != null) {
                return define;
            }
        }

        return null;
    }

    private void setLibraryIdentifier(VersionedIdentifier vid) {
        library.setIdentifier(vid);
        translatedLibrary.setIdentifier(vid);
    }

    private void addToLibrary(UsingDef usingDef) {
        if (library.getUsings() == null) {
            library.setUsings(of.createLibraryUsings());
        }
        library.getUsings().getDef().add(usingDef);

        translatedLibrary.add(usingDef);
    }

    private void addToLibrary(ValueSetDef vs) {
        if (library.getValueSets() == null) {
            library.setValueSets(of.createLibraryValueSets());
        }
        library.getValueSets().getDef().add(vs);

        translatedLibrary.add(vs);
    }

    private void addToLibrary(ParameterDef paramDef) {
        if (library.getParameters() == null) {
            library.setParameters(of.createLibraryParameters());
        }
        library.getParameters().getDef().add(paramDef);

        translatedLibrary.add(paramDef);
    }

    private void addToLibrary(ExpressionDef expDef) {
        if (library.getStatements() == null) {
            library.setStatements(of.createLibraryStatements());
        }
        library.getStatements().getDef().add(expDef);

        translatedLibrary.add(expDef);
    }

    private void addToLibrary(IncludeDef includeDef) {
        if (library.getIdentifier() == null || library.getIdentifier().getId() == null) {
            throw new IllegalArgumentException("Unnamed libraries cannot reference other libraries.");
        }

        if (library.getIncludes() == null) {
            library.setIncludes(of.createLibraryIncludes());
        }
        library.getIncludes().getDef().add(includeDef);

        translatedLibrary.add(includeDef);

        VersionedIdentifier libraryIdentifier = new VersionedIdentifier()
                .withId(includeDef.getPath())
                .withVersion(includeDef.getVersion());

        TranslatedLibrary referencedLibrary = LibraryManager.resolveLibrary(libraryIdentifier, errors);
        libraries.put(includeDef.getLocalIdentifier(), referencedLibrary);
    }

    private SystemModel getSystemModel() {
        return (SystemModel)getModel("System");
    }

    private void loadSystemLibrary() {
        TranslatedLibrary systemLibrary = SystemLibraryHelper.load(getSystemModel());
        libraries.put(systemLibrary.getIdentifier().getId(), systemLibrary);
    }

    private TranslatedLibrary resolveLibrary(String identifier) {
        return libraries.get(identifier);
    }

    private void addExpression(Expression expression) {
        expressions.add(expression);
    }

    private TrackBack getTrackBack(ParserRuleContext ctx) {
        TrackBack tb = new TrackBack(
                library.getIdentifier(),
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine() + 1, // 1-based instead of 0-based
                ctx.getStop().getLine(),
                ctx.getStop().getCharPositionInLine() + ctx.getStop().getText().length() // 1-based instead of 0-based
        );
        return tb;
    }

    private TrackBack track(Trackable trackable, ParserRuleContext ctx) {
        TrackBack tb = getTrackBack(ctx);

        trackable.getTrackbacks().add(tb);

        return tb;
    }
}
