package org.cqframework.cql.elm.serializing.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.module.jakarta.xmlbind.JakartaXmlBindAnnotationModule;
import org.cqframework.cql.elm.serializing.jackson.mixins.CqlToElmBaseMixIn;
import org.cqframework.cql.elm.serializing.jackson.mixins.TypeInfoMixIn;
import org.cqframework.cql.elm.serializing.jackson.mixins.TypeSpecifierMixIn;
import org.hl7.cql_annotations.r1.CqlToElmBase;
import org.hl7.elm.r1.Element;
import org.hl7.elm.r1.InstanceElement;
import org.hl7.elm.r1.Library;
import org.hl7.elm.r1.TupleElement;
import org.hl7.elm.r1.TypeSpecifier;
import org.hl7.elm.r1.VersionedIdentifier;

public class ElmJsonMapper {
    private static JsonMapper mapper = JsonMapper.builder()
            .defaultMergeable(true)
            .enable(SerializationFeature.INDENT_OUTPUT)
            .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .enable(MapperFeature.USE_BASE_TYPE_AS_DEFAULT_IMPL)
            .defaultPropertyInclusion(JsonInclude.Value.construct(
                    JsonInclude.Include.CUSTOM, JsonInclude.Include.NON_EMPTY, NoEmptyListsFilter.class, Void.class))
            .addModule(new JakartaXmlBindAnnotationModule())
            .addMixIn(Element.class, TypeInfoMixIn.class)
            .addMixIn(VersionedIdentifier.class, TypeInfoMixIn.class)
            .addMixIn(Library.Contexts.class, TypeInfoMixIn.class)
            .addMixIn(Library.Statements.class, TypeInfoMixIn.class)
            .addMixIn(Library.Codes.class, TypeInfoMixIn.class)
            .addMixIn(Library.Statements.class, TypeInfoMixIn.class)
            .addMixIn(Library.CodeSystems.class, TypeInfoMixIn.class)
            .addMixIn(Library.ValueSets.class, TypeInfoMixIn.class)
            .addMixIn(Library.Concepts.class, TypeInfoMixIn.class)
            .addMixIn(Library.Includes.class, TypeInfoMixIn.class)
            .addMixIn(Library.Usings.class, TypeInfoMixIn.class)
            .addMixIn(Library.Parameters.class, TypeInfoMixIn.class)
            .addMixIn(TupleElement.class, TypeInfoMixIn.class)
            .addMixIn(InstanceElement.class, TypeInfoMixIn.class)
            .addMixIn(TypeSpecifier.class, TypeSpecifierMixIn.class)
            .addMixIn(CqlToElmBase.class, CqlToElmBaseMixIn.class)
            .build();

    public static JsonMapper getMapper() {
        return mapper;
    }
}
