//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.11.22 at 03:20:44 PM CET 
//


package eu.opends.opendrive.roadGenerator;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for pedestrianType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="pedestrianType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="startPosition" type="{http://opends.eu/roadDescription}offroadPositionType"/>
 *         &lt;element name="targets" type="{http://opends.eu/roadDescription}targetsType"/>
 *         &lt;element name="triggerPosition" type="{http://opends.eu/roadDescription}onroadPositionType" minOccurs="0"/>
 *       &lt;/all>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pedestrianType", propOrder = {

})
public class PedestrianType {

    @XmlElement(required = true)
    protected OffroadPositionType startPosition;
    @XmlElement(required = true)
    protected TargetsType targets;
    protected OnroadPositionType triggerPosition;
    @XmlAttribute(name = "id")
    protected String id;

    /**
     * Gets the value of the startPosition property.
     * 
     * @return
     *     possible object is
     *     {@link OffroadPositionType }
     *     
     */
    public OffroadPositionType getStartPosition() {
        return startPosition;
    }

    /**
     * Sets the value of the startPosition property.
     * 
     * @param value
     *     allowed object is
     *     {@link OffroadPositionType }
     *     
     */
    public void setStartPosition(OffroadPositionType value) {
        this.startPosition = value;
    }

    /**
     * Gets the value of the targets property.
     * 
     * @return
     *     possible object is
     *     {@link TargetsType }
     *     
     */
    public TargetsType getTargets() {
        return targets;
    }

    /**
     * Sets the value of the targets property.
     * 
     * @param value
     *     allowed object is
     *     {@link TargetsType }
     *     
     */
    public void setTargets(TargetsType value) {
        this.targets = value;
    }

    /**
     * Gets the value of the triggerPosition property.
     * 
     * @return
     *     possible object is
     *     {@link OnroadPositionType }
     *     
     */
    public OnroadPositionType getTriggerPosition() {
        return triggerPosition;
    }

    /**
     * Sets the value of the triggerPosition property.
     * 
     * @param value
     *     allowed object is
     *     {@link OnroadPositionType }
     *     
     */
    public void setTriggerPosition(OnroadPositionType value) {
        this.triggerPosition = value;
    }

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

}
