package org.cqframework.cql.elm.visiting;

import org.hl7.elm.r1.*;

/**
 * Created by Bryn on 4/14/2016.
 */
public class ElmBaseLibraryVisitor<T, C> extends ElmBaseClinicalVisitor<T, C> implements ElmLibraryVisitor<T, C> {

    /**
     * Visit an Element in an ELM tree. This method will be called for
     * every node in the tree that is a descendant of the Element type.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    @Override
    public T visitElement(Element elm, C context) {
        if (elm instanceof IncludeDef) return visitIncludeDef((IncludeDef)elm, context);
        else if (elm instanceof ContextDef) return visitContextDef((ContextDef)elm, context);
        else if (elm instanceof Library) return visitLibrary((Library)elm, context);
        else if (elm instanceof UsingDef) return visitUsingDef((UsingDef)elm, context);
        else return super.visitElement(elm, context);
    }

    /**
     * Visit a Library. This method will be called for
     * every node in the tree that is a Library.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitLibrary(Library elm, C context) {
        return null;
    }

    /**
     * Visit a UsingDef. This method will be called for
     * every node in the tree that is a UsingDef.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitUsingDef(UsingDef elm, C context) {
        return null;
    }

    /**
     * Visit a IncludeDef. This method will be called for
     * every node in the tree that is a IncludeDef.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitIncludeDef(IncludeDef elm, C context) {
        return null;
    }

    /**
     * Visit a ContextDef. This method will be called for
     * every node in the tree that is a ContextDef.
     *
     * @param elm     the ELM tree
     * @param context the context passed to the visitor
     * @return the visitor result
     */
    public T visitContextDef(ContextDef elm, C context) {
        return null;
    }
}
