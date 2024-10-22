package com.a1stream.common.manager;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.constants.PJConstants;
import com.a1stream.common.model.HomePageMessageBO;
import com.a1stream.common.model.HomePageMessageItemBO;
import com.a1stream.common.model.MessageSendBO;
import com.a1stream.common.utils.TimeChangeUtils;
import com.a1stream.domain.entity.CmmMessageRemind;
import com.a1stream.domain.repository.CmmMessageRemindRepository;
import com.a1stream.domain.vo.CmmMessageRemindVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ymsl.plugins.userauth.util.ListSortUtils;
import com.ymsl.solid.base.util.BeanMapUtils;
import com.ymsl.solid.base.util.DateUtils;
import com.ymsl.solid.websocket.server.topic.sender.MessageDTO;
import com.ymsl.solid.websocket.server.topic.sender.TopicSender;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息推送
 *
 * @author dong zhen
 */
@Slf4j
@Component
public class MessageSendManager {

    @Resource
    private TopicSender topicSender;

    @Resource
    private CmmMessageRemindRepository cmmMessageRemindRepository;

    // 按照siteId推送消息(非自定义，按照cmmMessageRemind表的数据来)
    public void sendMessage(String siteId) {

        topicSender.sendMessage(siteId, new MessageDTO(this.createMessage(siteId)));
    }

    // 按照siteId和userId推送消息(非自定义，按照cmmMessageRemind表的数据来)
    public void sendMessage(String siteId, String userCd) {

        topicSender.sendMessageToSession(siteId, userCd, new MessageDTO(this.createMessage(siteId)));
    }

    // 按照siteId推送消息(自定义消息)
    public void sendMessageByCustomerForSite(String siteId, String message) {

        topicSender.sendMessage(siteId, new MessageDTO(message));
    }

    // 按照siteId和userId推送消息(自定义消息)
    public void sendMessageByCustomerForUser(String siteId, String userCd, String message) {

        topicSender.sendMessageToSession(siteId, userCd, new MessageDTO(message));
    }

    /**
     * 根据站点ID创建消息字符串。
     * 该方法查询未读消息，对消息进行排序，然后将消息转换为JSON字符串返回。
     *
     * @param siteId 站点ID，用于查询特定站点的未读消息。
     * @return 包含未读消息的JSON字符串。
     */
    private String createMessage(String siteId) {

        ObjectMapper mapper = new ObjectMapper();
        String message = "";
        try {
            List<CmmMessageRemind> chatMessageRemindList = cmmMessageRemindRepository.findBySiteIdAndReadType(siteId, PJConstants.ReadType.UNREAD);
            ListSortUtils.sort(chatMessageRemindList, new String[]{"createDate"}, new boolean[]{false});

            List<HomePageMessageBO> messageList = new ArrayList<>();
            HomePageMessageBO messageBO = new HomePageMessageBO();
            messageBO.setKey(PJConstants.NoticeType.MESSAGE);
            messageBO.setName(PJConstants.NoticeType.MESSAGE);

            List<HomePageMessageItemBO> messageItemList = new ArrayList<>();
            for (CmmMessageRemind member : chatMessageRemindList){
                HomePageMessageItemBO messageItemBO = new HomePageMessageItemBO();

                messageItemBO.setTitle(member.getBusinessType());
                messageItemBO.setDatetime(TimeChangeUtils.changeDateFormat(member.getCreateDate().format(DateTimeFormatter.ofPattern(DateUtils.FORMAT_YMDHMS)), DateUtils.FORMAT_YMDHMS, CommonConstants.DEFAULT_SIMPLEDATE_YMDHMS));
                messageItemBO.setDescription(member.getMessage());
                messageItemList.add(messageItemBO);
            }
            messageBO.setList(messageItemList);
            messageList.add(messageBO);
            MessageSendBO messageSendBO = new MessageSendBO();
            messageSendBO.setMessageType(PJConstants.MessageType.HOMEPAGE);
            messageSendBO.setMessage(messageList);
            message = mapper.writeValueAsString(messageSendBO);
        } catch (Exception e) {
            log.error("消息创建失败: {1}", e);
        }

        return message;
    }

    /* (non-Javadoc)
     * @see jp.co.yamaha_motor.xm03.common.manager.IMessageNotificationManager#notifyUserRolesInCode(java.lang.String, java.util.List, java.lang.String, java.lang.String, java.lang.String, java.lang.String[])
     */
    public void notifyUserRolesInCode(  String siteId,
                                        List<String> userRoleCodes,
                                        String messageTypeId,
                                        String messageCategoryId,
                                        String msgContents,
                                        String... templateArguments) {

        if(this.isListEmpty(userRoleCodes)) {
            return;
        }
    }

    public void notifyUserRoles(String siteId,
                                List<Long> roleIds,
                                String roleType,
                                String businessType,
                                String message,
                                String readCategoryType,
                                String createUser) {

        if(CollectionUtils.isEmpty(roleIds)) {
            return;
        }

        String roles = "[" + roleIds.stream()
                                    .map(id -> "{\"roleId\":\"" + id + "\"}")
                                    .collect(Collectors.joining(",")) + "]";

        CmmMessageRemindVO remind = new CmmMessageRemindVO();

        remind.setSysRoleIdList(roles);
        remind.setMessage(message);
        remind.setSiteId(siteId);
        remind.setBusinessType(businessType);
        remind.setReadCategoryType(readCategoryType);
        remind.setReadType(PJConstants.ReadType.UNREAD);
        remind.setCreateUser(createUser);
        remind.setCreateDate(LocalDateTime.now());
        remind.setRoleType(roleType);


        cmmMessageRemindRepository.save(BeanMapUtils.mapTo(remind, CmmMessageRemind.class));
    }

    /**
     * 
     */
    // private CmmMessageRemindVO buildCmmMessageRemindVO(String siteId,Long roleId, String roleCd, String roleType, String message, String businessType,String readCategoryType) {

    //     CmmMessageRemindVO cmmMessageRemindVO = new CmmMessageRemindVO();

    //     cmmMessageRemindVO.setSiteId(siteId);
    //     // cmmMessageRemindVO.setSysRoleId(roleId);
    //     // cmmMessageRemindVO.setSysRoleCd(roleCd);
    //     cmmMessageRemindVO.setBusinessType(roleType);
    //     cmmMessageRemindVO.setMessage(message);
    //     cmmMessageRemindVO.setBusinessType(businessType);
    //     cmmMessageRemindVO.setReadType(PJConstants.ReadType.UNREAD);
    //     cmmMessageRemindVO.setRoleType(roleType);
    //     cmmMessageRemindVO.setReadCategoryType(readCategoryType);

    //     return cmmMessageRemindVO;
    // }

    /**
     *
     * @param list
     * @return
     */
    private boolean isListEmpty(List<String> list) {
        return list==null || list.isEmpty();
    }

    /**
     *
     * @param siteId
     * @param roleCodes
     * @return
     */
//    private List<String> getRoleIdsByRoleCodes(String siteId, List<String> roleCodes) {
//
//        List<SysRole> rs =  this.createSysRoleQuery()
//                                .propertyIn(SysRole.ROLE_CODE, roleCodes.toArray(new String[roleCodes.size()]))
//                                .propertyIn(SysRole.SITE_ID, new String[] {siteId, COMMON_SITE_ID})//Must include the common site id.
//                                .list();
//        List<String> result = new ArrayList<String>(rs.size());
//        for (SysRole sysRole : rs) {
//            result.add(sysRole.getRoleId());
//        }
//        return result;
//    }

}
