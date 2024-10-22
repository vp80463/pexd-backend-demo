package com.a1stream.common.logic;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.a1stream.common.model.ConstantsBO;
import com.ymsl.plugins.userauth.util.ListSortUtils;

import software.amazon.awssdk.utils.StringUtils;

@Component
public class ConstantsLogic {

    public List<ConstantsBO> getConstantsData(Field[] fields) {

        List<ConstantsBO> constantsBOs = new ArrayList<>();

        for (Field field : fields) {

            if (field.getType() == ConstantsBO.class) {

                try {
                    constantsBOs.add((ConstantsBO) field.get(field.getName()));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        ListSortUtils.sort(constantsBOs, new String[] { "seq" });

        return constantsBOs;
    }

    public Map<String, ConstantsBO> getConstantsMap(Field[] fields) {

        List<ConstantsBO> constantsData = getConstantsData(fields);

        return constantsData.stream().collect(Collectors.toMap(ConstantsBO::getCodeDbid, Function.identity()));
    }

    public ConstantsBO getConstantsByCodeDbId(Field[] fields, String codeDbId) {

        if (StringUtils.isBlank(codeDbId)) {return new ConstantsBO();}

        return Objects.requireNonNullElse(this.getConstantsMap(fields).get(codeDbId), new ConstantsBO());
    }
}
