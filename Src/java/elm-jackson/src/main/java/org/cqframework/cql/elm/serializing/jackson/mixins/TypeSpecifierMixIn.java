package org.cqframework.cql.elm.serializing.jackson.mixins;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.hl7.elm.r1.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = NamedTypeSpecifier.class, name = "ns4:NamedTypeSpecifier"),
    @JsonSubTypes.Type(value = ListTypeSpecifier.class, name = "ns4:ListTypeSpecifier"),
    @JsonSubTypes.Type(value = IntervalTypeSpecifier.class, name = "ns4:IntervalTypeSpecifier"),
    @JsonSubTypes.Type(value = ChoiceTypeSpecifier.class, name = "ns4:ChoiceTypeSpecifier"),
    @JsonSubTypes.Type(value = ParameterTypeSpecifier.class, name = "ns4:ParameterTypeSpecifier"),
    @JsonSubTypes.Type(value = TupleTypeSpecifier.class, name = "ns4:TupleTypeSpecifier")
})
public interface TypeSpecifierMixIn {}
