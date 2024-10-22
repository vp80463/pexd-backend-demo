package com.a1stream.parts.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.constants.PJConstants.ProductClsType;
import com.a1stream.common.constants.PJConstants.SpStockStatus;
import com.a1stream.domain.bo.parts.SPM040201BO;
import com.a1stream.domain.bo.parts.SPM040202BO;
import com.a1stream.domain.form.parts.SPM040201Form;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.PartsRopqMonthlyVO;
import com.a1stream.domain.vo.ProductStockStatusVO;
import com.a1stream.domain.vo.ReorderGuidelineVO;
import com.a1stream.parts.service.SPM0402Service;
import com.alibaba.excel.util.StringUtils;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid2259
*/
@Component
public class SPM0402Facade {

    private static final String ERROR_MSG = "Error: ";
    private static final String WARNING_MSG = "Warning: ";

    @Resource
    private SPM0402Service spm0402Service;

    public Page<SPM040201BO> searchRoqRopList(SPM040201Form model,String siteId){

        //检查pointId是否存在
        if(ObjectUtils.isEmpty(model.getPointId())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.point"), model.getPoint(), CodedMessageUtils.getMessage("label.tablePointInfo")}));
        }

        //查看partsId是否存在
        if(ObjectUtils.isEmpty(model.getPartsId())&& StringUtils.isNotBlank(model.getPart())){
             throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.partsNo"), model.getPart(), CodedMessageUtils.getMessage("label.productInformation")}));
        }

        //将roproqData的productId全部抽取出来
        Page<SPM040201BO> pageData = spm0402Service.searchRoqRopList(model, siteId);
        List<SPM040201BO> roproqData = pageData.getContent();
        Set<Long> partsIds = roproqData.stream().map(SPM040201BO::getPartsId).collect(Collectors.toSet());

        //获取J1Total和J2Total
        Set<String> jTotalStatus = new HashSet<>();
        jTotalStatus.add(PJConstants.RopRoqParameter.KEY_PARTSJ1TOTAL);
        jTotalStatus.add(PJConstants.RopRoqParameter.KEY_PARTSJ2TOTAL);
        List<PartsRopqMonthlyVO> jTotalData = spm0402Service.getJ1TotalAndJ2Total(siteId,partsIds,jTotalStatus);

        //获取各状态库存下的数量
        Set<String> productStockStatusTypes = new HashSet<>();
        //stockQty
        productStockStatusTypes.add(SpStockStatus.ONHAND_QTY.getCodeDbid());
        //boQty
        productStockStatusTypes.add(SpStockStatus.BO_QTY.getCodeDbid());
        //onPurchaseQty
        productStockStatusTypes.add(SpStockStatus.EO_ONPURCHASE_QTY.getCodeDbid());
        productStockStatusTypes.add(SpStockStatus.RO_ONPURCHASE_QTY.getCodeDbid());
        productStockStatusTypes.add(SpStockStatus.HO_ONPURCHASE_QTY.getCodeDbid());
        productStockStatusTypes.add(SpStockStatus.WO_ONPURCHASE_QTY.getCodeDbid());
        List<ProductStockStatusVO> stockQuantitys = spm0402Service.findStockStatusList(siteId,model.getPointId(),partsIds,ProductClsType.PART.getCodeDbid(),productStockStatusTypes);

        for (SPM040201BO bo : pageData.getContent()) {

            //根据productId和roleType拿取数据并赋值
            Optional<PartsRopqMonthlyVO> j1Data = jTotalData.stream().filter(o -> bo.getPartsId().equals(o.getProductId()) && StringUtils.equals(PJConstants.RopRoqParameter.KEY_PARTSJ1TOTAL, o.getRopqType()) ).findFirst();
            Optional<PartsRopqMonthlyVO> j2Data = jTotalData.stream().filter(o -> bo.getPartsId().equals(o.getProductId()) && StringUtils.equals(PJConstants.RopRoqParameter.KEY_PARTSJ2TOTAL, o.getRopqType()) ).findFirst();
            if (j1Data.isPresent()) {
                bo.setTotalOne(j1Data.get().getStringValue());
            }
            if (j2Data.isPresent()) {
                bo.setTotalTwo(j2Data.get().getStringValue());
            }

            //默认当没值时为0
            bo.setStockQty(new BigDecimal(CommonConstants.INTEGER_ZERO));
            bo.setBoQty(new BigDecimal(CommonConstants.INTEGER_ZERO));
            //根据productId和productStockStatusType拿取数据并赋值
            Optional<ProductStockStatusVO> stockQty = stockQuantitys.stream().filter(o -> bo.getPartsId().equals(o.getProductId()) && StringUtils.equals(SpStockStatus.ONHAND_QTY.getCodeDbid(), o.getProductStockStatusType()) ).findFirst();
            if (stockQty.isPresent()) {
                bo.setStockQty(stockQty.get().getQuantity());
            }

            Optional<ProductStockStatusVO> boQty = stockQuantitys.stream().filter(o -> bo.getPartsId().equals(o.getProductId()) && StringUtils.equals(SpStockStatus.BO_QTY.getCodeDbid(), o.getProductStockStatusType()) ).findFirst();
            if (boQty.isPresent()) {
                bo.setBoQty(boQty.get().getQuantity());
            }

            //onPurchaseQty是四种不同状态下的库存数量累加后的值
            BigDecimal onPurchaseQty = new BigDecimal(CommonConstants.INTEGER_ZERO);
            Optional<ProductStockStatusVO> onePurchaseQty = stockQuantitys.stream().filter(o -> bo.getPartsId().equals(o.getProductId()) && StringUtils.equals(SpStockStatus.EO_ONPURCHASE_QTY.getCodeDbid(), o.getProductStockStatusType()) ).findFirst();
            Optional<ProductStockStatusVO> twoPurchaseQty = stockQuantitys.stream().filter(o -> bo.getPartsId().equals(o.getProductId()) && StringUtils.equals(SpStockStatus.RO_ONPURCHASE_QTY.getCodeDbid(), o.getProductStockStatusType()) ).findFirst();
            Optional<ProductStockStatusVO> threePurchaseQty = stockQuantitys.stream().filter(o -> bo.getPartsId().equals(o.getProductId()) && StringUtils.equals(SpStockStatus.HO_ONPURCHASE_QTY.getCodeDbid(), o.getProductStockStatusType()) ).findFirst();
            Optional<ProductStockStatusVO> fourPurchaseQty = stockQuantitys.stream().filter(o -> bo.getPartsId().equals(o.getProductId()) && StringUtils.equals(SpStockStatus.WO_ONPURCHASE_QTY.getCodeDbid(), o.getProductStockStatusType()) ).findFirst();
            if (onePurchaseQty.isPresent()) {
                onPurchaseQty = onPurchaseQty.add(onePurchaseQty.get().getQuantity());
            }
            if (twoPurchaseQty.isPresent()) {
                onPurchaseQty = onPurchaseQty.add(twoPurchaseQty.get().getQuantity());
            }
            if (threePurchaseQty.isPresent()) {
                onPurchaseQty = onPurchaseQty.add(threePurchaseQty.get().getQuantity());
            }
            if (fourPurchaseQty.isPresent()) {
                onPurchaseQty = onPurchaseQty.add(fourPurchaseQty.get().getQuantity());
            }
            bo.setOnPurchaseQty(onPurchaseQty);
        }

        return pageData;
    }

