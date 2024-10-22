package com.a1stream.common.manager;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import com.a1stream.common.bo.SDCommonCheckBO;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.MstCodeConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.model.BaseResult;
import com.a1stream.domain.entity.CmmBattery;
import com.a1stream.domain.entity.CmmPerson;
import com.a1stream.domain.entity.MstFacility;
import com.a1stream.domain.entity.MstProduct;
import com.a1stream.domain.entity.SerializedProduct;
import com.a1stream.domain.repository.CmmBatteryRepository;
import com.a1stream.domain.repository.CmmPersonRepository;
import com.a1stream.domain.repository.MstFacilityRepository;
import com.a1stream.domain.repository.MstProductRepository;
import com.a1stream.domain.repository.SerializedProductRepository;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.base.util.CodedMessageUtils;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * 整车共通校验管理
 *
 * @author dong zhen
 */
@Slf4j
@Service
public class SDCommonCheckManager {

    @Resource
    private MstFacilityRepository mstFacilityRepository;

    @Resource
    private CmmPersonRepository cmmPersonRepository;

    @Resource
    private MstProductRepository mstProductRepository;

    @Resource
    private SerializedProductRepository serializedProductRepository;

    @Resource
    private CmmBatteryRepository cmmBatteryRepository;

    @Resource
    private InventoryManager inventoryManager;

    private static final String NO_EXIT_CD = "M.E.00303";

    /**
     * 检查仓库代码、人员代码和产品代码是否存在。
     *
     * @param checkBO 包含待检查代码的对象，其中包括 siteId, pointCd, picCd 和 modelCd。
     * @throws BusinessCodedException 如果 checkBO 对象为空或任一代码在相应的数据库中不存在。
     */
    public void checkFacilityCdAndPersonCdAndProductCdIsExit(SDCommonCheckBO checkBO){

        if (checkBO == null ||
                StringUtils.isEmpty(checkBO.getSiteId()) ||
                StringUtils.isEmpty(checkBO.getPointCd()) ||
                StringUtils.isEmpty(checkBO.getPicCd()) ||
                StringUtils.isEmpty(checkBO.getModelCd())) {

            log.error("检验仓库代码、人员代码和产品代码参数失败，参数缺少");
            throw new BusinessCodedException(BaseResult.ERROR_MESSAGE);
        }

        MstFacility mstFacility = mstFacilityRepository.findBySiteIdAndFacilityCd(checkBO.getSiteId(), checkBO.getPointCd());
        if (mstFacility == null){
            throw new BusinessCodedException(CodedMessageUtils.getMessage(NO_EXIT_CD, new String[] {"label.point", checkBO.getPointCd(), "label.tableFacilityInfo"}));
        }

        CmmPerson cmmPerson = cmmPersonRepository.findBySiteIdAndPersonCd(checkBO.getSiteId(), checkBO.getPicCd());
        if (cmmPerson == null){
            throw new BusinessCodedException(CodedMessageUtils.getMessage(NO_EXIT_CD, new String[] {"label.pic", checkBO.getPicCd(), "label.tablePerson"}));
        }

        MstProduct mstProduct = mstProductRepository.findBySiteIdAndProductCd(checkBO.getSiteId(), checkBO.getModelCd());
        if (mstProduct == null){
            throw new BusinessCodedException(CodedMessageUtils.getMessage(NO_EXIT_CD, new String[] {"label.model", checkBO.getModelCd(), "label.tableProduct"}));
        }
    }

