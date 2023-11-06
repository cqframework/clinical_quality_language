package org.cqframework.cql.cql2elm.model;

import org.cqframework.cql.cql2elm.LibraryBuilder;
import org.hl7.elm.r1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Basic utility for maintaining a list of objects representing resolved identifiers, categorized by case sensitive matching
 */
public class ResolvedIdentifierList {
    private static final Logger logger = LoggerFactory.getLogger(ResolvedIdentifierList.class);

    private final List<ResolvedIdentifier> list;
    private final boolean hasEnclosingFunctionWithSameName;

    public static ResolvedIdentifierList outer(boolean hasEnclosingFunctionWithSameName) {
        return new ResolvedIdentifierList(hasEnclosingFunctionWithSameName, new ArrayList<>());
    }

    public static ResolvedIdentifierList inner() {
        return new ResolvedIdentifierList(false, new ArrayList<>());
    }

    public static ResolvedIdentifierList copy(ResolvedIdentifierList resolvedIdentifierList, ResolvedIdentifier toRemove) {
        final List<ResolvedIdentifier> resolvedIdentifiersCopy = new ArrayList<>(resolvedIdentifierList.getResolvedIdentifierList());
        resolvedIdentifiersCopy.remove(toRemove);
        return new ResolvedIdentifierList(resolvedIdentifierList.hasEnclosingFunctionWithSameName, resolvedIdentifiersCopy);
    }

    private ResolvedIdentifierList(boolean hasEnclosingFunctionWithSameName, List<ResolvedIdentifier> list) {
        this.hasEnclosingFunctionWithSameName = hasEnclosingFunctionWithSameName;
        this.list = list;
    }

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
    // LUKETODO:  this "shiftFirst" algorithm is fraught with race conditions and needs to be replaced with something more immutable like "returnRemainder"
    // LUKETODO:  add the identifier name as a field for debugging purposes
    // LUKETODO:  move the whole "check for dupes" logic in here
    // LUKETODO:  we want a requirement like the following:
    // 1) Check for dupes with EXACT matches
    // 2) Also check if we duplicate the enclosing expression name
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
            remainders = Collections.unmodifiableList(tempList);
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
                    logger.info("resolvedIdentifierList 2: {}", getResolvedIdentifierList().stream().map(match -> "[" + match.getIdentifier() + "] " + match.getResolvedElement().getClass() + " " + match.getMatchType()).collect(Collectors.toSet()));
                    logger.info("allHiddenCaseMatches: {}", remainders.stream().map(match -> "[" + match.getIdentifier() + "] " + match.getResolvedElement().getClass() + " " + match.getMatchType()).collect(Collectors.toSet()));
//                }

            checkForDupes(firstCaseMatch, libraryBuilder);
            //return first match:
            return (Expression) firstCaseMatch.getResolvedElement();
        } else if (mustResolve) {
            // ERROR:
            throw new IllegalArgumentException(String.format("Could not resolve identifier %s in the current library.", identifier));
        }

//        if (id != null) {
//            if (id.contains("Test") && ! identifier.contains("Test")) {
                logger.info("returning null");
//            }
//        }

        return null;
    }

    public void checkForDupes(ResolvedIdentifier firstCaseMatch, LibraryBuilder libraryBuilder) {
        //issue warning that multiple matches occurred:
        if (hasEnclosingFunctionWithSameName) {
            final List<ResolvedIdentifier> filtered = list.stream()
                    // LUKETODO:  consider filtering out other "Ref"s
                    .filter(match -> !(match.getResolvedElement() instanceof OperandRef))
                    .filter(match -> !(match.getResolvedElement() instanceof ValueSetRef))
                    .collect(Collectors.toList());
            if (!filtered.isEmpty()) {
                libraryBuilder.reportWarning("Identifier hiding detected: " +
                                "Identifier" + (filtered.size() > 1 ? "s" : "") + " in a broader scope hidden: " +
                                libraryBuilder.formatMatchedMessage(filtered),
                        (Expression) firstCaseMatch.getResolvedElement());
            }
        } else if (! remainders.isEmpty()) {
            final List<ResolvedIdentifier> filtered = remainders.stream()
                    // LUKETODO:  consider filtering out other "Ref"s
                    .filter(match -> !(match.getResolvedElement() instanceof OperandRef))
                    // LUKETODO:  this makes the ValueSet hiding use case fail
//                    .filter(match -> !(match.getResolvedElement() instanceof ValueSetRef))
                    .collect(Collectors.toList());

            // LUKETODO:  what about "NONE"?
            // LUKETODO:  unite the two into a single warning block
            final List<ResolvedIdentifier> caseInsensitiveMatches =
                    filtered.stream()
                            .filter(match -> MatchType.CASE_IGNORED == match.getMatchType())
                            .collect(Collectors.toList());
            final List<ResolvedIdentifier> exactIdentifierMatches =
                    filtered.stream()
                            .filter(match -> MatchType.EXACT == match.getMatchType())
                            .collect(Collectors.toList());

            if (!caseInsensitiveMatches.isEmpty()) {
                libraryBuilder.reportWarning("Case insensitive clashes detected: " +
                                "Identifier" + (caseInsensitiveMatches.size() > 1 ? "s" : "") + " for identifiers: " +
                                libraryBuilder.formatMatchedMessage(caseInsensitiveMatches),
                        (Expression) firstCaseMatch.getResolvedElement());

            }

            if (!exactIdentifierMatches.isEmpty()) {
                libraryBuilder.reportWarning("Identifier hiding detected: " +
                                "Identifier" + (exactIdentifierMatches.size() > 1 ? "s" : "") + " in a broader scope hidden: " +
                                libraryBuilder.formatMatchedMessage(exactIdentifierMatches),
                        (Expression) firstCaseMatch.getResolvedElement());

            }
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
