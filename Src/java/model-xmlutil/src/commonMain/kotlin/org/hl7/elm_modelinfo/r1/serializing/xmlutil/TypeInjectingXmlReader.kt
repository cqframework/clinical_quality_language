package org.hl7.elm_modelinfo.r1.serializing.xmlutil

import nl.adaptivity.xmlutil.XMLConstants
import nl.adaptivity.xmlutil.XmlDelegatingReader
import nl.adaptivity.xmlutil.XmlReader

// This reader injects the xsi:type attributes into the XML stream with the value of "usebaseclass"
// if it is not present
class TypeInjectingXmlReader(reader: XmlReader) : XmlDelegatingReader(reader) {

    override val attributeCount: Int
        get() = super.attributeCount + 1

    override fun getAttributeNamespace(index: Int): String {
        if (index == 0) {
            return XMLConstants.XSI_NS_URI
        }
        return super.getAttributeNamespace(index - 1)
    }

    override fun getAttributePrefix(index: Int): String {
        if (index == 0) {
            return XMLConstants.XSI_PREFIX
        }
        return super.getAttributePrefix(index - 1)
    }

    override fun getAttributeLocalName(index: Int): String {
        if (index == 0) {
            return "type"
        }
        return super.getAttributeLocalName(index - 1)
    }

    override fun getAttributeValue(index: Int): String {
        if (index == 0) {
            return super.getAttributeValue(XMLConstants.XSI_NS_URI, "type") ?: "usebaseclass"
        }
        return super.getAttributeValue(index - 1)
    }

    override fun getAttributeValue(nsUri: String?, localName: String): String? {
        if (nsUri == XMLConstants.XSI_NS_URI && localName == "type") {
            super.getAttributeValue(XMLConstants.XSI_NS_URI, "type") ?: "usebaseclass"
        }

        return super.getAttributeValue(nsUri, localName)
    }
}
