package com.a1stream.master.facade;
import java.math.RoundingMode;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.utils.NumberUtil;
import com.a1stream.domain.bo.master.CMM021101BO;
import com.a1stream.domain.entity.BinType;
import com.a1stream.domain.form.master.CMM021101Form;
import com.a1stream.domain.vo.BinTypeVO;
import com.a1stream.domain.vo.LocationVO;
import com.a1stream.master.service.CMM0211Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;
import jodd.util.StringUtil;

@Component
public class CMM0211Facade {

    @Resource
    private CMM0211Service cmm0211Service;

    public List<CMM021101BO> findBinTypeList(String siteId) {

        List<CMM021101BO> binTypeBo = cmm0211Service.findBinTypeList(siteId);
        for (CMM021101BO bo : binTypeBo) {

            changeNullToZero(bo);
            bo.setVolume(NumberUtil.divide(NumberUtil.toBigDecimal(NumberUtil.multiply(NumberUtil.multiply(bo.getLength(), bo.getWidth()), bo.getHeight()))
                                         , CommonConstants.BIGDECIMAL_HUNDRED_ROUND3, CommonConstants.INTEGER_TWO, RoundingMode.HALF_UP));
        }

        return binTypeBo;
    }

    //修改行
    public void updateRow(CMM021101Form model) {

        List<CMM021101BO> binTypeBo = cmm0211Service.findBinTypeList(model.getSiteId());

        changeNullToZero(BeanMapUtils.mapTo(model, CMM021101BO.class));
        //编辑前校验
        validInput(model,binTypeBo);

        BinTypeVO binTypeVO = cmm0211Service.findByBinTypeId(model);
        binTypeVO.setDescription(model.getDescription());
        binTypeVO.setLength(NumberUtil.toLong(model.getLength()));
        binTypeVO.setWidth(NumberUtil.toLong(model.getWidth()));
        binTypeVO.setHeight(NumberUtil.toLong(model.getHeight()));
        cmm0211Service.saveOrUpdateBinType(binTypeVO);
    }

    //删除行
    public void deleteRow(CMM021101Form model) {

        //校验bintype是否在location中已经使用
        List<LocationVO> locations = cmm0211Service.findLocationByBinTypeId(model);
        if(CollectionUtils.isNotEmpty(locations)) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00302"));
        }

        //删除
        BinTypeVO binTypeVO = cmm0211Service.findByBinTypeId(model);

        cmm0211Service.deleteRow(BeanMapUtils.mapTo(binTypeVO, BinType.class));
    }

    //新增行
    public void addRow(CMM021101Form model) {

        //校验binTypeCd是否存在
        List<CMM021101BO> binTypeBo = cmm0211Service.findBinTypeList(model.getSiteId());
        List<String> binTypeCdList = binTypeBo.stream().map(CMM021101BO::getBinTypeCd).toList();

        if(binTypeCdList.contains(model.getBinTypeCd())) {
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00301", new String[]{CodedMessageUtils.getMessage("label.binType")}));
        }

        changeNullToZero(BeanMapUtils.mapTo(model, CMM021101BO.class));

      //编辑前校验
        validInput(model,binTypeBo);

        //新增
        BinTypeVO binTypeVO = new BinTypeVO();
        binTypeVO.setBinTypeId(model.getBinTypeId());
        binTypeVO.setSiteId(model.getSiteId());
        binTypeVO.setBinTypeCd(model.getBinTypeCd());
        binTypeVO.setDescription(model.getDescription());
        binTypeVO.setLength(NumberUtil.toLong(model.getLength()));
        binTypeVO.setWidth(NumberUtil.toLong(model.getWidth()));
        binTypeVO.setHeight(NumberUtil.toLong(model.getHeight()));
        cmm0211Service.saveOrUpdateBinType(binTypeVO);
    }

    private void validInput(CMM021101Form model, List<CMM021101BO> binTypeBo) {

        //校验输入的描述相同 但是DB和侧边栏的ID并不相同 抛错该描述已经存在。

        for (CMM021101BO bo : binTypeBo) {
            if(StringUtil.equals(bo.getDescription(), model.getDescription()) && bo.getBinTypeId().compareTo(model.getBinTypeId()) != CommonConstants.INTEGER_ZERO) {
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00301", new String[]{CodedMessageUtils.getMessage("label.description")}));
            }
        }

        //校验输入的长/宽/高中的数值必须大于等于零
        compareWithZero(model.getLength(),"label.Length(mm)");
        compareWithZero(model.getWidth(),"label.Width(mm)");
        compareWithZero(model.getHeight(),"label.Height(mm)");
    }

    public void compareWithZero(Integer value,String label) {

        boolean result = false;
        if(value == null) {
            value = CommonConstants.INTEGER_ZERO;
        }
        if(CommonConstants.INTEGER_ZERO.compareTo(value) == CommonConstants.INTEGER_ONE) {
            result = true;
        }

        if(result) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00201", new String[]{
                                             CodedMessageUtils.getMessage(label),
                                             CommonConstants.CHAR_ZERO}));
        }
    }

    /**
     * @param bo
     */
    private CMM021101BO changeNullToZero(CMM021101BO bo) {
        bo.setLength(bo.getLength() == null?CommonConstants.INTEGER_ZERO:bo.getLength());
        bo.setWidth(bo.getWidth() == null?CommonConstants.INTEGER_ZERO:bo.getWidth());
        bo.setHeight(bo.getHeight() == null?CommonConstants.INTEGER_ZERO:bo.getHeight());

        return bo;
    }
}

