//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Aenderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2018.04.13 um 02:21:13 PM CEST 
//


package eu.opends.opendrive.geometryGenerator;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java-Klasse fuer road complex type.
 * 
 * <p>Das folgende Schemafragment gibt den erwarteten Content an, der in dieser Klasse enthalten ist.
 * 
 * <pre>
 * &lt;complexType name="road">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="start" type="{http://opends.eu/geometryDescription}startType" minOccurs="0"/>
 *         &lt;element name="geometries" type="{http://opends.eu/geometryDescription}geometriesType"/>
 *         &lt;element name="noOfLanes" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="width" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="speedLimit" type="{http://www.w3.org/2001/XMLSchema}double"/>
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
@XmlType(name = "road", propOrder = {

})
public class Road {

    protected StartType start;
    @XmlElement(required = true)
    protected GeometriesType geometries;
    protected int noOfLanes;
    protected double width;
    protected double speedLimit;
    @XmlAttribute(name = "id")
    protected String id;

    /**
     * Ruft den Wert der start-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link StartType }
     *     
     */
    public StartType getStart() {
        return start;
    }

    /**
     * Legt den Wert der start-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link StartType }
     *     
     */
    public void setStart(StartType value) {
        this.start = value;
    }

    /**
     * Ruft den Wert der geometries-Eigenschaft ab.
     * 
     * @return
     *     possible object is
     *     {@link GeometriesType }
     *     
     */
    public GeometriesType getGeometries() {
        return geometries;
    }

    /**
     * Legt den Wert der geometries-Eigenschaft fest.
     * 
     * @param value
     *     allowed object is
     *     {@link GeometriesType }
     *     
     */
    public void setGeometries(GeometriesType value) {
        this.geometries = value;
    }

    /**
     * Ruft den Wert der noOfLanes-Eigenschaft ab.
     * 
     */
    public int getNoOfLanes() {
        return noOfLanes;
    }

    /**
     * Legt den Wert der noOfLanes-Eigenschaft fest.
     * 
     */
    public void setNoOfLanes(int value) {
        this.noOfLanes = value;
    }

    /**
     * Ruft den Wert der width-Eigenschaft ab.
     * 
     */
    public double getWidth() {
        return width;
    }

    /**
     * Legt den Wert der width-Eigenschaft fest.
     * 
     */
    public void setWidth(double value) {
        this.width = value;
    }

    /**
     * Ruft den Wert der speedLimit-Eigenschaft ab.
     * 
     */
    public double getSpeedLimit() {
        return speedLimit;
    }

    /**
     * Legt den Wert der speedLimit-Eigenschaft fest.
     * 
     */
    public void setSpeedLimit(double value) {
        this.speedLimit = value;
    }

    /**
     * Ruft den Wert der id-Eigenschaft ab.
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
     * Legt den Wert der id-Eigenschaft fest.
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
