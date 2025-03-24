package com.bix.processor.message;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ImageProcessingMessage {
    private Long imageId;
    private Long userId;
    private List<String> operations;
}