    public void deleteRopRoq(SPM040201Form model) {

        //删除数据
        List<SPM040201BO> deleteList = model.getRopRoqData().getRemoveRecords();
        ReorderGuidelineVO reorderGuidelineVO = spm0402Service.findRoqRopByReorderGuidelineId(deleteList.get(CommonConstants.INTEGER_ZERO).getReorderGuidelineId());
        if (reorderGuidelineVO.getUpdateCount().equals(deleteList.get(CommonConstants.INTEGER_ZERO).getUpdateCount())) {
            spm0402Service.deleteRoqRopInfo(reorderGuidelineVO);
        }else{
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00314", new String[]{ CodedMessageUtils.getMessage("label.productNumber"),deleteList.get(CommonConstants.INTEGER_ZERO).getPartsCd(),CodedMessageUtils.getMessage("label.productInformation")}));
        }

    }

    public void editRoqRopList(SPM040201Form model,String siteId) {

        this.validateData(model,siteId);

        //将修改的数据转换为key为Id，value为自身的Map
        List<ReorderGuidelineVO> updateVoList = new ArrayList<>();
        List<SPM040201BO> updateList = model.getRopRoqData().getUpdateRecords();
        Map<Long, SPM040201BO> updateMap = new HashMap<>();
        for (SPM040201BO spm040201bo : updateList) {
            updateMap.put(spm040201bo.getReorderGuidelineId(), spm040201bo);
        }

        if (!updateMap.isEmpty()) {

            //获取待修改原数据随后修改
            List<ReorderGuidelineVO> reorderGuidelineVO = spm0402Service.findRoqRopByReorderGuidelineId(updateMap.keySet());
            for (ReorderGuidelineVO vo : reorderGuidelineVO) {
                SPM040201BO updateModel = updateMap.get(vo.getReorderGuidelineId());
                //check是否已被更新
                if(!updateModel.getUpdateCount().equals(vo.getUpdateCount())){
                    throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00314", new String[]{ CodedMessageUtils.getMessage("label.productNumber"),updateModel.getPartsCd(),CodedMessageUtils.getMessage("label.productInformation")}));
                }
                vo.setRopRoqManualFlag(updateModel.getSign());
                vo.setReorderPoint(updateModel.getRop());
                vo.setReorderQty(updateModel.getRoq());
                updateVoList.add(vo);
            }
        }

        //新增数据
        List<SPM040201BO> insertList = model.getRopRoqData().getInsertRecords();
        List<ReorderGuidelineVO> insertVoList = new ArrayList<>();
        for (SPM040201BO spm040201bo : insertList) {
            ReorderGuidelineVO vo = new ReorderGuidelineVO();
            vo.setSiteId(siteId);
            vo.setFacilityId(model.getPointId());
            vo.setProductId(spm040201bo.getPartsId());
            vo.setReorderPoint(spm040201bo.getRop());
            vo.setReorderQty(spm040201bo.getRoq());
            vo.setRopRoqManualFlag(spm040201bo.getSign());
            insertVoList.add(vo);
        }

        spm0402Service.editRoqRopInfo(updateVoList,insertVoList);

    }

