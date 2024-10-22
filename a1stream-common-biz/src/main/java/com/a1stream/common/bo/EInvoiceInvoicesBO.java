package com.a1stream.common.bo;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@XmlRootElement(name = "Invoices")
@XmlAccessorType(XmlAccessType.FIELD)
public class EInvoiceInvoicesBO {

    @XmlElement(name = "Inv")
    public List<EInvoiceInvBO> invModels;

}
