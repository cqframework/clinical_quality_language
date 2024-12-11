package org.cqframework.cql.elm.visiting

import org.hl7.elm.r1.AnyInCodeSystem
import org.hl7.elm.r1.AnyInValueSet
import org.hl7.elm.r1.BinaryExpression
import org.hl7.elm.r1.CalculateAge
import org.hl7.elm.r1.CalculateAgeAt
import org.hl7.elm.r1.Code
import org.hl7.elm.r1.CodeDef
import org.hl7.elm.r1.CodeFilterElement
import org.hl7.elm.r1.CodeRef
import org.hl7.elm.r1.CodeSystemDef
import org.hl7.elm.r1.CodeSystemRef
import org.hl7.elm.r1.Concept
import org.hl7.elm.r1.ConceptDef
import org.hl7.elm.r1.ConceptRef
import org.hl7.elm.r1.DateFilterElement
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.ExpandValueSet
import org.hl7.elm.r1.Expression
import org.hl7.elm.r1.ExpressionDef
import org.hl7.elm.r1.ExpressionRef
import org.hl7.elm.r1.FunctionRef
import org.hl7.elm.r1.InCodeSystem
import org.hl7.elm.r1.InValueSet
import org.hl7.elm.r1.IncludeElement
import org.hl7.elm.r1.OperatorExpression
import org.hl7.elm.r1.OtherFilterElement
import org.hl7.elm.r1.Property
import org.hl7.elm.r1.Quantity
import org.hl7.elm.r1.Ratio
import org.hl7.elm.r1.Retrieve
import org.hl7.elm.r1.Search
import org.hl7.elm.r1.SubsumedBy
import org.hl7.elm.r1.Subsumes
import org.hl7.elm.r1.UnaryExpression
import org.hl7.elm.r1.ValueSetDef
import org.hl7.elm.r1.ValueSetRef

/**
 * Provides the base implementation for an ElmClinicalVisitor
 *
 * @param <T> The return type of the visit operation. Use [Void] for
 * @param <C> The type of context passed to each visit method operations with no return type.
 *   </C></T>
 */
@Suppress("TooManyFunctions")
abstract class BaseElmClinicalVisitor<T, C> : BaseElmVisitor<T, C>(), ElmClinicalVisitor<T, C> {
    override fun visitElement(elm: Element, context: C): T {
        return when (elm) {
            is ExpressionDef -> visitExpressionDef(elm, context)
            is CodeDef -> visitCodeDef(elm, context)
            is CodeSystemDef -> visitCodeSystemDef(elm, context)
            is ValueSetDef -> visitValueSetDef(elm, context)
            is ConceptDef -> visitConceptDef(elm, context)
            is CodeFilterElement -> visitCodeFilterElement(elm, context)
            is DateFilterElement -> visitDateFilterElement(elm, context)
            is OtherFilterElement -> visitOtherFilterElement(elm, context)
            is IncludeElement -> visitIncludeElement(elm, context)
            else -> super.visitElement(elm, context)
        }
    }

