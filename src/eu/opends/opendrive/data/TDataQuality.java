//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.04.08 at 08:54:08 PM CEST 
//


package eu.opends.opendrive.data;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;


/**
 * <p>Java class for t_dataQuality complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="t_dataQuality">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="error" type="{}t_dataQuality_Error" minOccurs="0"/>
 *         &lt;element name="rawData" type="{}t_dataQuality_RawData" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "t_dataQuality", propOrder = {
    "error",
    "rawData"
})
public class TDataQuality {

    protected TDataQualityError error;
    protected TDataQualityRawData rawData;

    /**
     * Gets the value of the error property.
     * 
     * @return
     *     possible object is
     *     {@link TDataQualityError }
     *     
     */
    public TDataQualityError getError() {
        return error;
    }

    /**
     * Sets the value of the error property.
     * 
     * @param value
     *     allowed object is
     *     {@link TDataQualityError }
     *     
     */
    public void setError(TDataQualityError value) {
        this.error = value;
    }

    /**
     * Gets the value of the rawData property.
     * 
     * @return
     *     possible object is
     *     {@link TDataQualityRawData }
     *     
     */
    public TDataQualityRawData getRawData() {
        return rawData;
    }

    /**
     * Sets the value of the rawData property.
     * 
     * @param value
     *     allowed object is
     *     {@link TDataQualityRawData }
     *     
     */
    public void setRawData(TDataQualityRawData value) {
        this.rawData = value;
    }

}
