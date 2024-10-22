package com.a1stream.domain.bo.batch;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartsStoringReportModelBO {

	private String dealerCode; 
	private String consigneeCode;
	private String invoiceSeqNo;
	private String shipmentNo;
	private String storageDate;
	private String storageTime;
}
