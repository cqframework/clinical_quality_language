package org.cqframework.cql.poc.translator.model;

import org.cqframework.cql.poc.translator.expressions.Expression;
import org.cqframework.cql.poc.translator.expressions.LetStatement;
import org.cqframework.cql.poc.translator.model.logger.TrackBack;

import java.util.*;

public class CqlLibrary {
    private String library;
    private String version;

    private final Map<Integer, SourceDataCriteria> dataCriteriaByHash;
    private final Map<Integer, ValueSet> valueSetsByHash;
    private final Map<String, ValueSet> valueSetsByLocalIdentifier;
    private final Map<String, LetStatement> variables;
    private final List<Expression> expressions;

    public CqlLibrary() {
        dataCriteriaByHash = new HashMap<>();
        valueSetsByHash = new HashMap<>();
        valueSetsByLocalIdentifier = new HashMap<>();
        variables = new HashMap<>();
        expressions = new ArrayList<>();
    }

    public String getLibrary() {
        return library;
    }

    public void setLibrary(String library) {
        this.library = library;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Collection<SourceDataCriteria> getSourceDataCriteria() {
        return dataCriteriaByHash.values();
    }

    public SourceDataCriteria addSourceDataCriteria(SourceDataCriteria sourceDataCriteria) {
        SourceDataCriteria existing = dataCriteriaByHash.get(sourceDataCriteria.hashCode());
        if (existing != null) {
            existing.merge(sourceDataCriteria);
        } else {
            existing = dataCriteriaByHash.put(sourceDataCriteria.hashCode(), sourceDataCriteria);
        }

        return existing;
    }

    public Collection<ValueSet> getValueSets() {
        return valueSetsByHash.values();
    }

    public ValueSet getValueSetByLocalStringIdentifier(String localStringIdentifier) {
        return valueSetsByLocalIdentifier.get(localStringIdentifier);
    }

    public ValueSet addValueSet(ValueSet valueSet) {
        ValueSet existing = valueSetsByHash.get(valueSet.hashCode());
        if (existing != null) {
            existing.merge(valueSet);
        } else {
            existing = valueSetsByHash.put(valueSet.hashCode(), valueSet);
        }

        for (String localStringId : valueSet.getLocalStringIdentifiers()) {
            valueSetsByLocalIdentifier.put(localStringId, existing);
        }

        return existing;
    }

    public LetStatement addLetStatement(LetStatement let) {
        return getVariables().put(let.getIdentifier(), let);
    }

    public Map<String, LetStatement> getVariables() {
        return this.variables;
    }

    public List<Expression> getExpressions() {
        return this.expressions;
    }

    public boolean addExpression(Expression exp) {
        return getExpressions().add(exp);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CqlLibrary{");
        sb.append("\n  library='").append(library).append('\'');
        sb.append("\n  version='").append(version).append('\'');
        sb.append("\n  sourceDataCriteria={");
        for (SourceDataCriteria dr : getSourceDataCriteria()) {
            sb.append("\n    ").append(dr);
            for (TrackBack tb : dr.getTrackbacks()) {
                sb.append("\n      --> ").append(tb.toString());
            }
        }
        sb.append("\n  }");
        sb.append("\n  valueSets={");
        for (ValueSet vs : getValueSets()) {
            sb.append("\n    ").append(vs);
            for (TrackBack tb : vs.getTrackbacks()) {
                sb.append("\n      --> ").append(tb.toString());
            }
        }
        sb.append("\n  }");
        sb.append('}');
        return sb.toString();
    }
}
