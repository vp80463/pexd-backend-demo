
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
 *         <element name="ImportInvPatternResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
    "importInvPatternResult"
})
@XmlRootElement(name = "ImportInvPatternResponse")
public class ImportInvPatternResponse {

    @XmlElement(name = "ImportInvPatternResult")
    protected String importInvPatternResult;

}
