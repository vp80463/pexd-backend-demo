/**
 *
 */
package com.a1stream.parts.service;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.a1stream.domain.bo.parts.SPM020101BO;
import com.a1stream.domain.form.parts.SPM020101Form;
import com.a1stream.domain.repository.SalesOrderRepository;

import jakarta.annotation.Resource;

/**
* 功能描述:
*
* @author mid2259
*/
@Service
public class SPM020101Service {

    @Resource
    SalesOrderRepository salesOrderRepository;

    public Page<SPM020101BO> searchSalesOrderList(SPM020101Form model) {

        return salesOrderRepository.searchSalesOrderList(model);
    }
}
