
package com.a1stream.common.wsdl.org.tempuri;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;


/**
 * <p>anonymous complex type�� Java �ࡣ</p>
 * 
 * <p>����ģʽƬ��ָ�������ڴ����е�Ԥ�����ݡ�</p>
 * 
 * <pre>{@code
 * <complexType>
 *   <complexContent>
 *     <restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       <sequence>
 *         <element name="XMLCusData" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="username" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="pass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="convert" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       </sequence>
 *     </restriction>
 *   </complexContent>
 * </complexType>
 * }</pre>
 * 
 *
 * @author dong zhen
 */
@Setter
@Getter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "xmlCusData",
    "username",
    "pass",
    "convert"
})
@XmlRootElement(name = "UpdateCus")
public class UpdateCus {

    @XmlElement(name = "XMLCusData")
    protected String xmlCusData;

    protected String username;

    protected String pass;

    @XmlElement(required = true, type = Integer.class, nillable = true)
    protected Integer convert;
}
