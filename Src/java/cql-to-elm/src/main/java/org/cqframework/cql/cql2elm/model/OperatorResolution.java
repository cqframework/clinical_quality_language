package org.cqframework.cql.cql2elm.model;

import java.util.ArrayList;
import java.util.List;
import org.hl7.elm.r1.VersionedIdentifier;

public class OperatorResolution {
    public OperatorResolution() {}

    public OperatorResolution(Operator operator, Conversion[] conversions) {
        this.operator = operator;
        if (conversions != null) {
            setConversions(conversions);
        }
    }

    private Operator operator;

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    private boolean allowFluent = false;

    public boolean getAllowFluent() {
        return allowFluent;
    }

    public void setAllowFluent(boolean allowFluent) {
        this.allowFluent = allowFluent;
    }

    /*
    The versioned identifier (fully qualified, versioned, library identifier of the library in which the resolved operator
    is defined. This is set by the library resolution to allow the calling context to understand the defined location
    of the resolved operator.
     */
    private VersionedIdentifier libraryIdentifier;

    public VersionedIdentifier getLibraryIdentifier() {
        return libraryIdentifier;
    }

    public void setLibraryIdentifier(VersionedIdentifier libraryIdentifier) {
        this.libraryIdentifier = libraryIdentifier;
    }

    /*
    The local alias for the resolved library. This is set by the libraryBuilder to allow the invocation
    to set the library alias if necessary.
     */
    private String libraryName;

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    private void ensureConversions() {
        if (this.conversions == null) {
            this.conversions = new ArrayList<>();
        } else {
            this.conversions.clear();
        }
    }

    private List<Conversion> conversions;

    public Iterable<Conversion> getConversions() {
        return conversions;
    }

    public void setConversions(Iterable<Conversion> conversions) {
        ensureConversions();
        for (Conversion conversion : conversions) {
            this.conversions.add(conversion);
        }
    }

    public void setConversions(Conversion[] conversions) {
        ensureConversions();
        for (int i = 0; i < conversions.length; i++) {
            this.conversions.add(conversions[i]);
        }
    }

    public boolean hasConversions() {
        return this.conversions != null;
    }

    private boolean operatorHasOverloads = false;

    public boolean getOperatorHasOverloads() {
        return operatorHasOverloads;
    }

    public void setOperatorHasOverloads() {
        operatorHasOverloads = true;
    }

    private int score;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
