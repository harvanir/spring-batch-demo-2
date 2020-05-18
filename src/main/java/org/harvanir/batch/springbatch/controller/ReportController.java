package org.harvanir.batch.springbatch.controller;

import lombok.extern.slf4j.Slf4j;
import org.harvanir.batch.springbatch.entity.FileType;
import org.harvanir.batch.springbatch.entity.JobServiceRequest;
import org.harvanir.batch.springbatch.service.JobService;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Harvan Irsyadi
 */
@Slf4j
@RestController
public class ReportController {

    private final JobService jobService;

    public ReportController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/generate/{id}")
    public String findAll(@PathVariable Integer id, @RequestParam(required = false, name = "usePaginate") Boolean usePaginate
            , @RequestParam(required = false, name = "fileType") String fileType
    ) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        JobServiceRequest request = JobServiceRequest
                .builder()
                .id(id)
                .usePaginate(usePaginate)
                .fileType(fileType != null ? FileType.valueOf(fileType.toUpperCase()) : null)
                .build();
        jobService.run(request);

        stopWatch.stop();

        return String.format("Elapsed in %s millis.", stopWatch.getTotalTimeMillis());
    }
}