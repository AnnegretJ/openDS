//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.04.08 at 08:54:08 PM CEST 
//


package eu.opends.opendrive.data;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for t_road_objects_bridge complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_objects_bridge">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="validity" type="{}t_road_objects_object_laneValidity" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;group ref="{}g_additionalData"/>
 *       &lt;/sequence>
 *       &lt;attribute name="s" use="required" type="{}t_grEqZero" />
 *       &lt;attribute name="length" use="required" type="{}t_grEqZero" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" use="required" type="{}e_bridgeType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_objects_bridge", propOrder = {
    "validity",
    "userData",
    "include",
    "dataQuality"
})
public class TRoadObjectsBridge {

    protected List<TRoadObjectsObjectLaneValidity> validity;
    protected List<TUserData> userData;
    protected List<TInclude> include;
    protected TDataQuality dataQuality;
    @XmlAttribute(name = "s", required = true)
    protected double s;
    @XmlAttribute(name = "length", required = true)
    protected double length;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "type", required = true)
    protected EBridgeType type;

    /**
     * Gets the value of the validity property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the validity property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValidity().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadObjectsObjectLaneValidity }
     * 
     * 
     */
    public List<TRoadObjectsObjectLaneValidity> getValidity() {
        if (validity == null) {
            validity = new ArrayList<TRoadObjectsObjectLaneValidity>();
        }
        return this.validity;
    }

    /**
     * Gets the value of the userData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the userData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getUserData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TUserData }
     * 
     * 
     */
    public List<TUserData> getUserData() {
        if (userData == null) {
            userData = new ArrayList<TUserData>();
        }
        return this.userData;
    }

    /**
     * Gets the value of the include property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the include property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInclude().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TInclude }
     * 
     * 
     */
    public List<TInclude> getInclude() {
        if (include == null) {
            include = new ArrayList<TInclude>();
        }
        return this.include;
    }

    /**
     * Gets the value of the dataQuality property.
     * 
     * @return
     *     possible object is
     *     {@link TDataQuality }
     *     
     */
    public TDataQuality getDataQuality() {
        return dataQuality;
    }

    /**
     * Sets the value of the dataQuality property.
     * 
     * @param value
     *     allowed object is
     *     {@link TDataQuality }
     *     
     */
    public void setDataQuality(TDataQuality value) {
        this.dataQuality = value;
    }

    /**
     * Gets the value of the s property.
     * 
     */
    public double getS() {
        return s;
    }

    /**
     * Sets the value of the s property.
     * 
     */
    public void setS(double value) {
        this.s = value;
    }

    /**
     * Gets the value of the length property.
     * 
     */
    public double getLength() {
        return length;
    }

    /**
     * Sets the value of the length property.
     * 
     */
    public void setLength(double value) {
        this.length = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
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

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link EBridgeType }
     *     
     */
    public EBridgeType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link EBridgeType }
     *     
     */
    public void setType(EBridgeType value) {
        this.type = value;
    }

}