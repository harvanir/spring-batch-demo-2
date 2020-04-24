package org.harvanir.batch.springbatch.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Harvan Irsyadi
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
public class JobServiceRequest {

    private Integer id;

    private Boolean usePaginate;
}