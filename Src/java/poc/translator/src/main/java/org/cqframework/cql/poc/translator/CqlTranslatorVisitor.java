package org.cqframework.cql.poc.translator;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;
import org.cqframework.cql.gen.cqlBaseVisitor;
import org.cqframework.cql.gen.cqlParser;
import org.cqframework.cql.poc.translator.expressions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bobd on 7/24/14.
 */
public class CqlTranslatorVisitor extends cqlBaseVisitor {

    @Override
    public Object visitLetStatement(@NotNull cqlParser.LetStatementContext ctx) {
        return new LetStatement(ctx.IDENTIFIER().getText(), (Expression)visit(ctx.expression()));
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
        String unit = (ctx.unit() == null)? null: ctx.unit().getText();
        if(ctx.unit()!=null && ctx.unit().STRING()!=null){
            unit = unit.substring(2, unit.length() - 1);
        }
        return new QuantityLiteral(ctx.QUANTITY().getText(),unit);
    }

    @Override
    public Object visitExistenceExpression(@NotNull cqlParser.ExistenceExpressionContext ctx) {
        Expression ext = (Expression)this.visit(ctx.expression());
        return new ExistenceExpression(ctx.children.get(0).equals("not"),ext);
    }

    @Override
    public Object visitMultiplicationExpressionTerm(@NotNull cqlParser.MultiplicationExpressionTermContext ctx) {
        return new ArithmaticExpression((Expression)this.visit(ctx.expressionTerm(0)),
                                         ArithmaticExpression.Operator.bySymbol(ctx.getChild(1).getText()),
                                          (Expression)this.visit(ctx.expressionTerm(1)));
    }

    @Override
    public Object visitPowerExpressionTerm(@NotNull cqlParser.PowerExpressionTermContext ctx) {
        return new ArithmaticExpression((Expression)this.visit(ctx.expressionTerm(0)),
                ArithmaticExpression.Operator.POW,
                (Expression)this.visit(ctx.expressionTerm(1)));
    }

    @Override
    public Object visitAdditionExpressionTerm(@NotNull cqlParser.AdditionExpressionTermContext ctx) {
        return new ArithmaticExpression((Expression)this.visit(ctx.expressionTerm(0)),
                ArithmaticExpression.Operator.bySymbol(ctx.getChild(1).getText()),
                (Expression)this.visit(ctx.expressionTerm(1)));
    }

