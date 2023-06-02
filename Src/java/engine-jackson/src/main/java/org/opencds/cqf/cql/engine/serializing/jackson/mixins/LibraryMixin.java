package org.opencds.cqf.cql.engine.serializing.jackson.mixins;

import org.cqframework.cql.elm.execution.Library;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonIgnoreProperties(value = "type")
@JsonTypeName("library")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME, defaultImpl = Library.class)
public interface LibraryMixin {
}
