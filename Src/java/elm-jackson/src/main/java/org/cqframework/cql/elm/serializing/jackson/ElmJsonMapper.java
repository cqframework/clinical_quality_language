package org.cqframework.cql.elm.serializing.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.module.jakarta.xmlbind.JakartaXmlBindAnnotationModule;
import org.cqframework.cql.elm.serializing.jackson.mixins.CqlToElmBaseMixIn;
import org.cqframework.cql.elm.serializing.jackson.mixins.TrackableMixIn;
import org.cqframework.cql.elm.serializing.jackson.mixins.TypeSpecifierMixIn;
import org.cqframework.cql.elm.tracking.Trackable;
import org.hl7.cql_annotations.r1.CqlToElmBase;
import org.hl7.elm.r1.TypeSpecifier;

public class ElmJsonMapper {
    private static JsonMapper mapper = JsonMapper.builder()
            .defaultMergeable(true)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .enable(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL)
            .defaultPropertyInclusion(JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.NON_NULL))
            .addModule(new JakartaXmlBindAnnotationModule())
            .addMixIn(Trackable.class, TrackableMixIn.class)
            .addMixIn(TypeSpecifier.class, TypeSpecifierMixIn.class)
            .addMixIn(CqlToElmBase.class, CqlToElmBaseMixIn.class)
            .build();

    public static JsonMapper getMapper() {
        return mapper;
    }
}
