package org.cqframework.cql.elm.tracking;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Trackable {
    private final UUID trackerId;
    private final List<TrackBack> trackbacks;

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
}
