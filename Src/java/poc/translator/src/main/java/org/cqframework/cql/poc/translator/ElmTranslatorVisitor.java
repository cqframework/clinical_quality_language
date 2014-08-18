package org.cqframework.cql.poc.translator;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.cqframework.cql.gen.cqlBaseVisitor;
import org.cqframework.cql.gen.cqlLexer;
import org.cqframework.cql.gen.cqlParser;
import org.hl7.elm.r1.Add;
import org.hl7.elm.r1.And;
import org.hl7.elm.r1.BinaryExpression;
import org.hl7.elm.r1.ClinicalRequest;
import org.hl7.elm.r1.Equal;
import org.hl7.elm.r1.Expression;
import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.ExpressionRef;
import org.hl7.elm.r1.FunctionDef;
import org.hl7.elm.r1.FunctionRef;
import org.hl7.elm.r1.IsNull;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.Literal;
import org.hl7.elm.r1.ModelReference;
import org.hl7.elm.r1.Multiply;
import org.hl7.elm.r1.Not;
import org.hl7.elm.r1.NotEqual;
import org.hl7.elm.r1.ObjectFactory;
import org.hl7.elm.r1.Or;
import org.hl7.elm.r1.ParameterDef;
import org.hl7.elm.r1.Power;
import org.hl7.elm.r1.Property;
import org.hl7.elm.r1.UnaryExpression;
import org.hl7.elm.r1.ValueSetDef;
import org.hl7.elm.r1.ValueSetRef;
import org.hl7.elm.r1.VersionedIdentifier;

