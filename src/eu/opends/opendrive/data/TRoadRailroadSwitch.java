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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for t_road_railroad_switch complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_railroad_switch">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="mainTrack" type="{}t_road_railroad_switch_mainTrack"/>
 *         &lt;element name="sideTrack" type="{}t_road_railroad_switch_sideTrack"/>
 *         &lt;element name="partner" type="{}t_road_railroad_switch_partner" minOccurs="0"/>
 *         &lt;group ref="{}g_additionalData"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="position" use="required" type="{}e_road_railroad_switch_position" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_railroad_switch", propOrder = {
    "mainTrack",
    "sideTrack",
    "partner",
    "userData",
    "include",
    "dataQuality"
})
public class TRoadRailroadSwitch {

    @XmlElement(required = true)
    protected TRoadRailroadSwitchMainTrack mainTrack;
    @XmlElement(required = true)
    protected TRoadRailroadSwitchSideTrack sideTrack;
    protected TRoadRailroadSwitchPartner partner;
    protected List<TUserData> userData;
    protected List<TInclude> include;
    protected TDataQuality dataQuality;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "position", required = true)
    protected ERoadRailroadSwitchPosition position;

    /**
     * Gets the value of the mainTrack property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadRailroadSwitchMainTrack }
     *     
     */
    public TRoadRailroadSwitchMainTrack getMainTrack() {
        return mainTrack;
    }

    /**
     * Sets the value of the mainTrack property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadRailroadSwitchMainTrack }
     *     
     */
    public void setMainTrack(TRoadRailroadSwitchMainTrack value) {
        this.mainTrack = value;
    }

    /**
     * Gets the value of the sideTrack property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadRailroadSwitchSideTrack }
     *     
     */
    public TRoadRailroadSwitchSideTrack getSideTrack() {
        return sideTrack;
    }

    /**
     * Sets the value of the sideTrack property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadRailroadSwitchSideTrack }
     *     
     */
    public void setSideTrack(TRoadRailroadSwitchSideTrack value) {
        this.sideTrack = value;
    }

    /**
     * Gets the value of the partner property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadRailroadSwitchPartner }
     *     
     */
    public TRoadRailroadSwitchPartner getPartner() {
        return partner;
    }

    /**
     * Sets the value of the partner property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadRailroadSwitchPartner }
     *     
     */
    public void setPartner(TRoadRailroadSwitchPartner value) {
        this.partner = value;
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
     * Gets the value of the position property.
     * 
     * @return
     *     possible object is
     *     {@link ERoadRailroadSwitchPosition }
     *     
     */
    public ERoadRailroadSwitchPosition getPosition() {
        return position;
    }

    /**
     * Sets the value of the position property.
     * 
     * @param value
     *     allowed object is
     *     {@link ERoadRailroadSwitchPosition }
     *     
     */
    public void setPosition(ERoadRailroadSwitchPosition value) {
        this.position = value;
    }

}