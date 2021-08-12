package com.max.tech.ordering.web;

import com.max.tech.ordering.application.client.ClientService;
import com.max.tech.ordering.application.client.dto.ClientDTO;
import com.max.tech.ordering.application.client.dto.RegisterNewClientCommand;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/clients")
@Tag(name = "client", description = "The client REST API")
public class ClientController {
    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Register new client")
    public ClientDTO registerNewClient(@RequestBody @Valid RegisterNewClientCommand command) {
        var clientDTO = clientService.registerNewClient(command);
        HypermediaUtil.addLinks(clientDTO);
        return clientDTO;
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Find client by id")
    public ClientDTO findClientById(@PathVariable String id) {
        var clientDTO = clientService.findClientById(id);
        HypermediaUtil.addLinks(clientDTO);
        return clientDTO;
    }

    @DeleteMapping(value = "/{id}")
    @ApiOperation(value = "Delete client")
    public ResponseEntity<Void> deleteClient(@PathVariable String id) {
        clientService.removeClient(id);
        return ResponseEntity.noContent().build();
    }

}
