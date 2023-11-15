package org.cqframework.cql.elm.serializing.jackson;

import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.module.jakarta.xmlbind.JakartaXmlBindAnnotationModule;
import org.cqframework.cql.elm.serializing.jackson.mixins.CqlToElmBaseMixIn;
import org.cqframework.cql.elm.serializing.jackson.mixins.TrackableMixIn;
import org.cqframework.cql.elm.serializing.jackson.mixins.TypeSpecifierMixIn;
import org.cqframework.cql.elm.tracking.Trackable;
import org.hl7.cql_annotations.r1.CqlToElmBase;
import org.hl7.elm.r1.TypeSpecifier;

public class ElmXmlMapper {
    private static XmlMapper mapper = XmlMapper
            // TODO: Remove new XmlFactory(new WstxInputFactory(), new WstxOutputFactory()) in the future when Android
            //  receives an implementation of javax.xml.stream.XMLInputFactory.newFactory ('xml-apis:xml-apis:1.4.01')
            // Fixes #768: Forces the use of Woodstock for Android users.
            .builder(new XmlFactory(new WstxInputFactory(), new WstxOutputFactory()))
            .defaultUseWrapper(true)
            .defaultMergeable(true)
            .enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
            .enable(ToXmlGenerator.Feature.WRITE_XML_1_1)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .enable(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL)
            .defaultPropertyInclusion(JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.NON_NULL))
            .addModule(new JakartaXmlBindAnnotationModule())
            .addMixIn(Trackable.class, TrackableMixIn.class)
            .addMixIn(TypeSpecifier.class, TypeSpecifierMixIn.class)
            .addMixIn(CqlToElmBase.class, CqlToElmBaseMixIn.class)
            .build();

    public static XmlMapper getMapper() {
        return mapper;
    }
}
