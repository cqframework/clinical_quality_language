package org.cqframework.cql.elm.visiting;

import org.hl7.elm.r1.*;

/**
 * This interface defines a complete generic visitor for an Elm tree
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * @param <C> The type of context passed to each visit method
 * operations with no return type.
 */
public interface ElmLibraryVisitor<T, C> extends ElmClinicalVisitor<T, C> {
    /**
     * Visit a Library. This method will be called for
     * every node in the tree that is a Library.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitLibrary(Library elm, C context);

    /**
     * Visit a UsingDef. This method will be called for
     * every node in the tree that is a UsingDef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitUsingDef(UsingDef elm, C context);

    /**
     * Visit a IncludeDef. This method will be called for
     * every node in the tree that is a IncludeDef.
     *
     * @param elm the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    T visitIncludeDef(IncludeDef elm, C context);
}
