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
 * <p>Java class for t_road_link_predecessorSuccessor complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_link_predecessorSuccessor">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;group ref="{}g_additionalData"/>
 *       &lt;attribute name="elementType" use="required" type="{}e_road_link_elementType" />
 *       &lt;attribute name="elementId" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="contactPoint" type="{}e_contactPoint" />
 *       &lt;attribute name="elementS" type="{}t_grEqZero" />
 *       &lt;attribute name="elementDir" type="{}e_elementDir" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_link_predecessorSuccessor", propOrder = {
    "userData",
    "include",
    "dataQuality"
})
public class TRoadLinkPredecessorSuccessor {

    protected List<TUserData> userData;
    protected List<TInclude> include;
    protected TDataQuality dataQuality;
    @XmlAttribute(name = "elementType", required = true)
    protected ERoadLinkElementType elementType;
    @XmlAttribute(name = "elementId", required = true)
    protected String elementId;
    @XmlAttribute(name = "contactPoint")
    protected EContactPoint contactPoint;
    @XmlAttribute(name = "elementS")
    protected Double elementS;
    @XmlAttribute(name = "elementDir")
    protected String elementDir;

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
     * Gets the value of the elementType property.
     * 
     * @return
     *     possible object is
     *     {@link ERoadLinkElementType }
     *     
     */
    public ERoadLinkElementType getElementType() {
        return elementType;
    }

    /**
     * Sets the value of the elementType property.
     * 
     * @param value
     *     allowed object is
     *     {@link ERoadLinkElementType }
     *     
     */
    public void setElementType(ERoadLinkElementType value) {
        this.elementType = value;
    }

    /**
     * Gets the value of the elementId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getElementId() {
        return elementId;
    }

    /**
     * Sets the value of the elementId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setElementId(String value) {
        this.elementId = value;
    }

    /**
     * Gets the value of the contactPoint property.
     * 
     * @return
     *     possible object is
     *     {@link EContactPoint }
     *     
     */
    public EContactPoint getContactPoint() {
        return contactPoint;
    }

    /**
     * Sets the value of the contactPoint property.
     * 
     * @param value
     *     allowed object is
     *     {@link EContactPoint }
     *     
     */
    public void setContactPoint(EContactPoint value) {
        this.contactPoint = value;
    }

    /**
     * Gets the value of the elementS property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getElementS() {
        return elementS;
    }

    /**
     * Sets the value of the elementS property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setElementS(Double value) {
        this.elementS = value;
    }

    /**
     * Gets the value of the elementDir property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getElementDir() {
        return elementDir;
    }

    /**
     * Sets the value of the elementDir property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setElementDir(String value) {
        this.elementDir = value;
    }

}
