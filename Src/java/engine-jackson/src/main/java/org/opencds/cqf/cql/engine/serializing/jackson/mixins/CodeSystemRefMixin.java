package org.opencds.cqf.cql.engine.serializing.jackson.mixins;

import org.opencds.cqf.cql.engine.elm.execution.CodeSystemRefEvaluator;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", defaultImpl = CodeSystemRefEvaluator.class)
@JsonSubTypes({
    @Type(value = CodeSystemRefEvaluator.class, name = "CodeSystemRef")
})
public class CodeSystemRefMixin {
}
