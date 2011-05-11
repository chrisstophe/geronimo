//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-833 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.04.25 at 01:15:28 PM PDT 
//


package org.apache.geronimo.j2ee.deployment.model.naming;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for persistence-context-typeType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="persistence-context-typeType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="Transactional"/>
 *     &lt;enumeration value="Extended"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "persistence-context-typeType")
@XmlEnum
public enum PersistenceContextTypeType {

    @XmlEnumValue("Transactional")
    TRANSACTIONAL("Transactional"),
    @XmlEnumValue("Extended")
    EXTENDED("Extended");
    private final String value;

    PersistenceContextTypeType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PersistenceContextTypeType fromValue(String v) {
        for (PersistenceContextTypeType c: PersistenceContextTypeType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}