package com.max.tech.catalog.catalog.events;

public interface EventSubscriber<T extends Event> {

    void onEvent(T event);

}
