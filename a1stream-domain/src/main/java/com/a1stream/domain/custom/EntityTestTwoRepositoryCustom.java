package com.a1stream.domain.custom;

import com.a1stream.domain.vo.EntityTestTwoVO;

public interface EntityTestTwoRepositoryCustom {

    public EntityTestTwoVO findEntityTestTwoByStringTest(String string);

    public void insertCommonData(Long stringId, String stringTest, String stringName);

    public void deleteCommonDataByStringTest(String string);

    public void updateCommonDataByStringTest(String stringTest, String stringName);

}
