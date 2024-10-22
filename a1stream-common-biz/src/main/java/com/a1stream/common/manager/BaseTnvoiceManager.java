package com.a1stream.common.manager;

import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.handler.YamahaPublishSoapService;
import com.a1stream.common.model.EInvoiceBO;
import com.a1stream.common.model.InvoiceSoapResponseResult;
import jodd.util.StringUtil;
import org.springframework.stereotype.Component;

/**
 * @author dong zhen
 */
@Component
public class BaseTnvoiceManager {

    public InvoiceSoapResponseResult getEInvoiceResult(EInvoiceBO eInvoiceModel, String type, String area) {

        String pattern;
        String serail;
        String wsdlUrl = "http://cadminhntt78test.yamaha-motor.com.vn/yamahapublishservice.asmx?WSDL";
        pattern = StringUtil.equals(type, MstCodeConstants.InterfaceCodeTypeSub.KEY_EINVOICE_MC_RETAIL)? eInvoiceModel.getPatternSD() :eInvoiceModel.getPatternSPSV();
        serail = StringUtil.equals(type, MstCodeConstants.InterfaceCodeTypeSub.KEY_EINVOICE_MC_RETAIL)? eInvoiceModel.getSerialSD() :eInvoiceModel.getSerialSPSV();
        serail = "C" + this.getNowYear() + serail;
        eInvoiceModel.setPattern(pattern);
        eInvoiceModel.setSerial(serail);
        return YamahaPublishSoapService.of(area, wsdlUrl).processEInvoice(eInvoiceModel, "Connect to E Invoice failure! Please Issue again by CMM0716_01.");
    }

    private String getNowYear() {
        return String.valueOf(System.currentTimeMillis()).substring(2, 4);
    }
}
