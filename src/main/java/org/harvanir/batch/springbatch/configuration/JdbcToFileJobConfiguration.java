package org.harvanir.batch.springbatch.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.harvanir.batch.springbatch.batch.constant.BatchConstant;
import org.harvanir.batch.springbatch.batch.item.database.CustomJdbcCursorItemReader;
import org.harvanir.batch.springbatch.batch.mapper.ListObjectRowMapper;
import org.harvanir.batch.springbatch.batch.report.DefaultReportFactory;
import org.harvanir.batch.springbatch.batch.report.Report;
import org.harvanir.batch.springbatch.batch.report.ReportFactory;
import org.harvanir.batch.springbatch.batch.writer.WorkbookItemWriter;
import org.harvanir.batch.springbatch.entity.FileType;
import org.harvanir.batch.springbatch.entity.JobServiceRequest;
import org.harvanir.batch.springbatch.util.ObjectMapperUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.SimpleJobBuilder;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.function.FunctionItemProcessor;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.harvanir.batch.springbatch.batch.constant.BatchConstant.JdbcToFileJob.CSV_ITEM_WRITER;
import static org.harvanir.batch.springbatch.batch.constant.BatchConstant.JdbcToFileJob.DEFAULT_ITEM_READER;
import static org.harvanir.batch.springbatch.batch.constant.BatchConstant.JdbcToFileJob.EXCEL_COMPLETION_STEP;
import static org.harvanir.batch.springbatch.batch.constant.BatchConstant.JdbcToFileJob.EXCEL_ITEM_WRITER;
import static org.harvanir.batch.springbatch.batch.constant.BatchConstant.JdbcToFileJob.EXECEL_COMPLETION_TASKLET;
import static org.harvanir.batch.springbatch.batch.constant.BatchConstant.JdbcToFileJob.JOB_NAME;
import static org.harvanir.batch.springbatch.batch.constant.BatchConstant.JdbcToFileJob.PAGINATED_ITEM_READER;
import static org.harvanir.batch.springbatch.batch.constant.BatchConstant.JdbcToFileJob.WRITE_TO_FILE_STEP;

