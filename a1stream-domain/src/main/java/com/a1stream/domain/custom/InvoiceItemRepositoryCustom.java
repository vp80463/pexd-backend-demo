/**
 *
 */
package com.a1stream.domain.custom;

import java.util.List;

import org.springframework.data.domain.Page;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.parts.SPM020202FunctionBO;
import com.a1stream.domain.bo.parts.SPQ020302BO;
import com.a1stream.domain.bo.parts.SPQ020403BO;
import com.a1stream.domain.form.parts.SPM020202Form;
import com.a1stream.domain.form.parts.SPQ020302Form;
import com.a1stream.domain.form.parts.SPQ020401Form;


/**
* 功能描述:
*
* @author mid2259
*/
public interface InvoiceItemRepositoryCustom {

    List<SPQ020403BO> searchInvoiceInfoDetail(SPQ020401Form model);

    Page<SPQ020302BO> getInvoiceItemList(SPQ020302Form form, PJUserDetails uc);

    List<SPM020202FunctionBO> getSalesReturnHistoryDetailList(SPM020202Form form, String siteId);

}
