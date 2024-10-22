package com.a1stream.master.facade;

import java.util.List;

import org.springframework.stereotype.Component;

import com.a1stream.domain.bo.master.CMM020801BO;
import com.a1stream.domain.form.master.CMM020801Form;
import com.a1stream.master.service.CMM0208Service;

import jakarta.annotation.Resource;

/**
* 功能描述:Trader Info List明细画面
*
* mid2330
* 2024年7月25日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/07/25   Liu Chaoran     New
*/
@Component
public class CMM0208Facade {

    @Resource
    private CMM0208Service cmm0208Service;

    public List<CMM020801BO> findTraderInfoList(CMM020801Form form, String siteId) {

        return cmm0208Service.findTraderInfoList(form, siteId);
    }
}