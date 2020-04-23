package db.migration;

import db.migration.util.BulkStatement;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

@Slf4j
public class V1_0_1__insert_items extends BaseJavaMigration {

    @Override
    public void migrate(Context context) throws Exception {
        String insert = "insert into items (name, quantity, price, created_at, created_by, updated_at, updated_by) values (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = context.getConnection().prepareStatement(insert)) {
            BulkStatement bulkStatement = new BulkStatement();
            bulkStatement.executeBulk(ps, 100_000, this::setValues);

            log.info("Finished...");
        }
    }

    private void setValues(PreparedStatement ps, int counter) {
        try {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            String system = "system";

            ps.setString(1, String.format("Name %s", counter + 1));
            ps.setInt(2, 50);
            ps.setBigDecimal(3, BigDecimal.valueOf(50_000.00));
            ps.setTimestamp(4, now);
            ps.setString(5, system);
            ps.setTimestamp(6, now);
            ps.setString(7, system);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}