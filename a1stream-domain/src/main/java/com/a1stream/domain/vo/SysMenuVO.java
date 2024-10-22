package com.a1stream.domain.vo;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.a1stream.common.constants.CommonConstants;

import lombok.Getter;
import lombok.Setter;


/**
 * @author mid2010
 */
@Setter
@Getter
public class SysMenuVO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8816169058375246481L;

    private String menuId;

    private String siteId;

    private String menuCode;

    private String menuName;

    private String parentMenuId;

    private String urlType;

    private Integer menuSeq = CommonConstants.INTEGER_ZERO;

    private LocalDateTime effectivDate;

    private LocalDateTime expiredDate;

    private String linkUrl;

    private String visitableFlag;

    private String windowTarget;

    private String menuPic;

    private String menuLabel;

    private String extendList;

    private Integer updateCount;

    private String lastUpdatedBy;

    private LocalDateTime lastUpdated;

    private String createdBy;

    private LocalDateTime dateCreated;
}
