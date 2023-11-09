package org.hl7.cql.model;

import org.jvnet.jaxb2_commons.lang.*;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;
import org.jvnet.jaxb2_commons.locator.util.LocatorUtils;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ModelIdentifier", namespace = "urn:hl7-org:model")
public class ModelIdentifier
    implements Equals2, HashCode2, ToString2
{

    @XmlAttribute(name = "id")
    protected String id;
    @XmlAttribute(name = "system")
    protected String system;
    @XmlAttribute(name = "version")
    protected String version;

    /**
     * Gets the value of the id property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the system property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSystem() {
        return system;
    }

    /**
     * Sets the value of the system property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSystem(String value) {
        this.system = value;
    }

    /**
     * Gets the value of the version property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the version property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setVersion(String value) {
        this.version = value;
    }

    public ModelIdentifier withId(String value) {
        setId(value);
        return this;
    }

    public ModelIdentifier withSystem(String value) {
        setSystem(value);
        return this;
    }

    public ModelIdentifier withVersion(String value) {
        setVersion(value);
        return this;
    }

    public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object, EqualsStrategy2 strategy) {
        if ((object == null)||(this.getClass()!= object.getClass())) {
            return false;
        }
        if (this == object) {
            return true;
        }
        final ModelIdentifier that = ((ModelIdentifier) object);
        {
            String lhsId;
            lhsId = this.getId();
            String rhsId;
            rhsId = that.getId();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "id", lhsId), LocatorUtils.property(thatLocator, "id", rhsId), lhsId, rhsId, (this.id!= null), (that.id!= null))) {
                return false;
            }
        }
        {
            String lhsSystem;
            lhsSystem = this.getSystem();
            String rhsSystem;
            rhsSystem = that.getSystem();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "system", lhsSystem), LocatorUtils.property(thatLocator, "system", rhsSystem), lhsSystem, rhsSystem, (this.system!= null), (that.system!= null))) {
                return false;
            }
        }
        {
            String lhsVersion;
            lhsVersion = this.getVersion();
            String rhsVersion;
            rhsVersion = that.getVersion();
            if (!strategy.equals(LocatorUtils.property(thisLocator, "version", lhsVersion), LocatorUtils.property(thatLocator, "version", rhsVersion), lhsVersion, rhsVersion, (this.version!= null), (that.version!= null))) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(Object object) {
        final EqualsStrategy2 strategy = JAXBEqualsStrategy.getInstance();
        return equals(null, null, object, strategy);
    }

    public int hashCode(ObjectLocator locator, HashCodeStrategy2 strategy) {
        int currentHashCode = 1;
        {
            String theId;
            theId = this.getId();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "id", theId), currentHashCode, theId, (this.id!= null));
        }
        {
            String theSystem;
            theSystem = this.getSystem();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "system", theSystem), currentHashCode, theSystem, (this.system!= null));
        }
        {
            String theVersion;
            theVersion = this.getVersion();
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "version", theVersion), currentHashCode, theVersion, (this.version!= null));
        }
        return currentHashCode;
    }

    public int hashCode() {
        final HashCodeStrategy2 strategy = JAXBHashCodeStrategy.getInstance();
        return this.hashCode(null, strategy);
    }

    public String toString() {
        final ToStringStrategy2 strategy = JAXBToStringStrategy.getInstance();
        final StringBuilder buffer = new StringBuilder();
        append(null, buffer, strategy);
        return buffer.toString();
    }

    public StringBuilder append(ObjectLocator locator, StringBuilder buffer, ToStringStrategy2 strategy) {
        strategy.appendStart(locator, this, buffer);
        appendFields(locator, buffer, strategy);
        strategy.appendEnd(locator, this, buffer);
        return buffer;
    }

    public StringBuilder appendFields(ObjectLocator locator, StringBuilder buffer, ToStringStrategy2 strategy) {
        {
            String theId;
            theId = this.getId();
            strategy.appendField(locator, this, "id", buffer, theId, (this.id!= null));
        }
        {
            String theSystem;
            theSystem = this.getSystem();
            strategy.appendField(locator, this, "system", buffer, theSystem, (this.system!= null));
        }
        {
            String theVersion;
            theVersion = this.getVersion();
            strategy.appendField(locator, this, "version", buffer, theVersion, (this.version!= null));
        }
        return buffer;
    }

}
