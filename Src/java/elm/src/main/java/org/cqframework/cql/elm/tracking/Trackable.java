package org.cqframework.cql.elm.tracking;

import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Trackable {
    private final UUID trackerId;
    private final List<TrackBack> trackbacks;

    private DataType resultType;

    public Trackable() {
        this.trackerId = UUID.randomUUID();
        this.trackbacks = new ArrayList<>();
    }

    public UUID getTrackerId() {
        return trackerId;
    }

    public List<TrackBack> getTrackbacks() {
        return trackbacks;
    }

    @XmlTransient
    public DataType getResultType() {
        return resultType;
    }

    public void setResultType(DataType resultType) {
        this.resultType = resultType;
    }

    public Trackable withResultType(DataType resultType) {
        setResultType(resultType);
        return this;
    }
}
