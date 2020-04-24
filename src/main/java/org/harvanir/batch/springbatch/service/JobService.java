package org.harvanir.batch.springbatch.service;

import org.harvanir.batch.springbatch.entity.JobServiceRequest;

/**
 * @author Harvan Irsyadi
 */
public interface JobService {

    void run(JobServiceRequest jobServiceRequest);
}