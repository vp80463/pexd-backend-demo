package com.a1stream.unit.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.InterfConstants.InterfCode;
import com.a1stream.common.constants.MstCodeConstants.SystemParameterType;
import com.a1stream.common.constants.PJConstants.FacilityRoleType;
import com.a1stream.common.constants.PJConstants.OrgRelationType;
import com.a1stream.common.manager.CallNewIfsManager;
import com.a1stream.domain.bo.batch.HeaderBO;
import com.a1stream.domain.bo.batch.SdPSIExportItemBO;
import com.a1stream.domain.bo.batch.SdPSIExportItemXmlBO;
import com.a1stream.domain.entity.MstFacility;
import com.a1stream.domain.repository.CmmMstOrganizationRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.SerializedProductTranRepository;
import com.a1stream.domain.repository.SystemParameterRepository;
import com.a1stream.domain.vo.CmmMstOrganizationVO;
import com.a1stream.domain.vo.SystemParameterVO;
import com.alibaba.fastjson.JSON;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.base.util.IdUtils;
import com.ymsl.solid.base.util.Nulls;
import com.ymsl.solid.base.util.uuid.SnowflakeIdWorker;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SdPSIExportService {

    @Resource
    private SystemParameterRepository systemParameterRepo;

    @Resource
    private CmmMstOrganizationRepository cmmMstOrganizationRepo;

    @Resource
    private MstFacilityRepository mstFacilityRepo;

    @Resource
    private SerializedProductTranRepository serializedProductTranRepo;

    @Resource
    private CallNewIfsManager callNewIfsManager;

    @Value("${ifs.request.url}")
    private String ifsRequestUrl;

    public void sdPSIExport() {

        //1.取得S074LASTPSIDATE对应的systemParameter(siteId = 666N)的value(yyyymmdd)
        SystemParameterVO sysParam = BeanMapUtils.mapTo(systemParameterRepo.findSystemParameterBySiteIdAndSystemParameterTypeId(CommonConstants.CHAR_DEFAULT_SITE_ID,
                                                                                                                                SystemParameterType.LAST_PSI_DATE), SystemParameterVO.class);

        if (!Nulls.isNull(sysParam)) {

            String value = sysParam.getParameterValue();

            if (StringUtils.isNotBlank(value)) {

                //2.1获取yyyymm月份的第一天为dateFirst
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstants.DB_DATE_FORMAT_YMD);
                LocalDate valueDate = LocalDate.parse(value, formatter);
                LocalDate dateFirst = valueDate.withDayOfMonth(CommonConstants.INTEGER_ONE);

                //2.2若dateFirst > currentDate, 则dateTo = currentDate
                LocalDate dateTo = dateFirst.isAfter(LocalDate.now()) ? LocalDate.now() : null;
                String dateToStr = !Nulls.isNull(dateTo) ? dateTo.format(formatter) : StringUtils.EMPTY;
                String dateFirstStr = dateFirst.format(formatter);

                //3.1获取cmm_mst_organization
                List<CmmMstOrganizationVO> cmmMstOrganizationList = BeanMapUtils.mapListTo(cmmMstOrganizationRepo.getCmmMstOrganization(CommonConstants.CHAR_DEFAULT_SITE_ID,
                                                                                                                                        OrgRelationType.DEALER.getCodeDbid(),
                                                                                                                                        dateToStr), CmmMstOrganizationVO.class);

                if (cmmMstOrganizationList.isEmpty()) {
                    return;
                }

                //取得cmmMstOrganization.organizationCd作为siteId
                Set<String> siteIdSet = cmmMstOrganizationList.stream()
                                                              .map(CmmMstOrganizationVO::getOrganizationCd)
                                                              .collect(Collectors.toSet());

                //取得cmmMstOrganization.organizationId
                Set<Long> organizationIdSet = cmmMstOrganizationList.stream()
                                                                    .map(CmmMstOrganizationVO::getOrganizationId)
                                                                    .collect(Collectors.toSet());

                //3.2获取mst_facility
                List<MstFacility> mstFacilityList = mstFacilityRepo.findBySiteIdInAndFacilityRoleTypeAndSpPurchaseFlag(siteIdSet, 
                                                                                                                       FacilityRoleType.SHOP,
                                                                                                                       CommonConstants.CHAR_N);

                //取得mst_facility.facilityId
                Set<Long> facilityIdSet = mstFacilityList.stream()
                                                         .map(MstFacility::getFacilityId)
                                                         .collect(Collectors.toSet());

                //3.3查询sdPSI数据
                List<SdPSIExportItemBO> returnList = serializedProductTranRepo.getSdPSIExportList(siteIdSet,
                                                                                                  facilityIdSet,
                                                                                                  organizationIdSet,
                                                                                                  dateFirstStr,
                                                                                                  dateToStr);

                if (!returnList.isEmpty()) {

                    //设置Date为valueDate的后一天
                    String dateStr = valueDate.plusDays(CommonConstants.INTEGER_ONE).format(formatter);
                    returnList.forEach(item -> item.setDate(dateStr));

                    //Export to interface file.
                    String ifsCode = InterfCode.DMS_TO_SD_SDPSIEXPORT;
                    SdPSIExportItemXmlBO sendDataBo = new SdPSIExportItemXmlBO();
                    sendDataBo.setHeader(this.setHeaderBo());
                    sendDataBo.setExportItems(returnList);

                    String jsonStr = JSON.toJSON(sendDataBo).toString();
                    callNewIfsManager.callNewIfsService(ifsRequestUrl, ifsCode, jsonStr);
                    log.info("Async Message Send Success...");
                }
            }
        }
    }

    private HeaderBO setHeaderBo() {

        SnowflakeIdWorker snowflakeIdWorker = IdUtils.getSnowflakeIdWorker();

        String currentDateString = DateUtils.getCurrentDateString(DateUtils.FORMAT_YMDHMSS_HYPHEN);
        HeaderBO headerBO = new HeaderBO();
        headerBO.setRequestId(snowflakeIdWorker.nextIdStr());
        headerBO.setMessageType(InterfCode.DMS_TO_SD_SDPSIEXPORT);
        headerBO.setSenderCode(CommonConstants.CHAR_DMS);
        headerBO.setReceiverCode(CommonConstants.CHAR_YAMAHA);
        headerBO.setCreateDateTime(currentDateString);

        return headerBO;
    }

}
