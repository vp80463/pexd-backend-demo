
package com.a1stream.common.wsdl.org.tempuri;

import jakarta.xml.ws.*;

import javax.xml.namespace.QName;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * This class was generated by the XML-WS Tools.
 * XML-WS Tools 4.0.2
 * Generated source version: 3.0
 *
 * @author dong zhen
 */
@WebServiceClient(name = "PublishService", targetNamespace = "http://tempuri.org/", wsdlLocation = "classpath:wsdl/publishserviceDN.wsdl")
public class PublishService extends Service {

    private static final URL PUBLISHSERVICE_WSDL_LOCATION;
    private static final WebServiceException PUBLISHSERVICE_EXCEPTION;
    private static final QName PUBLISHSERVICE_QNAME = new QName("http://tempuri.org/", "PublishService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("classpath:wsdl/publishserviceDN.wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        PUBLISHSERVICE_WSDL_LOCATION = url;
        PUBLISHSERVICE_EXCEPTION = e;
    }

    public PublishService() {
        super(__getWsdlLocation(), PUBLISHSERVICE_QNAME);
    }

    public PublishService(WebServiceFeature... features) {
        super(__getWsdlLocation(), PUBLISHSERVICE_QNAME, features);
    }

    public PublishService(URL wsdlLocation) {
        super(wsdlLocation, PUBLISHSERVICE_QNAME);
    }

    public PublishService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, PUBLISHSERVICE_QNAME, features);
    }

    public PublishService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public PublishService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns PublishServiceSoap
     */
    @WebEndpoint(name = "PublishServiceSoap")
    public com.a1stream.common.wsdl.org.tempuri.PublishServiceSoap getPublishServiceSoap() {
        return super.getPort(new QName("http://tempuri.org/", "PublishServiceSoap"), com.a1stream.common.wsdl.org.tempuri.PublishServiceSoap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link jakarta.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns PublishServiceSoap
     */
    @WebEndpoint(name = "PublishServiceSoap")
    public com.a1stream.common.wsdl.org.tempuri.PublishServiceSoap getPublishServiceSoap(WebServiceFeature... features) {
        return super.getPort(new QName("http://tempuri.org/", "PublishServiceSoap"), com.a1stream.common.wsdl.org.tempuri.PublishServiceSoap.class, features);
    }

    private static URL __getWsdlLocation() {
        if (PUBLISHSERVICE_EXCEPTION!= null) {
            throw PUBLISHSERVICE_EXCEPTION;
        }
        return PUBLISHSERVICE_WSDL_LOCATION;
    }

}
