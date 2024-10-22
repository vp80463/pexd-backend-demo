package com.a1stream.domain.bo.service;

import com.a1stream.common.model.BaseBO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.util.List;

/**
 * @author dong zhen
 */
@Getter
@Setter
public class SVM010604BO extends BaseBO {

    @Serial
    private static final long serialVersionUID = 1499359067236473313L;

    /**
     * 序号
     */
    private int no;

    /**
     * 评分结果ID
     */
    private Long leadManagementResultId;

    /**
     * 评分日期
     */
    private String scoringDate;

    /**
     * 评分
     */
    private String scoring;

    /**
     * 经销商名称
     */
    private String dealerNm;

    /**
     * 评分人名字
     */
    private String firstNm;

    /**
     * 评分人名字
     */
    private String middleNm;

    /**
     * 评分人名字
     */
    private String lastNm;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String districtTownCity;

    /**
     * 手机号
     */
    private String mobilePhone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 邮箱2
     */
    private String email2;

    /**
     * 门店cd
     */
    private String shopCd;

    /**
     * 门店名称
     */
    private String shopNm;

    /**
     * 访问日期
     */
    private String visitDate;

    /**
     * 车架号
     */
    private String frameNo;

    /**
     * 车牌号
     */
    private String plateNo;

    /**
     * 车型cd
     */
    private String modelCd;

    /**
     * 车型名称
     */
    private String modelNm;

    /**
     * 车型使用年限
     */
    private String fscUseHistory;

    /**
     * 车辆当前里程
     */
    private String currentVoucher;

    /**
     * 下次保养日期
     */
    private String nextFscExpireDate;

    /**
     * 上次保养日期
     */
    private String lastOilChangeDate;

    /**
     * 油品名称
     */
    private String oilNm;

    /**
     * 咨询结果
     */
    private String telemarketingResult;

    /**
     * 咨询结果类型ID
     */
    private String telemarketingResultTypeId;

    /**
     * 咨询日期
     */
    private String callDateByDealer;

    /**
     * 备注
     */
    private String note;

    /**
     * 创建时间
     */
    private String timeStamp;

    /**
     * 联系状态
     */
    private String contactStatus;

    /**
     * 客户姓名
     */
    private String customerNm;

    /**
     * 客户类型
     */
    private String category;

    /**
     * 错误信息
     */
    private transient List<String> error;

    /**
     * 错误信息参数
     */
    private transient List<Object[]> errorParam;

    /**
     * 警告信息
     */
    private transient List<String> warning;

    /**
     * 警告信息参数
     */
    private transient List<Object[]> warningParam;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 警告信息
     */
    private String warningMessage;
}