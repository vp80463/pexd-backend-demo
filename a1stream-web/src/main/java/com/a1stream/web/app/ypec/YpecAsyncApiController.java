package com.a1stream.web.app.ypec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.facade.ValueListFacade;
import com.a1stream.common.manager.MessageSendManager;
import com.a1stream.common.model.MessageSendBO;
import com.a1stream.common.model.PartsVLBO;
import com.a1stream.common.model.PartsVLForm;
import com.a1stream.common.model.ValueListResultBO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ymsl.solid.base.util.BeanMapUtils;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("public/ypec")
public class YpecAsyncApiController {

    private static final String CHAR_PARTS_ID    = "partsId";
    private static final String CHAR_PARTS_NO    = "partsNo";
    private static final String CHAR_PARTS_NM    = "partsNm";
    private static final String CHAR_SUPERSEDING_PARTS_ID    = "supersedingPartsId";
    private static final String CHAR_SUPERSEDING_PARTS_CD    = "supersedingPartsCd";
    private static final String CHAR_SUPERSEDING_PARTS_NM    = "supersedingPartsNm";
    private static final String CHAR_LARGEGROUP_NM    = "largeGroupNm";
    private static final String CHAR_ONHAND_QTY    = "onHandQty";
    private static final String CHAR_SALESLOT_QTY    = "salesLotQty";
    private static final String CHAR_BATTERY_FLAG    = "batteryFlag";
    private static final String CHAR_STD_PRICE    = "stdPrice";
    private static final String CHAR_TAX_RATE    = "taxRate";
    private static final String CHAR_LOCATION_ID    = "locationId";
    private static final String CHAR_LOCATION_CD    = "locationCd";
    private static final String CHAR_ORDER_QTY    = "orderQty";

    private static final String CHAR_YPEC_PARTS_NO    = "part_no_from_ypec";
    private static final String CHAR_YPEC_PARTS_QTY    = "order_qty_from_ypec";
    private static final String CHAR_POINT_ID    = "pointId";
    private static final String CHAR_SITE_ID     = "siteId";
    private static final String CHAR_USER_CD     = "userCd";
    private static final String CHAR_TAXPERIOD     = "taxPeriod";

    private static final String CHAR_SUCCESS_MESSAGE     = "Data returned successfully, please close the page";
    private static final String CHAR_ERROR_MESSAGE     = "The product does not exist";


    @Resource
    ValueListFacade valueListFacade;

    @Resource
    private MessageSendManager messageSendManager;
    
    @RequestMapping(value = "/getYpecParts.json")
    public String getYpecParts(
        HttpServletRequest request) {

        String[] partNos   = request.getParameterValues(CHAR_YPEC_PARTS_NO);
        String[] partQtys = request.getParameterValues(CHAR_YPEC_PARTS_QTY);
        Long pointId = Long.parseLong(request.getParameter(CHAR_POINT_ID));
        String siteId = request.getParameter(CHAR_SITE_ID);
        String userCd = request.getParameter(CHAR_USER_CD);
        String taxPeriod = request.getParameter(CHAR_TAXPERIOD);
        //转换为1对1的map
        Map<String, String> partsMap = IntStream.range(CommonConstants.INTEGER_ZERO, partNos.length).boxed().collect(Collectors.toMap(i -> partNos[i], i -> partQtys[i]));
        
        PartsVLForm partsVLForm = new PartsVLForm();
        partsVLForm.setAutoFillFlag(CommonConstants.CHAR_Y);
        partsVLForm.setCurrentPage(CommonConstants.INTEGER_ONE);
        //能导入三十个产品
        partsVLForm.setPageSize(CommonConstants.INTEGER_THIRTY);
        partsVLForm.setFacilityId(pointId);
        partsVLForm.setPartsCds(Arrays.asList(partNos));
        ValueListResultBO partsList = valueListFacade.findPartsList(partsVLForm,siteId,taxPeriod);
        List<PartsVLBO> list = BeanMapUtils.mapListTo(partsList.getList(),PartsVLBO.class);

        //返回的数据
        List<Map<String, String>> resultList = new ArrayList<>();
        
        for (PartsVLBO data : list) {

            Map<String,String> result = new HashMap<>();
            result.put(CHAR_PARTS_ID, data.getId());
            result.put(CHAR_PARTS_NO, data.getCode());
            result.put(CHAR_PARTS_NM, data.getName());
            result.put(CHAR_SUPERSEDING_PARTS_ID, data.getSupersedingPartsId());
            result.put(CHAR_SUPERSEDING_PARTS_CD, data.getSupersedingPartsCd());
            result.put(CHAR_SUPERSEDING_PARTS_NM, data.getSupersedingPartsNm());
            result.put(CHAR_LARGEGROUP_NM, data.getLargeGroup());
            result.put(CHAR_ONHAND_QTY, (data.getOnHandQty() != null) ? data.getOnHandQty().toString() : CommonConstants.CHAR_ZERO);
            result.put(CHAR_SALESLOT_QTY, data.getSalLotSize().toString());
            result.put(CHAR_BATTERY_FLAG, data.getBatteryFlag());
            result.put(CHAR_STD_PRICE,data.getStdRetailPrice().toString());
            result.put(CHAR_TAX_RATE,(data.getTaxRate() != null) ? data.getTaxRate().toString() : CommonConstants.CHAR_BLANK);
            result.put(CHAR_LOCATION_ID,data.getMainLocationId());
            result.put(CHAR_LOCATION_CD,data.getMainLocationCd());
            result.put(CHAR_ORDER_QTY, partsMap.get(data.getCode()));

            resultList.add(result);
        }

        if(!resultList.isEmpty()){

            //将查询出来的数据转成json通过websocket发送给前端
            ObjectMapper objectMapper = new ObjectMapper();
            String json = CommonConstants.CHAR_BLANK;
            try {
                json = objectMapper.writeValueAsString(resultList);
                MessageSendBO messageSendBO = new MessageSendBO();
                messageSendBO.setMessageType(PJConstants.MessageType.YPEC);
                messageSendBO.setMessage(json);
                json = objectMapper.writeValueAsString(messageSendBO);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }

            messageSendManager.sendMessageByCustomerForUser(siteId,userCd,json); // 调用 messageService 将消息发送给前端
            return CHAR_SUCCESS_MESSAGE;
        }else{
            return CHAR_ERROR_MESSAGE;
        }
    }
}