    /**
     * 检查序列化产品关联参数
     * 该方法用于验证序列化产品关联的参数是否正确和完整
     * 其中包括 siteId, pointCd, picCd, modelCd, frameNo, salesDate, pointId
     * 它确保了在进行序列化产品操作之前，所有必要的参数都是有效且一致的
     *
     * @param checkBO 包含待验证参数的业务对象
     * @throws BusinessCodedException 如果任何参数验证失败，则抛出业务异常
     */
    public void checkSerializedProductCorrelation(SDCommonCheckBO checkBO){

        if (checkBO == null ||
                StringUtils.isEmpty(checkBO.getSiteId()) ||
                StringUtils.isEmpty(checkBO.getPointCd()) ||
                StringUtils.isEmpty(checkBO.getFrameNo()) ||
                StringUtils.isEmpty(checkBO.getModelCd()) ||
                StringUtils.isEmpty(checkBO.getSalesDate()) ||
                checkBO.getModelId() == null ||
                checkBO.getPointId() == null){
            log.error("检验SerializedProduct相关参数失败，参数缺少");
            throw new BusinessCodedException(BaseResult.ERROR_MESSAGE);
        }

        SerializedProduct serializedProduct = serializedProductRepository.findBySiteIdAndFrameNo(checkBO.getSiteId(), checkBO.getFrameNo());
        if (serializedProduct == null){
            throw new BusinessCodedException(CodedMessageUtils.getMessage(NO_EXIT_CD, new String[] {"label.frameNumber", checkBO.getFrameNo(), "label.tableProductSerialNumber"}));
        }

        if (CommonConstants.CHAR_Y.equals(serializedProduct.getEvFlag()) && StringUtils.isEmpty(checkBO.getBatteryNo1())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10229", new String[] {" label.batteryNo"}));
        }
        if (!serializedProduct.getFacilityId().equals(checkBO.getPointId())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10204", new String[] {checkBO.getFrameNo(), checkBO.getPointCd()}));
        }

        if (checkBO.getSalesDate().compareTo(serializedProduct.getManufacturingDate()) < 0){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00201", new String[] {"label.salesDate", "label.assemblyDate"}));
        }

        if (!PJConstants.SerialProQualityStatus.NORMAL.equals(serializedProduct.getQualityStatus()) || !PJConstants.SerialproductStockStatus.ONHAND.equals(serializedProduct.getStockStatus())){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10168", new String[] {checkBO.getFrameNo()}));
        }

        if (!inventoryManager.canDoAllocate(checkBO.getSiteId(), checkBO.getPointId(), checkBO.getModelId(), BigDecimal.ONE)){
            throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.10174", new String[] {checkBO.getModelCd()}));
        }
    }

    /**
     * 根据电池ID检查电池状态
     * 此方法主要用于在进行某些操作前验证电池状态是否符合要求
     * 它通过电池ID检索电池信息，并检查电池状态是否为“已发货”
     * 如果电池状态为“已发货”，则抛出业务异常，提示用户不能修改已发货的电池信息
     *
     * @param batteryNo1 第一个电池的编号，用于识别电池
     * @param batteryNo2 第二个电池的编号，用于识别电池
     * @param batteryId1 第一个电池的唯一ID，用于查询电池信息
     * @param batteryId2 第二个电池的唯一ID，用于查询电池信息
     * @throws BusinessCodedException 如果电池状态为“已发货”，抛出业务异常
     */
    public void checkBatteryStatus(String batteryNo1, String batteryNo2, Long batteryId1, Long batteryId2){

        if (StringUtils.isEmpty(batteryNo1) ||
                StringUtils.isEmpty(batteryNo2) ||
                batteryId1 == null ||
                batteryId2 == null){
            log.error("检验电池状态相关参数失败，参数缺少");
            throw new BusinessCodedException(BaseResult.ERROR_MESSAGE);
        }

        this.checkOneBatteryStatus(batteryNo1, batteryId1);
        this.checkOneBatteryStatus(batteryNo2, batteryId2);
    }

    private void checkOneBatteryStatus(String batteryNo, Long batteryId) {

        CmmBattery cmmBattery = cmmBatteryRepository.findByBatteryId(batteryId);
        try {
            if (cmmBattery != null && MstCodeConstants.SerialproductStockStatus.SHIPPED.equals(cmmBattery.getBatteryStatus())){
                throw new BusinessCodedException(CodedMessageUtils.getMessage("M.E.00309", new String[] {"label.batteryId", batteryNo, "label.cmmBatteryInfo"}));
            }
        } catch (Exception e){
            log.error("检查电池状态失败，可能为空指针错误：{}",  e.getMessage());
            throw new BusinessCodedException(BaseResult.ERROR_MESSAGE);
        }
    }
}
