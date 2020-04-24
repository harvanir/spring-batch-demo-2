package org.harvanir.batch.springbatch.batch.mapper;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Harvan Irsyadi
 */
public class ListObjectRowMapper implements RowMapper<List<Object>> {

    private int columnCount = -1;


    private void initialize(ResultSet rs) throws SQLException {
        if (columnCount == -1) {
            ResultSetMetaData metaData = rs.getMetaData();
            columnCount = metaData.getColumnCount();
        }
    }

    @Override
    public List<Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
        initialize(rs);

        List<Object> values = new ArrayList<>(columnCount);

        for (int i = 1; i <= columnCount; i++) {
            values.add(rs.getObject(i));
        }

        return values;
    }
}