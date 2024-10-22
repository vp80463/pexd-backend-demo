package com.a1stream.unit.facade;

import org.springframework.stereotype.Component;

import com.a1stream.unit.service.SdPSIExportService;

import jakarta.annotation.Resource;

@Component
public class SdPSIExportFacade {

    @Resource
    private SdPSIExportService sdPSIExportService;

    public void doSdPSIExport() {
        sdPSIExportService.sdPSIExport();
    }

}
