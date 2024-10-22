/**
 *
 */
package com.a1stream.common.bo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;

import lombok.Getter;
import lombok.Setter;

/**
* 功能描述:
*
* @author mid2259
*/
@Getter
@Setter
public class TargetOrderItemModel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String siteId;
    private Long deliveryPointId;
    private Long orderId;
    private String orderNo;
    private Long orderItemId;
    private Integer itemSeqNo;
    private String allocateDueDate;
    private Long productId;
    private BigDecimal originalQuantity;
    private BigDecimal changedQuantity;

    public static class AllcoatedCancelPriorityComparator implements Comparator {

        @Override
        public int compare(Object obj0, Object obj1) {

            int dueDateCompareResult;
            int orderNoCompareResult;
            //Compare allocateDueDate
            dueDateCompareResult = ((TargetOrderItemModel)obj1).getAllocateDueDate()
                                   .compareTo(((TargetOrderItemModel)obj0).getAllocateDueDate());
            if(dueDateCompareResult == 0){
                //Compare orderNo.
                orderNoCompareResult = ((TargetOrderItemModel)obj1).getOrderNo().compareTo(((TargetOrderItemModel)obj0).getOrderNo());
                return orderNoCompareResult;
            }else{
                return dueDateCompareResult;
            }

        }
    }
}
