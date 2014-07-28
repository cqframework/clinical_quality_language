package org.cqframework.cql.poc.translator.model;

import org.cqframework.cql.poc.translator.model.logger.Trackable;

public class DataRetrieve extends Trackable {
    public static enum Existence { Occurrence, NonOccurrence, UnknownOccurrence }

    private final Existence existence;
    private final String topic;
    private final String modality;
    // TODO: capture valuesetPathIdentifier
    private final String valueset;
    // TODO: capture embedded during phrase


    public DataRetrieve(Existence existence, String topic, String modality, String valueset) {
        super();

        this.existence = existence;
        this.topic = topic;
        this.modality = modality;
        this.valueset = valueset;
    }

    public Existence getExistence() {
        return existence;
    }

    public String getTopic() {
        return topic;
    }

    public String getModality() {
        return modality;
    }

    public String getValueset() {
        return valueset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DataRetrieve that = (DataRetrieve) o;

        if (existence != that.existence) return false;
        if (modality != null ? !modality.equals(that.modality) : that.modality != null) return false;
        if (!topic.equals(that.topic)) return false;
        if (valueset != null ? !valueset.equals(that.valueset) : that.valueset != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = existence.hashCode();
        result = 31 * result + topic.hashCode();
        result = 31 * result + (modality != null ? modality.hashCode() : 0);
        result = 31 * result + (valueset != null ? valueset.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DataRetrieve{" +
                "existence=" + existence +
                ", topic='" + topic + '\'' +
                ", modality='" + modality + '\'' +
                ", valueset='" + valueset + '\'' +
                '}';
    }
}
