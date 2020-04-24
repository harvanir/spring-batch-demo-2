package org.harvanir.batch.springbatch.batch.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.batch.item.database.Order;
import org.springframework.jdbc.core.PreparedStatementSetter;

import java.util.Map;

/**
 * @author Harvan Irsyadi
 */
@Builder
@AllArgsConstructor
public class DefaultReport implements Report {

    private final String selectClause;

    private final String fromClause;

    private final Map<String, Order> sortKeys;

    private final String sql;

    private final PreparedStatementSetter preparedStatementSetter;

    private final String header;

    @Override
    public String getSelectClause() {
        return selectClause;
    }

    @Override
    public String getFromClause() {
        return fromClause;
    }

    @Override
    public Map<String, Order> getSortKeys() {
        return sortKeys;
    }

    @Override
    public String getSql() {
        return sql;
    }

    @Override
    public PreparedStatementSetter getPreparedStatementSetter() {
        return preparedStatementSetter;
    }

    @Override
    public String getHeader() {
        return header;
    }
}