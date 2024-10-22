
package com.a1stream.common.wsdl.org.tempuri;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;


/**
 * <p>anonymous complex type的 Java 类。
 * 
 * <p>以下模式片段指定包含在此类中的预期内容。
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="GetMCCQThueByFkeysNoXMLSignResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 *
 * @author dong zhen
 */
@Setter
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "getMCCQThueByFkeysNoXMLSignResult"
})
@XmlRootElement(name = "GetMCCQThueByFkeysNoXMLSignResponse")
public class GetMCCQThueByFkeysNoXMLSignResponse {

    @XmlElement(name = "GetMCCQThueByFkeysNoXMLSignResult")
    protected String getMCCQThueByFkeysNoXMLSignResult;
}
