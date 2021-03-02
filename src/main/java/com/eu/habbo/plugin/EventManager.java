package com.eu.habbo.plugin;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.Easter;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.games.freeze.FreezeGame;
import com.eu.habbo.habbohotel.games.tag.TagGame;
import com.eu.habbo.habbohotel.items.interactions.games.football.InteractionFootballGate;
import com.eu.habbo.habbohotel.wired.highscores.WiredHighscoreManager;
import com.eu.habbo.messages.PacketManager;
import com.eu.habbo.plugin.events.CriticalProcessTimeEvent;
import com.eu.habbo.plugin.events.ExceptionEvent;
import com.eu.habbo.plugin.events.emulator.EmulatorConfigUpdatedEvent;
import com.eu.habbo.plugin.events.emulator.EmulatorLoadedEvent;
import com.eu.habbo.plugin.events.roomunit.RoomUnitLookAtPointEvent;
import com.eu.habbo.plugin.events.users.*;
import com.eu.habbo.threading.runnables.RoomTrashing;
import gnu.trove.set.hash.THashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class EventManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(EventManager.class);
    private final THashSet<Method> methods = new THashSet<>();

    public abstract class RegisteredListener {

        public abstract void call(Event e);

    }

    private class OneLineListener<T extends Event> extends RegisteredListener {

        Consumer<T> cons;
        boolean async = false;

        public OneLineListener(Consumer<T> cons2) {
            this.cons = cons2;
        }

        public OneLineListener(Consumer<T> cons, boolean async) {
            this.cons = cons;
            this.async = async;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void call(Event e) {
            Runnable r = ()-> cons.accept((T) e);
            if(async) {
                service.submit(r);
            } else {
                r.run();
            }

        }

    }

    private class MethodBasedListener extends RegisteredListener {

        Object o;
        Method m;
        boolean async;

        public MethodBasedListener(Object o, Method m) {
            this.o = o;
            this.m = m;
            async = m.isAnnotationPresent(Async.class);
        }

        public void call(Event e) {
            Runnable r = ()->{
                try {
                    m.setAccessible(true);
                    m.invoke(o, e);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
                    onException(e, e1);
                }
            };
            if(async) {
                service.submit(r);
            } else {
                r.run();
            }
        }

    }

    List<BiConsumer<Event, Exception>> exceptionHandlers;
    HashMap<Class<? extends Event>, Long> criticalTime;
    ExecutorService service;

    public EventManager() {
        exceptionHandlers = new ArrayList<>();
        criticalTime = new HashMap<>();
        service = Executors.newCachedThreadPool();
        registerDefaultEvents();
    }

    public void setCriticalTime(Class<? extends Event> event, long time, TimeUnit unit) {
        criticalTime.put(event, unit.toMillis(time));
    }

    public void setCriticalTime(Class<? extends Event> event, long time) {
        criticalTime.put(event, time);
    }

    private void onException(Event e, Exception ex) {
        for(BiConsumer<Event, Exception> handler : exceptionHandlers) {
            handler.accept(e, ex);
        }
        if(!(e instanceof ExceptionEvent)) {
            ExceptionEvent event = new ExceptionEvent(ex, e);
            callEvent(event);
            if(event.isPrintStackTrace()) {
                ex.printStackTrace();
            }
        } else {
            ex.printStackTrace();
        }

    }

    public void addExceptionHandler(BiConsumer<Event, Exception> handler) {
        exceptionHandlers.add(handler);
    }

    @SuppressWarnings("unchecked")
    public void registerListener(HabboPlugin habboPlugin, EventListener listener) {
        for(Method m : listener.getClass().getMethods()) {
            if(!m.isAnnotationPresent(EventHandler.class)) continue;
            if(m.getParameterCount() != 1) continue;
            if(!Event.class.isAssignableFrom(m.getParameterTypes()[0])) continue;

            Class<? extends Event> eventClazz = (Class<? extends Event>) m.getParameterTypes()[0];
            MethodBasedListener registeredListener = new MethodBasedListener(listener, m);
            HashMap<Class<? extends Event>, Set<RegisteredListener>> rmap = habboPlugin.listeners;
            if(!rmap.containsKey(eventClazz)) {
                rmap.put(eventClazz, new HashSet<>());
            }
            rmap.get(eventClazz).add(registeredListener);
        }
    }

    public <T extends Event> RegisteredListener register(HabboPlugin habboPlugin, Class<T> clazz, Consumer<T> cons) {
        OneLineListener<T> l = new OneLineListener<>(cons);
        HashMap<Class<? extends Event>, Set<RegisteredListener>> rmap = habboPlugin.listeners;
        if(!rmap.containsKey(clazz)) {
            rmap.put(clazz, new HashSet<>());
        }
        rmap.get(clazz).add(l);
        return l;
    }

    public <T extends Event> RegisteredListener register(HabboPlugin habboPlugin, Class<T> clazz, boolean async, Consumer<T> cons) {
        OneLineListener<T> l = new OneLineListener<>(cons, async);
        HashMap<Class<? extends Event>, Set<RegisteredListener>> rmap = habboPlugin.listeners;
        if(!rmap.containsKey(clazz)) {
            rmap.put(clazz, new HashSet<>());
        }
        rmap.get(clazz).add(l);
        return l;
    }

    public void unregisterListener(EventListener listener) {
        for (HabboPlugin habboPlugin : Emulator.getPluginManager().getPlugins()) {
            for (Map.Entry<Class<? extends Event>, Set<RegisteredListener>> en : habboPlugin.listeners.entrySet()) {
                Set<RegisteredListener> rl = null;
                for (RegisteredListener l : en.getValue()) {
                    if (l instanceof MethodBasedListener) {
                        if (((MethodBasedListener) l).o == listener) {
                            if (rl == null) rl = new HashSet<>();
                            rl.add(l);
                        }
                    }
                }
                if (rl != null) {
                    en.getValue().removeAll(rl);
                }
            }
        }
    }

    public void unregisterListener(RegisteredListener listener) {
        for (HabboPlugin habboPlugin : Emulator.getPluginManager().getPlugins()) {
            for (Map.Entry<Class<? extends Event>, Set<RegisteredListener>> en : habboPlugin.listeners.entrySet()) {
                Set<RegisteredListener> rl = null;
                for (RegisteredListener l : en.getValue()) {
                    if (l == listener) {
                        if (rl == null) rl = new HashSet<>();
                        rl.add(l);
                    }
                }
                if (rl != null) {
                    en.getValue().removeAll(rl);
                }
            }
        }
    }

    public <T extends Event> T callEvent(T e) {
        for (Method method : this.methods) {
            if (method.getParameterTypes().length == 1 && method.getParameterTypes()[0].isAssignableFrom(e.getClass())) {
                try {
                    method.invoke(null, e);
                } catch (Exception ex) {
                    LOGGER.error("Could not pass default event {} to {}: {}!", e.getClass().getName(), method.getClass().getName(), method.getName());
                    LOGGER.error("Caught exception", ex);
                }
            }
        }

        try {
            long start = System.currentTimeMillis();
            for (HabboPlugin habboPlugin : Emulator.getPluginManager().getPlugins()) {
                Set<RegisteredListener> listeners = habboPlugin.listeners.get(e.getClass());
                if (listeners != null) {
                    for (RegisteredListener list : listeners) {
                        list.call(e);
                    }
                }
            }
            long time = System.currentTimeMillis() - start;
            long maxTime = criticalTime.getOrDefault(e.getClass(), -1L);
            if(maxTime > 0 && time > maxTime) {
                CriticalProcessTimeEvent event = new CriticalProcessTimeEvent(e, time);
                callEvent(event);
                if(event.shouldBroadcast) {
                    LoggerFactory.getLogger(GameClient.class).error("[Event] "+e.getClass().getSimpleName() + " took " + time + " ms to process!");
                }
            }
        } catch(Exception ex) {
            onException(e, ex);
        }
        return e;
    }

    public boolean isRegistered(Class<? extends Event> clazz, boolean pluginsOnly) {
        for(HabboPlugin habboPlugin : Emulator.getPluginManager().getPlugins()){
            if (habboPlugin.isRegistered(clazz))
                return true;
        }

        if (!pluginsOnly) {
            for (Method method : this.methods) {
                if (method.getParameterTypes().length == 1 && method.getParameterTypes()[0].isAssignableFrom(clazz)) {
                    return true;
                }
            }
        }

        return false;
    }

    private void registerDefaultEvents() {
        try {
            this.methods.add(RoomTrashing.class.getMethod("onUserWalkEvent", UserTakeStepEvent.class));
            this.methods.add(Easter.class.getMethod("onUserChangeMotto", UserSavedMottoEvent.class));
            this.methods.add(TagGame.class.getMethod("onUserLookAtPoint", RoomUnitLookAtPointEvent.class));
            this.methods.add(TagGame.class.getMethod("onUserWalkEvent", UserTakeStepEvent.class));
            this.methods.add(FreezeGame.class.getMethod("onConfigurationUpdated", EmulatorConfigUpdatedEvent.class));
            this.methods.add(PacketManager.class.getMethod("onConfigurationUpdated", EmulatorConfigUpdatedEvent.class));
            this.methods.add(InteractionFootballGate.class.getMethod("onUserDisconnectEvent", UserDisconnectEvent.class));
            this.methods.add(InteractionFootballGate.class.getMethod("onUserExitRoomEvent", UserExitRoomEvent.class));
            this.methods.add(InteractionFootballGate.class.getMethod("onUserSavedLookEvent", UserSavedLookEvent.class));
            this.methods.add(PluginManager.class.getMethod("globalOnConfigurationUpdated", EmulatorConfigUpdatedEvent.class));
            this.methods.add(WiredHighscoreManager.class.getMethod("onEmulatorLoaded", EmulatorLoadedEvent.class));
        } catch (NoSuchMethodException e) {
            LOGGER.info("Failed to define default events!");
            LOGGER.error("Caught exception", e);
        }
    }
}