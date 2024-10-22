package com.a1stream.domain.batchdao;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;

public abstract class BaseJdbcDao {

    private JdbcOperations jdbc;

    /**
     * Execute a batch using the supplied SQL statement with the batch of supplied arguments.
     * @param sql the SQL statement to execute
     * @param batchArgs the List of Object arrays containing the batch of arguments for the query
     * @return an array containing the numbers of rows affected by each update in the batch
     */
    protected int[] batchUpdate(String sql, List<Object[]> batchArgs) throws DataAccessException {
        return this.jdbc.batchUpdate(sql, batchArgs);
    }

//    protected int update(String sql, String type) throws DataAccessException {
//
//        return this.jdbc.update(sql, type);
//    }

    protected int update(String sql, @Nullable Object... args) throws DataAccessException {

        return this.jdbc.update(sql, args);
    }

    protected List<Map<String, Object>> queryForList(String sql, Map<String,Object> parameters) throws DataAccessException {

        return this.jdbc.queryForList(sql, parameters);
    }

    protected List<T> query(String sql, RowMapper<T> prdAbcRowMapper, Object args) throws DataAccessException {

        return this.jdbc.query(sql, prdAbcRowMapper, args);
    }

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbc = new JdbcTemplate(dataSource);
    }
}