/**
 * @author Harvan Irsyadi
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class JdbcToFileJobConfiguration {

    @Bean
    public ReportFactory sqlBuilderFactory() {
        return new DefaultReportFactory();
    }

    @Bean
    @JobScope
    public JobServiceRequest jobServiceRequest(
            @Value("#{jobParameters}") Map<String, Object> jobParameters,
            ObjectMapper objectMapper
    ) {
        log.info("Initializing JobServiceRequest...");

        String payload = (String) jobParameters.get(BatchConstant.PAYLOAD);
        Assert.notNull(payload, "Payload cannot be null");

        JobServiceRequest request = ObjectMapperUtils.readValue(objectMapper, payload, JobServiceRequest.class);
        Assert.notNull(request, "Request cannot be null");

        return request;
    }

    @Bean
    @JobScope
    public Report report(
            ReportFactory reportFactory,
            JobServiceRequest jobServiceRequest
    ) {
        log.info("Initializing Report...");
        return reportFactory.get(jobServiceRequest.getId());
    }

    @Bean(name = DEFAULT_ITEM_READER)
    @JobScope
    public JdbcCursorItemReader<List<Object>> defaultItemReader(
            DataSource dataSource,
            Report report
    ) {
        log.info("Initializing defaultItemReader...");

        JdbcCursorItemReader<List<Object>> reader = new CustomJdbcCursorItemReader();
        reader.setDataSource(dataSource);
        reader.setRowMapper(new ListObjectRowMapper());
        reader.setSql(report.getSql());
        reader.setPreparedStatementSetter(report.getPreparedStatementSetter());

        return reader;
    }

    @Bean(name = PAGINATED_ITEM_READER)
    @JobScope
    public JdbcPagingItemReader<List<Object>> paginatedItemReader(
            DataSource dataSource,
            AppProperties properties,
            Report report
    ) {
        log.info("Initializing paginated item reader...");

        JdbcPagingItemReaderBuilder<List<Object>> builder = new JdbcPagingItemReaderBuilder<>();
        builder.selectClause(report.getSelectClause());
        builder.fromClause(report.getFromClause());
        builder.sortKeys(report.getSortKeys());
        builder.dataSource(dataSource);
        builder.rowMapper(new ListObjectRowMapper());
        builder.pageSize(properties.getReport().getChunkSize());
        builder.name("paginatedItemReader");

        return builder.build();
    }

    private String getFileName(AppProperties properties, String fileType) {
        String fileSeparator = System.getProperty("file.separator");
        String outputDir = properties.getReport().getOutputDirectory();

        return outputDir + fileSeparator + DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(ZonedDateTime.now()) + "-" + UUID.randomUUID().toString() + fileType;
    }

    @Bean(name = CSV_ITEM_WRITER)
    @JobScope
    public FlatFileItemWriter<List<Object>> csvItemWriter(Report report, AppProperties properties) {
        log.info("Initializing csvItemWriter...");

        FlatFileItemWriter<List<Object>> itemWriter = new FlatFileItemWriter<>();

        itemWriter.setResource(new FileSystemResource(getFileName(properties, ".csv")));
        itemWriter.setAppendAllowed(true);
        itemWriter.setLineAggregator(new DelimitedLineAggregator<>());
        itemWriter.setHeaderCallback(writer -> writer.write(String.join(",", report.getHeader())));

        return itemWriter;
    }

    @Bean(destroyMethod = "close")
    @JobScope
    public SXSSFWorkbook workbook(AppProperties appProperties) {
        log.info("Initializing Workbook...");

        return new SXSSFWorkbook(appProperties.getReport().getChunkSize());
    }

    @Bean(name = EXCEL_ITEM_WRITER)
    @JobScope
    public WorkbookItemWriter excelItemWriter(Workbook workBook, Report report) {
        log.info("Initializing excelItemWriter...");

        return new WorkbookItemWriter(workBook, report);
    }

    @Bean(name = WRITE_TO_FILE_STEP)
    @JobScope
    public Step writeToFileStep(
            StepBuilderFactory stepBuilderFactory,
            @Qualifier(DEFAULT_ITEM_READER) JdbcCursorItemReader<List<Object>> defaultItemReader,
            @Qualifier(PAGINATED_ITEM_READER) JdbcPagingItemReader<List<Object>> paginatedItemReader,
            @Qualifier(CSV_ITEM_WRITER) FlatFileItemWriter<List<Object>> csvItemWriter,
            @Qualifier(EXCEL_ITEM_WRITER) ItemWriter<List<Object>> excelItemWriter,
            AppProperties properties,
            JobServiceRequest jobServiceRequest
    ) {
        log.info("Initializing step...");

        SimpleStepBuilder<List<Object>, List<Object>> stepBuilder = stepBuilderFactory.get(WRITE_TO_FILE_STEP)
                .chunk(properties.getReport().getChunkSize());

        if (jobServiceRequest.getUsePaginate() != null && jobServiceRequest.getUsePaginate()) {
            stepBuilder.reader(paginatedItemReader);
        } else {
            stepBuilder.reader(defaultItemReader);
        }

        if (isCsv(jobServiceRequest)) {
            stepBuilder.writer(csvItemWriter);
        } else if (isExcel(jobServiceRequest)) {
            stepBuilder.writer(excelItemWriter);
        }

        stepBuilder.processor(new FunctionItemProcessor<>(o -> o));

        return stepBuilder.build();
    }

    private boolean isCsv(JobServiceRequest jobServiceRequest) {
        return jobServiceRequest.getFileType() == null || FileType.CSV.equals(jobServiceRequest.getFileType());
    }

    private boolean isExcel(JobServiceRequest jobServiceRequest) {
        return jobServiceRequest.getFileType() != null && FileType.EXCEL.equals(jobServiceRequest.getFileType());
    }

    @Bean(name = EXECEL_COMPLETION_TASKLET)
    @JobScope
    public Tasklet excelCompletionTasklet(Workbook workbook, AppProperties properties) {
        log.info("Initializing excelCompletionTasklet...");

        return (contribution, chunkContext) -> {
            log.info("Executing completion...");

            File file = new File(getFileName(properties, ".xlsx"));

            if (file.getParentFile().exists() || file.getParentFile().mkdirs()) {
                try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
                    workbook.write(fileOutputStream);
                } catch (Exception e) {
                    log.error("Error create file output stream.", e);
                }
            } else {
                log.warn("No file created: {}", file);
            }

            return RepeatStatus.FINISHED;
        };
    }

    @Bean(name = EXCEL_COMPLETION_STEP)
    @JobScope
    public Step excelCompletionStep(StepBuilderFactory stepBuilderFactory
            , @Qualifier(EXECEL_COMPLETION_TASKLET) Tasklet tasklet
    ) {
        return stepBuilderFactory.get(BatchConstant.JdbcToFileJob.EXCEL_COMPLETION_STEP)
                .tasklet(tasklet)
                .build();
    }

    private Job createJob(FileType fileType, JobBuilderFactory jobBuilderFactory, Step writeToFileStep, Step excelCompletionStep) {
        log.info("Initializing job...");

        SimpleJobBuilder jobBuilder = jobBuilderFactory.get(JOB_NAME)
                .start(writeToFileStep);

        if (FileType.EXCEL.equals(fileType)) {
            jobBuilder = jobBuilder.next(excelCompletionStep);
        }

        return jobBuilder.build();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Job jdbcToFileJob(JobServiceRequest jobServiceRequest, ApplicationContext applicationContext) {
        JobBuilderFactory jobBuilderFactory = applicationContext.getBean(JobBuilderFactory.class);
        Step writeToFileStep = (Step) applicationContext.getBean(WRITE_TO_FILE_STEP);
        Step excelCompletionStep = (Step) applicationContext.getBean(EXCEL_COMPLETION_STEP);

        return createJob(jobServiceRequest.getFileType(), jobBuilderFactory, writeToFileStep, excelCompletionStep);
    }
}