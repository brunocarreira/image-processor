package com.bix.processor.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TB_IMAGE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Image extends CoreEntity {

    private String fileName;
    private String filePath;
    @Enumerated(EnumType.STRING)
    private ImageStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // other fields and methods
}