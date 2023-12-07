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
        T result = defaultResult(elm, context);
        if (elm.getUsings() != null && elm.getUsings().getDef() != null && !elm.getUsings().getDef().isEmpty()) {
            for (UsingDef def : elm.getUsings().getDef()) {
                T childResult = visitElement(def, context);
                result = aggregateResult(result, childResult);
            }
        }
        if (elm.getIncludes() != null && elm.getIncludes().getDef() != null && !elm.getIncludes().getDef().isEmpty()) {
            for (IncludeDef def : elm.getIncludes().getDef()) {
                T childResult = visitElement(def, context);
                result = aggregateResult(result, childResult);
            }
        }
        if (elm.getCodeSystems() != null && elm.getCodeSystems().getDef() != null && !elm.getCodeSystems().getDef().isEmpty()) {
            for (CodeSystemDef def : elm.getCodeSystems().getDef()) {
                T childResult = visitElement(def, context);
                result = aggregateResult(result, childResult);
            }
        }
        if (elm.getValueSets() != null && elm.getValueSets().getDef() != null && !elm.getValueSets().getDef().isEmpty()) {
            for (ValueSetDef def : elm.getValueSets().getDef()) {
                T childResult = visitElement(def, context);
                result = aggregateResult(result, childResult);
            }
        }
        if (elm.getCodes() != null && elm.getCodes().getDef() != null && !elm.getCodes().getDef().isEmpty()) {
            for (CodeDef def : elm.getCodes().getDef()) {
                T childResult = visitElement(def, context);
                result = aggregateResult(result, childResult);
            }
        }
        if (elm.getConcepts() != null && elm.getConcepts().getDef() != null && !elm.getConcepts().getDef().isEmpty()) {
            for (ConceptDef def : elm.getConcepts().getDef()) {
                T childResult = visitElement(def, context);
                result = aggregateResult(result, childResult);
            }
        }
        if (elm.getParameters() != null && elm.getParameters().getDef() != null && !elm.getParameters().getDef().isEmpty()) {
            for (ParameterDef def : elm.getParameters().getDef()) {
                T childResult = visitElement(def, context);
                result = aggregateResult(result, childResult);
            }
        }
        if (elm.getContexts() != null && elm.getContexts().getDef() != null && !elm.getContexts().getDef().isEmpty()) {
            for (ContextDef def : elm.getContexts().getDef()) {
                T childResult = visitElement(def, context);
                result = aggregateResult(result, childResult);
            }
        }
        if (elm.getStatements() != null && elm.getStatements().getDef() != null && !elm.getStatements().getDef().isEmpty()) {
            for (ExpressionDef def : elm.getStatements().getDef()) {
                T childResult = visitElement(def, context);
                result = aggregateResult(result, childResult);
            }
        }
        return result;
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
        return defaultResult(elm, context);
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
        return defaultResult(elm, context);
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
        return defaultResult(elm, context);
    }
}
