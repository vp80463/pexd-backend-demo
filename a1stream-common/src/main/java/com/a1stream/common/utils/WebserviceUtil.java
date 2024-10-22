package com.a1stream.common.utils;

import jakarta.jws.WebService;
import jakarta.xml.ws.Service;
import jakarta.xml.ws.WebServiceClient;

import javax.xml.namespace.QName;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author dong zhen
 */
public class WebserviceUtil {

    private static final String SUFFIX_SOAP = "Soap";

    /**
     *  Find the endport class with the annotationed <code>WebService</code>  in ".wsdl" file and
     *  proxy it.
     *
     * @param wsdl The location of remote ".wsdl" file.
     * @param returnClazz The endport class with <code>WebService</code> annotation.
     * @return The instance of provided <code>returnClazz</code>.
     */
    public static <T> T findWebservice(String wsdl, Class<T> returnClazz) {

        checkIfValidSoapClass(returnClazz);
        Class<? extends Service> src = guessServiceClass(returnClazz);
        return findWebservice(wsdl, returnClazz, src);
    }



    /**
     *  Find the endport class with the annotationed <code>WebService</code>  in ".wsdl" file and
     *  proxy it.
     *
     * @param wsdl The location of remote ".wsdl" file.
     * @param returnClazz The endport class with <code>WebService</code> annotation.
     * @param serviceClzz The client service class with <code>WebServiceClient</code> annotation.
     * @return The instance of provided <code>returnClazz</code>.
     */
    public static <T> T findWebservice(String wsdl, Class<T> returnClazz, Class<? extends Service> serviceClzz) {

        checkIfValidSoapClass(returnClazz);
        checkIfValidServiceClass(serviceClzz);

        URL url = getWsdlURL(wsdl);
        WebServiceClient c = serviceClzz.getAnnotation(WebServiceClient.class);
        QName qname = new QName(getNamespace(c), getServiceName(c));
        Service service = Service.create(url, qname);
        return service.getPort(returnClazz);
    }


    private static void checkIfValidServiceClass(Class<? extends Service> src) {
        asserts(src != null, "Can't find the class extends '" + Service.class + "'");
        asserts(isAnnotationPresent(src, WebServiceClient.class), "@WebServiceClient is not present on class '" + src + "'");
    }

    private static <T> void checkIfValidSoapClass(Class<T> returnClazz) {
        asserts(isAnnotationPresent(returnClazz, WebService.class), "@WebService is not present on class '" + returnClazz + "'");
    }

    private static void asserts(boolean actual, String errorMessage) {
        if (!actual) {
            throw new IllegalStateException(errorMessage);
        }
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Service> guessServiceClass(Class<?> soapClzz) {

        String cn = soapClzz.getName();
        if (cn.endsWith(SUFFIX_SOAP)) {
            String serviceClzzName = cn.substring(0, cn.lastIndexOf(SUFFIX_SOAP));
            return (Class<? extends Service>) forClass(serviceClzzName);
        }
        return null;
    }

    private static URL getWsdlURL(String wsdl) {
        URL url = null;
        try {
            url = new URL(wsdl);
        } catch (MalformedURLException e) {
            throw new RuntimeException(String.format("Webservice URL '%s' cannot connect!", wsdl), e);
        }
        return url;
    }

    private static Class<?> forClass(String className) {
        try {
            return Class.forName(className, false, WebserviceUtil.class.getClassLoader());
        } catch (Exception e1) {
            try {
                return Class.forName(className, false, Thread.currentThread().getContextClassLoader());
            } catch (Exception e2) {
                // skip
            }
        }
        return null;
    }

    private static boolean isAnnotationPresent(Class<?> clzz, Class<? extends Annotation> annotationClazz) {
        return clzz != null && clzz.isAnnotationPresent(annotationClazz);
    }

    private static String getNamespace(WebServiceClient c) {
        return (c == null) ? null : c.targetNamespace();
    }

    private static String getServiceName(WebServiceClient c) {
        return (c == null) ? null : c.name();
    }
}
