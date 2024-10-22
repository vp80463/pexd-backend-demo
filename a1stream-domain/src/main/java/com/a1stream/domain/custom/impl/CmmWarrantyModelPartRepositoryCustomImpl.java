package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.a1stream.domain.custom.CmmWarrantyModelPartRepositoryCustom;
import com.a1stream.domain.vo.CmmWarrantyModelPartVO;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class CmmWarrantyModelPartRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements CmmWarrantyModelPartRepositoryCustom {


    /**
     * author: He Xiaochuan
     */
    @Override
    public List<CmmWarrantyModelPartVO> findModelPartByModelCd(String modelCd){

        Map<String, Object> params = new HashMap<>();
        StringBuilder sql = new StringBuilder();

        sql.append(" SELECT * FROM cmm_warranty_model_part cwmp ");
        sql.append("  WHERE :modelCd LIKE cwmp.model_cd || '%'  ");

        params.put("modelCd" ,modelCd);

        return super.queryForList(sql.toString(), params, CmmWarrantyModelPartVO.class);
    }
}
