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
 * <p>Java class for e_accessRestrictionType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="e_accessRestrictionType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="simulator"/>
 *     &lt;enumeration value="autonomousTraffic"/>
 *     &lt;enumeration value="pedestrian"/>
 *     &lt;enumeration value="passengerCar"/>
 *     &lt;enumeration value="bus"/>
 *     &lt;enumeration value="delivery"/>
 *     &lt;enumeration value="emergency"/>
 *     &lt;enumeration value="taxi"/>
 *     &lt;enumeration value="throughTraffic"/>
 *     &lt;enumeration value="truck"/>
 *     &lt;enumeration value="bicycle"/>
 *     &lt;enumeration value="motorcycle"/>
 *     &lt;enumeration value="none"/>
 *     &lt;enumeration value="trucks"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "e_accessRestrictionType")
@XmlEnum
public enum EAccessRestrictionType {

    @XmlEnumValue("simulator")
    SIMULATOR("simulator"),
    @XmlEnumValue("autonomousTraffic")
    AUTONOMOUS_TRAFFIC("autonomousTraffic"),
    @XmlEnumValue("pedestrian")
    PEDESTRIAN("pedestrian"),
    @XmlEnumValue("passengerCar")
    PASSENGER_CAR("passengerCar"),
    @XmlEnumValue("bus")
    BUS("bus"),
    @XmlEnumValue("delivery")
    DELIVERY("delivery"),
    @XmlEnumValue("emergency")
    EMERGENCY("emergency"),
    @XmlEnumValue("taxi")
    TAXI("taxi"),
    @XmlEnumValue("throughTraffic")
    THROUGH_TRAFFIC("throughTraffic"),
    @XmlEnumValue("truck")
    TRUCK("truck"),
    @XmlEnumValue("bicycle")
    BICYCLE("bicycle"),
    @XmlEnumValue("motorcycle")
    MOTORCYCLE("motorcycle"),
    @XmlEnumValue("none")
    NONE("none"),
    @XmlEnumValue("trucks")
    TRUCKS("trucks");
    private final String value;

    EAccessRestrictionType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EAccessRestrictionType fromValue(String v) {
        for (EAccessRestrictionType c: EAccessRestrictionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
