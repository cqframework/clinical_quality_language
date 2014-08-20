package org.cqframework.cql.poc.translator;

import com.sun.javaws.exceptions.InvalidArgumentException;
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
import org.cqframework.cql.poc.translator.model.ClassDetail;
import org.cqframework.cql.poc.translator.model.ModelHelper;
import org.cqframework.cql.poc.translator.model.QuickModelHelper;
import org.hl7.elm.r1.*;
import org.hl7.elm_modelinfo.r1.ModelInfo;

import javax.xml.namespace.QName;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ElmTranslatorVisitor extends cqlBaseVisitor {
    private final ObjectFactory of = new ObjectFactory();

    private Library library = null;
    private String currentContext = "UNKNOWN";

    //Put them here for now, but eventually somewhere else?
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
    public ParameterDef visitParameterDefinition(@NotNull cqlParser.ParameterDefinitionContext ctx) {
        ParameterDef param = of.createParameterDef()
                .withName(parseString(ctx.IDENTIFIER()))
                // TODO: Support types
                .withDefault(parseExpression(ctx.expression()));
        addToLibrary(param);

        return param;
    }

    @Override
    public ValueSetDef visitValuesetDefinitionByExpression(@NotNull cqlParser.ValuesetDefinitionByExpressionContext ctx) {
        ValueSetDef vs = of.createValueSetDef()
                .withName(parseString(ctx.VALUESET()))
                .withValueSet(parseExpression(ctx.expression()));
        addToLibrary(vs);

        return vs;
    }

    /*
     * TODO: The ELM model doesn't expect this form of valueset definition
     */
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
    public ExpressionDef visitLetStatement(@NotNull cqlParser.LetStatementContext ctx) {
        ExpressionDef let = of.createExpressionDef()
                .withName(parseString(ctx.IDENTIFIER()))
                .withContext(currentContext)
                .withExpression((Expression) visit(ctx.expression()));
        addToLibrary(let);

        return let;
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
                        .withValue((BigDecimal)df.parse(ctx.QUANTITY().getText()));
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
                    .withValueType(new QName(quantity.contains(".") ? "xs:int" : "xs:decimal"));
        }
    }

    @Override
    public UnaryExpression visitExistenceExpression(@NotNull cqlParser.ExistenceExpressionContext ctx) {
        UnaryExpression exp = "not".equals(parseString(ctx.getChild(0))) ? of.createIsEmpty() : of.createIsNotEmpty();
        return exp.withOperand(parseExpression(ctx.expression()));
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
    public Add visitAdditionExpressionTerm(@NotNull cqlParser.AdditionExpressionTermContext ctx) {
        return of.createAdd().withOperand(
                parseExpression(ctx.expressionTerm(0)),
                parseExpression(ctx.expressionTerm(1)));
    }

    @Override
    public Expression visitParenthesizedTerm(@NotNull cqlParser.ParenthesizedTermContext ctx) {
        return parseExpression(ctx.expression());
    }

    @Override
    public And visitAndExpression(@NotNull cqlParser.AndExpressionContext ctx) {
        return of.createAnd().withOperand(
                parseExpression(ctx.expression(0)),
                parseExpression(ctx.expression(1)));
    }

    @Override
    public Or visitOrExpression(@NotNull cqlParser.OrExpressionContext ctx) {
        // TODO: How to represent XOR in ELM?
        // boolean xor = ctx.getChild(1).getText().equals("xor");
        return of.createOr().withOperand(
                parseExpression(ctx.expression(0)),
                parseExpression(ctx.expression(1)));
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
        // TODO: There isn't always an easy way to distinguish if identifier is:
        // - a reference to a variable (ExpressionRef)
        // - a reference to a function (FunctionRef)
        // - a reference to a model (ModelReference)
        // - a property on a model (Property)
        // Each would return something different!
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
        // TODO: This doesn't properly support when the LHS is an expression (not a library)
        // TODO: There isn't always an easy way to distinguish if identifier is:
        // - a reference to a variable (ExpressionRef)
        // - a reference to a function (FunctionRef)
        // - a reference to a model (ModelReference)
        // - a property on a model (Property)
        // - a property on a tuple (Property?)
        // Each would return something different!
        Expression exp;
        if (ctx.VALUESET() != null) {
            exp = of.createValueSetRef()
                    // TODO: Support library name
                    .withName(parseString(ctx.VALUESET()));
        } else {
            exp = of.createExpressionRef()
                    .withLibraryName(parseString(ctx.expressionTerm()))
                    .withName(parseString(ctx.IDENTIFIER()));
        }

        return exp;
    }

    @Override
    public ExpressionRef visitIdentifierTerm(@NotNull cqlParser.IdentifierTermContext ctx) {
        // TODO: There isn't always an easy way to distinguish if identifier is:
        // - a reference to a variable (ExpressionRef)
        // - a reference to a function (FunctionRef)
        // - a reference to a model (ModelReference)
        // - a property on a model (Property)
        // Each would return something different!
        return of.createExpressionRef().withName(parseString(ctx.IDENTIFIER()));

    }

    @Override
    public Property visitValuesetPathIdentifier(@NotNull cqlParser.ValuesetPathIdentifierContext ctx) {
        return of.createProperty().withPath(parseString(ctx.IDENTIFIER()));
    }

    @Override
    public Property visitDuringPathIdentifier(@NotNull cqlParser.DuringPathIdentifierContext ctx) {
        // TODO: Can ValuesetPathIdentifier and DuringPathIdentifier share the same grammar rule?
        return of.createProperty().withPath(parseString(ctx.IDENTIFIER()));
    }

    @Override
    public Object visitTermExpression(@NotNull cqlParser.TermExpressionContext ctx) {
        return visit(ctx.expressionTerm());
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
    public ClinicalRequest visitRetrieve(@NotNull cqlParser.RetrieveContext ctx) {
        String occ = ctx.occurrence() != null ? ctx.occurrence().getText() : "Occurrence"; // TODO: Default occurrence label by model?
        String topic = parseString(ctx.topic());
        String modality = ctx.modality() != null ? ctx.modality().getText() : "";
        ClassDetail detail = modelHelper.getClassDetail(occ, topic, modality);

        ClinicalRequest request = of.createClinicalRequest()
                .withDataType(new QName(
                        modelHelper.getModelInfo().getUrl(),
                        detail != null ? detail.getClassInfo().getName() : String.format("%s%s%s", topic, modality, occ),
                        modelHelper.getModelInfo().getTargetQualifier().toString()));

        if (ctx.valueset() != null) {
            if (ctx.valuesetPathIdentifier() != null) {
                request.setCodeProperty(parseString(ctx.valuesetPathIdentifier()));
            } else if (detail != null && detail.getClassInfo().getPrimaryCodeAttribute() != null) {
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

        clinicalRequests.add(request); // TODO: Why are we collecting these?

        return request;
    }
/*
    TODO: Don't invest much time here until we know if it's changing in ELM

    @Override
    public Object visitQueryInclusionClause(@NotNull cqlParser.QueryInclusionClauseContext ctx) {
        boolean negated = ctx.getChild(0).equals("without");
        Expression expression = (Expression) visit(ctx.expression());
        AliasedQuerySource aqs = (AliasedQuerySource) visit(ctx.aliasedQuerySource());
        return new QueryInclusionClauseExpression(aqs, expression, negated);
    }

    @Override
    public Object visitReturnClause(@NotNull cqlParser.ReturnClauseContext ctx) {
        return visit(ctx.expression());
    }


    @Override
    public Object visitWhereClause(@NotNull cqlParser.WhereClauseContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Object visitSortByItem(@NotNull cqlParser.SortByItemContext ctx) {
        SortClause.SortDirection direction = SortClause.SortDirection.valueOf(ctx.sortDirection().getText());
        // TODO: This changed in the grammar from QualifiedIdentifier to ExpressionTerm!
        QualifiedIdentifier exp = (QualifiedIdentifier) visit(ctx.expressionTerm());
        return new SortItem(direction, exp);
    }

    @Override
    public Object visitSortClause(@NotNull cqlParser.SortClauseContext ctx) {
        SortClause.SortDirection direction = ctx.sortDirection() != null ? SortClause.SortDirection.valueOf(ctx.sortDirection().getText()) : null;
        List<SortItem> sortItems = new ArrayList<>();
        if (ctx.sortByItem() != null) {
            for (cqlParser.SortByItemContext sortByItemContext : ctx.sortByItem()) {
                sortItems.add((SortItem) visit(sortByItemContext));
            }
        }
        return new SortClause(direction, sortItems);
    }
*/

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

/*
    @Override
    public Object visitAliasedQuerySource(@NotNull cqlParser.AliasedQuerySourceContext ctx) {
        return new AliasedQuerySource((Expression) visit(ctx.querySource()), ctx.alias().getText());
    }

    @Override
    public Object visitQuery(@NotNull cqlParser.QueryContext ctx) {
        AliasedQuerySource aqs = (AliasedQuerySource) visit(ctx.aliasedQuerySource());
        List<QueryInclusionClauseExpression> qicx = new ArrayList<>();
        if (ctx.queryInclusionClause() != null) {
            for (cqlParser.QueryInclusionClauseContext queryInclusionClauseContext : ctx.queryInclusionClause()) {
                qicx.add((QueryInclusionClauseExpression) visit(queryInclusionClauseContext));
            }
        }
        Expression where = ctx.whereClause() != null ? (Expression) visit(ctx.whereClause()) : null;
        Expression ret = ctx.returnClause() != null ? (Expression) visit(ctx.returnClause()) : null;
        SortClause sort = ctx.sortClause() != null ? (SortClause) visit(ctx.sortClause()) : null;

        return new QueryExpression(aqs, qicx, where, ret, sort);
    }
*/

    @Override
    public Object visitMethodExpressionTerm(@NotNull cqlParser.MethodExpressionTermContext ctx) {
        FunctionRef fun = of.createFunctionRef();
        if (ctx.expressionTerm() instanceof cqlParser.AccessorExpressionTermContext) {
            ExpressionRef expRef = (ExpressionRef) visit(ctx.expressionTerm());
            fun.setLibraryName(expRef.getLibraryName());
            fun.setName(expRef.getName());
        } else {
            fun.setName(parseString(ctx.expressionTerm()));
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
                // TODO: Support parameter type
                fun.getParameter().add(of.createParameterDef().withName(parseString(opdef.IDENTIFIER())));
            }
        }
        fun.setExpression(parseExpression(ctx.functionBody()));
        fun.setContext(currentContext);
        addToLibrary(fun);

        return fun;
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

    private Literal createLiteral(String val, String type) {
        return of.createLiteral().withValue(val).withValueType(new QName("http://www.w3.org/2001/XMLSchema", type));
    }

    private Literal createLiteral(String bool) {
        return createLiteral(String.valueOf(bool), "string");
    }

    private Literal createLiteral(Boolean bool) {
        return createLiteral(String.valueOf(bool), "boolean");
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

    private void addToLibrary(ParameterDef paramDef) {
        if (library.getParameters() == null) {
            library.setParameters(of.createLibraryParameters());
        }
        library.getParameters().getDef().add(paramDef);
    }

    private TrackBack track(Trackable trackable, ParserRuleContext ctx) {
        TrackBack tb = new TrackBack(
                library.getIdentifier(),
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine() + 1, // 1-based instead of 0-based
                ctx.getStop().getLine(),
                ctx.getStop().getCharPositionInLine() + ctx.getStop().getText().length() // 1-based instead of 0-based
        );

        trackable.getTrackbacks().add(tb);

        return tb;
    }

    public static void main(String[] args) throws IOException {
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

        System.out.println(visitor.getLibrary().toString());
        //JAXB.marshal((new ObjectFactory()).createLibrary(visitor.getLibrary()), System.out);
    }
}
