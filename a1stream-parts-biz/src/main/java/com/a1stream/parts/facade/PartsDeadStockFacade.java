package com.a1stream.parts.facade;

import com.a1stream.parts.service.PartsDeadStockService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

@Component
public class PartsDeadStockFacade {

    @Resource
    private PartsDeadStockService partsDeadStockService;

    public void doDeadStockCalculate(String siteId) {

        partsDeadStockService.doDeadStockCalculate(siteId);
    }
}
