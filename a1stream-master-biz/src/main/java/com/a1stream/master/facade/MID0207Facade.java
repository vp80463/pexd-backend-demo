package com.a1stream.master.facade;

import com.a1stream.domain.bo.master.ProductInfoBO;
import com.a1stream.domain.bo.master.ProductInfoItemBO;
import com.a1stream.domain.bo.master.ProductPriceBO;
import com.a1stream.domain.form.master.MID0207Form;
import com.ymsl.solid.base.util.CodedMessageUtils;
import com.ymsl.solid.base.util.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dong zhen
 */
@Component
public class MID0207Facade {

    private static final String ERROR_MSG = "Error: ";
    private static final String WARNING_MSG = "Warning: ";

    public void checkFile(MID0207Form form) {

        for (ProductPriceBO member : form.getImportList()) {

            List<String> error          = new ArrayList<>();
            List<Object[]> errorParam   = new ArrayList<>();
            List<String> warning        = new ArrayList<>();
            List<Object[]> warningParam = new ArrayList<>();

            if (StringUtils.isEmpty(member.getCategoryCd())){
                error.add("M.E.00001");
                errorParam.add(new Object[]{"label.serialCode"});
            }

            if (StringUtils.isEmpty(member.getCategoryNm())){
                warning.add("M.E.00001");
                warningParam.add(new Object[]{"label.serialCode"});
            }

            member.setError(error);
            member.setErrorParam(errorParam);
            member.setWarning(warning);
            member.setWarningParam(warningParam);
        }
    }

    public void importFile(MID0207Form form) {

        System.out.println("保存逻辑");
    }

    public List<ProductPriceBO> getExportList(MID0207Form form) {

        List<ProductPriceBO> productPriceBOList = new ArrayList<>();
        BigDecimal price = new BigDecimal(30000000);
        for (int i = 0; i < 150000; i++) {
            ProductPriceBO member = new ProductPriceBO();

            member.setCategoryCd("TestCd");
            member.setCategoryNm("测试名称");
            member.setGoodsPrice(price);
            member.setGuidePrice(price);
            member.setMaxPrice(price);
            member.setTestA("TestCd");
            member.setTestB("TestCd");
            member.setTestC("TestCd");
            member.setTestD("TestCd");
            member.setTestE("TestCd");
            productPriceBOList.add(member);
        }
        return  productPriceBOList;
    }

    public List<ProductInfoBO> getExportList2(MID0207Form form) {
        List<ProductInfoBO> productPriceBOList = new ArrayList<>();
        BigDecimal price = new BigDecimal(30000000);
        for (int i = 0; i < 150000; i++) {
            ProductInfoBO member = new ProductInfoBO();

            member.setCategoryCd("TestCd");
            member.setCategoryNm("测试名称");
            member.setGoodsPrice(price.add(BigDecimal.valueOf(1)));
            member.setGuidePrice(price.add(BigDecimal.valueOf(2)));
            member.setMaxPrice(price.add(BigDecimal.valueOf(3)));
            member.setTestA("TestCdA");
            member.setTestB("TestCdB");
            member.setTestC("TestCdC");
            member.setTestD("TestCdD");
            member.setTestE("TestCdE");
            productPriceBOList.add(member);
        }
        return productPriceBOList;
    }

    public List<ProductInfoItemBO> getExportList3(MID0207Form form) {
        List<ProductInfoItemBO> productPriceBOList = new ArrayList<>();
        BigDecimal price = new BigDecimal(30000000);
        for (int i = 0; i < 150000; i++) {
            ProductInfoItemBO member = new ProductInfoItemBO();

            member.setCategoryCd("TestCdItem" + i);
            member.setCategoryNm("测试名称Item" + i);
            member.setGoodsPrice(price.add(BigDecimal.valueOf(1)));
            member.setGuidePrice(price.add(BigDecimal.valueOf(2)));
            member.setMaxPrice(price.add(BigDecimal.valueOf(3)));
            member.setTestA("TestCdA" + i);
            member.setTestB("TestCdB" + i);
            member.setTestC("TestCdC" + i);
            member.setTestD("TestCdD" + i);
            member.setTestE("TestCdE" + i);
            productPriceBOList.add(member);
        }
        return  productPriceBOList;
    }

    public Object getValidFileList(MID0207Form form) {

        for (ProductPriceBO member : form.getImportList()) {

            StringBuilder errorMsg = new StringBuilder(ERROR_MSG);
            StringBuilder warningMsg = new StringBuilder(WARNING_MSG);

            if (StringUtils.isEmpty(member.getCategoryCd())) {
                errorMsg.append(CodedMessageUtils.getMessage("M.E.00001", new Object[]{"label.serialCode"}));
            }
            if (StringUtils.isEmpty(member.getCategoryNm())){
                warningMsg.append(CodedMessageUtils.getMessage("M.E.00001", new Object[]{"label.serialCode"}));
            }

            if (ERROR_MSG.contentEquals(errorMsg)) {
                errorMsg.setLength(0);
            }
            if (WARNING_MSG.contentEquals(warningMsg)) {
                warningMsg.setLength(0);
            }
            member.setErrorMessage(errorMsg.toString());
            member.setWarningMessage(warningMsg.toString());
        }
        return form.getImportList();
    }
}
