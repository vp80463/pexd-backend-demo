package com.a1stream.common.auth;

import com.ymsl.solid.context.auth.BaseUserDetails;
import com.ymsl.solid.oauth2.client.config.OidcUserDetails;
import com.ymsl.solid.web.logging.mdc.MdcUser;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Getter
@Setter
public class PJUserDetails extends OidcUserDetails implements BaseUserDetails, MdcUser {

    @Serial
    private static final long serialVersionUID = 1L;

    private String userCode;
    private String userId;
    private String personName;
    private String personCode;
    private Long personId;
    private String loginDateTime;
    private String lastLoginDateTime;
    private String doFlag;

    //666N / dealer
    private String companyCode;
    //dealer
    private String dealerCode;
    private Locale appLocale;

    private String defaultPointCd;
    private Long defaultPointId;
    private String defaultPointNm;
    private String defaultPoint;
    
    //没有值,需要后续赋值
    private Long userOrgId;

    private String taxPeriod;

    private String localDataSourceId;

    private Map<String, Serializable> additionalInfo = new HashMap<>();

    /**
     * 端口
     */
    private String port;

    public PJUserDetails(String username, String password,
                         Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);

    }

    // for aws cognito oauth2 oidc constructor
    public PJUserDetails(String name, Collection<? extends GrantedAuthority> authorities,
                          OidcIdToken idToken, OidcUserRequest userRequest, Map<String, Object> claims,
                          OidcUserInfo userInfo, Map<String, Object> attributes) {
        super(name, authorities, idToken, userRequest, claims, userInfo, attributes);
    }

    @Override
    public String getMdcUserInfo() {
        return String.format("%s", userCode);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PJUserDetails userDetails = (PJUserDetails) o;
        if (!userCode.equals(userDetails.getUserCode()) || !appLocale.equals(userDetails.getAppLocale())) {
            return false;
        }
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
