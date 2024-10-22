package com.a1stream.common.facade;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.service.MenuService;
import com.a1stream.domain.bo.common.MenuBO;
import com.a1stream.domain.bo.common.MenuCategoryBO;
import com.a1stream.domain.bo.common.MenuTransationBO;
import com.a1stream.domain.vo.UserHabitVO;
import com.alibaba.excel.util.StringUtils;

import jakarta.annotation.Resource;

/**
 * 主页菜单获取逻辑
 *
 * @author mid1341
 */
@Component
public class MenuFacade {

    @Resource
    private MenuService menuService;

    public Object[] getMenuJsonStrByUserId(String userId) {

        List<MenuBO> menuList = this.findMeueByUserId(userId);

        List<MenuBO> levelOneMenus = this.filterMenuByNoneParentId(menuList);
        List<MenuBO> levelTwoMenus = this.filterMenuByParentId(menuList, levelOneMenus);
        List<MenuBO> levelThreeMenus = this.filterMenuByParentId(menuList, levelTwoMenus);

        Map<String, List<MenuBO>> levelTwoMenusMap = levelTwoMenus.stream().collect(Collectors.groupingBy(MenuBO::getParentId));
        Map<String, List<MenuBO>> levelThreeMenusMap = levelThreeMenus.stream().collect(Collectors.groupingBy(MenuBO::getParentId));

        List<MenuCategoryBO> menusInHomePage = new ArrayList<>();

        MenuCategoryBO levelOneMenuHomePage;
        MenuCategoryBO levelTwoMenuHomePage;
        MenuTransationBO levelThreeMenuHomePage;

        for (MenuBO levelOneMenu : levelOneMenus) {

            levelOneMenuHomePage = new MenuCategoryBO(levelOneMenu);

            for (MenuBO levelTwoMenu : (levelTwoMenusMap.containsKey(levelOneMenu.getId()) ? levelTwoMenusMap.get(levelOneMenu.getId()) : new ArrayList<MenuBO>()) ) {

                levelTwoMenuHomePage = new MenuCategoryBO(levelTwoMenu);

                for (MenuBO levelThreeMenu : (levelThreeMenusMap.containsKey(levelTwoMenu.getId()) ? levelThreeMenusMap.get(levelTwoMenu.getId()) : new ArrayList<MenuBO>())) {

                    levelThreeMenuHomePage = new MenuTransationBO(levelThreeMenu);

                    levelTwoMenuHomePage.getChildrenList().add(levelThreeMenuHomePage);
                }

                levelTwoMenuHomePage.setChildren(levelTwoMenuHomePage.getChildrenList().stream().toArray(Object[]::new));
                levelTwoMenuHomePage.setChildrenList(null);
                levelOneMenuHomePage.getChildrenList().add(levelTwoMenuHomePage);
            }

            levelOneMenuHomePage.setChildren(levelOneMenuHomePage.getChildrenList().stream().toArray(Object[]::new));
            levelOneMenuHomePage.setChildrenList(null);
            menusInHomePage.add(levelOneMenuHomePage);
        }

        return menusInHomePage.stream().toArray(Object[]::new);
    }

    private List<MenuBO> findMeueByUserId(String userId) {

        //仅666N才有返回值。
        UserHabitVO switchDealer = menuService.findSwitchDealer(userId);

        //不为空，且有选择对象时，才代表YMVN选择了切换经销商
        return !Objects.isNull(switchDealer) && StringUtils.isNotBlank(switchDealer.getHabitContent())
                            ? menuService.findMenuListBySwitchDealer(userId, switchDealer.getHabitContent())
                            : menuService.findMenuListByUserId(userId);
    }

    private List<MenuBO> filterMenuByNoneParentId(List<MenuBO> menuList) {
        return menuList.stream().filter(menu -> StringUtils.isBlank(menu.getParentId()))
                                .map(menu -> {menu.setRank(Objects.requireNonNullElse(menu.getRank(), 0)); return menu;})
                                .sorted(Comparator.comparing(MenuBO::getRank)).toList();
    }

    private List<MenuBO> filterMenuByParentId(List<MenuBO> menuList, List<MenuBO> parentMenuList) {

        Set<String> parentMenuIds = parentMenuList.stream().map(MenuBO::getId).collect(Collectors.toSet());

        return menuList.stream().filter(menu -> parentMenuIds.contains(menu.getParentId()))
                                .map(menu -> {menu.setRank(Objects.requireNonNullElse(menu.getRank(), 0)); return menu;})
                                .sorted(Comparator.comparing(MenuBO::getRank))
                                .toList();
    }
}
