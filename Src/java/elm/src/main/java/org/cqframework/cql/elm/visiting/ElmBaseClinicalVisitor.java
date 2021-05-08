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

    @Override
    public T visitElement(Element elm, C context) {
        if (elm instanceof CodeDef) return visitCodeDef((CodeDef)elm, context);
        else if (elm instanceof CodeSystemDef) return visitCodeSystemDef((CodeSystemDef)elm, context);
        else if (elm instanceof ValueSetDef) return visitValueSetDef((ValueSetDef)elm, context);
        else if (elm instanceof ConceptDef) return visitConceptDef((ConceptDef)elm, context);
        else if (elm instanceof CodeFilterElement) return visitCodeFilterElement((CodeFilterElement)elm, context);
        else if (elm instanceof DateFilterElement) return visitDateFilterElement((DateFilterElement)elm, context);
        else if (elm instanceof OtherFilterElement) return visitOtherFilterElement((OtherFilterElement)elm, context);
        else if (elm instanceof IncludeElement) return visitIncludeElement((IncludeElement)elm, context);
        return super.visitElement(elm, context);
    }

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
        else if (elm instanceof ValueSetRef) return visitValueSetRef((ValueSetRef)elm, context);
        else if (elm instanceof CodeRef) return visitCodeRef((CodeRef)elm, context);
        else if (elm instanceof ConceptRef) return visitConceptRef((ConceptRef)elm, context);
        else if (elm instanceof Concept) return visitConcept((Concept)elm, context);
        else if (elm instanceof Quantity) return visitQuantity((Quantity)elm, context);
        else if (elm instanceof Ratio) return visitRatio((Ratio)elm, context);
        else if (elm instanceof Retrieve) return visitRetrieve((Retrieve)elm, context);
        else return super.visitExpression(elm, context);
    }

    /**
     * Visit an OperatorExpression. This method will be called for
     * every node in the tree that is a OperatorExpression.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    @Override
    public T visitOperatorExpression(OperatorExpression elm, C context) {
        if (elm instanceof InCodeSystem) return visitInCodeSystem((InCodeSystem)elm, context);
        else if (elm instanceof AnyInCodeSystem) return visitAnyInCodeSystem((AnyInCodeSystem)elm, context);
        else if (elm instanceof InValueSet) return visitInValueSet((InValueSet)elm, context);
        else if (elm instanceof AnyInValueSet) return visitAnyInValueSet((AnyInValueSet)elm, context);
        else return super.visitOperatorExpression(elm, context);
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
        else if (elm instanceof Subsumes) return visitSubsumes((Subsumes)elm, context);
        else if (elm instanceof SubsumedBy) return visitSubsumedBy((SubsumedBy)elm, context);
        else return super.visitBinaryExpression(elm, context);
    }

    /**
     * Visit a CodeFilterElement. This method will be called for
     * every node in the tree that is a CodeFilterElement.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitCodeFilterElement(CodeFilterElement elm, C context) {
        T result = defaultResult();
        if (elm.getValue() != null) {
            T childResult = visitElement(elm.getValue(), context);
            result = aggregateResult(result, childResult);
        }
        return result;
    }

    /**
     * Visit a DateFilterElement. This method will be called for
     * every node in the tree that is a DateFilterElement.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitDateFilterElement(DateFilterElement elm, C context) {
        T result = defaultResult();
        if (elm.getValue() != null) {
            T childResult = visitElement(elm.getValue(), context);
            result = aggregateResult(result, childResult);
        }
        return result;
    }

    /**
     * Visit an OtherFilterElement. This method will be called for
     * every node in the tree that is an OtherFilterElement.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitOtherFilterElement(OtherFilterElement elm, C context) {
        T result = defaultResult();
        if (elm.getValue() != null) {
            T childResult = visitElement(elm.getValue(), context);
            result = aggregateResult(result, childResult);
        }
        return result;
    }

    /**
     * Visit an IncludeElement. This method will be called for
     * every node in the tree that is an IncludeElement.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitIncludeElement(IncludeElement elm, C context) {
        return defaultResult();
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
        T result = defaultResult();
        if (elm.getCodes() != null) {
            T childResult = visitElement(elm.getCodes(), context);
            result = aggregateResult(result, childResult);
        }
        if (elm.getDateRange() != null) {
            T childResult = visitElement(elm.getDateRange(), context);
            result = aggregateResult(result, childResult);
        }
        if (elm.getContext() != null) {
            T childResult = visitElement(elm.getContext(), context);
            result = aggregateResult(result, childResult);
        }
        for (IncludeElement ie : elm.getInclude()) {
            T childResult = visitElement(ie, context);
            result = aggregateResult(result, childResult);
        }
        for (CodeFilterElement cfe : elm.getCodeFilter()) {
            T childResult = visitElement(cfe, context);
            result = aggregateResult(result, childResult);
        }
        for (DateFilterElement dfe : elm.getDateFilter()) {
            T childResult = visitElement(dfe, context);
            result = aggregateResult(result, childResult);
        }
        for (OtherFilterElement ofe : elm.getOtherFilter()) {
            T childResult = visitElement(ofe, context);
            result = aggregateResult(result, childResult);
        }
        return result;
    }

    /**
     * Visit a Property. This method will be called for
     * every node in the tree that is a Property.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    @Override
    public T visitProperty(Property elm, C context) {
        if (elm instanceof Search) {
            return visitSearch((Search)elm, context);
        }
        return super.visitProperty(elm, context);
    }

    /**
     * Visit a Search. This method will be called for
     * every node in the tree that is a Search.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitSearch(Search elm, C context) {
        return visitChildren(elm, context);
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
        T result = defaultResult();
        if (elm.getAccessLevel() != null) {
            T childResult = visitAccessModifier(elm.getAccessLevel(), context);
            result = aggregateResult(result, childResult);
        }
        return result;
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
        T result = defaultResult();
        if (elm.getAccessLevel() != null) {
            T childResult = visitAccessModifier(elm.getAccessLevel(), context);
            result = aggregateResult(result, childResult);
        }
        for (CodeSystemRef codeSystemRef : elm.getCodeSystem()) {
            T childResult = visitElement(codeSystemRef, context);
            result = aggregateResult(result, childResult);
        }
        return result;
    }

    /**
     * Visit a CodeDef. This method will be called for
     * every node in the tree that is a CodeDef.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitCodeDef(CodeDef elm, C context) {
        T result = defaultResult();
        if (elm.getAccessLevel() != null) {
            T childResult = visitAccessModifier(elm.getAccessLevel(), context);
            result = aggregateResult(result, childResult);
        }
        if (elm.getCodeSystem() != null) {
            T childResult = visitCodeSystemRef(elm.getCodeSystem(), context);
            result = aggregateResult(result, childResult);
        }
        return result;
    }

    /**
     * Visit an ConceptDef. This method will be called for
     * every node in the tree that is an ConceptDef.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitConceptDef(ConceptDef elm, C context) {
        T result = defaultResult();
        if (elm.getAccessLevel() != null) {
            T childResult = visitAccessModifier(elm.getAccessLevel(), context);
            result = aggregateResult(result, childResult);
        }
        for (CodeRef cr : elm.getCode()) {
            T childResult = visitElement(cr, context);
            result = aggregateResult(result, childResult);
        }
        return result;
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
        return defaultResult();
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
        return defaultResult();
    }

    /**
     * Visit a CodeRef. This method will be called for
     * every node in the tree that is a CodeRef.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitCodeRef(CodeRef elm, C context) {
        return defaultResult();
    }

    /**
     * Visit a ConceptRef. This method will be called for
     * every node in the tree that is a ConceptRef.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitConceptRef(ConceptRef elm, C context) {
        return defaultResult();
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
        T result = defaultResult();
        if (elm.getSystem() != null) {
            T childResult = visitElement(elm.getSystem(), context);
            result = aggregateResult(result, childResult);
        }
        return result;
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
        T result = defaultResult();
        for (Code c : elm.getCode()) {
            T childResult = visitElement(c, context);
            result = aggregateResult(result, childResult);
        }
        return result;
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
        T result = defaultResult();
        if (elm.getCode() != null) {
            T childResult = visitElement(elm.getCode(), context);
            result = aggregateResult(result, childResult);
        }
        if (elm.getCodesystem() != null) {
            T childResult = visitElement(elm.getCodesystem(), context);
            result = aggregateResult(result, childResult);
        }
        return result;
    }

    /**
     * Visit an AnyInCodeSystem. This method will be called for
     * every node in the tree that is an AnyInCodeSystem.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitAnyInCodeSystem(AnyInCodeSystem elm, C context) {
        T result = defaultResult();
        if (elm.getCodes() != null) {
            T childResult = visitElement(elm.getCodes(), context);
            result = aggregateResult(result, childResult);
        }
        if (elm.getCodesystem() != null) {
            T childResult = visitElement(elm.getCodesystem(), context);
            result = aggregateResult(result, childResult);
        }
        return result;
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
        T result = defaultResult();
        if (elm.getCode() != null) {
            T childResult = visitElement(elm.getCode(), context);
            result = aggregateResult(result, childResult);
        }
        if (elm.getValueset() != null) {
            T childResult = visitElement(elm.getValueset(), context);
            result = aggregateResult(result, childResult);
        }
        return result;
    }

    /**
     * Visit an AnyInValueSet. This method will be called for
     * every node in the tree that is an AnyInValueSet.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitAnyInValueSet(AnyInValueSet elm, C context) {
        T result = defaultResult();
        if (elm.getCodes() != null) {
            T childResult = visitElement(elm.getCodes(), context);
            result = aggregateResult(result, childResult);
        }
        if (elm.getValueset() != null) {
            T childResult = visitElement(elm.getValueset(), context);
            result = aggregateResult(result, childResult);
        }
        return result;
    }

    /**
     * Visit an Subsumes. This method will be called for
     * every node in the tree that is an Subsumes.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitSubsumes(Subsumes elm, C context) {
        return visitChildren(elm, context);
    }

    /**
     * Visit an SubsumedBy. This method will be called for
     * every node in the tree that is an SubsumedBy.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitSubsumedBy(SubsumedBy elm, C context) {
        return visitChildren(elm, context);
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
        return defaultResult();
    }

    /**
     * Visit a Ratio. This method will be called for
     * every node in the tree that is a Ratio.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitRatio(Ratio elm, C context) {
        T result = defaultResult();
        if (elm.getDenominator() != null) {
            T childResult = visitElement(elm.getDenominator(), context);
            result = aggregateResult(result, childResult);
        }
        if (elm.getNumerator() != null) {
            T childResult = visitElement(elm.getNumerator(), context);
            result = aggregateResult(result, childResult);
        }
        return result;
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
        return visitChildren(elm, context);
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
        return visitChildren(elm, context);
    }
}
