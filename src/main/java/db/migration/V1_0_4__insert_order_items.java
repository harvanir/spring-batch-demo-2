package db.migration;

import db.migration.util.BulkStatement;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Random;

@Slf4j
public class V1_0_4__insert_order_items extends BaseJavaMigration {

    private Random random = new Random(0);

    @Override
    public void migrate(Context context) throws Exception {
        insertOrder(context.getConnection());
        insertOrderItem(context.getConnection());
    }

    private void insertOrder(Connection connection) throws SQLException {
        String insert = "insert into orders (status, status_code, created_at, created_by, updated_at, updated_by) values (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(insert)) {
            BulkStatement bulkStatement = new BulkStatement();
            bulkStatement.executeBulk(ps, 10_000, this::setOrderValues);
        }
    }

    private void setOrderValues(PreparedStatement ps, int counter) {
        try {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            String system = "system";

            ps.setString(1, "PENDING");
            ps.setInt(2, 0);
            ps.setTimestamp(3, now);
            ps.setString(4, system);
            ps.setTimestamp(5, now);
            ps.setString(6, system);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void insertOrderItem(Connection connection) throws SQLException {
        String insert = "insert into order_items(order_id, item_id, quantity, price, created_at, created_by, updated_at, updated_by) values(?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(insert)) {
            BulkStatement bulkStatement = new BulkStatement();
            bulkStatement.executeBulk(ps, 10_000 * 10, true, this::setOrderItemValues);
        }
    }

    private void setOrderItemValues(PreparedStatement ps, int counter) {
        try {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            String system = "system";

            ps.setInt(1, random.nextInt(10_000));
            ps.setInt(2, random.nextInt(100_000));
            ps.setInt(3, random.nextInt(50));
            ps.setBigDecimal(4, BigDecimal.valueOf(random.nextInt(50_000)));
            ps.setTimestamp(5, now);
            ps.setString(6, system);
            ps.setTimestamp(7, now);
            ps.setString(8, system);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}