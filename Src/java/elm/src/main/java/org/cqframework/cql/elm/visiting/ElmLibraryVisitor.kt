package org.cqframework.cql.elm.visiting

import org.hl7.elm.r1.ContextDef
import org.hl7.elm.r1.IncludeDef
import org.hl7.elm.r1.Library
import org.hl7.elm.r1.UsingDef

/**
 * This interface defines a complete generic visitor for an Elm tree
 *
 * @param <T> The return type of the visit operation. Use [Void] for
 * @param <C> The type of context passed to each visit method operations with no return type.
 *   </C></T>
 */
@Suppress("TooManyFunctions")
interface ElmLibraryVisitor<T, C> : ElmClinicalVisitor<T, C> {
    /**
     * Visit a Library. This method will be called for every node in the tree that is a Library.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitLibrary(elm: Library, context: C): T?

    /**
     * Visit a UsingDef. This method will be called for every node in the tree that is a UsingDef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitUsingDef(elm: UsingDef, context: C): T?

    /**
     * Visit a IncludeDef. This method will be called for every node in the tree that is a
     * IncludeDef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitIncludeDef(elm: IncludeDef, context: C): T?

    /**
     * Visit a ContextDef. This method will be called for every node in the tree that is a
     * ContextDef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    fun visitContextDef(elm: ContextDef, context: C): T?
}
