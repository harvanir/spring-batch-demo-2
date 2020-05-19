package org.harvanir.batch.springbatch.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Harvan Irsyadi
 */
@ConfigurationProperties(prefix = "app")
@Getter
public class AppProperties {

    private final Report report = new Report();

    @Getter
    @Setter
    public static class Report {

        private String outputDirectory = "output";

        private int chunkSize = 100;
    }
}