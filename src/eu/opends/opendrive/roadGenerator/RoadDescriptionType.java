//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.11.22 at 03:20:44 PM CET 
//


package eu.opends.opendrive.roadGenerator;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for roadDescriptionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="roadDescriptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="terrain" type="{http://opends.eu/roadDescription}terrainType"/>
 *         &lt;element name="segments" type="{http://opends.eu/roadDescription}segmentsType"/>
 *         &lt;element name="intersections" type="{http://opends.eu/roadDescription}intersectionsType" minOccurs="0"/>
 *         &lt;element name="traffic" type="{http://opends.eu/roadDescription}trafficType"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "roadDescriptionType", propOrder = {

})
@XmlRootElement(name="roadDescription", namespace="http://opends.eu/roadDescription")
public class RoadDescriptionType {

    @XmlElement(required = true)
    protected TerrainType terrain;
    @XmlElement(required = true)
    protected SegmentsType segments;
    protected IntersectionsType intersections;
    @XmlElement(required = true)
    protected TrafficType traffic;

    /**
     * Gets the value of the terrain property.
     * 
     * @return
     *     possible object is
     *     {@link TerrainType }
     *     
     */
    public TerrainType getTerrain() {
        return terrain;
    }

    /**
     * Sets the value of the terrain property.
     * 
     * @param value
     *     allowed object is
     *     {@link TerrainType }
     *     
     */
    public void setTerrain(TerrainType value) {
        this.terrain = value;
    }

    /**
     * Gets the value of the segments property.
     * 
     * @return
     *     possible object is
     *     {@link SegmentsType }
     *     
     */
    public SegmentsType getSegments() {
        return segments;
    }

    /**
     * Sets the value of the segments property.
     * 
     * @param value
     *     allowed object is
     *     {@link SegmentsType }
     *     
     */
    public void setSegments(SegmentsType value) {
        this.segments = value;
    }

    /**
     * Gets the value of the intersections property.
     * 
     * @return
     *     possible object is
     *     {@link IntersectionsType }
     *     
     */
    public IntersectionsType getIntersections() {
        return intersections;
    }

    /**
     * Sets the value of the intersections property.
     * 
     * @param value
     *     allowed object is
     *     {@link IntersectionsType }
     *     
     */
    public void setIntersections(IntersectionsType value) {
        this.intersections = value;
    }

    /**
     * Gets the value of the traffic property.
     * 
     * @return
     *     possible object is
     *     {@link TrafficType }
     *     
     */
    public TrafficType getTraffic() {
        return traffic;
    }

    /**
     * Sets the value of the traffic property.
     * 
     * @param value
     *     allowed object is
     *     {@link TrafficType }
     *     
     */
    public void setTraffic(TrafficType value) {
        this.traffic = value;
    }

}