package org.cqframework.cql.poc.translator;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.Interval;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.cqframework.cql.gen.cqlBaseVisitor;
import org.cqframework.cql.gen.cqlParser;
import org.cqframework.cql.poc.translator.expressions.*;
import org.cqframework.cql.poc.translator.model.CqlLibrary;
import org.cqframework.cql.poc.translator.model.SourceDataCriteria;
import org.cqframework.cql.poc.translator.model.ValueSet;
import org.cqframework.cql.poc.translator.model.logger.TrackBack;
import org.cqframework.cql.poc.translator.model.logger.Trackable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bobd on 7/24/14.
 */
public class CqlTranslatorVisitor extends cqlBaseVisitor {

    private final CqlLibrary library = new CqlLibrary();

    public CqlLibrary getLibrary() {
        return library;
    }


    @Override
    public Object visit(@NotNull ParseTree tree) {
        Object o = super.visit(tree);
        if (o instanceof Trackable && tree instanceof ParserRuleContext) {
            this.track((Trackable) o, (ParserRuleContext) tree);
        }
        if (o instanceof Expression) {
            library.addExpression((Expression) o);
        }

        return o;
    }

    @Override
    public Object visitLibraryDefinition(@NotNull cqlParser.LibraryDefinitionContext ctx) {
        library.setLibrary(nullOrString(ctx.IDENTIFIER()));
        library.setVersion(nullOrString(ctx.STRING()));

        return library;
    }

    @Override
    public Object visitValuesetDefinitionByConstructor(@NotNull cqlParser.ValuesetDefinitionByConstructorContext ctx) {
        ValueSet vs = new ValueSet(ctx.STRING().getText(), ctx.VALUESET().getText());
        track(vs, ctx);
        library.addValueSet(vs);

        return vs;
    }

    @Override
    public Object visitLetStatement(@NotNull cqlParser.LetStatementContext ctx) {
        LetStatement let = new LetStatement(ctx.IDENTIFIER().getText(), (Expression) visit(ctx.expression()));
        getLibrary().addLetStatement(let);
        return let;
    }

    @Override
    public Object visitStringLiteral(@NotNull cqlParser.StringLiteralContext ctx) {
        String str = ctx.STRING().getText();
        str = str.substring(1, str.length() - 1);
        return new StringLiteral(str);
    }

    @Override
    public Object visitBooleanLiteral(@NotNull cqlParser.BooleanLiteralContext ctx) {
        return new BooleanLiteral(ctx.getText());
    }

    @Override
    public Object visitNullLiteral(@NotNull cqlParser.NullLiteralContext ctx) {
        return new NullLiteral();
    }

    @Override
    public Object visitLiteral(@NotNull cqlParser.LiteralContext ctx) {
        return super.visitLiteral(ctx);
    }

    @Override
    public Object visitQuantityLiteral(@NotNull cqlParser.QuantityLiteralContext ctx) {
        String unit = (ctx.unit() == null) ? null : ctx.unit().getText();
        if (ctx.unit() != null && ctx.unit().STRING() != null) {
            unit = unit.substring(2, unit.length() - 1);
        }
        return new QuantityLiteral(ctx.QUANTITY().getText(), unit);
    }

    @Override
    public Object visitExistenceExpression(@NotNull cqlParser.ExistenceExpressionContext ctx) {
        Expression ext = (Expression) this.visit(ctx.expression());
        return new ExistenceExpression(ctx.children.get(0).getText().equals("not"), ext);
    }

    @Override
    public Object visitMultiplicationExpressionTerm(@NotNull cqlParser.MultiplicationExpressionTermContext ctx) {
        return new ArithmaticExpression((Expression) this.visit(ctx.expressionTerm(0)),
                ArithmaticExpression.Operator.bySymbol(ctx.getChild(1).getText()),
                (Expression) this.visit(ctx.expressionTerm(1)));
    }

    @Override
    public Object visitPowerExpressionTerm(@NotNull cqlParser.PowerExpressionTermContext ctx) {
        return new ArithmaticExpression((Expression) this.visit(ctx.expressionTerm(0)),
                ArithmaticExpression.Operator.POW,
                (Expression) this.visit(ctx.expressionTerm(1)));
    }

    @Override
    public Object visitAdditionExpressionTerm(@NotNull cqlParser.AdditionExpressionTermContext ctx) {
        return new ArithmaticExpression((Expression) this.visit(ctx.expressionTerm(0)),
                ArithmaticExpression.Operator.bySymbol(ctx.getChild(1).getText()),
                (Expression) this.visit(ctx.expressionTerm(1)));
    }

