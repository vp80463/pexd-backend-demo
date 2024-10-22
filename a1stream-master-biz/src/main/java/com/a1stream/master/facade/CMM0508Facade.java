package com.a1stream.master.facade;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.utils.NumberUtil;
import com.a1stream.domain.bo.master.CMM050801BO;
import com.a1stream.domain.entity.MstFacility;
import com.a1stream.domain.entity.SeasonIndexBatch;
import com.a1stream.domain.entity.SeasonIndexManual;
import com.a1stream.domain.form.master.CMM050801Form;
import com.a1stream.domain.vo.MstProductCategoryVO;
import com.a1stream.domain.vo.SeasonIndexBatchVO;
import com.a1stream.domain.vo.SeasonIndexManualVO;
import com.a1stream.master.service.CMM0508Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;
import jodd.util.StringUtil;

@Component
public class CMM0508Facade {

    @Resource
    private CMM0508Service cmm0508Service;

    public List<CMM050801BO> findSeasonIndexList(CMM050801Form model, String siteId) {

        List<CMM050801BO> resultList = new ArrayList<>();
        List<CMM050801BO> screenList = new ArrayList<>();
        List<MstProductCategoryVO> productCategories = new ArrayList<>();

        //校验point
        MstFacility facility = cmm0508Service.findFacility(model.getPointId());
        if(facility == null) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.point"), model.getPointvl(), CodedMessageUtils.getMessage("label.tablePointInfo")}));
        }

        if(model.getProductCategoryId() == null) {

            //查询大分类
            productCategories = cmm0508Service.findProductCategoryLargeList();
        }else {

            //编辑时查询大分类
            MstProductCategoryVO productCategory = cmm0508Service.findProductCategoryLargeById(model.getProductCategoryId());
            productCategories.add(productCategory);
        }

        //大分类的Id拼接为List
        List<Long> productCategoryIdList = productCategories.stream().map(MstProductCategoryVO::getProductCategoryId).toList();

        //根据SiteId和IdList查询SeasonIdex
        List<SeasonIndexManualVO> seasonIndexManuals = cmm0508Service.findSeasonIndexManualList(siteId,productCategoryIdList,model.getPointId());
        List<SeasonIndexBatchVO> seasonIndexBatchs = cmm0508Service.findSeasonIndexBatchList(siteId,productCategoryIdList,model.getPointId());

        //seasonIndexBatch设置到Bo
        BigDecimal multiplicand = new BigDecimal(CommonConstants.CHAR_HUNDRED);

        //根据系统当前月设置Batch的值到每个月
        setMonthValueBySystemMonth(resultList, seasonIndexBatchs, multiplicand);

        //seasonIndexManual设置到Bo
        setManualToResult(resultList, seasonIndexManuals, multiplicand);

        //设置lagreCategory到bo
        Map<Long, CMM050801BO> resultMap = resultList.stream().collect(Collectors.toMap(CMM050801BO::getProductCategoryId,each->each,(value1, value2) -> value1));

        for (MstProductCategoryVO productCategory : productCategories) {

            if(resultMap.containsKey(productCategory.getProductCategoryId())) {

                resultMap.get(productCategory.getProductCategoryId()).setLargeGroupId(productCategory.getProductCategoryId());
                resultMap.get(productCategory.getProductCategoryId()).setLargeGroup(productCategory.getCategoryCd()+CommonConstants.CHAR_SPACE+productCategory.getCategoryNm());
                resultMap.get(productCategory.getProductCategoryId()).setLargeGroupCd(productCategory.getCategoryCd());
                resultMap.get(productCategory.getProductCategoryId()).setLargeGroupNm(productCategory.getCategoryNm());
                screenList.add(resultMap.get(productCategory.getProductCategoryId()));
            }
        }

