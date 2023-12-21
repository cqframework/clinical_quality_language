package org.cqframework.cql.elm.requirements;

import org.hl7.elm.r1.*;

public class ElmQueryLetContext {
    public ElmQueryLetContext(VersionedIdentifier libraryIdentifier, LetClause letClause) {
        if (libraryIdentifier == null) {
            throw new IllegalArgumentException("libraryIdentifier is required");
        }
        if (letClause == null) {
            throw new IllegalArgumentException("letClause is required");
        }
        this.libraryIdentifier = libraryIdentifier;
        this.letClause = letClause;
    }

    private VersionedIdentifier libraryIdentifier;
    private LetClause letClause;

    public LetClause getLetClause() {
        return letClause;
    }

    public String getIdentifier() {
        return letClause.getIdentifier();
    }

    private ElmDataRequirement requirements;

    public ElmDataRequirement getRequirements() {
        return requirements;
    }

    public void setRequirements(ElmRequirement requirements) {
        if (requirements instanceof ElmDataRequirement) {
            this.requirements = (ElmDataRequirement) requirements;
        } else if (requirements instanceof ElmExpressionRequirement) {
            this.requirements = ElmDataRequirement.inferFrom((ElmExpressionRequirement) requirements);
        } else {
            // Should never land here, but defensively...
            this.requirements = new ElmDataRequirement(this.libraryIdentifier, new Retrieve());
        }
        this.requirements.setQuerySource(getLetClause());
    }

    public void reportProperty(ElmPropertyRequirement propertyRequirement) {
        requirements.reportProperty(propertyRequirement);
    }
}