    private void validateData(SPM040201Form model,String siteId){

        List<SPM040201BO> updateList = model.getRopRoqData().getUpdateRecords();
        List<SPM040201BO> insertList = model.getRopRoqData().getInsertRecords();
        List<SPM040201BO> allList = new ArrayList<>();
        allList.addAll(insertList);
        allList.addAll(updateList);

        List<String> siteList = new ArrayList<>();
        siteList.add(CommonConstants.CHAR_DEFAULT_SITE_ID);
        siteList.add(siteId);

        List<String> productNoList = new ArrayList<>();
        for (SPM040201BO member : allList) {
            productNoList.add(member.getPartsCd());
        }

        //将查询出来的产品转换为Key为ProductNo,value为Id的Map
        List<MstProductVO> productVos = spm0402Service.findProductByProductNo(productNoList, siteList);
        Map<String, Long> productMaps = productVos.stream().collect(Collectors.toMap(MstProductVO::getProductCd, MstProductVO::getProductId));

        //根据productMaps的产品Id查询ReorderGuideline中的信息，并转换成map
        List<ReorderGuidelineVO> reorderGuidelines = spm0402Service.findReorderGuidelineByProductNo(new ArrayList<>(productMaps.values()), siteList,model.getPointId());
        List<Long> productIdList = reorderGuidelines.stream().map(o -> o.getProductId()).collect(Collectors.toList());
        Map<Long, Long> reorderGuidelinesMaps = reorderGuidelines.stream().collect(Collectors.toMap(ReorderGuidelineVO::getProductId, ReorderGuidelineVO::getReorderGuidelineId));


        for (SPM040201BO member : allList) {

            //检查数据本身是否重复
            if (productNoList.indexOf(member.getPartsCd()) != productNoList.lastIndexOf(member.getPartsCd())) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00301", new String[]{ CodedMessageUtils.getMessage("label.productNumber")}));
            }

            //检查产品是否存在
            if (!productMaps.containsKey(member.getPartsCd())) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[]{ CodedMessageUtils.getMessage("label.productNumber"),member.getPartsCd(),CodedMessageUtils.getMessage("label.productInformation")}));
            }

            //合法性校验
            if (ObjectUtils.isEmpty(member.getRop())) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10326", new String[]{ CodedMessageUtils.getMessage("label.rop")}));
            }else if(member.getRop().compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00201", new String[]{ CodedMessageUtils.getMessage("label.rop"),CommonConstants.CHAR_ZERO}));
            }

            if (ObjectUtils.isEmpty(member.getRoq())) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10326", new String[]{ CodedMessageUtils.getMessage("label.roq")}));
            }else if(member.getRoq().compareTo(BigDecimal.ONE) < 0) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00201", new String[]{ CodedMessageUtils.getMessage("label.roq"),CommonConstants.CHAR_ONE}));
            }

        }

       //检查新增数据是否已在数据库中存在
        for (SPM040201BO bo: insertList) {
            if(productIdList.contains(bo.getPartsId())){
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00301", new String[]{ CodedMessageUtils.getMessage("label.productNumber")}));
            }
        }

      //检查修改数据是否已在数据库中存在
        for (SPM040201BO bo : updateList) {
            if (reorderGuidelinesMaps.containsKey(bo.getPartsId()) && !reorderGuidelinesMaps.get(bo.getPartsId()).equals(bo.getReorderGuidelineId()) ) {
                    throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00301", new String[]{ CodedMessageUtils.getMessage("label.productNumber")}));
                }

        }

    }

    public void saveFile(SPM040201Form form,String siteId) {

        List<String> siteList = new ArrayList<>();
        siteList.add(CommonConstants.CHAR_DEFAULT_SITE_ID);
        siteList.add(siteId);

        List<String> productNoList = form.getImportList().stream().map(o -> o.getPartsCd()).collect(Collectors.toList());


        List<MstProductVO> vos = spm0402Service.findProductByProductNo(productNoList, siteList);

        Map<String, Long> maps = vos.stream().collect(Collectors.toMap(MstProductVO::getProductCd, MstProductVO::getProductId));

        List<ReorderGuidelineVO> insertVoList = new ArrayList<>();
        for (SPM040202BO spm040201bo : form.getImportList()) {
            ReorderGuidelineVO vo = new ReorderGuidelineVO();
            vo.setSiteId(siteId);
            vo.setFacilityId(Long.valueOf(form.getOtherProperty().toString()));
            vo.setProductId(maps.get(spm040201bo.getPartsCd()));
            vo.setReorderPoint(spm040201bo.getRop());
            vo.setReorderQty(spm040201bo.getRoq());
            vo.setRopRoqManualFlag(spm040201bo.getSign());
            insertVoList.add(vo);
        }

        spm0402Service.editRoqRopInfo(null,insertVoList);
    }

    public void checkFile(SPM040201Form form,String siteId) {

        List<String> siteList = new ArrayList<>();
        siteList.add(CommonConstants.CHAR_DEFAULT_SITE_ID);
        siteList.add(siteId);

        List<String> productNoList = form.getImportList().stream().map(o -> o.getPartsCd()).collect(Collectors.toList());

        //将查询出来的产品转换为Key为ProductNo,value为Id的Map
        List<MstProductVO> vos = spm0402Service.findProductByProductNo(productNoList, siteList);
        Map<String, Long> productMaps = vos.stream().collect(Collectors.toMap(MstProductVO::getProductCd, MstProductVO::getProductId));

        //根据productMaps的产品Id查询ReorderGuideline中的信息，并抽取ProductId转换成list
        List<ReorderGuidelineVO> reorderGuidelines = spm0402Service.findReorderGuidelineByProductNo(new ArrayList<>(productMaps.values()), siteList,Long.parseLong(form.getOtherProperty().toString()));
        List<Long> productIdList = reorderGuidelines.stream().map(o -> o.getProductId()).collect(Collectors.toList());

        form.getImportList().forEach(member -> {

            List<String> error          = new ArrayList<>();
            List<Object[]> errorParam   = new ArrayList<>();
            List<String> warning        = new ArrayList<>();
            List<Object[]> warningParam = new ArrayList<>();

            //检查产品是否存在
            if (!productMaps.containsKey(member.getPartsCd())) {
                error.add("M.E.00303");
                errorParam.add(new Object[]{CodedMessageUtils.getMessage("label.productNumber"),member.getPartsCd(),CodedMessageUtils.getMessage("label.productInformation")});
            }

            //检查数据是否重复
            if (productNoList.indexOf(member.getPartsCd()) != productNoList.lastIndexOf(member.getPartsCd())) {
                error.add("M.E.00301");
                errorParam.add(new Object[]{CodedMessageUtils.getMessage("label.productNumber")});
            }

            //检查表里是否已存在数据
            if(productIdList.contains(productMaps.get(member.getPartsCd()))){
                error.add("M.E.00301");
                errorParam.add(new Object[]{CodedMessageUtils.getMessage("label.productNumber")});
            }

            //合法性校验
            if (ObjectUtils.isEmpty(member.getRop())) {
                error.add("M.E.10326");
                errorParam.add(new Object[]{CodedMessageUtils.getMessage("label.rop")});
            }else if(member.getRop().compareTo(BigDecimal.ZERO) < 0) {
                error.add("M.E.00201");
                errorParam.add(new Object[]{CodedMessageUtils.getMessage("label.rop"),CommonConstants.CHAR_ZERO});
            }

            if (ObjectUtils.isEmpty(member.getRoq())) {
                error.add("M.E.10326");
                errorParam.add(new Object[]{CodedMessageUtils.getMessage("label.roq")});
            }else if(member.getRoq().compareTo(BigDecimal.ONE) < 0) {
                error.add("M.E.00201");
                errorParam.add(new Object[]{"label.roq",CommonConstants.CHAR_ONE});
            }
            if (ObjectUtils.isEmpty(member.getSign())) {
                error.add("M.E.10326");
                errorParam.add(new Object[]{CodedMessageUtils.getMessage("label.manualROPROQSign")});
            }else if(!(StringUtils.equals(member.getSign(), CommonConstants.CHAR_Y) || StringUtils.equals(member.getSign(), CommonConstants.CHAR_N))) {
                error.add("M.E.00350");
                errorParam.add(new Object[]{CodedMessageUtils.getMessage("label.manualROPROQSign"),member.getSign()});
            }

            member.setError(error);
            member.setErrorParam(errorParam);
            member.setWarning(warning);
            member.setWarningParam(warningParam);
         });
    }

    public Object getValidFileList(SPM040201Form form,String siteId) {

        List<String> siteList = new ArrayList<>();
        siteList.add(CommonConstants.CHAR_DEFAULT_SITE_ID);
        siteList.add(siteId);

        List<String> productNoList = form.getImportList().stream()
                                         .map(SPM040202BO::getPartsCd)
                                         .collect(Collectors.toList());

        //将查询出来的产品转换为Key为ProductNo,value为Id的Map
        List<MstProductVO> vos = spm0402Service.findProductByProductNo(productNoList, siteList);
        Map<String, Long> productMaps = vos.stream().collect(Collectors.toMap(MstProductVO::getProductCd, MstProductVO::getProductId));

        //根据productMaps的产品Id查询ReorderGuideline中的信息，并抽取ProductId转换成list
        List<ReorderGuidelineVO> reorderGuidelines = spm0402Service.findReorderGuidelineByProductNo(new ArrayList<>(productMaps.values()), siteList,Long.parseLong(form.getOtherProperty().toString()));
        List<Long> productIdList = reorderGuidelines.stream().map(o -> o.getProductId()).collect(Collectors.toList());

        form.getImportList().forEach(member -> {
            StringBuilder errorMsg = new StringBuilder(ERROR_MSG);
            StringBuilder warningMsg = new StringBuilder(WARNING_MSG);

            //检查产品是否存在
            if (!productMaps.containsKey(member.getPartsCd())) {
                errorMsg.append(CodedMessageUtils.getMessage("M.E.00303", new Object[]{CodedMessageUtils.getMessage("label.productNumber"), member.getPartsCd(), CodedMessageUtils.getMessage("label.productInformation")}));
            }

            //检查数据是否重复
            if (Collections.frequency(productNoList, member.getPartsCd()) > 1) {
                errorMsg.append(CodedMessageUtils.getMessage("M.E.00301", new Object[]{CodedMessageUtils.getMessage("label.productNumber")}));
            }

            //检查表里是否已存在数据
            if (productIdList.contains(productMaps.get(member.getPartsCd()))) {
                errorMsg.append(CodedMessageUtils.getMessage("M.E.00301", new Object[]{CodedMessageUtils.getMessage("label.productNumber")}));
            }

            //合法性校验
            if (ObjectUtils.isEmpty(member.getRop())) {
                errorMsg.append(CodedMessageUtils.getMessage("M.E.10326", new Object[]{CodedMessageUtils.getMessage("label.rop")}));
            }else if(member.getRop().compareTo(BigDecimal.ZERO) < 0) {
                errorMsg.append(CodedMessageUtils.getMessage("M.E.00201", new Object[]{CodedMessageUtils.getMessage("label.rop"),CommonConstants.CHAR_ZERO}));
            }

            if (ObjectUtils.isEmpty(member.getRoq())) {
                errorMsg.append(CodedMessageUtils.getMessage("M.E.10326", new Object[]{CodedMessageUtils.getMessage("label.roq")}));
            }else if(member.getRoq().compareTo(BigDecimal.ONE) < 0) {
                errorMsg.append(CodedMessageUtils.getMessage("M.E.00201", new Object[]{CodedMessageUtils.getMessage("label.roq"),CommonConstants.CHAR_ONE}));
            }
            if (ObjectUtils.isEmpty(member.getSign())) {
                errorMsg.append(CodedMessageUtils.getMessage("M.E.00350", new Object[]{CodedMessageUtils.getMessage("label.manualROPROQSign")}));
            }else if(!(StringUtils.equals(member.getSign(), CommonConstants.CHAR_Y) || StringUtils.equals(member.getSign(), CommonConstants.CHAR_N))) {
                errorMsg.append(CodedMessageUtils.getMessage("M.E.00350", new Object[]{CodedMessageUtils.getMessage("label.manualROPROQSign"),member.getSign()}));
            }

            // Check for other conditions and append messages
            member.setErrorMessage(errorMsg.toString());
            member.setWarningMessage(warningMsg.toString());
        });

        return form.getImportList();
    }

}
