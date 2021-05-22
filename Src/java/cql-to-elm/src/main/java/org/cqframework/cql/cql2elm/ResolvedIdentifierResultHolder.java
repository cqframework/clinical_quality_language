package org.cqframework.cql.cql2elm;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Resolved identifiers is as simple class to maintain a collection of various matches after identifier resolution
 */
public class ResolvedIdentifierResultHolder {

    //collection of every match made within this method
    private Pair<String, Object> caseMatchedObject;

    //subsequent case matched resolutions maintained (if equals occurs more than once, excess matches stored in a 'hidden match' collection)
    //different from caseMatchCollection in structure, must be formatted at end of method.
    private List<Pair<String, Object>> hiddenCaseMatchCollection;

    //every match made where equals is false but equalsIgnoreCase is true.
    private List<Pair<String, Object>> caseIgnoredCollection;

    public Pair<String, Object> getCaseMatchedObject() {
        return caseMatchedObject;
    }

    /**
     * When a match occurs, we set the case matched object.  However, if one exists, it means
     * a match occurred subsequent to an initial match and hiding has occurred.
     *
     * @param identifier
     * @param resolvedIdentifier
     */
    public void setCaseMatchedObject(String identifier, Object resolvedIdentifier) {
        if (this.caseMatchedObject != null) {
            //case matched object already exists in this instance, add to the hidden match collection
            if (this.hiddenCaseMatchCollection == null) {
                this.hiddenCaseMatchCollection = new ArrayList<>();
            }
            this.hiddenCaseMatchCollection.add(new ImmutablePair<>(identifier, resolvedIdentifier));
        } else {
            this.caseMatchedObject = new ImmutablePair<>(identifier, resolvedIdentifier);
        }
    }

    public void addCaseIgnoredMatch(String identifier, Object caseIgnoredMatch) {
        if (this.caseIgnoredCollection == null) {
            this.caseIgnoredCollection = new ArrayList<>();
        }
        this.caseIgnoredCollection.add(new ImmutablePair<>(identifier, caseIgnoredMatch));

    }

    public void addAllCaseIgnored(List<Pair<String, Object>> input) {
        if (caseIgnoredCollection == null) {
            caseIgnoredCollection = new ArrayList<>();
        }
        caseIgnoredCollection.addAll(input);
    }

    public void addAllHidden(List<Pair<String, Object>> input) {
        if (hiddenCaseMatchCollection == null) {
            hiddenCaseMatchCollection = new ArrayList<>();
        }
        hiddenCaseMatchCollection.addAll(input);
    }

    public List<Pair<String, Object>> getHiddenCaseMatchCollection() {
        return hiddenCaseMatchCollection;
    }

    public List<Pair<String, Object>> getCaseIgnoredCollection() {
        return caseIgnoredCollection;
    }

    public void absorb(ResolvedIdentifierResultHolder ri) {
        if (ri.getHiddenCaseMatchCollection() != null) {
            this.addAllHidden(ri.getHiddenCaseMatchCollection());
        }

        if (ri.getCaseIgnoredCollection() != null) {
            this.addAllCaseIgnored(ri.getCaseIgnoredCollection());
        }

        if (ri.getCaseMatchedObject() != null) {
            this.setCaseMatchedObject(ri.getCaseMatchedObject().getLeft(), ri.getCaseMatchedObject().getRight());
        }
    }
}