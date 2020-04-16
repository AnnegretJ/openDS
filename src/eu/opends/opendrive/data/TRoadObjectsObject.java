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
 * <p>Java class for t_road_objects_object complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_objects_object">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="repeat" type="{}t_road_objects_object_repeat" minOccurs="0"/>
 *         &lt;element name="outline" type="{}t_road_objects_object_outlines_outline" minOccurs="0"/>
 *         &lt;element name="outlines" type="{}t_road_objects_object_outlines" minOccurs="0"/>
 *         &lt;element name="material" type="{}t_road_objects_object_material" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="validity" type="{}t_road_objects_object_laneValidity" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="parkingSpace" type="{}t_road_objects_object_parkingSpace" minOccurs="0"/>
 *         &lt;element name="markings" type="{}t_road_objects_object_markings" minOccurs="0"/>
 *         &lt;element name="borders" type="{}t_road_objects_object_borders" minOccurs="0"/>
 *         &lt;group ref="{}g_additionalData"/>
 *       &lt;/sequence>
 *       &lt;attribute name="type" type="{}e_objectType" />
 *       &lt;attribute name="subtype" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="dynamic" use="required" type="{}t_yesNo" />
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="id" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="s" use="required" type="{}t_grEqZero" />
 *       &lt;attribute name="t" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="zOffset" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="validLength" use="required" type="{}t_grEqZero" />
 *       &lt;attribute name="orientation" use="required" type="{}e_orientation" />
 *       &lt;attribute name="hdg" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="pitch" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="roll" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="height" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="length" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="width" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="radius" type="{http://www.w3.org/2001/XMLSchema}double" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_objects_object", propOrder = {
    "repeat",
    "outline",
    "outlines",
    "material",
    "validity",
    "parkingSpace",
    "markings",
    "borders",
    "userData",
    "include",
    "dataQuality"
})
public class TRoadObjectsObject {

    protected TRoadObjectsObjectRepeat repeat;
    protected TRoadObjectsObjectOutlinesOutline outline;
    protected TRoadObjectsObjectOutlines outlines;
    protected List<TRoadObjectsObjectMaterial> material;
    protected List<TRoadObjectsObjectLaneValidity> validity;
    protected TRoadObjectsObjectParkingSpace parkingSpace;
    protected TRoadObjectsObjectMarkings markings;
    protected TRoadObjectsObjectBorders borders;
    protected List<TUserData> userData;
    protected List<TInclude> include;
    protected TDataQuality dataQuality;
    @XmlAttribute(name = "type")
    protected EObjectType type;
    @XmlAttribute(name = "subtype")
    protected String subtype;
    @XmlAttribute(name = "dynamic", required = true)
    protected TYesNo dynamic;
    @XmlAttribute(name = "name")
    protected String name;
    @XmlAttribute(name = "id", required = true)
    protected String id;
    @XmlAttribute(name = "s", required = true)
    protected double s;
    @XmlAttribute(name = "t", required = true)
    protected double t;
    @XmlAttribute(name = "zOffset", required = true)
    protected double zOffset;
    @XmlAttribute(name = "validLength", required = true)
    protected double validLength;
    @XmlAttribute(name = "orientation", required = true)
    protected String orientation;
    @XmlAttribute(name = "hdg", required = true)
    protected double hdg;
    @XmlAttribute(name = "pitch", required = true)
    protected double pitch;
    @XmlAttribute(name = "roll", required = true)
    protected double roll;
    @XmlAttribute(name = "height", required = true)
    protected double height;
    @XmlAttribute(name = "length")
    protected Double length;
    @XmlAttribute(name = "width")
    protected Double width;
    @XmlAttribute(name = "radius")
    protected Double radius;

    /**
     * Gets the value of the repeat property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadObjectsObjectRepeat }
     *     
     */
    public TRoadObjectsObjectRepeat getRepeat() {
        return repeat;
    }

    /**
     * Sets the value of the repeat property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadObjectsObjectRepeat }
     *     
     */
    public void setRepeat(TRoadObjectsObjectRepeat value) {
        this.repeat = value;
    }

    /**
     * Gets the value of the outline property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadObjectsObjectOutlinesOutline }
     *     
     */
    public TRoadObjectsObjectOutlinesOutline getOutline() {
        return outline;
    }

    /**
     * Sets the value of the outline property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadObjectsObjectOutlinesOutline }
     *     
     */
    public void setOutline(TRoadObjectsObjectOutlinesOutline value) {
        this.outline = value;
    }

    /**
     * Gets the value of the outlines property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadObjectsObjectOutlines }
     *     
     */
    public TRoadObjectsObjectOutlines getOutlines() {
        return outlines;
    }

