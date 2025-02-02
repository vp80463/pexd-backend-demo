
package com.a1stream.common.wsdl.org.tempuri;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;
import jakarta.xml.bind.annotation.XmlSeeAlso;
import jakarta.xml.ws.RequestWrapper;
import jakarta.xml.ws.ResponseWrapper;


/**
 * This class was generated by the JAX-WS RI.
 * This interface represents the SOAP service contract for the YamahaPublishService.
 * It defines the methods that can be invoked remotely over the web.
 *
 * @author dong zhen
 */
@WebService(name = "YamahaPublishServiceSoap", targetNamespace = "http://tempuri.org/")
@XmlSeeAlso({
    ObjectFactory.class
})
public interface YamahaPublishServiceSoap {

    /**
     * Imports and publishes inventory data to the Yamaha system.
     *
     * @param account The account identifier for authentication.
     * @param aCpass The access password for authentication.
     * @param xmlInvData The inventory data in XML format.
     * @param username The username for authentication.
     * @param password The password for authentication.
     * @param pattern The pattern for the operation.
     * @param serial The serial number for the operation.
     * @param convert A flag indicating whether to convert the data.
     * @return A string representing the result of the operation.
     */
    @WebMethod(operationName = "ImportAndPublishInv", action = "http://tempuri.org/ImportAndPublishInv")
    @WebResult(name = "ImportAndPublishInvResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "ImportAndPublishInv", targetNamespace = "http://tempuri.org/", className = "com.ImportAndPublishInv")
    @ResponseWrapper(localName = "ImportAndPublishInvResponse", targetNamespace = "http://tempuri.org/", className = "com.ImportAndPublishInvResponse")
    public String importAndPublishInv(
        @WebParam(name = "Account", targetNamespace = "http://tempuri.org/")
        String account,
        @WebParam(name = "ACpass", targetNamespace = "http://tempuri.org/")
        String aCpass,
        @WebParam(name = "xmlInvData", targetNamespace = "http://tempuri.org/")
        String xmlInvData,
        @WebParam(name = "username", targetNamespace = "http://tempuri.org/")
        String username,
        @WebParam(name = "password", targetNamespace = "http://tempuri.org/")
        String password,
        @WebParam(name = "pattern", targetNamespace = "http://tempuri.org/")
        String pattern,
        @WebParam(name = "serial", targetNamespace = "http://tempuri.org/")
        String serial,
        @WebParam(name = "convert", targetNamespace = "http://tempuri.org/")
        int convert);

    /**
     * Retrieves tax information based on the provided fkeys.
     *
     * @param account The account identifier for authentication.
     * @param aCpass The access password for authentication.
     * @param username The username for authentication.
     * @param password The password for authentication.
     * @param pattern The pattern for the operation.
     * @param fkeys The fkeys to retrieve tax information for.
     * @return A string representing the tax information.
     */
    @WebMethod(operationName = "GetMCCQThueByFkeysNoXMLSign", action = "http://tempuri.org/GetMCCQThueByFkeysNoXMLSign")
    @WebResult(name = "GetMCCQThueByFkeysNoXMLSignResult", targetNamespace = "http://tempuri.org/")
    @RequestWrapper(localName = "GetMCCQThueByFkeysNoXMLSign", targetNamespace = "http://tempuri.org/", className = "com.GetMCCQThueByFkeysNoXMLSign")
    @ResponseWrapper(localName = "GetMCCQThueByFkeysNoXMLSignResponse", targetNamespace = "http://tempuri.org/", className = "com.GetMCCQThueByFkeysNoXMLSignResponse")
    public String getMCCQThueByFkeysNoXMLSign(
        @WebParam(name = "Account", targetNamespace = "http://tempuri.org/")
        String account,
        @WebParam(name = "ACpass", targetNamespace = "http://tempuri.org/")
        String aCpass,
        @WebParam(name = "username", targetNamespace = "http://tempuri.org/")
        String username,
        @WebParam(name = "password", targetNamespace = "http://tempuri.org/")
        String password,
        @WebParam(name = "pattern", targetNamespace = "http://tempuri.org/")
        String pattern,
        @WebParam(name = "fkeys", targetNamespace = "http://tempuri.org/")
        String fkeys);

}
