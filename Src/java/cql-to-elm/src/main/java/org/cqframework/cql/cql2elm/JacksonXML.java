package org.cqframework.cql.cql2elm;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.hl7.elm_modelinfo.r1.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SimpleTypeInfo.class, name = "ns4:SimpleTypeInfo"),
        @JsonSubTypes.Type(value = ClassInfo.class, name = "ns4:ClassInfo"),
        @JsonSubTypes.Type(value = ChoiceTypeInfo.class, name = "ns4:ChoiceTypeInfo"),
        @JsonSubTypes.Type(value = IntervalTypeInfo.class, name = "ns4:IntervalTypeInfo"),
        @JsonSubTypes.Type(value = ListTypeInfo.class, name = "ns4:ListTypeInfo"),
        @JsonSubTypes.Type(value = ProfileInfo.class, name = "ns4:ProfileInfo"),
        @JsonSubTypes.Type(value = TupleTypeInfo.class, name = "ns4:TupleTypeInfo")
})
interface TypeInfoMixIn {}

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = NamedTypeSpecifier.class, name = "ns4:NamedTypeSpecifier"),
        @JsonSubTypes.Type(value = ListTypeSpecifier.class, name = "ns4:ListTypeSpecifier"),
        @JsonSubTypes.Type(value = IntervalTypeSpecifier.class, name = "ns4:IntervalTypeSpecifier"),
        @JsonSubTypes.Type(value = ChoiceTypeSpecifier.class, name = "ns4:ChoiceTypeSpecifier"),
        @JsonSubTypes.Type(value = ParameterTypeSpecifier.class, name = "ns4:ParameterTypeSpecifier"),
        @JsonSubTypes.Type(value = BoundParameterTypeSpecifier.class, name = "ns4:BoundParameterTypeSpecifier"),
        @JsonSubTypes.Type(value = TupleTypeSpecifier.class, name = "ns4:TupleTypeSpecifier")
})
interface TypeSpecifierMixIn {}

/**
 * Waiting for a solution to this issue: https://github.com/FasterXML/jackson-databind/issues/2968
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        defaultImpl = NamedTypeSpecifier.class)
interface NamedTypeSpecifierMixIn {}

public class JacksonXML {

    static XmlMapper mapper = new XmlMapper().builder()
            .defaultUseWrapper(false)
            .addMixIn(TypeInfo.class, TypeInfoMixIn.class)
            .addMixIn(TypeSpecifier.class, TypeSpecifierMixIn.class)
            .addMixIn(NamedTypeSpecifier.class, NamedTypeSpecifierMixIn.class)
            .addModule(new JaxbAnnotationModule())
            .build();

    public static <T> T readValue(File src, Class<T> valueType) throws IOException {
        return mapper.readValue(src, valueType);
    }

    public static <T> T readValue(InputStream src, Class<T> valueType) throws IOException {
        return mapper.readValue(src, valueType);
    }
}
