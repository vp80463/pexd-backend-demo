package com.a1stream.service.facade;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants.ReservationMethod;
import com.a1stream.common.constants.PJConstants.ReservationServiceContents;
import com.a1stream.common.constants.PJConstants.ReservationStatus;
import com.a1stream.common.constants.PJConstants.ReservationTime;
import com.a1stream.common.constants.PJConstants.ServiceOrderStatus;
import com.a1stream.common.facade.HelperFacade;
import com.a1stream.domain.bo.service.SVM010701BO;
import com.a1stream.domain.form.service.SVM010701Form;
import com.a1stream.domain.form.service.SVM010702Form;
import com.a1stream.domain.vo.ServiceScheduleVO;
import com.a1stream.service.service.SVM0107Service;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;
import software.amazon.awssdk.utils.StringUtils;

/**
* 功能描述:
*
* @author mid1966
*/
@Component
public class SVM0107Facade {

    @Resource
    private SVM0107Service svm0107Service;

    @Resource
    private HelperFacade helperFacade;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);

    public List<SVM010701BO> findServiceReservationList(SVM010701Form form) {

        List<SVM010701BO> resultList = svm0107Service.findServiceReservationList(form);

        // 数据库为codeDbid 前台显示为codeData1
        Map<String, String> codeMap = helperFacade.getMstCodeInfoMap(ServiceOrderStatus.CODE_ID, ReservationStatus.CODE_ID, ReservationServiceContents.CODE_ID, ReservationMethod.CODE_ID, ReservationTime.CODE_ID);

        for(SVM010701BO bo : resultList) {

            bo.setReservationSts(codeMap.get(bo.getReservationSts()));
            bo.setServiceBooking(codeMap.get(bo.getServiceBooking()));
            bo.setReservationMethod(codeMap.get(bo.getReservationMethod()));
            bo.setServiceStatus(codeMap.get(bo.getServiceStatus()));
            bo.setDisViewFlag(StringUtils.isBlank(bo.getOrderNo()));


            if (form.isExportFlag()) {

                if (StringUtils.isNotBlank(bo.getReservationDate())) {
                    bo.setReservationDate(LocalDate.parse(bo.getReservationDate(), DATE_FORMATTER).format(CommonConstants.DEFAULT_DATE_FORMAT_LOC));
                }

                if (StringUtils.isNotBlank(bo.getReservationTime())) {
                    bo.setReservationTime(codeMap.get(bo.getReservationTime()));
                }
            }
        }

        return resultList;
    }

    public SVM010702Form getInfoByPlateNo(SVM010702Form form) {

        return svm0107Service.getInfoByPlateNo(form);
    }

    public SVM010702Form initial010702Screen(SVM010702Form form) {

        ServiceScheduleVO serviceScheduleVO = svm0107Service.findServiceScheduleVO(form.getServiceScheduleId());

        SVM010702Form initialForm = new SVM010702Form();

        initialForm.setPlateNo(serviceScheduleVO.getPlateNo());
        initialForm.setSerializedProductId(serviceScheduleVO.getSerializedProductId());
        initialForm.setModelCd(serviceScheduleVO.getProductCd());
        initialForm.setModelNm(serviceScheduleVO.getProductNm());
        initialForm.setProductId(serviceScheduleVO.getProductId());
        initialForm.setPointId(serviceScheduleVO.getFacilityId());
        initialForm.setOrderNo(serviceScheduleVO.getOrderNo());
        initialForm.setConsumerNm(serviceScheduleVO.getConsumerNm());
        initialForm.setConsumerId(serviceScheduleVO.getConsumerId());
        initialForm.setMobilePhone(serviceScheduleVO.getMobilePhone());
        initialForm.setReservationDate(serviceScheduleVO.getScheduleDate());
        initialForm.setReservationTime(serviceScheduleVO.getScheduleTime());
        initialForm.setServiceBooking(serviceScheduleVO.getServiceContents());
        initialForm.setReservationStatus(serviceScheduleVO.getReservationStatus());
        initialForm.setReservationMethod(serviceScheduleVO.getBookingMethod());
        initialForm.setMemo(serviceScheduleVO.getMemo());
        initialForm.setServiceScheduleId(serviceScheduleVO.getServiceScheduleId());
        initialForm.setServiceOrderSettledFlag(serviceScheduleVO.getServiceOrderSettledFlag());

        if (StringUtils.isNotBlank(serviceScheduleVO.getEstimateFinishTime())) {
            initialForm.setEfTime(serviceScheduleVO.getEstimateFinishTime().substring(0, 2) + ":" + serviceScheduleVO.getEstimateFinishTime().substring(2));
        }

        // 字段编辑判断
        if(initialForm.getReservationMethod().equals(ReservationMethod.MYYAMAHAAPP.getCodeDbid())) {

            initialForm.setDisEditFlag1(true);
        }

        if(initialForm.getReservationStatus().equals(ReservationStatus.CANCELLED.getCodeDbid())) {

            initialForm.setDisEditFlag2(true);
        }

        if(initialForm.getReservationStatus().equals(ReservationStatus.COMPLETED.getCodeDbid()) && initialForm.getServiceOrderSettledFlag().equals(CommonConstants.CHAR_N)) {

            initialForm.setDisEditFlag3(true);
        }

        return initialForm;
    }

    public Long confirm(SVM010702Form form) {

        this.validateData(form);

        ServiceScheduleVO serviceScheduleVO = null;

        // ServiceScheduleId不为空则更新，否则追加
        if(form.getServiceScheduleId() == null) {

            // 新增
            serviceScheduleVO = ServiceScheduleVO.create();
            serviceScheduleVO.setUpdateCount(0);
            serviceScheduleVO.setServiceOrderSettledFlag(CommonConstants.CHAR_N);
        } else {

            // 更新
            serviceScheduleVO = svm0107Service.findServiceScheduleVO(form.getServiceScheduleId());
        }

        serviceScheduleVO.setScheduleDate(form.getReservationDate());
        serviceScheduleVO.setScheduleTime(form.getReservationTime());
        serviceScheduleVO.setSiteId(form.getSiteId());
        serviceScheduleVO.setFacilityId(form.getPointId());
        serviceScheduleVO.setConsumerId(form.getConsumerId());
        serviceScheduleVO.setConsumerNm(form.getConsumerNm());
        serviceScheduleVO.setMobilePhone(form.getMobilePhone());
        serviceScheduleVO.setSerializedProductId(form.getSerializedProductId());
        serviceScheduleVO.setPlateNo(form.getPlateNo());
        serviceScheduleVO.setProductId(form.getProductId());
        serviceScheduleVO.setProductCd(form.getModelCd());
        serviceScheduleVO.setProductNm(form.getModelNm());
        serviceScheduleVO.setMemo(form.getMemo());
        serviceScheduleVO.setReservationStatus(form.getReservationStatus());
        serviceScheduleVO.setServiceContents(form.getServiceBooking());
        serviceScheduleVO.setBookingMethod(form.getReservationMethod());

        if (StringUtils.isNotBlank(form.getEfTime())) {
            serviceScheduleVO.setEstimateFinishTime(form.getEfTime().replace(":", ""));
        }

        // service_schedule 增改
        svm0107Service.updateServiceSchedule(serviceScheduleVO);

        return serviceScheduleVO.getServiceScheduleId();
    }

    private void validateData(SVM010702Form form) {

        // model存在性check
        if(StringUtils.isNotBlank(form.getModel()) && form.getProductId() == null) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00303", new String[] {CodedMessageUtils.getMessage("label.model"), form.getModelCd(), CodedMessageUtils.getMessage("label.tableProduct")}));
        }

        // ReservationData check
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate date = LocalDate.parse(form.getReservationDate(), formatter);
        LocalDate currentDate = LocalDate.now();

        if(date.isBefore(currentDate)) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00205", new String[] {CodedMessageUtils.getMessage("label.reservationDate"), CodedMessageUtils.getMessage("label.sysDate")}));
        }

        // plateNo. input check
        if(StringUtils.isBlank(form.getPlateNo()) && form.getReservationMethod().equals(ReservationMethod.DMS.getCodeDbid())) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00126", new String[] {CodedMessageUtils.getMessage("label.numberPlate")}));
        }

        // Reservation Status check
        if(form.getReservationStatus().equals(ReservationStatus.WAITCONFIRM.getCodeDbid()) && form.getReservationMethod().equals(ReservationMethod.DMS.getCodeDbid())) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00403"));
        }

        // mobile phone check
        if(form.getMobilePhone().length() < 10) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10331", new String[] {form.getMobilePhone()}));
        }

        // reservation by others check
        int rowCount = svm0107Service.getServiceScheduleRowCount(form);

        if(rowCount > 0) {

            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00404"));
        }
    }
}