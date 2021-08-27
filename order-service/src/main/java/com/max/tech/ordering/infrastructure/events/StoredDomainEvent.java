package com.max.tech.ordering.infrastructure.events;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "stored_events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoredDomainEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String payload;
    private String type;

    public StoredDomainEvent(String payload, String type) {
        this.payload = payload;
        this.type = type;
    }

}
