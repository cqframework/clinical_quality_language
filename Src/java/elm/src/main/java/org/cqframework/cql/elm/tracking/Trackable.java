package org.cqframework.cql.elm.tracking;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.hl7.cql.model.DataType;

import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class Trackable {
    private final UUID trackerId;
    private final List<TrackBack> trackbacks;

    private DataType resultType;

    public Trackable() {
        this.trackerId = UUID.randomUUID();
        this.trackbacks = new ArrayList<>();
    }

    @XmlTransient
    @JsonIgnore
    public UUID getTrackerId() {
        return trackerId;
    }

    @XmlTransient
    @JsonIgnore
    public List<TrackBack> getTrackbacks() {
        return trackbacks;
    }

    @XmlTransient
    @JsonIgnore
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
