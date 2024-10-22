/**
 *
 */
package com.a1stream.domain.form.parts;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.parts.SPM021101BO;

import lombok.Getter;
import lombok.Setter;

/**
* 功能描述:
*
* @author mid2215
*/
@Getter
@Setter
public class SPM021101Form extends BaseForm {

    private Long pointId;
    private String pointNm;
    private String pointCd;

    private String duNo;
    private List<SPM021101BO> content;

}
