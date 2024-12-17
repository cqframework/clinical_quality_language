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
    override fun visitElement(elm: Element, context: C): T? =
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
    override fun visitLibrary(elm: Library, context: C): T? {
        var result = visitFields(elm, context)
        elm.usings?.def?.forEach {
            val childResult = visitUsingDef(it!!, context)
            result = aggregateResult(result, childResult)
        }

        elm.includes?.def?.forEach {
            val childResult = visitIncludeDef(it!!, context)
            result = aggregateResult(result, childResult)
        }

        elm.codeSystems?.def?.forEach {
            val childResult = visitCodeSystemDef(it!!, context)
            result = aggregateResult(result, childResult)
        }

        elm.valueSets?.def?.forEach {
            val childResult = visitValueSetDef(it!!, context)
            result = aggregateResult(result, childResult)
        }

        elm.codes?.def?.forEach {
            val childResult = visitElement(it!!, context)
            result = aggregateResult(result, childResult)
        }

        elm.concepts?.def?.forEach {
            val childResult = visitConceptDef(it!!, context)
            result = aggregateResult(result, childResult)
        }

        elm.parameters?.def?.forEach {
            val childResult = visitParameterDef(it!!, context)
            result = aggregateResult(result, childResult)
        }

        elm.contexts?.def?.forEach {
            val childResult = visitContextDef(it!!, context)
            result = aggregateResult(result, childResult)
        }

        elm.statements?.def?.forEach {
            val childResult = visitExpressionDef(it!!, context)
            result = aggregateResult(result, childResult)
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
    override fun visitUsingDef(elm: UsingDef, context: C): T? = visitFields(elm, context)

    /**
     * Visit a IncludeDef. This method will be called for every node in the tree that is a
     * IncludeDef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitIncludeDef(elm: IncludeDef, context: C): T? = visitFields(elm, context)

    /**
     * Visit a ContextDef. This method will be called for every node in the tree that is a
     * ContextDef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    override fun visitContextDef(elm: ContextDef, context: C): T? = visitFields(elm, context)
}
