package org.harvanir.batch.springbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class SpringBatch2Application {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatch2Application.class, args);
    }
}