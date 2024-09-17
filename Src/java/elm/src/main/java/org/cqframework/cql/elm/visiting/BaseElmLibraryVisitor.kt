package org.cqframework.cql.elm.visiting

import org.hl7.elm.r1.ContextDef
import org.hl7.elm.r1.Element
import org.hl7.elm.r1.IncludeDef
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.UsingDef

/** Created by Bryn on 4/14/2016. */
abstract class BaseElmLibraryVisitor<T, C> :
    BaseElmClinicalVisitor<T, C>(), ElmLibraryVisitor<T, C> {
    /**
     * Visit an Element in an ELM tree. This method will be called for every node in the tree that
     * is a descendant of the Element type.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitElement(elm: Element, context: C): T =
        when (elm) {
            is IncludeDef -> visitIncludeDef(elm, context)
            is ContextDef -> visitContextDef(elm, context)
            is Library -> visitLibrary(elm, context)
            is UsingDef -> visitUsingDef(elm, context)
            else -> super.visitElement(elm, context)
        }

    /**
     * Visit a Library. This method will be called for every node in the tree that is a Library.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitLibrary(elm: Library, context: C): T {
        var result = visitFields(elm, context)
        if (!elm.usings?.def.isNullOrEmpty()) {
            for (def in elm.usings.def) {
                val childResult = visitUsingDef(def, context)
                result = aggregateResult(result, childResult)
            }
        }

        if (!elm.includes?.def.isNullOrEmpty()) {
            for (def in elm.includes.def) {
                val childResult = visitIncludeDef(def, context)
                result = aggregateResult(result, childResult)
            }
        }

        if (!elm.codeSystems?.def.isNullOrEmpty()) {
            for (def in elm.codeSystems.def) {
                val childResult = visitCodeSystemDef(def, context)
                result = aggregateResult(result, childResult)
            }
        }

        if (!elm.valueSets?.def.isNullOrEmpty()) {
            for (def in elm.valueSets.def) {
                val childResult = visitValueSetDef(def, context)
                result = aggregateResult(result, childResult)
            }
        }

        if (!elm.codes?.def.isNullOrEmpty()) {
            for (def in elm.codes.def) {
                val childResult = visitElement(def, context)
                result = aggregateResult(result, childResult)
            }
        }

        if (!elm.concepts?.def.isNullOrEmpty()) {
            for (def in elm.concepts.def) {
                val childResult = visitConceptDef(def, context)
                result = aggregateResult(result, childResult)
            }
        }

        if (!elm.parameters?.def.isNullOrEmpty()) {
            for (def in elm.parameters.def) {
                val childResult = visitParameterDef(def, context)
                result = aggregateResult(result, childResult)
            }
        }

        if (!elm.contexts?.def.isNullOrEmpty()) {
            for (def in elm.contexts.def) {
                val childResult = visitContextDef(def, context)
                result = aggregateResult(result, childResult)
            }
        }

        if (!elm.statements?.def.isNullOrEmpty()) {
            for (def in elm.statements.def) {
                val childResult = visitExpressionDef(def, context)
                result = aggregateResult(result, childResult)
            }
        }

        return result
    }

    /**
     * Visit a UsingDef. This method will be called for every node in the tree that is a UsingDef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitUsingDef(elm: UsingDef, context: C): T = visitFields(elm, context)

    /**
     * Visit a IncludeDef. This method will be called for every node in the tree that is a
     * IncludeDef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitIncludeDef(elm: IncludeDef, context: C): T = visitFields(elm, context)

    /**
     * Visit a ContextDef. This method will be called for every node in the tree that is a
     * ContextDef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitContextDef(elm: ContextDef, context: C): T = visitFields(elm, context)
}
