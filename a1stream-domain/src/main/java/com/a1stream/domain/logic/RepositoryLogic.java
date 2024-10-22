package com.a1stream.domain.logic;

import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Component;

@Component
public class RepositoryLogic {

    public void appendPagePara(Integer pageSize, Integer currentPage, StringBuilder sql, Map<String, Object> params) {

        if (!Objects.isNull(pageSize) && !Objects.isNull(currentPage) && pageSize != 0 && currentPage >= 1) {

            sql.append(" OFFSET :offsetValue ");
            sql.append(" LIMIT :limitValue");

            params.put("offsetValue", pageSize * (currentPage - 1));
            params.put("limitValue", pageSize);
        }
   }
}
