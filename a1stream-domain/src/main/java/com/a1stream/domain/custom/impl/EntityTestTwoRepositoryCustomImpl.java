package com.a1stream.domain.custom.impl;

import java.util.HashMap;
import java.util.Map;

import com.a1stream.domain.custom.EntityTestTwoRepositoryCustom;
import com.a1stream.domain.vo.EntityTestTwoVO;
import com.ymsl.solid.jpa.query.JpaNativeQuerySupportRepository;

public class EntityTestTwoRepositoryCustomImpl extends JpaNativeQuerySupportRepository implements EntityTestTwoRepositoryCustom {

    @Override
    public EntityTestTwoVO findEntityTestTwoByStringTest(String stringTest) {

        StringBuilder sql = new StringBuilder();
        Map<String, Object> param = new HashMap<>();

        sql.append(" select string_test as stringTest ");
        sql.append("      , string_nm   as stringName ");
        sql.append("   from entity_test_two           ");
        sql.append("  where string_test= :stringTest  ");
        param.put("stringTest", stringTest);

        return super.queryForSingle(sql.toString(), param, EntityTestTwoVO.class);
    }

    @Override
    public void insertCommonData(Long stringId, String stringTest, String stringName) {

        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> param = new HashMap<>();

        sql.append("INSERT INTO entity_test_two (string_id, string_test, string_nm) VALUES (:stringId, :stringTest, :stringName);");
        param.put("stringTest", stringTest);
        param.put("stringName", stringName);
        param.put("stringId", stringId);

        super.createSqlQuery(sql.toString(), param).executeUpdate();
    }

    @Override
    public void deleteCommonDataByStringTest(String stringTest) {

        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> param = new HashMap<>();

        sql.append(" delete from entity_test_two where string_test = :stringTest ");
        param.put("stringTest", stringTest);

        super.createSqlQuery(sql.toString(), param).executeUpdate();
    }

    @Override
    public void updateCommonDataByStringTest(String stringTest, String stringName) {
        StringBuilder sql = new StringBuilder();
        HashMap<String, Object> param = new HashMap<>();

        sql.append(" update entity_test_two set string_nm = :stringName where string_test = :stringTest ");
        param.put("stringTest", stringTest);
        param.put("stringName", stringName);

        super.createSqlQuery(sql.toString(), param).executeUpdate();
    }

}
