package org.opencds.cqf.cql.engine.serializing.jackson.modules;

import javax.xml.namespace.QName;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;

public class QNameFixerXMLMapperModifier extends BeanDeserializerModifier {
    public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
        if (beanDesc.getBeanClass() == QName.class) {
            return new QNameFixerXMLMapperDeserializer(deserializer);
        }
        return super.modifyDeserializer(config, beanDesc, deserializer);
    }
}