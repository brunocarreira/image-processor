package com.bix.processor.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Image extends CoreEntity {

    private String fileName;
    private String filePath;
    private String status;  // e.g., 'PENDING', 'PROCESSING', 'PROCESSED'

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // other fields and methods
}