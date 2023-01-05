//
// Diese Datei wurde mit der JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 generiert 
// Siehe <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Aenderungen an dieser Datei gehen bei einer Neukompilierung des Quellschemas verloren. 
// Generiert: 2018.04.13 um 02:21:13 PM CEST 
//


package eu.opends.opendrive.geometryGenerator;

import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.annotation.XmlElementDecl;
import jakarta.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the eu.opends.opendrive.geometryGenerator package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GeometryDescription_QNAME = new QName("http://opends.eu/geometryDescription", "geometryDescription");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: eu.opends.opendrive.geometryGenerator
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GeometryDescription }
     * 
     */
    public GeometryDescription createGeometryDescription() {
        return new GeometryDescription();
    }

    /**
     * Create an instance of {@link GeometriesType }
     * 
     */
    public GeometriesType createGeometriesType() {
        return new GeometriesType();
    }

    /**
     * Create an instance of {@link Road }
     * 
     */
    public Road createRoad() {
        return new Road();
    }

    /**
     * Create an instance of {@link LineType }
     * 
     */
    public LineType createLineType() {
        return new LineType();
    }

    /**
     * Create an instance of {@link StartType }
     * 
     */
    public StartType createStartType() {
        return new StartType();
    }

    /**
     * Create an instance of {@link SpiralType }
     * 
     */
    public SpiralType createSpiralType() {
        return new SpiralType();
    }

    /**
     * Create an instance of {@link ArcType }
     * 
     */
    public ArcType createArcType() {
        return new ArcType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GeometryDescription }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://opends.eu/geometryDescription", name = "geometryDescription")
    public JAXBElement<GeometryDescription> createGeometryDescription(GeometryDescription value) {
        return new JAXBElement<GeometryDescription>(_GeometryDescription_QNAME, GeometryDescription.class, null, value);
    }

}
