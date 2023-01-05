//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2018.11.22 at 03:20:44 PM CET 
//


package eu.opends.opendrive.roadGenerator;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for trafficType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="trafficType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="codriver" type="{http://opends.eu/roadDescription}codriverType"/>
 *         &lt;element name="vehicles" type="{http://opends.eu/roadDescription}vehiclesType" minOccurs="0"/>
 *         &lt;element name="pedestrians" type="{http://opends.eu/roadDescription}pedestriansType" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "trafficType", propOrder = {

})
public class TrafficType {

    @XmlElement(required = true)
    protected CodriverType codriver;
    protected VehiclesType vehicles;
    protected PedestriansType pedestrians;

    /**
     * Gets the value of the codriver property.
     * 
     * @return
     *     possible object is
     *     {@link CodriverType }
     *     
     */
    public CodriverType getCodriver() {
        return codriver;
    }

    /**
     * Sets the value of the codriver property.
     * 
     * @param value
     *     allowed object is
     *     {@link CodriverType }
     *     
     */
    public void setCodriver(CodriverType value) {
        this.codriver = value;
    }

    /**
     * Gets the value of the vehicles property.
     * 
     * @return
     *     possible object is
     *     {@link VehiclesType }
     *     
     */
    public VehiclesType getVehicles() {
        return vehicles;
    }

    /**
     * Sets the value of the vehicles property.
     * 
     * @param value
     *     allowed object is
     *     {@link VehiclesType }
     *     
     */
    public void setVehicles(VehiclesType value) {
        this.vehicles = value;
    }

    /**
     * Gets the value of the pedestrians property.
     * 
     * @return
     *     possible object is
     *     {@link PedestriansType }
     *     
     */
    public PedestriansType getPedestrians() {
        return pedestrians;
    }

    /**
     * Sets the value of the pedestrians property.
     * 
     * @param value
     *     allowed object is
     *     {@link PedestriansType }
     *     
     */
    public void setPedestrians(PedestriansType value) {
        this.pedestrians = value;
    }

}
