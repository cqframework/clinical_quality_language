package org.opencds.cqf.cql.engine.serializing.jackson.mixins;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.cqframework.cql.elm.execution.Annotation;
import org.cqframework.cql.elm.execution.CqlToElmError;
import org.cqframework.cql.elm.execution.CqlToElmInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CqlToElmInfo.class, name = "a:CqlToElmInfo"),
        @JsonSubTypes.Type(value = CqlToElmError.class, name = "a:CqlToElmError"),
        @JsonSubTypes.Type(value = Annotation.class, name = "a:Annotation")
})
public interface CqlToElmBaseMixIn {}