package com.a1stream.unit.facade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.domain.bo.unit.CMM090101BO;
import com.a1stream.domain.form.unit.CMM090101Form;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.unit.service.CMM0901Service;
import com.alibaba.excel.util.StringUtils;
import com.nimbusds.oauth2.sdk.util.CollectionUtils;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;

/**
*
* 功能描述:
*
* @author mid2215
*/
@Component
public class CMM0901Facade {

    @Resource
    private CMM0901Service cmm0901Service;

    public List<CMM090101BO> findMcPriceList(CMM090101Form model) {

        return cmm0901Service.findMcPriceList(model);
    }

    public CMM090101Form checkFile(CMM090101Form form) {

        // 上传的数据
        List<CMM090101BO> importList = form.getImportList();

        if (CollectionUtils.isEmpty(importList)) {
            return form;
        }

        //取到上传的所有modelCode
        List<String> productCodes = importList.stream().map(CMM090101BO::getModelCode).collect(Collectors.toList());

        //用这些code取到相应的MstProduct
        List<MstProductVO> mstProductVOs = cmm0901Service.findByModelCodeIn(productCodes);

        //创建一个Map<modelCode,MstProduct>，用于补全上传数据的其他字段的信息，显示于页面
        Map<String, MstProductVO> mstProductMap = mstProductVOs.stream().collect(Collectors.toMap(MstProductVO::getProductCd,vo -> vo));

        StringBuilder errorMsg = new StringBuilder();
        List<String> error = new ArrayList<>();

        //补全信息
        for(CMM090101BO cmm090101bo : importList){

            MstProductVO mstProductVO = mstProductMap.get(cmm090101bo.getModelCode());

            if(mstProductVO != null){

                cmm090101bo.setModelName(mstProductVO.getSalesDescription());
                cmm090101bo.setRegistrationDate(mstProductVO.getRegistrationDate());
                cmm090101bo.setExpiredDate(mstProductVO.getExpiredDate());
                cmm090101bo.setUpdateCount(mstProductVO.getUpdateCount());
            }else{
                errorMsg.append(CodedMessageUtils.getMessage("M.E.00303"
                        , new Object[]{CodedMessageUtils.getMessage("label.modelCode")
                        , cmm090101bo.getModelCode()
                        , CodedMessageUtils.getMessage("label.tableProduct")}));
            }

            // 如果modify price 不大于0，则提示用户
            if(cmm090101bo.getPrice().compareTo(BigDecimal.ZERO) <= 0){
                errorMsg.append(CodedMessageUtils.getMessage("M.E.00200"
                        , new String[]{ CodedMessageUtils.getMessage("label.price") + CommonConstants.CHAR_LEFT_PARENTHESIS
                                + cmm090101bo.getPrice() + CommonConstants.CHAR_RIGHT_PARENTHESIS
                        , CommonConstants.CHAR_ZERO}));
            }
            cmm090101bo.setErrorMessage(errorMsg.toString());
            if (errorMsg.length() > 0) {
                error.add(errorMsg.toString());
            }
            cmm090101bo.setError(error);
        }
        return form;
    }

    public void editModelPriceList(CMM090101Form model) {

        // 将修改的数据转换为key为Id，value为自身的Map
        List<MstProductVO> updateVoList = new ArrayList<>();
        List<CMM090101BO> updateList = model.getModelPriceData();

        Map<Long, CMM090101BO> updateMap = new HashMap<>();

        // 对手动添加的数据进行数据补齐
        fillInformationByModelCode(updateList,false);
        processUpdateData(model, updateVoList, updateList, updateMap);

        cmm0901Service.editModelPriceInfo(updateVoList);

    }

    private void fillInformationByModelCode(List<CMM090101BO> addList, boolean importFlag) {

        //取到上传的所有modelCode
        List<String> productCodes = addList.stream().map(CMM090101BO::getModelCode).collect(Collectors.toList());

        //用这些code取到相应的MstProduct
        List<MstProductVO> mstProductVOs = cmm0901Service.findByModelCodeIn(productCodes);

        //创建一个Map<modelCode,MstProduct>，用于补全上传数据的其他字段的信息，显示于页面
        Map<String, MstProductVO> mstProductMap = mstProductVOs.stream().collect(Collectors.toMap(MstProductVO::getProductCd,vo -> vo));

        //补全信息
        for(CMM090101BO cmm090101bo : addList){

            MstProductVO mstProductVO = mstProductMap.get(cmm090101bo.getModelCode());

            if(mstProductVO != null){

                cmm090101bo.setModelName(mstProductVO.getSalesDescription());
                cmm090101bo.setRegistrationDate(mstProductVO.getRegistrationDate());
                cmm090101bo.setExpiredDate(mstProductVO.getExpiredDate());
                cmm090101bo.setUpdateCount(mstProductVO.getUpdateCount());

                // 上传的数据补全信息时，不将productId放进去，用于隐藏删除按钮
                if(!importFlag){
                    cmm090101bo.setProductId(mstProductVO.getProductId());
                }
            }else{
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10237", new String[]{cmm090101bo.getModelCode(), CodedMessageUtils.getMessage("label.modelCode")}));
            }
        }
    }

    private void processUpdateData(CMM090101Form model, List<MstProductVO> updateVoList, List<CMM090101BO> updateList,Map<Long, CMM090101BO> updateMap) {

        //取到所有modelCode
        List<String> productCodes = updateList.stream().map(CMM090101BO::getModelCode).collect(Collectors.toList());

        for (CMM090101BO cmm090101bo : updateList) {

            // 如果新建或者上传，存在重复code，则提示用户
            if(Collections.frequency(productCodes, cmm090101bo.getModelCode()) > 1){

                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00304", new String[] {CodedMessageUtils.getMessage("label.modelCode"), cmm090101bo.getModelCode()}));
            }

            // 如果modify price 不大于0，则提示用户
            if(cmm090101bo.getPrice().compareTo(BigDecimal.ZERO) <= 0){
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00200"
                        , new String[]{ CodedMessageUtils.getMessage("label.price") + CommonConstants.CHAR_LEFT_PARENTHESIS
                                + cmm090101bo.getPrice() + CommonConstants.CHAR_RIGHT_PARENTHESIS
                        , CommonConstants.CHAR_ZERO}));
            }

            updateMap.put(cmm090101bo.getProductId(), cmm090101bo);
        }

        if (!updateMap.isEmpty()) {

            // 获取待修改原数据随后修改
            List<MstProductVO> mstProductVOs = cmm0901Service.findByProductIds(updateMap.keySet());
            for (MstProductVO vo : mstProductVOs) {

                CMM090101BO updateModel = updateMap.get(vo.getProductId());

                // check是否已被更新
                if(!updateModel.getUpdateCount().equals(vo.getUpdateCount())){
                    throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00314", new String[]{ CodedMessageUtils.getMessage("label.model"),updateModel.getModelCode(),CodedMessageUtils.getMessage("label.modelInformation")}));
                }

                if(StringUtils.equals(model.getPriceType(), PJConstants.PriceCategory.GOODSRETAILPRICE.getCodeDbid())){
                    vo.setStdRetailPrice(updateModel.getPrice());
                }else if(StringUtils.equals(model.getPriceType(), PJConstants.PriceCategory.GOODSEMPLOYEEPRICE.getCodeDbid())){
                    vo.setStdWsPrice(updateModel.getPrice());
                }

                LocalDate currentDate = LocalDate.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);
                vo.setStdPriceUpdateDate(currentDate.format(formatter));
                updateVoList.add(vo);
            }
        }
    }
}
