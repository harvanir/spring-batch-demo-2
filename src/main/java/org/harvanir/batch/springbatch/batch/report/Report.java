package org.harvanir.batch.springbatch.batch.report;

import org.springframework.batch.item.database.Order;
import org.springframework.jdbc.core.PreparedStatementSetter;

import java.util.Map;

/**
 * @author Harvan Irsyadi
 */
public interface Report {

    String getSelectClause();

    String getFromClause();

    String getWhereClause();

    Map<String, Object> getParameterValues();

    Map<String, Order> getSortKeys();

    String getSql();

    PreparedStatementSetter getPreparedStatementSetter();

    String[] getHeader();

    String getName();
}