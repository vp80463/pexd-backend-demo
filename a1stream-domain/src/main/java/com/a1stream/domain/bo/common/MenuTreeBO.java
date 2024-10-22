package com.a1stream.domain.bo.common;

import java.io.Serializable;
import java.util.List;

import com.a1stream.common.constants.CommonConstants;

import lombok.Getter;
import lombok.Setter;
import software.amazon.awssdk.utils.StringUtils;

@Getter
@Setter
public class MenuTreeBO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String menuCd;

    private String menuName;

    private String menuFullName;

    private int menuSeq;

    private String accessFlag;

    private List<MenuTreeBO> children;

    public void setMenuName(String menuName) {
        this.menuName = menuName;
        if (children.isEmpty()) {
            this.menuFullName = StringUtils.upperCase(this.menuCd) + CommonConstants.CHAR_SPACE + this.menuName;
        } else {
            this.menuFullName = this.menuName;
        }
    }
}