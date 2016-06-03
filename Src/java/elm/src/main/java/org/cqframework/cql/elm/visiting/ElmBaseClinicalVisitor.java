package org.cqframework.cql.elm.visiting;

import org.hl7.elm.r1.*;

/**
 * Provides the base implementation for an ElmClinicalVisitor
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * @param <C> The type of context passed to each visit method
 * operations with no return type.
 */
public class ElmBaseClinicalVisitor<T, C> extends ElmBaseVisitor<T, C> implements ElmClinicalVisitor<T, C> {

    /**
     * Visit an Expression. This method will be called for
     * every node in the tree that is an Expression.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    @Override
    public T visitExpression(Expression elm, C context) {
        if (elm instanceof Code) return visitCode((Code)elm, context);
        else if (elm instanceof CodeSystemRef) return visitCodeSystemRef((CodeSystemRef)elm, context);
        else if (elm instanceof Concept) return visitConcept((Concept)elm, context);
        else if (elm instanceof InCodeSystem) return visitInCodeSystem((InCodeSystem)elm, context);
        else if (elm instanceof InValueSet) return visitInValueSet((InValueSet)elm, context);
        else if (elm instanceof Quantity) return visitQuantity((Quantity)elm, context);
        else if (elm instanceof Retrieve) return visitRetrieve((Retrieve)elm, context);
        else if (elm instanceof ValueSetRef) return visitValueSetRef((ValueSetRef)elm, context);
        else return super.visitExpression(elm, context);
    }

    /**
     * Visit a UnaryExpression. This method will be called for
     * every node in the tree that is a UnaryExpression.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    @Override
    public T visitUnaryExpression(UnaryExpression elm, C context) {
        if (elm instanceof CalculateAge) return visitCalculateAge((CalculateAge)elm, context);
        else return super.visitUnaryExpression(elm, context);
    }

    /**
     * Visit a BinaryExpression. This method will be called for
     * every node in the tree that is a BinaryExpression.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    @Override
    public T visitBinaryExpression(BinaryExpression elm, C context) {
        if (elm instanceof CalculateAgeAt) return visitCalculateAgeAt((CalculateAgeAt)elm, context);
        else return super.visitBinaryExpression(elm, context);
    }

    /**
     * Visit a Retrieve. This method will be called for
     * every node in the tree that is a Retrieve.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitRetrieve(Retrieve elm, C context) {
        return null;
    }

    /**
     * Visit a CodeSystemDef. This method will be called for
     * every node in the tree that is a CodeSystemDef.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitCodeSystemDef(CodeSystemDef elm, C context) {
        return null;
    }

    /**
     * Visit a ValueSetDef. This method will be called for
     * every node in the tree that is a ValueSetDef.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitValueSetDef(ValueSetDef elm, C context) {
        return null;
    }

    /**
     * Visit a CodeSystemRef. This method will be called for
     * every node in the tree that is a CodeSystemRef.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitCodeSystemRef(CodeSystemRef elm, C context) {
        return null;
    }

    /**
     * Visit a ValueSetRef. This method will be called for
     * every node in the tree that is a ValueSetRef.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitValueSetRef(ValueSetRef elm, C context) {
        return null;
    }

    /**
     * Visit a Code. This method will be called for
     * every node in the tree that is a Code.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitCode(Code elm, C context) {
        return null;
    }

    /**
     * Visit a Concept. This method will be called for
     * every node in the tree that is a Concept.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitConcept(Concept elm, C context) {
        return null;
    }

    /**
     * Visit a InCodeSystem. This method will be called for
     * every node in the tree that is a InCodeSystem.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitInCodeSystem(InCodeSystem elm, C context) {
        return null;
    }

    /**
     * Visit a InValueSet. This method will be called for
     * every node in the tree that is a InValueSet.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitInValueSet(InValueSet elm, C context) {
        return null;
    }

    /**
     * Visit a Quantity. This method will be called for
     * every node in the tree that is a Quantity.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitQuantity(Quantity elm, C context) {
        return null;
    }

    /**
     * Visit a CalculateAge. This method will be called for
     * every node in the tree that is a CalculateAge.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitCalculateAge(CalculateAge elm, C context) {
        return null;
    }

    /**
     * Visit a CalculateAgeAt. This method will be called for
     * every node in the tree that is a CalculateAgeAt.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitCalculateAgeAt(CalculateAgeAt elm, C context) {
        return null;
    }
}
