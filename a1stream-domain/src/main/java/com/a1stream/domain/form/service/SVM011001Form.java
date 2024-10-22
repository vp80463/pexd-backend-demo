package com.a1stream.domain.form.service;

import java.util.List;

import com.a1stream.common.model.BaseForm;
import com.a1stream.domain.bo.service.SVM011001BO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SVM011001Form extends BaseForm {

    private static final long serialVersionUID = 1L;

    private String siteId;
    private String frameNo;
    private String pdiDate;
    private String modelCd;
    private String modelNm;
    private String colorNm;
    private String consumerCd;
    private String consumerNm;
    private Long pointId;
    private Long mechanicId;
    private String mechanicCd;
    private String mechanicNm;
    private String startTime;
    private String finishTime;
    private Long productId;
    private Long toProductId;
    private Long serializedProductId;
    private Long consumerId;
    private List<SVM011001BO> tableDataList;
    // 画面显示用
    private String model;
    private String consumer;

    /**
     * @return the model
     */
    public String getModel() {
        return this.modelCd + " " + this.modelNm + " " + this.colorNm;
    }

    /**
     * @param model the model to set
     */
    public void setModel(String model) {
        this.model = this.modelCd + " " + this.modelNm + " " + this.colorNm;
    }
    /**
     * @return the model
     */
    public String getConsumer() {
        return this.consumerCd + " " + this.consumerNm;
    }

    /**
     * @param consumer the model to set
     */
    public void setConsumer(String consumer) {
        this.consumer = this.consumerCd + " " + this.consumerNm;
    }
}
