package db.migration.util;

import lombok.extern.slf4j.Slf4j;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.ObjIntConsumer;

/**
 * @author Harvan Irsyadi
 */
@Slf4j
public class BulkStatement {

    public void executeBulk(PreparedStatement ps, int loop, ObjIntConsumer<PreparedStatement> preparedStatementConsumer) throws SQLException {
        executeBulk(ps, loop, false, preparedStatementConsumer);
    }

    public void executeBulk(PreparedStatement ps, int loop, boolean ignoreError, ObjIntConsumer<PreparedStatement> preparedStatementConsumer) throws SQLException {
        boolean executed = false;

        for (int i = 0; i < loop; i++) {
            preparedStatementConsumer.accept(ps, i);

            ps.addBatch();

            if ((i + 1) % 50 == 0) {
                executeBatch(ps, ignoreError);
                executed = true;
            } else {
                executed = false;
            }
        }

        if (!executed) {
            executeBatch(ps, ignoreError);
        }
    }

    private void executeBatch(PreparedStatement ps, boolean ignoreError) throws SQLException {
        if (ignoreError) {
            try {
                executeBatch(ps);
            } catch (SQLException e) {
                log.warn("Found error.", e);

                try {
                    ps.getConnection().rollback();
                } catch (SQLException se) {
                    log.error("Error rollback.", se);
                }
            }
        } else {
            executeBatch(ps);
        }
    }

    private void executeBatch(PreparedStatement ps) throws SQLException {
        ps.executeBatch();
        ps.getConnection().commit();
    }
}