import javax.xml.bind.JAXB;
import javax.xml.namespace.QName;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ElmTranslatorVisitor extends cqlBaseVisitor {
    private final ObjectFactory of = new ObjectFactory();

    private Library library = null;
    private String currentContext = "UNKNOWN";

    //Put them here for now, but eventually somewhere else?
    private final List<Expression> expressions = new ArrayList<>();
    private final List<ClinicalRequest> clinicalRequests = new ArrayList<>();

    public Library getLibrary() {
        return library;
    }


    @Override
    public Object visit(@NotNull ParseTree tree) {
        Object o = super.visit(tree);
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
        VersionedIdentifier vid = of.createVersionedIdentifier();
        vid.setId(parseString(ctx.IDENTIFIER()));
        vid.setVersion(parseString(ctx.STRING()));
        library.setIdentifier(vid);

        return vid;
    }

    @Override
    public ModelReference visitUsingDefinition(@NotNull cqlParser.UsingDefinitionContext ctx) {
        ModelReference.ReferencedModel ref = of.createModelReferenceReferencedModel();
        ref.setValue(parseString(ctx.IDENTIFIER()));
        ModelReference model = of.createModelReference();
        model.setReferencedModel(ref);
        addToLibrary(model);

        return model;
    }

    @Override
    public ParameterDef visitParameterDefinition(@NotNull cqlParser.ParameterDefinitionContext ctx) {
        ParameterDef param = of.createParameterDef();
        param.setName(parseString(ctx.IDENTIFIER()));
        // TODO: Support types
        param.setDefault(parseExpression(ctx.expression()));
        addToLibrary(param);

        return param;
    }

    @Override
    public ValueSetDef visitValuesetDefinitionByExpression(@NotNull cqlParser.ValuesetDefinitionByExpressionContext ctx) {
        ValueSetDef vs = of.createValueSetDef();
        vs.setName(parseString(ctx.VALUESET()));
        vs.setValueSet(parseExpression(ctx.expression()));
        addToLibrary(vs);

        return vs;
    }

    /*
     * TODO: The ELM model doesn't expect this form of valueset definition
     */
    @Override
    public ValueSetDef visitValuesetDefinitionByConstructor(@NotNull cqlParser.ValuesetDefinitionByConstructorContext ctx) {
        ValueSetDef vs = of.createValueSetDef();
        vs.setName(parseString(ctx.VALUESET()));
        Literal lit = of.createLiteral();
        lit.setValueType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
        lit.setValue(parseString(ctx.STRING()));
        vs.setValueSet(lit);
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
        ExpressionDef let = of.createExpressionDef();
        let.setName(parseString(ctx.IDENTIFIER()));
        let.setContext(currentContext);
        let.setExpression((Expression) visit(ctx.expression()));
        addToLibrary(let);

        return let;
    }

    @Override
    public Literal visitStringLiteral(@NotNull cqlParser.StringLiteralContext ctx) {
        Literal literal = of.createLiteral();
        literal.setValue(parseString(ctx.STRING()));
        literal.setValueType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
        return literal;
    }

    @Override
    public Literal visitBooleanLiteral(@NotNull cqlParser.BooleanLiteralContext ctx) {
        Literal literal = of.createLiteral();
        literal.setValue(parseString(ctx));
        literal.setValueType(new QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        return literal;
    }

    @Override
    public Literal visitNullLiteral(@NotNull cqlParser.NullLiteralContext ctx) {
        // TODO: Is this the right way?
        Literal literal = of.createLiteral();
        literal.setValue(null);
        return literal;
    }

    @Override
    public Literal visitQuantityLiteral(@NotNull cqlParser.QuantityLiteralContext ctx) {
        // TODO: How to do this in ELM?
        Literal literal = of.createLiteral();
        //return new QuantityLiteral(ctx.QUANTITY().getText(), unit);
        return literal;
    }

    @Override
    public UnaryExpression visitExistenceExpression(@NotNull cqlParser.ExistenceExpressionContext ctx) {
        UnaryExpression exp = "not".equals(parseString(ctx.getChild(0))) ? of.createIsEmpty() : of.createIsNotEmpty();
        exp.setOperand(parseExpression(ctx.expression()));

        return exp;
    }

    @Override
    public Multiply visitMultiplicationExpressionTerm(@NotNull cqlParser.MultiplicationExpressionTermContext ctx) {
        Multiply multiply = of.createMultiply();
        multiply.getOperand().add(parseExpression(ctx.expressionTerm(0)));
        multiply.getOperand().add(parseExpression(ctx.expressionTerm(1)));

        return multiply;
    }

    @Override
    public Power visitPowerExpressionTerm(@NotNull cqlParser.PowerExpressionTermContext ctx) {
        Power power = of.createPower();
        power.getOperand().add(parseExpression(ctx.expressionTerm(0)));
        power.getOperand().add(parseExpression(ctx.expressionTerm(1)));

        return power;
    }

    @Override
    public Add visitAdditionExpressionTerm(@NotNull cqlParser.AdditionExpressionTermContext ctx) {
        Add add = of.createAdd();
        add.getOperand().add(parseExpression(ctx.expressionTerm(0)));
        add.getOperand().add(parseExpression(ctx.expressionTerm(1)));

        return add;
    }

    @Override
    public Expression visitParenthesizedTerm(@NotNull cqlParser.ParenthesizedTermContext ctx) {
        return parseExpression(ctx.expression());
    }

    @Override
    public And visitAndExpression(@NotNull cqlParser.AndExpressionContext ctx) {
        And and = of.createAnd();
        and.getOperand().add(parseExpression(ctx.expression(0)));
        and.getOperand().add(parseExpression(ctx.expression(1)));

        return and;
    }

    @Override
    public Or visitOrExpression(@NotNull cqlParser.OrExpressionContext ctx) {
        // TODO: How to represent XOR in ELM?
        boolean xor = ctx.getChild(1).getText().equals("xor");
        Or or = of.createOr();
        or.getOperand().add(parseExpression(ctx.expression(0)));
        or.getOperand().add(parseExpression(ctx.expression(1)));

        return or;
    }

    @Override
    public BinaryExpression visitEqualityExpression(@NotNull cqlParser.EqualityExpressionContext ctx) {
        BinaryExpression exp = "=".equals(parseString(ctx.getChild(1))) ? of.createEqual() : of.createNotEqual();
        exp.getOperand().add(parseExpression(ctx.expression(0)));
        exp.getOperand().add(parseExpression(ctx.expression(1)));

        return exp;
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
        exp.getOperand().add(parseExpression(ctx.expression(0)));
        exp.getOperand().add(parseExpression(ctx.expression(1)));

        return exp;
    }

    @Override
    public ExpressionRef visitQualifiedIdentifier(@NotNull cqlParser.QualifiedIdentifierContext ctx) {
        // TODO: There isn't always an easy way to distinguish if identifier is:
        // - a reference to a variable (ExpressionRef)
        // - a reference to a function (FunctionRef)
        // - a reference to a model (ModelReference)
        // - a property on a model (Property)
        // Each would return something different!
        ExpressionRef exp = of.createExpressionRef();
        exp.setLibraryName(parseString(ctx.qualifier()));
        exp.setName(parseString(ctx.IDENTIFIER()));

        return exp;
    }


    @Override
    public Expression visitValueset(@NotNull cqlParser.ValuesetContext ctx) {
        Expression exp;
        if (ctx.VALUESET() != null) {
            ValueSetRef vs = of.createValueSetRef();
            // TODO: Support library name
            vs.setName(parseString(ctx.VALUESET()));
            exp = vs;
        } else {
            ExpressionRef vs = of.createExpressionRef();
            vs.setLibraryName(parseString(ctx.qualifier()));
            vs.setName(parseString(ctx.IDENTIFIER()));
            exp = vs;
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
            ValueSetRef vs = of.createValueSetRef();
            // TODO: Support library name
            vs.setName(parseString(ctx.VALUESET()));
            exp = vs;
        } else {
            ExpressionRef vs = of.createExpressionRef();
            vs.setLibraryName(parseString(ctx.expressionTerm()));
            vs.setName(parseString(ctx.IDENTIFIER()));
            exp = vs;
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
        ExpressionRef exp = of.createExpressionRef();
        exp.setName(parseString(ctx.IDENTIFIER()));

        return exp;
    }

    @Override
    public Property visitValuesetPathIdentifier(@NotNull cqlParser.ValuesetPathIdentifierContext ctx) {
        Property property = of.createProperty();
        property.setPath(parseString(ctx.IDENTIFIER()));

        return property;
    }

    @Override
    public Property visitDuringPathIdentifier(@NotNull cqlParser.DuringPathIdentifierContext ctx) {
        // TODO: Can ValuesetPathIdentifier and DuringPathIdentifier share the same grammar rule?
        Property property = of.createProperty();
        property.setPath(parseString(ctx.IDENTIFIER()));

        return property;
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
            IsNull isNull = of.createIsNull();
            isNull.setOperand(left);
            exp = isNull;
        } else {
            Literal right = of.createLiteral();
            right.setValue(lastChild);
            right.setValueType(new QName("http://www.w3.org/2001/XMLSchema", "boolean"));
            Equal eq = of.createEqual();
            eq.getOperand().add(left);
            eq.getOperand().add(right);
            exp = eq;
        }
        if ("not".equals(nextToLast)) {
            Not not = of.createNot();
            not.setOperand(exp);
            exp = not;
        }

        return exp;
    }

    @Override
    public ClinicalRequest visitRetrieve(@NotNull cqlParser.RetrieveContext ctx) {
        ClinicalRequest request = of.createClinicalRequest();
        String occ = ctx.occurrence() != null ? ctx.occurrence().getText() : "Occurrence";
        String subject = String.format("%s%s%s",
                parseString(ctx.topic()),
                parseString(ctx.modality()),
                occ);
        Literal subjectLiteral = of.createLiteral();
        subjectLiteral.setValue(subject);
        subjectLiteral.setValueType(new QName("http://www.w3.org/2001/XMLSchema", "string"));
        request.setSubject(subjectLiteral);
        request.setCodeProperty(parseString(ctx.valuesetPathIdentifier()));
        request.setCodes(parseExpression(ctx.valueset()));
        request.setDateProperty(parseString(ctx.duringPathIdentifier()));
        request.setDateRange(parseExpression(ctx.expression()));
        clinicalRequests.add(request);

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
        FunctionDef fun = of.createFunctionDef();
        fun.setName(parseString(ctx.IDENTIFIER()));
        if (ctx.operandDefinition() != null) {
            for (cqlParser.OperandDefinitionContext opdef : ctx.operandDefinition()) {
                ParameterDef param = of.createParameterDef();
                param.setName(parseString(opdef.IDENTIFIER()));
                // TODO: Support parameter type
                fun.getParameter().add(param);
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

        JAXB.marshal((new ObjectFactory()).createLibrary(visitor.getLibrary()), System.out);
    }
}
