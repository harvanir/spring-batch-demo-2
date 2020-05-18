package org.harvanir.batch.springbatch.batch.report;

import org.springframework.batch.item.database.Order;
import org.springframework.beans.factory.InitializingBean;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Harvan Irsyadi
 */
public class DefaultReportFactory implements ReportFactory, InitializingBean {

    private final Map<Integer, Report> sqlBuilderMap = new HashMap<>();

    @Override
    public Report get(int id) {
        Report report = sqlBuilderMap.get(id);

        if (report != null) {
            return report;
        }

        throw new SqlBuilderNotFoundException("No Report found for id: " + id);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String selectClause = "select id, name, quantity, price, created_at, created_by, updated_at, updated_by ";
        String fromClause = "from items";
        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("created_at", Order.ASCENDING);
        sortKeys.put("id", Order.ASCENDING);

        sqlBuilderMap.put(1, DefaultReport.builder()
                .selectClause(selectClause)
                .fromClause(fromClause)
                .sortKeys(sortKeys)
                .sql(selectClause + fromClause)
                .preparedStatementSetter(null)
                .header(new String[]{"id", "name", "quantity", "price", "created_at", "created_by", "updated_at", "updated_by"})
                .name("Item Report")
                .build());

        selectClause = "select id, status, status_code, created_at, created_by, updated_at, updated_by ";
        fromClause = "from orders";
        sortKeys = new HashMap<>();
        sortKeys.put("created_at", Order.ASCENDING);
        sortKeys.put("id", Order.ASCENDING);

        sqlBuilderMap.put(2, DefaultReport.builder()
                .selectClause(selectClause)
                .fromClause(fromClause)
                .sortKeys(sortKeys)
                .sql(selectClause + fromClause)
                .preparedStatementSetter(null)
                .header(new String[]{"id", "status", "status_code", "created_at", "created_by", "updated_at", "updated_by"})
                .name("Order Report")
                .build());
    }


    static class SqlBuilderNotFoundException extends RuntimeException {

        public SqlBuilderNotFoundException(String message) {
            super(message);
        }
    }
}