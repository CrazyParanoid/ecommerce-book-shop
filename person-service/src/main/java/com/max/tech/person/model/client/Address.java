package com.max.tech.person.model.client;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "addresses")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "city", columnDefinition = "TEXT")
    private String city;
    @Column(name = "street", columnDefinition = "TEXT")
    private String street;
    @Column(name = "house", columnDefinition = "TEXT")
    private String house;
    private Integer flat;
    private Integer floor;
    private Integer entrance;
}
