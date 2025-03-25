package com.bix.processor.message;

import com.bix.processor.domain.ProcessOperation;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ImageProcessingMessage {
    private Long imageId;
    private Long userId;
    private List<ProcessOperation> operations;
}
