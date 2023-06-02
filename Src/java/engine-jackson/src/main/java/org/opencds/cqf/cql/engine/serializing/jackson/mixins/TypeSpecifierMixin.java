package org.opencds.cqf.cql.engine.serializing.jackson.mixins;

import org.cqframework.cql.elm.execution.ChoiceTypeSpecifier;
import org.cqframework.cql.elm.execution.ListTypeSpecifier;
import org.cqframework.cql.elm.execution.NamedTypeSpecifier;
import org.cqframework.cql.elm.execution.ParameterTypeSpecifier;
import org.cqframework.cql.elm.execution.TupleTypeSpecifier;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @Type(value = TupleTypeSpecifier.class, name = "TupleTypeSpecifier"),
    @Type(value = NamedTypeSpecifier.class, name = "NamedTypeSpecifier"),
    @Type(value = ChoiceTypeSpecifier.class, name = "ChoiceTypeSpecifier"),
    @Type(value = ChoiceTypeSpecifier.class, name = "IntervalTypeSpecifier"),
    @Type(value = ListTypeSpecifier.class, name = "ListTypeSpecifier"),
    @Type(value = ParameterTypeSpecifier.class, name = "ParameterTypeSpecifier")
  })
public interface TypeSpecifierMixin  {
}
