
package com.a1stream.common.wsdl.org.tempuri;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
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
 *         <element name="certSerial" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="certString" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="cusCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="username" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         <element name="pass" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "certSerial",
    "certString",
    "cusCode",
    "username",
    "pass"
})
@XmlRootElement(name = "setCusCert")
public class SetCusCert {

    protected String certSerial;

    protected String certString;

    protected String cusCode;

    protected String username;

    protected String pass;
}
