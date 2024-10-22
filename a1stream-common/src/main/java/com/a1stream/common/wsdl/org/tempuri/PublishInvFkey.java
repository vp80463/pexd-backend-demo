
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
 *         <element name="Account" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="ACpass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="lsFkey" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="userName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="pass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="pattern" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="serial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "account",
    "aCpass",
    "lsFkey",
    "userName",
    "pass",
    "pattern",
    "serial"
})
@XmlRootElement(name = "publishInvFkey")
public class PublishInvFkey {

    @XmlElement(name = "Account")
    protected String account;

    @XmlElement(name = "ACpass")
    protected String aCpass;

    protected String lsFkey;

    protected String userName;

    protected String pass;

    protected String pattern;

    protected String serial;
}
