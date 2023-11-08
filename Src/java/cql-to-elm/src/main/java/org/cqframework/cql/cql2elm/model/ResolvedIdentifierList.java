package org.cqframework.cql.cql2elm.model;

import org.cqframework.cql.cql2elm.LibraryBuilder;
import org.hl7.elm.r1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Basic utility for maintaining a list of objects representing resolved identifiers, categorized by case sensitive matching
 */
public class ResolvedIdentifierList {
    // LUKETODO:  either turn this into a stack or make use of a stack
    private static final Logger logger = LoggerFactory.getLogger(ResolvedIdentifierList.class);

    private final List<ResolvedIdentifier> list = new ArrayList<>();

    public List<ResolvedIdentifier> getResolvedIdentifierList() {
        return this.list;
    }

    public void addResolvedIdentifier(ResolvedIdentifier ri) {
        this.list.add(ri);
    }

    /**
     * Establish match type and add new instance of ResolvedIdentifier, regardless of case matching
     *
     * @param identifier         Visible identifier name to use
     * @param val                Value of identifier used in case matching
     * @param checkVal           Value to be compared against
     * @param resolvedIdentifier Object to record to the list
     * @return resulting MatchType
     */
    public MatchType addResolvedIdentifier(String identifier, String val, String checkVal, Object resolvedIdentifier) {
        MatchType matchType = MatchType.resolveMatchType(val, checkVal);
        this.list.add(new ResolvedIdentifier(identifier, matchType, resolvedIdentifier));
        return matchType;
    }

    /**
     * Match type set to EXACT and new instance of ResolvedIdentifier added to list
     *
     * @param identifier identifier of resolved element
     * @param resolvedElement resolved element
     */
    public void addResolvedIdentifier(String identifier, MatchType mt, Object resolvedElement) {
        this.list.add(new ResolvedIdentifier(identifier, mt, resolvedElement));
    }

    /**
     * Match type set to EXACT and new instance of ResolvedIdentifier added to list
     *
     * @param identifier identifier of resolved element
     * @param resolvedElement resolved element
     */
    public void addExactMatchIdentifier(String identifier, Object resolvedElement) {
        this.list.add(new ResolvedIdentifier(identifier, MatchType.EXACT, resolvedElement));
    }

    private ResolvedIdentifier shiftedFirstInstance;
    private List<ResolvedIdentifier> remainders;

    /**
     * Returns first instance in the list where MatchType is EXACT, and removes it from the list
     *
     * @return ResolvedIdentifier
     */
    // LUKETODO:  add the identifier name as a field for debugging purposes
    public ResolvedIdentifier shiftFirstInstanceOfExactMatch() {
        if (shiftedFirstInstance != null && remainders != null) {
            return shiftedFirstInstance;
        }

        final ResolvedIdentifier first = this.list.stream()
                .filter(s -> s.getMatchType().equals(MatchType.EXACT))
                .findFirst()
                .orElse(null);

        if (first != null) {
            shiftedFirstInstance = new ResolvedIdentifier(first.getIdentifier(), first.getMatchType(), first.getResolvedElement());
            final List<ResolvedIdentifier> tempList = new ArrayList<>(list);
            tempList.remove(shiftedFirstInstance);
            remainders = new ArrayList<>(tempList);
            return shiftedFirstInstance;
        }

        return null;
    }

    public Expression checkForDupesOuter(String identifier, boolean mustResolve, LibraryBuilder libraryBuilder) {
        //shift the first instance of MatchType.EXACT, which will be what is returned.  The remainder of the list
        //is looked at to determine if hidden identifiers were found.
        final ResolvedIdentifier firstCaseMatch = shiftFirstInstanceOfExactMatch();

        if (firstCaseMatch != null) {

            //getAllMatchedIdentifiers returns any recorded identifier where MatchType is not NONE
//                if (id.contains("Authoring") || id.contains("Hidden") || id.contains("Test")) {
//                    logger.info("resolvedIdentifierList 2: {}", getResolvedIdentifierList().stream().map(match -> "[" + match.getIdentifier() + "] " + match.getResolvedElement().getClass() + " " + match.getMatchType()).collect(Collectors.toSet()));
//                    logger.info("allHiddenCaseMatches: {}", remainders.stream().map(match -> "[" + match.getIdentifier() + "] " + match.getResolvedElement().getClass() + " " + match.getMatchType()).collect(Collectors.toSet()));
//                }

            checkForDupes(firstCaseMatch, libraryBuilder);
            //return first match:
            final Expression resolvedElement = (Expression) firstCaseMatch.getResolvedElement();

            if (remainders != null) {
                remainders.clear();
            }

            list.clear();
            shiftedFirstInstance = null;

            return resolvedElement;
        } else if (mustResolve) {
            // ERROR:
            throw new IllegalArgumentException(String.format("Could not resolve identifier %s in the current library.", identifier));
        }

//        if (id != null) {
//            if (id.contains("Test") && ! identifier.contains("Test")) {
//                logger.info("returning null");
//            }
//        }

        // LUKETODO:  we need to record an identifier as "returned" so it's no longer considered even if
        if (remainders != null) {
            remainders.clear();
        }
        list.clear();
        shiftedFirstInstance = null;

        return null;
    }

    public void checkForDupes(ResolvedIdentifier firstCaseMatch, LibraryBuilder libraryBuilder) {
        //issue warning that multiple matches occurred:
        if (! remainders.isEmpty()) {
            /*
            int i = 0;
            for (int j = 0; j < 10; j++) {
               final int i = j; // inner variable within for
            }
            final int i = 0;
             */

            libraryBuilder.warnOnHiding(firstCaseMatch, remainders);
        }
    }

    /**
     * Combine list from another instance of ResolvedIdentifierList
     *
     * @param m instance of ResolvedIdentifierList
     */
    public void addAllResolvedIdentifiers(ResolvedIdentifierList m) {
        this.list.addAll(m.getResolvedIdentifierList());
    }

    /**
     * Returns first instance in the list where MatchType is EXACT.  List is ordered in first come first serve basis where first EXACT match is what is ultimately used.
     *
     * @return ResolvedIdentifier
     */
    public ResolvedIdentifier getFirstInstanceOfExactMatch() {
        return this.list.stream()
                .filter(s -> s.getMatchType().equals(MatchType.EXACT))
                .findFirst()
                .orElse(null);

    }

    /**
     * Returns list of ResolvedIdentifiers where MatchType isn't NONE
     * @return List of ResolvedIdentifier
     */
    public List<ResolvedIdentifier> getAllMatchedIdentifiers() {
        return this.list.stream()
                .filter(s -> !s.getMatchType().equals(MatchType.NONE))
                .collect(Collectors.toList());
    }

    private boolean canResolveToExpression(Object in) {
        try {
            Expression e = (Expression) in;
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @return Number of ResolvedIdentifiers
     */
    public int size() {
        return this.list.size();
    }
}
