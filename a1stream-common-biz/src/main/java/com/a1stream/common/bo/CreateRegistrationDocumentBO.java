package com.a1stream.common.bo;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import com.a1stream.domain.vo.CmmRegistrationDocumentVO;

import lombok.Getter;
import lombok.Setter;

/**
 * @author dong zhen
 */
@Getter
@Setter
public class CreateRegistrationDocumentBO implements Serializable {

    @Serial
    private static final long serialVersionUID = -5242623354746874888L;

    /**
     * 据点ID
     */
    private Long pointId;

    /**
     * 客户ID
     */
    private Long customerId;

    /**
     * 序列化产品ID
     */
    private Long serializedProductId;

    /**
     * 使用类型
     */
    private String useType;

    /**
     * 购买类型
     */
    private String purchaseType;

    /**
     * 所有者类型
     */
    private String ownerType;

    /**
     * 曾经拥有的车辆品牌名称
     */
    private String previousBikeBrandName;

    /**
     * 曾经拥有的车辆名称
     */
    private String previousBikeName;

    /**
     * 曾经拥有的车辆分类
     */
    private String previousBikeMtat;

    /**
     * 家庭人数
     */
    private Integer numberOfFamilyMember;

    /**
     * 家庭拥有的车子数量
     */
    private Integer numberOfBikesInFamily;

    /**
     * 电池ID1
     */
    private Long batteryId1;

    /**
     * 电池ID2
     */
    private Long batteryId2;

    private CmmRegistrationDocumentVO cmmRegistrationDocument;

    private List<CmmRegistrationDocumentVO> batteriesToSaveList;
}
