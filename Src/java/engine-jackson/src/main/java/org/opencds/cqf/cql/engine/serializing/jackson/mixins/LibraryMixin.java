package org.opencds.cqf.cql.engine.serializing.jackson.mixins;

import org.hl7.elm.r1.Library;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonIgnoreProperties(value = "type")
@JsonTypeName("library")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME, defaultImpl = Library.class)
public interface LibraryMixin {
}
