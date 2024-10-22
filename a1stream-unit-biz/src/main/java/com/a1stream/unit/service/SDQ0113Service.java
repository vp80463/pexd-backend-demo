package com.a1stream.unit.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.unit.SDQ011301BO;
import com.a1stream.domain.bo.unit.SDQ011302BO;
import com.a1stream.domain.form.unit.SDQ011301Form;
import com.a1stream.domain.form.unit.SDQ011302Form;
import com.a1stream.domain.repository.ProductStockStatusRepository;
import com.a1stream.domain.repository.SerializedProductRepository;

import jakarta.annotation.Resource;

@Service
public class SDQ0113Service {

    @Resource
    private ProductStockStatusRepository productStockStatusRepository;

    @Resource
    private SerializedProductRepository serializedProductRepository;

    public List<SDQ011301BO> findStockInformationInquiry(SDQ011301Form form) {

        return productStockStatusRepository.findStockInformationInquiry(form);
    }

    public List<SDQ011302BO> findExportByMotorcycle(SDQ011301Form form) {

        return serializedProductRepository.findExportByMotorcycle(form);
    }

    public List<SDQ011302BO> findStockInformationInquiryDetail(SDQ011302Form form) {

        return serializedProductRepository.findStockInformationInquiryDetail(form);
    }
}
