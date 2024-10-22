package com.a1stream.parts.facade;

import com.a1stream.domain.bo.batch.PartsStoringReportModelBO;
import com.a1stream.parts.service.PartsStoringReportService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PartsStoringReportFacade {

    @Resource
    private PartsStoringReportService partsStoringReportService;


    public void doStoringReport(List<PartsStoringReportModelBO> storingReports) {

        partsStoringReportService.doStoringReport(storingReports);
    }
}
