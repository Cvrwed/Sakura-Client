package cc.unknown.event.bus.impl;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

import cc.unknown.event.Listener;
import cc.unknown.event.annotations.EventLink;
import cc.unknown.event.bus.Bus;
import cc.unknown.event.impl.netty.PacketEvent;
import cc.unknown.event.impl.other.GameEvent;
import cc.unknown.event.impl.other.ServerJoinEvent;
import cc.unknown.event.impl.other.ServerKickEvent;
import cc.unknown.event.impl.other.WorldChangeEvent;
import cc.unknown.event.impl.render.RenderGUIEvent;
import cc.unknown.util.Accessor;
import cc.unknown.util.chat.ChatUtil;

public final class EventBus<Event> implements Bus<Event>, Accessor {
    private final Map<Type, List<CallSite<Event>>> callSiteMap;
    private final Map<Type, List<Listener<Event>>> listenerCache;
    private final ConcurrentLinkedQueue<Function<Event, Boolean>> customListeners = new ConcurrentLinkedQueue<>();
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    public EventBus() {
        callSiteMap = new HashMap<>();
        listenerCache = new HashMap<>();
    }

    @Override
    public void register(final Object subscriber) {
        try {
            for (final Field field : subscriber.getClass().getDeclaredFields()) {
                final EventLink annotation = field.getAnnotation(EventLink.class);
                if (annotation != null) {
                    final Type eventType = ((ParameterizedType) (field.getGenericType())).getActualTypeArguments()[0];

                    if (!field.isAccessible())
                        field.setAccessible(true);
                    try {
                        final Listener<Event> listener =
                                (Listener<Event>) LOOKUP.unreflectGetter(field)
                                        .invokeWithArguments(subscriber);

                        final byte priority = annotation.value();

                        final List<CallSite<Event>> callSites;
                        final CallSite<Event> callSite = new CallSite<>(subscriber, listener, priority);

                        if (this.callSiteMap.containsKey(eventType)) {
                            callSites = this.callSiteMap.get(eventType);
                            callSites.add(callSite);
                            callSites.sort((o1, o2) -> o2.priority - o1.priority);
                        } else {
                            callSites = new ArrayList<>(1);
                            callSites.add(callSite);
                            this.callSiteMap.put(eventType, callSites);
                        }
                    } catch (Throwable exception) {
                        //if (!Client.DEVELOPMENT_SWITCH) return;
                        ChatUtil.display("Exception in console");
                        exception.printStackTrace();
                    }
                }
            }

            this.populateListenerCache();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private void populateListenerCache() {
        final Map<Type, List<CallSite<Event>>> callSiteMap = this.callSiteMap;
        final Map<Type, List<Listener<Event>>> listenerCache = this.listenerCache;

        for (final Type type : callSiteMap.keySet()) {
            final List<CallSite<Event>> callSites = callSiteMap.get(type);
            final int size = callSites.size();
            final List<Listener<Event>> listeners = new ArrayList<>(size);

            for (int i = 0; i < size; i++)
                listeners.add(callSites.get(i).listener);

            listenerCache.put(type, listeners);
        }
    }

    @Override
    public void unregister(final Object subscriber) {
        for (List<CallSite<Event>> callSites : this.callSiteMap.values()) {
            callSites.removeIf(eventCallSite -> eventCallSite.owner == subscriber);
        }

        this.populateListenerCache();
    }

    @Override
    public void handle(final Event event) {
        try {
            if ((mc.world == null || mc.getNetHandler() == null || (!mc.getNetHandler().doneLoadingTerrain && !(event instanceof PacketEvent && ((PacketEvent) event).isReceive()))) && !(event instanceof RenderGUIEvent || event instanceof ServerKickEvent || event instanceof GameEvent || event instanceof WorldChangeEvent || event instanceof ServerJoinEvent)) {
                return;
            }

            final List<Listener<Event>> listeners = listenerCache.getOrDefault(event.getClass(), Collections.emptyList());

            int i = 0;
            final int listenersSize = listeners.size();

            while (i < listenersSize) {
                listeners.get(i++).call(event);
            }

            if (!this.customListeners.isEmpty()) {
                this.customListeners.removeIf(listener -> listener.apply(event));
            }

        } catch (Exception exception) {
            //if (Client.DEVELOPMENT_SWITCH) {
                exception.printStackTrace();
                ChatUtil.display("Exception in console");
            //}
        }
    }

    public void registerCustom(final Function<Event, Boolean> listener) {
        this.customListeners.add(listener);
    }

    public void handle(final Event event, final Object... clazz) {
        try {
            System.out.println(clazz.getClass().getDeclaredFields().length);

            for (final Field field : clazz.getClass().getDeclaredFields()) {
                final EventLink annotation = field.getAnnotation(EventLink.class);

                if (annotation != null) {
                    final Type eventType = ((ParameterizedType) (field.getGenericType())).getActualTypeArguments()[0];

                    if (!field.isAccessible())
                        field.setAccessible(true);

                    final Listener<Event> listener =
                            (Listener<Event>) LOOKUP.unreflectGetter(field)
                                    .invokeWithArguments(clazz);

                    System.out.println("Name: " + eventType.getTypeName());
                    System.out.println("Event: " + event.getClass().getSimpleName());
//                    listener.call(event);
                }
            }
        } catch (Throwable exception) {
            exception.printStackTrace();
        }
    }

    public Map<Type, List<CallSite<Event>>> getCallSiteMap() {
		return callSiteMap;
	}

	public Map<Type, List<Listener<Event>>> getListenerCache() {
		return listenerCache;
	}

	public ConcurrentLinkedQueue<Function<Event, Boolean>> getCustomListeners() {
		return customListeners;
	}

	public static MethodHandles.Lookup getLookup() {
		return LOOKUP;
	}

	private static class CallSite<Event> {
        private final Object owner;
        private final Listener<Event> listener;
        private final byte priority;

        public CallSite(Object owner, Listener<Event> listener, byte priority) {
            this.owner = owner;
            this.listener = listener;
            this.priority = priority;
        }

		public Object getOwner() {
			return owner;
		}

		public Listener<Event> getListener() {
			return listener;
		}

		public byte getPriority() {
			return priority;
		}
    }
}