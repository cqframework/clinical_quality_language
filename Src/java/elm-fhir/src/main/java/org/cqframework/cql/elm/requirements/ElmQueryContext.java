package org.cqframework.cql.elm.requirements;

import java.util.*;
import java.util.List;
import org.hl7.elm.r1.*;

public class ElmQueryContext {
    public ElmQueryContext(VersionedIdentifier libraryIdentifier, Query query) {
        if (libraryIdentifier == null) {
            throw new IllegalArgumentException("libraryIdentifier is required");
        }
        if (query == null) {
            throw new IllegalArgumentException("query is required");
        }
        this.libraryIdentifier = libraryIdentifier;
        this.query = query;
        this.queryRequirements = new ElmConjunctiveRequirement(libraryIdentifier, query);
        this.queryRequirement = new ElmQueryRequirement(libraryIdentifier, query);
    }

    private VersionedIdentifier libraryIdentifier;
    private Query query;
    private ElmExpressionRequirement queryRequirements;
    private ElmQueryAliasContext definitionContext;
    private List<ElmQueryAliasContext> aliasContexts = new ArrayList<ElmQueryAliasContext>();
    private ElmQueryLetContext letDefinitionContext;
    private List<ElmQueryLetContext> letContexts = new ArrayList<ElmQueryLetContext>();
    private ElmQueryRequirement queryRequirement;

    public void enterLetDefinitionContext(LetClause letClause) {
        if (letDefinitionContext != null) {
            throw new IllegalArgumentException("Let clause definition already in progress");
        }
        letDefinitionContext = new ElmQueryLetContext(libraryIdentifier, letClause);
    }

    public ElmQueryLetContext exitLetDefinitionContext(ElmRequirement requirements) {
        if (letDefinitionContext == null) {
            throw new IllegalArgumentException("Let definition not in progress");
        }
        letContexts.add(letDefinitionContext);
        ElmQueryLetContext result = letDefinitionContext;
        result.setRequirements(requirements);
        letDefinitionContext = null;
        return result;
    }

    public ElmQueryLetContext resolveLet(String identifier) {
        for (ElmQueryLetContext letContext : letContexts) {
            if (letContext.getIdentifier().equals(identifier)) {
                return letContext;
            }
        }
        return null;
    }

    public ElmQueryLetContext getLetContext(LetClause letClause) {
        for (ElmQueryLetContext letContext : letContexts) {
            if (letContext.getLetClause() == letClause) {
                return letContext;
            }
        }
        return null;
    }

    public void enterAliasDefinitionContext(AliasedQuerySource querySource) {
        if (definitionContext != null) {
            throw new IllegalArgumentException("Alias definition already in progress");
        }
        definitionContext = new ElmQueryAliasContext(libraryIdentifier, querySource);
    }

    public ElmQueryAliasContext exitAliasDefinitionContext(ElmRequirement requirements) {
        if (definitionContext == null) {
            throw new IllegalArgumentException("Alias definition not in progress");
        }
        aliasContexts.add(definitionContext);
        ElmQueryAliasContext result = definitionContext;
        result.setRequirements(requirements);
        definitionContext = null;
        return result;
    }

    public ElmQueryAliasContext resolveAlias(String aliasName) {
        for (ElmQueryAliasContext aliasContext : aliasContexts) {
            if (aliasContext.getAlias().equals(aliasName)) {
                return aliasContext;
            }
        }

        return null;
    }

    private ElmQueryAliasContext getAliasContext(AliasedQuerySource querySource) {
        for (ElmQueryAliasContext aliasContext : aliasContexts) {
            if (aliasContext.getAlias().equals(querySource.getAlias())) {
                return aliasContext;
            }
        }

        return null;
    }

    public void descopeAlias(AliasedQuerySource querySource) {
        ElmQueryAliasContext aliasContext = getAliasContext(querySource);
        if (aliasContext != null) {
            aliasContexts.remove(aliasContext);
            queryRequirement.addDataRequirements(aliasContext.getRequirements());
        }
        aliasContexts.removeIf(x -> x.getAlias().equals(querySource.getAlias()));
    }

    public void reportQueryRequirements(ElmRequirement requirements) {
        if (requirements instanceof ElmExpressionRequirement) {
            queryRequirements = queryRequirements.combine((ElmExpressionRequirement) requirements);
        }
    }

    public ElmQueryRequirement getQueryRequirement(ElmRequirement childRequirements, ElmRequirementsContext context) {
        // Gather requirements from any lets in scope in the query
        for (ElmQueryLetContext letContext : letContexts) {
            queryRequirement.addDataRequirements(letContext.getRequirements());
        }

        // Gather requirements from any sources still in scope in the query
        for (ElmQueryAliasContext aliasContext : aliasContexts) {
            queryRequirement.addDataRequirements(aliasContext.getRequirements());
        }

        // add child requirements gathered during the context
        queryRequirement.addChildRequirements(childRequirements);

        // distribute query requirements to each alias
        if (context.getOptions().getAnalyzeDataRequirements()) {
            queryRequirement.distributeExpressionRequirement(queryRequirements, context);
        }

        return queryRequirement;
    }
}
