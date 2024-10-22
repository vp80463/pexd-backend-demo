package com.a1stream.common.handler;


import com.a1stream.common.handler.impl.EinvoiceSoapResponseHandler;
import com.a1stream.common.handler.impl.TaxAuthoritySoapResponseHandler;
import com.a1stream.common.model.EInvoiceBO;
import com.a1stream.common.model.InvoiceSoapResponseResult;
import com.a1stream.common.model.TaxAuthorityBO;
import com.a1stream.common.utils.WebserviceUtil;
import com.a1stream.common.wsdl.org.tempuri.YamahaPublishServiceSoap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author dong zhen
 */
public enum YamahaPublishSoapService {

    NORTH,
    SOUTH,
    CENTER,
    CENTERNT,
    DEFAULT;

    private YamahaPublishServiceSoap soap;
    private final Log log = LogFactory.getLog(this.getClass());

    public static YamahaPublishSoapService of(String area, String wsdlUrl) {
        if(area != null) {
            String searching = area.toUpperCase();
            YamahaPublishSoapService f = YamahaPublishSoapService.valueOf(searching);
            if(f != null) {
                if(f.soap == null) {
                    f.soap = getSoapApi(wsdlUrl);
                }
                return f;
            }
        }
        return YamahaPublishSoapService.DEFAULT;
    }

    /**
     * Request e-invoice soap webservice
     *
     * @param eInvoiceModel  the request parameters
     * @param failureMessage the message when it's failure
     * @return response result
     */
    public InvoiceSoapResponseResult processEInvoice(EInvoiceBO eInvoiceModel, String failureMessage) {
        // Unknown area.
        if(this.soap == null) {
          return InvoiceSoapResponseResult.failure("Invalid input area!");
        }

        try {
            String responseText = soap.importAndPublishInv(eInvoiceModel.getAccount(),
                                                            eInvoiceModel.getACpass(),
                                                            eInvoiceModel.getXmlInvData(),
                                                            eInvoiceModel.getUsername(),
                                                            eInvoiceModel.getPass(),
                                                            eInvoiceModel.getPattern(),
                                                            eInvoiceModel.getSerial(),
                                                            eInvoiceModel.getConvert()
                                                        );
            return EinvoiceSoapResponseHandler.INSTANCE.handleResponseText(responseText);
        } catch (Exception e) {
            log.error("Failed to invoke E-Invoice soap webservice!", e);
            return InvoiceSoapResponseResult.failure(failureMessage);
        }
    }

    /**
     * Request tax authority soap webservice
     *
     * @param taxAuthorityModel the request parameters
     * @param failureMessage the message when it's failure
     * @return response result
     */
    public InvoiceSoapResponseResult processTaxAuthority(TaxAuthorityBO taxAuthorityModel, String failureMessage) {
        // Unknown area.
        if(this.soap == null) {
            return InvoiceSoapResponseResult.failure("Invalid input area!");
        }

        try {
            String responseText = this.soap.getMCCQThueByFkeysNoXMLSign(taxAuthorityModel.getAccount(),
                                                                        taxAuthorityModel.getACpass(),
                                                                        taxAuthorityModel.getUsername(),
                                                                        taxAuthorityModel.getPass(),
                                                                        taxAuthorityModel.getPattern(),
                                                                        taxAuthorityModel.getFkeys()
                                                                      );
            if(responseText == null || responseText.length() ==0) {
                return InvoiceSoapResponseResult.success("Unknown response text!");
            }
            return TaxAuthoritySoapResponseHandler.INSTANCE.handleResponseText(responseText);
        } catch (Exception e) {
            log.error("Failed to invoke Tax-Authority soap webservice!", e);
            return InvoiceSoapResponseResult.failure(failureMessage);
        }
    }

    private static YamahaPublishServiceSoap getSoapApi(String url) {
        return WebserviceUtil.findWebservice(url, YamahaPublishServiceSoap.class);
    }
}
