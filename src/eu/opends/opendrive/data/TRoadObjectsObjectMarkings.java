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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for t_road_objects_object_markings complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_objects_object_markings">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="marking" type="{}t_road_objects_object_markings_marking" maxOccurs="unbounded"/>
 *         &lt;group ref="{}g_additionalData"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_objects_object_markings", propOrder = {
    "marking",
    "userData",
    "include",
    "dataQuality"
})
public class TRoadObjectsObjectMarkings {

    @XmlElement(required = true)
    protected List<TRoadObjectsObjectMarkingsMarking> marking;
    protected List<TUserData> userData;
    protected List<TInclude> include;
    protected TDataQuality dataQuality;

    /**
     * Gets the value of the marking property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the marking property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMarking().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadObjectsObjectMarkingsMarking }
     * 
     * 
     */
    public List<TRoadObjectsObjectMarkingsMarking> getMarking() {
        if (marking == null) {
            marking = new ArrayList<TRoadObjectsObjectMarkingsMarking>();
        }
        return this.marking;
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

}