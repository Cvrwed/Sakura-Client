package cc.unknown.event;

import java.io.IOException;

@SuppressWarnings("hiding")
@FunctionalInterface
public interface Listener<Event> {
    void call(Event event) throws IOException;
}