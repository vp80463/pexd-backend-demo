package com.a1stream.parts.service;

import com.a1stream.domain.bo.parts.SPQ030201BO;
import com.a1stream.domain.form.parts.SPQ030201Form;
import com.a1stream.domain.repository.InventoryTransactionRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liu chaoran
 */
@Service
public class SPQ0302Service {

    @Resource
    private InventoryTransactionRepository inventoryTransactionRepo;

    public List<SPQ030201BO> findPartsAdjustmentHistoryList(SPQ030201Form model, String siteId) {

        return inventoryTransactionRepo.findPartsAdjustmentHistoryList(model, siteId);
    }

}
