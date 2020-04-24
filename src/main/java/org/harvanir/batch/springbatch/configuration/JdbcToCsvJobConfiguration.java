package org.harvanir.batch.springbatch.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.harvanir.batch.springbatch.batch.constant.BatchConstant;
import org.harvanir.batch.springbatch.batch.item.database.CustomJdbcCursorItemReader;
import org.harvanir.batch.springbatch.batch.mapper.ListObjectRowMapper;
import org.harvanir.batch.springbatch.batch.report.DefaultReportFactory;
import org.harvanir.batch.springbatch.batch.report.Report;
import org.harvanir.batch.springbatch.batch.report.ReportFactory;
import org.harvanir.batch.springbatch.entity.JobServiceRequest;
import org.harvanir.batch.springbatch.util.ObjectMapperUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.function.FunctionItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.harvanir.batch.springbatch.batch.constant.BatchConstant.JdbcToCsvJob.JOB_NAME;
import static org.harvanir.batch.springbatch.batch.constant.BatchConstant.JdbcToCsvJob.WRITE_TO_FILE_STEP;

/**
 * @author Harvan Irsyadi
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class JdbcToCsvJobConfiguration {

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
    public Report reportBuilder(
            ReportFactory reportFactory,
            JobServiceRequest jobServiceRequest
    ) {
        log.info("Initializing Report...");
        return reportFactory.get(jobServiceRequest.getId());
    }

    @Bean
    @JobScope
    public JdbcCursorItemReader<List<Object>> defaultItemReader(
            DataSource dataSource,
            Report report
    ) {
        log.info("Initializing item reader...");

        JdbcCursorItemReader<List<Object>> reader = new CustomJdbcCursorItemReader();
        reader.setDataSource(dataSource);
        reader.setRowMapper(new ListObjectRowMapper());
        reader.setSql(report.getSql());
        reader.setPreparedStatementSetter(report.getPreparedStatementSetter());

        return reader;
    }

    @Bean
    @JobScope
    public JdbcPagingItemReader<List<Object>> paginatedItemReader(
            DataSource dataSource,
            AppProperties properties,
            Report report
    ) {
        log.info("Initializing paginate item reader...");

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

    @Bean
    @JobScope
    public FlatFileItemWriter<List<Object>> itemWriter(Report report, AppProperties properties) {
        log.info("Initializing item writer...");

        String fileSeparator = System.getProperty("file.separator");
        String fileType = ".csv";
        String outputDir = properties.getReport().getOutputDirectory();
        String fileName = outputDir + fileSeparator + DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(ZonedDateTime.now()) + "-" + UUID.randomUUID().toString() + fileType;

        FlatFileItemWriter<List<Object>> itemWriter = new FlatFileItemWriter<>();

        itemWriter.setResource(new FileSystemResource(fileName));
        itemWriter.setAppendAllowed(true);
        itemWriter.setLineAggregator(new DelimitedLineAggregator<>());
        itemWriter.setHeaderCallback(writer -> writer.write(report.getHeader()));

        return itemWriter;
    }

    @Bean
    @JobScope
    public Step writeToFileStep(
            StepBuilderFactory stepBuilderFactory,
            JdbcCursorItemReader<List<Object>> defaultItemReader,
            JdbcPagingItemReader<List<Object>> paginatedItemReader,
            FlatFileItemWriter<List<Object>> itemWriter,
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

        stepBuilder
                .processor(new FunctionItemProcessor<>(o -> o))
                .writer(itemWriter);

        return stepBuilder.build();
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Job jdbcToCsvJob(JobBuilderFactory jobBuilderFactory, Step writeToFileStep) {
        log.info("Initializing job...");

        return jobBuilderFactory.get(JOB_NAME)
                .start(writeToFileStep)
                .build();
    }
}