        //返回并排序
        return screenList.stream().sorted(Comparator.comparing(CMM050801BO::getLargeGroupCd)).toList();
    }

    private void setManualToResult(List<CMM050801BO> resultList, List<SeasonIndexManualVO> seasonIndexManuals, BigDecimal multiplicand) {

        String setMonth;
        for (SeasonIndexManualVO seasonIndexManual : seasonIndexManuals) {

            CMM050801BO cmm050801bo = new CMM050801BO();

            cmm050801bo.setMonth01(seasonIndexManual.getIndexMonth01().multiply(multiplicand));
            cmm050801bo.setMonth02(seasonIndexManual.getIndexMonth02().multiply(multiplicand));
            cmm050801bo.setMonth03(seasonIndexManual.getIndexMonth03().multiply(multiplicand));
            cmm050801bo.setMonth04(seasonIndexManual.getIndexMonth04().multiply(multiplicand));
            cmm050801bo.setMonth05(seasonIndexManual.getIndexMonth05().multiply(multiplicand));
            cmm050801bo.setMonth06(seasonIndexManual.getIndexMonth06().multiply(multiplicand));
            cmm050801bo.setMonth07(seasonIndexManual.getIndexMonth07().multiply(multiplicand));
            cmm050801bo.setMonth08(seasonIndexManual.getIndexMonth08().multiply(multiplicand));
            cmm050801bo.setMonth09(seasonIndexManual.getIndexMonth09().multiply(multiplicand));
            cmm050801bo.setMonth10(seasonIndexManual.getIndexMonth10().multiply(multiplicand));
            cmm050801bo.setMonth11(seasonIndexManual.getIndexMonth11().multiply(multiplicand));
            cmm050801bo.setMonth12(seasonIndexManual.getIndexMonth12().multiply(multiplicand));
            cmm050801bo.setProductCategoryId(seasonIndexManual.getProductCategoryId());
            cmm050801bo.setManualFlag(seasonIndexManual.getManualFlag());

            //设值行合计
            //查询系统当前月
            LocalDate currentDate = LocalDate.now();
            int currentMonth = currentDate.getMonthValue();
            for (int i = 11 ;i>=0 ; i--) {

                //设置要设值的目标月份
                if (currentMonth - 1 < 0) {
                    setMonth = StringUtil.toString(12 + currentMonth--);
                } else {
                    setMonth = StringUtil.toString(currentMonth--);
                }

                if (setMonth.length() == 1){
                    setMonth = CommonConstants.CHAR_ZERO + setMonth;
                }

                try {
                    cmm050801bo.setMonthTotal(CommonConstants.BIGDECIMAL_ZERO);
                    BigDecimal monthValue = NumberUtil.toBigDecimal(BeanUtils.getProperty(cmm050801bo, CommonConstants.GROUP_TYPE_MONTH +setMonth));
                    cmm050801bo.setMonthTotal(NumberUtil.add(cmm050801bo.getMonthTotal(), monthValue));
                } catch (IllegalAccessException | InvocationTargetException| NoSuchMethodException e) {

                    throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00004"));
                }
            }

            resultList.add(cmm050801bo);
        }
    }

    private void setMonthValueBySystemMonth(List<CMM050801BO> resultList, List<SeasonIndexBatchVO> seasonIndexBatchs, BigDecimal multiplicand) {

        String month;
        String setMonth;
        for (SeasonIndexBatchVO seasonIndexBatch : seasonIndexBatchs) {

            CMM050801BO cmm050801bo = new CMM050801BO();
            cmm050801bo.setMonthTotal(CommonConstants.BIGDECIMAL_ZERO);

            //查询系统当前月
            LocalDate currentDate = LocalDate.now();
            int currentMonth = currentDate.getMonthValue()-2;

            for (int i = 11 ;i>=0 ; i--) {

                try {
                    //通过不同的当前月取到相应的月份值
                    if ( i == 0) {
                        month = StringUtil.toString(BeanUtils.getProperty(seasonIndexBatch, CommonConstants.CHAR_N+ "Index"));
                    } else {
                        month = StringUtil.toString(BeanUtils.getProperty(seasonIndexBatch, CommonConstants.CHAR_SMALL_N +i+ "Index"));
                    }

                    //设置要设值的目标月份
                    if (currentMonth - 1 < 0) {
                        setMonth = StringUtil.toString(12 + currentMonth--);
                    } else {
                        setMonth = StringUtil.toString(currentMonth--);
                    }

                    if (setMonth.length() == 1){
                        setMonth = CommonConstants.CHAR_ZERO + setMonth;
                    }

                    //total
                    BeanUtils.setProperty(cmm050801bo, CommonConstants.GROUP_TYPE_MONTH +setMonth, NumberUtil.toBigDecimal(month).multiply(multiplicand));
                    BeanUtils.setProperty(cmm050801bo, "monthTotal", NumberUtil.add(cmm050801bo.getMonthTotal(), NumberUtil.toBigDecimal(month).multiply(multiplicand)));
                    cmm050801bo.setProductCategoryId(seasonIndexBatch.getProductCategoryId());
                    cmm050801bo.setManualFlag(seasonIndexBatch.getManualFlag());
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00004"));
                }
            }
            resultList.add(cmm050801bo);
        }
    }

    //修改行
    public void updateRow(CMM050801Form model, String siteId) {

        changeNullToZero(model);

        validateInput(model);

        //更新
        SeasonIndexBatchVO seasonIndexBatch = cmm0508Service.findSeasonIndexBatch(siteId,model.getProductCategoryId(),model.getPointId());

        //manual不存在，删除batch，添加manual
        if(StringUtil.equals(model.getManualFlag(), CommonConstants.CHAR_N) && seasonIndexBatch != null) {

            SeasonIndexManualVO seasonIndexManualNew = new SeasonIndexManualVO();
            seasonIndexManualNew.setSiteId(siteId);
            seasonIndexManualNew.setFacilityId(model.getPointId());
            seasonIndexManualNew.setProductCategoryId(model.getProductCategoryId());
            seasonIndexManualNew.setManualFlag(CommonConstants.CHAR_Y);
            addNewSeasonIndexManual(model,seasonIndexManualNew);
            cmm0508Service.saveManualAndDeleteBatch(BeanMapUtils.mapTo(seasonIndexManualNew,SeasonIndexManual.class),BeanMapUtils.mapTo(seasonIndexBatch, SeasonIndexBatch.class));

        }else if(StringUtil.equals(model.getManualFlag(), CommonConstants.CHAR_Y)){

            //manual已经存在，更新
            SeasonIndexManualVO seasonIndexManual = cmm0508Service.findSeasonIndexManual(siteId, model.getProductCategoryId(), model.getPointId());
            if(seasonIndexManual != null) {

                addNewSeasonIndexManual(model,seasonIndexManual);
                cmm0508Service.saveOrUpdateManual(BeanMapUtils.mapTo(seasonIndexManual,SeasonIndexManual.class));
            }
        }
    }

    //删除行
    public void deleteRow(CMM050801Form model, String siteId) {

        //删除seasonIndexBatch
        if(StringUtil.equals(model.getManualFlag(), CommonConstants.CHAR_N)) {
            SeasonIndexBatchVO seasonIndexBatch = cmm0508Service.findSeasonIndexBatch(siteId,model.getProductCategoryId(),model.getPointId());
            cmm0508Service.deleteSeasonIndexBatch(BeanMapUtils.mapTo(seasonIndexBatch,SeasonIndexBatch.class));
        }else {
            //seasonIndexManual
            SeasonIndexManualVO seasonIndexManual = cmm0508Service.findSeasonIndexManual(siteId, model.getProductCategoryId(), model.getPointId());
            cmm0508Service.deleteSeasonIndexManual(BeanMapUtils.mapTo(seasonIndexManual,SeasonIndexManual.class));
        }
    }

    //新增行
    public void addRow(CMM050801Form model, String siteId) {

        changeNullToZero(model);

        //存在性校验
        SeasonIndexBatchVO seasonIndexBatch = cmm0508Service.findSeasonIndexBatch(siteId,model.getProductCategoryId(),model.getPointId());
        SeasonIndexManualVO seasonIndexManual = cmm0508Service.findSeasonIndexManual(siteId, model.getProductCategoryId(), model.getPointId());

        if(seasonIndexBatch != null || seasonIndexManual != null) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00301", new String[]{CodedMessageUtils.getMessage("label.largeCategory")}));
        }else {

            //校验12个月数值没有负数 且 总和等于1200
            validateInput(model);

            SeasonIndexManualVO seasonIndexManualNew = new SeasonIndexManualVO();
            seasonIndexManualNew.setSiteId(siteId);
            seasonIndexManualNew.setFacilityId(model.getPointId());
            seasonIndexManualNew.setProductCategoryId(model.getProductCategoryId());
            seasonIndexManualNew.setManualFlag(CommonConstants.CHAR_Y);
            addNewSeasonIndexManual(model,seasonIndexManualNew);
            cmm0508Service.saveOrUpdateManual(BeanMapUtils.mapTo(seasonIndexManualNew,SeasonIndexManual.class));
        }
    }

    public void validateInput(CMM050801Form model) {

        //月份对应label的map
        Map<String, String> monthMessageMap = new HashMap<>();
        monthMessageMap.put("month01", "label.jan");
        monthMessageMap.put("month02", "label.feb");
        monthMessageMap.put("month03", "label.mar");
        monthMessageMap.put("month04", "label.apr");
        monthMessageMap.put("month05", "label.may");
        monthMessageMap.put("month06", "label.jun");
        monthMessageMap.put("month07", "label.jul");
        monthMessageMap.put("month08", "label.aug");
        monthMessageMap.put("month09", "label.sep");
        monthMessageMap.put("month10", "label.oct");
        monthMessageMap.put("month11", "label.nov");
        monthMessageMap.put("month12", "label.dec");

        String setMonth = "";
        //查询系统当前月
        LocalDate currentDate = LocalDate.now();
        int currentMonth = currentDate.getMonthValue();

        //更新前校验1-12数字不小于0
        for (int i = 11 ;i>=0 ; i--) {
            try {

                //获取要取值的月份
                if (currentMonth - 1 < 0) {
                    setMonth = StringUtil.toString(12 + currentMonth--);
                } else {
                    setMonth = StringUtil.toString(currentMonth--);
                }

                if (setMonth.length() == 1){
                    setMonth = CommonConstants.CHAR_ZERO + setMonth;
                }

                BigDecimal monthValue =  NumberUtil.toBigDecimal(BeanUtils.getProperty(model, CommonConstants.GROUP_TYPE_MONTH + setMonth));
                if(NumberUtil.lt(monthValue, CommonConstants.BIGDECIMAL_ZERO)) {

                    throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00201", new String[]{
                                                     CodedMessageUtils.getMessage(monthMessageMap.containsKey(CommonConstants.GROUP_TYPE_MONTH + setMonth)
                                                                                 ?monthMessageMap.get(CommonConstants.GROUP_TYPE_MONTH + setMonth):CommonConstants.CHAR_BLANK),
                                                     CommonConstants.CHAR_ZERO}));
                }
            }catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00004"));
            }
        }

        //更新前校验total必须等于1200
        String totalRequire = "1200";
        if(!NumberUtil.equals(model.getMonthTotal(), NumberUtil.toBigDecimal(totalRequire))) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00204", new String[]{
                    CodedMessageUtils.getMessage("label.total"),
                    totalRequire}));
        }
    }

    public SeasonIndexManualVO addNewSeasonIndexManual(CMM050801Form model,SeasonIndexManualVO seasonIndexManualNew) {

        BigDecimal multiplicand = new BigDecimal(CommonConstants.CHAR_HUNDRED);
        seasonIndexManualNew.setIndexMonth01(NumberUtil.divide(model.getMonth01(), multiplicand, 2, RoundingMode.HALF_UP));
        seasonIndexManualNew.setIndexMonth02(NumberUtil.divide(model.getMonth02(), multiplicand, 2, RoundingMode.HALF_UP));
        seasonIndexManualNew.setIndexMonth03(NumberUtil.divide(model.getMonth03(), multiplicand, 2, RoundingMode.HALF_UP));
        seasonIndexManualNew.setIndexMonth04(NumberUtil.divide(model.getMonth04(), multiplicand, 2, RoundingMode.HALF_UP));
        seasonIndexManualNew.setIndexMonth05(NumberUtil.divide(model.getMonth05(), multiplicand, 2, RoundingMode.HALF_UP));
        seasonIndexManualNew.setIndexMonth06(NumberUtil.divide(model.getMonth06(), multiplicand, 2, RoundingMode.HALF_UP));
        seasonIndexManualNew.setIndexMonth07(NumberUtil.divide(model.getMonth07(), multiplicand, 2, RoundingMode.HALF_UP));
        seasonIndexManualNew.setIndexMonth08(NumberUtil.divide(model.getMonth08(), multiplicand, 2, RoundingMode.HALF_UP));
        seasonIndexManualNew.setIndexMonth09(NumberUtil.divide(model.getMonth09(), multiplicand, 2, RoundingMode.HALF_UP));
        seasonIndexManualNew.setIndexMonth10(NumberUtil.divide(model.getMonth10(), multiplicand, 2, RoundingMode.HALF_UP));
        seasonIndexManualNew.setIndexMonth11(NumberUtil.divide(model.getMonth11(), multiplicand, 2, RoundingMode.HALF_UP));
        seasonIndexManualNew.setIndexMonth12(NumberUtil.divide(model.getMonth12(), multiplicand, 2, RoundingMode.HALF_UP));

        return seasonIndexManualNew;
    }

    /**
     * @param bo
     */
    private void changeNullToZero(CMM050801Form bo) {
        bo.setMonth01(bo.getMonth01() == null?CommonConstants.BIGDECIMAL_ZERO:bo.getMonth01());
        bo.setMonth02(bo.getMonth02() == null?CommonConstants.BIGDECIMAL_ZERO:bo.getMonth02());
        bo.setMonth03(bo.getMonth03() == null?CommonConstants.BIGDECIMAL_ZERO:bo.getMonth03());
        bo.setMonth04(bo.getMonth04() == null?CommonConstants.BIGDECIMAL_ZERO:bo.getMonth04());
        bo.setMonth05(bo.getMonth05() == null?CommonConstants.BIGDECIMAL_ZERO:bo.getMonth05());
        bo.setMonth06(bo.getMonth06() == null?CommonConstants.BIGDECIMAL_ZERO:bo.getMonth06());
        bo.setMonth07(bo.getMonth07() == null?CommonConstants.BIGDECIMAL_ZERO:bo.getMonth07());
        bo.setMonth08(bo.getMonth08() == null?CommonConstants.BIGDECIMAL_ZERO:bo.getMonth08());
        bo.setMonth09(bo.getMonth09() == null?CommonConstants.BIGDECIMAL_ZERO:bo.getMonth09());
        bo.setMonth10(bo.getMonth10() == null?CommonConstants.BIGDECIMAL_ZERO:bo.getMonth10());
        bo.setMonth11(bo.getMonth11() == null?CommonConstants.BIGDECIMAL_ZERO:bo.getMonth11());
        bo.setMonth12(bo.getMonth12() == null?CommonConstants.BIGDECIMAL_ZERO:bo.getMonth12());
    }
}

