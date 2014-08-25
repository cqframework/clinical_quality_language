package org.cqframework.cql.poc.translator;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.cqframework.cql.elm.tracking.TrackBack;
import org.cqframework.cql.elm.tracking.Trackable;
import org.cqframework.cql.gen.cqlBaseVisitor;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;
import org.cqframework.cql.poc.translator.model.*;
import org.hl7.elm.r1.*;
import org.hl7.elm_modelinfo.r1.ModelInfo;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
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

public class ElmTranslatorVisitor extends cqlBaseVisitor {
    private final ObjectFactory of = new ObjectFactory();

    private Library library = null;
    private String currentContext = "UNKNOWN";

    //Put them here for now, but eventually somewhere else?
    private final HashMap<String, Library> libraries = new HashMap<>();
    private final List<ClinicalRequest> clinicalRequests = new ArrayList<>();
    private final List<Expression> expressions = new ArrayList<>();
    private ModelHelper modelHelper = null;

    public Library getLibrary() {
        return library;
    }

    public List<ClinicalRequest> getClinicalRequests() {
        return clinicalRequests;
    }

    public List<Expression> getExpressions() {
        return expressions;
    }

    @Override
    public Object visit(@NotNull ParseTree tree) {
        Object o = super.visit(tree);
        if (o instanceof Trackable && tree instanceof ParserRuleContext && !(tree instanceof cqlParser.LogicContext)) {
            this.track((Trackable) o, (ParserRuleContext) tree);
        }
        if (o instanceof Expression) {
            expressions.add((Expression) o);
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
        String modelIdentifier = parseString(ctx.IDENTIFIER());
        // TODO: This should load from a modelinfo file based on the modelIdentifier above. Hard-coding to QUICK for POC purposes.
        try {
            modelHelper = new ModelHelper(QuickModelHelper.load());
        }
        catch (ClassNotFoundException e) {
            // TODO: Should never occur...
        }

        // TODO: Needs to write xmlns and schemalocation to the resulting ELM XML document...

        ModelReference model = of.createModelReference()
                .withReferencedModel(of.createModelReferenceReferencedModel().withValue(modelHelper.getModelInfo().getUrl()));
        addToLibrary(model);

        return model;
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
    public Object visitStatement(@NotNull cqlParser.StatementContext ctx) {
        // TODO:
        return super.visitStatement(ctx);
    }

    @Override
    public ExpressionDef visitLetStatement(@NotNull cqlParser.LetStatementContext ctx) {
        ExpressionDef let = of.createExpressionDef()
                .withName(parseString(ctx.IDENTIFIER()))
                .withContext(currentContext)
                .withExpression((Expression) visit(ctx.expression()));
        addToLibrary(let);

        return let;
    }

    @Override
    public Object visitLiteral(@NotNull cqlParser.LiteralContext ctx) {
        // TODO:
        return super.visitLiteral(ctx);
    }

    @Override
    public Object visitLiteralTerm(@NotNull cqlParser.LiteralTermContext ctx) {
        // TODO:
        return super.visitLiteralTerm(ctx);
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
        // TODO:
        return super.visitIntervalSelector(ctx);
    }

    @Override
    public Object visitIntervalSelectorTerm(@NotNull cqlParser.IntervalSelectorTermContext ctx) {
        // TODO:
        return super.visitIntervalSelectorTerm(ctx);
    }

    @Override
    public Object visitTupleSelectorTerm(@NotNull cqlParser.TupleSelectorTermContext ctx) {
        // TODO:
        return super.visitTupleSelectorTerm(ctx);
    }

    @Override
    public Object visitTupleElementSelector(@NotNull cqlParser.TupleElementSelectorContext ctx) {
        // TODO:
        return super.visitTupleElementSelector(ctx);
    }

    @Override
    public Object visitTupleSelector(@NotNull cqlParser.TupleSelectorContext ctx) {
        // TODO:
        return super.visitTupleSelector(ctx);
    }

    @Override
    public Object visitListSelectorTerm(@NotNull cqlParser.ListSelectorTermContext ctx) {
        // TODO:
        return super.visitListSelectorTerm(ctx);
    }

    @Override
    public Object visitListSelector(@NotNull cqlParser.ListSelectorContext ctx) {
        // TODO:
        return super.visitListSelector(ctx);
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
                    .withValueType(resolveNamedType(quantity.contains(".") ? "Integer" : "Decimal"));
        }
    }

    @Override
    public Object visitValuesetLiteral(@NotNull cqlParser.ValuesetLiteralContext ctx) {
        // TODO:
        return super.visitValuesetLiteral(ctx);
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
    public Multiply visitMultiplicationExpressionTerm(@NotNull cqlParser.MultiplicationExpressionTermContext ctx) {
        return of.createMultiply().withOperand(
                parseExpression(ctx.expressionTerm(0)),
                parseExpression(ctx.expressionTerm(1)));
    }

    @Override
    public Power visitPowerExpressionTerm(@NotNull cqlParser.PowerExpressionTermContext ctx) {
        return of.createPower().withOperand(
                parseExpression(ctx.expressionTerm(0)),
                parseExpression(ctx.expressionTerm(1)));
    }

    @Override
    public Object visitPolarityExpressionTerm(@NotNull cqlParser.PolarityExpressionTermContext ctx) {
        if (ctx.getChild(0).getText() == "+") {
            return visit(ctx.expressionTerm());
        }

        return of.createNegate().withOperand(parseExpression(ctx.expressionTerm()));
    }

    @Override
    public Add visitAdditionExpressionTerm(@NotNull cqlParser.AdditionExpressionTermContext ctx) {
        return of.createAdd().withOperand(
                parseExpression(ctx.expressionTerm(0)),
                parseExpression(ctx.expressionTerm(1)));
    }

    @Override
    public Object visitPredecessorExpressionTerm(@NotNull cqlParser.PredecessorExpressionTermContext ctx) {
        // TODO:
        return super.visitPredecessorExpressionTerm(ctx);
    }

    @Override
    public Object visitSuccessorExpressionTerm(@NotNull cqlParser.SuccessorExpressionTermContext ctx) {
        // TODO:
        return super.visitSuccessorExpressionTerm(ctx);
    }

    @Override
    public Object visitDateTimeComponent(@NotNull cqlParser.DateTimeComponentContext ctx) {
        // TODO:
        return super.visitDateTimeComponent(ctx);
    }

    @Override
    public Object visitPluralDateTimePrecision(@NotNull cqlParser.PluralDateTimePrecisionContext ctx) {
        // TODO:
        return super.visitPluralDateTimePrecision(ctx);
    }

    @Override
    public Object visitDateTimePrecision(@NotNull cqlParser.DateTimePrecisionContext ctx) {
        // TODO:
        return super.visitDateTimePrecision(ctx);
    }

    @Override
    public Object visitTimeBoundaryExpressionTerm(@NotNull cqlParser.TimeBoundaryExpressionTermContext ctx) {
        // TODO:
        return super.visitTimeBoundaryExpressionTerm(ctx);
    }

    @Override
    public Object visitTimeUnitExpressionTerm(@NotNull cqlParser.TimeUnitExpressionTermContext ctx) {
        // TODO:
        return super.visitTimeUnitExpressionTerm(ctx);
    }

    @Override
    public Object visitTimingExpression(@NotNull cqlParser.TimingExpressionContext ctx) {
        // TODO:
        return super.visitTimingExpression(ctx);
    }

    @Override
    public Object visitDurationExpressionTerm(@NotNull cqlParser.DurationExpressionTermContext ctx) {
        // TODO:
        return super.visitDurationExpressionTerm(ctx);
    }

    @Override
    public Object visitRangeExpression(@NotNull cqlParser.RangeExpressionContext ctx) {
        // TODO:
        return super.visitRangeExpression(ctx);
    }

    @Override
    public Object visitTimeRangeExpression(@NotNull cqlParser.TimeRangeExpressionContext ctx) {
        // TODO:
        return super.visitTimeRangeExpression(ctx);
    }

    @Override
    public Object visitWidthExpressionTerm(@NotNull cqlParser.WidthExpressionTermContext ctx) {
        // TODO:
        return super.visitWidthExpressionTerm(ctx);
    }

    @Override
    public Object visitTermExpressionTerm(@NotNull cqlParser.TermExpressionTermContext ctx) {
        // TODO:
        return super.visitTermExpressionTerm(ctx);
    }

    @Override
    public Expression visitParenthesizedTerm(@NotNull cqlParser.ParenthesizedTermContext ctx) {
        return parseExpression(ctx.expression());
    }

    @Override
    public Object visitMembershipExpression(@NotNull cqlParser.MembershipExpressionContext ctx) {
        // TODO:
        return super.visitMembershipExpression(ctx);
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
        // TODO:
        return super.visitInFixSetExpression(ctx);
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
    public ExpressionRef visitQualifiedIdentifier(@NotNull cqlParser.QualifiedIdentifierContext ctx) {
        // QualifiedIdentifier can only appear as a query source, so it can only be either an
        // ExpressionRef, or a library qualified ExpressionRef.
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
        // TODO: Handle AliasRef as part of Query support
        // if left is an AliasRef
            // return a Property with a Path and no source
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

        if (left instanceof ExpressionRef) {
            if (ctx.IDENTIFIER() != null) {
                return of.createProperty()
                        .withSource(left)
                        .withPath(ctx.IDENTIFIER().getText());
            }
        }

        if (left instanceof Property) {
            if (ctx.IDENTIFIER() != null) {
                Property property = (Property)left;
                property.setPath(String.format("%s.%s", property.getPath(), ctx.IDENTIFIER().getText()));
                return property;
            }
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
            // 4: An unresolved identifier that must be resolved later (by a method or accessor)

        String identifier = ctx.IDENTIFIER().getText();

        String libraryName = resolveLibraryName(identifier);
        if (libraryName != null) {
            LibraryRef libraryRef = new LibraryRef();
            libraryRef.setLibraryName(libraryName);

            return libraryRef;
        }

        String parameterName = resolveParameterName(library, identifier);
        if (parameterName != null) {
            return of.createParameterRef().withName(parameterName);
        }

        String expressionName = resolveExpressionName(library, identifier);
        if (expressionName != null) {
            return of.createExpressionRef().withName(expressionName);
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
        if (ctx.getChild(1).getText() == "is") {
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
    public Object visitQuantityOffset(@NotNull cqlParser.QuantityOffsetContext ctx) {
        // TODO:
        return super.visitQuantityOffset(ctx);
    }

    @Override
    public Object visitRelativeQualifier(@NotNull cqlParser.RelativeQualifierContext ctx) {
        // TODO:
        return super.visitRelativeQualifier(ctx);
    }

    @Override
    public Object visitWithinIntervalOperatorPhrase(@NotNull cqlParser.WithinIntervalOperatorPhraseContext ctx) {
        // TODO:
        return super.visitWithinIntervalOperatorPhrase(ctx);
    }

    @Override
    public Object visitBeforeOrAfterIntervalOperatorPhrase(@NotNull cqlParser.BeforeOrAfterIntervalOperatorPhraseContext ctx) {
        // TODO:
        return super.visitBeforeOrAfterIntervalOperatorPhrase(ctx);
    }

    @Override
    public Object visitOverlapsIntervalOperatorPhrase(@NotNull cqlParser.OverlapsIntervalOperatorPhraseContext ctx) {
        // TODO:
        return super.visitOverlapsIntervalOperatorPhrase(ctx);
    }

    @Override
    public Object visitStartsIntervalOperatorPhrase(@NotNull cqlParser.StartsIntervalOperatorPhraseContext ctx) {
        // TODO:
        return super.visitStartsIntervalOperatorPhrase(ctx);
    }

    @Override
    public Object visitConcurrentWithIntervalOperatorPhrase(@NotNull cqlParser.ConcurrentWithIntervalOperatorPhraseContext ctx) {
        // TODO:
        return super.visitConcurrentWithIntervalOperatorPhrase(ctx);
    }

    @Override
    public Object visitMeetsIntervalOperatorPhrase(@NotNull cqlParser.MeetsIntervalOperatorPhraseContext ctx) {
        // TODO:
        return super.visitMeetsIntervalOperatorPhrase(ctx);
    }

    @Override
    public Object visitStartedByIntervalOperatorPhrase(@NotNull cqlParser.StartedByIntervalOperatorPhraseContext ctx) {
        // TODO:
        return super.visitStartedByIntervalOperatorPhrase(ctx);
    }

    @Override
    public Object visitEndedByIntervalOperatorPhrase(@NotNull cqlParser.EndedByIntervalOperatorPhraseContext ctx) {
        // TODO:
        return super.visitEndedByIntervalOperatorPhrase(ctx);
    }

    @Override
    public Object visitIncludesIntervalOperatorPhrase(@NotNull cqlParser.IncludesIntervalOperatorPhraseContext ctx) {
        // TODO:
        return super.visitIncludesIntervalOperatorPhrase(ctx);
    }

    @Override
    public Object visitEndsIntervalOperatorPhrase(@NotNull cqlParser.EndsIntervalOperatorPhraseContext ctx) {
        // TODO:
        return super.visitEndsIntervalOperatorPhrase(ctx);
    }

    @Override
    public Object visitIncludedInIntervalOperatorPhrase(@NotNull cqlParser.IncludedInIntervalOperatorPhraseContext ctx) {
        // TODO:
        return super.visitIncludedInIntervalOperatorPhrase(ctx);
    }

    @Override
    public Object visitIfThenElseExpressionTerm(@NotNull cqlParser.IfThenElseExpressionTermContext ctx) {
        // TODO:
        return super.visitIfThenElseExpressionTerm(ctx);
    }

    @Override
    public Object visitCaseExpressionTerm(@NotNull cqlParser.CaseExpressionTermContext ctx) {
        // TODO:
        return super.visitCaseExpressionTerm(ctx);
    }

    @Override
    public Object visitCaseExpressionItem(@NotNull cqlParser.CaseExpressionItemContext ctx) {
        // TODO:
        return super.visitCaseExpressionItem(ctx);
    }

    @Override
    public Object visitCoalesceExpressionTerm(@NotNull cqlParser.CoalesceExpressionTermContext ctx) {
        // TODO:
        return super.visitCoalesceExpressionTerm(ctx);
    }

    @Override
    public Object visitAggregateExpressionTerm(@NotNull cqlParser.AggregateExpressionTermContext ctx) {
        // TODO:
        return super.visitAggregateExpressionTerm(ctx);
    }

    @Override
    public ClinicalRequest visitRetrieve(@NotNull cqlParser.RetrieveContext ctx) {
        String occ = ctx.occurrence() != null ? ctx.occurrence().getText() : "Occurrence"; // TODO: Default occurrence label by model?
        String topic = parseString(ctx.topic());
        String modality = ctx.modality() != null ? ctx.modality().getText() : "";
        ClassDetail detail = modelHelper.getClassDetail(occ, topic, modality);

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
        AliasedQuerySource aqs = (AliasedQuerySource) visit(ctx.aliasedQuerySource());
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

    @Override
    public Object visitAliasedQuerySource(@NotNull cqlParser.AliasedQuerySourceContext ctx) {
        return of.createAliasedQuerySource().withExpression(parseExpression(ctx.querySource()))
                .withAlias(ctx.alias().getText());
    }

    @Override
    public Object visitQueryInclusionClause(@NotNull cqlParser.QueryInclusionClauseContext ctx) {
        boolean negated = ctx.getChild(0).equals("without");
        AliasedQuerySource aqs = (AliasedQuerySource) visit(ctx.aliasedQuerySource());
        Expression expression = (Expression) visit(ctx.expression());
        RelationshipClause result = negated ? of.createWithout() : of.createWith();
        return result.withExpression(aqs.getExpression()).withAlias(aqs.getAlias()).withWhere(expression);
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
        if (ctx.getText() == "desc") {
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
        // TODO:
        return super.visitIndexedExpressionTerm(ctx);
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
    public Object visitOperandDefinition(@NotNull cqlParser.OperandDefinitionContext ctx) {
        // TODO:
        return super.visitOperandDefinition(ctx);
    }

    @Override
    public Object visitFunctionBody(@NotNull cqlParser.FunctionBodyContext ctx) {
        // TODO:
        return super.visitFunctionBody(ctx);
    }

    @Override
    public Object visitReturnStatement(@NotNull cqlParser.ReturnStatementContext ctx) {
        // TODO:
        return super.visitReturnStatement(ctx);
    }

    @Override
    public Object visitFunctionDefinition(@NotNull cqlParser.FunctionDefinitionContext ctx) {
        FunctionDef fun = of.createFunctionDef().withName(parseString(ctx.IDENTIFIER()));
        if (ctx.operandDefinition() != null) {
            for (cqlParser.OperandDefinitionContext opdef : ctx.operandDefinition()) {
                // TODO: Support parameter type
                fun.getParameter().add(of.createParameterDef().withName(parseString(opdef.IDENTIFIER())));
            }
        }
        fun.setExpression(parseExpression(ctx.functionBody()));
        fun.setContext(currentContext);
        addToLibrary(fun);

        return fun;
    }

    @Override
    public Object visitValuesetIdentifier(@NotNull cqlParser.ValuesetIdentifierContext ctx) {
        // TODO:
        return super.visitValuesetIdentifier(ctx);
    }

    @Override
    public Object visitDuringIdentifier(@NotNull cqlParser.DuringIdentifierContext ctx) {
        // TODO:
        return super.visitDuringIdentifier(ctx);
    }

    @Override
    public Object visitRetrieveDefinition(@NotNull cqlParser.RetrieveDefinitionContext ctx) {
        // TODO:
        return super.visitRetrieveDefinition(ctx);
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
        if (typeName == "Boolean") return "bool";
        else if (typeName == "Integer") return "int";
        else if (typeName == "Decimal") return "decimal";
        else if (typeName == "String") return "string";
        else if (typeName == "DateTime") return "datetime";
        else return typeName;
    }

    private QName resolveAxisType(String occurrence, String topic, String modality) {
        ClassDetail detail = modelHelper.getClassDetail(occurrence, topic, modality);

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
        // TODO: Should attempt resolution in all models and throw if ambiguous
        // Library qualifier should be required to resolve ambiguity
        // Requires CQL change to allow qualifiers in atomicTypeSpecifier (which should really be called namedTypeSpecifier)
        String className = modelHelper.resolveClassName(typeName);

        if (className != null) {
            return new QName(modelHelper.getModelInfo().getUrl(), className);
        }

        return new QName("http://www.w3.org/2001/XMLSchema", resolveSystemNamedType(typeName));
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

    private String resolveExpressionName(Library library, String identifier) {
        if (library.getStatements() != null) {
            for (ExpressionDef current : library.getStatements().getDef()) {
                if (!(current instanceof FunctionDef) && !(current instanceof ClinicalRequestDef)
                        && (current.getName() == identifier)) {
                    return identifier;
                }
            }
        }

        return null;
    }

    private String resolveFunctionName(Library library, String identifier) {
        if (library.getStatements() != null) {
            for (ExpressionDef current : library.getStatements().getDef()) {
                if (current instanceof FunctionDef && current.getName() == identifier) {
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

    private String resolveParameterName(Library library, String identifier) {
        if (library.getParameters() != null) {
            for (ParameterDef current : library.getParameters().getDef()) {
                if (current.getName() == identifier) {
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
                if (current.getName() == identifier) {
                    return identifier;
                }
            }
        }

        return null;
    }

    private Library resolveLibrary(String identifier) {
        return libraries.getOrDefault(identifier, null);
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

        ElmTranslatorVisitor visitor = new ElmTranslatorVisitor();
        visitor.visit(tree);

        /* ToString output
        System.out.println(visitor.getLibrary().toString());
        */

        /* XML output
        JAXB.marshal((new ObjectFactory()).createLibrary(visitor.getLibrary()), System.out);
        */

        /* JSON output */
        JAXBContext jc = JAXBContext.newInstance(Library.class);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty("eclipselink.media-type", "application/json");
        marshaller.marshal(new ObjectFactory().createLibrary(visitor.getLibrary()), System.out);
    }
}
