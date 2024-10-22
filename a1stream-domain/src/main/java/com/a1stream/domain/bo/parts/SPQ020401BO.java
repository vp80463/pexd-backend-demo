/**
 *
 */
package com.a1stream.domain.bo.parts;

import java.util.List;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

/**
* 功能描述:
*
* @author mid2259
*/
@Getter
@Setter
public class SPQ020401BO extends BaseBO {
	private static final long serialVersionUID = 1L;

    private List<SPQ020402BO> summaryContent;
    private List<SPQ020403BO> detailContent;
}
