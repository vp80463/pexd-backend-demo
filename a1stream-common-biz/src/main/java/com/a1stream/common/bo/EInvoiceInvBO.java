package com.a1stream.common.bo;

import com.a1stream.domain.bo.common.EInvoiceInvoiceBO;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class EInvoiceInvBO {

    @XmlElement(name = "key")
    private String key;

    @XmlElement(name = "Invoice")
    private EInvoiceInvoiceBO invoice;
}
