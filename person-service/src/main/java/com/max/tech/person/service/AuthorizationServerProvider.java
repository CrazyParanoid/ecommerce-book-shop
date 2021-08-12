package com.max.tech.person.service;

import com.max.tech.person.model.Person;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class AuthorizationServerProvider {
    private final Keycloak keycloak;

    @Value("keycloak.realm")
    private String realm;

    public AuthorizationServerProvider(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    public <T extends Person> String registerUser(T person) {
        var usersResource = keycloak.realm(realm).users();
        var credentialRepresentation = createPasswordCredentials(person.getPassword());
        var user = new UserRepresentation();

        user.setUsername(person.getUserName());
        user.setCredentials(Collections.singletonList(credentialRepresentation));
        user.setFirstName(person.getFirstName());
        user.setLastName(person.getSurName());
        user.setEmail(person.getEmail());
        user.setEnabled(true);
        user.setEmailVerified(false);

        var response = usersResource.create(user);

        return CreatedResponseUtil.getCreatedId(response);
    }

    private CredentialRepresentation createPasswordCredentials(String password) {
        CredentialRepresentation passwordCredentials = new CredentialRepresentation();
        passwordCredentials.setTemporary(false);
        passwordCredentials.setType(CredentialRepresentation.PASSWORD);
        passwordCredentials.setValue(password);
        return passwordCredentials;
    }

}
