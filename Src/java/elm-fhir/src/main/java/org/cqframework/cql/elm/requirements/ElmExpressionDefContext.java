package org.cqframework.cql.elm.requirements;

import org.hl7.elm.r1.ExpressionDef;
import org.hl7.elm.r1.Query;
import org.hl7.elm.r1.VersionedIdentifier;

import java.util.Stack;

public class ElmExpressionDefContext {
    public ElmExpressionDefContext(VersionedIdentifier libraryIdentifier, ExpressionDef expressionDef) {
        if (libraryIdentifier == null) {
            throw new IllegalArgumentException("libraryIdentifier required");
        }
        this.libraryIdentifier = libraryIdentifier;

        if (expressionDef == null) {
            throw new IllegalArgumentException("expressionDef requirement");
        }
        this.expressionDef = expressionDef;
        this.reportedRequirements = new ElmRequirements(libraryIdentifier, expressionDef);
    }

    private VersionedIdentifier libraryIdentifier;
    public VersionedIdentifier getLibraryIdentifier() {
        return this.libraryIdentifier;
    }

    private ExpressionDef expressionDef;
    public ExpressionDef getExpressionDef() {
        return this.expressionDef;
    }

    private ElmRequirements reportedRequirements;
    public ElmRequirements getReportedRequirements() {
        return this.reportedRequirements;
    }

    public void reportRequirement(ElmRequirement requirement) {
        this.reportedRequirements.reportRequirement(requirement);
    }

    private Stack<ElmQueryContext> queryStack = new Stack<ElmQueryContext>();
    public void enterQueryContext(Query query) {
        queryStack.push(new ElmQueryContext(libraryIdentifier, query));
    }
    public ElmQueryContext exitQueryContext() {
        ElmQueryContext queryContext = queryStack.pop();
        return queryContext;
    }
    public ElmQueryContext getCurrentQueryContext() {
        if (queryStack.empty()) {
            throw new IllegalArgumentException("Not in a query context");
        }

        return queryStack.peek();
    }
    public boolean inQueryContext() {
        return !queryStack.empty();
    }

    public ElmQueryLetContext resolveLet(String letName) {
        ElmQueryLetContext letContext = null;
        for (ElmQueryContext queryContext : queryStack) {
            letContext = queryContext.resolveLet(letName);
            if (letContext != null) {
                break;
            }
        }

        if (letContext == null) {
            throw new IllegalArgumentException(String.format("Could not resolve let %s", letName));
        }

        return letContext;
    }

    public ElmQueryAliasContext resolveAlias(String aliasName) {
        ElmQueryAliasContext aliasContext = null;
        for (ElmQueryContext queryContext : queryStack) {
            aliasContext = queryContext.resolveAlias(aliasName);
            if (aliasContext != null) {
                break;
            }
        }

        if (aliasContext == null) {
            throw new IllegalArgumentException(String.format("Could not resolve alias %s", aliasName));
        }

        return aliasContext;
    }
}
