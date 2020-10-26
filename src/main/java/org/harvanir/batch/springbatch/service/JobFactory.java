package org.harvanir.batch.springbatch.service;

import org.harvanir.batch.springbatch.entity.JobServiceRequest;
import org.springframework.batch.core.Job;

/**
 * @author Harvan Irsyadi
 */
public interface JobFactory {

    Job defaultJob(JobServiceRequest jobServiceRequest);
}