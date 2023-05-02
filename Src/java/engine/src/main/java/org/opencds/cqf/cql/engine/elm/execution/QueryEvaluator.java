package org.opencds.cqf.cql.engine.elm.execution;

//import java.util.ArrayList;
//import java.util.Iterator;
//import java.util.LinkedHashMap;
//import java.util.List;
//
//import org.cqframework.cql.elm.execution.AliasedQuerySource;
//import org.cqframework.cql.elm.execution.ByColumn;
//import org.cqframework.cql.elm.execution.ByExpression;
//import org.cqframework.cql.elm.execution.LetClause;
//import org.opencds.cqf.cql.engine.execution.Context;
//import org.opencds.cqf.cql.engine.execution.State;
//import org.opencds.cqf.cql.engine.execution.Variable;
//import org.opencds.cqf.cql.engine.runtime.CqlList;
//import org.opencds.cqf.cql.engine.runtime.Tuple;
//import org.opencds.cqf.cql.engine.runtime.iterators.QueryIterator;

public class QueryEvaluator extends org.cqframework.cql.elm.execution.Query {
//
//    @SuppressWarnings("unchecked")
//    public Iterable<Object> ensureIterable(Object source) {
//        if (source instanceof Iterable) {
//            return (Iterable<Object>) source;
//        }
//        else {
//            ArrayList<Object> sourceList = new ArrayList<>();
//            if (source != null)
//                sourceList.add(source);
//            return sourceList;
//        }
//    }
//
//    private void evaluateLets(Context context, List<Variable> letVariables) {
//        for (int i = 0; i < getLet().size(); i++) {
//            letVariables.get(i).setValue(getLet().get(i).getExpression().evaluate(context));
//        }
//    }
//
//    private boolean evaluateRelationships(Context context) {
//        // TODO: This is the most naive possible implementation here, but it should perform okay with 1) caching and 2) small data sets
//        boolean shouldInclude = true;
//        for (org.cqframework.cql.elm.execution.RelationshipClause relationship : getRelationship()) {
//            boolean hasSatisfyingData = false;
//            Iterable<Object> relatedSourceData = ensureIterable(relationship.getExpression().evaluate(context));
//            for (Object relatedElement : relatedSourceData) {
//                context.push(new Variable().withName(relationship.getAlias()).withValue(relatedElement));
//                try {
//                    Object satisfiesRelatedCondition = relationship.getSuchThat().evaluate(context);
//                    if (relationship instanceof org.cqframework.cql.elm.execution.With
//                            || relationship instanceof org.cqframework.cql.elm.execution.Without)
//                    {
//                        if (satisfiesRelatedCondition instanceof Boolean && (Boolean) satisfiesRelatedCondition) {
//                            hasSatisfyingData = true;
//                            break; // Once we have detected satisfying data, no need to continue testing
//                        }
//                    }
//                }
//                finally {
//                    context.pop();
//                }
//            }
//
//            if ((relationship instanceof org.cqframework.cql.elm.execution.With && !hasSatisfyingData)
//                    || (relationship instanceof org.cqframework.cql.elm.execution.Without && hasSatisfyingData)) {
//                shouldInclude = false;
//                break; // Once we have determined the row should not be included, no need to continue testing other related information
//            }
//        }
//
//        return shouldInclude;
//    }
//
//    private boolean evaluateWhere(Context context) {
//        if (getWhere() != null) {
//            Object satisfiesCondition = this.getWhere().evaluate(context);
//            if (!(satisfiesCondition instanceof Boolean && (Boolean)satisfiesCondition)) {
//                return false;
//            }
//        }
//
//        return true;
//    }
//
//    private Object evaluateReturn(Context context, List<Variable> variables, List<Object> elements) {
//        return this.getReturn() != null ? this.getReturn().getExpression().evaluate(context) : constructResult(context, variables, elements);
//    }
//
//    private Object constructResult(Context context, List<Variable> variables, List<Object> elements) {
//        if (variables.size() > 1) {
//            LinkedHashMap<String,Object> elementMap = new LinkedHashMap<>();
//            for (int i = 0; i < variables.size(); i++) {
//                elementMap.put(variables.get(i).getName(), variables.get(i).getValue());
//            }
//
//            return new Tuple(context).withElements(elementMap);
//        }
//
//        return elements.get(0);
//    }
//
//    public void sortResult(List<Object> result, State state, String alias) {
//
//        org.cqframework.cql.elm.execution.SortClause sortClause = this.getSort();
//
//        if (sortClause != null) {
//
//            for (org.cqframework.cql.elm.execution.SortByItem byItem : sortClause.getBy()) {
//
//                if (byItem instanceof ByExpression) {
//                    result.sort(new CqlList(state, alias, ((ByExpression)byItem).getExpression()).expressionSort);
//                }
//
//                else if (byItem instanceof ByColumn) {
//                    result.sort(new CqlList(state, ((ByColumn)byItem).getPath()).columnSort);
//                }
//
//                else {
//                    result.sort(new CqlList().valueSort);
//                }
//
//                String direction = byItem.getDirection().value();
//                if (direction.equals("desc") || direction.equals("descending")) {
//                    java.util.Collections.reverse(result);
//                }
//            }
//        }
//    }
//
//    class QuerySource {
//        private String alias;
//        private boolean isList;
//        private Iterable<Object> data;
//
//        public QuerySource(String alias, Object data) {
//            this.alias = alias;
//            this.isList = data instanceof Iterable;
//            this.data = ensureIterable(data);
//        }
//
//        public String getAlias() {
//            return alias;
//        }
//
//        public boolean getIsList() {
//            return isList;
//        }
//
//        public Iterable<Object> getData() {
//            return data;
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    @Override
//    protected Object internalEvaluate(Context context) {
//
//        ArrayList<Iterator<Object>> sources = new ArrayList<Iterator<Object>>();
//        ArrayList<Variable> variables = new ArrayList<Variable>();
//        ArrayList<Variable> letVariables = new ArrayList<Variable>();
//        List<Object> result = new ArrayList<>();
//        boolean sourceIsList = false;
//        int pushCount = 0;
//        try {
//            for (AliasedQuerySource source : this.getSource()) {
//                QuerySource querySource = new QuerySource(source.getAlias(), source.getExpression().evaluate(context));
//                sources.add(querySource.getData().iterator());
//                if (querySource.getIsList()) {
//                    sourceIsList = true;
//                }
//                Variable variable = new Variable().withName(source.getAlias());
//                variables.add(variable);
//                context.push(variable);
//                pushCount++;
//            }
//
//            for (LetClause let : this.getLet()) {
//                Variable letVariable = new Variable().withName(let.getIdentifier());
//                letVariables.add(letVariable);
//                context.push(letVariable);
//                pushCount++;
//            }
//
//            QueryIterator iterator = new QueryIterator(context, sources);
//
//            while (iterator.hasNext()) {
//                List<Object> elements = (List<Object>)iterator.next();
//
//                // Assign range variables
//                assignVariables(variables, elements);
//
//                evaluateLets(context, letVariables);
//
//                // Evaluate relationships
//                if (!evaluateRelationships(context)) {
//                    continue;
//                }
//
//                if (!evaluateWhere(context)) {
//                    continue;
//                }
//
//                result.add(evaluateReturn(context, variables, elements));
//            }
//        }
//        finally {
//            while (pushCount > 0) {
//                context.pop();
//                pushCount--;
//            }
//        }
//
//        if (this.getReturn() != null && this.getReturn().isDistinct()) {
//            result = DistinctEvaluator.distinct(result, context);
//        }
//
//        sortResult(result, context, null);
//
//        if ((result == null || result.isEmpty()) && !sourceIsList) {
//            return null;
//        }
//
//        return sourceIsList ? result : result.get(0);
//    }
//
//    private void assignVariables(List<Variable> variables, List<Object> elements) {
//        for (int i = 0; i < variables.size(); i++) {
//            variables.get(i).setValue(elements.get(i));
//        }
//    }
}
