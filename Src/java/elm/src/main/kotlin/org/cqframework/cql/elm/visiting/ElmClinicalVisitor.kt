package org.cqframework.cql.elm.visiting

import org.hl7.elm.r1.AnyInCodeSystem
import org.hl7.elm.r1.AnyInValueSet
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
import org.hl7.elm.r1.InCodeSystem
import org.hl7.elm.r1.InValueSet
import org.hl7.elm.r1.IncludeElement
import org.hl7.elm.r1.OtherFilterElement
import org.hl7.elm.r1.Quantity
import org.hl7.elm.r1.Ratio
import org.hl7.elm.r1.Retrieve
import org.hl7.elm.r1.Search
import org.hl7.elm.r1.SubsumedBy
import org.hl7.elm.r1.Subsumes
import org.hl7.elm.r1.ValueSetDef
import org.hl7.elm.r1.ValueSetRef

/**
 * This interface defines a complete generic visitor for an Elm tree
 *
 * @param <T> The return type of the visit operation. Use [Void] for
 * @param <C> The type of context passed to each visit method operations with no return type.
 *   </C></T>
 */
@Suppress("TooManyFunctions")
interface ElmClinicalVisitor<T, C> : ElmVisitor<T, C> {
    /**
     * Visit a CodeFilterElement. This method will be called for every node in the tree that is a
     * CodeFilterElement.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitCodeFilterElement(elm: CodeFilterElement, context: C): T

    /**
     * Visit a DateFilterElement. This method will be called for every node in the tree that is a
     * DateFilterElement.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitDateFilterElement(elm: DateFilterElement, context: C): T

    /**
     * Visit an OtherFilterElement. This method will be called for every node in the tree that is an
     * OtherFilterElement.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitOtherFilterElement(elm: OtherFilterElement, context: C): T

    /**
     * Visit an IncludeElement. This method will be called for every node in the tree that is an
     * IncludeElement.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitIncludeElement(elm: IncludeElement, context: C): T

    /**
     * Visit a Retrieve. This method will be called for every node in the tree that is a Retrieve.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitRetrieve(elm: Retrieve, context: C): T

    /**
     * Visit a Search. This method will be called for every node in the tree that is a Search.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitSearch(elm: Search, context: C): T

    /**
     * Visit a CodeSystemDef. This method will be called for every node in the tree that is a
     * CodeSystemDef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitCodeSystemDef(elm: CodeSystemDef, context: C): T

    /**
     * Visit a ValueSetDef. This method will be called for every node in the tree that is a
     * ValueSetDef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitValueSetDef(elm: ValueSetDef, context: C): T

    /**
     * Visit a CodeDef. This method will be called for every node in the tree that is a CodeDef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitCodeDef(elm: CodeDef, context: C): T

    /**
     * Visit an ConceptDef. This method will be called for every node in the tree that is an
     * ConceptDef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitConceptDef(elm: ConceptDef, context: C): T

    /**
     * Visit a CodeSystemRef. This method will be called for every node in the tree that is a
     * CodeSystemRef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitCodeSystemRef(elm: CodeSystemRef, context: C): T

    /**
     * Visit a ValueSetRef. This method will be called for every node in the tree that is a
     * ValueSetRef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitValueSetRef(elm: ValueSetRef, context: C): T

    /**
     * Visit a CodeRef. This method will be called for every node in the tree that is a CodeRef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitCodeRef(elm: CodeRef, context: C): T

    /**
     * Visit a ConceptRef. This method will be called for every node in the tree that is a
     * ConceptRef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitConceptRef(elm: ConceptRef, context: C): T

    /**
     * Visit a Code. This method will be called for every node in the tree that is a Code.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitCode(elm: Code, context: C): T

    /**
     * Visit a Concept. This method will be called for every node in the tree that is a Concept.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitConcept(elm: Concept, context: C): T

    /**
     * Visit a InCodeSystem. This method will be called for every node in the tree that is a
     * InCodeSystem.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitInCodeSystem(elm: InCodeSystem, context: C): T

    /**
     * Visit an AnyInCodeSystem. This method will be called for every node in the tree that is an
     * AnyInCodeSystem.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitAnyInCodeSystem(elm: AnyInCodeSystem, context: C): T

    /**
     * Visit a InValueSet. This method will be called for every node in the tree that is a
     * InValueSet.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitInValueSet(elm: InValueSet, context: C): T

    /**
     * Visit an AnyInValueSet. This method will be called for every node in the tree that is an
     * AnyInValueSet.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitAnyInValueSet(elm: AnyInValueSet, context: C): T

    /**
     * Visit a Subsumes. This method will be called for every node in the tree that is a Subsumes.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitSubsumes(elm: Subsumes, context: C): T

    /**
     * Visit an SubsumedBy. This method will be called for every node in the tree that is an
     * SubsumedBy.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitSubsumedBy(elm: SubsumedBy, context: C): T

    /**
     * Visit a Quantity. This method will be called for every node in the tree that is a Quantity.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitQuantity(elm: Quantity, context: C): T

    /**
     * Visit a Ratio. This method will be called for every node in the tree that is a Ratio.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitRatio(elm: Ratio, context: C): T

    /**
     * Visit a CalculateAge. This method will be called for every node in the tree that is a
     * CalculateAge.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitCalculateAge(elm: CalculateAge, context: C): T

    /**
     * Visit a CalculateAgeAt. This method will be called for every node in the tree that is a
     * CalculateAgeAt.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitCalculateAgeAt(elm: CalculateAgeAt, context: C): T
}
