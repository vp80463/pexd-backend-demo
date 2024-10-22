package com.a1stream.parts.service;

import com.a1stream.domain.bo.parts.SPQ030401BO;
import com.a1stream.domain.form.parts.SPQ030401Form;
import com.a1stream.domain.repository.InventoryTransactionRepository;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liu chaoran
 */
@Service
public class SPQ0304Service {

    @Resource
    private InventoryTransactionRepository inventoryTransactionRepo;

    public List<SPQ030401BO> findPartsInOutHistoryList(SPQ030401Form model, String siteId) {

        return inventoryTransactionRepo.findPartsInOutHistoryList(model, siteId);
    }

}
