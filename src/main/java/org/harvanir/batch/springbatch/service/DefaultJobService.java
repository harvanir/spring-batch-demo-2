package org.harvanir.batch.springbatch.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.harvanir.batch.springbatch.batch.constant.BatchConstant;
import org.harvanir.batch.springbatch.entity.JobServiceRequest;
import org.harvanir.batch.springbatch.util.ObjectMapperUtils;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;

import java.util.UUID;

/**
 * @author Harvan Irsyadi
 */
@Slf4j
public class DefaultJobService implements JobService {

    private final JobLauncher jobLauncher;

    private final ObjectMapper objectMapper;

    private final JobFactory jobFactory;

    public DefaultJobService(JobLauncher jobLauncher, ObjectMapper objectMapper, JobFactory jobFactory) {
        this.jobLauncher = jobLauncher;
        this.objectMapper = objectMapper;
        this.jobFactory = jobFactory;
    }

    @Override
    public void run(JobServiceRequest jobServiceRequest) {
        try {
            JobParametersBuilder builder = new JobParametersBuilder();
            builder.addString(BatchConstant.RUN_ID, UUID.randomUUID().toString());

            String payload = ObjectMapperUtils.writeValueAsString(objectMapper, jobServiceRequest);
            if (payload != null) {
                builder.addString(BatchConstant.PAYLOAD, payload);
            }

            jobLauncher.run(jobFactory.defaultJob(jobServiceRequest), builder.toJobParameters());
        } catch (JobExecutionAlreadyRunningException | JobParametersInvalidException | JobInstanceAlreadyCompleteException | JobRestartException e) {
            log.error("Error run job.", e);
        }
    }
}