    /**
     * Visit an Expression. This method will be called for every node in the tree that is an
     * Expression.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitExpression(elm: Expression, context: C): T {
        return when (elm) {
            is FunctionRef -> visitFunctionRef(elm, context)
            is ExpressionRef -> visitExpressionRef(elm, context)
            is CodeSystemRef -> visitCodeSystemRef(elm, context)
            is ValueSetRef -> visitValueSetRef(elm, context)
            is CodeRef -> visitCodeRef(elm, context)
            is ConceptRef -> visitConceptRef(elm, context)
            is Code -> visitCode(elm, context)
            is Concept -> visitConcept(elm, context)
            is Quantity -> visitQuantity(elm, context)
            is Ratio -> visitRatio(elm, context)
            is Retrieve -> visitRetrieve(elm, context)
            else -> super.visitExpression(elm, context)
        }
    }

    /**
     * Visit an OperatorExpression. This method will be called for every node in the tree that is a
     * OperatorExpression.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitOperatorExpression(elm: OperatorExpression, context: C): T {
        return when (elm) {
            is InCodeSystem -> visitInCodeSystem(elm, context)
            is AnyInCodeSystem -> visitAnyInCodeSystem(elm, context)
            is InValueSet -> visitInValueSet(elm, context)
            is AnyInValueSet -> visitAnyInValueSet(elm, context)
            else -> super.visitOperatorExpression(elm, context)
        }
    }

    /**
     * Visit a UnaryExpression. This method will be called for every node in the tree that is a
     * UnaryExpression.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitUnaryExpression(elm: UnaryExpression, context: C): T {
        if (elm is ExpandValueSet) return visitExpandValueSet(elm, context)
        return if (elm is CalculateAge) visitCalculateAge(elm, context)
        else super.visitUnaryExpression(elm, context)
    }

    /**
     * Visit a BinaryExpression. This method will be called for every node in the tree that is a
     * BinaryExpression.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitBinaryExpression(elm: BinaryExpression, context: C): T {
        return when (elm) {
            is CalculateAgeAt -> visitCalculateAgeAt(elm, context)
            is Subsumes -> visitSubsumes(elm, context)
            is SubsumedBy -> visitSubsumedBy(elm, context)
            else -> super.visitBinaryExpression(elm, context)
        }
    }

    /**
     * Visit an ExpandValueSet. This method will be called for every node in the tree that is an
     * ExpandValueSet.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    open fun visitExpandValueSet(elm: ExpandValueSet, context: C): T {
        return visitFields(elm, context)
    }

    /**
     * Visit a CodeFilterElement. This method will be called for every node in the tree that is a
     * CodeFilterElement.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitCodeFilterElement(elm: CodeFilterElement, context: C): T {
        var result = visitFields(elm, context)
        if (elm.value != null) {
            val childResult = visitExpression(elm.value, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a DateFilterElement. This method will be called for every node in the tree that is a
     * DateFilterElement.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitDateFilterElement(elm: DateFilterElement, context: C): T {
        var result = visitFields(elm, context)

        if (elm.value != null) {
            val childResult = visitExpression(elm.value, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit an OtherFilterElement. This method will be called for every node in the tree that is an
     * OtherFilterElement.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitOtherFilterElement(elm: OtherFilterElement, context: C): T {
        var result = visitFields(elm, context)

        if (elm.value != null) {
            val childResult = visitExpression(elm.value, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit an IncludeElement. This method will be called for every node in the tree that is an
     * IncludeElement.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitIncludeElement(elm: IncludeElement, context: C): T {
        return visitFields(elm, context)
    }

    /**
     * Visit a Retrieve. This method will be called for every node in the tree that is a Retrieve.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitRetrieve(elm: Retrieve, context: C): T {
        var result = visitFields(elm, context)

        for (cfe in elm.codeFilter) {
            val childResult = visitCodeFilterElement(cfe, context)
            result = aggregateResult(result, childResult)
        }

        if (elm.codes != null) {
            val childResult = visitExpression(elm.codes, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.context != null) {
            val childResult = visitExpression(elm.context, context)
            result = aggregateResult(result, childResult)
        }
        for (dfe in elm.dateFilter) {
            val childResult = visitDateFilterElement(dfe, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.dateRange != null) {
            val childResult = visitExpression(elm.dateRange, context)
            result = aggregateResult(result, childResult)
        }

        if (elm.id != null) {
            val childResult = visitExpression(elm.id, context)
            result = aggregateResult(result, childResult)
        }

        for (ie in elm.include) {
            val childResult = visitIncludeElement(ie, context)
            result = aggregateResult(result, childResult)
        }

        for (ofe in elm.otherFilter) {
            val childResult = visitOtherFilterElement(ofe, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a Property. This method will be called for every node in the tree that is a Property.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitProperty(elm: Property, context: C): T {
        if (elm is Search) {
            return visitSearch(elm, context)
        }
        return super.visitProperty(elm, context)
    }

    /**
     * Visit a Search. This method will be called for every node in the tree that is a Search.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitSearch(elm: Search, context: C): T {
        var result = visitFields(elm, context)

        if (elm.source != null) {
            val childResult = visitExpression(elm.source, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a CodeSystemDef. This method will be called for every node in the tree that is a
     * CodeSystemDef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitCodeSystemDef(elm: CodeSystemDef, context: C): T {
        var result = visitFields(elm, context)
        if (elm.accessLevel != null) {
            val childResult = visitAccessModifier(elm.accessLevel, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a ValueSetDef. This method will be called for every node in the tree that is a
     * ValueSetDef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitValueSetDef(elm: ValueSetDef, context: C): T {
        var result = visitFields(elm, context)

        if (elm.accessLevel != null) {
            val childResult = visitAccessModifier(elm.accessLevel, context)
            result = aggregateResult(result, childResult)
        }
        for (codeSystemRef in elm.codeSystem) {
            val childResult = visitCodeSystemRef(codeSystemRef, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a CodeDef. This method will be called for every node in the tree that is a CodeDef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitCodeDef(elm: CodeDef, context: C): T {
        var result = visitFields(elm, context)

        if (elm.accessLevel != null) {
            val childResult = visitAccessModifier(elm.accessLevel, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.codeSystem != null) {
            val childResult: T? = visitCodeSystemRef(elm.codeSystem, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit an ConceptDef. This method will be called for every node in the tree that is an
     * ConceptDef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitConceptDef(elm: ConceptDef, context: C): T {
        var result = visitFields(elm, context)

        if (elm.accessLevel != null) {
            val childResult = visitAccessModifier(elm.accessLevel, context)
            result = aggregateResult(result, childResult)
        }
        for (cr in elm.code) {
            val childResult = visitCodeRef(cr, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a CodeSystemRef. This method will be called for every node in the tree that is a
     * CodeSystemRef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitCodeSystemRef(elm: CodeSystemRef, context: C): T {
        return visitFields(elm, context)
    }

    /**
     * Visit a ValueSetRef. This method will be called for every node in the tree that is a
     * ValueSetRef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitValueSetRef(elm: ValueSetRef, context: C): T {
        return visitFields(elm, context)
    }

    /**
     * Visit a CodeRef. This method will be called for every node in the tree that is a CodeRef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitCodeRef(elm: CodeRef, context: C): T {
        return visitFields(elm, context)
    }

    /**
     * Visit a ConceptRef. This method will be called for every node in the tree that is a
     * ConceptRef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitConceptRef(elm: ConceptRef, context: C): T {
        return visitFields(elm, context)
    }

    /**
     * Visit a Code. This method will be called for every node in the tree that is a Code.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitCode(elm: Code, context: C): T {
        var result = visitFields(elm, context)

        if (elm.system != null) {
            val childResult: T? = visitCodeSystemRef(elm.system, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a Concept. This method will be called for every node in the tree that is a Concept.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitConcept(elm: Concept, context: C): T {
        var result = visitFields(elm, context)

        for (c in elm.code) {
            val childResult = visitCode(c, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a InCodeSystem. This method will be called for every node in the tree that is a
     * InCodeSystem.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitInCodeSystem(elm: InCodeSystem, context: C): T {
        var result = visitFields(elm, context)

        if (elm.code != null) {
            val childResult = visitExpression(elm.code, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.codesystem != null) {
            val childResult: T? = visitCodeSystemRef(elm.codesystem, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.codesystemExpression != null) {
            val childResult = visitExpression(elm.codesystemExpression, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit an AnyInCodeSystem. This method will be called for every node in the tree that is an
     * AnyInCodeSystem.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitAnyInCodeSystem(elm: AnyInCodeSystem, context: C): T {
        var result = visitFields(elm, context)

        if (elm.codes != null) {
            val childResult = visitExpression(elm.codes, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.codesystem != null) {
            val childResult: T? = visitCodeSystemRef(elm.codesystem, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.codesystemExpression != null) {
            val childResult = visitExpression(elm.codesystemExpression, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a InValueSet. This method will be called for every node in the tree that is a
     * InValueSet.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitInValueSet(elm: InValueSet, context: C): T {
        var result = visitFields(elm, context)

        if (elm.code != null) {
            val childResult = visitExpression(elm.code, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.valueset != null) {
            val childResult: T? = visitValueSetRef(elm.valueset, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.valuesetExpression != null) {
            val childResult = visitExpression(elm.valuesetExpression, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit an AnyInValueSet. This method will be called for every node in the tree that is an
     * AnyInValueSet.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitAnyInValueSet(elm: AnyInValueSet, context: C): T {
        var result = visitFields(elm, context)

        if (elm.codes != null) {
            val childResult = visitExpression(elm.codes, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.valueset != null) {
            val childResult: T? = visitValueSetRef(elm.valueset, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.valuesetExpression != null) {
            val childResult = visitExpression(elm.valuesetExpression, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit an Subsumes. This method will be called for every node in the tree that is an Subsumes.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitSubsumes(elm: Subsumes, context: C): T {
        return visitFields(elm, context)
    }

    /**
     * Visit an SubsumedBy. This method will be called for every node in the tree that is an
     * SubsumedBy.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitSubsumedBy(elm: SubsumedBy, context: C): T {
        return visitFields(elm, context)
    }

    /**
     * Visit a Quantity. This method will be called for every node in the tree that is a Quantity.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitQuantity(elm: Quantity, context: C): T {
        return visitFields(elm, context)
    }

    /**
     * Visit a Ratio. This method will be called for every node in the tree that is a Ratio.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitRatio(elm: Ratio, context: C): T {
        var result = visitFields(elm, context)

        if (elm.denominator != null) {
            val childResult: T? = visitQuantity(elm.denominator, context)
            result = aggregateResult(result, childResult)
        }
        if (elm.numerator != null) {
            val childResult: T? = visitQuantity(elm.numerator, context)
            result = aggregateResult(result, childResult)
        }

        return result
    }

    /**
     * Visit a CalculateAge. This method will be called for every node in the tree that is a
     * CalculateAge.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitCalculateAge(elm: CalculateAge, context: C): T {
        return visitFields(elm, context)
    }

    /**
     * Visit a CalculateAgeAt. This method will be called for every node in the tree that is a
     * CalculateAgeAt.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitCalculateAgeAt(elm: CalculateAgeAt, context: C): T {
        return visitFields(elm, context)
    }
}
