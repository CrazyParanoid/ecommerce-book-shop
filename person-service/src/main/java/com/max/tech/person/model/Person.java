package com.max.tech.person.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.util.UUID;

@Data
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Person {
    @Id
    private UUID id;
    private String email;
    private String userName;
    private String password;
    private String firstName;
    private String middleName;
    private String surName;
}
