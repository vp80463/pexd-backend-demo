/**
 *
 */
package com.a1stream.domain.bo.parts;

import java.io.Serial;
import java.util.List;

import com.a1stream.common.model.BaseBO;

import lombok.Getter;
import lombok.Setter;

/**
* 功能描述:
*
* @author mid2330
*/
@Getter
@Setter
public class SPQ020601PrintBO extends BaseBO {

    @Serial
    private static final long serialVersionUID = 1L;

    private String point;

    private String date;

    private String boxNo;

    private String batchNo;

    private String pickingListNo;

    private List<SPQ020601BO> detailPrintList;

    private List<SPQ020601BO> detailPrintPickingList;
}
