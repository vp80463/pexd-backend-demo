package com.a1stream.master.facade;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.bo.master.CMM050901BO;
import com.a1stream.domain.form.master.CMM050901DetailForm;
import com.a1stream.domain.form.master.CMM050901Form;
import com.a1stream.domain.vo.MstFacilityVO;
import com.a1stream.domain.vo.MstProductVO;
import com.a1stream.domain.vo.ProductOrderResultHistoryVO;
import com.a1stream.domain.vo.ProductOrderResultSummaryVO;
import com.a1stream.master.service.CMM0509Service;
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
public class CMM0509Facade {

    @Resource
    private CMM0509Service cmm0509Service;

    private static final String ERROR_MSG = "Error: ";
    private static final String WARNING_MSG = "Warning: ";

    public void editPartsDemand(CMM050901DetailForm updateModel,String siteId) {

        //将页面上n0 到 n24 转换为list
        List<BigDecimal> nList = new ArrayList<>(Arrays.asList(new BigDecimal[CommonConstants.INTEGER_TWELVE]));

        for (int index = CommonConstants.INTEGER_ZERO; index <= CommonConstants.INTEGER_TWENTY_FOUR; index++) {

            String name =CommonConstants.CHAR_SMALL_N+index;
            BigDecimal value = getProperty(updateModel,name);
            nList.add(index,value);
        }

        //待删除的数据,并且将其备份
        List<ProductOrderResultSummaryVO> deleteVOs = cmm0509Service.findSummaryVOByProductId(siteId, updateModel.getPartsId(), updateModel.getPointId());
        List<ProductOrderResultHistoryVO> backupVos = getBackupData(deleteVOs, siteId,updateModel.getPointId());

        //年份，月份以及待保存的数据
        String sysYear = LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_Y));
        String sysMonth = LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_M));
        Integer month = Integer.parseInt(sysMonth);
        List<ProductOrderResultSummaryVO> updateList = new ArrayList<>();
        ProductOrderResultSummaryVO vo = null;
        boolean newFlag = true;

        for (int i = CommonConstants.INTEGER_ZERO; i <= CommonConstants.INTEGER_TWENTY_FOUR; i++) {

            if(newFlag) {
                vo = new  ProductOrderResultSummaryVO();
                vo.setFacilityId(updateModel.getPointId());
                vo.setSiteId(siteId);
                vo.setProductId(updateModel.getPartsId());
                vo.setTargetYear(sysYear);
                newFlag = false;
            }

            //为ProductOrderResultSummaryVO的monthxxQuantity属性赋值
            String name = CommonConstants.GROUP_TYPE_MONTH+ String.format(CommonConstants.CHAR_ADDZEROFORMAT,month)+CommonConstants.CHAR_QUANTITY;
            BigDecimal value = nList.get(i);
            setProperty(vo, name, value);

            //月份为0时,将月份重置为12，年份-1，重新初始化VO
            month--;
            if (Objects.equals(month, CommonConstants.INTEGER_ZERO)) {
                month=CommonConstants.INTEGER_TWELVE;
                sysYear=Integer.toString(Integer.parseInt(sysYear) - CommonConstants.INTEGER_ONE);
                updateList.add(vo);
                newFlag=true;
            }

            //补齐末尾数据
            if(i == CommonConstants.INTEGER_TWENTY_FOUR) {
                updateList.add(vo);
            }
        }

        cmm0509Service.editProductOrderResultSummaryRepository(deleteVOs, updateList,backupVos);

    }

    public void deletePartsDemandList(CMM050901Form model,String siteId) {

        List<ProductOrderResultSummaryVO> productOrderResultSummaryVO;
        if(!ObjectUtils.isEmpty(model.getPartsDemandData().getRemoveRecords())) {

            CMM050901BO deleteModel = model.getPartsDemandData().getRemoveRecords().get(CommonConstants.INTEGER_ZERO);
            productOrderResultSummaryVO = cmm0509Service.findSummaryVOByProductId(siteId, deleteModel.getPartsId(), deleteModel.getPointId());
        }else {
            productOrderResultSummaryVO = cmm0509Service.findBySiteIdAndFacilityId(siteId, model.getPointId());
        }

        //根据删除数据转化为备份数据
        List<ProductOrderResultHistoryVO> backupVos = getBackupData(productOrderResultSummaryVO, siteId, model.getPointId());

        cmm0509Service.deleteProductOrderResultSummaryRepository(productOrderResultSummaryVO,backupVos);
    }

    public PageImpl<CMM050901BO> getPartsDemandList(CMM050901Form model,String siteId) {

        //检查pointId是否存在
        if(ObjectUtils.isEmpty(model.getPointId())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.point"), model.getPoint(), CodedMessageUtils.getMessage("label.tablePointInfo")}));
        }

        //查看partsId是否存在
        if(ObjectUtils.isEmpty(model.getPartsId())&& StringUtils.isNotBlank(model.getNewPart())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.partsNo"), model.getNewPart(), CodedMessageUtils.getMessage("label.productInformation")}));
        }


        MstFacilityVO mstFacilityVO = cmm0509Service.findByFacilityId(model.getPointId());

        String sysYear = LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_Y));
        String sysMonth = LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_M));

        List<String> yearList = new ArrayList<>();
        yearList.add(sysYear);
        yearList.add(Integer.toString(Integer.parseInt(sysYear) - CommonConstants.INTEGER_ONE));
        yearList.add(Integer.toString(Integer.parseInt(sysYear) - CommonConstants.INTEGER_TWO));

        //先分页
        PageImpl<ProductOrderResultSummaryVO> pageVo = cmm0509Service.searchProductOrderResult(model, siteId, yearList);

        //根据分页的查询结果再对数据进行处理
        List<Long> partsId = pageVo.stream().map(ProductOrderResultSummaryVO::getProductId).collect(Collectors.toList());
        List<ProductOrderResultSummaryVO> productOrderResultSummaryVO = cmm0509Service.findProductOrderResultSummaryRepositoryInYears(siteId, yearList, partsId, model.getPointId());


        Set<Long> productIdList = new HashSet<>();

        //Map<产品id_年份，List<月份>>
        Map<String, List<BigDecimal>> dataMap = new HashMap<>();
        for (ProductOrderResultSummaryVO vo : productOrderResultSummaryVO) {

            //循环十二次，分别对应十二个月，list[1]为1月的数据，list[2]为2月的数据，依次类推
            List<BigDecimal> list = new ArrayList<>(Arrays.asList(new BigDecimal[CommonConstants.INTEGER_TWELVE]));
            for (int i = CommonConstants.INTEGER_ONE; i <= CommonConstants.INTEGER_TWELVE; i++) {

                String name = CommonConstants.GROUP_TYPE_MONTH+ String.format(CommonConstants.CHAR_ADDZEROFORMAT,i)+CommonConstants.CHAR_QUANTITY;
                BigDecimal monthQty = getProperty(vo,name);
                list.add(i,monthQty);
            }

            //例如我想要找到A产品2024年五月的数据，可以理解为是DataMap.get(A_2024).get(5)
            dataMap.put(vo.getProductId()+CommonConstants.CHAR_UNDERSCORE+vo.getTargetYear(), list);

            if (!productIdList.contains(vo.getProductId())) {
                productIdList.add(vo.getProductId());
            }
        }

        List<MstProductVO> mstProductVOs = cmm0509Service.findByProductIdIn(productIdList);
        Map<Long, String> productMap= mstProductVOs.stream().collect(Collectors.toMap(MstProductVO::getProductId, MstProductVO::getProductCd));

        //返回给前台的list
        List<CMM050901BO> bolist=new ArrayList<>();


        for (Long productId : productIdList) {


            //每个新的productId都要重置年份和月份
            Integer month = Integer.parseInt(sysMonth);
            sysYear = LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_Y));

            //将result填满N0到N24的数据，若没有数据则填0
            List<BigDecimal> result = new ArrayList<>();
            for (int i = CommonConstants.INTEGER_ZERO; i <= CommonConstants.INTEGER_TWENTY_FOUR; i++) {

                List<BigDecimal> monthList = dataMap.get(productId+CommonConstants.CHAR_UNDERSCORE+sysYear);

                if (!ObjectUtils.isEmpty(monthList)) {
                    result.add(i,monthList.get(month));
                }else {
                    result.add(i,new BigDecimal(CommonConstants.INTEGER_ZERO));
                }

                //如果月份等于0时，年份-1，且月份变成12
                month--;
                if (Objects.equals(month, CommonConstants.INTEGER_ZERO)) {
                    month=CommonConstants.INTEGER_TWELVE;
                    sysYear=Integer.toString(Integer.parseInt(sysYear) - CommonConstants.INTEGER_ONE);
                }
            }

            CMM050901BO bo = new CMM050901BO();
            bo.setDealerCode(mstFacilityVO.getSiteId());
            bo.setPointCode(mstFacilityVO.getFacilityCd());
            bo.setPointId(mstFacilityVO.getFacilityId());
            bo.setPartsId(productId);
            bo.setPartsNo(productMap.get(productId));

            //填充n0到n24的数据
            for (int i = CommonConstants.INTEGER_ZERO; i <= CommonConstants.INTEGER_TWENTY_FOUR; i++) {

                String name = CommonConstants.CHAR_SMALL_N+i;
                BigDecimal value = result.get(i);
                setProperty(bo, name, value);
            }

            bolist.add(bo);
        }
        return new PageImpl<>(bolist, PageRequest.of(model.getCurrentPage() - 1, model.getPageSize()), pageVo.getTotalElements());
    }

    //根据删除的数据转换为备份数据（只保存第一次，若已经在历史表中存在则不保存）
    private List<ProductOrderResultHistoryVO> getBackupData(List<ProductOrderResultSummaryVO> deleteVOs,String siteId,Long pointId){

        //备份时间
        String backupDate = LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD));

        List<ProductOrderResultHistoryVO> backupVos = new ArrayList<>();

        if (!ObjectUtils.isEmpty(deleteVOs)) {

          //将deleteVos中的productId属性全部抽出
            List<Long> productIdList = deleteVOs.stream().map(o -> o.getProductId()).collect(Collectors.toList());

            //准备验证是否已存在
            List<ProductOrderResultHistoryVO> productHistoryVo = cmm0509Service.findProductResultHistory(productIdList, pointId, siteId);

            for (ProductOrderResultSummaryVO deleteVO : deleteVOs) {

                //如果在数据库以PointId,SiteId,ProductId同时符合的数据已经存在，则跳过
                if (productHistoryVo.stream().anyMatch(obj -> obj.getProductId().equals(deleteVO.getProductId()) && StringUtils.equals(obj.getSiteId(), siteId)  && obj.getFacilityId().equals(pointId))) {
                    continue;
                }

                ProductOrderResultHistoryVO historyVO = new ProductOrderResultHistoryVO();
                historyVO.setSiteId(siteId);
                historyVO.setFacilityId(deleteVO.getFacilityId());
                historyVO.setProductId(deleteVO.getProductId());
                historyVO.setTargetYear(deleteVO.getTargetYear());
                historyVO.setBackupDate(backupDate);

                //循环十二次，将ProductOrderResultSummaryVO的十二月份数据赋值给ProductOrderResultHistoryVO的十二月份数据
                for (int i = CommonConstants.INTEGER_ONE; i <= CommonConstants.INTEGER_TWELVE; i++) {

                    String getName = CommonConstants.GROUP_TYPE_MONTH+ String.format(CommonConstants.CHAR_ADDZEROFORMAT,i)+CommonConstants.CHAR_QUANTITY;
                    BigDecimal monthQty = getProperty(deleteVO,getName);

                    String setName = CommonConstants.GROUP_TYPE_MONTH+ String.format(CommonConstants.CHAR_ADDZEROFORMAT,i)+CommonConstants.CHAR_QTY;
                    setProperty(historyVO, setName, monthQty);

                }

                backupVos.add(historyVO);
            }
        }

        return backupVos;
    }

    //检查导入数据的合法性
    public void checkFile(CMM050901Form form,String siteId) {

        //通过页面传的pointId获取据点数据
        MstFacilityVO mstFacilityVO = cmm0509Service.findByFacilityId(Long.valueOf(form.getOtherProperty().toString()));

        //产品只需要考虑666N
        List<String> siteList = new ArrayList<>();
        siteList.add(CommonConstants.CHAR_DEFAULT_SITE_ID);

        //抽取所有productNo
        List<String> productNoList = form.getImportList().stream().map(o -> o.getPartsNo()).collect(Collectors.toList());

        List<MstProductVO> productVos = cmm0509Service.findProductByProductNo(productNoList, siteList);

        for (CMM050901BO member : form.getImportList()) {

            List<String> error          = new ArrayList<>();
            List<Object[]> errorParam   = new ArrayList<>();
            List<String> warning        = new ArrayList<>();
            List<Object[]> warningParam = new ArrayList<>();

            //检查数据是否重复
            if (Collections.frequency(productNoList, member.getPartsNo()) > 1) {
                error.add("M.E.00304");
                errorParam.add(new Object[]{CodedMessageUtils.getMessage("label.productNumber"),member.getPartsNo()});
            }

            if (ObjectUtils.isEmpty(member.getDealerCode())){
                error.add("M.E.10326");
                errorParam.add(new Object[]{CodedMessageUtils.getMessage("label.dealerCode")});
            }

            if (ObjectUtils.isEmpty(member.getPointCode())){
                error.add("M.E.10326");
                errorParam.add(new Object[]{CodedMessageUtils.getMessage("label.pointCode")});
            }

            if (ObjectUtils.isEmpty(member.getPartsNo())){
                error.add("M.E.10326");
                errorParam.add(new Object[]{CodedMessageUtils.getMessage("label.partsNo")});
            }

            //检查产品是否存在
            if (productVos.stream().noneMatch(obj -> StringUtils.equals(obj.getProductCd(), member.getPartsNo()) )) {
                error.add("M.E.00303");
                errorParam.add(new Object[]{CodedMessageUtils.getMessage("label.productNumber"),member.getPartsNo()
                                           ,CodedMessageUtils.getMessage("label.tableProduct")+CommonConstants.CHAR_LEFT_PARENTHESIS+CommonConstants.CHAR_DEFAULT_SITE_ID+CommonConstants.CHAR_RIGHT_PARENTHESIS});
            }

            //检查siteId是否一致
            if (!StringUtils.equals(member.getDealerCode(), siteId)) {
                error.add("M.E.00336");
                errorParam.add(new Object[]{member.getDealerCode()});
            }

            //检查PointCd是否一致
            if (!StringUtils.equals(member.getPointCode(), mstFacilityVO.getFacilityCd())) {
                error.add("M.E.00303");
                errorParam.add(new Object[]{CodedMessageUtils.getMessage("label.pointCode"),member.getPointCode(),CodedMessageUtils.getMessage("label.tablePointInfo")});
            }
            member.setError(error);
            member.setErrorParam(errorParam);
            member.setWarning(warning);
            member.setWarningParam(warningParam);
        }
    }

    public Object getValidFileList(CMM050901Form form,String siteId) {

        //通过页面传的pointId获取据点数据
        MstFacilityVO mstFacilityVO = cmm0509Service.findByFacilityId(Long.valueOf(form.getOtherProperty().toString()));

        //产品只需要考虑666N
        List<String> siteList = new ArrayList<>();
        siteList.add(CommonConstants.CHAR_DEFAULT_SITE_ID);

        //抽取所有productNo
        List<String> productNoList = form.getImportList().stream().map(o -> o.getPartsNo()).collect(Collectors.toList());

        List<MstProductVO> productVos = cmm0509Service.findProductByProductNo(productNoList, siteList);

        form.getImportList().forEach(member -> {
            StringBuilder errorMsg = new StringBuilder(ERROR_MSG);
            StringBuilder warningMsg = new StringBuilder(WARNING_MSG);

            //检查数据是否重复
            if (Collections.frequency(productNoList, member.getPartsNo()) > 1) {

                errorMsg.append(CodedMessageUtils.getMessage("M.E.00304", new Object[]{"label.productNumber",member.getPartsNo()}));
            }

            if (ObjectUtils.isEmpty(member.getDealerCode())){

                errorMsg.append(CodedMessageUtils.getMessage("M.E.10326", new Object[]{"label.dealerCode"}));
            }

            if (ObjectUtils.isEmpty(member.getPointCode())){

                errorMsg.append(CodedMessageUtils.getMessage("M.E.10326", new Object[]{"label.pointCode"}));
            }

            if (ObjectUtils.isEmpty(member.getPartsNo())){

                errorMsg.append(CodedMessageUtils.getMessage("M.E.10326", new Object[]{"label.partsNo"}));
            }

            //检查产品是否存在
            if (productVos.stream().noneMatch(obj -> StringUtils.equals(obj.getProductCd(), member.getPartsNo()) )) {

                errorMsg.append(CodedMessageUtils.getMessage("M.E.00303", new Object[]{"label.productNumber",member.getPartsNo(),"label.tableProduct"}));
            }

            //检查siteId是否一致
            if (!StringUtils.equals(member.getDealerCode(), siteId)) {

                errorMsg.append(CodedMessageUtils.getMessage("M.E.00336", new Object[]{member.getDealerCode()}));
            }

            //检查PointCd是否一致
            if (!StringUtils.equals(member.getPointCode(), mstFacilityVO.getFacilityCd())) {

                errorMsg.append(CodedMessageUtils.getMessage("M.E.00303", new Object[]{"label.pointCode",member.getPointCode(),"label.tablePointInfo"}));
            }

            // Check for other conditions and append messages
            member.setErrorMessage(errorMsg.toString());
            member.setWarningMessage(warningMsg.toString());
        });

        return form.getImportList();

    }

    public void saveFile(CMM050901Form form,String siteId) {

        //最后保存的结果
        List<ProductOrderResultSummaryVO> resultImportList = new ArrayList<>();

        //产品只需要考虑666N
        List<String> siteList = new ArrayList<>();
        siteList.add(CommonConstants.CHAR_DEFAULT_SITE_ID);

        //抽取productNo
        List<String> productNoList = form.getImportList().stream().map(o -> o.getPartsNo()).collect(Collectors.toList());

        //抽取productIds
        List<MstProductVO> productVos = cmm0509Service.findProductByProductNo(productNoList, siteList);
        List<Long> productIds = productVos.stream().map(o -> o.getProductId()).collect(Collectors.toList());

        //将查询出来的产品转换为Key为ProductNo,value为Id的Map
        Map<String, Long> productMaps = productVos.stream().collect(Collectors.toMap(MstProductVO::getProductCd, MstProductVO::getProductId));

        //通过页面传的pointId获取据点数据
        MstFacilityVO mstFacilityVO = cmm0509Service.findByFacilityId(Long.valueOf(form.getOtherProperty().toString()));

        //待删除的数据,并且将其备份
        List<ProductOrderResultSummaryVO> deleteVOs = cmm0509Service.findSummaryVOByProductIds(siteId,productIds,mstFacilityVO.getFacilityId());
        List<ProductOrderResultHistoryVO> backupVos = getBackupData(deleteVOs, siteId, mstFacilityVO.getFacilityId());

        String sysMonth = LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_M));

        for (CMM050901BO member : form.getImportList()) {

            //将importList的n0 到 n24 转换为list
            List<BigDecimal> nList = new ArrayList<>(Arrays.asList(new BigDecimal[CommonConstants.INTEGER_TWELVE]));

            for (int index = CommonConstants.INTEGER_ZERO; index <= CommonConstants.INTEGER_TWENTY_FOUR; index++) {

                nList.add(index,getProperty(member,CommonConstants.CHAR_SMALL_N+index));
            }

            //每个产品都需重置月份和年份
            Integer month = Integer.parseInt(sysMonth);
            String sysYear = LocalDate.now().format(DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_Y));
            List<ProductOrderResultSummaryVO> summaryVOs = new ArrayList<>();
            ProductOrderResultSummaryVO vo = null;
            boolean newFlag = true;

            for (int i = CommonConstants.INTEGER_ZERO; i <= CommonConstants.INTEGER_TWENTY_FOUR; i++) {

                if(newFlag) {
                    vo = new  ProductOrderResultSummaryVO();
                    vo.setFacilityId(mstFacilityVO.getFacilityId());
                    vo.setSiteId(siteId);
                    vo.setProductId(productMaps.get(member.getPartsNo()));
                    vo.setTargetYear(sysYear);
                    newFlag = false;
                }

                //为ProductOrderResultSummaryVO的monthxxQuantity属性赋值
                String name =CommonConstants.GROUP_TYPE_MONTH+ String.format(CommonConstants.CHAR_ADDZEROFORMAT,month)+CommonConstants.CHAR_QUANTITY;
                BigDecimal value = nList.get(i);
                setProperty(vo,name,value);

                //月份为0时,将月份重置为12，年份-1，重新初始化VO
                month--;
                if (Objects.equals(month, CommonConstants.INTEGER_ZERO)) {
                    month=CommonConstants.INTEGER_TWELVE;
                    sysYear=Integer.toString(Integer.parseInt(sysYear) - CommonConstants.INTEGER_ONE);
                    summaryVOs.add(vo);
                    newFlag=true;
                }

                //补齐末尾数据
                if(i == CommonConstants.INTEGER_TWENTY_FOUR) {
                    summaryVOs.add(vo);
                }
            }

            resultImportList.addAll(summaryVOs);
        }

        cmm0509Service.editProductOrderResultSummaryRepository(deleteVOs, resultImportList,backupVos);
    }

    private void setProperty(Object vo,String name , BigDecimal value){

        try {
            BeanUtils.setProperty(vo, name ,value);
        } catch (Exception  e) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("jail.error.system"));
        }
    }

    private BigDecimal getProperty(Object vo,String name){

        String value;
        try {
            value = BeanUtils.getProperty(vo, name);
            if (ObjectUtils.isEmpty(value)){
                value = CommonConstants.CHAR_ZERO;
            }
        } catch (Exception  e) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("jail.error.system"));
        }
        return new BigDecimal(value);
    }

}