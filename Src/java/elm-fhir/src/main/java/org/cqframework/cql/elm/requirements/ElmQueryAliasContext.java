package org.cqframework.cql.elm.requirements;

import org.hl7.elm.r1.AliasedQuerySource;
import org.hl7.elm.r1.Property;
import org.hl7.elm.r1.Retrieve;
import org.hl7.elm.r1.VersionedIdentifier;

public class ElmQueryAliasContext {
    public ElmQueryAliasContext(VersionedIdentifier libraryIdentifier, AliasedQuerySource querySource) {
        if (libraryIdentifier == null) {
            throw new IllegalArgumentException("libraryIdentifier is required");
        }
        if (querySource == null) {
            throw new IllegalArgumentException("querySource is required");
        }
        this.libraryIdentifier = libraryIdentifier;
        this.querySource = querySource;
    }

    private VersionedIdentifier libraryIdentifier;
    private AliasedQuerySource querySource;
    public AliasedQuerySource getQuerySource() {
        return querySource;
    }
    public String getAlias() {
        return querySource.getAlias();
    }

    private ElmDataRequirement requirements;
    public ElmDataRequirement getRequirements() {
        return requirements;
    }
    public void setRequirements(ElmRequirement requirements) {
        if (requirements instanceof ElmDataRequirement) {
            this.requirements = (ElmDataRequirement)requirements;
        }
        else if (requirements instanceof ElmExpressionRequirement) {
            this.requirements = ElmDataRequirement.inferFrom((ElmExpressionRequirement)requirements);
        }
        else {
            // Should never land here, but defensively...
            this.requirements = new ElmDataRequirement(this.libraryIdentifier, new Retrieve());
        }
        this.requirements.setQuerySource(getQuerySource());
    }

    public void reportProperty(ElmPropertyRequirement propertyRequirement) {
        requirements.reportProperty(propertyRequirement);
    }
}
