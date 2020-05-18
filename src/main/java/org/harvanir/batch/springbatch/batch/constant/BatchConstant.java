package org.harvanir.batch.springbatch.batch.constant;

/**
 * @author Harvan Irsyadi
 */
public class BatchConstant {

    public static final String RUN_ID = "run.id";

    public static final String PAYLOAD = "payload";

    private BatchConstant() {
    }

    public static class JdbcToFileJob {

        public static final String JOB_NAME = "jdbcToFileJob";

        public static final String WRITE_TO_FILE_STEP = "writeToFileStep";

        public static final String EXCEL_COMPLETION_STEP = "excelCompletionStep";

        private JdbcToFileJob() {
        }
    }
}