package com.max.tech.payment.core.events.persistence;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "stored_events")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StoredEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(columnDefinition = "TEXT")
    private String payload;
    private String type;

    public StoredEvent(String payload, String type) {
        this.payload = payload;
        this.type = type;
    }
}