    @Override
    public Object visitParenthesizedTerm(@NotNull cqlParser.ParenthesizedTermContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Object visitAndExpression(@NotNull cqlParser.AndExpressionContext ctx) {
        return new AndExpression((Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitOrExpression(@NotNull cqlParser.OrExpressionContext ctx) {
        boolean xor = ctx.getChild(1).getText().equals("xor");
        return new OrExpression((Expression) visit(ctx.expression(0)), (Expression) visit(ctx.expression(1)), xor);
    }

    @Override
    public Object visitEqualityExpression(@NotNull cqlParser.EqualityExpressionContext ctx) {
        ComparisonExpression.Comparator comp = ComparisonExpression.Comparator.bySymbol(ctx.getChild(1).getText());
        return new ComparisonExpression((Expression) visit(ctx.expression(0)), comp, (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitInequalityExpression(@NotNull cqlParser.InequalityExpressionContext ctx) {
        ComparisonExpression.Comparator comp = ComparisonExpression.Comparator.bySymbol(ctx.getChild(1).getText());
        return new ComparisonExpression((Expression) visit(ctx.expression(0)), comp, (Expression) visit(ctx.expression(1)));
    }

    @Override
    public Object visitQualifiedIdentifier(@NotNull cqlParser.QualifiedIdentifierContext ctx) {
        String qualifier = (ctx.qualifier() != null) ? ctx.qualifier().getText() : null;
        return new QualifiedIdentifier(qualifier, ctx.IDENTIFIER().getText(), false);
    }

    @Override
    public Object visitValueset(@NotNull cqlParser.ValuesetContext ctx) {
        String qualifier = (ctx.qualifier() != null) ? ctx.qualifier().getText() : null;
        boolean isValuesetIdentifier = ctx.VALUESET() != null;

        String ident = isValuesetIdentifier ? ctx.VALUESET().getText() : ctx.IDENTIFIER().getText();
        if (ident != null && isValuesetIdentifier) {
            ident = ident.substring(1, ident.length() - 1);
        }
        return new QualifiedIdentifier(qualifier, ident, isValuesetIdentifier);
    }

    @Override
    public Object visitAccessorExpressionTerm(@NotNull cqlParser.AccessorExpressionTermContext ctx) {
        boolean isValueset = ctx.VALUESET() != null;
        String accessor = isValueset ? ctx.VALUESET().getText() : ctx.IDENTIFIER().getText();
        if (accessor != null && isValueset) {
            accessor = accessor.substring(1, accessor.length() - 1);
        }
        return new AccessorExpression((Expression) visit(ctx.expressionTerm()), accessor, (ctx.VALUESET() != null));
    }

    @Override
    public Object visitIdentifierTerm(@NotNull cqlParser.IdentifierTermContext ctx) {
        return new IdentifierExpression(ctx.getText());
    }

    @Override
    public Object visitValuesetPathIdentifier(@NotNull cqlParser.ValuesetPathIdentifierContext ctx) {
        return new IdentifierExpression(ctx.IDENTIFIER().getText());
    }

    @Override
    public Object visitDuringPathIdentifier(@NotNull cqlParser.DuringPathIdentifierContext ctx) {
        return new IdentifierExpression(ctx.IDENTIFIER().getText());
    }

    @Override
    public Object visitTermExpression(@NotNull cqlParser.TermExpressionContext ctx) {
        return visit(ctx.expressionTerm());
    }

    @Override
    public Object visitBooleanExpression(@NotNull cqlParser.BooleanExpressionContext ctx) {
        Expression left = (Expression) visit(ctx.expression());
        Expression right = null;
        ComparisonExpression.Comparator comp = null;
        String lastChild = ctx.getChild(ctx.getChildCount() - 1).getText();
        String nextToLast = ctx.getChild(ctx.getChildCount() - 2).getText();
        visit(ctx.getChild(ctx.getChildCount() - 1));
        if (lastChild.equals("null")) {
            right = new NullLiteral();
        } else {
            right = new BooleanLiteral(lastChild);
        }
        TrackBack tback = new TrackBack(
                (library == null) ? "unknown" : library.getLibrary(),
                library.getVersion(),
                ctx.getStop().getLine(),
                ctx.getStop().getCharPositionInLine() + ctx.getStop().getText().length() - 5,
                ctx.getStop().getLine(),
                ctx.getStop().getCharPositionInLine() + ctx.getStop().getText().length() - 1
        );
        right.addTrackBack(tback);
        comp = (nextToLast.equals("not")) ? ComparisonExpression.Comparator.bySymbol("<>") :
                ComparisonExpression.Comparator.bySymbol("=");

        return new ComparisonExpression(left, comp, right, true);
    }

    @Override
    public Object visitModality(@NotNull cqlParser.ModalityContext ctx) {
        return new IdentifierExpression(ctx.IDENTIFIER().getText());
    }


    @Override
    public Object visitRetrieve(@NotNull cqlParser.RetrieveContext ctx) {
        SourceDataCriteria dataCriteria = new SourceDataCriteria(
                extractExistence(ctx.existenceModifier()),
                nullOrQualifiedIdentifierExpression(ctx.topic()),
                nullOrIdentifierExpression(ctx.modality()),
                nullOrIdentifierExpression(ctx.valuesetPathIdentifier()),
                nullOrQualifiedIdentifierExpression(ctx.valueset())
        );
        track(dataCriteria, ctx);
        library.addSourceDataCriteria(dataCriteria);

        IdentifierExpression duringPathIdentifier = nullOrIdentifierExpression(ctx.duringPathIdentifier());
        Expression duringExpression = nullOrExpression(ctx.expression());

        return new RetrieveExpression(dataCriteria, duringPathIdentifier, duringExpression);
    }

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
        QualifiedIdentifier exp = (QualifiedIdentifier) visit(ctx.qualifiedIdentifier());
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


    @Override
    public Object visitMethodExpressionTerm(@NotNull cqlParser.MethodExpressionTermContext ctx) {
        Expression identifier = (Expression) visit(ctx.expressionTerm());
        List<Expression> parameters = new ArrayList<>();
        if (ctx.expression() != null) {
            for (cqlParser.ExpressionContext expressionContext : ctx.expression()) {
                parameters.add((Expression) visit(expressionContext));
            }
        }
        return new MethodExpression(identifier, parameters);
    }

    @Override
    public Object visitFunctionDefinition(@NotNull cqlParser.FunctionDefinitionContext ctx) {
        Expression _resturn = (Expression) visit(ctx.functionBody().returnStatement());
        String ident = ctx.IDENTIFIER().getText();
        List<OperandDefinition> operands = new ArrayList<>();
        if (ctx.operandDefinition() != null) {
            for (cqlParser.OperandDefinitionContext opdef : ctx.operandDefinition()) {
                operands.add(new OperandDefinition(opdef.IDENTIFIER().getText(), opdef.typeSpecifier().getText()));
            }
        }

        return new FunctionDef(ident, operands, _resturn);
    }

    private String nullOrString(ParseTree pt) {
        return pt == null ? null : pt.getText();
    }

    private IdentifierExpression nullOrIdentifierExpression(ParseTree pt) {
        return pt == null ? null : (IdentifierExpression) visit(pt);
    }

    private QualifiedIdentifier nullOrQualifiedIdentifierExpression(ParseTree pt) {
        return pt == null ? null : (QualifiedIdentifier) visit(pt);
    }

    private Expression nullOrExpression(ParseTree pt) {
        return pt == null ? null : (Expression) visit(pt);
    }

    private SourceDataCriteria.Existence extractExistence(cqlParser.ExistenceModifierContext ctx) {
        SourceDataCriteria.Existence existence = SourceDataCriteria.Existence.Occurrence;
        if (ctx != null && ctx.getText().equals("no")) {
            existence = SourceDataCriteria.Existence.NonOccurrence;
        } else if (ctx != null && ctx.getText().equals("unknown")) {
            existence = SourceDataCriteria.Existence.UnknownOccurrence;
        }

        return existence;
    }

    private TrackBack track(Trackable trackable, ParserRuleContext ctx) {
        String lib = library.getLibrary();
        if (lib == null) {
            lib = "unknown"; // TODO: use filename instead?
        }

        TrackBack tb = new TrackBack(
                lib,
                library.getVersion(),
                ctx.getStart().getLine(),
                ctx.getStart().getCharPositionInLine(),
                ctx.getStop().getLine(),
                ctx.getStop().getCharPositionInLine() + ctx.getStop().getText().length() - 1
        );

        trackable.addTrackBack(tb);

        return tb;
    }

    private String getOriginalString(ParserRuleContext ctx) {
        int a = ctx.start.getStartIndex();
        int b = ctx.stop.getStopIndex();
        Interval interval = new Interval(a, b);
        return ctx.start.getInputStream().getText(interval);
    }
}
