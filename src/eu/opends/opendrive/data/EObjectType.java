//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.04.08 at 08:54:08 PM CEST 
//


package eu.opends.opendrive.data;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for e_objectType.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;simpleType name="e_objectType">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="none"/>
 *     &lt;enumeration value="obstacle"/>
 *     &lt;enumeration value="car"/>
 *     &lt;enumeration value="pole"/>
 *     &lt;enumeration value="tree"/>
 *     &lt;enumeration value="vegetation"/>
 *     &lt;enumeration value="barrier"/>
 *     &lt;enumeration value="building"/>
 *     &lt;enumeration value="parkingSpace"/>
 *     &lt;enumeration value="patch"/>
 *     &lt;enumeration value="railing"/>
 *     &lt;enumeration value="trafficIsland"/>
 *     &lt;enumeration value="crosswalk"/>
 *     &lt;enumeration value="streetLamp"/>
 *     &lt;enumeration value="gantry"/>
 *     &lt;enumeration value="soundBarrier"/>
 *     &lt;enumeration value="truck"/>
 *     &lt;enumeration value="van"/>
 *     &lt;enumeration value="bus"/>
 *     &lt;enumeration value="trailer"/>
 *     &lt;enumeration value="bike"/>
 *     &lt;enumeration value="motorbike"/>
 *     &lt;enumeration value="tram"/>
 *     &lt;enumeration value="train"/>
 *     &lt;enumeration value="pedestrian"/>
 *     &lt;enumeration value="wind"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "e_objectType")
@XmlEnum
public enum EObjectType {

    @XmlEnumValue("none")
    NONE("none"),
    @XmlEnumValue("obstacle")
    OBSTACLE("obstacle"),
    @XmlEnumValue("car")
    CAR("car"),
    @XmlEnumValue("pole")
    POLE("pole"),
    @XmlEnumValue("tree")
    TREE("tree"),
    @XmlEnumValue("vegetation")
    VEGETATION("vegetation"),
    @XmlEnumValue("barrier")
    BARRIER("barrier"),
    @XmlEnumValue("building")
    BUILDING("building"),
    @XmlEnumValue("parkingSpace")
    PARKING_SPACE("parkingSpace"),
    @XmlEnumValue("patch")
    PATCH("patch"),
    @XmlEnumValue("railing")
    RAILING("railing"),
    @XmlEnumValue("trafficIsland")
    TRAFFIC_ISLAND("trafficIsland"),
    @XmlEnumValue("crosswalk")
    CROSSWALK("crosswalk"),
    @XmlEnumValue("streetLamp")
    STREET_LAMP("streetLamp"),
    @XmlEnumValue("gantry")
    GANTRY("gantry"),
    @XmlEnumValue("soundBarrier")
    SOUND_BARRIER("soundBarrier"),
    @XmlEnumValue("truck")
    TRUCK("truck"),
    @XmlEnumValue("van")
    VAN("van"),
    @XmlEnumValue("bus")
    BUS("bus"),
    @XmlEnumValue("trailer")
    TRAILER("trailer"),
    @XmlEnumValue("bike")
    BIKE("bike"),
    @XmlEnumValue("motorbike")
    MOTORBIKE("motorbike"),
    @XmlEnumValue("tram")
    TRAM("tram"),
    @XmlEnumValue("train")
    TRAIN("train"),
    @XmlEnumValue("pedestrian")
    PEDESTRIAN("pedestrian"),
    @XmlEnumValue("wind")
    WIND("wind");
    private final String value;

    EObjectType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EObjectType fromValue(String v) {
        for (EObjectType c: EObjectType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