    @Override
    public Object visitParenthesizedTerm(@NotNull cqlParser.ParenthesizedTermContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public Object visitAndExpression(@NotNull cqlParser.AndExpressionContext ctx) {
        return new AndExpression((Expression)visit(ctx.expression(0)),(Expression)visit(ctx.expression(1)));
    }

    @Override
    public Object visitOrExpression(@NotNull cqlParser.OrExpressionContext ctx) {
        boolean xor = ctx.getChild(1).getText().equals("xor");
        return new OrExpression((Expression)visit(ctx.expression(0)),(Expression)visit(ctx.expression(1)),xor);
    }

    @Override
    public Object visitEqualityExpression(@NotNull cqlParser.EqualityExpressionContext ctx) {
        ComparisionExpression.Comparator comp = ComparisionExpression.Comparator.bySymbol(ctx.getChild(1).getText());
        return new ComparisionExpression((Expression)visit(ctx.expression(0)),comp, (Expression)visit(ctx.expression(1)));
    }

    @Override
    public Object visitInequalityExpression(@NotNull cqlParser.InequalityExpressionContext ctx) {
        ComparisionExpression.Comparator comp = ComparisionExpression.Comparator.bySymbol(ctx.getChild(1).getText());
        return new ComparisionExpression((Expression)visit(ctx.expression(0)),comp, (Expression)visit(ctx.expression(1)));
    }

    @Override
    public Object visitQualifiedIdentifier(@NotNull cqlParser.QualifiedIdentifierContext ctx) {
        String qualifier = (ctx.qualifier() != null) ? ctx.qualifier().getText(): null;
        return new QualifiedIdentifier(qualifier,ctx.IDENTIFIER().getText(),false);
    }

    @Override
    public Object visitValueset(@NotNull cqlParser.ValuesetContext ctx) {
        String qualifier = (ctx.qualifier() != null) ? ctx.qualifier().getText(): null;
        boolean isValuesetIdentifier = ctx.VALUESET() != null;

        String ident = isValuesetIdentifier ? ctx.VALUESET().getText() : ctx.IDENTIFIER().getText();
        if(ident != null && isValuesetIdentifier){
            ident=ident.substring(1,ident.length()-1);
        }
        return new QualifiedIdentifier(qualifier,ident,isValuesetIdentifier);
    }

    @Override
    public Object visitAccessorExpressionTerm(@NotNull cqlParser.AccessorExpressionTermContext ctx) {
        boolean isValueset =ctx.VALUESET() != null;
        String accessor = isValueset ? ctx.VALUESET().getText(): ctx.IDENTIFIER().getText();
        if(accessor != null && isValueset){
            accessor = accessor.substring(1,accessor.length()-1);
        }
        return new AccessorExpression((Expression)visit(ctx.expressionTerm()),accessor,(ctx.VALUESET() != null));
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
        Expression left = (Expression)visit(ctx.expression());
        Expression right = null;
        ComparisionExpression.Comparator comp = null;
        String lastChild = ctx.getChild(ctx.getChildCount()-1).getText();
        String nextToLast = ctx.getChild(ctx.getChildCount()-2).getText();
        if(lastChild.equals("null")){
            right = new NullLiteral();
        }else{
            right = new BooleanLiteral(lastChild);
        }
        comp = (nextToLast.equals("not")) ? ComparisionExpression.Comparator.bySymbol("<>") :
                                            ComparisionExpression.Comparator.bySymbol("=");

        return new ComparisionExpression(left,comp,right);
    }

    @Override
    public Object visitRetrieve(@NotNull cqlParser.RetrieveContext ctx) {
        RetrieveExpression.ExModifier existenceModifier = null;
        if(ctx.existenceModifier() != null){
            existenceModifier = RetrieveExpression.ExModifier.valueOf(ctx.existenceModifier().getText());
        }
        QualifiedIdentifier topic = (QualifiedIdentifier)visit(ctx.topic());
        IdentifierExpression modality = ctx.modality() != null ? (IdentifierExpression)visit(ctx.modality()) : null;
        IdentifierExpression valuesetPathIdentifier = ctx.valuesetPathIdentifier() != null ? (IdentifierExpression)visit(ctx.valuesetPathIdentifier()): null;
        QualifiedIdentifier valueset = ctx.valueset() != null ? (QualifiedIdentifier)visit(ctx.valueset()): null;
        IdentifierExpression duringPathIdentifier = ctx.duringPathIdentifier() != null ? (IdentifierExpression)visit(ctx.duringPathIdentifier()):null;
        Expression duringExpression = ctx.expression()!=null ? (Expression)visit(ctx.expression()) : null;

        return new RetrieveExpression(existenceModifier,topic,modality,valuesetPathIdentifier,valueset,duringPathIdentifier,duringExpression);
    }

    @Override
    public Object visitQueryInclusionClause(@NotNull cqlParser.QueryInclusionClauseContext ctx) {
        boolean negated = ctx.getChild(0).equals("without");
        Expression expression = (Expression)visit(ctx.expression());
        AliasedQuerySource aqs = (AliasedQuerySource)visit(ctx.aliasedQuerySource());
        return new QueryInclusionClauseExpression(aqs,expression,negated);
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
        SortClause.SortDirection direction=SortClause.SortDirection.valueOf(ctx.sortDirection().getText());
        QualifiedIdentifier exp = (QualifiedIdentifier)visitQualifiedIdentifier(ctx.qualifiedIdentifier());
        return new SortItem(direction,exp);
    }

    @Override
    public Object visitSortClause(@NotNull cqlParser.SortClauseContext ctx) {
        SortClause.SortDirection direction = ctx.sortDirection() != null ? SortClause.SortDirection.valueOf(ctx.sortDirection().getText()): null;
        List<SortItem> sortItems = new ArrayList<>();
        if(ctx.sortByItem() != null){
            for (cqlParser.SortByItemContext sortByItemContext : ctx.sortByItem()) {
                sortItems.add((SortItem)visitSortByItem(sortByItemContext));
            }
        }
        return new SortClause(direction,sortItems);
    }

    @Override
    public Object visitQuerySource(@NotNull cqlParser.QuerySourceContext ctx) {
        ParseTree o = ctx.expression();
        if(o==null){
            o = ctx.retrieve();
        }
        if(o == null){
            o = ctx.qualifiedIdentifier();
        }
        return visit(o);
    }

    @Override
    public Object visitAliasedQuerySource(@NotNull cqlParser.AliasedQuerySourceContext ctx) {
        return new AliasedQuerySource((Expression)visit(ctx.querySource()),ctx.alias().getText());
    }

    @Override
    public Object visitQuery(@NotNull cqlParser.QueryContext ctx) {
       AliasedQuerySource aqs = (AliasedQuerySource)visit(ctx.aliasedQuerySource());
       List<QueryInclusionClauseExpression> qicx = new ArrayList<>();
       if(ctx.queryInclusionClause()!=null){
           for (cqlParser.QueryInclusionClauseContext queryInclusionClauseContext : ctx.queryInclusionClause()) {
               qicx.add((QueryInclusionClauseExpression)visit(queryInclusionClauseContext));
           }
       }
       Expression where = ctx.whereClause() != null ? (Expression)visit(ctx.whereClause()) : null;
       Expression ret = ctx.returnClause() != null ? (Expression)visit(ctx.returnClause()) : null;
       SortClause sort = ctx.sortClause() != null ? (SortClause)visit(ctx.sortClause()) : null;

       return new QueryExpression(aqs,qicx,where,ret,sort);
    }



    @Override
    public Object visitMethodExpressionTerm(@NotNull cqlParser.MethodExpressionTermContext ctx) {
        Expression identifier = (Expression)visit(ctx.expressionTerm());
        List<Expression>parameters = new ArrayList<>();
        if(ctx.expression() != null){
            for (cqlParser.ExpressionContext expressionContext : ctx.expression()) {
                parameters.add((Expression)visit(expressionContext));
            }
        }
        return new MethodExpression(identifier,parameters);
    }

    @Override
    public Object visitFunctionDefinition(@NotNull cqlParser.FunctionDefinitionContext ctx) {
        Expression _resturn = (Expression)visit(ctx.functionBody().returnStatement());
        String ident = ctx.IDENTIFIER().getText();
        List<OperandDefinition> operands = new ArrayList<>();
        if(ctx.operandDefinition() !=null){
            for (cqlParser.OperandDefinitionContext opdef : ctx.operandDefinition()) {
                 operands.add(new OperandDefinition(opdef.IDENTIFIER().getText(),opdef.typeSpecifier().getText()));
            }
        }

        return new FunctionDef(ident,operands,_resturn);
    }
}
