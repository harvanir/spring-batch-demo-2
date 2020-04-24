package org.harvanir.batch.springbatch.batch.constant;

/**
 * @author Harvan Irsyadi
 */
public class BatchConstant {

    public static final String RUN_ID = "run.id";

    public static final String PAYLOAD = "payload";

    private BatchConstant() {
    }

    public static class JdbcToCsvJob {

        public static final String JOB_NAME = "jdbcToCsvJob";

        public static final String WRITE_TO_FILE_STEP = "writeToFileStep";

        private JdbcToCsvJob() {
        }
    }
}