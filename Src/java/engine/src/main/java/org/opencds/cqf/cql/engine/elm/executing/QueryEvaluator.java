package org.opencds.cqf.cql.engine.elm.executing;

import org.cqframework.cql.elm.visiting.ElmLibraryVisitor;
import org.hl7.elm.r1.*;
import org.opencds.cqf.cql.engine.exception.CqlException;
import org.opencds.cqf.cql.engine.execution.State;
import org.opencds.cqf.cql.engine.execution.Variable;
import org.opencds.cqf.cql.engine.runtime.CqlList;
import org.opencds.cqf.cql.engine.runtime.Tuple;
import org.opencds.cqf.cql.engine.runtime.iterators.QueryIterator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class QueryEvaluator {

    @SuppressWarnings("unchecked")
    public static Iterable<Object> ensureIterable(Object source) {
        if (source instanceof Iterable) {
            return (Iterable<Object>) source;
        } else {
            ArrayList<Object> sourceList = new ArrayList<>();
            if (source != null)
                sourceList.add(source);
            return sourceList;
        }
    }

    private static void evaluateLets(Query elm, State state, List<Variable> letVariables,
            ElmLibraryVisitor<Object, State> visitor) {
        for (int i = 0; i < elm.getLet().size(); i++) {
            letVariables.get(i).setValue(visitor.visitExpression(elm.getLet().get(i).getExpression(), state));
        }
    }

    private static boolean evaluateRelationships(Query elm, State state, ElmLibraryVisitor<Object, State> visitor) {
        // TODO: This is the most naive possible implementation here, but it should
        // perform okay with 1) caching and 2) small data sets
        boolean shouldInclude = true;
        for (org.hl7.elm.r1.RelationshipClause relationship : elm.getRelationship()) {
            boolean hasSatisfyingData = false;
            Iterable<Object> relatedSourceData = ensureIterable(
                    visitor.visitExpression(relationship.getExpression(), state));
            for (Object relatedElement : relatedSourceData) {
                state.push(new Variable().withName(relationship.getAlias()).withValue(relatedElement));
                try {
                    Object satisfiesRelatedCondition = visitor.visitExpression(relationship.getSuchThat(), state);
                    if ((relationship instanceof org.hl7.elm.r1.With
                            || relationship instanceof org.hl7.elm.r1.Without)
                            && Boolean.TRUE.equals(satisfiesRelatedCondition)) {
                        hasSatisfyingData = true;
                        break; // Once we have detected satisfying data, no need to continue testing
                    }
                } finally {
                    state.pop();
                }
            }

            if ((relationship instanceof org.hl7.elm.r1.With && !hasSatisfyingData)
                    || (relationship instanceof org.hl7.elm.r1.Without && hasSatisfyingData)) {
                shouldInclude = false;
                break; // Once we have determined the row should not be included, no need to continue
                       // testing other related information
            }
        }

        return shouldInclude;
    }

    private static boolean evaluateWhere(Query elm, State state, ElmLibraryVisitor<Object, State> visitor) {
        if (elm.getWhere() != null) {
            Object satisfiesCondition = visitor.visitExpression(elm.getWhere(), state);
            if (!(satisfiesCondition instanceof Boolean && (Boolean) satisfiesCondition)) {
                return false;
            }
        }

        return true;
    }

    private static List<Object> evaluateAggregate(AggregateClause elm, State state, ElmLibraryVisitor<Object, State> visitor, List<Object> elements) {
        return Collections.singletonList(AggregateClauseEvaluator.aggregate(elm, state, visitor, elements));
    }

    private static Object constructTuple(State state, List<Variable> variables) {
        var elementMap = new LinkedHashMap<String, Object>();
        for (var v : variables) {
            elementMap.put(v.getName(), v.getValue());
        }

        return new Tuple(state).withElements(elementMap);
    }

    public static void sortResult(Query elm, List<Object> result, State state, String alias,
            ElmLibraryVisitor<Object, State> visitor) {

        SortClause sortClause = elm.getSort();

        if (sortClause != null) {

            for (SortByItem byItem : sortClause.getBy()) {

                if (byItem instanceof ByExpression) {
                    result.sort(
                            new CqlList(state, visitor, alias, ((ByExpression) byItem).getExpression()).expressionSort);
                } else if (byItem instanceof ByColumn) {
                    result.sort(new CqlList(state, ((ByColumn) byItem).getPath()).columnSort);
                } else {
                    result.sort(new CqlList().valueSort);
                }

                String direction = byItem.getDirection().value();
                if (direction.equals("desc") || direction.equals("descending")) {
                    java.util.Collections.reverse(result);
                }
            }
        }
    }

    static class QuerySource {
        private String alias;
        private boolean isList;
        private Iterable<Object> data;

        public QuerySource(String alias, Object data) {
            this.alias = alias;
            this.isList = data instanceof Iterable;
            this.data = ensureIterable(data);
        }

        public String getAlias() {
            return alias;
        }

        public boolean getIsList() {
            return isList;
        }

        public Iterable<Object> getData() {
            return data;
        }
    }

    @SuppressWarnings("unchecked")
    public static Object internalEvaluate(Query elm, State state, ElmLibraryVisitor<Object, State> visitor) {
        if (elm.getAggregate() != null && elm.getReturn() != null) {
            throw new CqlException("aggregate and return are mutually exclusive");
        }

        var sources = new ArrayList<Iterator<Object>>();
        var variables = new ArrayList<Variable>();
        var letVariables = new ArrayList<Variable>();
        List<Object> result = new ArrayList<>();
        boolean sourceIsList = false;
        int pushCount = 0;
        try {
            for (AliasedQuerySource source : elm.getSource()) {
                Object obj = visitor.visitExpression(source.getExpression(), state);
                QuerySource querySource = new QuerySource(source.getAlias(), obj);
                sources.add(querySource.getData().iterator());
                if (querySource.getIsList()) {
                    sourceIsList = true;
                }
                Variable variable = new Variable().withName(source.getAlias());
                variables.add(variable);
                state.push(variable);
                pushCount++;
            }

            for (LetClause let : elm.getLet()) {
                Variable letVariable = new Variable().withName(let.getIdentifier());
                letVariables.add(letVariable);
                state.push(letVariable);
                pushCount++;
            }

            QueryIterator iterator = new QueryIterator(state, sources);

            while (iterator.hasNext()) {
                List<Object> elements = (List<Object>) iterator.next();

                // Assign  variables
                assignVariables(variables, elements);

                evaluateLets(elm, state, letVariables, visitor);

                if (!evaluateRelationships(elm, state, visitor)) {
                    continue;
                }

                if (!evaluateWhere(elm, state, visitor)) {
                    continue;
                }

                // There's a "return" clause in the CQL
                if (elm.getReturn() != null) {
                    result.add(visitor.visitExpression(elm.getReturn().getExpression(), state));
                }
                // There's an "aggregate" clause in the CQL OR there's an implicit multi-source return
                else if (elm.getAggregate() != null || variables.size() > 1) {
                    result.add(constructTuple(state, variables));
                }
                // implicit return with 1 source
                else {
                    result.add(elements.get(0));
                }
            }
        } finally {
            while (pushCount > 0) {
                state.pop();
                pushCount--;
            }
        }

        if (elm.getReturn() != null && elm.getReturn().isDistinct()) {
            result = DistinctEvaluator.distinct(result, state);
        }

        if (elm.getAggregate() != null) {
            result = evaluateAggregate(elm.getAggregate(), state, visitor, result);
        }

        sortResult(elm, result, state, null, visitor);

        if ((result == null || result.isEmpty()) && !sourceIsList) {
            return null;
        }

        return elm.getAggregate() != null || !sourceIsList ? result.get(0) : result;
    }

    private static void assignVariables(List<Variable> variables, List<Object> elements) {
        for (int i = 0; i < variables.size(); i++) {
            variables.get(i).setValue(elements.get(i));
        }
    }
}
