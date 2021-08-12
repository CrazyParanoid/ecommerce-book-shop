package com.max.tech.ordering.infrastructure.events.subscribers;

public interface EventSubscriber<T extends Event> {

    void onEvent(T event);

}
