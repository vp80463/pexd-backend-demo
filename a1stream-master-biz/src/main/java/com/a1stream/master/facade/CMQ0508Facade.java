package com.a1stream.master.facade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.utils.PartNoUtil;
import com.a1stream.domain.bo.master.CMQ050801BO;
import com.a1stream.domain.bo.master.CMQ050801ExportBO;
import com.a1stream.domain.form.master.CMQ050801Form;
import com.a1stream.domain.vo.ProductOrderResultHistoryVO;
import com.a1stream.domain.vo.ProductOrderResultSummaryVO;
import com.a1stream.master.service.CMQ0508Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.Nulls;

import jakarta.annotation.Resource;

/**
* 功能描述:Parts Summary Information明细画面
*
* mid2330
* 2024年6月7日
*
*  MODIFICATION HISTORY
*  Rev.   Date         Name          Comment
*  1.0    2024/06/07   Liu Chaoran     New
*/
@Component
public class CMQ0508Facade {

    @Resource
    private CMQ0508Service cmm0508Service;

    public Page<CMQ050801BO> findPartsSummaryList(CMQ050801Form model, String siteId) {

        this.check(model);
        Page<CMQ050801BO> findSummaryDatas = cmm0508Service.findPartsSummaryList(model, siteId);

        String sysYear = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_Y);
        String sysMonth = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_M);
        List<String> yearList = new ArrayList<>();
        yearList.add(sysYear);
        yearList.add(Integer.toString(Integer.parseInt(sysYear) - CommonConstants.INTEGER_ONE));
        yearList.add(Integer.toString(Integer.parseInt(sysYear) - CommonConstants.INTEGER_TWO));

        currentDemandPageData(model, siteId, findSummaryDatas, sysMonth, yearList);

        originaleDemandPageData(model, siteId, findSummaryDatas, sysMonth, yearList);

        return findSummaryDatas;
    }

    private void check(CMQ050801Form form) {

        //检查parts
        if (StringUtils.isNotBlank(form.getParts()) && Nulls.isNull(form.getPartsId())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {
                                             CodedMessageUtils.getMessage("label.partsNo"),
                                             form.getParts(),
                                             CodedMessageUtils.getMessage("label.tableProduct")}));
        }
    }

    private void currentDemandPageData(CMQ050801Form model, String siteId, Page<CMQ050801BO> findSummaryDatas, String sysMonth, List<String> yearList) {
        Map<Long, CMQ050801BO> partsIdMap = new HashMap<>();
        findSummaryDatas.forEach(bo -> partsIdMap.put(bo.getPartsId(), bo));

        List<ProductOrderResultSummaryVO> productOrderResultSummaryVO;
        if(model.getPartsId() != null ) {
            productOrderResultSummaryVO = cmm0508Service.findProductOrderResultSummaryRepositoryInYears(siteId, yearList, model.getPartsId(), model.getPointId());
        }else {
            productOrderResultSummaryVO = cmm0508Service.findProductOrderResultSummaryRepositoryInYearsNoProductId(siteId, yearList,model.getPointId());
        }

        Map<String, List<BigDecimal>> dataMap = new HashMap<>();
        for (ProductOrderResultSummaryVO vo : productOrderResultSummaryVO) {
            List<BigDecimal> list = new ArrayList<>(Arrays.asList(new BigDecimal[CommonConstants.INTEGER_TWELVE]));
            for (int i = CommonConstants.INTEGER_ONE; i <= CommonConstants.INTEGER_TWELVE; i++) {
                String name = CommonConstants.GROUP_TYPE_MONTH + String.format(CommonConstants.CHAR_ADDZEROFORMAT, i) + CommonConstants.CHAR_QUANTITY;
                BigDecimal monthQty = getProperty(vo, name);
                list.add(i, monthQty);
            }
            dataMap.put(vo.getProductId() + CommonConstants.CHAR_UNDERSCORE + vo.getTargetYear(), list);
        }

        Integer month = Integer.parseInt(sysMonth);
        String sysYear = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_Y);
        for (CMQ050801BO findHistoryData : findSummaryDatas) {
            if (PJConstants.DemandSource.CURRENTDEMAND.equals(findHistoryData.getDemandSource())) {
                // 只处理 demandSource 为 1 的数据
                List<BigDecimal> result = new ArrayList<>();
                CMQ050801BO bo = monthData(sysYear, dataMap, findHistoryData.getPartsId(), month, result);
                findHistoryData.setN0(bo.getN0());
                findHistoryData.setN1(bo.getN1());
                findHistoryData.setN0(bo.getN0());
                findHistoryData.setN1(bo.getN1());
                findHistoryData.setN2(bo.getN2());
                findHistoryData.setN3(bo.getN3());
                findHistoryData.setN4(bo.getN4());
                findHistoryData.setN5(bo.getN5());
                findHistoryData.setN6(bo.getN6());
                findHistoryData.setN7(bo.getN7());
                findHistoryData.setN8(bo.getN8());
                findHistoryData.setN9(bo.getN9());
                findHistoryData.setN10(bo.getN10());
                findHistoryData.setN11(bo.getN11());
                findHistoryData.setN12(bo.getN12());
                findHistoryData.setN13(bo.getN13());
                findHistoryData.setN14(bo.getN14());
                findHistoryData.setN15(bo.getN15());
                findHistoryData.setN16(bo.getN16());
                findHistoryData.setN17(bo.getN17());
                findHistoryData.setN18(bo.getN18());
                findHistoryData.setN19(bo.getN19());
                findHistoryData.setN20(bo.getN20());
                findHistoryData.setN21(bo.getN21());
                findHistoryData.setN22(bo.getN22());
                findHistoryData.setN23(bo.getN23());
                findHistoryData.setN24(bo.getN24());
            }
        }
    }

    private void originaleDemandPageData(CMQ050801Form model, String siteId, Page<CMQ050801BO> findSummaryDatas, String sysMonth, List<String> yearList) {
        if(!PJConstants.DemandSource.CURRENTDEMAND.equals(model.getDemandSource())) {

            Map<Long, CMQ050801BO> partsIdMap = new HashMap<>();
            findSummaryDatas.forEach(bo -> partsIdMap.put(bo.getPartsId(), bo));

            //DemandSource为1，走original demand处理
            List<ProductOrderResultHistoryVO> productOrderResultHistoryVO;
            if(model.getPartsId() != null ) {
                productOrderResultHistoryVO = cmm0508Service.findProductOrderResultHisRepositoryInYears(siteId, yearList, model.getPartsId(), model.getPointId());
            }else {
                productOrderResultHistoryVO = cmm0508Service.findProductOrderResultHisRepositoryInYearsNoProductId(siteId, yearList,model.getPointId());
            }

            //Map<产品id_年份，List<月份>>
            Map<String, List<BigDecimal>> dataMap = new HashMap<>();
            for (ProductOrderResultHistoryVO vo : productOrderResultHistoryVO) {

                //循环十二次，分别对应十二个月，list[1]为1月的数据，list[2]为2月的数据，依次类推
                List<BigDecimal> list = new ArrayList<>(Arrays.asList(new BigDecimal[CommonConstants.INTEGER_TWELVE]));
                for (int i = CommonConstants.INTEGER_ONE; i <= CommonConstants.INTEGER_TWELVE; i++) {
                    String name = CommonConstants.GROUP_TYPE_MONTH+ String.format(CommonConstants.CHAR_ADDZEROFORMAT,i)+CommonConstants.CHAR_QTY;
                    BigDecimal monthQty = getProperty(vo,name);
                    list.add(i,monthQty);
                }

                //例如我想要找到A产品2024年五月的数据，可以理解为是DataMap.get(A_2024).get(5)
                dataMap.put(vo.getProductId()+CommonConstants.CHAR_UNDERSCORE+vo.getTargetYear(), list);
            }

            Integer month = Integer.parseInt(sysMonth);
            String sysYear = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_Y);
            findSummaryDatas.forEach(findHistoryData -> {
                List<BigDecimal> result = new ArrayList<>();
                CMQ050801BO bo = monthData(sysYear, dataMap, findHistoryData.getPartsId(), month, result);

                findHistoryData.setN0(bo.getN0());
                findHistoryData.setN1(bo.getN1());
                findHistoryData.setN0(bo.getN0());
                findHistoryData.setN1(bo.getN1());
                findHistoryData.setN2(bo.getN2());
                findHistoryData.setN3(bo.getN3());
                findHistoryData.setN4(bo.getN4());
                findHistoryData.setN5(bo.getN5());
                findHistoryData.setN6(bo.getN6());
                findHistoryData.setN7(bo.getN7());
                findHistoryData.setN8(bo.getN8());
                findHistoryData.setN9(bo.getN9());
                findHistoryData.setN10(bo.getN10());
                findHistoryData.setN11(bo.getN11());
                findHistoryData.setN12(bo.getN12());
                findHistoryData.setN13(bo.getN13());
                findHistoryData.setN14(bo.getN14());
                findHistoryData.setN15(bo.getN15());
                findHistoryData.setN16(bo.getN16());
                findHistoryData.setN17(bo.getN17());
                findHistoryData.setN18(bo.getN18());
                findHistoryData.setN19(bo.getN19());
                findHistoryData.setN20(bo.getN20());
                findHistoryData.setN21(bo.getN21());
                findHistoryData.setN22(bo.getN22());
                findHistoryData.setN23(bo.getN23());
                findHistoryData.setN24(bo.getN24());
            });
        }
    }

  //Demand Source为Original Demand类型时处理
    private void originaleDemandData(CMQ050801Form model, String siteId, List<CMQ050801BO> findSummaryDatas, List<CMQ050801BO> processedDataList, String sysMonth, List<String> yearList) {
        if(!PJConstants.DemandSource.CURRENTDEMAND.equals(model.getDemandSource())) {

            Map<Long, CMQ050801BO> partsIdMap = new HashMap<>();
            findSummaryDatas.forEach(bo -> partsIdMap.put(bo.getPartsId(), bo));

            //DemandSource为1，走original demand处理
            List<ProductOrderResultHistoryVO> productOrderResultHistoryVO;
            if(model.getPartsId() != null ) {
                productOrderResultHistoryVO = cmm0508Service.findProductOrderResultHisRepositoryInYears(siteId, yearList, model.getPartsId(), model.getPointId());
            }else {
                productOrderResultHistoryVO = cmm0508Service.findProductOrderResultHisRepositoryInYearsNoProductId(siteId, yearList,model.getPointId());
            }

            //Map<产品id_年份，List<月份>>
            Map<String, List<BigDecimal>> dataMap = new HashMap<>();
            for (ProductOrderResultHistoryVO vo : productOrderResultHistoryVO) {

                //循环十二次，分别对应十二个月，list[1]为1月的数据，list[2]为2月的数据，依次类推
                List<BigDecimal> list = new ArrayList<>(Arrays.asList(new BigDecimal[CommonConstants.INTEGER_TWELVE]));
                for (int i = CommonConstants.INTEGER_ONE; i <= CommonConstants.INTEGER_TWELVE; i++) {
                    String name = CommonConstants.GROUP_TYPE_MONTH+ String.format(CommonConstants.CHAR_ADDZEROFORMAT,i)+CommonConstants.CHAR_QTY;
                    BigDecimal monthQty = getProperty(vo,name);
                    list.add(i,monthQty);
                }

                //例如我想要找到A产品2024年五月的数据，可以理解为是DataMap.get(A_2024).get(5)
                dataMap.put(vo.getProductId()+CommonConstants.CHAR_UNDERSCORE+vo.getTargetYear(), list);
            }

            Integer month = Integer.parseInt(sysMonth);
            String sysYear = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_Y);
            findSummaryDatas.forEach(findHistoryData -> {
                List<BigDecimal> result = new ArrayList<>();
                CMQ050801BO bo = monthData(sysYear, dataMap, findHistoryData.getPartsId(), month, result);

                findHistoryData.setN0(bo.getN0());
                findHistoryData.setN1(bo.getN1());
                findHistoryData.setN0(bo.getN0());
                findHistoryData.setN1(bo.getN1());
                findHistoryData.setN2(bo.getN2());
                findHistoryData.setN3(bo.getN3());
                findHistoryData.setN4(bo.getN4());
                findHistoryData.setN5(bo.getN5());
                findHistoryData.setN6(bo.getN6());
                findHistoryData.setN7(bo.getN7());
                findHistoryData.setN8(bo.getN8());
                findHistoryData.setN9(bo.getN9());
                findHistoryData.setN10(bo.getN10());
                findHistoryData.setN11(bo.getN11());
                findHistoryData.setN12(bo.getN12());
                findHistoryData.setN13(bo.getN13());
                findHistoryData.setN14(bo.getN14());
                findHistoryData.setN15(bo.getN15());
                findHistoryData.setN16(bo.getN16());
                findHistoryData.setN17(bo.getN17());
                findHistoryData.setN18(bo.getN18());
                findHistoryData.setN19(bo.getN19());
                findHistoryData.setN20(bo.getN20());
                findHistoryData.setN21(bo.getN21());
                findHistoryData.setN22(bo.getN22());
                findHistoryData.setN23(bo.getN23());
                findHistoryData.setN24(bo.getN24());
                processedDataList.add(findHistoryData);
            });
        }
    }

    //Demand Source为Current Demand类型时处理
    private void currentDemandData(CMQ050801Form model, String siteId, List<CMQ050801BO> findSummaryDatas, List<CMQ050801BO> findPartsSummaryDatas1, String sysMonth, List<String> yearList) {
        if(!PJConstants.DemandSource.ORIGINALDEMAND.equals(model.getDemandSource())) {
            //DemandSource为2，走current demand处理
            List<ProductOrderResultSummaryVO> productOrderResultSummaryVO;
            if(model.getPartsId() != null ) {
                productOrderResultSummaryVO = cmm0508Service.findProductOrderResultSummaryRepositoryInYears(siteId, yearList, model.getPartsId(), model.getPointId());
            }else {
                productOrderResultSummaryVO = cmm0508Service.findProductOrderResultSummaryRepositoryInYearsNoProductId(siteId, yearList,model.getPointId());
            }

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
            }
            for (CMQ050801BO findSummaryData : findSummaryDatas) {
                if (!productIdList.contains(findSummaryData.getPartsId())) {
                    productIdList.add(findSummaryData.getPartsId());
                }
            }

            Integer month = Integer.parseInt(sysMonth);
            String sysYear = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_Y);
            findSummaryDatas.forEach(findHistoryData -> {
                List<BigDecimal> result = new ArrayList<>();
                CMQ050801BO bo = monthData(sysYear, dataMap, findHistoryData.getPartsId(), month, result);

                findHistoryData.setN0(bo.getN0());
                findHistoryData.setN1(bo.getN1());
                findHistoryData.setN0(bo.getN0());
                findHistoryData.setN1(bo.getN1());
                findHistoryData.setN2(bo.getN2());
                findHistoryData.setN3(bo.getN3());
                findHistoryData.setN4(bo.getN4());
                findHistoryData.setN5(bo.getN5());
                findHistoryData.setN6(bo.getN6());
                findHistoryData.setN7(bo.getN7());
                findHistoryData.setN8(bo.getN8());
                findHistoryData.setN9(bo.getN9());
                findHistoryData.setN10(bo.getN10());
                findHistoryData.setN11(bo.getN11());
                findHistoryData.setN12(bo.getN12());
                findHistoryData.setN13(bo.getN13());
                findHistoryData.setN14(bo.getN14());
                findHistoryData.setN15(bo.getN15());
                findHistoryData.setN16(bo.getN16());
                findHistoryData.setN17(bo.getN17());
                findHistoryData.setN18(bo.getN18());
                findHistoryData.setN19(bo.getN19());
                findHistoryData.setN20(bo.getN20());
                findHistoryData.setN21(bo.getN21());
                findHistoryData.setN22(bo.getN22());
                findHistoryData.setN23(bo.getN23());
                findHistoryData.setN24(bo.getN24());
                findPartsSummaryDatas1.add(findHistoryData);
            });
        }
    }

    //月份数据处理
    private CMQ050801BO monthData(String sysYear, Map<String, List<BigDecimal>> dataMap, Long productId, Integer month, List<BigDecimal> result) {
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

        CMQ050801BO bo = new CMQ050801BO();
        bo.setPartsId(productId);
        //填充n0到n24的数据
        for (int i = CommonConstants.INTEGER_ZERO; i <= CommonConstants.INTEGER_TWENTY_FOUR; i++) {
            try {
                BeanUtils.setProperty(bo,CommonConstants.CHAR_SMALL_N+i, result.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return bo;
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

    //导出处理
    public List<CMQ050801BO> findPartsSummaryExport(CMQ050801Form model, String siteId) {

        List<CMQ050801BO> findSummaryDatas = cmm0508Service.findPartsSummaryExportList(model, siteId);

        List<CMQ050801BO> processedDataList = new ArrayList<>();
        List<CMQ050801BO> findPartsSummaryDatas1 = new ArrayList<>();

        String sysYear = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_Y);
        String sysMonth = DateUtils.getCurrentDateString(CommonConstants.DB_DATE_FORMAT_M);
        List<String> yearList = new ArrayList<>();
        yearList.add(sysYear);
        yearList.add(Integer.toString(Integer.parseInt(sysYear) - CommonConstants.INTEGER_ONE));
        yearList.add(Integer.toString(Integer.parseInt(sysYear) - CommonConstants.INTEGER_TWO));

        currentDemandData(model, siteId, findSummaryDatas, findPartsSummaryDatas1, sysMonth, yearList);

        originaleDemandData(model, siteId, findSummaryDatas, processedDataList, sysMonth, yearList);
        processedDataList.addAll(findPartsSummaryDatas1.stream()
                .map(bo -> {
                    CMQ050801BO copiedBo = SerializationUtils.clone(bo);
                    copiedBo.setDemandSource(PJConstants.DemandSource.CURRENTDEMAND);
                    return copiedBo;
                })
                .toList());

        return findSummaryDatas;
    }

    public List<CMQ050801ExportBO> findPartsSummaryExportList(CMQ050801Form model, String siteId) {

        List<CMQ050801BO> list = this.findPartsSummaryExport(model, siteId);
        List<CMQ050801ExportBO> exportList = new ArrayList<>();

        for (CMQ050801BO bo: list) {
            bo.setPartsCd(PartNoUtil.format(bo.getPartsCd()));
            CMQ050801ExportBO member = new CMQ050801ExportBO();

            member.setPartsCd(bo.getPartsCd());
            member.setDemandSource(bo.getDemandSource());
            member.setLargeGroupCd(bo.getLargeGroupCd());
            member.setMiddleGroupCd(bo.getMiddleGroupCd());

            member.setCostUsage(bo.getCostUsage());
            member.setJOne(bo.getJOne());
            member.setJTwo(bo.getJTwo());
            member.setTrendIndex(bo.getTrendIndex());
            member.setSeasonIndex(bo.getSeasonIndex());
            member.setDemandForecast(bo.getDemandForecast());
            member.setRop(bo.getRop());
            member.setRoq(bo.getRoq());
            member.setManualSign(bo.getManualSign());
            member.setBoQty(bo.getBoQty());
            member.setOnHandQty(bo.getOnHandQty());
            member.setTotalStock(bo.getTotalStock());
            member.setFutureStock(bo.getFutureStock());
            member.setN0(bo.getN0());
            member.setN1(bo.getN1());
            member.setN2(bo.getN2());
            member.setN3(bo.getN3());
            member.setN4(bo.getN4());
            member.setN5(bo.getN5());
            member.setN6(bo.getN6());
            member.setN7(bo.getN7());
            member.setN8(bo.getN8());
            member.setN9(bo.getN9());
            member.setN10(bo.getN10());
            member.setN11(bo.getN11());
            member.setN12(bo.getN12());
            member.setN13(bo.getN13());
            member.setN14(bo.getN14());
            member.setN15(bo.getN15());
            member.setN16(bo.getN16());
            member.setN17(bo.getN17());
            member.setN18(bo.getN18());
            member.setN19(bo.getN19());
            member.setN20(bo.getN20());
            member.setN21(bo.getN21());
            member.setN22(bo.getN22());
            member.setN23(bo.getN23());
            member.setN24(bo.getN24());
            exportList.add(member);
        }
        return exportList;
    }

}

