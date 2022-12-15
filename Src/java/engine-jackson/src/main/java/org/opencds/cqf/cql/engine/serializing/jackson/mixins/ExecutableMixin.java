package org.opencds.cqf.cql.engine.serializing.jackson.mixins;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class ExecutableMixin {
}
