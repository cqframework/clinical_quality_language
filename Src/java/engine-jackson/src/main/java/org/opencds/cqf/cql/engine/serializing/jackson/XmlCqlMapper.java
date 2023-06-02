package org.opencds.cqf.cql.engine.serializing.jackson;

import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import org.cqframework.cql.elm.execution.CodeSystemRef;
import org.cqframework.cql.elm.execution.CqlToElmBase;
import org.cqframework.cql.elm.execution.Element;
import org.cqframework.cql.elm.execution.Expression;
import org.cqframework.cql.elm.execution.ExpressionDef;
import org.cqframework.cql.elm.execution.TypeSpecifier;
import org.opencds.cqf.cql.engine.elm.execution.Executable;
import org.opencds.cqf.cql.engine.serializing.jackson.mixins.*;
import org.opencds.cqf.cql.engine.serializing.jackson.modules.QNameFixerXMLMapperModifier;

public class XmlCqlMapper {
    private static final XmlMapper mapper = XmlMapper
            // TODO: Remove new XmlFactory(new WstxInputFactory(), new WstxOutputFactory()) in the future when Android
            //  receives an implementation of javax.xml.stream.XMLInputFactory.newFactory ('xml-apis:xml-apis:1.4.01')
            // Fixes ClinicalQualityLanguage#768: Forces the use of Woodstock for Android users.
            .builder(new XmlFactory(new WstxInputFactory(), new WstxOutputFactory()))
            .defaultUseWrapper(true)
            .defaultMergeable(true)
            .enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION)
            .enable(ToXmlGenerator.Feature.WRITE_XML_1_1)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .enable(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL)
            .defaultPropertyInclusion(JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.NON_NULL))
            .addModule(new JaxbAnnotationModule())
            // Jackson's base QName deserializer does not unpack the namespace. This Modifier fixes it.
            .addModule(new SimpleModule().setDeserializerModifier(new QNameFixerXMLMapperModifier()))
            //.addMixIn(Library.class, LibraryMixin.class) // Some case sensitivity issue in the name [L]ibrary
            // The ordering here of the mix ins for
            // ExpressionDef -> CodeSystemRef ->  Expression -> Element matters,
            // so the mix-ins match most specific to least
            .addMixIn(ExpressionDef.class, ExpressionDefMixin.class)
            .addMixIn(CodeSystemRef.class, CodeSystemRefMixin.class)
            .addMixIn(Expression.class, ExpressionMixin.class)
            .addMixIn(TypeSpecifier.class, TypeSpecifierMixin.class)
            .addMixIn(CqlToElmBase.class, CqlToElmBaseMixIn.class)
            .addMixIn(Element.class, ElementMixin.class)
            .addMixIn(Executable.class, ExecutableMixin.class)
            .build();

    public static XmlMapper getMapper() {
        return mapper;
    }
}
