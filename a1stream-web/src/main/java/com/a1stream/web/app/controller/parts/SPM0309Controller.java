package com.a1stream.web.app.controller.parts;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.domain.bo.parts.SPM030901BO;
import com.a1stream.domain.form.parts.SPM030901Form;
import com.a1stream.parts.facade.SPM0309Facade;
import com.ymsl.solid.web.restful.json.model.RestProcessAware;

import jakarta.annotation.Resource;

/**
*
* 功能描述: 库存盘点时真实库存录入
*
* @author mid2215
*/
@RestController
@RequestMapping("parts/spm0309")
public class SPM0309Controller implements RestProcessAware{

    @Resource
    private SPM0309Facade spm0309Facade;

    /**
     * 根据条件查找部品真实库存
     */
    @PostMapping(value = "/findPartsActualStockList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<SPM030901BO> findPartsActualStockList(@RequestBody final SPM030901Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        return spm0309Facade.findPartsActualStockList(form, uc.getDealerCode());
    }

    /**
     * 删除部品库存
     */
    @PostMapping(value = "/deletePartsActualStock.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deletePartsActualStock(@RequestBody final SPM030901Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        spm0309Facade.deletePartsActualStock(form, uc.getDealerCode());
    }

    /**
     * 新建或修改部品的真实库存（侧边栏）
     */
    @PostMapping(value = "/newOrModifyPartsActualStock.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveOrUpdatePartsActualStock(@RequestBody final SPM030901Form model, @AuthenticationPrincipal final PJUserDetails uc) {

        spm0309Facade.saveOrUpdatePartsActualStock(model, uc);
    }

    /**
     * 批量修改部品真实库存（明细）
     */
    @PostMapping(value = "/editStockList.json", produces = MediaType.APPLICATION_JSON_VALUE)
    public void editPartsActualStockList(@RequestBody final SPM030901Form form, @AuthenticationPrincipal final PJUserDetails uc) {

        spm0309Facade.editPartsActualStockList(form, uc.getDealerCode());
    }
}