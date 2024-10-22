package com.a1stream.unit.facade;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.a1stream.common.bo.FilesUploadReturnBO;
import com.a1stream.common.bo.FilesUploadUtilBO;
import com.a1stream.common.bo.JasperExportDetailTestBO;
import com.a1stream.common.bo.JasperExportTestBO;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.constants.MstCodeConstants.SystemParameterType;
import com.a1stream.common.model.BaseResult;
import com.a1stream.common.model.EInvoiceBO;
import com.a1stream.domain.form.master.FileUploadTestForm;
import com.a1stream.unit.service.FileUploadTestService;
import com.ymsl.solid.base.util.DateUtils;

import jakarta.annotation.Resource;

/**
 * @author dong zhen
 */
@Component
public class FileUploadTestFacade {

    @Resource
    private FileUploadTestService fileUploadTestService;

    public void multipleFileUploadTest(FileUploadTestForm form) {

        form.setBusinessType("HOME_CAROUSEL_IMAGE");
        Long businessId = 132456789L;
        System.out.println("处理逻辑");
        fileUploadTestService.multipleFileUploadTest(form, businessId);
    }

    public void singleFileUploadTest(FileUploadTestForm form, MultipartFile[] singleFile) {

        form.setBusinessType("SALES");
        Long businessId = 132456789L;
        System.out.println("处理逻辑2");
        fileUploadTestService.singleFileUploadTest(form, singleFile, businessId);
    }

    public void escortFileUploadTest(FileUploadTestForm form, MultipartFile[] files) {

        form.setBusinessType(MstCodeConstants.SystemParameterType.PRIVACYPOLICYPHOTOPATH);
        Long businessId = 132456789L;
        System.out.println("Upload privacy picture");
        fileUploadTestService.escortFileUploadTest(form, files, businessId);
    }

    public List<FilesUploadReturnBO> getImageUrlList(FileUploadTestForm form) {

        form.setBusinessType("PURCHASE");
        System.out.println("处理逻辑4");
        return fileUploadTestService.getImageUrlList(form);
    }

    public List<JasperExportTestBO> getJasperDataList(FileUploadTestForm form) {

        List<JasperExportTestBO> returnList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {

            JasperExportTestBO exportTestBO = new JasperExportTestBO();
            exportTestBO.setDate(DateUtils.getCurrentDateString(DateUtils.FORMAT_YMDHMS));
            exportTestBO.setDeliveryNo("1233211233321" + i);
            exportTestBO.setDealerDescription("Xiamen YAMAHA");
            exportTestBO.setToAddress("Xiamen Software Park Phase III, Fujian Province");
            exportTestBO.setQrCode("0123456789AEC");
            exportTestBO.setBarCode("DSS999664852326");
            exportTestBO.setLogo("./logoImg/YAMAHA_Blue.jpg");
            List<JasperExportDetailTestBO> detailList = getDetailTestBOList();
            exportTestBO.setDetailTestList(detailList);
            returnList.add(exportTestBO);
        }
        System.out.println("处理逻辑5");
        return returnList;
    }

    private static List<JasperExportDetailTestBO> getDetailTestBOList() {
        List<JasperExportDetailTestBO> detailList = new ArrayList<>();
        for (int j = 0; j < 10; j++) {

            JasperExportDetailTestBO detailTestBO = new JasperExportDetailTestBO();
            detailTestBO.setLocationCd("1-1-1-1");
            detailTestBO.setProductCd("AAAQQQWWW");
            detailTestBO.setAccCategoryNm("Test Name");
            detailTestBO.setColorNm("Pink");
            detailTestBO.setAccSpecification("L");
            detailTestBO.setDeliveryQty(new BigDecimal(100).setScale(0, RoundingMode.HALF_UP));
            detailList.add(detailTestBO);
        }
        return detailList;
    }

