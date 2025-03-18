package com.bix.processor.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Process extends CoreEntity {

    private Long imageId;
    private String status;  // 'IN_PROGRESS', 'COMPLETED', 'FAILED'

    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;
    private Date endTime;
}