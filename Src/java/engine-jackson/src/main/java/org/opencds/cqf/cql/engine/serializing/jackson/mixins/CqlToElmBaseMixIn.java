package org.opencds.cqf.cql.engine.serializing.jackson.mixins;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.hl7.cql_annotations.r1.Annotation;
import org.hl7.cql_annotations.r1.CqlToElmError;
import org.hl7.cql_annotations.r1.CqlToElmInfo;

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