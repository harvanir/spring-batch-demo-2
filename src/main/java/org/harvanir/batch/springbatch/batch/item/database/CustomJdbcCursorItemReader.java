package org.harvanir.batch.springbatch.batch.item.database;

import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CustomJdbcCursorItemReader extends JdbcCursorItemReader<List<Object>> { // NOSONAR

    private Field sqlField;

    private Field preparedStatementField;

    private Field preparedStatementSetterField;

    public CustomJdbcCursorItemReader() {
        initialize();
    }

    private void initialize() {
        try {
            sqlField = JdbcCursorItemReader.class.getDeclaredField("sql");
            sqlField.setAccessible(true); //NOSONAR

            preparedStatementField = JdbcCursorItemReader.class.getDeclaredField("preparedStatement");
            preparedStatementField.setAccessible(true); //NOSONAR

            preparedStatementSetterField = JdbcCursorItemReader.class.getDeclaredField("preparedStatementSetter");
            preparedStatementSetterField.setAccessible(true); //NOSONAR
        } catch (Exception e) {
            throw new JdbcItemReaderException("Error initialize", e);
        }
    }

    @Override
    protected void openCursor(Connection con) {
        try {
            openCursorInternal(con);
        } catch (IllegalAccessException e) {
            throw new JdbcItemReaderException("Error open cursor.", e);
        }
    }

    private void openCursorInternal(Connection con) throws IllegalAccessException {
        String sql = (String) this.sqlField.get(this);
        PreparedStatementSetter preparedStatementSetter = (PreparedStatementSetter) this.preparedStatementSetterField.get(this);

        try {
            PreparedStatement preparedStatement;
            if (isUseSharedExtendedConnection()) {
                preparedStatement = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
                        ResultSet.HOLD_CURSORS_OVER_COMMIT);
            } else {
                preparedStatement = con.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            }

            preparedStatementField.set(this, preparedStatement); //NOSONAR
            applyStatementSettings(preparedStatement);

            if (preparedStatementSetter != null) {
                preparedStatementSetter.setValues(preparedStatement);
            }
            rs = preparedStatement.executeQuery();
            handleWarnings(preparedStatement);
        } catch (SQLException se) {
            close();
            DataAccessException exception = getExceptionTranslator().translate("Executing query", getSql(), se);
            if (exception != null) {
                throw exception;
            }
        }
    }
}