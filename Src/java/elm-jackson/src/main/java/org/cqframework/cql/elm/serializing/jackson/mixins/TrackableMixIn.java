package org.cqframework.cql.elm.serializing.jackson.mixins;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;
import java.util.UUID;
import org.cqframework.cql.elm.tracking.TrackBack;
import org.hl7.cql.model.DataType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public interface TrackableMixIn {
    @JsonIgnore
    public UUID getTrackerId();

    @JsonIgnore
    public List<TrackBack> getTrackbacks();

    @JsonIgnore
    public DataType getResultType();
}
