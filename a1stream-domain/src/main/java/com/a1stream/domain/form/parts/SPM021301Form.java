/**
 *
 */
package com.a1stream.domain.form.parts;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.parts.SPM021301BO;

import lombok.Getter;
import lombok.Setter;

/**
* 功能描述:
*
* @author mid2259
*/
@Getter
@Setter
public class SPM021301Form extends BaseForm {

	private static final long serialVersionUID = 1L;
    private String point;
    private Long pointId;
    private String dateFrom;
    private String dateTo;
    private String notContact;
    private String noneBOOnly;
    private String consumer;
    private Long consumerId;
    private String mobile;
    private String orderNo;

    private List<SPM021301BO> content;

    private boolean pageFlg = true;
}
