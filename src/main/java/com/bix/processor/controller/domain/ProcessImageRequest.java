package com.bix.processor.controller.domain;

import com.bix.processor.domain.ProcessOperation;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProcessImageRequest {
    private List<ProcessOperation> operations;
}