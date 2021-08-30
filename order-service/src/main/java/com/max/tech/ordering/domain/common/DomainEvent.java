package com.max.tech.ordering.domain.common;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public interface DomainEvent {
}
