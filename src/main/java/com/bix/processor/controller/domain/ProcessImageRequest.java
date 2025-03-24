package com.bix.processor.controller.domain;

import com.bix.processor.domain.ProcessOperation;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcessImageRequest {
    private ProcessOperation operation;
}