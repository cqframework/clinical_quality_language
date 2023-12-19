package org.cqframework.cql.tools.xsd2modelinfo;

import java.util.HashMap;
import java.util.Map;

public class ModelImporterMapperValue {
    public enum Relationship {
        RETYPE,
        EXTEND
    }

    private final String targetSystemClass;
    private final Relationship relationship;
    private final Map<String, String> targetClassElementMap;

    public ModelImporterMapperValue(String targetSystemClass, Relationship relationship) {
        this.targetSystemClass = targetSystemClass;
        this.relationship = relationship;
        this.targetClassElementMap = new HashMap<>();
    }

    public String getTargetSystemClass() {
        return targetSystemClass;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public Map<String, String> getTargetClassElementMap() {
        return targetClassElementMap;
    }

    public void addClassElementMapping(String element, String targetElement) {
        targetClassElementMap.put(element, targetElement);
    }

    public static ModelImporterMapperValue newRetype(String targetSystemClass) {
        return new ModelImporterMapperValue(targetSystemClass, Relationship.RETYPE);
    }

    public static ModelImporterMapperValue newExtend(String targetSystemClass) {
        return new ModelImporterMapperValue(targetSystemClass, Relationship.EXTEND);
    }
}
