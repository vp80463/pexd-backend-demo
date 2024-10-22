package com.a1stream.parts.facade;

import com.a1stream.domain.bo.batch.PartsReturnRequestModelBO;
import com.a1stream.parts.service.PartsReturnRequestService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PartsReturnRequestFacade {

    @Resource
    private PartsReturnRequestService partsReturnRequestService;


    public void doDeadStockCalculate(List<PartsReturnRequestModelBO> returnRequsts) {

        partsReturnRequestService.exportPartReturnRequstFile(returnRequsts);
    }
}
