package com.a1stream.common.service;

import com.a1stream.common.constants.CommonConstants;
import com.a1stream.common.model.DealerInfoBO;
import com.a1stream.common.model.MenuSettingForm;
import com.a1stream.domain.entity.UserHabit;
import com.a1stream.domain.repository.CmmSiteMasterRepository;
import com.a1stream.domain.repository.UserHabitRepository;
import com.a1stream.domain.vo.UserHabitVO;
import com.ymsl.solid.base.util.BeanMapUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author dong zhen
 */
@Service
public class MenuSettingService {

    @Resource
    private UserHabitRepository userHabitRepository;

    @Resource
    private CmmSiteMasterRepository cmmSiteMasterRepository;

    public UserHabitVO getUserHabit(String baseSiteId, String userId, String userHabitTypeId) {

        UserHabit userHabit = userHabitRepository.findByUserIdAndSiteIdAndUserHabitTypeId(userId, baseSiteId, userHabitTypeId);
        return BeanMapUtils.mapTo(userHabit, UserHabitVO.class);
    }

    public List<DealerInfoBO> getAllDealerList(String dealerRetrieve) {

        return cmmSiteMasterRepository.getAllDealerList(dealerRetrieve);
    }

    public void switchSystemDealer(MenuSettingForm form) {

        UserHabit userHabit = userHabitRepository.findByUserIdAndSiteIdAndUserHabitTypeId(form.getUserId(), form.getSiteId(), form.getUserHabitTypeId());
        if (userHabit == null) {
            userHabit = new UserHabit();
            userHabit.setUserId(form.getUserId());
            userHabit.setSiteId(form.getSiteId());
            userHabit.setUserHabitTypeId(form.getUserHabitTypeId());
        }
        userHabit.setHabitContent(form.getDealerCd());
        userHabitRepository.save(userHabit);
    }

    public void clearSystemDealer(MenuSettingForm form) {

        UserHabit userHabit = userHabitRepository.findByUserIdAndSiteIdAndUserHabitTypeId(form.getUserId(), form.getSiteId(), form.getUserHabitTypeId());
        if (userHabit != null){
            userHabit.setHabitContent(CommonConstants.CHAR_BLANK);
            userHabitRepository.save(userHabit);
        }
    }
}
