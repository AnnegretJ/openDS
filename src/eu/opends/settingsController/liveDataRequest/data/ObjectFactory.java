//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.8-b130911.1802 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2020.12.01 at 10:36:04 AM CET 
//


package eu.opends.settingsController.liveDataRequest.data;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the eu.opends.settingsController.liveDataRequest.data package. 
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

    private final static QName _LiveDataRequest_QNAME = new QName("", "liveDataRequest");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: eu.opends.settingsController.liveDataRequest.data
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link LiveDataRequest }
     * 
     */
    public LiveDataRequest createLiveDataRequest() {
        return new LiveDataRequest();
    }

    /**
     * Create an instance of {@link Location }
     * 
     */
    public Location createLocation() {
        return new Location();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LiveDataRequest }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "liveDataRequest")
    public JAXBElement<LiveDataRequest> createLiveDataRequest(LiveDataRequest value) {
        return new JAXBElement<LiveDataRequest>(_LiveDataRequest_QNAME, LiveDataRequest.class, null, value);
    }

}