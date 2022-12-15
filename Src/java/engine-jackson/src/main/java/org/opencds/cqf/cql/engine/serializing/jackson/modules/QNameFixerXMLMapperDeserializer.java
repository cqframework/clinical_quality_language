package org.opencds.cqf.cql.engine.serializing.jackson.modules;

import javax.xml.namespace.QName;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;

public class QNameFixerXMLMapperDeserializer extends JsonDeserializer<QName> {
    JsonDeserializer<?> originalDeserializer;
    public QNameFixerXMLMapperDeserializer(JsonDeserializer<?> deserializer) {
        originalDeserializer = deserializer;
    }

    @Override
    public QName deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        var qName = (QName) originalDeserializer.deserialize(jsonParser, deserializationContext);

        if (qName.getLocalPart().indexOf(":") > 0) {
            var prefix = qName.getLocalPart().split(":")[0];
            var localPart = qName.getLocalPart().split(":")[1];
            var namespace = ((FromXmlParser)deserializationContext.getParser()).getStaxReader().getNamespaceContext().getNamespaceURI(prefix);

            return new QName(namespace, localPart, prefix);
        }

        return qName;
    }
}