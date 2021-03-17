package org.cqframework.cql.elm.visiting;

import org.hl7.elm.r1.*;
import org.hl7.elm.r1.Library.Statements;

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
        if(elm.getUsings() != null && elm.getUsings().getDef() != null && !elm.getUsings().getDef().isEmpty()) {
            elm.getUsings().getDef().stream().forEach(using -> visitElement(using, context));
        }
        if (elm.getIncludes() != null && elm.getIncludes().getDef() != null && !elm.getIncludes().getDef().isEmpty()) {
            elm.getIncludes().getDef().stream().forEach(include -> visitElement(include, context));
        }
        if (elm.getCodeSystems() != null && elm.getCodeSystems().getDef() != null && !elm.getCodeSystems().getDef().isEmpty()) {
            elm.getCodeSystems().getDef().stream().forEach(codeSystem -> visitElement(codeSystem, context));
        }
        if (elm.getValueSets() != null && elm.getValueSets().getDef() != null && !elm.getValueSets().getDef().isEmpty()) {
            elm.getValueSets().getDef().stream().forEach(valueset -> visitElement(valueset, context));
        }
        if (elm.getCodes() != null && elm.getCodes().getDef() != null && !elm.getCodes().getDef().isEmpty()) {
            elm.getCodes().getDef().stream().forEach(code -> visitElement(code, context));
        }
        if (elm.getConcepts() != null && elm.getConcepts().getDef() != null && !elm.getConcepts().getDef().isEmpty()) {
            elm.getConcepts().getDef().stream().forEach(concept -> visitElement(concept, context));
        }
        if (elm.getParameters() != null && elm.getParameters().getDef() != null && !elm.getParameters().getDef().isEmpty()) {
            elm.getParameters().getDef().stream().forEach(param -> visitElement(param, context));
        }
        if (elm.getContexts() != null && elm.getContexts().getDef() != null && !elm.getContexts().getDef().isEmpty()) {
            elm.getContexts().getDef().stream().forEach(contextDef -> visitElement(contextDef, context));
        }
        if (elm.getStatements() != null && elm.getStatements().getDef() != null && !elm.getStatements().getDef().isEmpty()) {
            visitStatements(elm.getStatements(), context);
        }
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

    public T visitStatements(Statements elm, C context) {
        int n = elm.getDef().size();
        for (int i=0; i<n; i++) {
            Element c = elm.getDef().get(i);
            if (c instanceof ExpressionDef) {
                visitElement((ExpressionDef)c, context);
            } else if (c instanceof ContextDef) {
                visitElement((ContextDef)c, context);
            } else if (c instanceof FunctionDef) {
                visitElement((FunctionDef)c, context);
            }
        }
        return null;
    }
}
