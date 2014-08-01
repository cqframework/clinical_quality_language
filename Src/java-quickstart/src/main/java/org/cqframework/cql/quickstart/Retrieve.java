package org.cqframework.cql.quickstart;

public class Retrieve {
    public static enum Existence {Occurrence, NonOccurrence, UnknownOccurrence}

    private final Existence existence;
    private final String topic;
    private final String modality;
    // TODO: Support valuesetPathIdentifier
    private final ValueSet valueset;
    // TODO: Support "during" filter

    public Retrieve(Existence existence, String topic, String modality, ValueSet valueset) {
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

    public ValueSet getValueset() {
        return valueset;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Retrieve retrieve = (Retrieve) o;

        if (existence != retrieve.existence) return false;
        if (modality != null ? !modality.equals(retrieve.modality) : retrieve.modality != null) return false;
        if (!topic.equals(retrieve.topic)) return false;
        if (valueset != null ? !valueset.equals(retrieve.valueset) : retrieve.valueset != null) return false;

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
        return "Retrieve{" +
                "existence=" + existence +
                ", topic='" + topic + '\'' +
                ", modality='" + modality + '\'' +
                ", valueset=" + valueset +
                '}';
    }
}
