package org.cqframework.cql.poc.translator.model;

import org.cqframework.cql.poc.translator.model.logger.TrackBack;

import java.util.Collection;
import java.util.HashMap;

public class CqlLibrary {
    private String library;
    private String version;

    private final HashMap<Integer, DataRetrieve> dataRetrievesByHash;
    private final HashMap<Integer, ValueSet> valueSetsByHash;
    private final HashMap<String, ValueSet> valueSetsByLocalIdentifier;

    public CqlLibrary() {
        dataRetrievesByHash = new HashMap<>();
        valueSetsByHash = new HashMap<>();
        valueSetsByLocalIdentifier = new HashMap<>();
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

    public Collection<DataRetrieve> getDataRetrieves() {
        return dataRetrievesByHash.values();
    }

    public DataRetrieve addDataRetrieve(DataRetrieve dataRetrieve) {
        DataRetrieve existing = dataRetrievesByHash.get(dataRetrieve.hashCode());
        if (existing != null) {
            existing.merge(dataRetrieve);
        } else {
            existing = dataRetrievesByHash.put(dataRetrieve.hashCode(), dataRetrieve);
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CqlLibrary{");
        sb.append("\n  library='").append(library).append('\'');
        sb.append("\n  version='").append(version).append('\'');
        sb.append("\n  dataRetrieves={");
        for (DataRetrieve dr : getDataRetrieves()) {
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
