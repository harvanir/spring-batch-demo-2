package org.harvanir.batch.springbatch.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.harvanir.batch.springbatch.service.DefaultJobService;
import org.harvanir.batch.springbatch.service.JobService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Harvan Irsyadi
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class ServiceConfiguration {

    @Bean
    public JobService jobService(JobLauncher jobLauncher, ObjectFactory<Job> jdbcToCsvJob, ObjectMapper objectMapper) {
        return new DefaultJobService(jobLauncher, jdbcToCsvJob, objectMapper);
    }
}