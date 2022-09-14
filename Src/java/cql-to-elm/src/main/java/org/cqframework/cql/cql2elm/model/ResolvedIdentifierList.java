package org.cqframework.cql.cql2elm.model;

import org.hl7.elm.r1.Expression;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Basic utility for maintaining a list of objects representing resolved identifiers, categorized by case sensitive matching
 */
public class ResolvedIdentifierList {

    private List<ResolvedIdentifier> list;

    public ResolvedIdentifierList() {
        this.list = new ArrayList<>();
    }

    public List<ResolvedIdentifier> getList() {
        return this.list;
    }

    public void add(ResolvedIdentifier ri) {
        this.list.add(ri);
    }

    /**
     * Establish match type and add new instance of ResolvedIdentifier
     *
     * @param identifier         Visible identifier name to use
     * @param val                Value of identifier used in case matching
     * @param checkVal           Value to be compared against
     * @param resolvedIdentifier Object to record to the list
     */
    public void add(String identifier, String val, String checkVal, Object resolvedIdentifier) {
        this.list.add(new ResolvedIdentifier(identifier, MatchType.checkMatch(val, checkVal), resolvedIdentifier));
    }

    /**
     * Match type set to EXACT and new instance of ResolvedIdentifier added to list
     *
     * @param identifier
     * @param resolvedElement
     */
    public void addExactMatch(String identifier, Object resolvedElement) {
        this.list.add(new ResolvedIdentifier(identifier, MatchType.EXACT, resolvedElement));
    }

    /**
     * Combine list from another instance of ResolvedIdentifierList
     *
     * @param m
     */
    public void addAll(ResolvedIdentifierList m) {
        this.list.addAll(m.getList());
    }

    /**
     * Returns first instance in the list where MatchType is EXACT.  List is ordered in first come first serve basis where first EXACT match is what is ultimately used.
     *
     * @return
     */
    public ResolvedIdentifier getFirstCaseMatch() {
        return this.list.stream()
                .filter(s -> s.getMatchType().equals(MatchType.EXACT))
                .findFirst()
                .get();
    }

    /**
     * Returns first instance in list where MatchType is EXACT and the object can resolve to an Expression.
     *
     * @return
     */
    public ResolvedIdentifier getFirstCaseMatchExpression() {
        return this.list.stream()
                .filter(s -> s.getMatchType().equals(MatchType.EXACT) && canResolveToExpression(s.getResolvedElement()))
                .findFirst()
                .get();
    }

    /**
     * @param matchType
     * @return
     */
    public List<ResolvedIdentifier> getAllMatchesByType(MatchType matchType) {
        return this.list.stream()
                .filter(s -> s.getMatchType().equals(matchType))
                .collect(Collectors.toList());
    }

    /**
     * @param in
     * @return
     */
    private boolean canResolveToExpression(Object in) {
        try {
            Expression e = (Expression) in;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @return
     */
    public int size() {
        return this.list.size();
    }
}
