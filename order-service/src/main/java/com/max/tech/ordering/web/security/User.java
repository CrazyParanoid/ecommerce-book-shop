package com.max.tech.ordering.web.security;

import java.util.List;

public record User(String id, List<Role> roles) {
}
