//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.04.08 at 08:54:08 PM CEST 
//


package eu.opends.opendrive.data;

import java.util.ArrayList;
import java.util.List;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for t_road_lanes_laneSection_lcr_lane_roadMark complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_lanes_laneSection_lcr_lane_roadMark">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="sway" type="{}t_road_lanes_laneSection_lcr_lane_roadMark_sway" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="type" type="{}t_road_lanes_laneSection_lcr_lane_roadMark_type" minOccurs="0"/>
 *         &lt;element name="explicit" type="{}t_road_lanes_laneSection_lcr_lane_roadMark_explicit" minOccurs="0"/>
 *         &lt;group ref="{}g_additionalData"/>
 *       &lt;/sequence>
 *       &lt;attribute name="sOffset" use="required" type="{}t_grEqZero" />
 *       &lt;attribute name="type" use="required" type="{}e_roadMarkType" />
 *       &lt;attribute name="weight" type="{}e_roadMarkWeight" />
 *       &lt;attribute name="color" use="required" type="{}e_roadMarkColor" />
 *       &lt;attribute name="material" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="width" type="{}t_grEqZero" />
 *       &lt;attribute name="laneChange" type="{}e_road_lanes_laneSection_lcr_lane_roadMark_laneChange" />
 *       &lt;attribute name="height" type="{http://www.w3.org/2001/XMLSchema}double" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_lanes_laneSection_lcr_lane_roadMark", propOrder = {
    "sway",
    "laneRoadMarkType",
    "explicit",
    "userData",
    "include",
    "dataQuality"
})
public class TRoadLanesLaneSectionLcrLaneRoadMark {

    protected List<TRoadLanesLaneSectionLcrLaneRoadMarkSway> sway;
    @XmlElement(name = "type")
    protected TRoadLanesLaneSectionLcrLaneRoadMarkType laneRoadMarkType;
    protected TRoadLanesLaneSectionLcrLaneRoadMarkExplicit explicit;
    protected List<TUserData> userData;
    protected List<TInclude> include;
    protected TDataQuality dataQuality;
    @XmlAttribute(name = "sOffset", required = true)
    protected double sOffset;
    @XmlAttribute(name = "type", required = true)
    protected ERoadMarkType type;
    @XmlAttribute(name = "weight")
    protected ERoadMarkWeight weight;
    @XmlAttribute(name = "color", required = true)
    protected ERoadMarkColor color;
    @XmlAttribute(name = "material")
    protected String material;
    @XmlAttribute(name = "width")
    protected Double width;
    @XmlAttribute(name = "laneChange")
    protected ERoadLanesLaneSectionLcrLaneRoadMarkLaneChange laneChange;
    @XmlAttribute(name = "height")
    protected Double height;

    /**
     * Gets the value of the sway property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the sway property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSway().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadLanesLaneSectionLcrLaneRoadMarkSway }
     * 
     * 
     */
    public List<TRoadLanesLaneSectionLcrLaneRoadMarkSway> getSway() {
        if (sway == null) {
            sway = new ArrayList<TRoadLanesLaneSectionLcrLaneRoadMarkSway>();
        }
        return this.sway;
    }

    /**
     * Gets the value of the laneRoadMarkType property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadLanesLaneSectionLcrLaneRoadMarkType }
     *     
     */
    public TRoadLanesLaneSectionLcrLaneRoadMarkType getLaneRoadMarkType() {
        return laneRoadMarkType;
    }

    /**
     * Sets the value of the laneRoadMarkType property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadLanesLaneSectionLcrLaneRoadMarkType }
     *     
     */
    public void setLaneRoadMarkType(TRoadLanesLaneSectionLcrLaneRoadMarkType value) {
        this.laneRoadMarkType = value;
    }

    /**
     * Gets the value of the explicit property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadLanesLaneSectionLcrLaneRoadMarkExplicit }
     *     
     */
    public TRoadLanesLaneSectionLcrLaneRoadMarkExplicit getExplicit() {
        return explicit;
    }

    /**
     * Sets the value of the explicit property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadLanesLaneSectionLcrLaneRoadMarkExplicit }
     *     
     */
    public void setExplicit(TRoadLanesLaneSectionLcrLaneRoadMarkExplicit value) {
        this.explicit = value;
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
     * Gets the value of the sOffset property.
     * 
     */
    public double getSOffset() {
        return sOffset;
    }

    /**
     * Sets the value of the sOffset property.
     * 
     */
    public void setSOffset(double value) {
        this.sOffset = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link ERoadMarkType }
     *     
     */
    public ERoadMarkType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link ERoadMarkType }
     *     
     */
    public void setType(ERoadMarkType value) {
        this.type = value;
    }

    /**
     * Gets the value of the weight property.
     * 
     * @return
     *     possible object is
     *     {@link ERoadMarkWeight }
     *     
     */
    public ERoadMarkWeight getWeight() {
        return weight;
    }

    /**
     * Sets the value of the weight property.
     * 
     * @param value
     *     allowed object is
     *     {@link ERoadMarkWeight }
     *     
     */
    public void setWeight(ERoadMarkWeight value) {
        this.weight = value;
    }

    /**
     * Gets the value of the color property.
     * 
     * @return
     *     possible object is
     *     {@link ERoadMarkColor }
     *     
     */
    public ERoadMarkColor getColor() {
        return color;
    }

    /**
     * Sets the value of the color property.
     * 
     * @param value
     *     allowed object is
     *     {@link ERoadMarkColor }
     *     
     */
    public void setColor(ERoadMarkColor value) {
        this.color = value;
    }

    /**
     * Gets the value of the material property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMaterial() {
        return material;
    }

    /**
     * Sets the value of the material property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMaterial(String value) {
        this.material = value;
    }

    /**
     * Gets the value of the width property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getWidth() {
        return width;
    }

    /**
     * Sets the value of the width property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setWidth(Double value) {
        this.width = value;
    }

    /**
     * Gets the value of the laneChange property.
     * 
     * @return
     *     possible object is
     *     {@link ERoadLanesLaneSectionLcrLaneRoadMarkLaneChange }
     *     
     */
    public ERoadLanesLaneSectionLcrLaneRoadMarkLaneChange getLaneChange() {
        return laneChange;
    }

    /**
     * Sets the value of the laneChange property.
     * 
     * @param value
     *     allowed object is
     *     {@link ERoadLanesLaneSectionLcrLaneRoadMarkLaneChange }
     *     
     */
    public void setLaneChange(ERoadLanesLaneSectionLcrLaneRoadMarkLaneChange value) {
        this.laneChange = value;
    }

    /**
     * Gets the value of the height property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setHeight(Double value) {
        this.height = value;
    }

}
