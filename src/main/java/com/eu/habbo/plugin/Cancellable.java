package com.eu.habbo.plugin;

public interface Cancellable {
    boolean isCancelled();

    void setCancelled(boolean cancel);
}
