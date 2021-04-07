package com.eu.habbo.plugin;

import com.eu.habbo.habbohotel.users.Habbo;
import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.io.InputStream;
import java.net.URLClassLoader;
import java.util.*;

public abstract class HabboPlugin {
    protected HashMap<Class<? extends Event>, Set<EventManager.RegisteredListener>> listeners = new HashMap<>();

    public HabboPluginConfiguration configuration;
    public URLClassLoader classLoader;
    public InputStream stream;

    public abstract void onEnable() throws Exception;

    public abstract void onDisable() throws Exception;

    public boolean isRegistered(Class<? extends Event> clazz) {
        return listeners.containsKey(clazz);
    }

    public abstract boolean hasPermission(Habbo habbo, String key);
}