    public void testWsdl() {

        EInvoiceBO invoiceBO = new EInvoiceBO();
        invoiceBO.setAccount("yamahamotoradmin");
        invoiceBO.setACpass("0100774342aA@");
        invoiceBO.setConvert(0);
        invoiceBO.setPass("Einv#@oi@vn#pt20");
        invoiceBO.setPattern("1/003");
        invoiceBO.setPatternSD("1/003");
        invoiceBO.setPatternSPSV("1/004");
        invoiceBO.setSerial("C24TXC");
        invoiceBO.setSerialSD("TXC");
        invoiceBO.setSerialSPSV("TPC");
        invoiceBO.setUsername("yamahamotorservice");
        invoiceBO.setXmlInvData("<Invoices>\n" +
                "  <Inv>\n" +
                "    <key>7ccf856a-51be-449a-a3c0-273e96464dd8</key>\n" +
                "    <Invoice>\n" +
                "      <CusCode>YT18SO2403A000053</CusCode>\n" +
                "      <CusName>XIANG  YU  SU</CusName>\n" +
                "      <CusAddress>Huyện An Phú, Tỉnh An Giang</CusAddress>\n" +
                "      <CusPhone>1234567982</CusPhone>\n" +
                "      <CusTaxCode></CusTaxCode>\n" +
                "      <PaymentMethod>Tiền mặt</PaymentMethod>\n" +
                "      <Products>\n" +
                "        <Product>\n" +
                "          <Code>RLCSSS520LY029021</Code>\n" +
                "          <ProdName>Xe mô tô hai bánh Yamaha ; Số loại:SIRIUS-BGY1; Mới 100%; SK:RLCSSS520LY029021; SM:E44KE-030571; Màu:Đen nhám; Phiên bản:Sirius-BGY1</ProdName>\n" +
                "          <ProdUnit>Chiếc\t</ProdUnit>\n" +
                "          <ProdQuantity>1</ProdQuantity>\n" +
                "          <ProdPrice>18981482</ProdPrice>\n" +
                "          <Amount>18981482</Amount>\n" +
                "          <IsSum>0</IsSum>\n" +
                "          <Discount>0</Discount>\n" +
                "          <DiscountAmount>0</DiscountAmount>\n" +
                "        </Product>\n" +
                "      </Products>\n" +
                "      <SST>0</SST>\n" +
                "      <Total>18981482</Total>\n" +
                "      <VATRate>10</VATRate>\n" +
                "      <VATAmount>1898148</VATAmount>\n" +
                "      <Amount>20879630</Amount>\n" +
                "      <AmountInWords>Hai mươi triệu tám trăm bảy mươi chín nghìn sáu trăm ba mươi đồng</AmountInWords>\n" +
                "      <ArisingDate>12/03/2024</ArisingDate>\n" +
                "      <Extra>12222220@qq.com</Extra>\n" +
                "      <LenhDieuDong>SO2403A000053</LenhDieuDong>\n" +
                "      <DonViDieuDong></DonViDieuDong>\n" +
                "      <VeViec></VeViec>\n" +
                "      <NguoiVanChuyen></NguoiVanChuyen>\n" +
                "      <SoHopDong></SoHopDong>\n" +
                "      <PhuongTienvc></PhuongTienvc>\n" +
                "      <XuatTaiKho></XuatTaiKho>\n" +
                "      <NhapTaiKho></NhapTaiKho>\n" +
                "      <PYCGH></PYCGH>\n" +
                "      <PXNDDH></PXNDDH>\n" +
                "      <ProductLine></ProductLine>\n" +
                "      <NguonDuLieu>webservice_SD</NguonDuLieu>\n" +
                "      <NoteYamaha></NoteYamaha>\n" +
                "      <USER_ID>ADMINSD</USER_ID>\n" +
                "      <Extra3>12222220@qq.com</Extra3>\n" +
                "      <Extra4></Extra4>\n" +
                "      <Extra5></Extra5>\n" +
                "      <Extra6></Extra6>\n" +
                "      <DIACHIGIAO></DIACHIGIAO>\n" +
                "      <BillWay>SO2403A000053</BillWay>\n" +
                "      <SOPHIEUTHU>SO2403A000053</SOPHIEUTHU>\n" +
                "      <SALEORDER>SO2403A000053</SALEORDER>\n" +
                "      <DEALERCODE></DEALERCODE>\n" +
                "      <CONSIGNEECODE></CONSIGNEECODE>\n" +
                "      <InvoiceCKs></InvoiceCKs>\n" +
                "    </Invoice>\n" +
                "  </Inv>\n" +
                "</Invoices>");
        fileUploadTestService.testWsdl(invoiceBO);
    }

    public void SDM050101Test(FileUploadTestForm form, MultipartFile file, BaseResult result) {

        form.setBusinessType(SystemParameterType.PROMOTION_PICTURE_URL);
        Long businessId = 20240918L;
        FilesUploadUtilBO fileUploadUtil = fileUploadTestService.SDM050101Test(form, file, businessId);
        MultipartFile singleFile = fileUploadUtil.getSingleFile();
        String oriName = singleFile.getOriginalFilename();
        Map<String, String> oldAndNewFileNameMap = fileUploadUtil.getOldAndNewFileNameMap();
        String fileName = oldAndNewFileNameMap.get(oriName);
        String path = fileUploadTestService.getPath(SystemParameterType.PROMOTION_PICTURE_URL);
        result.setData(path + CommonConstants.CHAR_SLASH + fileName);
    }
}
