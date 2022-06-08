package org.cqframework.cql.cql2elm;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.hl7.cql_annotations.r1.Annotation;
import org.hl7.cql_annotations.r1.CqlToElmBase;
import org.hl7.cql_annotations.r1.CqlToElmError;
import org.hl7.cql_annotations.r1.CqlToElmInfo;
import org.hl7.elm_modelinfo.r1.*;
import org.hl7.elm_modelinfo.r1.ChoiceTypeSpecifier;
import org.hl7.elm_modelinfo.r1.IntervalTypeSpecifier;
import org.hl7.elm_modelinfo.r1.ListTypeSpecifier;
import org.hl7.elm_modelinfo.r1.NamedTypeSpecifier;
import org.hl7.elm_modelinfo.r1.ParameterTypeSpecifier;
import org.hl7.elm_modelinfo.r1.TupleTypeSpecifier;
import org.hl7.elm_modelinfo.r1.TypeSpecifier;

import java.io.*;
import java.net.URI;
import java.net.URL;

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

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CqlToElmInfo.class, name = "a:CqlToElmInfo"),
        @JsonSubTypes.Type(value = CqlToElmError.class, name = "a:CqlToElmError"),
        @JsonSubTypes.Type(value = Annotation.class, name = "a:Annotation")
})
interface CqlToElmBaseMixIn {}

public class ModelInfoXmlReader {
    static XmlMapper mapper = new XmlMapper().builder()
            .defaultUseWrapper(false)
            .defaultMergeable(true)
            .enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
            .enable(ToXmlGenerator.Feature.WRITE_XML_1_1)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .enable(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL)
            .addModule(new JaxbAnnotationModule())
            .addMixIn(TypeInfo.class, TypeInfoMixIn.class)
            .addMixIn(TypeSpecifier.class, TypeSpecifierMixIn.class)
            .addMixIn(CqlToElmBase.class, CqlToElmBaseMixIn.class)
            .build();

    public static <T> T readValue(File src, Class<T> valueType) throws IOException {
        return mapper.readValue(src, valueType);
    }

    public static <T> T readValue(Reader src, Class<T> valueType) throws IOException {
        return mapper.readValue(src, valueType);
    }

    public static <T> T readValue(InputStream src, Class<T> valueType) throws IOException {
        return mapper.readValue(src, valueType);
    }

    public static <T> T readValue(URL url, Class<T> valueType) throws IOException {
        return mapper.readValue(url, valueType);
    }

    public static <T> T readValue(URI uri, Class<T> valueType) throws IOException {
        return mapper.readValue(uri.toURL(), valueType);
    }

    public static <T> T readValue(String string, Class<T> valueType) throws IOException {
        return mapper.readValue(string, valueType);
    }

    public static void writeValue(Object value, Writer dest) throws IOException {
        mapper.writeValue(dest, value);
    }
}
