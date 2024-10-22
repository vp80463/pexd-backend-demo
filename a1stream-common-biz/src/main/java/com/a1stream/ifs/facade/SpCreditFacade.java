package com.a1stream.ifs.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import com.a1stream.common.constants.CommonConstants;
import com.a1stream.domain.entity.CmmSiteMaster;
import com.a1stream.domain.vo.CmmMessageVO;
import com.a1stream.ifs.bo.SpCreditBO;
import com.a1stream.ifs.service.SpCreditService;
import com.alibaba.excel.util.StringUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.Resource;

@Component
public class SpCreditFacade {

    @Resource
    private SpCreditService spCreditService;

    public void doCredit(List<SpCreditBO> spCreditBOs){

        Set<String> siteIdSet = spCreditBOs.stream().map(SpCreditBO::getDealerCode).collect(Collectors.toSet());
        List<CmmSiteMaster> cmmSiteMasterList = spCreditService.findSiteMasterBySiteIdIn(siteIdSet);
        Map<String, CmmSiteMaster> mstProductVOMap = cmmSiteMasterList.stream().collect(Collectors.toMap(CmmSiteMaster::getSiteId, cmmSiteMaster -> cmmSiteMaster));
        List<Long> roleIdList = spCreditService.getRolIds();
        String roleListJson = convertToJson(roleIdList);

        // 删除CmmMessage表中siteId不等于666N的数据
        spCreditService.deleteIlliegalData();

        StringBuilder message = new StringBuilder(CommonConstants.CHAR_BLANK);

        // 按照siteId的数量进行循环
        for(String siteId : siteIdSet){

            // 判断site在cmm_site_master是否存在且有效
            if(null == mstProductVOMap.get(siteId)){
                return;
            }

            // 根据SiteId下的facility拼接message
            for(SpCreditBO spCreditBO : spCreditBOs){
                if(siteId.equals(spCreditBO.getDealerCode())){
                    if(StringUtils.isBlank(message)){
                        message.append("Số dư còn: " + spCreditBO.getCreditBalance() + CommonConstants.CHAR_SPACE);
                    }

                    message.append(CommonConstants.CHAR_COMMA + spCreditBO.getConsigneeCd() + " Giá trị hàng chờ chuyển: " + spCreditBO.getAllocateAmount());
                }
            }

            CmmMessageVO cmmMessageVO = buildCmmMessageVO(siteId, message.toString(), roleListJson);
            spCreditService.insertMessage(cmmMessageVO);

            // 清空message内容
            message.setLength(0);
        }
    }

    /**
     * roleList转json
     */
    public String convertToJson(List<Long> roleIdList){
        
        List<Map<String, Long>> jsonList = new ArrayList<>();

        // 遍历 roleIds 并构建 JSON 对象
        for (Long roleId : roleIdList) {
            jsonList.add(Map.of("roleId", roleId));
        }
        
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.writeValueAsString(jsonList);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    /**
     * 创建一个CmmMessage对象
     */
    private CmmMessageVO buildCmmMessageVO(String siteId, String message, String roleList) {

        CmmMessageVO cmmMessageVO = new CmmMessageVO();

        cmmMessageVO.setSiteId(siteId);
        cmmMessageVO.setMessage(message);
        cmmMessageVO.setSysRoleIdList(roleList);

        return cmmMessageVO;
    }
}