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
 * <p>Java class for t_road_planView_geometry complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_road_planView_geometry">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="line" type="{}t_road_planView_geometry_line"/>
 *         &lt;element name="spiral" type="{}t_road_planView_geometry_spiral"/>
 *         &lt;element name="arc" type="{}t_road_planView_geometry_arc"/>
 *         &lt;element name="poly3" type="{}t_road_planView_geometry_poly3"/>
 *         &lt;element name="paramPoly3" type="{}t_road_planView_geometry_paramPoly3"/>
 *         &lt;group ref="{}g_additionalData"/>
 *       &lt;/choice>
 *       &lt;attribute name="s" use="required" type="{}t_grEqZero" />
 *       &lt;attribute name="x" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="y" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="hdg" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="length" use="required" type="{}t_grEqZero" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_road_planView_geometry", propOrder = {
    "line",
    "spiral",
    "arc",
    "poly3",
    "paramPoly3",
    "userData",
    "include",
    "dataQuality"
})
public class TRoadPlanViewGeometry {

    protected TRoadPlanViewGeometryLine line;
    protected TRoadPlanViewGeometrySpiral spiral;
    protected TRoadPlanViewGeometryArc arc;
    protected TRoadPlanViewGeometryPoly3 poly3;
    protected TRoadPlanViewGeometryParamPoly3 paramPoly3;
    protected List<TUserData> userData;
    protected List<TInclude> include;
    protected TDataQuality dataQuality;
    @XmlAttribute(name = "s", required = true)
    protected double s;
    @XmlAttribute(name = "x", required = true)
    protected double x;
    @XmlAttribute(name = "y", required = true)
    protected double y;
    @XmlAttribute(name = "hdg", required = true)
    protected double hdg;
    @XmlAttribute(name = "length", required = true)
    protected double length;

    /**
     * Gets the value of the line property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadPlanViewGeometryLine }
     *     
     */
    public TRoadPlanViewGeometryLine getLine() {
        return line;
    }

    /**
     * Sets the value of the line property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadPlanViewGeometryLine }
     *     
     */
    public void setLine(TRoadPlanViewGeometryLine value) {
        this.line = value;
    }

    /**
     * Gets the value of the spiral property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadPlanViewGeometrySpiral }
     *     
     */
    public TRoadPlanViewGeometrySpiral getSpiral() {
        return spiral;
    }

    /**
     * Sets the value of the spiral property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadPlanViewGeometrySpiral }
     *     
     */
    public void setSpiral(TRoadPlanViewGeometrySpiral value) {
        this.spiral = value;
    }

    /**
     * Gets the value of the arc property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadPlanViewGeometryArc }
     *     
     */
    public TRoadPlanViewGeometryArc getArc() {
        return arc;
    }

    /**
     * Sets the value of the arc property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadPlanViewGeometryArc }
     *     
     */
    public void setArc(TRoadPlanViewGeometryArc value) {
        this.arc = value;
    }

    /**
     * Gets the value of the poly3 property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadPlanViewGeometryPoly3 }
     *     
     */
    public TRoadPlanViewGeometryPoly3 getPoly3() {
        return poly3;
    }

    /**
     * Sets the value of the poly3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadPlanViewGeometryPoly3 }
     *     
     */
    public void setPoly3(TRoadPlanViewGeometryPoly3 value) {
        this.poly3 = value;
    }

    /**
     * Gets the value of the paramPoly3 property.
     * 
     * @return
     *     possible object is
     *     {@link TRoadPlanViewGeometryParamPoly3 }
     *     
     */
    public TRoadPlanViewGeometryParamPoly3 getParamPoly3() {
        return paramPoly3;
    }

    /**
     * Sets the value of the paramPoly3 property.
     * 
     * @param value
     *     allowed object is
     *     {@link TRoadPlanViewGeometryParamPoly3 }
     *     
     */
    public void setParamPoly3(TRoadPlanViewGeometryParamPoly3 value) {
        this.paramPoly3 = value;
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
     * Gets the value of the x property.
     * 
     */
    public double getX() {
        return x;
    }

    /**
     * Sets the value of the x property.
     * 
     */
    public void setX(double value) {
        this.x = value;
    }

    /**
     * Gets the value of the y property.
     * 
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the value of the y property.
     * 
     */
    public void setY(double value) {
        this.y = value;
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

}
