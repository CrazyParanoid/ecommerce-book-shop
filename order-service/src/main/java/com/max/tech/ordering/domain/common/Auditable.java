package com.max.tech.ordering.domain.common;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
public abstract class Auditable {
    @CreatedDate
    @Column(name = "created_at", columnDefinition = "DATE")
    private LocalDateTime createdAt;
    @LastModifiedDate
    @Column(name = "updated_at", columnDefinition = "DATE")
    private LocalDateTime updatedAt;
}