    /**
     * Sets the value of the outlines property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadObjectsObjectOutlines }
     *     
     */
    public void setOutlines(TRoadObjectsObjectOutlines value) {
        this.outlines = value;
    }

    /**
     * Gets the value of the material property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the material property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMaterial().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TRoadObjectsObjectMaterial }
     * 
     * 
     */
    public List<TRoadObjectsObjectMaterial> getMaterial() {
        if (material == null) {
            material = new ArrayList<TRoadObjectsObjectMaterial>();
        }
        return this.material;
    }

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
     * Gets the value of the parkingSpace property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadObjectsObjectParkingSpace }
     *     
     */
    public TRoadObjectsObjectParkingSpace getParkingSpace() {
        return parkingSpace;
    }

    /**
     * Sets the value of the parkingSpace property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadObjectsObjectParkingSpace }
     *     
     */
    public void setParkingSpace(TRoadObjectsObjectParkingSpace value) {
        this.parkingSpace = value;
    }

    /**
     * Gets the value of the markings property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadObjectsObjectMarkings }
     *     
     */
    public TRoadObjectsObjectMarkings getMarkings() {
        return markings;
    }

    /**
     * Sets the value of the markings property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadObjectsObjectMarkings }
     *     
     */
    public void setMarkings(TRoadObjectsObjectMarkings value) {
        this.markings = value;
    }

    /**
     * Gets the value of the borders property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadObjectsObjectBorders }
     *     
     */
    public TRoadObjectsObjectBorders getBorders() {
        return borders;
    }

    /**
     * Sets the value of the borders property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadObjectsObjectBorders }
     *     
     */
    public void setBorders(TRoadObjectsObjectBorders value) {
        this.borders = value;
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
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link EObjectType }
     *     
     */
    public EObjectType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link EObjectType }
     *     
     */
    public void setType(EObjectType value) {
        this.type = value;
    }

    /**
     * Gets the value of the subtype property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubtype() {
        return subtype;
    }

    /**
     * Sets the value of the subtype property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubtype(String value) {
        this.subtype = value;
    }

    /**
     * Gets the value of the dynamic property.
     * 
     * @return
     *     possible object is
     *     {@link TYesNo }
     *     
     */
    public TYesNo getDynamic() {
        return dynamic;
    }

    /**
     * Sets the value of the dynamic property.
     * 
     * @param value
     *     allowed object is
     *     {@link TYesNo }
     *     
     */
    public void setDynamic(TYesNo value) {
        this.dynamic = value;
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
     * Gets the value of the t property.
     * 
     */
    public double getT() {
        return t;
    }

    /**
     * Sets the value of the t property.
     * 
     */
    public void setT(double value) {
        this.t = value;
    }

    /**
     * Gets the value of the zOffset property.
     * 
     */
    public double getZOffset() {
        return zOffset;
    }

    /**
     * Sets the value of the zOffset property.
     * 
     */
    public void setZOffset(double value) {
        this.zOffset = value;
    }

    /**
     * Gets the value of the validLength property.
     * 
     */
    public double getValidLength() {
        return validLength;
    }

    /**
     * Sets the value of the validLength property.
     * 
     */
    public void setValidLength(double value) {
        this.validLength = value;
    }

    /**
     * Gets the value of the orientation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOrientation() {
        return orientation;
    }

    /**
     * Sets the value of the orientation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOrientation(String value) {
        this.orientation = value;
    }

    /**
     * Gets the value of the hdg property.
     * 
     */
    public double getHdg() {
        return hdg;
    }

    /**
     * Sets the value of the hdg property.
     * 
     */
    public void setHdg(double value) {
        this.hdg = value;
    }

    /**
     * Gets the value of the pitch property.
     * 
     */
    public double getPitch() {
        return pitch;
    }

    /**
     * Sets the value of the pitch property.
     * 
     */
    public void setPitch(double value) {
        this.pitch = value;
    }

    /**
     * Gets the value of the roll property.
     * 
     */
    public double getRoll() {
        return roll;
    }

    /**
     * Sets the value of the roll property.
     * 
     */
    public void setRoll(double value) {
        this.roll = value;
    }

    /**
     * Gets the value of the height property.
     * 
     */
    public double getHeight() {
        return height;
    }

    /**
     * Sets the value of the height property.
     * 
     */
    public void setHeight(double value) {
        this.height = value;
    }

    /**
     * Gets the value of the length property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getLength() {
        return length;
    }

    /**
     * Sets the value of the length property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setLength(Double value) {
        this.length = value;
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
     * Gets the value of the radius property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getRadius() {
        return radius;
    }

    /**
     * Sets the value of the radius property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setRadius(Double value) {
        this.radius = value;
    }

}
