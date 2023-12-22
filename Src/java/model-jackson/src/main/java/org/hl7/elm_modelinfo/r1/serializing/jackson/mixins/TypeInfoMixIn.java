package org.hl7.elm_modelinfo.r1.serializing.jackson.mixins;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.hl7.elm_modelinfo.r1.*;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = SimpleTypeInfo.class, name = "ns4:SimpleTypeInfo"),
    @JsonSubTypes.Type(value = ClassInfo.class, name = "ns4:ClassInfo"),
    @JsonSubTypes.Type(value = ChoiceTypeInfo.class, name = "ns4:ChoiceTypeInfo"),
    @JsonSubTypes.Type(value = IntervalTypeInfo.class, name = "ns4:IntervalTypeInfo"),
    @JsonSubTypes.Type(value = ListTypeInfo.class, name = "ns4:ListTypeInfo"),
    @JsonSubTypes.Type(value = ProfileInfo.class, name = "ns4:ProfileInfo"),
    @JsonSubTypes.Type(value = TupleTypeInfo.class, name = "ns4:TupleTypeInfo")
})
public interface TypeInfoMixIn {}
