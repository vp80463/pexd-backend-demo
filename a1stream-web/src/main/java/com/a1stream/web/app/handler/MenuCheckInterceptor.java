/**
 *
 */
package com.a1stream.web.app.handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import com.a1stream.common.auth.PJUserDetails;
import com.a1stream.common.facade.MenuCheckFacade;
import com.a1stream.common.model.MenuCheckBO;
import com.ymsl.solid.base.exception.BusinessCodedException;
import com.ymsl.solid.web.AbstractHandlerInterceptor;
import com.ymsl.solid.web.usercontext.UserDetailsAccessor;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 菜单检查拦截器
 */
@Component
public class MenuCheckInterceptor extends AbstractHandlerInterceptor {

    @Resource
    private MenuCheckFacade menuCheckFac;

    @Override
    protected boolean executePreHandle(final HttpServletRequest request, final HttpServletResponse response, final HandlerMethod handler) throws Exception {

        MenuCheckBO menuCheck = new MenuCheckBO();
        // 夜间时段
        menuCheckFac.isMenuAccessTimePeriod(menuCheck);
        // 检查请求路径
        Pattern pattern = Pattern.compile("/([^/]+)/([^/]+)\\.json$");
        Matcher matcher = pattern.matcher(request.getRequestURI());
        String menuCd = "";
        if (matcher.find()) {
            String requestMapping = matcher.group(1); // cmm0704
            menuCd = requestMapping.length() > 7 ? requestMapping.substring(0, 7) : requestMapping;
        }
        PJUserDetails uc = (PJUserDetails) UserDetailsAccessor.DEFAULT.get();
        // 菜单检查：盘点状态
        menuCheckFac.isPartsStockTaking(menuCheck, uc.getDealerCode(), menuCd, uc.getDefaultPointId());

        if(!menuCheck.isPermission()) {
            throw new BusinessCodedException(menuCheck.getMessage());
        }

        return true;
    }
}
