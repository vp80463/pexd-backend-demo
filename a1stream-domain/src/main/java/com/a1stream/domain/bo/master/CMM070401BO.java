package com.a1stream.domain.bo.master;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.a1stream.common.constants.CommonConstants;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.utils.StringUtils;

@Getter
@Setter
public class CMM070401BO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String menuId;

    private String menuCd;

    private String menuName;

    private String menuFullName;

    private int menuSeq;

    private String workDayCheck = CommonConstants.CHAR_N;
    private String accountMonthCheck = CommonConstants.CHAR_N;
    private String partsStockTakingCheck = CommonConstants.CHAR_N;
    private String unitStockTakingCheck = CommonConstants.CHAR_N;

    private List<CMM070401BO> children = new ArrayList<>();

    public void setMenuName(String menuName) {
        this.menuName = menuName;
        if (children.isEmpty()) {
            this.menuFullName = StringUtils.upperCase(this.menuCd) + CommonConstants.CHAR_SPACE + this.menuName;
        } else {
            this.menuFullName = this.menuName;
        }
    }
}
