
package com.a1stream.common.wsdl.org.tempuri;

import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebEndpoint;
import jakarta.xml.ws.WebServiceClient;
import jakarta.xml.ws.WebServiceFeature;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;


/**
 * This class was generated by the JAX-WS RI.
 * This class represents a web service client for the PublishServiceDN.
 * It is generated by the JAX-WS RI and extends the Service class.
 *
 * @author dong zhen
 */
@WebServiceClient(name = "PublishService", targetNamespace = "http://tempuri.org/", wsdlLocation = "classpath:wsdl/publishserviceDN.wsdl")
public class PublishServiceDN extends Service {
    /**
     * Static URL for the WSDL location.
     */
    private static final URL PUBLISHSERVICE_WSDL_LOCATION;
    /**
     * Logger instance for logging warnings.
     */
    private static final Logger logger = Logger.getLogger(com.a1stream.common.wsdl.org.tempuri.PublishServiceDN.class.getName());

    static {
        URL url = null;
        try {
//            // Get the base URL of the class
//            URL baseUrl = com.a1stream.web.app.wsdl.org.tempuri.PublishServiceDN.class.getResource(".");
//            // Construct the URL for the WSDL location
//            url = new URL(baseUrl, "file:/usr/local/wildfly-9.0.2.Final/publishserviceDN.wsdl");
            // Get the WSDL file from the classpath
            url = PublishServiceDN.class.getClassLoader().getResource("wsdl/publishserviceDN.wsdl");
            if (url == null) {
                throw new MalformedURLException("WSDL file not found in classpath");
            }
        } catch (MalformedURLException e) {
            // Log a warning if the URL cannot be created
            logger.warning("Failed to create URL for the wsdl Location: 'classpath:wsdl/publishserviceDN.wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        // Assign the URL to the static variable
        PUBLISHSERVICE_WSDL_LOCATION = url;
    }

    /**
     * Constructor with WSDL location and service name.
     *
     * @param wsdlLocation The URL of the WSDL
     * @param serviceName The QName of the service
     */
    public PublishServiceDN(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    /**
     * Default constructor using the static WSDL location and service name.
     */
    public PublishServiceDN() {
        super(PUBLISHSERVICE_WSDL_LOCATION, new QName("http://tempuri.org/", "PublishService"));
    }

    /**
     * Getter for the PublishServiceSoap port.
     *
     * @return An instance of PublishServiceSoap
     */
    @WebEndpoint(name = "PublishServiceSoap")
    public PublishServiceSoap getPublishServiceSoap() {
        return super.getPort(new QName("http://tempuri.org/", "PublishServiceSoap"), PublishServiceSoap.class);
    }

    /**
     * Getter for the PublishServiceSoap port with additional features.
     *
     * @param features A list of WebServiceFeature to configure on the proxy
     * @return An instance of PublishServiceSoap
     */
    @WebEndpoint(name = "PublishServiceSoap")
    public PublishServiceSoap getPublishServiceSoap(WebServiceFeature... features) {
        return super.getPort(new QName("http://tempuri.org/", "PublishServiceSoap"), PublishServiceSoap.class, features);
    }
}
