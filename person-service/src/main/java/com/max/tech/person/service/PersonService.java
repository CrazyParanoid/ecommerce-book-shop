package com.max.tech.person.service;

import com.max.tech.person.model.Person;
import com.max.tech.person.model.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PersonService {
    private final AuthorizationServerProvider authorizationServerProvider;
    private final PersonRepository personRepository;

    @Autowired
    public PersonService(AuthorizationServerProvider authorizationServerProvider, PersonRepository personRepository) {
        this.authorizationServerProvider = authorizationServerProvider;
        this.personRepository = personRepository;
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public <T extends Person> void registerPerson(T person) {
        var userId = authorizationServerProvider.registerUser(person);

    }

}
