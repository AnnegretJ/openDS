//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.04.08 at 08:54:08 PM CEST 
//


package eu.opends.opendrive.data;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_roadMarkWeight.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="e_roadMarkWeight">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="standard"/>
 *     &lt;enumeration value="bold"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "e_roadMarkWeight")
@XmlEnum
public enum ERoadMarkWeight {

    @XmlEnumValue("standard")
    STANDARD("standard"),
    @XmlEnumValue("bold")
    BOLD("bold");
    private final String value;

    ERoadMarkWeight(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ERoadMarkWeight fromValue(String v) {
        for (ERoadMarkWeight c: ERoadMarkWeight.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
