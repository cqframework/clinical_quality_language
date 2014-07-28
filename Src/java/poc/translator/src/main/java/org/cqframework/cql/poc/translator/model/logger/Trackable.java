package org.cqframework.cql.poc.translator.model.logger;

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

    public void addTrackBack(TrackBack trackback) {
        this.trackbacks.add(trackback);
    }

    public void addTrackBack(String library, String version, int startLine, int startChar, int endLine, int endChar) {
        addTrackBack(new TrackBack(library, version, startLine, startChar, endLine, endChar));
    }

    public void merge(Trackable other) {
        this.trackbacks.addAll(other.getTrackbacks());
    }
}
